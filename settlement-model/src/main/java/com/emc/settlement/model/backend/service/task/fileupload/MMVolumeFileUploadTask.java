package com.emc.settlement.model.backend.service.task.fileupload;

public enum MMVolumeFileUploadTask {

	CAPTUREMETADATA(1, "captureMetaData"),
	VALIDATEMMFILE(2, "validateMMFile"),
	UPDATEEBTEVENT(3, "updateEBTEvent"),
	MMFILEUPLOADEXCEPTION(4, "mmFileUploadException"),
	SENDEXCEPTIONNOTIFICATION(5, "sendExceptionNotification");

	private Integer key;
	private String value;

	MMVolumeFileUploadTask(Integer key, String value) {
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
