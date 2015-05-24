package com.teamdc.stephendiniz.autoaway.classes;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.teamdc.stephendiniz.autoaway.Activity_Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sscotti on 5/10/15.
 */
public class GPS {

    private static GPS gps = new GPS();

    private Context context;

    private static boolean init;
    private LocationManager locationManager;
    private boolean estado;

    private Map<String, Location> currentLocations = new HashMap<String, Location>();

    public static GPS getInstance(Context context) {
        gps.setContext(context);
        return gps;
    }

    public void setContext(Context context) {
        this.context = context;
        validateInitialConfig();
    }

    private void validateInitialConfig() {
        if(!init){
            synchronized (this){
                locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastKnownLocation != null){
                    currentLocations.put(LocationManager.GPS_PROVIDER, lastKnownLocation);
                }

                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if(lastKnownLocation != null){
                    currentLocations.put(LocationManager.NETWORK_PROVIDER, lastKnownLocation);
                }

                init = true;
            }
        }
    }

    public Location getCurrentLocation(){
        return getCurrentLocation(LocationManager.NETWORK_PROVIDER);
    }

    public Location getCurrentLocation(String provider){
        return this.currentLocations.get(provider);
    }

    public Location getCurrentGPSLocation(){
        return this.getCurrentLocation(LocationManager.GPS_PROVIDER);
    }

    public Location getCurrentNetworkLocation(){
        return this.getCurrentLocation(LocationManager.NETWORK_PROVIDER);
    }

    public boolean getEstado(){
        return estado;
    }

    public boolean isGPSEnabled(){
        return this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isNetworkEnabled(){
        return this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            GPS.this.currentLocations.put(location.getProvider(), location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String s) {
        }

        public void onProviderDisabled(String s) {
        }
    };

}
