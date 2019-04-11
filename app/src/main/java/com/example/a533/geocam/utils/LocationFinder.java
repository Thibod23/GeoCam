package com.example.a533.geocam.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

public class LocationFinder {
    private Context context;
    private LocationManager locationManager;

    public LocationFinder(LocationManager locationManager, Context context) {
        this.locationManager = locationManager;
        this.context = context;
    }

    public double findLng() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return location.getLongitude();
    }

    public double findLat() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return location.getLatitude();
    }
}
