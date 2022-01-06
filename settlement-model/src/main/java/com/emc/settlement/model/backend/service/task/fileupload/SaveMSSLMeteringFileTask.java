package com.emc.settlement.model.backend.service.task.fileupload;


public enum SaveMSSLMeteringFileTask {

	CAPTUREMETADATA(1, "captureMetaData"),
	SENDACKTOMSSL(2, "sendACKToMSSL"),
	STOREMETERINGFILE(3, "storeMeteringFile"),
	UPDATEEVENT(4, "updateEvent"),
	ALERTNOTIFY(5, "alertNotify");

	private Integer key;
	private String value;

	SaveMSSLMeteringFileTask(Integer key, String value) {
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
