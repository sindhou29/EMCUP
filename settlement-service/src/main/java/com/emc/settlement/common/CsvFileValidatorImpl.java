/**
 * 
 */
package com.emc.settlement.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.stereotype.Component;

import com.emc.settlement.model.backend.pojo.CsvFileValidator;
import com.opencsv.CSVReader;

/**
 * @author DWTN1561
 *
 */
@Component
public class CsvFileValidatorImpl {
	
	protected static final Logger logger = Logger.getLogger(CsvFileValidatorImpl.class);
	
	public boolean isFilenameEmpty(String fname)
	{
		// display "CsvFileValidator.isFilenameEmpty() -- begin"
		boolean isEmpty = false;

		if (fname == null) {
		    isEmpty = true;

		}
		else if (fname.length() <= 0) {
		    isEmpty = true;

		}
		else {
		    isEmpty = false;
		}

		return isEmpty;
		
	}
	
	public int readFileData(String fname, int num_columns, CSVReader csvReader, CsvFileValidator csvFileValidator)
	{
		int validationNumberError = 0;

		// 0 = success, else is exception
		int rowNum = 0;
		
		
		Map<Integer,List<String>> csvFileData = new HashMap<Integer,List<String>>(); 

		// row number of the row that generates an exception
		int column = 0;
		rowNum = 0;
		String[] line;
		try {
			line = csvReader.readNext();

			// count does not count nulls in any[];  length includes null in any[]
			while (line !=null && line.length > 0) {
			    if (line.length != num_columns) {
			        
			    	validationNumberError = 4;
			    	csvFileValidator.setMessage("Number of columns is not " + num_columns + " (line " + rowNum + ").");
	
			        // 29082009 change exit to return validationNumberError
			        // exit
			        return validationNumberError;
			    }
			    else {
			        column = 0;
	
			        List<String> colValues = new ArrayList<String>();
			        while (column < num_columns) {
			        	colValues.add(line[column]);
			            column = column + 1;
			        }
			        csvFileData.put(rowNum, colValues);
			        rowNum = rowNum + 1;
			        line = null;
	
			        line = csvReader.readNext();
			    }
			}
			csvFileValidator.csvFileData = csvFileData;
	
			if (column != num_columns) {
				csvFileValidator.setMessage("File is not in CSV format (line " + rowNum + ").");
				csvFileValidator.setValidationNumberError(3);
			}
	
			logger.log(Priority.INFO,"[EMC] CsvFileValidator.readFileData() -- Number of Records : " + rowNum);
	
		} catch (IOException e) {
			logger.error("Exception "+e.getMessage());
		}
		
		return validationNumberError;
		
	}

}
