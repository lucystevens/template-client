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

public abstract class AbstractApiClient<T> {
	
	protected static final MediaType MEDIATYPE_JSON = MediaType.parse("application/json; charset=utf-8");
	protected static final String STATUS_URL = "/api/status";
	
	protected final Gson gson;
	protected final OkHttpClient httpClient;
	protected final Class<T> clazz;

	@Inject
	public AbstractApiClient(Gson gson, OkHttpClient httpClient, Class<T> clazz) {
		this.gson = gson;
		this.httpClient = httpClient;
		this.clazz = clazz;
	}
	
	protected abstract String getAddress();
	
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
	
	protected RequestBody createBody(T object) {
		String json = gson.toJson(object);
		return RequestBody.create(json, MEDIATYPE_JSON);
	}
	
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
