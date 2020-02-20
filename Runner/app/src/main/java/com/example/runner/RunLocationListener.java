package com.example.runner;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class RunLocationListener implements LocationListener {

    //initialise location variable
    private Location lastLocation = null;

    //update location
    @Override
    public void onLocationChanged(Location location) {
        Log.d("mdp", location.getLatitude() + " " + location.getLongitude());
        lastLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // information about the signal, i.e. number of satellites
        Log.d("mdp", "onStatusChanged: " + provider + " " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        // the user enabled (for example) the GPS
        Log.d("mdp", "onProviderEnabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // the user disabled (for example) the GPS
        Log.d("mdp", "onProviderDisabled: " + provider);
    }

    //get function
    public Location getlastLocation() {
        return lastLocation;
    }
}
