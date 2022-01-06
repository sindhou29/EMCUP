package com.emc.settlement.model.backend.service.task.runrelated;


public enum SetPackageAuthorizationTask {

	SETTPKGAUTHORIZATION(1, "settPkgAuthorization");

	private Integer key;
	private String value;

	SetPackageAuthorizationTask(Integer key, String value) {
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
