package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledRerunAccountsVerificationTask {
	
	PREPAREDATA(1, "prepareData"), 
	VERIFYACCOUNTSEXISTENCE(2, "verifySettAccountsInRerun"), 
	PREPAREALERTINFO(3,"prepareAlertInfo"), 
	UPDATEEVENT(4, "updateEvent"),
	INITIALIZEVARIABLES(5, "initializeVariables"),
	GETTRADINGDATESRANGE(6, "getTradingDatesRange"),
	CHECKSCHEDULEANDSHAREPLEX(7, "checkScheduleAndShareplex"),
	CHECKBUSINESSDAY(8, "checkBusinessDay");
	
	private Integer key;
	private String value;
	
	ScheduledRerunAccountsVerificationTask(Integer key, String value) {
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
