package com.emc.settlement.backend.runrelated;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emc.settlement.model.backend.exceptions.USAPException;
import com.emc.settlement.common.AlertNotificationImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.USAPCommon;

@Component
public class USAPCommonImpl {

	
	protected static final Logger logger = Logger.getLogger(USAPCommonImpl.class);
	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private AlertNotificationImpl alertNotificationImpl;
	
	public void populateUsapFile(USAPCommon usapCommonObj, String baseDir, String ftpSvrname, String ftpUsername, String ftpPassword, String file_to_mssl_via_aq, String msslDestinationURL, String usapJmsType) throws USAPException
	{
		/* ******************
		   Extract data from NEM_SETTLEMENT_USAP_RESULTS table to generate USAP interface file 

		   Input Arguments: 
		   @param eveId as String  -- jam events id 
		   
		   Main Logic:	
		   1.  Retrieve all authorised final settlement runs from NEM_SETTLEMENT_RUNS table with usap_date as null
		   2.  Use settRunID to retrieve USAP report data from NEM_SETTLEMENT_USAP_RESULTS table
		   3.  Output USAP report data into text file
		   
		* ******************* */
		logger.log(Priority.INFO,"[EMC] USAPCommon.populateUsapFile() ...");

		try{
		    String periodLine;
		    String heucLine;
		    String meucLine;
		    String usepLine;
		    String afpLine;
		    String vcrpLine;
		    String fsrpLine;
		    // DRCAP CHANGE START
		    String heurLine="";
		    String hlcuLine="";
		    boolean isdrEffective = false;
		    Date drEffectiveDate = utilityFunctions.getSysParamTime( "DR_EFFECTIVE_DATE");
		    
		    
		     // DR Phase 2 FR 2.4.2.10.1
		        if (usapCommonObj.settlementDate.compareTo(drEffectiveDate) < 0) {
		            isdrEffective = false;
		        }
		        else {
		            isdrEffective = true;
		        }
		    
		    // DRCAP CHANGE END
		    
		    

		    // 7.0.13
		    boolean isfscEffective = false;

		    if (utilityFunctions.isAfterFSCEffectiveStartDate(usapCommonObj.settlementDate) && utilityFunctions.isBeforeFSCEffectiveEndDate(usapCommonObj.settlementDate)) {
		        isfscEffective = true;
		    }
		    else {
		        isfscEffective = false;
		    }

		    // 7.0.13    
		    String emcadmnLine;

		    // 7.1.101
		    String psoadmnLine;

		    // 7.1.101
		    String wmepLine;

		    // 7.1.101
		    // 7.1.01
		    boolean WMEProundedEMCfeesRateEff = utilityFunctions.isAfterWMEPunroundedEMCfeesEffectiveDate(usapCommonObj.settlementDate);
		    boolean DummyDREff = utilityFunctions.isDummyDREffectiveDate(usapCommonObj.settlementDate); //BypassDRCAPPh2forSP Added

		    // Added for FSC Implementation
		    StringBuffer sbuf= new StringBuffer();

		    // to store USAP interface file content while it is being generated
		    // Get USAP Sequence
		    String sqlCommand = "SELECT NEM.USAP_SEQ.NEXTVAL From DUAL";

			usapCommonObj.usapTransactionId = jdbcTemplate.queryForObject(sqlCommand, new Object[] {}, Integer.class);
			
		    // put header contents into USAP file
		    if (file_to_mssl_via_aq.equalsIgnoreCase("N")) {
		        // BPM 2.6.07 (AQ)
		        sbuf = sbuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		    }

		    // BPM 2.6.07 (AQ)
		    sbuf = sbuf.append("<FileData>" + "\n");
		    sbuf = sbuf.append("<UserId>" + usapCommonObj.usapUserId + "</UserId>" + "\n");
		    sbuf = sbuf.append("<TransactionId>MC" + usapCommonObj.usapTransactionId + "</TransactionId>" + "\n");
		    sbuf = sbuf.append("<ContentFormat>" + usapCommonObj.usapContentFormat + "</ContentFormat>" + "\n");
		    sbuf = sbuf.append("<Compressed>" + usapCommonObj.usapCompression + "</Compressed>" + "\n");
		    sbuf = sbuf.append("<Data>" + "\n");
		    int rowNo = 0;
		    usepLine = "F,USEP," + usapCommonObj.settDate;
		    heucLine = "F,HEUC," + usapCommonObj.settDate;
		    meucLine = "F,MEUC," + usapCommonObj.settDate;
		    afpLine = "F,AFP," + usapCommonObj.settDate;
		    vcrpLine = "F,VCRP," + usapCommonObj.settDate;
		    fsrpLine = "F,FSRP," + usapCommonObj.settDate;
		    emcadmnLine = "F,EMCADMN," + usapCommonObj.settDate;
		    
		    //if(isdrEffective)  
		     if(isdrEffective || DummyDREff)  // Also included BypassDRCAPPh2forSP 
			{
		    // DRCAP CHANGE START
		 	 heurLine = "F,HEUR," + usapCommonObj.settDate;
		 	 hlcuLine = "F,HLCU," + usapCommonObj.settDate;
		    // DRCAP CHANGE END
			}

		    // 7.1.101
		    psoadmnLine = "F,PSOADMN," + usapCommonObj.settDate;

		    // 7.1.101
		    wmepLine = "F,WMEP," + usapCommonObj.settDate;

		    // 7.1.101
		    // For FSC Implemenatation
		    periodLine = ",GIP/GXP,DayValue";

		    // build SQL to fetch USAP result records from NEM_SETTLEMENT_USAP_RESULTS
		    /*  
		                            		This is the original PL/SQL sql retrieving using str_id and period conditions
		                            		for 48 periods. The SQL will be executed 48 times, each time fetching one or more rows  !!
		                            		Rewrote it to call only ONCE to fetch all 48 or more periods, sorted by period
		                            		
		                            			sqlUsapResultsCommand as String = "SELECT TO_CHAR( NVL (sru.usep, 0)), TO_CHAR( NVL (sru.heuc, 0)),"
		                            					+ " TO_CHAR( NVL (sru.meuc, 0)), TO_CHAR( NVL (sru.afp, 0)), TO_CHAR( NVL (sru.vcrp, 0))"
		                            					+ " FROM nem_settlement_usap_results sru"
		                            					+ " WHERE sru.str_id = ? AND sru.period = ?"
		                            
		                            			// original PL/SQL procedure:
		                            			For periods for 1..48 
		                            				ExecuteSQL sqlUsapResultsCommand
		                            				Fetch data and append onto output string
		                            			End
		                            	
		                            	*/
		    // Query modified for FSC Implementation
		    
		    //--------------------------DRCAP CHANGES START------------------------------------------------ //
		    
			if(isdrEffective)
			{
		    
		    sqlCommand = "SELECT TO_CHAR( NVL (sru.usep, 0)) USEP, TO_CHAR( NVL (sru.heuc, 0)) HEUC,TO_CHAR( NVL (sru.heur, 0)) HEUR, " + 
		                 " TO_CHAR( NVL (sru.hlcu, 0)) HLCU,TO_CHAR( NVL (sru.meuc, 0)) MEUC, TO_CHAR( NVL (sru.afp, 0)) AFP, " + 
		                 " TO_CHAR( NVL (sru.vcrp, 0)) VCRP, TO_CHAR( NVL (sru.fsrp, 0)) FSRP, TO_CHAR( sru.period) PERIOD, " + 
		                 " TO_CHAR( NVL (sru.emcadmn, 0)) EMCADM, TO_CHAR( NVL (sru.psoadmn, 0)) PSOADM, TO_CHAR( sru.wmep) WMEP " + 
		                 " FROM nem_settlement_usap_results sru" + 
		                 " WHERE sru.str_id = '" + usapCommonObj.settRunId + "' ORDER BY sru.period";

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list) {
		        // periodLine = periodLine + "," + String(period)
		        periodLine = periodLine + "," + (String)row.get("PERIOD");

		        // Modified for FSC Implementation
		        usepLine = usepLine + "," + (String)row.get("USEP");
		        heucLine = heucLine + "," + (String)row.get("HEUC");
		        meucLine = meucLine + "," + (String)row.get("MEUC");
		        afpLine = afpLine + "," + (String)row.get("AFP");
		        vcrpLine = vcrpLine + "," + (String)row.get("VCRP");
		        fsrpLine = fsrpLine + "," + (String)row.get("FSRP");
		        emcadmnLine = emcadmnLine + "," + (String)row.get("EMCADM");

		        // 7.1.101
		        psoadmnLine = psoadmnLine + "," + (String)row.get("PSOADM");

		        // 7.1.101
		        wmepLine = wmepLine + "," + (String)row.get("WMEP");
		        
		        // DRCAP CHANGE START
		 	     heurLine = heurLine + "," + (String)row.get("HEUR");
		 	 	 hlcuLine = hlcuLine + "," + (String)row.get("HLCU");
		        // DRCAP CHANGE END

		        // 7.1.101
		        // Added for FSC Implementation
		        rowNo = rowNo + 1;
		    }


		    // add data strings 
		    sbuf = sbuf.append( periodLine + "\n");
		    sbuf = sbuf.append( usepLine + "\n");
		    sbuf = sbuf.append( heucLine + "\n");
		    //DRCAP CHANGE START
		    sbuf = sbuf.append( heurLine + "\n");
		    sbuf = sbuf.append( hlcuLine + "\n");
		    //DRCAP CHANGE END
		    sbuf = sbuf.append( meucLine + "\n");
		    sbuf = sbuf.append( afpLine + "\n");
		    sbuf = sbuf.append( vcrpLine + "\n");

		    // 7.0.13
		    if (isfscEffective == true) {
		        sbuf = sbuf.append( fsrpLine + "\n");
		    }

		    // 7.0.13       
		    // 7.1.01
		    //logger.log(Priority.INFO,"Rahul WMEProundedEMCfeesRateEff " + WMEProundedEMCfeesRateEff, severity : WARNING);

		    if (WMEProundedEMCfeesRateEff == true) {
		        sbuf = sbuf.append( emcadmnLine + "\n");

		        // 7.1.101
		        sbuf = sbuf.append( psoadmnLine + "\n");

		        // 7.1.101
		        sbuf = sbuf.append( wmepLine + "\n");

		        // 7.1.101
		    }

		    // 7.1.01
		    // Added for FSC Implementation
		    logger.log(Priority.INFO,"[EMC] Total rows returned from NEM_SETTLEMENT_RUNS = " + rowNo);

		    // EXCEPTION - Settlement Run record not found!
		    if (rowNo <= 0) {
		        logger.log(Priority.INFO,"[EMC] USAPCommon.populateUsapFile() -- No authorised final settlement runs for USAP file.");

		        throw new Exception( "No authorised final settlement runs for USAP file.");
		    }

		    // put in ending data tags
		    sbuf = sbuf.append( "</Data>" + "\n");
		    sbuf = sbuf.append( "<SendingPartyType>" + usapCommonObj.usapSendingPartyType + "</SendingPartyType>" + "\n");
		    sbuf = sbuf.append( "</FileData>");

		    logger.log(Priority.INFO,"[EMC] USAPCommon.populateUsapFile() -- fileContents=" + sbuf.toString());


		  }else
		  {
		  	
		  	sqlCommand = " SELECT TO_CHAR( NVL (sru.usep, 0)) USEP, TO_CHAR( NVL (sru.heuc, 0)) HEUC, " + 
		                 " TO_CHAR( NVL (sru.meuc, 0)) MEUC, TO_CHAR( NVL (sru.afp, 0)) AFP, " + 
		                 " TO_CHAR( NVL (sru.vcrp, 0)) VCRP, TO_CHAR( NVL (sru.fsrp, 0)) FSRP, TO_CHAR( sru.period) PERIOD, " + 
		                 " TO_CHAR( NVL (sru.emcadmn, 0)) EMCADM, TO_CHAR( NVL (sru.psoadmn, 0)) PSOADM, TO_CHAR( sru.wmep) WMEP " + 
		                 " FROM nem_settlement_usap_results sru" + 
		                 " WHERE sru.str_id = '" + usapCommonObj.settRunId + "' ORDER BY sru.period";

		  	List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list) {
		        // periodLine = periodLine + "," + String(period)
		        periodLine = periodLine + "," + (String)row.get("PERIOD");

		        // Modified for FSC Implementation
		        usepLine = usepLine + "," +(String)row.get("USEP");
		        heucLine = heucLine + "," +(String)row.get("HEUC");
			
			    //BypassDRCAPPh2forSP Added
			    if (DummyDREff) {
		               heurLine = heurLine + "," + (String)row.get("HEUC");         //BypassDRCAPPh2forSP Added
		               hlcuLine = hlcuLine + "," + "0";            //BypassDRCAPPh2forSP Added 
			    } 
		    	
		        meucLine = meucLine + "," + (String)row.get("MEUC");
		        afpLine = afpLine + "," + (String)row.get("AFP");
		        vcrpLine = vcrpLine + "," + (String)row.get("VCRP");
		        fsrpLine = fsrpLine + "," + (String)row.get("FSRP");
		        emcadmnLine = emcadmnLine + "," + (String)row.get("EMCADM");

		        // 7.1.101
		        psoadmnLine = psoadmnLine + "," + (String)row.get("PSOADM");

		        // 7.1.101
		        wmepLine = wmepLine + "," + (String)row.get("WMEP");
		        
		        // 7.1.101
		        // Added for FSC Implementation
		        rowNo = rowNo + 1;
		    }


		    // add data strings 
		    sbuf = sbuf.append( periodLine + "\n");
		    sbuf = sbuf.append( usepLine + "\n");
		    sbuf = sbuf.append( heucLine + "\n");
		    
			    //BypassDRCAPPh2forSP Added
			    if (DummyDREff) {
			       sbuf = sbuf.append( heurLine + "\n");        //BypassDRCAPPh2forSP Added
			       sbuf = sbuf.append( hlcuLine + "\n");  //BypassDRCAPPh2forSP Added
			    } 
			        
		    sbuf = sbuf.append( meucLine + "\n");
		    sbuf = sbuf.append( afpLine + "\n");
		    sbuf = sbuf.append( vcrpLine + "\n");

		    // 7.0.13
		    if (isfscEffective == true) {
		        sbuf = sbuf.append( fsrpLine + "\n");
		    }

		    // 7.0.13       
		    // 7.1.01
		    //logger.log(Priority.INFO,"Rahul WMEProundedEMCfeesRateEff " + WMEProundedEMCfeesRateEff, severity : WARNING);

		    if (WMEProundedEMCfeesRateEff == true) {
		        sbuf = sbuf.append( emcadmnLine + "\n");

		        // 7.1.101
		        sbuf = sbuf.append( psoadmnLine + "\n");

		        // 7.1.101
		        sbuf = sbuf.append( wmepLine + "\n");

		        // 7.1.101
		    }

		    // 7.1.01
		    // Added for FSC Implementation
		    logger.log(Priority.INFO,"[EMC] Total rows returned from NEM_SETTLEMENT_RUNS = " + rowNo);

		    // EXCEPTION - Settlement Run record not found!
		    if (rowNo <= 0) {
		        logger.log(Priority.INFO,"[EMC] USAPCommon.populateUsapFile() -- No authorised final settlement runs for USAP file.");

		        throw new Exception( "No authorised final settlement runs for USAP file.");
		    }

		    // put in ending data tags
		    sbuf = sbuf.append( "</Data>" + "\n");
		    sbuf = sbuf.append( "<SendingPartyType>" + usapCommonObj.usapSendingPartyType + "</SendingPartyType>" + "\n");
		    sbuf = sbuf.append( "</FileData>");

		    logger.log(Priority.INFO,"[EMC] USAPCommon.populateUsapFile() -- fileContents=" + sbuf.toString());
		  	
		  	
		  }

