package com.emc.settlement.common;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

@Component
public interface KieServerService {

	public <T> T getRequest(String request) throws Exception;

	public <P> JsonNode postData(String request);

	public <T,P> T postData(String request, P payLoad) throws Exception;
}
