package com.emc.settlement.model.backend.exceptions;

public class EnergyBidPriceMinException extends BaseException {

	public EnergyBidPriceMinException() {
		super();
	}

	@Override
	public Integer getExceptionCode() {
		return ExceptionCode.ENERGYBIDPRICEMINEXCEPTION.getValue();
	}

	public String message;
	public String execStep;
	public int validationType;
	public int validationNumber;
    
    public EnergyBidPriceMinException(int vType, int vNum, String message, String step) {
        super(vType +","+ vNum +","+ message +","+ step );        
        this.validationNumber = vNum;
        this.validationType = vType;
        this.execStep = step;
        this.message = message;
    }

}
