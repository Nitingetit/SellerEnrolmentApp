package com.ambsellerapp.backgroundservices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class Tracklocation extends Service
{
private static final String TAG = "BOOMBOOMTESTGPS";
private LocationManager mLocationManager = null;
private static final int LOCATION_INTERVAL = 10;
private static final float LOCATION_DISTANCE = 1;

private class LocationListener implements android.location.LocationListener{
    Location mLastLocation;
    public LocationListener(String provider)
    {
        Log.e(TAG, "LocationListener " + provider);
        mLastLocation = new Location(provider);
    }
    @Override
    public void onLocationChanged(Location location)
    {
    	turnGPSOn();
        Log.e(TAG, "onLocationChanged: " + location);
        mLastLocation.set(location);
        if (location != null) {
            String message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
         Toast.makeText(Tracklocation.this, message,Toast.LENGTH_LONG).show();  
         turnGPSOff();
        }
        
        
    }
    @Override
    public void onProviderDisabled(String provider)
    {
        Log.e(TAG, "onProviderDisabled: " + provider);            
    }
    @Override
    public void onProviderEnabled(String provider)
    {
        Log.e(TAG, "onProviderEnabled: " + provider);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        Log.e(TAG, "onStatusChanged: " + provider);
    }
} 
LocationListener[] mLocationListeners = new LocationListener[] {
        new LocationListener(LocationManager.GPS_PROVIDER),
        new LocationListener(LocationManager.NETWORK_PROVIDER)
};
@Override
public IBinder onBind(Intent arg0)
{
    return null;
}
@Override
public int onStartCommand(Intent intent, int flags, int startId)
{
    Log.e(TAG, "onStartCommand");
    super.onStartCommand(intent, flags, startId);       
    return START_STICKY;
}
@Override
public void onCreate()
{
    Log.e(TAG, "onCreate");
    initializeLocationManager();
    try {
        mLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                mLocationListeners[1]);
    } catch (java.lang.SecurityException ex) {
        Log.i(TAG, "fail to request location update, ignore", ex);
    } catch (IllegalArgumentException ex) {
        Log.d(TAG, "network provider does not exist, " + ex.getMessage());
    }
    try {
        mLocationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                mLocationListeners[0]);
    } catch (java.lang.SecurityException ex) {
        Log.i(TAG, "fail to request location update, ignore", ex);
    } catch (IllegalArgumentException ex) {
        Log.d(TAG, "gps provider does not exist " + ex.getMessage());
    }
}
@Override
public void onDestroy()
{
    Log.e(TAG, "onDestroy");
    super.onDestroy();
    if (mLocationManager != null) {
        for (int i = 0; i < mLocationListeners.length; i++) {
            try {
                mLocationManager.removeUpdates(mLocationListeners[i]);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
    }
} 

private void turnGPSOn(){
    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

    if(!provider.contains("gps")){ //if gps is disabled
        final Intent poke = new Intent();
        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"); 
        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        poke.setData(Uri.parse("3")); 
        sendBroadcast(poke);
    }
}

private void turnGPSOff(){
    String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

    if(provider.contains("gps")){ //if gps is enabled
        final Intent poke = new Intent();
        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        poke.setData(Uri.parse("3")); 
        sendBroadcast(poke);


   }
}


private void initializeLocationManager() {
    Log.e(TAG, "initializeLocationManager");
    if (mLocationManager == null) {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }
}
}