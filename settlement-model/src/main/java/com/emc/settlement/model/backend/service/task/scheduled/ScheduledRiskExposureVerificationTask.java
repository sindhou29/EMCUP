package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledRiskExposureVerificationTask {

	CREATEEVENT(1, "createEvent"), 
	CREATERISKREPORT(2, "createRiskReport"), 
	UPDATEEVENT(3, "updateEvent"),
	INITIALIZEVARIABLES(4, "initializeVariables"),
	GETTRADINGDATESRANGE(5, "getTradingDatesRange"),
	CHECKSCHEDULEANDSHAREPLEX(7, "checkScheduleAndShareplex");


	private Integer key;
	private String value;

	ScheduledRiskExposureVerificationTask(Integer key, String value) {
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
