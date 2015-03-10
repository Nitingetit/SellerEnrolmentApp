package com.ambsellerapp.utilies;

import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.AMBSEA.BuildConfig;

/**
 * This class is only to hold location related methods.
 */
public class LocationUtils {
	
	private static final String LOG_TAG = "LocationUtils";
	
	/**
	 * Get the most accurate last known location of the user.
	 * Requires ACCESS_FINE_LOCATION permission
	 * @param pContext
	 */
	public static Location getBestLastKnownLocation(Context pContext){
		if( BuildConfig.DEBUG ) {
			Log.i( LOG_TAG, "getBestLastKnownLocation() called.");
		}
		Location mostAccurateLocation = null;
		try {
			LocationManager locationManager = (LocationManager)pContext.getSystemService(Context.LOCATION_SERVICE);
			List<String> providersList = locationManager.getAllProviders();
			if( providersList == null || providersList.isEmpty() ) {
				return mostAccurateLocation;
			}
			for( String provider : providersList ) {
				Location location = locationManager.getLastKnownLocation(provider);	
				if( mostAccurateLocation == null ) {
					if( location != null ) {
						mostAccurateLocation = location;
					}
				} else {
					if( location != null && location.getAccuracy() > mostAccurateLocation.getAccuracy() ) {
						mostAccurateLocation = location;
					}
				}
			}
		} catch (Exception e) {
			Log.e( LOG_TAG, "getBestLastKnownLocation()", e );
		}
		if( BuildConfig.DEBUG ) {
			Log.i( LOG_TAG, "getBestLastKnownLocation() " + mostAccurateLocation);
		}
		return mostAccurateLocation;
	}

	/**
	 * @param pContext
	 * @return user's Address based on last known location
	 */
	public static Address getAddressByGeoCoder(Context pContext, Location pLocation) {
		try {
			Geocoder geocoder = new Geocoder(pContext);
			List<Address> addressList = geocoder.getFromLocation(pLocation.getLatitude(), pLocation.getLongitude(), 1);
			return addressList.get(0);
		} catch ( Exception e ) {
			Log.e( LOG_TAG, "getCityByGeoCoder(): " + e.getLocalizedMessage() );
			return null;
		}
	}
	
	
	
	/**
	 * try to get the 'best' location selected from all providers
	 */
	public static Location getBestLocation(Context pContext) {
	    Location gpslocation = getLocationByProvider(pContext,LocationManager.GPS_PROVIDER);
	    Location networkLocation =
	            getLocationByProvider(pContext,LocationManager.NETWORK_PROVIDER);
	    // if we have only one location available, the choice is easy
	    if (gpslocation == null) {
	        Log.d(LOG_TAG, "No GPS Location available.");
	        return networkLocation;
	    }
	    if (networkLocation == null) {
	        Log.d(LOG_TAG, "No Network Location available");
	        return gpslocation;
	    }
	    // a locationupdate is considered 'old' if its older than the configured
	    // update interval. this means, we didn't get a
	    // update from this provider since the last check
	    long old = System.currentTimeMillis() - 0;
	    boolean gpsIsOld = (gpslocation.getTime() < old);
	    boolean networkIsOld = (networkLocation.getTime() < old);
	    // gps is current and available, gps is better than network
	    if (!gpsIsOld) {
	        Log.d(LOG_TAG, "Returning current GPS Location");
	        return gpslocation;
	    }
	    // gps is old, we can't trust it. use network location
	    if (!networkIsOld) {
	        Log.d(LOG_TAG, "GPS is old, Network is current, returning network");
	        return networkLocation;
	    }
	    // both are old return the newer of those two
	    if (gpslocation.getTime() > networkLocation.getTime()) {
	        Log.d(LOG_TAG, "Both are old, returning gps(newer)");
	        return gpslocation;
	    } else {
	        Log.d(LOG_TAG, "Both are old, returning network(newer)");
	        return networkLocation;
	    }
	}

	/**
	 * get the last known location from a specific provider (network/gps)
	 */
	public static Location getLocationByProvider(Context pContext,String provider) {
	    Location location = null;
	   /* if (!isProviderSupported(provider)) {
	        return null;
	    }*/
	    LocationManager locationManager = (LocationManager) pContext.getApplicationContext()
	            .getSystemService(Context.LOCATION_SERVICE);
	    try {
	        if (locationManager.isProviderEnabled(provider)) {
	            location = locationManager.getLastKnownLocation(provider);
	        }
	    } catch (IllegalArgumentException e) {
	        Log.d(LOG_TAG, "Cannot acces Provider " + provider);
	    }
	    return location;
	}
}
