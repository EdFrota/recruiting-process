package com.efrota.recruitingprocess.controller.handler;

import java.io.Serializable;
import java.util.Date;

/**
 * Handle API responses that are not error.
 * 
 * @author edmundofrota
 *
 */
@SuppressWarnings("serial")
public class ResponseDetails implements Serializable {

	private Date timestamp;
	private String message;
	private String path;

	public ResponseDetails() {
		super();
	}

	public ResponseDetails(String message, String path) {
		this();
		this.timestamp = new Date();
		this.message = message;
		this.path = path;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
