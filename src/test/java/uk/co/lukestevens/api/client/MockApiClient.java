package uk.co.lukestevens.api.client;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;

public class MockApiClient<T> extends AbstractApiClient<T> {
	
	String address;

	protected MockApiClient(Gson gson, OkHttpClient httpClient, String address, Class<T> clazz) {
		super(gson, httpClient, clazz);
		this.address = address;
	}

	@Override
	protected String getAddress() {
		return address;
	}

}
