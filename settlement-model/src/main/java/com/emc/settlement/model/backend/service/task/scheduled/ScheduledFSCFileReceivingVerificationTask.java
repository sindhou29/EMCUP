package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledFSCFileReceivingVerificationTask {

	CREATEEVENT(1, "createEvent"), 
	CHECKEBTEVENT(2, "checkEBTEvent"), 
	UPDATEEVENT(3, "updateEvent"), 
	ALERTNOTIFICAION(4, "alertNotification")

	;
	private Integer key;
	private String value;

	ScheduledFSCFileReceivingVerificationTask(Integer key, String value) {
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
