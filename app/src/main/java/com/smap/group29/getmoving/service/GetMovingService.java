package com.smap.group29.getmoving.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smap.group29.getmoving.NewUserActivity;


import java.util.HashMap;
import java.util.Map;

public class GetMovingService extends Service {

    // inspired by https://github.com/SenSaa/Pedometer/blob/master/app/src/main/java/com/example/yusuf/pedometer/StepCountingService.java


    private static final String LOGD = "getmovingservice";
    public static final String BROADCAST_ACTION_STEPS = "com.smap.group29.getmoving.service.getmovingservice_broadcast";

    private StepCounter mStepCounter;
    private OpenWeatherAPI mOpenWeatherAPI;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference dbRef = db.collection("KspUsers");

    private String userID;

    // check service running
     boolean mBound = false;

     Intent stepIntent, weatherIntent;

    // handler for broadcasting data at x intervals
    private final Handler mHandler = new Handler();


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
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            userID = acct.getId();
        }

        // declares global broadcast
        stepIntent = new Intent(BROADCAST_ACTION_STEPS);

        mStepCounter = new StepCounter(this);
        mOpenWeatherAPI = new OpenWeatherAPI(this);
        mHandler.removeCallbacks(updateBroadcastData);
        mHandler.post(updateBroadcastData);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        mBound = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBound = false;
        Log.v("service", "Stopped");
    }


    // runnable used for async method
    public Runnable updateBroadcastData = new Runnable() {
        @Override
        public void run() {
            // only allow if stepcounterservice is active
            if (mBound){
                broadcastSteps();
                updateDailySteps();
                mHandler.postDelayed(this,1000);
            }
        }
    };

    public void updateDailySteps(){


        //creating new document and storing the data with hashmap
        DocumentReference documentReference = mStore.collection("KspUsers").document(userID);

        Map<String,Object> steps = new HashMap<>();
        steps.put("dailysteps", String.valueOf(mStepCounter.getSteps()));
        dbRef.document(userID).update(steps).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(GetMovingService.this, "steps updated" + mStepCounter.getSteps(),Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v("updatesteps",e.getMessage());
            }
        });
    }


    private void broadcastSteps(){
        Log.d(LOGD, "broadcasting step values");
        stepIntent.putExtra("counted_steps",mStepCounter.getSteps());
        sendBroadcast(stepIntent);
    }
}
