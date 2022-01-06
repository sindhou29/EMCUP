package com.emc.settlement.model.backend.service.task.runrelated;


public enum FinalAuthoriseSettlementRunTask {

	AUTHORISATIONSETTLEMENTRUN(1, "authorisationSettlementRun"),
	CREATEUSAPFILEFROMMSSL(2, "createUsapFileForMSSL"),
	GENERATEACCOUNTFILES(3, "generateAccountFiles"),
	STARTDAILYMANUALPROCESS(4, "startDailyManualProcess"),
	STARTMMBRAP(5, "startMMBRAP"),
	STARTMMBRAR(6, "startMMBRAR"),
	EXCEPTIONHANDLER(7, "exceptionHandler");


	private Integer key;
	private String value;

	FinalAuthoriseSettlementRunTask(Integer key, String value) {
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
