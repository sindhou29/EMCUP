package com.emc.settlement.model.backend.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class MsExcelFile  implements Serializable{
	
	public String filename;
	public int numSheets;
	public byte[] fileContent;
	public List<String> worksheetName = new ArrayList<String>();
	public HSSFWorkbook workbook;
	public List<HSSFSheet> worksheet = new ArrayList<HSSFSheet>();
	
	public String getFilename() {
		return filename;
	}
	public HSSFWorkbook getWorkbook() {
		return workbook;
	}
	public void setWorkbook(HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	public int getNumSheets() {
		return numSheets;
	}
	public void setNumSheets(int numSheets) {
		this.numSheets = numSheets;
	}

	public byte[] getFileContent() {
		return fileContent;
	}
	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}
	public List<String> getWorksheetName() {
		return worksheetName;
	}
	public void setWorksheetName(List<String> worksheetName) {
		this.worksheetName = worksheetName;
	}
	public List<HSSFSheet> getWorksheet() {
		return worksheet;
	}
	public void setWorksheet(List<HSSFSheet> worksheet) {
		this.worksheet = worksheet;
	}

}
