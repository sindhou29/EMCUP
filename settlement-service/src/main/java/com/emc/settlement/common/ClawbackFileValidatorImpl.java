/**
 * 
 */
package com.emc.settlement.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.model.backend.exceptions.ClawbackQuantitiesException;
import com.emc.settlement.model.backend.pojo.fileupload.ClawbackQuantities;
import com.emc.settlement.model.backend.pojo.fileupload.SACInfo;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


/**
 * @author DWTN1561
 *
 */
@Component
public class ClawbackFileValidatorImpl {

	protected static final Logger logger = Logger.getLogger(ClawbackFileValidatorImpl.class);
	/**
	 * 
	 */
	public ClawbackFileValidatorImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	PavPackageImpl pavPackageImpl;
    @Autowired
	private JdbcTemplate jdbcTemplate;
    
	public String getNodeId(String standingVer, String nodeName)
	{
		try{

		    // Get Node ID by Node Name
		    String sqlNodeID = "select ID from NEM.NEM_NODES " + 
		    "where VERSION=? and NAME=?";
		    String nodeId = null;

			Object[] params = new Object[2];
			params[0] =  standingVer;
			params[1] =  nodeName;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlNodeID, params);
			for (Map row : list) {
				nodeId = (String)row.get("ID");
				break;
			}
			
		    return nodeId;
		}
		catch (Exception e) {
		    logger.log(Priority.INFO,"[EMC] Exception in ClawbackFileValidator.getNodeId() : " + e.getMessage());

		    return null;
		}
		
	}
	
	public SACInfo getSACId(String standingVer, String extId)
	{
		try {
		    // Get SAC ID by EXTERNAL_ID
		    String sqlSacID = "select ID, EMBEDDED_GEN, RETAILER_ID, SAC_TYPE from NEM.NEM_SETTLEMENT_ACCOUNTS " + 
		    "where VERSION=? and EXTERNAL_ID=?";

		    // sacId as String = null
		    SACInfo sac = new SACInfo();
		    sac.externalId = extId;
		    sac.sacId = null;

		    Object[] params = new Object[2];
			params[0] = standingVer;
			params[1] = extId;
			List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlSacID, params);

			for(Map<String, Object> recordMap : resultList) {
				sac.sacId  = (String) recordMap.get("ID");
				sac.embeddedGen  = (String) recordMap.get("EMBEDDED_GEN");
				sac.retailerId  = (String) recordMap.get("RETAILER_ID");
				sac.sacType  = (String) recordMap.get("SAC_TYPE");
				break;
			}

		    return sac;
		}
		catch (Exception e) {
		    logger.log(Priority.INFO,"[EMC] Exception in ClawbackFileValidator.getSACId() : " + e.getMessage());

		    return null;
		}
		
	}
	
	public  List<ClawbackQuantities> loadSEWApproveClawbackData(String ebtEventId, String content, Boolean fromSEW, Boolean clwqEmpty, String eventId) throws Exception
	{
	    String msgStep = "ClawbackFileValidator" + "." + "loadSEWApproveClawbackData()";
	    utilityFunctions.logJAMMessage(eventId, "I", msgStep, 
	                                   "Loading SEW Approved Non Providing Facilities File Data - Starting.", 
	                                   "");

	    content = this.standardiseContentLineSeparator(content);
	    StringBuffer contentStringBuffer = new StringBuffer( content);
	    int indexOfDate = contentStringBuffer.indexOf("DATE");
	    int indexOfDateBR = contentStringBuffer.indexOf("\n", indexOfDate);
	    int indexOfHead = contentStringBuffer.indexOf("HEAD");
	    int indexOfHeadBR = contentStringBuffer.indexOf("\n", indexOfHead);
	    int indexOfEOF = contentStringBuffer.indexOf("EOF");
	    /* parse trading date */
	    Date mySettlementDate = null;
	    String dateString = content.substring(indexOfDate, indexOfDateBR);
	    List<String> myDateTokenList = utilityFunctions.stringToTokenList(dateString);
	    mySettlementDate = utilityFunctions.stringToDate(((String) myDateTokenList.get(1)), "yyyyMMdd");
	    /* parse data */
	    List<ClawbackQuantities> myAcceptedClawbackQuantitiesList = new ArrayList<ClawbackQuantities>();
	    String dataContent="";
	    //UATSHARP-240 (Item-1) - Handling IndexOutOfBoundsException
	    if(indexOfHeadBR+1 != indexOfEOF) {
	    	dataContent = content.substring(indexOfHeadBR + 1, indexOfEOF - 1);
	    }
	    int lineNumber = 1;
	    utilityFunctions.logJAMMessage(eventId, "I", msgStep, 
	                                   "Line by Line Processing of SEW Approved Non Providing Facilities File Data - Starting.", 
	                                   "");

	    InputStream dataContentInputStream = new ByteArrayInputStream(dataContent.getBytes());

	    // read it with BufferedReader
	    BufferedReader dataContentReader = new BufferedReader(new InputStreamReader(dataContentInputStream));
	    String dataContentLine;

	    while ((dataContentLine = dataContentReader.readLine()) != null) {
	        // get the current standing version and also validate trading date
	        String standingVersion = pavPackageImpl.getStandingVersion(mySettlementDate);
	        {
	            Integer[] dataLengthArray = { 4, 2, 11, 8, 8, 8, 3, 3, 3, 3 };
	            String[] dataContentArray = new String[dataLengthArray.length];
	            StringBuffer dataContentStrBuff = new StringBuffer(dataContentLine);

	            for (int index = 0; index < dataLengthArray.length; index++) {
	                // read the characters based on data length
	                dataContentArray[index] = dataContentStrBuff.substring(0, 
	                                                                       dataLengthArray[index]);

	                // remove the read data plus a space
	                if (index < dataLengthArray.length - 1) {
	                    String dataDelimiter = dataContentStrBuff.substring(dataLengthArray[index], 
	                                                                        dataLengthArray[index] + 1);
	                    dataContentStrBuff = dataContentStrBuff.delete(0, dataLengthArray[index] + 1);
	                }
	            }

	            ClawbackQuantities myClawbackQuantities = new ClawbackQuantities();
	            myClawbackQuantities.settlementDate = mySettlementDate;
	            myClawbackQuantities.recordType = dataContentArray[0];
	            String b1 = dataContentArray[3].trim();
	            String b2 = dataContentArray[4].trim();
	            String b3 = dataContentArray[5].trim();
	            boolean isREG = dataContentArray[6].trim().isEmpty() == false;
	            boolean isPRIRES = dataContentArray[7].trim().isEmpty() == false;
	            boolean isSECRES = dataContentArray[8].trim().isEmpty() == false;
	            boolean isCONRES = dataContentArray[9].trim().isEmpty() == false;
	            myClawbackQuantities.period = Integer.parseInt(dataContentArray[1].trim());
	            String periodTimeInterval = dataContentArray[2].trim();
	            myClawbackQuantities.ndeId = this.getNodeId( standingVersion, b1 + " : " + b2 + " : " + b3);
	            myClawbackQuantities.ndeVersion = standingVersion;
	            int myClawbackQuantityCount = 0;
	            List<String> ancillaryTypes = new ArrayList<String>();
	            ancillaryTypes.clear();

	            if (isREG) {
	                ancillaryTypes.add(myClawbackQuantityCount, "REG");

	                myClawbackQuantityCount = myClawbackQuantityCount + 1;
	            }

	            if (isPRIRES) {
	                ancillaryTypes.add(myClawbackQuantityCount, "PRIRES");

	                myClawbackQuantityCount = myClawbackQuantityCount + 1;
	            }

	            if (isSECRES) {
	                ancillaryTypes.add(myClawbackQuantityCount, "SECRES");

	                myClawbackQuantityCount = myClawbackQuantityCount + 1;
	            }

	            if (isCONRES) {
	                ancillaryTypes.add(myClawbackQuantityCount, "CONRES");

	                myClawbackQuantityCount = myClawbackQuantityCount + 1;
	            }

	            {
	                int index = 0;

	                while (index < myClawbackQuantityCount) {
	                    ClawbackQuantities myClawbackQuantityData = new ClawbackQuantities();
	                    myClawbackQuantityData.settlementDate = myClawbackQuantities.settlementDate;
	                    myClawbackQuantityData.recordType = myClawbackQuantities.recordType;
	                    myClawbackQuantityData.period = myClawbackQuantities.period;
	                    myClawbackQuantityData.ndeId = myClawbackQuantities.ndeId;
	                    myClawbackQuantityData.ndeVersion = myClawbackQuantities.ndeVersion;
	                    myClawbackQuantityData.ancillaryType = ancillaryTypes.get(index);
	                    myAcceptedClawbackQuantitiesList.add(myClawbackQuantityData);

	                    index = index + 1;
	                }
	            }
	        }

	        lineNumber = lineNumber + 1;
	    }

	    utilityFunctions.logJAMMessage(eventId, "I", msgStep, 
	                                   "Line by Line Processing of SEW Approved Non Providing Facilities File Data - Completed Successfully.", 
	                                   "");

	    if (myAcceptedClawbackQuantitiesList.isEmpty()) {
	        clwqEmpty = true;

	        // if the data list is empty then treat the file as EMPTY clawback file
	        ClawbackQuantities myClawbackQuantityData = new ClawbackQuantities();
	        myClawbackQuantityData.settlementDate = mySettlementDate;
	        myClawbackQuantityData.recordType = "EMPTY";
	        myClawbackQuantityData.period = null;
	        myClawbackQuantityData.ndeId = null;
	        myClawbackQuantityData.ndeVersion = null;
	        myClawbackQuantityData.ancillaryType = null;
	        myAcceptedClawbackQuantitiesList.add(myClawbackQuantityData);
	    }

	    utilityFunctions.logJAMMessage(eventId, "I", msgStep, 
	                                   "Loading SEW Approved Non Providing Facilities File Data - Completed Successfully.", 
	                                   "");

	    return myAcceptedClawbackQuantitiesList;
	}

	
	public String standardiseContentLineSeparator(String content)
	{
		
		// BEGIN: Convert all possible end of line into \n, this will cater for all type of end of line
		// this will also prevent carriage return to be count when using String.length()
		StringBuffer contentStringBuffer = new StringBuffer();
		try {
		// convert String into InputStream
		InputStream contentInputStream = new ByteArrayInputStream( content.getBytes());

		// read it with BufferedReader
		BufferedReader contentReader = new BufferedReader(new InputStreamReader(contentInputStream));
		String line;

		// read the first line
		
			line = contentReader.readLine();

			while (line != null) {

				contentStringBuffer.append( line);
	
			    // read the next line
			    line = contentReader.readLine();
	
			    if (line != null) {
			        contentStringBuffer.append("\n");
			    }
			}

			contentReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception "+e.getMessage());
		}
		return contentStringBuffer.toString();

		// END: Convert all possible end of line into \n, this will cater for all type of end of line
		
	}
	
	public List<ClawbackQuantities> validateClawbackData(String ebtEventId, String eventId, String content, Boolean fromSEW, Boolean clwqEmpty) throws ClawbackQuantitiesException, SQLException, IOException, Exception
	{
	    String msgStep = "ClawbackFileValidator" + "." + "validateClawbackData";

	    if (content == null || content.length() == 0) {
	        throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4201, 
	                                          1, "File content is empty (EBE_ID=" + ebtEventId + ", Event ID = " + eventId + ")", 
	                                          msgStep);
	    }

	    content = this.standardiseContentLineSeparator(content);
	    StringBuffer contentStringBuffer = new StringBuffer(content);
	    int indexOfFileName = contentStringBuffer.indexOf("FNAM");
	    int indexOfFileNameBR = contentStringBuffer.indexOf("\n", indexOfFileName);
	    int indexOfDate = contentStringBuffer.indexOf("DATE");
	    int indexOfDateBR = contentStringBuffer.indexOf("\n", indexOfDate);
	    int indexOfHead = contentStringBuffer.indexOf("HEAD");
	    int indexOfHeadBR = contentStringBuffer.indexOf("\n", indexOfHead);
	    int indexOfEOF = contentStringBuffer.indexOf("EOF");

	    if (indexOfFileName < 0) {
	        throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4202, 
	                                          1, "FNAM is missing", 
	                                          msgStep);
	    }

	    if (indexOfDate < 0) {
	        throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4202, 
	                                          2, "DATE is missing", 
	                                          msgStep);
	    }

	    if (indexOfHead < 0) {
	        throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4202, 
	                                          3, "HEAD is missing", 
	                                          msgStep);
	    }

	    if (indexOfEOF < 0) {
	        throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4202, 
	                                          4, "EOF is missing", msgStep);
	    }

	    // File Name is provided here but will not be used in the validation yet
	    String fileNameString = content.substring(indexOfFileName, indexOfFileNameBR);
	    /* parse trading date */
	    Date mySettlementDate = null;
	    String dateString = content.substring(indexOfDate, indexOfDateBR);
	    List<String> myDateTokenList = utilityFunctions.stringToTokenList(dateString);

	    if (myDateTokenList.size() < 2) {
	        throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4202, 
	                                          2, "Trading Date is missing", 
	                                          msgStep);
	    }
	    else {
	        mySettlementDate = utilityFunctions.stringToDate(((String) myDateTokenList.get(1)), "yyyyMMdd");
	    }

	    /* parse header */
	    String headString = content.substring(indexOfHead, indexOfHeadBR);

	    if (headString.indexOf("PD") < 0 || headString.indexOf("HH:MM HH:MM") < 0 || headString.indexOf("B1") < 0 || headString.indexOf("B2") < 0 || headString.indexOf("B3") < 0 || headString.indexOf("REG") < 0 || headString.indexOf("PRI") < 0 || headString.indexOf("SEC") < 0 || headString.indexOf("CON") < 0) {
	        throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION", 4203, 
	                                          3, "Invalid header", msgStep);
	    }

	    utilityFunctions.logJAMMessage(eventId, "I", msgStep, 
	                                   "Validating Non Providing Facilities File Format : Non Providing Facilities File Format is Valid", 
	                                   "");

	    /* parse data */
	    List<ClawbackQuantities> myAcceptedClawbackQuantitiesList = new ArrayList<ClawbackQuantities>();
	    List<String> checkDuplicateKeyList = new ArrayList<String>();
	    String dataContent = ((indexOfEOF-indexOfHeadBR)>2 ) ? content.substring(indexOfHeadBR + 1, indexOfEOF - 1) : "";
	    int lineNumber = 1;
	    InputStream dataContentInputStream = new ByteArrayInputStream(dataContent.getBytes());

	    // read it with BufferedReader
	    BufferedReader dataContentReader = new BufferedReader(new InputStreamReader(dataContentInputStream));
	    String dataContentLine;

	    while ((dataContentLine = dataContentReader.readLine()) != null) {
	    	this.validateLine(ebtEventId, mySettlementDate,dataContentLine, lineNumber, 
	                                           fromSEW, checkDuplicateKeyList, 
	                                           myAcceptedClawbackQuantitiesList);
	    	
	        lineNumber = lineNumber + 1;
	    }

	    dataContentReader.close();

	    // END: Convert all possible end of line into \n, this will cater for all type of end of line
	    if (myAcceptedClawbackQuantitiesList.isEmpty()) {
	        clwqEmpty = true;

	        // if the data list is empty then treat the file as EMPTY clawback file
	        ClawbackQuantities myClawbackQuantityData = new ClawbackQuantities();
	        myClawbackQuantityData.settlementDate = mySettlementDate;
	        myClawbackQuantityData.recordType = "EMPTY";
	        myClawbackQuantityData.period = null;
	        myClawbackQuantityData.ndeId = null;
	        myClawbackQuantityData.ndeVersion = null;
	        myClawbackQuantityData.ancillaryType = null;
	        myAcceptedClawbackQuantitiesList.add(myClawbackQuantityData);
	    }

	    utilityFunctions.logJAMMessage(eventId, "I", msgStep, 
	                                   "Validating Non Providing Facilities Data : Non Providing Facilities Data are Valid", 
	                                   "");

	    return myAcceptedClawbackQuantitiesList;
	}

	public void validateFileSumbissionDeadline(String sewUploadEventsID, Date settlementDate, Boolean fromSEW, String sewOperationType) throws Exception
	{
		/*try {*/
		Date submissionTime = null;
		String sqlUploadTimestamp = null;

		if (! fromSEW) {
		    sqlUploadTimestamp = "select sysdate from dual";
		}
		else if (fromSEW) {
		    if (sewOperationType.equals("UPL")) {
		        sqlUploadTimestamp = "SELECT CREATED_DATE FROM sewfo.SEW_UPLOAD_EVENTS WHERE ID = ? ";
		    }
		    else if (sewOperationType.equals("APP")) {
		        sqlUploadTimestamp = "SELECT AUTHORISED_DATE FROM sewfo.SEW_UPLOAD_EVENTS WHERE ID = ? ";
		    }
		}

		boolean isPeriodTimeIntervalValid = false;

		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlUploadTimestamp, sewUploadEventsID);

		for(Map<String, Object> recordMap : resultList) {
			if (sewOperationType.equals("UPL")) {
				submissionTime = (Date) recordMap.get("CREATED_DATE");
				break;
			} else if (sewOperationType.equals("APP")) {
				submissionTime = (Date) recordMap.get("AUTHORISED_DATE");
				break;
			} else {
				submissionTime = (Date) recordMap.get("SYSDATE");
				break;
			}
		}


		// BEGIN: Check TD+9 5PM
		Date submissionDeadlineDate = settlementDate;
    	Calendar cal = Calendar.getInstance();

		// add 9 business day
		{
		    int index = 0;
		    while (index < 9) {
		    	cal.setTime(submissionDeadlineDate);
		    	cal.add(Calendar.DAY_OF_MONTH, 1);
		    	submissionDeadlineDate = cal.getTime();

		        while (! utilityFunctions.isBusinessDay(submissionDeadlineDate)) {
		        	cal = Calendar.getInstance();
		        	cal.setTime(submissionDeadlineDate);
			    	cal.add(Calendar.DAY_OF_MONTH, 1);
			    	submissionDeadlineDate = cal.getTime();
		        }

		        index = index + 1;
		    }
		}

		cal = Calendar.getInstance();
    	cal.setTime(submissionDeadlineDate);
    	cal.add(Calendar.HOUR_OF_DAY, 17);
    	submissionDeadlineDate = cal.getTime();

    	if(submissionTime == null) submissionTime = new Date();
    	logger.info("Check TD+9 5PM - submissionDeadlineDate : "+submissionDeadlineDate+" submissionTime : "+submissionTime);
    	
    	// >= and <=
		if (submissionTime.compareTo(submissionDeadlineDate) >= 0) {
		    throw new ClawbackQuantitiesException("CLAWBACK_UPLOAD_CUTOFF_TIME_VALIDATION", 
		                                       4202,  0,  "Non Providing Facilities File Upload/Authorization Date for Settlement Date " + 
		                                      new SimpleDateFormat("dd MMM yyyy").format(settlementDate) + 
		                                      " should not exceed Cut Off time (Trading Date + 9 Business Days at 05:00 PM) " + 
		                                      new SimpleDateFormat("dd MMM yyyy hh:mma").format(submissionDeadlineDate) + 
		                                      ". This File will be rejected.", "ClawbackFileValidator.validateFileSubmissionDeadline");
		}

		// END
		// BEGIN: Check TD+5 5PM
		submissionDeadlineDate = settlementDate;

		// add 5 business day
		{
		    int index = 0;

		    while (index < 5) {
				cal = Calendar.getInstance();
		    	cal.setTime(submissionDeadlineDate);
		    	cal.add(Calendar.DAY_OF_MONTH, 1);
		    	submissionDeadlineDate = cal.getTime();

		        while (! utilityFunctions.isBusinessDay(submissionDeadlineDate)) {
					cal = Calendar.getInstance();
			    	cal.setTime(submissionDeadlineDate);
			    	cal.add(Calendar.DAY_OF_MONTH, 1);
			    	submissionDeadlineDate = cal.getTime();
		        }

		        index = index + 1;
		    }
		}

		cal = Calendar.getInstance();
    	cal.setTime(submissionDeadlineDate);
    	cal.add(Calendar.HOUR_OF_DAY, 17);
    	submissionDeadlineDate = cal.getTime();

    	logger.info("Check TD+5 5PM - submissionDeadlineDate : "+submissionDeadlineDate+" submissionTime : "+submissionTime);
    	// >= and <=
		if (submissionTime.compareTo(submissionDeadlineDate) >= 0) {
		    // send alert
		    // Alert that File Has been Submitted after TD+5 5P.M. cut off date. 
		    utilityFunctions.updateSEWFileProcessingStatus("",  "File has been uploaded after Cut Off time (Trading Date + 5 Business Days at 05:00 PM). This File will still be processed for next Day P Run.", 
		                                                    sewUploadEventsID, 
		                                                    "");
		}

		/*}catch(Exception e)
		{
			logger.error("Exception "+e.getMessage());
		}*/
		// END
		
	}
	
	public void validateLine(String ebtEventId, Date settlementDate,String dataContentLine, int lineNumber, boolean fromSEW, List checkDuplicateKeyList, List<ClawbackQuantities> clawbackQuantityList) throws ClawbackQuantitiesException, SQLException, Exception
	{

	    String msgStep = "ClawbackFileValidator.validateLine()";
	    int actualLineNumber = lineNumber + 3;

	    // get the current standing version and also validate trading date
	    String standingVersion = pavPackageImpl.getStandingVersion(settlementDate);

	    // check whether the line is an empty string
	    if (dataContentLine.isEmpty()) {
	        throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE", 20, 
	                                          actualLineNumber, "Data Should not be an empty line", 
	                                          msgStep);
	    }


		// data line length must be a fixed length of 62 characters
		if (dataContentLine.length() != 62) {
			throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE",
											  20, actualLineNumber,
											  "Data line length must be 62 characters",
											  msgStep);
		}

		Integer[] dataLengthArray = { 4, 2, 11, 8, 8, 8, 3, 3, 3, 3 };
		String[] dataContentArray = new String[dataLengthArray.length];
		StringBuffer dataContentStrBuff = new StringBuffer(dataContentLine);

		for (int index = 0; index < dataLengthArray.length; index++) {
			// read the characters based on data length
			dataContentArray[index] = dataContentStrBuff.substring(0, dataLengthArray[index]);

			// remove the read data plus a space
			if (index < dataLengthArray.length - 1) {
				String dataDelimiter = dataContentStrBuff.substring(dataLengthArray[index],
																	 dataLengthArray[index] + 1);

				if (!dataDelimiter.equals(" ")) {
					throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE",
													  20, actualLineNumber,
													  "Data delimiter must be a space",
													  msgStep);
				}

				dataContentStrBuff = dataContentStrBuff.delete(0, dataLengthArray[index] + 1);
			}
		}

		ClawbackQuantities myClawbackQuantities = new ClawbackQuantities();
		myClawbackQuantities.settlementDate = settlementDate;
		myClawbackQuantities.recordType = dataContentArray[0];
		String b1 = dataContentArray[3].trim();
		String b2 = dataContentArray[4].trim();
		String b3 = dataContentArray[5].trim();
		// validation: data must contain b1, b2, b3
		if (b1.isEmpty() && b2.isEmpty() && b3.isEmpty()) {
			throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE",
											  20, actualLineNumber,
											  "Data must contain B1, B2, B3. ",
											  msgStep);
		}

		boolean isREG = dataContentArray[6].trim().isEmpty() == false;
		boolean isPRIRES = dataContentArray[7].trim().isEmpty() == false;
		boolean isSECRES = dataContentArray[8].trim().isEmpty() == false;
		boolean isCONRES = dataContentArray[9].trim().isEmpty() == false;

		// the key word REG must be case sensitive
		if (isREG && !dataContentArray[6].equals("REG")) {
			throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE",
											  121, actualLineNumber,
											  dataContentArray[6] + " is invalid flag for Regulation Facility. (Expected: REG)",
											  msgStep);
		}

		// the key word PRI must be case sensitive
		if (isPRIRES && !dataContentArray[7].equals("PRI")) {
			throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE",
											  121, actualLineNumber,
											  dataContentArray[7] + " is invalid flag for Primary Reserve Facility. (Expected: PRI)",
											  msgStep);
		}

		// the key word SEC must be case sensitive
		if (isSECRES && !dataContentArray[8].equals("SEC")) {
			throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE",
											  121, actualLineNumber,
											  dataContentArray[8] + " is invalid flag for Secondary Reserve Facility. (Expected: SEC)",
											  msgStep);
		}

		// the key word CON must be case sensitive
		if (isCONRES && !dataContentArray[9].equals("CON")) {
			throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE",
											  121, actualLineNumber,
											  dataContentArray[9] + " is invalid flag for Contingency Reserve Facility. (Expected: CON)",
											  msgStep);
		}

		if ((isREG || isPRIRES || isSECRES || isCONRES) == false) {
			throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE",
											  121, actualLineNumber,
											  "Data must state REG, PRI, SEC or CON",
											  msgStep);
		}

		// check ancillaryType here
		// check the record type, this record type can be added more
		if (!myClawbackQuantities.recordType.equals("UPRR")) {
			throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE",
											  40, actualLineNumber,
											  "Invalid record type \"" + myClawbackQuantities.recordType + "\"",
											  msgStep);
		}

		try {
			myClawbackQuantities.period = Integer.parseInt(dataContentArray[1].trim());
		}
		catch (NumberFormatException e) {
			throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE",
											  40, actualLineNumber,
											  "Period must be a number",
											  msgStep);
		}

		if (myClawbackQuantities.period < 1 || myClawbackQuantities.period > 48) {
			throw new ClawbackQuantitiesException("CLAWBACK_VALIDATE_LINE",
											  40, actualLineNumber,
											  "Period value must be between 1 and 48",
											  msgStep);
		}

		String periodTimeInterval = dataContentArray[2].trim();
		String sqlCheckPeriodTimeInterval = " SELECT 1 FROM nem.def_periods " +
		" WHERE period_number = ? " +
		" AND ? BETWEEN effective_from_date " +
		" AND NVL (effective_to_date, to_date('3000-01-31','yyyy-mm-dd')) " +
		" AND ( TO_CHAR (period_begin_time, 'HH24:MI') || '-' || " +
		" TO_CHAR (period_end_time + 1 / 86400, 'HH24:MI')) = ? ";

		boolean isPeriodTimeIntervalValid = false;

		Object[] params = new Object[3];
		params[0] = myClawbackQuantities.period;
		params[1] = utilityFunctions.convertUDateToSDate(myClawbackQuantities.settlementDate);
		params[2] = periodTimeInterval;
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCheckPeriodTimeInterval, params);

		if(!resultList.isEmpty()) {
			isPeriodTimeIntervalValid = true;
		}

		if (isPeriodTimeIntervalValid == false) {
			throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION",
											  4203, actualLineNumber,
											  "Period time interval (" + periodTimeInterval + ") is not valid for Trading Date " +
											  new SimpleDateFormat("dd MMM yyyy").format(myClawbackQuantities.settlementDate) +
											  " period " + myClawbackQuantities.period,
											  msgStep);
		}

		myClawbackQuantities.ndeId = this.getNodeId(standingVersion,  b1 + " : " + b2 + " : " + b3);
		myClawbackQuantities.ndeVersion = standingVersion;

		// Start: REG data verification
		if (isREG) {
			String sqlCheckRegulationFacility = " SELECT count(fct.nde_id) CNT" +
			" FROM NEM.NEM_FACILITIES fct, NEM.NEM_ANCILLARY_GROUPS acg " +
			" WHERE fct.nde_id = ? AND fct.VERSION = ? AND acg.VERSION = fct.VERSION " +
			" AND fct.facility_type = 'UNT' AND acg.ancillary_type = 'RGL' ";

			params = new Object[2];
			params[0] = myClawbackQuantities.ndeId;
			params[1] = standingVersion;
			resultList = jdbcTemplate.queryForList(sqlCheckRegulationFacility, params);
			for(Map<String, Object> recordMap : resultList) {
				if (((BigDecimal) recordMap.get("CNT")).intValue() == 0) {
					throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION",
							4203, actualLineNumber,
							"Node name should be valid (" + b1 + ":" + b2 + ":" + b3 + ") for Regulation Facility",
							msgStep);
				}

				break;

			}
		}

		// End: REG data verification
		// Start: PRIRES, SECRES, CONRES data verification
		if (isPRIRES || isSECRES || isCONRES) {
			String sqlCheckReserveFacility = " SELECT count(fct.nde_id) CNT" +
			" FROM NEM.NEM_FACILITIES fct, NEM.NEM_ANCILLARY_GROUPS acg " +
			" WHERE fct.nde_id = ? AND fct.VERSION = ? AND fct.VERSION = acg.VERSION " +
			" AND (fct.facility_type = 'UNT' OR fct.facility_type = 'DPL') " +
			" AND acg.ancillary_type = 'RSV' ";

			params = new Object[2];
			params[0] = myClawbackQuantities.ndeId;
			params[1] = standingVersion;
			resultList = jdbcTemplate.queryForList(sqlCheckReserveFacility, params);
			for(Map<String, Object> recordMap : resultList) {
				// if the facility is not a Reserve Facility
				if (((BigDecimal) recordMap.get("CNT")).intValue() == 0) {
					throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION",
							4203, actualLineNumber,
							"Node name should be valid (" + b1 + ":" + b2 + ":" + b3 + ") for Reserve Facility",
							msgStep);
				}

				break;
			}
		}

		String keyToCheckDuplicate = myClawbackQuantities.recordType + ":" + myClawbackQuantities.period + ":" + b1 + ":" + b2 + ":" + b3;
		keyToCheckDuplicate = keyToCheckDuplicate.toUpperCase();

		// start 2.7.09 add REG, PRI, SEC, CON into duplicate checking
		List<String> keyToCheckDuplicateList = new ArrayList<String>();

		if (isREG) {
			keyToCheckDuplicateList.add(keyToCheckDuplicate + ":" + "REG");
		}

		if (isPRIRES) {
			keyToCheckDuplicateList.add(keyToCheckDuplicate + ":" + "PRI");
		}

		if (isSECRES) {
			keyToCheckDuplicateList.add(keyToCheckDuplicate + ":" + "SEC");
		}

		if (isCONRES) {
			keyToCheckDuplicateList.add(keyToCheckDuplicate + ":" + "CON");
		}

		for (String keyItem : keyToCheckDuplicateList) {
			if (checkDuplicateKeyList.contains(keyItem)) {
				throw new ClawbackQuantitiesException("CLAWBACK_DATA_VALIDATION",
												  4203, actualLineNumber,
												  "Duplicate Non Providing Facilities Data.",
												  msgStep);
			}
			else {
				checkDuplicateKeyList.add(keyItem);
			}
		}

		// end 2.7.09
		int myClawbackQuantityCount = 0;
		List<String> ancillaryTypes = new ArrayList<String>();
		ancillaryTypes.clear();

		if (isREG) {
			ancillaryTypes.add(myClawbackQuantityCount, "REG");

			myClawbackQuantityCount = myClawbackQuantityCount + 1;
		}

		if (isPRIRES) {
			ancillaryTypes.add(myClawbackQuantityCount, "PRIRES");

			myClawbackQuantityCount = myClawbackQuantityCount + 1;
		}

		if (isSECRES) {
			ancillaryTypes.add(myClawbackQuantityCount, "SECRES");

			myClawbackQuantityCount = myClawbackQuantityCount + 1;
		}

		if (isCONRES) {
			ancillaryTypes.add(myClawbackQuantityCount, "CONRES");

			myClawbackQuantityCount = myClawbackQuantityCount + 1;
		}

		{
			int index = 0;

			while (index < myClawbackQuantityCount) {
				ClawbackQuantities myClawbackQuantityData = new ClawbackQuantities();
				myClawbackQuantityData.settlementDate = myClawbackQuantities.settlementDate;
				myClawbackQuantityData.recordType = myClawbackQuantities.recordType;
				myClawbackQuantityData.period = myClawbackQuantities.period;
				myClawbackQuantityData.ndeId = myClawbackQuantities.ndeId;
				myClawbackQuantityData.ndeVersion = myClawbackQuantities.ndeVersion;
				myClawbackQuantityData.ancillaryType = ancillaryTypes.get(index);
				clawbackQuantityList.add(myClawbackQuantityData);

				index = index + 1;
			}
		}
	}
}
