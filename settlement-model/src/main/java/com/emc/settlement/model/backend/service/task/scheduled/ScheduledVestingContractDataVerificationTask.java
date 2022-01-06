package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledVestingContractDataVerificationTask {
	
	INITIALIZEVARIABLES(1, "initializeVariables"), 
	INITIALIZESETTLEMENTDATE(2, "initializeSettlementDate"), 
	CHECKCUTOFFTIME(3, "checkCutoffTime"), 
	SENDNOTIFICATION(4, "alertNotification"),
	UPDATEEVENT(5, "updateEvent"),
	GETTRADINGDATESRANGE(6, "getTradingDatesRange"),
	CHECKSCHEDULEANDSHAREPLEX(7, "checkScheduleAndShareplex"),
	CHECKBUSINESSDAY(8, "checkBusinessDay");

	private Integer key;
	private String value;

	ScheduledVestingContractDataVerificationTask(Integer key, String value) {
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
