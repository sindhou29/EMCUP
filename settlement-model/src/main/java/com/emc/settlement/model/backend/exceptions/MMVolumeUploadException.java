/**
 * 
 */
package com.emc.settlement.model.backend.exceptions;

/**
 * @author DWTN1561
 *
 */
public class MMVolumeUploadException extends BaseException{
	
	public String message;
	public String execStep;
	public int validationType;
	public int validationNumber;

	/**
	 * 
	 */
	public MMVolumeUploadException() {
		super();
	}

	@Override
	public Integer getExceptionCode() {
		return ExceptionCode.MMVOLUMEUPLOADEXCEPTION.getValue();
	}

	public MMVolumeUploadException(int vNum, int vType, String msg, String step) {
        super(vNum +","+ vType +","+ msg +","+ step );        
        this.validationNumber = vNum;
        this.validationType = vType;
        this.execStep = step;
        this.message = msg;
    }

}
