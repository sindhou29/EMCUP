package com.emc.settlement.model.backend.service.task.fileupload;

public enum ForwardSalesContractUploadMainTask {

	CAPTUREMETADATA(1, "captureMetaData"),
	SENDACKTOMSSL(2, "sendACKToMSSL"),
	VALIDATEFSCFILE(3, "validateFSCFile"),
	UPDATEEBTEVENT(4, "updateEBTEvent"),
	FSCUPLOADEXCEPTION(5, "fscUploadException"),
	SENDEXCEPTIONNOTIFICATION(6, "sendExceptionNotification");

	private Integer key;
	private String value;

	ForwardSalesContractUploadMainTask(Integer key, String value) {
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
