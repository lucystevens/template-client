package uk.co.lukestevens.api.client;

import java.io.IOException;

import uk.co.lukestevens.models.Example;

public interface TemplateApi {
	
	public Example getExample(int id) throws IOException;
	
	public Example createExample(Example example) throws IOException;
	
	public Example updateExample(Example example) throws IOException;
	
	public void deleteExample(int id) throws IOException;

}
