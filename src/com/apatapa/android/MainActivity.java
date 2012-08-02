package com.apatapa.android;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity implements ApatapaURLDefinitions {
	
	// Stored for the rest of the app to see.
	public static double latitude, longitude;
	// Application context
	private static Context applicationContext;

	// Used to retrieve the user's location. Results are stored
	// statically in this class.
	static LocationHelper locationHelper;
	static LocationHelper.LocationResult locationResult;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applicationContext = this.getApplicationContext();
        
        // Setup location request. Find the user's location before they're
        // even logged in.
        updateLocation();
    }
    
    public static void updateLocation(final Callback callback) {
        locationHelper = new LocationHelper();
        locationResult = new LocationHelper.LocationResult() {
			
			@Override
			public void gotLocation(Location location) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				
				if ( null != callback )
					callback.performCallback();
			}
		};
		locationHelper.init(applicationContext, locationResult);
    }
    
    public static void updateLocation() {
    	updateLocation(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
    
    /********************************
     * Responses to UI Elements
     *******************************/
    
    
    /**
     * loginButtonClicked
     * 
     * What happens when the "OK" button is clicked at the login screen.
     */
    public void loginButtonClicked(View view) {
    	new LoginProgress().execute();
    }
    
    private class LoginProgress extends AsyncTask<Void, Void, Boolean> {
    	ProgressDialog loadingDialog;

    	protected void onPreExecute() {
    		loadingDialog = new ProgressDialog(MainActivity.this);
    		loadingDialog.setTitle("Apatapa");
    		loadingDialog.setMessage("Logging in...");
    		loadingDialog.setIndeterminate(true);
    		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		loadingDialog.show();
    	}
    	
    	protected void onPostExecute(Boolean success) {
    		loadingDialog.cancel();
    		
    		if ( success ) {
    			Intent successIntent = new Intent(MainActivity.this, SuccessActivity.class); 
    			startActivity( successIntent );
    		}
    	}
    	
		@Override
		protected Boolean doInBackground(Void...voids) {
			EditText usernameField = (EditText)findViewById(R.id.usernameField);
			String username = usernameField.getText().toString();
			EditText passwordField = (EditText)findViewById(R.id.passwordField);
			String password = passwordField.getText().toString();

			return ConnectionManager.login(username, password);
		}
    }
}
