package uk.co.lukestevens.api.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A class containing methods to help with parsing and handling
 * of requests to and responses from server-lib backed CRUD APIs
 * @author Luke Stevens
 *
 * @param <T> The class objects this api should return
 */
public abstract class AbstractApiClient<T> {
	
	protected static final MediaType MEDIATYPE_JSON = MediaType.parse("application/json; charset=utf-8");
	protected static final String STATUS_URL = "/api/status";
	
	protected final Gson gson;
	protected final OkHttpClient httpClient;
	protected final Class<T> clazz;

	/**
	 * Constructs a new AbstractApiClient
	 * @param gson Gson parser
	 * @param httpClient OkHttpClient
	 * @param clazz The class for objects being returned by this API
	 */
	@Inject
	protected AbstractApiClient(Gson gson, OkHttpClient httpClient, Class<T> clazz) {
		this.gson = gson;
		this.httpClient = httpClient;
		this.clazz = clazz;
	}
	
	protected abstract String getAddress();
	
	/**
	 * Executes a call to this APIs status endpoint.
	 * @return An ApiStatus object containing the application name
	 * and version.
	 * @throws IOException If the server fails to respond to the request
	 */
	public ApiStatus status() throws IOException {
		Request request = new Request.Builder().url(getAddress() + STATUS_URL).build();
		try (Response response = httpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new ApiClientException(response.code(), response.message());
			}

			String responseBody = response.body().string();
			return gson.fromJson(responseBody, ApiStatus.class);
		}
	}
	
	/**
	 * Create a JSON request body using the supplied object
	 * @param object The object to serialise to JSON
	 * @return An OkHttp request body containing JSON
	 */
	protected RequestBody createBody(T object) {
		String json = gson.toJson(object);
		return RequestBody.create(json, MEDIATYPE_JSON);
	}
	
	/**
	 * Handle a constructed request by executing it, and parsing the response.
	 * @param request A constructed and ready-to-execute OkHttp request
	 * @return The parsed data object from the response, or null if no content
	 * is returned from the server.
	 * @throws IOException If the server fails to respond to the request,
	 * or the response contains errors.
	 */
	protected T handleRequest(Request request) throws IOException {
		try (Response response = httpClient.newCall(request).execute()) {
			if (response.code() >= 500) {
				throw new ApiClientException(response.code(), response.message());
			}

			String responseBody = response.body().string();
			return response.code() == HttpURLConnection.HTTP_NO_CONTENT?
					null : 
					parseResponse(responseBody);
		}
	}
	
	/**
	 * Parse a response body, retrieving the data object, or
	 * throwing errors returned by the server.
	 * @param body The response body as JSON
	 * @return The parsed object from a successful response
	 * @throws IOException If the body cannot be parsed, or contains
	 * errors from the server.
	 */
	protected T parseResponse(String body) throws IOException {
		JsonObject json = gson.fromJson(body, JsonObject.class);
		boolean success = json.has("success") && json.get("success").getAsBoolean();
		if(success) {
			return gson.fromJson(json.get("data"), clazz);
		}
		else {
			throw new ApiClientException(
				json.get("errors")
					.getAsJsonObject()
					.entrySet()
					.stream()
					.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getAsString()))
				);
		}
	}

}
