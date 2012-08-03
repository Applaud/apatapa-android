package com.apatapa.android.models;

import java.util.Date;

public class Newsfeed {
	
	private String title, subtitle, body, imageURL;
	private Date date;
	
	public Newsfeed(
			String title,
			String subtitle,
			String body,
			String imageURL,
			Date date) {
		this.title = title;
		this.subtitle = subtitle;
		this.body = body;
		this.imageURL = imageURL;
		this.date = date;
	}
	
	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public String getBody() {
		return body;
	}

	public String getImageURL() {
		return imageURL;
	}

	public Date getDate() {
		return date;
	}

}
