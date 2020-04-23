package com.smap.group29.getmoving;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//this class has been inspired by https://www.youtube.com/watch?v=CNGMWnmldaU and
// https://hub.packtpub.com/step-detector-and-step-counters-sensors/

public class StepCounter extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    boolean running;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(stepSensor != null){
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI);

        }
        else{
            Toast.makeText(this,"Sensor not found",Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

     //   txtCountedSteps.setText(String.valueOf(sensorEvent.values[0]));

    }





}
