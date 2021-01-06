package uk.co.lukestevens.api.client;

import java.io.IOException;
import javax.inject.Inject;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import uk.co.lukestevens.annotations.ApplicationConfig;
import uk.co.lukestevens.config.Config;
import uk.co.lukestevens.models.Example;

public class TemplateApiClient extends AbstractApiClient<Example> implements TemplateApi {

	private static final String ADDRESS_PROPERTY = "template.api.address";
	private static final String RESOURCE_URL = "/api/example/";

	private final Config config;

	@Inject
	public TemplateApiClient(@ApplicationConfig Config config, Gson gson, OkHttpClient httpClient) {
		super(gson, httpClient, Example.class);
		this.config = config;
	}
	
	@Override
	protected String getAddress() {
		return this.config.getAsString(ADDRESS_PROPERTY);
	}

	String url() {
		return this.getAddress() + RESOURCE_URL;
	}
	
	@Override
	public Example getExample(int id) throws IOException {
		Request request = new Request.Builder().url(url() + id).build();
		return this.handleRequest(request);
	}

	@Override
	public Example createExample(Example example) throws IOException {
		RequestBody body = createBody(example);
		Request request = new Request.Builder().url(url()).post(body).build();
		return this.handleRequest(request);
	}

	@Override
	public Example updateExample(Example example) throws IOException {
		RequestBody body = createBody(example);
		Request request = new Request.Builder().url(url() + example.getId()).put(body).build();
		return this.handleRequest(request);
	}

	@Override
	public void deleteExample(int id) throws IOException {
		Request request = new Request.Builder().url(url() + id).delete().build();
		this.handleRequest(request);
	}

}
