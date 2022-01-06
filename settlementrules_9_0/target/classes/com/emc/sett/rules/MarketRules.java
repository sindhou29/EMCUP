package com.emc.sett.rules;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.kie.api.runtime.KieRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.sett.common.AbstractSettlementData;
import com.emc.sett.common.IRuleflowInterface;
import com.emc.sett.common.SettlementRunException;
import com.emc.sett.impl.Account;
import com.emc.sett.impl.Adjustment;
import com.emc.sett.impl.Bilateral;
import com.emc.sett.impl.Brq;
import com.emc.sett.impl.Cnmea;
import com.emc.sett.impl.CnmeaSettRe;
import com.emc.sett.impl.Facility;
import com.emc.sett.impl.Fsc;
import com.emc.sett.impl.Ftr;
import com.emc.sett.impl.Market;
import com.emc.sett.impl.Mnmea;
import com.emc.sett.impl.MnmeaSub;
import com.emc.sett.impl.Participant;
import com.emc.sett.impl.Period;
import com.emc.sett.impl.Rerun;
import com.emc.sett.impl.Reserve;
import com.emc.sett.impl.RsvClass;
import com.emc.sett.impl.SettlementData;
import com.emc.sett.impl.Tvc;
import com.emc.sett.impl.Vesting;
import com.emc.sett.utils.CsvHelper;
import com.emc.sett.utils.DatabaseReader;
import com.emc.sett.utils.ResultComparators;
import com.emc.sett.utils.RunResultGenerator;
import com.emc.sett.utils.UtilityFunctions;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;

public class MarketRules implements IRuleflowInterface {
	
    private static final Logger logger = LoggerFactory.getLogger(MarketRules.class);
    
    private static final String RULES_VERSION = 
    		"Settlement Rules library version 9.0 (RM499 - CP66 - CR576 merged with RM00001132 - Net AFP for Residential Consumers Embedded with IGS)";
    private static final String [][] RULE_GROUPS = {{"reserve","ksession-reserve"},{"acct_stmt","ksession-acct_stmt"}};
    
    private boolean csvHasHeader = false;
    private DataSource dataSource = null;

    private boolean priceCapZeroAlert = false;
    private boolean priceCapNegativeAlert = false;
    
	@Override
	public void setDataSource(DataSource ds) {
    	this.dataSource = ds;
	}

	@Override
	public String getVersionString() {
		return RULES_VERSION;
	}

	@Override
	public String [][] getRuleflowGroups() {
		int len = RULE_GROUPS.length;
		String [][] grps = new String[len][2];
		for (int i=0; i<RULE_GROUPS.length; i++) {
			grps[i][0] = RULE_GROUPS[i][0];
			grps[i][1] = RULE_GROUPS[i][1];
		}
		return grps;
	}

	@Override
	public AbstractSettlementData prepareData(String logPrefix, SettRunPkg runPackage, SettlementRunParams params,
			DataSource ds) throws Exception {

	    logger.info(logPrefix + "Starting rules: " + RULES_VERSION );
	    
	    if ( params.runType.equalsIgnoreCase("P") || params.runType.equalsIgnoreCase("F")) {
	    	if (params.isFSCEffective == true) {
		    	SimpleDateFormat qdf = new SimpleDateFormat("dd-MMM-yyyy");
		        String msg = "Settlement Date " + qdf.format(params.settlementDate) + 
		        		" is within FSC Scheme Effective Start Date " + 
				    	qdf.format(UtilityFunctions.getSysParamTime(ds, "FSC_EFF_START_DATE")) + 
				        " and Effective End Date " + qdf.format(UtilityFunctions.getSysParamTime(ds, "FSC_EFF_END_DATE"));
		        UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", "MarketRules.prepareData", msg, "");
	    	} else {
	    		params.isFSCEffective = UtilityFunctions.isFSCEffDate(ds, params.sqlSettlementDate);
		    	if (params.isFSCEffective == true) {
			    	SimpleDateFormat qdf = new SimpleDateFormat("dd-MMM-yyyy");
			        String msg = "Settlement Date " + qdf.format(params.settlementDate) + 
			        		" is within FSC Scheme Effective Start Date " + 
					    	qdf.format(UtilityFunctions.getSysParamTime(ds, "FSC_EFF_START_DATE")) + 
					        " and Effective End Date " + qdf.format(UtilityFunctions.getSysParamTime(ds, "FSC_EFF_END_DATE"));
			        UtilityFunctions.logJAMMessage(ds, params.runEveId, "I", "MarketRules.prepareData", msg, "");
		    	}
	    	}
	    }

		AbstractSettlementData data = null;
		
		if (params.isRegressionMode() == false) {
			data = generateDataCSV(logPrefix, runPackage, params, ds);
		} else {
			data = readInputCSV(logPrefix, runPackage, params);
		}
		return data;
	}

