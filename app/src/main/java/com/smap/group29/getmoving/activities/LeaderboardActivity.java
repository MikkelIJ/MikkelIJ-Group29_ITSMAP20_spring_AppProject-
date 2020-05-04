package com.smap.group29.getmoving.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.adaptor.LeaderboardAdaptor;
import com.smap.group29.getmoving.model.NewUser;
import com.smap.group29.getmoving.service.GetMovingService;
import com.smap.group29.getmoving.utils.GlobalConstants;


// Firebase recyclerview inspired by https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#using-the-firebaserecycleradapter
// and https://codinginflow.com/tutorials/android/firebaseui-firestorerecycleradapter/part-4-new-note-activity
public class LeaderboardActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbRef = db.collection(GlobalConstants.FIREBASE_USER_COLLECTION);

    private LeaderboardAdaptor mAdapter;

    private RecyclerView mUserList;
    //private FirestoreRecyclerAdapter adapter;

    private Intent stepIntent;
    private Button btn_back;

    private GetMovingService mService;
    private boolean mBound = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        initUI();
        setupRecyclerView();
        setUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();

        stepIntent = new Intent(this,GetMovingService.class);
        bindService(stepIntent,serviceConnection,Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(stepIntent,serviceConnection,Context.BIND_AUTO_CREATE);
        mBound = true;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GetMovingService.LocalBinder binder = (GetMovingService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            //mService.updateBroadcastData.run();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
        unbindService(serviceConnection);
    }




    private void setupRecyclerView() {

        mUserList = findViewById(R.id.rv_leaderboard);
        Query query = dbRef.orderBy("dailysteps",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<NewUser> mOptions = new FirestoreRecyclerOptions.Builder<NewUser>().setQuery(query, NewUser.class).build();

        mAdapter = new LeaderboardAdaptor(mOptions);

        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
        mUserList.setAdapter(mAdapter);
    }

    private void initUI() {
        btn_back = findViewById(R.id.btn_leaderboardBack);
    }

    private void setUI() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

