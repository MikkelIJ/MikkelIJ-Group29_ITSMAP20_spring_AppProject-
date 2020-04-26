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

public class GetMovingService extends Service implements SensorEventListener {

    // inspired by https://github.com/SenSaa/Pedometer/blob/master/app/src/main/java/com/example/yusuf/pedometer/StepCountingService.java

    private static final String LOGD = "getmovingservice";
    public static final String BROADCAST_ACTION = "com.smap.group29.getmoving.service.getmovingservice_broadcast";


    // using Sensormanager to access stepsensors.
     SensorManager mSensorManager;
     Sensor mStepCounterSensor;
     Sensor mStepDetectorSensor;

    // check service running
     boolean mBound = false;

     int mCurrentStepsDetected;
     int mStepCounter;
     int mNewStepCounter;

     Intent stepIntent;

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
        mSensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (mStepCounterSensor != null){
            Toast.makeText(this,"Started counting steps",Toast.LENGTH_LONG).show();
            mSensorManager.registerListener(this,mStepCounterSensor,SensorManager.SENSOR_DELAY_UI);
        }else {
            Toast.makeText(this,"Device not compatible :(",Toast.LENGTH_LONG).show();
        }
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (mStepDetectorSensor != null){
            Toast.makeText(this,"Started counting steps",Toast.LENGTH_LONG).show();
            mSensorManager.registerListener(this,mStepDetectorSensor,SensorManager.SENSOR_DELAY_UI);
        }else {
            Toast.makeText(this,"Device not compatible :(",Toast.LENGTH_LONG).show();
        }

        mHandler.removeCallbacks(updateBroadcastData);
        mHandler.post(updateBroadcastData);

        mBound = true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mBound = false;
        Log.v("service", "Stopped");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            int step_counted = (int) event.values[0];

            if (mStepCounter == 0){
                mStepCounter = (int) event.values[0];
            }
            mNewStepCounter = step_counted - mStepCounter;

        }
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            int step_detected = (int) event.values[0];
            mCurrentStepsDetected += step_detected;
        }

        Log.v("service steps counted ",String.valueOf(mNewStepCounter));
        Log.v("service steps detected ",String.valueOf(mCurrentStepsDetected));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
        stepIntent.putExtra("counted_steps",mNewStepCounter);
        stepIntent.putExtra("detected_steps",mCurrentStepsDetected);
        sendBroadcast(stepIntent);
    }

}