			//------------------------  DRCAP CHANGES END------------------------------------------ //

		    // Write to USAP File
			Integer obj = new Integer(usapCommonObj.usapTransactionId);
		    Double x = obj.doubleValue();
		    DecimalFormat dcformat = new DecimalFormat("00000");
		    String usapFn = dcformat.format(x) + "usap" + utilityFunctions.getyyyyMMddHHmmss(new Date()) + ".dat";
		    String usapFile = baseDir + usapFn;
		    BufferedWriter bw = null;
			FileWriter fw = null;
			try {
				fw = new FileWriter(usapFile);
				bw = new BufferedWriter(fw);
				bw.write(sbuf.toString());
			} catch (IOException e) {
				logger.log(Priority.INFO,"[EMC] Error creating or writing to USAP File: " + baseDir + usapFn + ".");
				logger.error("Exception "+e.getMessage());
			} finally {
				try {
					if (bw != null)
						bw.close();
					if (fw != null)
						fw.close();
				} catch (IOException ex) {
					logger.error("Exception "+ex.getMessage());
				}
			}

		    if (file_to_mssl_via_aq.equalsIgnoreCase("N")) {
		        // BPM 2.6.07 (AQ)
		        // Write to USAP Trigger file
		        String trgFn = usapFn.replace( ".dat", ".trg");
		        String trgFile = baseDir + trgFn;
			    bw = null;
				fw = null;
				try {
					fw = new FileWriter(trgFile);
					bw = new BufferedWriter(fw);
					bw.write("");
				} catch (IOException e) {
					logger.log(Priority.INFO,"[EMC] Error creating or writing to USAP Trigger File: " + baseDir + trgFn + ".");
					logger.error("Exception "+e.getMessage());
				} finally {
					try {
						if (bw != null)
							bw.close();
						if (fw != null)
							fw.close();
					} catch (IOException ex) {
						logger.error("Exception "+ex.getMessage());
					}
				}

		        logger.log(Priority.INFO,"[EMC] USAPCommon.populateUsapFile() -- USAP trigger file generated and USAP data saved into file: " + usapFn);

		        // BPM 2.6.07 (AQ)
		        String remoteUsapDir = utilityFunctions.getSysParamVarChar( "USAP_DIR");

		        // FTP USAP file and Trg file to Database Server
		        logger.log(Priority.INFO,"[EMC] FTP USAP file to " + ftpSvrname + " ...");
		        
		        
		        FTPClient ftpClient = new FTPClient();
		        try {
		        	ftpClient.connect(ftpSvrname);
		        	ftpClient.login(ftpUsername, ftpPassword);
		        	ftpClient.enterLocalPassiveMode();
		            // "F1formular"
		        }
		        catch (Exception e) {
		            logger.log(Priority.INFO,"[EMC] Unable to connect to FTP Server: " + ftpSvrname);

		            throw new Exception( "Unable to connect to FTP Server: " + ftpSvrname);
		        }

		        // FTP USAP file
		        logger.log(Priority.INFO,"[EMC] Storing USAP File to FTP Server ...");

		        try {
		        	File firstLocalFile = new File(baseDir + usapFn);
		        	InputStream inputStream = new FileInputStream(firstLocalFile);
		        	ftpClient.storeFile(  remoteUsapDir + "/" + usapFn, inputStream);
		            //ftpClient.storeFile( baseDir + usapFn,  remoteUsapDir + "/" + usapFn, "A");

		            logger.log(Priority.INFO,"[EMC] USAP file successfully stored to FTP Server. Remote File: " + usapFn);
		            inputStream.close();
		        }
		        catch (Exception e) {
		            logger.log(Priority.INFO,"[EMC] Unable to store USAP file: " + remoteUsapDir + "/" + usapFn + 
		            " to FTP Server: " + ftpSvrname);

		            throw new Exception( "Unable to store UASP file: " + remoteUsapDir + "/" + usapFn + 
		                            " to FTP Server: " + ftpSvrname);
		        }

		        // FTP USAP file
		        logger.log(Priority.INFO,"[EMC] Storing USAP Trigger File to FTP Server ...");

		        try {
		        	File firstLocalFile = new File(baseDir + trgFn);
		        	InputStream inputStream = new FileInputStream(firstLocalFile);
		        	ftpClient.storeFile(  remoteUsapDir + "/" + trgFn, inputStream);
		            //ftpServer.storeFileIn( baseDir + trgFn,  remoteUsapDir + "/" + trgFn,  "A");

		            logger.log(Priority.INFO,"[EMC] USAP Trigger file successfully stored to FTP Server. Remote File: " + trgFn);
		            inputStream.close();
		        }
		        catch (Exception e) {
		            logger.log(Priority.INFO,"[EMC] Unable to store trigger file: " + remoteUsapDir + "/" + trgFn + 
		            " to FTP Server: " + ftpSvrname);

		            throw new Exception( "Unable to store trigger file: " + remoteUsapDir + "/" + trgFn + 
		                            " to FTP Server: " + ftpSvrname);
		        }

		        ftpClient.disconnect();

		        logger.log(Priority.INFO,"[EMC] Disconnected from FTP Server.");
		    }
		    else if (file_to_mssl_via_aq.equalsIgnoreCase("Y")) {
		        // BPM 2.6.07 (AQ)
		        logger.log(Priority.INFO,"[EMC] USAPCommon.populateUsapFile() -- In AQ method NO USAP trigger file generated and Only USAP data saved into file: " + usapFn);

		        logger.log(Priority.INFO,"[EMC] [USAP File] Start Processing USAP File sending to MSSL using Oracle AQ.");

		        AlertNotification alert = new AlertNotification();

		        // alert.businessModule = "Sending ACK/USAP using AQ";
		        alert.importance = "HIGH";

		        // alert.subject = "";
		        alert.jmsType = usapJmsType;

		        // "Raw Price Document";
		        alert.content = sbuf.toString();
		        alert.noticeType = "Sending Acknowledgement and USAP Data To MSSL";
		        alert.destination = msslDestinationURL;
		        alert.ackDBUpdate = false;
		        alertNotificationImpl.sendAckUsapToMSSLviaAQ(alert);

		        logger.log(Priority.INFO,"[EMC] [USAP File] USAP File successfully sent to MSSL using Oracle AQ.");
		    }

		    // BPM 2.6.07 (AQ)
		}
		catch (Exception e) {
		    // BPM 2.6.07 (AQ)
		    if (file_to_mssl_via_aq.equalsIgnoreCase("N")) {
		        logger.log(Priority.INFO,"[EMC] [USAP File] Failed to send USAP File to MSSL using Unix and Oracle program. Exception in USAPCommon.populateUsapFile().");
		    }
		    else if (file_to_mssl_via_aq.equalsIgnoreCase("Y")) {
		        logger.log(Priority.INFO,"[EMC] [USAP File] Failed to send USAP File to MSSL using Oracle AQ. Exception in USAPCommon.populateUsapFile().");
		    }

		    // BPM 2.6.07 (AQ)
		    throw new USAPException(e.getMessage());
		   
		}
		
	}
}
