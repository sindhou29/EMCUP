package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CsvFileValidator  implements Serializable{

	public String comments;
	public String ebt_transaction;
	public String event_type;
	public String filename;
	public String message;
	public String pkg_version;
	public String settlement_date;
	public String uploaded_by;
	public String valid_yn;
	public Map<Integer,List<String>> csvFileData;
	public int validationNumberError;
	public int csv_column_count;
	
	
	public int getCsv_column_count() {
		return csv_column_count;
	}
	public void setCsv_column_count(int csv_column_count) {
		this.csv_column_count = csv_column_count;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getEbt_transaction() {
		return ebt_transaction;
	}
	public void setEbt_transaction(String ebt_transaction) {
		this.ebt_transaction = ebt_transaction;
	}
	public String getEvent_type() {
		return event_type;
	}
	public void setEvent_type(String event_type) {
		this.event_type = event_type;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPkg_version() {
		return pkg_version;
	}
	public void setPkg_version(String pkg_version) {
		this.pkg_version = pkg_version;
	}
	public String getSettlement_date() {
		return settlement_date;
	}
	public void setSettlement_date(String settlement_date) {
		this.settlement_date = settlement_date;
	}
	public String getUploaded_by() {
		return uploaded_by;
	}
	public void setUploaded_by(String uploaded_by) {
		this.uploaded_by = uploaded_by;
	}
	public String getValid_yn() {
		return valid_yn;
	}
	public void setValid_yn(String valid_yn) {
		this.valid_yn = valid_yn;
	}
	public Map<Integer, List<String>> getCsvFileData() {
		return csvFileData;
	}
	public void setCsvFileData(Map<Integer, List<String>> csvFileData) {
		this.csvFileData = csvFileData;
	}
	public int getValidationNumberError() {
		return validationNumberError;
	}
	public void setValidationNumberError(int validationNumberError) {
		this.validationNumberError = validationNumberError;
	}


	
	
}
