package com.smap.group29.getmoving.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.sensor.StepCounter;
import com.smap.group29.getmoving.service.GetMovingService;
import com.smap.group29.getmoving.service.GetMovingService.LocalBinder;
import com.smap.group29.getmoving.onlineAPI.OpenWeatherAPI;
import com.smap.group29.getmoving.utils.DataHelper;
import com.smap.group29.getmoving.utils.GlobalConstants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.smap.group29.getmoving.service.GetMovingService.BROADCAST_ACTION_STEPS;
import static com.smap.group29.getmoving.onlineAPI.OpenWeatherAPI.BROADCAST_ACTION_WEATHER;


public class UserActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    String userID;



    private static final String LOGD = "userActivity";

    private Button btn_leaderboard;
    private TextView tv_name, tv_age, tv_city, tv_weatherTemp,tv_weatherFeelsLike, tv_weatherHumid, tv_weatherDescription, tv_stepsToday, tv_stepsTotal, tv_email, tv_dailyGoal;
    private ImageView iv_userPicture, iv_weatherIcon;

    private Intent stepIntent;
    private NotificationManagerCompat notificationManager;

    private GetMovingService mService;
    private boolean mBound = false;

    private OpenWeatherAPI mOpenWeatherAPI;
    private static UserActivity mCurrentInstance;

    private IntentFilter getCurrentStepsFilter = new IntentFilter();
    private IntentFilter getCurrentWeatherFilter = new IntentFilter();

    private long stepsCounted = 0;
    private long prevStepsCounted = 0;
    private long stepsTotal = 0;



    private StepCounter mStepCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        notificationManager = NotificationManagerCompat.from(this);

        loadPic();
        initUI();
        getUserDataFirebase();




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




    public void loadPic(){
        //Waiting one second with loading the picture to make sure the picture is on firebase before trying to load it into the imageview

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                final StorageReference imgProfile = storageReference.child("users/"+ mAuth.getUid()+"profile.jpg");
                imgProfile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(iv_userPicture);
                    }
                });
            }
        }, 1000);   //5 seconds

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
        registerReceiver(broadcastReceiverSteps,getCurrentStepsFilter);
        registerReceiver(broadcastReceiverWeather,getCurrentWeatherFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mService.updateUserValueFirebase("stepstotal",stepsTotal+"");
        unbindService(serviceConnection);
        mBound = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(stepIntent);
    }

    private void openSettings() {
        Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
        intent.putExtra("name", tv_name.getText().toString());
        intent.putExtra("age", tv_age.getText().toString());
        intent.putExtra("city", tv_city.getText().toString());
        intent.putExtra("steps", tv_dailyGoal.getText().toString());
        intent.putExtra("email", tv_email.getText().toString());

        startActivity(intent);
    }



    public void logout(){
        mService.GM_removeCallbacks();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();

    }


    private void initService(){

        // Binds ListActivity to the WordlearnerService.
        stepIntent = new Intent(this,GetMovingService.class);
        startService(stepIntent); //startservice() is needed for the app to run forever. Though bindService() should call startService() i can not get it to work otherwise
        bindService(stepIntent,serviceConnection,Context.BIND_AUTO_CREATE); // bindService() makes it posible to use mService and use it to execute methods in the service
    }

    private void registerIntentFilters(){
        getCurrentStepsFilter.addAction(BROADCAST_ACTION_STEPS);
        registerReceiver(broadcastReceiverSteps,getCurrentStepsFilter);

        getCurrentWeatherFilter.addAction(BROADCAST_ACTION_WEATHER);
        registerReceiver(broadcastReceiverWeather,getCurrentWeatherFilter);
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            mService.GM_removeCallbacks();
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
        tv_dailyGoal          = findViewById(R.id.et_dailygoal);
        iv_userPicture        = findViewById(R.id.iv_userProfilePic);
        iv_weatherIcon        = findViewById(R.id.iv_weatherIcon);
        btn_leaderboard       = findViewById(R.id.btn_user_leaderboard);
        tv_email              = findViewById(R.id.tv_userEmail);

    }

/*
    public void createNotification(){
        String dailySteps = (tv_stepsToday.getText().toString());
        String dailyGoal = (tv_dailyGoal.getText().toString());


        Intent serviceIntent = new Intent(this, UserActivity.class);
        serviceIntent.putExtra("dailySteps", dailySteps);
        serviceIntent.putExtra("dailyGoal", dailyGoal);

        ContextCompat.startForegroundService(this, serviceIntent);

    }

 */


    private BroadcastReceiver broadcastReceiverSteps = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stepsCounted = intent.getLongExtra("counted_steps",0);

            Log.v("bc","steps counted:" + stepsCounted);
            tv_stepsToday.setText(String.valueOf(stepsCounted));
            if (stepsCounted > prevStepsCounted){
                stepsTotal = stepsTotal + stepsCounted - prevStepsCounted;
                tv_stepsTotal.setText(String.valueOf(stepsTotal));
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
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().load(weatherMessage.get(4)).error(R.drawable.noimage).into(iv_weatherIcon);
        }
    };


    private void getUserDataFirebase(){
        DocumentReference documentReference = mStore.collection(GlobalConstants.FIREBASE_USER_COLLECTION).document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.v("fb", "DocumentSnapshot data: " + document.getData());
                    //stepsTotal = Integer.parseInt(document.getString("stepstotal"));
                    //tv_stepsTotal.setText(document.getString("stepstotal"));
                    tv_name.setText(document.getString("name"));
                    tv_email.setText(document.getString("email"));
                    tv_age.setText(document.getString("age"));
                    tv_city.setText(document.getString("city"));
                    tv_dailyGoal.setText(String.valueOf(document.getLong("dailygoal")));
                    } else {
                        Log.d("fb", "No such document");
                    }
                } else {
                    Log.d("fb", "get failed with ", task.getException());
                }
            }
        });
    }

}