	@Override
	public AbstractSettlementData generateDataCSV(String logPrefix, SettRunPkg runPackage, SettlementRunParams params,
			DataSource ds) throws Exception {
		
		SettlementData data = new SettlementData();
		
		DatabaseReader.readGlobal(logPrefix, runPackage, params, data, ds);
		
		if (params.runType.equalsIgnoreCase("P") || params.runType.equalsIgnoreCase("F")) {
			DatabaseReader.readNodesPF(logPrefix, runPackage, params, data, ds);
		} else if (params.runType.equalsIgnoreCase("R") || params.runType.equalsIgnoreCase("S")) {
			DatabaseReader.readNodesRS(logPrefix, runPackage, params, data, ds);
		} else {
		    throw new SettlementRunException("Invalid run type.", MarketRules.class + ".prepareData()");
		}
		
		DatabaseReader.readAccounts(logPrefix, runPackage, params, data, ds);
		
//		DatabaseReader.readReserves(logPrefix, runPackage, params, data, ds);	// move to the last
		
//		DatabaseReader.readAdjustment(logPrefix, runPackage, params, data, ds);	// move down after rerun and period
		
		DatabaseReader.readMarket(logPrefix, runPackage, params, data, ds);
		
		DatabaseReader.readRerun(logPrefix, runPackage, params, data, ds);
		
		if (params.runType.equalsIgnoreCase("P") || params.runType.equalsIgnoreCase("F")) {
			DatabaseReader.readIntervalPF(logPrefix, runPackage, params, data, ds);
		} else if (params.runType.equalsIgnoreCase("R") || params.runType.equalsIgnoreCase("S")) {
			DatabaseReader.readIntervalRS(logPrefix, runPackage, params, data, ds);
		} else {
		    throw new SettlementRunException("Invalid run type.", MarketRules.class + ".prepareData()");
		}
		
		DatabaseReader.readAdjustment(logPrefix, runPackage, params, data, ds);
		
		DatabaseReader.readClasses(logPrefix, runPackage, params, data, ds);
		
		DatabaseReader.readReserves(logPrefix, runPackage, params, data, ds);
		
		//
		// Generate the input CSV files..
		//
		generateInputCSV(params, data);
		
		return data;
	}

	@Override
	public AbstractSettlementData readInputCSV(String logPrefix, SettRunPkg runPackage, SettlementRunParams params) throws Exception {
		
		String path = params.getRegressionData() + "/input";
		
		SettlementData data = new SettlementData();
		
		csvHasHeader = CsvHelper.readCsvGlobal(path, data);
		
		CsvHelper.readCsvMarkets(path, data, csvHasHeader);
		
		CsvHelper.readCsvBRQs(path, data, csvHasHeader);
		
		CsvHelper.readCsvClass(path, data, csvHasHeader);
		
		CsvHelper.readCsvReserve(path, data, csvHasHeader);
		
		CsvHelper.readCsvNodes(path, data, csvHasHeader);
		
		CsvHelper.readCsvAccounts(path, data, csvHasHeader);
		
		CsvHelper.readCsvParticipants(path, data, csvHasHeader);
		
		CsvHelper.readCsvIntervals(path, data, csvHasHeader);
		
		CsvHelper.readCsvBilaterals(path, data, csvHasHeader);
		
		CsvHelper.readCsvFtrs(path, data, csvHasHeader);
		
		CsvHelper.readCsvReruns(path, data, csvHasHeader);
		
		CsvHelper.readCsvMnmeaSub(path, data, csvHasHeader);
		
		CsvHelper.readCsvMnmea(path, data, csvHasHeader);
		
		CsvHelper.readCsvNmeaGrp(path, data, csvHasHeader);
		
		CsvHelper.readCsvCnmea(path, data, csvHasHeader);
		
		CsvHelper.readCsvTvc(path, data, csvHasHeader);
		
		CsvHelper.readCsvVesting(path, data, csvHasHeader);
		
		CsvHelper.readCsvFsc(path, data, csvHasHeader);
		
		CsvHelper.readCsvAdjustments(path, data, csvHasHeader);
		
		return data;
	}

