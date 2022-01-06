package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledMSSLDataVerificationTask {

	INITIALIZEVARIABLES(1, "initializeVariables"), 
	PREPAREDATA(2, "prepareData"), 
	CHECKMSSLVERSION(3, "checkMSSLVersion"), 
	UPDATEEVENT(4, "updateEvent"), 
	SENDALERTEMAIL(5, "sendAlertEmail"),
	GETTRADINGDATESRANGE(6, "getTradingDatesRange"),
	CHECKSCHEDULEANDSHAREPLEX(7, "checkScheduleAndShareplex"),
	CHECKBUSINESSDAY(8, "checkBusinessDay");

	;
	private Integer key;
	private String value;

	ScheduledMSSLDataVerificationTask(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
