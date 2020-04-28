package com.smap.group29.getmoving.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GetMovingService extends Service {

    // inspired by https://github.com/SenSaa/Pedometer/blob/master/app/src/main/java/com/example/yusuf/pedometer/StepCountingService.java

    private static final String LOGD = "getmovingservice";
    public static final String BROADCAST_ACTION = "com.smap.group29.getmoving.service.getmovingservice_broadcast";

    private StepCounter mStepCounter;
    private OpenWeatherAPI mOpenWeatherAPI;

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

        // declares global broadcast
        stepIntent = new Intent(BROADCAST_ACTION);


        mStepCounter = new StepCounter(this);
        mOpenWeatherAPI = new OpenWeatherAPI(this);
        mHandler.removeCallbacks(updateBroadcastData);
        mHandler.post(updateBroadcastData);

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
                mHandler.postDelayed(this,1000);
            }
        }
    };


    private void broadcastSteps(){
        Log.d(LOGD, "broadcasting step values");
        stepIntent.putExtra("counted_steps",mStepCounter.getSteps());
        sendBroadcast(stepIntent);
    }

    private void broadcastWeather(){
        Log.d(LOGD, "broadcasting weather temp and city");
        mOpenWeatherAPI.sendRequest();
        stepIntent.putExtra("counted_steps",mStepCounter.getSteps());
        sendBroadcast(stepIntent);
    }

}
