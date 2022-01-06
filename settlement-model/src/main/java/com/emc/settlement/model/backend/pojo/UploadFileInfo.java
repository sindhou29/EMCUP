package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.util.Date;


public class UploadFileInfo  implements Serializable{
	
	public String comments="";
	public String compressed;
	public String contentFormat;
	public String fileType;
	public String filename;
	public String transId;
	public String uploadUsername;

	public Date settlementDate;
	public Date uploadTime;
	
	
	public String getComments() {
		return _returnEmptyStringForNulls(comments);
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCompressed() {
		return compressed;
	}

	public void setCompressed(String compressed) {
		this.compressed = compressed;
	}

	public String getContentFormat() {
		return contentFormat;
	}

	public void setContentFormat(String contentFormat) {
		this.contentFormat = contentFormat;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFilename() {
		return _returnEmptyStringForNulls(filename);
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getUploadUsername() {
		return _returnEmptyStringForNulls(uploadUsername);
	}

	public void setUploadUsername(String uploadUsername) {
		this.uploadUsername = uploadUsername;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}
	
	private String _returnEmptyStringForNulls(String input) {
		if (input == null || "null".equalsIgnoreCase(input) || (input != null && input.length() < 1)) {
			return "";
		}
		return input;
	}
}
