package uk.co.lukestevens.api.client.template;

import java.io.IOException;

import uk.co.lukestevens.api.models.Example;

/**
 * An interface defining the methods for the API client
 * 
 * @author Luke Stevens
 */
public interface TemplateApi {
	
	public Example getExample(int id) throws IOException;
	
	public Example createExample(Example example) throws IOException;
	
	public Example updateExample(Example example) throws IOException;
	
	public void deleteExample(int id) throws IOException;

}
