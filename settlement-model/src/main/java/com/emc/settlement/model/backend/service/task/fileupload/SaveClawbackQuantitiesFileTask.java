package com.emc.settlement.model.backend.service.task.fileupload;

public enum SaveClawbackQuantitiesFileTask {

	CAPTUREMETADATA(1, "captureMetaData"),
	STORECLAWBACKFILE(2, "storeClawbackFile"),
	UPDATEEVENT(3, "updateEvent"),
	ALERTNOTIFY(4, "alertNotify");

	private Integer key;
	private String value;

	SaveClawbackQuantitiesFileTask(Integer key, String value) {
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
