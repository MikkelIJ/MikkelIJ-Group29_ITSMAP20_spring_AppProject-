package com.smap.group29.getmoving.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smap.group29.getmoving.R;
import com.smap.group29.getmoving.onlineAPI.OpenWeatherAPI;
import com.smap.group29.getmoving.sensor.StepCounter;
import com.smap.group29.getmoving.utils.FirebaseUtil;
import com.smap.group29.getmoving.utils.GlobalConstants;
import com.smap.group29.getmoving.utils.Notifications;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private FirebaseUtil firebaseUtil;
    private Date currentTime = Calendar.getInstance().getTime();
    private int currentDay = 4;
    private int currentSteps = 666;

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
        getValFirebase();
        listenToFirebase();

    }

    //inspired by https://www.youtube.com/watch?v=FbpD5RZtbCc
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // service stops after 1 minute if this notification is not set
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_walk)
                    .setContentTitle("Notification from GetMoving")
                    .setContentText("goalReached")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();

            startForeground(1, notification);

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
            if (mStepCounter.getSteps() != currentSteps){
                broadcastSteps();
            }
            mHandler.postDelayed(this,1000);
        }
    };


    public Runnable updateStepsLeaderBoard = new Runnable() {
        @Override
        public void run() {

            if (mStepCounter.getSteps() != 0)
            {
                updateDailySteps(mStepCounter.getSteps());
                if (currentTime.getDay() != currentDay){
                    updateDailySteps(0);
                    currentDay = currentTime.getDay();
                }
            }
            tellProgressbarToStart();
            Log.v("sec","runnable activated");
            mHandler.postDelayed(this,30000);


        }
    };


    public void updateDailySteps(int stepValue){


            String userID = mAuth.getCurrentUser().getUid();
            //creating new document and storing the data with hashmap
            DocumentReference documentReference = db.collection(GlobalConstants.FIREBASE_USER_COLLECTION).document(userID);

            Map<String, Object> steps = new HashMap<>();
            steps.put("dailysteps", String.valueOf(stepValue));
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

    public void updateUserValueFirebase(final String string, final String value){

        dbRef.document().update(string,value).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v("updatevalue","userValue " + string + "was updated to" + value);
            }
        });
    }

    private void listenToFirebase(){
//        //setting up the data from firebase user
//        final DocumentReference documentReference = db.collection(GlobalConstants.FIREBASE_USER_COLLECTION).document(userID);
//        documentReference.addSnapshotListener(, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if (e!=null){
//                    Log.v("onEvent","Error:"+e.getMessage());
//                }else {
//                    Log.v("fb", "DocumentSnapshot data: " + documentSnapshot.get("name"));
//                }
//            }
//        });
    }

    public String userClickedLeaderboard(){
        return "userClicked";
    }

}
