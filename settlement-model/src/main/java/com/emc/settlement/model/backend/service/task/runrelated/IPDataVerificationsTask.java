package com.emc.settlement.model.backend.service.task.runrelated;


public enum IPDataVerificationsTask {

	EXCEPTIONHANDLER(1, "exceptionHandler"),
	OTHEREXCEPTIONHANDLER(2, "otherExceptionHandler");

	private Integer key;
	private String value;

	IPDataVerificationsTask(Integer key, String value) {
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
