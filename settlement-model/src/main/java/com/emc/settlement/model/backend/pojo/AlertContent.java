package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.util.Date;

public class AlertContent implements Serializable{

	public String comment;
	public String errorMsg;
	public String filename;
	public String settlementDate;
	public String uploadUser;
	
	public Date uploadTime;
	public Date validatedTime;
	
	public boolean valid;
	
	
	
	

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(String settlementDate) {
		this.settlementDate = settlementDate;
	}

	public String getUploadUser() {
		return uploadUser;
	}

	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public Date getValidatedTime() {
		return validatedTime;
	}

	public void setValidatedTime(Date validatedTime) {
		this.validatedTime = validatedTime;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
}
