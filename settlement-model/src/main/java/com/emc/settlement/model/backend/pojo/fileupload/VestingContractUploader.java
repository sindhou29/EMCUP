/**
 * 
 */
package com.emc.settlement.model.backend.pojo.fileupload;


import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.emc.settlement.model.backend.pojo.CsvFileValidator;
/**
 * @author DWTN1561
 *
 */
public class VestingContractUploader  implements Serializable{

	protected static final Logger logger = Logger.getLogger(VestingContractUploader.class);
	/**
	 * 
	 */
	public VestingContractUploader() {
		// TODO Auto-generated constructor stub
	}
	
	public VestingContractUploader(String logPrefixArg, String eveIdArg, CsvFileValidator csvFileValidatorArg) {
		this.logPrefix = logPrefixArg;
		this.eveId = eveIdArg;
		this.csvFileValidator = csvFileValidatorArg;
	}
	
	public CsvFileValidator csvFileValidator;
	public String eveId;
	public String logPrefix;
	public String msgStep;
	public int rowNum;
	public Map<Object, Object> sacSoldId = new HashMap<Object, Object>();
	public List<String> vcDbInsert = new ArrayList<String>();
	
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

	public Map getSacSoldId() {
		return sacSoldId;
	}

	public void setSacSoldId(Map sacSoldId) {
		this.sacSoldId = sacSoldId;
	}

	public List<String> getVcDbInsert() {
		return vcDbInsert;
	}

	public void setVcDbInsert(List<String> vcDbInsert) {
		this.vcDbInsert = vcDbInsert;
	}

}
