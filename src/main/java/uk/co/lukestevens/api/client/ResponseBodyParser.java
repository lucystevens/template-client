package uk.co.lukestevens.api.client;

import java.io.IOException;

public interface ResponseBodyParser<T> {
	public T parse(String body) throws IOException;
}
