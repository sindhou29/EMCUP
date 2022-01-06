package com.emc.settlement.model.backend.service.task.runrelated;


public enum CashFlowReconciliationTask {

	CHECKRUNSTATUS(1, "checkRunStatus"),
	GENERATEEXCELSHEET(2, "generateExcelsheet"),
	CALCULATECASHFLOW(3, "calculateCashFlow");

	private Integer key;
	private String value;

	CashFlowReconciliationTask(Integer key, String value) {
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
