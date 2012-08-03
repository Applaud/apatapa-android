package com.apatapa.android;

public interface ApatapaURLDefinitions {
	
	/** The address of the server. All URLs will be appended to this. **/
	public static final String SERVER_URL = "http://ec2-107-22-6-55.compute-1.amazonaws.com";
	
	/** Sites within the backend. Don't forget the leading and trailing '/' **/
	// Where to go log in
	public static final String LOGIN_URL = "/accounts/mobilelogin/";
	
	// Where to download possible businesses based on location
	public static final String WHEREAMI_URL = "/mobile/whereami/";
	// Where to post our current business to
	public static final String CHECKIN_URL = "/mobile/checkin/";
}
