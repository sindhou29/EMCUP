package com.emc.settlement.model.backend.service.task.scheduled;

public enum ScheduledDailyRunSummaryTask {

	CHECKTODAYRUN(1, "checkTodayRun"),
	INITIALIZEVARIABLES(2, "initializeVariables"),
	GETTRADINGDATESRANGE(3, "getTradingDatesRange");


	private Integer key;
	private String value;

	ScheduledDailyRunSummaryTask(Integer key, String value) {
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
