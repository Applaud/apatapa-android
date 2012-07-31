package com.apatapa.android;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/*
 * BusinessLocationsTracker.java
 * 
 * Keeps track of the user's current location. Provides the initial list of businesses
 * from which the end-user may select their location. Notifies pages to close when the
 * user has traveled outside of the acceptable radius of the business.
 * 
 * @author lukelovett
 *
 */

public class BusinessLocationsTracker implements ApatapaURLDefinitions {
	
	// How often we want to update location.
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	// How far we can travel to still be inside a business' radius (m)
	private static final int MIN_RADIUS = 250;
	// The location manager
	private static LocationManager locationManager;
	// The last location we received that we liked
	private static Location lastLocation;
	
	/**
	 * CONSTRUCTOR
	 * 
	 * @param context Activity's context to use to access system services.
	 */
	public BusinessLocationsTracker( Context context ) {
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		
		// Register callback to location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				TWO_MINUTES, MIN_RADIUS, new LocationListener() {
			
				public void onLocationChanged(Location loc) {
					if ( trustNewLocation(loc, lastLocation) ) {
						lastLocation = loc;
						
						// Do some more stuff with location here.
					}
				}
				
				// Required in a LocationListener
				public void onStatusChanged(String provider, int status, Bundle extras) {}
				
				// Required in a LocationListener
    			public void onProviderEnabled(String provider) {}
    			
				// Required in a LocationListener
    			public void onProviderDisabled(String provider) {}
		});
	}
	
	
	/**
	 * trustNewLocation
	 * 
	 * Determine whether to use a new location that we just received. Takes into account accuracy,
	 * and age of location estimate. Much of this taken from:
	 * http://developer.android.com/guide/topics/location/strategies.html
	 * 
	 * @param newLoc The location that was just received.
	 * @param oldLoc The older location we were trusting.
	 * @return true if "newLoc" should be taken over "oldLoc"
	 */
	private boolean trustNewLocation(Location newLoc, Location oldLoc) {
		if ( null == oldLoc )
			return true;

		// Check whether the new location fix is newer or older
		long timeDelta = oldLoc.getTime() - newLoc.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (oldLoc.getAccuracy() - newLoc.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = ( oldLoc.getProvider() == null && newLoc.getProvider() == null ) ||
				oldLoc.equals( newLoc );

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

}
