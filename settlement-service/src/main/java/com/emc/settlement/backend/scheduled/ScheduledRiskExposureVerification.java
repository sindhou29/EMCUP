/**
 * 
 */
package com.emc.settlement.backend.scheduled;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.rex.model.bc.am.common.RexInternalService;
import com.emc.rex.model.bc.am.common.RexInternalService_Service;
import com.emc.settlement.common.PavPackageImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.oracle.xmlns.adf.svc.errors.ServiceException;

/**
 * @author DWTN1561
 *
 */
@Service
public class ScheduledRiskExposureVerification {

	/**
	 * 
	 */
	public ScheduledRiskExposureVerification() {
		// TODO Auto-generated constructor stub
	}

	private static final Logger logger = Logger.getLogger(ScheduledRiskExposureVerification.class);


	
	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private PavPackageImpl pavPackageImpl;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	String logPrefix = "[SH-SREV] ";
	
	
	public Map<String, Object> initializeVariables(Map<String, Object> variableMap) throws Exception {
		String msgStep = "ScheduledRiskExposureVerification.initializeVariables()";
		String soapServiceUrl = (String) variableMap.get("soapServiceUrl");
		SettlementRunParams settlementParam = (SettlementRunParams) variableMap.get("settlementParam");
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( soapServiceUrl : " + soapServiceUrl
				+ " settlementParam : " + settlementParam + ")");
		try {
			logger.info("Starting Activity " + msgStep);
			Map<String, String> propertiesMap = UtilityFunctions.getProperties();
			soapServiceUrl = propertiesMap.get("soapServiceUrl");

			settlementParam.setRunType("P");

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DATE, 1);
			settlementParam.setRunDate(cal.getTime());
		} catch (Exception e) {
			logger.log(Priority.FATAL, logPrefix + " <" + msgStep + "> Exception: " + e.getMessage());
			throw new Exception("Abnormal Termination of Scheduled RiskExposure Verification: (initializeVariables)");
		}
		logger.info("Returning from service " + msgStep + " - soapServiceUrl : " + soapServiceUrl);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		logger.info("Returning from service "+msgStep+" - ( soapServiceUrl :" + soapServiceUrl
				+ " settlementParam.getRunType() :" + settlementParam.getRunType()
				+ " settlementParam.getRunDate() :" + settlementParam.getRunDate()
				+ " settlementParam :" + settlementParam + ")");
		return variableMap;

	}
	
    @Transactional
	public Map<String, Object> createEvent(Map<String, Object> variableMap)
    {

		String eveId = (String) variableMap.get("eveId");
		Date runDate = (Date) variableMap.get("runDate");
		Date settlementDate = (Date) variableMap.get("settlementDate");
		String settlementType = (String) variableMap.get("settlementType");
		Boolean isSchedule = (Boolean)variableMap.get("isSchedule");


		String msgStep = "ScheduledRiskExposureVerification.createEvent()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( eveId : " + eveId
				+ " runDate : " + runDate
				+ " settlementType : " + settlementType
				+ " isSchedule : " + isSchedule
				+ " settlementDate : " + settlementDate + ")");
    	try {
    		logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

    		Calendar cal = Calendar.getInstance();
			runDate  = cal.getTime();
			
    	    settlementDate = null;
    	    settlementType="";
    	    isSchedule = true;

    	    // Create JAM_EVENTS
    	    eveId = utilityFunctions.createJAMEvent("EXE", "Risk Exposure Report Verify"); 
    	}
    	catch (Exception e) {
    		logger.info(logPrefix + "Exception occured on " + msgStep + ": " + e.getMessage());
    	}
    	variableMap.put("eveId", eveId);
    	variableMap.put("runDate", runDate);
    	variableMap.put("settlementDate", settlementDate);
    	variableMap.put("settlementType", settlementType);
    	variableMap.put("isSchedule", isSchedule);
		logger.info("Returning from service "+msgStep+" - ( eveId :" + eveId
				+ " runDate :" + runDate
				+ " settlementDate :" + settlementDate
				+ " isSchedule :" + isSchedule
				+ " settlementType :" + settlementType + ")");
    	return variableMap;
    }


    @Transactional
	public void createRiskReport(Map<String, Object> variableMap)
    {
		Date runDate = (Date) variableMap.get("runDate");
		Date settlementDate = (Date) variableMap.get("settlementDate");
		String settlementType = (String) variableMap.get("settlementType");
		Boolean isSchedule = (Boolean)variableMap.get("isSchedule");

		String msgStep = "ScheduledRiskExposureVerification.createRiskReport()";
		logger.log(Priority.INFO, "Input Parameters for "+ msgStep+"  ( runDate : " + runDate
				+ " settlementType : " + settlementType
				+ " isSchedule : " + isSchedule
				+ " settlementDate : " + settlementDate + ")");
		try{
			String dateFormat = "yyyy-MM-dd";
			SimpleDateFormat sdf  = new SimpleDateFormat(dateFormat);
			String unpaidAmount = "";
			String uninvoicedAmount = "";
			BigDecimal currentExposure = new BigDecimal(0.000);

			// DRSAT-524
			String netExposure = "";
			String creditSupportValue = "";
			BigDecimal adeAmount = new BigDecimal(0.000);
			String noOfAdeDay = "";
			String prepayment = "";
			String sqlCommand;
			int countX = 0;

			// BPM 2.5.2 Flag to identify whether this is the first date of active trading by the MP and if yes then exit.
			String incDateinRiskExpRep;

			// End
			// Change request: Risk Exposure Report, ADE Report
			String version = "";
			String settRunId = "";
			Map<String, String> pRunList = new HashMap<String, String>();
			Date pRunDate = null;
			Date settDateFRun = null;
			String accountId = "";
			String ptpId = "";

			// DRCAP Phase 2 Changes//
			String ptpName = "";

			// DRCAP Phase 2 Changes //
			String dispTitle = "";
			String dispRunDate = "";
			Date debitDueDate = null;
			Date creditDueDate = null;
			double debitDays;
			double creditDays;
			String noOfDebitUnpaidDay = "";
			String noOfCreditUnpaidDay = "";
			String f_str_id = "";
			Date localRunDate = new Date();

			// HWY
			String unpaidDebitStartDate = "";
			String unpaidDebitEndDate = "";
			String unpaidCreditStartDate = "";
			String unpaidCreditEndDate = "";
			String unpaidStartDate = "";
			String unpaidEndDate = "";
			String unInvoicedStartDate = "";
			String unInvoicedEndDate = "";

			// Added on 25 Sep 2015 for Setting Info
			logger.info(logPrefix + "Inserting Data into Settings to Track First NON ZERO sum(abs(charge code)) for an new MP.");

			String insertPTPCmd = "INSERT INTO NEM.NEM_RISKEX_PTP_START_SETT_DATE (PARTICIPANT_NAME, PTP_ID, START_TRADING_DATE) " +
					"    SELECT ptp.name PARTICIPANT_NAME, ptp.ID PTP_ID, sa.settlement_date  START_TRADING_DATE                       " +
					"    FROM                                                                                                          " +
					"    nem.nem_participants ptp,                                                                                     " +
					"    (                                                                                                             " +
					"      SELECT sac.ptp_id,                                                                                          " +
					"             sac.ptp_version,                                                                                     " +
					"             nas.str_id,                                                                                          " +
					"             nas.settlement_date,                                                                                 " +
					"             SUM (ABS (nas.column_5)) ptp_total_due                                                               " +
					"        FROM nem.nem_account_statements nas,                                                                      " +
					"             nem.nem_settlement_accounts sac,                                                                     " +
					"             nem.nem_settlement_runs nstr,                                                                        " +
					"             nem.nem_package_authorisations pka                                                                   " +
					"       WHERE     nas.report_section = 'MAIN'                                                                      " +
					"             AND nas.column_1 IS NOT NULL                                                                         " +
					"             AND nas.sac_id = sac.id                                                                              " +
					"             AND nas.sac_version = sac.version                                                                    " +
					"            AND nas.settlement_date = nstr.settlement_date                                                        " +
					"            AND nas.str_id = nstr.id                                                                              " +
					"            AND trunc(nstr.run_date) = trunc(sysdate)                                                             " +
					"            AND nstr.run_type in ('P',  'F')                                                                      " +
					"            AND pka.pkg_id = nstr.pkg_id                                                                          " +
					"            AND pka.authorisation_status IN ('WAITING')                                                           " +
					"            AND pka.authorisation_date =                                                                          " +
					"                                           (SELECT MAX (pka1.authorisation_date)                                  " +
					"                                              FROM nem.nem_package_authorisations pka1                            " +
					"                                             WHERE pka1.pkg_id = pka.pkg_id)                                      " +
					" and                                                                                       " +
					" (                                                                                         " +
					" exists (select 1 from pvfo.PVF_RISKEX_PTP_START_SETT_DT_V ptpinner                        " +
					"                     where  ptpinner.ptp_id = sac.ptp_id                                   " +
					"                      and    nas.settlement_date < ptpinner.START_TRADING_DATE             " +
					"                      )                                                                    " +
					" OR                                                                                        " +
					" not exists (select 1 from pvfo.PVF_RISKEX_PTP_START_SETT_DT_V ptpinner                    " +
					"                     where  ptpinner.ptp_id = sac.ptp_id                                   " +
					"                      )                                                                    " +
					" )                                                                                         " +
					"   GROUP BY nas.str_id,                                                                                           " +
					"            nas.settlement_date,                                                                                  " +
					"            sac.ptp_id,                                                                                           " +
					"            sac.ptp_version                                                                                       " +
					"    HAVING    SUM (ABS (nas.column_5)) > 0                                                                        " +
					"      )  sa                                  " +
					"      where    sa.ptp_id = ptp.id            " +
					"      AND      sa.ptp_version = ptp.version  ";

			// "      group by PTP ID only ";

			jdbcTemplate.update(insertPTPCmd, new Object[] {});

			logger.info(logPrefix + "Deleting Data from Settings to Remove new MP info whose Start Date Trading Value reset to Zero.");

			String deletePTPCmd = "Delete from NEM.NEM_RISKEX_PTP_START_SETT_DATE  " +
					"             Where PTP_ID IN                                                                                          " +
					"             (SELECT ptp_id PTP_ID                                                                                    " +
					"              FROM                                                                                                    " +
					"                      (                                                                                               " +
					"         SELECT sac.ptp_id,                                                                                           " +
					"                      sac.ptp_version,                                                                                " +
					"                      nas.str_id,                                                                                     " +
					"                      nas.settlement_date,                                                                            " +
					"                      SUM (ABS (nas.column_5)) ptp_total_due                                                          " +
					"                 FROM nem.nem_account_statements nas,                                                                 " +
					"                      nem.nem_settlement_accounts sac,                                                                " +
					"                      nem.nem_settlement_runs nstr,                                                                   " +
					"                      nem.nem_package_authorisations pka                                                              " +
					"                WHERE     nas.report_section = 'MAIN'                                                                 " +
					"                      AND nas.column_1 IS NOT NULL                                                                    " +
					"                      AND nas.sac_id = sac.id                                                                         " +
					"                      AND nas.sac_version = sac.version                                                               " +
					"                     AND nas.settlement_date = nstr.settlement_date                                                   " +
					"                     AND nas.str_id = nstr.id                                                                         " +
					"                     AND trunc(nstr.run_date) = trunc(sysdate)                                                        " +
					"                     AND nstr.run_type in ('P',  'F')                                                                 " +
					"                     AND pka.pkg_id = nstr.pkg_id                                                                     " +
					"                     AND pka.authorisation_status IN ('WAITING')                                                      " +
					"                     AND pka.authorisation_date =                                                                     " +
					"                                                    (SELECT MAX (pka1.authorisation_date)                             " +
					"                                                       FROM nem.nem_package_authorisations pka1                       " +
					"                                                      WHERE pka1.pkg_id = pka.pkg_id)                                 " +
					"                     and exists (select 1 from pvfo.PVF_RISKEX_PTP_START_SETT_DT_V ptpinner                           " +
					"                                         where  ptpinner.ptp_id = sac.ptp_id                                          " +
					"                                          and    ptpinner.START_TRADING_DATE = nas.settlement_date                    " +
					"                                          )                                                                           " +
					"            GROUP BY nas.str_id,                                                                                      " +
					"                     nas.settlement_date,                                                                             " +
					"                     sac.ptp_id,                                                                                      " +
					"                     sac.ptp_version                                                                                  " +
					"              HAVING    SUM (ABS (nas.column_5)) = 0                                                                  " +
					"                      )                                                                                               " +
					"                 ) ";

			jdbcTemplate.update(deletePTPCmd);

			logger.info(logPrefix + "Starting Risk Report and ADE processing.");

			// End
			// 	Testing
			// 	runFrom = "F"
			// 	runDate = '2011-03-08'
			// 	settlementDate = '2010-06-15'
			//  Testing
			// if runFrom = "B" then			//Change request: Risk Exposure Report, ADE Report
			if (settlementDate == null) {
				// get the p-run date
				sqlCommand = "SELECT settlement_date, SR.id, run_date " +
						"FROM NEM.NEM_SETTLEMENT_RUNS SR, NEM.JAM_EVENTS EVE " +
						"WHERE SR.EVE_ID = EVE.ID AND RUN_TYPE = 'P' and " +
						//"TRUNC(run_date)=TRUNC(sysdate-100) AND COMPLETED = 'Y' AND SUCCESS = 'Y' " + //TODO
						"TRUNC(run_date)=TRUNC(?) AND COMPLETED = 'Y' AND SUCCESS = 'Y' " +
						"ORDER BY settlement_date,seq ASC";

				List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(runDate));
				for(Map recordMap: resultList) {
					localRunDate = (Date) recordMap.get("RUN_DATE");
					Date settDate = (Date) recordMap.get("SETTLEMENT_DATE");
					pRunList.put(new SimpleDateFormat("yyyy-MM-dd").format(settDate), (String) recordMap.get("ID"));

				}
			}
			else {
				if (settlementType.equalsIgnoreCase("P")) {
					// get the run date s
					sqlCommand = "SELECT settlement_date, SR.id, run_date " +
							"FROM NEM.NEM_SETTLEMENT_RUNS SR, NEM.JAM_EVENTS EVE, NEM.JAM_EVENT_SCHEDULES ES " +
							"WHERE EVE.ESD_ID = ES.ID AND SR.EVE_ID = EVE.ID AND RUN_TYPE = 'P' and " +
							"trunc(settlement_date)= trunc(?) AND COMPLETED = 'Y' AND SUCCESS = 'Y' " +
							"AND ES.FREQUENCY = 'D' ORDER BY run_date DESC";

					//Object[] params = new Object[1];
					//params[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
					List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(settlementDate));
					for (Map row : list) {
						localRunDate = (Date) row.get("run_date");
					}

					// get the run id
					sqlCommand = "SELECT settlement_date, SR.id, run_date " +
							"FROM NEM.NEM_SETTLEMENT_RUNS SR, NEM.JAM_EVENTS EVE " +
							"WHERE SR.EVE_ID = EVE.ID AND RUN_TYPE = 'P' and " +
							"trunc(settlement_date)= trunc(?) AND COMPLETED = 'Y' AND SUCCESS = 'Y' " +
							"ORDER BY run_date DESC";

					//Object[] params1 = new Object[1];
					//params1[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
					List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(settlementDate));
					for (Map row : list1) {
						Date settDate = (Date) row.get("settlement_date");
						if (pRunList.size() == 0) {
							pRunList.put(sdf.format(settDate), (String)row.get("ID"));
						}
					}
					// get number of SD count
					sqlCommand = "SELECT count(distinct settlement_date) " +
							"FROM NEM.NEM_SETTLEMENT_RUNS SR, NEM.JAM_EVENTS EVE " +
							"WHERE SR.EVE_ID = EVE.ID AND RUN_TYPE = 'P' AND " +
							"TRUNC(run_date)= trunc(?) AND settlement_date<= trunc(?) AND " +
							"COMPLETED = 'Y' AND SUCCESS = 'Y' " +
							"ORDER BY settlement_date,seq ASC";

					Object[] params2 = new Object[2];
					params2[0] =  utilityFunctions.convertUDateToSDate(localRunDate);//sdf.format(localRunDate);
					params2[1] =  utilityFunctions.convertUDateToSDate(settlementDate);
					countX = jdbcTemplate.queryForObject(sqlCommand, params2, Integer.class);
				}
				else {
					// It's not a P-Run
					logger.log(Priority.INFO,logPrefix + "The run (" + settlementType + ") is not a P-Run, " +
							"will not generate Risk Exposure and ADE Report.");

					return;
				}
			}

			// End of Change request: Risk Exposure Report, ADE Report
			logger.info(logPrefix + "The run date are " + ((localRunDate != null) ? utilityFunctions.getddMMyyyy(localRunDate) : null));

			logger.info(logPrefix + "The number of P-Run date are " + pRunList.size());

			logger.info(logPrefix + "The count for x is " + String.valueOf(countX));

			//    sqlCommand = "SELECT settlement_date FROM NEM.NEM_SETTLEMENT_RUNS SR, NEM.JAM_EVENTS EVE " +
			//                 "WHERE SR.EVE_ID = EVE.ID AND RUN_TYPE='F' and TRUNC(RUN_DATE)=TRUNC(TO_DATE(?, 'YYYY-MM-DD')) " +
			//                 "AND COMPLETED = 'Y' AND SUCCESS = 'Y' ORDER BY settlement_date DESC"
			if (localRunDate!= null) {
				sqlCommand = "SELECT MAX (str.settlement_date) settlement_date " +
						"  FROM nem.nem_settlement_runs str, nem.nem_package_authorisations pka  " +
						"WHERE     str.run_type = 'F' " +
						"  AND TRUNC (str.run_date) > TRUNC (TO_DATE(?, 'YYYY-MM-DD') - 20) " +
						"  AND pka.pkg_id = str.pkg_id " +
						"  AND pka.authorisation_status IN ('AUTHORISED', '1ST AUTHORISED', 'WAITING') " +
						"  AND pka.authorisation_date = (SELECT MAX (pka1.authorisation_date) " +
						"                                  FROM nem.nem_package_authorisations pka1 " +
						"                                WHERE pka1.pkg_id = pka.pkg_id) " +
						"  AND str.run_date = (SELECT MAX (str1.run_date) " +
						"                        FROM nem.nem_settlement_runs str1 " +
						"                      WHERE str1.settlement_date = str.settlement_date)";

				Object[] params = new Object[1];
				params[0] = new SimpleDateFormat("yyyy-MM-dd").format(localRunDate);
				Object[] result = utilityFunctions.queryforList(sqlCommand, params, "SETTLEMENT_DATE");
				settDateFRun = (Date) result[0];
			}

			// DRSAT-523, select the latest P-run settlement date on run date
			//    settDatePRun as Time = null
			//    sqlCommand = "SELECT settlement_date FROM NEM.NEM_SETTLEMENT_RUNS SR, NEM.JAM_EVENTS EVE " +
			//                 "WHERE SR.EVE_ID = EVE.ID AND RUN_TYPE='P' and TRUNC(RUN_DATE)=TRUNC(TO_DATE(?, 'YYYY-MM-DD')) " +
			//                 "AND COMPLETED = 'Y' AND SUCCESS = 'Y' ORDER BY settlement_date DESC"
			//    for each row in executeQuery(DynamicSQL, sentence : sqlCommand, implname : dbpath,
			//    inParameters : params) do
			//        settDatePRun = Time(String.valueOf(o : row[1]))
			//        exit
			//    end
			// DRSAT-523 end
			if (pRunList.size() == 0 || settDateFRun == null) {
				String msg = " No P-Run or F-Run found for Run Date: " + utilityFunctions.getddMMyyyy(localRunDate);

				logger.info(logPrefix + msg);

				throw new Exception(msg);
			}

			logger.info(logPrefix + "The F settlement date are " + utilityFunctions.getddMMyyyy(settDateFRun));

			dispRunDate = utilityFunctions.getddMMyyyy(localRunDate);
			f_str_id = utilityFunctions.getLatestRunId(settDateFRun, "F");


			//    p_str_id as String = UtilityFunctions.getLatestRunId(settlementDate : settDatePRun, runType : "P")	// DRSAT-523
			Iterator<Map.Entry<String, String>> iterator = pRunList.entrySet().iterator();
			while(iterator.hasNext()) {
				Map.Entry<String, String> settlement_date = iterator.next();
				// pRunList loop start
				// Change request: Risk Exposure Report, ADE Report
				if (settlementDate == null) {
					countX = 1 + countX;
				}

				// DRSAT-603


				int successRuns = 0;

				sqlCommand = "SELECT count(str.ID) CNT " +
		                     "  FROM nem.nem_settlement_runs str, nem.nem_package_authorisations pka " +
		                     "WHERE     str.run_type IN ('F', 'P') " +
		                     "  AND settlement_date BETWEEN (SELECT MIN (settlement_date) " +
		                     "                               FROM (SELECT ROWNUM, settlement_date " +
		                     "                                     FROM (  SELECT *  " +
		                     "                                             FROM nem_settlement_calendar " +
		                     "                                             WHERE settlement_date < " +
		                     "                                                   (TO_DATE (?,'YYYY-MM-DD') + 1) " +
		                     "                                             ORDER BY settlement_date DESC) " +
		                     "                                     WHERE ROWNUM < 91)) " +
		                     "                                       AND TO_DATE (?,'YYYY-MM-DD') " +
		                     "  AND pka.pkg_id = str.pkg_id " +
		                     "  AND pka.authorisation_status IN ('AUTHORISED','1ST AUTHORISED',  'WAITING') " +
		                     "  AND pka.authorisation_date = (SELECT MAX (pka1.authorisation_date) " +
		                     "                                FROM nem.nem_package_authorisations pka1 " +
		                     "                                WHERE pka1.pkg_id = pka.pkg_id) " +
		                     "  AND str.run_date = (SELECT MAX (str1.run_date) " +
		                     "                      FROM nem.nem_settlement_runs str1 " +
		                     "                      WHERE str1.settlement_date = str.settlement_date " +
		                     "                        AND str1.run_type IN ('P', 'F') " +
		                     "                        AND str1.ID NOT IN (SELECT STR_ID FROM NEM.NEM_TEST_SETTLEMENT_RUNS_DTL))";

				Object[] params = new Object[2];
				params[0] = settlement_date.getKey();
				params[1] = settlement_date.getKey();
				Object[] result = utilityFunctions.queryforList(sqlCommand, params, "CNT");
				successRuns = ((BigDecimal)result[0]).intValue();

				// DRSAT-603 end
				logger.info(logPrefix + "The count for successful runs is " + successRuns);

				pRunDate = new SimpleDateFormat("yyyy-MM-dd").parse(settlement_date.getKey());
				settRunId = settlement_date.getValue();

				logger.info(logPrefix + " Processing settlement date " + utilityFunctions.getddMMyyyy(pRunDate));

				// Change request: Risk Exposure Report, ADE Report
				// get the standing data versin number
				version = pavPackageImpl.getStandingVersion(pRunDate);

				logger.info(logPrefix + " The standing version is " + version);

				int pos;

				// Change request (apply fixes for handling multiple unauthorized P-runs)
				Date pRunUnauth = utilityFunctions.addDays(pRunDate, (- 1 * (countX - 1)));
				Date pRunAuth =  utilityFunctions.addDays(pRunDate, (- 1 * (countX)));

				// the sequent number for report records
				pos = 1;

				// DRCAP Phase 2 Changes - Added //
				String sacType;
				int headerpos = 0;

				// DRCAP Phase 2 Changes - Added //
				// DRCAP Phase 2 Changes - Comment - Start //
				try{

					sqlCommand = "select distinct ptp.ID, ptp.NAME from NEM.NEM_SETTLEMENT_ACCOUNTS SAC, NEM.NEM_PARTICIPANTS PTP " +
			                     "WHERE ptp.VERSION=? and sac.ptp_id = ptp.id  AND sac.ptp_version= ptp.version and sac.version =? and sac.SAC_TYPE NOT IN ('A','T','O') ";

					/*if(pstmt != null) pstmt.close();
					if(rs != null) rs.close();
					pstmt = conn.prepareStatement(sqlCommand);
					pstmt.setString(1, version);
					pstmt.setString(2, version);
					rs = pstmt.executeQuery();*/

					params = new Object[2];
					params[0] = version;
					params[1] = version;
					result = null;
					List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, params);
					for (Map row : list) {
						// ptp loop start
						ptpId = (String) row.get("ID");

						// DRCAP Phase 2 Changes //
						ptpName = (String) row.get("NAME");

						// DRCAP Phase 2 Changes //
						logger.info(logPrefix + " The iteration started For Market Participant:CP66 " + ptpName);

						// initialize the ptp's data               //DRCAP Phase 2 Changes //
						unpaidAmount = "0";
						uninvoicedAmount = "0";
						adeAmount = new BigDecimal(0.00);
						int adeCount = 0;

						// Change request: Risk Exposure Report, ADE Report
						creditSupportValue = "0";
						prepayment = "0";
						BigDecimal adeTotal = new BigDecimal(0.0);

						// logger.info(logPrefix + " After Risk report 1111" );
						// (20,6)
						String sqlCommand1;

						String riskExposure = "";

						// initialize the temporary data
						BigDecimal csvNonCash = new BigDecimal(00000000000000000000.00000);
			            
			            BigDecimal csvCash =  new BigDecimal(00000000000000000000.00000);
			            BigDecimal csvTotal =  new BigDecimal(00000000000000000000.00000);
			            BigDecimal ne =  new BigDecimal(000000000000000000000000000000.000000000000000);   
			            // DRCAPWARRANTY 55
			            BigDecimal ene =  new BigDecimal(00000000000000000000.00);

						// extract ADE details
						String[] adeResults = { dispTitle };
						BigDecimal total =  new BigDecimal(00000000000000000000.00);
			            BigDecimal ADE =  new BigDecimal(00000000000000000000.00);
						incDateinRiskExpRep = "N";

						logger.info(logPrefix + " before sqlcommand1 execution" );
						sqlCommand1 = "SELECT settlement_date, RUN_TYPE, seq, " +
			                          "           ADE_report_PTP_Amount stmt, running_avg_sum_ADE ADE,                                                                                                                      " +
			                          "           ptp_id ptpid , ptp,                                                                                                                                                       " +
			                          "           str_id,                                                                                                                                                                   " +
			                          "          min_outstanding_invoiced_date, max_outstanding_invoiced_date, ((-1) * total_outstd_inv_amount) INV_AMOUNT ,                                                                              " +
			                          "          min_outstand_uninvoiced_date, max_outstand_uninvoiced_date, ((-1) * total_outstd_un_inv_amount) UN_INV_AMOUNT ,                                                                             " +
			                          "          ((-1) * final_curr_exp) CURR_EXP ,                                                                                                                                                     " +
			                          "          final_X,                                                                                                                                                                   " +
			                          "          final_ADE_for_SD                                                                                                                                                           " +
			                          "                        FROM                                                                                                                                                         " +
			                          "                        (                                                                                                                                                            " +
			                          "                        select sac_date_dtl.settlement_date settlement_date, (-1 * PTP_TOTAL_DUE) ADE_REport_PTP_Amount, str.seq, STR.RUN_TYPE , sac_date_dtl.ptp_id,                " +
			                          "                        sum((-1 * PTP_TOTAL_DUE)) over ( partition by sac_date_dtl.ptp_id                                                                                            " +
			                          "                                                                             order by sac_date_dtl.settlement_date                                                                   " +
			                          "                                                                             ) running_sum_ADE_b4_avg,                                                                               " +
			                          "                        round(                                                                                                                                                       " +
			                          "                        (                                                                                                                                                            " +
			                          "                        sum((-1 * PTP_TOTAL_DUE)) over ( partition by sac_date_dtl.ptp_id                                                                                            " +
			                          "                                                                             order by sac_date_dtl.settlement_date                                                                   " +
			                          "                                                                             )                                                                                                       " +
			                          "                        ) /                                                                                                                                                          " +
			                          "                        (                                                                                                                                                            " +
			                          "                        count((-1 * PTP_TOTAL_DUE)) over ( partition by sac_date_dtl.ptp_id                                                                                          " +
			                          "                                                                             order by sac_date_dtl.settlement_date                                                                   " +
			                          "                                                                             )                                                                                                       " +
			                          "                        ), 2)                                                                                                                                                        " +
			                          "                        running_avg_sum_ADE,                                                                                                                                         " +
			                          "                        sac_date_dtl.ptp,                                                                                                                                            " +
			                          "                        STR.ID str_id,                                                                                                                                               " +
			                          "                        min(   Case                                                                                                                                                  " +
			                          "                                   when sac_date_dtl.PTP_PAYMENT_DUE_DATE > trunc(sysdate) and sac_date_dtl.settlement_date <=latest_final_run.settlement_date then                  " +
			                          "                                         sac_date_dtl.settlement_date                                                                                                        " +
			                          "                                   End                                                                                                                                               " +
			                          "                              )                                                                                                                                                      " +
			                          "                        over ( partition by sac_date_dtl.ptp_id                                                                                                                      " +
			                          "                                  order by sac_date_dtl.settlement_date                                                                                                              " +
			                          "                                )                                                                                                                                                    " +
			                          "                        min_outstanding_invoiced_date,                                                                                                                               " +
			                          "                        max(   Case                                                                                                                                                  " +
			                          "                                   when sac_date_dtl.PTP_PAYMENT_DUE_DATE > trunc(sysdate) and sac_date_dtl.settlement_date <=latest_final_run.settlement_date then                  " +
			                          "                                         sac_date_dtl.settlement_date                                                                                                                 " +
			                          "                                   End                                                                                                                                               " +
			                          "                              )                                                                                                                                                      " +
			                          "                        over ( partition by sac_date_dtl.ptp_id                                                                                                                      " +
			                          "                                  order by sac_date_dtl.settlement_date                                                                                                              " +
			                          "                                )                                                                                                                                                    " +
			                          "                        max_outstanding_invoiced_date,                                                                                                                               " +
			                          "                        (                                                                                                                                                            " +
			                          "                        sum(   Case                                                                                                                                                  " +
			                          "                                   when sac_date_dtl.PTP_PAYMENT_DUE_DATE > trunc(sysdate) and sac_date_dtl.settlement_date <=latest_final_run.settlement_date then                  " +
			                          "                                        sac_date_dtl.PTP_TOTAL_DUE                                                                                                                   " +
			                          "                                   End                                                                                                                                               " +
			                          "                              )                                                                                                                                                      " +
			                          "                        over ( partition by sac_date_dtl.ptp_id                                                                                                                      " +
			                          "                                  order by sac_date_dtl.settlement_date                                                                                                              " +
			                          "                                )                                                                                                                                                    " +
			                          "                        )                                                                                                                                                            " +
			                          "                        total_outstd_inv_amount,                                                                                                                                     " +
			                          "                        min(   Case                                                                                                                                                  " +
			                          "                                   when sac_date_dtl.settlement_date >latest_final_run.settlement_date and sac_date_dtl.settlement_date < (parameter_p_sett_date.sett_date + 1) then " +
			                          "                                         sac_date_dtl.settlement_date                                                                                                           " +
			                          "                                   End                                                                                                                                               " +
			                          "                              )                                                                                                                                                      " +
			                          "                        over ( partition by sac_date_dtl.ptp_id                                                                                                                      " +
			                          "                                  order by sac_date_dtl.settlement_date                                                                                                              " +
			                          "                                )                                                                                                                                                    " +
			                          "                        min_outstand_uninvoiced_date,                                                                                                                                " +
			                          "                        max(   Case                                                                                                                                                  " +
			                          "                                   when sac_date_dtl.settlement_date >latest_final_run.settlement_date and sac_date_dtl.settlement_date < (parameter_p_sett_date.sett_date + 1) then " +
			                          "                                         sac_date_dtl.settlement_date                                                                                                                 " +
			                          "                                   End                                                                                                                                               " +
			                          "                              )                                                                                                                                                      " +
			                          "                        over ( partition by sac_date_dtl.ptp_id                                                                                                                      " +
			                          "                                  order by sac_date_dtl.settlement_date                                                                                                              " +
			                          "                                )                                                                                                                                                    " +
			                          "                        max_outstand_uninvoiced_date,                                                                                                                                " +
			                          "                        (                                                                                                                                                            " +
			                          "                        sum(   Case                                                                                                                                                  " +
			                          "                                  when sac_date_dtl.settlement_date >latest_final_run.settlement_date and sac_date_dtl.settlement_date < (parameter_p_sett_date.sett_date + 1) then  " +
			                          "                                        sac_date_dtl.PTP_TOTAL_DUE                                                                                                                   " +
			                          "                                   End                                                                                                                                               " +
			                          "                              )                                                                                                                                                      " +
			                          "                        over ( partition by sac_date_dtl.ptp_id                                                                                                                      " +
			                          "                                  order by sac_date_dtl.settlement_date                                                                                                              " +
			                          "                                )                                                                                                                                                    " +
			                          "                        )                                                                                                                                                            " +
			                          "                        total_outstd_un_inv_amount,                                                                                                                                  " +
			                          "                        sum(   Case                                                                                                                                                  " +
			                          "                                   when sac_date_dtl.PTP_PAYMENT_DUE_DATE > trunc(sysdate) and sac_date_dtl.settlement_date < (parameter_p_sett_date.sett_date + 1)  then            " +
			                          "                                        sac_date_dtl.PTP_TOTAL_DUE                                                                                                                   " +
			                          "                                   End                                                                                                                                               " +
			                          "                              )                                                                                                                                                      " +
			                          "                        over ( partition by sac_date_dtl.ptp_id                                                                                                                      " +
			                          "                                  order by sac_date_dtl.settlement_date                                                                                                              " +
			                          "                                )                                                                                                                                                    " +
			                          "                        final_curr_exp,                                                                                                                                              " +
			                          "                        sum(   Case                                                                                                                                                  " +
			                          "                                   when sac_date_dtl.PTP_PAYMENT_DUE_DATE > trunc(sysdate) and sac_date_dtl.settlement_date < (parameter_p_sett_date.sett_date + 1)  then            " +
			                          "                                        1                                                                                                                                            " +
			                          "                                   End                                                                                                                                               " +
			                          "                              )                                                                                                                                                      " +
			                          "                        over ( partition by sac_date_dtl.ptp_id                                                                                                                      " +
			                          "                                  order by sac_date_dtl.settlement_date                                                                                                              " +
			                          "                                )                                                                                                                                                    " +
			                          "                        final_X,                                                                                                                                                     " +
			                          "                        round(                                                                                                                                                       " +
			                          "                        (                                                                                                                                                            " +
			                          "                        sum((-1 * PTP_TOTAL_DUE)) over ( partition by sac_date_dtl.ptp_id                                                                                            " +
			                          "                                                                             order by sac_date_dtl.settlement_date                                                                   " +
			                          "                                                                             )                                                                                                       " +
			                          "                        ) *  (1/num_of_days_denom)                                                                                                                                   " +
			                          "                        , 2)  final_ADE_for_SD                                                                                                                                       " +
			                          "                        from                                                                                                                                                         " +
			                          "                        (select TO_DATE (?,'YYYY-MM-DD') sett_date                                                                                                                   " +
			                          "                          from Dual                                                                                                                                                  " +
			                          "                        ) parameter_p_sett_date ,                                                                                                                                    " +
			                          "                        (select TO_DATE (?,'YYYY-MM-DD') settlement_date from Dual                                                                                                   " +
			                          "                        ) latest_final_run,                                                                                                                                          " +
			                          "                        nem.nem_settlement_runs str,                                                                                                                                 " +
			                          "                        (  SELECT                                                                                                                                                    " +
			                          "                         NEM.GET_SETT_DEBIT_CREDIT_DUEDATE (inner_dtl.settlement_date, inner_dtl.ptp_debit_credit)                                                                   " +
			                          "                        PTP_Payment_due_date   ,                                                                                                                                     " +
			                          "                         inner_dtl.ptp_debit_credit,                                                                                                                                 " +
			                          "                         inner_dtl.PTP,                                                                                                                                              " +
			                          "                         inner_dtl.ptp_total_due,                                                                                                                                    " +
			                          "                         inner_dtl.STR_ID,                                                                                                                                           " +
			                          "                         inner_dtl.Settlement_date,                                                                                                                                  " +
			                          "                         inner_dtl.ptp_id,                                                                                                                                           " +
			                          "                         inner_dtl.ptp_version,                                                                                                                                      " +
			                          "                           (                                                                                                                                                         " +
			                          "                           Case                                                                                                                                                      " +
			                          "                                 When START_TRADING_DATE is NULL Then least(90,count_success_runs)                                                                                   " +
			                          "                                 When (TO_DATE (?,'YYYY-MM-DD') - (START_TRADING_DATE-1))  >= 90 Then least(90,count_success_runs)                                                " +
			                          "                                 Else   least((TO_DATE (?,'YYYY-MM-DD') - (START_TRADING_DATE-1)),count_success_runs)                                                             " +
			                          "                           End) num_of_days_denom                                                                                                                                    " +
			                          "                        from                                                                                                                                                         " +
			                          "                        (                                                                                                                                                            " +
			                          "                        SELECT                                                                                                                                                       " +
			                          "                            DECODE(SIGN(sum(nas.column_5)), -1, 'DEBIT', 'CREDIT') ptp_debit_credit ,                                                                                " +
			                          "                            ptp.name PTP,                                                                                                                                            " +
			                          "                            sum(nas.column_5) ptp_total_due,                                                                                                                         " +
			                          "                            nas.str_id, nas.settlement_date,                                                                                                                         " +
			                          "                            sac.ptp_id,                                                                                                                                              " +
			                          "                            sac.ptp_version,                                                                                                                                         " +
			                          "                            STDATE.START_TRADING_DATE     START_TRADING_DATE,                                                                                    " +
			                          "                              to_number(?)   count_success_runs,                                                                                                                                " +
			                          "                           min(settlement_date)    min_sett_date_success_run                                                                                                         " +
			                          "                          FROM nem.nem_account_statements nas, nem.nem_settlement_accounts sac, nem.nem_participants ptp, pvfo.PVF_RISKEX_PTP_START_SETT_DT_V stdate                  " +
			                          "                         WHERE     nas.report_section = 'MAINTOTAL'                                                                                                                  " +
			                          "                               AND nas.column_1 = 'Total Due (Owed)'                                                                                                                 " +
			                          "                               AND nas.sac_id = sac.id                                                                                                                               " +
			                          "                               AND nas.sac_version = sac.version                                                                                                                     " +
			                          "                               AND sac.ptp_id = ptp.id                                                                                                                               " +
			                          "                               AND sac.ptp_version = ptp.version                                                                                                                     " +
			                          "                               AND ptp.id = stdate.ptp_id                                                                                                                            " +
			                          "                               AND ptp.id =?                                                                                                                                        " +
			                          "                               AND nas.settlement_date >=                                                                                                                            " +
			                          "                                                   (                                                                                                                                 " +
			                          "                                                   Case                                                                                                                              " +
			                          "                                                         When STDATE.START_TRADING_DATE = '01-Jan-2014' Then (TO_DATE (?,'YYYY-MM-DD') - 91)                                              " +
			                          "                                                         When (TO_DATE (?,'YYYY-MM-DD') - (STDATE.START_TRADING_DATE-1))  >= 90 Then (TO_DATE (?,'YYYY-MM-DD') - 91)           " +
			                          "                                                         Else   STDATE.START_TRADING_DATE                                                                                            " +
			                          "                                                   End )                                                                                                                             " +
			                          "                               AND (nas.str_id, nas.settlement_date) in                                                                                                              " +
			                          "                                                        (                                                                                                                            " +
			                          "                                                        SELECT str.ID, str.settlement_date                                                                                           " +
			                          "                                                          FROM nem.nem_settlement_runs str, nem.nem_package_authorisations pka                                                       " +
			                          "                                                         WHERE     str.run_type IN ('F', 'P')                                                                                        " +
			                          "                                                               AND settlement_date BETWEEN (SELECT MIN (settlement_date)                                                             " +
			                          "                                                                                              FROM (SELECT ROWNUM, settlement_date                                                   " +
			                          "                                                                                                      FROM (  SELECT *                                                               " +
			                          "                                                                                                                FROM nem_settlement_calendar                                         " +
			                          "                                                                                                               WHERE settlement_date <                                               " +
			                          "                                                                                                                        (TO_DATE (?,'YYYY-MM-DD')  + 1)                              " +
			                          "                                                                                                            ORDER BY settlement_date DESC)                                           " +
			                          "                                                                                                     WHERE ROWNUM < 91))                                                             " +
			                          "                                                                                       AND TO_DATE (?,'YYYY-MM-DD')                                                                   " +
			                          "                                                               AND pka.pkg_id = str.pkg_id                                                                                           " +
			                          "                                                               AND pka.authorisation_status IN ('AUTHORISED', '1ST AUTHORISED',  'WAITING')                                                             " +
			                          "                                                               AND pka.authorisation_date = (SELECT MAX (pka1.authorisation_date)                                                    " +
			                          "                                                                                               FROM nem.nem_package_authorisations pka1                                              " +
			                          "                                                                                              WHERE pka1.pkg_id = pka.pkg_id)                                                        " +
			                          "                                                               AND str.run_date =                                                                                                    " +
			                          "                                                                      (SELECT MAX (str1.run_date)                                                                                    " +
			                          "                                                                         FROM nem.nem_settlement_runs str1                                                                           " +
			                          "                                                                        WHERE str1.settlement_date = str.settlement_date                                                             " +
			                          "                                                                              AND str1.run_type IN ('P', 'F') AND str1.ID NOT IN (SELECT STR_ID FROM NEM.NEM_TEST_SETTLEMENT_RUNS_DTL))                                                                       " +
			                          "                                                        )                                                                                                                            " +
			                          "                                                        group by                                                                                                                     " +
			                          "                                                        ptp.name ,                                                                                                                   " +
			                          "                                                        nas.str_id,                                                                                                                  " +
			                          "                                                        nas.settlement_date,                                                                                                         " +
			                          "                                                        sac.ptp_id,                                                                                                                  " +
			                          "                                                        sac.ptp_version,                                                                                                             " +
			                          "                                                        STDATE.START_TRADING_DATE                                                                                " +
			                          "                        )       inner_dtl                                                                                                                                            " +
			                          "                        )  sac_date_dtl                                                                                                                                              " +
			                          "                         where sac_date_dtl.settlement_date=str.settlement_date                                                                                                      " +
			                          "                         and sac_date_dtl.str_id = str.id                                                                                                                            " +
			                          "                         and sac_date_dtl.ptp_id = ?                                                                                                                                 " +
			                          "                        )                                                                                                                                                            " +
			                          "                        order by settlement_date ";
						
						Object[] params3 = new Object[12];
			            params3[0] =  new SimpleDateFormat("yyyy-MM-dd").format(pRunDate);// get the ADE Report Data
			            params3[1] =  new SimpleDateFormat("yyyy-MM-dd").format(settDateFRun);// DRSAT-603
			            params3[2] =  new SimpleDateFormat("yyyy-MM-dd").format(pRunDate);// pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
			            params3[3] =  new SimpleDateFormat("yyyy-MM-dd").format(pRunDate);// pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
			            params3[4] =  String.valueOf(successRuns);// DRSAT-603
			            params3[5] =  ptpId;// pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
			            params3[6] =  new SimpleDateFormat("yyyy-MM-dd").format(pRunDate);
			            params3[7] =  new SimpleDateFormat("yyyy-MM-dd").format(pRunDate);// pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
			            params3[8] =  new SimpleDateFormat("yyyy-MM-dd").format(pRunDate);// pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
			            params3[9] =  new SimpleDateFormat("yyyy-MM-dd").format(pRunDate);// pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
			            params3[10] =  new SimpleDateFormat("yyyy-MM-dd").format(pRunDate);// pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
			            params3[11] =  ptpId;// pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
			            List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand1, params3);
			            for (Map row1 : list1) {
							// logger.info(logPrefix + " After sqlcommand1 execution" + sqlCommand1);
							// start loop for sqlCommand1
							Date sdTime = (Date)row1.get("settlement_date");
							String SD = new SimpleDateFormat("dd MMM yyyy").format(sdTime);
							BigDecimal Seq = (BigDecimal)row1.get("seq");
							String runType = (String)row1.get("RUN_TYPE");
							BigDecimal amt = (BigDecimal)row1.get("stmt");
							amt.setScale(20,2);
							DecimalFormat decfmt = new DecimalFormat("#,###.00");
							String stmtAmt = decfmt.format(amt);
							ADE = (BigDecimal)row1.get("ADE");

							//logger.info(logPrefix + " Under Main loop execution " );
							String adeCmd = "INSERT INTO NEM.NEM_SETTLEMENT_PTP_STATEMENTS (ID,REPORT_SECTION,SEQ,COLUMN_1,COLUMN_2,COLUMN_3,COLUMN_4,COLUMN_5,STR_ID,PTP_ID,PTP_VERSION,SETTLEMENT_DATE) VALUES ( SYS_GUID(),?,?,?,?,?,?,?,?,?,?,? )";
							if (headerpos == 0) {
								// prepare the ADE report header
								Object[] params4 = new Object[11];
			        			params4[0] =  "ADEHEADER";
			        			params4[1] =  String.valueOf(pos);
			        			params4[2] =  "Settlement Date";
			        			params4[3] =  "Run Type";
			        			params4[4] =  "Run ID";
			        			params4[5] =  "Amount";
			        			params4[6] =  "ADE";
			        			params4[7] =  settRunId;
			        			params4[8] =  ptpId;
			        			params4[9] =  version;
			        			params4[10] =  utilityFunctions.convertUDateToSDate(pRunDate);
			        			jdbcTemplate.update(adeCmd, params4);
								headerpos = headerpos + 1;
							}

							// prepare the ADE report details
							Object[] params4 = new Object[11];
		        			params4[0] =  "ADEDETAIL";
		        			params4[1] =  String.valueOf(pos);
		        			params4[2] =  SD;
		        			params4[3] =  runType;
		        			params4[4] =  Seq.intValue();
		        			params4[5] =  stmtAmt;
		        			params4[6] =  decfmt.format(ADE);
		        			params4[7] =  settRunId;
		        			params4[8] =  ptpId;
		        			params4[9] =  version;
		        			params4[10] =  utilityFunctions.convertUDateToSDate(pRunDate);
		        			jdbcTemplate.update(adeCmd, params4);
							pos = pos + 1;

							if (SD.equalsIgnoreCase(new SimpleDateFormat("dd MMM yyyy").format(pRunDate))) {
								// SD = Prelim Date start
								// 						// get the CSV, non cash type
								// 						params1[0] = accountId;
								// 						params1[1] = localRunDate.format(mask : dateFormat);
								// 						sqlCommand1 = "SELECT NVL(SUM(ncs.amount),0.00000) FROM NEM_CREDIT_SUPPORT ncs, " +
								// 									  "NEM_CREDIT_SUPPORT_TYPES cst WHERE ncs.sac_id=? AND ncs.approval_status='A' AND " +
								// 									  "TO_DATE(?, 'YYYY-MM-DD') BETWEEN ncs.start_date AND ncs.end_date AND " +
								// 									  "ncs.credit_support_type_id=cst.id AND cst.name <> 'Cash Deposit'";
								//
								// 						foreach (row1 in DynamicSQL.executeQuery(sentence : sqlCommand1, implname : dbpath,
								// 																 inParameters : params1)) {
								// 							csvNonCash = Decimal.valueOf(value : row1[1]);
								// 						}
								//
								// 						// get the CSV, cash type
								// 						params1[0] = accountId;
								// 						params1[1] = localRunDate.format(mask : dateFormat);
								// 						params1[2] = localRunDate.format(mask : dateFormat);
								// 						sqlCommand1 = "SELECT NVL(SUM(ncs.amount),0.00000) FROM NEM_CREDIT_SUPPORT ncs, " +
								// 									  "NEM_CREDIT_SUPPORT_TYPES cst WHERE ncs.sac_id=? AND ncs.approval_status='A' AND " +
								// 									  "(TO_DATE(?, 'YYYY-MM-DD') BETWEEN ncs.start_date AND ncs.end_date OR " +
								// 									  "(ncs.end_date is null AND TO_DATE(?, 'YYYY-MM-DD') >= ncs.start_date)) AND " +
								// 									  "ncs.credit_support_type_id=cst.id AND cst.name = 'Cash Deposit' AND " +
								// 									  "ncs.version = (SELECT max(version) FROM NEM_CREDIT_SUPPORT WHERE id=ncs.id)";
								//
								// 						foreach (row1 in DynamicSQL.executeQuery(sentence : sqlCommand1, implname : dbpath,
								// 																 inParameters : params1)) {
								// 							csvCash = Decimal.valueOf(value : row1[1]);
								// 						}
								// 						csvTotal = csvCash + csvNonCash;
								logger.info(logPrefix + " get the CSV, non cash and cash type For Market Participant: " + ptpName);

								// get the CSV, non cash and cash type
								//params1.clear();

								//params1[0] = pRunDate.format(mask : dateFormat);//Not Used

								// pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
								//params1[1] = pRunDate.format(mask : dateFormat);//Not Used

								// pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
								// params1[2] = pRunDate.format(mask : dateFormat);  //pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
								// 	params1[3] = pRunDate.format(mask : dateFormat);   //pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
								// 	params1[4] = pRunDate.format(mask : dateFormat);   //pRunUnauth.format(mask : dateFormat);  //DRCAP Phase 2 Changes //
								//params1[2] = ptpId;//Not Used
			                    /*                     String sqlCommandCSV = "select y.id ptp_id,  y.name ptp_name, y.version ptp_version, nvl(sum(x.amount),0) amount                       " +
			                                                            "                                        from                                                                                                                                                                                                                                                                                                                                                                                                                        " +
			                                                            "                                        (                                                                                                                                                                                                                                                                                                                                                                                                                           " +
			                                                            "                                        select ptp.id ptp_id, ptp.name, ptp.version, csv.amount                                                                                                                                                                                                                                                                                                                                                                     " +
			                                                            "                                        from nem.nem_credit_support csv,                                                                                                                                                                                                                                                                                                                                                                                            " +
			                                                            "                                        nem.nem_participants ptp,                                                                                                                                                                                                                                                                                                                                                                                                   " +
			                                                            "                                        nem.nem_credit_support_types csvtype                                                                                                                                                                                                                                                                                                                                                                                        " +
			                                                            "                                        where csv.ptp_id = ptp.id                                                                                                                                                                                                                                                                                                                                                                                                   " +
			                                                            "                                        and ptp.VERSION =                                                                                                                                                                                                                                                                                                                                                                                                           " +
			                                                            "                                                           nem.pav$packaging.get_curr_version_pkt ('STANDING',                                                                                                                                                                                                                                                                                                                                                      " +
			                                                            "                                                                                                   TO_DATE (?, 'YYYY-MM-DD'),                                                                                                                                                                                                                                                                                                                                       " +
			                                                            "                                                                                                   NULL)                                                                                                                                                                                                                                                                                                                                                            " +
			                                                            "                                        and csv.CREDIT_SUPPORT_TYPE_ID = csvtype.id                                                                                                                                                                                                                                                                                                                                                                                 " +
			                                                            "                                        and  trunc(sysdate) between csv.start_date and decode(csvtype.name, 'Cash Deposit', (nvl(csv.end_date,  TO_DATE ('31-Dec-3000', 'DD-MON-YYYY')))-1,nvl(csv.end_date,  TO_DATE ('31-Dec-3000', 'DD-MON-YYYY')) )                                                                                                                                                                                                             " +
			                                                            "                                        and csv.version in                                                                                                                                                                                                                                                                                                                                                                                                          " +
			                                                            "                                        (                                                                                                                                                                                                                                                                                                                                                                                                                           " +
			                                                            "                                        select max(to_number(csvin.version)) from nem.nem_credit_support csvin,  nem.nem_credit_support_types csvtypein where csvin.ptp_id =csv.ptp_id and  csvin.CREDIT_SUPPORT_TYPE_ID =csvtypein.id and trunc(sysdate) between csvin.start_date and decode(csvtypein.name, 'Cash Deposit', (nvl(csvin.end_date,  TO_DATE ('31-Dec-3000', 'DD-MON-YYYY')))-1,nvl(csvin.end_date,  TO_DATE ('31-Dec-3000', 'DD-MON-YYYY')) )       " +
			                                                            "                                        and csvin.start_date = csv.start_date and nvl(csvin.end_date,  TO_DATE ('31-Dec-3000', 'DD-MON-YYYY')) = nvl(csv.end_date,  TO_DATE ('31-Dec-3000', 'DD-MON-YYYY')) and csvin.sac_id is null                                                                                                                                                                                                                                " +
			                                                            "                                        and csvin.CREDIT_SUPPORT_TYPE_ID = csv.CREDIT_SUPPORT_TYPE_ID and csvin.id = csv.id                                                                                                                                                                                                                                                                                                                                         " +
			                                                            "                                        union all                                                                                                                                                                                                                                                                                                                                                                                                                   " +
			                                                            "                                        select max(to_number(csvin.version)) from nem.nem_credit_support csvin,  nem.nem_credit_support_types csvtypein where csvin.sac_id =csv.sac_id and  csvin.CREDIT_SUPPORT_TYPE_ID =csvtypein.id and  trunc(sysdate) between csvin.start_date and decode(csvtypein.name, 'Cash Deposit', (nvl(csvin.end_date,  TO_DATE ('31-Dec-3000', 'DD-MON-YYYY')))-1,nvl(csvin.end_date,  TO_DATE ('31-Dec-3000', 'DD-MON-YYYY')) )      " +
			                                                            "                                        and csvin.start_date = csv.start_date and nvl(csvin.end_date,  TO_DATE ('31-Dec-3000', 'DD-MON-YYYY')) = nvl(csv.end_date,  TO_DATE ('31-Dec-3000', 'DD-MON-YYYY')) and csvin.sac_id is not null                                                                                                                                                                                                                            " +
			                                                            "                                        and csvin.CREDIT_SUPPORT_TYPE_ID = csv.CREDIT_SUPPORT_TYPE_ID and csvin.id = csv.id                                                                                                                                                                                                                                                                                                                                         " +
			                                                            "                                        )                                                                                                                                                                                                                                                                                                                                                                                                                           " +
			                                                            "                                                              and csv.lock_version = 1 and csv.approval_status = 'A'                                                                                                                                                                                                                                                                                                                                                " +
			                                                            "                                        ) x,                                                                                                                                                                                                                                                                                                                                                                                                                        " +
			                                                            "                                        nem.nem_participants  y                                                                                                                                                                                                                                                                                                                                                                                                     " +
			                                                            "                                        where X.PTP_ID (+) = y.id                                                                                                                                                                                                                                                                                                                                                                                                   " +
			                                                            "                                            and     x.version  (+) = y.version                                                                                                                                                                                                                                                                                                                                                                                      " +
			                                                            "                                            and y.version =  nem.pav$packaging.get_curr_version_pkt ('STANDING',                                                                                                                                                                                                                                                                                                                                                    " +
			                                                            "                                                                                                    TO_DATE (?, 'YYYY-MM-DD'),                                                                                                                                                                                                                                                                                                                                      " +
			                                                            "                                                                                                   NULL)                                                                                                                                                                                                                                                                                                                                                            " +
			                                                            "                                            and y.id = ? " +
			                                                            "                                        group by   y.id,  y.name, y.version  ";

			                                                            // logger.info(logPrefix + " Sql Command CSV  " +sqlCommandCSV);
			                                                            foreach (row2 in DynamicSQL.executeQuery(sentence : sqlCommandCSV,
			                                                                                                     implname : dbpath, inParameters : params1)) {
			                                                                csvTotal = Decimal.valueOf(value : row2[4]);
			                                                            } */
								// Round the string to 2 decimal places
								ene = csvTotal;
								creditSupportValue = String.valueOf(ene);

								// logger.info "[EMC] Credit Support Value: " + creditSupportValue
								//params1.clear();

								// DRSAT-523, get credit or debit
								//params1[0] = ptpId;//Not Used
								//params1[1] = settRunId;//Not Used

								// p_str_id
								String creditDebit = "";
			                    /*                     String sqlDebitCredit = "select DECODE(SIGN(sum(nas.column_5)), -1, 'DEBIT', 'CREDIT') ptp_debit_credit " +
			                                                            "FROM nem.nem_account_statements nas, nem.nem_settlement_accounts sac " +
			                                                            "WHERE     nas.report_section = 'MAINTOTAL' " +
			                                                            "     AND nas.column_1 = 'Total Due (Owed)' " +
			                                                            "     AND nas.sac_id = sac.id " +
			                                                            "     AND nas.sac_version = sac.version " +
			                                                            "     AND sac.ptp_id = ? " +
			                                                            "     AND nas.str_id = ? ";

			                                                            foreach (row4 in DynamicSQL.executeQuery(sentence : sqlDebitCredit,
			                                                                                                     implname : dbpath, inParameters : params1)) {
			                                                                creditDebit = String.valueOf(o : row4[1]);
			                                                            } */
								// DRSAT-523 end
								//params1.clear();

								// get the prepayment
								//params1[0] = ptpId;//Not Used
								//params1[1] = creditDebit;//Not Used

								// DRSAT-523
								// params1[1] = pRunDate.format(mask : dateFormat);
								// String sqlCommandppmt = "SELECT NVL(SUM(ppmt.amount),0)  FROM NEM.NEM_PREPAYMENTS ppmt, nem.nem_prepayment_types ppmt_types  "+
								// 					"WHERE ppmt.ptp_id=?   "+
								// 				"AND ppmt.approval_status='A' AND ppmt.lock_version = 1 AND trunc(sysdate) >= ppmt.value_date AND PPMT_TYPES.NAME= 'Preliminary Run'  "+
								// 			"AND ppmt.settlement_date=?  ";
								// Added for Jira - DRSAT307
								// modified for Jira - DRSAT-523, change from credit to debit depending on account statement.
			                    /*                     String sqlCommandppmt = "SELECT NVL(SUM(ppmt.amount),0)  FROM NEM.NEM_PREPAYMENTS ppmt " +
			                                                            " WHERE ppmt.ptp_id= ? " +
			                                                            " AND ppmt.approval_status='A' AND ppmt.lock_version = 1 " +
			                                                            " AND trunc(sysdate) between ppmt.value_date  " +
			                                                            " AND (SELECT MAX(settlement_date) " +
			                                                            " FROM nem.nem_settlement_calendar " +
			                                                            " WHERE settlement_date < NEM.GET_SETT_DEBIT_CREDIT_DUEDATE(ppmt.settlement_date, ?) " +
			                                                            " AND day_type          = 'B') ";

			                                                            foreach (row3 in DynamicSQL.executeQuery(sentence : sqlCommandppmt,
			                                                                                                     implname : dbpath, inParameters : params1)) {
			                                                                prepayment = String.valueOf(o : row3[1]);
			                                                            } */
								// logger.info(logPrefix + " sqlCommandppmt  " );
								// DRCAP PHASE 2 Changes by HE Raghu - Finding all the details for consolidated Risk Exposure
								String X = "";
								int numX = 0;

								// DRCAPWARRANTY-48 fixes start
								if ((Date)row1.get("min_outstanding_invoiced_date") == null) {
									unpaidStartDate = " ";
								}
			                    else {
									Date unpaidStart = (Date)row1.get("min_outstanding_invoiced_date");
									unpaidStartDate = new SimpleDateFormat("dd MMM").format(unpaidStart);
								}

								if ((Date)row1.get("max_outstanding_invoiced_date") == null) {
									unpaidEndDate = " ";
								}
			                    else {
									Date unpaidEnd = (Date)row1.get("max_outstanding_invoiced_date");
									unpaidEndDate = new SimpleDateFormat("dd MMM").format(unpaidEnd);
								}

								if ((Date)row1.get("min_outstand_uninvoiced_date") == null) {
									unInvoicedStartDate = " ";
								}
			                    else {
									Date unInvoicedStart = (Date)row1.get("min_outstand_uninvoiced_date");
									unInvoicedStartDate = new SimpleDateFormat("dd MMM").format(unInvoicedStart);
								}

								if ((Date)row1.get("max_outstand_uninvoiced_date") == null) {
									unInvoicedEndDate = " ";
								}
			                    else {
									Date unInvoicedEnd = (Date)row1.get("max_outstand_uninvoiced_date");
									unInvoicedEndDate = new SimpleDateFormat("dd MMM").format(unInvoicedEnd);
								}

								// 	unpaidStartDate = String.valueOf(o : row1[9]);
								// 	unpaidEndDate = String.valueOf(o : row1[10]);
								if ((BigDecimal)row1.get("INV_AMOUNT") == null || String.valueOf(((BigDecimal)row1.get("INV_AMOUNT"))).equalsIgnoreCase("")) {
									unpaidAmount = "0";
								}
			                    else {
									unpaidAmount = String.valueOf(((BigDecimal)row1.get("INV_AMOUNT")));
								}

								// DRCAPWARRANTY-48 fixes end
								// 	unInvoicedStartDate = String.valueOf(o : row1[12]);
								// 	unInvoicedEndDate = String.valueOf(o : row1[13]);
								if ((BigDecimal)row1.get("UN_INV_AMOUNT") == null || String.valueOf(((BigDecimal)row1.get("UN_INV_AMOUNT"))).equalsIgnoreCase("")) {
									uninvoicedAmount = "0";
								}
			                    else {
			                    	uninvoicedAmount = String.valueOf(((BigDecimal)row1.get("UN_INV_AMOUNT")));
								}

								// currentExposure = String.valueOf(o : row1[15]);
								currentExposure = (BigDecimal)row1.get("CURR_EXP") == null ? currentExposure :(BigDecimal)row1.get("CURR_EXP");
								X = (BigDecimal)row1.get("final_X") == null ? " " : String.valueOf(((BigDecimal)row1.get("final_X")));
								numX = (BigDecimal)row1.get("final_X") == null ? numX : ((BigDecimal)row1.get("final_X")).intValue();

								// adeAmount = String.valueOf(o : row1[17]);
								adeAmount = (BigDecimal)row1.get("final_ADE_for_SD");

								// logger.info "[EMC] Prepayment: " + prepayment
								// ne = currentExposure + (Decimal.valueOf(value : 20 - numX) * adeAmount) - Decimal.valueOf(value : prepayment);
								int formatted_numX = ((20 - numX) * ADE.intValue());
								ne = currentExposure.add(new BigDecimal(formatted_numX)).subtract(new BigDecimal(prepayment));

								// ne = Decimal.valueOf(value : currentExposure) + Decimal.valueOf(value : ((20 - numX) * Decimal.valueOf(value : adeAmount))) - Decimal.valueOf(value : prepayment); // DRSAT-JIRA 298 ENE
								// Round the string to decimal places
								ene = ne;
								netExposure = String.valueOf(ene);

								if (csvTotal.compareTo(BigDecimal.ZERO) > 0) {
									ene = (ne.divide(csvTotal)).multiply(new BigDecimal(100.00));
								}

								if (csvTotal.compareTo(BigDecimal.ZERO) == 0) {
									if ((ne.compareTo(BigDecimal.ZERO) > 0)) {
										ene = new BigDecimal(100.00);
									}

									if (ne.compareTo(BigDecimal.ZERO) <= 0) {
										ene = new BigDecimal(0.00);
									}
								}

								// Rahul set BPM Param ENE_NOTIFICATION_THRESHOLD 60    ENE_MARGIN_CALL_THRESHOLD 70
			                    /*                     if (ene.round(scale : 2) > ENE_MARGIN_CALL_THRESHOLD) {
			                                                                logger.info(logPrefix + " Margin Call will be issued when estimated net exposure >70% of credit support value: " + ene.round(scale : 2) + " Market Participant: " + ptpName);

			                                                                EMC.AlertNotification alert;

			                                                                // alert.content = "Filename: " + fileInfo.filename + ";\n\n" +
			                                                                // "File upload time: " + fileInfo.uploadTime.format(mask : DISPLAY_TIME_FORMAT) + ";\n\n" +
			                                                                // "Settlement Date: " + fileInfoSettlementDateStr + ";\n\n" +
			                                                                // "File upload user: " + fileInfo.uploadUsername + ";\n\n" +
			                                                                // "User Comments: " + fileInfo.comments + ";\n\n" +
			                                                                // "Validated time: " + 'now'.format(mask : DISPLAY_TIME_FORMAT) + ";\n\n" +
			                                                                // "Valid: N;\n\n" +
			                                                                // "Error Message: " + msslException.errorMsg + " line(" + ((String) (msslException.rowNumber)) + "). Event ID: " + eventId;
			                                                                alert.content = " Margin Call will be issued when estimated net exposure >70% of credit support value: " + ene.round(scale : 2) + " For Market Participant: " + ptpName;
			                                                                alert.recipients = DAILY_RUN_SUMMARY_EMAIL;
			                                                                alert.subject = "Margin Call For Market Participant: " + ptpName;
			                                                                alert.noticeType = "Risk Exposure Margin Call and Notification";
			                                                                alert.sendEmail();
			                                                            }
			                                                            else if (ene.round(scale : 2) > ENE_NOTIFICATION_THRESHOLD) {
			                                                                logger.info(logPrefix + " Notification will be issued when estimated net exposure >60% of credit support value: " + ene.round(scale : 2) + " Market Participant: " + ptpName);

			                                                                EMC.AlertNotification alert;
			                                                                alert.content = " Notification will be issued when estimated net exposure >60% of credit support value: " + ene.round(scale : 2) + " For Market Participant: " + ptpName;
			                                                                alert.recipients = DAILY_RUN_SUMMARY_EMAIL;

			                                                                // DRSAT-671
			                                                                // alert.subject = "Margin Call For Market Participant: " + ptpName;
			                                                                alert.subject = "Notification of 60% Risk Exposure For Market Participant: " + ptpName;
			                                                                alert.noticeType = "Risk Exposure Margin Call and Notification";
			                                                                alert.sendEmail();
			                                                            }
			                                                            else {
			                                                                logger.info(logPrefix + " NO Notification or Margin Call as estimated net exposure % of credit support value: " + ene.round(scale : 2) + " For Market Participant: " + ptpName);
			                                                            } */
								// Change request: Risk Exposure Report, ADE Report
								// if (csvTotal == 0) {
								// 		riskExposure = "";
								// 	}
								// 	else {
								// 		riskExposure = String.valueOf(o : ene.round(scale : 2));
								// 	}
								riskExposure =  String.valueOf(ene); //String.valueOf(o : ene.round(scale : 2)); // TODo MUrali - need to check scale value

								// logger.info(logPrefix + " Risk Exposure: " + riskExposure);
								// logger.info(logPrefix + " Updating account " + dispTitle);
								String insertCmd = "INSERT INTO NEM.NEM_SETTLEMENT_PTP_STATEMENTS " +
			                    "(ID,REPORT_SECTION,SEQ,COLUMN_1,COLUMN_2,STR_ID,PTP_ID,PTP_VERSION,SETTLEMENT_DATE) " +
			                    "VALUES ( SYS_GUID(),?,?,?,?,?,?,?,? )";
			                    /*insertParams[0] = "RISKHEADER";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "Market Participant: " + ptpName;
			                    insertParams[3] = "";
			                    insertParams[4] = settRunId;
			                    insertParams[5] = ptpId;
			                    insertParams[6] = version;
			                    insertParams[7] = pRunDate;*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header one " );
			                    /*pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "";
			                    insertParams[3] = "";*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 2 " );
			                    /*pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "Amount invoiced but unpaid at " + dispRunDate +
			                    " (" + unpaidStartDate + "-" + unpaidEndDate + ")";
			                    insertParams[3] = unpaidAmount;*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 3 " );
			                    /*pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "Settlements calculated but not invoiced " +
			                    " (" + unInvoicedStartDate + "-" + unInvoicedEndDate + ")";
			                    insertParams[3] = uninvoicedAmount;*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 4 " );
			                    /*pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "";
			                    insertParams[3] = "";*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 5 " );
			                    /*pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "Current exposure";
			                    insertParams[3] = String.valueOf(currentExposure);*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 6 " );
			                    /*pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "Estimated Net Exposure";
			                    insertParams[3] = netExposure;*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 7 " );
			                   /* pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "Credit Support Value";
			                    insertParams[3] = creditSupportValue;*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 8 " );
			                    /*pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "Risk Exposure against CSV";
			                    insertParams[3] = riskExposure;*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 9 " );
			                    /*pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "";
			                    insertParams[3] = "";*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 10 " );
			                   /* pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "ADE (Average Daily Exposure)";
			                    insertParams[3] = String.valueOf(ADE);*/
								//Not Used

								// String.valueOf(o : adeAmount);	// Use ADE instead of final ade
								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 11 " );
			                    /*pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "X";
			                    insertParams[3] = X;*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 12" );
			                    /*pos = pos + 1;
			                    insertParams[0] = "RISKEXPOSR";
			                    insertParams[1] = String.valueOf(pos);
			                    insertParams[2] = "Prepayment";
			                    insertParams[3] = prepayment;*/
								//Not Used

								// DynamicSQL.execute(sentence : insertCmd, implname : dbpath, inParameters : insertParams);
								// logger.info(logPrefix + " Risk Header 13" );
								pos = pos + 1;
							}

							// SD = Prelim Date End
						}


						// end loop for sqlCommand1
						// This is to generate report for those who has not traded ever //
						logger.info("[EMC] Generating Blank Report for Market Participants Who Never Traded " + ptpName);



						//params1[0] = pRunDate.format(mask : dateFormat);//Not Used

						// params1[1] = pRunDate.format(mask : dateFormat);
						//params1[1] = ptpId;//Not Used

						//            params1[2] = ptpName;
			            /*             String sqlCommandBlank = "select count(*) from pvfo.PVF_RISKEX_PTP_START_SETT_DT_V ptpriskexpdtl " +
			                                    "where TO_DATE (?,'YYYY-MM-DD') >= ptpriskexpdtl.START_TRADING_DATE                              " +
			                                    "and ptp_id =?                                                                                   " +
			                                    "having count(*) = 0";

			                                    foreach (row1 in DynamicSQL.executeQuery(sentence : sqlCommandBlank,
			                                                                             implname : dbpath, inParameters : params1)) {
			                                        // start loop for sqlCommandBlank
			                                        String insertCmdBlank = "INSERT INTO NEM.NEM_SETTLEMENT_PTP_STATEMENTS " +
			                                        "(ID,REPORT_SECTION,SEQ,COLUMN_1,COLUMN_2,STR_ID,PTP_ID,PTP_VERSION,SETTLEMENT_DATE) " +
			                                        "VALUES ( SYS_GUID(),?,?,?,?,?,?,?,? )";
			                                        Any[] insertParamsBlank;
			                                        insertParamsBlank[0] = "RISKHEADER";
			                                        insertParamsBlank[1] = String.valueOf(o : pos);
			                                        insertParamsBlank[2] = "Market Participant: " + ptpName;
			                                        insertParamsBlank[3] = "";
			                                        insertParamsBlank[4] = settRunId;
			                                        insertParamsBlank[5] = ptpId;
			                                        insertParamsBlank[6] = version;
			                                        insertParamsBlank[7] = pRunDate;
			                                        //DynamicSQL.execute(sentence : insertCmdBlank, implname : dbpath,
			                                          //                 inParameters : insertParamsBlank);

			                                        // logger.info(logPrefix + " Risk Header one " );
			                                        pos = pos + 1;
			                                        insertParamsBlank[0] = "RISKEXPOSR";
			                                        insertParamsBlank[1] = String.valueOf(o : pos);
			                                        insertParamsBlank[2] = "";
			                                        insertParamsBlank[3] = "";
			                                        //DynamicSQL.execute(sentence : insertCmdBlank, implname : dbpath,
			                                          //                 inParameters : insertParamsBlank);

			                                        // logger.info(logPrefix + " Risk Header 2 " );
			                                        pos = pos + 1;
			                                        insertParamsBlank[0] = "RISKEXPOSR";
			                                        insertParamsBlank[1] = String.valueOf(o : pos);
			                                        insertParamsBlank[2] = "No Risk Exposure Report is Generated as this MP has not Started to Trade.";
			                                        insertParamsBlank[3] = "";
			                                        //DynamicSQL.execute(sentence : insertCmdBlank, implname : dbpath,
			                                          //                 inParameters : insertParamsBlank);

			                                        pos = pos + 1;
			                                    } */
						// end loop for sqlCommandBlank
						// end //
					}

				}catch (Exception e) {
					logger.info(logPrefix + " Exception: " + e.getMessage());
					e.printStackTrace();
					throw e;
				}


				//CP66
				// DRCAP Phase 2 Changes - Added //
				// DRCAP Phase 2 Changes - Comment - Start //
				try{

					Object[] params3 = new Object[3];

		        	params3[0] = version;
		        	params3[1] = version;
		        	params3[2] = settlement_date.getKey();
					//sqlCommand = "select distinct ptp.ID, ptp.NAME from NEM.NEM_SETTLEMENT_ACCOUNTS SAC, NEM.NEM_PARTICIPANTS PTP " +
					//             "WHERE ptp.VERSION=? and sac.ptp_id = ptp.id  AND sac.ptp_version= ptp.version and sac.version =? and sac.SAC_TYPE NOT IN ('A','T','O') ";

					sqlCommand = "select distinct ptp.ID, ptp.NAME from NEM.NEM_SETTLEMENT_ACCOUNTS SAC, NEM.NEM_PARTICIPANTS PTP,pvfo.PVF_RISKEX_PTP_START_SETT_DT_V std   " +
								"	WHERE ptp.VERSION=?    " +
								"	and sac.ptp_id = ptp.id     " +
								"	AND sac.ptp_version= ptp.version    " +
								"	and sac.version =?   " +
								"	and sac.SAC_TYPE NOT IN ('A','T','O')    " +
								"	and std.ptp_id = ptp.id   " +
								"	and std.start_trading_date >=  (SELECT MIN (settlement_date)      " +
		                        "               FROM (SELECT ROWNUM, settlement_date      " +
		                        "                       FROM (  SELECT *     " +
		                        "                                 FROM nem_settlement_calendar      " +
		                        "                                WHERE settlement_date < (TO_DATE ( ?, 'YYYY-MM-DD') + 1)   " +
		                        "                             ORDER BY settlement_date DESC)   " +
		                        "                      WHERE ROWNUM < 90))";

					List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, params3);
			        for (Map row : list1) {
						// ptp loop start
						ptpId = (String)row.get("ID");

						// DRCAP Phase 2 Changes //
						ptpName = (String) row.get("NAME");

						// DRCAP Phase 2 Changes //
						logger.info(logPrefix + " The iteration started For Market Participant:CP66 " + ptpName);

						// Start CP66
						try {
							Date startTradingDate = null;
							Date cp66EffectiveDate = null;
							BigDecimal forcastDailyGrossWithdrawal = new BigDecimal(00000000000000000000.000).setScale(3);
							BigDecimal forcastDailyGrossInjection = new BigDecimal(00000000000000000000.000).setScale(3);
							BigDecimal gst = new BigDecimal(00000000000000000000.000).setScale(3);
							BigDecimal avgWep = new BigDecimal(00000000000000000000.00).setScale(2);
							BigDecimal avgAfp = new BigDecimal(00000000000000000000.00).setScale(2);
							BigDecimal maximumDailyNetWithdrawal = new BigDecimal(00000000000000000000.000).setScale(3);
							BigDecimal maximumDailyAfpQuantties = new BigDecimal(00000000000000000000.000).setScale(3);
							BigDecimal maximumDailyWeqFigures = new BigDecimal(00000000000000000000.000).setScale(3);
							BigDecimal maximumDailyIeqFigures = new BigDecimal(00000000000000000000.000).setScale(3);
							BigDecimal ade = new BigDecimal(00000000000000000000.00).setScale(2);

							Date currentTime = new Date();
							String ade2SqlCommand;

							String adePtpStatementInputsInsertSqlCommand;

							logger.info(logPrefix + " Start CP66 " + ptpName + ptpId);

							// Start Check the aps_system_parameters
							ade2SqlCommand = " select date_value from APS_SYSTEM_PARAMETERS where name = 'CR_DEFAULT_EFFECTIVE_DATE' and trunc(date_value) <= trunc(sysdate) ";


							List<Map<String, Object>> list2 = jdbcTemplate.queryForList(ade2SqlCommand, new Object[] {});
					        for (Map row1 : list2) {
			                    cp66EffectiveDate = (Date)row1.get("date_value");
			                    break;
			                }

							logger.info(logPrefix + "cp66EffectiveDate: " + cp66EffectiveDate);

							// End Check the aps_system_parameters
							// Check the start_trading_date
							// confirm the boundary "start_trading_date >=  "
							ade2SqlCommand = " select start_trading_date from pvfo.PVF_RISKEX_PTP_START_SETT_DT_V  " +
			                                 " where ptp_id =?  " +
			                                 " and start_trading_date >=  " +
			                                 " (SELECT MIN (settlement_date) " +
			                                 "  FROM (SELECT ROWNUM, settlement_date " +
			                                 "          FROM (  SELECT * " +
			                                 "                    FROM nem_settlement_calendar " +
			                                 "                   WHERE settlement_date < (TO_DATE ( ?, 'YYYY-MM-DD') + 1) " +
			                                 "                ORDER BY settlement_date DESC) " +
			                                 "         WHERE ROWNUM < 90)) ";
							
							Object[] ade2Params = new Object[2];
			                ade2Params[0] = ptpId;
			                ade2Params[1] = settlement_date.getKey();
			                List<Map<String, Object>> list3 = jdbcTemplate.queryForList(ade2SqlCommand, ade2Params);
					        for (Map row1 : list3) {
			                    startTradingDate = (Date)row1.get("start_trading_date");		
			                    break;
			                }

							logger.info(logPrefix + "startTradingDate: " + startTradingDate);

							if (startTradingDate != null && cp66EffectiveDate != null) {
								logger.info(logPrefix + "MP is started < 90 day so going this loop");

								// Calculate for the SettlementDate
								// FORCAST_DAILY_GROSS_WITHDRAWAL
								// FORCAST_DAILY_GROSS_INJECTION
								// MAXIMUM_DAILY_WEQ_FIGURES
								// MAXIMUM_DAILY_IEQ_FIGURES
								// MAXIMUM_DAILY_NET_WITHDRAWAL
								// MAXIMUM_DAILY_AFP_QUANTITIES
								ade2SqlCommand = " select FORECASTDAILYGROSSWITHDRAWAL, FORECASTDAILYGROSSINJECTION from nem_ade_inputs " +
			                                     " where approval_status = 'A' " +
			                                     " and expired_date > sysdate " +
			                                     " and PTP_ID = ? ";

								List<Map<String, Object>> list4 = jdbcTemplate.queryForList(ade2SqlCommand, new Object[] {ptpId});
						        for (Map row1 : list4) {
			                        forcastDailyGrossWithdrawal = (BigDecimal)row1.get("FORECASTDAILYGROSSWITHDRAWAL");
			                        forcastDailyGrossInjection = (BigDecimal)row1.get("FORECASTDAILYGROSSINJECTION");	
			                        break;
			                    }

								// GST
								ade2SqlCommand = "  SELECT value FROM NEM.NEM_GST_CODES WHERE NAME='A7' AND VERSION=? ";

								List<Map<String, Object>> list5 = jdbcTemplate.queryForList(ade2SqlCommand, new Object[] {version});
						        for (Map row1 : list5) {
			                        gst = (BigDecimal)row1.get("value");
			                        break;
			                    }

								logger.info(logPrefix + "Start Rolling 90 day average");

								// Rolling 90 day average
								// COLUMN_3      COLUMN_4        COLUMN_5        COLUMN_6        COLUMN_7        COLUMN_8        COLUMN_9                COLUMN_10           COLUMN_11                   COLUMN_12       COLUMN_13                   COLUMN_14
								// AFP ($/MWh)   MEUC ($/MWh)    USEP ($/MWh)    HEUC ($/MWh)    HEUR ($/MWh)    HLCU ($/MWh)    EMC Admin ($/MWh)       PSO Admin ($/MWh)   Wholesale Price ($/MWh)     VCRP ($/MWh)    EMC Price Adj Fees ($/MWh)  EMC Price Cap Fees ($/MWh)
								// USEP + HEUC + MEUC + PSO Fee + EMC Fee (EMC Admin)(  EMC Price Cap Fees + EMC Price Adj Fees)
								ade2SqlCommand = "select round(avg(DAILY_WEP),2) WEP,round(avg(DAILY_AFP),2) AFP	"+
												"	from(	"+
												"	SELECT  settlement_date,run_type,round(avg(COLUMN_5+COLUMN_6+COLUMN_4+COLUMN_10 + COLUMN_9)  over (partition by settlement_date),2) DAILY_WEP	"+
												"	, round(avg(column_3)  over (partition by settlement_date),2)  DAILY_AFP	"+
												"                                     FROM NEM_RUN_STATEMENTS nrs, (SELECT str.ID, STR.SETTLEMENT_DATE,run_type 	"+
												"                                     FROM nem.nem_settlement_runs str, 	"+
												"                                     nem.nem_package_authorisations pka 	"+
												"                                     WHERE     str.run_type IN ('F', 'P') 	"+
												"                                                    AND settlement_date BETWEEN (SELECT MIN ( 	"+
												"                                                                                           settlement_date) 	"+
												"                                                                                   FROM (SELECT ROWNUM, 	"+
												"                                                                                                settlement_date 	"+
												"                                                                                           FROM (  SELECT * 	"+
												"                                                                                                     FROM nem_settlement_calendar 	"+
												"                                                                                                    WHERE settlement_date < 	"+
												"                                                                                                             (  TO_DATE ( 	"+
												"                                                                                                                   ?, 	"+
												"                                                                                                                   'YYYY-MM-DD') 	"+
												"                                                                                                              + 1) 	"+
												"                                                                                                 ORDER BY settlement_date DESC) 	"+
												"                                                                                          WHERE ROWNUM < 	"+
												"                                                                                                   91)) 	"+
												"                                                                            AND TO_DATE ( 	"+
												"                                                                                   ?, 	"+
												"                                                                                   'YYYY-MM-DD') 	"+
												"                                                    AND pka.pkg_id = str.pkg_id 	"+
												"                                                    AND pka.authorisation_status IN ('AUTHORISED','1ST AUTHORISED', "+
												"                                                                                     'WAITING') 	"+
												"                                                    AND pka.authorisation_date = 	"+
												"                                                           (SELECT MAX (pka1.authorisation_date) 	"+
												"                                                              FROM nem.nem_package_authorisations pka1 	"+
												"                                                             WHERE pka1.pkg_id = pka.pkg_id) 	"+
												"                                                    AND str.run_date = 	"+
												"                                                           (SELECT MAX (str1.run_date) 	"+
												"                                                              FROM nem.nem_settlement_runs str1 	"+
												"                                                             WHERE     str1.settlement_date = 	"+
												"                                                                          str.settlement_date 	"+
												"                                                                   AND str1.run_type IN ('P', 'F'))) nsr	"+
												"                                     WHERE     nrs.report_Section IN ('MWP') 	"+
												"                                     AND nrs.str_id =nsr.id	"+
												"                                     order by settlement_date	"+
												"	) ";

								Object[] ade2Params1 = new Object[2];
			                    ade2Params1[0] = settlement_date.getKey();
			                    ade2Params1[1] = settlement_date.getKey();			
			                    List<Map<String, Object>> list6 = jdbcTemplate.queryForList(ade2SqlCommand, ade2Params1);
						        for (Map row1 : list6) {
			                        avgWep = (BigDecimal)row1.get("WEP");
			                        avgAfp = (BigDecimal)row1.get("AFP");
			                        break;
			                    }

								logger.info(logPrefix + "Start Maximum Daily");

								// Maximum Daily Net Withdrawal, Maximum Daily AFP Quantity,Maximum Daily WEQ Figures,Maximum Daily IEQ Figures
								ade2SqlCommand =
				                    		" SELECT sac_date_dtl.ptp_id,    "+
											"        dailyNetWithdrawal,    "+
											"        dailyAfpQuantties,    "+
											"        dailyWeqFigures,    "+
											"        dailyIeqFigures,    "+
											"        sac_date_dtl.Settlement_date    "+
											"    FROM nem.nem_settlement_runs str,    "+
											"         (SELECT inner_dtl.PTP,    "+
											"                 inner_dtl.dailyNetWithdrawal,    "+
											"                 inner_dtl.dailyAfpQuantties,    "+
											"                 inner_dtl.dailyWeqFigures,    "+
											"                 inner_dtl.dailyIeqFigures,    "+
											"                 inner_dtl.STR_ID,    "+
											"                 inner_dtl.Settlement_date,    "+
											"                 inner_dtl.ptp_id,    "+
											"                 inner_dtl.ptp_version    "+
											"            FROM (  SELECT ptp.name PTP,    "+
											"                           SUM (NVL (nas.column_8, 0) - NVL (nas.column_3, 0)) dailyNetWithdrawal,    "+
											"                           SUM (NVL (nas.column_8, 0) + ABS (NVL (nas.column_3, 0))) dailyAfpQuantties,    "+
											"                           SUM (NVL (nas.column_8, 0)) dailyWeqFigures,    "+
											"                           SUM (NVL (nas.column_3, 0)) dailyIeqFigures,    "+
											"                           nas.str_id,    "+
											"                           nas.settlement_date,    "+
											"                           sac.ptp_id,    "+
											"                           sac.ptp_version,    "+
											"                           STDATE.START_TRADING_DATE START_TRADING_DATE    "+
											"                      FROM nem.nem_account_statements nas,    "+
											"                           nem.nem_settlement_accounts sac,    "+
											"                           nem.nem_participants ptp,    "+
											"                           pvfo.PVF_RISKEX_PTP_START_SETT_DT_V stdate    "+
											"                     WHERE     report_section = 'GENLOAD'    "+
											"                           AND (column_2 <> 'Total' OR column_2 IS NULL)    "+
											"                           AND nas.sac_id = sac.id    "+
											"                           AND nas.sac_version = sac.version    "+
											"                           AND sac.ptp_id = ptp.id    "+
											"                           AND sac.ptp_version = ptp.version    "+
											"                           AND ptp.id = stdate.ptp_id    "+
											"                           AND ptp.id = ?    "+
											"                           AND nas.settlement_date >=    "+
											"                                  (CASE    "+
											"                                      WHEN STDATE.START_TRADING_DATE =    "+
											"                                              '01-Jan-2014'    "+
											"                                      THEN    "+
											"                                         (  TO_DATE ( ?,    "+
											"                                                     'YYYY-MM-DD')    "+
											"                                          - 91)    "+
											"                                      WHEN (  TO_DATE ( ?,    "+
											"                                                       'YYYY-MM-DD')    "+
											"                                            - (STDATE.START_TRADING_DATE - 1)) >=    "+
											"                                              90    "+
											"                                      THEN    "+
											"                                         (  TO_DATE ( ?,    "+
											"                                                     'YYYY-MM-DD')    "+
											"                                          - 91)    "+
											"                                      ELSE    "+
											"                                         STDATE.START_TRADING_DATE    "+
											"                                   END)    "+
											"                           AND (nas.str_id, nas.settlement_date) IN (SELECT str.ID,    "+
											"                                                                            str.settlement_date    "+
											"                                                                       FROM nem.nem_settlement_runs str,    "+
											"                                                                            nem.nem_package_authorisations pka    "+
											"                                                                      WHERE     str.run_type IN ('F',    "+
											"                                                                                                 'P')    "+
											"                                                                            AND settlement_date BETWEEN (SELECT MIN (    "+
											"                                                                                                                   settlement_date)    "+
											"                                                                                                           FROM (SELECT ROWNUM,    "+
											"                                                                                                                        settlement_date    "+
											"                                                                                                                   FROM (  SELECT *    "+
											"                                                                                                                             FROM nem_settlement_calendar    "+
											"                                                                                                                            WHERE settlement_date <    "+
											"                                                                                                                                     (  TO_DATE (    "+
											"                                                                                                                                           ?,    "+
											"                                                                                                                                           'YYYY-MM-DD')    "+
											"                                                                                                                                      + 1)    "+
											"                                                                                                                         ORDER BY settlement_date DESC)    "+
											"                                                                                                                  WHERE ROWNUM <    "+
											"                                                                                                                           90))    "+
											"                                                                                                    AND TO_DATE (    "+
											"                                                                                                           ?,    "+
											"                                                                                                           'YYYY-MM-DD')    "+
											"                                                                            AND pka.pkg_id =    "+
											"                                                                                   str.pkg_id    "+
											"                                                                            AND pka.authorisation_status IN ('AUTHORISED',    "+
											"                                                                                                             '1ST AUTHORISED',    "+
											"                                                                                                             'WAITING')    "+
											"                                                                            AND pka.authorisation_date =    "+
											"                                                                                   (SELECT MAX (    "+
											"                                                                                              pka1.authorisation_date)    "+
											"                                                                                      FROM nem.nem_package_authorisations pka1    "+
											"                                                                                     WHERE pka1.pkg_id =    "+
											"                                                                                              pka.pkg_id)    "+
											"                                                                            AND str.run_date =    "+
											"                                                                                   (SELECT MAX (    "+
											"                                                                                              str1.run_date)    "+
											"                                                                                      FROM nem.nem_settlement_runs str1    "+
											"                                                                                     WHERE     str1.settlement_date =    "+
											"                                                                                                  str.settlement_date    "+
											"                                                                                           AND str1.run_type IN ('P',    "+
											"                                                                                                                 'F')    "+
											"                                                                                           AND str1.ID NOT IN (SELECT STR_ID FROM NEM.NEM_TEST_SETTLEMENT_RUNS_DTL)    "+
											"                                                                                   ))    "+
											"                  GROUP BY ptp.name,    "+
											"                           nas.str_id,    "+
											"                           nas.settlement_date,    "+
											"                           sac.ptp_id,    "+
											"                           sac.ptp_version,    "+
											"                           STDATE.START_TRADING_DATE) inner_dtl) sac_date_dtl    "+
											"   WHERE     sac_date_dtl.settlement_date = str.settlement_date    "+
											"         AND sac_date_dtl.str_id = str.id    "+
											"         AND sac_date_dtl.ptp_id = ?  ";

								BigDecimal tmpMaximumDailyNetWithdrawal = new BigDecimal(00000000000000000000.000);
								BigDecimal tmpMaximumDailyAfpQuantties = new BigDecimal(00000000000000000000.000);
								BigDecimal tmpMaximumDailyWeqFigures = new BigDecimal(00000000000000000000.000);
								BigDecimal tmpMaximumDailyIeqFigures = new BigDecimal(00000000000000000000.000);
								int tmpIndex= 0 ;

								Object[] ade2Params2 = new Object[7];
			                	ade2Params2[0] = ptpId;
			                	ade2Params2[1] = settlement_date.getKey();
			                	ade2Params2[2] = settlement_date.getKey();           	                    
			                	ade2Params2[3] = settlement_date.getKey();       	                         	                    
			                	ade2Params2[4] = settlement_date.getKey();                            
			                	ade2Params2[5] = settlement_date.getKey();		                                     
			                	ade2Params2[6] = ptpId;
			                	List<Map<String, Object>> list7 = jdbcTemplate.queryForList(ade2SqlCommand, ade2Params2);
						        for (Map row1 : list7) {
									tmpMaximumDailyNetWithdrawal = (BigDecimal)row1.get("dailyNetWithdrawal");
									tmpMaximumDailyAfpQuantties = (BigDecimal)row1.get("dailyAfpQuantties");
									tmpMaximumDailyWeqFigures = (BigDecimal)row1.get("dailyWeqFigures");
									tmpMaximumDailyIeqFigures = (BigDecimal)row1.get("dailyIeqFigures");

									if(tmpIndex==0){
										maximumDailyNetWithdrawal = tmpMaximumDailyNetWithdrawal;
										maximumDailyAfpQuantties = tmpMaximumDailyAfpQuantties;
										maximumDailyWeqFigures = tmpMaximumDailyWeqFigures;
										maximumDailyIeqFigures = tmpMaximumDailyIeqFigures;
									}else {
										if(tmpMaximumDailyNetWithdrawal.compareTo(maximumDailyNetWithdrawal) > 0 ){
											maximumDailyNetWithdrawal = tmpMaximumDailyNetWithdrawal;
										}

										if(tmpMaximumDailyAfpQuantties.compareTo(maximumDailyAfpQuantties) > 0){
											maximumDailyAfpQuantties = tmpMaximumDailyAfpQuantties;
										}

										if(tmpMaximumDailyWeqFigures.compareTo(maximumDailyWeqFigures) > 0){
											maximumDailyWeqFigures = tmpMaximumDailyWeqFigures;
										}

										if(tmpMaximumDailyIeqFigures.compareTo(maximumDailyIeqFigures) > 0){
											maximumDailyIeqFigures = tmpMaximumDailyIeqFigures;
										}
									}

									//TODO remove
									//logger.info(logPrefix + "Start calculation. Settlement Date:"+ade2SqlCommandRow[6]);

									tmpIndex = tmpIndex +1;
								}

								logger.info(logPrefix + "Start calculation");

								// ADE
								if(maximumDailyNetWithdrawal == null)
									maximumDailyNetWithdrawal = new BigDecimal(00000000000000000000.00000);
								
								BigDecimal tmp1 = maximumDailyNetWithdrawal;
									
								//tmp1.setScale(5);

								if ((forcastDailyGrossWithdrawal.subtract(forcastDailyGrossInjection)).compareTo(maximumDailyNetWithdrawal) > 0) {
									tmp1 = forcastDailyGrossWithdrawal.subtract(forcastDailyGrossInjection);
								}

								if(maximumDailyAfpQuantties == null)
									maximumDailyAfpQuantties = new BigDecimal(00000000000000000000.00000);
								
								BigDecimal tmp2 = maximumDailyAfpQuantties;
								//tmp2.setScale(5);

								if ((forcastDailyGrossWithdrawal.add(forcastDailyGrossInjection)).compareTo(maximumDailyAfpQuantties) > 0) {
									tmp2 = forcastDailyGrossWithdrawal.add(forcastDailyGrossInjection);
								}

								//ade = (1 + gst) * avgWep * tmp1 + (1 + gst) * avgAfp * tmp2;
								ade = ((new BigDecimal(1.000).add(gst)).multiply(avgWep).multiply(tmp1)).add((new BigDecimal(1.0).add(gst)).multiply(avgAfp).multiply(tmp2));

								logger.info(logPrefix + "ptpId= " + ptpId);

								logger.info(logPrefix + "ptpName= " + ptpName);

								logger.info(logPrefix + "version= " + version);

								logger.info(logPrefix + "startTradingDate= " + startTradingDate);

								logger.info(logPrefix + "settlement_date= " + settlement_date);

								logger.info(logPrefix + "cp66EffectiveDate= " + cp66EffectiveDate);

								logger.info(logPrefix + "gst= " + gst);

								logger.info(logPrefix + "avgWep= " + avgWep);

								logger.info(logPrefix + "avgAfp= " + avgAfp);

								logger.info(logPrefix + "forcastDailyGrossWithdrawal= " + forcastDailyGrossWithdrawal);

								logger.info(logPrefix + "forcastDailyGrossInjection= " + forcastDailyGrossInjection);

								logger.info(logPrefix + "maximumDailyNetWithdrawal= " + maximumDailyNetWithdrawal);

								logger.info(logPrefix + "maximumDailyAfpQuantties= " + maximumDailyAfpQuantties);

								logger.info(logPrefix + "tmp1= " + tmp1);

								logger.info(logPrefix + "tmp2= " + tmp2);

								logger.info(logPrefix + "maximumDailyWeqFigures= " + maximumDailyWeqFigures);

								logger.info(logPrefix + "maximumDailyIeqFigures= " + maximumDailyIeqFigures);

								logger.info(logPrefix + "ade= " + ade);

								// Insert to statement table
								adePtpStatementInputsInsertSqlCommand = " INSERT INTO NEM.NEM_ADE_PTP_STATEMENTS (STR_ID,PTP_ID,PTP_VERSION,SETTLEMENT_DATE,CREATED_DATE,ADE,NINETY_DAY_AVG_WEP,NINETY_DAY_AVG_AFP,FORCAST_DAILY_GROSS_WITHDRAWAL,FORCAST_DAILY_GROSS_INJECTION,MAXIMUM_DAILY_WEQ_FIGURES,MAXIMUM_DAILY_IEQ_FIGURES,MAXIMUM_DAILY_NET_WITHDRAWAL,MAXIMUM_DAILY_AFP_QUANTITIES) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,? ,?) ";

								Object[] adePtpStatementInputsInsertParams = new Object[14];
			                    adePtpStatementInputsInsertParams[0] = settRunId;
			                    adePtpStatementInputsInsertParams[1] = ptpId;
			                    adePtpStatementInputsInsertParams[2] = version;
			                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd"); 
			                    adePtpStatementInputsInsertParams[3] = new java.sql.Date((sdf1.parse(settlement_date.getKey())).getTime());
			                    adePtpStatementInputsInsertParams[4] = utilityFunctions.convertUDateToSDate(currentTime);
			                    adePtpStatementInputsInsertParams[5] = String.valueOf(ade);
			                    adePtpStatementInputsInsertParams[6] = String.valueOf(avgWep);
			                    adePtpStatementInputsInsertParams[7] = String.valueOf(avgAfp);
			                    adePtpStatementInputsInsertParams[8] = String.valueOf(forcastDailyGrossWithdrawal);
			                    adePtpStatementInputsInsertParams[9] = String.valueOf(forcastDailyGrossInjection);
			                    adePtpStatementInputsInsertParams[10] = String.valueOf(maximumDailyWeqFigures);
			                    adePtpStatementInputsInsertParams[11] = String.valueOf(maximumDailyIeqFigures);
			                    adePtpStatementInputsInsertParams[12] = String.valueOf(maximumDailyNetWithdrawal);
			                    adePtpStatementInputsInsertParams[13] = String.valueOf(maximumDailyAfpQuantties);
			                    jdbcTemplate.update(adePtpStatementInputsInsertSqlCommand, adePtpStatementInputsInsertParams);
								logger.info(logPrefix + "End Insert");
							}
						}
			            catch (Exception ex) {
							logger.info(logPrefix + " Exception in ADE calculation: " + ex.getMessage());

							throw ex;
						}
						// End CP66
					}

				}catch (Exception e) {
					logger.info(logPrefix + " ExceptionCP66: " + e.getMessage());

					throw e;
				}

				// ptp loop end
				logger.info(logPrefix + " Create Risk Report finished.");
			}

			
			// pRunList loop end
			if (isSchedule == true) {
				// RM#433 Risk Exp Changes - calling Schedule Risk Run via NEMSCAP Service Call
				// for Schedule Run
				// Added by Rupesh
				logger.info(logPrefix + " Calling startREXRunByRunDate.........");

				// RexInternalService.startREXRunByRunDate( utilityFunctions.getddMMyyyy(new
				// Date()), "Schedule Run - No Action required", "S", "SYSTEM");
				RexInternalService_Service service = new RexInternalService_Service();
				RexInternalService rexInternalServicesSoapHttpPort = service.getRexInternalServiceSoapHttpPort();
				rexInternalServicesSoapHttpPort.startREXRunByRunDate(utilityFunctions.dateToString(new Date()),
						"Schedule Run - No Action required", "S", "SYSTEM");

				logger.info(logPrefix + " Calling startREXRunByRunDate finished...");
			}

		}catch(Exception e) {
			e.printStackTrace();
			logger.error(logPrefix + "Accounts in Rerun is not valid. " + e.getMessage());
			//errAlert = e.getMessage();//TODO MURALI

			//valid = false;//TODO MURALI
		}

	}
	
   	@Transactional
   	public void updateEvent(Map<String, Object> variableMap) {

		String eveId = (String) variableMap.get("eveId");


		String msgStep = "ScheduledRiskExposureVerification.updateEvent()";
		logger.info("Input parameters "+msgStep+" - valid :" + true + " eveId :" + eveId);
		try {
			utilityFunctions.updateJAMEvent(true, eveId);
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					logPrefix + msgStep + e.getMessage());
		}

	}
   
}
