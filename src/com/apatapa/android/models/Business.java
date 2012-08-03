package com.apatapa.android.models;

import java.util.List;

public class Business {

	private String name, googleID, primaryColor, secondaryColor;
	private double latitude, longitude;
	private boolean generic;
	private List<String> types;
	private long businessID;

	public Business(String name,
					String googleID,
					double latitude,
					double longitude,
					String primaryColor,
					String secondaryColor,
					boolean generic,
					List<String> types) {
		this.name = name;
		this.googleID = googleID;
		this.latitude = latitude;
		this.longitude = longitude;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.generic = generic;
		this.types = types;
	}
		
	public String getName() {
		return name;
	}

	public String getGoogleID() {
		return googleID;
	}

	public String getPrimaryColor() {
		return primaryColor;
	}

	public String getSecondaryColor() {
		return secondaryColor;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public boolean isGeneric() {
		return generic;
	}

	public List<String> getTypes() {
		return types;
	}
	
	public long getBusinessID() {
		return this.businessID;
	}
	
	public void setBusinessID( long businessID ) {
		this.businessID = businessID;
	}
	
	public String toString() {
		return this.name;
	}
}
