package com.emc.settlement.model.backend.exceptions;

public class MsslException extends BaseException {
	
	public String validationType;
	public int validationNumber;
	public int rowNumber;
	public String errorMsg;
	public String execStep;
	
    public MsslException() {
        super();   
    }

	@Override
	public Integer getExceptionCode() {
		return ExceptionCode.MSSLEXCEPTION.getValue();
	}

	public MsslException(String validationType, int validationNumber, int rowNumber,String errorMsg, String execStep) {
        super(validationType +","+ validationNumber +","+ rowNumber +","+ errorMsg +","+ execStep);        
        this.validationType = validationType;
        this.validationNumber = validationNumber;
        this.rowNumber = rowNumber;
        this.errorMsg = errorMsg;
        this.execStep = execStep;
    }
    

	public String getValidationType() {
		return validationType;
	}

	public int getValidationNumber() {
		return validationNumber;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public String getExecStep() {
		return execStep;
	}

}
