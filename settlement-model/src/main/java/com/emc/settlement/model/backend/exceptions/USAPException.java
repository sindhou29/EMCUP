package com.emc.settlement.model.backend.exceptions;

import static com.emc.settlement.model.backend.exceptions.ExceptionCode.USAPEXCEPTION;

public class USAPException extends BaseException {
	
	public String message;

	
    public USAPException() {
        super();   
    }
    
    public USAPException(String message) {
        this.message = message;
    }

	public String getMessage() {
		return message;
	}

	@Override
	public Integer getExceptionCode() {
		return USAPEXCEPTION.getValue();
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
}
