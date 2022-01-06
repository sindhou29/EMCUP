package com.emc.settlement.model.backend.service.task.runrelated;


public enum RunValidationsTask {

	VALIDATESETTLEMENTRUN(1, "validateSettlementRun"),
	NOTVALID(2, "notValid"),
	FORTESTRUN(3, "forTestRun"),
	EXCEPTIONHANDLER(4, "exceptionHandler");

	private Integer key;
	private String value;

	RunValidationsTask(Integer key, String value) {
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
