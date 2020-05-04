package com.smap.group29.getmoving.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import java.util.Calendar; // used for resetting stepcounter every day
import java.util.Date;


import com.smap.group29.getmoving.service.GetMovingService;

public class StepCounter implements SensorEventListener {

    private GetMovingService mGetMovingService;
    private final SensorManager mSensorManager;
    private final Sensor mStepCounterSensor;
    //private final Sensor mAccelerometerSensor;
    private int stepsCounted = 0;
    private double accelerometer = 0;

    Date currentTime = Calendar.getInstance().getTime();


    public StepCounter(GetMovingService activity) {
        this.mGetMovingService = activity;
        mSensorManager = (SensorManager)this.mGetMovingService.getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mSensorManager.registerListener(this,mStepCounterSensor,mSensorManager.SENSOR_DELAY_UI);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.v("event",String.valueOf(event.values[0]));
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            stepsCounted = (int) event.values[0];
            currentTime.toString();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //no use
    }

    public int getSteps(){
        Log.v("getSteps",String.valueOf(stepsCounted));

        return stepsCounted;
    }

    public void unRegisterStepCounter(){
        mSensorManager.unregisterListener(this,mStepCounterSensor);
    }



}
