package com.emc.settlement.model.backend.service.task.fileupload;

public enum BilateralContractValidateAndUploadDataTask {

	VALIDATEBILATERALCONTRACTS(1, "validateBilateralContracts"),
	UPLOADBILATERALCONTRACTS(2, "uploadBilateralContracts"),
	HANDLEEXCEPTION(3, "handleException");

	private Integer key;
	private String value;

	BilateralContractValidateAndUploadDataTask(Integer key, String value) {
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
