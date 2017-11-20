package com.striketec.mobile.util;

import android.os.Handler;

/**
 * Stopwatch timer class to track training time.
 *
 * @author #1053
 */
public class TrainingTimer implements Runnable {

    private Handler customHandler;

    private String timerTime = "";

    private long elapsedTime = 0L;

    private long secs, mins, hrs;

    private String seconds, minutes, hours;

    private long startTime = System.currentTimeMillis();

    public TrainingTimer(Handler handler) {
        this.customHandler = handler;
        startTime = System.currentTimeMillis();
        this.customHandler.postDelayed(this, 100);
    }

    public long getStartTime() {
        return startTime;
    }

    public void stopTimer() {
        customHandler.removeCallbacks(this);
        customHandler = null;
    }

    public String getTimerTime() {
        return timerTime;
    }

    @Override
    public void run() {
        elapsedTime = System.currentTimeMillis() - startTime;

        secs = (long) (elapsedTime / 1000);
        mins = (long) (secs / 60);
        hrs = (long) (mins / 60);

        // Seconds
        secs = secs % 60;
        seconds = String.valueOf(secs);
        if (secs == 0) {
            seconds = "00";
        }
        if (secs < 10 && secs > 0) {
            seconds = "0" + seconds;
        }

        // Minutes
        mins %= 60;
        minutes = String.valueOf(mins);
        if (mins == 0) {
            minutes = "00";
        }
        if (mins < 10 && mins > 0) {
            minutes = "0" + minutes;
        }

        // Hours
        hours = String.valueOf(hrs);
        if (hrs == 0) {
            hours = "00";
        }
        if (hrs < 10 && hrs > 0) {
            hours = "0" + hours;
        }

        timerTime = hours + ":" + minutes + ":" + seconds;

        // Set the time on BagTrainingOneFragment UI
        customHandler.postDelayed(this, 100);
    }
}
