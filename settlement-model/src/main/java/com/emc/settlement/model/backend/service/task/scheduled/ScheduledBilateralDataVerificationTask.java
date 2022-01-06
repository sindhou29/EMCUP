package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledBilateralDataVerificationTask {

	INITIALIZEVARIABLES(1, "initializeVariables"),
	CHECK(2, "check"), 
	CREATEEVENT(3, "createEvent"), 
	UPDATEEVENT(4, "updateEvent"), 
	SENDALERTEMAIL(5, "sendAlertEmail"),
	GETTRADINGDATESRANGE(6, "getTradingDatesRange"),
	CHECKSCHEDULEANDSHAREPLEX(7, "checkScheduleAndShareplex"),
	CHECKBUSINESSDAY(8, "checkBusinessDay");

	private Integer key;
	private String value;

	ScheduledBilateralDataVerificationTask(Integer key, String value) {
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
