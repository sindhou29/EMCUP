package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.MarketT;
import com.emc.sett.model.ReserveT;

public class Market extends MarketT {
	
    //private static final Logger logger = LoggerFactory.getLogger(Market.class);

	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String market_afp = line[0];
		String market_ega_weq = line[1];
		String market_heua = line[2];
		String market_heuc = line[3];
		String market_prq = line[4];
		String market_rsa = line[5];
		String market_rsc = line[6];
		String market_srq = line[7];
		String market_trq = line[8];
		String market_tte = line[9];
		String market_weq = line[10];
		int interval = Integer.parseInt(line[11]);
		String market_number = String.format("%1$02d", interval);
		String market_pcu_nodes = line[12];
		String market_str_id = line[13];
		String market_meuc = line[14];
		String market_usep = line[15];
		String market_vcrp = line[16];
		String market_feq = line[17];
		String market_fsc = line[18];
		String market_vcsc = line[19];
		String market_hq = line[20];
		String csz = line[21];
		String market_wmq = line[22];
		String market_wmep = line[23];
		
		this.periodId = market_number;
		this.runId = market_str_id;
		this.usep = market_usep.length() > 0? new BigDecimal(market_usep): null;
		this.wmep = market_wmep.length() > 0? new BigDecimal(market_wmep): null;
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return periodId;
	}
    
    public BigDecimal sumAllReservesRsc() {
    	
		//long start = System.currentTimeMillis();
		BigDecimal result = BigDecimal.ZERO;
		
    	for (ReserveT rsv : this.reserves) {
    		if (rsv.getRsc() != null) {
	    		result = result.add(rsv.getRsc());
    		}
    	}
        //long mark = System.currentTimeMillis();
        //logger.info("Time spent sumAllReservesRsc(): " + (mark - start) + " ms");
    	
        return result;
    }

	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) {
		
		String market_afp = line[0];
		String market_ega_weq = line[1];
		String market_round_heua = line[2];
		String market_heuc = line[3];
		String market_prq = line[4];
		String market_rsa = line[5];
		String market_rsc = line[6];
		String market_srq = line[7];
		String market_trq = line[8];
		String market_tte = line[9];
		String market_weq = line[10];
		int interval = Integer.parseInt(line[11]);
		String market_number = String.format("%1$02d", interval);
		String market_pcu_nodes = line[12];
		String market_str_id = line[13];
		String market_meuc = line[14];
		String market_usep = line[15];
		String market_vcrp = line[16];
		String market_feq = line[17];
		String market_fsc = line[18];
		String market_vcsc = line[19];
		String market_hq = line[20];
		String market_acctg_neaa = line[21];
		String market_wcq = line[22];
		String market_round_heuc = line[23];
		String market_wsp = line[24];
		String market_wmq = line[25];
		String market_fsrp = line[26];
		String market_fssc = line[27];
		String market_fsq = line[28];
		String market_emcadmn = line[29];
		String market_psoadmn = line[30];
		String market_wmep = line[31];
		String market_round_heur = line[32];
		String market_round_hlcu = line[33];
		String market_lcsc = line[34];
		String market_wdq = line[35];
		String market_hlcu = line[36];
		
		this.afp = market_afp.length() > 0? new BigDecimal(market_afp): null;
		this.egaWeq = market_ega_weq.length() > 0? new BigDecimal(market_ega_weq): null;
		this.roundedHeua = market_round_heua.length() > 0? new BigDecimal(market_round_heua): null;
		this.heuc = market_heuc.length() > 0? new BigDecimal(market_heuc): null;
		this.prq = market_prq.length() > 0? new BigDecimal(market_prq): null;
		this.rsa = market_rsa.length() > 0? new BigDecimal(market_rsa): null;
		this.rsc = market_rsc.length() > 0? new BigDecimal(market_rsc): null;
		this.srq = market_srq.length() > 0? new BigDecimal(market_srq): null;
		this.trq = market_trq.length() > 0? new BigDecimal(market_trq): null;
		this.tte = market_tte.length() > 0? new BigDecimal(market_tte): null;
		this.weq = market_weq.length() > 0? new BigDecimal(market_weq): null;
		this.periodId = market_number;
		this.pcuCount = market_pcu_nodes.length() > 0? new BigDecimal(market_pcu_nodes): null;
		this.runId = market_str_id;
		this.meuc = market_meuc.length() > 0? new BigDecimal(market_meuc): null;
		this.usep = market_usep.length() > 0? new BigDecimal(market_usep): null;
		this.vcrp = market_vcrp.length() > 0? new BigDecimal(market_vcrp): null;
		this.feq = market_feq.length() > 0? new BigDecimal(market_feq): null;
		this.fsc = market_fsc.length() > 0? new BigDecimal(market_fsc): null;
		this.vcsc = market_vcsc.length() > 0? new BigDecimal(market_vcsc): null;
		this.hq = market_hq.length() > 0? new BigDecimal(market_hq): null;
		this.accountingNeaa = market_acctg_neaa.length() > 0? new BigDecimal(market_acctg_neaa): null;
		this.wcq = market_wcq.length() > 0? new BigDecimal(market_wcq): null;
		this.roundedHeuc = market_round_heuc.length() > 0? new BigDecimal(market_round_heuc): null;
		this.wsp = market_wsp.length() > 0? new BigDecimal(market_wsp): null;
		this.wmq = market_wmq.length() > 0? new BigDecimal(market_wmq): null;
		
		this.fsrp = market_fsrp.length() > 0? new BigDecimal(market_fsrp): null;
		this.fssc = market_fssc.length() > 0? new BigDecimal(market_fssc): null;
		this.fsq = market_fsq.length() > 0? new BigDecimal(market_fsq): null;
		this.emcAdm = market_emcadmn.length() > 0? new BigDecimal(market_emcadmn): null;
		this.psoAdm = market_psoadmn.length() > 0? new BigDecimal(market_psoadmn): null;
		this.wmepOutput = market_wmep.length() > 0? new BigDecimal(market_wmep): null;
		this.roundedHeur = market_round_heur.length() > 0? new BigDecimal(market_round_heur): null;
		this.roundedHlcu = market_round_hlcu.length() > 0? new BigDecimal(market_round_hlcu): null;
		this.lcsc = market_lcsc.length() > 0? new BigDecimal(market_lcsc): null;
		this.wdq = market_wdq.length() > 0? new BigDecimal(market_wdq): null;
		this.hlcu = market_hlcu.length() > 0? new BigDecimal(market_hlcu): null;
	}
	
	public String PFCheck(Market in) {

		String result = null;

		if ((this.afp == null && in.afp != null) || (this.afp != null && in.afp == null)) result = "afp missing value";
		if ((this.egaWeq == null && in.egaWeq != null) || (this.egaWeq != null && in.egaWeq == null)) result = "egaWeq missing value";
		if ((this.roundedHeua == null && in.roundedHeua != null) || (this.roundedHeua != null && in.roundedHeua == null)) result = "roundedHeua missing value";
		if ((this.heuc == null && in.heuc != null) || (this.heuc != null && in.heuc == null)) result = "heuc missing value";
		if ((this.prq == null && in.prq != null) || (this.prq != null && in.prq == null)) result = "prq missing value";
		if ((this.rsa == null && in.rsa != null) || (this.rsa != null && in.rsa == null)) result = "rsa missing value";
		if ((this.rsc == null && in.rsc != null) || (this.rsc != null && in.rsc == null)) result = "rsc missing value";
		if ((this.srq == null && in.srq != null) || (this.srq != null && in.srq == null)) result = "srq missing value";
		if ((this.trq == null && in.trq != null) || (this.trq != null && in.trq == null)) result = "trq missing value";
		if ((this.tte == null && in.tte != null) || (this.tte != null && in.tte == null)) result = "tte missing value";
		if ((this.weq == null && in.weq != null) || (this.weq != null && in.weq == null)) result = "weq missing value";
		if ((this.periodId == null && in.periodId != null) || (this.periodId != null && in.periodId == null)) result = "periodId missing value";
		if ((this.pcuCount == null && in.pcuCount != null) || (this.pcuCount != null && in.pcuCount == null)) result = "pcuCount missing value";
		if ((this.meuc == null && in.meuc != null) || (this.meuc != null && in.meuc == null)) result = "meuc missing value";
		if ((this.usep == null && in.usep != null) || (this.usep != null && in.usep == null)) result = "usep missing value";
		if ((this.vcrp == null && in.vcrp != null) || (this.vcrp != null && in.vcrp == null)) result = "vcrp missing value";
		if ((this.feq == null && in.feq != null) || (this.feq != null && in.feq == null)) result = "feq missing value";
		if ((this.fsc == null && in.fsc != null) || (this.fsc != null && in.fsc == null)) result = "fsc missing value";
		if ((this.vcsc == null && in.vcsc != null) || (this.vcsc != null && in.vcsc == null)) result = "vcsc missing value";
		if ((this.hq == null && in.hq != null) || (this.hq != null && in.hq == null)) result = "hq missing value";
		if ((this.accountingNeaa == null && in.accountingNeaa != null) || (this.accountingNeaa != null && in.accountingNeaa == null)) result = "accountingNeaa missing value";
		if ((this.wcq == null && in.wcq != null) || (this.wcq != null && in.wcq == null)) result = "wcq missing value";
		if ((this.roundedHeuc == null && in.roundedHeuc != null) || (this.roundedHeuc != null && in.roundedHeuc == null)) result = "roundedHeuc missing value";
		if ((this.wsp == null && in.wsp != null) || (this.wsp != null && in.wsp == null)) result = "wsp missing value";
		if ((this.wmq == null && in.wmq != null) || (this.wmq != null && in.wmq == null)) result = "wmq missing value";
		if ((this.fsrp == null && in.fsrp != null) || (this.fsrp != null && in.fsrp == null)) result = "fsrp missing value";
		if ((this.fssc == null && in.fssc != null) || (this.fssc != null && in.fssc == null)) result = "fssc missing value";
		if ((this.fsq == null && in.fsq != null) || (this.fsq != null && in.fsq == null)) result = "fsq missing value";
		if ((this.emcAdm == null && in.emcAdm != null) || (this.emcAdm != null && in.emcAdm == null)) result = "emcAdm missing value";
		if ((this.psoAdm == null && in.psoAdm != null) || (this.psoAdm != null && in.psoAdm == null)) result = "psoAdm missing value";
		if ((this.wmepOutput == null && in.wmepOutput != null) || (this.wmepOutput != null && in.wmepOutput == null)) result = "wmepOutput missing value";
		if ((this.roundedHeur == null && in.roundedHeur != null) || (this.roundedHeur != null && in.roundedHeur == null)) result = "roundedHeur missing value";
		if ((this.roundedHlcu == null && in.roundedHlcu != null) || (this.roundedHlcu != null && in.roundedHlcu == null)) result = "roundedHlcu missing value";
		if ((this.lcsc == null && in.lcsc != null) || (this.lcsc != null && in.lcsc == null)) result = "lcsc missing value";
		if ((this.wdq == null && in.wdq != null) || (this.wdq != null && in.wdq == null)) result = "wdq missing value";
		if ((this.hlcu == null && in.hlcu != null) || (this.hlcu != null && in.hlcu == null)) result = "hlcu missing value";

		return result;
	}

	public String RSCheck(Market in) {

		String result = null;

		if ((this.prq == null && in.prq != null) || (this.prq != null && in.prq == null)) result = "prq missing value";
		if ((this.rsc == null && in.rsc != null) || (this.rsc != null && in.rsc == null)) result = "rsc missing value";
		if ((this.srq == null && in.srq != null) || (this.srq != null && in.srq == null)) result = "srq missing value";
		if ((this.trq == null && in.trq != null) || (this.trq != null && in.trq == null)) result = "trq missing value";
		if ((this.periodId == null && in.periodId != null) || (this.periodId != null && in.periodId == null)) result = "periodId missing value";
		if ((this.meuc == null && in.meuc != null) || (this.meuc != null && in.meuc == null)) result = "meuc missing value";
		if ((this.usep == null && in.usep != null) || (this.usep != null && in.usep == null)) result = "usep missing value";
		//if ((this.fsc == null && in.fsc != null) || (this.fsc != null && in.fsc == null)) result = "fsc missing value";
		if ((this.fsrp == null && in.fsrp != null) || (this.fsrp != null && in.fsrp == null)) result = "fsrp missing value";
		if ((this.fssc == null && in.fssc != null) || (this.fssc != null && in.fssc == null)) result = "fssc missing value";
		if ((this.fsq == null && in.fsq != null) || (this.fsq != null && in.fsq == null)) result = "fsq missing value";
		//if ((this.wmepOutput == null && in.wmepOutput != null) || (this.wmepOutput != null && in.wmepOutput == null)) result = "wmepOutput missing value";

		return result;
	}

	public String equal(Market in) {

		String result = null;

		if (this.afp != null && in.afp != null) if (this.afp.compareTo(in.afp) != 0) result = "afp value mismatch";
		if (this.egaWeq != null && in.egaWeq != null) if (this.egaWeq.compareTo(in.egaWeq) != 0) result = "egaWeq value mismatch";
		if (this.roundedHeua != null && in.roundedHeua != null) if (this.roundedHeua.compareTo(in.roundedHeua) != 0) result = "roundedHeua value mismatch";
		if (this.heuc != null && in.heuc != null) if (this.heuc.setScale(8, BigDecimal.ROUND_HALF_UP).compareTo(in.heuc.setScale(8, BigDecimal.ROUND_HALF_UP)) != 0) result = "heuc value mismatch";
		if (this.prq != null && in.prq != null) if (this.prq.compareTo(in.prq) != 0) result = "prq value mismatch";
		if (this.rsa != null && in.rsa != null) if (this.rsa.compareTo(in.rsa) != 0) result = "rsa value mismatch";
		if (this.rsc != null && in.rsc != null) if (this.rsc.compareTo(in.rsc) != 0) result = "rsc value mismatch";
		if (this.srq != null && in.srq != null) if (this.srq.compareTo(in.srq) != 0) result = "srq value mismatch";
		if (this.trq != null && in.trq != null) if (this.trq.compareTo(in.trq) != 0) result = "trq value mismatch";
		if (this.tte != null && in.tte != null) if (this.tte.compareTo(in.tte) != 0) result = "tte value mismatch";
		if (this.weq != null && in.weq != null) if (this.weq.compareTo(in.weq) != 0) result = "weq value mismatch";
		if (this.periodId != null && in.periodId != null) if (this.periodId.compareTo(in.periodId) != 0) result = "periodId value mismatch";
		if (this.pcuCount != null && in.pcuCount != null) if (this.pcuCount.compareTo(in.pcuCount) != 0) result = "pcuCount value mismatch";
		if (this.meuc != null && in.meuc != null) if (this.meuc.compareTo(in.meuc) != 0) result = "meuc value mismatch";
		if (this.usep != null && in.usep != null) if (this.usep.compareTo(in.usep) != 0) result = "usep value mismatch";
		if (this.vcrp != null && in.vcrp != null) if (this.vcrp.compareTo(in.vcrp) != 0) result = "vcrp value mismatch";
		if (this.feq != null && in.feq != null) if (this.feq.compareTo(in.feq) != 0) result = "feq value mismatch";
		if (this.fsc != null && in.fsc != null) if (this.fsc.compareTo(in.fsc) != 0) result = "fsc value mismatch";
		if (this.vcsc != null && in.vcsc != null) if (this.vcsc.compareTo(in.vcsc) != 0) result = "vcsc value mismatch";
		if (this.hq != null && in.hq != null) if (this.hq.compareTo(in.hq) != 0) result = "hq value mismatch";
		if (this.accountingNeaa != null && in.accountingNeaa != null) if (this.accountingNeaa.compareTo(in.accountingNeaa) != 0) result = "accountingNeaa value mismatch";
		if (this.wcq != null && in.wcq != null) if (this.wcq.compareTo(in.wcq) != 0) result = "wcq value mismatch";
		if (this.roundedHeuc != null && in.roundedHeuc != null) if (this.roundedHeuc.compareTo(in.roundedHeuc) != 0) result = "roundedHeuc value mismatch";
		if (this.wsp != null && in.wsp != null) if (this.wsp.compareTo(in.wsp) != 0) result = "wsp value mismatch";
		if (this.wmq != null && in.wmq != null) if (this.wmq.compareTo(in.wmq) != 0) result = "wmq value mismatch";
		if (this.fsrp != null && in.fsrp != null) if (this.fsrp.compareTo(in.fsrp) != 0) result = "fsrp value mismatch";
		if (this.fssc != null && in.fssc != null) if (this.fssc.compareTo(in.fssc) != 0) result = "fssc value mismatch";
		if (this.fsq != null && in.fsq != null) if (this.fsq.compareTo(in.fsq) != 0) result = "fsq value mismatch";
		if (this.emcAdm != null && in.emcAdm != null) if (this.emcAdm.setScale(8, BigDecimal.ROUND_HALF_UP).compareTo(in.emcAdm.setScale(8, BigDecimal.ROUND_HALF_UP)) != 0) result = "emcAdm value mismatch";
		if (this.psoAdm != null && in.psoAdm != null) if (this.psoAdm.setScale(8, BigDecimal.ROUND_HALF_UP).compareTo(in.psoAdm.setScale(8, BigDecimal.ROUND_HALF_UP)) != 0) result = "psoAdm value mismatch";
		if (this.wmepOutput != null && in.wmepOutput != null) if (this.wmepOutput.compareTo(in.wmepOutput) != 0) result = "wmepOutput value mismatch";
		if (this.roundedHeur != null && in.roundedHeur != null) if (this.roundedHeur.compareTo(in.roundedHeur) != 0) result = "roundedHeur value mismatch";
		if (this.roundedHlcu != null && in.roundedHlcu != null) if (this.roundedHlcu.compareTo(in.roundedHlcu) != 0) result = "roundedHlcu value mismatch";
		if (this.lcsc != null && in.lcsc != null) if (this.lcsc.compareTo(in.lcsc) != 0) result = "lcsc value mismatch";
		if (this.wdq != null && in.wdq != null) if (this.wdq.compareTo(in.wdq) != 0) result = "wdq value mismatch";
		if (this.hlcu != null && in.hlcu != null) if (this.hlcu.compareTo(in.hlcu) != 0) result = "hlcu value mismatch";

		return result;
	}

	public String toInputString() {
		
		String result = (this.afp != null? this.afp.toString(): "") + "," +
				(this.egaWeq != null? this.egaWeq.toString(): "") + "," +
				(this.heua != null? this.heua.toString(): "") + "," +
				(this.heuc != null? this.heuc.toString(): "") + "," +
				(this.prq != null? this.prq.toString(): "") + "," +
				(this.rsa != null? this.rsa.toString(): "") + "," +
				(this.rsc != null? this.rsc.toString(): "") + "," +
				(this.srq != null? this.srq.toString(): "") + "," +
				(this.trq != null? this.trq.toString(): "") + "," +
				(this.tte != null? this.tte.toString(): "") + "," +
				(this.weq != null? this.weq.toString(): "") + "," +
				this.periodId + "," +
				(this.pcuCount != null? this.pcuCount.toString(): "") + "," +
				this.runId + "," +
				(this.meuc != null? this.meuc.toString(): "") + "," +
				(this.usep != null? this.usep.toString(): "") + "," +
				(this.vcrp != null? this.vcrp.toString(): "") + "," +
				(this.feq != null? this.feq.toString(): "") + "," +
				(this.fsc != null? this.fsc.toString(): "") + "," +
				(this.vcsc != null? this.vcsc.toString(): "") + "," +
				(this.hq != null? this.hq.toString(): "") + "," +
				/*(this.csz != null? this.csz.toString(): "") +*/ "," +
				(this.wmq != null? this.wmq.toString(): "") + "," +
				(this.wmep != null? this.wmep.toString(): "");
		
		return result;
	}

	public String toOutputString() {
		
		String result = (this.afp != null? this.afp.toString(): "") + "," +
				(this.egaWeq != null? this.egaWeq.toString(): "") + "," +
				(this.roundedHeua != null? this.roundedHeua.toString(): "") + "," +
				(this.heuc != null? this.heuc.toString(): "") + "," +
				(this.prq != null? this.prq.toString(): "") + "," +
				(this.rsa != null? this.rsa.toString(): "") + "," +
				(this.rsc != null? this.rsc.toString(): "") + "," +
				(this.srq != null? this.srq.toString(): "") + "," +
				(this.trq != null? this.trq.toString(): "") + "," +
				(this.tte != null? this.tte.toString(): "") + "," +
				(this.weq != null? this.weq.toString(): "") + "," +
				this.periodId + "," +
				(this.pcuCount != null? this.pcuCount.toString(): "") + "," +
				this.runId + "," +
				(this.meuc != null? this.meuc.toString(): "") + "," +
				(this.usep != null? this.usep.toString(): "") + "," +
				(this.vcrp != null? this.vcrp.toString(): "") + "," +
				(this.feq != null? this.feq.toString(): "") + "," +
				(this.fsc != null? this.fsc.toString(): "") + "," +
				(this.vcsc != null? this.vcsc.toString(): "") + "," +
				(this.hq != null? this.hq.toString(): "") + "," +
				(this.accountingNeaa != null? this.accountingNeaa.toString(): "") + "," +
				(this.wcq != null? this.wcq.toString(): "") + "," +
				(this.roundedHeuc != null? this.roundedHeuc.toString(): "") + "," +
				(this.wsp != null? this.wsp.toString(): "") + "," +
				(this.wmq != null? this.wmq.toString(): "") + "," +
				(this.fsrp != null? this.fsrp.toString(): "") + "," +
				(this.fssc != null? this.fssc.toString(): "") + "," +
				(this.fsq != null? this.fsq.toString(): "") + "," +
				(this.emcAdm != null? this.emcAdm.toString(): "") + "," +
				(this.psoAdm != null? this.psoAdm.toString(): "") + "," +
				(this.wmepOutput != null? this.wmepOutput.toString(): "") + "," +
				(this.roundedHeur != null? this.roundedHeur.toString(): "") + "," +
				(this.roundedHlcu != null? this.roundedHlcu.toString(): "") + "," +
				(this.lcsc != null? this.lcsc.toString(): "") + "," +
				(this.wdq != null? this.wdq.toString(): "") + "," +
				(this.hlcu != null? this.hlcu.toString(): "");
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"afp," +
			"egaWeq," +
			"heua," +
			"heuc," +
			"prq," +
			"rsa," +
			"rsc," +
			"srq," +
			"trq," +
			"tte," +
			"weq," +
			"periodId," +
			"pcuCount," +
			"runId," +
			"meuc," +
			"usep," +
			"vcrp," +
			"feq," +
			"fsc," +
			"vcsc," +
			"hq," +
			"csz," +
			"wmq," +
			"wmep";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"afp," +
			"egaWeq," +
			"roundedHeua," +
			"heuc," +
			"prq," +
			"rsa," +
			"rsc," +
			"srq," +
			"trq," +
			"tte," +
			"weq," +
			"periodId," +
			"pcuCount," +
			"runId," +
			"meuc," +
			"usep," +
			"vcrp," +
			"feq," +
			"fsc," +
			"vcsc," +
			"hq," +
			"accountingNeaa," +
			"wcq," +
			"roundedHeuc," +
			"wsp," +
			"wmq," +
			"fsrp," +
			"fssc," +
			"fsq," +
			"emcAdm," +
			"psoAdm," +
			"wmepOutput," +
			"roundedHeur," +
			"roundedHlcu," +
			"lcsc," +
			"wdq," +
			"hlcu";

		return header;
	}
}
