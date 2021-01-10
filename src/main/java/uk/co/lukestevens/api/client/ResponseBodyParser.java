package uk.co.lukestevens.api.client;

import java.io.IOException;

/**
 * A functional interface for parsing the body from
 * an HTTP response
 * @author Luke Stevens
 *
 * @param <T> The type of object returned by the parser
 */
@FunctionalInterface
public interface ResponseBodyParser<T> {
	
	/**
	 * Parse the json body from a server response
	 * @param body The json body
	 * @return The Java object represented by this body
	 * @throws IOException If there is an exception when parsing
	 */
 	public T parse(String body) throws IOException;
}
