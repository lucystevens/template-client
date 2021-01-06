package uk.co.lukestevens.api.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ApiClientException extends IOException {

	private static final long serialVersionUID = -4451575244945464154L;
	
	private static final String ERROR_RESPONSE_TEMPLATE = "HTTP %d: %s";
	
	static String compileMessage(Map<String, String> errors) {
		return String.join("\n", errors.values());
	}
	
	private Map<String, String> errors = new HashMap<>();
	
	public ApiClientException(String message) {
		super(message);
	}
	
	public ApiClientException(Map<String, String> errors) {
		this(compileMessage(errors));
		this.errors = errors;
	}
	
	public ApiClientException(int httpCode, String message) {
		this(String.format(ERROR_RESPONSE_TEMPLATE, httpCode, message));
	}
	
	public Map<String, String> getServerErrors(){
		return this.errors;
	}
	
	public String getServerError(String errorKey) {
		return this.errors.get(errorKey);
	}

}
