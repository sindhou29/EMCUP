package com.emc.sett.utils;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.sett.impl.Account;
import com.emc.sett.impl.Adjustment;
import com.emc.sett.impl.Bilateral;
import com.emc.sett.impl.Cnmea;
import com.emc.sett.impl.Facility;
import com.emc.sett.impl.Fsc;
import com.emc.sett.impl.Global;
import com.emc.sett.impl.Market;
import com.emc.sett.impl.Mnmea;
import com.emc.sett.impl.MnmeaSub;
import com.emc.sett.impl.CnmeaSettRe;
import com.emc.sett.impl.Participant;
import com.emc.sett.impl.Period;
import com.emc.sett.impl.Rerun;
import com.emc.sett.impl.Reserve;
import com.emc.sett.impl.RsvClass;
import com.emc.sett.impl.SettlementData;
import com.emc.sett.impl.Tvc;
import com.emc.sett.impl.Vesting;
import com.emc.sett.utils.CSVReader;

public class ResultComparators {
	
    private static final Logger logger = LoggerFactory.getLogger(ResultComparators.class);

	public static void compareMarketResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Market> MarketResults = new HashMap<String, Market>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.MARKET_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Market clazz = new Market();
			clazz.initOutput(nextLine);
			
			MarketResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Market> entry : data.getMarket().entrySet()) {
			
			Market actual = entry.getValue();
			
			Market expect = MarketResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Market.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
	            logger.error( "not found: " + actual.getKey());
			}
		}
        logger.info( "Market count: " + MarketResults.size() + " expected, " + data.getMarket().size() + " actual");
        logger.info( "Market: " + pass + " - passed, " + fail + " - failed");
	}
	
	public static void compareClassResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, RsvClass> RsvClassResults = new HashMap<String, RsvClass>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.CLASS_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
			RsvClass clazz = new RsvClass();
			clazz.initOutput(nextLine);
			
			RsvClassResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, RsvClass> entry : data.getRsvClass().entrySet()) {
			
			RsvClass actual = entry.getValue();
			
			RsvClass expect = RsvClassResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + RsvClass.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
	            logger.error( "not found: " + actual.getKey());
			}
		}
        logger.info( "RsvClass count: " + RsvClassResults.size() + " expected, " + data.getRsvClass().size() + " actual");
        logger.info( "RsvClass: " + pass + " - passed, " + fail + " - failed");
	}
	
	public static void compareReserveResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Reserve> ReserveResults = new HashMap<String, Reserve>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.RESERVE_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Reserve clazz = new Reserve();
			clazz.initOutput(nextLine);
			
			ReserveResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Reserve> entry : data.getReserve().entrySet()) {
			
			Reserve actual = entry.getValue();
			
			Reserve expect = ReserveResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Reserve.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
	            logger.error( "not found: " + actual.getKey());
			}
		}
        logger.info( "Reserve count: " + ReserveResults.size() + " expected, " + data.getReserve().size() + " actual");
        logger.info( "Reserve: " + pass + " - passed, " + fail + " - failed");
	}
	
	public static void compareFacilityResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Facility> FacilityResults = new HashMap<String, Facility>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.NODE_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Facility clazz = new Facility();
			clazz.initOutput(nextLine);
			
			FacilityResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Facility> entry : data.getFacility().entrySet()) {
			
			Facility actual = entry.getValue();
			
			Facility expect = FacilityResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Facility.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
	            logger.error( "not found: " + actual.getKey());
			}
		}
        logger.info( "Facility count: " + FacilityResults.size() + " expected, " + data.getFacility().size() + " actual");
        logger.info( "Facility: " + pass + " - passed, " + fail + " - failed");
	}
	
	public static void comparePeriodResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Period> PeriodResults = new HashMap<String, Period>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.INTERVAL_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Period clazz = new Period();
			clazz.initOutput(nextLine);
			
			PeriodResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Period> entry : data.getPeriod().entrySet()) {
			
			Period actual = entry.getValue();
			
			Period expect = PeriodResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Period.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
	            logger.error( "not found: " + actual.getKey());
			}
		}
        logger.info( "Period count: " + PeriodResults.size() + " expected, " + data.getPeriod().size() + " actual");
        logger.info( "Period: " + pass + " - passed, " + fail + " - failed");
	}
	
	public static void compareBilateralResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Bilateral> BilateralResults = new HashMap<String, Bilateral>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.BILATERAL_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Bilateral clazz = new Bilateral();
			clazz.initOutput(nextLine);
			
			BilateralResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Bilateral> entry : data.getBilateral().entrySet()) {
			
			Bilateral actual = entry.getValue();
			
			Bilateral expect = BilateralResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Bilateral.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "Bilateral count: " + BilateralResults.size() + " expected, " + data.getBilateral().size() + " actual");
		logger.info( "Bilateral: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareAccountResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Account> AccountResults = new HashMap<String, Account>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.ACCOUNT_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Account clazz = new Account();
			clazz.initOutput(nextLine);
			
			AccountResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Account> entry : data.getAccount().entrySet()) {
			
			Account actual = entry.getValue();
			
			Account expect = AccountResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Account.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "Account count: " + AccountResults.size() + " expected, " + data.getAccount().size() + " actual");
		logger.info( "Account: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareRerunResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Rerun> RerunResults = new HashMap<String, Rerun>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.RERUN_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Rerun clazz = new Rerun();
			clazz.initOutput(nextLine);
			
			RerunResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Rerun> entry : data.getRerun().entrySet()) {
			
			Rerun actual = entry.getValue();
			
			Rerun expect = RerunResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Rerun.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "Rerun count: " + RerunResults.size() + " expected, " + data.getRerun().size() + " actual");
		logger.info( "Rerun: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareAdjustmentResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Adjustment> AdjustmentResults = new HashMap<String, Adjustment>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.ADJUSTMENT_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Adjustment clazz = new Adjustment();
			clazz.initOutput(nextLine);
			
			AdjustmentResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Adjustment> entry : data.getAdjustment().entrySet()) {
			
			Adjustment actual = entry.getValue();
			
			Adjustment expect = AdjustmentResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Adjustment.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "Adjustment count: " + AdjustmentResults.size() + " expected, " + data.getAdjustment().size() + " actual");
		logger.info( "Adjustment: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareTvcResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Tvc> TvcResults = new HashMap<String, Tvc>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.TVC_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Tvc clazz = new Tvc();
			clazz.initOutput(nextLine);
			
			TvcResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Tvc> entry : data.getTVC().entrySet()) {
			
			Tvc actual = entry.getValue();
			
			Tvc expect = TvcResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Tvc.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "Tvc count: " + TvcResults.size() + " expected, " + data.getTVC().size() + " actual");
		logger.info( "Tvc: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareVestingResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Vesting> VestingResults = new HashMap<String, Vesting>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.VESTING_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Vesting clazz = new Vesting();
			clazz.initOutput(nextLine);
			
			VestingResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Vesting> entry : data.getVesting().entrySet()) {
			
			Vesting actual = entry.getValue();
			
			Vesting expect = VestingResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Vesting.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "Vesting count: " + VestingResults.size() + " expected, " + data.getVesting().size() + " actual");
		logger.info( "Vesting: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareFscResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Fsc> FscResults = new HashMap<String, Fsc>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.FSC_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Fsc clazz = new Fsc();
			clazz.initOutput(nextLine);
			
			FscResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Fsc> entry : data.getFsc().entrySet()) {
			
			Fsc actual = entry.getValue();
			
			Fsc expect = FscResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Fsc.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "Fsc count: " + FscResults.size() + " expected, " + data.getFsc().size() + " actual");
		logger.info( "Fsc: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareGlobalResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
	    String [] nextLine;
	    boolean line1 = true;
		Global expect = null;
		
	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.GLOBAL_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	expect = new Global();
	    	expect.initOutput(nextLine);
			//break;
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		Global actual = data.getGlobal();
		
		if (expect != null) {
			String different = expect.equal(actual);
			String missing = null;
			if (rrun == false) {
				missing = expect.PFCheck(actual);
			} else {
				missing = expect.RSCheck(actual);
			}
			if (different == null && missing == null) {
				pass = pass + 1;
			} else {
				if (fail == 0) {
		            logger.info( "header: ," + Global.getOutputHeader() + ", ");
				}
				fail = fail + 1;
	            logger.info( "expect: ," + expect.toOutputString() + ", ");
	            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
			}
		} else {
			fail = fail + 1;
			logger.error( "not found: Global");
		}
		logger.info( "Global: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareCnmeaSettReResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, CnmeaSettRe> NmeaGrpResults = new HashMap<String, CnmeaSettRe>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = null;
	    File f = new File(path + CsvHelper.NMEAGRP_CSV);
	    if (f.exists() && !f.isDirectory()) {
		    reader = new CSVReader(new FileReader(path + CsvHelper.NMEAGRP_CSV));
	    } else {
		    reader = new CSVReader(new FileReader(path + CsvHelper.CNMEASETTRE_CSV));
	    }
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	CnmeaSettRe clazz = new CnmeaSettRe();
			clazz.initOutput(nextLine);
			
			NmeaGrpResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, CnmeaSettRe> entry : data.getCnmeaSettRe().entrySet()) {
			
			CnmeaSettRe actual = entry.getValue();
			
			CnmeaSettRe expect = NmeaGrpResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + CnmeaSettRe.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "CnmeaSettRe count: " + NmeaGrpResults.size() + " expected, " + data.getCnmeaSettRe().size() + " actual");
		logger.info( "CnmeaSettRe: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareCnmeaResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Cnmea> CnmeaResults = new HashMap<String, Cnmea>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.CNMEA_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Cnmea clazz = new Cnmea();
			clazz.initOutput(nextLine);
			
			CnmeaResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Cnmea> entry : data.getCnmea().entrySet()) {
			
			Cnmea actual = entry.getValue();
			
			Cnmea expect = CnmeaResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Cnmea.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "Cnmea count: " + CnmeaResults.size() + " expected, " + data.getCnmea().size() + " actual");
		logger.info( "Cnmea: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareMnmeaResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Mnmea> MnmeaResults = new HashMap<String, Mnmea>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.MNMEA_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Mnmea clazz = new Mnmea();
			clazz.initOutput(nextLine);
			
			MnmeaResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Mnmea> entry : data.getMnmea().entrySet()) {
			
			Mnmea actual = entry.getValue();
			
			Mnmea expect = MnmeaResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Mnmea.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "Mnmea count: " + MnmeaResults.size() + " expected, " + data.getMnmea().size() + " actual");
		logger.info( "Mnmea: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareMnmeaSubResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, MnmeaSub> MnmeaSubResults = new HashMap<String, MnmeaSub>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.MNMEASUB_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	MnmeaSub clazz = new MnmeaSub();
			clazz.initOutput(nextLine);
			
			MnmeaSubResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, MnmeaSub> entry : data.getMnmeaSub().entrySet()) {
			
			MnmeaSub actual = entry.getValue();
			
			MnmeaSub expect = MnmeaSubResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + MnmeaSub.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "MnmeaSub count: " + MnmeaSubResults.size() + " expected, " + data.getMnmeaSub().size() + " actual");
		logger.info( "MnmeaSub: " + pass + " - passed, " + fail + " - failed");
 	}
	
	public static void compareParticipantResults(String path, SettlementData data, boolean hasHeader, boolean rrun) throws Exception {
		
		Map<String, Participant> ParticipantResults = new HashMap<String, Participant>();
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CsvHelper.MP_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Participant clazz = new Participant();
			clazz.initOutput(nextLine);
			
			ParticipantResults.put(clazz.getKey(), clazz);
		}
	    reader.close();
		
		int pass = 0;
		int fail = 0;
		for (Map.Entry<String, Participant> entry : data.getParticipant().entrySet()) {
			
			Participant actual = entry.getValue();
			
			Participant expect = ParticipantResults.get(actual.getKey());
			
			if (expect != null) {
				String different = expect.equal(actual);
				String missing = null;
				if (rrun == false) {
					missing = expect.PFCheck(actual);
				} else {
					missing = expect.RSCheck(actual);
				}
				if (different == null && missing == null) {
					pass = pass + 1;
				} else {
					if (fail == 0) {
			            logger.info( "header: ," + Participant.getOutputHeader() + ", ");
					}
					fail = fail + 1;
		            logger.info( "expect: ," + expect.toOutputString() + ", ");
		            logger.info( "actual: ," + actual.toOutputString() + ", at " + (different!=null? different: "") + (different!=null && missing!=null? " and ": "") + (missing!=null? missing: ""));
				}
			} else {
				fail = fail + 1;
				logger.error( "not found: " + actual.getKey());
			}
		}
		logger.info( "Participant count: " + ParticipantResults.size() + " expected, " + data.getParticipant().size() + " actual");
		logger.info( "Participant: " + pass + " - passed, " + fail + " - failed");
 	}
}
