package com.emc.sett.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.emc.sett.impl.Account;
import com.emc.sett.impl.Adjustment;
import com.emc.sett.impl.Bilateral;
import com.emc.sett.impl.Brq;
import com.emc.sett.impl.Cnmea;
import com.emc.sett.impl.Facility;
import com.emc.sett.impl.Fsc;
import com.emc.sett.impl.Ftr;
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

public class CsvHelper {
	
	public static final String GLOBAL_CSV		= "/global.csv";
	public static final String MARKET_CSV		= "/market.csv";
	public static final String BRQ_CSV			= "/brq.csv";
	public static final String CLASS_CSV		= "/class.csv";
	public static final String RESERVE_CSV		= "/reserve.csv";
	public static final String NODE_CSV			= "/node.csv";
	public static final String ACCOUNT_CSV		= "/account.csv";
	public static final String MP_CSV			= "/mp.csv";
	public static final String INTERVAL_CSV		= "/interval.csv";
	public static final String BILATERAL_CSV	= "/bilateral.csv";
	public static final String FTR_CSV			= "/ftr.csv";
	public static final String ADJUSTMENT_CSV	= "/adjustment.csv";
	public static final String RERUN_CSV		= "/rerun.csv";
	public static final String MNMEASUB_CSV		= "/mnmeasub.csv";
	public static final String MNMEA_CSV		= "/mnmea.csv";
	public static final String NMEAGRP_CSV		= "/nmeagrp.csv";
	public static final String CNMEASETTRE_CSV	= "/cnmeasettre.csv";
	public static final String CNMEA_CSV		= "/cnmea.csv";
	public static final String TVC_CSV			= "/tvc.csv";
	public static final String VESTING_CSV		= "/vesting.csv";
	public static final String FSC_CSV			= "/fsc.csv";

