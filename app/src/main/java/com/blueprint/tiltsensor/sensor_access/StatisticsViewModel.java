package com.blueprint.tiltsensor.sensor_access;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;

import com.blueprint.tiltsensor.data_model.Statistics;

import java.util.Timer;
import java.util.TimerTask;

public class StatisticsViewModel extends AndroidViewModel implements SensorEventListener{

    private MutableLiveData<Statistics> statistics;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private long beginTime;


    public StatisticsViewModel(Application ctx){
        super(ctx);

        beginTime = System.currentTimeMillis();

        mSensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SensorManager mSensorMgr = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);

        HandlerThread mHandlerThread = new HandlerThread("sensorThread");

        mHandlerThread.start();

        Handler handler = new Handler(mHandlerThread.getLooper());

        mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, handler);

        mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, handler);

    }


    public LiveData<Statistics> getStatistics() {
        if (statistics == null) {
            statistics = new MutableLiveData<Statistics>();
            loadData();
        }
        return statistics;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(sensorEvent.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(sensorEvent.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void loadData() {

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                int axisX = SensorManager.AXIS_X;
                int axisZ = SensorManager.AXIS_Z;

                float[] adjustedRotationMatrix = new float[9];

                mSensorManager.getRotationMatrix(mRotationMatrix, null,
                        mAccelerometerReading, mMagnetometerReading);

                SensorManager.remapCoordinateSystem(mRotationMatrix,axisX,axisZ,adjustedRotationMatrix);

                mSensorManager.getOrientation(adjustedRotationMatrix,mOrientationAngles);

                float azimuth = mOrientationAngles[0] * -57;
                float pitch = mOrientationAngles[1] * -57;
                float roll = mOrientationAngles[2] * -57;

                Statistics stat = new Statistics(azimuth,pitch,roll,
                        System.currentTimeMillis() - beginTime);

                statistics.postValue(stat);
            }

        }, 0, 20);

    }

}
