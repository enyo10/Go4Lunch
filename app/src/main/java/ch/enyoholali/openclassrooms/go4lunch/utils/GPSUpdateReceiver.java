package ch.enyoholali.openclassrooms.go4lunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;


public class GPSUpdateReceiver extends BroadcastReceiver {

    private static final String TAG =GPSUpdateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);

        Log.d(TAG,"Latitude " + location.getLatitude() + " et longitude " + location.getLongitude());
    }
}