	@Override
	public void populateFacts(KieRuntime kie, AbstractSettlementData data, String ruleflowGroup) throws Exception {

		if (ruleflowGroup.equals(RULE_GROUPS[0][0]) == true) {
			insertReserveFacts(kie, (SettlementData) data);
		} else if (ruleflowGroup.equals(RULE_GROUPS[1][0]) == true) {
			insertSettStmtFacts(kie, (SettlementData) data);
		} else {
			throw new SettlementRunException("Unknown ruleflow group - " + ruleflowGroup);
		}
	}

	@Override
	public boolean generateInputCSV(SettlementRunParams params, AbstractSettlementData data) {
		
		try {
			CsvHelper.dumpAllAsInputFiles(createDirectory(params, "input"), (SettlementData)data, true);
			return true;
		} catch (Exception e) {
			logger.error("generateInputCSV: ", e);
		}
		return false;
	}

	@Override
	public boolean generateOutputCSV(SettlementRunParams params, AbstractSettlementData data) {
		
		try {
			CsvHelper.dumpAllAsOutputFiles(createDirectory(params, "output"), (SettlementData)data, true);
			return true;
		} catch (Exception e) {
			logger.error("generateOutputCSV: ", e);
		}
		return false;
	}

	@Override
	public AlertNotification storeData(String logPrefix, SettRunPkg runPackage, SettlementRunParams params, AlertNotification alert,
			AbstractSettlementData data, DataSource ds) throws Exception {
		
		AlertNotification result = alert;
		
		if (params.isRegressionMode() == false) {
			generateOutputCSV(params, data);
			
			if (params.isTestingMode() == false) {
				writeResultsDB(logPrefix, runPackage, params, data, ds);
				result = alertNotofication(logPrefix, params, alert, ds);
			}
		} else {
			compareOutputCSV(params, data);
		}
		
		return result;
	}

	@Override
	public void writeResultsDB(String logPrefix, SettRunPkg runPackage, SettlementRunParams params,
			AbstractSettlementData data, DataSource ds) throws Exception {

		String msgStep = MarketRules.class + ".storeData()";

	    logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

	    RunResultGenerator resultWriter = new RunResultGenerator(logPrefix, params.runEveId);
	    
	    resultWriter.startGeneration(ds);
	    
	    // resultWriter.readCSVFilesDRandFSC(csvfilepath : csvfilepath);
	    resultWriter.readSettlementData(runPackage, params, (SettlementData)data);

		if (params.runType.equalsIgnoreCase("P") || params.runType.equalsIgnoreCase("F")) {
	        // Insert USAP, NEUA, NARP and Run Statements Results
	        // resultWriter.generateMarketResultsPC();          
	        // [ITSM 17002] 7.1.05
	        // boolean WMEProundedEMCfeesRateEff = UtilityFunctions.isAfterWMEPunroundedEMCfeesEffectiveDate(settDate : settlementParam.settlementDate);              
	        if (params.isWMEPRoundedFeeEffDate == true) {
	            //resultWriter.generateMarketResultsDRFSCIGS();
	            resultWriter.generateMarketResultsIGS();

	            // IGS Scheme also added in this result writing //7.1 01
	        } else {
	            //resultWriter.generateMarketResultsDRandFSC();
	            resultWriter.generateMarketResults();

	            // Added for FSC Implemenation [ITSM-16708]
	        }

	        // resultWriter.generateMarketResultsFSC(); //Added for FSC Implemenation [ITSM-16708]
	        // write NEM_RUN_STATEMENTS
	        // Insert Settlement Results
	        // resultWriter.generateSettlementResults();
	        resultWriter.generateSettlementResults();

	        // Added for FSC Implemenation [ITSM-16708]
	        // reserve Non Period Charges
	        resultWriter.reverseNonPeriodCharges();

	        // Generate Non Period Charges
	        resultWriter.generateNonPeriodCharges();

	        // Generate periodic Charges
	        resultWriter.generatePeriodicCharges();

	        // read Fees GST from Haley directly
	        // Generate Market Participant Reports after EMC Price Cap Regime
	        // resultWriter.generateMPReportsPC();
	        resultWriter.generateMPReports();

	        // Added for FSC Implemenation [ITSM-16708]
	        // read new interval
	        // Generate Main Reports
	        // resultWriter.generateMainReportsEG();
	        resultWriter.generateMainReports();

	        // Generate Participant Summary
	        resultWriter.generateParticipantSummary();

	        // Added for FSC Implemenation [ITSM-16708]
	        // Generate Other Reports									// read new account
	        resultWriter.generateOtherReports();

	        // [ITSM15890] Set alert status
	        priceCapZeroAlert = resultWriter.isPriceCapZero();
	        priceCapNegativeAlert = resultWriter.isPriceCapNegative();
		} else if (params.runType.equalsIgnoreCase("R") || params.runType.equalsIgnoreCase("S")) {
	        // Generate Market Participant Reports
	        resultWriter.generateReportsForRSRun();

	        // Generate Settlement Results
	        resultWriter.generateSettlementResultsForRS();
		}
		
		resultWriter.endGeneration();
	}

