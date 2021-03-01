package uk.co.lukestevens.api.client;

/**
 * Simple class representing a server-lib status response
 * 
 * @author Luke Stevens
 */
public class ApiStatus {
	
	protected String name;
	protected String version;
	
	/**
	 * @return The service name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return The service version
	 */
	public String getVersion() {
		return version;
	}
	
	

}
