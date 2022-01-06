package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledRegenerationOfMCRDataTask {
	
	INITIALIZEVARIABLES(1,"initializeVariables"),
	INITIALIZE(2,"initialize"),
	CREATEMCEDATAPACKAGES(3,"createMCEDataPackages"),
	NEXTDAY(4,"nextDay"),
	GETTRADINGDATESRANGE(5, "getTradingDatesRange"),
	CHECKBUSINESSDAY(6, "checkBusinessDay"),
	CHECKSCHEDULEANDSHAREPLEX(7, "checkScheduleAndShareplex");

	private Integer key;
	private String value;

	ScheduledRegenerationOfMCRDataTask(Integer key, String value) {
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
