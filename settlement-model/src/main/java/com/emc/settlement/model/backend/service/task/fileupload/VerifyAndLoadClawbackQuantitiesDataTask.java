package com.emc.settlement.model.backend.service.task.fileupload;

public enum VerifyAndLoadClawbackQuantitiesDataTask {

	VERIFYANDLOADCLAWBACKDATA(1, "verifyAndLoadClawbackData"),
	UPDATEEVENT(2, "updateEvent"),
	UPDATEFAILEVENT(3, "updateFailEvent"),
	ALERTNOTIFY(4, "alertNotify");



	private Integer key;
	private String value;

	VerifyAndLoadClawbackQuantitiesDataTask(Integer key, String value) {
		this.key = key;
		this.value = value;
	}

	public Integer getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}
}
