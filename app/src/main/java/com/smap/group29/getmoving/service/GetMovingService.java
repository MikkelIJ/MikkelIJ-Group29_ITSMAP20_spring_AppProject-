package com.smap.group29.getmoving.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.model.NewUser;
import com.smap.group29.getmoving.activities.UserActivity;
import com.smap.group29.getmoving.model.NewUser;
import com.smap.group29.getmoving.onlineAPI.OpenWeatherAPI;
import com.smap.group29.getmoving.sensor.StepCounter;
import com.smap.group29.getmoving.utils.DataHelper;
import com.smap.group29.getmoving.utils.GlobalConstants;
import com.smap.group29.getmoving.utils.Notifications;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.smap.group29.getmoving.utils.GlobalConstants.CHANNEL_ID1;
import static com.smap.group29.getmoving.utils.GlobalConstants.CHANNEL_ID2;
import static com.smap.group29.getmoving.utils.GlobalConstants.CHANNEL_NAME1;
import static com.smap.group29.getmoving.utils.GlobalConstants.CHANNEL_NAME2;
import static com.smap.group29.getmoving.utils.GlobalConstants.FILE_NAME;


public class GetMovingService extends Service {

    // inspired by https://github.com/SenSaa/Pedometer/blob/master/app/src/main/java/com/example/yusuf/pedometer/StepCountingService.java

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "currentsteps";

    private static final String LOGD = "getmovingservice";
    public static final String BROADCAST_ACTION_STEPS = "com.smap.group29.getmoving.service.getmovingservice_broadcast";

    private StepCounter mStepCounter;
    private OpenWeatherAPI mOpenWeatherAPI;
    private NotificationManagerCompat notificationManager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private String userID;
    //private long dailygoal;
    private CollectionReference dbRef = db.collection(GlobalConstants.FIREBASE_USER_COLLECTION);
    TextView steps;

    private NotificationManager mNotificationManager;
    private Notification mNotification;

    private Calendar c; // c.get(Calendar.SECOND)
    //private Notifications mNotifications = new Notifications();
    private DataHelper mDataHelper;

    private long milestoneStep;
    private long currentSteps;
    private long totalStepsSinceReboot;
    private long today;
    private long additionStep;
    private long dailyGoal;




