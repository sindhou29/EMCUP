package com.emc.settlement.model.backend.service.task.runrelated;


public enum ForceDenySettlementRunTask {

	FORCEDENYSETTRUN(1, "forceDenySettRun"),
	EXCEPTIONHANDLER(2, "exceptionHandler");

	private Integer key;
	private String value;

	ForceDenySettlementRunTask(Integer key, String value) {
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
