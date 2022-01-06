package com.emc.settlement.model.backend.service.task.runrelated;


public enum ApproveSettlementRunTask {

	FIRSTTIERAUTHSETTLEMENTRUN(1, "firstTierAuthSettlementRun"),
	EXCEPTIONHANDLER(2, "exceptionHandler");

	private Integer key;
	private String value;

	ApproveSettlementRunTask(Integer key, String value) {
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
