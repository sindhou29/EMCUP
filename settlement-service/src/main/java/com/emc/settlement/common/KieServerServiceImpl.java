package com.emc.settlement.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import sun.misc.BASE64Encoder;

import org.springframework.stereotype.Component;

@Component
public class KieServerServiceImpl implements KieServerService {

	private static final Logger logger = Logger.getLogger(KieServerServiceImpl.class);
	
	public String accessKey;
	private static String bpmsUserId;
	private static String bpmsEncrypted;
	private static String bpmsKey;
	private static String bpmsPWD;
	private static String rulesHost;


	private String serviceUrl;

	public KieServerServiceImpl() {
		loadProperties();
		this.serviceUrl = rulesHost;//"http://10.1.152.124:11080";
	}

	@Override
	public <T> T getRequest(String request) throws Exception{
		return parseResponse(request, null, "GET");
	}

	@Override
	public <P> JsonNode postData(String request) {
		return getJsonData(request, null, "POST");
	}

	@Override
	public <T, P> T postData(String request, P payLoad) throws Exception {
		return parseResponse(request, payLoad, "POST");
	}

	private <P> JsonNode getJsonData(String request, P payLoad, String requestType) {
		HttpURLConnection connection = null;
		InputStream in = null;
		try {
			connection = getConnection(serviceUrl + request, payLoad, requestType);
			logger.info("getJsonData : " + serviceUrl + request);
			in = connection.getInputStream();
			return mapJsonToNode(in);
		}
		catch (IOException e) {
			logger.error("Connection failed to " + serviceUrl + request + e.getMessage());
		}
		finally {
			saveClose(in);
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}

	private void saveClose(InputStream in) {
		if (in != null) {
			try {
				in.close();
			}
			catch (IOException e) {
			}
		}
	}

	private <T, P> T parseResponse(String request, P payLoad, String requestType) throws Exception {

		HttpURLConnection connection = null;
		InputStream in = null;
		try {
			connection = getConnection(serviceUrl + request, payLoad, requestType);
			logger.info("parseResponse : " + serviceUrl + request);
			int responseCode = connection.getResponseCode();
			switch (responseCode) {
				case HttpURLConnection.HTTP_OK:
				case HttpURLConnection.HTTP_CREATED:
					in = connection.getInputStream();
					break;
				case HttpURLConnection.HTTP_NO_CONTENT:
					return null;
				case HttpURLConnection.HTTP_UNAUTHORIZED:
					throw new Exception(connection.getResponseMessage());
				case HttpURLConnection.HTTP_INTERNAL_ERROR:
					InputStream errorStream = connection.getErrorStream();
					String errorString = readErrorStream(errorStream);
					if(errorString.contains(" No process available with given id")) {
						throw new Exception(connection.getResponseMessage());
					}
			}

			return mapJsonToObject(in);
		}
		catch (IOException e) {

			logger.error("Exception Connection failed to " + serviceUrl + request + e.getMessage());
		}
		finally {
			saveClose(in);
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}

	private String readErrorStream(InputStream errorStream) throws IOException {

		StringBuilder builder = new StringBuilder();
		try(BufferedReader in = new BufferedReader(new InputStreamReader(errorStream))) {
			String line;
			while((line = in.readLine())!= null) {
				builder.append(line);
			}
			in.close();
		}
		return builder.toString();
	}

	private JsonNode mapJsonToNode(InputStream in) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readTree(in);
	}

	private <T> T mapJsonToObject(InputStream in) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(in, new TypeReference<T>() {
		});
	}

	private <P> HttpURLConnection getConnection(String serviceUrl, P payLoad, String requestType) throws IOException {

		URL url = new URL(serviceUrl);
		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Accept", "application/json");
		connection.setConnectTimeout(1200000);

		if ("GET".equals(requestType)) {
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/plain");
		}
		else if ("POST".equals(requestType)) {
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			final String username = bpmsUserId;//"bpmsadmin";
			final String password = bpmsPWD;//"P@ssw0rd321";
			final String authString = username + ":" + password;
			
			
			BASE64Encoder enc = new BASE64Encoder();
			String authEncodedStr = new String(enc.encode(authString.getBytes()));
			connection.setRequestProperty("Authorization", "Basic " + authEncodedStr);
			if (payLoad != null) {
				connection.setUseCaches(false);
				connection.setRequestProperty("charset", "utf-8");
				connection.setRequestProperty("Connection", "close");
				connection.setRequestProperty("Content-Type", "application/json");
				ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(payLoad);
				connection.getOutputStream().write(json.getBytes("UTF-8"));
			}
		}
		return connection;
	}
	
	
	private void loadProperties() {
		
		Map<String, String> propertiesMap = UtilityFunctions.getProperties();
		bpmsUserId = propertiesMap.get("bpmsUserId");
		bpmsEncrypted = propertiesMap.get("bpmsEncrypted");
		if(bpmsEncrypted!=null && bpmsEncrypted.equals("Y")) {
			bpmsKey = propertiesMap.get("bpmsKey");
			try {
				bpmsPWD = EncryptionService.decryptString(propertiesMap.get("bpmsPWD"), bpmsKey);
			}
			catch (Exception e) {
				logger.error("Password encryption failed");
			}
		} else {
			bpmsPWD = propertiesMap.get("bpmsPWD");
		}
		rulesHost = propertiesMap.get("rulesHost");
	    
	}
}
