package com.emc.settlement.model.backend.service.task.fileupload;

public enum BilateralContractUploadMainTask {

	CAPTUREFILEUPLOADPARAMETERS(1, "captureFileUploadParameters"),
	STORECSVFILEINTODATABASE(2, "storeCSVFileIntoDatabase"),
	VALIDATEFILE(3, "validateFile"),
	UPDATEEBTEVENT(4, "updateEBTEvent"),
	HANDLEEXCEPTION(5, "handleException"),
	SENDEXCEPTIONNOTIFICATION(6, "sendExceptionNotification");

	private Integer key;
	private String value;

	BilateralContractUploadMainTask(Integer key, String value) {
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
