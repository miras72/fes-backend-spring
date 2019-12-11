package pl.tycm.fes.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatusResponse {
	
	private int statusCode;
	private String message;
	
	@JsonProperty(value = "status_code")
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
