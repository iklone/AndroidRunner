package com.example.runner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RunnerService extends Service {

    private final IBinder binder = new MyBinder();
    private Runner runner;

    public RunnerService() {}

    public void onCreate(Intent intent) {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MyBinder extends Binder {
        void setRunner(Runner passedRunner) {
            runner = passedRunner;
        }

        void start(Boolean reset) {
            //Start recording distance
            runner.start(reset);
        }

        void stop() {
            //Stop recording distance
            runner.stop();
        }

        //is session in progress?
        boolean isRecording() {
            return runner.isRecording();
        }

        //get time from runner
        long getCurrentTime() {
            return runner.getTime();
        }

        //get distance from runner
        float getCurrentDistance() {
            return runner.getDistance();
        }
    }
}
