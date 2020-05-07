package com.smap.group29.getmoving.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.activities.UserActivity;
import com.smap.group29.getmoving.onlineAPI.OpenWeatherAPI;
import com.smap.group29.getmoving.sensor.StepCounter;
import com.smap.group29.getmoving.utils.GlobalConstants;
import com.smap.group29.getmoving.utils.Notifications;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import static com.smap.group29.getmoving.utils.Notifications.CHANNEL_1_ID;

public class GetMovingService extends Service {

    // inspired by https://github.com/SenSaa/Pedometer/blob/master/app/src/main/java/com/example/yusuf/pedometer/StepCountingService.java

    private static final String LOGD = "getmovingservice";
    public static final String BROADCAST_ACTION_STEPS = "com.smap.group29.getmoving.service.getmovingservice_broadcast";

    private StepCounter mStepCounter;
    private OpenWeatherAPI mOpenWeatherAPI;
    private NotificationManagerCompat notificationManager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private String userID, dailyGoal;
    private CollectionReference dbRef = db.collection(GlobalConstants.FIREBASE_USER_COLLECTION);
    TextView steps;

    private Calendar c; // c.get(Calendar.SECOND)
    private Notifications mNotifications = new Notifications();



    // check service running


     Intent stepIntent, weatherIntent, timerIntent;

    // handler for broadcasting data at x intervals
    public final Handler mHandler = new Handler();


    // Binder
    private final IBinder binder = new LocalBinder();
    // source https://developer.android.com/guide/components/bound-services
    public class LocalBinder extends Binder {
        public GetMovingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return GetMovingService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // declares global broadcast
        stepIntent = new Intent(BROADCAST_ACTION_STEPS);
        timerIntent = new Intent("TIMER");

        mStepCounter = new StepCounter(this);
        mOpenWeatherAPI = new OpenWeatherAPI(this);
        mHandler.removeCallbacks(updateBroadcastData);
        mHandler.post(updateBroadcastData);
        mHandler.removeCallbacks(updateStepsLeaderBoard);
        mHandler.post(updateStepsLeaderBoard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = mAuth.getCurrentUser().getUid();


        notificationManager = NotificationManagerCompat.from(this);



    }

    //inspired by https://www.youtube.com/watch?v=FbpD5RZtbCc
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String dailygoal = intent.getStringExtra("dailyGoal");
        String dailysteps = intent.getStringExtra("dailySteps");
        String goalReached = "You have reached your daily goal of " + dailyGoal + " steps";

        //Opens the userActivty from the notification
        Intent notificationIntent = new Intent(this, UserActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,notificationIntent,0);


        if (dailygoal == dailysteps){
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_walk)
                    .setContentTitle("Notification from GetMoving")
                    .setContentText(goalReached)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();

            startForeground(1, notification);

        }


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mStepCounter.unRegisterStepCounter();
        mHandler.removeCallbacks(updateBroadcastData);
        mHandler.removeCallbacks(updateStepsLeaderBoard);
        Log.v("service", "Stopped");
    }

    public void createNotification(){

        String dailySteps = String.valueOf(mStepCounter.getSteps());
        String goalReached = "You have reached your daily goal of " + dailyGoal + " steps";
        if(dailySteps == dailyGoal){
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_walk)
                    .setContentTitle("Notification from GetMoving")
                    .setContentText(goalReached)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            notificationManager.notify(1, notification);

        }

    }


    public void GM_removeCallbacks(){
        mHandler.removeCallbacks(updateStepsLeaderBoard);
    }





    // runnable used for async method
    public Runnable updateBroadcastData = new Runnable() {
        @Override
        public void run() {
            // only allow if stepcounterservice is active

                broadcastSteps();
                mHandler.postDelayed(this,1000);

        }
    };


    public Runnable updateStepsLeaderBoard = new Runnable() {
        @Override
        public void run() {

            Log.v("sec","runnable activated");
            updateDailySteps();
            tellProgressbarToStart();
            mHandler.postDelayed(this,30000);

        }
    };


    public void updateDailySteps(){


            String userID = mAuth.getCurrentUser().getUid();
            //creating new document and storing the data with hashmap
            DocumentReference documentReference = db.collection(GlobalConstants.FIREBASE_USER_COLLECTION).document(userID);

            Map<String, Object> steps = new HashMap<>();
            steps.put("dailysteps", String.valueOf(mStepCounter.getSteps()));
            dbRef.document(userID).update(steps).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Toast.makeText(GetMovingService.this, "steps updated" + mStepCounter.getSteps(),Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.v("updatesteps", e.getMessage());
                }
            });

    }


    private void broadcastSteps(){
        Log.v("getSteps", "broadcasting step values");
        stepIntent.putExtra("counted_steps",mStepCounter.getSteps());
        sendBroadcast(stepIntent);
    }

    private void tellProgressbarToStart(){
        Log.v("sec","tellprogressbarToStart activated");
        timerIntent.putExtra("startprogressbar",1);
        sendBroadcast(timerIntent);
    }


}
