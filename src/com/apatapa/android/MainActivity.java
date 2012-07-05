package com.apatapa.android;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.apatapa.android.R;

public class MainActivity extends Activity implements ApatapaURLDefinitions {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    	ProgressBar loginProgress = (ProgressBar)findViewById(R.id.loginProgress);

    	EditText usernameField = (EditText)findViewById(R.id.usernameField);
		String username = usernameField.getText().toString();
		EditText passwordField = (EditText)findViewById(R.id.passwordField);
		String password = passwordField.getText().toString();

		// Animate the progress bar to show that we're actually doing something
		loginProgress.setVisibility(View.VISIBLE);
		
		// Tell the connection manager to log us in.
		if ( ConnectionManager.login(username, password)) {
			Intent successIntent = new Intent(this, SuccessActivity.class);
			startActivity( successIntent );
		}
		
		loginProgress.setVisibility(View.GONE);
    }
}
