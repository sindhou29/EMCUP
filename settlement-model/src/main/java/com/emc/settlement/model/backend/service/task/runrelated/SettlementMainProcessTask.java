package com.emc.settlement.model.backend.service.task.runrelated;


public enum SettlementMainProcessTask {

	INITIALIZEVARIABLES(1, "initializeVariables"),
	CREATESCHEDULEEVENT(2, "createScheduleEvent"),
	CREATERISKREPORT(3, "createRiskReport"),
	STARTDAILYPRUDENTIALPROCESS(4, "startDailyPrudentialProcess"),
	UPDATEEVENTSTATUS(5, "updateEventStatus"),
	EXCEPTIONHANDLER(6, "exceptionHandler"),
	GENERATEADFFILE(7, "generateADFFile");

	private Integer key;
	private String value;

	SettlementMainProcessTask(Integer key, String value) {
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
