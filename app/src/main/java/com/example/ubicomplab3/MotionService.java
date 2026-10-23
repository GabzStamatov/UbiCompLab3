package com.example.ubicomplab3;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


//create the service that the button will use
public class MotionService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private HandlerThread sensorThread;
    private Handler sensorHandler;
    private float lastX;
    private float lastY;
    private float lastZ;
    private boolean firstReading = true; //tells the app if this is the first reading

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

        sensorThread = new HandlerThread("SensorThread");
        sensorThread.start(); //start a separate thread

        sensorHandler = new Handler(sensorThread.getLooper());


        //create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    "motion_channel",
                    "Motion Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    @Override //called each time the service is started
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MotionService", "Service Started");

        if (accelerometer != null) { //if the accelerometer is available
            sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL,  //delay between readings
                    sensorHandler
            );
        }

        return START_STICKY; //stay running/ attempt
    }

    @Override //terminate
    public void onDestroy() {
        super.onDestroy();

        sensorManager.unregisterListener(this);

        sensorThread.quitSafely();

        Log.d("MotionService", "Service Destroyed");
    }

    @Override //called when the sensor sends new readings (X, Y, Z)
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        //Log.d("MotionService", "X: " + x + " Y: " + y + " Z: " + z);
        if (firstReading) { //checks if this is the first reading
            lastX = x;
            lastY = y;
            lastZ = z;

            firstReading = false;
            return;
        }
        float deltaX = Math.abs(x - lastX);
        float deltaY = Math.abs(y - lastY);
        float deltaZ = Math.abs(z - lastZ);

        if (deltaX > 2 || deltaY > 2 || deltaZ > 2) {
            Log.d("MotionService", "Movement Detected!");
            showNotification();
        }

        //update the last readings
        lastX = x;
        lastY = y;
        lastZ = z;

    }

    @Override //called when the sensor's accuracy changes, but will not be used
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //builds and posts the notification
    private void showNotification() {

        String time = new SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        Notification.Builder builder =
                new Notification.Builder(this, "motion_channel")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Movement Detected!")
                        .setContentText("Phone moved at " + time)
                        .setAutoCancel(true);

        Notification notification = builder.build();

        NotificationManager manager =
                getSystemService(NotificationManager.class);

        manager.notify(1, notification); //show the notification

    }


}
