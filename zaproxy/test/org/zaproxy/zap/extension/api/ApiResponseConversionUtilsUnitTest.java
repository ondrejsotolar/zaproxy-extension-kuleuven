package org.zaproxy.zap.extension.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.parosproxy.paros.network.HttpHeader;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.network.HttpRequestHeader;
import org.parosproxy.paros.network.HttpResponseHeader;
import org.zaproxy.zap.network.HttpRequestBody;
import org.zaproxy.zap.network.HttpResponseBody;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ApiResponseConversionUtilsUnitTest {
	
	@Mock
	HttpMessage message;
	
	@Mock
	HttpRequestHeader requestHeader;
	
	@Mock
	HttpRequestBody requestBody;

	@Mock
	HttpResponseHeader responseHeader;

	@Mock
	HttpResponseBody responseBody;

	@Before
	public void prepareMessage() {
		given(message.getRequestHeader()).willReturn(requestHeader);
		given(message.getRequestBody()).willReturn(requestBody);
		given(message.getResponseHeader()).willReturn(responseHeader);
		given(message.getResponseBody()).willReturn(responseBody);
	}
	
	@Test
	public void nameOfApiResponseShouldBeConstant() {
		ApiResponseSet<String> response = ApiResponseConversionUtils.httpMessageToSet(0, message);

		assertThat(response.getName(), is("message"));
	}

	@Test
	public void historyIdShouldBecomeIdOfApiResponse() {
		ApiResponseSet<String> response = ApiResponseConversionUtils.httpMessageToSet(42, message);

		assertThat(response.getValues(), hasEntry("id", "42"));
	}

	@Test
	public void shouldHaveUndefinedHistoryTypeByDefault() {
		// Given / When
		ApiResponseSet<String> response = ApiResponseConversionUtils.httpMessageToSet(0, message);
		// Then
		assertThat(response.getValues(), hasEntry("type", "-1"));
	}

	@Test
	public void shouldIncludeHistoryTypeInApiResponse() {
		// Given
		int historyType = 2;
		// When
		ApiResponseSet<String> response = ApiResponseConversionUtils.httpMessageToSet(0, historyType, message);
		// Then
		assertThat(response.getValues(), hasEntry("type", (Object) "2"));
	}

	@Test
	public void propertiesFromGivenHttpMessageShouldReflectInApiResponse() {
		given(message.getCookieParamsAsString()).willReturn("testCookieParams");
		given(message.getNote()).willReturn("testNote");
		given(requestHeader.toString()).willReturn("testRequestHeader");
		given(requestBody.toString()).willReturn("testRequestBody");
		given(responseHeader.toString()).willReturn("testResponseHeader");
		given(message.getTimeSentMillis()).willReturn(1010101010101L);
		given(message.getTimeElapsedMillis()).willReturn(200);
		
		ApiResponseSet<String> response = ApiResponseConversionUtils.httpMessageToSet(0, message);
		
		assertThat(response.getValues(), hasEntry("cookieParams", "testCookieParams"));
		assertThat(response.getValues(), hasEntry("note", "testNote"));
		assertThat(response.getValues(), hasEntry("requestHeader", requestHeader.toString()));
		assertThat(response.getValues(), hasEntry("requestBody", requestBody.toString()));
		assertThat(response.getValues(), hasEntry("responseHeader", responseHeader.toString()));
		assertThat(response.getValues(), hasEntry("timestamp", "1010101010101"));
		assertThat(response.getValues(), hasEntry("rtt", "200"));
	}

	@Test
	public void compressedResponseBodyShouldBeDeflatedIntoApiResponse() throws Exception {
		given(responseHeader.getHeader(HttpHeader.CONTENT_ENCODING)).willReturn(HttpHeader.GZIP);
		given(responseBody.getBytes()).willReturn(gzip(new byte[] {97, 98, 99}));

		ApiResponseSet<String> response = ApiResponseConversionUtils.httpMessageToSet(0, message);
		
		assertThat(response.getValues(), hasEntry("responseBody", "abc"));
	}

	@Test
	public void brokenCompressedResponseBodyShouldBeStoredAsStringRepresentationInApiResponse() {
		given(responseHeader.getHeader(HttpHeader.CONTENT_ENCODING)).willReturn(HttpHeader.GZIP);
		given(responseBody.getBytes()).willReturn(new byte[] {0,0,0});

		ApiResponseSet<String> response = ApiResponseConversionUtils.httpMessageToSet(0, message);
		
		assertThat(response.getValues(), hasEntry("responseBody", responseBody.toString()));
	}
	
	private byte[] gzip(byte[] raw) throws Exception {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream(raw.length);
		GZIPOutputStream zip = new GZIPOutputStream(bytes);
		zip.write(raw);
		zip.close();
		return bytes.toByteArray();
		
	}

}
