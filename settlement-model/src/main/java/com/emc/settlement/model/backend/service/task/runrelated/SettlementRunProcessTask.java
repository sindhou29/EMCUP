package com.emc.settlement.model.backend.service.task.runrelated;


public enum SettlementRunProcessTask {

	STARTLOGGINGMESSAGES(1, "startLoggingMessages"),
	REGISTEREVENTFORRUN(2, "registerEventForRun"),
	PREREQUISITECOMPLETE(3, "preRequisiteComplete"),
	PREPARERUNPACKAGES(4, "prepareRunPackages"),
	CREATERUNENTRY(5, "createRunEntry"),
	INVOKERULES(6, "invokeRules"),
	EXCEPTIONNOTIFICATION(7, "exceptionNotification"),
	UPDATETESTRUNSTATUS(8, "updateTestRunStatus");

	private Integer key;
	private String value;

	SettlementRunProcessTask(Integer key, String value) {
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
