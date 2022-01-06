package com.emc.settlement.model.backend.exceptions;

public class VestingContractUploadException extends BaseException{

	public VestingContractUploadException() {
		super();
	}

	@Override
	public Integer getExceptionCode() {
		return ExceptionCode.VESTINGCONTRACTUPLOADEXCEPTION.getValue();
	}

	public String message;
	public String execStep;
	public int validationType;
	public int validationNumber;
    
    public VestingContractUploadException(int vNum, int vType, String message, String step) {
        super(vNum +","+ vType +","+ message +","+ step );        
        this.validationNumber = vNum;
        this.validationType = vType;
        this.execStep = step;
        this.message = message;
    }

}
