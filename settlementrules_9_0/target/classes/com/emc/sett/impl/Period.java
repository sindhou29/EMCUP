package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.AdjustmentT;
import com.emc.sett.model.PeriodT;

public class Period extends PeriodT {

	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String interval_account = line[0];
		String interval_baq_purchased = line[1];
		String interval_baq_sold = line[2];
		String interval_beq_purchased = line[3];
		String interval_beq_sold = line[4];
		String interval_besc = line[5];
		String interval_bfq_purchased = line[6];
		String interval_bfq_sold = line[7];
		String interval_bif_purchased = line[8];
		String interval_bif_sold = line[9];
		String interval_bwf_purchased = line[10];
		String interval_bwf_sold = line[11];
		String interval_delta_wcq = line[12];
		String interval_delta_weq = line[13];
		String interval_comp_emca = line[14];
		String interval_comp_psoa = line[15];
		String interval_ega_ieq = line[16];
		String interval_ega_weq = line[17];
		String interval_ega_wpq = line[18];
		String interval_eua = line[19];
		String interval_fcc = line[20];
		String interval_feq = line[21];
		String interval_fsc = line[22];
		String interval_fsd = line[23];
		String interval_gesc = line[24];
		String interval_gesce = line[25];
		String interval_gescn = line[26];
		String interval_gescp = line[27];
		String interval_gmee = line[28];
		String interval_gmef = line[29];
		String interval_heua = line[30];
		String interval_heusa = line[31];
		String interval_ieq = line[32];
		String interval_ieqp = line[33];
		String interval_imp_emca = line[34];
		String interval_imp_psoa = line[35];
		String interval_inc_nmea = line[36];
		String interval_v_fsc = line[37];
		String interval_v_gesc = line[38];
		String interval_v_gescn = line[39];
		String interval_v_gmee = line[40];
		String interval_v_inc_nmea = line[41];
		String interval_v_lesd = line[42];
		String interval_v_lesdn = line[43];
		String interval_v_nasc = line[44];
		String interval_v_nesc = line[45];
		String interval_v_nmea = line[46];
		String interval_v_npsc = line[47];
		String interval_v_rsc = line[48];
		String interval_lesd = line[49];
		String interval_lesdn = line[50];
		String interval_lesdp = line[51];
		String interval_lmee = line[52];
		String interval_lmef = line[53];
		String interval_mep = line[54];
		String interval_meusa = line[55];
		String interval_mfp = line[56];
		String interval_mrp = line[57];
		String interval_nasc = line[58];
		String interval_neaa = line[59];
		String interval_nead = line[60];
		String interval_negc = line[61];
		String interval_negc_ieq = line[62];
		String interval_nelc = line[63];
		String interval_nesc = line[64];
		String interval_nfsc = line[65];
		String interval_nmea = line[66];
		String interval_nde_count = line[67];
		String interval_npsc = line[68];
		String interval_nrsc = line[69];
		String interval_ntsc = line[70];
		int interval = Integer.parseInt(line[71]);
		String interval_number = String.format("%1$02d", interval);
		String interval_a_fsd = line[72];
		String interval_a_gesc = line[73];
		String interval_a_gesce = line[74];
		String interval_a_gescp = line[75];
		String interval_a_gmef = line[76];
		String interval_a_heusa = line[77];
		String interval_a_inc_nmea = line[78];
		String interval_a_lesd = line[79];
		String interval_a_lesdp = line[80];
		String interval_a_lmee = line[81];
		String interval_a_lmef = line[82];
		String interval_a_meusa = line[83];
		String interval_a_nasc = line[84];
		String interval_a_nesc = line[85];
		String interval_a_nmea = line[86];
		String interval_a_npsc = line[87];
		String interval_a_rsd = line[88];
		String interval_rcc = line[89];
		String interval_rsa = line[90];
		String interval_rsc = line[91];
		String interval_rsd = line[92];
		String interval_max_ieq = line[93];
		String interval_total_besc = line[94];
		String interval_total_fsc = line[95];
		String interval_total_fsd = line[96];
		String interval_total_gesc = line[97];
		String interval_total_gesce = line[98];
		String interval_total_gescn = line[99];
		String interval_total_gescp = line[100];
		String interval_total_gmee = line[101];
		String interval_total_gmef = line[102];
		String interval_total_heusa = line[103];
		String interval_total_lesd = line[104];
		String interval_total_lesdn = line[105];
		String interval_total_lesdp = line[106];
		String interval_total_lmee = line[107];
		String interval_total_lmef = line[108];
		String interval_total_meusa = line[109];
		String interval_total_nasc = line[110];
		String interval_total_nesc = line[111];
		String interval_total_nfsc = line[112];
		String interval_total_nmea = line[113];
		String interval_total_npsc = line[114];
		String interval_total_nrsc = line[115];
		String interval_total_inc_nmea = line[116];
		String interval_total_rcc = line[117];
		String interval_total_rsc = line[118];
		String interval_total_rsd = line[119];
		String interval_tte = line[120];
		String interval_usep = line[121];
		String interval_vcrp = line[122];
		String interval_vcrpk = line[123];
		String interval_vcsc = line[124];
		String interval_vcsck = line[125];
		String interval_wcq = line[126];
		String interval_weq = line[127];
		String interval_wpq = line[128];
		String interval_facility_rsc = line[129];
		String interval_gmea = line[130];
		String interval_lmea = line[131];
		String interval_a_lmea = line[132];
		String interval_a_gmea = line[133];
		String interval_ega_id = line[134];
		String interval_fee_total = line[135];
		String interval_imp_afp = line[136];
		String interval_imp_heuc = line[137];
		String interval_imp_meuc = line[138];
		String interval_imp_usep = line[139];
		String interval_wfq = line[140];
		String interval_wmq = line[141];
		String interval_delta_wmq = line[142];
		String interval_delta_wfq = line[143];
		String interval_gen_node = line[144];
		String interval_delta_wdq = line[145];
		String interval_lcp = line[146];
		String interval_wdq = line[147];
		String interval_imp_heur = line[148];
		String interval_imp_hlcu = line[149];
		
