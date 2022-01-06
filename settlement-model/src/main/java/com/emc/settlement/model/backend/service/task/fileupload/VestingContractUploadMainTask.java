package com.emc.settlement.model.backend.service.task.fileupload;

public enum VestingContractUploadMainTask {

	CAPTUREMETADATA(1, "captureMetaData"),
	SENDACKTOMSSL(2, "sendACKToMSSL"),
	VALIDATEVESTINGFILE(3, "validateVestingFile"),
	UPDATEEBTEVENT(4, "updateEBTEvent"),
	CHECKCUTOFF(5, "checkCutoff"),
	CALCULATEENERGYBIDPRICEMIN(6, "calculateEnergyBidPriceMin"),
	HANDLEEXCEPTION(7, "handleException"),
	SENDEXCEPTIONNOTIFICATION(8, "sendExceptionNotification"),
	ENERGYBIDPRICEMINEXCEPTION(9, "energyBidPriceMinException"),
	SENDWARNINGNOTIFICATION(10, "sendWarningNotification");

	private Integer key;
	private String value;

	VestingContractUploadMainTask(Integer key, String value) {
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
