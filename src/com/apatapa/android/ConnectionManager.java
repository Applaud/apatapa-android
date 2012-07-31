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

/**
 * ConnectionManager.java
 * 
 * @author lukelovett
 *
 * Handles all of the connection logic between the mobile application and
 * the server.
 */

public class ConnectionManager implements ApatapaURLDefinitions {
	
	

	// Make a POST request to backend to check username/password
	private static HttpClient http = new DefaultHttpClient();	// For making HTTP requests
	public static String SESSION_TOKEN;	// Stores our session token
	
	/**
	 * login
	 * 
	 * Logs in the user.
	 * 
	 * @param username The username
	 * @param password The password
	 * 
	 * @return true if successful, false otherwise
	 */
	public static boolean login(String username, String password) {
		String login = SERVER_URL + LOGIN_URL;
		HttpPost postRequest = new HttpPost(login);
		
		// The response from the server
		HttpResponse response = null;
		try {
			List<NameValuePair> postData = new ArrayList<NameValuePair>(2);
			postData.add(new BasicNameValuePair("username", username));
			postData.add(new BasicNameValuePair("password", password));
			postRequest.setEntity(new UrlEncodedFormEntity(postData));
			
			response = http.execute(postRequest);
		} catch ( ClientProtocolException e ) {
			System.err.println("ClientProtocolException");
			e.printStackTrace();
		} catch ( IOException e ) {
			System.err.println("IOException from URL: "+login);
			e.printStackTrace();
		}

		// Read the response
		// Go to the next page if we were successful logging in.
		if (200 == response.getStatusLine().getStatusCode()) {
			// TODO: Save the session token.
			try {
				SESSION_TOKEN = EntityUtils.toString(response.getEntity());
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return true;
		}

		return false;
	}

}
