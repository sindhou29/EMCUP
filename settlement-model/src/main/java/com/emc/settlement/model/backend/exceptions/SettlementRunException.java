package com.emc.settlement.model.backend.exceptions;

import static com.emc.settlement.model.backend.exceptions.ExceptionCode.SETTLEMENTRUNEXCEPTION;

public class SettlementRunException extends BaseException {
	
	public String message;
	public String execStep;

	
    public SettlementRunException() {
        super();   
    }
    
    public SettlementRunException(String message, String execStep) {
        this.message = message;
        this.execStep = execStep;
    }

	public String getExecStep() {
		return execStep;
	}

	public void setExecStep(String execStep) {
		this.execStep = execStep;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public Integer getExceptionCode() {
		return SETTLEMENTRUNEXCEPTION.getValue();
	}

	public void setMessage(String message) {
		this.message = message;
	}
    
}
