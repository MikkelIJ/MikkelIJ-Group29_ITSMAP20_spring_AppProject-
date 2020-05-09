package com.smap.group29.getmoving.activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.adaptor.LeaderboardAdaptor;
import com.smap.group29.getmoving.model.NewUser;
import com.smap.group29.getmoving.service.GetMovingService;
import com.smap.group29.getmoving.utils.GlobalConstants;
import com.squareup.picasso.Picasso;

import java.util.Collections;


// Firebase recyclerview inspired by https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#using-the-firebaserecycleradapter
// and https://codinginflow.com/tutorials/android/firebaseui-firestorerecycleradapter/part-4-new-note-activity
public class LeaderboardActivity extends AppCompatActivity{

    private FirebaseFirestore mStore;
    private CollectionReference collectionReference;
    private FirebaseAuth mAuth;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private DocumentReference documentReference;


    private LeaderboardAdaptor mAdapter;
    private RecyclerView mUserList;
    private LinearLayoutManager mLinearLayoutManager;

    private Intent stepIntent;
    private IntentFilter getTimerFlagFilter = new IntentFilter();
    private TextView tv_header, tv_wins, tv_followers, tv_totalSteps;
    private Button btn_back, btn_switchView, btn_follow;
    private ImageView iv_userPictureLand;

    private GetMovingService mService;
    private boolean mBound = false;

    private ProgressBar progressBar;



    private String userID;
    private int orientation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        initUI();

        setUI();
        orientation = getResources().getConfiguration().orientation;
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        collectionReference = mStore.collection(GlobalConstants.FIREBASE_USER_COLLECTION);
        documentReference = mStore.collection(GlobalConstants.FIREBASE_USER_COLLECTION).document(userID);
        storageReference = FirebaseStorage.getInstance().getReference();

        getTimerFlagFilter.addAction("TIMER");
        registerReceiver(broadcastReceiver,getTimerFlagFilter);

        getUserWithMostSteps();
        setupRecyclerView();



    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("sec","broadcastReceiver activated");
            int x = intent.getIntExtra("startprogressbar",0);
            if (x == 1){
                timeTilRefresh();
                x = 0;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
        stepIntent = new Intent(this,GetMovingService.class);
        bindService(stepIntent,serviceConnection,Context.BIND_AUTO_CREATE);

    }



    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GetMovingService.LocalBinder binder = (GetMovingService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.GM_removeCallbacks();
            mService.updateBroadcastData.run();
            mService.updateStepsLeaderBoard.run();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(stepIntent,serviceConnection,Context.BIND_AUTO_CREATE);
        mBound = true;
        registerReceiver(broadcastReceiver,getTimerFlagFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
        unbindService(serviceConnection);
        mBound = false;
    }


    private void setupRecyclerView() {


        mUserList = findViewById(R.id.rv_leaderboard);
        Query query = collectionReference.orderBy("dailysteps",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<NewUser> mOptions = new FirestoreRecyclerOptions.Builder<NewUser>().setQuery(query, NewUser.class).build();
        mAdapter = new LeaderboardAdaptor(mOptions);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(this));
        mLinearLayoutManager.setSmoothScrollbarEnabled(true);
        //mLinearLayoutManager.smoothScrollToPosition(); // todo

        mAdapter.setOnItemClickListener(new LeaderboardAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    NewUser user = documentSnapshot.toObject(NewUser.class);
                    getUserDataFirebase(user.getuID());
                    loadPic(user.getuID());
                }
            }
        });
        mUserList.setAdapter(mAdapter);

    }

    private void initUI() {
        btn_back = findViewById(R.id.btn_leaderboardBack);
        progressBar = findViewById(R.id.progressBar3);
        tv_header = findViewById(R.id.tv_LeaderboardHeader);
        tv_wins = findViewById(R.id.tv_leaderboardLand_Wins);
        tv_followers = findViewById(R.id.tv_leaderboardLandFollowers);
        tv_totalSteps = findViewById(R.id.tv_totalStepsLeaderboardLand);
        iv_userPictureLand = findViewById(R.id.iv_leaderboardLand);
    }


    private void setUI() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void timeTilRefresh(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final CountDownTimer newtimer = new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        progressBar.setProgress((int) ((double) (30000-millisUntilFinished) / 30000 * 100));
                        Log.v("sec",String.valueOf(millisUntilFinished));
                    }
                    public void onFinish() {
                        progressBar.setProgress(0);
                    }
                };
                newtimer.start();
            }
        };
          runnable.run();
    }



    private void getUserDataFirebase(String userID){
        DocumentReference documentReference = mStore.collection(GlobalConstants.FIREBASE_USER_COLLECTION).document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.v("fb", "DocumentSnapshot data: " + document.getData());
                        tv_totalSteps.setText("Total steps: "+document.getString("stepstotal"));
                        tv_followers.setText("Followers: "+document.getString("followers"));
                        tv_wins.setText("Wins: "+document.getString("wins"));
                    } else {
                        Log.d("fb", "No such document");
                    }
                } else {
                    Log.d("fb", "get failed with ", task.getException());
                }
            }
        });
    }

    public void loadPic(final String userID){
        //Waiting one second with loading the picture to make sure the picture is on firebase before trying to load it into the imageview

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                final StorageReference imgProfile = storageReference.child("users/"+ userID+"profile.jpg");
                imgProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(iv_userPictureLand);
                    }
                });
            }
        }, 1000);

    }

    private void getUserWithMostSteps(){
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            collectionReference.orderBy("dailysteps",Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            String userID =  documentSnapshot.getData().get("uID").toString();
                            loadPic(userID);
                            getUserDataFirebase(userID);
                        }


                    }
                }
            });

            getUserDataFirebase(userID);
            loadPic(userID);
        }
    }

}

