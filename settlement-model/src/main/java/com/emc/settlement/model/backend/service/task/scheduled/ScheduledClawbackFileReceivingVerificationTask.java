package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledClawbackFileReceivingVerificationTask {
	
	CREATEEVENT(1, "createEvent"),
	CHECKCLAWBACKFILE(2, "checkClawbackFile"),
	ALERTNOTIFICATION(3, "alertNotification"),
	UPDATEEVENT(4, "updateEvent"),
	INITIALIZEVARIABLES(5, "initializeVariables"),
	GETTRADINGDATESRANGE(6, "getTradingDatesRange"),
	CHECKSCHEDULEANDSHAREPLEX(7, "checkScheduleAndShareplex"),
	CHECKBUSINESSDAY(8, "checkBusinessDay")
	;
	private Integer key;
	private String value;

	ScheduledClawbackFileReceivingVerificationTask(Integer key, String value) {
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
