package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceTask {

	CREATEEVENT(1, "createEvent"), 
	CHECKAUTHFEESDATA(2, "checkAuthFeesData"), 
	SENDALERTEMAIL(3,"sendAlertEmail"), 
	UPDATEEVENT(4, "updateEvent"),
	INITIALIZEVARIABLES(5, "initializeVariables"),
	GETTRADINGDATESRANGE(6, "getTradingDatesRange"),
	CHECKSCHEDULEANDSHAREPLEX(7, "checkScheduleAndShareplex"),
	CHECKBUSINESSDAY(8, "checkBusinessDay");

	private Integer key;
	private String value;

	ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceTask(Integer key, String value) {
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
