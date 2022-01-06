/**
 * 
 */
package com.emc.settlement.model.backend.exceptions;

import lombok.Data;

/**
 * @author DWTN1561
 *
 */
@Data
public class BilateralContractUploadException extends BaseException {

	/**
	 * 
	 */
	public BilateralContractUploadException() {
		super();
	}
	
	public String execStep;
	public int validationType;
	public int validationNumber;
	
    
    public BilateralContractUploadException(int vNum, int vType, String msg, String step) {
        super(vNum +","+ vType +","+ msg +","+ step );        
        this.validationNumber = vNum;
        this.validationType = vType;
        this.execStep = step;
        this.message = msg;
    }

    @Override
	public Integer getExceptionCode() {
		return ExceptionCode.BILATERALCONTRACTUPLOADEXCEPTION.getValue();
	}

}
