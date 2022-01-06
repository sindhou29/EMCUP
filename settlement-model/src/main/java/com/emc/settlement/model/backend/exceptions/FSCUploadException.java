package com.emc.settlement.model.backend.exceptions;

public class FSCUploadException extends BaseException{

	public FSCUploadException() {
		super();
	}

	@Override
	public Integer getExceptionCode() {
		return ExceptionCode.FSCUPLOADEXCEPTION.getValue();
	}

	public String message;
	public String execStep;
	public int validationType;
	public int validationNumber;
	
    
    public FSCUploadException(int vNum, int vType, String msg, String step) {
        super(vNum +","+ vType +","+ msg +","+ step );        
        this.validationNumber = vNum;
        this.validationType = vType;
        this.execStep = step;
        this.message = msg;
    }

}
