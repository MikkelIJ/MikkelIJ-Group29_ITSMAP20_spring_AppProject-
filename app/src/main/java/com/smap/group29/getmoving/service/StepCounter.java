package com.smap.group29.getmoving.service;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class StepCounter implements SensorEventListener {

    GetMovingService mActivity;
    private final SensorManager mSensorManager;
    private final Sensor mStepCounterSensor;
    private final Sensor mAccelerometerSensor;
    private int stepsCounted = 0;
    private int accelerometer = 0;

    public StepCounter(GetMovingService activity) {
        this.mActivity = activity;
        mSensorManager = (SensorManager)this.mActivity.getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,mStepCounterSensor,mSensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this,mAccelerometerSensor,mSensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.v("event",String.valueOf(event.values[0]));

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            stepsCounted = stepsCounted + (int) event.values[0];
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accelerometer = (int) event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //no use
    }

    public int getSteps(){
        Log.v("getSteps",String.valueOf(stepsCounted));
        Log.v("getAccel",String.valueOf(accelerometer));
        return stepsCounted;
    }
}