	@Override
	public boolean compareOutputCSV(SettlementRunParams params, AbstractSettlementData data) {
		
		SettlementData inp = (SettlementData)data;
		try {
			boolean rrun = "RSrs".contains(inp.getGlobal().getRunType());
			String path = params.getRegressionData() + "/output";
			
			logger.info("Settlement Date: " + inp.getGlobal().getTradingDate());
			logger.info("Run Type       : " + inp.getGlobal().getRunType());
			
			ResultComparators.compareMarketResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareClassResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareReserveResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareFacilityResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.comparePeriodResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareBilateralResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareAccountResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareRerunResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareAdjustmentResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareTvcResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareVestingResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareFscResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareGlobalResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareCnmeaSettReResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareCnmeaResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareMnmeaResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareMnmeaSubResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			ResultComparators.compareParticipantResults(path, (SettlementData)data, csvHasHeader, rrun);
			
			return true;
		} catch (Exception e) {
			logger.error("compareOutputCSV: ", e);
		}
		return false;
	}
	
	/*
	 * [ITSM15890] Part of BR006 - Alert checking
	 */
	private AlertNotification alertNotofication(String logPrefix, SettlementRunParams params, AlertNotification alert, DataSource ds) throws Exception {
		
	    logger.info(logPrefix + "Starting Activity: alertNotofication ...");
	    
	    if (priceCapZeroAlert == true || priceCapNegativeAlert == true) {
	    	
	    	if (alert != null) {
	    		
			    // make sure it is off first
		        alert.notificationReady = false;
		
			    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		        String msg = null;
	
		        if (priceCapZeroAlert == true) {
		            msg = "EMC Admin Fee for Settlement Date " + df.format(params.sqlSettlementDate) + 
		                  " is Zero. Kindly contact Finance.";
		        }
	
		        if (priceCapNegativeAlert == true) {
		            msg = "EMC Admin Fee for Settlement Date " + df.format(params.sqlSettlementDate) + 
		                  " is Negative. Kindly contact Finance.";
		        }
	
		        // BPM Log
		        logger.warn(logPrefix + msg);
	
		        // Send Alert Email
		        alert.businessModule = "CSV Write Back to DB (Price Cap)";
		        alert.recipients = System.getProperty("emcpso.upload.fail.email");
		        alert.subject = "EMC Price Adj Rate warning";
		        alert.content = msg;
		        alert.noticeType = "EMC Price Adj Rate warning";
		        
		        // set true to indicate alert required
		        alert.notificationReady = true;
	
		        // Log JAM Message
		        UtilityFunctions.logJAMMessage(ds, params.runEveId, "W", "MarketRules.alertNotofication", msg, "");
	    	} else {
		        logger.warn(logPrefix + " AlertNotification object is not available");
	    	}
	    }
	        
		return alert;
	}
	
	private void insertReserveFacts(KieRuntime kie, SettlementData data) throws Exception {
		try {
			kie.setGlobal("csz", data.getGlobal().getCsz());
			
			for (Map.Entry<String, Market> entry : data.getMarket().entrySet()) {
				kie.insert((Market)entry.getValue());
			}
			for (Map.Entry<String, Facility> entry : data.getFacility().entrySet()) {
				kie.insert((Facility)entry.getValue());
			}
			for (Map.Entry<String, Reserve> entry : data.getReserve().entrySet()) {
				kie.insert((Reserve)entry.getValue());
			}
			for (Map.Entry<String, RsvClass> entry : data.getRsvClass().entrySet()) {
				kie.insert((RsvClass)entry.getValue());
			}
			for (Map.Entry<String, Brq> entry : data.getBrq().entrySet()) {
				kie.insert((Brq)entry.getValue());
			}
			
		} catch (Exception e) {
			logger.error("insertReserveFacts: ", e);
			throw e;
		}
	}
	
