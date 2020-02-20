package com.example.runner;

import android.os.Handler;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

public class Runner {

    private long time;
    private float distance;
    private boolean isRecording;
    Handler h = new Handler();
    private Location lastLocation = null;
    private boolean newThread = true;

    private LocationManager locationManager;
    private RunLocationListener locationListener;

    public Runner(Context mContext) {
        //intialise vars
        isRecording = false;
        time = 0;

        //use main activity context to set up location manager and listener
        locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        Log.d("mdp", "gotten system service");
        locationListener = new RunLocationListener();

        //intialise location
        try {
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e) {
            Log.d("mdp", e.toString());
        }

        //setup recurrent location updates
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5, locationListener);
        } catch (SecurityException e) {
            Log.d("mdp", e.toString());
        }
    }

    //is session in progress?
    public Boolean isRecording() {
        return isRecording;
    }

    //start session. Either start new or resume old
    public void start(Boolean reset) {
        if (!isRecording()) {
            if (reset) { //if starting new session, reset vars
                distance = 0;
                time = 0;
            }

            isRecording = true;

            Log.d("mdp", "Thread state = " + run.currentThread().getState());

            //only initialise thread if it has not been already
            if (newThread) {
                run.start();
                newThread = false;
            }
        }
    }

    //halt session. Either final or temporary pause
    public void stop() {
        if (isRecording()) {
            isRecording = false;
            //run.interrupt();
        }
    }

    //get session time
    public long getTime() {
        return time;
    }

    //get session distance
    public float getDistance() {
        return distance;
    }

    //thread for timing
    Thread run = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (isRecording()) { //only update time if session is in progress
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            secondLapse();
                        }
                    });
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    });

    //update every second. Increment time and update distance
    private void secondLapse() {
        time++;
        if (time%1 == 0) {//every 5 seconds update location
            updateLocation();
        }
    }

    //update location and distance
    private void updateLocation() {
        //Find distance in metres between previous known location and new only if location has changed
        Location newLocation = locationListener.getlastLocation();

        //only update distance if location has changed
        if (lastLocation != newLocation) {
            float newDistance = lastLocation.distanceTo(newLocation);

            //add new distance
            distance = distance + newDistance;

            //update location
            lastLocation = newLocation;
            Log.d("mdp", "Distance = " + distance);
        }
    }
}
