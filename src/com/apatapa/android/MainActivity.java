package com.apatapa.android;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.example.secondapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    	Log.d("foo","Reached log in button");
    	
    	ProgressBar loginProgress = (ProgressBar)findViewById(R.id.loginProgress);

    	EditText usernameField = (EditText)findViewById(R.id.usernameField);
    	String username = usernameField.getText().toString();
    	EditText passwordField = (EditText)findViewById(R.id.passwordField);
    	String password = passwordField.getText().toString();

    	// Make a POST request to backend to check username/password
    	HttpClient http = new DefaultHttpClient();
    	HttpPost postRequest = new HttpPost(LOGIN_URL);
    	
    	// The response from the server
    	HttpResponse response = null;
    	try {
    		List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
    		postData.add(new BasicNameValuePair("username", username));
    		postData.add(new BasicNameValuePair("password", password));
    		postRequest.setEntity(new UrlEncodedFormEntity(postData));
    		
    		// Animate the progress bar to show that we're actually doing something
    		loginProgress.setVisibility(View.VISIBLE);
    		response = http.execute(postRequest);
    	} catch ( ClientProtocolException e ) {
    		System.err.println("ClientProtocolException");
    		e.printStackTrace();
    	} catch ( IOException e ) {
    		System.err.println("IOException");
    		e.printStackTrace();
    	}
    	
    	loginProgress.setVisibility(View.GONE);
    	
    	// Read the response
		// Go to the next page if we were successful logging in.
    	if (200 == response.getStatusLine().getStatusCode()) {
    		Intent successIntent = new Intent(this, SuccessActivity.class);
    		startActivity( successIntent );
    	}
    	TextView messageView = (TextView)findViewById(R.id.messageText);
    	try {
			messageView.setText(EntityUtils.toString(response.getEntity()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
