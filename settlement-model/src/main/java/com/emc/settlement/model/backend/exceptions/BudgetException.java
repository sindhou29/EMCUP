package com.emc.settlement.model.backend.exceptions;

public class BudgetException extends BaseException{

	public BudgetException() {
		super();
	}

	@Override
	public Integer getExceptionCode() {
		return ExceptionCode.BUDGETEXCEPTION.getValue();
	}
	
	public String message;
	public String execStep;
	public int validationType;
	public int validationNumber;
	public int rowNumber;
	
    
    public BudgetException(int vNum, int vType, String msg, String step, int rowNumber) {
        super(vNum +","+ vType +","+ msg +","+ step+" ,"+ rowNumber );        
        this.validationNumber = vNum;
        this.validationType = vType;
        this.execStep = step;
        this.message = msg;
    }
}
