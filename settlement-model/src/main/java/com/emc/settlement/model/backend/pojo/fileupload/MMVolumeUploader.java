package com.emc.settlement.model.backend.pojo.fileupload;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.emc.settlement.model.backend.pojo.CsvFileValidator;


public class MMVolumeUploader  implements Serializable{

	protected static final Logger logger = Logger.getLogger(MMVolumeUploader.class);
	
	public MMVolumeUploader() {
		super();
	}
	
	public CsvFileValidator getCsvFileValidator() {
		return csvFileValidator;
	}
	public void setCsvFileValidator(CsvFileValidator csvFileValidator) {
		this.csvFileValidator = csvFileValidator;
	}
	public String getEveId() {
		return eveId;
	}
	public void setEveId(String eveId) {
		this.eveId = eveId;
	}
	public String getLogPrefix() {
		return logPrefix;
	}
	public void setLogPrefix(String logPrefix) {
		this.logPrefix = logPrefix;
	}
	public String getMsgStep() {
		return msgStep;
	}
	public void setMsgStep(String msgStep) {
		this.msgStep = msgStep;
	}
	public int getRowNum() {
		return rowNum;
	}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	public static Logger getLogger() {
		return logger;
	}

	public CsvFileValidator csvFileValidator;
	public String eveId;
	public String logPrefix;
	public String msgStep;
	public int rowNum;

}
