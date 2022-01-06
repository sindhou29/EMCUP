package com.emc.settlement.model.backend.exceptions;

import static com.emc.settlement.model.backend.exceptions.ExceptionCode.SCHEDULEDVESTINGCONTRACTEXCEPTION;

public class VestingContractException extends BaseException {
	
	public String msg;
	public String msgStep;
	
    public VestingContractException() {
        super();   
    }

	@Override
	public Integer getExceptionCode() {
		return SCHEDULEDVESTINGCONTRACTEXCEPTION.getValue();
	}

	public VestingContractException(String msgArg, String msgStepArg) {
        super(msgArg +","+ msgStepArg);        
        this.msg = msgArg;
        this.msgStep = msgStepArg;
    }

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsgStep() {
		return msgStep;
	}

	public void setMsgStep(String msgStep) {
		this.msgStep = msgStep;
	}
    


}
