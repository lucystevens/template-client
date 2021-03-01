package uk.co.lukestevens.api.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * An exception to be thrown by API clients processing requests to
 * server-lib backed services. This class contains an error map to
 * get the individual errors returned by the service, if applicable.
 * 
 * @author Luke Stevens
 */
public class ApiClientException extends IOException {

	private static final long serialVersionUID = -4451575244945464154L;
	
	private static final String ERROR_RESPONSE_TEMPLATE = "HTTP %d: %s";
	
	// Compile a map of errors into a single string messsage
	static String compileMessage(Map<String, String> errors) {
		return String.join("\n", errors.values());
	}
	
	private Map<String, String> errors = new HashMap<>();
	
	/**
	 * Create a new ApiClientException without a server error map
	 * @param message The message for the exception
	 */
	public ApiClientException(String message) {
		super(message);
	}
	
	/**
	 * Create a new ApiClientException using a map of errors
	 * provided by the server
	 * @param errors Map of errors provided by the server
	 */
	public ApiClientException(Map<String, String> errors) {
		this(compileMessage(errors));
		this.errors = errors;
	}
	
	/**
	 * Create a new ApiClientException for a server response with an error http code
	 * @param httpCode The http code
	 * @param message The response message
	 */
	public ApiClientException(int httpCode, String message) {
		this(String.format(ERROR_RESPONSE_TEMPLATE, httpCode, message));
	}
	
	/**
	 * @return A map of errors provided by the server. Often
	 * returned in cases where parameters of the request or body
	 * are invalid.
	 */
	public Map<String, String> getServerErrors(){
		return this.errors;
	}
	
	/**
	 * Retrieve a single error from the map provided by the server
	 * @param errorKey The key to use to get the error 
	 * @return A server error
	 */
	public String getServerError(String errorKey) {
		return this.errors.get(errorKey);
	}

}
