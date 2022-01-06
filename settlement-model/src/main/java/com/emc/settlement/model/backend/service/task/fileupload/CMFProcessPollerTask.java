package com.emc.settlement.model.backend.service.task.fileupload;

public enum CMFProcessPollerTask {

	CHECKUNRESPONSEPROCESSES(1, "checkUnresponseProcesses"),
	CHECKUNPROCESSEDCMF(2, "checkUnProcessedCMF"),
	GETSHAREPLEXMODE(3, "getShareplexMode");

	private Integer key;
	private String value;

	CMFProcessPollerTask(Integer key, String value) {
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
