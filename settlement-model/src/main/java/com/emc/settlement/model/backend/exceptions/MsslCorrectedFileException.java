package com.emc.settlement.model.backend.exceptions;

public class MsslCorrectedFileException extends BaseException {

	public String errorMsg;
	public int rowNumber;
	public int validationNumber;
	public String validationType;
	
    public MsslCorrectedFileException() {
        super();   
    }

	@Override
	public Integer getExceptionCode() {
		return ExceptionCode.MSSLCORRECTEDFILEEXCEPTION.getValue();
	}

	public MsslCorrectedFileException(String validationType, int validationNumber, int rowNumber,String errorMsg) {
        super(validationType +","+ validationNumber +","+ rowNumber +","+ errorMsg);        
        this.validationType = validationType;
        this.validationNumber = validationNumber;
        this.rowNumber = rowNumber;
        this.errorMsg = errorMsg;
    }

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public int getValidationNumber() {
		return validationNumber;
	}

	public void setValidationNumber(int validationNumber) {
		this.validationNumber = validationNumber;
	}

	public String getValidationType() {
		return validationType;
	}

	public void setValidationType(String validationType) {
		this.validationType = validationType;
	}
}