    private List<String> userListIDs;


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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
        //getValFirebase();
        mDataHelper = new DataHelper(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendOnChannel1(){
        View v;
        long currentsteps = handleSteps();
        String NOTIFICATION_CHANNEL_ID = "com.example.mikkel1";

        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME2, NotificationManager.IMPORTANCE_HIGH);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        Notification notification1 = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_walk)
                .setContentTitle("GetMoving")
                .setContentText(getString(R.string.goal_reached) + dailyGoal + getString(R.string.steps))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(2,notification1);
        //startForeground(30,notification1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notification_ServiceIsLive(){
        View v;
        String NOTIFICATION_CHANNEL_ID = "com.example.mikkel2";

        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME1, NotificationManager.IMPORTANCE_LOW);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        long currentsteps = 1111;
        Notification notification2 = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_walk)
                .setContentTitle("GetMoving")
                .setContentText(getString(R.string.Service_live))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOngoing(false)
                .build();


        startForeground(20,notification2);
    }

    //inspired by https://www.youtube.com/watch?v=FbpD5RZtbCc
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    //    service is killed by android system after 1 minute if this notification is not set

        notification_ServiceIsLive();
        getUserDataFirebase();

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentTitle("GetMoving is counting your steps in the background")
                .setContentText("Steps today: " + handleSteps())
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(200, notification);
    }

    public void createNotificationChannels(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID1,
                    CHANNEL_NAME1,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            mNotificationManager = getSystemService(NotificationManager.class);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(serviceChannel);
            //mNotificationManager.notify();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mStepCounter.unRegisterStepCounter();
        mHandler.removeCallbacks(updateBroadcastData);
        mHandler.removeCallbacks(updateStepsLeaderBoard);
        Log.v("service", "Stopped");
    }





    public void GM_removeCallbacks(){
        mHandler.removeCallbacks(updateStepsLeaderBoard);
    }

    private void getUserDataFirebase() {
        DocumentReference documentReference = db.collection(GlobalConstants.FIREBASE_USER_COLLECTION).document(userID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    //Log.d("TAG", "Current data: " + documentSnapshot.getData());
                    long dailySteps = documentSnapshot.getLong("dailysteps");
                    dailyGoal = documentSnapshot.getLong("dailygoal");
                    long prevDailyGoal =  getPreferences("dailygoal");
                    if (prevDailyGoal == -1) {
                        if (dailySteps > dailyGoal) {
                            sendOnChannel1();
                            savePreferences("dailygoal", dailyGoal);
                        }
                    }
                    if (prevDailyGoal != dailyGoal){
                        if (dailySteps > dailyGoal){
                            sendOnChannel1();
                            savePreferences("dailygoal", dailyGoal);
                        }
                    }
                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });
    }


    // runnable used for async method
    public Runnable updateBroadcastData = new Runnable() {
        @Override
        public void run() {

            broadcastSteps();
            //sendNotification(handleSteps());
            mHandler.postDelayed(this,10);
        }
    };


    public Runnable updateStepsLeaderBoard = new Runnable() {
        @Override
        public void run() {
            updateDailySteps(handleSteps());
            //broadcastSteps();
            tellProgressbarToStart();
            Log.v("sec","runnable activated");

            mHandler.postDelayed(this,30000);


        }
    };


    public void updateDailySteps(long stepValue){

            String userID = mAuth.getCurrentUser().getUid();

            dbRef.document(userID).update("dailysteps",stepValue).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("TAG", "DocumentSnapshot successfully updated!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("TAG", "Error updating document", e);
                }
            });



    }


    private void broadcastSteps(){
        Log.v("getSteps", "broadcasting step values");
        stepIntent.putExtra("counted_steps",handleSteps());
        sendBroadcast(stepIntent);
    }

    private void tellProgressbarToStart(){
        Log.v("sec","tellprogressbarToStart activated");
        timerIntent.putExtra("startprogressbar",1);
        sendBroadcast(timerIntent);
    }

    public void getValFirebase(){

        dbRef.document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.v("fb", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("fb", "No such document");
                    }
                } else {
                    Log.d("fb", "get failed with ", task.getException());
                }
            }
        });
    }


    private long handleSteps(){
        totalStepsSinceReboot = mStepCounter.getSteps();

        Log.d("skridt1",String.valueOf("totalStepsSinceReboot" + totalStepsSinceReboot));

        today = getPreferences(today());
        Log.d("skridt2",String.valueOf("milestoneStep 1" + milestoneStep));
        if(today == -1){ // if new day, then assign total steps to milestonestep
            milestoneStep = totalStepsSinceReboot;
            Log.d("skridt3",String.valueOf(milestoneStep));
            savePreferences(today(), milestoneStep);

            mDataHelper.save(milestoneStep);
//            int savedVal = mDataHelper.load(milestoneStep);
        }else {
            additionStep = totalStepsSinceReboot - Integer.parseInt(mDataHelper.load().trim()) ; // reset to 0
            Log.d("skridt4",String.valueOf(additionStep));
            savePreferences(today(), additionStep);
            currentSteps = additionStep;
            Log.d("skridt5",String.valueOf(currentSteps));
        }

        Log.d("event",String.valueOf(milestoneStep));

        return currentSteps;
    }

    public String today(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(Calendar.getInstance().getTime());
    }

    private void savePreferences(String key, long value) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(key,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private long getPreferences(String key){
        SharedPreferences sharedPreferences = this.getSharedPreferences(key,MODE_PRIVATE);

        return sharedPreferences.getLong(key,-1);
    }



}
