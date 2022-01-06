package com.emc.settlement.model.backend.service.task.fileupload;

public enum EMCPSOBudgetUploadTask {

	CAPTUREFILEUPLOADPARAMETERS(1, "captureFileUploadParameters"),
	VALIDATEFILE(2, "validateFile"),
	GETBUDGETDATA(3, "getBudgetData"),
	VALIDATEBUDGETDATA(4, "validateBudgetData"),
	UPLOADBUDGETDATA(5, "uploadBudgetData"),
	UPDATEEBTEVENT(6, "updateEBTEvent"),
	HANDLEEXCEPTION(7, "handleException"),
	SENDEXCEPTIONNOTIFICATION(8, "sendExceptionNotification");


	private Integer key;
	private String value;

	EMCPSOBudgetUploadTask(Integer key, String value) {
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
