package com.emc.settlement.model.backend.service.task.runrelated;

public enum PerformTestRunTask {

	INITIALIZEVARIABLES(1, "initializeVariables");

	private Integer key;
	private String value;

	PerformTestRunTask(Integer key, String value) {
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
