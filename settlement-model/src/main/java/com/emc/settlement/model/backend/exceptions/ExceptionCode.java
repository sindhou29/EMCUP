package com.emc.settlement.model.backend.exceptions;

public enum ExceptionCode {

	BILATERALCONTRACTUPLOADEXCEPTION("BilateralContractUploadException",1),
	MMVOLUMEUPLOADEXCEPTION("MMVolumeUploadException",2),
	ACCOUNTVALIDATIONEXCEPTION("AccountValidationException",3),
	AUTHORISATIONEXCEPTION("AuthorisationException",4),
	VESTINGCONTRACTUPLOADEXCEPTION("VestingContractUploadException",5),
	MSSLEXCEPTION("MsslException",6),
	ENERGYBIDPRICEMINEXCEPTION("EnergyBidPriceMinException",7),
	BUDGETEXCEPTION("BudgetException",8),
	MSSLCORRECTEDFILEEXCEPTION("MsslCorrectedFileException",9),
	CLAWBACKQUANTITIESEXCEPTION("ClawbackQuantitiesException",10),
	FSCUPLOADEXCEPTION("FSCUploadException",11),
	SETTRUNINFOEXCEPTION("SettRunInfoException",12),
	SETTLEMENTRUNEXCEPTION("SettlementRunException",13),
	USAPEXCEPTION("USAPException",14),
	VALIDATEDATAEXCEPTION("ValidateDataException",15),
	VALIDATERUNEXCEPTION("ValidateRunException",16),
	SCHEDULEDVESTINGCONTRACTEXCEPTION("ScheduledVestingContractFileReceivingException",17);

	public final String key;
	public final Integer value;

	ExceptionCode(String key, Integer value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}
	public Integer getValue() {
		return value;
	}

}
