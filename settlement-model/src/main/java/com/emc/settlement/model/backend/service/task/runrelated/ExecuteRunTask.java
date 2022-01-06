package com.emc.settlement.model.backend.service.task.runrelated;


public enum ExecuteRunTask {

	MANUALSETTLEMENTDATE(1, "manualSettlementDate");

	private Integer key;
	private String value;

	ExecuteRunTask(Integer key, String value) {
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
