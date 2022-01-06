package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.ReserveT;
import com.emc.sett.model.FacilityT;

public class Facility extends FacilityT {
	
	public Facility() {
		
	}

	public Facility(Facility fct) {
		
		this.hasSzIdx = fct.hasSzIdx;
		this.positiveInj = fct.positiveInj;
		this.cntbSrq = fct.cntbSrq;
		this.pcu = fct.pcu;
		this.scu = fct.scu;
		this.accountId = fct.accountId;
		this.deltaIeq = fct.deltaIeq;
		this.feqAdj = fct.feqAdj;
		this.fsc = fct.fsc;
		this.gesce = fct.gesce;
		this.gescn = fct.gescn;
		this.gescp = fct.gescp;
		this.gfq = fct.gfq;
		this.gmee = fct.gmee;
		this.accountingGmef = fct.accountingGmef;
		this.grq = fct.grq;
		this.ieq = fct.ieq;
		this.periodId = fct.periodId;
		this.ipf = fct.ipf;
		this.ipw = fct.ipw;
		this.accountingLesdn = fct.accountingLesdn;
		this.accountingLesdp = fct.accountingLesdp;
		this.lrq = fct.lrq;
		this.mep = fct.mep;
		this.facilityId = fct.facilityId;
		this.neaa = fct.neaa;
		this.accountingNegc = fct.accountingNegc;
		this.negcSzIdx = fct.negcSzIdx;
		this.accountingNelc = fct.accountingNelc;
		this.nrsc = fct.nrsc;
		this.accountingNtsc = fct.accountingNtsc;
		this.sizeIndex = fct.sizeIndex;
		this.accountingRcc = fct.accountingRcc;
		this.rrs = fct.rrs;
		this.rrsFactor = fct.rrsFactor;
		this.accountingRsc = fct.accountingRsc;
		this.accountingRsd = fct.accountingRsd;
		this.rtq = fct.rtq;
		this.rts = fct.rts;
		this.shd = fct.shd;
		this.spf = fct.spf;
		this.tFactor = fct.tFactor;
		this.vcrp = fct.vcrp;
		this.weq = fct.weq;
		this.facilityRsc = fct.facilityRsc;
		this.accountingFsd = fct.accountingFsd;
		this.fcc = fct.fcc;
		this.nfsc = fct.nfsc;
		this.gmea = fct.gmea;
		this.nodeType = fct.nodeType;
		this.noCsz = fct.noCsz;
		this.lcq = fct.lcq;
		this.lcscp = fct.lcscp;
		this.oiec = fct.oiec;
		this.siec = fct.siec;
		this.wlq = fct.wlq;
		this.complFlag = fct.complFlag;
	}

	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String node_has_sz_idx = line[0];
		String node_positive_inj = line[1];
		String node_cntb_srq = line[2];
		String node_pcu = line[3];
		String node_scu = line[4];
		String node_account = line[5];
		String node_delta_ieq = line[6];
		String node_feq_adj = line[7];
		String node_fsc = line[8];
		String node_gesce = line[9];
		String node_gescn = line[10];
		String node_gescp = line[11];
		String node_gfq = line[12];
		String node_gmee = line[13];
		String node_gmef = line[14];
		String node_grq = line[15];
		String node_ieq = line[16];
		int interval = Integer.parseInt(line[17]);
		String node_interval = String.format("%1$02d", interval);
		String node_ipf = line[18];
		String node_ipw = line[19];
		String node_lesdn = line[20];
		String node_lesdp = line[21];
		String node_lrq = line[22];
		String node_mep = line[23];
		String node_name = line[24];
		String node_neaa = line[25];
		String node_negc = line[26];
		String node_negc_sz_idx = line[27];
		String node_nelc = line[28];
		String node_nrsc = line[29];
		String node_ntsc = line[30];
		String node_size_index = line[31];
		String node_rcc = line[32];
		String node_rrs = line[33];
		String node_rrs_factor = line[34];
		String node_rsc = line[35];
		String node_rsd = line[36];
		String node_rtq = line[37];
		String node_rts = line[38];
		String node_shd = line[39];
		String node_spf = line[40];
		String node_t_factor = line[41];
		String node_vcrp = line[42];
		String node_weq = line[43];
		String node_facility_rsc = line[44];
		String node_acctg_rcc = line[45];
		String node_acctg_rsc = line[46];
		String node_acctg_rsd = line[47];
		String node_type = line[48];
		String no_CSZ_Applicable = line[49];
		String node_lcq = line[50];
		String node_oiec = line[51];
		String node_siec = line[52];
		String node_wlq = line[53];
		String node_complFlag = line[54];
		
