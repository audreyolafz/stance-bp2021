package com.blueprint.tiltsensor.data_model;

/*
    Adithya Selvakumar
 */

public class Statistics {

    private float azimuthAngle;
    private float rollAngle;
    private float pitchAngle;
    private long timeElapsed; //in milliseconds

    public Statistics(float azimuthAngle, float rollAngle, float pitchAngle, long timeElapsed) {
        this.azimuthAngle = azimuthAngle;
        this.rollAngle = rollAngle;
        this.pitchAngle = pitchAngle;
        this.timeElapsed = timeElapsed;
    }

    public float getAzimuthAngle() {
        return azimuthAngle;
    }

    public void setAzimuthAngle(float azimuthAngle) {
        this.azimuthAngle = azimuthAngle;
    }

    public float getRollAngle() {
        return rollAngle;
    }

    public void setRollAngle(float rollAngle) {
        this.rollAngle = rollAngle;
    }

    public float getPitchAngle() {
        return pitchAngle;
    }

    public void setPitchAngle(float pitchAngle) {
        this.pitchAngle = pitchAngle;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

}
