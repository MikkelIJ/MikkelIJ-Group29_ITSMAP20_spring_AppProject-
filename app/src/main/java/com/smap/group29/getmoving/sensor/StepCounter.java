package com.smap.group29.getmoving.sensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.smap.group29.getmoving.service.GetMovingService;
import static android.hardware.Sensor.TYPE_STEP_COUNTER;

public class StepCounter extends Activity implements SensorEventListener {

    private GetMovingService mGetMovingService;
    private final SensorManager mSensorManager;
    private final Sensor mStepCounterSensor;
    private long currentSteps;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public StepCounter(GetMovingService activity) {
        this.mGetMovingService = activity;
        mSensorManager = (SensorManager)this.mGetMovingService.getSystemService(Context.SENSOR_SERVICE);
        mStepCounterSensor = mSensorManager.getDefaultSensor(TYPE_STEP_COUNTER);
        mSensorManager.registerListener(this,mStepCounterSensor, SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        currentSteps = (long) event.values[0]; // event returns step counts as double. It is only reset at reboot.

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //no use
    }



    // method for accessing step value from GetMovingService
    public long getSteps(){
        //Log.v("getSteps",String.valueOf(currentSteps));
        return currentSteps;
    }
    // used when app is terminated ( onDestroy)
    public void unRegisterStepCounter(){
        mSensorManager.unregisterListener(this,mStepCounterSensor);
    }

}
