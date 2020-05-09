package com.smap.group29.getmoving.sensor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Calendar; // used for resetting stepcounter every day


import com.smap.group29.getmoving.service.GetMovingService;
import com.smap.group29.getmoving.utils.DataHelper;

import static android.hardware.Sensor.TYPE_STEP_COUNTER;

public class StepCounter extends Activity implements SensorEventListener {




    private GetMovingService mGetMovingService;
    private final SensorManager mSensorManager;
    private final Sensor mStepCounterSensor;
    private int milestoneStep;
    private long currentSteps;
    private long totalStepsSinceReboot;
    private int today;
    private long additionStep;
    //private DataHelper mDataHelper = new DataHelper(this);



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public StepCounter(GetMovingService activity) {
        this.mGetMovingService = activity;
        mSensorManager = (SensorManager)this.mGetMovingService.getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(TYPE_STEP_COUNTER);
        mSensorManager.registerListener(this,mStepCounterSensor, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        currentSteps = (long) event.values[0];
//        totalStepsSinceReboot = (int) event.values[0];
//
//        Log.d("skridt1",String.valueOf("totalStepsSinceReboot" + totalStepsSinceReboot));
//
//            today = getPreferences(today());
//        Log.d("skridt2",String.valueOf("milestoneStep 1" + milestoneStep));
//        if(today == -1){ // if new day, then assign total steps to milestonestep
//            milestoneStep = totalStepsSinceReboot;
//            Log.d("skridt3",String.valueOf(milestoneStep));
//            savePreferences(today(), milestoneStep);
////            mDataHelper.save(milestoneStep);
////            int savedVal = mDataHelper.load(milestoneStep);
//        }else {
//            additionStep = totalStepsSinceReboot - milestoneStep; // reset to 0
//            Log.d("skridt4",String.valueOf(additionStep));
//            savePreferences(today(), additionStep);
//            currentSteps = additionStep;
//            Log.d("skridt5",String.valueOf(currentSteps));
//        }
//
//        Log.d("event",String.valueOf(milestoneStep));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //no use
    }

    public String today(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(Calendar.getInstance().getTime());
    }

    private void savePreferences(String key, int value) {
        SharedPreferences sharedPreferences = this.mGetMovingService.getSharedPreferences(key,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private int getPreferences(String key){
        SharedPreferences sharedPreferences = this.mGetMovingService.getSharedPreferences(key,MODE_PRIVATE);

        return sharedPreferences.getInt(key,-1);
    }



    public long getSteps(){
        Log.v("getSteps",String.valueOf(currentSteps));
        return currentSteps;
    }

    public void unRegisterStepCounter(){
        mSensorManager.unregisterListener(this,mStepCounterSensor);
    }

}
