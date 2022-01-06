/*
 * Copyright (c) 2018 Energy Market Company Pte. Ltd.
 * 
 * All rights reserved
 * RunResultGenerator.java
 * Version:
 *   0.1 2018/03/01
 * 
 * Revisions:
 *   Converted from Branch 4 BPM codes
 */
package com.emc.sett.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.sett.common.SettlementResultType;
import com.emc.sett.common.SettlementRunException;
import com.emc.sett.impl.*;
import com.emc.sett.impl.comparators.CompareAccount;
import com.emc.sett.impl.comparators.CompareAdjustment;
import com.emc.sett.impl.comparators.CompareBrq;
import com.emc.sett.impl.comparators.CompareCnmea;
import com.emc.sett.impl.comparators.CompareCnmeaSettRe;
import com.emc.sett.impl.comparators.CompareFacility;
import com.emc.sett.impl.comparators.CompareFsc;
import com.emc.sett.impl.comparators.CompareFtr;
import com.emc.sett.impl.comparators.CompareMarket;
import com.emc.sett.impl.comparators.CompareMnmea;
import com.emc.sett.impl.comparators.CompareMnmeaSub;
import com.emc.sett.impl.comparators.CompareParticipant;
import com.emc.sett.impl.comparators.ComparePeriod;
import com.emc.sett.impl.comparators.CompareRerun;
import com.emc.sett.impl.comparators.CompareReserve;
import com.emc.sett.impl.comparators.CompareRsvClass;
import com.emc.sett.impl.comparators.CompareTvc;
import com.emc.sett.impl.comparators.CompareVesting;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;

/*
 * 
 * @author Tse Hing Chuen
 */
public class RunResultGenerator {

    private static final Logger logger = LoggerFactory.getLogger(RunResultGenerator.class);
    
