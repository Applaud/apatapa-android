package com.apatapa.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

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
			Log.e("apatapa","ClientProtocolException");
			e.printStackTrace();
		} catch ( IOException e ) {
			Log.e("apatapa","IOException from URL: "+login);
			e.printStackTrace();
		}

		// Read the response
		// Go to the next page if we were successful logging in.
		if (200 == response.getStatusLine().getStatusCode()) {
			try {
				Pattern sessionPattern = Pattern.compile("sessionid=[a-f0-9]+;");
				Header cookie = response.getHeaders("Set-Cookie")[0];
				Matcher sessionMatcher = sessionPattern.matcher( cookie.getValue() );
				if ( sessionMatcher.find() )
					SESSION_TOKEN = sessionMatcher.group();
				else
					Log.e("ERROR","Could not grab the session token!");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			return true;
		}

		return false;
	}
	
	public static void serverGET(String URL, Map params, ResponseHandler handler) {
		String fullURL = SERVER_URL + URL + GETURLFromMap(params);
		
		// Construct the GET request
		HttpGet getRequest = new HttpGet(fullURL);
		getRequest.setHeader("Cookie", SESSION_TOKEN);
		
		HttpResponse response = null;
		try {
			response = http.execute(getRequest);
		} catch ( ClientProtocolException e ) {
			Log.e("apatapa","ClientProtocolException");
			e.printStackTrace();
		} catch ( IOException e ) {
			Log.e("apatapa","IOException from URL: "+fullURL);
			e.printStackTrace();
		}
		
		switch (response.getStatusLine().getStatusCode()) {
		case 500:
			//TODO: What to do when server craps out?
			break;
		case 403:
			Log.e("apatapa","Got HTTP forbidden.");
			break;
		case 200:
			Log.v("apatapa","Seems like everything went ok. Got a 200.");
			break;
		default:
			Log.e("apatapa","Status code unaccounted for. ("+response.getStatusLine().getStatusCode()+")");
		}
		
		// Handle the response
		if ( null != handler ) {
			try {
				handler.handleResponse(response);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void serverPOST(String URL, Map params, ResponseHandler handler) {
		String csrfToken = getCSRFTokenFromURL(URL);
		String fullURL = SERVER_URL + URL;
		
		// Construct the POST request
		HttpPost postRequest = new HttpPost(fullURL);
		postRequest.setHeader("Accept", "application/json");
		postRequest.setHeader("Cookie", "csrftoken="+csrfToken+"; "+SESSION_TOKEN);
		Log.d("debug","The cookie header is this: "+postRequest.getHeaders("Cookie")[0].toString());
		postRequest.setHeader("Content-type","application/json");
		postRequest.setHeader("X-CSRFToken", csrfToken);
		JSONObject json = new JSONObject( params );
		Log.d("debug","Sending these JSONd params: "+json.toString());
		StringEntity jsonBody = null;
		try {
			jsonBody = new StringEntity(json.toString(),"UTF-8");
		} catch (UnsupportedEncodingException e1) {
			Log.e("apatapa", "Cannot encode into json.");
			e1.printStackTrace();
		}
		HttpParams csrfParam = new BasicHttpParams();
		csrfParam.setParameter("csrfmiddlewaretoken", csrfToken);
		postRequest.setParams(csrfParam);	
//		Log.v("debug","This is the post request you are making:"+postRequest.);
		postRequest.setEntity(jsonBody);
		
		HttpResponse response = null;
		try {
			response = http.execute(postRequest);
		} catch ( ClientProtocolException e ) {
			Log.e("apatapa","ClientProtocolException");
			e.printStackTrace();
		} catch ( IOException e ) {
			Log.e("apatapa","IOException from URL: "+fullURL);
			e.printStackTrace();
		}
		
		switch (response.getStatusLine().getStatusCode()) {
		case 500:
			//TODO: What to do when server craps out?
			break;
		case 403:
			Log.e("apatapa","Got HTTP forbidden. CSRFToken="+csrfToken);
			break;
		case 200:
			Log.v("apatapa","Seems like everything went ok. Got a 200.");
			break;
		default:
			Log.e("apatapa","Status code unaccounted for. ("+response.getStatusLine().getStatusCode()+")");
		}
		
		// Handle the response
		if ( null != handler ) {
			try {
				handler.handleResponse( response );
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * getCSRFTokenFromURL
	 * 
	 * Grabs the CSRF Token from a URL. 
	 * 
	 * @param URL The URL from which to ask for the CSRF Token. This should NOT
	 * include the domain name of the server.
	 * @return The String representation of the CSRF token.
	 */
	private static String getCSRFTokenFromURL(String URL) {
		String fullURL = SERVER_URL + URL;
		HttpGet getRequest = new HttpGet(fullURL);
		
		HttpResponse response = null;
		try {
			response = http.execute(getRequest);
		} catch ( ClientProtocolException e ) {
			Log.e("apatapa","ClientProtocolException");
			e.printStackTrace();
		} catch ( IOException e ) {
			Log.e("apatapa","IOException from URL: "+fullURL);
			e.printStackTrace();
		}
		
		String csrf = null;
		try {
			csrf = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			Log.e("apatapa","Could not grab CSRF token from response from "+fullURL);
		}
		
		return csrf;
	}
	
	private static String GETURLFromMap( Map<String,Object> map ) {
		// The initial ?
		StringBuilder getString = new StringBuilder("?");
		
		// Append key,value pairs
		for ( Map.Entry<String, Object> entry : map.entrySet() ) {
			getString.append(entry.getKey() + "=" + entry.getValue() + "&");
		}
		
		// Remove final &
		return getString.substring(0,getString.length()-2);
	}

}
