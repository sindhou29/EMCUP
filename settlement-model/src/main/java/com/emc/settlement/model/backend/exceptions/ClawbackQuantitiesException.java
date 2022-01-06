/**
 * 
 */
package com.emc.settlement.model.backend.exceptions;

/**
 * @author DWTN1561
 *
 */
public class ClawbackQuantitiesException extends BaseException{

	/**
	 * 
	 */
	public ClawbackQuantitiesException() {
		super();
	}

	@Override
	public Integer getExceptionCode() {
		return ExceptionCode.CLAWBACKQUANTITIESEXCEPTION.getValue();
	}

	public String errorMsg;
	public String execStep;
	public String validationType;
	public int validationNumber;
	public int rowNumber;
	
    
    public ClawbackQuantitiesException(String vType, int vNum, int rowNum, String errorMsg, String step) {
        super(vType +","+ vNum +","+ rowNum +","+ errorMsg +","+ step );        
        this.validationNumber = vNum;
        this.rowNumber = rowNum;
        this.validationType = vType;
        this.execStep = step;
        this.errorMsg = errorMsg;
    }
}
