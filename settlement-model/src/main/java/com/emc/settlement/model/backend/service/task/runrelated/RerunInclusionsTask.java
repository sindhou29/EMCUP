package com.emc.settlement.model.backend.service.task.runrelated;


public enum RerunInclusionsTask {

	INCLUDERERUNSPRELIM(1, "includeRerunsPrelim"),
	INCLUDERERUNSFINAL(2, "includeRerunsFinal"),
	GETLASTINCLUDEDRERUNS(3, "getLastIncludedReruns");

	private Integer key;
	private String value;

	RerunInclusionsTask(Integer key, String value) {
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
