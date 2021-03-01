package uk.co.lukestevens.api.client;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import uk.co.lukestevens.utils.InternalFileReader;

public class AbstractApiClientTest {
	
	Gson gson = new Gson();
	
	String jsonFromFile(String file) throws IOException {
		file = file.endsWith(".json")? file : file + ".json";
		try(InternalFileReader reader = new InternalFileReader("json", file)) {
			return reader.readFile();
		}
	}
	
	public OkHttpClient mockHttpClient(Response.Builder responseBuilder, String bodyJson) throws IOException {
		ResponseBody body = ResponseBody.create(bodyJson, AbstractApiClient.MEDIATYPE_JSON);
		responseBuilder.body(body).protocol(Protocol.HTTP_1_0);
		
		Call httpCall = mock(Call.class);
		when(httpCall.execute()).thenAnswer((i) -> 
			responseBuilder.build());
		
		OkHttpClient mockClient = mock(OkHttpClient.class);
		when(mockClient.newCall(ArgumentMatchers.any())).thenAnswer((i) -> {
			Request req = i.getArgument(0);
			responseBuilder.request(req);
			return httpCall;
		});
		
		return mockClient;
	}
	
	@Test
	public void testStatus_responseSuccessful() throws IOException {
		String jsonBody = jsonFromFile("testApiStatus");
		Response.Builder response = new Response.Builder()
				.code(200)
				.message("Success!");
		
		OkHttpClient httpClient = mockHttpClient(response, jsonBody);
		AbstractApiClient<JsonObject> apiClient = 
				new MockApiClient<>(gson, httpClient, "http://localhost", JsonObject.class);
		
		ApiStatus apiStatus = apiClient.status();
		assertEquals("api-client-test", apiStatus.getName());
		assertEquals("1.0.0-test", apiStatus.getVersion());
		
		Request request = response.getRequest$okhttp();
		assertEquals("GET", request.method());
		assertEquals("http://localhost/api/status", request.url().toString());
	}
	
	@Test
	public void testStatus_responseUnsuccessful() throws IOException {
		Response.Builder response = new Response.Builder()
				.code(500)
				.message("Invalid request.");
		
		OkHttpClient httpClient = mockHttpClient(response, "");
		AbstractApiClient<JsonObject> apiClient = 
				new MockApiClient<>(gson, httpClient, "http://localhost", JsonObject.class);
		
		ApiClientException e = assertThrows(ApiClientException.class, apiClient::status);
		assertEquals("HTTP 500: Invalid request.", e.getMessage());
	}
	
	@Test
	public void testCreateBody() throws IOException {
		ApiStatus status = new ApiStatus();
		status.name="api-client-test";
		status.version="1.0.0-test";
		
		AbstractApiClient<ApiStatus> apiClient = new MockApiClient<>(gson, null, null, ApiStatus.class);
		RequestBody body = apiClient.createBody(status);
		assertEquals("application", body.contentType().type());
		assertEquals("json", body.contentType().subtype());
		assertEquals(Charset.forName("utf-8"), body.contentType().charset());
		assertEquals(49, body.contentLength());
	}
	
	@Test
	public void testHandleRequest_successful() throws IOException {
		String jsonBody = jsonFromFile("testSampleDataResponse");
		String address = "http://localhost/api/sample";
		Response.Builder response = new Response.Builder()
				.code(200)
				.message("Success!");
		
		OkHttpClient httpClient = mockHttpClient(response, jsonBody);
		AbstractApiClient<SampleData> apiClient = 
				new MockApiClient<>(gson, httpClient, address, SampleData.class);
		
		Request request = new Request.Builder().url(address).build();
		String actualBody = apiClient.handleRequest(request, body -> body);
		assertEquals(jsonBody, actualBody);
	}
	
	@Test
	public void testHandleRequest_unsuccessful() throws IOException {
		String jsonBody = jsonFromFile("testSampleDataResponse");
		String address = "http://localhost/api/sample";
		Response.Builder response = new Response.Builder()
				.code(500)
				.message("Invalid request.");
		
		OkHttpClient httpClient = mockHttpClient(response, jsonBody);
		AbstractApiClient<SampleData> apiClient = 
				new MockApiClient<>(gson, httpClient, address, SampleData.class);
		
		Request request = new Request.Builder().url(address).build();
		ApiClientException e = assertThrows(ApiClientException.class, 
				() -> apiClient.handleRequest(request, body -> body));
		assertEquals("HTTP 500: Invalid request.", e.getMessage());
	}
	
	@Test
	public void testHandleRequest_noContent() throws IOException {
		String jsonBody = jsonFromFile("testSampleDataResponse");
		String address = "http://localhost/api/sample";
		Response.Builder response = new Response.Builder()
				.code(204)
				.message("No content.");
		
		OkHttpClient httpClient = mockHttpClient(response, jsonBody);
		AbstractApiClient<SampleData> apiClient = 
				new MockApiClient<>(gson, httpClient, address, SampleData.class);
		
		Request request = new Request.Builder().url(address).build();
		String actualJson = apiClient.handleRequest(request, body -> body);
		assertNull(actualJson);
	}
	
	@Test
	public void testParseResponseAsObject_withErrors() throws IOException {
		String jsonBody = jsonFromFile("testResponseWithErrors");
		AbstractApiClient<SampleData> apiClient = 
				new MockApiClient<>(gson, null, null, SampleData.class);
		
		ApiClientException e = assertThrows(ApiClientException.class, 
				() -> apiClient.parseResponseAsObject(jsonBody));
		assertEquals("Not a png\nInput too long", e.getMessage());
		assertEquals("Input too long", e.getServerError("input"));
		assertEquals("Not a png", e.getServerError("image"));
	}
	
	@Test
	public void testParseResponseAsObject() throws IOException {
		String jsonBody = jsonFromFile("testSampleDataResponse");
		AbstractApiClient<SampleData> apiClient = 
				new MockApiClient<>(gson, null, null, SampleData.class);
		
		SampleData data = apiClient.parseResponseAsObject(jsonBody);
		assertEquals(5, data.getId());
		assertEquals("some-value", data.getValue());
	}
	
	@Test
	public void testParseResponseAsList() throws IOException {
		String jsonBody = jsonFromFile("testSampleDataListResponse");
		AbstractApiClient<SampleData> apiClient = 
				new MockApiClient<>(gson, null, null, SampleData.class);
		
		List<SampleData> data = apiClient.parseResponseAsList(jsonBody);
		assertEquals(2, data.size());
		
		SampleData data1 = data.get(0);
		assertEquals(2, data1.getId());
		assertEquals("some-value", data1.getValue());
		
		SampleData data2 = data.get(1);
		assertEquals(3, data2.getId());
		assertEquals("another-value", data2.getValue());
	}

}
