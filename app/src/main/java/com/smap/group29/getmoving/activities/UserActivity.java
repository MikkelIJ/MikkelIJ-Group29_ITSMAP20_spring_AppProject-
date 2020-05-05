package com.smap.group29.getmoving.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.service.GetMovingService;
import com.smap.group29.getmoving.service.GetMovingService.LocalBinder;
import com.smap.group29.getmoving.onlineAPI.OpenWeatherAPI;
import com.smap.group29.getmoving.utils.GlobalConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.annotation.Nullable;

import static com.smap.group29.getmoving.service.GetMovingService.BROADCAST_ACTION_STEPS;
import static com.smap.group29.getmoving.onlineAPI.OpenWeatherAPI.BROADCAST_ACTION_WEATHER;


public class UserActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    String userID;


    private static final String LOGD = "userActivity";

    private Button btn_leaderboard;
    private EditText et_dailyGoal;
    private TextView tv_name, tv_age, tv_city, tv_weatherTemp,tv_weatherFeelsLike, tv_weatherHumid, tv_weatherDescription, tv_stepsToday, tv_stepsTotal, tv_email;
    private ImageView iv_userPicture, iv_weatherIcon;

    private Intent stepIntent;

    private GetMovingService mService;
    private boolean mBound = false;

    private OpenWeatherAPI mOpenWeatherAPI;
    private static UserActivity mCurrentInstance;

    private IntentFilter getCurrentStepsFilter = new IntentFilter();
    private IntentFilter getCurrentWeatherFilter = new IntentFilter();

    private int stepsCounted = 0;
    private int prevStepsCounted = 0;
    private int totalsteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        initUI();




        //setting up the data from firebase user
        final DocumentReference documentReference = mStore.collection(GlobalConstants.FIREBASE_USER_COLLECTION).document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    Log.v("onEvent","Error:"+e.getMessage());
                }else {
                    totalsteps = Integer.valueOf(Math.toIntExact(documentSnapshot.getLong("stepstotal")));
                    tv_stepsTotal.setText(String.valueOf(documentSnapshot.getLong("stepstotal")));
                    tv_name.setText(documentSnapshot.getString("name"));
                    tv_email.setText(documentSnapshot.getString("email"));
                    tv_age.setText(documentSnapshot.getString("age"));
                    tv_city.setText(documentSnapshot.getString("city"));

                    et_dailyGoal.setText(documentSnapshot.getString("dailysteps"));

                }
            }
        });



        //loading the picture from firebase into imageview
        final StorageReference imgProfile = storageReference.child("users/"+ mAuth.getUid()+"profile.jpg");
        imgProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(iv_userPicture);
            }
        });



        registerIntentFilters();
        mOpenWeatherAPI = new OpenWeatherAPI(this,2624652);



        btn_leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, LeaderboardActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.settings:
                openSettings();
                break;


            case R.id.logout:
                logout();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
        intent.putExtra("name", tv_name.getText().toString());
        intent.putExtra("age", tv_age.getText().toString());
        intent.putExtra("city", tv_city.getText().toString());
        intent.putExtra("steps", et_dailyGoal.getText().toString());
        intent.putExtra("email", tv_email.getText().toString());

        startActivity(intent);
    }

    /*
    private void uploadUserID(){
        //upload userId to storage for gloabel access to fetch users for leaderboard
        StorageReference fileRef = storageReference.child("userlist/");
        Intent userIntent = new Intent();
        userIntent.putExtra("userUri",mAuth.getCurrentUser().getUid().toString());
        Uri userUri =  userIntent.getData();
        fileRef.putFile(userUri);
    }

 */

    public void logout(){
        mService.GM_removeCallbacks();
        mService.updateDailySteps(); // upload latest stepsvalue and total steps
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();

    }


    @Override
    protected void onStop() {
        super.onStop();

        unbindService(serviceConnection);
        mBound = false;

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiverSteps);
        unregisterReceiver(broadcastReceiverWeather);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(stepIntent,serviceConnection,Context.BIND_AUTO_CREATE);
        mBound = true;
        //getCurrentStepsFilter.addAction(BROADCAST_ACTION_STEPS);
        registerReceiver(broadcastReceiverSteps,getCurrentStepsFilter);

        //IntentFilter getCurrentWeatherFilter = new IntentFilter();
        //getCurrentWeatherFilter.addAction(BROADCAST_ACTION_WEATHER);
        registerReceiver(broadcastReceiverWeather,getCurrentWeatherFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(stepIntent);
    }

    private void initService(){

        // Binds ListActivity to the WordlearnerService.

        stepIntent = new Intent(this,GetMovingService.class);
        startService(stepIntent);
        bindService(stepIntent,serviceConnection,Context.BIND_AUTO_CREATE);
    }

    private void registerIntentFilters(){
        //IntentFilter getCurrentStepsFilter = new IntentFilter();
        getCurrentStepsFilter.addAction(BROADCAST_ACTION_STEPS);
        registerReceiver(broadcastReceiverSteps,getCurrentStepsFilter);

        //IntentFilter getCurrentWeatherFilter = new IntentFilter();
        getCurrentWeatherFilter.addAction(BROADCAST_ACTION_WEATHER);
        registerReceiver(broadcastReceiverWeather,getCurrentWeatherFilter);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            mService.updateBroadcastData.run();
            mService.updateStepsLeaderBoard.run();
            mOpenWeatherAPI.sendRequest();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private void initUI(){
        tv_name               = findViewById(R.id.tv_userActivityName);
        tv_age                = findViewById(R.id.tv_userAge);
        tv_city               = findViewById(R.id.tv_userCity);
        tv_weatherTemp        = findViewById(R.id.tv_WeatherTemp);
        tv_weatherFeelsLike   = findViewById(R.id.tv_WeatherFeelsLike);
        tv_weatherHumid       = findViewById(R.id.tv_weatherHumid);
        tv_weatherDescription = findViewById(R.id.tv_weatherDescription);
        tv_stepsToday         = findViewById(R.id.tv_steps_today);
        tv_stepsTotal         = findViewById(R.id.tv_stepsTotal);
        et_dailyGoal          = findViewById(R.id.et_dailygoal);
        iv_userPicture        = findViewById(R.id.iv_userProfilePic);
        iv_weatherIcon        = findViewById(R.id.iv_weatherIcon);
        btn_leaderboard       = findViewById(R.id.btn_user_leaderboard);
        tv_email              = findViewById(R.id.tv_userEmail);





    }


    private BroadcastReceiver broadcastReceiverSteps = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stepsCounted = intent.getIntExtra("counted_steps",0);

            Log.v("bcSteps","steps counted:" + stepsCounted);
            tv_stepsToday.setText(String.valueOf(stepsCounted));

            if (stepsCounted != prevStepsCounted){
                totalsteps = totalsteps + stepsCounted - prevStepsCounted;
                tv_stepsTotal.setText(String.valueOf(totalsteps));
                prevStepsCounted = stepsCounted;
            }
        }
    };

    private BroadcastReceiver broadcastReceiverWeather = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> weatherMessage = intent.getStringArrayListExtra("temp");
            Log.v("bcWeather","weathermessage:" + weatherMessage);

            tv_weatherTemp.setText(weatherMessage.get(0));
            tv_weatherFeelsLike.setText(weatherMessage.get(1));
            tv_weatherHumid.setText(weatherMessage.get(2));
            tv_weatherDescription.setText(weatherMessage.get(3));
            Picasso.get().load(weatherMessage.get(4)).error(R.drawable.noimage).into(iv_weatherIcon);
        }
    };
}
