package com.example.ubicomplab3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;


//create the service that the button will use
public class MotionService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;


    @Override
    public IBinder onBind(Intent intent) { //must include onBind() because Java requires it, but it returns null because it's not being used
        return null;
    }

    //Service life cycle methods
    @Override //called when the service is first created
    public void onCreate() {
        super.onCreate();
        Log.d("MotionService", "Service Created");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); //access the sensor manager

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //get the accelerometer sensor

    }

    @Override //called each time the service is started
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MotionService", "Service Started");
        return START_STICKY; //stay running/ attempt
    }

    @Override //terminate
    public void onDestroy() {
        super.onDestroy();
        Log.d("MotionService", "Service Destroyed");
    }

    @Override //called when the sensor sends new readings (X, Y, Z)
    public void onSensorChanged(SensorEvent event) {

    }

    @Override //called when the sensor's accuracy changes, but will not be used
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
