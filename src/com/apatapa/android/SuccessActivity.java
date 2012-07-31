package com.apatapa.android;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

public class SuccessActivity extends Activity {
	
	LocationHelper locationHelper;
	LocationHelper.LocationResult locationResult;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        
        // Setup location request
        locationHelper = new LocationHelper();
        locationResult = new LocationHelper.LocationResult() {
			
			@Override
			public void gotLocation(Location location) {
				TextView latitudeView = (TextView)findViewById(R.id.latitudeField);
				TextView longitudeView = (TextView)findViewById(R.id.longitudeField);
				
				latitudeView.setText(""+location.getLatitude());
				longitudeView.setText(""+location.getLongitude());
			}
		};
		locationHelper.init(this, locationResult);
    }
}
