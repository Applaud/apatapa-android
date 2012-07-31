package com.apatapa.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SuccessActivity extends Activity {
	

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        
        TextView latitudeView = (TextView)findViewById(R.id.latitudeField);
        TextView longitudeView = (TextView)findViewById(R.id.longitudeField);

        latitudeView.setText(""+MainActivity.latitude);
        longitudeView.setText(""+MainActivity.longitude);
    }
}
