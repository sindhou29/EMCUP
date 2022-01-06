package com.emc.settlement.model.backend.service.task.runrelated;


public enum LoadPendingCMFFilesTask {

	LOADPENDINGCMFFILES(1, "loadPendingCMFFiles");

	private Integer key;
	private String value;

	LoadPendingCMFFilesTask(Integer key, String value) {
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
