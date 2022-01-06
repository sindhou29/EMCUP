package com.emc.settlement.model.backend.service.task.fileupload;

public enum CMFEmailNotificationTask {

	CHECKEMAILSTATUS(1, "checkEmailStatus");

	private Integer key;
	private String value;

	CMFEmailNotificationTask(Integer key, String value) {
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