		this.accountId = interval_account;
		this.deltaWcq = interval_delta_wcq.length() > 0? new BigDecimal(interval_delta_wcq): null;
		this.deltaWeq = interval_delta_weq.length() > 0? new BigDecimal(interval_delta_weq): null;
		this.impEmca = interval_imp_emca.length() > 0? new BigDecimal(interval_imp_emca): null;
		this.impPsoa = interval_imp_psoa.length() > 0? new BigDecimal(interval_imp_psoa): null;
		this.mfp = interval_mfp.length() > 0? new BigDecimal(interval_mfp): null;
		this.periodId = interval_number;
		this.wcq = interval_wcq.length() > 0? new BigDecimal(interval_wcq): null;
		this.weq = interval_weq.length() > 0? new BigDecimal(interval_weq): null;
		this.wpq = interval_wpq.length() > 0? new BigDecimal(interval_wpq): null;
		this.egaId = interval_ega_id;
		this.impAfp = interval_imp_afp.length() > 0? new BigDecimal(interval_imp_afp): null;
		this.impHeuc = interval_imp_heuc.length() > 0? new BigDecimal(interval_imp_heuc): null;
		this.impMeuc = interval_imp_meuc.length() > 0? new BigDecimal(interval_imp_meuc): null;
		this.impUsep = interval_imp_usep.length() > 0? new BigDecimal(interval_imp_usep): null;
		this.wfq = interval_wfq.length() > 0? new BigDecimal(interval_wfq): null;
		this.wmq = interval_wmq.length() > 0? new BigDecimal(interval_wmq): null;
		this.deltaWmq = interval_delta_wmq.trim().length() > 0? new BigDecimal(interval_delta_wmq): null;
		this.deltaWfq = interval_delta_wfq.trim().length() > 0? new BigDecimal(interval_delta_wfq): null;
		this.genNode = interval_gen_node.equals("1");
		this.deltaWdq = interval_delta_wdq.length() > 0? new BigDecimal(interval_delta_wdq): null;
		this.lcp = interval_lcp.length() > 0? new BigDecimal(interval_lcp): null;
		this.wdq = interval_wdq.length() > 0? new BigDecimal(interval_wdq): null;
		this.impHeur = interval_imp_heur.length() > 0? new BigDecimal(interval_imp_heur): null;
		this.impHlcu = interval_imp_hlcu.length() > 0? new BigDecimal(interval_imp_hlcu): null;
		this.totalR = false;
	}
	
	public void setPeriodId(int idx) {
		this.periodId = String.format("%1$02d", idx);
	}
	
	public void setPeriodId(String idx) {
		int interval = Integer.parseInt(idx);
		setPeriodId(interval);
	}
	
	public String getKey() {
		return accountId + periodId;
	}
    
    public BigDecimal sumIncludedNmea() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getNmea() != null) {
		    		result = result.add(adj.getNmea());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedIpGstNmea() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getIpGstNmea() != null) {
		    		result = result.add(adj.getIpGstNmea());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedOpGstNmea() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getOpGstNmea() != null) {
		    		result = result.add(adj.getOpGstNmea());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedTotalNmea() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getTotalNmea() != null) {
		    		result = result.add(adj.getTotalNmea());
	    		}
	    	}
		}
    	
        return result;
    }
    
	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) {
		
		String interval_account = line[0];
		String interval_baq_purchased = line[1];
		String interval_baq_sold = line[2];
		String interval_beq_purchased = line[3];
		String interval_beq_sold = line[4];
		String interval_besc = line[5];
		String interval_bfq_purchased = line[6];
		String interval_bfq_sold = line[7];
		String interval_bif_purchased = line[8];
		String interval_bif_sold = line[9];
		String interval_bwf_purchased = line[10];
		String interval_bwf_sold = line[11];
		String interval_delta_wcq = line[12];
		String interval_delta_weq = line[13];
		String interval_comp_emca = line[14];
		String interval_comp_psoa = line[15];
		String interval_ega_ieq = line[16];
		String interval_ega_weq = line[17];
		String interval_ega_wpq = line[18];
		String interval_acctg_eua = line[19];
		String interval_fcc = line[20];
		String interval_feq = line[21];
		String interval_fsc = line[22];
		String interval_acctg_fsd = line[23];
		String interval_gesc = line[24];
		String interval_gesce = line[25];
		String interval_gescn = line[26];
		String interval_gescp = line[27];
		String interval_gmee = line[28];
		String interval_acctg_gmef = line[29];
		String interval_heua = line[30];
		String interval_acctg_heusa = line[31];
		String interval_ieq = line[32];
		String interval_ieqp = line[33];
		String interval_imp_emca = line[34];
		String interval_imp_psoa = line[35];
		String interval_inc_nmea = line[36];
		String interval_v_fsc = line[37];
		String interval_v_gesc = line[38];
		String interval_v_gescn = line[39];
		String interval_v_gmee = line[40];
		String interval_v_inc_nmea = line[41];
		String interval_v_lesd = line[42];
		String interval_v_lesdn = line[43];
		String interval_v_nasc = line[44];
		String interval_v_nesc = line[45];
		String interval_v_nmea = line[46];
		String interval_v_npsc = line[47];
		String interval_v_rsc = line[48];
		String interval_lesd = line[49];
		String interval_acctg_lesdn = line[50];
		String interval_acctg_lesdp = line[51];
		String interval_acctg_lmee = line[52];
		String interval_acctg_lmef = line[53];
		String interval_mep = line[54];
		String interval_acctg_meusa = line[55];
		String interval_mfp = line[56];
		String interval_mrp = line[57];
		String interval_nasc = line[58];
		String interval_neaa = line[59];
		String interval_acctg_nead = line[60];
		String interval_negc = line[61];
		String interval_negc_ieq = line[62];
		String interval_nelc = line[63];
		String interval_nesc = line[64];
		String interval_nfsc = line[65];
		String interval_nmea = line[66];
		String interval_nde_count = line[67];
		String interval_npsc = line[68];
		String interval_nrsc = line[69];
		String interval_ntsc = line[70];
		int interval = Integer.parseInt(line[71]);
		String interval_number = String.format("%1$02d", interval);
		String interval_a_fsd = line[72];
		String interval_a_gesc = line[73];
		String interval_a_gesce = line[74];
		String interval_a_gescp = line[75];
		String interval_a_gmef = line[76];
		String interval_a_heusa = line[77];
		String interval_a_inc_nmea = line[78];
		String interval_a_lesd = line[79];
		String interval_a_lesdp = line[80];
		String interval_a_lmee = line[81];
		String interval_a_lmef = line[82];
		String interval_a_meusa = line[83];
		String interval_a_nasc = line[84];
		String interval_a_nesc = line[85];
		String interval_a_nmea = line[86];
		String interval_a_npsc = line[87];
		String interval_a_rsd = line[88];
		String interval_rcc = line[89];
		String interval_rsa = line[90];
		String interval_rsc = line[91];
		String interval_rsd = line[92];
		String interval_max_ieq = line[93];
		String interval_total_besc = line[94];
		String interval_total_fsc = line[95];
		String interval_total_fsd = line[96];
		String interval_total_gesc = line[97];
		String interval_total_gesce = line[98];
		String interval_total_gescn = line[99];
		String interval_total_gescp = line[100];
		String interval_total_gmee = line[101];
		String interval_total_gmef = line[102];
		String interval_total_heusa = line[103];
		String interval_total_lesd = line[104];
		String interval_total_lesdn = line[105];
		String interval_total_lesdp = line[106];
		String interval_total_lmee = line[107];
		String interval_total_lmef = line[108];
		String interval_total_meusa = line[109];
		String interval_total_nasc = line[110];
		String interval_total_nesc = line[111];
		String interval_total_nfsc = line[112];
		String interval_total_nmea = line[113];
		String interval_total_npsc = line[114];
		String interval_total_nrsc = line[115];
		String interval_total_inc_nmea = line[116];
		String interval_total_rcc = line[117];
		String interval_total_rsc = line[118];
		String interval_total_rsd = line[119];
		String interval_tte = line[120];
		String interval_usep = line[121];
		String interval_vcrp = line[122];
		String interval_vcrpk = line[123];
		String interval_vcsc = line[124];
		String interval_vcsck = line[125];
		String interval_wcq = line[126];
		String interval_weq = line[127];
		String interval_wpq = line[128];
		String interval_facility_rsc = line[129];
		String interval_gmea = line[130];
		String interval_acctg_lmea = line[131];
		String interval_a_lmea = line[132];
		String interval_a_gmea = line[133];
		String interval_ega_id = line[134];
		String interval_fee_total = line[135];
		String interval_imp_afp = line[136];
		String interval_imp_heuc = line[137];
		String interval_imp_meuc = line[138];
		String interval_imp_usep = line[139];
		String interval_load_fsd = line[140];
		String interval_avcsc = line[141];
		String interval_wfq = line[142];
		String interval_wmq = line[143];
		String interval_delta_wmq = line[144];
		String interval_delta_wfq = line[145];
		String interval_comp_emcadm_price_cap = line[146];
		String interval_comp_emcadm_price_adj = line[147];
		String interval_fsrp = line[148];
		String interval_fsrpk = line[149];
		String interval_fssc = line[150];
		String interval_fssck = line[151];
		String interval_fsscp = line[152];
		String interval_fsscn = line[153];
		String interval_fsscrp = line[154];
		String interval_fsscrn = line[155];
		String interval_a_heur = line[156];
		String interval_a_lcsc = line[157];
		String interval_a_meuc = line[158];
		String interval_a_nsfc = line[159];
		String interval_a_ntsc = line[160];
		String interval_delta_wdq = line[161];
		String interval_emca = line[162];
		String interval_hersa = line[163];
		String interval_heur = line[164];
		String interval_round_hlcsa = line[165];
		String interval_hlcu = line[166];
		String interval_lcp = line[167];
		String interval_lcsc = line[168];
		String interval_meuc = line[169];
		String interval_psoa = line[170];
		String interval_total_heur = line[171];
		String interval_total_hlcu = line[172];
		String interval_total_lcsc = line[173];
		String interval_total_lmea = line[174];
		String interval_wdq = line[175];
		String interval_gen_node = line[176];
		String interval_imp_heur = line[177];
		String interval_imp_hlcu = line[178];
		String interval_a_hersa = line[179];
		String interval_a_hlcsa = line[180];
		String interval_v_lcsc = line[181];
		String interval_hlcsa = line[182];
		String interval_gen_fsd = line[183];
		
		this.accountId = interval_account;
		this.baqPurchased = interval_baq_purchased.length() > 0? new BigDecimal(interval_baq_purchased): null;
		this.baqSold = interval_baq_sold.length() > 0? new BigDecimal(interval_baq_sold): null;
		this.beqPurchased = interval_beq_purchased.length() > 0? new BigDecimal(interval_beq_purchased): null;
		this.beqSold = interval_beq_sold.length() > 0? new BigDecimal(interval_beq_sold): null;
		this.besc = interval_besc.length() > 0? new BigDecimal(interval_besc): null;
		this.bfqPurchased = interval_bfq_purchased.length() > 0? new BigDecimal(interval_bfq_purchased): null;
		this.bfqSold = interval_bfq_sold.length() > 0? new BigDecimal(interval_bfq_sold): null;
		this.bifPurchased = interval_bif_purchased.length() > 0? new BigDecimal(interval_bif_purchased): null;
		this.bifSold = interval_bif_sold.length() > 0? new BigDecimal(interval_bif_sold): null;
		this.bwfPurchased = interval_bwf_purchased.length() > 0? new BigDecimal(interval_bwf_purchased): null;
		this.bwfSold = interval_bwf_sold.length() > 0? new BigDecimal(interval_bwf_sold): null;
		this.deltaWcq = interval_delta_wcq.length() > 0? new BigDecimal(interval_delta_wcq): null;
		this.deltaWeq = interval_delta_weq.length() > 0? new BigDecimal(interval_delta_weq): null;
		this.compEmca = interval_comp_emca.length() > 0? new BigDecimal(interval_comp_emca): null;
		this.compPsoa = interval_comp_psoa.length() > 0? new BigDecimal(interval_comp_psoa): null;
		this.egaIeq = interval_ega_ieq.length() > 0? new BigDecimal(interval_ega_ieq): null;
		this.egaWeq = interval_ega_weq.length() > 0? new BigDecimal(interval_ega_weq): null;
		this.egaWpq = interval_ega_wpq.length() > 0? new BigDecimal(interval_ega_wpq): null;
		this.accountingEua = interval_acctg_eua.length() > 0? new BigDecimal(interval_acctg_eua): null;
		this.fcc = interval_fcc.length() > 0? new BigDecimal(interval_fcc): null;
		this.feq = interval_feq.length() > 0? new BigDecimal(interval_feq): null;
		this.fsc = interval_fsc.length() > 0? new BigDecimal(interval_fsc): null;
		this.accountingFsd = interval_acctg_fsd.length() > 0? new BigDecimal(interval_acctg_fsd): null;
		this.gesc = interval_gesc.length() > 0? new BigDecimal(interval_gesc): null;
		this.gesce = interval_gesce.length() > 0? new BigDecimal(interval_gesce): null;
		this.gescn = interval_gescn.length() > 0? new BigDecimal(interval_gescn): null;
		this.gescp = interval_gescp.length() > 0? new BigDecimal(interval_gescp): null;
		this.gmee = interval_gmee.length() > 0? new BigDecimal(interval_gmee): null;
		this.accountingGmef = interval_acctg_gmef.length() > 0? new BigDecimal(interval_acctg_gmef): null;
		this.heua = interval_heua.length() > 0? new BigDecimal(interval_heua): null;
		this.accountingHeusa = interval_acctg_heusa.length() > 0? new BigDecimal(interval_acctg_heusa): null;
		this.ieq = interval_ieq.length() > 0? new BigDecimal(interval_ieq): null;
		this.ieqp = interval_ieqp.length() > 0? new BigDecimal(interval_ieqp): null;
		this.impEmca = interval_imp_emca.length() > 0? new BigDecimal(interval_imp_emca): null;
		this.impPsoa = interval_imp_psoa.length() > 0? new BigDecimal(interval_imp_psoa): null;
		this.incNmea = interval_inc_nmea.length() > 0? new BigDecimal(interval_inc_nmea): null;
		this.ipGstFsc = interval_v_fsc.length() > 0? new BigDecimal(interval_v_fsc): null;
		this.ipGstGesc = interval_v_gesc.length() > 0? new BigDecimal(interval_v_gesc): null;
		this.ipGstGescn = interval_v_gescn.length() > 0? new BigDecimal(interval_v_gescn): null;
		this.ipGstGmee = interval_v_gmee.length() > 0? new BigDecimal(interval_v_gmee): null;
		this.ipGstIncNmea = interval_v_inc_nmea.length() > 0? new BigDecimal(interval_v_inc_nmea): null;
		this.ipGstLesd = interval_v_lesd.length() > 0? new BigDecimal(interval_v_lesd): null;
		this.ipGstLesdn = interval_v_lesdn.length() > 0? new BigDecimal(interval_v_lesdn): null;
		this.ipGstNasc = interval_v_nasc.length() > 0? new BigDecimal(interval_v_nasc): null;
		this.ipGstNesc = interval_v_nesc.length() > 0? new BigDecimal(interval_v_nesc): null;
		this.ipGstNmea = interval_v_nmea.length() > 0? new BigDecimal(interval_v_nmea): null;
		this.ipGstNpsc = interval_v_npsc.length() > 0? new BigDecimal(interval_v_npsc): null;
		this.ipGstRsc = interval_v_rsc.length() > 0? new BigDecimal(interval_v_rsc): null;
		this.lesd = interval_lesd.length() > 0? new BigDecimal(interval_lesd): null;
		this.accountingLesdn = interval_acctg_lesdn.length() > 0? new BigDecimal(interval_acctg_lesdn): null;
		this.accountingLesdp = interval_acctg_lesdp.length() > 0? new BigDecimal(interval_acctg_lesdp): null;
		this.accountingLmee = interval_acctg_lmee.length() > 0? new BigDecimal(interval_acctg_lmee): null;
		this.accountingLmef = interval_acctg_lmef.length() > 0? new BigDecimal(interval_acctg_lmef): null;
		this.mep = interval_mep.length() > 0? new BigDecimal(interval_mep): null;
		this.accountingMeusa = interval_acctg_meusa.length() > 0? new BigDecimal(interval_acctg_meusa): null;
		this.mfp = interval_mfp.length() > 0? new BigDecimal(interval_mfp): null;		
		this.mrp = interval_mrp.length() > 0? new BigDecimal(interval_mrp): null;
		this.nasc = interval_nasc.length() > 0? new BigDecimal(interval_nasc): null;
		this.neaa = interval_neaa.length() > 0? new BigDecimal(interval_neaa): null;
		this.accountingNead = interval_acctg_nead.length() > 0? new BigDecimal(interval_acctg_nead): null;
		this.negc = interval_negc.length() > 0? new BigDecimal(interval_negc): null;
		this.negcIeq = interval_negc_ieq.length() > 0? new BigDecimal(interval_negc_ieq): null;
		this.nelc = interval_nelc.length() > 0? new BigDecimal(interval_nelc): null;
		this.nesc = interval_nesc.length() > 0? new BigDecimal(interval_nesc): null;
		this.nfsc = interval_nfsc.length() > 0? new BigDecimal(interval_nfsc): null;
		this.nmea = interval_nmea.length() > 0? new BigDecimal(interval_nmea): null;
		this.nodeCount = interval_nde_count.length() > 0? new BigDecimal(interval_nde_count): null;
		this.npsc = interval_npsc.length() > 0? new BigDecimal(interval_npsc): null;
		this.nrsc = interval_nrsc.length() > 0? new BigDecimal(interval_nrsc): null;
		this.ntsc = interval_ntsc.length() > 0? new BigDecimal(interval_ntsc): null;
		this.periodId = interval_number;
		this.opGstFsd = interval_a_fsd.length() > 0? new BigDecimal(interval_a_fsd): null;
		this.opGstGesc = interval_a_gesc.length() > 0? new BigDecimal(interval_a_gesc): null;
		this.opGstGesce = interval_a_gesce.length() > 0? new BigDecimal(interval_a_gesce): null;
		this.opGstGescp = interval_a_gescp.length() > 0? new BigDecimal(interval_a_gescp): null;
		this.opGstGmef = interval_a_gmef.length() > 0? new BigDecimal(interval_a_gmef): null;
		this.opGstHeusa = interval_a_heusa.length() > 0? new BigDecimal(interval_a_heusa): null;
		this.opGstIncNmea = interval_a_inc_nmea.length() > 0? new BigDecimal(interval_a_inc_nmea): null;
		this.opGstLesd = interval_a_lesd.length() > 0? new BigDecimal(interval_a_lesd): null;
		this.opGstLesdp = interval_a_lesdp.length() > 0? new BigDecimal(interval_a_lesdp): null;
		this.opGstLmee = interval_a_lmee.length() > 0? new BigDecimal(interval_a_lmee): null;
		this.opGstLmef = interval_a_lmef.length() > 0? new BigDecimal(interval_a_lmef): null;
		this.opGstMeusa = interval_a_meusa.length() > 0? new BigDecimal(interval_a_meusa): null;
		this.opGstNasc = interval_a_nasc.length() > 0? new BigDecimal(interval_a_nasc): null;
		this.opGstNesc = interval_a_nesc.length() > 0? new BigDecimal(interval_a_nesc): null;
		this.opGstNmea = interval_a_nmea.length() > 0? new BigDecimal(interval_a_nmea): null;
		this.opGstNpsc = interval_a_npsc.length() > 0? new BigDecimal(interval_a_npsc): null;
		this.opGstRsd = interval_a_rsd.length() > 0? new BigDecimal(interval_a_rsd): null;
		this.rcc = interval_rcc.length() > 0? new BigDecimal(interval_rcc): null;
		this.rsa = interval_rsa.length() > 0? new BigDecimal(interval_rsa): null;
		this.rsc = interval_rsc.length() > 0? new BigDecimal(interval_rsc): null;
		this.rsd = interval_rsd.length() > 0? new BigDecimal(interval_rsd): null;
		this.maxIeq = interval_max_ieq.length() > 0? new BigDecimal(interval_max_ieq): null;
		this.totalBesc = interval_total_besc.length() > 0? new BigDecimal(interval_total_besc): null;
		this.totalFsc = interval_total_fsc.length() > 0? new BigDecimal(interval_total_fsc): null;
		this.totalFsd = interval_total_fsd.length() > 0? new BigDecimal(interval_total_fsd): null;
		this.totalGesc = interval_total_gesc.length() > 0? new BigDecimal(interval_total_gesc): null;
		this.totalGesce = interval_total_gesce.length() > 0? new BigDecimal(interval_total_gesce): null;
		this.totalGescn = interval_total_gescn.length() > 0? new BigDecimal(interval_total_gescn): null;
		this.totalGescp = interval_total_gescp.length() > 0? new BigDecimal(interval_total_gescp): null;
		this.totalGmee = interval_total_gmee.length() > 0? new BigDecimal(interval_total_gmee): null;
		this.totalGmef = interval_total_gmef.length() > 0? new BigDecimal(interval_total_gmef): null;
		this.totalHeusa = interval_total_heusa.length() > 0? new BigDecimal(interval_total_heusa): null;
		this.totalLesd = interval_total_lesd.length() > 0? new BigDecimal(interval_total_lesd): null;
		this.totalLesdn = interval_total_lesdn.length() > 0? new BigDecimal(interval_total_lesdn): null;
		this.totalLesdp = interval_total_lesdp.length() > 0? new BigDecimal(interval_total_lesdp): null;
		this.totalLmee = interval_total_lmee.length() > 0? new BigDecimal(interval_total_lmee): null;
		this.totalLmef = interval_total_lmef.length() > 0? new BigDecimal(interval_total_lmef): null;
		this.totalMeusa = interval_total_meusa.length() > 0? new BigDecimal(interval_total_meusa): null;
		this.totalNasc = interval_total_nasc.length() > 0? new BigDecimal(interval_total_nasc): null;
		this.totalNesc = interval_total_nesc.length() > 0? new BigDecimal(interval_total_nesc): null;
		this.totalNfsc = interval_total_nfsc.length() > 0? new BigDecimal(interval_total_nfsc): null;
		this.totalNmea = interval_total_nmea.length() > 0? new BigDecimal(interval_total_nmea): null;
		this.totalNpsc = interval_total_npsc.length() > 0? new BigDecimal(interval_total_npsc): null;
		this.totalNrsc = interval_total_nrsc.length() > 0? new BigDecimal(interval_total_nrsc): null;
		this.totalIncNmea = interval_total_inc_nmea.length() > 0? new BigDecimal(interval_total_inc_nmea): null;
		this.totalRcc = interval_total_rcc.length() > 0? new BigDecimal(interval_total_rcc): null;
		this.totalRsc = interval_total_rsc.length() > 0? new BigDecimal(interval_total_rsc): null;
		this.totalRsd = interval_total_rsd.length() > 0? new BigDecimal(interval_total_rsd): null;
		this.tte = interval_tte.length() > 0? new BigDecimal(interval_tte): null;
		this.usep = interval_usep.length() > 0? new BigDecimal(interval_usep): null;
		this.vcrp = interval_vcrp.length() > 0? new BigDecimal(interval_vcrp): null;
		this.vcrpk = interval_vcrpk.length() > 0? new BigDecimal(interval_vcrpk): null;
		this.vcsc = interval_vcsc.length() > 0? new BigDecimal(interval_vcsc): null;
		this.vcsck = interval_vcsck.length() > 0? new BigDecimal(interval_vcsck): null;
		this.wcq = interval_wcq.length() > 0? new BigDecimal(interval_wcq): null;
		this.weq = interval_weq.length() > 0? new BigDecimal(interval_weq): null;
		this.wpq = interval_wpq.length() > 0? new BigDecimal(interval_wpq): null;
		this.facilityRsc = interval_facility_rsc.length() > 0? new BigDecimal(interval_facility_rsc): null;
		this.gmea = interval_gmea.length() > 0? new BigDecimal(interval_gmea): null;
		this.accountingLmea = interval_acctg_lmea.length() > 0? new BigDecimal(interval_acctg_lmea): null;
		this.opGstLmea = interval_a_lmea.length() > 0? new BigDecimal(interval_a_lmea): null;
		this.opGstGmea = interval_a_gmea.length() > 0? new BigDecimal(interval_a_gmea): null;
		this.egaId = interval_ega_id;
		this.compFeeTotal = interval_fee_total.length() > 0? new BigDecimal(interval_fee_total): null;
		this.impAfp = interval_imp_afp.length() > 0? new BigDecimal(interval_imp_afp): null;
		this.impHeuc = interval_imp_heuc.length() > 0? new BigDecimal(interval_imp_heuc): null;
		this.impMeuc = interval_imp_meuc.length() > 0? new BigDecimal(interval_imp_meuc): null;
		this.impUsep = interval_imp_usep.length() > 0? new BigDecimal(interval_imp_usep): null;
		this.loadFsd = interval_load_fsd.length() > 0? new BigDecimal(interval_load_fsd): null;
		this.avcsc = interval_avcsc.length() > 0? new BigDecimal(interval_avcsc): null;
		this.wfq = interval_wfq.length() > 0? new BigDecimal(interval_wfq): null;
		this.wmq = interval_wmq.length() > 0? new BigDecimal(interval_wmq): null;
		this.deltaWmq = interval_delta_wmq.length() > 0? new BigDecimal(interval_delta_wmq): null;
		this.deltaWfq = interval_delta_wfq.length() > 0? new BigDecimal(interval_delta_wfq): null;
		this.compEmcAdmCap = interval_comp_emcadm_price_cap.length() > 0? new BigDecimal(interval_comp_emcadm_price_cap): null;
		this.compEmcAdmAdj = interval_comp_emcadm_price_adj.length() > 0? new BigDecimal(interval_comp_emcadm_price_adj): null;
		this.fsrp = interval_fsrp.length() > 0? new BigDecimal(interval_fsrp): null;
		this.fsrpk = interval_fsrpk.length() > 0? new BigDecimal(interval_fsrpk): null;
		this.fssc = interval_fssc.length() > 0? new BigDecimal(interval_fssc): null;
		this.fssck = interval_fssck.length() > 0? new BigDecimal(interval_fssck): null;
		this.fsscp = interval_fsscp.length() > 0? new BigDecimal(interval_fsscp): null;
		this.fsscn = interval_fsscn.length() > 0? new BigDecimal(interval_fsscn): null;
		this.fsscrp = interval_fsscrp.length() > 0? new BigDecimal(interval_fsscrp): null;
		this.fsscrn = interval_fsscrn.length() > 0? new BigDecimal(interval_fsscrn): null;
		this.opGstHeur = interval_a_heur.length() > 0? new BigDecimal(interval_a_heur): null;
		this.opGstLcsc = interval_a_lcsc.length() > 0? new BigDecimal(interval_a_lcsc): null;
		this.opGstMeuc = interval_a_meuc.length() > 0? new BigDecimal(interval_a_meuc): null;
		this.opGstNsfc = interval_a_nsfc.length() > 0? new BigDecimal(interval_a_nsfc): null;
		this.opGstNtsc = interval_a_ntsc.length() > 0? new BigDecimal(interval_a_ntsc): null;
		this.deltaWdq = interval_delta_wdq.length() > 0? new BigDecimal(interval_delta_wdq): null;
		this.emca = interval_emca.length() > 0? new BigDecimal(interval_emca): null;
		this.hersa = interval_hersa.length() > 0? new BigDecimal(interval_hersa): null;
		this.heur = interval_heur.length() > 0? new BigDecimal(interval_heur): null;
		this.roundedHlcsa = interval_round_hlcsa.length() > 0? new BigDecimal(interval_round_hlcsa): null;
		this.hlcu = interval_hlcu.length() > 0? new BigDecimal(interval_hlcu): null;
		this.lcp = interval_lcp.length() > 0? new BigDecimal(interval_lcp): null;
		this.lcsc = interval_lcsc.length() > 0? new BigDecimal(interval_lcsc): null;
		this.meuc = interval_meuc.length() > 0? new BigDecimal(interval_meuc): null;
		this.psoa = interval_psoa.length() > 0? new BigDecimal(interval_psoa): null;
		this.totalHeur = interval_total_heur.length() > 0? new BigDecimal(interval_total_heur): null;
		this.totalHlcu = interval_total_hlcu.length() > 0? new BigDecimal(interval_total_hlcu): null;
		this.totalLcsc = interval_total_lcsc.length() > 0? new BigDecimal(interval_total_lcsc): null;
		this.totalLmea = interval_total_lmea.length() > 0? new BigDecimal(interval_total_lmea): null;
		this.wdq = interval_wdq.length() > 0? new BigDecimal(interval_wdq): null;
		this.genNode = interval_gen_node.equals("1");
		this.impHeur = interval_imp_heur.length() > 0? new BigDecimal(interval_imp_heur): null;
		this.impHlcu = interval_imp_hlcu.length() > 0? new BigDecimal(interval_imp_hlcu): null;
		this.opGstHersa = interval_a_hersa.length() > 0? new BigDecimal(interval_a_hersa): null;
		this.opGstHlcsa = interval_a_hlcsa.length() > 0? new BigDecimal(interval_a_hlcsa): null;
		this.ipGstLcsc = interval_v_lcsc.length() > 0? new BigDecimal(interval_v_lcsc): null;
		this.hlcsa = interval_hlcsa.length() > 0? new BigDecimal(interval_hlcsa): null;
		this.genFsd = interval_gen_fsd.length() > 0? new BigDecimal(interval_gen_fsd): null;
	}
	
	public String PFCheck(Period in) {

		String result = null;

		if ((this.baqPurchased == null && in.baqPurchased != null) || (this.baqPurchased != null && in.baqPurchased == null)) result = "baqPurchased missing value";
		if ((this.baqSold == null && in.baqSold != null) || (this.baqSold != null && in.baqSold == null)) result = "baqSold missing value";
		if ((this.beqPurchased == null && in.beqPurchased != null) || (this.beqPurchased != null && in.beqPurchased == null)) result = "beqPurchased missing value";
		if ((this.beqSold == null && in.beqSold != null) || (this.beqSold != null && in.beqSold == null)) result = "beqSold missing value";
		if ((this.besc == null && in.besc != null) || (this.besc != null && in.besc == null)) result = "besc missing value";
		if ((this.bfqPurchased == null && in.bfqPurchased != null) || (this.bfqPurchased != null && in.bfqPurchased == null)) result = "bfqPurchased missing value";
		if ((this.bfqSold == null && in.bfqSold != null) || (this.bfqSold != null && in.bfqSold == null)) result = "bfqSold missing value";
		if ((this.bifPurchased == null && in.bifPurchased != null) || (this.bifPurchased != null && in.bifPurchased == null)) result = "bifPurchased missing value";
		if ((this.bifSold == null && in.bifSold != null) || (this.bifSold != null && in.bifSold == null)) result = "bifSold missing value";
		if ((this.bwfPurchased == null && in.bwfPurchased != null) || (this.bwfPurchased != null && in.bwfPurchased == null)) result = "bwfPurchased missing value";
		if ((this.bwfSold == null && in.bwfSold != null) || (this.bwfSold != null && in.bwfSold == null)) result = "bwfSold missing value";

		if ((this.compEmca == null && in.compEmca != null) || (this.compEmca != null && in.compEmca == null)) result = "compEmca missing value";
		if ((this.compPsoa == null && in.compPsoa != null) || (this.compPsoa != null && in.compPsoa == null)) result = "compPsoa missing value";
		if ((this.egaIeq == null && in.egaIeq != null) || (this.egaIeq != null && in.egaIeq == null)) result = "egaIeq missing value";
		if ((this.egaWeq == null && in.egaWeq != null) || (this.egaWeq != null && in.egaWeq == null)) result = "egaWeq missing value";
		if ((this.egaWpq == null && in.egaWpq != null) || (this.egaWpq != null && in.egaWpq == null)) result = "egaWpq missing value";
		if ((this.accountingEua == null && in.accountingEua != null) || (this.accountingEua != null && in.accountingEua == null)) result = "accountingEua missing value";
		if ((this.fcc == null && in.fcc != null) || (this.fcc != null && in.fcc == null)) result = "fcc missing value";
		if ((this.feq == null && in.feq != null) || (this.feq != null && in.feq == null)) result = "feq missing value";
		if ((this.fsc == null && in.fsc != null) || (this.fsc != null && in.fsc == null)) result = "fsc missing value";
		if ((this.accountingFsd == null && in.accountingFsd != null) || (this.accountingFsd != null && in.accountingFsd == null)) result = "accountingFsd missing value";
		if ((this.gesc == null && in.gesc != null) || (this.gesc != null && in.gesc == null)) result = "gesc missing value";
		if ((this.gesce == null && in.gesce != null) || (this.gesce != null && in.gesce == null)) result = "gesce missing value";
		if ((this.gescn == null && in.gescn != null) || (this.gescn != null && in.gescn == null)) result = "gescn missing value";
		if ((this.gescp == null && in.gescp != null) || (this.gescp != null && in.gescp == null)) result = "gescp missing value";
		//if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		//if ((this.accountingGmef == null && in.accountingGmef != null) || (this.accountingGmef != null && in.accountingGmef == null)) result = "accountingGmef missing value";
		if ((this.heua == null && in.heua != null) || (this.heua != null && in.heua == null)) result = "heua missing value";
		if ((this.accountingHeusa == null && in.accountingHeusa != null) || (this.accountingHeusa != null && in.accountingHeusa == null)) result = "accountingHeusa missing value";
		if ((this.ieq == null && in.ieq != null) || (this.ieq != null && in.ieq == null)) result = "ieq missing value";
		if ((this.ieqp == null && in.ieqp != null) || (this.ieqp != null && in.ieqp == null)) result = "ieqp missing value";
		//if ((this.impEmca == null && in.impEmca != null) || (this.impEmca != null && in.impEmca == null)) result = "impEmca missing value";
		//if ((this.impPsoa == null && in.impPsoa != null) || (this.impPsoa != null && in.impPsoa == null)) result = "impPsoa missing value";
		if ((this.incNmea == null && in.incNmea != null) || (this.incNmea != null && in.incNmea == null)) result = "incNmea missing value";
		if ((this.ipGstFsc == null && in.ipGstFsc != null) || (this.ipGstFsc != null && in.ipGstFsc == null)) result = "ipGstFsc missing value";
		if ((this.ipGstGesc == null && in.ipGstGesc != null) || (this.ipGstGesc != null && in.ipGstGesc == null)) result = "ipGstGesc missing value";
		if ((this.ipGstGescn == null && in.ipGstGescn != null) || (this.ipGstGescn != null && in.ipGstGescn == null)) result = "ipGstGescn missing value";
		//if ((this.ipGstGmee == null && in.ipGstGmee != null) || (this.ipGstGmee != null && in.ipGstGmee == null)) result = "ipGstGmee missing value";
		if ((this.ipGstIncNmea == null && in.ipGstIncNmea != null) || (this.ipGstIncNmea != null && in.ipGstIncNmea == null)) result = "ipGstIncNmea missing value";
		if ((this.ipGstLesd == null && in.ipGstLesd != null) || (this.ipGstLesd != null && in.ipGstLesd == null)) result = "ipGstLesd missing value";
		if ((this.ipGstLesdn == null && in.ipGstLesdn != null) || (this.ipGstLesdn != null && in.ipGstLesdn == null)) result = "ipGstLesdn missing value";
		if ((this.ipGstNasc == null && in.ipGstNasc != null) || (this.ipGstNasc != null && in.ipGstNasc == null)) result = "ipGstNasc missing value";
		if ((this.ipGstNesc == null && in.ipGstNesc != null) || (this.ipGstNesc != null && in.ipGstNesc == null)) result = "ipGstNesc missing value";
		//if ((this.ipGstNmea == null && in.ipGstNmea != null) || (this.ipGstNmea != null && in.ipGstNmea == null)) result = "ipGstNmea missing value";
		//if ((this.ipGstNpsc == null && in.ipGstNpsc != null) || (this.ipGstNpsc != null && in.ipGstNpsc == null)) result = "ipGstNpsc missing value";
		if ((this.ipGstRsc == null && in.ipGstRsc != null) || (this.ipGstRsc != null && in.ipGstRsc == null)) result = "ipGstRsc missing value";
		if ((this.lesd == null && in.lesd != null) || (this.lesd != null && in.lesd == null)) result = "lesd missing value";
		if ((this.accountingLesdn == null && in.accountingLesdn != null) || (this.accountingLesdn != null && in.accountingLesdn == null)) result = "accountingLesdn missing value";
		if ((this.accountingLesdp == null && in.accountingLesdp != null) || (this.accountingLesdp != null && in.accountingLesdp == null)) result = "accountingLesdp missing value";
		if ((this.accountingLmee == null && in.accountingLmee != null) || (this.accountingLmee != null && in.accountingLmee == null)) result = "accountingLmee missing value";
		if ((this.accountingLmef == null && in.accountingLmef != null) || (this.accountingLmef != null && in.accountingLmef == null)) result = "accountingLmef missing value";
		if ((this.mep == null && in.mep != null) || (this.mep != null && in.mep == null)) result = "mep missing value";
		if ((this.accountingMeusa == null && in.accountingMeusa != null) || (this.accountingMeusa != null && in.accountingMeusa == null)) result = "accountingMeusa missing value";

		if ((this.nasc == null && in.nasc != null) || (this.nasc != null && in.nasc == null)) result = "nasc missing value";
		if ((this.neaa == null && in.neaa != null) || (this.neaa != null && in.neaa == null)) result = "neaa missing value";
		if ((this.accountingNead == null && in.accountingNead != null) || (this.accountingNead != null && in.accountingNead == null)) result = "accountingNead missing value";
		if ((this.negc == null && in.negc != null) || (this.negc != null && in.negc == null)) result = "negc missing value";
		if ((this.negcIeq == null && in.negcIeq != null) || (this.negcIeq != null && in.negcIeq == null)) result = "negcIeq missing value";
		if ((this.nelc == null && in.nelc != null) || (this.nelc != null && in.nelc == null)) result = "nelc missing value";
		if ((this.nesc == null && in.nesc != null) || (this.nesc != null && in.nesc == null)) result = "nesc missing value";
		if ((this.nfsc == null && in.nfsc != null) || (this.nfsc != null && in.nfsc == null)) result = "nfsc missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";
		if ((this.nodeCount == null && in.nodeCount != null) || (this.nodeCount != null && in.nodeCount == null)) result = "nodeCount missing value";
		//if ((this.npsc == null && in.npsc != null) || (this.npsc != null && in.npsc == null)) result = "npsc missing value";
		if ((this.nrsc == null && in.nrsc != null) || (this.nrsc != null && in.nrsc == null)) result = "nrsc missing value";
		if ((this.ntsc == null && in.ntsc != null) || (this.ntsc != null && in.ntsc == null)) result = "ntsc missing value";

		if ((this.opGstFsd == null && in.opGstFsd != null) || (this.opGstFsd != null && in.opGstFsd == null)) result = "opGstFsd missing value";
		if ((this.opGstGesc == null && in.opGstGesc != null) || (this.opGstGesc != null && in.opGstGesc == null)) result = "opGstGesc missing value";
		if ((this.opGstGesce == null && in.opGstGesce != null) || (this.opGstGesce != null && in.opGstGesce == null)) result = "opGstGesce missing value";
		if ((this.opGstGescp == null && in.opGstGescp != null) || (this.opGstGescp != null && in.opGstGescp == null)) result = "opGstGescp missing value";
		//if ((this.opGstGmef == null && in.opGstGmef != null) || (this.opGstGmef != null && in.opGstGmef == null)) result = "opGstGmef missing value";
		if ((this.opGstHeusa == null && in.opGstHeusa != null) || (this.opGstHeusa != null && in.opGstHeusa == null)) result = "opGstHeusa missing value";
		if ((this.opGstIncNmea == null && in.opGstIncNmea != null) || (this.opGstIncNmea != null && in.opGstIncNmea == null)) result = "opGstIncNmea missing value";
		if ((this.opGstLesd == null && in.opGstLesd != null) || (this.opGstLesd != null && in.opGstLesd == null)) result = "opGstLesd missing value";
		if ((this.opGstLesdp == null && in.opGstLesdp != null) || (this.opGstLesdp != null && in.opGstLesdp == null)) result = "opGstLesdp missing value";
		if ((this.opGstLmee == null && in.opGstLmee != null) || (this.opGstLmee != null && in.opGstLmee == null)) result = "opGstLmee missing value";
		if ((this.opGstLmef == null && in.opGstLmef != null) || (this.opGstLmef != null && in.opGstLmef == null)) result = "opGstLmef missing value";
		if ((this.opGstMeusa == null && in.opGstMeusa != null) || (this.opGstMeusa != null && in.opGstMeusa == null)) result = "opGstMeusa missing value";
		if ((this.opGstNasc == null && in.opGstNasc != null) || (this.opGstNasc != null && in.opGstNasc == null)) result = "opGstNasc missing value";
		if ((this.opGstNesc == null && in.opGstNesc != null) || (this.opGstNesc != null && in.opGstNesc == null)) result = "opGstNesc missing value";
		if ((this.opGstNmea == null && in.opGstNmea != null) || (this.opGstNmea != null && in.opGstNmea == null)) result = "opGstNmea missing value";
		//if ((this.opGstNpsc == null && in.opGstNpsc != null) || (this.opGstNpsc != null && in.opGstNpsc == null)) result = "opGstNpsc missing value";
		if ((this.opGstRsd == null && in.opGstRsd != null) || (this.opGstRsd != null && in.opGstRsd == null)) result = "opGstRsd missing value";
		if ((this.rcc == null && in.rcc != null) || (this.rcc != null && in.rcc == null)) result = "rcc missing value";
		if ((this.rsa == null && in.rsa != null) || (this.rsa != null && in.rsa == null)) result = "rsa missing value";
		if ((this.rsc == null && in.rsc != null) || (this.rsc != null && in.rsc == null)) result = "rsc missing value";
		if ((this.rsd == null && in.rsd != null) || (this.rsd != null && in.rsd == null)) result = "rsd missing value";
		if ((this.maxIeq == null && in.maxIeq != null) || (this.maxIeq != null && in.maxIeq == null)) result = "maxIeq missing value";
		if ((this.totalBesc == null && in.totalBesc != null) || (this.totalBesc != null && in.totalBesc == null)) result = "totalBesc missing value";
		if ((this.totalFsc == null && in.totalFsc != null) || (this.totalFsc != null && in.totalFsc == null)) result = "totalFsc missing value";
		if ((this.totalFsd == null && in.totalFsd != null) || (this.totalFsd != null && in.totalFsd == null)) result = "totalFsd missing value";
		if ((this.totalGesc == null && in.totalGesc != null) || (this.totalGesc != null && in.totalGesc == null)) result = "totalGesc missing value";
		if ((this.totalGesce == null && in.totalGesce != null) || (this.totalGesce != null && in.totalGesce == null)) result = "totalGesce missing value";
		if ((this.totalGescn == null && in.totalGescn != null) || (this.totalGescn != null && in.totalGescn == null)) result = "totalGescn missing value";
		if ((this.totalGescp == null && in.totalGescp != null) || (this.totalGescp != null && in.totalGescp == null)) result = "totalGescp missing value";
		//if ((this.totalGmee == null && in.totalGmee != null) || (this.totalGmee != null && in.totalGmee == null)) result = "totalGmee missing value";
		//if ((this.totalGmef == null && in.totalGmef != null) || (this.totalGmef != null && in.totalGmef == null)) result = "totalGmef missing value";
		if ((this.totalHeusa == null && in.totalHeusa != null) || (this.totalHeusa != null && in.totalHeusa == null)) result = "totalHeusa missing value";
		if ((this.totalLesd == null && in.totalLesd != null) || (this.totalLesd != null && in.totalLesd == null)) result = "totalLesd missing value";
		if ((this.totalLesdn == null && in.totalLesdn != null) || (this.totalLesdn != null && in.totalLesdn == null)) result = "totalLesdn missing value";
		if ((this.totalLesdp == null && in.totalLesdp != null) || (this.totalLesdp != null && in.totalLesdp == null)) result = "totalLesdp missing value";
		if ((this.totalLmee == null && in.totalLmee != null) || (this.totalLmee != null && in.totalLmee == null)) result = "totalLmee missing value";
		if ((this.totalLmef == null && in.totalLmef != null) || (this.totalLmef != null && in.totalLmef == null)) result = "totalLmef missing value";
		if ((this.totalMeusa == null && in.totalMeusa != null) || (this.totalMeusa != null && in.totalMeusa == null)) result = "totalMeusa missing value";
		if ((this.totalNasc == null && in.totalNasc != null) || (this.totalNasc != null && in.totalNasc == null)) result = "totalNasc missing value";
		if ((this.totalNesc == null && in.totalNesc != null) || (this.totalNesc != null && in.totalNesc == null)) result = "totalNesc missing value";
		if ((this.totalNfsc == null && in.totalNfsc != null) || (this.totalNfsc != null && in.totalNfsc == null)) result = "totalNfsc missing value";
		if ((this.totalNmea == null && in.totalNmea != null) || (this.totalNmea != null && in.totalNmea == null)) result = "totalNmea missing value";
		//if ((this.totalNpsc == null && in.totalNpsc != null) || (this.totalNpsc != null && in.totalNpsc == null)) result = "totalNpsc missing value";
		if ((this.totalNrsc == null && in.totalNrsc != null) || (this.totalNrsc != null && in.totalNrsc == null)) result = "totalNrsc missing value";
		if ((this.totalIncNmea == null && in.totalIncNmea != null) || (this.totalIncNmea != null && in.totalIncNmea == null)) result = "totalIncNmea missing value";
		if ((this.totalRcc == null && in.totalRcc != null) || (this.totalRcc != null && in.totalRcc == null)) result = "totalRcc missing value";
		if ((this.totalRsc == null && in.totalRsc != null) || (this.totalRsc != null && in.totalRsc == null)) result = "totalRsc missing value";
		if ((this.totalRsd == null && in.totalRsd != null) || (this.totalRsd != null && in.totalRsd == null)) result = "totalRsd missing value";
		if ((this.tte == null && in.tte != null) || (this.tte != null && in.tte == null)) result = "tte missing value";

		if ((this.vcrp == null && in.vcrp != null) || (this.vcrp != null && in.vcrp == null)) result = "vcrp missing value";
		//if ((this.vcrpk == null && in.vcrpk != null) || (this.vcrpk != null && in.vcrpk == null)) result = "vcrpk missing value";
		if ((this.vcsc == null && in.vcsc != null) || (this.vcsc != null && in.vcsc == null)) result = "vcsc missing value";
		//if ((this.vcsck == null && in.vcsck != null) || (this.vcsck != null && in.vcsck == null)) result = "vcsck missing value";

		if ((this.facilityRsc == null && in.facilityRsc != null) || (this.facilityRsc != null && in.facilityRsc == null)) result = "facilityRsc missing value";
		//if ((this.gmea == null && in.gmea != null) || (this.gmea != null && in.gmea == null)) result = "gmea missing value";
		if ((this.accountingLmea == null && in.accountingLmea != null) || (this.accountingLmea != null && in.accountingLmea == null)) result = "accountingLmea missing value";
		if ((this.opGstLmea == null && in.opGstLmea != null) || (this.opGstLmea != null && in.opGstLmea == null)) result = "opGstLmea missing value";
		//if ((this.opGstGmea == null && in.opGstGmea != null) || (this.opGstGmea != null && in.opGstGmea == null)) result = "opGstGmea missing value";

		if ((this.compFeeTotal == null && in.compFeeTotal != null) || (this.compFeeTotal != null && in.compFeeTotal == null)) result = "compFeeTotal missing value";

		if ((this.loadFsd == null && in.loadFsd != null) || (this.loadFsd != null && in.loadFsd == null)) result = "loadFsd missing value";
		if ((this.avcsc == null && in.avcsc != null) || (this.avcsc != null && in.avcsc == null)) result = "avcsc missing value";

		if ((this.compEmcAdmCap == null && in.compEmcAdmCap != null) || (this.compEmcAdmCap != null && in.compEmcAdmCap == null)) result = "compEmcAdmCap missing value";
		if ((this.compEmcAdmAdj == null && in.compEmcAdmAdj != null) || (this.compEmcAdmAdj != null && in.compEmcAdmAdj == null)) result = "compEmcAdmAdj missing value";
		if ((this.fsrp == null && in.fsrp != null) || (this.fsrp != null && in.fsrp == null)) result = "fsrp missing value";
		if ((this.fsrpk == null && in.fsrpk != null) || (this.fsrpk != null && in.fsrpk == null)) result = "fsrpk missing value";
		if ((this.fssc == null && in.fssc != null) || (this.fssc != null && in.fssc == null)) result = "fssc missing value";
		if ((this.fssck == null && in.fssck != null) || (this.fssck != null && in.fssck == null)) result = "fssck missing value";
		if ((this.fsscp == null && in.fsscp != null) || (this.fsscp != null && in.fsscp == null)) result = "fsscp missing value";
		if ((this.fsscn == null && in.fsscn != null) || (this.fsscn != null && in.fsscn == null)) result = "fsscn missing value";
		if ((this.fsscrp == null && in.fsscrp != null) || (this.fsscrp != null && in.fsscrp == null)) result = "fsscrp missing value";
		if ((this.fsscrn == null && in.fsscrn != null) || (this.fsscrn != null && in.fsscrn == null)) result = "fsscrn missing value";
		if ((this.opGstHeur == null && in.opGstHeur != null) || (this.opGstHeur != null && in.opGstHeur == null)) result = "opGstHeur missing value";
		if ((this.opGstLcsc == null && in.opGstLcsc != null) || (this.opGstLcsc != null && in.opGstLcsc == null)) result = "opGstLcsc missing value";
		if ((this.opGstMeuc == null && in.opGstMeuc != null) || (this.opGstMeuc != null && in.opGstMeuc == null)) result = "opGstMeuc missing value";
		if ((this.opGstNsfc == null && in.opGstNsfc != null) || (this.opGstNsfc != null && in.opGstNsfc == null)) result = "opGstNsfc missing value";
		if ((this.opGstNtsc == null && in.opGstNtsc != null) || (this.opGstNtsc != null && in.opGstNtsc == null)) result = "opGstNtsc missing value";

		if ((this.emca == null && in.emca != null) || (this.emca != null && in.emca == null)) result = "emca missing value";
		if ((this.hersa == null && in.hersa != null) || (this.hersa != null && in.hersa == null)) result = "hersa missing value";
		if ((this.heur == null && in.heur != null) || (this.heur != null && in.heur == null)) result = "heur missing value";
		if ((this.roundedHlcsa == null && in.roundedHlcsa != null) || (this.roundedHlcsa != null && in.roundedHlcsa == null)) result = "roundedHlcsa missing value";
		if ((this.hlcu == null && in.hlcu != null) || (this.hlcu != null && in.hlcu == null)) result = "hlcu missing value";

		if ((this.lcsc == null && in.lcsc != null) || (this.lcsc != null && in.lcsc == null)) result = "lcsc missing value";
		if ((this.meuc == null && in.meuc != null) || (this.meuc != null && in.meuc == null)) result = "meuc missing value";
		if ((this.psoa == null && in.psoa != null) || (this.psoa != null && in.psoa == null)) result = "psoa missing value";
		if ((this.totalHeur == null && in.totalHeur != null) || (this.totalHeur != null && in.totalHeur == null)) result = "totalHeur missing value";
		if ((this.totalHlcu == null && in.totalHlcu != null) || (this.totalHlcu != null && in.totalHlcu == null)) result = "totalHlcu missing value";
		if ((this.totalLcsc == null && in.totalLcsc != null) || (this.totalLcsc != null && in.totalLcsc == null)) result = "totalLcsc missing value";
		if ((this.totalLmea == null && in.totalLmea != null) || (this.totalLmea != null && in.totalLmea == null)) result = "totalLmea missing value";

		if ((this.opGstHersa == null && in.opGstHersa != null) || (this.opGstHersa != null && in.opGstHersa == null)) result = "opGstHersa missing value";
		if ((this.opGstHlcsa == null && in.opGstHlcsa != null) || (this.opGstHlcsa != null && in.opGstHlcsa == null)) result = "opGstHlcsa missing value";
		if ((this.ipGstLcsc == null && in.ipGstLcsc != null) || (this.ipGstLcsc != null && in.ipGstLcsc == null)) result = "ipGstLcsc missing value";
		if ((this.hlcsa == null && in.hlcsa != null) || (this.hlcsa != null && in.hlcsa == null)) result = "hlcsa missing value";
		if ((this.genFsd == null && in.genFsd != null) || (this.genFsd != null && in.genFsd == null)) result = "genFsd missing value";

		return result;
	}
	
	public String RSCheck(Period in) {

		String result = null;

		if ((this.baqPurchased == null && in.baqPurchased != null) || (this.baqPurchased != null && in.baqPurchased == null)) result = "baqPurchased missing value";
		if ((this.baqSold == null && in.baqSold != null) || (this.baqSold != null && in.baqSold == null)) result = "baqSold missing value";
		if ((this.beqPurchased == null && in.beqPurchased != null) || (this.beqPurchased != null && in.beqPurchased == null)) result = "beqPurchased missing value";
		if ((this.beqSold == null && in.beqSold != null) || (this.beqSold != null && in.beqSold == null)) result = "beqSold missing value";
		if ((this.besc == null && in.besc != null) || (this.besc != null && in.besc == null)) result = "besc missing value";
		if ((this.bfqPurchased == null && in.bfqPurchased != null) || (this.bfqPurchased != null && in.bfqPurchased == null)) result = "bfqPurchased missing value";
		if ((this.bfqSold == null && in.bfqSold != null) || (this.bfqSold != null && in.bfqSold == null)) result = "bfqSold missing value";
		if ((this.bifPurchased == null && in.bifPurchased != null) || (this.bifPurchased != null && in.bifPurchased == null)) result = "bifPurchased missing value";
		if ((this.bifSold == null && in.bifSold != null) || (this.bifSold != null && in.bifSold == null)) result = "bifSold missing value";
		if ((this.bwfPurchased == null && in.bwfPurchased != null) || (this.bwfPurchased != null && in.bwfPurchased == null)) result = "bwfPurchased missing value";
		if ((this.bwfSold == null && in.bwfSold != null) || (this.bwfSold != null && in.bwfSold == null)) result = "bwfSold missing value";

		//if ((this.compEmca == null && in.compEmca != null) || (this.compEmca != null && in.compEmca == null)) result = "compEmca missing value";
		//if ((this.compPsoa == null && in.compPsoa != null) || (this.compPsoa != null && in.compPsoa == null)) result = "compPsoa missing value";
		//if ((this.egaIeq == null && in.egaIeq != null) || (this.egaIeq != null && in.egaIeq == null)) result = "egaIeq missing value";
		//if ((this.egaWeq == null && in.egaWeq != null) || (this.egaWeq != null && in.egaWeq == null)) result = "egaWeq missing value";
		//if ((this.egaWpq == null && in.egaWpq != null) || (this.egaWpq != null && in.egaWpq == null)) result = "egaWpq missing value";
		//if ((this.accountingEua == null && in.accountingEua != null) || (this.accountingEua != null && in.accountingEua == null)) result = "accountingEua missing value";
		if ((this.fcc == null && in.fcc != null) || (this.fcc != null && in.fcc == null)) result = "fcc missing value";
		//if ((this.feq == null && in.feq != null) || (this.feq != null && in.feq == null)) result = "feq missing value";
		if ((this.fsc == null && in.fsc != null) || (this.fsc != null && in.fsc == null)) result = "fsc missing value";
		//if ((this.accountingFsd == null && in.accountingFsd != null) || (this.accountingFsd != null && in.accountingFsd == null)) result = "accountingFsd missing value";
		//if ((this.gesc == null && in.gesc != null) || (this.gesc != null && in.gesc == null)) result = "gesc missing value";
		//if ((this.gesce == null && in.gesce != null) || (this.gesce != null && in.gesce == null)) result = "gesce missing value";
		//if ((this.gescn == null && in.gescn != null) || (this.gescn != null && in.gescn == null)) result = "gescn missing value";
		if ((this.gescp == null && in.gescp != null) || (this.gescp != null && in.gescp == null)) result = "gescp missing value";
		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.accountingGmef == null && in.accountingGmef != null) || (this.accountingGmef != null && in.accountingGmef == null)) result = "accountingGmef missing value";
		//if ((this.heua == null && in.heua != null) || (this.heua != null && in.heua == null)) result = "heua missing value";
		//if ((this.accountingHeusa == null && in.accountingHeusa != null) || (this.accountingHeusa != null && in.accountingHeusa == null)) result = "accountingHeusa missing value";
		//if ((this.ieq == null && in.ieq != null) || (this.ieq != null && in.ieq == null)) result = "ieq missing value";
		//if ((this.ieqp == null && in.ieqp != null) || (this.ieqp != null && in.ieqp == null)) result = "ieqp missing value";
		if ((this.impEmca == null && in.impEmca != null) || (this.impEmca != null && in.impEmca == null)) result = "impEmca missing value";
		if ((this.impPsoa == null && in.impPsoa != null) || (this.impPsoa != null && in.impPsoa == null)) result = "impPsoa missing value";
		if ((this.incNmea == null && in.incNmea != null) || (this.incNmea != null && in.incNmea == null)) result = "incNmea missing value";
		if ((this.ipGstFsc == null && in.ipGstFsc != null) || (this.ipGstFsc != null && in.ipGstFsc == null)) result = "ipGstFsc missing value";
		//if ((this.ipGstGesc == null && in.ipGstGesc != null) || (this.ipGstGesc != null && in.ipGstGesc == null)) result = "ipGstGesc missing value";
		//if ((this.ipGstGescn == null && in.ipGstGescn != null) || (this.ipGstGescn != null && in.ipGstGescn == null)) result = "ipGstGescn missing value";
		if ((this.ipGstGmee == null && in.ipGstGmee != null) || (this.ipGstGmee != null && in.ipGstGmee == null)) result = "ipGstGmee missing value";
		if ((this.ipGstIncNmea == null && in.ipGstIncNmea != null) || (this.ipGstIncNmea != null && in.ipGstIncNmea == null)) result = "ipGstIncNmea missing value";
		//if ((this.ipGstLesd == null && in.ipGstLesd != null) || (this.ipGstLesd != null && in.ipGstLesd == null)) result = "ipGstLesd missing value";
		//if ((this.ipGstLesdn == null && in.ipGstLesdn != null) || (this.ipGstLesdn != null && in.ipGstLesdn == null)) result = "ipGstLesdn missing value";
		//if ((this.ipGstNasc == null && in.ipGstNasc != null) || (this.ipGstNasc != null && in.ipGstNasc == null)) result = "ipGstNasc missing value";
		//if ((this.ipGstNesc == null && in.ipGstNesc != null) || (this.ipGstNesc != null && in.ipGstNesc == null)) result = "ipGstNesc missing value";
		if ((this.ipGstNmea == null && in.ipGstNmea != null) || (this.ipGstNmea != null && in.ipGstNmea == null)) result = "ipGstNmea missing value";
		//if ((this.ipGstNpsc == null && in.ipGstNpsc != null) || (this.ipGstNpsc != null && in.ipGstNpsc == null)) result = "ipGstNpsc missing value";
		if ((this.ipGstRsc == null && in.ipGstRsc != null) || (this.ipGstRsc != null && in.ipGstRsc == null)) result = "ipGstRsc missing value";
		//if ((this.lesd == null && in.lesd != null) || (this.lesd != null && in.lesd == null)) result = "lesd missing value";
		//if ((this.accountingLesdn == null && in.accountingLesdn != null) || (this.accountingLesdn != null && in.accountingLesdn == null)) result = "accountingLesdn missing value";
		//if ((this.accountingLesdp == null && in.accountingLesdp != null) || (this.accountingLesdp != null && in.accountingLesdp == null)) result = "accountingLesdp missing value";
		if ((this.accountingLmee == null && in.accountingLmee != null) || (this.accountingLmee != null && in.accountingLmee == null)) result = "accountingLmee missing value";
		if ((this.accountingLmef == null && in.accountingLmef != null) || (this.accountingLmef != null && in.accountingLmef == null)) result = "accountingLmef missing value";
		if ((this.mep == null && in.mep != null) || (this.mep != null && in.mep == null)) result = "mep missing value";
		//if ((this.accountingMeusa == null && in.accountingMeusa != null) || (this.accountingMeusa != null && in.accountingMeusa == null)) result = "accountingMeusa missing value";

		//if ((this.nasc == null && in.nasc != null) || (this.nasc != null && in.nasc == null)) result = "nasc missing value";
		//if ((this.neaa == null && in.neaa != null) || (this.neaa != null && in.neaa == null)) result = "neaa missing value";
		//if ((this.accountingNead == null && in.accountingNead != null) || (this.accountingNead != null && in.accountingNead == null)) result = "accountingNead missing value";
		//if ((this.negc == null && in.negc != null) || (this.negc != null && in.negc == null)) result = "negc missing value";
		//if ((this.negcIeq == null && in.negcIeq != null) || (this.negcIeq != null && in.negcIeq == null)) result = "negcIeq missing value";
		//if ((this.nelc == null && in.nelc != null) || (this.nelc != null && in.nelc == null)) result = "nelc missing value";
		//if ((this.nesc == null && in.nesc != null) || (this.nesc != null && in.nesc == null)) result = "nesc missing value";
		//if ((this.nfsc == null && in.nfsc != null) || (this.nfsc != null && in.nfsc == null)) result = "nfsc missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";
		if ((this.nodeCount == null && in.nodeCount != null) || (this.nodeCount != null && in.nodeCount == null)) result = "nodeCount missing value";
		//if ((this.npsc == null && in.npsc != null) || (this.npsc != null && in.npsc == null)) result = "npsc missing value";
		if ((this.nrsc == null && in.nrsc != null) || (this.nrsc != null && in.nrsc == null)) result = "nrsc missing value";
		if ((this.ntsc == null && in.ntsc != null) || (this.ntsc != null && in.ntsc == null)) result = "ntsc missing value";

		//if ((this.opGstFsd == null && in.opGstFsd != null) || (this.opGstFsd != null && in.opGstFsd == null)) result = "opGstFsd missing value";
		//if ((this.opGstGesc == null && in.opGstGesc != null) || (this.opGstGesc != null && in.opGstGesc == null)) result = "opGstGesc missing value";
		//if ((this.opGstGesce == null && in.opGstGesce != null) || (this.opGstGesce != null && in.opGstGesce == null)) result = "opGstGesce missing value";
		if ((this.opGstGescp == null && in.opGstGescp != null) || (this.opGstGescp != null && in.opGstGescp == null)) result = "opGstGescp missing value";
		if ((this.opGstGmef == null && in.opGstGmef != null) || (this.opGstGmef != null && in.opGstGmef == null)) result = "opGstGmef missing value";
		//if ((this.opGstHeusa == null && in.opGstHeusa != null) || (this.opGstHeusa != null && in.opGstHeusa == null)) result = "opGstHeusa missing value";
		if ((this.opGstIncNmea == null && in.opGstIncNmea != null) || (this.opGstIncNmea != null && in.opGstIncNmea == null)) result = "opGstIncNmea missing value";
		//if ((this.opGstLesd == null && in.opGstLesd != null) || (this.opGstLesd != null && in.opGstLesd == null)) result = "opGstLesd missing value";
		//if ((this.opGstLesdp == null && in.opGstLesdp != null) || (this.opGstLesdp != null && in.opGstLesdp == null)) result = "opGstLesdp missing value";
		if ((this.opGstLmee == null && in.opGstLmee != null) || (this.opGstLmee != null && in.opGstLmee == null)) result = "opGstLmee missing value";
		if ((this.opGstLmef == null && in.opGstLmef != null) || (this.opGstLmef != null && in.opGstLmef == null)) result = "opGstLmef missing value";
		//if ((this.opGstMeusa == null && in.opGstMeusa != null) || (this.opGstMeusa != null && in.opGstMeusa == null)) result = "opGstMeusa missing value";
		//if ((this.opGstNasc == null && in.opGstNasc != null) || (this.opGstNasc != null && in.opGstNasc == null)) result = "opGstNasc missing value";
		//if ((this.opGstNesc == null && in.opGstNesc != null) || (this.opGstNesc != null && in.opGstNesc == null)) result = "opGstNesc missing value";
		if ((this.opGstNmea == null && in.opGstNmea != null) || (this.opGstNmea != null && in.opGstNmea == null)) result = "opGstNmea missing value";
		//if ((this.opGstNpsc == null && in.opGstNpsc != null) || (this.opGstNpsc != null && in.opGstNpsc == null)) result = "opGstNpsc missing value";
		if ((this.opGstRsd == null && in.opGstRsd != null) || (this.opGstRsd != null && in.opGstRsd == null)) result = "opGstRsd missing value";
		if ((this.rcc == null && in.rcc != null) || (this.rcc != null && in.rcc == null)) result = "rcc missing value";
		//if ((this.rsa == null && in.rsa != null) || (this.rsa != null && in.rsa == null)) result = "rsa missing value";
		if ((this.rsc == null && in.rsc != null) || (this.rsc != null && in.rsc == null)) result = "rsc missing value";
		if ((this.rsd == null && in.rsd != null) || (this.rsd != null && in.rsd == null)) result = "rsd missing value";
		//if ((this.maxIeq == null && in.maxIeq != null) || (this.maxIeq != null && in.maxIeq == null)) result = "maxIeq missing value";
		if ((this.totalBesc == null && in.totalBesc != null) || (this.totalBesc != null && in.totalBesc == null)) result = "totalBesc missing value";
		if ((this.totalFsc == null && in.totalFsc != null) || (this.totalFsc != null && in.totalFsc == null)) result = "totalFsc missing value";
		if ((this.totalFsd == null && in.totalFsd != null) || (this.totalFsd != null && in.totalFsd == null)) result = "totalFsd missing value";
		//if ((this.totalGesc == null && in.totalGesc != null) || (this.totalGesc != null && in.totalGesc == null)) result = "totalGesc missing value";
		//if ((this.totalGesce == null && in.totalGesce != null) || (this.totalGesce != null && in.totalGesce == null)) result = "totalGesce missing value";
		//if ((this.totalGescn == null && in.totalGescn != null) || (this.totalGescn != null && in.totalGescn == null)) result = "totalGescn missing value";
		if ((this.totalGescp == null && in.totalGescp != null) || (this.totalGescp != null && in.totalGescp == null)) result = "totalGescp missing value";
		if ((this.totalGmee == null && in.totalGmee != null) || (this.totalGmee != null && in.totalGmee == null)) result = "totalGmee missing value";
		if ((this.totalGmef == null && in.totalGmef != null) || (this.totalGmef != null && in.totalGmef == null)) result = "totalGmef missing value";
		//if ((this.totalHeusa == null && in.totalHeusa != null) || (this.totalHeusa != null && in.totalHeusa == null)) result = "totalHeusa missing value";
		//if ((this.totalLesd == null && in.totalLesd != null) || (this.totalLesd != null && in.totalLesd == null)) result = "totalLesd missing value";
		//if ((this.totalLesdn == null && in.totalLesdn != null) || (this.totalLesdn != null && in.totalLesdn == null)) result = "totalLesdn missing value";
		//if ((this.totalLesdp == null && in.totalLesdp != null) || (this.totalLesdp != null && in.totalLesdp == null)) result = "totalLesdp missing value";
		if ((this.totalLmee == null && in.totalLmee != null) || (this.totalLmee != null && in.totalLmee == null)) result = "totalLmee missing value";
		if ((this.totalLmef == null && in.totalLmef != null) || (this.totalLmef != null && in.totalLmef == null)) result = "totalLmef missing value";
		//if ((this.totalMeusa == null && in.totalMeusa != null) || (this.totalMeusa != null && in.totalMeusa == null)) result = "totalMeusa missing value";
		//if ((this.totalNasc == null && in.totalNasc != null) || (this.totalNasc != null && in.totalNasc == null)) result = "totalNasc missing value";
		//if ((this.totalNesc == null && in.totalNesc != null) || (this.totalNesc != null && in.totalNesc == null)) result = "totalNesc missing value";
		//if ((this.totalNfsc == null && in.totalNfsc != null) || (this.totalNfsc != null && in.totalNfsc == null)) result = "totalNfsc missing value";
		if ((this.totalNmea == null && in.totalNmea != null) || (this.totalNmea != null && in.totalNmea == null)) result = "totalNmea missing value";
		//if ((this.totalNpsc == null && in.totalNpsc != null) || (this.totalNpsc != null && in.totalNpsc == null)) result = "totalNpsc missing value";
		if ((this.totalNrsc == null && in.totalNrsc != null) || (this.totalNrsc != null && in.totalNrsc == null)) result = "totalNrsc missing value";
		if ((this.totalIncNmea == null && in.totalIncNmea != null) || (this.totalIncNmea != null && in.totalIncNmea == null)) result = "totalIncNmea missing value";
		if ((this.totalRcc == null && in.totalRcc != null) || (this.totalRcc != null && in.totalRcc == null)) result = "totalRcc missing value";
		if ((this.totalRsc == null && in.totalRsc != null) || (this.totalRsc != null && in.totalRsc == null)) result = "totalRsc missing value";
		if ((this.totalRsd == null && in.totalRsd != null) || (this.totalRsd != null && in.totalRsd == null)) result = "totalRsd missing value";
		//if ((this.tte == null && in.tte != null) || (this.tte != null && in.tte == null)) result = "tte missing value";

		//if ((this.vcrp == null && in.vcrp != null) || (this.vcrp != null && in.vcrp == null)) result = "vcrp missing value";
		//if ((this.vcrpk == null && in.vcrpk != null) || (this.vcrpk != null && in.vcrpk == null)) result = "vcrpk missing value";
		//if ((this.vcsc == null && in.vcsc != null) || (this.vcsc != null && in.vcsc == null)) result = "vcsc missing value";
		//if ((this.vcsck == null && in.vcsck != null) || (this.vcsck != null && in.vcsck == null)) result = "vcsck missing value";

		if ((this.facilityRsc == null && in.facilityRsc != null) || (this.facilityRsc != null && in.facilityRsc == null)) result = "facilityRsc missing value";
		if ((this.gmea == null && in.gmea != null) || (this.gmea != null && in.gmea == null)) result = "gmea missing value";
		if ((this.accountingLmea == null && in.accountingLmea != null) || (this.accountingLmea != null && in.accountingLmea == null)) result = "accountingLmea missing value";
		if ((this.opGstLmea == null && in.opGstLmea != null) || (this.opGstLmea != null && in.opGstLmea == null)) result = "opGstLmea missing value";
		if ((this.opGstGmea == null && in.opGstGmea != null) || (this.opGstGmea != null && in.opGstGmea == null)) result = "opGstGmea missing value";

		//if ((this.compFeeTotal == null && in.compFeeTotal != null) || (this.compFeeTotal != null && in.compFeeTotal == null)) result = "compFeeTotal missing value";

		//if ((this.loadFsd == null && in.loadFsd != null) || (this.loadFsd != null && in.loadFsd == null)) result = "loadFsd missing value";
		//if ((this.avcsc == null && in.avcsc != null) || (this.avcsc != null && in.avcsc == null)) result = "avcsc missing value";

		//if ((this.compEmcAdmCap == null && in.compEmcAdmCap != null) || (this.compEmcAdmCap != null && in.compEmcAdmCap == null)) result = "compEmcAdmCap missing value";
		//if ((this.compEmcAdmAdj == null && in.compEmcAdmAdj != null) || (this.compEmcAdmAdj != null && in.compEmcAdmAdj == null)) result = "compEmcAdmAdj missing value";
		//if ((this.fsrp == null && in.fsrp != null) || (this.fsrp != null && in.fsrp == null)) result = "fsrp missing value";
		if ((this.fsrpk == null && in.fsrpk != null) || (this.fsrpk != null && in.fsrpk == null)) result = "fsrpk missing value";
		//if ((this.fssc == null && in.fssc != null) || (this.fssc != null && in.fssc == null)) result = "fssc missing value";
		if ((this.fssck == null && in.fssck != null) || (this.fssck != null && in.fssck == null)) result = "fssck missing value";
		//if ((this.fsscp == null && in.fsscp != null) || (this.fsscp != null && in.fsscp == null)) result = "fsscp missing value";
		//if ((this.fsscn == null && in.fsscn != null) || (this.fsscn != null && in.fsscn == null)) result = "fsscn missing value";
		if ((this.fsscrp == null && in.fsscrp != null) || (this.fsscrp != null && in.fsscrp == null)) result = "fsscrp missing value";
		if ((this.fsscrn == null && in.fsscrn != null) || (this.fsscrn != null && in.fsscrn == null)) result = "fsscrn missing value";
		//if ((this.opGstHeur == null && in.opGstHeur != null) || (this.opGstHeur != null && in.opGstHeur == null)) result = "opGstHeur missing value";
		if ((this.opGstLcsc == null && in.opGstLcsc != null) || (this.opGstLcsc != null && in.opGstLcsc == null)) result = "opGstLcsc missing value";
		//if ((this.opGstMeuc == null && in.opGstMeuc != null) || (this.opGstMeuc != null && in.opGstMeuc == null)) result = "opGstMeuc missing value";
		//if ((this.opGstNsfc == null && in.opGstNsfc != null) || (this.opGstNsfc != null && in.opGstNsfc == null)) result = "opGstNsfc missing value";
		//if ((this.opGstNtsc == null && in.opGstNtsc != null) || (this.opGstNtsc != null && in.opGstNtsc == null)) result = "opGstNtsc missing value";

		//if ((this.emca == null && in.emca != null) || (this.emca != null && in.emca == null)) result = "emca missing value";
		//if ((this.hersa == null && in.hersa != null) || (this.hersa != null && in.hersa == null)) result = "hersa missing value";
		//if ((this.heur == null && in.heur != null) || (this.heur != null && in.heur == null)) result = "heur missing value";
		//if ((this.roundedHlcsa == null && in.roundedHlcsa != null) || (this.roundedHlcsa != null && in.roundedHlcsa == null)) result = "roundedHlcsa missing value";
		//if ((this.hlcu == null && in.hlcu != null) || (this.hlcu != null && in.hlcu == null)) result = "hlcu missing value";

		//if ((this.lcsc == null && in.lcsc != null) || (this.lcsc != null && in.lcsc == null)) result = "lcsc missing value";
		//if ((this.meuc == null && in.meuc != null) || (this.meuc != null && in.meuc == null)) result = "meuc missing value";
		//if ((this.psoa == null && in.psoa != null) || (this.psoa != null && in.psoa == null)) result = "psoa missing value";
		//if ((this.totalHeur == null && in.totalHeur != null) || (this.totalHeur != null && in.totalHeur == null)) result = "totalHeur missing value";
		//if ((this.totalHlcu == null && in.totalHlcu != null) || (this.totalHlcu != null && in.totalHlcu == null)) result = "totalHlcu missing value";
		//if ((this.totalLcsc == null && in.totalLcsc != null) || (this.totalLcsc != null && in.totalLcsc == null)) result = "totalLcsc missing value";
		if ((this.totalLmea == null && in.totalLmea != null) || (this.totalLmea != null && in.totalLmea == null)) result = "totalLmea missing value";

		//if ((this.opGstHersa == null && in.opGstHersa != null) || (this.opGstHersa != null && in.opGstHersa == null)) result = "opGstHersa missing value";
		//if ((this.opGstHlcsa == null && in.opGstHlcsa != null) || (this.opGstHlcsa != null && in.opGstHlcsa == null)) result = "opGstHlcsa missing value";
		//if ((this.ipGstLcsc == null && in.ipGstLcsc != null) || (this.ipGstLcsc != null && in.ipGstLcsc == null)) result = "ipGstLcsc missing value";
		//if ((this.hlcsa == null && in.hlcsa != null) || (this.hlcsa != null && in.hlcsa == null)) result = "hlcsa missing value";

		return result;
	}
	
	public String equal(Period in) {

		String result = null;

		if (this.baqPurchased != null && in.baqPurchased != null) if (this.baqPurchased.compareTo(in.baqPurchased) != 0) result = "baqPurchased value mismatch";
		if (this.baqSold != null && in.baqSold != null) if (this.baqSold.compareTo(in.baqSold) != 0) result = "baqSold value mismatch";
		if (this.beqPurchased != null && in.beqPurchased != null) if (this.beqPurchased.compareTo(in.beqPurchased) != 0) result = "beqPurchased value mismatch";
		if (this.beqSold != null && in.beqSold != null) if (this.beqSold.compareTo(in.beqSold) != 0) result = "beqSold value mismatch";
		if (this.besc != null && in.besc != null) if (this.besc.compareTo(in.besc) != 0) result = "besc value mismatch";
		if (this.bfqPurchased != null && in.bfqPurchased != null) if (this.bfqPurchased.compareTo(in.bfqPurchased) != 0) result = "bfqPurchased value mismatch";
		if (this.bfqSold != null && in.bfqSold != null) if (this.bfqSold.compareTo(in.bfqSold) != 0) result = "bfqSold value mismatch";
		if (this.bifPurchased != null && in.bifPurchased != null) if (this.bifPurchased.compareTo(in.bifPurchased) != 0) result = "bifPurchased value mismatch";
		if (this.bifSold != null && in.bifSold != null) if (this.bifSold.compareTo(in.bifSold) != 0) result = "bifSold value mismatch";
		if (this.bwfPurchased != null && in.bwfPurchased != null) if (this.bwfPurchased.compareTo(in.bwfPurchased) != 0) result = "bwfPurchased value mismatch";
		if (this.bwfSold != null && in.bwfSold != null) if (this.bwfSold.compareTo(in.bwfSold) != 0) result = "bwfSold value mismatch";
		//if (this.deltaWcq != null && in.deltaWcq != null) if (this.deltaWcq.compareTo(in.deltaWcq) != 0) result = "deltaWcq value mismatch";
		//if (this.deltaWeq != null && in.deltaWeq != null) if (this.deltaWeq.compareTo(in.deltaWeq) != 0) result = "deltaWeq value mismatch";
		if (this.compEmca != null && in.compEmca != null) if (this.compEmca.compareTo(in.compEmca) != 0) result = "compEmca value mismatch";
		if (this.compPsoa != null && in.compPsoa != null) if (this.compPsoa.compareTo(in.compPsoa) != 0) result = "compPsoa value mismatch";
		if (this.egaIeq != null && in.egaIeq != null) if (this.egaIeq.compareTo(in.egaIeq) != 0) result = "egaIeq value mismatch";
		if (this.egaWeq != null && in.egaWeq != null) if (this.egaWeq.compareTo(in.egaWeq) != 0) result = "egaWeq value mismatch";
		if (this.egaWpq != null && in.egaWpq != null) if (this.egaWpq.compareTo(in.egaWpq) != 0) result = "egaWpq value mismatch";
		if (this.accountingEua != null && in.accountingEua != null) if (this.accountingEua.compareTo(in.accountingEua) != 0) result = "accountingEua value mismatch";
		if (this.fcc != null && in.fcc != null) if (this.fcc.compareTo(in.fcc) != 0) result = "fcc value mismatch";
		if (this.feq != null && in.feq != null) if (this.feq.compareTo(in.feq) != 0) result = "feq value mismatch";
		if (this.fsc != null && in.fsc != null) if (this.fsc.compareTo(in.fsc) != 0) result = "fsc value mismatch";
		if (this.accountingFsd != null && in.accountingFsd != null) if (this.accountingFsd.compareTo(in.accountingFsd) != 0) result = "accountingFsd value mismatch";
		if (this.gesc != null && in.gesc != null) if (this.gesc.compareTo(in.gesc) != 0) result = "gesc value mismatch";
		if (this.gesce != null && in.gesce != null) if (this.gesce.compareTo(in.gesce) != 0) result = "gesce value mismatch";
		if (this.gescn != null && in.gescn != null) if (this.gescn.compareTo(in.gescn) != 0) result = "gescn value mismatch";
		if (this.gescp != null && in.gescp != null) if (this.gescp.compareTo(in.gescp) != 0) result = "gescp value mismatch";
		if (this.gmee != null && in.gmee != null) if (this.gmee.compareTo(in.gmee) != 0) result = "gmee value mismatch";
		if (this.accountingGmef != null && in.accountingGmef != null) if (this.accountingGmef.compareTo(in.accountingGmef) != 0) result = "accountingGmef value mismatch";
		if (this.heua != null && in.heua != null) if (this.heua.compareTo(in.heua) != 0) result = "heua value mismatch";
		if (this.accountingHeusa != null && in.accountingHeusa != null) if (this.accountingHeusa.compareTo(in.accountingHeusa) != 0) result = "accountingHeusa value mismatch";
		if (this.ieq != null && in.ieq != null) if (this.ieq.compareTo(in.ieq) != 0) result = "ieq value mismatch";
		if (this.ieqp != null && in.ieqp != null) if (this.ieqp.compareTo(in.ieqp) != 0) result = "ieqp value mismatch";
		if (this.impEmca != null && in.impEmca != null) if (this.impEmca.compareTo(in.impEmca) != 0) result = "impEmca value mismatch";
		if (this.impPsoa != null && in.impPsoa != null) if (this.impPsoa.compareTo(in.impPsoa) != 0) result = "impPsoa value mismatch";
		if (this.incNmea != null && in.incNmea != null) if (this.incNmea.compareTo(in.incNmea) != 0) result = "incNmea value mismatch";
		if (this.ipGstFsc != null && in.ipGstFsc != null) if (this.ipGstFsc.compareTo(in.ipGstFsc) != 0) result = "ipGstFsc value mismatch";
		if (this.ipGstGesc != null && in.ipGstGesc != null) if (this.ipGstGesc.compareTo(in.ipGstGesc) != 0) result = "ipGstGesc value mismatch";
		if (this.ipGstGescn != null && in.ipGstGescn != null) if (this.ipGstGescn.compareTo(in.ipGstGescn) != 0) result = "ipGstGescn value mismatch";
		if (this.ipGstGmee != null && in.ipGstGmee != null) if (this.ipGstGmee.compareTo(in.ipGstGmee) != 0) result = "ipGstGmee value mismatch";
		if (this.ipGstIncNmea != null && in.ipGstIncNmea != null) if (this.ipGstIncNmea.compareTo(in.ipGstIncNmea) != 0) result = "ipGstIncNmea value mismatch";
		if (this.ipGstLesd != null && in.ipGstLesd != null) if (this.ipGstLesd.compareTo(in.ipGstLesd) != 0) result = "ipGstLesd value mismatch";
		if (this.ipGstLesdn != null && in.ipGstLesdn != null) if (this.ipGstLesdn.compareTo(in.ipGstLesdn) != 0) result = "ipGstLesdn value mismatch";
		if (this.ipGstNasc != null && in.ipGstNasc != null) if (this.ipGstNasc.compareTo(in.ipGstNasc) != 0) result = "ipGstNasc value mismatch";
		if (this.ipGstNesc != null && in.ipGstNesc != null) if (this.ipGstNesc.compareTo(in.ipGstNesc) != 0) result = "ipGstNesc value mismatch";
		if (this.ipGstNmea != null && in.ipGstNmea != null) if (this.ipGstNmea.compareTo(in.ipGstNmea) != 0) result = "ipGstNmea value mismatch";
		if (this.ipGstNpsc != null && in.ipGstNpsc != null) if (this.ipGstNpsc.compareTo(in.ipGstNpsc) != 0) result = "ipGstNpsc value mismatch";
		if (this.ipGstRsc != null && in.ipGstRsc != null) if (this.ipGstRsc.compareTo(in.ipGstRsc) != 0) result = "ipGstRsc value mismatch";
		if (this.lesd != null && in.lesd != null) if (this.lesd.compareTo(in.lesd) != 0) result = "lesd value mismatch";
		if (this.accountingLesdn != null && in.accountingLesdn != null) if (this.accountingLesdn.compareTo(in.accountingLesdn) != 0) result = "accountingLesdn value mismatch";
		if (this.accountingLesdp != null && in.accountingLesdp != null) if (this.accountingLesdp.compareTo(in.accountingLesdp) != 0) result = "accountingLesdp value mismatch";
		if (this.accountingLmee != null && in.accountingLmee != null) if (this.accountingLmee.compareTo(in.accountingLmee) != 0) result = "accountingLmee value mismatch";
		if (this.accountingLmef != null && in.accountingLmef != null) if (this.accountingLmef.compareTo(in.accountingLmef) != 0) result = "accountingLmef value mismatch";
		if (this.mep != null && in.mep != null) if (this.mep.compareTo(in.mep) != 0) result = "mep value mismatch";
		if (this.accountingMeusa != null && in.accountingMeusa != null) if (this.accountingMeusa.compareTo(in.accountingMeusa) != 0) result = "accountingMeusa value mismatch";

		if (this.nasc != null && in.nasc != null) if (this.nasc.compareTo(in.nasc) != 0) result = "nasc value mismatch";
		if (this.neaa != null && in.neaa != null) if (this.neaa.compareTo(in.neaa) != 0) result = "neaa value mismatch";
		if (this.accountingNead != null && in.accountingNead != null) if (this.accountingNead.compareTo(in.accountingNead) != 0) result = "accountingNead value mismatch";
		if (this.negc != null && in.negc != null) if (this.negc.compareTo(in.negc) != 0) result = "negc value mismatch";
		if (this.negcIeq != null && in.negcIeq != null) if (this.negcIeq.compareTo(in.negcIeq) != 0) result = "negcIeq value mismatch";
		if (this.nelc != null && in.nelc != null) if (this.nelc.compareTo(in.nelc) != 0) result = "nelc value mismatch";
		if (this.nesc != null && in.nesc != null) if (this.nesc.compareTo(in.nesc) != 0) result = "nesc value mismatch";
		if (this.nfsc != null && in.nfsc != null) if (this.nfsc.compareTo(in.nfsc) != 0) result = "nfsc value mismatch";
		if (this.nmea != null && in.nmea != null) if (this.nmea.compareTo(in.nmea) != 0) result = "nmea value mismatch";
		if (this.nodeCount != null && in.nodeCount != null) if (this.nodeCount.compareTo(in.nodeCount) != 0) result = "nodeCount value mismatch";
		if (this.npsc != null && in.npsc != null) if (this.npsc.compareTo(in.npsc) != 0) result = "npsc value mismatch";
		if (this.nrsc != null && in.nrsc != null) if (this.nrsc.compareTo(in.nrsc) != 0) result = "nrsc value mismatch";
		if (this.ntsc != null && in.ntsc != null) if (this.ntsc.compareTo(in.ntsc) != 0) result = "ntsc value mismatch";

		if (this.opGstFsd != null && in.opGstFsd != null) if (this.opGstFsd.compareTo(in.opGstFsd) != 0) result = "opGstFsd value mismatch";
		if (this.opGstGesc != null && in.opGstGesc != null) if (this.opGstGesc.compareTo(in.opGstGesc) != 0) result = "opGstGesc value mismatch";
		if (this.opGstGesce != null && in.opGstGesce != null) if (this.opGstGesce.compareTo(in.opGstGesce) != 0) result = "opGstGesce value mismatch";
		if (this.opGstGescp != null && in.opGstGescp != null) if (this.opGstGescp.compareTo(in.opGstGescp) != 0) result = "opGstGescp value mismatch";
		if (this.opGstGmef != null && in.opGstGmef != null) if (this.opGstGmef.compareTo(in.opGstGmef) != 0) result = "opGstGmef value mismatch";
		if (this.opGstHeusa != null && in.opGstHeusa != null) if (this.opGstHeusa.compareTo(in.opGstHeusa) != 0) result = "opGstHeusa value mismatch";
		if (this.opGstIncNmea != null && in.opGstIncNmea != null) if (this.opGstIncNmea.compareTo(in.opGstIncNmea) != 0) result = "opGstIncNmea value mismatch";
		if (this.opGstLesd != null && in.opGstLesd != null) if (this.opGstLesd.compareTo(in.opGstLesd) != 0) result = "opGstLesd value mismatch";
		if (this.opGstLesdp != null && in.opGstLesdp != null) if (this.opGstLesdp.compareTo(in.opGstLesdp) != 0) result = "opGstLesdp value mismatch";
		if (this.opGstLmee != null && in.opGstLmee != null) if (this.opGstLmee.compareTo(in.opGstLmee) != 0) result = "opGstLmee value mismatch";
		if (this.opGstLmef != null && in.opGstLmef != null) if (this.opGstLmef.compareTo(in.opGstLmef) != 0) result = "opGstLmef value mismatch";
		if (this.opGstMeusa != null && in.opGstMeusa != null) if (this.opGstMeusa.compareTo(in.opGstMeusa) != 0) result = "opGstMeusa value mismatch";
		if (this.opGstNasc != null && in.opGstNasc != null) if (this.opGstNasc.compareTo(in.opGstNasc) != 0) result = "opGstNasc value mismatch";
		if (this.opGstNesc != null && in.opGstNesc != null) if (this.opGstNesc.compareTo(in.opGstNesc) != 0) result = "opGstNesc value mismatch";
		if (this.opGstNmea != null && in.opGstNmea != null) if (this.opGstNmea.compareTo(in.opGstNmea) != 0) result = "opGstNmea value mismatch";
		if (this.opGstNpsc != null && in.opGstNpsc != null) if (this.opGstNpsc.compareTo(in.opGstNpsc) != 0) result = "opGstNpsc value mismatch";
		if (this.opGstRsd != null && in.opGstRsd != null) if (this.opGstRsd.compareTo(in.opGstRsd) != 0) result = "opGstRsd value mismatch";
		if (this.rcc != null && in.rcc != null) if (this.rcc.compareTo(in.rcc) != 0) result = "rcc value mismatch";
		if (this.rsa != null && in.rsa != null) if (this.rsa.compareTo(in.rsa) != 0) result = "rsa value mismatch";
		if (this.rsc != null && in.rsc != null) if (this.rsc.compareTo(in.rsc) != 0) result = "rsc value mismatch";
		if (this.rsd != null && in.rsd != null) if (this.rsd.compareTo(in.rsd) != 0) result = "rsd value mismatch";
		if (this.maxIeq != null && in.maxIeq != null) if (this.maxIeq.compareTo(in.maxIeq) != 0) result = "maxIeq value mismatch";
		if (this.totalBesc != null && in.totalBesc != null) if (this.totalBesc.compareTo(in.totalBesc) != 0) result = "totalBesc value mismatch";
		if (this.totalFsc != null && in.totalFsc != null) if (this.totalFsc.compareTo(in.totalFsc) != 0) result = "totalFsc value mismatch";
		if (this.totalFsd != null && in.totalFsd != null) if (this.totalFsd.compareTo(in.totalFsd) != 0) result = "totalFsd value mismatch";
		if (this.totalGesc != null && in.totalGesc != null) if (this.totalGesc.compareTo(in.totalGesc) != 0) result = "totalGesc value mismatch";
		if (this.totalGesce != null && in.totalGesce != null) if (this.totalGesce.compareTo(in.totalGesce) != 0) result = "totalGesce value mismatch";
		if (this.totalGescn != null && in.totalGescn != null) if (this.totalGescn.compareTo(in.totalGescn) != 0) result = "totalGescn value mismatch";
		if (this.totalGescp != null && in.totalGescp != null) if (this.totalGescp.compareTo(in.totalGescp) != 0) result = "totalGescp value mismatch";
		if (this.totalGmee != null && in.totalGmee != null) if (this.totalGmee.compareTo(in.totalGmee) != 0) result = "totalGmee value mismatch";
		if (this.totalGmef != null && in.totalGmef != null) if (this.totalGmef.compareTo(in.totalGmef) != 0) result = "totalGmef value mismatch";
		if (this.totalHeusa != null && in.totalHeusa != null) if (this.totalHeusa.compareTo(in.totalHeusa) != 0) result = "totalHeusa value mismatch";
		if (this.totalLesd != null && in.totalLesd != null) if (this.totalLesd.compareTo(in.totalLesd) != 0) result = "totalLesd value mismatch";
		if (this.totalLesdn != null && in.totalLesdn != null) if (this.totalLesdn.compareTo(in.totalLesdn) != 0) result = "totalLesdn value mismatch";
		if (this.totalLesdp != null && in.totalLesdp != null) if (this.totalLesdp.compareTo(in.totalLesdp) != 0) result = "totalLesdp value mismatch";
		if (this.totalLmee != null && in.totalLmee != null) if (this.totalLmee.compareTo(in.totalLmee) != 0) result = "totalLmee value mismatch";
		if (this.totalLmef != null && in.totalLmef != null) if (this.totalLmef.compareTo(in.totalLmef) != 0) result = "totalLmef value mismatch";
		if (this.totalMeusa != null && in.totalMeusa != null) if (this.totalMeusa.compareTo(in.totalMeusa) != 0) result = "totalMeusa value mismatch";
		if (this.totalNasc != null && in.totalNasc != null) if (this.totalNasc.compareTo(in.totalNasc) != 0) result = "totalNasc value mismatch";
		if (this.totalNesc != null && in.totalNesc != null) if (this.totalNesc.compareTo(in.totalNesc) != 0) result = "totalNesc value mismatch";
		if (this.totalNfsc != null && in.totalNfsc != null) if (this.totalNfsc.compareTo(in.totalNfsc) != 0) result = "totalNfsc value mismatch";
		if (this.totalNmea != null && in.totalNmea != null) if (this.totalNmea.compareTo(in.totalNmea) != 0) result = "totalNmea value mismatch";
		if (this.totalNpsc != null && in.totalNpsc != null) if (this.totalNpsc.compareTo(in.totalNpsc) != 0) result = "totalNpsc value mismatch";
		if (this.totalNrsc != null && in.totalNrsc != null) if (this.totalNrsc.compareTo(in.totalNrsc) != 0) result = "totalNrsc value mismatch";
		if (this.totalIncNmea != null && in.totalIncNmea != null) if (this.totalIncNmea.compareTo(in.totalIncNmea) != 0) result = "totalIncNmea value mismatch";
		if (this.totalRcc != null && in.totalRcc != null) if (this.totalRcc.compareTo(in.totalRcc) != 0) result = "totalRcc value mismatch";
		if (this.totalRsc != null && in.totalRsc != null) if (this.totalRsc.compareTo(in.totalRsc) != 0) result = "totalRsc value mismatch";
		if (this.totalRsd != null && in.totalRsd != null) if (this.totalRsd.compareTo(in.totalRsd) != 0) result = "totalRsd value mismatch";
		if (this.tte != null && in.tte != null) if (this.tte.compareTo(in.tte) != 0) result = "tte value mismatch";

		if (this.vcrp != null && in.vcrp != null) if (this.vcrp.compareTo(in.vcrp) != 0) result = "vcrp value mismatch";
		if (this.vcrpk != null && in.vcrpk != null) if (this.vcrpk.compareTo(in.vcrpk) != 0) result = "vcrpk value mismatch";
		if (this.vcsc != null && in.vcsc != null) if (this.vcsc.compareTo(in.vcsc) != 0) result = "vcsc value mismatch";
		if (this.vcsck != null && in.vcsck != null) if (this.vcsck.compareTo(in.vcsck) != 0) result = "vcsck value mismatch";

		if (this.facilityRsc != null && in.facilityRsc != null) if (this.facilityRsc.compareTo(in.facilityRsc) != 0) result = "facilityRsc value mismatch";
		if (this.gmea != null && in.gmea != null) if (this.gmea.compareTo(in.gmea) != 0) result = "gmea value mismatch";
		if (this.accountingLmea != null && in.accountingLmea != null) if (this.accountingLmea.compareTo(in.accountingLmea) != 0) result = "accountingLmea value mismatch";
		if (this.opGstLmea != null && in.opGstLmea != null) if (this.opGstLmea.compareTo(in.opGstLmea) != 0) result = "opGstLmea value mismatch";
		if (this.opGstGmea != null && in.opGstGmea != null) if (this.opGstGmea.compareTo(in.opGstGmea) != 0) result = "opGstGmea value mismatch";

		if (this.compFeeTotal != null && in.compFeeTotal != null) if (this.compFeeTotal.compareTo(in.compFeeTotal) != 0) result = "compFeeTotal value mismatch";

		if (this.loadFsd != null && in.loadFsd != null) if (this.loadFsd.compareTo(in.loadFsd) != 0) result = "loadFsd value mismatch";
		if (this.avcsc != null && in.avcsc != null) if (this.avcsc.compareTo(in.avcsc) != 0) result = "avcsc value mismatch";

		if (this.compEmcAdmCap != null && in.compEmcAdmCap != null) if (this.compEmcAdmCap.compareTo(in.compEmcAdmCap) != 0) result = "compEmcAdmCap value mismatch";
		if (this.compEmcAdmAdj != null && in.compEmcAdmAdj != null) if (this.compEmcAdmAdj.compareTo(in.compEmcAdmAdj) != 0) result = "compEmcAdmAdj value mismatch";
		if (this.fsrp != null && in.fsrp != null) if (this.fsrp.compareTo(in.fsrp) != 0) result = "fsrp value mismatch";
		if (this.fsrpk != null && in.fsrpk != null) if (this.fsrpk.compareTo(in.fsrpk) != 0) result = "fsrpk value mismatch";
		if (this.fssc != null && in.fssc != null) if (this.fssc.compareTo(in.fssc) != 0) result = "fssc value mismatch";
		if (this.fssck != null && in.fssck != null) if (this.fssck.compareTo(in.fssck) != 0) result = "fssck value mismatch";
		if (this.fsscp != null && in.fsscp != null) if (this.fsscp.compareTo(in.fsscp) != 0) result = "fsscp value mismatch";
		if (this.fsscn != null && in.fsscn != null) if (this.fsscn.compareTo(in.fsscn) != 0) result = "fsscn value mismatch";
		if (this.fsscrp != null && in.fsscrp != null) if (this.fsscrp.compareTo(in.fsscrp) != 0) result = "fsscrp value mismatch";
		if (this.fsscrn != null && in.fsscrn != null) if (this.fsscrn.compareTo(in.fsscrn) != 0) result = "fsscrn value mismatch";
		if (this.opGstHeur != null && in.opGstHeur != null) if (this.opGstHeur.compareTo(in.opGstHeur) != 0) result = "opGstHeur value mismatch";
		if (this.opGstLcsc != null && in.opGstLcsc != null) if (this.opGstLcsc.compareTo(in.opGstLcsc) != 0) result = "opGstLcsc value mismatch";
		if (this.opGstMeuc != null && in.opGstMeuc != null) if (this.opGstMeuc.compareTo(in.opGstMeuc) != 0) result = "opGstMeuc value mismatch";
		if (this.opGstNsfc != null && in.opGstNsfc != null) if (this.opGstNsfc.compareTo(in.opGstNsfc) != 0) result = "opGstNsfc value mismatch";
		if (this.opGstNtsc != null && in.opGstNtsc != null) if (this.opGstNtsc.compareTo(in.opGstNtsc) != 0) result = "opGstNtsc value mismatch";

		if (this.emca != null && in.emca != null) if (this.emca.compareTo(in.emca) != 0) result = "emca value mismatch";
		if (this.hersa != null && in.hersa != null) if (this.hersa.compareTo(in.hersa) != 0) result = "hersa value mismatch";
		if (this.heur != null && in.heur != null) if (this.heur.compareTo(in.heur) != 0) result = "heur value mismatch";
		if (this.roundedHlcsa != null && in.roundedHlcsa != null) if (this.roundedHlcsa.compareTo(in.roundedHlcsa) != 0) result = "roundedHlcsa value mismatch";
		if (this.hlcu != null && in.hlcu != null) if (this.hlcu.compareTo(in.hlcu) != 0) result = "hlcu value mismatch";

		if (this.lcsc != null && in.lcsc != null) if (this.lcsc.compareTo(in.lcsc) != 0) result = "lcsc value mismatch";
		if (this.meuc != null && in.meuc != null) if (this.meuc.compareTo(in.meuc) != 0) result = "meuc value mismatch";
		if (this.psoa != null && in.psoa != null) if (this.psoa.compareTo(in.psoa) != 0) result = "psoa value mismatch";
		if (this.totalHeur != null && in.totalHeur != null) if (this.totalHeur.compareTo(in.totalHeur) != 0) result = "totalHeur value mismatch";
		if (this.totalHlcu != null && in.totalHlcu != null) if (this.totalHlcu.compareTo(in.totalHlcu) != 0) result = "totalHlcu value mismatch";
		if (this.totalLcsc != null && in.totalLcsc != null) if (this.totalLcsc.compareTo(in.totalLcsc) != 0) result = "totalLcsc value mismatch";
		if (this.totalLmea != null && in.totalLmea != null) if (this.totalLmea.compareTo(in.totalLmea) != 0) result = "totalLmea value mismatch";

		if (this.opGstHersa != null && in.opGstHersa != null) if (this.opGstHersa.compareTo(in.opGstHersa) != 0) result = "opGstHersa value mismatch";
		if (this.opGstHlcsa != null && in.opGstHlcsa != null) if (this.opGstHlcsa.compareTo(in.opGstHlcsa) != 0) result = "opGstHlcsa value mismatch";
		if (this.ipGstLcsc != null && in.ipGstLcsc != null) if (this.ipGstLcsc.compareTo(in.ipGstLcsc) != 0) result = "ipGstLcsc value mismatch";
		//if (this.hlcsa != null && in.hlcsa != null) if (this.hlcsa.compareTo(in.hlcsa) != 0) result = "hlcsa value mismatch";
		if (this.genFsd != null && in.genFsd != null) if (this.genFsd.compareTo(in.genFsd) != 0) result = "genFsd value mismatch";

		return result;
	}
	
	public String toInputString() {
		
		String result = (this.accountId != null? this.accountId: "") + "," +
				(this.baqPurchased != null? this.baqPurchased.toString(): "") + "," +
				(this.baqSold != null? this.baqSold.toString(): "") + "," +
				(this.beqPurchased != null? this.beqPurchased.toString(): "") + "," +
				(this.beqSold != null? this.beqSold.toString(): "") + "," +
				(this.besc != null? this.besc.toString(): "") + "," +
				(this.bfqPurchased != null? this.bfqPurchased.toString(): "") + "," +
				(this.bfqSold != null? this.bfqSold.toString(): "") + "," +
				(this.bifPurchased != null? this.bifPurchased.toString(): "") + "," +
				(this.bifSold != null? this.bifSold.toString(): "") + "," +
				(this.bwfPurchased != null? this.bwfPurchased.toString(): "") + "," +
				(this.bwfSold != null? this.bwfSold.toString(): "") + "," +
				(this.deltaWcq != null? this.deltaWcq.toString(): "") + "," +
				(this.deltaWeq != null? this.deltaWeq.toString(): "") + "," +
				(this.compEmca != null? this.compEmca.toString(): "") + "," +
				(this.compPsoa != null? this.compPsoa.toString(): "") + "," +
				(this.egaIeq != null? this.egaIeq.toString(): "") + "," +
				(this.egaWeq != null? this.egaWeq.toString(): "") + "," +
				(this.egaWpq != null? this.egaWpq.toString(): "") + "," +
				(this.eua != null? this.eua.toString(): "") + "," +
				(this.fcc != null? this.fcc.toString(): "") + "," +
				(this.feq != null? this.feq.toString(): "") + "," +
				(this.fsc != null? this.fsc.toString(): "") + "," +
				(this.fsd != null? this.fsd.toString(): "") + "," +
				(this.gesc != null? this.gesc.toString(): "") + "," +
				(this.gesce != null? this.gesce.toString(): "") + "," +
				(this.gescn != null? this.gescn.toString(): "") + "," +
				(this.gescp != null? this.gescp.toString(): "") + "," +
				(this.gmee != null? this.gmee.toString(): "") + "," +
				(this.gmef != null? this.gmef.toString(): "") + "," +
				(this.heua != null? this.heua.toString(): "") + "," +
				(this.heusa != null? this.heusa.toString(): "") + "," +
				(this.ieq != null? this.ieq.toString(): "") + "," +
				(this.ieqp != null? this.ieqp.toString(): "") + "," +
				(this.impEmca != null? this.impEmca.toString(): "") + "," +
				(this.impPsoa != null? this.impPsoa.toString(): "") + "," +
				(this.incNmea != null? this.incNmea.toString(): "") + "," +
				(this.ipGstFsc != null? this.ipGstFsc.toString(): "") + "," +
				(this.ipGstGesc != null? this.ipGstGesc.toString(): "") + "," +
				(this.ipGstGescn != null? this.ipGstGescn.toString(): "") + "," +
				(this.ipGstGmee != null? this.ipGstGmee.toString(): "") + "," +
				(this.ipGstIncNmea != null? this.ipGstIncNmea.toString(): "") + "," +
				(this.ipGstLesd != null? this.ipGstLesd.toString(): "") + "," +
				(this.ipGstLesdn != null? this.ipGstLesdn.toString(): "") + "," +
				(this.ipGstNasc != null? this.ipGstNasc.toString(): "") + "," +
				(this.ipGstNesc != null? this.ipGstNesc.toString(): "") + "," +
				(this.ipGstNmea != null? this.ipGstNmea.toString(): "") + "," +
				(this.ipGstNpsc != null? this.ipGstNpsc.toString(): "") + "," +
				(this.ipGstRsc != null? this.ipGstRsc.toString(): "") + "," +
				(this.lesd != null? this.lesd.toString(): "") + "," +
				(this.lesdn != null? this.lesdn.toString(): "") + "," +
				(this.lesdp != null? this.lesdp.toString(): "") + "," +
				(this.lmee != null? this.lmee.toString(): "") + "," +
				(this.lmef != null? this.lmef.toString(): "") + "," +
				(this.mep != null? this.mep.toString(): "") + "," +
				(this.meusa != null? this.meusa.toString(): "") + "," +
				(this.mfp != null? this.mfp.toString(): "") + "," +
				(this.mrp != null? this.mrp.toString(): "") + "," +
				(this.nasc != null? this.nasc.toString(): "") + "," +
				(this.neaa != null? this.neaa.toString(): "") + "," +
				(this.nead != null? this.nead.toString(): "") + "," +
				(this.negc != null? this.negc.toString(): "") + "," +
				(this.negcIeq != null? this.negcIeq.toString(): "") + "," +
				(this.nelc != null? this.nelc.toString(): "") + "," +
				(this.nesc != null? this.nesc.toString(): "") + "," +
				(this.nfsc != null? this.nfsc.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				(this.nodeCount != null? this.nodeCount.toString(): "") + "," +
				(this.npsc != null? this.npsc.toString(): "") + "," +
				(this.nrsc != null? this.nrsc.toString(): "") + "," +
				(this.ntsc != null? this.ntsc.toString(): "") + "," +
				this.periodId + "," +
				(this.opGstFsd != null? this.opGstFsd.toString(): "") + "," +
				(this.opGstGesc != null? this.opGstGesc.toString(): "") + "," +
				(this.opGstGesce != null? this.opGstGesce.toString(): "") + "," +
				(this.opGstGescp != null? this.opGstGescp.toString(): "") + "," +
				(this.opGstGmef != null? this.opGstGmef.toString(): "") + "," +
				(this.opGstHeusa != null? this.opGstHeusa.toString(): "") + "," +
				(this.opGstIncNmea != null? this.opGstIncNmea.toString(): "") + "," +
				(this.opGstLesd != null? this.opGstLesd.toString(): "") + "," +
				(this.opGstLesdp != null? this.opGstLesdp.toString(): "") + "," +
				(this.opGstLmee != null? this.opGstLmee.toString(): "") + "," +
				(this.opGstLmef != null? this.opGstLmef.toString(): "") + "," +
				(this.opGstMeusa != null? this.opGstMeusa.toString(): "") + "," +
				(this.opGstNasc != null? this.opGstNasc.toString(): "") + "," +
				(this.opGstNesc != null? this.opGstNesc.toString(): "") + "," +
				(this.opGstNmea != null? this.opGstNmea.toString(): "") + "," +
				(this.opGstNpsc != null? this.opGstNpsc.toString(): "") + "," +
				(this.opGstRsd != null? this.opGstRsd.toString(): "") + "," +
				(this.rcc != null? this.rcc.toString(): "") + "," +
				(this.rsa != null? this.rsa.toString(): "") + "," +
				(this.rsc != null? this.rsc.toString(): "") + "," +
				(this.rsd != null? this.rsd.toString(): "") + "," +
				(this.maxIeq != null? this.maxIeq.toString(): "") + "," +
				(this.totalBesc != null? this.totalBesc.toString(): "") + "," +
				(this.totalFsc != null? this.totalFsc.toString(): "") + "," +
				(this.totalFsd != null? this.totalFsd.toString(): "") + "," +
				(this.totalGesc != null? this.totalGesc.toString(): "") + "," +
				(this.totalGesce != null? this.totalGesce.toString(): "") + "," +
				(this.totalGescn != null? this.totalGescn.toString(): "") + "," +
				(this.totalGescp != null? this.totalGescp.toString(): "") + "," +
				(this.totalGmee != null? this.totalGmee.toString(): "") + "," +
				(this.totalGmef != null? this.totalGmef.toString(): "") + "," +
				(this.totalHeusa != null? this.totalHeusa.toString(): "") + "," +
				(this.totalLesd != null? this.totalLesd.toString(): "") + "," +
				(this.totalLesdn != null? this.totalLesdn.toString(): "") + "," +
				(this.totalLesdp != null? this.totalLesdp.toString(): "") + "," +
				(this.totalLmee != null? this.totalLmee.toString(): "") + "," +
				(this.totalLmef != null? this.totalLmef.toString(): "") + "," +
				(this.totalMeusa != null? this.totalMeusa.toString(): "") + "," +
				(this.totalNasc != null? this.totalNasc.toString(): "") + "," +
				(this.totalNesc != null? this.totalNesc.toString(): "") + "," +
				(this.totalNfsc != null? this.totalNfsc.toString(): "") + "," +
				(this.totalNmea != null? this.totalNmea.toString(): "") + "," +
				(this.totalNpsc != null? this.totalNpsc.toString(): "") + "," +
				(this.totalNrsc != null? this.totalNrsc.toString(): "") + "," +
				(this.totalIncNmea != null? this.totalIncNmea.toString(): "") + "," +
				(this.totalRcc != null? this.totalRcc.toString(): "") + "," +
				(this.totalRsc != null? this.totalRsc.toString(): "") + "," +
				(this.totalRsd != null? this.totalRsd.toString(): "") + "," +
				(this.tte != null? this.tte.toString(): "") + "," +
				(this.usep != null? this.usep.toString(): "") + "," +
				(this.vcrp != null? this.vcrp.toString(): "") + "," +
				(this.vcrpk != null? this.vcrpk.toString(): "") + "," +
				(this.vcsc != null? this.vcsc.toString(): "") + "," +
				(this.vcsck != null? this.vcsck.toString(): "") + "," +
				(this.wcq != null? this.wcq.toString(): "") + "," +
				(this.weq != null? this.weq.toString(): "") + "," +
				(this.wpq != null? this.wpq.toString(): "") + "," +
				(this.facilityRsc != null? this.facilityRsc.toString(): "") + "," +
				(this.gmea != null? this.gmea.toString(): "") + "," +				
				(this.lmea != null? this.lmea.toString(): "") + "," +
				(this.opGstLmea != null? this.opGstLmea.toString(): "") + "," +
				(this.opGstGmea != null? this.opGstGmea.toString(): "") + "," +
				this.egaId + "," +
				(this.compFeeTotal != null? this.compFeeTotal.toString(): "") + "," +
				(this.impAfp != null? this.impAfp.toString(): "") + "," +
				(this.impHeuc != null? this.impHeuc.toString(): "") + "," +
				(this.impMeuc != null? this.impMeuc.toString(): "") + "," +
				(this.impUsep != null? this.impUsep.toString(): "") + "," +
				(this.wfq != null? this.wfq.toString(): "") + "," +
				(this.wmq != null? this.wmq.toString(): "") + "," +
				(this.deltaWmq != null? this.deltaWmq.toString(): "") + "," +
				(this.deltaWfq != null? this.deltaWfq.toString(): "") + "," +
				(this.genNode == true? "1": "0") + "," +
				(this.deltaWdq != null? this.deltaWdq.toString(): "") + "," +
				(this.lcp != null? this.lcp.toString(): "") + "," +
				(this.wdq != null? this.wdq.toString(): "") + "," +
				(this.impHeur != null? this.impHeur.toString(): "") + "," +
				(this.impHlcu != null? this.impHlcu.toString(): "");
		
		return result;
	}

	public String toOutputString() {
		
		String result = (this.accountId != null? this.accountId: "") + "," +
				(this.baqPurchased != null? this.baqPurchased.toString(): "") + "," +
				(this.baqSold != null? this.baqSold.toString(): "") + "," +
				(this.beqPurchased != null? this.beqPurchased.toString(): "") + "," +
				(this.beqSold != null? this.beqSold.toString(): "") + "," +
				(this.besc != null? this.besc.toString(): "") + "," +
				(this.bfqPurchased != null? this.bfqPurchased.toString(): "") + "," +
				(this.bfqSold != null? this.bfqSold.toString(): "") + "," +
				(this.bifPurchased != null? this.bifPurchased.toString(): "") + "," +
				(this.bifSold != null? this.bifSold.toString(): "") + "," +
				(this.bwfPurchased != null? this.bwfPurchased.toString(): "") + "," +
				(this.bwfSold != null? this.bwfSold.toString(): "") + "," +
				(this.deltaWcq != null? this.deltaWcq.toString(): "") + "," +
				(this.deltaWeq != null? this.deltaWeq.toString(): "") + "," +
				(this.compEmca != null? this.compEmca.toString(): "") + "," +
				(this.compPsoa != null? this.compPsoa.toString(): "") + "," +
				(this.egaIeq != null? this.egaIeq.toString(): "") + "," +
				(this.egaWeq != null? this.egaWeq.toString(): "") + "," +
				(this.egaWpq != null? this.egaWpq.toString(): "") + "," +
				(this.accountingEua != null? this.accountingEua.toString(): "") + "," +
				(this.fcc != null? this.fcc.toString(): "") + "," +
				(this.feq != null? this.feq.toString(): "") + "," +
				(this.fsc != null? this.fsc.toString(): "") + "," +
				(this.accountingFsd != null? this.accountingFsd.toString(): "") + "," +
				(this.gesc != null? this.gesc.toString(): "") + "," +
				(this.gesce != null? this.gesce.toString(): "") + "," +
				(this.gescn != null? this.gescn.toString(): "") + "," +
				(this.gescp != null? this.gescp.toString(): "") + "," +
				(this.gmee != null? this.gmee.toString(): "") + "," +
				(this.accountingGmef != null? this.accountingGmef.toString(): "") + "," +
				(this.heua != null? this.heua.toString(): "") + "," +
				(this.accountingHeusa != null? this.accountingHeusa.toString(): "") + "," +
				(this.ieq != null? this.ieq.toString(): "") + "," +
				(this.ieqp != null? this.ieqp.toString(): "") + "," +
				(this.impEmca != null? this.impEmca.toString(): "") + "," +
				(this.impPsoa != null? this.impPsoa.toString(): "") + "," +
				(this.incNmea != null? this.incNmea.toString(): "") + "," +
				(this.ipGstFsc != null? this.ipGstFsc.toString(): "") + "," +
				(this.ipGstGesc != null? this.ipGstGesc.toString(): "") + "," +
				(this.ipGstGescn != null? this.ipGstGescn.toString(): "") + "," +
				(this.ipGstGmee != null? this.ipGstGmee.toString(): "") + "," +
				(this.ipGstIncNmea != null? this.ipGstIncNmea.toString(): "") + "," +
				(this.ipGstLesd != null? this.ipGstLesd.toString(): "") + "," +
				(this.ipGstLesdn != null? this.ipGstLesdn.toString(): "") + "," +
				(this.ipGstNasc != null? this.ipGstNasc.toString(): "") + "," +
				(this.ipGstNesc != null? this.ipGstNesc.toString(): "") + "," +
				(this.ipGstNmea != null? this.ipGstNmea.toString(): "") + "," +
				(this.ipGstNpsc != null? this.ipGstNpsc.toString(): "") + "," +
				(this.ipGstRsc != null? this.ipGstRsc.toString(): "") + "," +
				(this.lesd != null? this.lesd.toString(): "") + "," +
				(this.accountingLesdn != null? this.accountingLesdn.toString(): "") + "," +
				(this.accountingLesdp != null? this.accountingLesdp.toString(): "") + "," +
				(this.accountingLmee != null? this.accountingLmee.toString(): "") + "," +
				(this.accountingLmef != null? this.accountingLmef.toString(): "") + "," +
				(this.mep != null? this.mep.toString(): "") + "," +
				(this.accountingMeusa != null? this.accountingMeusa.toString(): "") + "," +
				(this.mfp != null? this.mfp.toString(): "") + "," +
				(this.mrp != null? this.mrp.toString(): "") + "," +
				(this.nasc != null? this.nasc.toString(): "") + "," +
				(this.neaa != null? this.neaa.toString(): "") + "," +
				(this.accountingNead != null? this.accountingNead.toString(): "") + "," +
				(this.negc != null? this.negc.toString(): "") + "," +
				(this.negcIeq != null? this.negcIeq.toString(): "") + "," +
				(this.nelc != null? this.nelc.toString(): "") + "," +
				(this.nesc != null? this.nesc.toString(): "") + "," +
				(this.nfsc != null? this.nfsc.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				(this.nodeCount != null? this.nodeCount.toString(): "") + "," +
				(this.npsc != null? this.npsc.toString(): "") + "," +
				(this.nrsc != null? this.nrsc.toString(): "") + "," +
				(this.ntsc != null? this.ntsc.toString(): "") + "," +
				this.periodId + "," +
				(this.opGstFsd != null? this.opGstFsd.toString(): "") + "," +
				(this.opGstGesc != null? this.opGstGesc.toString(): "") + "," +
				(this.opGstGesce != null? this.opGstGesce.toString(): "") + "," +
				(this.opGstGescp != null? this.opGstGescp.toString(): "") + "," +
				(this.opGstGmef != null? this.opGstGmef.toString(): "") + "," +
				(this.opGstHeusa != null? this.opGstHeusa.toString(): "") + "," +
				(this.opGstIncNmea != null? this.opGstIncNmea.toString(): "") + "," +
				(this.opGstLesd != null? this.opGstLesd.toString(): "") + "," +
				(this.opGstLesdp != null? this.opGstLesdp.toString(): "") + "," +
				(this.opGstLmee != null? this.opGstLmee.toString(): "") + "," +
				(this.opGstLmef != null? this.opGstLmef.toString(): "") + "," +
				(this.opGstMeusa != null? this.opGstMeusa.toString(): "") + "," +
				(this.opGstNasc != null? this.opGstNasc.toString(): "") + "," +
				(this.opGstNesc != null? this.opGstNesc.toString(): "") + "," +
				(this.opGstNmea != null? this.opGstNmea.toString(): "") + "," +
				(this.opGstNpsc != null? this.opGstNpsc.toString(): "") + "," +
				(this.opGstRsd != null? this.opGstRsd.toString(): "") + "," +
				(this.rcc != null? this.rcc.toString(): "") + "," +
				(this.rsa != null? this.rsa.toString(): "") + "," +
				(this.rsc != null? this.rsc.toString(): "") + "," +
				(this.rsd != null? this.rsd.toString(): "") + "," +
				(this.maxIeq != null? this.maxIeq.toString(): "") + "," +
				(this.totalBesc != null? this.totalBesc.toString(): "") + "," +
				(this.totalFsc != null? this.totalFsc.toString(): "") + "," +
				(this.totalFsd != null? this.totalFsd.toString(): "") + "," +
				(this.totalGesc != null? this.totalGesc.toString(): "") + "," +
				(this.totalGesce != null? this.totalGesce.toString(): "") + "," +
				(this.totalGescn != null? this.totalGescn.toString(): "") + "," +
				(this.totalGescp != null? this.totalGescp.toString(): "") + "," +
				(this.totalGmee != null? this.totalGmee.toString(): "") + "," +
				(this.totalGmef != null? this.totalGmef.toString(): "") + "," +
				(this.totalHeusa != null? this.totalHeusa.toString(): "") + "," +
				(this.totalLesd != null? this.totalLesd.toString(): "") + "," +
				(this.totalLesdn != null? this.totalLesdn.toString(): "") + "," +
				(this.totalLesdp != null? this.totalLesdp.toString(): "") + "," +
				(this.totalLmee != null? this.totalLmee.toString(): "") + "," +
				(this.totalLmef != null? this.totalLmef.toString(): "") + "," +
				(this.totalMeusa != null? this.totalMeusa.toString(): "") + "," +
				(this.totalNasc != null? this.totalNasc.toString(): "") + "," +
				(this.totalNesc != null? this.totalNesc.toString(): "") + "," +
				(this.totalNfsc != null? this.totalNfsc.toString(): "") + "," +
				(this.totalNmea != null? this.totalNmea.toString(): "") + "," +
				(this.totalNpsc != null? this.totalNpsc.toString(): "") + "," +
				(this.totalNrsc != null? this.totalNrsc.toString(): "") + "," +
				(this.totalIncNmea != null? this.totalIncNmea.toString(): "") + "," +
				(this.totalRcc != null? this.totalRcc.toString(): "") + "," +
				(this.totalRsc != null? this.totalRsc.toString(): "") + "," +
				(this.totalRsd != null? this.totalRsd.toString(): "") + "," +
				(this.tte != null? this.tte.toString(): "") + "," +
				(this.usep != null? this.usep.toString(): "") + "," +
				(this.vcrp != null? this.vcrp.toString(): "") + "," +
				(this.vcrpk != null? this.vcrpk.toString(): "") + "," +
				(this.vcsc != null? this.vcsc.toString(): "") + "," +
				(this.vcsck != null? this.vcsck.toString(): "") + "," +
				(this.wcq != null? this.wcq.toString(): "") + "," +
				(this.weq != null? this.weq.toString(): "") + "," +
				(this.wpq != null? this.wpq.toString(): "") + "," +
				(this.facilityRsc != null? this.facilityRsc.toString(): "") + "," +
				(this.gmea != null? this.gmea.toString(): "") + "," +				
				(this.accountingLmea != null? this.accountingLmea.toString(): "") + "," +
				(this.opGstLmea != null? this.opGstLmea.toString(): "") + "," +
				(this.opGstGmea != null? this.opGstGmea.toString(): "") + "," +
				this.egaId + "," +
				(this.compFeeTotal != null? this.compFeeTotal.toString(): "") + "," +
				(this.impAfp != null? this.impAfp.toString(): "") + "," +
				(this.impHeuc != null? this.impHeuc.toString(): "") + "," +
				(this.impMeuc != null? this.impMeuc.toString(): "") + "," +
				(this.impUsep != null? this.impUsep.toString(): "") + "," +
				(this.loadFsd != null? this.loadFsd.toString(): "") + "," +
				(this.avcsc != null? this.avcsc.toString(): "") + "," +
				(this.wfq != null? this.wfq.toString(): "") + "," +
				(this.wmq != null? this.wmq.toString(): "") + "," +
				(this.deltaWmq != null? this.deltaWmq.toString(): "") + "," +
				(this.deltaWfq != null? this.deltaWfq.toString(): "") + "," +
				(this.compEmcAdmCap != null? this.compEmcAdmCap.toString(): "") + "," +
				(this.compEmcAdmAdj != null? this.compEmcAdmAdj.toString(): "") + "," +
				(this.fsrp != null? this.fsrp.toString(): "") + "," +
				(this.fsrpk != null? this.fsrpk.toString(): "") + "," +
				(this.fssc != null? this.fssc.toString(): "") + "," +
				(this.fssck != null? this.fssck.toString(): "") + "," +
				(this.fsscp != null? this.fsscp.toString(): "") + "," +
				(this.fsscn != null? this.fsscn.toString(): "") + "," +
				(this.fsscrp != null? this.fsscrp.toString(): "") + "," +
				(this.fsscrn != null? this.fsscrn.toString(): "") + "," +
				(this.opGstHeur != null? this.opGstHeur.toString(): "") + "," +
				(this.opGstLcsc != null? this.opGstLcsc.toString(): "") + "," +
				(this.opGstMeuc != null? this.opGstMeuc.toString(): "") + "," +
				(this.opGstNsfc != null? this.opGstNsfc.toString(): "") + "," +
				(this.opGstNtsc != null? this.opGstNtsc.toString(): "") + "," +
				(this.deltaWdq != null? this.deltaWdq.toString(): "") + "," +
				(this.emca != null? this.emca.toString(): "") + "," +
				(this.hersa != null? this.hersa.toString(): "") + "," +
				(this.heur != null? this.heur.toString(): "") + "," +
				(this.roundedHlcsa != null? this.roundedHlcsa.toString(): "") + "," +
				(this.hlcu != null? this.hlcu.toString(): "") + "," +
				(this.lcp != null? this.lcp.toString(): "") + "," +
				(this.lcsc != null? this.lcsc.toString(): "") + "," +
				(this.meuc != null? this.meuc.toString(): "") + "," +
				(this.psoa != null? this.psoa.toString(): "") + "," +
				(this.totalHeur != null? this.totalHeur.toString(): "") + "," +
				(this.totalHlcu != null? this.totalHlcu.toString(): "") + "," +
				(this.totalLcsc != null? this.totalLcsc.toString(): "") + "," +
				(this.totalLmea != null? this.totalLmea.toString(): "") + "," +
				(this.wdq != null? this.wdq.toString(): "") + "," +
				(this.genNode == true? "1": "0") + "," +
				(this.impHeur != null? this.impHeur.toString(): "") + "," +
				(this.impHlcu != null? this.impHlcu.toString(): "") + "," +
				(this.opGstHersa != null? this.opGstHersa.toString(): "") + "," +
				(this.opGstHlcsa != null? this.opGstHlcsa.toString(): "") + "," +
				(this.ipGstLcsc != null? this.ipGstLcsc.toString(): "") + "," +
				(this.hlcsa != null? this.hlcsa.toString(): "") + "," +
				(this.genFsd != null? this.genFsd.toString(): "");
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"accountId," +
			"baqPurchased," +
			"baqSold," +
			"beqPurchased," +
			"beqSold," +
			"besc," +
			"bfqPurchased," +
			"bfqSold," +
			"bifPurchased," +
			"bifSold," +
			"bwfPurchased," +
			"bwfSold," +
			"deltaWcq," +
			"deltaWeq," +
			"compEmca," +
			"compPsoa," +
			"egaIeq," +
			"egaWeq," +
			"egaWpq," +
			"eua," +
			"fcc," +
			"feq," +
			"fsc," +
			"fsd," +
			"gesc," +
			"gesce," +
			"gescn," +
			"gescp," +
			"gmee," +
			"gmef," +
			"heua," +
			"heusa," +
			"ieq," +
			"ieqp," +
			"impEmca," +
			"impPsoa," +
			"incNmea," +
			"ipGstFsc," +
			"ipGstGesc," +
			"ipGstGescn," +
			"ipGstGmee," +
			"ipGstIncNmea," +
			"ipGstLesd," +
			"ipGstLesdn," +
			"ipGstNasc," +
			"ipGstNesc," +
			"ipGstNmea," +
			"ipGstNpsc," +
			"ipGstRsc," +
			"lesd," +
			"lesdn," +
			"lesdp," +
			"lmee," +
			"lmef," +
			"mep," +
			"meusa," +
			"mfp," +
			"mrp," +
			"nasc," +
			"neaa," +
			"nead," +
			"negc," +
			"negcIeq," +
			"nelc," +
			"nesc," +
			"nfsc," +
			"nmea," +
			"nodeCount," +
			"npsc," +
			"nrsc," +
			"ntsc," +
			"periodId," +
			"opGstFsd," +
			"opGstGesc," +
			"opGstGesce," +
			"opGstGescp," +
			"opGstGmef," +
			"opGstHeusa," +
			"opGstIncNmea," +
			"opGstLesd," +
			"opGstLesdp," +
			"opGstLmee," +
			"opGstLmef," +
			"opGstMeusa," +
			"opGstNasc," +
			"opGstNesc," +
			"opGstNmea," +
			"opGstNpsc," +
			"opGstRsd," +
			"rcc," +
			"rsa," +
			"rsc," +
			"rsd," +
			"maxIeq," +
			"totalBesc," +
			"totalFsc," +
			"totalFsd," +
			"totalGesc," +
			"totalGesce," +
			"totalGescn," +
			"totalGescp," +
			"totalGmee," +
			"totalGmef," +
			"totalHeusa," +
			"totalLesd," +
			"totalLesdn," +
			"totalLesdp," +
			"totalLmee," +
			"totalLmef," +
			"totalMeusa," +
			"totalNasc," +
			"totalNesc," +
			"totalNfsc," +
			"totalNmea," +
			"totalNpsc," +
			"totalNrsc," +
			"totalIncNmea," +
			"totalRcc," +
			"totalRsc," +
			"totalRsd," +
			"tte," +
			"usep," +
			"vcrp," +
			"vcrpk," +
			"vcsc," +
			"vcsck," +
			"wcq," +
			"weq," +
			"wpq," +
			"facilityRsc," +
			"gmea," +
			"lmea," +
			"opGstLmea," +
			"opGstGmea," +
			"egaId," +
			"compFeeTotal," +
			"impAfp," +
			"impHeuc," +
			"impMeuc," +
			"impUsep," +
			"wfq," +
			"wmq," +
			"deltaWmq," +
			"deltaWfq," +
			"genNode," +
			"deltaWdq," +
			"lcp," +
			"wdq," +
			"impHeur," +
			"impHlcu";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"accountId," +
			"baqPurchased," +
			"baqSold," +
			"beqPurchased," +
			"beqSold," +
			"besc," +
			"bfqPurchased," +
			"bfqSold," +
			"bifPurchased," +
			"bifSold," +
			"bwfPurchased," +
			"bwfSold," +
			"deltaWcq," +
			"deltaWeq," +
			"compEmca," +
			"compPsoa," +
			"egaIeq," +
			"egaWeq," +
			"egaWpq," +
			"accountingEua," +
			"fcc," +
			"feq," +
			"fsc," +
			"accountingFsd," +
			"gesc," +
			"gesce," +
			"gescn," +
			"gescp," +
			"gmee," +
			"accountingGmef," +
			"heua," +
			"accountingHeusa," +
			"ieq," +
			"ieqp," +
			"impEmca," +
			"impPsoa," +
			"incNmea," +
			"ipGstFsc," +
			"ipGstGesc," +
			"ipGstGescn," +
			"ipGstGmee," +
			"ipGstIncNmea," +
			"ipGstLesd," +
			"ipGstLesdn," +
			"ipGstNasc," +
			"ipGstNesc," +
			"ipGstNmea," +
			"ipGstNpsc," +
			"ipGstRsc," +
			"lesd," +
			"accountingLesdn," +
			"accountingLesdp," +
			"accountingLmee," +
			"accountingLmef," +
			"mep," +
			"accountingMeusa," +
			"mfp," +
			"mrp," +
			"nasc," +
			"neaa," +
			"accountingNead," +
			"negc," +
			"negcIeq," +
			"nelc," +
			"nesc," +
			"nfsc," +
			"nmea," +
			"nodeCount," +
			"npsc," +
			"nrsc," +
			"ntsc," +
			"periodId," +
			"opGstFsd," +
			"opGstGesc," +
			"opGstGesce," +
			"opGstGescp," +
			"opGstGmef," +
			"opGstHeusa," +
			"opGstIncNmea," +
			"opGstLesd," +
			"opGstLesdp," +
			"opGstLmee," +
			"opGstLmef," +
			"opGstMeusa," +
			"opGstNasc," +
			"opGstNesc," +
			"opGstNmea," +
			"opGstNpsc," +
			"opGstRsd," +
			"rcc," +
			"rsa," +
			"rsc," +
			"rsd," +
			"maxIeq," +
			"totalBesc," +
			"totalFsc," +
			"totalFsd," +
			"totalGesc," +
			"totalGesce," +
			"totalGescn," +
			"totalGescp," +
			"totalGmee," +
			"totalGmef," +
			"totalHeusa," +
			"totalLesd," +
			"totalLesdn," +
			"totalLesdp," +
			"totalLmee," +
			"totalLmef," +
			"totalMeusa," +
			"totalNasc," +
			"totalNesc," +
			"totalNfsc," +
			"totalNmea," +
			"totalNpsc," +
			"totalNrsc," +
			"totalIncNmea," +
			"totalRcc," +
			"totalRsc," +
			"totalRsd," +
			"tte," +
			"usep," +
			"vcrp," +
			"vcrpk," +
			"vcsc," +
			"vcsck," +
			"wcq," +
			"weq," +
			"wpq," +
			"facilityRsc," +
			"gmea," +
			"accountingLmea," +
			"opGstLmea," +
			"opGstGmea," +
			"egaId," +
			"compFeeTotal," +
			"impAfp," +
			"impHeuc," +
			"impMeuc," +
			"impUsep," +
			"loadFsd," +
			"avcsc," +
			"wfq," +
			"wmq," +
			"deltaWmq," +
			"deltaWfq," +
			"compEmcAdmCap," +
			"compEmcAdmAdj," +
			"fsrp," +
			"fsrpk," +
			"fssc," +
			"fssck," +
			"fsscp," +
			"fsscn," +
			"fsscrp," +
			"fsscrn," +
			"opGstHeur," +
			"opGstLcsc," +
			"opGstMeuc," +
			"opGstNsfc," +
			"opGstNtsc," +
			"deltaWdq," +
			"emca," +
			"hersa," +
			"heur," +
			"roundedHlcsa," +
			"hlcu," +
			"lcp," +
			"lcsc," +
			"meuc," +
			"psoa," +
			"totalHeur," +
			"totalHlcu," +
			"totalLcsc," +
			"totalLmea," +
			"wdq," +
			"genNode," +
			"impHeur," +
			"impHlcu," +
			"opGstHersa," +
			"opGstHlcsa," +
			"ipGstLcsc," +
			"hlcsa," +
			"genFsd";

		return header;
	}
}
