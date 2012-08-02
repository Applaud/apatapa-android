package com.apatapa.android;

import java.util.HashMap;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SuccessActivity extends Activity implements ApatapaURLDefinitions {
	
	public String[] testStrings = {"one", "two", "three", "four", "five"};
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        
        // Put items into the table
        ListView listView = (ListView)findViewById(R.id.businessListing);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testStrings));
        listView.setTextFilterEnabled(true);
        
        // Grab nearby businesses from the server
        HashMap<String, Double> params = new HashMap<String,Double>();
        params.put("latitude", MainActivity.latitude);
        params.put("longitude", MainActivity.longitude);
        ConnectionManager.serverGET(WHEREAMI_URL, params);
    }
    
    /********************************
     * Responses to UI Elements
     *******************************/
    
    
    /**
     * loginButtonClicked
     * 
     * What happens when the "OK" button is clicked at the login screen.
     */
    public void refreshButtonClicked(View view) {
    	// Get new location
    	MainActivity.updateLocation(new Callback() {
    		public void performCallback() {
    			System.out.println("Callback performed.");
    			
    			//TODO: update the listview to show the new locations.
    		}
    	});
    }
}
