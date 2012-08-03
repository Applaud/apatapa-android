package com.apatapa.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.apatapa.android.models.Business;

public class SuccessActivity extends Activity implements ApatapaURLDefinitions, OnItemClickListener {

	public String[] testStrings = {"one", "two", "three", "four", "five"};
	private ArrayList<Business> businesses;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_success);

		ListView businessList = (ListView)findViewById(R.id.businessListing);
		businessList.setOnItemClickListener(this);
		
		new LocationsProgress().execute();
	}

	/**
	 * LocationsProgress
	 * 
	 * This gives a ProgressDialog while obtaining nearby locations.
	 *
	 */
	private class LocationsProgress extends AsyncTask<Void, Void, Void> {
		ProgressDialog loadingDialog;

		protected void onPreExecute() {
			loadingDialog = new ProgressDialog(SuccessActivity.this);
			loadingDialog.setTitle("Apatapa");
			loadingDialog.setMessage("Finding nearby locations...");
			loadingDialog.setIndeterminate(true);
			loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			loadingDialog.show();
		}

		protected void onPostExecute(Void unused) {
			loadingDialog.cancel();

			// Put items into the table
			ArrayList<String> businessStrings = new ArrayList<String>();
			for ( Business b : businesses ) {
				businessStrings.add( b.toString() );
			}
			ListView listView = (ListView)findViewById(R.id.businessListing);
			listView.setAdapter(
					new ArrayAdapter<String>(
							SuccessActivity.this, 
							android.R.layout.simple_list_item_1, 
							businessStrings) 
					);
			listView.setTextFilterEnabled(true);
		}

		@Override
		protected Void doInBackground(Void...voids) {
			// Grab nearby businesses from the server
			HashMap<String, Double> params = new HashMap<String,Double>();
			params.put("latitude", MainActivity.latitude);
			params.put("longitude", MainActivity.longitude);
			ConnectionManager.serverGET(WHEREAMI_URL, params, new ResponseHandler<Void>() {
				@Override
				public Void handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {

					// Create models for each of the businesses we received, and store them in a list.
					JSONObject json = null;
					JSONArray data = null;
					try {
						json = new JSONObject( EntityUtils.toString(response.getEntity()) );
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					try {
						data = json.getJSONArray("nearby_businesses");
						businesses = new ArrayList<Business>();
						for ( int i=0; i<data.length(); i++ ) {
							JSONObject businessObject = data.getJSONObject(i);

							// Parse business types
							ArrayList<String> businessTypes = new ArrayList<String>();
							JSONArray typesArray = businessObject.getJSONArray("types");
							for ( int j=0; j<typesArray.length(); j++) {
								businessTypes.add(typesArray.getString(j));
							}

							// Create the model
							Business business = new Business( 
									businessObject.optString("name"),
									businessObject.optString("goog_id"),
									businessObject.optDouble("latitude"),
									businessObject.optDouble("longitude"),
									businessObject.optString("primary"),
									businessObject.optString("secondary"),
									businessObject.optBoolean("generic"),
									businessTypes );

							// Add it to our list
							businesses.add( business );
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.v("apatapa","Handled "+data.length()+" businesses.");

					return null;
				}
			});
			
			return null;
		}
	}
	
	/**
	 * CheckinProgress
	 * 
	 * Displays a ProgressDialog while checking into the selected business.
	 *
	 */
	private class CheckinProgress extends AsyncTask<Void, Void, Void> {
		ProgressDialog loadingDialog;

		public void onPreExecute() {
			loadingDialog = new ProgressDialog(SuccessActivity.this);
			loadingDialog.setTitle("Apatapa");
			loadingDialog.setMessage("Please wait...");
			loadingDialog.setIndeterminate(true);
			loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			loadingDialog.show();
		}
		
		public void onPostExecute(Void unused) {
			loadingDialog.dismiss();
			Log.v("verbose", "Logged into business with id of "+MainActivity.currentBusiness.getBusinessID());
		}
		
		public Void doInBackground(Void...voids) {
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("latitude", MainActivity.currentBusiness.getLatitude());
			params.put("longitude", MainActivity.currentBusiness.getLongitude());
			params.put("goog_id", MainActivity.currentBusiness.getGoogleID());
			params.put("types", MainActivity.currentBusiness.getTypes());
			params.put("name", MainActivity.currentBusiness.getName());
			
			ConnectionManager.serverPOST(CHECKIN_URL, params, new ResponseHandler<Void>() {

				@Override
				public Void handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					
					JSONObject json = null;
					String jsonString = null;
					try {
						jsonString = EntityUtils.toString(response.getEntity());
						json = new JSONObject( jsonString );
					} catch (ParseException e) {
						System.err.println("This is not valid json: "+jsonString);
						e.printStackTrace();
					} catch (JSONException e) {
						System.err.println("This is not valid json: "+jsonString);
						e.printStackTrace();
					}
					
					long businessID = 0;
					try {
						businessID = json.getLong("business_id");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					MainActivity.currentBusiness.setBusinessID(businessID);

					return null;
				}
				
			});
			
			return null;
		}
		
	}

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

				new LocationsProgress().execute();
			}
		});
	}

	@Override
	/**
	 * onItemClick
	 * 
	 * This gets called when the user selects a business from the ListView.
	 */
	public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
		Business b = businesses.get(position);
		MainActivity.currentBusiness = b;
		
		new CheckinProgress().execute();
	}
}
