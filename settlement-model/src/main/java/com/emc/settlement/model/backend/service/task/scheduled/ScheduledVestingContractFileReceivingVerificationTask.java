package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledVestingContractFileReceivingVerificationTask {
	
	INITIALIZEVARIABLES(1, "initializeVariables"), 
	CHECKCOUNT(2, "checkCount"), 
	CREATEEVENT(3, "createEvent"), 
	CHECKEBTEVENT(4, "checkEBTEvent"), 
	ALERTNOTIFICATION(5, "alertNotification"),
	UPDATEEVENT(6, "updateEvent"),
	CHECKSCHEDULEANDSHAREPLEX(7, "checkScheduleAndShareplex");

	private Integer key;
	private String value;

	ScheduledVestingContractFileReceivingVerificationTask(Integer key, String value) {
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
