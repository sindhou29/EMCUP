package com.emc.settlement.common;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.emc.settlement.model.backend.exceptions.MsslCorrectedFileException;
import com.emc.settlement.model.backend.exceptions.MsslException;
import com.emc.settlement.model.backend.pojo.fileupload.DateRange;
import com.emc.settlement.model.backend.pojo.fileupload.MSSLCorrectedHeader;
import com.emc.settlement.model.backend.pojo.fileupload.MSSLCorrectedHeaderDetail;
import com.opencsv.CSVReader;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MSSLCorrectedHeaderImpl {	
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	MSSLCorrectedHeaderDetailImpl headerDetailImpl;
	
	protected static final Logger logger = Logger.getLogger(MSSLCorrectedHeaderImpl.class);
	
	public boolean parse(MSSLCorrectedHeader cmHeader, String inContent, String execStep) throws MsslException, IOException, MsslCorrectedFileException
	{
		logger.log(Priority.INFO,"[EMC] MSSLCorrectedHeader -- parse = [" + inContent + "]");

		String content = inContent + "\n";
		StringReader stringReader = new StringReader(content);
		CSVReader csvReader = new CSVReader(stringReader);
		String[] line = csvReader.readNext();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		boolean startTagFound = false;
		boolean periondTagFound = false;
		boolean affectedSAMNNTagFound = false;
		boolean endTagFound = false;
		
		while (line != null) {
		    // check header start "ADJUSTMENT SUMMARY START" tag
		    if (cmHeader.lineNumber == 0) {
		        if (String.valueOf(line[0]).indexOf("ADJUSTMENT SUMMARY START") >= 0) {
		            startTagFound = true;
		        }
		        else {
		            csvReader.close();

		            throw new MsslException("CMF_DATA_VALIDATION",  4201, cmHeader.lineNumber + 1, "Corrected Metering File Not Start with a Start Tag", 
		                                execStep);
		        }
		    }

		    // check "Adjusted Period" tag
		    if (cmHeader.lineNumber == 1) {
		        if (String.valueOf(line[0]).indexOf("Adjusted Period") >= 0) {
		            periondTagFound = true;
		        }
		        else {
		            csvReader.close();

		            throw new MsslException("CMF_DATA_VALIDATION", 4202, cmHeader.lineNumber + 1, "Corrected Metering File Format is not valid", 
		                                execStep);
		        }
		    }

		    // get periods
		    if (cmHeader.lineNumber == 2 && startTagFound && periondTagFound) {
		        int periodsNum = line.length;

		        if (periodsNum < 1) {
		            csvReader.close();

		            throw new MsslException("CMF_DATA_VALIDATION", 4203, cmHeader.lineNumber, 
		                                "Settlement Date should not be empty.", 
		                                execStep);
		        }
		        else {
		            for (int index = 0; index <= periodsNum - 1; index++) {
		                String period = String.valueOf(line[index]).trim();
		                try {
		                    Date tDate = sdf.parse(period);
		                    period = sdf.format(tDate);
		                }
		                catch (ParseException e) {
		                    csvReader.close();

		                    throw new MsslException("CMF_DATA_VALIDATION", 4204, 
		                    		cmHeader.lineNumber + 1, "Settlement Date In 'Adjust Period' must be valid.", 
		                                        execStep);
		                }

		                if (cmHeader.periods.indexOf(period) != - 1) {
		                    csvReader.close();

		                    throw new MsslException("CMF_DATA_VALIDATION", 4205, 
		                    		cmHeader.lineNumber + 1, "Duplicated Settlement Date In 'Adjust Period' Found.", 
		                                        execStep);
		                }

		                cmHeader.periods.add(period);
		            }
		        }
		    }

		    // check "Affected SA/MNN" tag 		
		    if (cmHeader.lineNumber == 3) {
		        if (String.valueOf(line[0]).indexOf("Affected SA/MNN") >= 0) {
		            affectedSAMNNTagFound = true;
		        }
		        else {
		            csvReader.close();

		            throw new MsslException("CMF_DATA_VALIDATION", 4206, cmHeader.lineNumber + 1, 
		                                "Corrected Metering File Format is not valid", 
		                                execStep);
		        }
		    }

		    // get details
		    if (cmHeader.lineNumber > 3 && startTagFound && periondTagFound && affectedSAMNNTagFound && ! endTagFound && String.valueOf(line[0]).indexOf( "ADJUSTMENT SUMMARY END") == - 1) {
		        int lineLength = line.length;

		        if (lineLength < 1) {
		            csvReader.close();

		            throw new MsslCorrectedFileException("CMF_DATA_VALIDATION", 4203, 
		            		cmHeader.lineNumber, "Affected SA/MNN should not be empty.");
		        }
		        else {
		            MSSLCorrectedHeaderDetail headerDetail = new MSSLCorrectedHeaderDetail();
		            headerDetail.name = String.valueOf(line[0]);
		            headerDetail.periods = new ArrayList<String>();
					headerDetail.ranges = new ArrayList<DateRange>();
					headerDetail.datesFromCorrectedData = new ArrayList<String>();

		            for (int index = 1; index <= lineLength - 1; index++) {
		                String dPeriod = String.valueOf(line[index]).trim();
		                try {
		                    Date tDate = sdf.parse(dPeriod);
		                    dPeriod = sdf.format(tDate);
		                }
		                catch (ParseException e) {
		                    csvReader.close();

		                    throw new MsslException("CMF_DATA_VALIDATION", 4204, 
		                    		cmHeader.lineNumber + 1, "Settlement Date must be valid.", 
		                                        execStep);
		                }

		                if (headerDetail.periods.indexOf(dPeriod) != - 1) {
		                    csvReader.close();

		                    throw new MsslException("CMF_DATA_VALIDATION", 4205, 
		                    		cmHeader.lineNumber + 1, "Duplicated Settlement Date found.", 
		                                        execStep);
		                }

		                // check if the dPeriod is in periods
		                if (cmHeader.periods.indexOf(dPeriod) == - 1) {
		                    csvReader.close();

		                    throw new MsslException("CMF_DATA_VALIDATION", 4205, 
		                    		cmHeader.lineNumber + 1, "Settlement Date for " + headerDetail.name + " is not found in 'Adjust Period'.", 
		                                        execStep);
		                }

		                headerDetail.periods.add(dPeriod);
		            }

		            headerDetailImpl.generateRange(headerDetail);
		            cmHeader.details.add(headerDetail);
		            
		        }
		    }

		    if (String.valueOf(line[0]).indexOf("ADJUSTMENT SUMMARY END") >= 0) {
		        List<String> detailsPeriods = new ArrayList<String>();

		        for (int idx = 0; idx <= cmHeader.details.size() - 1; idx++) {
		            for (String item : cmHeader.details.get(idx).periods) {
		                if (detailsPeriods.indexOf(item) == - 1) {
		                    detailsPeriods.add(item);
		                }
		            }
		        }

		        if (detailsPeriods.size() != cmHeader.periods.size()) {
		            throw new MsslException("CMF_DATA_VALIDATION", 4205, cmHeader.lineNumber + 1, 
		                                "Period in 'Adjusted Period' is not completed find in 'Affected SA/MNN' ", 
		                                execStep);
		        }

		        if (startTagFound && periondTagFound && affectedSAMNNTagFound) {
		            endTagFound = true;
		            cmHeader.isValid = true;
		            csvReader.close();

		            return true;
		        }
		        else {
		            csvReader.close();

		            return false;
		        }
		    }

		    line = csvReader.readNext();
		    cmHeader.lineNumber = cmHeader.lineNumber + 1;
		}
		// end while line <> null 
		csvReader.close();

		return false;
		
	}
	
	public void calculateDateRanges(MSSLCorrectedHeader cmHeader, Date nextDay)
	{

		cmHeader.rDateRange = utilityFunctions.getSettlementDateRange(nextDay, "R", false);
		cmHeader.fDateRange = utilityFunctions.getSettlementDateRange(nextDay, "F", false);
		cmHeader.sDateRange = utilityFunctions.getSettlementDateRange(nextDay, "S", false);
		cmHeader.pDateRange = utilityFunctions.getSettlementDateRange(nextDay, "P", false);

	    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
	    
	    logger.log(Priority.INFO,"[EMC] Settlement Date Range(P): " + sdf.format(cmHeader.pDateRange.startDate) + 
	    " - " + sdf.format(cmHeader.pDateRange.endDate));

	    logger.log(Priority.INFO,"[EMC] Settlement Date Range(F): " + sdf.format(cmHeader.fDateRange.startDate) + 
	    " - " + sdf.format(cmHeader.fDateRange.endDate));

	    logger.log(Priority.INFO,"[EMC] Settlement Date Range(R): " + sdf.format(cmHeader.rDateRange.startDate) + 
	    " - " + sdf.format(cmHeader.rDateRange.endDate));

	    logger.log(Priority.INFO,"[EMC] Settlement Date Range(S): " + sdf.format(cmHeader.sDateRange.startDate) + 
	    " - " + sdf.format(cmHeader.sDateRange.endDate));
	}

	public void validate(MSSLCorrectedHeader cmHeader, String settlementDate, String accountName, int lineNum, String accountType, String execStep, String standingVersion) throws Exception
	{
	    // logMessage "[EMC] Starting Method MSSLCorrectedHeader.validate() ..."
	    // check if the account is in header
	    boolean accountFound = false;
	    boolean dateFound = false;
	    String sacName = "";

	    if (accountType.equals("N")) {
	        sacName = utilityFunctions.getSACByNodeName( accountName,  standingVersion);
	    }

	    for (int idx = 0; idx <= cmHeader.details.size() - 1; idx++) {
	    	//RM658  - CR631 raised for the corrected metering file issue
	        //if ((cmHeader.details.get(idx).name == accountName) || (cmHeader.details.get(idx).name == sacName)) {
	    	if ((cmHeader.details.get(idx).name.equalsIgnoreCase(accountName))) {
	            accountFound = true;

	            // check date and cache Date if found
	            if (cmHeader.details.get(idx).periods.indexOf(settlementDate) != - 1) {
	                dateFound = true;

	                if (cmHeader.details.get(idx).datesFromCorrectedData.indexOf(settlementDate) == - 1) {
	                	cmHeader.details.get(idx).datesFromCorrectedData.add(settlementDate);
	                	cmHeader.details.get(idx).type = accountType;
	                	cmHeader.validSettlementDates.add(settlementDate);
	                }

	                break;
	            }
	        }
	    }
	}
	
	public void addDetail(MSSLCorrectedHeader cmHeader, String settlementDate, String accountName, int lineNum, String accountType)
	{
		logger.log(Priority.INFO,"[EMC] MSSLCorrectedHeader -- add detail according to=[" + settlementDate + " " + accountName + "]");

		// check if the account is in header
		boolean accountFound = false;
		boolean dateFound = false;
		int idxFound = 0;

		
		if (cmHeader.details != null) {
		    for (int idx = 0; idx <= cmHeader.details.size() - 1; idx++) {
		        if (cmHeader.details.get(idx).name.equalsIgnoreCase(accountName)) {
		            accountFound = true;
		            idxFound = idx;

		            // check date and cache Date if found
		            
		            if (cmHeader.details.get(idx).periods.indexOf(settlementDate) != - 1) {
		            //if (details[idx].periods.indexOf(settlementDate) != - 1) {
		                dateFound = true;

		                // throw new MsslException("CMF_DATA_VALIDATION", 4205, lineNum+lineNumber+1, "Duplicate " + accountName + " for settlement Date " + settlementDate)
		            }
		        }
		    }
		}

		
		
		if (accountFound && ! dateFound) {
			cmHeader.details.get(idxFound).periods.add(settlementDate);

		    if (cmHeader.details.get(idxFound).datesFromCorrectedData.indexOf(settlementDate) < 0) {
		    	cmHeader.details.get(idxFound).datesFromCorrectedData.add(settlementDate);
		    	
		    	cmHeader.details.get(idxFound).type = accountType;
		    	cmHeader.validSettlementDates.add(settlementDate);
		    }
		    headerDetailImpl.generateRange(cmHeader.details.get(idxFound));
		}

		if (! accountFound) {
		    // add account to header
		    if (cmHeader.periods.indexOf(settlementDate) < 0) {
		    	cmHeader.periods.add(settlementDate);
		    }

		    MSSLCorrectedHeaderDetail headerDetail = new MSSLCorrectedHeaderDetail();
		    headerDetail.name = accountName;
			headerDetail.periods = new ArrayList<String>();
			headerDetail.datesFromCorrectedData = new ArrayList<String>();
			headerDetail.ranges = new ArrayList<>();

		    if (headerDetail.periods.indexOf(settlementDate) < 0) {
		    	headerDetail.periods.add(settlementDate);

		        if (headerDetail.datesFromCorrectedData.indexOf( settlementDate) < 0) {
		        	headerDetail.datesFromCorrectedData.add( settlementDate);
		        	headerDetail.type = accountType;
		            cmHeader.validSettlementDates.add(settlementDate);
		        }
		    }

		    headerDetailImpl.generateRange(headerDetail);

		    cmHeader.details.add(headerDetail);

		    logger.log(Priority.INFO,"[EMC] Added MSSLCorrectedHeaderDetail, ExternalID: " + accountName + 
		    ", Account Type: " + accountType);
		}
		
	}

	public String getIncompletedCorrectedData(MSSLCorrectedHeader cmHeader)
	{
		for (int idx = 0; idx <= cmHeader.details.size() - 1; idx++) {
		    if (! headerDetailImpl.hasCompletedCorrectedData(cmHeader.details.get(idx))) {
		        return cmHeader.details.get(idx).name + "," + cmHeader.details.get(idx).datesFromCorrectedData;
		    }
		}

		return null;
	}

	public List<String> writeToDB(MSSLCorrectedHeader cmHeader, String ebtEveId, String uploadUser, Date uploadTime, String msgStep) throws MsslException, Exception
	{
		List<String> idList = new ArrayList<String>();
		HashMap<String, String> runTypeMap = this.getRunTypes(cmHeader, uploadTime);
		String addrCC = utilityFunctions.getSysParamVarChar("MP EMAIL NOTIFICATION CC ADDR");
		String subject = utilityFunctions.getSysParamVarChar("MP EMAIL NOTIFICATION SUBJECT");
		String eBody = utilityFunctions.getSysParamVarChar("MP EMAIL NOTIFICATION BODY");
		eBody = eBody.replace("\\n", "\n");

		for (int idx = 0; idx <= cmHeader.details.size() - 1; idx++) {
			String id = headerDetailImpl.writeToDB(cmHeader.details.get(idx), ebtEveId, uploadUser, uploadTime,
					runTypeMap, addrCC, subject, eBody, msgStep);

			if (id != null) {
				idList.add(id);
			}
		}
		return idList;
	}
	
	public HashMap<String,String> getRunTypes(MSSLCorrectedHeader cmHeader, Date uploadTime)
	{
		// settlementDate = "1-Sep-2009"
		HashMap<String, String> map = new HashMap<String, String>();

		// uploadTime = uploadTime.datePart + '1d';
		uploadTime = utilityFunctions.addDays(uploadTime, 1);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		String runType = "";

		for (String date : cmHeader.validSettlementDates) {
			Date tDate = null;
			try {
				tDate = dateFormat.parse(date);
			} catch (ParseException e) {
				logger.error("Exception " + e.getMessage());
			}

			if (tDate.compareTo(cmHeader.sDateRange.endDate) <= 0) {
				runType = "S";
			} else if (tDate.compareTo(cmHeader.rDateRange.startDate) < 0) {
				runType = "S0";

				// between S and R
			} else if (tDate.compareTo(cmHeader.rDateRange.endDate) <= 0) {
				runType = "R";
			} else if (tDate.compareTo(cmHeader.fDateRange.startDate) < 0) {
				runType = "R0";

				// between R and F
			} else if (tDate.compareTo(cmHeader.fDateRange.endDate) <= 0) {
				runType = "F";
			} else if (tDate.compareTo(cmHeader.pDateRange.startDate) < 0) {
				runType = "F0";

				// between F and P
			} else if (tDate.compareTo(cmHeader.pDateRange.endDate) <= 0) {
				runType = "P";
			} else {
				runType = "P0";

				// after P
			}

			map.put(date, runType);

			// logger.info("Preparing Map - [CMF] Run Type: " + runType + " for Settlement
			// Date: " + date);
		}
		return map;

	}
}
