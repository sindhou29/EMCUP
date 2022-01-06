package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledCMWHDataVerificationTask {
	
	INITIALIZEVARIABLES(1, "initializeVariables"),
	CHECKCMWH(2, "checkCMWH"), 
	CREATEEVENT(3, "createEvent"), 
	UPDATEEVENT(4, "updateEvent"), 
	ALERTNOTIFICATION(5, "alertNotification"),
	GETTRADINGDATESRANGE(6, "getTradingDatesRange"),
	CHECKSCHEDULEANDSHAREPLEX(7, "checkScheduleAndShareplex"),
	CHECKBUSINESSDAY(8, "checkBusinessDay");
	
	private Integer key;
	private String value;

	ScheduledCMWHDataVerificationTask(Integer key, String value) {
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