	public static boolean readCsvGlobal(String path, SettlementData data) throws Exception {
		
	    boolean line1 = true;

	    CSVReader reader = new CSVReader(new FileReader(path + GLOBAL_CSV));
	    List<String[]> lines = reader.readAll();
	    boolean hasHeader = (lines.size() == 2);
	    
	    /*if ((nextLine = reader.readNext()) != null) {
	    	data.getGlobal().initInput(nextLine);
		}*/
	    reader.close();
	    
	    for (String [] line : lines) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	data.getGlobal().initInput(line);
	    }
	    return hasHeader;
	}
	
	public static void readCsvMarkets(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + MARKET_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
			Market mkt = new Market();
			mkt.initInput(nextLine);
			
			mkt.setMeuc(data.getGlobal().getMeuc());
			
			data.getMarket().put(mkt.getKey(), mkt);
		}
	    reader.close();
	}
	
	public static void readCsvBRQs(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    CSVReader reader = new CSVReader(new FileReader(path + BRQ_CSV));
	    
	    List<String[]> contracts = reader.readAll();
	    
	    reader.close();
	    
	    if (contracts.size() > 0 && hasHeader == true) {
	    	contracts.remove(0);
	    }
	    
	    Comparator<String[]> comp = new Comparator<String[]>() {
	    	//
	    	// group BRQ contracts by Reserve class then Node then Period
	    	//
	        public int compare(String[] csvLine1, String[] csvLine2) {
	            return (csvLine1[6]+csvLine1[5]+csvLine1[4]).compareTo((csvLine2[6]+csvLine2[5]+csvLine2[4]));
	        }
	    };
	    
	    Collections.sort(contracts, comp);
	    
	    String key = "";
	    List<Brq> buy = null;
	    List<Brq> sell = null;
	    for (String[] str : contracts) {

			Brq brq = new Brq();
			brq.initInput(str);
			
			//
			// prepare the contracts for reserves
			//
			if (key.equals(brq.getKey()) == false) {
				
				if (key.isEmpty() == false) {
					data.getBrqBuyContract().put(key, buy);
					data.getBrqSellerContract().put(key, sell);
				    buy = null;
				    sell = null;
				}
				
				buy = new ArrayList<Brq>();
				sell = new ArrayList<Brq>();
				key = brq.getKey();
				
				if (brq.isPurchaseContract()) {
					buy.add(brq);
				}
				if (brq.isSellingContract()) {
					sell.add(brq);
				}
				if (brq.isEmptyContract()) {
					buy.add(brq);
					sell.add(brq);
				}
			} else {
				if (brq.isPurchaseContract()) {
					buy.add(brq);
				}
				if (brq.isSellingContract()) {
					sell.add(brq);
				}
				if (brq.isEmptyContract()) {
					buy.add(brq);
					sell.add(brq);
				}
			}
			
			data.getBrq().put(brq.getKey(), brq);
	    }
	    
	    if (buy != null) {
	    	data.getBrqBuyContract().put(key, buy);
	    	data.getBrqSellerContract().put(key, sell);
	    }
	}
	
	public static void readCsvClass(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CLASS_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
			RsvClass clazz = new RsvClass();
			clazz.initInput(nextLine);
			
			data.getRsvClass().put(clazz.getKey(), clazz);
		}
	    reader.close();
	}
	
	public static void readCsvReserve(String path, SettlementData data, boolean hasHeader) throws Exception {

	    CSVReader reader = new CSVReader(new FileReader(path + RESERVE_CSV));
	    
	    List<String[]> reserve = reader.readAll();
	    
	    reader.close();
	    
	    if (reserve.size() > 0 && hasHeader == true) {
	    	reserve.remove(0);
	    }

	    Comparator<String[]> comp = new Comparator<String[]>() {
	    	//
	    	// group reserves by Node then Period
	    	//
	        public int compare(String[] csvLine1, String[] csvLine2) {
	            return (csvLine1[10]+csvLine1[11]).compareTo((csvLine2[10]+csvLine2[11]));
	        }
	    };
	    
	    Collections.sort(reserve, comp);
	    
	    String key = "";
	    List<Reserve> rsvs = null;
	    for (String[] str : reserve) {
	    	
			Reserve rsv = new Reserve();
			rsv.initInput(str);
			
			// assign contracts to reserve
			List<Brq> buy = (List<Brq>)data.getBrqBuyContract().get(rsv.getKey());
			if (buy != null) {
				rsv.getPurchasedContracts().addAll(buy);
			}
			
			List<Brq> sell = (List<Brq>)data.getBrqSellerContract().get(rsv.getKey());
			if (sell != null) {
				rsv.getSoldContracts().addAll(sell);
			}
			// end contracts assignment
			
			//
			// prepare the reserves for node
			//
			if (key.equals(rsv.getNodeKey()) == false) {
				
				if (key.isEmpty() == false) {
					data.getNodeReserve().put(key, rsvs);
					rsvs = null;
				}
				
				rsvs = new ArrayList<Reserve>();
				key = rsv.getNodeKey();
				
				rsvs.add(rsv);
			} else {
				rsvs.add(rsv);
			}
			
			data.getReserve().put(rsv.getKey(), rsv);
			
			data.getMarket().get(rsv.getPeriodId()).getReserves().add(rsv);
			
			data.getRsvClass().get(rsv.getRsvClsKey()).getReserves().add(rsv);
	    }
	    
	    if (rsvs != null) {
	    	data.getNodeReserve().put(key, rsvs);
	    }
	}
	
	
	public static void readCsvNodes(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + NODE_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
			Facility node = new Facility();
			node.initInput(nextLine);
			
			// assign reserves to node
			List<Reserve> rsv = (List<Reserve>)data.getNodeReserve().get(node.getKey());
			if (rsv != null) {
				node.getReserves().addAll(rsv);
			}
			// end reserves assignment
			
			node.setWeq(BigDecimal.ZERO);
			node.setFcc(BigDecimal.ZERO);
			
			data.getFacility().put(node.getKey(), node);
		}
	    reader.close();
	}
	
	public static void readCsvAccounts(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + ACCOUNT_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
			Account acct = new Account();
			acct.initInput(nextLine);
			
			data.getAccount().put(acct.getKey(), acct);
		}
	    reader.close();
	}
	
	public static void readCsvParticipants(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + MP_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Participant mp = new Participant();
			mp.initInput(nextLine);
			
			data.getParticipant().put(mp.getKey(), mp);
		}
	    reader.close();
	}
	
	public static void readCsvIntervals(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + INTERVAL_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
			Period pd = new Period();
			pd.initInput(nextLine);
			
			pd.setTotalR(false);
			
			Market mkt = data.getMarket().get(pd.getPeriodId());
			pd.setUsep(mkt.getUsep());
			
			data.getPeriod().put(pd.getKey(), pd);
		}
	    reader.close();
	}
	
	public static void readCsvBilaterals(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + BILATERAL_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Bilateral contract = new Bilateral();
	    	contract.initInput(nextLine);
			
	    	data.getBilateral().put(contract.getKey(), contract);
		}
	    reader.close();
	}
	
	public static void readCsvFtrs(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + FTR_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Ftr contract = new Ftr();
	    	contract.initInput(nextLine);
			
	    	data.getFtr().put(contract.getKey(), contract);
		}
	    reader.close();
	}
	
	public static void readCsvAdjustments(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;
	    
	    String str_id = data.getGlobal().getRunId();

	    CSVReader reader = new CSVReader(new FileReader(path + ADJUSTMENT_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Adjustment adj = new Adjustment();
	    	adj.initInput(nextLine);
			
	    	data.getAdjustment().put(adj.getKey(), adj);
	    	
	    	// TODO: add....
	    	data.getMnmeaSub().get(str_id+adj.getSettId()).getAdjustments().add(adj);
	    	
	    	data.getRerun().get(adj.getName()+adj.getGstRate().toString()+(adj.isTaxable() == true? "1": "0")).getAdjustments().add(adj);
	    	
	    	data.getPeriod().get(adj.getName()+adj.getPeriodId()).getAdjustments().add(adj);
	    	
	    	data.getCnmea().get(adj.getSettId()+adj.getName()).getAdjustments().add(adj);
	    	
	    	data.getCnmeaSettRe().get(adj.getName()+adj.getPeriodId()+adj.getVersion()).getAdjustments().add(adj);
	    	
	    	data.getMnmea().get(adj.getSettId()+adj.getPeriodId()).getAdjustments().add(adj);
		}
	    reader.close();
	}
	
	public static void readCsvReruns(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + RERUN_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Rerun adj = new Rerun();
	    	adj.initInput(nextLine);
			
	    	data.getRerun().put(adj.getKey(), adj);
		}
	    reader.close();
	}
	
	public static void readCsvMnmeaSub(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + MNMEASUB_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	MnmeaSub adj = new MnmeaSub();
	    	adj.initInput(nextLine);
			
	    	data.getMnmeaSub().put(adj.getKey(), adj);
		}
	    reader.close();
	}
	
	public static void readCsvMnmea(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + MNMEA_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Mnmea adj = new Mnmea();
	    	adj.initInput(nextLine);
			
	    	data.getMnmea().put(adj.getKey(), adj);
		}
	    reader.close();
	}
	
	public static void readCsvNmeaGrp(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;
	    CSVReader reader = null;
	    
	    File f = new File(path + NMEAGRP_CSV);
	    if (f.exists() && !f.isDirectory()) {
		    reader = new CSVReader(new FileReader(path + NMEAGRP_CSV));
	    } else {
		    reader = new CSVReader(new FileReader(path + CNMEASETTRE_CSV));
	    }
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	CnmeaSettRe adj = new CnmeaSettRe();
	    	adj.initInput(nextLine);
			
	    	data.getCnmeaSettRe().put(adj.getKey(), adj);
		}
	    reader.close();
	}
	
	public static void readCsvCnmea(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + CNMEA_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Cnmea adj = new Cnmea();
	    	adj.initInput(nextLine);
			
	    	data.getCnmea().put(adj.getKey(), adj);
		}
	    reader.close();
	}
	
	public static void readCsvTvc(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + TVC_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Tvc adj = new Tvc();
	    	adj.initInput(nextLine);
			
	    	data.getTVC().put(adj.getKey(), adj);
		}
	    reader.close();
	}
	
	public static void readCsvVesting(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + VESTING_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Vesting adj = new Vesting();
	    	adj.initInput(nextLine);
			
	    	data.getVesting().put(adj.getKey(), adj);
		}
	    reader.close();
	}
	
	public static void readCsvFsc(String path, SettlementData data, boolean hasHeader) throws Exception {
		
	    boolean line1 = true;
	    String [] nextLine;

	    CSVReader reader = new CSVReader(new FileReader(path + FSC_CSV));
	    
	    while ((nextLine = reader.readNext()) != null) {
	    	if (line1 == true && hasHeader == true) {
	    		line1 = false;
	    		continue;
	    	}
	    	Fsc adj = new Fsc();
	    	adj.initInput(nextLine);
			
	    	data.getFsc().put(adj.getKey(), adj);
		}
	    reader.close();
	}
	
	public static void dumpAllAsInputFiles(String path, SettlementData data, boolean hasHeader) throws Exception {
		
		File dump = null;
		FileWriter writer = null;
		
		try {
			dump = new File(path + GLOBAL_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Global.getInputHeader() + "\n");
			}
			Global gbl = data.getGlobal();
			writer.write(gbl.toInputString() + "\n");
	        writer.close();
	        
			dump = new File(path + MARKET_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Market.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Market> entry : data.getMarket().entrySet()) {
				Market mkt = entry.getValue();
				writer.write(mkt.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + BRQ_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Brq.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Brq> entry : data.getBrq().entrySet()) {
				Brq brq = entry.getValue();
				writer.write(brq.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + CLASS_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(RsvClass.getInputHeader() + "\n");
			}
			for (Map.Entry<String, RsvClass> entry : data.getRsvClass().entrySet()) {
				RsvClass cls = entry.getValue();
				writer.write(cls.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + RESERVE_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Reserve.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Reserve> entry : data.getReserve().entrySet()) {
				Reserve rsv = entry.getValue();
				writer.write(rsv.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + NODE_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Facility.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Facility> entry : data.getFacility().entrySet()) {
				Facility fct = entry.getValue();
				writer.write(fct.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + ACCOUNT_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Account.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Account> entry : data.getAccount().entrySet()) {
				Account sac = entry.getValue();
				writer.write(sac.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + MP_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Participant.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Participant> entry : data.getParticipant().entrySet()) {
				Participant mp = entry.getValue();
				writer.write(mp.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + INTERVAL_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Period.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Period> entry : data.getPeriod().entrySet()) {
				Period pd = entry.getValue();
				writer.write(pd.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + BILATERAL_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Bilateral.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Bilateral> entry : data.getBilateral().entrySet()) {
				Bilateral bc = entry.getValue();
				writer.write(bc.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + FTR_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Ftr.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Ftr> entry : data.getFtr().entrySet()) {
				Ftr ftr = entry.getValue();
				writer.write(ftr.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + ADJUSTMENT_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Adjustment.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Adjustment> entry : data.getAdjustment().entrySet()) {
				Adjustment adj = entry.getValue();
				writer.write(adj.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + RERUN_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Rerun.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Rerun> entry : data.getRerun().entrySet()) {
				Rerun rr = entry.getValue();
				writer.write(rr.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + MNMEASUB_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(MnmeaSub.getInputHeader() + "\n");
			}
			for (Map.Entry<String, MnmeaSub> entry : data.getMnmeaSub().entrySet()) {
				MnmeaSub rr = entry.getValue();
				writer.write(rr.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + MNMEA_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Mnmea.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Mnmea> entry : data.getMnmea().entrySet()) {
				Mnmea rr = entry.getValue();
				writer.write(rr.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + CNMEASETTRE_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(CnmeaSettRe.getInputHeader() + "\n");
			}
			for (Map.Entry<String, CnmeaSettRe> entry : data.getCnmeaSettRe().entrySet()) {
				CnmeaSettRe rr = entry.getValue();
				writer.write(rr.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + CNMEA_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Cnmea.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Cnmea> entry : data.getCnmea().entrySet()) {
				Cnmea rr = entry.getValue();
				writer.write(rr.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + TVC_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Tvc.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Tvc> entry : data.getTVC().entrySet()) {
				Tvc rr = entry.getValue();
				writer.write(rr.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + VESTING_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Vesting.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Vesting> entry : data.getVesting().entrySet()) {
				Vesting rr = entry.getValue();
				writer.write(rr.toInputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + FSC_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Fsc.getInputHeader() + "\n");
			}
			for (Map.Entry<String, Fsc> entry : data.getFsc().entrySet()) {
				Fsc rr = entry.getValue();
				writer.write(rr.toInputString() + "\n");
			}
	        writer.close();
	        writer = null;
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	public static void dumpAllAsOutputFiles(String path, SettlementData data, boolean hasHeader) throws Exception {
		
		File dump = null;
		FileWriter writer = null;
		
		try {
			dump = new File(path + GLOBAL_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Global.getOutputHeader() + "\n");
			}
			Global gbl = data.getGlobal();
			writer.write(gbl.toOutputString() + "\n");
	        writer.close();
	        
			dump = new File(path + MARKET_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Market.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Market> entry : data.getMarket().entrySet()) {
				Market mkt = entry.getValue();
				writer.write(mkt.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + BRQ_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Brq.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Brq> entry : data.getBrq().entrySet()) {
				Brq brq = entry.getValue();
				writer.write(brq.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + CLASS_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(RsvClass.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, RsvClass> entry : data.getRsvClass().entrySet()) {
				RsvClass cls = entry.getValue();
				writer.write(cls.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + RESERVE_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Reserve.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Reserve> entry : data.getReserve().entrySet()) {
				Reserve rsv = entry.getValue();
				writer.write(rsv.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + NODE_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Facility.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Facility> entry : data.getFacility().entrySet()) {
				Facility fct = entry.getValue();
				writer.write(fct.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + ACCOUNT_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Account.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Account> entry : data.getAccount().entrySet()) {
				Account sac = entry.getValue();
				writer.write(sac.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + MP_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Participant.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Participant> entry : data.getParticipant().entrySet()) {
				Participant mp = entry.getValue();
				writer.write(mp.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + INTERVAL_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Period.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Period> entry : data.getPeriod().entrySet()) {
				Period pd = entry.getValue();
				writer.write(pd.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + BILATERAL_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Bilateral.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Bilateral> entry : data.getBilateral().entrySet()) {
				Bilateral bc = entry.getValue();
				writer.write(bc.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + FTR_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Ftr.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Ftr> entry : data.getFtr().entrySet()) {
				Ftr ftr = entry.getValue();
				writer.write(ftr.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + ADJUSTMENT_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Adjustment.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Adjustment> entry : data.getAdjustment().entrySet()) {
				Adjustment adj = entry.getValue();
				writer.write(adj.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + RERUN_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Rerun.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Rerun> entry : data.getRerun().entrySet()) {
				Rerun rr = entry.getValue();
				writer.write(rr.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + MNMEASUB_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(MnmeaSub.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, MnmeaSub> entry : data.getMnmeaSub().entrySet()) {
				MnmeaSub rr = entry.getValue();
				writer.write(rr.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + MNMEA_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Mnmea.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Mnmea> entry : data.getMnmea().entrySet()) {
				Mnmea rr = entry.getValue();
				writer.write(rr.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + CNMEASETTRE_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(CnmeaSettRe.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, CnmeaSettRe> entry : data.getCnmeaSettRe().entrySet()) {
				CnmeaSettRe rr = entry.getValue();
				writer.write(rr.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + CNMEA_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Cnmea.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Cnmea> entry : data.getCnmea().entrySet()) {
				Cnmea rr = entry.getValue();
				writer.write(rr.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + TVC_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Tvc.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Tvc> entry : data.getTVC().entrySet()) {
				Tvc rr = entry.getValue();
				writer.write(rr.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + VESTING_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Vesting.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Vesting> entry : data.getVesting().entrySet()) {
				Vesting rr = entry.getValue();
				writer.write(rr.toOutputString() + "\n");
			}
	        writer.close();
	        
			dump = new File(path + FSC_CSV);
			writer = new FileWriter(dump);
			if (hasHeader) {
				writer.write(Fsc.getOutputHeader() + "\n");
			}
			for (Map.Entry<String, Fsc> entry : data.getFsc().entrySet()) {
				Fsc rr = entry.getValue();
				writer.write(rr.toOutputString() + "\n");
			}
	        writer.close();
	        writer = null;
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
