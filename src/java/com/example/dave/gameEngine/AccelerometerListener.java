package com.example.dave.gameEngine;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by mfaella on 28/02/16.
 */
public class AccelerometerListener implements SensorEventListener {

    private GameSection gw=null;

    public AccelerometerListener(){}

    public void setGW(GameSection gw){
        this.gw = gw;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(gw==null) return;

        float x = event.values[0], y = event.values[1], z = event.values[2];
        gw.setGravity(-x, y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // NOP
    }
}
