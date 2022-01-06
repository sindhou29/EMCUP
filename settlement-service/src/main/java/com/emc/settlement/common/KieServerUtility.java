package com.emc.settlement.common;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KieServerUtility {

	private static final Logger logger = Logger.getLogger(KieServerUtility.class);
	
	@Autowired
	private KieServerService kieServerService;

	public KieServerUtility(KieServerService kieServerService) {
		this.kieServerService = kieServerService;
	}

	public <P> Integer startProcessInstance(String request, P payload) throws Exception {
		
		try {
			return kieServerService.postData(request, payload);
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new Exception(e.getMessage());
		} 
	}

}
