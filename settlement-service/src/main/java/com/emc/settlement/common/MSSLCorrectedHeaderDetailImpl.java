package com.emc.settlement.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.settlement.model.backend.exceptions.MsslException;
import com.emc.settlement.model.backend.pojo.fileupload.DateRange;
import com.emc.settlement.model.backend.pojo.fileupload.MSSLCorrectedHeaderDetail;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class MSSLCorrectedHeaderDetailImpl {

	SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MMM-yyyy");
	private static final Logger logger = Logger.getLogger(MSSLCorrectedHeaderDetailImpl.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private UtilityFunctions utilityFunctions;
	
	public void generateRange(MSSLCorrectedHeaderDetail headerDetail)
	{
		List<Date> dates = new ArrayList<Date>();
		
		int len = headerDetail.periods.size();

		if (headerDetail.periods != null && len > 0) {
			{    
		        int idx = 0;

		        while (idx <= len - 1) {
		            Date tDate = null;
					try {
						tDate = dateFormat.parse(headerDetail.periods.get(idx));
					} catch (ParseException e) {
						logger.error("Exception "+e.getMessage());
					}
					dates.add(0, tDate);
		            //dates.insert( 0, tDate);

		            idx = idx + 1;
		        }
		    }
			Collections.sort(dates);
		    int lastRangeIdx = 0;
		    {
		        int idx = 0;

				DateRange range = null;
				while (idx <= len - 1) {
					if (idx > 0) {
						if (!dates.get(idx).equals(utilityFunctions.addDays(dates.get(idx - 1), 1))) {
								range = new DateRange();
								range.from = dateFormat.format(dates.get(lastRangeIdx));
								range.to = dateFormat.format(dates.get(idx - 1));
		                    // insert ranges using int = 0, value = range
		                    //ranges.extend(from : range.from, @to : range.@to);
								headerDetail.ranges.add(range);
		                    lastRangeIdx = idx;
		                }
		            }
					
/*			        if(range!=null) {
						headerDetail.ranges.add(range);
						logger.info("Adding - From : "+range.from+" To : "+range.to);
					}*/

		            idx = idx + 1;
		        }


		    }

		    DateRange lastRange = new DateRange();
		    lastRange.from = dateFormat.format(dates.get(lastRangeIdx));
		    lastRange.to = dateFormat.format(dates.get(len - 1));
		    // insert ranges using int = 0, value = lastRange
		    headerDetail.ranges.add(lastRange);
		}
		
		
		
}
	public boolean hasCompletedCorrectedData(MSSLCorrectedHeaderDetail headerDetails)
	{
		if (headerDetails.datesFromCorrectedData.size() != headerDetails.periods.size()) {
		    return false;
		}

		return true;
	}
		
	public String writeToDB(MSSLCorrectedHeaderDetail headerDetails, String ebtEveId, String uploadUser, Date uploadTime, HashMap runTypeMap, String addrCC, String subject, String eBody, String msgStep) throws MsslException, Exception
	{
		//logMessage("[EMC] EMC.FileUpload.MSSLCorrectedHeaderDetail.writeToDB ");
		
	    String sqlCommand;
	    // get id

	    String retrievedId = null;
	    String retrievedVersion = null;
	    String runType="";
	    String detailId = null;
	    
	    try{
	    // Commented by RO on 5 Sep 2011 for resolving CTR/CMF issue (BPM 2.5.5) - INCIDENT ITSM-24721 - CMF File Processing Error
	    
	    // Commented by RO on 5 Sep 2011 for resolving CTR/CMF issue (BPM 2.5.5) - INCIDENT ITSM-24721 - CMF File Processing Error    
	    //sqlCommand = "SELECT MAX(ID), MAX(TO_NUMBER(version)) FROM NEM.NEM_SETTLEMENT_ACCOUNTS " + 
	                 //"WHERE EXTERNAL_ID = ?";                 
	    sqlCommand = "SELECT ID, version FROM NEM.NEM_SETTLEMENT_ACCOUNTS " + 
	                 "WHERE version = (SELECT TO_CHAR(MAX(TO_NUMBER(version))) FROM NEM.NEM_SETTLEMENT_ACCOUNTS WHERE EXTERNAL_ID =?) " +
	                 "AND   EXTERNAL_ID =?";
	    
		Object[] params = new Object[2];
		params[0] =  headerDetails.name;
		params[1] =  headerDetails.name;
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, params);
		for (Map row : list) {
			retrievedId = (String) row.get("ID");
			retrievedVersion = (String) row.get("version");
		}

	    if (retrievedId == null) {
	        // Commented by RO on 5 Sep 2011 for resolving CTR/CMF issue (BPM 2.5.5) - INCIDENT ITSM-24721 - CMF File Processing Error
	        //sqlCommand = "SELECT MAX(SAC_ID), MAX(TO_NUMBER(sac_version)) FROM NEM.NEM_NODES " + 
	                     //"WHERE NAME = ?";
	        sqlCommand = "SELECT SAC_ID, sac_version FROM NEM.NEM_NODES " + 
	                     "WHERE sac_version=(SELECT TO_CHAR(MAX(TO_NUMBER(sac_version))) FROM NEM.NEM_NODES WHERE NAME =?) " +
	                     "AND   name = ?";
//			Object[] params1 = new Object[1];
//			params1[0] =  headerDetails.name;
//			params1[1] =  headerDetails.name;
//			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, params1);
	        List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, params);
			for (Map row : list1) {
				retrievedId = (String) row.get("SAC_ID");
				retrievedVersion = (String) row.get("sac_version");
			}
	    }

	    if (retrievedId == null) {
	        throw new MsslException( "DATA_VALIDATION",  4113,  0, "SAC or Node Name should be valid. (" + headerDetails.name + ")", 
	                             "MSSLCorrectedHeaderDetail.writeToDB()");
	    }

	    detailId = utilityFunctions.getEveId();

	    // write NEM_EBT_EVENTS_DTL

	    //params[2] = "A4DF640CC9A9E844E0303302010AF1F9"; This is for testing failure of CTR
	    
	    sqlCommand = "INSERT into NEM.NEM_EBT_EVENTS_DTL(ID, EBE_ID, SAC_ID, SAC_VERSION) " + 
	                 "VALUES (?,?,?,?)";

	    logger.info("NEM.NEM_EBT_EVENTS_DTL - ID : "+detailId+" EBE_ID : "+ebtEveId+"  SAC_ID : "+retrievedId+"  SAC_VERSION : "+retrievedVersion);
		Object[] params1 = new Object[4];
		params1[0] =  detailId;
		params1[1] =  ebtEveId;
		params1[2] =  retrievedId;
		params1[3] =  retrievedVersion;
		jdbcTemplate.update(sqlCommand, params1);
		

	    String body = "";

	    for (int idx = 0; idx <= headerDetails.ranges.size() - 1; idx++) {
	        body = body + "\n\n";
	        Date dateFrom = utilityFunctions.stringToDate(headerDetails.ranges.get(idx).from, "dd-MMM-yyyy");
	        Date dateTo = utilityFunctions.stringToDate( headerDetails.ranges.get(idx).to, "dd-MMM-yyyy");
	        String lastRunType = "";
	        Date startDate = dateFrom;
	        Date lastDate = dateFrom;

//	        logger.info("dateTo : "+dateTo+"  dateFrom : "+dateFrom);
	        while (dateTo.compareTo(dateFrom) >= 0) {
	        	Date currDate = dateFrom;
	            String currDateStr = dateFormat.format(currDate);
	            
//	            logger.info("currDateStr : "+currDateStr+"  RunType : "+runTypeMap.get(currDateStr));
	            
	            runType = ((String) runTypeMap.get(currDateStr));

					if (runType != null) {
						// if (Time.compare(a : uploadTime, b : uploadTime.datePart + '17h') > 0) {
						Date addedtime = utilityFunctions.get5PMTime();

						if (uploadTime.compareTo(addedtime) > 0) {
							if (runType.equals("P")) {
								runType = "F";
							} else if (runType.equals("F")) {
								runType = "R";
							} else if (runType.equals("R")) {
								runType = "S";
							} else if (runType.equals("S")) {
								runType = "S";

								// throw Exception ("Received MSSL File for tomorrows S-Run after 5:00pm")
							}
						}

						if (runType.length() > 1) {
							// P0 -> P, F0 -> F, R0 -> R, S0 -> S
							runType = runType.substring(0, 1);
						}

						if (!runType.equals(lastRunType) && !lastRunType.equals("")) {
							if (startDate.compareTo(lastDate) == 0) {
								body = body + "Included in " + lastRunType + " Run for Settlement Dates\t"
										+ new SimpleDateFormat("dd MMM yyyy").format(startDate);
							} else {
								body = body + "Included in " + lastRunType + " Run for Settlement Dates\t"
										+ new SimpleDateFormat("dd MMM yyyy").format(startDate) + " - "
										+ new SimpleDateFormat("dd MMM yyyy").format(lastDate);
							}

							body = body + "\n\n";
							startDate = currDate;
						}

						lastRunType = runType;
						lastDate = currDate;
						
			            Calendar calDateFrom = Calendar.getInstance();
			            calDateFrom.setTime(dateFrom);
			            calDateFrom.add(Calendar.DATE, 1);
			            dateFrom = calDateFrom.getTime();
					}

	        }

	        if (startDate.compareTo(lastDate) == 0) {
	            body = body + "Included in " + lastRunType + " Run for Settlement Dates\t" + new SimpleDateFormat("dd MMM yyyy").format(startDate);
	        }
	        else {
	            body = body + "Included in " + lastRunType + " Run for Settlement Dates\t" + new SimpleDateFormat("dd MMM yyyy").format(startDate) + 
	                   " - " + new SimpleDateFormat("dd MMM yyyy").format(lastDate);
	        }

	        body = body + "\n\n";
	        body = body + "\n\n\nPlease note the qualifying factors for MSSL''s submission of corrected meter files:\n" + 
	               "a) TD+9 business day, 5pm  (for Final run)\n" + 
	               "b) TD+47 business day, 5pm (for first re-run)\n" + 
	               "c) TD+252 business day, 5pm (for second re-run)\n\n\n" + 
	               "Note : This is a computer-generated email, for enquiries, please contact Settlement at Tel : 6779 3000 or email to settlement@emcsg.com";
	    }

	    // write NEM_MP_EMAIL_NOTIFICATIONS
	    sqlCommand = "INSERT into NEM.NEM_MP_EMAIL_NOTIFICATIONS (ID, NOTICE_TYPE, SAC_ID, " + 
	                 "SAC_VERSION, ADDR_CC, SUBJECT, BODY) VALUES (?,?,?,?,?,?,'" + eBody + body + "')";

		Object[] params2 = new Object[6];
		params2[0] =  detailId;
		params2[1] =  "CORRECTED_METERING_FILE";
		params2[2] =  retrievedId;
		params2[3] =  retrievedVersion;
		params2[4] =  addrCC;
		params2[5] =  subject;
		jdbcTemplate.update(sqlCommand, params2);
	    // write NEM_MP_EMAIL_STATUS
	    String notificationStausID = null;
	    sqlCommand = "SELECT ID FROM NEM.NEM_NOTIFICATION_STATUS WHERE STATUS = 'DRAFT'";

		List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
		for (Map row : list1) {
			notificationStausID = (String) row.get("ID");
		}
		

	    // Insert into NEM_MP_EMAIL_STATUS table
	    sqlCommand = "INSERT INTO NEM.NEM_MP_EMAIL_STATUS(ID, MP_EMAIL_NOTIFICATIONS_ID, " + 
	                 "NOTIFICATION_STATUS_ID, CREATED_TIME, CREATED_BY) VALUES (?,?,?,SYSDATE,?)";
		
		Object[] params3 = new Object[4];
		params3[0] =  utilityFunctions.getEveId();
		params3[1] =  detailId;
		params3[2] =  notificationStausID;
		params3[3] =  uploadUser;
		jdbcTemplate.update(sqlCommand, params3);
		
	    String sqlRangeDtl = "INSERT into NEM.NEM_EBT_EVENT_DATE_RANGE_DTL " + 
	    "(ID, EBT_EVENTS_DTL_ID, START_DATE, END_DATE, RUN_TYPE) VALUES ( ?,?, " + 
	    "TO_DATE(?, 'dd-MON-yyyy'),TO_DATE(?, 'dd-MON-yyyy'),? )";
	    String sqlEmailDtl = "INSERT into NEM.NEM_EBT_EMAIL_DATE_RANGE_DTL " + 
	    "(ID, MP_EMAIL_NOTIFICATION_ID, START_DATE, END_DATE, RUN_TYPE) " + 
	    " VALUES ( ?,?, TO_DATE(?, 'dd-MON-yyyy'),TO_DATE(?, 'dd-MON-yyyy'),? )";

	    Date addedtime = utilityFunctions.get5PMTime();
        
	    for (int idx = 0; idx <= headerDetails.ranges.size() - 1; idx++) {
	        //runType = ((String) runTypeMap.get( headerDetails.ranges.get(idx).from));
	        
	        Date dateFrom = utilityFunctions.stringToDate(headerDetails.ranges.get(idx).from, "dd-MMM-yyyy");
	        String currDateStr = dateFormat.format(dateFrom);
	        runType = ((String) runTypeMap.get(currDateStr));
				if (runType != null) {

					// if (Time.compare(a : uploadTime, b : uploadTime.datePart + '17h') > 0) {
					if (uploadTime.compareTo(addedtime) > 0) {
						if (runType.equals("P")) {
							runType = "F";
						} else if (runType.equals("F")) {
							runType = "R";
						} else if (runType.equals("R")) {
							runType = "S";
						} else if (runType.equals("S")) {
							throw new Exception("Received MSSL File for tomorrows S-Run after 5:00pm");
						}
					}

					if (runType.length() > 1) {
						// P0 -> P, F0 -> F, R0 -> R, S0 -> S
						runType = runType.substring(0, 1);
					}

					Object[] params4 = new Object[5];
					params4[0] = utilityFunctions.getEveId();
					params4[1] = detailId;
					params4[2] = headerDetails.ranges.get(idx).from;
					params4[3] = headerDetails.ranges.get(idx).to;
					params4[4] = runType;
					logger.info("writeToDB NEM_EBT_EVENT_DATE_RANGE_DTL - Eve Id: "+params4[0]+" EBT_EVENTS_DTL_ID : "+detailId+" START_DATE : "+params4[2]+" END_DATE : "+params4[3]+" RUN_TYPE : "+runType);
					jdbcTemplate.update(sqlRangeDtl, params4);

					if (!runType.equals("P")) {

						Object[] params5 = new Object[5];
						params5[0] = utilityFunctions.getEveId();
						params5[1] = detailId;
						params5[2] = headerDetails.ranges.get(idx).from;
						params5[3] = headerDetails.ranges.get(idx).to;
						params5[4] = runType;
						jdbcTemplate.update(sqlEmailDtl, params5);

						// logMessage "[CMF] Inserted Date Range Detail, Date from: " + ranges[idx].from
						// + ", Date to: " + ranges[idx].to + ", Run Type: " + runType
					} else {
						// For P-Run, no need to send email to MP
						detailId = null;
					}
				}
	    }
	    
	    }catch (Exception e) {
	    	logger.error("Exception "+e.getMessage());
	    	e.printStackTrace();
			throw new MsslException("DATA INSERT", 0, 0, e.getMessage(), msgStep);
	    }

	    return detailId;
	}
	
}
