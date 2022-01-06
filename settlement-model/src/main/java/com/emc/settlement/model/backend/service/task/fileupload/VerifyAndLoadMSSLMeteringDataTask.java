package com.emc.settlement.model.backend.service.task.fileupload;

public enum VerifyAndLoadMSSLMeteringDataTask {

	UPDATEPROCESSEDSTATUS(1, "updateProcessedStatus"),
	VALIDATEANDLOADCMFDATA(2, "validateAndLoadCMFData"),
	VALIDATEANDLOADDAILYMETERINGDATA(3, "validateAndLoadDailyMeteringData"),
	UPDATEEVENT(4, "updateEvent"),
	UPDATEFAILEVENT(5, "updateFailEvent"),
	ALERTNOTIFY(6, "alertNotify");

	private Integer key;
	private String value;

	VerifyAndLoadMSSLMeteringDataTask(Integer key, String value) {
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