		this.scu = node_scu.equals("1");
		this.accountId = node_account;
		this.deltaIeq = node_delta_ieq.length() > 0? new BigDecimal(node_delta_ieq): null;
		this.gfq = node_gfq.length() > 0? new BigDecimal(node_gfq): null;
		this.grq = node_grq.length() > 0? new BigDecimal(node_grq): null;
		this.ieq = node_ieq.length() > 0? new BigDecimal(node_ieq): null;
		this.periodId = node_interval;
		this.lrq = node_lrq.length() > 0? new BigDecimal(node_lrq): null;
		this.mep = node_mep.length() > 0? new BigDecimal(node_mep): null;
		this.facilityId = node_name;
		this.shd = node_shd.length() > 0? new BigDecimal(node_shd): null;
		this.spf = node_spf.length() > 0? new BigDecimal(node_spf): null;
		this.nodeType = node_type;
		this.noCsz = no_CSZ_Applicable.equals("1");
		this.lcq = node_lcq.length() > 0? new BigDecimal(node_lcq): null;
		this.oiec = node_oiec.length() > 0? new BigDecimal(node_oiec): null;
		this.siec = node_siec.length() > 0? new BigDecimal(node_siec): null;
		this.wlq = node_wlq.length() > 0? new BigDecimal(node_wlq): null;
		this.complFlag = node_complFlag;
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return facilityId + periodId;
	}
    
    public BigDecimal sumRscOverAllMnn() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.reserves != null) {
	    	for (ReserveT rsv : this.reserves) {
	    		if (((Reserve)rsv).getRsc() != null) {
		    		result = result.add(((Reserve)rsv).getRsc());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumFacilityRscOverAllMnn() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.reserves != null) {
	    	for (ReserveT rsv : this.reserves) {
	    		if (((Reserve)rsv).getFacilityRsc() != null) {
		    		result = result.add(((Reserve)rsv).getFacilityRsc());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumRsdOverAllMnn() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.reserves != null) {
	    	for (ReserveT rsv : this.reserves) {
	    		if (((Reserve)rsv).getRsd() != null) {
		    		result = result.add(((Reserve)rsv).getRsd());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumRccOverAllMnn() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.reserves != null) {
	    	for (ReserveT rsv : this.reserves) {
	    		if (((Reserve)rsv).getRcc() != null) {
		    		result = result.add(((Reserve)rsv).getRcc());
	    		}
	    	}
		}
    	
        return result;
    }
    
	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) {
		
		String node_has_sz_idx = line[0];
		String node_positive_inj = line[1];
		String node_cntb_srq = line[2];
		String node_pcu = line[3];
		String node_scu = line[4];
		String node_account = line[5];
		String node_delta_ieq = line[6];
		String node_feq_adj = line[7];
		String node_fsc = line[8];
		String node_gesce = line[9];
		String node_gescn = line[10];
		String node_gescp = line[11];
		String node_gfq = line[12];
		String node_gmee = line[13];
		String node_acctg_gmef = line[14];
		String node_grq = line[15];
		String node_ieq = line[16];
		int interval = Integer.parseInt(line[17]);
		String node_interval = String.format("%1$02d", interval);
		String node_ipf = line[18];
		String node_ipw = line[19];
		String node_acctg_lesdn = line[20];
		String node_acctg_lesdp = line[21];
		String node_lrq = line[22];
		String node_mep = line[23];
		String node_name = line[24];
		String node_neaa = line[25];
		String node_acctg_negc = line[26];
		String node_negc_sz_idx = line[27];
		String node_acctg_nelc = line[28];
		String node_nrsc = line[29];
		String node_acctg_ntsc = line[30];
		String node_size_index = line[31];
		String node_acctg_rcc = line[32];
		String node_rrs = line[33];
		String node_rrs_factor = line[34];
		String node_acctg_rsc = line[35];
		String node_acctg_rsd = line[36];
		String node_rtq = line[37];
		String node_rts = line[38];
		String node_shd = line[39];
		String node_spf = line[40];
		String node_t_factor = line[41];
		String node_vcrp = line[42];
		String node_weq = line[43];
		String node_facility_rsc = line[44];
		String node_acctg_fsd = line[45];
		String node_fcc = line[46];
		String node_nfsc = line[47];
		String node_gmea = line[48];
		String node_type = line[49];
		String no_CSZ_Applicable = line[50];
		String node_lcq = line[51];
		String node_lcscp = line[52];
		String node_oiec = line[53];
		String node_siec = line[54];
		String node_wlq = line[55];
		String node_complFlag = line[56];
		
		this.hasSzIdx = node_has_sz_idx.equals("1");
		this.positiveInj = node_positive_inj.equals("1");
		this.cntbSrq = node_cntb_srq.length() > 0? new BigDecimal(node_cntb_srq): null;
		this.pcu = node_pcu.equals("1");
		this.scu = node_scu.equals("1");
		this.accountId = node_account;
		this.deltaIeq = node_delta_ieq.length() > 0? new BigDecimal(node_delta_ieq): null;
		this.feqAdj = node_feq_adj.length() > 0? new BigDecimal(node_feq_adj): null;
		this.fsc = node_fsc.length() > 0? new BigDecimal(node_fsc): null;
		this.gesce = node_gesce.length() > 0? new BigDecimal(node_gesce): null;
		this.gescn = node_gescn.length() > 0? new BigDecimal(node_gescn): null;
		this.gescp = node_gescp.length() > 0? new BigDecimal(node_gescp): null;
		this.gfq = node_gfq.length() > 0? new BigDecimal(node_gfq): null;
		this.gmee = node_gmee.length() > 0? new BigDecimal(node_gmee): null;
		this.accountingGmef = node_acctg_gmef.length() > 0? new BigDecimal(node_acctg_gmef): null;
		this.grq = node_grq.length() > 0? new BigDecimal(node_grq): null;
		this.ieq = node_ieq.length() > 0? new BigDecimal(node_ieq): null;
		this.periodId = node_interval;
		this.ipf = node_ipf.length() > 0? new BigDecimal(node_ipf): null;
		this.ipw = node_ipw.length() > 0? new BigDecimal(node_ipw): null;
		this.accountingLesdn = node_acctg_lesdn.length() > 0? new BigDecimal(node_acctg_lesdn): null;
		this.accountingLesdp = node_acctg_lesdp.length() > 0? new BigDecimal(node_acctg_lesdp): null;
		this.lrq = node_lrq.length() > 0? new BigDecimal(node_lrq): null;
		this.mep = node_mep.length() > 0? new BigDecimal(node_mep): null;
		this.facilityId = node_name;
		this.neaa = node_neaa.length() > 0? new BigDecimal(node_neaa): null;
		this.accountingNegc = node_acctg_negc.length() > 0? new BigDecimal(node_acctg_negc): null;
		this.negcSzIdx = node_negc_sz_idx.length() > 0? new BigDecimal(node_negc_sz_idx): null;
		this.accountingNelc = node_acctg_nelc.length() > 0? new BigDecimal(node_acctg_nelc): null;
		this.nrsc = node_nrsc.length() > 0? new BigDecimal(node_nrsc): null;
		this.accountingNtsc = node_acctg_ntsc.length() > 0? new BigDecimal(node_acctg_ntsc): null;
		this.sizeIndex = node_size_index.length() > 0? new BigDecimal(node_size_index): null;
		this.accountingRcc = node_acctg_rcc.length() > 0? new BigDecimal(node_acctg_rcc): null;
		this.rrs = node_rrs.length() > 0? new BigDecimal(node_rrs): null;
		this.rrsFactor = node_rrs_factor.length() > 0? new BigDecimal(node_rrs_factor): null;
		this.accountingRsc = node_acctg_rsc.length() > 0? new BigDecimal(node_acctg_rsc): null;
		this.accountingRsd = node_acctg_rsd.length() > 0? new BigDecimal(node_acctg_rsd): null;
		this.rtq = node_rtq.length() > 0? new BigDecimal(node_rtq): null;
		this.rts = node_rts.length() > 0? new BigDecimal(node_rts): null;
		this.shd = node_shd.length() > 0? new BigDecimal(node_shd): null;
		this.spf = node_spf.length() > 0? new BigDecimal(node_spf): null;
		this.tFactor = node_t_factor.length() > 0? new BigDecimal(node_t_factor): null;
		this.vcrp = node_vcrp.length() > 0? new BigDecimal(node_vcrp): null;
		this.weq = node_weq.length() > 0? new BigDecimal(node_weq): null;
		this.facilityRsc = node_facility_rsc.length() > 0? new BigDecimal(node_facility_rsc): null;
		this.accountingFsd = node_acctg_fsd.length() > 0? new BigDecimal(node_acctg_fsd): null;
		this.fcc = node_fcc.length() > 0? new BigDecimal(node_fcc): null;
		this.nfsc = node_nfsc.length() > 0? new BigDecimal(node_nfsc): null;
		this.gmea = node_gmea.length() > 0? new BigDecimal(node_gmea): null;
		this.nodeType = node_type;
		this.noCsz = no_CSZ_Applicable.equals("1");
		this.lcq = node_lcq.length() > 0? new BigDecimal(node_lcq): null;
		this.lcscp = node_lcscp.length() > 0? new BigDecimal(node_lcscp): null;
		this.oiec = node_oiec.length() > 0? new BigDecimal(node_oiec): null;
		this.siec = node_siec.length() > 0? new BigDecimal(node_siec): null;
		this.wlq = node_wlq.length() > 0? new BigDecimal(node_wlq): null;
		this.complFlag = node_complFlag;
	}
	
	public String PFCheck(Facility in) {

		String result = null;

		if ((this.feqAdj == null && in.feqAdj != null) || (this.feqAdj != null && in.feqAdj == null)) result = "feqAdj missing value";
		if ((this.fsc == null && in.fsc != null) || (this.fsc != null && in.fsc == null)) result = "fsc missing value";
		if ((this.gesce == null && in.gesce != null) || (this.gesce != null && in.gesce == null)) result = "gesce missing value";
		if ((this.gescn == null && in.gescn != null) || (this.gescn != null && in.gescn == null)) result = "gescn missing value";
		if ((this.gescp == null && in.gescp != null) || (this.gescp != null && in.gescp == null)) result = "gescp missing value";
		//if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		//if ((this.accountingGmef == null && in.accountingGmef != null) || (this.accountingGmef != null && in.accountingGmef == null)) result = "accountingGmef missing value";
		if ((this.ipf == null && in.ipf != null) || (this.ipf != null && in.ipf == null)) result = "ipf missing value";
		if ((this.ipw == null && in.ipw != null) || (this.ipw != null && in.ipw == null)) result = "ipw missing value";
		if ((this.accountingLesdn == null && in.accountingLesdn != null) || (this.accountingLesdn != null && in.accountingLesdn == null)) result = "accountingLesdn missing value";
		if ((this.accountingLesdp == null && in.accountingLesdp != null) || (this.accountingLesdp != null && in.accountingLesdp == null)) result = "accountingLesdp missing value";
		if ((this.neaa == null && in.neaa != null) || (this.neaa != null && in.neaa == null)) result = "neaa missing value";
		if ((this.accountingNegc == null && in.accountingNegc != null) || (this.accountingNegc != null && in.accountingNegc == null)) result = "accountingNegc missing value";
		//if ((this.negcSzIdx == null && in.negcSzIdx != null) || (this.negcSzIdx != null && in.negcSzIdx == null)) result = "negcSzIdx missing value";
		if ((this.accountingNelc == null && in.accountingNelc != null) || (this.accountingNelc != null && in.accountingNelc == null)) result = "accountingNelc missing value";
		if ((this.nrsc == null && in.nrsc != null) || (this.nrsc != null && in.nrsc == null)) result = "nrsc missing value";
		if ((this.accountingNtsc == null && in.accountingNtsc != null) || (this.accountingNtsc != null && in.accountingNtsc == null)) result = "accountingNtsc missing value";
		if ((this.sizeIndex == null && in.sizeIndex != null) || (this.sizeIndex != null && in.sizeIndex == null)) result = "sizeIndex missing value";
		if ((this.accountingRcc == null && in.accountingRcc != null) || (this.accountingRcc != null && in.accountingRcc == null)) result = "accountingRcc missing value";
		if ((this.rrs == null && in.rrs != null) || (this.rrs != null && in.rrs == null)) result = "rrs missing value";
		if ((this.rrsFactor == null && in.rrsFactor != null) || (this.rrsFactor != null && in.rrsFactor == null)) result = "rrsFactor missing value";
		if ((this.accountingRsc == null && in.accountingRsc != null) || (this.accountingRsc != null && in.accountingRsc == null)) result = "accountingRsc missing value";
		if ((this.accountingRsd == null && in.accountingRsd != null) || (this.accountingRsd != null && in.accountingRsd == null)) result = "accountingRsd missing value";
		if ((this.rtq == null && in.rtq != null) || (this.rtq != null && in.rtq == null)) result = "rtq missing value";
		if ((this.rts == null && in.rts != null) || (this.rts != null && in.rts == null)) result = "rts missing value";
		if ((this.tFactor == null && in.tFactor != null) || (this.tFactor != null && in.tFactor == null)) result = "tFactor missing value";
		if ((this.vcrp == null && in.vcrp != null) || (this.vcrp != null && in.vcrp == null)) result = "vcrp missing value";
		if ((this.facilityRsc == null && in.facilityRsc != null) || (this.facilityRsc != null && in.facilityRsc == null)) result = "facilityRsc missing value";
		if ((this.accountingFsd == null && in.accountingFsd != null) || (this.accountingFsd != null && in.accountingFsd == null)) result = "accountingFsd missing value";
		if ((this.fcc == null && in.fcc != null) || (this.fcc != null && in.fcc == null)) result = "fcc missing value";
		if ((this.nfsc == null && in.nfsc != null) || (this.nfsc != null && in.nfsc == null)) result = "nfsc missing value";
		//if ((this.gmea == null && in.gmea != null) || (this.gmea != null && in.gmea == null)) result = "gmea missing value";
		if ((this.lcscp == null && in.lcscp != null) || (this.lcscp != null && in.lcscp == null)) result = "lcscp missing value";

		return result;
	}
	
	public String RSCheck(Facility in) {

		String result = null;

		if ((this.fsc == null && in.fsc != null) || (this.fsc != null && in.fsc == null)) result = "fsc missing value";
		//if ((this.gescp == null && in.gescp != null) || (this.gescp != null && in.gescp == null)) result = "gescp missing value";
		//if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		//if ((this.accountingGmef == null && in.accountingGmef != null) || (this.accountingGmef != null && in.accountingGmef == null)) result = "accountingGmef missing value";
		if ((this.ipf == null && in.ipf != null) || (this.ipf != null && in.ipf == null)) result = "ipf missing value";
		if ((this.ipw == null && in.ipw != null) || (this.ipw != null && in.ipw == null)) result = "ipw missing value";
		if ((this.accountingLesdn == null && in.accountingLesdn != null) || (this.accountingLesdn != null && in.accountingLesdn == null)) result = "accountingLesdn missing value";
		if ((this.accountingLesdp == null && in.accountingLesdp != null) || (this.accountingLesdp != null && in.accountingLesdp == null)) result = "accountingLesdp missing value";
		if ((this.accountingNegc == null && in.accountingNegc != null) || (this.accountingNegc != null && in.accountingNegc == null)) result = "accountingNegc missing value";
		//if ((this.negcSzIdx == null && in.negcSzIdx != null) || (this.negcSzIdx != null && in.negcSzIdx == null)) result = "negcSzIdx missing value";
		//if ((this.accountingNelc == null && in.accountingNelc != null) || (this.accountingNelc != null && in.accountingNelc == null)) result = "accountingNelc missing value";
		if ((this.nrsc == null && in.nrsc != null) || (this.nrsc != null && in.nrsc == null)) result = "nrsc missing value";
		if ((this.accountingNtsc == null && in.accountingNtsc != null) || (this.accountingNtsc != null && in.accountingNtsc == null)) result = "accountingNtsc missing value";
		//if ((this.sizeIndex == null && in.sizeIndex != null) || (this.sizeIndex != null && in.sizeIndex == null)) result = "sizeIndex missing value";
		if ((this.accountingRcc == null && in.accountingRcc != null) || (this.accountingRcc != null && in.accountingRcc == null)) result = "accountingRcc missing value";
		if ((this.rrs == null && in.rrs != null) || (this.rrs != null && in.rrs == null)) result = "rrs missing value";
		if ((this.rrsFactor == null && in.rrsFactor != null) || (this.rrsFactor != null && in.rrsFactor == null)) result = "rrsFactor missing value";
		if ((this.accountingRsc == null && in.accountingRsc != null) || (this.accountingRsc != null && in.accountingRsc == null)) result = "accountingRsc missing value";
		if ((this.accountingRsd == null && in.accountingRsd != null) || (this.accountingRsd != null && in.accountingRsd == null)) result = "accountingRsd missing value";
		if ((this.rtq == null && in.rtq != null) || (this.rtq != null && in.rtq == null)) result = "rtq missing value";
		if ((this.rts == null && in.rts != null) || (this.rts != null && in.rts == null)) result = "rts missing value";
		//if ((this.tFactor == null && in.tFactor != null) || (this.tFactor != null && in.tFactor == null)) result = "tFactor missing value";
		//if ((this.vcrp == null && in.vcrp != null) || (this.vcrp != null && in.vcrp == null)) result = "vcrp missing value";
		if ((this.facilityRsc == null && in.facilityRsc != null) || (this.facilityRsc != null && in.facilityRsc == null)) result = "facilityRsc missing value";
		//if ((this.accountingFsd == null && in.accountingFsd != null) || (this.accountingFsd != null && in.accountingFsd == null)) result = "accountingFsd missing value";
		if ((this.fcc == null && in.fcc != null) || (this.fcc != null && in.fcc == null)) result = "fcc missing value";
		//if ((this.gmea == null && in.gmea != null) || (this.gmea != null && in.gmea == null)) result = "gmea missing value";

		return result;
	}
	
	public String equal(Facility in) {

		String result = null;

		if (this.hasSzIdx != in.hasSzIdx) result = "hasSzIdx value mismatch";
		if (this.positiveInj != in.positiveInj) result = "positiveInj value mismatch";
		if (this.pcu != in.pcu) result = "pcu value mismatch";
		if (this.feqAdj != null && in.feqAdj != null) if (this.feqAdj.compareTo(in.feqAdj) != 0) result = "feqAdj value mismatch";
		if (this.fsc != null && in.fsc != null) if (this.fsc.compareTo(in.fsc) != 0) result = "fsc value mismatch";
		if (this.gesce != null && in.gesce != null) if (this.gesce.compareTo(in.gesce) != 0) result = "gesce value mismatch";
		if (this.gescn != null && in.gescn != null) if (this.gescn.compareTo(in.gescn) != 0) result = "gescn value mismatch";
		if (this.gescp != null && in.gescp != null) if (this.gescp.compareTo(in.gescp) != 0) result = "gescp value mismatch";
		//if (this.gmee != null && in.gmee != null) if (this.gmee.compareTo(in.gmee) != 0) result = "gmee value mismatch";
		//if (this.accountingGmef != null && in.accountingGmef != null) if (this.accountingGmef.compareTo(in.accountingGmef) != 0) result = "accountingGmef value mismatch";
		if (this.grq != null && in.grq != null) if (this.grq.compareTo(in.grq) != 0) result = "grq value mismatch";
		if (this.ieq != null && in.ieq != null) if (this.ieq.compareTo(in.ieq) != 0) result = "ieq value mismatch";
		if (this.periodId != null && in.periodId != null) if (this.periodId.compareTo(in.periodId) != 0) result = "periodId value mismatch";
		//if (this.ipf != null && in.ipf != null) if (this.ipf.compareTo(in.ipf) != 0) result = "ipf value mismatch";
		//if (this.ipw != null && in.ipw != null) if (this.ipw.compareTo(in.ipw) != 0) result = "ipw value mismatch";
		if (this.accountingLesdn != null && in.accountingLesdn != null) if (this.accountingLesdn.compareTo(in.accountingLesdn) != 0) result = "accountingLesdn value mismatch";
		if (this.accountingLesdp != null && in.accountingLesdp != null) if (this.accountingLesdp.compareTo(in.accountingLesdp) != 0) result = "accountingLesdp value mismatch";
		if (this.neaa != null && in.neaa != null) if (this.neaa.compareTo(in.neaa) != 0) result = "neaa value mismatch";
		if (this.accountingNegc != null && in.accountingNegc != null) if (this.accountingNegc.compareTo(in.accountingNegc) != 0) result = "accountingNegc value mismatch";
		if (this.negcSzIdx != null && in.negcSzIdx != null) if (this.negcSzIdx.compareTo(in.negcSzIdx) != 0) result = "negcSzIdx value mismatch";
		if (this.accountingNelc != null && in.accountingNelc != null) if (this.accountingNelc.compareTo(in.accountingNelc) != 0) result = "accountingNelc value mismatch";
		if (this.nrsc != null && in.nrsc != null) if (this.nrsc.compareTo(in.nrsc) != 0) result = "nrsc value mismatch";
		if (this.accountingNtsc != null && in.accountingNtsc != null) if (this.accountingNtsc.compareTo(in.accountingNtsc) != 0) result = "accountingNtsc value mismatch";
		//if (this.sizeIndex != null && in.sizeIndex != null) if (this.sizeIndex.compareTo(in.sizeIndex) != 0) result = "sizeIndex value mismatch";
		if (this.accountingRcc != null && in.accountingRcc != null) if (this.accountingRcc.compareTo(in.accountingRcc) != 0) result = "accountingRcc value mismatch";
		if (this.rrs != null && in.rrs != null) if (this.rrs.compareTo(in.rrs) != 0) result = "rrs value mismatch";
		//if (this.rrsFactor != null && in.rrsFactor != null) if (this.rrsFactor.compareTo(in.rrsFactor) != 0) result = "rrsFactor value mismatch";
		if (this.accountingRsc != null && in.accountingRsc != null) if (this.accountingRsc.compareTo(in.accountingRsc) != 0) result = "accountingRsc value mismatch";
		if (this.accountingRsd != null && in.accountingRsd != null) if (this.accountingRsd.compareTo(in.accountingRsd) != 0) result = "accountingRsd value mismatch";
		//if (this.rtq != null && in.rtq != null) if (this.rtq.compareTo(in.rtq) != 0) result = "rtq value mismatch";
		//if (this.rts != null && in.rts != null) if (this.rts.compareTo(in.rts) != 0) result = "rts value mismatch";
		//if (this.tFactor != null && in.tFactor != null) if (this.tFactor.compareTo(in.tFactor) != 0) result = "tFactor value mismatch";
		if (this.vcrp != null && in.vcrp != null) if (this.vcrp.compareTo(in.vcrp) != 0) result = "vcrp value mismatch";
		if (this.facilityRsc != null && in.facilityRsc != null) if (this.facilityRsc.compareTo(in.facilityRsc) != 0) result = "facilityRsc value mismatch";
		if (this.accountingFsd != null && in.accountingFsd != null) if (this.accountingFsd.compareTo(in.accountingFsd) != 0) result = "accountingFsd value mismatch";
		if (this.fcc != null && in.fcc != null) if (this.fcc.compareTo(in.fcc) != 0) result = "fcc value mismatch";
		if (this.nfsc != null && in.nfsc != null) if (this.nfsc.compareTo(in.nfsc) != 0) result = "nfsc value mismatch";
		if (this.gmea != null && in.gmea != null) if (this.gmea.compareTo(in.gmea) != 0) result = "gmea value mismatch";
		if (this.lcscp != null && in.lcscp != null) if (this.lcscp.compareTo(in.lcscp) != 0) result = "lcscp value mismatch";

		return result;
	}

	public String toInputString() {
		
		String result = (this.hasSzIdx == true? "1": "0") + "," +
				(this.positiveInj == true? "1": "0") + "," +
				(this.cntbSrq != null? this.cntbSrq.toString(): "") + "," +
				(this.pcu == true? "1": "0") + "," +
				(this.scu == true? "1": "0") + "," +
				this.accountId + "," +
				(this.deltaIeq != null? this.deltaIeq.toString(): "") + "," +
				(this.feqAdj != null? this.feqAdj.toString(): "") + "," +
				(this.fsc != null? this.fsc.toString(): "") + "," +
				(this.gesce != null? this.gesce.toString(): "") + "," +
				(this.gescn != null? this.gescn.toString(): "") + "," +
				(this.gescp != null? this.gescp.toString(): "") + "," +
				(this.gfq != null? this.gfq.toString(): "") + "," +
				(this.gmee != null? this.gmee.toString(): "") + "," +
				(this.gmef != null? this.gmef.toString(): "") + "," +
				(this.grq != null? this.grq.toString(): "") + "," +
				(this.ieq != null? this.ieq.toString(): "") + "," +
				this.periodId + "," +
				(this.ipf != null? this.ipf.toString(): "") + "," +
				(this.ipw != null? this.ipw.toString(): "") + "," +
				(this.lesdn != null? this.lesdn.toString(): "") + "," +
				(this.lesdp != null? this.lesdp.toString(): "") + "," +
				(this.lrq != null? this.lrq.toString(): "") + "," +
				(this.mep != null? this.mep.toString(): "") + "," +
				this.facilityId + "," +
				(this.neaa != null? this.neaa.toString(): "") + "," +
				(this.negc != null? this.negc.toString(): "") + "," +
				(this.negcSzIdx != null? this.negcSzIdx.toString(): "") + "," +
				(this.nelc != null? this.nelc.toString(): "") + "," +
				(this.nrsc != null? this.nrsc.toString(): "") + "," +
				(this.ntsc != null? this.ntsc.toString(): "") + "," +
				(this.sizeIndex != null? this.sizeIndex.toString(): "") + "," +
				(this.rcc != null? this.rcc.toString(): "") + "," +
				(this.rrs != null? this.rrs.toString(): "") + "," +
				(this.rrsFactor != null? this.rrsFactor.toString(): "") + "," +
				(this.rsc != null? this.rsc.toString(): "") + "," +
				(this.rsd != null? this.rsd.toString(): "") + "," +
				(this.rtq != null? this.rtq.toString(): "") + "," +
				(this.rts != null? this.rts.toString(): "") + "," +
				(this.shd != null? this.shd.toString(): "") + "," +
				(this.spf != null? this.spf.toString(): "") + "," +
				(this.tFactor != null? this.tFactor.toString(): "") + "," +
				(this.vcrp != null? this.vcrp.toString(): "") + "," +
				(this.weq != null? this.weq.toString(): "") + "," +
				(this.facilityRsc != null? this.facilityRsc.toString(): "") + "," +
				(this.accountingRcc != null? this.accountingRcc.toString(): "") + "," +
				(this.accountingRsc != null? this.accountingRsc.toString(): "") + "," +
				(this.accountingRsd != null? this.accountingRsd.toString(): "") + "," +
				this.nodeType + "," +
				(this.noCsz == true? "1": "0") + "," +
				(this.lcq != null? this.lcq.toString(): "") + "," +
				(this.oiec != null? this.oiec.toString(): "") + "," +
				(this.siec != null? this.siec.toString(): "") + "," +
				(this.wlq != null? this.wlq.toString(): "") + "," +
				(this.complFlag != null? this.complFlag.toString(): "");
		
		return result;
	}

	public String toOutputString() {
		
		String result = (this.hasSzIdx == true? "1": "0") + "," +
				(this.positiveInj == true? "1": "0") + "," +
				(this.cntbSrq != null? this.cntbSrq.toString(): "") + "," +
				(this.pcu == true? "1": "0") + "," +
				(this.scu == true? "1": "0") + "," +
				this.accountId + "," +
				(this.deltaIeq != null? this.deltaIeq.toString(): "") + "," +
				(this.feqAdj != null? this.feqAdj.toString(): "") + "," +
				(this.fsc != null? this.fsc.toString(): "") + "," +
				(this.gesce != null? this.gesce.toString(): "") + "," +
				(this.gescn != null? this.gescn.toString(): "") + "," +
				(this.gescp != null? this.gescp.toString(): "") + "," +
				(this.gfq != null? this.gfq.toString(): "") + "," +
				(this.gmee != null? this.gmee.toString(): "") + "," +
				(this.accountingGmef != null? this.accountingGmef.toString(): "") + "," +
				(this.grq != null? this.grq.toString(): "") + "," +
				(this.ieq != null? this.ieq.toString(): "") + "," +
				this.periodId + "," +
				(this.ipf != null? this.ipf.toString(): "") + "," +
				(this.ipw != null? this.ipw.toString(): "") + "," +
				(this.accountingLesdn != null? this.accountingLesdn.toString(): "") + "," +
				(this.accountingLesdp != null? this.accountingLesdp.toString(): "") + "," +
				(this.lrq != null? this.lrq.toString(): "") + "," +
				(this.mep != null? this.mep.toString(): "") + "," +
				this.facilityId + "," +
				(this.neaa != null? this.neaa.toString(): "") + "," +
				(this.accountingNegc != null? this.accountingNegc.toString(): "") + "," +
				(this.negcSzIdx != null? this.negcSzIdx.toString(): "") + "," +
				(this.accountingNelc != null? this.accountingNelc.toString(): "") + "," +
				(this.nrsc != null? this.nrsc.toString(): "") + "," +
				(this.accountingNtsc != null? this.accountingNtsc.toString(): "") + "," +
				(this.sizeIndex != null? this.sizeIndex.toString(): "") + "," +
				(this.accountingRcc != null? this.accountingRcc.toString(): "") + "," +
				(this.rrs != null? this.rrs.toString(): "") + "," +
				(this.rrsFactor != null? this.rrsFactor.toString(): "") + "," +
				(this.accountingRsc != null? this.accountingRsc.toString(): "") + "," +
				(this.accountingRsd != null? this.accountingRsd.toString(): "") + "," +
				(this.rtq != null? this.rtq.toString(): "") + "," +
				(this.rts != null? this.rts.toString(): "") + "," +
				(this.shd != null? this.shd.toString(): "") + "," +
				(this.spf != null? this.spf.toString(): "") + "," +
				(this.tFactor != null? this.tFactor.toString(): "") + "," +
				(this.vcrp != null? this.vcrp.toString(): "") + "," +
				(this.weq != null? this.weq.toString(): "") + "," +
				(this.facilityRsc != null? this.facilityRsc.toString(): "") + "," +
				(this.accountingFsd != null? this.accountingFsd.toString(): "") + "," +
				(this.fcc != null? this.fcc.toString(): "") + "," +
				(this.nfsc != null? this.nfsc.toString(): "") + "," +
				(this.gmea != null? this.gmea.toString(): "") + "," +
				this.nodeType + "," +
				(this.noCsz == true? "1": "0") + "," +
				(this.lcq != null? this.lcq.toString(): "") + "," +
				(this.lcscp != null? this.lcscp.toString(): "") + "," +
				(this.oiec != null? this.oiec.toString(): "") + "," +
				(this.siec != null? this.siec.toString(): "") + "," +
				(this.wlq != null? this.wlq.toString(): "") + "," +
				(this.complFlag != null? this.complFlag.toString(): "");
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"hasSzIdx," +
			"positiveInj," +
			"cntbSrq," +
			"pcu," +
			"scu," +
			"accountId," +
			"deltaIeq," +
			"feqAdj," +
			"fsc," +
			"gesce," +
			"gescn," +
			"gescp," +
			"gfq," +
			"gmee," +
			"gmef," +
			"grq," +
			"ieq," +
			"periodId," +
			"ipf," +
			"ipw," +
			"lesdn," +
			"lesdp," +
			"lrq," +
			"mep," +
			"facilityId," +
			"neaa," +
			"negc," +
			"negcSzIdx," +
			"nelc," +
			"nrsc," +
			"ntsc," +
			"sizeIndex," +
			"rcc," +
			"rrs," +
			"rrsFactor," +
			"rsc," +
			"rsd," +
			"rtq," +
			"rts," +
			"shd," +
			"spf," +
			"tFactor," +
			"vcrp," +
			"weq," +
			"facilityRsc," +
			"accountingRcc," +
			"accountingRsc," +
			"accountingRsd," +
			"nodeType," +
			"noCsz," +
			"lcq," +
			"oiec," +
			"siec," +
			"wlq," +
			"complFlag";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"hasSzIdx," +
			"positiveInj," +
			"cntbSrq," +
			"pcu," +
			"scu," +
			"accountId," +
			"deltaIeq," +
			"feqAdj," +
			"fsc," +
			"gesce," +
			"gescn," +
			"gescp," +
			"gfq," +
			"gmee," +
			"accountingGmef," +
			"grq," +
			"ieq," +
			"periodId," +
			"ipf," +
			"ipw," +
			"accountingLesdn," +
			"accountingLesdp," +
			"lrq," +
			"mep," +
			"facilityId," +
			"neaa," +
			"accountingNegc," +
			"negcSzIdx," +
			"accountingNelc," +
			"nrsc," +
			"accountingNtsc," +
			"sizeIndex," +
			"accountingRcc," +
			"rrs," +
			"rrsFactor," +
			"accountingRsc," +
			"accountingRsd," +
			"rtq," +
			"rts," +
			"shd," +
			"spf," +
			"tFactor," +
			"vcrp," +
			"weq," +
			"facilityRsc," +
			"accountingFsd," +
			"fcc," +
			"nfsc," +
			"gmea," +
			"nodeType," +
			"noCsz," +
			"lcq," +
			"lcscp," +
			"oiec," +
			"siec," +
			"wlq," +
			"complFlag";

		return header;
	}
}