	private void insertSettStmtFacts(KieRuntime kie, SettlementData data) throws Exception {
		try {
			kie.setGlobal("csz", data.getGlobal().getCsz());
			kie.setGlobal("igsEffective", data.getGlobal().isIgsEffective());
			kie.setGlobal("inGstRate", data.getGlobal().getInputGstRate());
			kie.setGlobal("outGstRate", data.getGlobal().getOutputGstRate());
			kie.setGlobal("meuc", data.getGlobal().getMeuc());
			kie.setGlobal("isWithinFSCSchemeEffectiveDateRange", false);
			kie.setGlobal("nafpEffective", data.getGlobal().isNafpEffective());
			
			GregorianCalendar gcal = new GregorianCalendar();
			XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
			kie.setGlobal("currentTime", xgcal);
			
			kie.insert(data.getGlobal());
			for (Map.Entry<String, Market> entry : data.getMarket().entrySet()) {
				kie.insert((Market)entry.getValue());
			}
			for (Map.Entry<String, Participant> entry : data.getParticipant().entrySet()) {
				kie.insert((Participant)entry.getValue());
			}
			for (Map.Entry<String, Account> entry : data.getAccount().entrySet()) {
				kie.insert((Account)entry.getValue());
			}
			for (Map.Entry<String, Period> entry : data.getPeriod().entrySet()) {
				kie.insert((Period)entry.getValue());
			}
			for (Map.Entry<String, Bilateral> entry : data.getBilateral().entrySet()) {
				kie.insert((Bilateral)entry.getValue());
			}
			for (Map.Entry<String, Vesting> entry : data.getVesting().entrySet()) {
				kie.insert((Vesting)entry.getValue());
			}
			for (Map.Entry<String, Tvc> entry : data.getTVC().entrySet()) {
				kie.insert((Tvc)entry.getValue());
			}
			for (Map.Entry<String, Fsc> entry : data.getFsc().entrySet()) {
				kie.insert((Fsc)entry.getValue());
			}
			for (Map.Entry<String, Facility> entry : data.getFacility().entrySet()) {
				kie.insert((Facility)entry.getValue());
			}
			for (Map.Entry<String, Ftr> entry : data.getFtr().entrySet()) {
				kie.insert((Ftr)entry.getValue());
			}
			for (Map.Entry<String, Rerun> entry : data.getRerun().entrySet()) {
				kie.insert((Rerun)entry.getValue());
			}
			for (Map.Entry<String, MnmeaSub> entry : data.getMnmeaSub().entrySet()) {
				kie.insert((MnmeaSub)entry.getValue());
			}
			for (Map.Entry<String, Adjustment> entry : data.getAdjustment().entrySet()) {
				kie.insert((Adjustment)entry.getValue());
			}
			for (Map.Entry<String, Cnmea> entry : data.getCnmea().entrySet()) {
				kie.insert((Cnmea)entry.getValue());
			}
			for (Map.Entry<String, Mnmea> entry : data.getMnmea().entrySet()) {
				kie.insert((Mnmea)entry.getValue());
			}
			for (Map.Entry<String, CnmeaSettRe> entry : data.getCnmeaSettRe().entrySet()) {
				kie.insert((CnmeaSettRe)entry.getValue());
			}
			
		} catch (Exception e) {
			logger.error("insertSettStmtFacts: ", e);
			throw e;
		}
	}
	
	private String createDirectory(SettlementRunParams params, String storage) throws Exception {
		
		File f = null;
		String path = storage;
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    SimpleDateFormat rdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
	    
	    path = params.getCsvStorage() + "/" + sdf.format(params.sqlSettlementDate);	    
	    f = new File(path);
	    if (f.isDirectory() == false) {
		    if (f.mkdirs() == false) {
		    	throw new Exception("Cannot Create Directory: " + path);
		    }
	    }
	    
	    path = path + "/" + params.runType;	    
	    f = new File(path);
	    if (f.isDirectory() == false) {
		    if (f.mkdirs() == false) {
		    	throw new Exception("Cannot Create Directory: " + path);
		    }
	    }
	    
	    path = path + "/" + rdf.format(params.runDate);	    
	    f = new File(path);
	    if (f.isDirectory() == false) {
		    if (f.mkdirs() == false) {
		    	throw new Exception("Cannot Create Directory: " + path);
		    }
	    }
	    
	    path = path + "/" + storage;	    
	    f = new File(path);
	    if (f.isDirectory() == false) {
		    if (f.mkdirs() == false) {
		    	throw new Exception("Cannot Create Directory: " + path);
		    }
	    }
		 
		return path;
	}
}
