/**
 * 
 */
package com.emc.settlement.model.backend.pojo.fileupload;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.emc.settlement.model.backend.pojo.CsvFileValidator;

/**
 * @author DWTN1561
 *
 */
public class ForwardSalesContractUploader  implements Serializable{

	/**
	 * 
	 */
	public ForwardSalesContractUploader() {
		// TODO Auto-generated constructor stub
	}
	
	public String msgStep;
	public String logPrefix;
	public int rowNum;
	public CsvFileValidator csvFileValidator;
	public String eveId;
	public Map<Object, Object> sacSoldId = new HashMap<Object, Object>();
	public List<String> fscDbInsert = new ArrayList<String>();
	
	protected static final Logger logger = Logger.getLogger(ForwardSalesContractUploader.class);
	
	public String getMsgStep() {
		return msgStep;
	}
	public void setMsgStep(String msgStep) {
		this.msgStep = msgStep;
	}
	public String getLogPrefix() {
		return logPrefix;
	}
	public void setLogPrefix(String logPrefix) {
		this.logPrefix = logPrefix;
	}
	public int getRowNum() {
		return rowNum;
	}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
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
	public Map<Object, Object> getSacSoldId() {
		return sacSoldId;
	}
	public void setSacSoldId(Map<Object, Object> sacSoldId) {
		this.sacSoldId = sacSoldId;
	}
	public List<String> getFscDbInsert() {
		return fscDbInsert;
	}
	public void setFscDbInsert(List<String> fscDbInsert) {
		this.fscDbInsert = fscDbInsert;
	}
	public static Logger getLogger() {
		return logger;
	}
	


}
