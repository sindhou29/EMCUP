package com.emc.settlement.model.backend.exceptions;

import static com.emc.settlement.model.backend.exceptions.ExceptionCode.VALIDATERUNEXCEPTION;

public class ValidateRunException extends BaseException {
	
	public String msg;
	
    public ValidateRunException() {
        super();   
    }
    
    public ValidateRunException(String msgArg) {
        super(msgArg);        
        this.msg = msgArg;

    }

	@Override
	public Integer getExceptionCode() {
		return VALIDATERUNEXCEPTION.getValue();
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
