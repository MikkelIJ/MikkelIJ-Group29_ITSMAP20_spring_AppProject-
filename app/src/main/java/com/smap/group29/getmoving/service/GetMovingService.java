package com.smap.group29.getmoving.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.onlineAPI.OpenWeatherAPI;
import com.smap.group29.getmoving.sensor.StepCounter;
import com.smap.group29.getmoving.utils.DataHelper;
import com.smap.group29.getmoving.utils.GlobalConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


import static com.smap.group29.getmoving.utils.GlobalConstants.CHANNEL_NAME1;
import static com.smap.group29.getmoving.utils.GlobalConstants.CHANNEL_NAME2;
import static com.smap.group29.getmoving.utils.GlobalConstants.CHANNEL_NAME3;


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

    private Calendar c; // c.get(Calendar.SECOND)
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
    public void notification_YouGotTheLead(){
        //Setting up notification when user gets the lead on leaderboard
        View v;
        String NOTIFICATION_CHANNEL_ID = "com.example.mikkel3";

        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME3, NotificationManager.IMPORTANCE_HIGH);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        Notification notification3 = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_walk)
                .setContentTitle("GetMoving")
                .setContentText(getString(R.string.you_got_the_lead))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(3,notification3);
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
        getUserWithMostSteps();

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
                    long prevDailyGoal =  getPreferencesLong("dailygoal");
                    if (prevDailyGoal == -1) {
                        if (dailySteps > dailyGoal) {
                            sendOnChannel1();
                            savePreferencesLong("dailygoal", dailyGoal);
                        }
                    }
                    if (prevDailyGoal != dailyGoal){
                        if (dailySteps > dailyGoal){
                            sendOnChannel1();
                            savePreferencesLong("dailygoal", dailyGoal);
                        }
                    }
                } else {
                    Log.d("TAG", "Current data: null");
                }
            }
        });
    }

    private void getUserWithMostSteps(){
        dbRef.orderBy ("dailysteps",Query.Direction.DESCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){

                    if (doc.getData() != null) {
                        String flag = getPreferencesString("lead");
                        String storeID = (String) doc.get("uID");
                        String userID = mAuth.getUid();
                        if (flag.contains("lead")){
                                if (storeID.contains(userID)){
                                    notification_YouGotTheLead();
                                    savePreferencesString("lead",doc.getString("uID"));
                            }
                        }
                        if (flag.contains(userID)){
                            if (!storeID.contains(userID)){
                                savePreferencesString("lead","lead");
                            }
                        }
                    }
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

        today = getPreferencesLong(today());
        Log.d("skridt2",String.valueOf("milestoneStep 1" + milestoneStep));
        if(today == -1){ // if new day, then assign total steps to milestonestep
            milestoneStep = totalStepsSinceReboot;
            Log.d("skridt3",String.valueOf(milestoneStep));
            savePreferencesLong(today(), milestoneStep);

            mDataHelper.save(milestoneStep);
//            int savedVal = mDataHelper.load(milestoneStep);
        }else {
            additionStep = totalStepsSinceReboot - Integer.parseInt(mDataHelper.load().trim()) ; // reset to 0
            Log.d("skridt4",String.valueOf(additionStep));
            savePreferencesLong(today(), additionStep);
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

    private void savePreferencesLong(String key, long value) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(key,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private void savePreferencesString(String key, String value) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(key,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private long getPreferencesLong(String key){
        SharedPreferences sharedPreferences = this.getSharedPreferences(key,MODE_PRIVATE);

        return sharedPreferences.getLong(key,-1);
    }

    private String getPreferencesString(String key){
        SharedPreferences sharedPreferences = this.getSharedPreferences(key,MODE_PRIVATE);

        return sharedPreferences.getString(key,"lead");
    }
}