    private DataSource ds = null;
    private Connection conn = null;
	
//    private static SimpleDateFormat qdf = new SimpleDateFormat("dd-MMM-yyyy");
    private final String commonSqlCmd = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, COLUMN_16, COLUMN_17, COLUMN_18, COLUMN_19, COLUMN_20, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) " +
    "VALUES ( SYS_GUID(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
    
    private static final String DATETIME_FORMAT_STR = "dd-MMM-yyyy";
    
    private SettRunPkg runPackage = null;
    private SettlementRunParams params = null;
    private SettlementData data = null;
    
    private List<Account> sortAccount = new ArrayList<>();
    private List<Rerun> sortRerun = new ArrayList<>();
    private List<Adjustment> sortAdjustment = new ArrayList<>();
    private List<Cnmea> sortAdjustTotal = new ArrayList<>();
    private List<Period> sortInterval = new ArrayList<>();
    private List<Market> sortMarket = new ArrayList<>();
    private List<Facility> sortNode = new ArrayList<>();
    private List<Reserve> sortReserve = new ArrayList<>();
    private List<RsvClass> sortClass = new ArrayList<>();
    private List<Ftr> sortFtr = new ArrayList<>();
    private List<Vesting> sortVesting = new ArrayList<>();
    private List<Mnmea> sortMnmea = new ArrayList<>();
    private List<MnmeaSub> sortMnmeasub = new ArrayList<>();
    private List<CnmeaSettRe> sortCnmeaSettRe = new ArrayList<>();
    private List<Brq> sortBRQ = new ArrayList<>();
    private List<Tvc> sortTvc = new ArrayList<>();
    private List<Fsc> sortForwardSales = new ArrayList<>();
    private List<Participant> sortMp = new ArrayList<>();
    private String runEventId;
    private int seq = 0;		// sequence number for NEM_ACCOUNT_STATEMENTS
    private HashMap<String, Boolean> generatorAccounts = new HashMap<>();
    private String logPrefix;
    private Date sqlSettDate;
    private String settDate;
    private HashMap<String, Boolean> embeddedGenerators = new HashMap<>();
    private String settRunId;
    private String emcAdm;
    private String psoAdm;
    private String totalMW;
    private String cmwhEmcAdm;
    private String cmwhPsoAdm;
    private String sacVersion;
    private String userId;
    private String standingVersion;
    private String settRunType;
    private String emcAdmStr;
    private String psoAdmStr;
    private String totalGmee;
    private String totalGmef;
    private String totalLmee;
    private String totalLmef;
    private String totalNmea;
    private BigDecimal inputGst;
    private String emcadmPriceCap;
    private String emcadmPriceAdjRate;
    private String emcadmPriceAdjRateRound;
    private HashMap<String, Facility> sortNode1 = new HashMap<>();
    private HashMap<String, Vesting> sortVesting1 = new HashMap<>();
    private HashMap<String, Fsc> sortForwardSales1 = new HashMap<>();
    private HashMap<String, Integer> storedSeq = new HashMap<>();
    private HashMap<String, NonPeriodCharges> npcTotal = new HashMap<>();

    public RunResultGenerator(String logPrefix, String runEventId) {
    	this.logPrefix = logPrefix;
    	this.runEventId = runEventId;
    }
    
    public void startGeneration(DataSource ds) throws Exception {
    	
		this.ds = ds;
		this.conn = ds.getConnection();
//		this.conn.setAutoCommit(false);
    }
    
    public void endGeneration() throws Exception {
    	
		if (this.conn != null) {
//			this.conn.commit();
			this.conn.close();
		}
    }
    
    /*
     * Formerly readCSVFiles()
     */
	public void readSettlementData(SettRunPkg runPackage, SettlementRunParams params, SettlementData data)
		throws Exception {
		
		String msgStep = "RunResultGenerator.readSettlementData()";
		
		this.runPackage = runPackage;
		this.params = params;
		this.data = data;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");
			
			sortAccount = new ArrayList<Account>();
			sortAccount.addAll(data.getAccount().values());
		    Collections.sort(sortAccount, new CompareAccount());
		    
		    sortRerun = new ArrayList<Rerun>();
		    sortRerun.addAll(data.getRerun().values());
		    Collections.sort(sortRerun, new CompareRerun());
		    
		    sortAdjustment = new ArrayList<Adjustment>();
		    sortAdjustment.addAll(data.getAdjustment().values());
		    Collections.sort(sortAdjustment, new CompareAdjustment());
		    
		    sortAdjustTotal = new ArrayList<Cnmea>();
		    sortAdjustTotal.addAll(data.getCnmea().values());
		    Collections.sort(sortAdjustTotal, new CompareCnmea());
		    
			sortInterval = new ArrayList<Period>();
			sortInterval.addAll(data.getPeriod().values());
		    Collections.sort(sortInterval, new ComparePeriod());
		    
		    sortMarket = new ArrayList<Market>();
		    sortMarket.addAll(data.getMarket().values());
		    Collections.sort(sortMarket, new CompareMarket());
		    
		    sortNode = new ArrayList<Facility>();
		    sortNode.addAll(data.getFacility().values());
		    Collections.sort(sortNode, new CompareFacility());
		    for (Facility nde : sortNode) {
		    	sortNode1.put(nde.getAccountId(), nde);
		    }
		    
		    sortReserve = new ArrayList<Reserve>();
		    sortReserve.addAll(data.getReserve().values());
		    Collections.sort(sortReserve, new CompareReserve());
		    
		    sortClass = new ArrayList<RsvClass>();
		    sortClass.addAll(data.getRsvClass().values());
		    Collections.sort(sortClass, new CompareRsvClass());
		    
		    sortFtr = new ArrayList<Ftr>();
		    sortFtr.addAll(data.getFtr().values());
		    Collections.sort(sortFtr, new CompareFtr());
		    
		    sortVesting = new ArrayList<Vesting>();
		    sortVesting.addAll(data.getVesting().values());
		    Collections.sort(sortVesting, new CompareVesting());
		    for (Vesting v : sortVesting) {
		    	sortVesting1.put(v.getAccountId(), v);
		    }
		    
		    sortForwardSales = new ArrayList<Fsc>();
		    sortForwardSales.addAll(data.getFsc().values());
		    Collections.sort(sortForwardSales, new CompareFsc());
		    for (Fsc f : sortForwardSales) {
		    	sortForwardSales1.put(f.getAccountId(), f);
		    }
		    
		    sortMnmeasub = new ArrayList<MnmeaSub>();
		    sortMnmeasub.addAll(data.getMnmeaSub().values());
		    Collections.sort(sortMnmeasub, new CompareMnmeaSub());
		    
			sortMnmea = new ArrayList<Mnmea>();
		    sortMnmea.addAll(data.getMnmea().values());
		    Collections.sort(sortMnmea, new CompareMnmea());
		    
		    sortCnmeaSettRe = new ArrayList<CnmeaSettRe>();
		    sortCnmeaSettRe.addAll(data.getCnmeaSettRe().values());
		    Collections.sort(sortCnmeaSettRe, new CompareCnmeaSettRe());
		    
		    sortBRQ = new ArrayList<Brq>();
		    sortBRQ.addAll(data.getBrq().values());
		    Collections.sort(sortBRQ, new CompareBrq());
		    
		    sortTvc = new ArrayList<Tvc>();
		    sortTvc.addAll(data.getTVC().values());
		    Collections.sort(sortTvc, new CompareTvc());
		    
		    sortMp = new ArrayList<Participant>();
		    sortMp.addAll(data.getParticipant().values());
		    Collections.sort(sortMp, new CompareParticipant());
		    
		    // to support regression, these should be taken from the params instead
	        settRunId = params.runId;
	        //settRunId = data.getGlobal().getRunId();
	        sqlSettDate = new Date(data.getGlobal().getTradingDate().toGregorianCalendar().getTime().getTime());
	        
	        //settDate = qdf.format(sqlSettDate);
			ZonedDateTime zdt = ZonedDateTime.of(sqlSettDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
			DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
			settDate = sqlFormatter.format(zdt);
			
	        settRunType = data.getGlobal().getRunType();
	        inputGst = data.getGlobal().getInputGstRate();

	        if ("PF".contains(settRunType)) {
	            emcAdmStr = UtilityFunctions.trimZeroDecimal(data.getGlobal().getCmwhEmcAdmRounded());
	            psoAdmStr = UtilityFunctions.trimZeroDecimal(data.getGlobal().getCmwhPsoAdmRounded());

	            // [ITSM15890] begin 4 decimal places
	            emcAdm = data.getGlobal().getCmwhEmcAdmRounded().setScale(4, RoundingMode.HALF_UP).toPlainString();
	            psoAdm = data.getGlobal().getCmwhPsoAdmRounded().setScale(4, RoundingMode.HALF_UP).toPlainString();

	            // [ITSM15890] end
	            totalMW = data.getGlobal().getTotalTte().toPlainString();	//globalLine[14];
	            totalGmee = UtilityFunctions.trimZeroDecimal(data.getGlobal().getTotalGmee());
	            totalGmef = UtilityFunctions.trimZeroDecimal(data.getGlobal().getTotalGmef());
	            totalLmee = UtilityFunctions.trimZeroDecimal(data.getGlobal().getTotalLmee());
	            totalLmef = UtilityFunctions.trimZeroDecimal(data.getGlobal().getTotalLmef());
	            totalNmea = UtilityFunctions.trimZeroDecimal(data.getGlobal().getTotalNmea());
	            cmwhEmcAdm = data.getGlobal().getCmwhEmcAdm().setScale(15, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();	//globalLine[25];
	            cmwhPsoAdm = data.getGlobal().getCmwhPsoAdm().setScale(17, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();	//globalLine[26];

	            // [ITSM15890] begin
	            emcadmPriceCap = data.getGlobal().getEmcAdmPriceCap().stripTrailingZeros().toPlainString();	//globalLine[27];
	            emcadmPriceAdjRate = data.getGlobal().getEmcAdmPriceAdjRate().setScale(18, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();	//globalLine[28];
	            emcadmPriceAdjRateRound = data.getGlobal().getEmcAdmPriceAdjRateRounded().stripTrailingZeros().toPlainString();	//globalLine[29];

	            // [ITSM15890] end
	        }

	        sacVersion = data.getGlobal().getStandingVersion();


	        for (Account recaccount : sortAccount) {
	            String sacId = recaccount.getAccountId();

	            // account_name

	            // Determine if this is a Generator Account, use Account's GESC to decide
	            if (recaccount.getGesc().signum() != 0) {
		            generatorAccounts.put(sacId, true);
		        // SATSHARP-246, just check existence will do
	            //} else {
		        //    generatorAccounts.put(sacId, false);
	            }

	            // Determine if this is a Embedded Generator Account
	            if (recaccount.isPriceNeutralization()) {
	                embeddedGenerators.put(sacId, true);
	            //} else {
	            //    embeddedGenerators.put(sacId, false);
	            }
	        }

	        // Get ID for user "SYSTEM"
	        userId = UtilityFunctions.getUserId(ds, "SYSTEM");

	        // Get Standing Version
	        standingVersion = PavPackage.getStandingVersion(ds, sqlSettDate);
	        
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
		}
	}
    
	public void generateMarketResults()
		throws Exception {
		
		String msgStep = "RunResultGenerator.generateMarketResults()";
		PreparedStatement stmt = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");
			
		   /* String market_number;
		    String market_AFP;
		    String market_HEUC;
		    String market_MEUC;
		    String market_USEP;
		    String market_VRCP;
		    String market_HEUA;
		    String market_WSP;
		    String market_HEUR;
		    String market_HLCU;*/

		    String sqlUsap = "INSERT INTO NEM.NEM_SETTLEMENT_USAP_RESULTS " + 
		    "(ID, PERIOD, AFP, MEUC, HEUC, USEP, STR_ID, VCRP, HEUR, HLCU) " + 
		    "VALUES ( SYS_GUID(),?,?,?,?,?,?,?,?,? )";
		    String sqlEnergy = "INSERT INTO NEM.NEM_ENERGY_UPLIFT_AMOUNTS " + 
		    "(ID, PERIOD, UPLIFT_AMOUNT, STR_ID) " + 
		    "VALUES ( SYS_GUID(),?,?,? )";
		    String sqlAlloc = "INSERT INTO NEM.NEM_ALLOC_REGULATION_PRICES " + 
		    "(ID, PERIOD, ALLOCATED_PRICE, STR_ID) " + 
		    "VALUES ( SYS_GUID(),?,?,? )";

		    // MWP - NEM_RUN_STATEMENTS
		    String sqlRunStat1MWP = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14) VALUES " + 
		    "( SYS_GUID(),?,'MWPHEADER',?,'Settlement Date','Period','AFP ($/MWh)','MEUC ($/MWh)','USEP ($/MWh)','HEUC ($/MWh)','HEUR ($/MWh)','HLCU ($/MWh)'," + 
		    "'EMC Admin ($/MWh)','PSO Admin ($/MWh)','Wholesale Price ($/MWh)','VCRP ($/MWh)','EMC Price Adj Fees ($/MWh)','EMC Price Cap Fees ($/MWh)')";
		    String sqlRunStat2MWP = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14) VALUES " + 
		    "( SYS_GUID(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		    
		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Inserting USAP Results, Energy Uplift Amounts, Alloc Regulation Prices, and Run Statements Results ...", 
		                                   "");

		    SimpleDateFormat mwpDf = new SimpleDateFormat("dd MMM yyyy");
		    SimpleDateFormat mnmeaDf = new SimpleDateFormat("dd-MMM-yyyy");
		    
		    // MNMEA - NEM_RUN_STATEMENTS
		    String sqlCommand1MNMEA = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12) VALUES( SYS_GUID(),?,'MNMEAHDR'," + 
		    "?,'Settlement Date','Run Type','Period','GMEE','GMEF','LMEE','LMEF','NMEA','','','','')";
		    String sqlCommand2MNMEA = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12) VALUES( SYS_GUID(),?,'MNMEA'," + 
		    "?,?,?,?,?,?,?,?,?,?,?,?,?)";
		    String sqlCommand3MNMEA = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12) VALUES( SYS_GUID(),?,'MNMEASUB'," + 
		    "?,'','',?,?,?,?,?,?,?,?,?,?)";
		    String sqlCommand4MNMEA = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12) VALUES( SYS_GUID(),?,'MNMEATOTAL'," + 
		    "?,?,?,?,?,?,?,?,?,?,?,?,?)";

		    int seq = 1;
			stmt = conn.prepareStatement(sqlRunStat1MWP);
			stmt.setString(1, settRunId);
			stmt.setInt(2, seq);
			stmt.executeQuery();
			stmt.close();

			SortedSet<String> keys = new TreeSet<String>(data.getMarket().keySet());
		    for (String key : keys) {
		    	Market market = data.getMarket().get(key);
		    	
		        // Insert into NEM_SETTLEMENT_USAP_RESULTS
				stmt = conn.prepareStatement(sqlUsap);
				stmt.setString(1, Integer.valueOf(market.getPeriodId()).toString());
				stmt.setBigDecimal(2, market.getAfp());
				stmt.setBigDecimal(3, market.getMeuc());
				stmt.setBigDecimal(4, market.getHeuc());
				stmt.setBigDecimal(5, market.getUsep());
				stmt.setString(6, settRunId);
				stmt.setBigDecimal(7, market.getVcrp());
				stmt.setBigDecimal(8, market.getHeur());
				stmt.setBigDecimal(9, market.getHlcu());
				stmt.executeQuery();
				stmt.close();

		        // Insert into NEM_ENERGY_UPLIFT_AMOUNTS
				stmt = conn.prepareStatement(sqlEnergy);
				stmt.setString(1, market.getPeriodId());
				stmt.setBigDecimal(2, market.getHeua());
				stmt.setString(3, settRunId);
				stmt.executeQuery();
				stmt.close();

		        // Insert into NEM_ALLOC_REGULATION_PRICES
				stmt = conn.prepareStatement(sqlAlloc);
				stmt.setString(1, market.getPeriodId());
				stmt.setString(2, (market.getAfp() == null? "0": market.getAfp().toPlainString()));
				stmt.setString(3, settRunId);
				stmt.executeQuery();
				stmt.close();

		        seq += 1;
		        // Insert into NEM_RUN_STATEMENTS
				stmt = conn.prepareStatement(sqlRunStat2MWP);
				stmt.setString(1, settRunId);
				stmt.setString(2, "MWP");
				stmt.setString(3, String.valueOf(seq));
				stmt.setString(4, mwpDf.format(sqlSettDate).toUpperCase());
				stmt.setString(5, Integer.valueOf(market.getPeriodId()).toString());
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(market.getAfp()));
				stmt.setString(7, UtilityFunctions.trimZeroDecimal(market.getMeuc()));
				stmt.setString(8, UtilityFunctions.trimZeroDecimal(market.getUsep()));
				stmt.setString(9, UtilityFunctions.trimZeroDecimal(market.getHeuc().setScale(2, RoundingMode.HALF_UP)));
				stmt.setString(10, UtilityFunctions.trimZeroDecimal(market.getHeur().setScale(2, RoundingMode.HALF_UP)));
				stmt.setString(11, UtilityFunctions.trimZeroDecimal(market.getHlcu().setScale(2, RoundingMode.HALF_UP)));
				stmt.setString(12, cmwhEmcAdm);
				stmt.setString(13, cmwhPsoAdm);
				stmt.setString(14, UtilityFunctions.trimZeroDecimal(market.getWsp()));
				stmt.setString(15, UtilityFunctions.trimZeroDecimal(market.getVcrp()));
				stmt.setString(16, emcadmPriceAdjRate);
				stmt.setString(17, emcadmPriceCap);
				stmt.executeQuery();
				stmt.close();
		    }

		    // /////////////////////////////////////////////////
		    // Write mnmea data to NEM_RUN_STATEMENT table
		    // ////////////////////////////////////////////////
		    logger.info(logPrefix + "Inserting data into NEM_RUN_STATEMENTS table (MNMEA) ...");

		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Inserting data into NEM_RUN_STATEMENTS table (MNMEA) ...", 
		                                   "");

	        String globalRunId = data.getGlobal().getRunId();
		    //String mnmeasub_rerun_id;
		    String mnmea_rerun_id;
		    String prev_mnmea_rerun_id = null;
		    /*String prev_settDateStr = null;
		    String[] mnmeaLine = null;
		    String[] mnmeasubLine = null;
		    String[] mnmeagrpLine = null;*/

		    // MNMEA Header
		    seq = 1;

			stmt = conn.prepareStatement(sqlCommand1MNMEA);
			stmt.setString(1, settRunId);
			stmt.setInt(2, seq);
			stmt.executeQuery();
			stmt.close();
			
		    for (Mnmea mnmea : sortMnmea) {
		        mnmea_rerun_id = mnmea.getSettId();

		        if (prev_mnmea_rerun_id != null && mnmea_rerun_id.equals(prev_mnmea_rerun_id) == false) {
		            // Get Mnmeasub Line
		        	MnmeaSub mnmeasub = data.getMnmeaSub().get(globalRunId+prev_mnmea_rerun_id);

		            if (mnmeasub == null) {
		                throw new Exception("Mnmeasub CSV File is Empty !!!");
		            }

		            seq = seq + 1;

					stmt = conn.prepareStatement(sqlCommand3MNMEA);
					stmt.setString(1, settRunId);
					stmt.setInt(2, seq);
					stmt.setString(3, "Sub Total");
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(mnmeasub.getGmee()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(mnmeasub.getGmef()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(mnmeasub.getLmee()));
					stmt.setString(7, UtilityFunctions.trimZeroDecimal(mnmeasub.getLmef()));
					stmt.setString(8, UtilityFunctions.trimZeroDecimal(mnmeasub.getNmea()));
					stmt.setString(9, "");
					stmt.setString(10, "");
					stmt.setString(11, "");
					stmt.setString(12, "");
					stmt.executeQuery();
					stmt.close();
		        }

		        seq = seq + 1;

				stmt = conn.prepareStatement(sqlCommand2MNMEA);
				stmt.setString(1, settRunId);
				stmt.setInt(2, seq);
				stmt.setString(3, mnmeaDf.format(mnmea.getSettDate().toGregorianCalendar().getTime()));
				stmt.setString(4, mnmea.getRunType());
				stmt.setString(5, Integer.valueOf(mnmea.getPeriodId()).toString());
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(mnmea.getGmee()));
				stmt.setString(7, UtilityFunctions.trimZeroDecimal(mnmea.getGmef()));
				stmt.setString(8, UtilityFunctions.trimZeroDecimal(mnmea.getLmee()));
				stmt.setString(9, UtilityFunctions.trimZeroDecimal(mnmea.getLmef()));
				stmt.setString(10, UtilityFunctions.trimZeroDecimal(mnmea.getNmea()));
				stmt.setString(11, "");
				stmt.setString(12, "");
				stmt.setString(13, "");
				stmt.setString(14, "");
				stmt.executeQuery();
				stmt.close();

		        prev_mnmea_rerun_id = mnmea_rerun_id;
		    }

		    if (prev_mnmea_rerun_id != null) {
	        	MnmeaSub mnmeasub = data.getMnmeaSub().get(globalRunId+prev_mnmea_rerun_id);

	            if (mnmeasub == null) {
		            throw new Exception("Mnmeasub CSV File is Empty !!!");
		        }

		        seq = seq + 1;

				stmt = conn.prepareStatement(sqlCommand3MNMEA);
				stmt.setString(1, settRunId);
				stmt.setInt(2, seq);
				stmt.setString(3, "Sub Total");
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(mnmeasub.getGmee()));
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(mnmeasub.getGmef()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(mnmeasub.getLmee()));
				stmt.setString(7, UtilityFunctions.trimZeroDecimal(mnmeasub.getLmef()));
				stmt.setString(8, UtilityFunctions.trimZeroDecimal(mnmeasub.getNmea()));
				stmt.setString(9, "");
				stmt.setString(10, "");
				stmt.setString(11, "");
				stmt.setString(12, "");
				stmt.executeQuery();
				stmt.close();
		    }

		    if (sortMnmea.size() == 0) {
		        // Mnmea is empty, put all "0"
	            int i = 1;

	            while (i <= data.getMarket().size()) {
	                seq = seq + 1;

					stmt = conn.prepareStatement(sqlCommand2MNMEA);
					stmt.setString(1, settRunId);
					stmt.setInt(2, seq);
					stmt.setString(3, "");
					stmt.setString(4, "");
					stmt.setString(5, Integer.toString(i));
					stmt.setString(6, "0");
					stmt.setString(7, "0");
					stmt.setString(8, "0");
					stmt.setString(9, "0");
					stmt.setString(10, "0");
					stmt.setString(11, "");
					stmt.setString(12, "");
					stmt.setString(13, "");
					stmt.setString(14, "");
					stmt.executeQuery();
					stmt.close();

	                i = i + 1;
	            }

		        // MNMEA SUB-Total
		        seq = seq + 1;

				stmt = conn.prepareStatement(sqlCommand3MNMEA);
				stmt.setString(1, settRunId);
				stmt.setInt(2, seq);
				stmt.setString(3, "Sub Total");
				stmt.setString(4, "0");
				stmt.setString(5, "0");
				stmt.setString(6, "0");
				stmt.setString(7, "0");
				stmt.setString(8, "0");
				stmt.setString(9, "");
				stmt.setString(10, "");
				stmt.setString(11, "");
				stmt.setString(12, "");
				stmt.executeQuery();
				stmt.close();

		        totalGmee = "";
		        totalGmef = "";
		        totalLmee = "";
		        totalLmef = "";
		        totalNmea = "";
		    }

		    // MNMEA TOTAL
		    seq = seq + 1;

			stmt = conn.prepareStatement(sqlCommand4MNMEA);
			stmt.setString(1, settRunId);
			stmt.setInt(2, seq);
			stmt.setString(3, "");
			stmt.setString(4, "");
			stmt.setString(5, "Total");
			stmt.setString(6, totalGmee);
			stmt.setString(7, totalGmef);
			stmt.setString(8, totalLmee);
			stmt.setString(9, totalLmef);
			stmt.setString(10, totalNmea);
			stmt.setString(11, "");
			stmt.setString(12, "");
			stmt.setString(13, "");
			stmt.setString(14, "");
			stmt.executeQuery();
			stmt.close();
			
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    
	public void generateMarketResultsIGS()
		throws Exception {
		
		String msgStep = "RunResultGenerator.generateMarketResultsIGS()";
		PreparedStatement stmt = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");
			
		   /* String market_number;
		    String market_AFP;
		    String market_HEUC;
		    String market_MEUC;
		    String market_USEP;
		    String market_VRCP;
		    String market_HEUA;
		    String market_WSP;
		    String market_emcadmn;

		    // 7.1.01
		    String market_psoadmn;

		    // 7.1.01
		    String market_wmep;
		    String market_HEUR;
		    String market_HLCU;*/

		    // 7.1.01
		    // FSC Implementation - modified the query to include FSRP
		    //String market_FSRP;
		    String sqlUsap = "INSERT INTO NEM.NEM_SETTLEMENT_USAP_RESULTS " + 
		    "(ID, PERIOD, AFP, MEUC, HEUC, USEP, STR_ID, VCRP, EMCADMN, PSOADMN, WMEP, HEUR, HLCU) " + 
		    "VALUES ( SYS_GUID(),?,?,?,?,?,?,?,?,?,?,?,? )";

		    // 7.1.01
		    // String sqlUsap; 
		    // 7.1.01
		    // logMessage("Rahul UtilityFunctions.isAfterWMEPunroundedEMCfeesEffectiveDate(settDate : settDate) " + UtilityFunctions.isAfterWMEPunroundedEMCfeesEffectiveDate(settDate : settDate), severity : WARNING);
		    // if (UtilityFunctions.isAfterWMEPunroundedEMCfeesEffectiveDate(settDate : settDate) == true) {
		    //    sqlUsap = "INSERT INTO NEM.NEM_SETTLEMENT_USAP_RESULTS " + 
		    //    "(ID, PERIOD, AFP, MEUC, HEUC, USEP, STR_ID, VCRP, EMCADMN, PSOADMN, WMEP) " + 
		    //    "VALUES ( SYS_GUID(),?,?,?,?,?,?,?,?,?,? )"; // 7.1.01 	
		    // } else {
		    //    sqlUsap = "INSERT INTO NEM.NEM_SETTLEMENT_USAP_RESULTS " + 
		    //    "(ID, PERIOD, AFP, MEUC, HEUC, USEP, STR_ID, VCRP) " + 
		    //    "VALUES ( SYS_GUID(),?,?,?,?,?,?,? )";           
		    // }
		    // [ITSM 17002] 7.1.05  - end    
		    String sqlEnergy = "INSERT INTO NEM.NEM_ENERGY_UPLIFT_AMOUNTS " + 
		    "(ID, PERIOD, UPLIFT_AMOUNT, STR_ID) " + 
		    "VALUES ( SYS_GUID(),?,?,? )";
		    String sqlAlloc = "INSERT INTO NEM.NEM_ALLOC_REGULATION_PRICES " + 
		    "(ID, PERIOD, ALLOCATED_PRICE, STR_ID) " + 
		    "VALUES ( SYS_GUID(),?,?,? )";

		    // MWP - NEM_RUN_STATEMENTS
		    String sqlRunStat1MWP = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14) VALUES " + 
		    "( SYS_GUID(),?,'MWPHEADER',?,'Settlement Date','Period','AFP ($/MWh)','MEUC ($/MWh)','USEP ($/MWh)','HEUC ($/MWh)','HEUR ($/MWh)','HLCU ($/MWh)'," + 
		    "'EMC Admin ($/MWh)','PSO Admin ($/MWh)','Wholesale Price ($/MWh)','VCRP ($/MWh)','EMC Price Adj Fees ($/MWh)','EMC Price Cap Fees ($/MWh)')";
		    String sqlRunStat2MWP = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14) VALUES " + 
		    "( SYS_GUID(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		    
		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Inserting USAP Results, Energy Uplift Amounts, Alloc Regulation Prices, and Run Statements Results ...", 
		                                   "");

		    SimpleDateFormat mwpDf = new SimpleDateFormat("dd MMM yyyy");
		    SimpleDateFormat mnmeaDf = new SimpleDateFormat("dd-MMM-yyyy");
		    
		    // MNMEA - NEM_RUN_STATEMENTS
		    String sqlCommand1MNMEA = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12) VALUES( SYS_GUID(),?,'MNMEAHDR'," + 
		    "?,'Settlement Date','Run Type','Period','GMEE','GMEF','LMEE','LMEF','NMEA','','','','')";
		    String sqlCommand2MNMEA = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12) VALUES( SYS_GUID(),?,'MNMEA'," + 
		    "?,?,?,?,?,?,?,?,?,?,?,?,?)";
		    String sqlCommand3MNMEA = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12) VALUES( SYS_GUID(),?,'MNMEASUB'," + 
		    "?,'','',?,?,?,?,?,?,?,?,?,?)";
		    String sqlCommand4MNMEA = "INSERT INTO NEM.NEM_RUN_STATEMENTS (ID, STR_ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12) VALUES( SYS_GUID(),?,'MNMEATOTAL'," + 
		    "?,?,?,?,?,?,?,?,?,?,?,?,?)";

		    int seq = 1;
			stmt = conn.prepareStatement(sqlRunStat1MWP);
			stmt.setString(1, settRunId);
			stmt.setInt(2, seq);
			stmt.executeQuery();
			stmt.close();

			SortedSet<String> keys = new TreeSet<String>(data.getMarket().keySet());
		    for (String key : keys) {
		    	Market market = data.getMarket().get(key);
		    	
		        // Insert into NEM_SETTLEMENT_USAP_RESULTS
				stmt = conn.prepareStatement(sqlUsap);
				stmt.setString(1, Integer.valueOf(market.getPeriodId()).toString());
				stmt.setBigDecimal(2, market.getAfp());
				stmt.setBigDecimal(3, market.getMeuc());
				stmt.setBigDecimal(4, market.getHeuc());
				stmt.setBigDecimal(5, market.getUsep());
				stmt.setString(6, settRunId);
				stmt.setBigDecimal(7, market.getVcrp());
				stmt.setBigDecimal(8, market.getEmcAdm());
				stmt.setBigDecimal(9, market.getPsoAdm());
				stmt.setBigDecimal(10, market.getWmep());
				stmt.setBigDecimal(11, market.getHeur());
				stmt.setBigDecimal(12, market.getHlcu());
				stmt.executeQuery();
				stmt.close();

		        // Insert into NEM_ENERGY_UPLIFT_AMOUNTS
				stmt = conn.prepareStatement(sqlEnergy);
				stmt.setString(1, market.getPeriodId());
				stmt.setBigDecimal(2, market.getHeua());
				stmt.setString(3, settRunId);
				stmt.executeQuery();
				stmt.close();

		        // Insert into NEM_ALLOC_REGULATION_PRICES
				stmt = conn.prepareStatement(sqlAlloc);
				stmt.setString(1, market.getPeriodId());
				stmt.setString(2, (market.getAfp() == null? "0": market.getAfp().toPlainString()));
				stmt.setString(3, settRunId);
				stmt.executeQuery();
				stmt.close();

		        seq += 1;
		        // Insert into NEM_RUN_STATEMENTS
				stmt = conn.prepareStatement(sqlRunStat2MWP);
				stmt.setString(1, settRunId);
				stmt.setString(2, "MWP");
				stmt.setString(3, String.valueOf(seq));
				stmt.setString(4, mwpDf.format(sqlSettDate).toUpperCase());
				stmt.setString(5, Integer.valueOf(market.getPeriodId()).toString());
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(market.getAfp()));
				stmt.setString(7, UtilityFunctions.trimZeroDecimal(market.getMeuc()));
				stmt.setString(8, UtilityFunctions.trimZeroDecimal(market.getUsep()));
				stmt.setString(9, UtilityFunctions.trimZeroDecimal(market.getHeuc().setScale(2, RoundingMode.HALF_UP)));
				stmt.setString(10, UtilityFunctions.trimZeroDecimal(market.getHeur().setScale(2, RoundingMode.HALF_UP)));
				stmt.setString(11, UtilityFunctions.trimZeroDecimal(market.getHlcu().setScale(2, RoundingMode.HALF_UP)));
				stmt.setString(12, cmwhEmcAdm);
				stmt.setString(13, cmwhPsoAdm);
				stmt.setString(14, UtilityFunctions.trimZeroDecimal(market.getWsp()));
				stmt.setString(15, UtilityFunctions.trimZeroDecimal(market.getVcrp()));
				stmt.setString(16, emcadmPriceAdjRate);
				stmt.setString(17, emcadmPriceCap);
				stmt.executeQuery();
				stmt.close();
		    }

		    // /////////////////////////////////////////////////
		    // Write mnmea data to NEM_RUN_STATEMENT table
		    // ////////////////////////////////////////////////
		    logger.info(logPrefix + "Inserting data into NEM_RUN_STATEMENTS table (MNMEA) ...");

		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Inserting data into NEM_RUN_STATEMENTS table (MNMEA) ...", 
		                                   "");

	        String globalRunId = data.getGlobal().getRunId();
		    //String mnmeasub_rerun_id;
		    String mnmea_rerun_id;
		    String prev_mnmea_rerun_id = null;
		    /*String prev_settDateStr = null;
		    String[] mnmeaLine = null;
		    String[] mnmeasubLine = null;
		    String[] mnmeagrpLine = null;*/

		    // MNMEA Header
		    seq = 1;

			stmt = conn.prepareStatement(sqlCommand1MNMEA);
			stmt.setString(1, settRunId);
			stmt.setInt(2, seq);
			stmt.executeQuery();
			stmt.close();
			
		    for (Mnmea mnmea : sortMnmea) {
		        mnmea_rerun_id = mnmea.getSettId();

		        if (prev_mnmea_rerun_id != null && mnmea_rerun_id.equals(prev_mnmea_rerun_id) == false) {
		            // Get Mnmeasub Line
		        	MnmeaSub mnmeasub = data.getMnmeaSub().get(globalRunId+prev_mnmea_rerun_id);

		            if (mnmeasub == null) {
		                throw new Exception("Mnmeasub CSV File is Empty !!!");
		            }

		            seq = seq + 1;

					stmt = conn.prepareStatement(sqlCommand3MNMEA);
					stmt.setString(1, settRunId);
					stmt.setInt(2, seq);
					stmt.setString(3, "Sub Total");
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(mnmeasub.getGmee()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(mnmeasub.getGmef()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(mnmeasub.getLmee()));
					stmt.setString(7, UtilityFunctions.trimZeroDecimal(mnmeasub.getLmef()));
					stmt.setString(8, UtilityFunctions.trimZeroDecimal(mnmeasub.getNmea()));
					stmt.setString(9, "");
					stmt.setString(10, "");
					stmt.setString(11, "");
					stmt.setString(12, "");
					stmt.executeQuery();
					stmt.close();
		        }

		        seq = seq + 1;

				stmt = conn.prepareStatement(sqlCommand2MNMEA);
				stmt.setString(1, settRunId);
				stmt.setInt(2, seq);
				stmt.setString(3, mnmeaDf.format(mnmea.getSettDate().toGregorianCalendar().getTime()));
				stmt.setString(4, mnmea.getRunType());
				stmt.setString(5, Integer.valueOf(mnmea.getPeriodId()).toString());
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(mnmea.getGmee()));
				stmt.setString(7, UtilityFunctions.trimZeroDecimal(mnmea.getGmef()));
				stmt.setString(8, UtilityFunctions.trimZeroDecimal(mnmea.getLmee()));
				stmt.setString(9, UtilityFunctions.trimZeroDecimal(mnmea.getLmef()));
				stmt.setString(10, UtilityFunctions.trimZeroDecimal(mnmea.getNmea()));
				stmt.setString(11, "");
				stmt.setString(12, "");
				stmt.setString(13, "");
				stmt.setString(14, "");
				stmt.executeQuery();
				stmt.close();

		        prev_mnmea_rerun_id = mnmea_rerun_id;
		    }

		    if (prev_mnmea_rerun_id != null) {
	        	MnmeaSub mnmeasub = data.getMnmeaSub().get(globalRunId+prev_mnmea_rerun_id);

	            if (mnmeasub == null) {
		            throw new Exception("Mnmeasub CSV File is Empty !!!");
		        }

		        seq = seq + 1;

				stmt = conn.prepareStatement(sqlCommand3MNMEA);
				stmt.setString(1, settRunId);
				stmt.setInt(2, seq);
				stmt.setString(3, "Sub Total");
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(mnmeasub.getGmee()));
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(mnmeasub.getGmef()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(mnmeasub.getLmee()));
				stmt.setString(7, UtilityFunctions.trimZeroDecimal(mnmeasub.getLmef()));
				stmt.setString(8, UtilityFunctions.trimZeroDecimal(mnmeasub.getNmea()));
				stmt.setString(9, "");
				stmt.setString(10, "");
				stmt.setString(11, "");
				stmt.setString(12, "");
				stmt.executeQuery();
				stmt.close();
		    }

		    if (sortMnmea.size() == 0) {
		        // Mnmea is empty, put all "0"
	            int i = 1;

	            while (i <= data.getMarket().size()) {
	                seq = seq + 1;

					stmt = conn.prepareStatement(sqlCommand2MNMEA);
					stmt.setString(1, settRunId);
					stmt.setInt(2, seq);
					stmt.setString(3, "");
					stmt.setString(4, "");
					stmt.setString(5, Integer.toString(i));
					stmt.setString(6, "0");
					stmt.setString(7, "0");
					stmt.setString(8, "0");
					stmt.setString(9, "0");
					stmt.setString(10, "0");
					stmt.setString(11, "");
					stmt.setString(12, "");
					stmt.setString(13, "");
					stmt.setString(14, "");
					stmt.executeQuery();
					stmt.close();

	                i = i + 1;
	            }

		        // MNMEA SUB-Total
		        seq = seq + 1;

				stmt = conn.prepareStatement(sqlCommand3MNMEA);
				stmt.setString(1, settRunId);
				stmt.setInt(2, seq);
				stmt.setString(3, "Sub Total");
				stmt.setString(4, "0");
				stmt.setString(5, "0");
				stmt.setString(6, "0");
				stmt.setString(7, "0");
				stmt.setString(8, "0");
				stmt.setString(9, "");
				stmt.setString(10, "");
				stmt.setString(11, "");
				stmt.setString(12, "");
				stmt.executeQuery();
				stmt.close();

		        totalGmee = "";
		        totalGmef = "";
		        totalLmee = "";
		        totalLmef = "";
		        totalNmea = "";
		    }

		    // MNMEA TOTAL
		    seq = seq + 1;

			stmt = conn.prepareStatement(sqlCommand4MNMEA);
			stmt.setString(1, settRunId);
			stmt.setInt(2, seq);
			stmt.setString(3, "");
			stmt.setString(4, "");
			stmt.setString(5, "Total");
			stmt.setString(6, totalGmee);
			stmt.setString(7, totalGmef);
			stmt.setString(8, totalLmee);
			stmt.setString(9, totalLmef);
			stmt.setString(10, totalNmea);
			stmt.setString(11, "");
			stmt.setString(12, "");
			stmt.setString(13, "");
			stmt.setString(14, "");
			stmt.executeQuery();
			stmt.close();
			
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());

		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");

		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateSettlementResults()
			throws Exception {
			
		String msgStep = "RunResultGenerator.generateSettlementResults()";
		PreparedStatement stmt = null;
		ResultSet rs = null;
			
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");
	

		    Timestamp startTime = new Timestamp(System.currentTimeMillis());
		    BigDecimal col2;
		    BigDecimal col2a;
		    BigDecimal col3;
		    BigDecimal col3a;
		    String sacId;

		    logger.info(logPrefix + "Inserting data into NEM_SETTLEMENT_RESULTS table ...");

		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Inserting data into NEM_SETTLEMENT_RESULTS table ...", 
		                                   "");

		    // /////////////////////////////////////////////////
		    // Write data to NEM_SETTLEMENT_RESULTS table
		    // /////////////////////////////////////////////////
		    String srtName;
		    HashMap<String, SettlementResultType> resultTypes = new HashMap<>();
		    String sqlCommand1 = "INSERT INTO NEM.NEM_SETTLEMENT_RESULTS VALUES ( SYS_GUID(),?,?,?,?,?,?,?,?,? )";
		    String lastSac = "";
		    
		    String sqlCommand = "SELECT ID, NAME, GST_VERSION FROM NEM.NEM_SETTLEMENT_RESULT_TYPES WHERE VERSION = ?";
			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, sacVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        srtName = rs.getString(2);
		        SettlementResultType rt = new SettlementResultType();
		        rt.id = rs.getString(1);
		        rt.gstVersion = rs.getString(3);
		        resultTypes.put(srtName, rt);
	        }
			rs.close();
			stmt.close();

			if (sortCnmeaSettRe.size() > 0) {
				boolean required = false;
				stmt = conn.prepareStatement(sqlCommand1);
			    for (CnmeaSettRe settResults : sortCnmeaSettRe) {
			        if (settResults == null) {
			            throw new Exception("Nmeagrp CSV File is Empty !!!");
			        }
	
			        boolean flag = settResults.isNmeaRequired();
	
			        // flag of required
			        // logMessage "nmeagrp required " + flag
			        if (flag == true) {
			        	required = true;
			            // chargeable item GMEE
			            col2 = settResults.getGmee();
	
			            if (col2 == null) {
			                col2 = BigDecimal.ZERO;
			            }
	
			            col3 = settResults.getIpGstGmee();
	
			            if (col3 == null) {
			                col3 = BigDecimal.ZERO;
			            }
	
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(settResults.getPeriodId()).toString());
						stmt.setString(2, col2.negate().stripTrailingZeros().toPlainString());
						stmt.setString(3, col3.stripTrailingZeros().toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("GMEE").id);
						stmt.setString(6, settResults.getVersion());
						stmt.setString(7, settResults.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
						
			            // logMessage "[GMEE] "+  sqlCommand1 + params1		   			
			            // chargeable item GMEF
			            col2 = settResults.getGmef();
	
			            if (col2 == null) {
			                col2 = BigDecimal.ZERO;
			            }
	
			            col3 = settResults.getOpGstGmef();
	
			            if (col3 == null) {
			                col3 = BigDecimal.ZERO;
			            }
	
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(settResults.getPeriodId()).toString());
						stmt.setString(2, col2.negate().stripTrailingZeros().toPlainString());
						stmt.setString(3, col3.stripTrailingZeros().toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("GMEF").id);
						stmt.setString(6, settResults.getVersion());
						stmt.setString(7, settResults.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
	
			            col2 = settResults.getLmee();
	
			            if (col2 == null) {
			                col2 = BigDecimal.ZERO;
			            }
	
			            col3 = settResults.getOpGstLmee();
	
			            if (col3 == null) {
			                col3 = BigDecimal.ZERO;
			            }
	
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(settResults.getPeriodId()).toString());
						stmt.setString(2, col2.negate().stripTrailingZeros().toPlainString());
						stmt.setString(3, col3.stripTrailingZeros().toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("LMEE").id);
						stmt.setString(6, settResults.getVersion());
						stmt.setString(7, settResults.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
	
			            // chargeable item LMEF
			            col2 = settResults.getLmef();
	
			            if (col2 == null) {
			                col2 = BigDecimal.ZERO;
			            }
	
			            col3 = settResults.getOpGstLmef();
	
			            if (col3 == null) {
			                col3 = BigDecimal.ZERO;
			            }
	
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(settResults.getPeriodId()).toString());
						stmt.setString(2, col2.negate().stripTrailingZeros().toPlainString());
						stmt.setString(3, col3.stripTrailingZeros().toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("LMEF").id);
						stmt.setString(6, settResults.getVersion());
						stmt.setString(7, settResults.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
	
			            // chargeable item NMEA
			            col2 = settResults.getNmea();
	
			            if (col2 == null) {
			                col2 = BigDecimal.ZERO;
			            }
	
			            col3 = settResults.getOpGstNmea();
	
			            if (col3 == null) {
			                col3 = BigDecimal.ZERO;
			            }
	
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(settResults.getPeriodId()).toString());
						stmt.setString(2, col2.stripTrailingZeros().toPlainString());
						stmt.setString(3, col3.negate().stripTrailingZeros().toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("NMEA").id);
						stmt.setString(6, settResults.getVersion());
						stmt.setString(7, settResults.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
			        }
			    }
			    if (required == true) {
			    	stmt.executeBatch();
			    }
			    stmt.close();
			}

			if (sortInterval.size() > 0) {
				stmt = conn.prepareStatement(sqlCommand1);
			    for (Period recinterval : sortInterval) {
			        if (recinterval == null) {
			            throw new Exception("Interval CSV File is Empty !!!");
			        }
	
			        sacId = recinterval.getAccountId();
			        
			        if (sacId != null && sacId.equals(lastSac) == false) {
			        	logger.info(logPrefix + "Inserting data into NEM_SETTLEMENT_RESULTS for SAC_ID: " + sacId);
	
			            lastSac = sacId;
			        }
	
			        // chargeable item FCC
		            col2 = recinterval.getFcc();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, BigDecimal.ZERO.toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("FCC").id);
					stmt.setString(6, resultTypes.get("FCC").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item FSC
		            col2 = recinterval.getFsc();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
		            col3 = recinterval.getIpGstFsc();
	
		            if (col3 == null) {
		                col3 = BigDecimal.ZERO;
		            }
	
			        if (col2.signum() != 0 || col3.signum() != 0) {
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
						stmt.setString(2, col2.stripTrailingZeros().toPlainString());
						stmt.setString(3, col3.stripTrailingZeros().toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("FSC").id);
						stmt.setString(6, resultTypes.get("FSC").gstVersion);
						stmt.setString(7, recinterval.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
			        }
	
			        // chargeable item FSD
		            col2 = recinterval.getAccountingFsd();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
		            col3 = recinterval.getOpGstFsd();
	
		            if (col3 == null) {
		                col3 = BigDecimal.ZERO;
		            }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.negate().stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("FSD").id);
					stmt.setString(6, resultTypes.get("FSD").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item GESCE
		            col2 = recinterval.getGesce();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
		            col3 = recinterval.getOpGstGesce();
	
		            if (col3 == null) {
		                col3 = BigDecimal.ZERO;
		            }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("GESCE").id);
					stmt.setString(6, resultTypes.get("GESCE").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item GESCN
		            col2 = recinterval.getGescn();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
		            col3 = recinterval.getIpGstGescn();
	
		            if (col3 == null) {
		                col3 = BigDecimal.ZERO;
		            }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("GESCN").id);
					stmt.setString(6, resultTypes.get("GESCN").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item GESCP
		            col2 = recinterval.getGescp();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
		            col3 = recinterval.getOpGstGescp();
	
		            if (col3 == null) {
		                col3 = BigDecimal.ZERO;
		            }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("GESCP").id);
					stmt.setString(6, resultTypes.get("GESCP").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item GMEA
		            col2 = recinterval.getGmea();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
		            col3 = recinterval.getOpGstGmea();
	
		            if (col3 == null) {
		                col3 = BigDecimal.ZERO;
		            }
	
			        if (col2.signum() != 0) {
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
						stmt.setString(2, col2.stripTrailingZeros().toPlainString());
						stmt.setString(3, col3.stripTrailingZeros().toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("GMEA").id);
						stmt.setString(6, resultTypes.get("GMEA").gstVersion);
						stmt.setString(7, recinterval.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
			        }
	
			        // chargeable item HERSA
		            col2 = recinterval.getHersa();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
		            col3 = recinterval.getOpGstHersa();
	
		            if (col3 == null) {
		                col3 = BigDecimal.ZERO;
		            }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("HERSA").id);
					stmt.setString(6, resultTypes.get("HERSA").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item HLCSA
		            col2 = recinterval.getRoundedHlcsa();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
		            col3 = recinterval.getOpGstHlcsa();
	
		            if (col3 == null) {
		                col3 = BigDecimal.ZERO;
		            }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("HLCSA").id);
					stmt.setString(6, resultTypes.get("HLCSA").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item LESDN/LESDP
			        col2 = recinterval.getAccountingLesdp();
	
			        // LESDP
			        col2a = recinterval.getAccountingLesdn();
	
			        // LESDN
			        if (col2 == null) {
		                col2 = BigDecimal.ZERO;
			        }
	
			        if (col2a == null) {
		                col2a = BigDecimal.ZERO;
			        }
	
			        col3 = recinterval.getOpGstLesdp();
	
			        // a_LESDP
			        col3a = recinterval.getIpGstLesdn();
	
			        // v_LESDN
			        if (col3 == null) {
			            col3 = BigDecimal.ZERO;
			        }
	
			        if (col3a == null) {
			            col3a = BigDecimal.ZERO;
			        }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(4, settRunId);
					if (col2.signum() < 0) {
						stmt.setString(2, col2.negate().stripTrailingZeros().toPlainString());
						stmt.setString(3, col3.stripTrailingZeros().toPlainString());
						stmt.setString(5, resultTypes.get("LESDP").id);
						stmt.setString(6, resultTypes.get("LESDP").gstVersion);
					} else {
						stmt.setString(2, col2a.negate().stripTrailingZeros().toPlainString());
						stmt.setString(3, col3a.stripTrailingZeros().toPlainString());
						stmt.setString(5, resultTypes.get("LESDN").id);
						stmt.setString(6, resultTypes.get("LESDN").gstVersion);
					}
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item LMEA
		            col2 = recinterval.getAccountingLmea();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
		            col3 = recinterval.getOpGstLmea();
	
		            if (col3 == null) {
		                col3 = BigDecimal.ZERO;
		            }
	
			        if (col2.signum() != 0) {
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
						stmt.setString(2, col2.negate().stripTrailingZeros().toPlainString());
						stmt.setString(3, col3.stripTrailingZeros().toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("LMEA").id);
						stmt.setString(6, resultTypes.get("LMEA").gstVersion);
						stmt.setString(7, recinterval.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
			        }
	
			        // chargeable item MEUSA
		            col2 = recinterval.getAccountingMeusa();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
		            col3 = recinterval.getOpGstMeusa();
	
		            if (col3 == null) {
		                col3 = BigDecimal.ZERO;
		            }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.negate().stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("MEUSA").id);
					stmt.setString(6, resultTypes.get("MEUSA").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item BESC / NBSC
		            col2 = recinterval.getBesc();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
			        if (col2.signum() != 0) {
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
						stmt.setString(2, col2.stripTrailingZeros().toPlainString());
						stmt.setString(3, BigDecimal.ZERO.toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("NBSC").id);
						stmt.setString(6, resultTypes.get("NBSC").gstVersion);
						stmt.setString(7, recinterval.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
			        }
	
			        // chargeable item NEAA
		            col2 = recinterval.getNeaa();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
		            
		            Account sac = data.getAccount().get(sacId);
	
			        if (sac != null && sac.isPriceNeutralization() == true) {
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
						stmt.setString(2, col2.stripTrailingZeros().toPlainString());
						stmt.setString(3, BigDecimal.ZERO.toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("NEAA").id);
						stmt.setString(6, resultTypes.get("NEAA").gstVersion);
						stmt.setString(7, recinterval.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
			        }
	
			        // chargeable item NEAD
		            col2 = recinterval.getAccountingNead();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.negate().stripTrailingZeros().toPlainString());
					stmt.setString(3, BigDecimal.ZERO.toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("NEAD").id);
					stmt.setString(6, resultTypes.get("NEAD").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item NEGC
			        // only for Embedded Generator
			        // Commented the following to resolve "Missing NEGC value in Solomon for GREEN PA_E (BPM 2.5.1)"
			        // if (intervalLine[134].trim() != "") {
			        //    col2 = intervalLine[61].trim();
			        //    if (col2 == "") {
			        //        col2 = "0";
			        //    }
			        // comment end
			        // Added the following to resolve "Missing NEGC value in Solomon for GREEN PA_E (BPM 2.5.1)"
			        // For ITEM NEGC
		            col2 = recinterval.getNegc();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
			        if (sac != null && sac.isPriceNeutralization() == true) {
			            // Addition end         
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
						stmt.setString(2, col2.stripTrailingZeros().toPlainString());
						stmt.setString(3, BigDecimal.ZERO.toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("NEGC").id);
						stmt.setString(6, resultTypes.get("NEGC").gstVersion);
						stmt.setString(7, recinterval.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
	
			            // Added the following to resolve "Missing NEGC value in Solomon for GREEN PA_E (BPM 2.5.1)"   
			        }
	
			        // Addition end 
			        // Commented the following to resolve "Missing NEGC value in Solomon for GREEN PA_E (BPM 2.5.1)"
			        // }
			        // comment end
			        // chargeable item NELC
		            col2 = recinterval.getNelc();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
			        if (sac != null && sac.isPriceNeutralization() == true) {
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
						stmt.setString(2, col2.stripTrailingZeros().toPlainString());
						stmt.setString(3, BigDecimal.ZERO.toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, resultTypes.get("NELC").id);
						stmt.setString(6, resultTypes.get("NELC").gstVersion);
						stmt.setString(7, recinterval.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
			        }
	
			        // chargeable item NFSC
		            col2 = recinterval.getNfsc();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
			        col3 = recinterval.getIpGstFsc();
			        col3a = recinterval.getOpGstFsd();
	
			        if (col3 == null) {
			            col3 = BigDecimal.ZERO;
			        }
	
			        if (col3a == null) {
			            col3a = BigDecimal.ZERO;
			        }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.subtract(col3a).stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("NFSC").id);
					stmt.setString(6, resultTypes.get("NFSC").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item NRSC
		            col2 = recinterval.getNrsc();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
			        col3 = recinterval.getIpGstRsc();
			        col3a = recinterval.getOpGstRsd();
	
			        if (col3 == null) {
			            col3 = BigDecimal.ZERO;
			        }
	
			        if (col3a == null) {
			            col3a = BigDecimal.ZERO;
			        }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.subtract(col3a).stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("NRSC").id);
					stmt.setString(6, resultTypes.get("NRSC").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item RCC
		            col2 = recinterval.getRcc();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, BigDecimal.ZERO.toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("RCC").id);
					stmt.setString(6, resultTypes.get("RCC").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item RSC
		            col2 = recinterval.getRsc();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
			        col3 = recinterval.getIpGstRsc();
			        
			        if (col3 == null) {
			            col3 = BigDecimal.ZERO;
			        }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("RSC").id);
					stmt.setString(6, resultTypes.get("RSC").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item RSD
		            col2 = recinterval.getRsd();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
			        col3 = recinterval.getOpGstRsd();
			        
			        if (col3 == null) {
			            col3 = BigDecimal.ZERO;
			        }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("RSD").id);
					stmt.setString(6, resultTypes.get("RSD").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // chargeable item VCSC
		            col2 = recinterval.getVcsc();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
			        if (col2.signum() != 0) {
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
						stmt.setString(2, col2.stripTrailingZeros().toPlainString());
						stmt.setString(3, BigDecimal.ZERO.toPlainString());
						stmt.setString(4, settRunId);
						if (col2.signum() < 0) {
							stmt.setString(5, resultTypes.get("VCSCN").id);
							stmt.setString(6, resultTypes.get("VCSCN").gstVersion);
						} else {
							stmt.setString(5, resultTypes.get("VCSCP").id);
							stmt.setString(6, resultTypes.get("VCSCP").gstVersion);
						}
						stmt.setString(7, recinterval.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
			        }
	
			        // chargeable item VCSCK
		            col2 = recinterval.getVcsck();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
			        if (col2.signum() != 0) {
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
						stmt.setString(2, col2.stripTrailingZeros().toPlainString());
						stmt.setString(3, BigDecimal.ZERO.toPlainString());
						stmt.setString(4, settRunId);
						if (col2.signum() < 0) {
							stmt.setString(5, resultTypes.get("VCSCRN").id);
							stmt.setString(6, resultTypes.get("VCSCRN").gstVersion);
						} else {
							stmt.setString(5, resultTypes.get("VCSCRP").id);
							stmt.setString(6, resultTypes.get("VCSCRP").gstVersion);
						}
						stmt.setString(7, recinterval.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
			        }
	
			        // ////Start FSC Implementation
			        if (params.isFSCEffective == true) {
				        // chargeable item FSSC
			            col2 = recinterval.getFssc();
		
			            if (col2 == null) {
			                col2 = BigDecimal.ZERO;
			            }
		
				        // insert non zero values also for FSSC for FSC holders
				        // if (col2 != "0") {
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
						stmt.setString(2, col2.stripTrailingZeros().toPlainString());
						stmt.setString(3, BigDecimal.ZERO.toPlainString());
						stmt.setString(4, settRunId);
						if (col2.signum() < 0) {
							stmt.setString(5, resultTypes.get("FSSCN").id);
							stmt.setString(6, resultTypes.get("FSSCN").gstVersion);
						} else {
							stmt.setString(5, resultTypes.get("FSSCP").id);
							stmt.setString(6, resultTypes.get("FSSCP").gstVersion);
						}
						stmt.setString(7, recinterval.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
				        // }
						
				        // chargeable item FSSCK
			            col2 = recinterval.getFssck();
		
			            if (col2 == null) {
			                col2 = BigDecimal.ZERO;
			            }
		
				        // insert non zero values also for FSSC for FSC holders
				        // if (col2 != "0") {
						//stmt = conn.prepareStatement(sqlCommand1);
						stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
						stmt.setString(2, col2.stripTrailingZeros().toPlainString());
						stmt.setString(3, BigDecimal.ZERO.toPlainString());
						stmt.setString(4, settRunId);
						if (col2.signum() < 0) {
							stmt.setString(5, resultTypes.get("FSSCRN").id);
							stmt.setString(6, resultTypes.get("FSSCRN").gstVersion);
						} else {
							stmt.setString(5, resultTypes.get("FSSCRP").id);
							stmt.setString(6, resultTypes.get("FSSCRP").gstVersion);
						}
						stmt.setString(7, recinterval.getAccountId());
						stmt.setString(8, sacVersion);
						stmt.setString(9, settDate);
						//stmt.executeQuery();
						//stmt.close();
						stmt.addBatch();
				        // }
			        }
			        // /////End FSC Implementation
	
			        // For DRCAP
			        // chargeable item LCSCP
		            col2 = recinterval.getLcsc();
	
		            if (col2 == null) {
		                col2 = BigDecimal.ZERO;
		            }
	
		            // fix for RM00001132
			        col3 = recinterval.getIpGstLcsc();
	
			        if (col3 == null) {
			            col3 = BigDecimal.ZERO;
			        }
	
					//stmt = conn.prepareStatement(sqlCommand1);
					stmt.setString(1, Integer.valueOf(recinterval.getPeriodId()).toString());
					stmt.setString(2, col2.stripTrailingZeros().toPlainString());
					stmt.setString(3, col3.stripTrailingZeros().toPlainString());
					stmt.setString(4, settRunId);
					stmt.setString(5, resultTypes.get("LCSCP").id);
					stmt.setString(6, resultTypes.get("LCSCP").gstVersion);
					stmt.setString(7, recinterval.getAccountId());
					stmt.setString(8, sacVersion);
					stmt.setString(9, settDate);
					//stmt.executeQuery();
					//stmt.close();
					stmt.addBatch();
	
			        // End DRCAP Implementation
			    }
			    stmt.executeBatch();
			    stmt.close();
			}

		    Timestamp endTime = new Timestamp(System.currentTimeMillis());

		    logger.info(logPrefix + "NEM_SETTLEMENT_RESULTS Insertion Done. Start time: " + 
		    startTime + ", end time: " + endTime);
		    
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void reverseNonPeriodCharges()
		throws Exception {
		
		String msgStep = "RunResultGenerator.reverseNonPeriodCharges()";
		PreparedStatement stmt = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");
		    
		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
                    "Reverse existing Non Period Charges", 
                    "");

			// Reverse existing Non Period Charges
			String sqlCommand = "UPDATE nem.nem_non_period_charges " + 
								"SET approval_status = 'D' " + 
								"WHERE ncc_id IN ( SELECT id FROM nem.nem_non_period_charge_codes " + 
								"WHERE solomon_code IN ( 'EMCADMIN', 'PSOADMIN', 'EMCADMINR', 'PSOADMINR', 'MACP', 'MEUS' ) ) " + 
								"AND start_date = ? AND approval_status != 'D'";
			
			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, settDate);
			stmt.executeQuery();
			stmt.close();

		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void generateNonPeriodCharges()
		throws Exception {
		
		String msgStep = "RunResultGenerator.generateNonPeriodCharges()";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");
		    
		    BigDecimal emcAmt;
		    BigDecimal emcGst;
		    BigDecimal psoAmt;
		    BigDecimal psoGst;
		    String sacId;
		    String admInGstId = "";
		    String admOutGstId = "";
		    String gstId;
		    String emcADMNCCId = "";
		    String psoADMNCCId = "";
		    String emcADMRNCCId = "";
		    String psoADMRNCCId = "";
		    String calRule;

		    // Create Non Period Charges
		    java.sql.Timestamp enteredDate = new Timestamp(System.currentTimeMillis());

		    // Get Non Period Charge Codes
		    String npcc;
		    String sqlCommand = "SELECT ID, SOLOMON_CODE FROM NEM.NEM_NON_PERIOD_CHARGE_CODES " + 
		    "WHERE SOLOMON_CODE IN ('EMCADMIN', 'PSOADMIN', 'EMCADMINR', 'PSOADMINR') ";

			stmt = conn.prepareStatement(sqlCommand);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        npcc = rs.getString(2);

		        switch (npcc) {
		        case "EMCADMIN":
		            emcADMNCCId = rs.getString(1);
		            break;
		        case "PSOADMIN":
		            psoADMNCCId = rs.getString(1);
		            break;
		        case "EMCADMINR":
		            emcADMRNCCId = rs.getString(1);
		            break;
		        case "PSOADMINR":
		            psoADMRNCCId = rs.getString(1);
		            break;
		        default:
		        	break;
		        }
		    }
			rs.close();
			stmt.close();

		    // Get GST ID for 'NON APPLICABLE INPUT TAX'
		    sqlCommand = "SELECT NAME, GST_ID FROM NEM.NEM_SETTLEMENT_RESULT_TYPES " + 
		                 "WHERE NAME IN ('ADMIN_IN', 'ADMIN_OUT') " + 
		                 "AND VERSION = ?";

			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, standingVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        if (rs.getString(1).equals("ADMIN_IN") == true) {
		            admInGstId = rs.getString(2);
		        } else {
		            admOutGstId = rs.getString(2);
		        }
		    }
			rs.close();
			stmt.close();

		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Inserting Non Period Charge Results ...", 
		                                   "");

		    // Insert into Non Period Charge
		    String sqlNPC = "INSERT INTO NEM.NEM_NON_PERIOD_CHARGES (ID, NPC_TYPE, EXTERNAL_ID, NAME, CHARGE_DATE," + 
		    " ENTERED_DATE, CHARGE_FREQUENCY, CALCULATION_RULE, DEBIT_CREDIT, AMOUNT, START_DATE, END_DATE," + 
		    " COMMENTS, SAC_ID, GST_ID, APPROVAL_STATUS, APPROVAL_TIMESTAMP, APPROVER_ID, LOCK_VERSION, NCC_ID) " + 
		    " VALUES ( SYS_GUID(),'ACC','NPC:' || NPC_SEQ.NEXTVAL,?,?,?,'D',?,?,?,?,?,?,?,?,?,SYSDATE,?,?,?)";

		    for (Account sac : sortAccount) {
		        if (sac == null) {
		            break;
		        }

		        emcAmt = sac.getAccountingEmcAdm();
		        emcGst = sac.getAccountingOpGstEmcAdm();
		        psoAmt = sac.getAccountingPsoAdm();
		        psoGst = sac.getAccountingOpGstPsoAdm();
		        sacId = sac.getAccountId();

		        if (sac.isUnderRetailer() == true || sac.isEmcAccount() == true || sac.isPsoAccount() == true || (emcAmt.signum() != 0 || psoAmt.signum() != 0)) {
		            calRule = "Y";
		            gstId = admOutGstId;

		            if (sac.isEmcAccount() == true) {
		                emcAmt = sac.getAdmFee();
		                emcGst = BigDecimal.ZERO;
		                calRule = "N";
		                gstId = admInGstId;
		            }

		            if (sac.isPsoAccount() == false) {
		                String feeName;
		                BigDecimal feeRate = new BigDecimal(emcAdm);

		                if (feeRate.signum() < 0) {
		                    feeName = "APPROX. (" + feeRate.abs().toPlainString() + ") $/MW";
		                } else {
		                    feeName = "APPROX. " + feeRate.toPlainString() + " $/MW";
		                }

		    			stmt = conn.prepareStatement(sqlNPC);
		    			stmt.setString(1, feeName);	// NAME: MACP SD DD MMM YYYY
		    			stmt.setString(2, settDate);	// CHARGE_DATE: Settlement Date
		    			stmt.setTimestamp(3, enteredDate);	// ENTERED_DATE: Current Time
		    			stmt.setString(4, calRule);
		    			stmt.setString(5, (emcAmt.signum() > 0 ? "C" : "D")); // DEBIT_CREDIT
		    			stmt.setBigDecimal(6, emcAmt.abs().stripTrailingZeros());	// EMCADMIN FEE
		    			stmt.setString(7, settDate);	// START_DATE: Settlement Date
		    			stmt.setString(8, settDate);	// END_DATE: Settlement Date?
		    			stmt.setString(9, "Total MW: " + totalMW);	// COMMENTS: MACP RECOVERED ...
		    			stmt.setString(10, sacId);	// SAC_ID: Account ID for "EMC REC_A"
		    			stmt.setString(11, gstId);	// GST_ID: ID of NEM_GST_CODES with Description = "NON APPLICABLE INPUT TAX"
		    			stmt.setString(12, "A");	// Approved
		    			stmt.setString(13, userId);
		    			stmt.setInt(14, 1);
		    			stmt.setString(15, sac.isEmcAccount() == true ? emcADMRNCCId : emcADMNCCId);	// NCC_ID
		    			stmt.executeQuery();
		    			stmt.close();
		            }

		            if (sac.isPsoAccount() == true) {
		                psoAmt = sac.getAdmFee();
		                psoGst = BigDecimal.ZERO;
		                calRule = "N";
		                gstId = admInGstId;
		            }

		            if (sac.isEmcAccount() == false) {
		                String feeName;
		                BigDecimal feeRate = new BigDecimal(psoAdm);

		                if (feeRate.signum() < 0) {
		                    feeName = "APPROX. (" + feeRate.abs().toPlainString() + ") $/MW";
		                } else {
		                    feeName = "APPROX. " + feeRate.toPlainString() + " $/MW";
		                }

		    			stmt = conn.prepareStatement(sqlNPC);
		    			stmt.setString(1, feeName);	// NAME: MACP SD DD MMM YYYY
		    			stmt.setString(2, settDate);	// CHARGE_DATE: Settlement Date
		    			stmt.setTimestamp(3, enteredDate);	// ENTERED_DATE: Current Time
		    			stmt.setString(4, calRule);
		    			stmt.setString(5, (psoAmt.signum() > 0 ? "C" : "D")); // DEBIT_CREDIT
		    			stmt.setBigDecimal(6, psoAmt.abs().stripTrailingZeros());	// EMCADMIN FEE
		    			stmt.setString(7, settDate);	// START_DATE: Settlement Date
		    			stmt.setString(8, settDate);	// END_DATE: Settlement Date?
		    			stmt.setString(9, "Total MW: " + totalMW);	// COMMENTS: MACP RECOVERED ...
		    			stmt.setString(10, sacId);	// SAC_ID: Account ID for "EMC REC_A"
		    			stmt.setString(11, gstId);	// GST_ID: ID of NEM_GST_CODES with Description = "NON APPLICABLE INPUT TAX"
		    			stmt.setString(12, "A");	// Approved
		    			stmt.setString(13, userId);
		    			stmt.setInt(14, 1);
		    			stmt.setString(15, sac.isPsoAccount() == true ? psoADMRNCCId : psoADMNCCId);	// NCC_ID
		    			stmt.executeQuery();
		    			stmt.close();
		            }
		        }
		    }
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<String, BigDecimal> getGstRates(String gstVersion)
		throws Exception {
		
		String msgStep = "RunResultGenerator.getGstRates()";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");

		    HashMap<String, BigDecimal> gstCodes = new HashMap<>();
		    
		    // replace computeGst()
		    String sqlGstCodes = "SELECT id, VALUE FROM nem_gst_codes " + 
		    	    "WHERE version = ?";
		    
			stmt = conn.prepareStatement(sqlGstCodes);
			stmt.setString(1, gstVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
				String gId = rs.getString(1);
				BigDecimal gRate = rs.getBigDecimal(2);
				gstCodes.put(gId, gRate);
			}
			rs.close();
			stmt.close();

		    return gstCodes;
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<String, ResultDetails> getNodeSACIds(String nodeVersion)
		throws Exception {
		
		String msgStep = "RunResultGenerator.getNodeSACIds()";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");

		    HashMap<String, ResultDetails> nodeSacs = new HashMap<>();

			// replace getNodeSACId()
		    String sqlNode = "SELECT id, sac_id, sac_version " + 
		    	    "FROM nem_nodes WHERE version = ?";
		    
			stmt = conn.prepareStatement(sqlNode);
			stmt.setString(1, nodeVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
				String ndeId = rs.getString(1);
				ResultDetails acct = new ResultDetails();
				acct.sac = rs.getString(2);
				acct.sacVer = rs.getString(3);
				nodeSacs.put(ndeId, acct);
			}
			rs.close();
			stmt.close();
					    
		    return nodeSacs;
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<String, String> getDispSACIds()
		throws Exception {
		
		String msgStep = "RunResultGenerator.getDispSACIds()";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");

		    HashMap<String, String> nodeSacs = new HashMap<>();

		    String sqlCommand = "SELECT dds.id, dsp.sac_id " + 
		    	    "FROM nem_disputes dsp, nem_dispute_decisions dds " + 
		    	    "WHERE dds.dsp_id = dsp.id ";
		    
			stmt = conn.prepareStatement(sqlCommand);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
				String sac = rs.getString(1);
				String id = rs.getString(2);
				nodeSacs.put(id, sac);
			}
			rs.close();
			stmt.close();
					    
		    return nodeSacs;
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<String, Boolean> getDrSacIds(String stdVer)
		throws Exception {
		
		String msgStep = "RunResultGenerator.getDrSacIds()";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");

		    HashMap<String, Boolean> accountDR = new HashMap<>();
		    
		    String sqlCommand = "select nde.id, nde.sac_id, fct.id from nem.nem_nodes nde, nem.nem_facilities fct, NEM.NEM_GENERATION_TYPES gnt " + 
		    "where nde.id=fct.nde_id " + 
		    "and nde.version = fct.nde_version " + 
		    "and nde.version = ? " + 
		    "and nde.node_type = 'L' " + 
		    "and FCT.GNT_ID = gnt.id " + 
		    "and fct.gnt_version = gnt.version " + 
		    "and fct.facility_type = 'DPL' " + 
		    "and gnt.facility_type = fct.facility_type " + 
		    "AND gnt.generation_type = 'DR'";

			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, stdVer);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        //String nde_id = rs.getString(1);
		        String account_id = rs.getString(2);
		        //String fct_id = rs.getString(3);
		        accountDR.put(account_id, true);
		    }
			rs.close();
			stmt.close();

		    return accountDR;
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<String, String> getDrNdeIds(String stdVer)
		throws Exception {
		
		String msgStep = "RunResultGenerator.getDrNdeIds()";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");

		    HashMap<String, String> nodeDR = new HashMap<>();

		    String sqlCommand = "select nde.name, nde.sac_id, fct.id from nem.nem_nodes nde, nem.nem_facilities fct, NEM.NEM_GENERATION_TYPES gnt " + 
		    	    "where nde.id=fct.nde_id " + 
		    	    "and nde.version = fct.nde_version " + 
		    	    "and nde.version = ? " + 
		    	    "and nde.node_type = 'L' " + 
		    	    "and FCT.GNT_ID = gnt.id " + 
		    	    "and fct.gnt_version = gnt.version " + 
		    	    "and fct.facility_type = 'DPL' " + 
		    	    "and gnt.facility_type = fct.facility_type " + 
		    	    "AND gnt.generation_type = 'DR'";

			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, stdVer);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        String nde_id = rs.getString(1);
		        //String account_id = rs.getString(2);
		        String fct_id = rs.getString(3);
    	        nodeDR.put(nde_id, fct_id);
    	    }
			rs.close();
			stmt.close();

		    return nodeDR;
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void doNonPeriodChargesPC(String chargeType, String chargeFreq)
		throws Exception {
		
		String msgStep = "RunResultGenerator.doNonPeriodChargesPC()";
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");
		    
		    LocalDate date8 = LocalDate.of(3000, 1, 31);
		    //String infiniteDate = qdf.format(Date.valueOf(date8));
			ZonedDateTime zdt = ZonedDateTime.of(date8.atStartOfDay(), ZoneId.systemDefault());
			DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
			String infiniteDate = sqlFormatter.format(zdt);
		    String nodeId;
		    String sacId;
		    String sacVer;
		    BigDecimal amount;
		    String gstId;
		    BigDecimal gst = BigDecimal.ZERO;
		    String npcId;
		    String debitCredit;
		    String gstRegistered;
		    String gst_type;
		    
		    HashMap<String, BigDecimal> gstCodes = getGstRates(standingVersion);
		    
		    HashMap<String, ResultDetails> nodeSacs = getNodeSACIds(standingVersion);

		    // ITSM 15932
		    String nccCode;

		    // ITSM 15890
		    // Query npc with taxable
		    String sqlCommand = "select npc.sac_id, npc.nde_id, npc.amount, npc.gst_id, " + 
		    "npc.id, npc.debit_credit, sa.gst_registered " + 
		    ", DECODE(SUBSTR(gst.NAME,1,1), 'V', 'INPUT_GST', 'A', 'OUTPUT_GST', 'NONE') gst_type " + 
		    ", ncc.SOLOMON_CODE code " + 
		    "from nem_non_period_charges npc, nem_settlement_accounts sa " + 
		    ", nem_gst_codes gst, nem_non_period_charge_codes ncc " + 
		    "where npc.sac_id = sa.id and sa.version = ? and " + 
		    "npc.npc_type = ? and npc.charge_frequency = ? " + 
		    "and ? between npc.start_date and NVL (npc.end_date, ?) " + 
		    "and npc.approval_status = 'A' " + 
		    "and npc.gst_id=gst.id " + 
		    "and gst.version = ? " + 
		    "and npc.NCC_ID = ncc.ID ";

		    // ITSM 15890
		    // Insert into Periodic Results
		    String sqlPeriodC = "INSERT INTO NEM.NEM_PERIODIC_RESULTS (ID, CALCULATION_RESULT, " + 
		    "GST_AMOUNT, STR_ID, NPC_ID, SAC_ID, SAC_VERSION) VALUES (SYS_GUID(),?,?,?,?,?,?)";
			
		    // ITSM 15932   
			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, standingVersion);
			stmt.setString(2, chargeType);
			stmt.setString(3, chargeFreq);
			stmt.setString(4, settDate);
			stmt.setString(5, infiniteDate);
			stmt.setString(6, standingVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        Boolean useHaley = false;
		        npcId = rs.getString(5);	//((String) row[5]);
		        debitCredit = rs.getString(6);	//((String) row[6]);
		        amount = rs.getBigDecimal(3);	//((Decimal) row[3]);
		        sacId = rs.getString(1);	//String.valueOf(o : row[1]);
		        nccCode = rs.getString(9);	//((String) row[9]);

		        if (debitCredit.equals("D")) {
		            amount = amount.negate();
		        }

		        // ITSM 15890
		        // resolve issue of the actual Fees amount for calculate GST is more than 2 decimal places
		        // take the GST from Haley directly
		        Account sac = data.getAccount().get(sacId);
		        
		        if (sac != null) {
		        	if (nccCode.equals("EMCADMIN")) {
	                    // EMC Fees
	                    gst = sac.getAccountingOpGstEmcAdm();
	                    useHaley = true;
		        	}
	                if (nccCode.equals("PSOADMIN")) {
	                    // PSO Fees
	                    gst = sac.getAccountingOpGstPsoAdm();
	                    useHaley = true;
	                }
		        }

		        gstId = rs.getString(4);	//String.valueOf(o : row[4]);
		        gstRegistered = rs.getString(7);	//String.valueOf(o : row[7]);
		        gst_type = rs.getString(8);	//String.valueOf(o : row[8]);

		        // ITSM 15932
		        // ITSM 15890 - bypass existing codes since we are geting GST from Haley
		        if (useHaley == false) {
		            if (gstId != null && gstRegistered.equals("Y") == true) {
		            	BigDecimal rate = gstCodes.get(gstId);
		                if (rate == null) {
		        			rs.close();
		        			stmt.close();
		                    throw new Exception("Cannot find GST Id: " + gstId);
		                }
		                gst = amount.multiply(rate);
		            } else if (gstId != null && gstRegistered.equals("N") == true && gst_type.equals("OUTPUT_GST") == true) {
		                // ITSM 15932
		            	BigDecimal rate = gstCodes.get(gstId);
		                if (rate == null) {
		        			rs.close();
		        			stmt.close();
		                    throw new Exception("Cannot find GST Id: " + gstId);
		                }
		                gst = amount.multiply(rate);
		                // ITSM 15932			
		            } else {
		                gst = BigDecimal.ZERO;
		            }
		        }

		        nodeId = rs.getString(2);	//String.valueOf(o : row[2]);

		        if (chargeType.equals("ACC") == true) {
		            sacVer = runPackage.standingVersion;
		        } else {
		            // NDC
		        	ResultDetails acct = nodeSacs.get(nodeId);
	                if (acct == null) {
	        			rs.close();
	        			stmt.close();
	                    throw new Exception("Cannot find SAC ID for Node Id: " + nodeId + " and version: " + runPackage.standingVersion);
	                }
	                sacId = acct.sac;
	                sacVer = acct.sacVer;
		        }

		        // calculation_results
    			stmt1 = conn.prepareStatement(sqlPeriodC);
    			stmt1.setBigDecimal(1, amount.stripTrailingZeros());
    			stmt1.setBigDecimal(2, gst.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros());
    			stmt1.setString(3, settRunId);
    			stmt1.setString(4, npcId);
    			stmt1.setString(5, sacId);
    			stmt1.setString(6, sacVer);
    			stmt1.executeQuery();
    			stmt1.close();
		    }
			rs.close();
			stmt.close();
			
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt1 != null)
					stmt1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void doNonPeriodCharges(String chargeType, String chargeFreq)
		throws Exception {
		
		String msgStep = "RunResultGenerator.doNonPeriodChargesPC()";
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");
		    
		    LocalDate date8 = LocalDate.of(3000, 1, 31);
		    //String infiniteDate = qdf.format(Date.valueOf(date8));
			ZonedDateTime zdt = ZonedDateTime.of(date8.atStartOfDay(), ZoneId.systemDefault());
			DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
			String infiniteDate = sqlFormatter.format(zdt);
		    String nodeId;
		    String sacId;
		    String sacVer;
		    BigDecimal amount;
		    String gstId;
		    BigDecimal gst = BigDecimal.ZERO;
		    String npcId;
		    String debitCredit;
		    String gstRegistered;
		    String gst_type;
		    
		    HashMap<String, BigDecimal> gstCodes = getGstRates(standingVersion);
		    
		    HashMap<String, ResultDetails> nodeSacs = getNodeSACIds(standingVersion);

		    // ITSM 15932
		    // Query npc with taxable
		    String sqlCommand = "select npc.sac_id, npc.nde_id, npc.amount, npc.gst_id, " + 
		    	    "npc.id, npc.debit_credit, sa.gst_registered " + 
		    	    ", DECODE(SUBSTR(gst.NAME,1,1), 'V', 'INPUT_GST', 'A', 'OUTPUT_GST', 'NONE') gst_type " + 
		    	    "from nem_non_period_charges npc, nem_settlement_accounts sa " + 
		    	    ", nem_gst_codes gst " + 
		    	    "where npc.sac_id = sa.id and sa.version = ? and " + 
		    	    "npc.npc_type = ? and npc.charge_frequency = ? " + 
		    	    "and ? between npc.start_date and NVL (npc.end_date, ?) " + 
		    	    "and npc.approval_status = 'A' " + 
		    	    "and npc.gst_id=gst.id " + 
		    	    "and gst.version = ? ";

		    // ITSM 15890
		    // Insert into Periodic Results
		    String sqlPeriodC = "INSERT INTO NEM.NEM_PERIODIC_RESULTS (ID, CALCULATION_RESULT, " + 
		    "GST_AMOUNT, STR_ID, NPC_ID, SAC_ID, SAC_VERSION) VALUES (SYS_GUID(),?,?,?,?,?,?)";
		    
		    // ITSM 15932   
			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, standingVersion);
			stmt.setString(2, chargeType);
			stmt.setString(3, chargeFreq);
			stmt.setString(4, settDate);
			stmt.setString(5, infiniteDate);
			stmt.setString(6, standingVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        npcId = rs.getString(5);	//((String) row[5]);
		        debitCredit = rs.getString(6);	//((String) row[6]);
		        amount = rs.getBigDecimal(3);	//((Decimal) row[3]);

		        if (debitCredit.equals("D")) {
		            amount = amount.negate();
		        }

		        gstId = rs.getString(4);	//String.valueOf(o : row[4]);
		        gstRegistered = rs.getString(7);	//String.valueOf(o : row[7]);
		        gst_type = rs.getString(8);	//String.valueOf(o : row[8]);

		        // ITSM 15932
	            if (gstId != null && gstRegistered.equals("Y") == true) {
	            	BigDecimal rate = gstCodes.get(gstId);
	                if (rate == null) {
	        			rs.close();
	        			stmt.close();
	                    throw new Exception("Cannot find GST Id: " + gstId);
	                }
	                gst = amount.multiply(rate);
	            } else if (gstId != null && gstRegistered.equals("N") == true && gst_type.equals("OUTPUT_GST") == true) {
	                // ITSM 15932
	            	BigDecimal rate = gstCodes.get(gstId);
	                if (rate == null) {
	        			rs.close();
	        			stmt.close();
	                    throw new Exception("Cannot find GST Id: " + gstId);
	                }
	                gst = amount.multiply(rate);
	                // ITSM 15932			
	            } else {
	                gst = BigDecimal.ZERO;
	            }

		        nodeId = rs.getString(2);	//String.valueOf(o : row[2]);
		        sacId = rs.getString(1);	//String.valueOf(o : row[1]);

		        if (chargeType.equals("ACC") == true) {
		            sacVer = runPackage.standingVersion;
		        } else {
		            // NDC
		        	ResultDetails acct = nodeSacs.get(nodeId);
	                if (acct == null) {
	        			rs.close();
	        			stmt.close();
	                    throw new Exception("Cannot find SAC ID for Node Id: " + nodeId + " and version: " + runPackage.standingVersion);
	                }
	                sacId = acct.sac;
	                sacVer = acct.sacVer;
		        }

		        // calculation_results
    			stmt1 = conn.prepareStatement(sqlPeriodC);
    			stmt1.setBigDecimal(1, amount.stripTrailingZeros());
    			stmt1.setBigDecimal(2, gst.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros());
    			stmt1.setString(3, settRunId);
    			stmt1.setString(4, npcId);
    			stmt1.setString(5, sacId);
    			stmt1.setString(6, sacVer);
    			stmt1.executeQuery();
    			stmt1.close();
		    }
			rs.close();
			stmt.close();
			
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt1 != null)
					stmt1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void generatePeriodicCharges()
		throws Exception {
		
		String msgStep = "RunResultGenerator.generatePeriodicCharges()";
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet rs = null;
		
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");
		    
		    LocalDate date8 = LocalDate.of(3000, 1, 31);
		    //String infiniteDate = qdf.format(Date.valueOf(date8));
			ZonedDateTime zdt = ZonedDateTime.of(date8.atStartOfDay(), ZoneId.systemDefault());
			DateTimeFormatter sqlFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT_STR);
			String infiniteDate = sqlFormatter.format(zdt);
		    String nodeId;
		    String sacId;
		    String sacVer = null;
		    String ddnId;
		    BigDecimal amount;
		    String gstId;
		    BigDecimal gst;
		    String npcId;
		    String debitCredit;
		    String gstRegistered;
		    String gst_type;
		    
		    HashMap<String, BigDecimal> gstCodes = getGstRates(standingVersion);
		    
		    HashMap<String, ResultDetails> nodeSacs = getNodeSACIds(standingVersion);
		    
		    HashMap<String, String> Sacs = getDispSACIds();

		    // Generate One-Off charges
		    // 	sqlOneOff as String = "SELECT npc.id, npc.amount, npc.gst_id, npc.nde_id, "
		    // 		+ "npc.sac_Id, npc.ddn_id, npc.debit_credit "
		    // 		+ "FROM nem_non_period_charges npc "
		    // 		+ "WHERE npc.charge_frequency = 'O' "	// Once
		    // 		+ "AND npc.approval_status  = 'A' AND ? "
		    // 		+ "BETWEEN npc.start_date AND NVL (npc.end_date, ? )"
		    // Generate One-Off charges (query with taxable)
		    String sqlOneOff = "SELECT npc.id, npc.amount, npc.gst_id, npc.nde_id, " + 
		    "npc.sac_Id, npc.ddn_id, npc.debit_credit, sa.gst_registered " + 
		    ", DECODE(SUBSTR(gst.NAME,1,1), 'V', 'INPUT_GST', 'A', 'OUTPUT_GST', 'NONE') gst_type " + 
		    "FROM nem_non_period_charges npc, nem_settlement_accounts sa " + 
		    ", nem_gst_codes gst " + 
		    "WHERE npc.sac_id = sa.id and sa.version = ? and " + 
		    "npc.charge_frequency = 'O' " + 
		    "AND npc.approval_status  = 'A' AND ? " + 
		    "BETWEEN npc.start_date AND NVL (npc.end_date, ? ) " + 
		    "and npc.gst_id=gst.id " + 
		    "and gst.version = ?";

		    // ITSM 15932
		    // Insert into Periodic Results
		    String sqlPeriodC = "INSERT INTO NEM.NEM_PERIODIC_RESULTS (ID, CALCULATION_RESULT, " + 
		    "GST_AMOUNT, STR_ID, NPC_ID, SAC_ID, SAC_VERSION) VALUES (SYS_GUID(),?,?,?,?,?,?)";

		    // ITSM 15932
			stmt = conn.prepareStatement(sqlOneOff);
			stmt.setString(1, standingVersion);
			stmt.setString(2, settDate);
			stmt.setString(3, infiniteDate);
			stmt.setString(4, standingVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        npcId = rs.getString(1);	//((String) row[1]);
		        amount = rs.getBigDecimal(2);	//((Decimal) row[2]);
		        gstId = rs.getString(3);	//String.valueOf(o : row[3]);
		        gstRegistered = rs.getString(8);	//String.valueOf(o : row[8]);
		        gst_type = rs.getString(9);	//String.valueOf(o : row[9]);

		        // ITSM 15932
		        // start 2.7.09 move debit credit before passing it to computeGst
		        debitCredit = rs.getString(7);	//String.valueOf(o : row[7]);

		        if (debitCredit.equalsIgnoreCase("D")) {
		            amount = amount.negate();
		        }

		        // end 2.7.09
	            if (gstId != null && gstRegistered.equals("Y") == true) {
	            	BigDecimal rate = gstCodes.get(gstId);
	                if (rate == null) {
	        			rs.close();
	        			stmt.close();
	                    throw new Exception("Cannot find GST Id: " + gstId);
	                }
	                gst = amount.multiply(rate);
	            } else if (gstId != null && gstRegistered.equals("N") == true && gst_type.equals("OUTPUT_GST") == true) {
	                // ITSM 15932
	            	BigDecimal rate = gstCodes.get(gstId);
	                if (rate == null) {
	        			rs.close();
	        			stmt.close();
	                    throw new Exception("Cannot find GST Id: " + gstId);
	                }
	                gst = amount.multiply(rate);
	                // ITSM 15932			
	            } else {
	                gst = BigDecimal.ZERO;
		        }

		        nodeId = rs.getString(4);	//String.valueOf(o : row[4]);
		        sacId = rs.getString(5);	//String.valueOf(o : row[5]);
		        ddnId = rs.getString(6);	//String.valueOf(o : row[6]);

		        if (nodeId != null && sacId == null && ddnId == null) {
		            // Node
		        	ResultDetails acct = nodeSacs.get(nodeId);
	                if (acct == null) {
	        			rs.close();
	        			stmt.close();
	                    throw new Exception("Cannot find SAC ID for Node Id: " + nodeId + " and version: " + runPackage.standingVersion);
	                }
	                sacId = acct.sac;
	                sacVer = acct.sacVer;
		        } else if (sacId != null && nodeId == null && ddnId == null) {
		            // Account
		            sacVer = runPackage.standingVersion;
		        } else if (ddnId != null && sacId == null && nodeId == null) {
		            // Dispute
		        	sacId = Sacs.get(ddnId);
		            if (sacId == null) {
	        			rs.close();
	        			stmt.close();
		                throw new Exception("Cannot find SAC ID for Dispute Id: " + ddnId);
		            }

		            sacVer = runPackage.standingVersion;
		        }

		        // calculation_results
    			stmt1 = conn.prepareStatement(sqlPeriodC);
    			stmt1.setBigDecimal(1, amount.stripTrailingZeros());
    			stmt1.setBigDecimal(2, gst.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros());
    			stmt1.setString(3, settRunId);
    			stmt1.setString(4, npcId);
    			stmt1.setString(5, sacId);
    			stmt1.setString(6, sacVer);
    			stmt1.executeQuery();
    			stmt1.close();
		    }

		    // Generate Daily Periodic Charges
		    // ITSM 15890 - resolve issues for Fees GST
		    doNonPeriodChargesPC("ACC", "D");

		    doNonPeriodCharges("NDC", "D");

		    // Generate Monthly Periodic Charges
		    LocalDate ldate = sqlSettDate.toLocalDate();
		    if (ldate.getDayOfMonth() == 1) {
		        doNonPeriodCharges("ACC", "M");

		        doNonPeriodCharges("NDC", "M");
		    }
			
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt1 != null)
					stmt1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateMPReports()
			throws Exception {
			
		String msgStep = "RunResultGenerator.generateMPReports()";
		PreparedStatement stmt = null;
		ResultSet rs = null;
			
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");

		    Timestamp startTime = new Timestamp(System.currentTimeMillis());
			
		    // [ITSM-12670]
		    // generatorAccount as Bool
		    boolean adjustmentExist = false;
		    String sacId;
		    /* account name from account entity*/
		    String col2footerEUA;
		    /* account_EUA from account entity*/
		    String col2footerBRC;
		    /* account_FCC from account entity*/
		    String col2footerBRV;
		    /* account_RCC from account entity*/
		    String col8footerFSD;
		    /* account_FSD from account entity*/
		    String col2footerBEC;
		    /* account BESC from account entity*/
		    String col2footerGEN2;
		    /* account_GESC from account entity*/
		    String col4footerGEN2;
		    /* account_LESD from account entity*/
		    String col2footerGEN1;
		    /* account_GESC from account entity*/
		    String col4footerGEN1;
		    /* account_LESD from account entity*/
		    String col6footerRSC;
		    /* account_RSC from account entity*/
		    String col6footerRSD;
		    /* account_RSD from account entity*/
		    String col2footerRSV;
		    /* account_NRSC from account entity*/
		    String col2footerRSVC;
		    /* account_RSC from account entity*/
		    String col2footerRSVCDPL;
		    /* account_facility_RSC from account entity*/
		    String col7footerNEAD;
		    /* account_NEAD from account entity*/
		    String col6footerNFSC1;
		    /* account_NFSC from account entity*/
		    String col6footerNFSC2;
		    /* account_NFSC from account entity*/
		    String col6footerNRSCGEN;
		    /* account_NRSC from account entity*/
		    String col2footerNTRSC;
		    /* account_NTSC from account entity*/
		    String col2footerREG;
		    /* account_NFSC from account entity*/
		    String col2footerREGC;
		    /* account_FSC from account entity*/
		    String col2footerVEST;
		    /* account_VCSC from account entity*/
		    String col2footerForwardSales;

		    // Added for Forward Sales Implementation [ITSM-16708]
		    /* account_FSSC from account entity*/
		    String col2footerOTHER;
		    /* account_other_total from account entity*/
		    String col11footerNEAA;
		    /* account_NEAA from account entity */
		    String marketStrId;
		    String market_number;
		    String interval_account;
		    String interval_number;
		    String node_account;
		    String node_name;
		    String node_interval;
		    String reserve_node_name;
		    String reserve_class_name;
		    String reserve_interval;
		    String reserve_account;
		    String class_name;
		    String class_interval;
		    String ftr_node;
		    String ftr_interval;
		    String vesting_account;
		    String vesting_number;
/*		    String forwardsales_account;

		    // added for Forward Sales Implementation [ITSM-16708]
		    String forwardsales_number;

*/		    // added for Forward Sales Implementation [ITSM-16708]
		    String brqAccount;
		    String tvc_account;
		    String clawBackFlag;
		    clawBackFlag = "N";

		    // [ITSM-12670]
		    String tvc_number;

		    // [ITSM-12670]
//		    String adjust_total_rrstr_id;
//		    String adjust_total_sacId;
//		    String adjustment_rrstr_id;
//		    String prev_adjustment_rrstr_id = "";
//		    String adjustment_name;
//		    String prev_adjustment_name = "";
//		    String adjustment_interval;
//		    String rerun_id = null;
//		    int gst_rate;
		    String ignoreNegIEQ;
		    int vestingRecordCount;
/*		    int forwardSalesRecordCount;

*/		    // Added for Forward Sales implementation [ITSM-16708]
		    // {ITSM-15086]
		    // [ITSM-15086] Get the LNG Rules effective date
		    Date LngRulesEffectiveDate = UtilityFunctions.getSysParamTime(ds, "LNG_VEST_EFFECTIVE_DATE");

		    // RM00000510 Net AFP Rule change
		    Date NetAFPChangeEffective = UtilityFunctions.getSysParamTime(ds, "MOD_GEN_FEQ_EFFECT_DATE");
		    boolean AccountIsResidential = false;

		    // nrscReportGenerated as Bool
//		    String prev_settDateStr;
//		    String prev_interval_number;
//		    String sqlCommand;
//		    String sqlCommand1;
		    /*Any[] params1;
		    Any[] params2;
		    Any[] params3;
		    Any[] params4;
		    Any[] acg_effectiveness_param;*/

		    // 2.7.05 Changes start here - Array variable declaration
		    HashMap<String, String> acg_effectiveness_search_map = new HashMap<>();

		    // 2.7.09 Add SAC_ID into search map
		    HashMap<String, String> sac_id_effectiveness_search_map = new HashMap<>();
		    //Object[int] acg_effectiveness_array;
		    final int KEY_NODE_NAME = 0;
		    final int KEY_RESERVE_CLASS_NAME = 1;

		    // 2.7.09 Add SAC_ID into array
		    final int KEY_SAC_ID = 2;

		    // 2.7.05 Populating 2 dimensional array
		    String sqlCmdAcgEffectiveness = "SELECT   nde.NAME, " + 
		    "         acg.NAME reserve_class_type, " + 
		    "         nde.SAC_ID " + 
		    "    FROM nem_ancillary_providers acp, " + 
		    "         nem_ancillary_groups acg, " + 
		    "         nem_facilities fct, " + 
		    "         nem_group_effectiveness ge, " + 
		    "         nem_nodes nde " + 
		    "   WHERE acp.acg_id = acg.ID " + 
		    "     AND acp.acg_version = acg.VERSION " + 
		    "     AND fct.ID = acp.fct_id " + 
		    "     AND fct.VERSION = acp.fct_version " + 
		    "     AND ge.acg_id = acg.ID " + 
		    "     AND ge.acg_version = acg.VERSION " + 
		    "     AND acg.VERSION = ? " + 
		    "     AND acg.ancillary_type = 'RSV' " + 
		    "     AND fct.nde_id = nde.ID " + 
		    "     AND fct.nde_version = nde.VERSION " + 
		    " ORDER BY  nde.NAME";

		    // acg_effectiveness_printout_string as String = "acg_effectiveness_printout_string: \n" // for logging array value
		    int acgIndex = 0;

			stmt = conn.prepareStatement(sqlCmdAcgEffectiveness);
			stmt.setString(1, sacVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        // acg_effectiveness_printout_string = acg_effectiveness_printout_string + String.valueOf(o : acg_effectiveness_result[1]) + " " + String.valueOf(o : acg_effectiveness_result[2]) + "\n"   // for logging array value
		        String[] acg_effectiveness_info = new String[3];
		        acg_effectiveness_info[KEY_NODE_NAME] = rs.getString(1);	//String.valueOf(o : acg_effectiveness_result[1]);
		        acg_effectiveness_info[KEY_RESERVE_CLASS_NAME] = rs.getString(2);	//String.valueOf(o : acg_effectiveness_result[2]);

		        // 2.7.09 Add SAC_ID into array
		        acg_effectiveness_info[KEY_SAC_ID] = rs.getString(3);	//String.valueOf(o : acg_effectiveness_result[3]);
		        //acg_effectiveness_array[acgIndex] = acg_effectiveness_info;

		        // build a search map based on Node Name and Class Name
		        acg_effectiveness_search_map.put(acg_effectiveness_info[KEY_NODE_NAME] + " " + acg_effectiveness_info[KEY_RESERVE_CLASS_NAME], 
		                                         acg_effectiveness_info[KEY_SAC_ID]);

		        // 2.7.09 Add SAC_ID into search map
		        sac_id_effectiveness_search_map.put(acg_effectiveness_info[KEY_SAC_ID], 
		                                            acg_effectiveness_info[KEY_SAC_ID]);

		        acgIndex = acgIndex + 1;
		    }
			rs.close();
			stmt.close();

		    // logMessage acg_effectiveness_printout_string  // for logging array value
		    // 2.7.05 Changes end here
		    // EUA
/*		    String sqlCmd1EUA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, COLUMN_16, COLUMN_17, COLUMN_18, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'EUAHEADER',?," + 
		    "'Period','Total LCSC ($)','HEUA','HLCU ($/MWh)','HEUR ($/MWh)','HEUC ($/MWh)','MEUC ($/MWh)','Total WDQ (MWh)','WDQ (MWh)','Total WEQ (MWh)','WEQ (MWh)'," + 
		    "'Total WMQ (MWh)','WMQ (MWh)','HLCSA','HERSA','HEUSA','MEUSA','Energy Uplift Amount',?,?,?,? )";
		    String sqlCmd2EUA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, COLUMN_16, COLUMN_17, COLUMN_18, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'EUA',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3EUA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, COLUMN_16, COLUMN_17, COLUMN_18, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'EUATOTAL',?,'Total Energy Uplift',?,'','','','','','','','','','','','','','','','',?  ,?,?,? )";

		    // BRC
		    String sqlCmd1BRC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'BRCHEADER',?," + 
		    "'Period','BFQ Purchased','BFQ Sold','MFP ($/MWh)','FCC','','','','','','','','','',?,?,?,? )";
		    String sqlCmd2BRC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'BRC',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3BRC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'BRCTOTAL',?,'Total Regulation Contract Credit for Run'," + 
		    "?,'','','','','','','','','','','','',?,?,?,? )";

		    // CNMEA
		    String sqlCmd1CNMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'CNMEAHDR',?," + 
		    "'Run Type','Settlement Date','Period','GMEE','GMEF','LMEE','LMEF','NMEA','','','','','','',?,?,?,? )";
		    String sqlCmd2CNMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'CNMEA',?,?,?,?,?,?,?,?,?,'','','','','','',?,?,?,? )";
		    String sqlCmd3CNMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'CNMEASUB',?,'','',?,?,?,?,?,?,'','','','','','',?,?,?,? )";
		    String sqlCmd4CNMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'CNMEATOTAL',?,'','',?,?,?,?,?,?,'','','','','','',?,?,?,? )";

		    // BRV
		    String sqlCmd1BRV = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'BRVHEADER',?," + 
		    "'Period','Reserve Class','BRQ Purchased (MWh)','BRQ Sold (MWh)','MRP ($/MWh)','RCC','','','','','','','','',?,?,?,? )";
		    String sqlCmd2BRV = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'BRV',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3BRV = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'BRVTOTAL',?," + 
		    "'Total Bilateral Reserve Contract Settlement Credit for Run'," + 
		    "?,'','','','','','','','','','','','',?,?,?,? )";

		    // FSD
		    String sqlCmd1FSD = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'FSDHEADER',?," + 
		    "'Period','MNN','Total FSC','Total Market FEQ (MWh)','AFP ($/MWh)','Gen FEQ (MWh)','Load FEQ (MWh)','FSD','','','','','','',?,?,?,? )";
		    String sqlCmd2FSD = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'FSD',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3FSD = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'FSDTOTAL',?,'Total Regulation Settlement Debit'," + 
		    "'','','','','','',?,'','','','','','',?,?,?,? )";

		    // BEC
		    String sqlCmd1BEC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'BEQHEADER',?," + 
		    "'Period','Contract Node','BAQ Purchased (MWh)','BAQ Sold (MWh)','BWF Purchased (MWh)','BWF Sold (MWh)'," + 
		    "'BIF Purchased (MWh)','BIF Sold (MWh)','USEP ($/MWh)','BESC','','','','',?,?,?,? )";
		    String sqlCmd2BEC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'BEQ',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3BEC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'BEQTOTAL',?,'Total Bilateral Contract Settlement Credit'," + 
		    "?,'','','','','','','','','','','','',?,?,?,? )";

		    // GEN2
		    String sqlCmd1GEN2 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'GENHEADER',?," + 
		    "'Period','MNN','IEQ (MWh)','MEP ($/MWh)','GESCN','GESCP','GESCE','WEQ (MWh)','USEP ($/MWh)','LESDP','LESDN','','','',?,?,?,? )";
		    String sqlCmd2GEN2 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'GENLOAD',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3GEN2 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'GENTOTAL',?,'Total GESC',?,'Total LESD',?,'','','','','','','','','','',?,?,?,? )";

		    // GEN1
		    String sqlCmd1GEN1 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'GENHEADER',?," + 
		    "'Period','MNN','IEQ (MWh)','MEP ($/MWh)','GESCN','GESCP','GESCE','WEQ (MWh)','USEP ($/MWh)','LESDP','LESDN','','','',?,?,?,? )";
		    String sqlCmd2GEN1 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'GENLOAD',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3GEN1 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'GENLOAD',?,?,'Total','','',?,?,?,?,'',?,?,?,?,?,?,?,?,? )";
		    String sqlCmd4GEN1 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'GENTOTAL',?,'Total GESC',?,'Total LESD',?,'','','','','','','','','','',?,?,?,? )";

		    // LCSC
		    String sqlCmd1LCSC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'LCSCHEADER',?," + 
		    "'Period','Facility','OIEC (MWh)','SIEC (MWh)','WLQ (MWh)','LCQ (MWh)','LCP ($/MWh)','LCSCP','LCSC','Compliant','Non Compliant','Partial Compliant','','',?,?,?,? )";
		    String sqlCmd2LCSC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'LCSC',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3LCSC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'LCSCSUB',?,?,'Total','','',?,?,?,?,?,'',?,?,?,?,?,?,?,? )";
		    String sqlCmd4LCSC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'LCSCTOTAL',?,'Total LCSC','','','','','','','',?,'','','','','',?,?,?,? )";

		    // RSC
		    String sqlCmd1RSC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'RSCHEADER',?," + 
		    "'Period','MNN','Reserve Class','MRP ($/MWh)','GRQ + LRQ (MWh)','RSC','','','','','','','','','Fail to Provide',?,?,?,? )";
		    String sqlCmd2RSC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'RSC',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3RSC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'RSCTOTAL',?,'Total Reserve Settlement Credit'," + 
		    "'','','','',?,'','','','','','','','','',?,?,?,? )";

		    // RSD
		    String sqlCmd1RSD = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'RSDHEADER',?," + 
		    "'Period','MNN','Reserve Class','Total RSC','RRS','RSD','','','','','','','','',?,?,?,? )";
		    String sqlCmd2RSD = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'RSD',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3RSD = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'RSDTOTAL',?,'Total Net Reserve Settlement Debit'," + 
		    "'','','','',?,'','','','','','','','',?,?,?,? )";

		    // RSV
		    String sqlCmd1RSV = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'RSVHEADER',?," + 
		    "'Period','Reserve Class','Total RSC','RRS','RSD','RSC','RCC','NRSC','','','','','','',?,?,?,? )";
		    String sqlCmd2RSV = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'RESERVE',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3RSV = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'RSVTOTAL',?,'Report Total',?,'','','','','','','','','','','','',?,?,?,? )";

		    // RSVC
		    String sqlCmd1RSVC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'RSVCHEADER',?," + 
		    "'Period','Reserve Class','MRP ($/MWh)','GRQ + LRQ (MWh)','RSC','','','','','','','','','',?,?,?,? )";
		    String sqlCmd2RSVC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'RESERVECR',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3RSVC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'RSVCTOTAL',?,'Total Reserve Settlement Credit'," + 
		    "?,'','','','','','','','','','','','',?,?,?,? )";

		    // RSVCDPL
		    String sqlCmd1RSVCDPL = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'RSVCDPLHR',?," + 
		    "'Period','Reserve Class','Facility','MRP ($/MWh)','LRQ (MWh)','RSC','','','','','','','','','Fail to Provide',?,?,?,? )";
		    String sqlCmd2RSVCDPL = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'RSVDPLCR',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3RSVCDPL = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'RSVCDPLTOT',?,'Total Reserve Settlement Credit'," + 
		    "?,'','','','','','','','','','','','','',?,?,?,? )";

		    // NEAA
		    String sqlCmd1NEAA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'NEAAHEADER',?," + 
		    "'Period','MNN','IEQ (MWh)','Total IEQ (MWh)','WPQ (MWh)','MEP ($/MWh)','USEP ($/MWh)','HEUC ($/MWh)','NELC','NEGC','NEAA','','','',?,?,?,? )";
		    String sqlCmd2NEAA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NEAA',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3NEAA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NEAASUB',?,?,'Total','','','','','','','','',?,'','','',?,?,?,? )";
		    String sqlCmd4NEAA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NEAATOTAL',?,'','Total NEAA: ','','','','','','','','',?,'','','',?,?,?,? )";

		    // NEAD
		    String sqlCmd1NEAD = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'NEADHEADER',?," + 
		    "'Period','NEAA','Total WEQ (MWh)','SUM Embedded Gen (MWh)','WEQ (MWh)','Embedded Gen(MWh)'," + 
		    "'NEAD','','','','','','','',?,?,?,? )";
		    String sqlCmd2NEAD = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NEAD',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3NEAD = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NEADTOTAL',?,'','Total NEAD:','','','','',?,'','','','','','','',?,?,?,? )";

		    // NFSC1
		    String sqlCmd1NFSC1 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'NFSCHDR',?," + 
		    "'Period','MNN','FSC','FSD','FCC','NFSC','','','','','','','','',?,?,?,? )";
		    String sqlCmd2NFSC1 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NFSC',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3NFSC1 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NFSCTOTAL',?,'Total Net Regulation Settlement Credit'," + 
		    "'','','','',?,'','','','','','','','',?,?,?,? )";

		    // NFSC2
		    String sqlCmd1NFSC2 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'NFSCHEADER',?," + 
		    "'Period','MNN','FSC','FSD','FCC','NFSC','','','','','','','','',?,?,?,? )";
		    String sqlCmd2NFSC2 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NFSC',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3NFSC2 = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NFSCTOTAL',?,'Total Net Regulation Settlement Credit'," + 
		    "'','','','',?,'','','','','','','','',?,?,?,? )";

		    // NRSCGEN
		    String sqlCmd1NRSCGEN = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'NRSCHDR',?," + 
		    "'Period','MNN','RSC','RCC','RSD','NRSC','','','','','','','','',?,?,?,? )";
		    String sqlCmd2NRSCGEN = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NRSC',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3NRSCGEN = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NRSCTOTAL',?,'Total Net Reserve Settlement Credit'," + 
		    "'','','','',?,'','','','','','','','',?,?,?,? )";

		    // NTRSC
		    String sqlCmd1NTRSC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'NTRSCHEAD',?," + 
		    "'Period','Contract Node','Qty (MWh)','USEP ($/MWh)','MEP ($/MWh)','NTSC','','','','','','','','',?,?,?,? )";
		    String sqlCmd2NTRSC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NTRSC',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3NTRSC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'NTRSCTOTAL',?,'Total Net Transmission Rights Settlement Credit for Run'," + 
		    "?,'','','','','','','','','','','','',?,?,?,? )";

		    // REG
		    String sqlCmd1REG = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'REGHEADER',?," + 
		    "'Period','Total FSC','Total Market FEQ (MWh)','FEQ (MWh)','AFP ($/MWh)','FSD','FSC','FCC'," + 
		    "'NFSC','','','','','',?,?,?,? )";
		    String sqlCmd2REG = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'REG',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3REG = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'REGTOTAL',?,'Total Net Regulation Settlement Credit for Run'," + 
		    "?,'','','','','','','','','','','','',?,?,?,? )";

		    // REGC
		    String sqlCmd1REGC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'REGCHEADER',?," + 
		    "'Period','Node','MFP ($/MWh)','GFQ (MWh)','FSC','','','','','','','','','','Fail to Provide',?,?,?,? )";
		    String sqlCmd2REGC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'REGCR',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3REGC = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'REGCTOTAL',?,'Total Regulation Credit for Run'," + 
		    "?,'','','','','','','','','','','','','',?,?,?,? )";

		    // VEST
		    String sqlCmd1VEST = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'VESTHEADER',?," + 
		    "'Period','VCRP ($/MWh)','VCP ($/MWh)','VCQ (MWh)','VCSC','Total Vesting Contracts'," + 
		    "'Contract','MSSL VCSC','','','','','','',?,?,?,? )";
*/		    
		    String sqlCmd2VEST = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'VEST',?,?,?,?,?,?,'',?,?,'','','','','','',?,?,?,? )";
/*		    String sqlCmd3VEST = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'VESTOTAL',?,'Total Vesting Contract Amount for Settlement Run'," + 
		    "?,'','','','','','','','','','','','',?,?,?,? )";
*/
/*		    // Begin Forward Sales Implementation
		    String sqlCmd1ForwardSales = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'FSCHEADER',?," + 
		    "'Period','FSRP ($/MWh)','FSP ($/MWh)','FSQ (MWh)','FSSC','Total Forward Sales Contracts'," + 
		    "'Contract','MSSL FSSC','','','','','','',?,?,?,? )";
		    String sqlCmd2ForwardSales = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'FSC',?,?,?,?,?,?,'',?,?,'','','','','','',?,?,?,? )";
		    String sqlCmd3ForwardSales = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'FSCTOTAL',?,'Total Forward Sales Contract Amount for Settlement Run'," + 
		    "?,'','','','','','','','','','','','',?,?,?,? )";

*/		    // End Forward Sales Implementation
		    // FEES
		    // [ITSM15890]
/*		    String sqlCmd1FEES = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'FEESHEADER',?," + 
		    "'Settlement Date','Participant','Period','EMC Price Cap MW Charge ($/MWh)','EMC Price Adj MW Charge ($/MWh)','EMC ADMIN MW Charge ($/MWh)','PSO ADMIN MW Charge ($/MWh)','Traded Energy (MWh)'," + 
		    "'Total Traded Energy (MWh)','EMC Price Cap Charge ($)','EMC Price Adj Charge ($)','PSO ADMIN Charge ($)','Total ($)','',?,?,?,? )";
		    String sqlCmd3FEES = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID()," + 
		    "'EMCPSOFEES',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
*/
		    logger.info(logPrefix + "Generating Market Participant Reports Post EG for Clawback and Zero RSV RSC ...");

		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Generating Market Participant Reports Post EG for Clawback and Zero RSV RSC ...", 
		                                   "");

		    ignoreNegIEQ = UtilityFunctions.getSysParamVarChar(ds, "IGNORE_NEG_IEQ");

		    // ***************************************
		    // get DR accounts' id
		    HashMap<String, Boolean> accountDR = getDrSacIds(standingVersion);
		    HashMap<String,String> nodeDR = getDrNdeIds(standingVersion);
		    
		    SimpleDateFormat feesDf = new SimpleDateFormat("dd MMM yyyy");
		    SimpleDateFormat mnmeaDf = new SimpleDateFormat("dd-MMM-yyyy");

		    // /////////////////////////////////////Preparing the Clawback Array/////////////////////////////
		    String sqlCmdClawBack = "SELECT clq.period, nde.NAME, clq.ancillary_type, " + 
		    "    CASE                                        " + 
		    "       WHEN clq.ancillary_type = 'REG'          " + 
		    "          THEN 'GFQ'                            " + 
		    "       WHEN (clq.ancillary_type IN ('PRIRES', 'SECRES', 'CONRES')  AND nde.NODE_TYPE <> 'L') " + 
		    "          THEN 'GRQ'                            " + 
		    "       WHEN (clq.ancillary_type IN ('PRIRES', 'SECRES', 'CONRES')  AND nde.NODE_TYPE = 'L') " + 
		    "          THEN 'LRQ'                            " + 
		    "    END CASE                                    " + 
		    " FROM nem_clawback_quantities clq, nem.nem_nodes nde " + 
		    " WHERE clq.nde_version=?" + 
		    "   AND clq.settlement_date=?" + 
		    "   AND clq.nde_id = nde.ID                          " + 
		    "   AND clq.nde_version = nde.VERSION                " + 
		    "   AND clq.record_type<>'EMPTY'                     " + 
		    "   AND clq.VERSION =                                " + 
		    "       (SELECT TO_CHAR( MAX( TO_NUMBER( pkg_inner.VERSION ))) " + 
		    "          FROM   pav_packages pkg_inner, pav_package_types pkt_inner  " + 
		    "         WHERE pkg_inner.pkt_id = pkt_inner.ID AND pkt_inner.NAME = 'SETTLEMENT_CLAWBACK_QUANTITIES' " + 
		    "           AND clq.settlement_date BETWEEN pkg_inner.effective_date AND pkg_inner.end_date) ";
		    HashMap<Integer, String> clawbackPeriodArray = new HashMap<>();
		    HashMap<Integer, String> clawbackNodeNameArray = new HashMap<>();
		    HashMap<Integer, String> clawbackAncillaryTypeArray = new HashMap<>();
		    HashMap<Integer, String> clawbackQuantityTypeArray = new HashMap<>();
		    int clawbackIndex = 0;

			stmt = conn.prepareStatement(sqlCmdClawBack);
			stmt.setString(1, sacVersion);
			stmt.setString(2, settDate);
			stmt.executeQuery();
			rs = stmt.getResultSet();
	
			while (rs.next()) {
		        clawbackPeriodArray.put(clawbackIndex, rs.getString(1));	//String.valueOf(o : row[1]);
		        clawbackNodeNameArray.put(clawbackIndex, rs.getString(2));	//String.valueOf(o : row[2]);
		        clawbackAncillaryTypeArray.put(clawbackIndex, rs.getString(3));	//String.valueOf(o : row[3]);
		        clawbackQuantityTypeArray.put(clawbackIndex, rs.getString(4));	//String.valueOf(o : row[4]);
		        clawbackIndex = clawbackIndex + 1;
		    }
			rs.close();
			stmt.close();

		    // //////////////////////////////////////////////////////////////////////////////////////////////
		    // /////////////////////////////////////////////////
		    // GENERATE MARKET PARTICIPANT REPORTS
		    // /////////////////////////////////////////////////		
			stmt = conn.prepareStatement(commonSqlCmd);
		    for (Account sac : sortAccount) {
		        // generatorAccount = false
		        sacId = sac.getAccountId();

		        // account_name
		        logger.info(logPrefix + "Inserting data into NEM_ACCOUNT_STATEMENTS (MP Reports - Post EG for Clawback and Zero RSV RSC) for Account: " + sac.getDisplayTitle());

		        // [ITSM-15086] reset the vesting record count
		        vestingRecordCount = 0;

/*		        // reset the forward sales record count
		        forwardSalesRecordCount = 0;
*/
		        // EUA HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "EUAHEADER", seq,
					    "Period","Total LCSC ($)","HEUA","HLCU ($/MWh)","HEUR ($/MWh)","HEUC ($/MWh)","MEUC ($/MWh)","Total WDQ (MWh)","WDQ (MWh)","Total WEQ (MWh)",
					    "WEQ (MWh)","Total WMQ (MWh)","WMQ (MWh)","HLCSA","HERSA","HEUSA","MEUSA","Energy Uplift Amount",
						"","",
						settRunId, sacId, sacVersion, settDate);

		        // BRC HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "BRCHEADER", seq,
					    "Period","BFQ Purchased","BFQ Sold","MFP ($/MWh)","FCC",
						"","","","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // BRV HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "BRVHEADER", seq,
					    "Period","Reserve Class","BRQ Purchased (MWh)","BRQ Sold (MWh)","MRP ($/MWh)","RCC",
						"","","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // FSD HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "FSDHEADER", seq,
					    "Period","MNN","Total FSC","Total Market FEQ (MWh)","AFP ($/MWh)","Gen FEQ (MWh)","Load FEQ (MWh)","FSD",
						"","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // BEC HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "BEQHEADER", seq,
					    "Period","Contract Node","BAQ Purchased (MWh)","BAQ Sold (MWh)","BWF Purchased (MWh)","BWF Sold (MWh)",
						"BIF Purchased (MWh)","BIF Sold (MWh)","USEP ($/MWh)","BESC",
						"","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // GEN1 HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "GENHEADER", seq,
					    "Period","MNN","IEQ (MWh)","MEP ($/MWh)","GESCN","GESCP","GESCE","WEQ (MWh)","USEP ($/MWh)","LESDP","LESDN",
						"","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // LCSC HEADER
		        if (accountDR.containsKey(sacId) == true) {
		            seq = seq + 1;
					prepareAccountStatement(stmt, "LCSCHEADER", seq,
						    "Period","Facility","OIEC (MWh)","SIEC (MWh)","WLQ (MWh)","LCQ (MWh)","LCP ($/MWh)","LCSCP","LCSC","Compliant","Non Compliant","Partial Compliant",
							"","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // RSC HEADER
		        // if (Decimal(accountLine[71])) != 0 then
		        // 2.7.09 check whether the SAC_ID is included because of group effectiveness
		        if (sac.getRsc().signum() != 0 || sac_id_effectiveness_search_map.containsKey(sac.getAccountId()) == true) {
		            seq = seq + 1;
					prepareAccountStatement(stmt, "RSCHEADER", seq,
						    "Period","MNN","Reserve Class","MRP ($/MWh)","GRQ + LRQ (MWh)","RSC","","","","","","","","","Fail to Provide",
							"","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // RSD HEADER
		        if (sac.getRsd().signum() != 0) {
		            seq = seq + 1;
					prepareAccountStatement(stmt, "RSDHEADER", seq,
						    "Period","MNN","Reserve Class","Total RSC","RRS","RSD",
							"","","","","","","","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // RSVCDPL HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "RSVCDPLHR", seq,
					    "Period","Reserve Class","Facility","MRP ($/MWh)","LRQ (MWh)","RSC","","","","","","","","","Fail to Provide",
						"","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // NEAA HEADER
		        if (sac.isPriceNeutralization() == true) {
		            seq = seq + 1;
					prepareAccountStatement(stmt, "NEAAHEADER", seq,
						    "Period","MNN","IEQ (MWh)","Total IEQ (MWh)","WPQ (MWh)","MEP ($/MWh)","USEP ($/MWh)","HEUC ($/MWh)","NELC","NEGC","NEAA",
							"","","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // NEAD HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "NEADHEADER", seq,
					    "Period","NEAA","Total WEQ (MWh)","SUM Embedded Gen (MWh)","WEQ (MWh)","Embedded Gen(MWh)","NEAD",
						"","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // NFSC1 HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "NFSCHDR", seq,
					    "Period","MNN","FSC","FSD","FCC","NFSC",
						"","","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // NRSCGEN HEADER
		        if (sortNode1.containsKey(sacId) == true) {
		            seq = seq + 1;
					prepareAccountStatement(stmt, "NRSCHDR", seq,
						    "Period","MNN","RSC","RCC","RSD","NRSC",
							"","","","","","","","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // NTRSC HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "NTRSCHEAD", seq,
					    "Period","Contract Node","Qty (MWh)","USEP ($/MWh)","MEP ($/MWh)","NTSC",
						"","","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // REGC HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "REGCHEADER", seq,
					    "Period","Node","MFP ($/MWh)","GFQ (MWh)","FSC","","","","","","","","","","Fail to Provide",
						"","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // FEES HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "FEESHEADER", seq,
					    "Settlement Date","Participant","Period","EMC Price Cap MW Charge ($/MWh)","EMC Price Adj MW Charge ($/MWh)","EMC ADMIN MW Charge ($/MWh)","PSO ADMIN MW Charge ($/MWh)","Traded Energy (MWh)", 
						"Total Traded Energy (MWh)","EMC Price Cap Charge ($)","EMC Price Adj Charge ($)","PSO ADMIN Charge ($)","Total ($)",
						"","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // VEST HEADER
		        // For Forward Sales Implementation FORWARD SALES HEADER
		        if (sortVesting1.containsKey(sacId) == true) {
		            seq = seq + 1;
					prepareAccountStatement(stmt, "VESTHEADER", seq,
						    "Period","VCRP ($/MWh)","VCP ($/MWh)","VCQ (MWh)","VCSC","Total Vesting Contracts",
							"Contract","MSSL VCSC",
							"","","","","","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // For Forward Sales Implementation FORWARD SALES HEADER
		        if (params.isFSCEffective == true && sortForwardSales1.containsKey(sacId) == true) {
		            seq = seq + 1;
					prepareAccountStatement(stmt, "FSCHEADER", seq,
						    "Period","FSRP ($/MWh)","FSP ($/MWh)","FSQ (MWh)","FSSC","Total Forward Sales Contracts",
							"Contract","MSSL FSSC",
							"","","","","","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // CNMEA HEADER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "CNMEAHDR", seq,
					    "Run Type","Settlement Date","Period","GMEE","GMEF","LMEE","LMEF","NMEA",
						"","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // CNMEA DETAILS & SUB TOTAL
		        /*prev_settDateStr = null;

		        for (Adjustment recadj : sortAdjustment) {

		            if (recadj == null) {
		                logger.warn(logPrefix + "Adjustment is empty or contain invalid data.");

		                break;
		            }

		            adjustmentExist = true;
		            adjustment_name = recadj.getName();
		            adjustment_rrstr_id = recadj.getSettId();
		            adjustment_interval = recadj.getPeriodId();

		            if (adjustment_name.equals(sacId) == true) {
		                // CNMEA SUB TOTAL
		                if (prev_settDateStr != null && recadj.getSettDate().toString().equals(prev_settDateStr) == false) {
		                    //for (Cnmea cnmea : sortAdjustTotal) {
		                    	
		                    	Cnmea cnmea = data.getCnmea().get(recadj.getSettId() + sacId);
		                        if (cnmea == null) {
		                        	conn.close();
		                            throw new Exception("Adjust Total CSV File is Empty !!!");
		                        }

		                        //adjust_total_rrstr_id = cnmea.getSettId();	//adjust_totalLine[5];
		                        //adjust_total_sacId = cnmea.getAccountId();	//adjust_totalLine[6];

		                        //if (adjust_total_rrstr_id.equals(prev_adjustment_rrstr_id) && adjust_total_sacId.equals(prev_adjustment_name)) {
		                            seq = seq + 1;
		            				stmt = conn.prepareStatement(sqlCmd3CNMEA);
		            				stmt.setInt(1, seq);
		            				stmt.setString(2, "Sub-Total");
		            				stmt.setString(3, cnmea.getGmee().toString());
		            				stmt.setString(4, cnmea.getAccountingGmef().toString());
		            				stmt.setString(5, cnmea.getAccountingLmee().toString());
		            				stmt.setString(6, cnmea.getAccountingLmef().toString());
		            				stmt.setString(7, cnmea.getNmea().toString());
		            				stmt.setString(8, settRunId);
		            				stmt.setString(9, sacId);
		            				stmt.setString(10, sacVersion);
		            				stmt.setString(11, settDate);
		            				stmt.executeQuery();
		            				stmt.close();

		                        //    break;
		                        //}

		                        // if adjust_total
		                    //}

		                    // for each adjust_total
		                }

		                // if prev_settDateStr
		                // CNMEA DETAIL
		                seq = seq + 1;
		                Date dt = new Date(recadj.getSettDate().toGregorianCalendar().getTime().getTime());
		                
        				stmt = conn.prepareStatement(sqlCmd2CNMEA);
        				stmt.setInt(1, seq);
        				stmt.setString(2, recadj.getRunType());
        				stmt.setString(3, mnmeaDf.format(dt).toUpperCase());
        				stmt.setString(4, Integer.valueOf(recadj.getPeriodId()).toString());
        				stmt.setString(5, recadj.getGmee().toString());
        				stmt.setString(6, recadj.getAccountingGmef().toString());
        				stmt.setString(7, recadj.getAccountingLmee().toString());
        				stmt.setString(8, recadj.getAccountingLmef().toString());
        				stmt.setString(9, recadj.getNmea().toString());
        				stmt.setString(10, settRunId);
        				stmt.setString(11, sacId);
        				stmt.setString(12, sacVersion);
        				stmt.setString(13, settDate);
        				stmt.executeQuery();
        				stmt.close();

		                prev_settDateStr = recadj.getSettDate().toString();
		                prev_adjustment_rrstr_id = adjustment_rrstr_id;
		                prev_adjustment_name = adjustment_name;
		            }

		            // if adjustment_name = sacId 
		        }

		        // for each adjustment
		        if (prev_settDateStr != null) {
                    //for (Cnmea cnmea : sortAdjustTotal) {
                    	Cnmea cnmea = data.getCnmea().get(prev_adjustment_rrstr_id + sacId);
                        if (cnmea == null) {
                        	conn.close();
                            throw new Exception("Adjust Total CSV File is Empty !!!");
                        }

		                //adjust_total_rrstr_id = cnmea.getSettId();	//adjust_totalLine[5];
		                //adjust_total_sacId = cnmea.getAccountId();	//adjust_totalLine[6];

                        //if (adjust_total_rrstr_id.equals(prev_adjustment_rrstr_id) && adjust_total_sacId.equals(prev_adjustment_name)) {
		                    seq = seq + 1;
            				stmt = conn.prepareStatement(sqlCmd3CNMEA);
            				stmt.setInt(1, seq);
            				stmt.setString(2, "Sub-Total");
            				stmt.setString(3, cnmea.getGmee().toString());
            				stmt.setString(4, cnmea.getAccountingGmef().toString());
            				stmt.setString(5, cnmea.getAccountingLmee().toString());
            				stmt.setString(6, cnmea.getAccountingLmef().toString());
            				stmt.setString(7, cnmea.getNmea().toString());
            				stmt.setString(8, settRunId);
            				stmt.setString(9, sacId);
            				stmt.setString(10, sacVersion);
            				stmt.setString(11, settDate);
            				stmt.executeQuery();
            				stmt.close();

		                //    break;
		                //}

		                // if adjust_total
		            //}

		            // for each adjust_total
		        }*/
		        
		        for (MnmeaSub recMea : sortMnmeasub) {
		        	
		        	String prev_adjustment_rrstr_id = null;
		        	
		        	for (int p = 1; p <= 48; p++) {
	                	String period = String.format("%1$02d", p);
	                	Adjustment recadj = data.getAdjustment().get(recMea.getRerunId() + period + sacId);
	                	
			            if (recadj == null) {
			                logger.warn(logPrefix + "Adjustment is empty or contain invalid data.");
			                break;
			            }
			            
			            adjustmentExist = true;
			            
		                // CNMEA DETAIL
		                seq = seq + 1;
		                Date dt = new Date(recadj.getSettDate().toGregorianCalendar().getTime().getTime());
		                
        				prepareAccountStatement(stmt, "CNMEA", seq,
        						recadj.getRunType(),
        						mnmeaDf.format(dt),	//.toUpperCase(),
        						Integer.valueOf(recadj.getPeriodId()).toString(),
        						UtilityFunctions.trimZeroDecimal(recadj.getGmee()),
        						UtilityFunctions.trimZeroDecimal(recadj.getAccountingGmef()),
        						UtilityFunctions.trimZeroDecimal(recadj.getAccountingLmee()),
        						UtilityFunctions.trimZeroDecimal(recadj.getAccountingLmef()),
        						UtilityFunctions.trimZeroDecimal(recadj.getNmea()),
        						"","","","","","","","","","","","",
        						settRunId, sacId, sacVersion, settDate);
        				
        				prev_adjustment_rrstr_id = recadj.getSettId();
	                }
		        	
			        if (prev_adjustment_rrstr_id != null) {
			        	
                    	Cnmea cnmea = data.getCnmea().get(prev_adjustment_rrstr_id + sacId);
                        if (cnmea == null) {
                            throw new Exception("Adjust Total CSV File is Empty !!!");
                        }

	                    seq = seq + 1;
        				prepareAccountStatement(stmt, "CNMEASUB", seq,
        						"","",
        						"Sub-Total",
        						UtilityFunctions.trimZeroDecimal(cnmea.getGmee()),
        						UtilityFunctions.trimZeroDecimal(cnmea.getAccountingGmef()),
        						UtilityFunctions.trimZeroDecimal(cnmea.getAccountingLmee()),
        						UtilityFunctions.trimZeroDecimal(cnmea.getAccountingLmef()),
        						UtilityFunctions.trimZeroDecimal(cnmea.getNmea()),
        						"","","","","","","","","","","","",
        						settRunId, sacId, sacVersion, settDate);
			        }
		        }

		        // if prev_settDateStr not null
		        if (adjustmentExist == false) {
	                int p = 1;

	                while (p <= 48) {
	                    seq = seq + 1;
        				prepareAccountStatement(stmt, "CNMEA", seq,
        						"","",
        						Integer.toString(p),
        						"0","0","0","0","0",
        						"","","","","","","","","","","","",
        						settRunId, sacId, sacVersion, settDate);

	                    p = p + 1;
	                }

		            seq = seq + 1;
    				prepareAccountStatement(stmt, "CNMEASUB", seq,
    						"","",
    						"Sub-Total",
    						"0","0","0","0","0",
    						"","","","","","","","","","","","",
    						settRunId, sacId, sacVersion, settDate);
		        }

		        for (Period recinterval : sortInterval) {
		            if (recinterval == null) {
		                throw new Exception("Interval CSV File is Empty !!!");
		            }

		            interval_account = recinterval.getAccountId();	//intervalLine[0];
		            interval_number = recinterval.getPeriodId();	//intervalLine[71];

		            if (interval_account.equals(sacId) == true) {
		                // get market line details
		                /*if (interval_number.trim().length() == 1) {
		                    index = "0" + interval_number.trim();
		                } else {
		                    index = interval_number.trim();
		                }*/

		                Market mkt = data.getMarket().get(interval_number);

		                if (mkt == null) {
		                    throw new Exception("Market CSV File is Empty !!!");
		                }

		                /*	not required after migration
		                marketStrId = mkt.getRunId();	//marketLine[13];

		                if (marketStrId.equals(settRunId) != true) {
		                    logger.warn(logPrefix + "Invalid settlement id in Market CSV File !!!");
		                }

		                market_number = mkt.getPeriodId();	//marketLine[11];

		                if (market_number.equals(interval_number) != true) {
		                	logger.warn(logPrefix + "Market number and Interval number not same !!!");
		                }*/

		                // nrscReportGenerated = false
		                boolean lcscReportGenerated = false;
		                boolean generateLcscSub = false;

		                // Process with Node details	
		                for (Facility recnode : sortNode) {

		                    if (recnode == null) {
		                        throw new Exception("Node CSV File is Empty !!!");
		                    }

		                    node_account = recnode.getAccountId();	//nodeLine[5];
		                    node_interval = recnode.getPeriodId();	//nodeLine[17];

		                    if (sacId.equals(node_account) == true && node_interval.equals(interval_number) == true) {
		                        // FSD DETAILS
		                        // if substring(nodeLine[24],0,1) <> "I" then
		                        // 	generatorAccount = true
		                        // 	generatorAccounts[sacId] = true
		                        if (generatorAccounts.containsKey(sacId) == true) {
		                            if (sqlSettDate.after(NetAFPChangeEffective) == true && sac.isResidentialAccount() == true) {
		                                logger.info(logPrefix + "Skipping Insert FSD for " + sacId + " as the account is redential and NetAFP change is effective...");
		                            } else {
			                            seq = seq + 1;
			            				prepareAccountStatement(stmt, "FSD", seq,
			            						Integer.valueOf(interval_number).toString(),
			            						recnode.getFacilityId(),
			            						UtilityFunctions.trimZeroDecimal(mkt.getFsc()),
			            						UtilityFunctions.trimZeroDecimal(mkt.getFeq()),
			            						UtilityFunctions.trimZeroDecimal(mkt.getAfp()),
			            						UtilityFunctions.trimZeroDecimal(recnode.getFeqAdj()),
			            					    "0",
			            					    UtilityFunctions.trimZeroDecimal(recnode.getAccountingFsd()),
			            						"","","","","","","","","","","","",
			            						settRunId, sacId, sacVersion, settDate);
		                            }
		                        }

		                        // GEN1 DETAILS
		                        if (sac.getGesc().signum() != 0) {
		                            seq = seq + 1;
		            				prepareAccountStatement(stmt, "GENLOAD", seq,
		            						Integer.valueOf(interval_number).toString(),
		            						recnode.getFacilityId(),
		            						UtilityFunctions.trimZeroDecimal(recnode.getIeq()),
		            						UtilityFunctions.trimZeroDecimal(recnode.getMep()),
		            						UtilityFunctions.trimZeroDecimal(recnode.getGescn()),
		            						UtilityFunctions.trimZeroDecimal(recnode.getGescp()),
		            						UtilityFunctions.trimZeroDecimal(recnode.getGesce()),
		            					    "0",
		            					    UtilityFunctions.trimZeroDecimal(recinterval.getUsep()),
		            					    "0",
		            					    "0",
		            						"","","","","","","","","",
		            						settRunId, sacId, sacVersion, settDate);
		                        }

		                        // LCSC DETAILS
		                        // if accountDR[sacId] != null and nodeLine[56] != "" then
		                        if (nodeDR.containsKey(recnode.getFacilityId()) == true) {
		                            seq = seq + 1;

		                            if (recnode.getComplFlag() != null && recnode.getComplFlag().isEmpty() == false) {
		                                String nc = "No";
		                                String fc = "No";
		                                String pc = "No";

		                                if (recnode.getComplFlag().equals("F") == true) {
		                                    fc = "Yes";
		                                }

		                                if (recnode.getComplFlag().equals("N") == true) {
		                                    nc = "Yes";
		                                }

		                                if (recnode.getComplFlag().equals("P") == true) {
		                                    pc = "Yes";
		                                }

			        					prepareAccountStatement(stmt, "LCSC", seq,
			        							Integer.valueOf(interval_number).toString(),
			        							recnode.getFacilityId(),
			        							UtilityFunctions.trimZeroDecimal(recnode.getOiec()),
			        							UtilityFunctions.trimZeroDecimal(recnode.getSiec()),
			        							UtilityFunctions.trimZeroDecimal(recnode.getWlq()),
			        							UtilityFunctions.trimZeroDecimal(recnode.getLcq()),
			        							UtilityFunctions.trimZeroDecimal(recinterval.getLcp()),
			        							UtilityFunctions.trimZeroDecimal(recnode.getLcscp()),
			        							UtilityFunctions.trimZeroDecimal(recnode.getLcscp()),
			        						    fc,
			        						    nc,
			        						    pc,
			        							"","","","","","","","",
			        							settRunId, sacId, sacVersion, settDate);
			            				
		                                generateLcscSub = true;
		                            } else {
			        					prepareAccountStatement(stmt, "LCSC", seq,
			        							Integer.valueOf(interval_number).toString(),
			        							recnode.getFacilityId(),
			        						    "","",
			        						    UtilityFunctions.trimZeroDecimal(recnode.getWlq()),
			        						    "",
			        						    UtilityFunctions.trimZeroDecimal(recinterval.getLcp()),
			        						    "","",
			        						    "-",
			        						    "-",
			        						    "-",
			        							"","","","","","","","",
			        							settRunId, sacId, sacVersion, settDate);
		                            }
		                            
		                            lcscReportGenerated = true;
		                        }

		                        // NFSC2 DETAILS
		                        // if substring(nodeLine[24],0,1) <> "I" then
		                        if (generatorAccounts.containsKey(sacId) == true) {
		                            seq = seq + 1;
		            				prepareAccountStatement(stmt, "NFSC", seq,
		            						Integer.valueOf(interval_number).toString(),
		            						recnode.getFacilityId(),
		            						UtilityFunctions.trimZeroDecimal(recnode.getFsc()),
		            						UtilityFunctions.trimZeroDecimal(recnode.getAccountingFsd()),
		            						UtilityFunctions.trimZeroDecimal(recnode.getFcc()),
		            						UtilityFunctions.trimZeroDecimal(recnode.getNfsc()),
		            						"","","","","","","","","","","","","","",
		            						settRunId, sacId, sacVersion, settDate);
		                        }

		                        // NRSCGEN DETAILS
		                        // Part of 2.7.05 below remain unchanged
		                        seq = seq + 1;
	        					prepareAccountStatement(stmt, "NRSC", seq,
	        							Integer.valueOf(interval_number).toString(),
	        							recnode.getFacilityId(),
	        							UtilityFunctions.trimZeroDecimal(recnode.getAccountingRsc()),
	        							UtilityFunctions.trimZeroDecimal(recnode.getAccountingRcc()),
	        							UtilityFunctions.trimZeroDecimal(recnode.getAccountingRsd()),
	        							UtilityFunctions.trimZeroDecimal(recnode.getNrsc()),
	        							"","","","","","","","","","","","","","",
	        							settRunId, sacId, sacVersion, settDate);

		                        // Part of 2.7.05 above remain unchanged
		                        // nrscReportGenerated = true
		                        // REGC
		                        // Commented below as the BPM 2.5.9 Clawback related changes - Zero values to be displayed for RSC and FSC
		                        // if (((Decimal) nodeLine[12]) != 0) {
	            				if (recnode.getNodeType().equals("L") == false) {
		                            seq = seq + 1;

		                            if (clawbackIndex > 0) {
		                                for (int eachIteration = 0; eachIteration < clawbackPeriodArray.size(); eachIteration++) {
		                                    if (Integer.parseInt(recnode.getPeriodId()) == Integer.parseInt(clawbackPeriodArray.get(eachIteration)) && 
		                                    		recnode.getFacilityId().equals(clawbackNodeNameArray.get(eachIteration)) == true && 
		                                    		clawbackQuantityTypeArray.get(eachIteration).equals("GFQ") == true) {
		                                        clawBackFlag = "Y";
		                                    }
		                                }
		                            }

		            				prepareAccountStatement(stmt, "REGCR", seq,
		            						Integer.valueOf(interval_number).toString(),
		            						recnode.getFacilityId(),
		            						UtilityFunctions.trimZeroDecimal(recinterval.getMfp()),
		            						UtilityFunctions.trimZeroDecimal(recnode.getGfq()),
		            						UtilityFunctions.trimZeroDecimal(recnode.getFsc()),
		            					    "","","","","","","","","",
		            					    clawBackFlag,
		            						"","","","","",
		            						settRunId, sacId, sacVersion, settDate);

		                            if (clawBackFlag.equals("Y") == true) {
		                                clawBackFlag = "N";
		                            }
		                        }

		                        // 2.7.06 If ends here to exclude L type Nodes		        		                      
		                        // }
		                        // NEAA DETAILS
		                        // if accountLine[2] = "1" then
	            				if (sac.getNeaa().signum() != 0) {
		                            //if ((ignoreNegIEQ.equalsIgnoreCase("Y") == true && recnode.getIeq().signum() >= 0) || (ignoreNegIEQ.equalsIgnoreCase("Y") == true)) {
	            					// PROD1SHARP-41, suppress negative IEQ facility in NEAA report
			                        if ((ignoreNegIEQ.equalsIgnoreCase("Y") == true && recnode.getIeq().signum() >= 0) || (ignoreNegIEQ.equalsIgnoreCase("Y") == false)) {
		                                seq = seq + 1;
			        					prepareAccountStatement(stmt, "NEAA", seq,
			        							Integer.valueOf(interval_number).toString(),
			        							recnode.getFacilityId(),
			        							UtilityFunctions.trimZeroDecimal(recnode.getIeq()),
			        							UtilityFunctions.trimZeroDecimal(recinterval.getIeqp()),	// PROD1SHARP-41, total NEAA IEQ fix
			        						    "0",
			        						    UtilityFunctions.trimZeroDecimal(recnode.getMep()),
			        						    UtilityFunctions.trimZeroDecimal(recinterval.getUsep()),
			        						    UtilityFunctions.trimZeroDecimal(mkt.getRoundedHeuc()),
			        						    UtilityFunctions.trimZeroDecimal(recnode.getAccountingNelc()),
			        						    "0",
			        						    recnode.getAccountingNelc().signum() == 0 ? "0" : UtilityFunctions.trimZeroDecimal(recnode.getNeaa()),
			        							"","","","","","","","","",
			        							settRunId, sacId, sacVersion, settDate);
		                            }
		                        }

		                        // Process Reserve
		                        node_name = recnode.getFacilityId();	//nodeLine[24];

		                        // Process ftr
		                        for (Ftr recftr : sortFtr) {
		                            if (recftr == null) {
		                                throw new Exception("FTR CSV File is Empty !!!");
		                            }

		                            ftr_node = recftr.getNodeId();	//ftrLine[2];
		                            ftr_interval = recftr.getPeriodId();	//ftrLine[3];

		                            if (ftr_node.equals(node_name) == true && Integer.parseInt(ftr_interval) == Integer.parseInt(node_interval)) {
		                                // NTRSC DETAILS	
		                            	if (sac.getNtsc().signum() != 0) {
		                                    seq = seq + 1;
				            				prepareAccountStatement(stmt, "NTRSC", seq,
				            						Integer.valueOf(interval_number).toString(),
				            						recnode.getFacilityId(),
				            						UtilityFunctions.trimZeroDecimal(recftr.getFtq()),
				            						UtilityFunctions.trimZeroDecimal(recinterval.getUsep()),
				            						UtilityFunctions.trimZeroDecimal(recnode.getMep()),
				            						UtilityFunctions.trimZeroDecimal(recnode.getAccountingNtsc()),
				            						"","","","","","","","","","","","","","",
				            						settRunId, sacId, sacVersion, settDate);
		                                }
		                            }

		                            // if ftr
		                        }

		                        // for each recftr
		                    }

		                    // if sacId = node_account and node_interval = interval_number
		                }

		                // for each recnode
		                // Process vesting
		                if (sqlSettDate.getTime() < LngRulesEffectiveDate.getTime()) {
		                    for (Vesting recvesting : sortVesting) {
		                        if (recvesting == null) {
		                            throw new Exception("Vesting CSV File is Empty !!!");
		                        }

		                        vesting_account = recvesting.getAccountId();	//vestingLine[3];
		                        vesting_number = recvesting.getPeriodId();	//vestingLine[4];

		                        if (sacId.equals(vesting_account) == true && Integer.parseInt(interval_number) == Integer.parseInt(vesting_number)) {
		                            // VEST DETAILS
		                            seq = seq + 1;

		                            if (sac.isMsslAccount() == false) {
		                                // Account is not MSSL
		                            	if (recvesting.getHp().signum() == 0 && recvesting.getHq().signum() == 0) {
				        					prepareAccountStatement(stmt, "VEST", seq,
				        							Integer.valueOf(interval_number).toString(),
				        						    "","","","","","","",
				        							"","","","","","","","","","","","",
				        							settRunId, sacId, sacVersion, settDate);
		                            	} else {
				        					prepareAccountStatement(stmt, "VEST", seq,
				        							Integer.valueOf(interval_number).toString(),
				        							UtilityFunctions.trimZeroDecimal(recinterval.getVcrp()),
				        							UtilityFunctions.trimZeroDecimal(recvesting.getHp()),
				        							UtilityFunctions.trimZeroDecimal(recvesting.getHq()),
				        							UtilityFunctions.trimZeroDecimal(recinterval.getAvcsc()),
				        						    "",
				        						    recvesting.getContractName(),
				        							"",
				        							"","","","","","","","","","","","",
				        							settRunId, sacId, sacVersion, settDate);
		                            	}
		                            } else {
		                                // Account is MSSL
			        					prepareAccountStatement(stmt, "VEST", seq,
			        							Integer.valueOf(interval_number).toString(),
			        							UtilityFunctions.trimZeroDecimal(recinterval.getVcrpk()),
			        							UtilityFunctions.trimZeroDecimal(recvesting.getHp()),
			        							UtilityFunctions.trimZeroDecimal(mkt.getHq()),
			        							UtilityFunctions.trimZeroDecimal(recinterval.getVcsck()),
			        						    "",
			        						    recvesting.getContractName(),
			        						    UtilityFunctions.trimFormatedDecimal(recinterval.getVcsck()),
			        							"","","","","","","","","","","","",
			        							settRunId, sacId, sacVersion, settDate);
		                            }
		                        }

		                        // if sacId = vesting_account and interval_number = vesting_number 
		                    }
		                } else {
		                    if (sac.isMsslAccount() == true) {
		                        // Account is MSSL
		                        for (Vesting recvesting : sortVesting) {
		                            if (recvesting == null) {
		                                throw new Exception("Vesting CSV File is Empty !!!");
		                            }

			                        vesting_account = recvesting.getAccountId();	//vestingLine[3];
			                        vesting_number = recvesting.getPeriodId();	//vestingLine[4];

			                        if (sacId.equals(vesting_account) == true && Integer.parseInt(interval_number) == Integer.parseInt(vesting_number)) {
		                                // VEST DETAILS
		                                seq = seq + 1;
			        					prepareAccountStatement(stmt, "VEST", seq,
			        							Integer.valueOf(interval_number).toString(),
			        							UtilityFunctions.trimZeroDecimal(recinterval.getVcrpk()),
			        							UtilityFunctions.trimZeroDecimal(recvesting.getHp()),
			        							UtilityFunctions.trimZeroDecimal(mkt.getHq()),
			        							UtilityFunctions.trimZeroDecimal(recinterval.getVcsck()),
			        						    "",
			        						    recvesting.getContractName(),
			        						    UtilityFunctions.trimFormatedDecimal(recinterval.getVcsck()),
			        							"","","","","","","","","","","","",
			        							settRunId, sacId, sacVersion, settDate);
		                            }

		                            // if sacId = vesting_account and interval_number = vesting_number 
		                        }
		                    }
		                }

		                // for each recvesting	
		                // Forward Sales Implemenation Begin [ITSM-16708]
		                if (params.isFSCEffective == true) {
			                for (Fsc recforwardsales : sortForwardSales) {
			                    if (recforwardsales == null) {
			                        throw new Exception("Forward Sales CSV File is Empty !!!");
			                    }
	
			                    String forwardsales_account = recforwardsales.getAccountId();	//forwardSalesLine[3];
			                    String forwardsales_number = recforwardsales.getPeriodId();	//forwardSalesLine[4];
	
		                        if (sacId.equals(forwardsales_account) == true && Integer.parseInt(interval_number) == Integer.parseInt(forwardsales_number)) {
			                        // FORWARD SALES DETAILS
			                        seq = seq + 1;
	
			                        // if the account is a retailer then mask FSP as blank ie ""
			                        // if(accountLine[123] == "R" ){
			                        // logMessage("**Account is a retailer set FSP as blank ** ");
			                        // forwardSalesLine[0] = "";
			                        // }
			                        if (sac.isMsslAccount() == false) {
			                            // Account is not MSSL
		                            	if (recforwardsales.getFsp().signum() == 0 && recforwardsales.getFsq().signum() == 0) {
				        					prepareAccountStatement(stmt, "FSC", seq,
				        							Integer.valueOf(interval_number).toString(),
				        						    "","","","","","","",
				        							"","","","","","","","","","","","",
				        							settRunId, sacId, sacVersion, settDate);
		                            	} else {
				        					prepareAccountStatement(stmt, "FSC", seq,
				        							Integer.valueOf(interval_number).toString(),
				        							UtilityFunctions.trimZeroDecimal(recforwardsales.getFsrp()),
				        							UtilityFunctions.trimZeroDecimal(recforwardsales.getFsp()),
				        							UtilityFunctions.trimZeroDecimal(recforwardsales.getFsq()),
				        							UtilityFunctions.trimZeroDecimal(recforwardsales.getFssc()),
				        						    "",
				        						    recforwardsales.getContractName(),
				        							"",
				        							"","","","","","","","","","","","",
				        							settRunId, sacId, sacVersion, settDate);
		                            	}
			                        } else {
			                            // Account is MSSL
			        					prepareAccountStatement(stmt, "FSC", seq,
			        							Integer.valueOf(interval_number).toString(),
			        							UtilityFunctions.trimZeroDecimal(recinterval.getFsrpk()),
			        							UtilityFunctions.trimZeroDecimal(recforwardsales.getFsp()),
			        							UtilityFunctions.trimZeroDecimal(mkt.getFsq()),
			        							UtilityFunctions.trimZeroDecimal(recinterval.getFssck()),
			        						    "",
			        						    recforwardsales.getContractName(),
			        						    UtilityFunctions.trimFormatedDecimal(recinterval.getFssck()),
			        							"","","","","","","","","","","","",
			        							settRunId, sacId, sacVersion, settDate);
			                        }
			                    }
			                    // if sacId == forwardsales_account && interval_number == forwardsales_number 
			                }
			            }
		                // Forward Sales Implementation End
		                
		                // FSD DETAILS without NODE name details
		                if (generatorAccounts.containsKey(sacId) == false) {
		                    seq = seq + 1;
                            if (sqlSettDate.after(NetAFPChangeEffective) == true && sac.isResidentialAccount() == true) {
                				prepareAccountStatement(stmt, "FSD", seq,
                						Integer.valueOf(interval_number).toString(),
                					    "",
                					    UtilityFunctions.trimZeroDecimal(mkt.getFsc()),
                					    UtilityFunctions.trimZeroDecimal(mkt.getFeq()),
                					    UtilityFunctions.trimZeroDecimal(mkt.getAfp()),
                					    UtilityFunctions.trimZeroDecimal(recinterval.getFeq()),
                					    "0",
                					    UtilityFunctions.trimZeroDecimal(recinterval.getAccountingFsd()),
                						"","","","","","","","","","","","",
                						settRunId, sacId, sacVersion, settDate);
                            } else {
                				prepareAccountStatement(stmt, "FSD", seq,
                						Integer.valueOf(interval_number).toString(),
                					    "",
                					    UtilityFunctions.trimZeroDecimal(mkt.getFsc()),
                					    UtilityFunctions.trimZeroDecimal(mkt.getFeq()),
                					    UtilityFunctions.trimZeroDecimal(mkt.getAfp()),
                					    "0",
                					    UtilityFunctions.trimZeroDecimal(recinterval.getFeq()),
                					    UtilityFunctions.trimZeroDecimal(recinterval.getAccountingFsd()),
                						"","","","","","","","","","","","",
                						settRunId, sacId, sacVersion, settDate);
                            }
		                } else {
		                    // EGA Opt for direct
		                    if (sac.getLesd().signum() != 0) {
		                        seq = seq + 1;
	                            if (sqlSettDate.after(NetAFPChangeEffective) == true && sac.isResidentialAccount() == true) {
		            				prepareAccountStatement(stmt, "FSD", seq,
		            						Integer.valueOf(interval_number).toString(),
		            					    "",
		            					    UtilityFunctions.trimZeroDecimal(mkt.getFsc()),
		            					    UtilityFunctions.trimZeroDecimal(mkt.getFeq()),
		            					    UtilityFunctions.trimZeroDecimal(mkt.getAfp()),
		            					    UtilityFunctions.trimZeroDecimal(recinterval.getFeq()),
		            					    "0",
		            					    UtilityFunctions.trimZeroDecimal(recinterval.getGenFsd()),
		            						"","","","","","","","","","","","",
		            						settRunId, sacId, sacVersion, settDate);
	                            } else {
		            				prepareAccountStatement(stmt, "FSD", seq,
		            						Integer.valueOf(interval_number).toString(),
		            					    "",
		            					    UtilityFunctions.trimZeroDecimal(mkt.getFsc()),
		            					    UtilityFunctions.trimZeroDecimal(mkt.getFeq()),
		            					    UtilityFunctions.trimZeroDecimal(mkt.getAfp()),
		            					    "0",
		            					    UtilityFunctions.trimZeroDecimal(recinterval.getWeq()),
		            					    UtilityFunctions.trimZeroDecimal(recinterval.getLoadFsd()),
		            						"","","","","","","","","","","","",
		            						settRunId, sacId, sacVersion, settDate);
	                            }
		                    }
		                }

		                // BEC DETAILS
		                if (sac.getBesc().signum() != 0) {
		                    seq = seq + 1;
            				prepareAccountStatement(stmt, "BEQ", seq,
            						Integer.valueOf(interval_number).toString(),
            					    "SHUB",
            					    (recinterval.getBaqPurchased().signum() == 0 ? "" : UtilityFunctions.trimZeroDecimal(recinterval.getBaqPurchased())),
            					    (recinterval.getBaqSold().signum() == 0 ? "" : UtilityFunctions.trimZeroDecimal(recinterval.getBaqSold())),
            					    (recinterval.getBwfPurchased().signum() == 0 ? "" : UtilityFunctions.trimZeroDecimal(recinterval.getBwfPurchased())),
            					    (recinterval.getBwfSold().signum() == 0 ? "" : UtilityFunctions.trimZeroDecimal(recinterval.getBwfSold())),
            					    (recinterval.getBifPurchased().signum() == 0 ? "" : UtilityFunctions.trimZeroDecimal(recinterval.getBifPurchased())),
            					    (recinterval.getBifSold().signum() == 0 ? "" : UtilityFunctions.trimZeroDecimal(recinterval.getBifSold())),
            					    UtilityFunctions.trimZeroDecimal(recinterval.getUsep()),
            					    UtilityFunctions.trimFormatedDecimal(recinterval.getBesc()),
            						"","","","","","","","","","",
            						settRunId, sacId, sacVersion, settDate);
		                }

		                // EUA DETAILS
		                seq = seq + 1;
        				prepareAccountStatement(stmt, "EUA", seq,
        						Integer.valueOf(interval_number).toString(),
        						UtilityFunctions.trimZeroDecimal(mkt.getLcsc()),
        						UtilityFunctions.trimZeroDecimal(mkt.getRoundedHeua()),
        						UtilityFunctions.trimZeroDecimal(mkt.getRoundedHlcu()),
        						UtilityFunctions.trimZeroDecimal(mkt.getRoundedHeur()),
        						UtilityFunctions.trimZeroDecimal(mkt.getRoundedHeuc()),
        						UtilityFunctions.trimZeroDecimal(mkt.getMeuc()),
        						UtilityFunctions.trimZeroDecimal(mkt.getWdq()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getWdq()),
        						UtilityFunctions.trimZeroDecimal(mkt.getWeq()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getWeq()),
        						UtilityFunctions.trimZeroDecimal(mkt.getWmq()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getWmq()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getRoundedHlcsa()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getHersa()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getAccountingHeusa()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getAccountingMeusa()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getAccountingEua()),
        						"","",
        						settRunId, sacId, sacVersion, settDate);

		                // BRC DETAILS
        				if (sac.getFcc().signum() != 0) {
		                    seq = seq + 1;
	        				prepareAccountStatement(stmt, "BRC", seq,
	        						Integer.valueOf(interval_number).toString(),
	        						UtilityFunctions.trimZeroDecimal(recinterval.getBfqPurchased()),
	        						UtilityFunctions.trimZeroDecimal(recinterval.getBfqSold()),
	        						UtilityFunctions.trimZeroDecimal(recinterval.getMfp()),
	        						UtilityFunctions.trimZeroDecimal(recinterval.getFcc()),
	        						"","","","","","","","","","","","","","","",
	        						settRunId, sacId, sacVersion, settDate);
		                }

		                // GEN2 DETAILS
		                if (sac.getLesd().signum() != 0 || sac.getGesc().signum() == 0) {
		                    seq = seq + 1;
	        				prepareAccountStatement(stmt, "GENLOAD", seq,
	        						Integer.valueOf(interval_number).toString(),
	        					    "","","",
	        					    "0","0","0",
	        					    UtilityFunctions.trimZeroDecimal(recinterval.getWeq()),
	        					    UtilityFunctions.trimZeroDecimal(recinterval.getUsep()),
	        					    UtilityFunctions.trimZeroDecimal(recinterval.getAccountingLesdp()),
	        					    UtilityFunctions.trimZeroDecimal(recinterval.getAccountingLesdn()),
	        						"","","","","","","","","",
	        						settRunId, sacId, sacVersion, settDate);
		                }

		                // GEN1 - TOTAL DETAILS
		                if (sac.getGesc().signum() != 0) {
		                    seq = seq + 1;
	        				prepareAccountStatement(stmt, "GENLOAD", seq,
	        						Integer.valueOf(interval_number).toString(),
	        					    "Total",
	        					    "",
	        					    "",
	        					    UtilityFunctions.trimZeroDecimal(recinterval.getGescn()),
	        					    UtilityFunctions.trimZeroDecimal(recinterval.getGescp()),
	        					    UtilityFunctions.trimZeroDecimal(recinterval.getGesce()),
	        					    "0","","0","0",
	        						"","","","","","","","","",
	        						settRunId, sacId, sacVersion, settDate);
		                }

		                // LCSC - SUBTOTAL DETAILS
		                if (lcscReportGenerated == true) {
		                    seq = seq + 1;

		                    if (generateLcscSub == true) {
		    					prepareAccountStatement(stmt, "LCSCSUB", seq,
		    							Integer.valueOf(interval_number).toString(),
		    						    "Total",
		    						    "","","","","",
		    						    UtilityFunctions.trimZeroDecimal(recinterval.getLcsc()),
		    						    UtilityFunctions.trimZeroDecimal(recinterval.getLcsc()),
		    						    "","","",
		    							"","","","","","","","",
		    							settRunId, sacId, sacVersion, settDate);
		                    } else {
		    					prepareAccountStatement(stmt, "LCSCSUB", seq,
		    							Integer.valueOf(interval_number).toString(),
		    						    "Total",
		    						    "","","","","",
		    						    UtilityFunctions.trimZeroDecimal(recinterval.getLcsc()),
		    						    UtilityFunctions.trimZeroDecimal(recinterval.getLcsc()),
		    						    "","","",
		    							"","","","","","","","",
		    							settRunId, sacId, sacVersion, settDate);
		                    }
		                }

		                // NEAA DETAILS
		                if (sac.isPriceNeutralization() == true) {
		                    // if Decimal(accountLine[41]) <> 0 then
		                    seq = seq + 1;
	        				
	        				if (sac.getNeaa().signum() == 0) {
		    					prepareAccountStatement(stmt, "NEAA", seq,
		    							Integer.valueOf(interval_number).toString(),
		    						    "",
		    						    "0","0","0",
		    						    "",
		    						    "0","0","0","0","0",
		    							"","","","","","","","","",
		    							settRunId, sacId, sacVersion, settDate);
	        				} else {
		    					prepareAccountStatement(stmt, "NEAA", seq,
		    							Integer.valueOf(interval_number).toString(),
		    						    "",
		    						    "0","0",
		    						    UtilityFunctions.trimZeroDecimal(recinterval.getWpq()),
		    						    "",
		    						    UtilityFunctions.trimZeroDecimal(recinterval.getUsep()),
		    						    UtilityFunctions.trimZeroDecimal(mkt.getRoundedHeuc()),
		    						    "0",
		    						    UtilityFunctions.trimZeroDecimal(recinterval.getNegc()),
		    						    (recinterval.getNegc().signum() == 0 ? "0" : UtilityFunctions.trimZeroDecimal(recinterval.getNeaa())),
		    							"","","","","","","","","",
		    							settRunId, sacId, sacVersion, settDate);
	        				}
		                }

		                // NEAA SUB TOTAL
		                if (sac.isPriceNeutralization() == true) {
		                    seq = seq + 1;
	    					prepareAccountStatement(stmt, "NEAASUB", seq,
	    							Integer.valueOf(interval_number).toString(),
	    						    "Total",
	    						    "","","","","","","","",
	    						    UtilityFunctions.trimZeroDecimal(recinterval.getNeaa()),
	    							"","","","","","","","","",
	    							settRunId, sacId, sacVersion, settDate);
		                }

		                // NEAD DETAILS
		                seq = seq + 1;
        				prepareAccountStatement(stmt, "NEAD", seq,
        						Integer.valueOf(interval_number).toString(),
        						UtilityFunctions.trimZeroDecimal(mkt.getAccountingNeaa()),
        						UtilityFunctions.trimZeroDecimal(mkt.getWeq()),
        						UtilityFunctions.trimZeroDecimal(mkt.getRsa()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getWeq()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getRsa()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getAccountingNead()),
        						"","","","","","","","","","","","","",
        						settRunId, sacId, sacVersion, settDate);

		                // NFSC1 DETAILS
		                if (generatorAccounts.containsKey(sacId) == false) {
		                    seq = seq + 1;
	        				prepareAccountStatement(stmt, "NFSC", seq,
	        						Integer.valueOf(interval_number).toString(),
	        					    "",
	        					    UtilityFunctions.trimZeroDecimal(recinterval.getFsc()),
	        					    UtilityFunctions.trimZeroDecimal(recinterval.getAccountingFsd()),
	        					    UtilityFunctions.trimZeroDecimal(recinterval.getFcc()),
	        					    UtilityFunctions.trimZeroDecimal(recinterval.getNfsc()),
	        						"","","","","","","","","","","","","","",
	        						settRunId, sacId, sacVersion, settDate);
		                }

		                // 					if nrscReportGenerated = true then
		                // NRSCGEN DETAILS - Interval Level
		                // 						seq = seq + 1
		                // 						params2 = [seq,
		                // 								   intervalLine[71],
		                // 								   "",
		                // 								   intervalLine[91],	// interval_RSC
		                // 								   intervalLine[89],	// interval_RCC
		                // 								   formatDecimalSign(EMC.UtilityFunctions, val : -Decimal(intervalLine[92])),	// interval_RSD
		                // 								   intervalLine[69],	// interval_NRSC
		                // 								   "", "", "", "", "","",
		                // 								   settRunId, sacId, sacVersion, settDate]
		                // 						execute(DynamicSQL, sentence : sqlCmd2NRSCGEN, implname : dbpath, inParameters : params2)
		                // 					end
		                // REG DETAILS - Obsoleted
		                // FEES DETAILS
		                seq = seq + 1;
        				prepareAccountStatement(stmt, "EMCPSOFEES", seq,
        						feesDf.format(sqlSettDate).toUpperCase(),
        						sac.getDisplayTitle(),
        						Integer.valueOf(interval_number).toString(),
        						emcadmPriceCap,
        						emcadmPriceAdjRateRound,
        						emcAdmStr,
        						psoAdmStr,
        						UtilityFunctions.trimZeroDecimal(recinterval.getTte()), 
        						totalMW,
        						UtilityFunctions.trimZeroDecimal(recinterval.getCompEmcAdmCap()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getCompEmcAdmAdj()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getCompPsoa()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getCompFeeTotal()),
        						"","","","","","","",
        						settRunId, sacId, sacVersion, settDate);
		            }

		            // if interval_account = sacId 
		        }

		        // for each recinterval
		        storedSeq.put(sacId, seq);

		        // [ITSM-15086] Reserve the report sequence number for all vestings, apply to non-MSSL accounts only
		        if (sqlSettDate.getTime() < LngRulesEffectiveDate.getTime()) {
		            seq = seq + sortTvc.size();
		        } else {
		            if (sac.isMsslAccount() == false) {
		                for (Vesting recvesting : sortVesting) {
		                    if (recvesting == null) {
		                        throw new Exception("Vesting CSV File is Empty !!!");
		                    }

		                    vesting_account = recvesting.getAccountId();	//vestingLine[3];

		                    if (sacId.equals(vesting_account) == true) {
		                        vestingRecordCount = vestingRecordCount + 1;
		                    }
		                }

		                // for sortTvc which is end of the sortAccount loop
		                // [ITSM-15086] Reserve the sequence number for vesting as well
		                seq = seq + vestingRecordCount + sortTvc.size();
		            }
		        }

		        for (Brq brq : sortBRQ) {
		            if (brq == null) {
		                throw new Exception("BRQ CSV File is Empty !!!");
		            }

		            brqAccount = brq.getAccountId();	//brqLine[3];

                    if (sacId.equals(brqAccount) == true) {
		                // BRV DETAILS
                    	if (sac.getRcc().signum() != 0) {
		                    seq = seq + 1;
	        				prepareAccountStatement(stmt, "BRV", seq,
	        						Integer.valueOf(brq.getPeriodId()).toString(),
	        						brq.getReserveClass(),
	        						UtilityFunctions.trimZeroDecimal(brq.getPurchased()),
	        						UtilityFunctions.trimZeroDecimal(brq.getSold()),
	        					    "",		// TODO: ???"MRP ($/MWh)",
	        					    "",		// TODO: ???"RCC",
	        						"","","","","","","","","","","","","","",
	        						settRunId, sacId, sacVersion, settDate);
		                    /*params2 = { seq, brqLine[4], // the brq's interval
		                              brqLine[6], // the brq's reserve
		                              brqLine[0], // the brq's purchased
		                              brqLine[1], // the brq's sold
		                              brqLine[7], // ???
		                              brqLine[8], "", "", "", "", "", "", "", "", settRunId, sacId, sacVersion, settDate };
		                    DynamicSQL.execute(sentence : sqlCmd2BRV, implname : dbpath, 
		                                       inParameters : params2);*/
		                }
		            }
		        }

		        for (Reserve recreserve : sortReserve) {
		            if (recreserve == null) {
		                throw new Exception("Reserve CSV File is Empty !!!");
		            }

		            reserve_node_name = recreserve.getNode();	//reserveLine[10];

		            // the reserve's node name
		            reserve_interval = recreserve.getPeriodId();	//reserveLine[11];

		            // the reserve's interval number
		            reserve_account = recreserve.getAccountId();	//reserveLine[14];

		            // the reserve's account
		            if (reserve_account.equals(sacId) == true) {
		                // BRV DETAILS
		                // 				if Decimal(accountLine[70]) <> 0 then
		                // 					seq = seq + 1
		                // 					params2 = [seq,
		                // 							   brqLine[4],
		                // 							   brqLine[5],
		                // 							   brqLine[6],
		                // 							   brqLine[7],
		                // 							   brqLine[8],
		                // 							   brqLine[9],
		                // 							   "", "", "", "", "", "",
		                // 							   settRunId, sacId, sacVersion, settDate]
		                // 					execute(DynamicSQL, sentence : sqlCmd2BRV, implname : dbpath, inParameters : params2)
		                // 				end
		                // RSC & RSVC DETAILS
		                // Commented below as the BPM 2.5.9 Clawback related changes - Zero values to be displayed for RSC and FSC
		                // if (((Decimal) reserveLine[6]) > 0 || (((Decimal) reserveLine[4]) != 0 && ((Decimal) reserveLine[12]) != 0)) {
		                // RSC              
		                // 2.7.05 start - search for particular nodename (reserveLine[10]) matches with class name (reserveLine[9])
		                if (acg_effectiveness_search_map.get(recreserve.getNode() + " " + recreserve.getName()) != null) {
		                    // 2.7.05   node name = reserveLine[10]   reserve class name = reserveLine[9]
		                    seq = seq + 1;

		                    if (clawbackIndex > 0) {
		                        for (int eachIteration = 0; eachIteration < clawbackPeriodArray.size(); eachIteration++) {
                                    if (Integer.parseInt(recreserve.getPeriodId()) == Integer.parseInt(clawbackPeriodArray.get(eachIteration)) && 
                                    		recreserve.getNode().equals(clawbackNodeNameArray.get(eachIteration)) == true && 
                                    		((clawbackQuantityTypeArray.get(eachIteration).equals("GRQ") == true) || (clawbackQuantityTypeArray.get(eachIteration).equals("LRQ") == true)) &&
                                    		recreserve.getName().substring(0, 6).equals(clawbackAncillaryTypeArray.get(eachIteration)) == true &&
                                    		recreserve.getRsq().signum() == 0) {
                                        clawBackFlag = "Y";
                                    }
		                        }
		                    }

	    					prepareAccountStatement(stmt, "RSC", seq,
	    							Integer.valueOf(recreserve.getPeriodId()).toString(),
	    							recreserve.getNode(),
	    							recreserve.getName(),
	    							UtilityFunctions.trimZeroDecimal(recreserve.getMrp()),
	    							UtilityFunctions.trimZeroDecimal(recreserve.getRsq()),
	    							UtilityFunctions.trimZeroDecimal(recreserve.getAccountingRsc()),
	    						    "","","","","","","","",
	    						    clawBackFlag,
	    							"","","","","",
	    							settRunId, sacId, sacVersion, settDate);

		                    if (clawBackFlag.equals("Y") == true) {
		                        clawBackFlag = "N";
		                    }
		                }

		                // if ends here - 2.7.05 ends
		                // }
		                // RSVCDPL DETAILS
		                // Commented below as the BPM 2.5.9 Clawback related changes - Zero values to be displayed for RSC and FSC
		                // if (((Decimal) reserveLine[13]) != 0) {
		                // 2.7.05 start - search for particular nodename (reserveLine[10]) matches with class name (reserveLine[9])
		                // if get(acg_effectiveness_search_map, arg1 : reserveLine[10] + " " + reserveLine[9]) != null then
		                if (acg_effectiveness_search_map.get(reserve_node_name + " " + recreserve.getName()) != null) {
		                    seq = seq + 1;

		                    if (clawbackIndex > 0) {
		                        for (int eachIteration = 0; eachIteration < clawbackPeriodArray.size(); eachIteration++) {
                                    if (Integer.parseInt(recreserve.getPeriodId()) == Integer.parseInt(clawbackPeriodArray.get(eachIteration)) && 
                                    		recreserve.getNode().equals(clawbackNodeNameArray.get(eachIteration)) == true && 
                                    		clawbackQuantityTypeArray.get(eachIteration).equals("LRQ") == true &&
                                    		recreserve.getName().substring(0, 6).equals(clawbackAncillaryTypeArray.get(eachIteration)) == true) {
                                        clawBackFlag = "Y";
                                    }
		                        }
		                    }

	        				prepareAccountStatement(stmt, "RSVDPLCR", seq,
	        						Integer.valueOf(recreserve.getPeriodId()).toString(),
	        						recreserve.getName(),
	        						recreserve.getNode(),
	        						UtilityFunctions.trimZeroDecimal(recreserve.getMrp()),
	        						UtilityFunctions.trimZeroDecimal(recreserve.getLrq()),
	        						UtilityFunctions.trimZeroDecimal(recreserve.getFacilityRsc()),
	        					    "","","","","","","","",
	        					    clawBackFlag,
	        						"","","","","",
	        						settRunId, sacId, sacVersion, settDate);

		                    if (clawBackFlag.equals("Y") == true) {
		                        clawBackFlag = "N";
		                    }
		                }

		                // if ends here - 2.7.05 ends
		                // }
		                // Process Class
		                reserve_class_name = recreserve.getName();	//reserveLine[9];

		                // the reserve's name
		                /*if (reserve_interval.trim().length() == 1) {
		                    index = reserve_class_name + "0" + reserve_interval.trim();
		                }
		                else {
		                    index = reserve_class_name + reserve_interval.trim();
		                }*/

		                RsvClass rsvCls = data.getRsvClass().get(reserve_class_name + reserve_interval);
		                if (rsvCls == null) {
		                    throw new Exception("Class CSV File record not found");
		                }

		                class_name = rsvCls.getReserveClass();	//classLine[0];

		                // the class's name
		                class_interval = rsvCls.getPeriodId();	//classLine[1];

		                // the class's interval number
		                // Process Node
		                /*if (reserve_interval.trim().length() == 1) {
		                    index = reserve_node_name + "0" + reserve_interval.trim();
		                }
		                else {
		                    index = reserve_node_name + reserve_interval.trim();
		                }*/

		                Facility nde = data.getFacility().get(reserve_node_name + reserve_interval);
		                if (nde == null) {
		                    throw new Exception("Node CSV FIle record not found");
		                }

		                // RSD DETAILS
		                if (sac.getRsd().signum() != 0 && recreserve.getAccountingRsd().signum() != 0) {
		                    seq = seq + 1;
	    					prepareAccountStatement(stmt, "RSD", seq,
	    							Integer.valueOf(reserve_interval).toString(),
	    							recreserve.getNode(),
	    							recreserve.getName(),
	    							UtilityFunctions.trimZeroDecimal(rsvCls.getRsc()),
	    							UtilityFunctions.trimZeroDecimal(nde.getRrs()),
	    							UtilityFunctions.trimZeroDecimal(recreserve.getAccountingRsd()),
	    							"","","","","","","","","","","","","","",
	    							settRunId, sacId, sacVersion, settDate);
		                }
		            }

		            // if reserve_node_name = node_name and reserve_interval = node_interval
		        }

		        // for each recreserve
		        //accountLine = recaccount.fields(delim : ",");
		        col2footerEUA = UtilityFunctions.trimZeroDecimal(sac.getEua());	//accountLine[12];
		        col2footerBRC = UtilityFunctions.trimZeroDecimal(sac.getFcc());	//accountLine[13];
		        col2footerBRV = UtilityFunctions.trimZeroDecimal(sac.getRcc());	//accountLine[70];
		        col8footerFSD = UtilityFunctions.trimZeroDecimal(sac.getFsd());	//accountLine[15];
		        //col2footerBEC = accountLine[10];
		        col2footerBEC = UtilityFunctions.trimFormatedDecimal(sac.getBesc());
		        col2footerGEN2 = UtilityFunctions.trimZeroDecimal(sac.getGesc());	//accountLine[16];
		        col4footerGEN2 = UtilityFunctions.trimZeroDecimal(sac.getLesd());	//accountLine[37];
		        col2footerGEN1 = UtilityFunctions.trimZeroDecimal(sac.getGesc());	//accountLine[16];
		        col4footerGEN1 = UtilityFunctions.trimZeroDecimal(sac.getLesd());	//accountLine[37];
		        col6footerRSC = UtilityFunctions.trimZeroDecimal(sac.getRsc());	//accountLine[71];
		        col6footerRSD = UtilityFunctions.trimZeroDecimal(sac.getRsd());	//accountLine[72];
		        col2footerRSV = UtilityFunctions.trimZeroDecimal(sac.getNrsc());	//accountLine[49];
		        col2footerRSVC = UtilityFunctions.trimZeroDecimal(sac.getRsc());	//accountLine[71];
		        col2footerRSVCDPL = UtilityFunctions.trimZeroDecimal(sac.getRsc());	//accountLine[71];

		        // account_RSC previous: accountLine[108]
		        col7footerNEAD = UtilityFunctions.trimZeroDecimal(sac.getTotalNead());	//accountLine[88];
		        col6footerNFSC1 = UtilityFunctions.trimZeroDecimal(sac.getNfsc());	//accountLine[47];
		        col6footerNFSC2 = UtilityFunctions.trimZeroDecimal(sac.getNfsc());	//accountLine[47];
		        col6footerNRSCGEN = UtilityFunctions.trimZeroDecimal(sac.getNrsc());	//accountLine[49];
		        col2footerNTRSC = UtilityFunctions.trimZeroDecimal(sac.getNtsc());	//accountLine[50];
		        col2footerREG = UtilityFunctions.trimZeroDecimal(sac.getNfsc());	//accountLine[47];
		        col2footerREGC = UtilityFunctions.trimZeroDecimal(sac.getFsc());	//accountLine[14];
		        //col2footerVEST = accountLine[106];
		        if (sac.getVcsc().signum() == 0) {
		        	col2footerVEST = "";
		        } else {
			        col2footerVEST = UtilityFunctions.trimFormatedDecimal(sac.getVcsc());
		        }
		        //col2footerForwardSales = accountLine[122];
		        // account_fssc
		        if (sac.getFssc().signum() == 0) {
		        	col2footerForwardSales = "";
		        } else {
		        	col2footerForwardSales = UtilityFunctions.trimFormatedDecimal(sac.getFssc());
		        }

		        // Added for FSC Implementation
		        col11footerNEAA = UtilityFunctions.trimZeroDecimal(sac.getNeaa());	//accountLine[41];

		        // account_NEAA
		        sacId = sac.getAccountId();	//accountLine[39];

		        // account_name
		        // EUA FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "EUATOTAL", seq,
					    "Total Energy Uplift",
					    col2footerEUA,
					    "","","","","","","","","","","","","","","","",
						"","",
						settRunId, sacId, sacVersion, settDate);

		        // BRC FOORTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "BRCTOTAL", seq,
					    "Total Regulation Contract Credit for Run",
					    col2footerBRC,
					    "","","",
						"","","","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // CNMEA FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "CNMEATOTAL", seq,
					    "","",
					    "Total",
					    UtilityFunctions.trimZeroDecimal(sac.getIncGmee()),
					    UtilityFunctions.trimZeroDecimal(sac.getIncGmef()),
					    UtilityFunctions.trimZeroDecimal(sac.getIncLmee()),
					    UtilityFunctions.trimZeroDecimal(sac.getIncLmef()),
					    UtilityFunctions.trimZeroDecimal(sac.getIncNmea()),
						"","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // BRV FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "BRVTOTAL", seq,
					    "Total Bilateral Reserve Contract Settlement Credit for Run",
					    col2footerBRV,
					    "","","","",
						"","","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // FSD FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "FSDTOTAL", seq,
					    "Total Regulation Settlement Debit",
					    "","","","","","",
					    col8footerFSD,
						"","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // BEC FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "BEQTOTAL", seq,
					    "Total Bilateral Contract Settlement Credit",
					    col2footerBEC,
					    "","","","","","","","",
						"","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // GEN1 FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "GENTOTAL", seq,
					    "Total GESC",
					    col2footerGEN1,
					    "Total LESD",
					    col4footerGEN1,
					    "","","","","","","",
						"","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // LCSC FOOTER
		        if (accountDR.containsKey(sacId) != false && accountDR.get(sacId) == true) {
			        seq = seq + 1;
					prepareAccountStatement(stmt, "LCSCTOTAL", seq,
						    "Total LCSC",
						    "","","","","","","",
						    UtilityFunctions.trimZeroDecimal(sac.getLcsc()),
						    "","","",
							"","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // RSC FOOTER
		        // if (Decimal(accountLine[71])) != 0 then
		        // 2.7.09 check whether the SAC_ID is included because of group effectiveness
		        if (sac.getRsc().signum() != 0 || sac_id_effectiveness_search_map.get(sac.getAccountId()) != null) {
			        seq = seq + 1;
					prepareAccountStatement(stmt, "RSCTOTAL", seq,
						    "Total Reserve Settlement Credit",
						    "","","","",
						    col6footerRSC,
						    "","","","","","","","","",
							"","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // RSD FOOTER
		        if (sac.getRsd().signum() != 0) {
		            seq = seq + 1;
					prepareAccountStatement(stmt, "RSDTOTAL", seq,
						    "Total Net Reserve Settlement Debit",
						    "","","","",
						    col6footerRSD,
							"","","","","","","","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // RSVCPDL FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "RSVCDPLTOT", seq,
					    "Total Reserve Settlement Credit",
					    col2footerRSVCDPL,
					    "","","","","","","","","","","","","",
						"","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // NEAA FOOTER
		        if (sac.isPriceNeutralization() == true) {
			        seq = seq + 1;
					prepareAccountStatement(stmt, "NEAATOTAL", seq,
						    "",
						    "Total NEAA: ",
						    "","","","","","","","",
						    col11footerNEAA,
							"","","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // NEAD FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "NEADTOTAL", seq,
					    "",
					    "Total NEAD:",
					    "","","","",
					    col7footerNEAD,
						"","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // NFSC1 FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "NFSCTOTAL", seq,
					    "Total Net Regulation Settlement Credit",
					    "","","","",
					    col6footerNFSC1,
						"","","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // NRSCGEN FOOTER
		        if (sortNode1.containsKey(sacId) == true) {
			        seq = seq + 1;
					prepareAccountStatement(stmt, "NRSCTOTAL", seq,
						    "Total Net Reserve Settlement Credit",
						    "","","","",
						    col6footerNRSCGEN,
							"","","","","","","","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // NTRSC FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "NTRSCTOTAL", seq,
					    "Total Net Transmission Rights Settlement Credit for Run",
					    col2footerNTRSC,
					    "","","","",
						"","","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // REGC FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "REGCTOTAL", seq,
					    "Total Regulation Credit for Run",
					    col2footerREGC,
					    "","","","","","","","","","","","","",
						"","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // VEST FOOTER
		        if (sortVesting1.containsKey(sacId) == true) {
			        seq = seq + 1;
					prepareAccountStatement(stmt, "VESTOTAL", seq,
						    "Total Vesting Contract Amount for Settlement Run",
						    col2footerVEST,
						    "","","","","","",
							"","","","","","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }

		        // FORWARD SALES FOOTER - Added for FSC Implementation
		        if (params.isFSCEffective == true && sortForwardSales1.containsKey(sacId) == true) {
			        seq = seq + 1;
					prepareAccountStatement(stmt, "FSCTOTAL", seq,
						    "Total Forward Sales Contract Amount for Settlement Run",
						    col2footerForwardSales,
						    "","","","","","",
							"","","","","","","","","","","","",
							settRunId, sacId, sacVersion, settDate);
		        }		        
			    stmt.executeBatch();
		    }
		    stmt.close();

		    // for each recaccount
			stmt = conn.prepareStatement(sqlCmd2VEST);
		    for (Account recaccount_tvc : sortAccount) {
		        // generatorAccount = false
		        sacId = recaccount_tvc.getAccountId();	//accountLine[39];

		        // account_name
		        seq = storedSeq.get(sacId);

		        if (sqlSettDate.getTime() < LngRulesEffectiveDate.getTime()) {
		            // [ITSM-12670] Process tvc
		            for (Tvc rectvc : sortTvc) {
		                if (rectvc == null) {
		                    throw new Exception("Tvc CSV File is Empty !!!");
		                }

		                tvc_account = rectvc.getAccountId();	//tvcLine[0];

		                // tvc_account
		                tvc_number = rectvc.getPeriodId();	//tvcLine[1];

		                // tvc_interval	
		                // TVC DETAILS					
		                if (sacId.equals(tvc_account) == true) {
		                    if (recaccount_tvc.isMsslAccount() == false) {
		                        // Account is not MSSL
		        				stmt.setInt(1, seq);
		        				stmt.setString(2, Integer.valueOf(rectvc.getPeriodId()).toString());
		        				stmt.setString(3, UtilityFunctions.trimZeroDecimal(rectvc.getVcrp()));
		        				stmt.setString(4, UtilityFunctions.trimZeroDecimal(rectvc.getTvp()));
		        				stmt.setString(5, UtilityFunctions.trimZeroDecimal(rectvc.getTvq()));
		        				stmt.setString(6, UtilityFunctions.trimZeroDecimal(rectvc.getVcsc()));
		        				stmt.setString(7, rectvc.getContractName());
		        				stmt.setString(8, "");
		        				stmt.setString(9, settRunId);
		        				stmt.setString(10, sacId);
		        				stmt.setString(11, sacVersion);
		        				stmt.setString(12, settDate);

		        				stmt.addBatch();

		                        seq = seq + 1;
		                    }
		                }

		                // if sacId = tvc_account and interval_number = tvc_number 	
		            }
		        } else {
		            // Account is not MSSL
                    if (recaccount_tvc.isMsslAccount() == false) {
		                // [ITSM-15086] Insert VEST details here
		                for (Vesting recvesting : sortVesting) {
		                    if (recvesting == null) {
		                        throw new Exception("Vesting CSV File is Empty !!!");
		                    }

		                    vesting_account = recvesting.getAccountId();	//vestingLine[3];

			                if (sacId.equals(vesting_account) == true) {
		                        // VEST DETAILS
		                        seq = seq + 1;

		        				stmt.setInt(1, seq);
		        				stmt.setString(2, Integer.valueOf(recvesting.getPeriodId()).toString());
		        				
		        				if (recvesting.getHp().signum() == 0 && recvesting.getHq().signum() == 0) {
			        				stmt.setString(3, "");
			        				stmt.setString(4, "");
			        				stmt.setString(5, "");
			        				stmt.setString(6, "");
			        				stmt.setString(7, "");
		        				} else {
			        				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recvesting.getVcrp()));
			        				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recvesting.getHp()));
			        				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recvesting.getHq()));
			        				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recvesting.getVcsc()));
			        				stmt.setString(7, recvesting.getContractName());
		        				}
		        				
		        				stmt.setString(8, "");
		        				stmt.setString(9, settRunId);
		        				stmt.setString(10, sacId);
		        				stmt.setString(11, sacVersion);
		        				stmt.setString(12, settDate);

		        				stmt.addBatch();
		                    }

		                    // if sacId = vesting_account
		                }

		                // [ITSM-12670] Process tvc
			            for (Tvc rectvc : sortTvc) {
			                if (rectvc == null) {
			                    throw new Exception("Tvc CSV File is Empty !!!");
			                }

			                tvc_account = rectvc.getAccountId();	//tvcLine[0];

			                // tvc_account
			                tvc_number = rectvc.getPeriodId();	//tvcLine[1];

		                    // tvc_interval	
		                    // TVC DETAILS					
			                if (sacId.equals(tvc_account) == true) {
		                        seq = seq + 1;
		        				stmt.setInt(1, seq);
		        				stmt.setString(2, Integer.valueOf(rectvc.getPeriodId()).toString());
		        				stmt.setString(3, UtilityFunctions.trimZeroDecimal(rectvc.getVcrp()));
		        				stmt.setString(4, UtilityFunctions.trimZeroDecimal(rectvc.getTvp()));
		        				stmt.setString(5, UtilityFunctions.trimZeroDecimal(rectvc.getTvq()));
		        				stmt.setString(6, UtilityFunctions.trimZeroDecimal(rectvc.getVcsc()));
		        				stmt.setString(7, rectvc.getContractName());
		        				stmt.setString(8, "");
		        				stmt.setString(9, settRunId);
		        				stmt.setString(10, sacId);
		        				stmt.setString(11, sacVersion);
		        				stmt.setString(12, settDate);

		        				stmt.addBatch();
		                    }

		                    // if sacId = tvc_account and interval_number = tvc_number
		                }
		            }
		        }

		        // for each rectvc 
		    }
		    stmt.executeBatch();
		    stmt.close();

		    // for each recaccount_tvc
		    Timestamp endTime = new Timestamp(System.currentTimeMillis());

		    logger.info(logPrefix + "Nem_Account_Statements (MP Reports) Insertion Done (Post EG for Clawback and Zero RSV RSC). Start time: " + 
		    startTime + ", end time: " + endTime);
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateMainReports()
			throws Exception {
			
		String msgStep = "RunResultGenerator.generateMainReports()";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;
			
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");

		    Timestamp startTime = new Timestamp(System.currentTimeMillis());
			
		    //String col2;
		    String sacId;
		    /* account name from account entity*/
		    boolean taxable = false;
		    boolean isGSTRegisteredRerunID_GMEE = false;

		    // ITSM - 15932 Added GST Flag
		    String rerun_id = null;
		    int gst_rate = 0;

		    // generatorAccount as Bool
		    //Any[] params;
		    //Any[] params1;
		    //String[] accountLine = null;
		    //String[] rerunLine = null;

		    // MAIN
		    String sqlCommandMain = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'MAIN'," + 
		    "?,?,?,?,?,?,'','','','','','','','','',?,?,?,?)";
		    String sqlCommandMainSub = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'MAINSUB'," + 
		    "?,?,?,?,?,?,'','','','','','','','','',?,?,?,?)";
		    String sqlCommandMainAcct = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8,COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'MAINACCT'," + 
		    "?,?,?,?,?,?,'','','','','','','','','',?,?,?,?)";
		    String sqlCommandMainTotal = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'MAINTOTAL'," + 
		    "?,?,?,?,?,?,'','','','','','','','','',?,?,?,?)";
		    String sqlNPCharges = "SELECT pre.calculation_result amount, " + 
		    "DECODE(SUBSTR(gst.NAME,1,1), 'V', pre.gst_amount, 0) input_gst, " + 
		    "DECODE(SUBSTR(gst.NAME,1,1), 'A', pre.gst_amount, 0) output_gst, " + 
		    "SUBSTR(ncc.solomon_code || ' - ' || npc.NAME, 1, 60) NAME " + 
		    "FROM NEM_PERIODIC_RESULTS pre, NEM_NON_PERIOD_CHARGES npc, " + 
		    "NEM_NON_PERIOD_CHARGE_CODES ncc, NEM_GST_CODES gst " + 
		    "WHERE pre.npc_id = npc.ID AND pre.sac_id = ? AND pre.sac_version = ? " + 
		    "AND pre.str_id = ? AND npc.ncc_id = ncc.ID " + 
		    "AND npc.gst_id = gst.ID AND gst.VERSION = ?" + 
		    "order by name";

		    // ITSM 15386 making the sequence correct		
		    logger.info(logPrefix + "Generating MAIN Reports ...");

		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Generating MAIN Reports ...", "");

		    // /////////////////////////////////////////////////
		    // GENERATE MAIN REPORTS
		    // /////////////////////////////////////////////////	
		    for (Account recaccount : sortAccount) {
		        sacId = recaccount.getAccountId();	//accountLine[39];

		        // account_name
		        // MAIN HEADER
		        seq = 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "");
				stmt.setString(3, "Net Amt");
				stmt.setString(4, "Input GST");
				stmt.setString(5, "Output GST");
				stmt.setString(6, "Total Amt");
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        // MAIN DETAILS
		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "GESC - Generator Energy Settlement Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getGesc()));
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recaccount.getIpGstGesc()));
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recaccount.getOpGstGesc()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalGesc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "LESD - Load Energy Settlement Debit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getLesd()));
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recaccount.getIpGstLesd()));
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recaccount.getOpGstLesd()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalLesd()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "LCSC - Load Curtailment Settlement Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getLcsc()));
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recaccount.getIpGstLcsc()));
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recaccount.getOpGstLcsc()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalLcsc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "BESC - Bilateral Energy Settlement Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getBesc()));
				stmt.setString(4, "0");
				stmt.setString(5, "0");
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalBesc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMainSub);
				stmt.setInt(1, seq);
				stmt.setString(2, "NESC - Net Energy Settlement Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getNesc()));
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recaccount.getIpGstNesc()));
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recaccount.getOpGstNesc()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalNesc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "RSC - Reserve Settlement Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getRsc()));
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recaccount.getIpGstRsc()));
				stmt.setString(5, "0");
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalRsc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "RSD - Reserve Settlement Debit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getRsd()));
				stmt.setString(4, "0");
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recaccount.getOpGstRsd()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalRsd()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "RCC - Reserve Contract Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getRcc()));
				stmt.setString(4, "0");
				stmt.setString(5, "0");
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalRcc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMainSub);
				stmt.setInt(1, seq);
				stmt.setString(2, "NRSC - Net Reserve Settlement Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getNrsc()));
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recaccount.getIpGstNrsc()));
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recaccount.getOpGstNrsc()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalNrsc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "FSC - Regulation Settlement Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getFsc()));
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recaccount.getIpGstFsc()));
				stmt.setString(5, "0");
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalFsc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "FSD - Regulation Settlement Debit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getFsd()));
				stmt.setString(4, "0");
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recaccount.getOpGstFsd()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalFsd()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "FCC - Regulation Contract Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getFcc()));
				stmt.setString(4, "0");
				stmt.setString(5, "0");
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalFcc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMainSub);
				stmt.setInt(1, seq);
				stmt.setString(2, "NFSC - Net Regulation Settlement Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getNfsc()));
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recaccount.getIpGstNfsc()));
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recaccount.getOpGstNfsc()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalNfsc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "NTSC - Net Transmission Settlement Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getNtsc()));
				stmt.setString(4, "0");
				stmt.setString(5, "0");
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalNtsc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "VCSC - Vesting Contract Settlement Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getVcsc()));
				stmt.setString(4, "0");
				stmt.setString(5, "0");
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalVcsc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        // //FSC Implementation - Begin [ITSM-16708]
				if (params.isFSCEffective == true) {
			        // logMessage("**FSC in MAIN -->"+ accountLine[122]+ accountLine[121]+"settRunId->"+settRunId+"sacId->"+sacId+"sacVersion->"+sacVersion+"settdate->"+settDate );
			        seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "FSSC - Forward Sales Contract Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getFssc()));
					stmt.setString(4, "0");
					stmt.setString(5, "0");
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalFssc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, sacId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();
				}
		        // //FSC Implementation - End
				
		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "HEUC - Hourly Energy Uplift");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getHeusa()));
				stmt.setString(4, "0");
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recaccount.getOpGstHeusa()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalHeusa()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "MEUC - Monthly Energy Uplift");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getMeusa()));
				stmt.setString(4, "0");
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recaccount.getOpGstMeusa()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalMeusa()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMainAcct);
				stmt.setInt(1, seq);
				stmt.setString(2, "NASC - Net Account Settlement Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recaccount.getNasc()));
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recaccount.getIpGstNasc()));
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recaccount.getOpGstNasc()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recaccount.getTotalNasc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();
		    }

		    for (Account recacct : sortAccount) {
		        // generatorAccount = false
		        sacId = recacct.getAccountId();	//accountLine[39];

		        // account_name
		        taxable = recacct.isTaxable();	//accountLine[4];

		        // account_taxable    - Also used for ITSM 15932 for finding GST Flag
		        // 		generatorAccounts[sacId] = false
		        // 		if accountLine[8] = "1" then
		        // 			embeddedGenerators[sacId] = true
		        // 		else
		        // 			embeddedGenerators[sacId] = false
		        // 		end
		        logger.info(logPrefix + "Inserting data into NEM_ACCOUNT_STATEMENTS (MAIN) for Account: " + recacct.getDisplayTitle());

		        Rerun rerun = null;

		        for (Rerun recrerun : sortRerun) {
		        	rerun = recrerun;

		            // to get the first record only
		            break;
		        }

		        if (rerun != null) {
		            rerun_id = rerun.getName();	//rerunLine[0];
		        }

		        if (rerun_id != null) {
		            for (Rerun recrerun : sortRerun) {

		                if (recrerun != null) {
		                    rerun_id = recrerun.getName();	//rerunLine[0];
		                    gst_rate = recrerun.getGstRate().multiply(BigDecimal.valueOf(100)).intValue();	//((int) (((Decimal) rerunLine[1]) * 100));
		                    isGSTRegisteredRerunID_GMEE = recrerun.isTaxable();	//rerunLine[18];

		                    // ITSM - 15932 Added GST Flag
		                } else {
		                    logger.warn(logPrefix + "Rerun is empty or contain invalid data.");
		                }

		                if (sacId.equals(rerun_id) == true) {
		                    seq = seq + 1;

		                    // For NON GST MP the GST Rate will be NA - ITSM 15932
		                    if (isGSTRegisteredRerunID_GMEE == false) {
		        				stmt = conn.prepareStatement(sqlCommandMain);
		        				stmt.setInt(1, seq);
		        				stmt.setString(2, "GMEE - Generation Metering Adjustment on Energy GST(" + "NA" + ")");
		        				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recrerun.getIncGmee()));
		        				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recrerun.getIpGstIncGmee()));
		        				stmt.setString(5, "0");
		        				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recrerun.getTotalIncGmee()));
		        				stmt.setString(7, settRunId);
		        				stmt.setString(8, sacId);
		        				stmt.setString(9, sacVersion);
		        				stmt.setString(10, settDate);
		        				stmt.executeQuery();
		        				stmt.close();
		                    } else {
		        				stmt = conn.prepareStatement(sqlCommandMain);
		        				stmt.setInt(1, seq);
		        				stmt.setString(2, "GMEE - Generation Metering Adjustment on Energy GST(" + gst_rate + "%)");
		        				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recrerun.getIncGmee()));
		        				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recrerun.getIpGstIncGmee()));
		        				stmt.setString(5, "0");
		        				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recrerun.getTotalIncGmee()));
		        				stmt.setString(7, settRunId);
		        				stmt.setString(8, sacId);
		        				stmt.setString(9, sacVersion);
		        				stmt.setString(10, settDate);
		        				stmt.executeQuery();
		        				stmt.close();
		                    }

		                    // End - ITSM 15932
		                    seq = seq + 1;
	        				stmt = conn.prepareStatement(sqlCommandMain);
	        				stmt.setInt(1, seq);
	        				stmt.setString(2, "GMEF - Generation Metering Adjustment on Fees GST(" + gst_rate + "%)");
	        				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recrerun.getIncGmef()));
	        				stmt.setString(4, "0");
	        				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recrerun.getOpGstIncGmef()));
	        				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recrerun.getTotalIncGmef()));
	        				stmt.setString(7, settRunId);
	        				stmt.setString(8, sacId);
	        				stmt.setString(9, sacVersion);
	        				stmt.setString(10, settDate);
	        				stmt.executeQuery();
	        				stmt.close();

		                    seq = seq + 1;
	        				stmt = conn.prepareStatement(sqlCommandMain);
	        				stmt.setInt(1, seq);
	        				stmt.setString(2, "LMEE - Load Metering Adjustment on Energy GST(" + gst_rate + "%)");
	        				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recrerun.getIncLmee()));
	        				stmt.setString(4, "0");
	        				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recrerun.getOpGstIncLmee()));
	        				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recrerun.getTotalIncLmee()));
	        				stmt.setString(7, settRunId);
	        				stmt.setString(8, sacId);
	        				stmt.setString(9, sacVersion);
	        				stmt.setString(10, settDate);
	        				stmt.executeQuery();
	        				stmt.close();

		                    seq = seq + 1;
	        				stmt = conn.prepareStatement(sqlCommandMain);
	        				stmt.setInt(1, seq);
	        				stmt.setString(2, "LMEF - Load Metering Adjustment on Fees GST(" + gst_rate + "%)");
	        				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recrerun.getIncLmef()));
	        				stmt.setString(4, "0");
	        				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recrerun.getOpGstIncLmef()));
	        				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recrerun.getTotalIncLmef()));
	        				stmt.setString(7, settRunId);
	        				stmt.setString(8, sacId);
	        				stmt.setString(9, sacVersion);
	        				stmt.setString(10, settDate);
	        				stmt.executeQuery();
	        				stmt.close();
		                }
		            }

		            // for each recrerun
		        } else {
		            // even if no rerun, still need to populate these reports
		            gst_rate = inputGst.multiply(BigDecimal.valueOf(100)).intValue();

		            // input_gst
		            seq = seq + 1;

		            // For NON GST MP the GST Rate will be NA - ITSM 15932
		            if (taxable == false) {
        				stmt = conn.prepareStatement(sqlCommandMain);
        				stmt.setInt(1, seq);
        				stmt.setString(2, "GMEE - Generation Metering Adjustment on Energy GST(" + "NA" + ")");
        				stmt.setString(3, "0");
        				stmt.setString(4, "0");
        				stmt.setString(5, "0");
        				stmt.setString(6, "0");
        				stmt.setString(7, settRunId);
        				stmt.setString(8, sacId);
        				stmt.setString(9, sacVersion);
        				stmt.setString(10, settDate);
        				stmt.executeQuery();
        				stmt.close();
		            } else {
        				stmt = conn.prepareStatement(sqlCommandMain);
        				stmt.setInt(1, seq);
        				stmt.setString(2, "GMEE - Generation Metering Adjustment on Energy GST(" + gst_rate + "%)");
        				stmt.setString(3, "0");
        				stmt.setString(4, "0");
        				stmt.setString(5, "0");
        				stmt.setString(6, "0");
        				stmt.setString(7, settRunId);
        				stmt.setString(8, sacId);
        				stmt.setString(9, sacVersion);
        				stmt.setString(10, settDate);
        				stmt.executeQuery();
        				stmt.close();
		            }

		            // End - ITSM 15932
		            seq = seq + 1;
    				stmt = conn.prepareStatement(sqlCommandMain);
    				stmt.setInt(1, seq);
    				stmt.setString(2, "GMEF - Generation Metering Adjustment on Fees GST(" + gst_rate + "%)");
    				stmt.setString(3, "0");
    				stmt.setString(4, "0");
    				stmt.setString(5, "0");
    				stmt.setString(6, "0");
    				stmt.setString(7, settRunId);
    				stmt.setString(8, sacId);
    				stmt.setString(9, sacVersion);
    				stmt.setString(10, settDate);
    				stmt.executeQuery();
    				stmt.close();

		            seq = seq + 1;
    				stmt = conn.prepareStatement(sqlCommandMain);
    				stmt.setInt(1, seq);
    				stmt.setString(2, "LMEE - Load Metering Adjustment on Energy GST(" + gst_rate + "%)");
    				stmt.setString(3, "0");
    				stmt.setString(4, "0");
    				stmt.setString(5, "0");
    				stmt.setString(6, "0");
    				stmt.setString(7, settRunId);
    				stmt.setString(8, sacId);
    				stmt.setString(9, sacVersion);
    				stmt.setString(10, settDate);
    				stmt.executeQuery();
    				stmt.close();

		            seq = seq + 1;
    				stmt = conn.prepareStatement(sqlCommandMain);
    				stmt.setInt(1, seq);
    				stmt.setString(2, "LMEF - Load Metering Adjustment on Fees GST(" + gst_rate + "%)");
    				stmt.setString(3, "0");
    				stmt.setString(4, "0");
    				stmt.setString(5, "0");
    				stmt.setString(6, "0");
    				stmt.setString(7, settRunId);
    				stmt.setString(8, sacId);
    				stmt.setString(9, sacVersion);
    				stmt.setString(10, settDate);
    				stmt.executeQuery();
    				stmt.close();
		        }

		        // if rerun <> null
		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMainSub);
				stmt.setInt(1, seq);
				stmt.setString(2, "NMEA - Net Metering Adjustment Amount");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recacct.getIncNmea()));
				stmt.setString(4, UtilityFunctions.trimZeroDecimal(recacct.getIpGstIncNmea()));
				stmt.setString(5, UtilityFunctions.trimZeroDecimal(recacct.getOpGstIncNmea()));
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recacct.getTotalIncNmea()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "NELC - Net Energy Load Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recacct.getNelc()));
				stmt.setString(4, "0");
				stmt.setString(5, "0");
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recacct.getTotalNelc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "NEGC - Net Energy Generation Credit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recacct.getNegc()));
				stmt.setString(4, "0");
				stmt.setString(5, "0");
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recacct.getTotalNegc()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMainSub);
				stmt.setInt(1, seq);
				stmt.setString(2, "NEAA - Net Energy Adjustment Amount");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recacct.getNeaa()));
				stmt.setString(4, "0");
				stmt.setString(5, "0");
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recacct.getTotalNeaa()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlCommandMain);
				stmt.setInt(1, seq);
				stmt.setString(2, "NEAD - Net Energy Adjustment Debit");
				stmt.setString(3, UtilityFunctions.trimZeroDecimal(recacct.getNead()));
				stmt.setString(4, "0");
				stmt.setString(5, "0");
				stmt.setString(6, UtilityFunctions.trimZeroDecimal(recacct.getTotalNead()));
				stmt.setString(7, settRunId);
				stmt.setString(8, sacId);
				stmt.setString(9, sacVersion);
				stmt.setString(10, settDate);
				stmt.executeQuery();
				stmt.close();

		        NonPeriodCharges npc = new NonPeriodCharges();
				/*BigDecimal npcAmount = BigDecimal.ZERO;
				BigDecimal npcInputGst = BigDecimal.ZERO;
				BigDecimal npcOutputGst = BigDecimal.ZERO;
				BigDecimal npcTotal = BigDecimal.ZERO;*/

		        // HWY comment out the following line to solve issue EMCS-680
		        // if Decimal(accountLine[68]) <> 0 then
		        // 			seq = seq + 1
		        // 			params1 = [seq,
		        // 					   "EMCADMIN - APPROX. " + emcAdm + " $/MW",
		        // 					   accountLine[11],	//account_EMCADM
		        // 					   "0",
		        // 					   accountLine[51],	//account_a_EMCADM
		        // 					   accountLine[77],	//account_total_EMCADM
		        // 					   settRunId, sacId, sacVersion, settDate]
		        // 			execute(DynamicSQL, sentence : sqlCommandMain, implname : dbpath, inParameters : params1)	
				npc.amount = npc.amount.subtract(recacct.getAccountingEmcAdm());	// - ((Decimal) accountLine[11]);
				npc.outputGst = npc.outputGst.subtract(recacct.getAccountingOpGstEmcAdm());	// - ((Decimal) accountLine[51]);
				npc.total = npc.total.subtract(recacct.getAccountingTotalEmcAdm());	// - ((Decimal) accountLine[77]);

		        // 			seq = seq + 1
		        // 			params1 = [seq,
		        // 					   "PSOADMIN - APPROX. " + psoAdm + " $/MW",
		        // 					   accountLine[69],	//account_PSOADM
		        // 					   "0",
		        // 					   accountLine[66],	//account_a_PSOADM
		        // 					   accountLine[101],	//account_total_PSOADM
		        // 					   settRunId, sacId, sacVersion, settDate]
		        // 			execute(DynamicSQL, sentence : sqlCommandMain, implname : dbpath, inParameters : params1)	
				npc.amount = npc.amount.subtract(recacct.getAccountingPsoAdm());	// - ((Decimal) accountLine[69]);
				npc.outputGst = npc.outputGst.subtract(recacct.getAccountingOpGstPsoAdm());	// - ((Decimal) accountLine[66]);
				npc.total = npc.total.subtract(recacct.getAccountingTotalPsoAdm());	// - ((Decimal) accountLine[101]);

		        // HWY comment out the following line to solve issue EMCS-680
		        // end
		        if (recacct.isEmcAccount() == true) {
		            // EMC Account
		            // 			seq = seq + 1
		            // 			params1 = [seq,
		            // 					   "EMCADMINR - APPROX. " + emcAdm + " $/MW",
		            // 					   accountLine[9],	//account_ADMFEE
		            // 					   "0",
		            // 					   "0",
		            // 					   accountLine[9],	//account_ADMFEE
		            // 					   settRunId, sacId, sacVersion, settDate]
		            // 			execute(DynamicSQL, sentence : sqlCommandMain, implname : dbpath, inParameters : params1)	
		        	npc.amount = npc.amount.subtract(recacct.getAdmFee());	// - ((Decimal) accountLine[9]);
		        	npc.total = npc.total.subtract(recacct.getAdmFee());	// - ((Decimal) accountLine[9]);
		        }

		        if (recacct.isPsoAccount() == true) {
		            // PSO Account				
		            // 			seq = seq + 1
		            // 			params1 = [seq,
		            // 					   "PSOADMINR - APPROX. " + psoAdm + " $/MW",
		            // 					   accountLine[9],	//account_ADMFEE
		            // 					   "0",
		            // 					   "0",
		            // 					   accountLine[9],	//account_ADMFEE
		            // 					   settRunId, sacId, sacVersion, settDate]
		            // 			execute(DynamicSQL, sentence : sqlCommandMain, implname : dbpath, inParameters : params1)	
		        	npc.amount = npc.amount.subtract(recacct.getAdmFee());	// - ((Decimal) accountLine[9]);
		        	npc.total = npc.total.subtract(recacct.getAdmFee());	// - ((Decimal) accountLine[9]);
		        }

		        // Non Period Charges
				stmt1 = conn.prepareStatement(sqlNPCharges);
				stmt1.setString(1, sacId);
				stmt1.setString(2, sacVersion);
				stmt1.setString(3, settRunId);
				stmt1.setString(4, sacVersion);
				stmt1.executeQuery();
				rs1 = stmt1.getResultSet();
		
		        // npc as NonPeriodCharges
				while (rs1.next()) {
		            BigDecimal amount = rs1.getBigDecimal(1);	//((Decimal) row[1]);
		            BigDecimal inputGst = rs1.getBigDecimal(2);	//((Decimal) row[2]);
		            BigDecimal outputGst = rs1.getBigDecimal(3);	//((Decimal) row[3]);
		            BigDecimal total = amount.add(inputGst).add(outputGst);
		            npc.total = npc.total.add(total);
		            npc.amount = npc.amount.add(amount);
		            npc.inputGst = npc.inputGst.add(inputGst);
		            npc.outputGst = npc.outputGst.add(outputGst);
		            String name = rs1.getString(4);	//String.valueOf(o : row[4]);
		            
		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, name);
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(amount));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(inputGst));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(outputGst));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(total));
					stmt.setString(7, settRunId);
					stmt.setString(8, sacId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();
		        }
				rs1.close();
				stmt1.close();

		        BigDecimal tempAccLine75 = BigDecimal.ZERO;

		        if (recacct.getTotalAmount() != null) {
		            tempAccLine75 = recacct.getTotalAmount();
		        }

		        BigDecimal tempAccLine46 = BigDecimal.ZERO;

		        if (recacct.getNetAmount() != null) {
		            tempAccLine46 = recacct.getNetAmount();
		        }

		        npcTotal.put(sacId, npc);

		        // DRCAP 01 Change  if (settRunType != "F" || (settRunType == "F" && accountLine[109].trim() != "EMC RECOVERY A/C")) {
		        if (settRunType.equals("F") != true || (settRunType.equals("F") == true && recacct.getDisplayTitle().equals("EMC ADJUST A/C") != true)) {
		            seq = seq + 1;

		            // params1 = { seq, "Total Due (Owed)", ((Decimal) accountLine[46]) + npc.amount, ((Decimal) accountLine[36]) + npc.inputGst, ((Decimal) accountLine[68]) + npc.outputGst, ((Decimal) accountLine[75]) + npc.total, settRunId, sacId, sacVersion, settDate };
					stmt = conn.prepareStatement(sqlCommandMainTotal);
					stmt.setInt(1, seq);
					stmt.setString(2, "Total Due (Owed)");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(tempAccLine46.add(npc.amount)));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recacct.getIpGstTotal().add(npc.inputGst)));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recacct.getOpGstTotal().add(npc.outputGst)));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(tempAccLine75.add(npc.total)));
					stmt.setString(7, settRunId);
					stmt.setString(8, sacId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();
		        }
		    }

		    // for each recaccount
		    Timestamp endTime = new Timestamp(System.currentTimeMillis());

		    logger.info(logPrefix + "Nem_Account_Statements (Main Reports) Insertion Done (Post EG for Clawback and Zero RSV RSC). Start time: " + 
		    startTime + ", end time: " + endTime);
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (rs1 != null)
					rs1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt1 != null)
					stmt1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateParticipantSummary()
			throws Exception {
			
		String msgStep = "RunResultGenerator.generateParticipantSummary()";
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;
			
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");

		    //Timestamp startTime = new Timestamp(System.currentTimeMillis());
		    
		    //String col2;
		    String mpId;
		    /* account name from account entity*/
		    boolean taxable;
		    //boolean isGSTRegisteredRerunID_GMEE;

		    // ITSM - 15932 Added GST Flag
		    //String rerun_id = null;
		    int gst_rate;

		    // generatorAccount as Bool
		    //Any[] params;
		    //Any[] params1;
		    //String[] mpLine = null;
		    //String[] rerunLine = null;

		    // MAIN
		    String sqlCommandMain = "INSERT INTO NEM.NEM_SETTLEMENT_PTP_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, " + 
		    "STR_ID, PTP_ID, PTP_VERSION, SETTLEMENT_DATE, CREATED_DATE) VALUES ( SYS_GUID(),'PTPMAIN'," + 
		    "?,?,?,?,?,?,?,?,?,?,SYSDATE)";
		    String sqlCommandMainSub = "INSERT INTO NEM.NEM_SETTLEMENT_PTP_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, " + 
		    "STR_ID, PTP_ID, PTP_VERSION, SETTLEMENT_DATE, CREATED_DATE) VALUES ( SYS_GUID(),'PTPMAINSUB'," + 
		    "?,?,?,?,?,?,?,?,?,?,SYSDATE)";
		    String sqlCommandMainAcct = "INSERT INTO NEM.NEM_SETTLEMENT_PTP_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, " + 
		    "STR_ID, PTP_ID, PTP_VERSION, SETTLEMENT_DATE, CREATED_DATE) VALUES ( SYS_GUID(),'PTPMAINPTP'," + 
		    "?,?,?,?,?,?,?,?,?,?,SYSDATE)";
		    String sqlCommandMainTotal = "INSERT INTO NEM.NEM_SETTLEMENT_PTP_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, " + 
		    "STR_ID, PTP_ID, PTP_VERSION, SETTLEMENT_DATE, CREATED_DATE) VALUES ( SYS_GUID(),'PTPMAINTOTAL'," + 
		    "?,?,?,?,?,?,?,?,?,?,SYSDATE)";
		    String sqlNPCharges = "select amount, input_gst, output_gst, name from (" +
		    "select sum(amount) amount, sum(input_gst) input_gst, sum(output_gst) output_gst, name from (" + 
		    "SELECT pre.calculation_result amount, " + 
		    "DECODE(SUBSTR(gst.NAME,1,1), 'V', pre.gst_amount, 0) input_gst, " + 
		    "DECODE(SUBSTR(gst.NAME,1,1), 'A', pre.gst_amount, 0) output_gst, " + 
		    "SUBSTR(ncc.solomon_code || ' - ' || npc.NAME, 1, 60) NAME " + 
		    "FROM NEM_PERIODIC_RESULTS pre, NEM_NON_PERIOD_CHARGES npc, NEM_SETTLEMENT_ACCOUNTS sac, " + 
		    "NEM_NON_PERIOD_CHARGE_CODES ncc, NEM_GST_CODES gst " + 
		    "WHERE pre.npc_id = npc.ID AND pre.sac_id = sac.id AND pre.sac_version = sac.version AND sac.ptp_id = ? AND pre.sac_version = ? " + 
		    "AND pre.str_id = ? AND npc.ncc_id = ncc.ID " + 
		    "AND npc.gst_id = gst.ID AND gst.VERSION = ? " + 
		    "order by name) group by name) order by name";

		    // ITSM 15386 making the sequence correct		
		    logger.info(logPrefix + "Generating Participant Summary ...");

		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Generating Participant Summary ...", "");

		    // /////////////////////////////////////////////////
		    // GENERATE PARTICIPANT SUMMARY
		    // /////////////////////////////////////////////////	
		    for (Participant recMp : sortMp) {
		        mpId = recMp.getParticipantId();	//mpLine[45];

		        // Not EMC and PSO
		        if (recMp.isEmcAccount() == false && recMp.isPsoAccount() == false) {
		            // account_name
		            // MAIN HEADER
		            seq = 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "");
					stmt.setString(3, "Net Amt");
					stmt.setString(4, "Input GST");
					stmt.setString(5, "Output GST");
					stmt.setString(6, "Total Amt");
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            // MAIN DETAILS
		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "GESC - Generator Energy Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getGesc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstGesc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstGesc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalGesc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "LESD - Load Energy Settlement Debit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getLesd()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstLesd()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstLesd()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalLesd()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "LCSC - Load Curtailment Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getLcsc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstLcsc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstLcsc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalLcsc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "BESC - Bilateral Energy Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getBesc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstBesc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstBesc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalBesc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMainSub);
					stmt.setInt(1, seq);
					stmt.setString(2, "NESC - Net Energy Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getNesc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstNesc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstNesc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalNesc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "RSC - Reserve Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getRsc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstRsc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstRsc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalRsc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "RSD - Reserve Settlement Debit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getRsd()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstRsd()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstRsd()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalRsd()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "RCC - Reserve Contract Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getRcc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstRcc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstRcc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalRcc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMainSub);
					stmt.setInt(1, seq);
					stmt.setString(2, "NRSC - Net Reserve Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getNrsc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstNrsc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstNrsc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalNrsc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "FSC - Regulation Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getFsc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstFsc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstFsc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalFsc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "FSD - Regulation Settlement Debit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getFsd()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstFsd()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstFsd()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalFsd()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "FCC - Regulation Contract Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getFcc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstFcc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstFcc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalFcc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMainSub);
					stmt.setInt(1, seq);
					stmt.setString(2, "NFSC - Net Regulation Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getNfsc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstNfsc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstNfsc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalNfsc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "NTSC - Net Transmission Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getNtsc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstNtsc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstNtsc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalNtsc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "VCSC - Vesting Contract Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getVcsc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstVcsc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstVcsc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalVcsc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            // //FSC Implementation - Begin [ITSM-16708]
					if (params.isFSCEffective == true) {
			            // logMessage("**FSC in MAIN -->"+ accountLine[122]+ accountLine[121]+"settRunId->"+settRunId+"sacId->"+sacId+"sacVersion->"+sacVersion+"settdate->"+settDate );
			            seq = seq + 1;
						stmt = conn.prepareStatement(sqlCommandMain);
						stmt.setInt(1, seq);
						stmt.setString(2, "FSSC - Forward Sales Contract Settlement Credit");
						stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getFssc()));
						stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstFssc()));
						stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstFssc()));
						stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalFssc()));
						stmt.setString(7, settRunId);
						stmt.setString(8, mpId);
						stmt.setString(9, sacVersion);
						stmt.setString(10, settDate);
						stmt.executeQuery();
						stmt.close();
					}
		            // //FSC Implementation - End
					
		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "HEUC - Hourly Energy Uplift");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getHeuc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstHeuc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstHeuc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalHeuc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "MEUC - Monthly Energy Uplift");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getMeuc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstMeuc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstMeuc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalMeuc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMainAcct);
					stmt.setInt(1, seq);
					stmt.setString(2, "NPSC - Net Participant Settlement Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getNasc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstNasc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstNasc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalNasc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            if (recMp.getRerunGstRate() == null || recMp.getRerunGstRate().signum() < 0) {
		                // even if no rerun, still need to populate these reports
		                gst_rate = inputGst.multiply(BigDecimal.valueOf(100)).intValue();	//((int) (inputGst * 100).round());
		            } else {
			            gst_rate = recMp.getRerunGstRate().multiply(BigDecimal.valueOf(100)).intValue();	//((int) (((Decimal) mpLine[129]) * 100));
		            }

		            // input_gst
		            seq = seq + 1;
		            taxable = recMp.isTaxable();	//mpLine[128];

		            // For NON GST MP the GST Rate will be NA - ITSM 15932
		            if (taxable == false) {
						stmt = conn.prepareStatement(sqlCommandMain);
						stmt.setInt(1, seq);
						stmt.setString(2, "GMEE - Generation Metering Adjustment on Energy GST(" + "NA" + ")");
						stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getGmee()));
						stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstGmee()));
						stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstGmee()));
						stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalGmee()));
						stmt.setString(7, settRunId);
						stmt.setString(8, mpId);
						stmt.setString(9, sacVersion);
						stmt.setString(10, settDate);
						stmt.executeQuery();
						stmt.close();
		            } else {
						stmt = conn.prepareStatement(sqlCommandMain);
						stmt.setInt(1, seq);
						stmt.setString(2, "GMEE - Generation Metering Adjustment on Energy GST(" + gst_rate + "%)");
						stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getGmee()));
						stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstGmee()));
						stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstGmee()));
						stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalGmee()));
						stmt.setString(7, settRunId);
						stmt.setString(8, mpId);
						stmt.setString(9, sacVersion);
						stmt.setString(10, settDate);
						stmt.executeQuery();
						stmt.close();
		            }

		            // End - ITSM 15932
		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "GMEF - Generation Metering Adjustment on Fees GST(" + gst_rate + "%)");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getGmef()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstGmef()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstGmef()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalGmef()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "LMEE - Load Metering Adjustment on Energy GST(" + gst_rate + "%)");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getLmee()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstLmee()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstLmee()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalLmee()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "LMEF - Load Metering Adjustment on Fees GST(" + gst_rate + "%)");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getLmef()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstLmef()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstLmef()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalLmef()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMainSub);
					stmt.setInt(1, seq);
					stmt.setString(2, "NMEA - Net Metering Adjustment Amount");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getNmea()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstNmea()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstNmea()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalNmea()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "NELC - Net Energy Load Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getNelc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstNelc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstNelc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalNelc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "NEGC - Net Energy Generation Credit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getNegc()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstNegc()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstNegc()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalNegc()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMainSub);
					stmt.setInt(1, seq);
					stmt.setString(2, "NEAA - Net Energy Adjustment Amount");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getNeaa()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstNeaa()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstNeaa()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalNeaa()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMain);
					stmt.setInt(1, seq);
					stmt.setString(2, "NEAD - Net Energy Adjustment Debit");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getNead()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstNead()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstNead()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalNead()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();

		            // Non Period Charges
					stmt1 = conn.prepareStatement(sqlNPCharges);
					stmt1.setString(1, mpId);
					stmt1.setString(2, sacVersion);
					stmt1.setString(3, settRunId);
					stmt1.setString(4, sacVersion);
					stmt1.executeQuery();
					rs1 = stmt1.getResultSet();
			
			        // npc as NonPeriodCharges
					while (rs1.next()) {
			            BigDecimal amount = rs1.getBigDecimal(1);	//((Decimal) row[1]);
			            BigDecimal inputGst = rs1.getBigDecimal(2);	//((Decimal) row[2]);
			            BigDecimal outputGst = rs1.getBigDecimal(3);	//((Decimal) row[3]);
			            BigDecimal total = amount.add(inputGst).add(outputGst);
//			            npc.total = npc.total.add(total);
//			            npc.amount = npc.amount.add(amount);
//			            npc.inputGst = npc.inputGst.add(inputGst);
//			            npc.outputGst = npc.outputGst.add(outputGst);
			            String name = rs1.getString(4);	//String.valueOf(o : row[4]);
			            
			            seq = seq + 1;
						stmt = conn.prepareStatement(sqlCommandMain);
						stmt.setInt(1, seq);
						stmt.setString(2, name);
						stmt.setString(3, UtilityFunctions.trimZeroDecimal(amount));
						stmt.setString(4, UtilityFunctions.trimZeroDecimal(inputGst));
						stmt.setString(5, UtilityFunctions.trimZeroDecimal(outputGst));
						stmt.setString(6, UtilityFunctions.trimZeroDecimal(total));
						stmt.setString(7, settRunId);
						stmt.setString(8, mpId);
						stmt.setString(9, sacVersion);
						stmt.setString(10, settDate);
						stmt.executeQuery();
						stmt.close();
			        }
					rs1.close();
					stmt1.close();

		            //        if settRunType != "F" or (settRunType = "F" and trim(mpLine[109]) != "EMC RECOVERY A/C") then
		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlCommandMainTotal);
					stmt.setInt(1, seq);
					stmt.setString(2, "Total Due (Owed)");
					stmt.setString(3, UtilityFunctions.trimZeroDecimal(recMp.getNetTotal()));
					stmt.setString(4, UtilityFunctions.trimZeroDecimal(recMp.getIpGstTotal()));
					stmt.setString(5, UtilityFunctions.trimZeroDecimal(recMp.getOpGstTotal()));
					stmt.setString(6, UtilityFunctions.trimZeroDecimal(recMp.getTotalAmount()));
					stmt.setString(7, settRunId);
					stmt.setString(8, mpId);
					stmt.setString(9, sacVersion);
					stmt.setString(10, settDate);
					stmt.executeQuery();
					stmt.close();
		        }
		    }

		    // for each recaccount
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs1 != null)
					rs1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt1 != null)
					stmt1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateOtherReports()
			throws Exception {
			
		String msgStep = "RunResultGenerator.generateOtherReports()";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;
			
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");

		    //String[] accountLine = null;
		    //String[] rerunLine = null;
		    //String col2;
		    String sacId;
		    /* account name from account entity*/
		    //Any[] params;
		    //Any[] params1;

		    // OTHER
		    String sqlOtherHeader = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'OTHERHEAD'," + 
		    "?,'Description','Charge','','','','','','','','','','','','',?,?,?,?)";
		    String sqlOther = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'OTHER'," + 
		    "?,?,?,'','','','','','','','','','','','',?,?,?,?)";

		    // [ITSM15890] new report sub-section
		    String sqlOtherSub = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'OTHERSUB'," + 
		    "?,?,?,'','','','','','','','','','','','',?,?,?,?)";
		    String sqlOtherTotal = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'OTHERTOTAL'," + 
		    "?,'Total Other Charges for the Run',?,'','','','','','','','','','','','',?,?,?,?)";
		    String sqlNPCharges = "SELECT pre.calculation_result amount, " + 
		    "DECODE(SUBSTR(gst.NAME,1,1), 'V', pre.gst_amount, 0) input_gst, " + 
		    "DECODE(SUBSTR(gst.NAME,1,1), 'A', pre.gst_amount, 0) output_gst, " + 
		    "SUBSTR(ncc.solomon_code || ' - ' || npc.NAME, 1, 60) NAME, ncc.solomon_code " + 
		    "FROM NEM_PERIODIC_RESULTS pre, NEM_NON_PERIOD_CHARGES npc, " + 
		    "NEM_NON_PERIOD_CHARGE_CODES ncc, NEM_GST_CODES gst " + 
		    "WHERE pre.npc_id = npc.ID AND pre.sac_id = ? AND pre.sac_version = ? " + 
		    "AND pre.str_id = ? AND npc.ncc_id = ncc.ID " + 
		    "AND npc.gst_id = gst.ID AND gst.VERSION = ? " +
		    "order by ncc.SOLOMON_CODE";	// DR1SHARP-31, fix to ensure EMCADMIN comes before PSOADMIN
		    String sqlNPCCount = "SELECT count(*) " + 
		    "FROM NEM_PERIODIC_RESULTS pre, NEM_NON_PERIOD_CHARGES npc, " + 
		    "NEM_NON_PERIOD_CHARGE_CODES ncc, NEM_GST_CODES gst " + 
		    "WHERE pre.npc_id = npc.ID AND pre.sac_id = ? AND pre.sac_version = ? " + 
		    "AND pre.str_id = ? AND npc.ncc_id = ncc.ID " + 
		    "AND npc.gst_id = gst.ID AND gst.VERSION = ?";

		    logger.info(logPrefix + "Generating OTHER Reports ...");

		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Generating OTHER Reports ...", "");

		    // /////////////////////////////////////////////////
		    // GENERATE OTHER REPORTS
		    // /////////////////////////////////////////////////	
		    for (Account recaccount : sortAccount) {
		        sacId = recaccount.getAccountId();	//accountLine[39];

		        // account_name
		        // OTHER HEADER
		        seq = seq + 1;
				stmt = conn.prepareStatement(sqlOtherHeader);
				stmt.setInt(1, seq);
				stmt.setString(2, settRunId);
				stmt.setString(3, sacId);
				stmt.setString(4, sacVersion);
				stmt.setString(5, settDate);
				stmt.executeQuery();
				stmt.close();

		        // OTHER DETAILS
		        if (recaccount.isPsoAccount() == false) {
		            // [ITSM15890] add Price Cap info for all accounts except PSO
		            int count = 0;

					stmt = conn.prepareStatement(sqlNPCCount);
					stmt.setString(1, sacId);
					stmt.setString(2, sacVersion);
					stmt.setString(3, settRunId);
					stmt.setString(4, sacVersion);
					stmt.executeQuery();
					rs = stmt.getResultSet();
					
					while (rs.next()) {
						count = rs.getInt(1);
				    }
					rs.close();
					stmt.close();

		            if (count > 0) {
		                BigDecimal cap = BigDecimal.ZERO;
		                BigDecimal adj = BigDecimal.ZERO;

		                if (recaccount.isEmcAccount() == true) {
		                    // EMC Account
		                    cap = recaccount.getAdmFeeCap().setScale(2, RoundingMode.HALF_UP);	//accountLine[118];
		                    adj = recaccount.getAdmFeeAdj().setScale(2, RoundingMode.HALF_UP);	//accountLine[119];
		                } else {
		                	cap = recaccount.getEmcAdmCap().setScale(2, RoundingMode.HALF_UP);	//accountLine[116];
		                	adj = recaccount.getEmcAdmAdj().setScale(2, RoundingMode.HALF_UP);	//accountLine[117];
		                }

		                seq = seq + 1;
		                String feeName;
		                BigDecimal feeRate = new BigDecimal(emcadmPriceCap);

		                if (feeRate.signum() < 0) {
		                    feeName = "EMC Price Cap - (" + feeRate.abs() + ") $/MW";
		                } else {
		                    feeName = "EMC Price Cap - " + emcadmPriceCap + " $/MW";
		                }

						stmt = conn.prepareStatement(sqlOther);
						stmt.setInt(1, seq);
						stmt.setString(2, feeName);
						stmt.setString(3, cap.toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, sacId);
						stmt.setString(6, sacVersion);
						stmt.setString(7, settDate);
						stmt.executeQuery();
						stmt.close();

		                seq = seq + 1;
		                feeRate = new BigDecimal(emcadmPriceAdjRateRound);

		                if (feeRate.signum() < 0) {
		                    feeName = "EMC Price Adj - APPROX. (" + feeRate.abs() + ") $/MW";
		                } else {
		                    feeName = "EMC Price Adj - APPROX. " + emcadmPriceAdjRateRound + " $/MW";
		                }

						stmt = conn.prepareStatement(sqlOther);
						stmt.setInt(1, seq);
						stmt.setString(2, feeName);
						stmt.setString(3, adj.toPlainString());
						stmt.setString(4, settRunId);
						stmt.setString(5, sacId);
						stmt.setString(6, sacVersion);
						stmt.setString(7, settDate);
						stmt.executeQuery();
						stmt.close();
		            }
		        }

		        NonPeriodCharges npc = new NonPeriodCharges();
		        npc.total = recaccount.getEmcAdm().add(recaccount.getPsoAdm());	//((Decimal) accountLine[11]) + ((Decimal) accountLine[69]);
		        //npc.total = - npc.total;

		        // OTHER DETAILS (EMC & PSO Specific SAC ID only)
		        if (recaccount.isEmcAccount() == true) {
		            // EMC Account
		            npc.total = npc.total.subtract(recaccount.getAdmFee());	// - ((Decimal) accountLine[9]);
		        }

		        if (recaccount.isPsoAccount() == true) {
		            // PSO Account
		            npc.total = npc.total.subtract(recaccount.getAdmFee());	// - ((Decimal) accountLine[9]);
		        }

		        // Non Period Charges
				stmt1 = conn.prepareStatement(sqlNPCharges);
				stmt1.setString(1, sacId);
				stmt1.setString(2, sacVersion);
				stmt1.setString(3, settRunId);
				stmt1.setString(4, sacVersion);
				stmt1.executeQuery();
				rs1 = stmt1.getResultSet();
		
		        // npc as NonPeriodCharges
				while (rs1.next()) {
		            BigDecimal amount = rs1.getBigDecimal(1);	//((Decimal) row[1]);
		            BigDecimal inputGst = rs1.getBigDecimal(2);	//((Decimal) row[2]);
		            BigDecimal outputGst = rs1.getBigDecimal(3);	//((Decimal) row[3]);
		            BigDecimal total = amount;

		            npc.total = npc.total.add(total);
		            npc.amount = npc.amount.add(amount);
		            npc.inputGst = npc.inputGst.add(inputGst);
		            npc.outputGst = npc.outputGst.add(outputGst);
		            String name = rs1.getString(4);	//String.valueOf(o : row[4]);
		            String code = rs1.getString(5);	//String.valueOf(o : row[5]);
		            
		            // [ITSM15890] solomon code
		            seq = seq + 1;

		            // [ITSM15890] insert sub-section for all EMCADMIN....
		            if (code.equals("EMCADMIN") == true || code.equals("EMCADMINR") == true) {
						stmt = conn.prepareStatement(sqlOtherSub);
						stmt.setInt(1, seq);
						stmt.setString(2, "Sub Total: " + name);
						stmt.setString(3, UtilityFunctions.trimZeroDecimal(total));
						stmt.setString(4, settRunId);
						stmt.setString(5, sacId);
						stmt.setString(6, sacVersion);
						stmt.setString(7, settDate);
						stmt.executeQuery();
						stmt.close();
		            } else {
						stmt = conn.prepareStatement(sqlOther);
						stmt.setInt(1, seq);
						stmt.setString(2, name);
						stmt.setString(3, UtilityFunctions.trimZeroDecimal(total));
						stmt.setString(4, settRunId);
						stmt.setString(5, sacId);
						stmt.setString(6, sacVersion);
						stmt.setString(7, settDate);
						stmt.executeQuery();
						stmt.close();
		            }
		        }
				rs1.close();
				stmt1.close();

		        // OTHER TOTAL
		        // DRCAP 01 Change  if (settRunType != "F" || (settRunType == "F" && accountLine[109].trim() != "EMC RECOVERY A/C")) {
		        if (settRunType.equals("F") != true || (settRunType.equals("F") == true && recaccount.getDisplayTitle().equals("EMC ADJUST A/C") != true)) {
		            BigDecimal x = BigDecimal.ZERO;
		            
		            if (recaccount.isEmcAccount() == true || recaccount.isPsoAccount() == true) {
		                // EMC/PSO Account
		                x = recaccount.getAdmFee().add(npc.total);	//accountLine[9];
		            } else {
		            	x = recaccount.getOtherTotal().add(npc.total);	//accountLine[107];

		                // account_other_total
		            }

		            String col2 = UtilityFunctions.formatDecimal(x);

		            seq = seq + 1;
					stmt = conn.prepareStatement(sqlOtherTotal);
					stmt.setInt(1, seq);
					stmt.setString(2, col2);
					stmt.setString(3, settRunId);
					stmt.setString(4, sacId);
					stmt.setString(5, sacVersion);
					stmt.setString(6, settDate);
					stmt.executeQuery();
					stmt.close();
		        }
		    }

		    // for each recaccount
		    
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (rs1 != null)
					rs1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt1 != null)
					stmt1.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateReportsForRSRun()
			throws Exception {
			
		String msgStep = "RunResultGenerator.generateReportsForRSRun()";
		PreparedStatement stmt = null;
			
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");

		    String sacId;
		    /* account name from account entity*/
		    //String col11footerNEAA;
		    /* account_NEAA from account entity */
		    String col9footerGMEA;
		    /* account_QMEA from account entity */
		    String col12footerLMEA;
		    /* account_LMEA from account entity */
		    String col2footerNMEA;
		    /* account_NMEA from account entity */
		    String col3footerNMEA;
		    /* account_NMEA from account entity */
		    String col4footerNMEA;
		    /* account_NMEA from account entity */
		    String col5footerNMEA;
		    /* account_NMEA from account entity */
		    String col6footerNMEA;
		    /* account_NMEA from account entity */
		    //String marketStrId;
		    String interval_account;
		    String interval_number;
		    String node_account;
		    String node_interval;
		    //Any[] params1;
		    //Any[] params2;
		    //Any[] params3;
		    //Any[] params4;

		    // GMEA
/*		    String sqlCmd1GMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'GMEAHEADER',?," + 
		    "'Period','MNN','Changed IEQ (MWh)','MEP ($/MWh)','PSO Admin','EMC Admin'," + 
		    "'GMEE','GMEF','GMEA','','','','','',?,?,?,? )";
		    String sqlCmd2GMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'GMEA',?,?,?,?,?," + 
		    "?,?,?,?,?,'','','','','',?,?,?,? )";
		    String sqlCmd3GMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'GMEATOTAL',?," + 
		    "'','','','','','','','Total GMEA',?,'','','','','',?,?,?,? )";

		    // LMEA
		    String sqlCmd1LMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, COLUMN_16, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'LMEAHEADER',?," + 
		    "'Period','Changed WEQ (MWh)','Changed WDQ (MWh)','Changed WMQ (MWh)','Changed WFQ (MWh)','USEP ($/MWh)','AFP ($/MWh)'," + 
		    "'HEUR ($/MWh)','HLCU ($/MWh)','HEUC ($/MWh)','MEUC ($/MWh)','PSO Admin','EMC Admin','LMEE','LMEF','LMEA',?,?,?,? )";
		    String sqlCmd2LMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, COLUMN_16, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'LMEA',?,?,?,?,?," + 
		    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";
		    String sqlCmd3LMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, COLUMN_15, COLUMN_16, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'LMEATOTAL',?," + 
		    "'','','','','','','','','','','','','','','Total LMEA',?,?,?,?,? )";

		    // NMEA
		    String sqlCmd1NMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID() ,'NMEAHEADER',?," + 
		    "'Period','GMEE','GMEF','LMEE','LMEF','NMEA'," + 
		    "'','','','','','','','',?,?,?,? )";
		    String sqlCmd2NMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'NMEA',?,?,?,?,?," + 
		    "?,?,'','','','','','','','',?,?,?,? )";
		    String sqlCmd3NMEA = "INSERT INTO NEM.NEM_ACCOUNT_STATEMENTS (ID, REPORT_SECTION, " + 
		    "SEQ, COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4, COLUMN_5, COLUMN_6, COLUMN_7, " + 
		    "COLUMN_8, COLUMN_9, COLUMN_10, COLUMN_11, COLUMN_12, COLUMN_13, COLUMN_14, " + 
		    "STR_ID, SAC_ID, SAC_VERSION, SETTLEMENT_DATE) VALUES ( SYS_GUID(),'NMEATOTAL',?," + 
		    "'Total',?,?,?,?,?,'','','','','','','','',?,?,?,? )";
*/
		    logger.info(logPrefix + "Generating Account Statement Reports ...");

		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Generating Account Statement Reports ...", "");

			stmt = conn.prepareStatement(commonSqlCmd);
		    for (Account recaccount : sortAccount) {
		        // generatorAccount = false
		        sacId = recaccount.getAccountId();	//accountLine[39];

		        // account_name
		        // 		generatorAccounts[sacId] = false
		        // 		if accountLine[8] = "1" then
		        // 			embeddedGenerators[sacId] = true
		        // 		else
		        // 			embeddedGenerators[sacId] = false
		        // 		end
		        seq = seq + 1;
				prepareAccountStatement(stmt, "GMEAHEADER", seq,
					    "Period","MNN","Changed IEQ (MWh)","MEP ($/MWh)","PSO Admin","EMC Admin",
						"GMEE","GMEF","GMEA",
						"","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        seq = seq + 1;
				prepareAccountStatement(stmt, "LMEAHEADER", seq,
					    "Period","Changed WEQ (MWh)","Changed WDQ (MWh)","Changed WMQ (MWh)","Changed WFQ (MWh)","USEP ($/MWh)","AFP ($/MWh)",
					    "HEUR ($/MWh)","HLCU ($/MWh)","HEUC ($/MWh)","MEUC ($/MWh)","PSO Admin","EMC Admin","LMEE","LMEF","LMEA",
						"","","","",
						settRunId, sacId, sacVersion, settDate);

		        seq = seq + 1;
				prepareAccountStatement(stmt, "NMEAHEADER", seq,
					    "Period","GMEE","GMEF","LMEE","LMEF","NMEA",
					    "","","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);
		    }

		    // for each recaccount
		    for (Account recaccount : sortAccount) {
		        // generatorAccount = false
		        sacId = recaccount.getAccountId();	//accountLine[39];

		        // account_name
		        logger.info(logPrefix + "Insert data into NEM_ACCOUNT_STATEMENTS for SAC_ID: " + sacId);

		        for (Period recinterval : sortInterval) {
		            if (recinterval == null) {
		                throw new Exception("Interval CSV File is Empty !!!");
		            }

		            interval_account = recinterval.getAccountId();	//intervalLine[0];
		            interval_number = recinterval.getPeriodId();	//intervalLine[71];

		            if (interval_account.equals(sacId) == true) {
		                // get market line details
		                /*if (interval_number.trim().length() == 1) {
		                    index = "0" + interval_number.trim();
		                } else {
		                    index = interval_number.trim();
		                }*/

		                //marketLine = sortMarket[index].fields(delim : ",");
		                Market mkt = data.getMarket().get(interval_number);

		                if (mkt == null) {
		                    throw new Exception("Market CSV File is Empty !!!");
		                }

		                /* no longer using after migration
		                marketStrId = mkt.getRunId();	//marketLine[13];

		                if (marketStrId.equals(settRunId) != true) {
		                    logger.warn(logPrefix + "Invalid settlement id in Market CSV File !!!");
		                }

		                market_number = marketLine[11];

		                if (market_number != interval_number) {
		                    logMessage(logPrefix + "Market number and Interval number not same !!!", severity : WARNING);
		                }*/

		                // Process with Node details	
		                for (Facility recnode : sortNode) {

		                    if (recnode == null) {
		                        throw new Exception("Node CSV File is Empty !!!");
		                    }

		                    node_account = recnode.getAccountId();	//nodeLine[5];
		                    node_interval = recnode.getPeriodId();	//nodeLine[17];

		                    if (sacId.equals(node_account) == true && node_interval.equals(interval_number) == true) {
		                        // GMEA DETAILS
		                        if (recnode.getNodeType().equals("G") == true) {
		                            // only for Generator
		                            seq = seq + 1;
		            				prepareAccountStatement(stmt, "GMEA", seq,
		            						Integer.valueOf(recinterval.getPeriodId()).toString(),
		            						recnode.getFacilityId(),
		            						UtilityFunctions.trimZeroDecimal(recnode.getDeltaIeq()),
		            						UtilityFunctions.trimZeroDecimal(recnode.getMep()),
		            					    (recaccount.isUnderRetailer() == true ? "0" : UtilityFunctions.trimZeroDecimal(recinterval.getImpPsoa())),
		            					    (recaccount.isUnderRetailer() == true ? "0" : UtilityFunctions.trimZeroDecimal(recinterval.getImpEmca())),
		            					    UtilityFunctions.trimZeroDecimal(recnode.getGmee()),
		            					    UtilityFunctions.trimZeroDecimal(recnode.getAccountingGmef()),	// UATSHARP-232, use accounting value instead
		            					    UtilityFunctions.trimZeroDecimal(recnode.getGmea()),
		            						"","","","","","","","","","","",
		            						settRunId, sacId, sacVersion, settDate);
		                        }
		                    }

		                    // if sacId = node_account and node_interval = interval_number
		                }

		                // for each recnode
		                seq = seq + 1;
        				prepareAccountStatement(stmt, "LMEA", seq,
        						Integer.valueOf(recinterval.getPeriodId()).toString(),
        						UtilityFunctions.trimZeroDecimal(recinterval.getDeltaWeq()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getDeltaWdq()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getDeltaWmq()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getDeltaWfq()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getImpUsep()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getImpAfp()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getImpHeur()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getImpHlcu()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getImpHeuc()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getImpMeuc()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getImpPsoa()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getImpEmca()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getAccountingLmee()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getAccountingLmef()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getAccountingLmea()),
        						"","","","",
        						settRunId, sacId, sacVersion, settDate);

		                // NMEA DETAILS
		                seq = seq + 1;
        				prepareAccountStatement(stmt, "NMEA", seq,
        						Integer.valueOf(recinterval.getPeriodId()).toString(),
        						UtilityFunctions.trimZeroDecimal(recinterval.getGmee()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getAccountingGmef()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getAccountingLmee()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getAccountingLmef()),
        						UtilityFunctions.trimZeroDecimal(recinterval.getNmea()),
        					    "","","","","","","","","","","","","","",
        						settRunId, sacId, sacVersion, settDate);
		            }

		            // if interval_account = sacId 
		        }

		        // for each recinterval	
		    }

		    for (Account recaccount : sortAccount) {
		        col9footerGMEA = UtilityFunctions.trimZeroDecimal(recaccount.getGmea());	//accountLine[113];
		        col12footerLMEA = UtilityFunctions.trimZeroDecimal(recaccount.getLmea());	//accountLine[114];
		        col2footerNMEA = UtilityFunctions.trimZeroDecimal(recaccount.getGmee());	//accountLine[17];
		        col3footerNMEA = UtilityFunctions.trimZeroDecimal(recaccount.getGmef());	//accountLine[110];
		        col4footerNMEA = UtilityFunctions.trimZeroDecimal(recaccount.getLmee());	//accountLine[111];
		        col5footerNMEA = UtilityFunctions.trimZeroDecimal(recaccount.getLmef());	//accountLine[112];
		        col6footerNMEA = UtilityFunctions.trimZeroDecimal(recaccount.getNmea());	//accountLine[115];
		        sacId = recaccount.getAccountId();	//accountLine[39];

		        // account_name
		        // logMessage logPrefix + "Insert NEM_ACCOUNT_STATEMENTS Footer for SAC_ID: " + sacId
		        // GMEA FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "GMEATOTAL", seq,
						"","","","","","","",
					    "Total GMEA",
					    col9footerGMEA,
						"","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);

		        // LMEA FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "LMEATOTAL", seq,
						"","","","","","","","","","","","","","",
						"Total LMEA",
						col12footerLMEA,
						"","","","",
						settRunId, sacId, sacVersion, settDate);

		        // NMEA FOOTER
		        seq = seq + 1;
				prepareAccountStatement(stmt, "NMEATOTAL", seq,
						"Total",
						col2footerNMEA,
						col3footerNMEA,
						col4footerNMEA,
						col5footerNMEA,
						col6footerNMEA,
					    "","","","","","","","","","","","","","",
						settRunId, sacId, sacVersion, settDate);
		    }
		    stmt.executeBatch();
		    stmt.close();

		    // for each recaccount
		    logger.info(logPrefix + "Nem_Account_Statements Insertion Done.");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void generateSettlementResultsForRS()
			throws Exception {
			
		String msgStep = "RunResultGenerator.generateSettlementResultsForRS()";
		PreparedStatement stmt = null;
		ResultSet rs = null;
			
		try {
		    logger.info(logPrefix + "Starting Method: " + msgStep + " ...");

		    //String col1;
		    /* interval number from interval entity */
		    String sacId = "";
		    /* account name from account entity*/
		    String sqlCommand;
		    String sqlCommand1;
		    //Any[] params1;

		    logger.info(logPrefix + "Inserting data into NEM_SETTLEMENT_RESULTS ...");

		    UtilityFunctions.logJAMMessage(ds, runEventId, "I", msgStep, 
		                                   "Inserting NEM_SETTLEMENT_RESULTS data ...", "");

		    // /////////////////////////////////////////////////
		    // Write data to NEM_SETTLEMENT_RESULTS table
		    // /////////////////////////////////////////////////
		    //String[] params;
		    String srtName;
		    HashMap<String, SettlementResultType> resultTypes = new HashMap<>();
		    
		    sqlCommand1 = "INSERT INTO NEM.NEM_SETTLEMENT_RESULTS VALUES ( SYS_GUID(),?,?,?,?,?,?,?,?,? )";
		    String lastSac = "";

		    sqlCommand = "SELECT ID, NAME, GST_VERSION FROM NEM.NEM_SETTLEMENT_RESULT_TYPES WHERE VERSION = ? ORDER BY NAME";

			stmt = conn.prepareStatement(sqlCommand);
			stmt.setString(1, sacVersion);
			stmt.executeQuery();
			rs = stmt.getResultSet();
			
			while (rs.next()) {
				SettlementResultType rt = new SettlementResultType();
		        srtName = rs.getString(2);
		        rt.id = rs.getString(1);
		        rt.gstVersion = rs.getString(3);
		        
		        resultTypes.put(srtName, rt);
		    }
			rs.close();
			stmt.close();

			stmt = conn.prepareStatement(sqlCommand1);
		    for (Period recinterval : sortInterval) {
		        if (sacId.equals(lastSac) != true) {
		            logger.info(logPrefix + "Insert data into NEM_SETTLEMENT_RESULTS for SAC_ID: " + sacId);

		            lastSac = sacId;
		        }

		        if (recinterval == null) {
		            throw new Exception("Interval CSV File is Empty !!!");
		        }

		        sacId = recinterval.getAccountId();	//intervalLine[0];
		        //col1 = recinterval.getPeriodId();	//intervalLine[71];

		        // chargeable item GMEA
		        BigDecimal gmea = recinterval.getGmea();	//intervalLine[130].trim();

		        if (gmea == null) {
		        	gmea = BigDecimal.ZERO;
		        }

		        BigDecimal gmeaGst = recinterval.getOpGstGmea();	//intervalLine[133].trim();

		        if (gmeaGst == null) {
		        	gmeaGst = BigDecimal.ZERO;
		        }

				//stmt = conn.prepareStatement(sqlCommand1);
				stmt.setInt(1, Integer.parseInt(recinterval.getPeriodId()));
				stmt.setBigDecimal(2, gmea);
				stmt.setBigDecimal(3, gmeaGst);
				stmt.setString(4, settRunId);
				stmt.setString(5, resultTypes.get("GMEA").id);
				stmt.setString(6, resultTypes.get("GMEA").gstVersion);
				stmt.setString(7, sacId);
				stmt.setString(8, sacVersion);
				stmt.setString(9, settDate);
				//stmt.executeQuery();
				//stmt.close();
				stmt.addBatch();

		        // chargeable item GMEE
				BigDecimal gmee = recinterval.getGmee();	//intervalLine[28].trim();

		        if (gmee == null) {
		        	gmee = BigDecimal.ZERO;
		        }

		        BigDecimal gmeeGst = recinterval.getIpGstGmee();	//intervalLine[40].trim();

		        if (gmeeGst == null) {
		        	gmeeGst = BigDecimal.ZERO;
		        }

				//stmt = conn.prepareStatement(sqlCommand1);
				stmt.setInt(1, Integer.parseInt(recinterval.getPeriodId()));
				stmt.setBigDecimal(2, gmee);
				stmt.setBigDecimal(3, gmeeGst);
				stmt.setString(4, settRunId);
				stmt.setString(5, resultTypes.get("GMEE").id);
				stmt.setString(6, resultTypes.get("GMEE").gstVersion);
				stmt.setString(7, sacId);
				stmt.setString(8, sacVersion);
				stmt.setString(9, settDate);
				//stmt.executeQuery();
				//stmt.close();
				stmt.addBatch();

		        // chargeable item GMEF
				BigDecimal gmef = recinterval.getGmef();	//intervalLine[29].trim();

		        if (gmef == null) {
		        	gmef = BigDecimal.ZERO;
		        }

		        BigDecimal gmefGst = recinterval.getOpGstGmef();	//intervalLine[76].trim();

		        if (gmefGst == null) {
		        	gmefGst = BigDecimal.ZERO;
		        }

				//stmt = conn.prepareStatement(sqlCommand1);
				stmt.setInt(1, Integer.parseInt(recinterval.getPeriodId()));
				stmt.setBigDecimal(2, gmef);
				stmt.setBigDecimal(3, gmefGst);
				stmt.setString(4, settRunId);
				stmt.setString(5, resultTypes.get("GMEF").id);
				stmt.setString(6, resultTypes.get("GMEF").gstVersion);
				stmt.setString(7, sacId);
				stmt.setString(8, sacVersion);
				stmt.setString(9, settDate);
				//stmt.executeQuery();
				//stmt.close();
				stmt.addBatch();

		        // chargeable item LMEA
				BigDecimal lmea = recinterval.getLmea();	//intervalLine[131].trim();

		        if (lmea == null) {
		        	lmea = BigDecimal.ZERO;
		        }

		        BigDecimal lmeaGst = recinterval.getOpGstLmea();	//intervalLine[132].trim();

		        if (lmeaGst == null) {
		        	lmeaGst = BigDecimal.ZERO;
		        }

				//stmt = conn.prepareStatement(sqlCommand1);
				stmt.setInt(1, Integer.parseInt(recinterval.getPeriodId()));
				stmt.setBigDecimal(2, lmea);
				stmt.setBigDecimal(3, lmeaGst);
				stmt.setString(4, settRunId);
				stmt.setString(5, resultTypes.get("LMEA").id);
				stmt.setString(6, resultTypes.get("LMEA").gstVersion);
				stmt.setString(7, sacId);
				stmt.setString(8, sacVersion);
				stmt.setString(9, settDate);
				//stmt.executeQuery();
				//stmt.close();
				stmt.addBatch();

		        // chargeable item LMEE
				BigDecimal lmee = recinterval.getLmee();	//intervalLine[52].trim();

		        if (lmee == null) {
		        	lmee = BigDecimal.ZERO;
		        }

		        BigDecimal lmeeGst = recinterval.getOpGstLmee();	//intervalLine[81].trim();

		        if (lmeeGst == null) {
		        	lmeeGst = BigDecimal.ZERO;
		        }

				//stmt = conn.prepareStatement(sqlCommand1);
				stmt.setInt(1, Integer.parseInt(recinterval.getPeriodId()));
				stmt.setBigDecimal(2, lmee);
				stmt.setBigDecimal(3, lmeeGst);
				stmt.setString(4, settRunId);
				stmt.setString(5, resultTypes.get("LMEE").id);
				stmt.setString(6, resultTypes.get("LMEE").gstVersion);
				stmt.setString(7, sacId);
				stmt.setString(8, sacVersion);
				stmt.setString(9, settDate);
				//stmt.executeQuery();
				//stmt.close();
				stmt.addBatch();

		        // chargeable item LMEF
				BigDecimal lmef = recinterval.getLmef();	//getAccountingLmef();	//intervalLine[53].trim(); // using Lmef instead

		        if (lmef == null) {
		        	lmef = BigDecimal.ZERO;
		        }

		        BigDecimal lmefGst = recinterval.getOpGstLmef();	//intervalLine[82].trim();

		        if (lmefGst == null) {
		        	lmefGst = BigDecimal.ZERO;
		        }

				//stmt = conn.prepareStatement(sqlCommand1);
				stmt.setInt(1, Integer.parseInt(recinterval.getPeriodId()));
				stmt.setBigDecimal(2, lmef);
				stmt.setBigDecimal(3, lmefGst);
				stmt.setString(4, settRunId);
				stmt.setString(5, resultTypes.get("LMEF").id);
				stmt.setString(6, resultTypes.get("LMEF").gstVersion);
				stmt.setString(7, sacId);
				stmt.setString(8, sacVersion);
				stmt.setString(9, settDate);
				//stmt.executeQuery();
				//stmt.close();
				stmt.addBatch();

		        // chargeable item NMEA with interval_a_NMEA
				BigDecimal nmea = recinterval.getNmea();	//intervalLine[66].trim();

		        if (nmea == null) {
		        	nmea = BigDecimal.ZERO;
		        }

		        BigDecimal nmeaIGst = recinterval.getIpGstNmea();	//intervalLine[46].trim();

		        if (nmeaIGst == null) {
		        	nmeaIGst = BigDecimal.ZERO;
		        }

		        BigDecimal nmeaOGst = recinterval.getOpGstNmea();	//intervalLine[86].trim();

		        if (nmeaOGst == null) {
		        	nmeaOGst = BigDecimal.ZERO;
		        }

				//stmt = conn.prepareStatement(sqlCommand1);
				stmt.setInt(1, Integer.parseInt(recinterval.getPeriodId()));
				stmt.setBigDecimal(2, nmea);
				stmt.setBigDecimal(3, nmeaIGst.add(nmeaOGst));
				stmt.setString(4, settRunId);
				stmt.setString(5, resultTypes.get("NMEA").id);
				stmt.setString(6, resultTypes.get("NMEA").gstVersion);
				stmt.setString(7, sacId);
				stmt.setString(8, sacVersion);
				stmt.setString(9, settDate);
				//stmt.executeQuery();
				//stmt.close();
				stmt.addBatch();
		    }
		    stmt.executeBatch();
		    stmt.close();

		    logger.info(logPrefix + "Nem_Settlement_Results Insertion Done.");
		} catch (Exception e) {
		    logger.error(logPrefix + "<" + msgStep + "> Exception: ", e);	// + e.toString());
		
		    UtilityFunctions.logJAMMessage(ds, runEventId, "E", 
		                                   msgStep, e.toString(), "");
		
		    throw new SettlementRunException(e.toString(), msgStep);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isPriceCapZero() {
		BigDecimal final_rate;

		if (settRunType.equals("P") == true || settRunType.equals("F") == true) {
		    final_rate = data.getGlobal().getEmcAdmPriceAdjRate().add(data.getGlobal().getEmcAdmPriceCap());	// + ((Decimal) emcadmPriceCap);

		    if (final_rate.signum() == 0) {
		        return true;
		    } else {
		        return false;
		    }
		} else {
		    return false;
		}
	}
	
	public boolean isPriceCapNegative() {
		BigDecimal final_rate;

		if (settRunType.equals("P") == true || settRunType.equals("F") == true) {
		    final_rate = data.getGlobal().getEmcAdmPriceAdjRate().add(data.getGlobal().getEmcAdmPriceCap());	// + ((Decimal) emcadmPriceCap);

		    if (final_rate.signum() < 0) {
		        return true;
		    } else {
		        return false;
		    }
		} else {
		    return false;
		}
	}
	
	public void prepareAccountStatement(PreparedStatement stmt, String section, int seq,
			String col1, String col2, String col3, String col4, String col5, String col6, String col7, String col8, String col9, String col10,
			String col11, String col12, String col13, String col14, String col15, String col16, String col17, String col18, String col19, String col20,
			String strId, String sacId, String sacVer, String sd)
			throws Exception {
	    
		stmt.setString(1, section);
		stmt.setInt(2, seq);
		stmt.setString(3, col1);
		stmt.setString(4, col2);
		stmt.setString(5, col3);
		stmt.setString(6, col4);
		stmt.setString(7, col5);
		stmt.setString(8, col6);
		stmt.setString(9, col7);
		stmt.setString(10, col8);
		stmt.setString(11, col9);
		stmt.setString(12, col10);
		stmt.setString(13, col11);
		stmt.setString(14, col12);
		stmt.setString(15, col13);
		stmt.setString(16, col14);
		stmt.setString(17, col15);
		stmt.setString(18, col16);
		stmt.setString(19, col17);
		stmt.setString(20, col18);
		stmt.setString(21, col19);
		stmt.setString(22, col20);
		stmt.setString(23, strId);
		stmt.setString(24, sacId);
		stmt.setString(25, sacVer);
		stmt.setString(26, sd);
		stmt.addBatch();
	}
}

class ResultDetails {
	String sac;
	String sacVer;
}

class NonPeriodCharges {
	BigDecimal amount = BigDecimal.ZERO;
	BigDecimal inputGst = BigDecimal.ZERO;
	BigDecimal outputGst = BigDecimal.ZERO;
	BigDecimal total = BigDecimal.ZERO;
}
