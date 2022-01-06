package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.AccountT;

public class Account extends AccountT {
	
	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String account_mssl = line[0];
		String account_net_sett = line[1];
		String account_pn = line[2];
		String account_emc = line[3];
		String account_taxable = line[4];
		String account_intertie = line[5];
		String account_pso = line[6];
		String account_ega_retailer = line[7];
		String account_under_retailer = line[8];
		String account_admfee = line[9];
		String account_besc = line[10];
		String account_emcadm = line[11];
		String account_eua = line[12];
		String account_fcc = line[13];
		String account_fsc = line[14];
		String account_fsd = line[15];
		String account_gesc = line[16];
		String account_gmee = line[17];
		String account_heusa = line[18];
		String account_inc_gmee = line[19];
		String account_inc_gmef = line[20];
		String account_inc_lmee = line[21];
		String account_inc_lmef = line[22];
		String account_inc_nmea = line[23];
		String account_v_admfee = line[24];
		String account_v_fsc = line[25];
		String account_v_gesc = line[26];
		String account_v_inc_gmee = line[27];
		String account_v_inc_nmea = line[28];
		String account_v_lesd = line[29];
		String account_v_nasc = line[30];
		String account_v_nesc = line[31];
		String account_v_nfsc = line[32];
		String account_v_npsc = line[33];
		String account_v_nrsc = line[34];
		String account_v_rsc = line[35];
		String account_v_stmt = line[36];
		String account_lesd = line[37];
		String account_meusa = line[38];
		String account_name = line[39];
		String account_nasc = line[40];
		String account_neaa = line[41];
		String account_nead = line[42];
		String account_negc = line[43];
		String account_nelc = line[44];
		String account_nesc = line[45];
		String account_net_amt = line[46];
		String account_nfsc = line[47];
		String account_npsc = line[48];
		String account_nrsc = line[49];
		String account_ntsc = line[50];
		String account_a_emcadm = line[51];
		String account_a_fsd = line[52];
		String account_a_gesc = line[53];
		String account_a_heusa = line[54];
		String account_a_inc_gmef = line[55];
		String account_a_inc_lmee = line[56];
		String account_a_inc_lmef = line[57];
		String account_a_inc_nmea = line[58];
		String account_a_lesd = line[59];
		String account_a_meusa = line[60];
		String account_a_nasc = line[61];
		String account_a_nesc = line[62];
		String account_a_nfsc = line[63];
		String account_a_npsc = line[64];
		String account_a_nrsc = line[65];
		String account_a_psoadm = line[66];
		String account_a_rsd = line[67];
		String account_a_stmt = line[68];
		String account_psoadm = line[69];
		String account_rcc = line[70];
		String account_rsc = line[71];
		String account_rsd = line[72];
		String account_str_id = line[73];
		String account_total_admfee = line[74];
		String account_total_amt = line[75];
		String account_total_besc = line[76];
		String account_total_emcadm = line[77];
		String account_total_fcc = line[78];
		String account_total_fsc = line[79];
		String account_total_fsd = line[80];
		String account_total_gesc = line[81];
		String account_total_gmee = line[82];
		String account_total_heusa = line[83];
		String account_total_lesd = line[84];
		String account_total_meusa = line[85];
		String account_total_nasc = line[86];
		String account_total_neaa = line[87];
		String account_total_nead = line[88];
		String account_total_negc = line[89];
		String account_total_nelc = line[90];
		String account_total_nesc = line[91];
		String account_total_nfsc = line[92];
		String account_total_npsc = line[93];
		String account_total_nrsc = line[94];
		String account_total_ntsc = line[95];
		String account_total_inc_gmee = line[96];
		String account_total_inc_gmef = line[97];
		String account_total_inc_lmee = line[98];
		String account_total_inc_lmef = line[99];
		String account_total_inc_nmea = line[100];
		String account_total_psoadm = line[101];
		String account_total_rcc = line[102];
		String account_total_rsc = line[103];
		String account_total_rsd = line[104];
		String account_total_vcsc = line[105];
		String account_vcsc = line[106];
		String account_other_total = line[107];
		String account_facility_rsc = line[108];
		String account_disp_title = line[109];
		String account_breach_flag = line[110];
		String account_mp = line[111];
		String account_residential = line[112];
		
		this.msslAccount = account_mssl.equals("1");
		this.netSett = account_net_sett.equals("1");
		this.priceNeutralization = account_pn.equals("1");
		this.emcAccount = account_emc.equals("1");
		this.taxable = account_taxable.equals("1");
		this.intertie = account_intertie.equals("1");
		this.psoAccount = account_pso.equals("1");
		this.egaRetailer = account_ega_retailer.equals("1");
		this.underRetailer = account_under_retailer.equals("1");
		this.accountId = account_name;
		this.runId = account_str_id;
		this.displayTitle = account_disp_title;
		this.breached = account_breach_flag.equals("1");
		this.participantId = account_mp;
		this.residentialAccount = account_residential.equals("1");
	}
	
	public String getKey() {
		return accountId;
	}

	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) {
		
		String account_mssl = line[0];
		String account_net_sett = line[1];
		String account_pn = line[2];
		String account_emc = line[3];
		String account_taxable = line[4];
		String account_intertie = line[5];
		String account_pso = line[6];
		String account_ega_retailer = line[7];
		String account_under_retailer = line[8];
		String account_admfee = line[9];
		String account_besc = line[10];
		String account_acctg_emcadm = line[11];
		String account_eua = line[12];
		String account_fcc = line[13];
		String account_fsc = line[14];
		String account_fsd = line[15];
		String account_gesc = line[16];
		String account_gmee = line[17];
		String account_heusa = line[18];
		String account_inc_gmee = line[19];
		String account_inc_gmef = line[20];
		String account_inc_lmee = line[21];
		String account_inc_lmef = line[22];
		String account_inc_nmea = line[23];
		String account_v_admfee = line[24];
		String account_v_fsc = line[25];
		String account_v_gesc = line[26];
		String account_v_inc_gmee = line[27];
		String account_v_inc_nmea = line[28];
		String account_v_lesd = line[29];
		String account_v_nasc = line[30];
		String account_v_nesc = line[31];
		String account_v_nfsc = line[32];
		String account_v_npsc = line[33];
		String account_v_nrsc = line[34];
		String account_v_rsc = line[35];
		String account_v_stmt = line[36];
		String account_lesd = line[37];
		String account_meusa = line[38];
		String account_name = line[39];
		String account_nasc = line[40];
		String account_neaa = line[41];
		String account_nead = line[42];
		String account_negc = line[43];
		String account_nelc = line[44];
		String account_nesc = line[45];
		String account_net_amt = line[46];
		String account_nfsc = line[47];
		String account_npsc = line[48];
		String account_nrsc = line[49];
		String account_ntsc = line[50];
		String account_acctg_a_emcadm = line[51];
		String account_a_fsd = line[52];
		String account_a_gesc = line[53];
		String account_a_heusa = line[54];
		String account_a_inc_gmef = line[55];
		String account_a_inc_lmee = line[56];
		String account_a_inc_lmef = line[57];
		String account_a_inc_nmea = line[58];
		String account_a_lesd = line[59];
		String account_a_meusa = line[60];
		String account_a_nasc = line[61];
		String account_a_nesc = line[62];
		String account_a_nfsc = line[63];
		String account_a_npsc = line[64];
		String account_a_nrsc = line[65];
		String account_acctg_a_psoadm = line[66];
		String account_a_rsd = line[67];
		String account_a_stmt = line[68];
		String account_acctg_psoadm = line[69];
		String account_rcc = line[70];
		String account_rsc = line[71];
		String account_rsd = line[72];
		String account_str_id = line[73];
		String account_total_admfee = line[74];
		String account_total_amt = line[75];
		String account_total_besc = line[76];
		String account_acctg_total_emcadm = line[77];
		String account_total_fcc = line[78];
		String account_total_fsc = line[79];
		String account_total_fsd = line[80];
		String account_total_gesc = line[81];
		String account_total_gmee = line[82];
		String account_total_heusa = line[83];
		String account_total_lesd = line[84];
		String account_total_meusa = line[85];
		String account_total_nasc = line[86];
		String account_total_neaa = line[87];
		String account_total_nead = line[88];
		String account_total_negc = line[89];
		String account_total_nelc = line[90];
		String account_total_nesc = line[91];
		String account_total_nfsc = line[92];
		String account_total_npsc = line[93];
		String account_total_nrsc = line[94];
		String account_total_ntsc = line[95];
		String account_total_inc_gmee = line[96];
		String account_total_inc_gmef = line[97];
		String account_total_inc_lmee = line[98];
		String account_total_inc_lmef = line[99];
		String account_total_inc_nmea = line[100];
		String account_acctg_total_psoadm = line[101];
		String account_total_rcc = line[102];
		String account_total_rsc = line[103];
		String account_total_rsd = line[104];
		String account_total_vcsc = line[105];
		String account_vcsc = line[106];
		String account_other_total = line[107];
		String account_facility_rsc = line[108];
		String account_disp_title = line[109];
		String account_gmef = line[110];
		String account_lmee = line[111];
		String account_lmef = line[112];
		String account_gmea = line[113];
		String account_lmea = line[114];
		String account_nmea = line[115];
		String account_emcadm_cap = line[116];
		String account_emcadm_adj = line[117];
		String account_admfee_cap = line[118];
		String account_admfee_adj = line[119];
		String account_breach_flag = line[120];
		String account_total_fssc = line[121];
		String account_fssc = line[122];
		String account_mp = line[123];
		String account_a_besc = line[124];
		String account_a_fcc = line[125];
		String account_a_fsc = line[126];
		String account_a_gmee = line[127];
		String account_a_gmef = line[128];
		String account_a_heuc = line[129];
		String account_a_lcsc = line[130];
		String account_a_lmee = line[131];
		String account_a_lmef = line[132];
		String account_a_meuc = line[133];
		String account_a_neaa = line[134];
		String account_a_nead = line[135];
		String account_a_negc = line[136];
		String account_a_nelc = line[137];
		String account_a_nmea = line[138];
		String account_a_ntsc = line[139];
		String account_a_rcc = line[140];
		String account_a_rsc = line[141];
		String account_a_total_gmef = line[142];
		String account_a_vcsc = line[143];
		String account_heuc = line[144];
		String account_lcsc = line[145];
		String account_meuc = line[146];
		String account_total_heuc = line[147];
		String account_total_lcsc = line[148];
		String account_total_lmee = line[149];
		String account_total_lmef = line[150];
		String account_total_meuc = line[151];
		String account_total_nmea = line[152];
		String account_v_besc = line[153];
		String account_v_emcadm = line[154];
		String account_v_fcc = line[155];
		String account_v_fsd = line[156];
		String account_v_gmee = line[157];
		String account_v_gmef = line[158];
		String account_v_heuc = line[159];
		String account_v_lcsc = line[160];
		String account_v_lmee = line[161];
		String account_v_lmef = line[162];
		String account_v_meuc = line[163];
		String account_v_neaa = line[164];
		String account_v_nead = line[165];
		String account_v_negc = line[166];
		String account_v_nelc = line[167];
		String account_v_nmea = line[168];
		String account_v_ntsc = line[169];
		String account_v_psoadm = line[170];
		String account_v_rcc = line[171];
		String account_v_rsd = line[172];
		String account_v_vcsc = line[173];
		String account_residential = line[174];
		
		this.msslAccount = account_mssl.equals("1");
		this.netSett = account_net_sett.equals("1");
		this.priceNeutralization = account_pn.equals("1");
		this.emcAccount = account_emc.equals("1");
		this.taxable = account_taxable.equals("1");
		this.intertie = account_intertie.equals("1");
		this.psoAccount = account_pso.equals("1");
		this.egaRetailer = account_ega_retailer.equals("1");
		this.underRetailer = account_under_retailer.equals("1");
		this.admFee = account_admfee.length() > 0? new BigDecimal(account_admfee): null;
		this.besc = account_besc.length() > 0? new BigDecimal(account_besc): null;
		this.accountingEmcAdm = account_acctg_emcadm.length() > 0? new BigDecimal(account_acctg_emcadm): null;
		this.eua = account_eua.length() > 0? new BigDecimal(account_eua): null;
		this.fcc = account_fcc.length() > 0? new BigDecimal(account_fcc): null;
		this.fsc = account_fsc.length() > 0? new BigDecimal(account_fsc): null;
		this.fsd = account_fsd.length() > 0? new BigDecimal(account_fsd): null;
		this.gesc = account_gesc.length() > 0? new BigDecimal(account_gesc): null;
		this.gmee = account_gmee.length() > 0? new BigDecimal(account_gmee): null;
		this.heusa = account_heusa.length() > 0? new BigDecimal(account_heusa): null;
		this.incGmee = account_inc_gmee.length() > 0? new BigDecimal(account_inc_gmee): null;
		this.incGmef = account_inc_gmef.length() > 0? new BigDecimal(account_inc_gmef): null;
		this.incLmee = account_inc_lmee.length() > 0? new BigDecimal(account_inc_lmee): null;
		this.incLmef = account_inc_lmef.length() > 0? new BigDecimal(account_inc_lmef): null;
		this.incNmea = account_inc_nmea.length() > 0? new BigDecimal(account_inc_nmea): null;
		this.ipGstAdmFee = account_v_admfee.length() > 0? new BigDecimal(account_v_admfee): null;
		this.ipGstFsc = account_v_fsc.length() > 0? new BigDecimal(account_v_fsc): null;
		this.ipGstGesc = account_v_gesc.length() > 0? new BigDecimal(account_v_gesc): null;
		this.ipGstIncGmee = account_v_inc_gmee.length() > 0? new BigDecimal(account_v_inc_gmee): null;
		this.ipGstIncNmea = account_v_inc_nmea.length() > 0? new BigDecimal(account_v_inc_nmea): null;
		this.ipGstLesd = account_v_lesd.length() > 0? new BigDecimal(account_v_lesd): null;
		this.ipGstNasc = account_v_nasc.length() > 0? new BigDecimal(account_v_nasc): null;
		this.ipGstNesc = account_v_nesc.length() > 0? new BigDecimal(account_v_nesc): null;
		this.ipGstNfsc = account_v_nfsc.length() > 0? new BigDecimal(account_v_nfsc): null;
		this.ipGstNpsc = account_v_npsc.length() > 0? new BigDecimal(account_v_npsc): null;
		this.ipGstNrsc = account_v_nrsc.length() > 0? new BigDecimal(account_v_nrsc): null;
		this.ipGstRsc = account_v_rsc.length() > 0? new BigDecimal(account_v_rsc): null;
		this.ipGstTotal = account_v_stmt.length() > 0? new BigDecimal(account_v_stmt): null;
		this.lesd = account_lesd.length() > 0? new BigDecimal(account_lesd): null;
		this.meusa = account_meusa.length() > 0? new BigDecimal(account_meusa): null;
		this.accountId = account_name;
		this.nasc = account_nasc.length() > 0? new BigDecimal(account_nasc): null;
		this.neaa = account_neaa.length() > 0? new BigDecimal(account_neaa): null;
		this.nead = account_nead.length() > 0? new BigDecimal(account_nead): null;
		this.negc = account_negc.length() > 0? new BigDecimal(account_negc): null;
		this.nelc = account_nelc.length() > 0? new BigDecimal(account_nelc): null;
		this.nesc = account_nesc.length() > 0? new BigDecimal(account_nesc): null;
		this.netAmount = account_net_amt.length() > 0? new BigDecimal(account_net_amt): null;
		this.nfsc = account_nfsc.length() > 0? new BigDecimal(account_nfsc): null;
		this.npsc = account_npsc.length() > 0? new BigDecimal(account_npsc): null;
		this.nrsc = account_nrsc.length() > 0? new BigDecimal(account_nrsc): null;
		this.ntsc = account_ntsc.length() > 0? new BigDecimal(account_ntsc): null;
		this.accountingOpGstEmcAdm = account_acctg_a_emcadm.length() > 0? new BigDecimal(account_acctg_a_emcadm): null;
		this.opGstFsd = account_a_fsd.length() > 0? new BigDecimal(account_a_fsd): null;
		this.opGstGesc = account_a_gesc.length() > 0? new BigDecimal(account_a_gesc): null;
		this.opGstHeusa = account_a_heusa.length() > 0? new BigDecimal(account_a_heusa): null;
		this.opGstIncGmef = account_a_inc_gmef.length() > 0? new BigDecimal(account_a_inc_gmef): null;
		this.opGstIncLmee = account_a_inc_lmee.length() > 0? new BigDecimal(account_a_inc_lmee): null;
		this.opGstIncLmef = account_a_inc_lmef.length() > 0? new BigDecimal(account_a_inc_lmef): null;
		this.opGstIncNmea = account_a_inc_nmea.length() > 0? new BigDecimal(account_a_inc_nmea): null;
		this.opGstLesd = account_a_lesd.length() > 0? new BigDecimal(account_a_lesd): null;
		this.opGstMeusa = account_a_meusa.length() > 0? new BigDecimal(account_a_meusa): null;
		this.opGstNasc = account_a_nasc.length() > 0? new BigDecimal(account_a_nasc): null;
		this.opGstNesc = account_a_nesc.length() > 0? new BigDecimal(account_a_nesc): null;
		this.opGstNfsc = account_a_nfsc.length() > 0? new BigDecimal(account_a_nfsc): null;
		this.opGstNpsc = account_a_npsc.length() > 0? new BigDecimal(account_a_npsc): null;
		this.opGstNrsc = account_a_nrsc.length() > 0? new BigDecimal(account_a_nrsc): null;
		this.accountingOpGstPsoAdm = account_acctg_a_psoadm.length() > 0? new BigDecimal(account_acctg_a_psoadm): null;
		this.opGstRsd = account_a_rsd.length() > 0? new BigDecimal(account_a_rsd): null;
		this.opGstTotal = account_a_stmt.length() > 0? new BigDecimal(account_a_stmt): null;
		this.accountingPsoAdm = account_acctg_psoadm.length() > 0? new BigDecimal(account_acctg_psoadm): null;
		this.rcc = account_rcc.length() > 0? new BigDecimal(account_rcc): null;
		this.rsc = account_rsc.length() > 0? new BigDecimal(account_rsc): null;
		this.rsd = account_rsd.length() > 0? new BigDecimal(account_rsd): null;
		this.runId = account_str_id;
		this.totalAdmFee = account_total_admfee.length() > 0? new BigDecimal(account_total_admfee): null;
		this.totalAmount = account_total_amt.length() > 0? new BigDecimal(account_total_amt): null;
		this.totalBesc = account_total_besc.length() > 0? new BigDecimal(account_total_besc): null;
		this.accountingTotalEmcAdm = account_acctg_total_emcadm.length() > 0? new BigDecimal(account_acctg_total_emcadm): null;
		this.totalFcc = account_total_fcc.length() > 0? new BigDecimal(account_total_fcc): null;
		this.totalFsc = account_total_fsc.length() > 0? new BigDecimal(account_total_fsc): null;
		this.totalFsd = account_total_fsd.length() > 0? new BigDecimal(account_total_fsd): null;
		this.totalGesc = account_total_gesc.length() > 0? new BigDecimal(account_total_gesc): null;
		this.totalGmee = account_total_gmee.length() > 0? new BigDecimal(account_total_gmee): null;
		this.totalHeusa = account_total_heusa.length() > 0? new BigDecimal(account_total_heusa): null;
		this.totalLesd = account_total_lesd.length() > 0? new BigDecimal(account_total_lesd): null;
		this.totalMeusa = account_total_meusa.length() > 0? new BigDecimal(account_total_meusa): null;
		this.totalNasc = account_total_nasc.length() > 0? new BigDecimal(account_total_nasc): null;
		this.totalNeaa = account_total_neaa.length() > 0? new BigDecimal(account_total_neaa): null;
		this.totalNead = account_total_nead.length() > 0? new BigDecimal(account_total_nead): null;
		this.totalNegc = account_total_negc.length() > 0? new BigDecimal(account_total_negc): null;
		this.totalNelc = account_total_nelc.length() > 0? new BigDecimal(account_total_nelc): null;
		this.totalNesc = account_total_nesc.length() > 0? new BigDecimal(account_total_nesc): null;
		this.totalNfsc = account_total_nfsc.length() > 0? new BigDecimal(account_total_nfsc): null;
		this.totalNpsc = account_total_npsc.length() > 0? new BigDecimal(account_total_npsc): null;
		this.totalNrsc = account_total_nrsc.length() > 0? new BigDecimal(account_total_nrsc): null;
		this.totalNtsc = account_total_ntsc.length() > 0? new BigDecimal(account_total_ntsc): null;
		this.totalIncGmee = account_total_inc_gmee.length() > 0? new BigDecimal(account_total_inc_gmee): null;
		this.totalIncGmef = account_total_inc_gmef.length() > 0? new BigDecimal(account_total_inc_gmef): null;
		this.totalIncLmee = account_total_inc_lmee.length() > 0? new BigDecimal(account_total_inc_lmee): null;
		this.totalIncLmef = account_total_inc_lmef.length() > 0? new BigDecimal(account_total_inc_lmef): null;
		this.totalIncNmea = account_total_inc_nmea.length() > 0? new BigDecimal(account_total_inc_nmea): null;
		this.accountingTotalPsoAdm = account_acctg_total_psoadm.length() > 0? new BigDecimal(account_acctg_total_psoadm): null;
		this.totalRcc = account_total_rcc.length() > 0? new BigDecimal(account_total_rcc): null;
		this.totalRsc = account_total_rsc.length() > 0? new BigDecimal(account_total_rsc): null;
		this.totalRsd = account_total_rsd.length() > 0? new BigDecimal(account_total_rsd): null;
		this.totalVcsc = account_total_vcsc.length() > 0? new BigDecimal(account_total_vcsc): null;
		this.vcsc = account_vcsc.length() > 0? new BigDecimal(account_vcsc): null;
		this.otherTotal = account_other_total.length() > 0? new BigDecimal(account_other_total): null;
		this.facilityRsc = account_facility_rsc.length() > 0? new BigDecimal(account_facility_rsc): null;
		this.displayTitle = account_disp_title;
		this.gmef = account_gmef.length() > 0? new BigDecimal(account_gmef): null;
		this.lmee = account_lmee.length() > 0? new BigDecimal(account_lmee): null;
		this.lmef = account_lmef.length() > 0? new BigDecimal(account_lmef): null;
		this.gmea = account_gmea.length() > 0? new BigDecimal(account_gmea): null;
		this.lmea = account_lmea.length() > 0? new BigDecimal(account_lmea): null;
		this.nmea = account_nmea.length() > 0? new BigDecimal(account_nmea): null;
		this.emcAdmCap = account_emcadm_cap.length() > 0? new BigDecimal(account_emcadm_cap): null;
		this.emcAdmAdj = account_emcadm_adj.length() > 0? new BigDecimal(account_emcadm_adj): null;
		this.admFeeCap = account_admfee_cap.length() > 0? new BigDecimal(account_admfee_cap): null;
		this.admFeeAdj = account_admfee_adj.length() > 0? new BigDecimal(account_admfee_adj): null;
		this.breached = account_breach_flag.equals("1");
		this.totalFssc = account_total_fssc.length() > 0? new BigDecimal(account_total_fssc): null;
		this.fssc = account_fssc.length() > 0? new BigDecimal(account_fssc): null;
		this.participantId = account_mp;
		this.opGstBesc = account_a_besc.length() > 0? new BigDecimal(account_a_besc): null;
		this.opGstFcc = account_a_fcc.length() > 0? new BigDecimal(account_a_fcc): null;
		this.opGstFsc = account_a_fsc.length() > 0? new BigDecimal(account_a_fsc): null;
		this.opGstGmee = account_a_gmee.length() > 0? new BigDecimal(account_a_gmee): null;
		this.opGstGmef = account_a_gmef.length() > 0? new BigDecimal(account_a_gmef): null;
		this.opGstHeuc = account_a_heuc.length() > 0? new BigDecimal(account_a_heuc): null;
		this.opGstLcsc = account_a_lcsc.length() > 0? new BigDecimal(account_a_lcsc): null;
		this.opGstLmee = account_a_lmee.length() > 0? new BigDecimal(account_a_lmee): null;
		this.opGstLmef = account_a_lmef.length() > 0? new BigDecimal(account_a_lmef): null;
		this.opGstMeuc = account_a_meuc.length() > 0? new BigDecimal(account_a_meuc): null;
		this.opGstNeaa = account_a_neaa.length() > 0? new BigDecimal(account_a_neaa): null;
		this.opGstNead = account_a_nead.length() > 0? new BigDecimal(account_a_nead): null;
		this.opGstNegc = account_a_negc.length() > 0? new BigDecimal(account_a_negc): null;
		this.opGstNelc = account_a_nelc.length() > 0? new BigDecimal(account_a_nelc): null;
		this.opGstNmea = account_a_nmea.length() > 0? new BigDecimal(account_a_nmea): null;
		this.opGstNtsc = account_a_ntsc.length() > 0? new BigDecimal(account_a_ntsc): null;
		this.opGstRcc = account_a_rcc.length() > 0? new BigDecimal(account_a_rcc): null;
		this.opGstRsc = account_a_rsc.length() > 0? new BigDecimal(account_a_rsc): null;
		this.opGstTotalGmef = account_a_total_gmef.length() > 0? new BigDecimal(account_a_total_gmef): null;
		this.opGstVcsc = account_a_vcsc.length() > 0? new BigDecimal(account_a_vcsc): null;
		this.heuc = account_heuc.length() > 0? new BigDecimal(account_heuc): null;
		this.lcsc = account_lcsc.length() > 0? new BigDecimal(account_lcsc): null;
		this.meuc = account_meuc.length() > 0? new BigDecimal(account_meuc): null;
		this.totalHeuc = account_total_heuc.length() > 0? new BigDecimal(account_total_heuc): null;
		this.totalLcsc = account_total_lcsc.length() > 0? new BigDecimal(account_total_lcsc): null;
		this.totalLmee = account_total_lmee.length() > 0? new BigDecimal(account_total_lmee): null;
		this.totalLmef = account_total_lmef.length() > 0? new BigDecimal(account_total_lmef): null;
		this.totalMeuc = account_total_meuc.length() > 0? new BigDecimal(account_total_meuc): null;
		this.totalNmea = account_total_nmea.length() > 0? new BigDecimal(account_total_nmea): null;
		this.ipGstBesc = account_v_besc.length() > 0? new BigDecimal(account_v_besc): null;
		this.ipGstEmcAdm = account_v_emcadm.length() > 0? new BigDecimal(account_v_emcadm): null;
		this.ipGstFcc = account_v_fcc.length() > 0? new BigDecimal(account_v_fcc): null;
		this.ipGstFsd = account_v_fsd.length() > 0? new BigDecimal(account_v_fsd): null;
		this.ipGstGmee = account_v_gmee.length() > 0? new BigDecimal(account_v_gmee): null;
		this.ipGstGmef = account_v_gmef.length() > 0? new BigDecimal(account_v_gmef): null;
		this.ipGstHeuc = account_v_heuc.length() > 0? new BigDecimal(account_v_heuc): null;
		this.ipGstLcsc = account_v_lcsc.length() > 0? new BigDecimal(account_v_lcsc): null;
		this.ipGstLmee = account_v_lmee.length() > 0? new BigDecimal(account_v_lmee): null;
		this.ipGstLmef = account_v_lmef.length() > 0? new BigDecimal(account_v_lmef): null;
		this.ipGstMeuc = account_v_meuc.length() > 0? new BigDecimal(account_v_meuc): null;
		this.ipGstNeaa = account_v_neaa.length() > 0? new BigDecimal(account_v_neaa): null;
		this.ipGstNead = account_v_nead.length() > 0? new BigDecimal(account_v_nead): null;
		this.ipGstNegc = account_v_negc.length() > 0? new BigDecimal(account_v_negc): null;
		this.ipGstNelc = account_v_nelc.length() > 0? new BigDecimal(account_v_nelc): null;
		this.ipGstNmea = account_v_nmea.length() > 0? new BigDecimal(account_v_nmea): null;
		this.ipGstNtsc = account_v_ntsc.length() > 0? new BigDecimal(account_v_ntsc): null;
		this.ipGstPsoAdm = account_v_psoadm.length() > 0? new BigDecimal(account_v_psoadm): null;
		this.ipGstRcc = account_v_rcc.length() > 0? new BigDecimal(account_v_rcc): null;
		this.ipGstRsd = account_v_rsd.length() > 0? new BigDecimal(account_v_rsd): null;
		this.ipGstVcsc = account_v_vcsc.length() > 0? new BigDecimal(account_v_vcsc): null;
		this.residentialAccount = account_residential.equals("1");
	}
	
	public String PFCheck(Account in) {

		String result = null;

		if ((this.admFee == null && in.admFee != null) || (this.admFee != null && in.admFee == null)) result = "admFee missing value";
		if ((this.besc == null && in.besc != null) || (this.besc != null && in.besc == null)) result = "besc missing value";
		if ((this.accountingEmcAdm == null && in.accountingEmcAdm != null) || (this.accountingEmcAdm != null && in.accountingEmcAdm == null)) result = "accountingEmcAdm missing value";
		if ((this.eua == null && in.eua != null) || (this.eua != null && in.eua == null)) result = "eua missing value";
		if ((this.fcc == null && in.fcc != null) || (this.fcc != null && in.fcc == null)) result = "fcc missing value";
		if ((this.fsc == null && in.fsc != null) || (this.fsc != null && in.fsc == null)) result = "fsc missing value";
		if ((this.fsd == null && in.fsd != null) || (this.fsd != null && in.fsd == null)) result = "fsd missing value";
		if ((this.gesc == null && in.gesc != null) || (this.gesc != null && in.gesc == null)) result = "gesc missing value";
		//if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.heusa == null && in.heusa != null) || (this.heusa != null && in.heusa == null)) result = "heusa missing value";
		if ((this.incGmee == null && in.incGmee != null) || (this.incGmee != null && in.incGmee == null)) result = "incGmee missing value";
		if ((this.incGmef == null && in.incGmef != null) || (this.incGmef != null && in.incGmef == null)) result = "incGmef missing value";
		if ((this.incLmee == null && in.incLmee != null) || (this.incLmee != null && in.incLmee == null)) result = "incLmee missing value";
		if ((this.incLmef == null && in.incLmef != null) || (this.incLmef != null && in.incLmef == null)) result = "incLmef missing value";
		if ((this.incNmea == null && in.incNmea != null) || (this.incNmea != null && in.incNmea == null)) result = "incNmea missing value";
		if ((this.ipGstAdmFee == null && in.ipGstAdmFee != null) || (this.ipGstAdmFee != null && in.ipGstAdmFee == null)) result = "ipGstAdmFee missing value";
		if ((this.ipGstFsc == null && in.ipGstFsc != null) || (this.ipGstFsc != null && in.ipGstFsc == null)) result = "ipGstFsc missing value";
		if ((this.ipGstGesc == null && in.ipGstGesc != null) || (this.ipGstGesc != null && in.ipGstGesc == null)) result = "ipGstGesc missing value";
		if ((this.ipGstIncGmee == null && in.ipGstIncGmee != null) || (this.ipGstIncGmee != null && in.ipGstIncGmee == null)) result = "ipGstIncGmee missing value";
		if ((this.ipGstIncNmea == null && in.ipGstIncNmea != null) || (this.ipGstIncNmea != null && in.ipGstIncNmea == null)) result = "ipGstIncNmea missing value";
		if ((this.ipGstLesd == null && in.ipGstLesd != null) || (this.ipGstLesd != null && in.ipGstLesd == null)) result = "ipGstLesd missing value";
		if ((this.ipGstNasc == null && in.ipGstNasc != null) || (this.ipGstNasc != null && in.ipGstNasc == null)) result = "ipGstNasc missing value";
		if ((this.ipGstNesc == null && in.ipGstNesc != null) || (this.ipGstNesc != null && in.ipGstNesc == null)) result = "ipGstNesc missing value";
		if ((this.ipGstNfsc == null && in.ipGstNfsc != null) || (this.ipGstNfsc != null && in.ipGstNfsc == null)) result = "ipGstNfsc missing value";
		//if ((this.ipGstNpsc == null && in.ipGstNpsc != null) || (this.ipGstNpsc != null && in.ipGstNpsc == null)) result = "ipGstNpsc missing value";
		if ((this.ipGstNrsc == null && in.ipGstNrsc != null) || (this.ipGstNrsc != null && in.ipGstNrsc == null)) result = "ipGstNrsc missing value";
		if ((this.ipGstRsc == null && in.ipGstRsc != null) || (this.ipGstRsc != null && in.ipGstRsc == null)) result = "ipGstRsc missing value";
		if ((this.ipGstTotal == null && in.ipGstTotal != null) || (this.ipGstTotal != null && in.ipGstTotal == null)) result = "ipGstTotal missing value";
		if ((this.lesd == null && in.lesd != null) || (this.lesd != null && in.lesd == null)) result = "lesd missing value";
		if ((this.meusa == null && in.meusa != null) || (this.meusa != null && in.meusa == null)) result = "meusa missing value";
		if ((this.nasc == null && in.nasc != null) || (this.nasc != null && in.nasc == null)) result = "nasc missing value";
		if ((this.neaa == null && in.neaa != null) || (this.neaa != null && in.neaa == null)) result = "neaa missing value";
		if ((this.nead == null && in.nead != null) || (this.nead != null && in.nead == null)) result = "nead missing value";
		if ((this.negc == null && in.negc != null) || (this.negc != null && in.negc == null)) result = "negc missing value";
		if ((this.nelc == null && in.nelc != null) || (this.nelc != null && in.nelc == null)) result = "nelc missing value";
		if ((this.nesc == null && in.nesc != null) || (this.nesc != null && in.nesc == null)) result = "nesc missing value";
		if ((this.netAmount == null && in.netAmount != null) || (this.netAmount != null && in.netAmount == null)) result = "netAmount missing value";
		if ((this.nfsc == null && in.nfsc != null) || (this.nfsc != null && in.nfsc == null)) result = "nfsc missing value";
		//if ((this.npsc == null && in.npsc != null) || (this.npsc != null && in.npsc == null)) result = "npsc missing value";
		if ((this.nrsc == null && in.nrsc != null) || (this.nrsc != null && in.nrsc == null)) result = "nrsc missing value";
		if ((this.ntsc == null && in.ntsc != null) || (this.ntsc != null && in.ntsc == null)) result = "ntsc missing value";
		if ((this.accountingOpGstEmcAdm == null && in.accountingOpGstEmcAdm != null) || (this.accountingOpGstEmcAdm != null && in.accountingOpGstEmcAdm == null)) result = "accountingOpGstEmcAdm missing value";
		if ((this.opGstFsd == null && in.opGstFsd != null) || (this.opGstFsd != null && in.opGstFsd == null)) result = "opGstFsd missing value";
		if ((this.opGstGesc == null && in.opGstGesc != null) || (this.opGstGesc != null && in.opGstGesc == null)) result = "opGstGesc missing value";
		if ((this.opGstHeusa == null && in.opGstHeusa != null) || (this.opGstHeusa != null && in.opGstHeusa == null)) result = "opGstHeusa missing value";
		if ((this.opGstIncGmef == null && in.opGstIncGmef != null) || (this.opGstIncGmef != null && in.opGstIncGmef == null)) result = "opGstIncGmef missing value";
		if ((this.opGstIncLmee == null && in.opGstIncLmee != null) || (this.opGstIncLmee != null && in.opGstIncLmee == null)) result = "opGstIncLmee missing value";
		if ((this.opGstIncLmef == null && in.opGstIncLmef != null) || (this.opGstIncLmef != null && in.opGstIncLmef == null)) result = "opGstIncLmef missing value";
		if ((this.opGstIncNmea == null && in.opGstIncNmea != null) || (this.opGstIncNmea != null && in.opGstIncNmea == null)) result = "opGstIncNmea missing value";
		if ((this.opGstLesd == null && in.opGstLesd != null) || (this.opGstLesd != null && in.opGstLesd == null)) result = "opGstLesd missing value";
		if ((this.opGstMeusa == null && in.opGstMeusa != null) || (this.opGstMeusa != null && in.opGstMeusa == null)) result = "opGstMeusa missing value";
		if ((this.opGstNasc == null && in.opGstNasc != null) || (this.opGstNasc != null && in.opGstNasc == null)) result = "opGstNasc missing value";
		if ((this.opGstNesc == null && in.opGstNesc != null) || (this.opGstNesc != null && in.opGstNesc == null)) result = "opGstNesc missing value";
		if ((this.opGstNfsc == null && in.opGstNfsc != null) || (this.opGstNfsc != null && in.opGstNfsc == null)) result = "opGstNfsc missing value";
		//if ((this.opGstNpsc == null && in.opGstNpsc != null) || (this.opGstNpsc != null && in.opGstNpsc == null)) result = "opGstNpsc missing value";
		if ((this.opGstNrsc == null && in.opGstNrsc != null) || (this.opGstNrsc != null && in.opGstNrsc == null)) result = "opGstNrsc missing value";
		if ((this.accountingOpGstPsoAdm == null && in.accountingOpGstPsoAdm != null) || (this.accountingOpGstPsoAdm != null && in.accountingOpGstPsoAdm == null)) result = "accountingOpGstPsoAdm missing value";
		if ((this.opGstRsd == null && in.opGstRsd != null) || (this.opGstRsd != null && in.opGstRsd == null)) result = "opGstRsd missing value";
		if ((this.opGstTotal == null && in.opGstTotal != null) || (this.opGstTotal != null && in.opGstTotal == null)) result = "opGstTotal missing value";
		if ((this.accountingPsoAdm == null && in.accountingPsoAdm != null) || (this.accountingPsoAdm != null && in.accountingPsoAdm == null)) result = "accountingPsoAdm missing value";
		if ((this.rcc == null && in.rcc != null) || (this.rcc != null && in.rcc == null)) result = "rcc missing value";
		if ((this.rsc == null && in.rsc != null) || (this.rsc != null && in.rsc == null)) result = "rsc missing value";
		if ((this.rsd == null && in.rsd != null) || (this.rsd != null && in.rsd == null)) result = "rsd missing value";
		if ((this.totalAdmFee == null && in.totalAdmFee != null) || (this.totalAdmFee != null && in.totalAdmFee == null)) result = "totalAdmFee missing value";
		if ((this.totalAmount == null && in.totalAmount != null) || (this.totalAmount != null && in.totalAmount == null)) result = "totalAmount missing value";
		if ((this.totalBesc == null && in.totalBesc != null) || (this.totalBesc != null && in.totalBesc == null)) result = "totalBesc missing value";
		if ((this.accountingTotalEmcAdm == null && in.accountingTotalEmcAdm != null) || (this.accountingTotalEmcAdm != null && in.accountingTotalEmcAdm == null)) result = "accountingTotalEmcAdm missing value";
		if ((this.totalFcc == null && in.totalFcc != null) || (this.totalFcc != null && in.totalFcc == null)) result = "totalFcc missing value";
		if ((this.totalFsc == null && in.totalFsc != null) || (this.totalFsc != null && in.totalFsc == null)) result = "totalFsc missing value";
		if ((this.totalFsd == null && in.totalFsd != null) || (this.totalFsd != null && in.totalFsd == null)) result = "totalFsd missing value";
		if ((this.totalGesc == null && in.totalGesc != null) || (this.totalGesc != null && in.totalGesc == null)) result = "totalGesc missing value";
		if ((this.totalGmee == null && in.totalGmee != null) || (this.totalGmee != null && in.totalGmee == null)) result = "totalGmee missing value";
		if ((this.totalHeusa == null && in.totalHeusa != null) || (this.totalHeusa != null && in.totalHeusa == null)) result = "totalHeusa missing value";
		if ((this.totalLesd == null && in.totalLesd != null) || (this.totalLesd != null && in.totalLesd == null)) result = "totalLesd missing value";
		if ((this.totalMeusa == null && in.totalMeusa != null) || (this.totalMeusa != null && in.totalMeusa == null)) result = "totalMeusa missing value";
		if ((this.totalNasc == null && in.totalNasc != null) || (this.totalNasc != null && in.totalNasc == null)) result = "totalNasc missing value";
		if ((this.totalNeaa == null && in.totalNeaa != null) || (this.totalNeaa != null && in.totalNeaa == null)) result = "totalNeaa missing value";
		if ((this.totalNead == null && in.totalNead != null) || (this.totalNead != null && in.totalNead == null)) result = "totalNead missing value";
		if ((this.totalNegc == null && in.totalNegc != null) || (this.totalNegc != null && in.totalNegc == null)) result = "totalNegc missing value";
		if ((this.totalNelc == null && in.totalNelc != null) || (this.totalNelc != null && in.totalNelc == null)) result = "totalNelc missing value";
		if ((this.totalNesc == null && in.totalNesc != null) || (this.totalNesc != null && in.totalNesc == null)) result = "totalNesc missing value";
		if ((this.totalNfsc == null && in.totalNfsc != null) || (this.totalNfsc != null && in.totalNfsc == null)) result = "totalNfsc missing value";
		//if ((this.totalNpsc == null && in.totalNpsc != null) || (this.totalNpsc != null && in.totalNpsc == null)) result = "totalNpsc missing value";
		if ((this.totalNrsc == null && in.totalNrsc != null) || (this.totalNrsc != null && in.totalNrsc == null)) result = "totalNrsc missing value";
		if ((this.totalNtsc == null && in.totalNtsc != null) || (this.totalNtsc != null && in.totalNtsc == null)) result = "totalNtsc missing value";
		if ((this.totalIncGmee == null && in.totalIncGmee != null) || (this.totalIncGmee != null && in.totalIncGmee == null)) result = "totalIncGmee missing value";
		if ((this.totalIncGmef == null && in.totalIncGmef != null) || (this.totalIncGmef != null && in.totalIncGmef == null)) result = "totalIncGmef missing value";
		if ((this.totalIncLmee == null && in.totalIncLmee != null) || (this.totalIncLmee != null && in.totalIncLmee == null)) result = "totalIncLmee missing value";
		if ((this.totalIncLmef == null && in.totalIncLmef != null) || (this.totalIncLmef != null && in.totalIncLmef == null)) result = "totalIncLmef missing value";
		if ((this.totalIncNmea == null && in.totalIncNmea != null) || (this.totalIncNmea != null && in.totalIncNmea == null)) result = "totalIncNmea missing value";
		if ((this.accountingTotalPsoAdm == null && in.accountingTotalPsoAdm != null) || (this.accountingTotalPsoAdm != null && in.accountingTotalPsoAdm == null)) result = "accountingTotalPsoAdm missing value";
		if ((this.totalRcc == null && in.totalRcc != null) || (this.totalRcc != null && in.totalRcc == null)) result = "totalRcc missing value";
		if ((this.totalRsc == null && in.totalRsc != null) || (this.totalRsc != null && in.totalRsc == null)) result = "totalRsc missing value";
		if ((this.totalRsd == null && in.totalRsd != null) || (this.totalRsd != null && in.totalRsd == null)) result = "totalRsd missing value";
		if ((this.totalVcsc == null && in.totalVcsc != null) || (this.totalVcsc != null && in.totalVcsc == null)) result = "totalVcsc missing value";
		if ((this.vcsc == null && in.vcsc != null) || (this.vcsc != null && in.vcsc == null)) result = "vcsc missing value";
		if ((this.otherTotal == null && in.otherTotal != null) || (this.otherTotal != null && in.otherTotal == null)) result = "otherTotal missing value";
		if ((this.facilityRsc == null && in.facilityRsc != null) || (this.facilityRsc != null && in.facilityRsc == null)) result = "facilityRsc missing value";
		//if ((this.gmef == null && in.gmef != null) || (this.gmef != null && in.gmef == null)) result = "gmef missing value";
		//if ((this.lmee == null && in.lmee != null) || (this.lmee != null && in.lmee == null)) result = "lmee missing value";
		//if ((this.lmef == null && in.lmef != null) || (this.lmef != null && in.lmef == null)) result = "lmef missing value";
		//if ((this.gmea == null && in.gmea != null) || (this.gmea != null && in.gmea == null)) result = "gmea missing value";
		//if ((this.lmea == null && in.lmea != null) || (this.lmea != null && in.lmea == null)) result = "lmea missing value";
		//if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";
		if ((this.emcAdmCap == null && in.emcAdmCap != null) || (this.emcAdmCap != null && in.emcAdmCap == null)) result = "emcAdmCap missing value";
		if ((this.emcAdmAdj == null && in.emcAdmAdj != null) || (this.emcAdmAdj != null && in.emcAdmAdj == null)) result = "emcAdmAdj missing value";
		if ((this.admFeeCap == null && in.admFeeCap != null) || (this.admFeeCap != null && in.admFeeCap == null)) result = "admFeeCap missing value";
		if ((this.admFeeAdj == null && in.admFeeAdj != null) || (this.admFeeAdj != null && in.admFeeAdj == null)) result = "admFeeAdj missing value";
		if ((this.totalFssc == null && in.totalFssc != null) || (this.totalFssc != null && in.totalFssc == null)) result = "totalFssc missing value";
		if ((this.fssc == null && in.fssc != null) || (this.fssc != null && in.fssc == null)) result = "fssc missing value";
		if ((this.opGstBesc == null && in.opGstBesc != null) || (this.opGstBesc != null && in.opGstBesc == null)) result = "opGstBesc missing value";
		if ((this.opGstFcc == null && in.opGstFcc != null) || (this.opGstFcc != null && in.opGstFcc == null)) result = "opGstFcc missing value";
		if ((this.opGstFsc == null && in.opGstFsc != null) || (this.opGstFsc != null && in.opGstFsc == null)) result = "opGstFsc missing value";
		if ((this.opGstGmee == null && in.opGstGmee != null) || (this.opGstGmee != null && in.opGstGmee == null)) result = "opGstGmee missing value";
		if ((this.opGstGmef == null && in.opGstGmef != null) || (this.opGstGmef != null && in.opGstGmef == null)) result = "opGstGmef missing value";
		if ((this.opGstHeuc == null && in.opGstHeuc != null) || (this.opGstHeuc != null && in.opGstHeuc == null)) result = "opGstHeuc missing value";
		if ((this.opGstLcsc == null && in.opGstLcsc != null) || (this.opGstLcsc != null && in.opGstLcsc == null)) result = "opGstLcsc missing value";
		if ((this.opGstLmee == null && in.opGstLmee != null) || (this.opGstLmee != null && in.opGstLmee == null)) result = "opGstLmee missing value";
		if ((this.opGstLmef == null && in.opGstLmef != null) || (this.opGstLmef != null && in.opGstLmef == null)) result = "opGstLmef missing value";
		if ((this.opGstMeuc == null && in.opGstMeuc != null) || (this.opGstMeuc != null && in.opGstMeuc == null)) result = "opGstMeuc missing value";
		if ((this.opGstNeaa == null && in.opGstNeaa != null) || (this.opGstNeaa != null && in.opGstNeaa == null)) result = "opGstNeaa missing value";
		if ((this.opGstNead == null && in.opGstNead != null) || (this.opGstNead != null && in.opGstNead == null)) result = "opGstNead missing value";
		if ((this.opGstNegc == null && in.opGstNegc != null) || (this.opGstNegc != null && in.opGstNegc == null)) result = "opGstNegc missing value";
		if ((this.opGstNelc == null && in.opGstNelc != null) || (this.opGstNelc != null && in.opGstNelc == null)) result = "opGstNelc missing value";
		//if ((this.opGstNmea == null && in.opGstNmea != null) || (this.opGstNmea != null && in.opGstNmea == null)) result = "opGstNmea missing value";
		if ((this.opGstNtsc == null && in.opGstNtsc != null) || (this.opGstNtsc != null && in.opGstNtsc == null)) result = "opGstNtsc missing value";
		if ((this.opGstRcc == null && in.opGstRcc != null) || (this.opGstRcc != null && in.opGstRcc == null)) result = "opGstRcc missing value";
		if ((this.opGstRsc == null && in.opGstRsc != null) || (this.opGstRsc != null && in.opGstRsc == null)) result = "opGstRsc missing value";
		if ((this.opGstTotalGmef == null && in.opGstTotalGmef != null) || (this.opGstTotalGmef != null && in.opGstTotalGmef == null)) result = "opGstTotalGmef missing value";
		if ((this.opGstVcsc == null && in.opGstVcsc != null) || (this.opGstVcsc != null && in.opGstVcsc == null)) result = "opGstVcsc missing value";
		if ((this.heuc == null && in.heuc != null) || (this.heuc != null && in.heuc == null)) result = "heuc missing value";
		if ((this.lcsc == null && in.lcsc != null) || (this.lcsc != null && in.lcsc == null)) result = "lcsc missing value";
		if ((this.meuc == null && in.meuc != null) || (this.meuc != null && in.meuc == null)) result = "meuc missing value";
		if ((this.totalHeuc == null && in.totalHeuc != null) || (this.totalHeuc != null && in.totalHeuc == null)) result = "totalHeuc missing value";
		if ((this.totalLcsc == null && in.totalLcsc != null) || (this.totalLcsc != null && in.totalLcsc == null)) result = "totalLcsc missing value";
		if ((this.totalLmee == null && in.totalLmee != null) || (this.totalLmee != null && in.totalLmee == null)) result = "totalLmee missing value";
		if ((this.totalLmef == null && in.totalLmef != null) || (this.totalLmef != null && in.totalLmef == null)) result = "totalLmef missing value";
		if ((this.totalMeuc == null && in.totalMeuc != null) || (this.totalMeuc != null && in.totalMeuc == null)) result = "totalMeuc missing value";
		//if ((this.totalNmea == null && in.totalNmea != null) || (this.totalNmea != null && in.totalNmea == null)) result = "totalNmea missing value";
		if ((this.ipGstBesc == null && in.ipGstBesc != null) || (this.ipGstBesc != null && in.ipGstBesc == null)) result = "ipGstBesc missing value";
		if ((this.ipGstEmcAdm == null && in.ipGstEmcAdm != null) || (this.ipGstEmcAdm != null && in.ipGstEmcAdm == null)) result = "ipGstEmcAdm missing value";
		if ((this.ipGstFcc == null && in.ipGstFcc != null) || (this.ipGstFcc != null && in.ipGstFcc == null)) result = "ipGstFcc missing value";
		if ((this.ipGstFsd == null && in.ipGstFsd != null) || (this.ipGstFsd != null && in.ipGstFsd == null)) result = "ipGstFsd missing value";
		if ((this.ipGstGmee == null && in.ipGstGmee != null) || (this.ipGstGmee != null && in.ipGstGmee == null)) result = "ipGstGmee missing value";
		if ((this.ipGstGmef == null && in.ipGstGmef != null) || (this.ipGstGmef != null && in.ipGstGmef == null)) result = "ipGstGmef missing value";
		if ((this.ipGstHeuc == null && in.ipGstHeuc != null) || (this.ipGstHeuc != null && in.ipGstHeuc == null)) result = "ipGstHeuc missing value";
		if ((this.ipGstLcsc == null && in.ipGstLcsc != null) || (this.ipGstLcsc != null && in.ipGstLcsc == null)) result = "ipGstLcsc missing value";
		if ((this.ipGstLmee == null && in.ipGstLmee != null) || (this.ipGstLmee != null && in.ipGstLmee == null)) result = "ipGstLmee missing value";
		if ((this.ipGstLmef == null && in.ipGstLmef != null) || (this.ipGstLmef != null && in.ipGstLmef == null)) result = "ipGstLmef missing value";
		if ((this.ipGstMeuc == null && in.ipGstMeuc != null) || (this.ipGstMeuc != null && in.ipGstMeuc == null)) result = "ipGstMeuc missing value";
		if ((this.ipGstNeaa == null && in.ipGstNeaa != null) || (this.ipGstNeaa != null && in.ipGstNeaa == null)) result = "ipGstNeaa missing value";
		if ((this.ipGstNead == null && in.ipGstNead != null) || (this.ipGstNead != null && in.ipGstNead == null)) result = "ipGstNead missing value";
		if ((this.ipGstNegc == null && in.ipGstNegc != null) || (this.ipGstNegc != null && in.ipGstNegc == null)) result = "ipGstNegc missing value";
		if ((this.ipGstNelc == null && in.ipGstNelc != null) || (this.ipGstNelc != null && in.ipGstNelc == null)) result = "ipGstNelc missing value";
		//if ((this.ipGstNmea == null && in.ipGstNmea != null) || (this.ipGstNmea != null && in.ipGstNmea == null)) result = "ipGstNmea missing value";
		if ((this.ipGstNtsc == null && in.ipGstNtsc != null) || (this.ipGstNtsc != null && in.ipGstNtsc == null)) result = "ipGstNtsc missing value";
		if ((this.ipGstPsoAdm == null && in.ipGstPsoAdm != null) || (this.ipGstPsoAdm != null && in.ipGstPsoAdm == null)) result = "ipGstPsoAdm missing value";
		if ((this.ipGstRcc == null && in.ipGstRcc != null) || (this.ipGstRcc != null && in.ipGstRcc == null)) result = "ipGstRcc missing value";
		if ((this.ipGstRsd == null && in.ipGstRsd != null) || (this.ipGstRsd != null && in.ipGstRsd == null)) result = "ipGstRsd missing value";
		if ((this.ipGstVcsc == null && in.ipGstVcsc != null) || (this.ipGstVcsc != null && in.ipGstVcsc == null)) result = "ipGstVcsc missing value";

		return result;
	}
	
	public String RSCheck(Account in) {

		String result = null;

		if ((this.besc == null && in.besc != null) || (this.besc != null && in.besc == null)) result = "besc missing value";
		//if ((this.accountingEmcAdm == null && in.accountingEmcAdm != null) || (this.accountingEmcAdm != null && in.accountingEmcAdm == null)) result = "accountingEmcAdm missing value";
		if ((this.fcc == null && in.fcc != null) || (this.fcc != null && in.fcc == null)) result = "fcc missing value";
		if ((this.fsc == null && in.fsc != null) || (this.fsc != null && in.fsc == null)) result = "fsc missing value";
		//if ((this.gesc == null && in.gesc != null) || (this.gesc != null && in.gesc == null)) result = "gesc missing value";
		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.incGmee == null && in.incGmee != null) || (this.incGmee != null && in.incGmee == null)) result = "incGmee missing value";
		if ((this.incGmef == null && in.incGmef != null) || (this.incGmef != null && in.incGmef == null)) result = "incGmef missing value";
		if ((this.incLmee == null && in.incLmee != null) || (this.incLmee != null && in.incLmee == null)) result = "incLmee missing value";
		if ((this.incLmef == null && in.incLmef != null) || (this.incLmef != null && in.incLmef == null)) result = "incLmef missing value";
		if ((this.incNmea == null && in.incNmea != null) || (this.incNmea != null && in.incNmea == null)) result = "incNmea missing value";
		if ((this.ipGstFsc == null && in.ipGstFsc != null) || (this.ipGstFsc != null && in.ipGstFsc == null)) result = "ipGstFsc missing value";
		//if ((this.ipGstGesc == null && in.ipGstGesc != null) || (this.ipGstGesc != null && in.ipGstGesc == null)) result = "ipGstGesc missing value";
		if ((this.ipGstIncGmee == null && in.ipGstIncGmee != null) || (this.ipGstIncGmee != null && in.ipGstIncGmee == null)) result = "ipGstIncGmee missing value";
		if ((this.ipGstIncNmea == null && in.ipGstIncNmea != null) || (this.ipGstIncNmea != null && in.ipGstIncNmea == null)) result = "ipGstIncNmea missing value";
		//if ((this.ipGstLesd == null && in.ipGstLesd != null) || (this.ipGstLesd != null && in.ipGstLesd == null)) result = "ipGstLesd missing value";
		//if ((this.ipGstNasc == null && in.ipGstNasc != null) || (this.ipGstNasc != null && in.ipGstNasc == null)) result = "ipGstNasc missing value";
		//if ((this.ipGstNesc == null && in.ipGstNesc != null) || (this.ipGstNesc != null && in.ipGstNesc == null)) result = "ipGstNesc missing value";
		if ((this.ipGstNfsc == null && in.ipGstNfsc != null) || (this.ipGstNfsc != null && in.ipGstNfsc == null)) result = "ipGstNfsc missing value";
		//if ((this.ipGstNpsc == null && in.ipGstNpsc != null) || (this.ipGstNpsc != null && in.ipGstNpsc == null)) result = "ipGstNpsc missing value";
		if ((this.ipGstNrsc == null && in.ipGstNrsc != null) || (this.ipGstNrsc != null && in.ipGstNrsc == null)) result = "ipGstNrsc missing value";
		if ((this.ipGstRsc == null && in.ipGstRsc != null) || (this.ipGstRsc != null && in.ipGstRsc == null)) result = "ipGstRsc missing value";
		//if ((this.ipGstTotal == null && in.ipGstTotal != null) || (this.ipGstTotal != null && in.ipGstTotal == null)) result = "ipGstTotal missing value";
		//if ((this.neaa == null && in.neaa != null) || (this.neaa != null && in.neaa == null)) result = "neaa missing value";
		//if ((this.nead == null && in.nead != null) || (this.nead != null && in.nead == null)) result = "nead missing value";
		//if ((this.negc == null && in.negc != null) || (this.negc != null && in.negc == null)) result = "negc missing value";
		//if ((this.nelc == null && in.nelc != null) || (this.nelc != null && in.nelc == null)) result = "nelc missing value";
		//if ((this.npsc == null && in.npsc != null) || (this.npsc != null && in.npsc == null)) result = "npsc missing value";
		if ((this.nrsc == null && in.nrsc != null) || (this.nrsc != null && in.nrsc == null)) result = "nrsc missing value";
		if ((this.ntsc == null && in.ntsc != null) || (this.ntsc != null && in.ntsc == null)) result = "ntsc missing value";
		//if ((this.accountingOpGstEmcAdm == null && in.accountingOpGstEmcAdm != null) || (this.accountingOpGstEmcAdm != null && in.accountingOpGstEmcAdm == null)) result = "accountingOpGstEmcAdm missing value";
		//if ((this.opGstGesc == null && in.opGstGesc != null) || (this.opGstGesc != null && in.opGstGesc == null)) result = "opGstGesc missing value";
		if ((this.opGstIncGmef == null && in.opGstIncGmef != null) || (this.opGstIncGmef != null && in.opGstIncGmef == null)) result = "opGstIncGmef missing value";
		if ((this.opGstIncLmee == null && in.opGstIncLmee != null) || (this.opGstIncLmee != null && in.opGstIncLmee == null)) result = "opGstIncLmee missing value";
		if ((this.opGstIncLmef == null && in.opGstIncLmef != null) || (this.opGstIncLmef != null && in.opGstIncLmef == null)) result = "opGstIncLmef missing value";
		if ((this.opGstIncNmea == null && in.opGstIncNmea != null) || (this.opGstIncNmea != null && in.opGstIncNmea == null)) result = "opGstIncNmea missing value";
		if ((this.opGstNrsc == null && in.opGstNrsc != null) || (this.opGstNrsc != null && in.opGstNrsc == null)) result = "opGstNrsc missing value";
		//if ((this.accountingOpGstPsoAdm == null && in.accountingOpGstPsoAdm != null) || (this.accountingOpGstPsoAdm != null && in.accountingOpGstPsoAdm == null)) result = "accountingOpGstPsoAdm missing value";
		if ((this.opGstRsd == null && in.opGstRsd != null) || (this.opGstRsd != null && in.opGstRsd == null)) result = "opGstRsd missing value";
		//if ((this.accountingPsoAdm == null && in.accountingPsoAdm != null) || (this.accountingPsoAdm != null && in.accountingPsoAdm == null)) result = "accountingPsoAdm missing value";
		if ((this.rcc == null && in.rcc != null) || (this.rcc != null && in.rcc == null)) result = "rcc missing value";
		if ((this.rsc == null && in.rsc != null) || (this.rsc != null && in.rsc == null)) result = "rsc missing value";
		if ((this.rsd == null && in.rsd != null) || (this.rsd != null && in.rsd == null)) result = "rsd missing value";
		if ((this.totalBesc == null && in.totalBesc != null) || (this.totalBesc != null && in.totalBesc == null)) result = "totalBesc missing value";
		//if ((this.accountingTotalEmcAdm == null && in.accountingTotalEmcAdm != null) || (this.accountingTotalEmcAdm != null && in.accountingTotalEmcAdm == null)) result = "accountingTotalEmcAdm missing value";
		if ((this.totalFcc == null && in.totalFcc != null) || (this.totalFcc != null && in.totalFcc == null)) result = "totalFcc missing value";
		if ((this.totalFsc == null && in.totalFsc != null) || (this.totalFsc != null && in.totalFsc == null)) result = "totalFsc missing value";
		//if ((this.totalGesc == null && in.totalGesc != null) || (this.totalGesc != null && in.totalGesc == null)) result = "totalGesc missing value";
		//if ((this.totalNeaa == null && in.totalNeaa != null) || (this.totalNeaa != null && in.totalNeaa == null)) result = "totalNeaa missing value";
		//if ((this.totalNead == null && in.totalNead != null) || (this.totalNead != null && in.totalNead == null)) result = "totalNead missing value";
		//if ((this.totalNegc == null && in.totalNegc != null) || (this.totalNegc != null && in.totalNegc == null)) result = "totalNegc missing value";
		//if ((this.totalNelc == null && in.totalNelc != null) || (this.totalNelc != null && in.totalNelc == null)) result = "totalNelc missing value";
		//if ((this.totalNpsc == null && in.totalNpsc != null) || (this.totalNpsc != null && in.totalNpsc == null)) result = "totalNpsc missing value";
		if ((this.totalNrsc == null && in.totalNrsc != null) || (this.totalNrsc != null && in.totalNrsc == null)) result = "totalNrsc missing value";
		if ((this.totalNtsc == null && in.totalNtsc != null) || (this.totalNtsc != null && in.totalNtsc == null)) result = "totalNtsc missing value";
		if ((this.totalIncGmee == null && in.totalIncGmee != null) || (this.totalIncGmee != null && in.totalIncGmee == null)) result = "totalIncGmee missing value";
		if ((this.totalIncGmef == null && in.totalIncGmef != null) || (this.totalIncGmef != null && in.totalIncGmef == null)) result = "totalIncGmef missing value";
		if ((this.totalIncLmee == null && in.totalIncLmee != null) || (this.totalIncLmee != null && in.totalIncLmee == null)) result = "totalIncLmee missing value";
		if ((this.totalIncLmef == null && in.totalIncLmef != null) || (this.totalIncLmef != null && in.totalIncLmef == null)) result = "totalIncLmef missing value";
		if ((this.totalIncNmea == null && in.totalIncNmea != null) || (this.totalIncNmea != null && in.totalIncNmea == null)) result = "totalIncNmea missing value";
		//if ((this.accountingTotalPsoAdm == null && in.accountingTotalPsoAdm != null) || (this.accountingTotalPsoAdm != null && in.accountingTotalPsoAdm == null)) result = "accountingTotalPsoAdm missing value";
		if ((this.totalRcc == null && in.totalRcc != null) || (this.totalRcc != null && in.totalRcc == null)) result = "totalRcc missing value";
		if ((this.totalRsc == null && in.totalRsc != null) || (this.totalRsc != null && in.totalRsc == null)) result = "totalRsc missing value";
		if ((this.totalRsd == null && in.totalRsd != null) || (this.totalRsd != null && in.totalRsd == null)) result = "totalRsd missing value";
		//if ((this.otherTotal == null && in.otherTotal != null) || (this.otherTotal != null && in.otherTotal == null)) result = "otherTotal missing value";
		if ((this.facilityRsc == null && in.facilityRsc != null) || (this.facilityRsc != null && in.facilityRsc == null)) result = "facilityRsc missing value";
		if ((this.gmef == null && in.gmef != null) || (this.gmef != null && in.gmef == null)) result = "gmef missing value";
		if ((this.lmee == null && in.lmee != null) || (this.lmee != null && in.lmee == null)) result = "lmee missing value";
		if ((this.lmef == null && in.lmef != null) || (this.lmef != null && in.lmef == null)) result = "lmef missing value";
		if ((this.gmea == null && in.gmea != null) || (this.gmea != null && in.gmea == null)) result = "gmea missing value";
		if ((this.lmea == null && in.lmea != null) || (this.lmea != null && in.lmea == null)) result = "lmea missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";
		//if ((this.emcAdmCap == null && in.emcAdmCap != null) || (this.emcAdmCap != null && in.emcAdmCap == null)) result = "emcAdmCap missing value";
		//if ((this.emcAdmAdj == null && in.emcAdmAdj != null) || (this.emcAdmAdj != null && in.emcAdmAdj == null)) result = "emcAdmAdj missing value";
		//if ((this.admFeeCap == null && in.admFeeCap != null) || (this.admFeeCap != null && in.admFeeCap == null)) result = "admFeeCap missing value";
		//if ((this.admFeeAdj == null && in.admFeeAdj != null) || (this.admFeeAdj != null && in.admFeeAdj == null)) result = "admFeeAdj missing value";
		if ((this.totalFssc == null && in.totalFssc != null) || (this.totalFssc != null && in.totalFssc == null)) result = "totalFssc missing value";
		if ((this.fssc == null && in.fssc != null) || (this.fssc != null && in.fssc == null)) result = "fssc missing value";
		if ((this.opGstBesc == null && in.opGstBesc != null) || (this.opGstBesc != null && in.opGstBesc == null)) result = "opGstBesc missing value";
		if ((this.opGstFcc == null && in.opGstFcc != null) || (this.opGstFcc != null && in.opGstFcc == null)) result = "opGstFcc missing value";
		if ((this.opGstFsc == null && in.opGstFsc != null) || (this.opGstFsc != null && in.opGstFsc == null)) result = "opGstFsc missing value";
		if ((this.opGstGmee == null && in.opGstGmee != null) || (this.opGstGmee != null && in.opGstGmee == null)) result = "opGstGmee missing value";
		if ((this.opGstLcsc == null && in.opGstLcsc != null) || (this.opGstLcsc != null && in.opGstLcsc == null)) result = "opGstLcsc missing value";
		if ((this.opGstLmee == null && in.opGstLmee != null) || (this.opGstLmee != null && in.opGstLmee == null)) result = "opGstLmee missing value";
		if ((this.opGstLmef == null && in.opGstLmef != null) || (this.opGstLmef != null && in.opGstLmef == null)) result = "opGstLmef missing value";
		if ((this.opGstMeuc == null && in.opGstMeuc != null) || (this.opGstMeuc != null && in.opGstMeuc == null)) result = "opGstMeuc missing value";
		if ((this.opGstNeaa == null && in.opGstNeaa != null) || (this.opGstNeaa != null && in.opGstNeaa == null)) result = "opGstNeaa missing value";
		if ((this.opGstNead == null && in.opGstNead != null) || (this.opGstNead != null && in.opGstNead == null)) result = "opGstNead missing value";
		if ((this.opGstNegc == null && in.opGstNegc != null) || (this.opGstNegc != null && in.opGstNegc == null)) result = "opGstNegc missing value";
		if ((this.opGstNelc == null && in.opGstNelc != null) || (this.opGstNelc != null && in.opGstNelc == null)) result = "opGstNelc missing value";
		//if ((this.opGstNmea == null && in.opGstNmea != null) || (this.opGstNmea != null && in.opGstNmea == null)) result = "opGstNmea missing value";
		if ((this.opGstNtsc == null && in.opGstNtsc != null) || (this.opGstNtsc != null && in.opGstNtsc == null)) result = "opGstNtsc missing value";
		if ((this.opGstRcc == null && in.opGstRcc != null) || (this.opGstRcc != null && in.opGstRcc == null)) result = "opGstRcc missing value";
		if ((this.opGstRsc == null && in.opGstRsc != null) || (this.opGstRsc != null && in.opGstRsc == null)) result = "opGstRsc missing value";
		if ((this.opGstVcsc == null && in.opGstVcsc != null) || (this.opGstVcsc != null && in.opGstVcsc == null)) result = "opGstVcsc missing value";
		//if ((this.lcsc == null && in.lcsc != null) || (this.lcsc != null && in.lcsc == null)) result = "lcsc missing value";
		//if ((this.totalLcsc == null && in.totalLcsc != null) || (this.totalLcsc != null && in.totalLcsc == null)) result = "totalLcsc missing value";
		if ((this.ipGstBesc == null && in.ipGstBesc != null) || (this.ipGstBesc != null && in.ipGstBesc == null)) result = "ipGstBesc missing value";
		if ((this.ipGstEmcAdm == null && in.ipGstEmcAdm != null) || (this.ipGstEmcAdm != null && in.ipGstEmcAdm == null)) result = "ipGstEmcAdm missing value";
		if ((this.ipGstFcc == null && in.ipGstFcc != null) || (this.ipGstFcc != null && in.ipGstFcc == null)) result = "ipGstFcc missing value";
		if ((this.ipGstFsd == null && in.ipGstFsd != null) || (this.ipGstFsd != null && in.ipGstFsd == null)) result = "ipGstFsd missing value";
		if ((this.ipGstGmef == null && in.ipGstGmef != null) || (this.ipGstGmef != null && in.ipGstGmef == null)) result = "ipGstGmef missing value";
		if ((this.ipGstHeuc == null && in.ipGstHeuc != null) || (this.ipGstHeuc != null && in.ipGstHeuc == null)) result = "ipGstHeuc missing value";
		//if ((this.ipGstLcsc == null && in.ipGstLcsc != null) || (this.ipGstLcsc != null && in.ipGstLcsc == null)) result = "ipGstLcsc missing value";
		if ((this.ipGstLmee == null && in.ipGstLmee != null) || (this.ipGstLmee != null && in.ipGstLmee == null)) result = "ipGstLmee missing value";
		if ((this.ipGstLmef == null && in.ipGstLmef != null) || (this.ipGstLmef != null && in.ipGstLmef == null)) result = "ipGstLmef missing value";
		if ((this.ipGstMeuc == null && in.ipGstMeuc != null) || (this.ipGstMeuc != null && in.ipGstMeuc == null)) result = "ipGstMeuc missing value";
		if ((this.ipGstNeaa == null && in.ipGstNeaa != null) || (this.ipGstNeaa != null && in.ipGstNeaa == null)) result = "ipGstNeaa missing value";
		if ((this.ipGstNead == null && in.ipGstNead != null) || (this.ipGstNead != null && in.ipGstNead == null)) result = "ipGstNead missing value";
		if ((this.ipGstNegc == null && in.ipGstNegc != null) || (this.ipGstNegc != null && in.ipGstNegc == null)) result = "ipGstNegc missing value";
		if ((this.ipGstNelc == null && in.ipGstNelc != null) || (this.ipGstNelc != null && in.ipGstNelc == null)) result = "ipGstNelc missing value";
		if ((this.ipGstNtsc == null && in.ipGstNtsc != null) || (this.ipGstNtsc != null && in.ipGstNtsc == null)) result = "ipGstNtsc missing value";
//		if ((this.ipGstPsoAdm == null && in.ipGstPsoAdm != null) || (this.ipGstPsoAdm != null && in.ipGstPsoAdm == null)) result = "ipGstPsoAdm missing value";
		if ((this.ipGstRcc == null && in.ipGstRcc != null) || (this.ipGstRcc != null && in.ipGstRcc == null)) result = "ipGstRcc missing value";
		if ((this.ipGstRsd == null && in.ipGstRsd != null) || (this.ipGstRsd != null && in.ipGstRsd == null)) result = "ipGstRsd missing value";
		if ((this.ipGstVcsc == null && in.ipGstVcsc != null) || (this.ipGstVcsc != null && in.ipGstVcsc == null)) result = "ipGstVcsc missing value";

		return result;
	}

	public String equal(Account in) {

		String result = null;

		if (this.admFee != null && in.admFee != null) if (this.admFee.compareTo(in.admFee) != 0) result = "admFee value mismatch";
		if (this.besc != null && in.besc != null) if (this.besc.compareTo(in.besc) != 0) result = "besc value mismatch";
		if (this.accountingEmcAdm != null && in.accountingEmcAdm != null) if (this.accountingEmcAdm.compareTo(in.accountingEmcAdm) != 0) result = "accountingEmcAdm value mismatch";
		if (this.eua != null && in.eua != null) if (this.eua.compareTo(in.eua) != 0) result = "eua value mismatch";
		if (this.fcc != null && in.fcc != null) if (this.fcc.compareTo(in.fcc) != 0) result = "fcc value mismatch";
		if (this.fsc != null && in.fsc != null) if (this.fsc.compareTo(in.fsc) != 0) result = "fsc value mismatch";
		if (this.fsd != null && in.fsd != null) if (this.fsd.compareTo(in.fsd) != 0) result = "fsd value mismatch";
		if (this.gesc != null && in.gesc != null) if (this.gesc.compareTo(in.gesc) != 0) result = "gesc value mismatch";
		if (this.gmee != null && in.gmee != null) if (this.gmee.compareTo(in.gmee) != 0) result = "gmee value mismatch";
		if (this.heusa != null && in.heusa != null) if (this.heusa.compareTo(in.heusa) != 0) result = "heusa value mismatch";
		if (this.incGmee != null && in.incGmee != null) if (this.incGmee.compareTo(in.incGmee) != 0) result = "incGmee value mismatch";
		if (this.incGmef != null && in.incGmef != null) if (this.incGmef.compareTo(in.incGmef) != 0) result = "incGmef value mismatch";
		if (this.incLmee != null && in.incLmee != null) if (this.incLmee.compareTo(in.incLmee) != 0) result = "incLmee value mismatch";
		if (this.incLmef != null && in.incLmef != null) if (this.incLmef.compareTo(in.incLmef) != 0) result = "incLmef value mismatch";
		if (this.incNmea != null && in.incNmea != null) if (this.incNmea.compareTo(in.incNmea) != 0) result = "incNmea value mismatch";
		if (this.ipGstAdmFee != null && in.ipGstAdmFee != null) if (this.ipGstAdmFee.compareTo(in.ipGstAdmFee) != 0) result = "ipGstAdmFee value mismatch";
		if (this.ipGstFsc != null && in.ipGstFsc != null) if (this.ipGstFsc.compareTo(in.ipGstFsc) != 0) result = "ipGstFsc value mismatch";
		if (this.ipGstGesc != null && in.ipGstGesc != null) if (this.ipGstGesc.compareTo(in.ipGstGesc) != 0) result = "ipGstGesc value mismatch";
		if (this.ipGstIncGmee != null && in.ipGstIncGmee != null) if (this.ipGstIncGmee.compareTo(in.ipGstIncGmee) != 0) result = "ipGstIncGmee value mismatch";
		if (this.ipGstIncNmea != null && in.ipGstIncNmea != null) if (this.ipGstIncNmea.compareTo(in.ipGstIncNmea) != 0) result = "ipGstIncNmea value mismatch";
		if (this.ipGstLesd != null && in.ipGstLesd != null) if (this.ipGstLesd.compareTo(in.ipGstLesd) != 0) result = "ipGstLesd value mismatch";
		if (this.ipGstNasc != null && in.ipGstNasc != null) if (this.ipGstNasc.compareTo(in.ipGstNasc) != 0) result = "ipGstNasc value mismatch";
		if (this.ipGstNesc != null && in.ipGstNesc != null) if (this.ipGstNesc.compareTo(in.ipGstNesc) != 0) result = "ipGstNesc value mismatch";
		if (this.ipGstNfsc != null && in.ipGstNfsc != null) if (this.ipGstNfsc.compareTo(in.ipGstNfsc) != 0) result = "ipGstNfsc value mismatch";
		if (this.ipGstNpsc != null && in.ipGstNpsc != null) if (this.ipGstNpsc.compareTo(in.ipGstNpsc) != 0) result = "ipGstNpsc value mismatch";
		if (this.ipGstNrsc != null && in.ipGstNrsc != null) if (this.ipGstNrsc.compareTo(in.ipGstNrsc) != 0) result = "ipGstNrsc value mismatch";
		if (this.ipGstRsc != null && in.ipGstRsc != null) if (this.ipGstRsc.compareTo(in.ipGstRsc) != 0) result = "ipGstRsc value mismatch";
		if (this.ipGstTotal != null && in.ipGstTotal != null) if (this.ipGstTotal.compareTo(in.ipGstTotal) != 0) result = "ipGstTotal value mismatch";
		if (this.lesd != null && in.lesd != null) if (this.lesd.compareTo(in.lesd) != 0) result = "lesd value mismatch";
		if (this.meusa != null && in.meusa != null) if (this.meusa.compareTo(in.meusa) != 0) result = "meusa value mismatch";
		if (this.nasc != null && in.nasc != null) if (this.nasc.compareTo(in.nasc) != 0) result = "nasc value mismatch";
		if (this.neaa != null && in.neaa != null) if (this.neaa.compareTo(in.neaa) != 0) result = "neaa value mismatch";
		if (this.nead != null && in.nead != null) if (this.nead.compareTo(in.nead) != 0) result = "nead value mismatch";
		if (this.negc != null && in.negc != null) if (this.negc.compareTo(in.negc) != 0) result = "negc value mismatch";
		if (this.nelc != null && in.nelc != null) if (this.nelc.compareTo(in.nelc) != 0) result = "nelc value mismatch";
		if (this.nesc != null && in.nesc != null) if (this.nesc.compareTo(in.nesc) != 0) result = "nesc value mismatch";
		if (this.netAmount != null && in.netAmount != null) if (this.netAmount.compareTo(in.netAmount) != 0) result = "netAmount value mismatch";
		if (this.nfsc != null && in.nfsc != null) if (this.nfsc.compareTo(in.nfsc) != 0) result = "nfsc value mismatch";
		if (this.npsc != null && in.npsc != null) if (this.npsc.compareTo(in.npsc) != 0) result = "npsc value mismatch";
		if (this.nrsc != null && in.nrsc != null) if (this.nrsc.compareTo(in.nrsc) != 0) result = "nrsc value mismatch";
		if (this.ntsc != null && in.ntsc != null) if (this.ntsc.compareTo(in.ntsc) != 0) result = "ntsc value mismatch";
		if (this.accountingOpGstEmcAdm != null && in.accountingOpGstEmcAdm != null) if (this.accountingOpGstEmcAdm.compareTo(in.accountingOpGstEmcAdm) != 0) result = "accountingOpGstEmcAdm value mismatch";
		if (this.opGstFsd != null && in.opGstFsd != null) if (this.opGstFsd.compareTo(in.opGstFsd) != 0) result = "opGstFsd value mismatch";
		if (this.opGstGesc != null && in.opGstGesc != null) if (this.opGstGesc.compareTo(in.opGstGesc) != 0) result = "opGstGesc value mismatch";
		if (this.opGstHeusa != null && in.opGstHeusa != null) if (this.opGstHeusa.compareTo(in.opGstHeusa) != 0) result = "opGstHeusa value mismatch";
		if (this.opGstIncGmef != null && in.opGstIncGmef != null) if (this.opGstIncGmef.compareTo(in.opGstIncGmef) != 0) result = "opGstIncGmef value mismatch";
		if (this.opGstIncLmee != null && in.opGstIncLmee != null) if (this.opGstIncLmee.compareTo(in.opGstIncLmee) != 0) result = "opGstIncLmee value mismatch";
		if (this.opGstIncLmef != null && in.opGstIncLmef != null) if (this.opGstIncLmef.compareTo(in.opGstIncLmef) != 0) result = "opGstIncLmef value mismatch";
		if (this.opGstIncNmea != null && in.opGstIncNmea != null) if (this.opGstIncNmea.compareTo(in.opGstIncNmea) != 0) result = "opGstIncNmea value mismatch";
		if (this.opGstLesd != null && in.opGstLesd != null) if (this.opGstLesd.compareTo(in.opGstLesd) != 0) result = "opGstLesd value mismatch";
		if (this.opGstMeusa != null && in.opGstMeusa != null) if (this.opGstMeusa.compareTo(in.opGstMeusa) != 0) result = "opGstMeusa value mismatch";
		if (this.opGstNasc != null && in.opGstNasc != null) if (this.opGstNasc.compareTo(in.opGstNasc) != 0) result = "opGstNasc value mismatch";
		if (this.opGstNesc != null && in.opGstNesc != null) if (this.opGstNesc.compareTo(in.opGstNesc) != 0) result = "opGstNesc value mismatch";
		if (this.opGstNfsc != null && in.opGstNfsc != null) if (this.opGstNfsc.compareTo(in.opGstNfsc) != 0) result = "opGstNfsc value mismatch";
		if (this.opGstNpsc != null && in.opGstNpsc != null) if (this.opGstNpsc.compareTo(in.opGstNpsc) != 0) result = "opGstNpsc value mismatch";
		if (this.opGstNrsc != null && in.opGstNrsc != null) if (this.opGstNrsc.compareTo(in.opGstNrsc) != 0) result = "opGstNrsc value mismatch";
		if (this.accountingOpGstPsoAdm != null && in.accountingOpGstPsoAdm != null) if (this.accountingOpGstPsoAdm.compareTo(in.accountingOpGstPsoAdm) != 0) result = "accountingOpGstPsoAdm value mismatch";
		if (this.opGstRsd != null && in.opGstRsd != null) if (this.opGstRsd.compareTo(in.opGstRsd) != 0) result = "opGstRsd value mismatch";
		if (this.opGstTotal != null && in.opGstTotal != null) if (this.opGstTotal.compareTo(in.opGstTotal) != 0) result = "opGstTotal value mismatch";
		if (this.accountingPsoAdm != null && in.accountingPsoAdm != null) if (this.accountingPsoAdm.compareTo(in.accountingPsoAdm) != 0) result = "accountingPsoAdm value mismatch";
		if (this.rcc != null && in.rcc != null) if (this.rcc.compareTo(in.rcc) != 0) result = "rcc value mismatch";
		if (this.rsc != null && in.rsc != null) if (this.rsc.compareTo(in.rsc) != 0) result = "rsc value mismatch";
		if (this.rsd != null && in.rsd != null) if (this.rsd.compareTo(in.rsd) != 0) result = "rsd value mismatch";
		if (this.totalAdmFee != null && in.totalAdmFee != null) if (this.totalAdmFee.compareTo(in.totalAdmFee) != 0) result = "totalAdmFee value mismatch";
		if (this.totalAmount != null && in.totalAmount != null) if (this.totalAmount.compareTo(in.totalAmount) != 0) result = "totalAmount value mismatch";
		if (this.totalBesc != null && in.totalBesc != null) if (this.totalBesc.compareTo(in.totalBesc) != 0) result = "totalBesc value mismatch";
		if (this.accountingTotalEmcAdm != null && in.accountingTotalEmcAdm != null) if (this.accountingTotalEmcAdm.compareTo(in.accountingTotalEmcAdm) != 0) result = "accountingTotalEmcAdm value mismatch";
		if (this.totalFcc != null && in.totalFcc != null) if (this.totalFcc.compareTo(in.totalFcc) != 0) result = "totalFcc value mismatch";
		if (this.totalFsc != null && in.totalFsc != null) if (this.totalFsc.compareTo(in.totalFsc) != 0) result = "totalFsc value mismatch";
		if (this.totalFsd != null && in.totalFsd != null) if (this.totalFsd.compareTo(in.totalFsd) != 0) result = "totalFsd value mismatch";
		if (this.totalGesc != null && in.totalGesc != null) if (this.totalGesc.compareTo(in.totalGesc) != 0) result = "totalGesc value mismatch";
		//if (this.totalGmee != null && in.totalGmee != null) if (this.totalGmee.compareTo(in.totalGmee) != 0) result = "totalGmee value mismatch";
		if (this.totalHeusa != null && in.totalHeusa != null) if (this.totalHeusa.compareTo(in.totalHeusa) != 0) result = "totalHeusa value mismatch";
		if (this.totalLesd != null && in.totalLesd != null) if (this.totalLesd.compareTo(in.totalLesd) != 0) result = "totalLesd value mismatch";
		if (this.totalMeusa != null && in.totalMeusa != null) if (this.totalMeusa.compareTo(in.totalMeusa) != 0) result = "totalMeusa value mismatch";
		if (this.totalNasc != null && in.totalNasc != null) if (this.totalNasc.compareTo(in.totalNasc) != 0) result = "totalNasc value mismatch";
		if (this.totalNeaa != null && in.totalNeaa != null) if (this.totalNeaa.compareTo(in.totalNeaa) != 0) result = "totalNeaa value mismatch";
		if (this.totalNead != null && in.totalNead != null) if (this.totalNead.compareTo(in.totalNead) != 0) result = "totalNead value mismatch";
		if (this.totalNegc != null && in.totalNegc != null) if (this.totalNegc.compareTo(in.totalNegc) != 0) result = "totalNegc value mismatch";
		if (this.totalNelc != null && in.totalNelc != null) if (this.totalNelc.compareTo(in.totalNelc) != 0) result = "totalNelc value mismatch";
		if (this.totalNesc != null && in.totalNesc != null) if (this.totalNesc.compareTo(in.totalNesc) != 0) result = "totalNesc value mismatch";
		if (this.totalNfsc != null && in.totalNfsc != null) if (this.totalNfsc.compareTo(in.totalNfsc) != 0) result = "totalNfsc value mismatch";
		if (this.totalNpsc != null && in.totalNpsc != null) if (this.totalNpsc.compareTo(in.totalNpsc) != 0) result = "totalNpsc value mismatch";
		if (this.totalNrsc != null && in.totalNrsc != null) if (this.totalNrsc.compareTo(in.totalNrsc) != 0) result = "totalNrsc value mismatch";
		if (this.totalNtsc != null && in.totalNtsc != null) if (this.totalNtsc.compareTo(in.totalNtsc) != 0) result = "totalNtsc value mismatch";
		if (this.totalIncGmee != null && in.totalIncGmee != null) if (this.totalIncGmee.compareTo(in.totalIncGmee) != 0) result = "totalIncGmee value mismatch";
		if (this.totalIncGmef != null && in.totalIncGmef != null) if (this.totalIncGmef.compareTo(in.totalIncGmef) != 0) result = "totalIncGmef value mismatch";
		if (this.totalIncLmee != null && in.totalIncLmee != null) if (this.totalIncLmee.compareTo(in.totalIncLmee) != 0) result = "totalIncLmee value mismatch";
		if (this.totalIncLmef != null && in.totalIncLmef != null) if (this.totalIncLmef.compareTo(in.totalIncLmef) != 0) result = "totalIncLmef value mismatch";
		if (this.totalIncNmea != null && in.totalIncNmea != null) if (this.totalIncNmea.compareTo(in.totalIncNmea) != 0) result = "totalIncNmea value mismatch";
		if (this.accountingTotalPsoAdm != null && in.accountingTotalPsoAdm != null) if (this.accountingTotalPsoAdm.compareTo(in.accountingTotalPsoAdm) != 0) result = "accountingTotalPsoAdm value mismatch";
		if (this.totalRcc != null && in.totalRcc != null) if (this.totalRcc.compareTo(in.totalRcc) != 0) result = "totalRcc value mismatch";
		if (this.totalRsc != null && in.totalRsc != null) if (this.totalRsc.compareTo(in.totalRsc) != 0) result = "totalRsc value mismatch";
		if (this.totalRsd != null && in.totalRsd != null) if (this.totalRsd.compareTo(in.totalRsd) != 0) result = "totalRsd value mismatch";
		if (this.totalVcsc != null && in.totalVcsc != null) if (this.totalVcsc.compareTo(in.totalVcsc) != 0) result = "totalVcsc value mismatch";
		if (this.vcsc != null && in.vcsc != null) if (this.vcsc.compareTo(in.vcsc) != 0) result = "vcsc value mismatch";
		if (this.otherTotal != null && in.otherTotal != null) if (this.otherTotal.compareTo(in.otherTotal) != 0) result = "otherTotal value mismatch";
		if (this.facilityRsc != null && in.facilityRsc != null) if (this.facilityRsc.compareTo(in.facilityRsc) != 0) result = "facilityRsc value mismatch";
		if (this.gmef != null && in.gmef != null) if (this.gmef.compareTo(in.gmef) != 0) result = "gmef value mismatch";
		if (this.lmee != null && in.lmee != null) if (this.lmee.compareTo(in.lmee) != 0) result = "lmee value mismatch";
		if (this.lmef != null && in.lmef != null) if (this.lmef.compareTo(in.lmef) != 0) result = "lmef value mismatch";
		if (this.gmea != null && in.gmea != null) if (this.gmea.compareTo(in.gmea) != 0) result = "gmea value mismatch";
		if (this.lmea != null && in.lmea != null) if (this.lmea.compareTo(in.lmea) != 0) result = "lmea value mismatch";
		if (this.nmea != null && in.nmea != null) if (this.nmea.compareTo(in.nmea) != 0) result = "nmea value mismatch";
		if (this.emcAdmCap != null && in.emcAdmCap != null) if (this.emcAdmCap.compareTo(in.emcAdmCap) != 0) result = "emcAdmCap value mismatch";
		if (this.emcAdmAdj != null && in.emcAdmAdj != null) if (this.emcAdmAdj.compareTo(in.emcAdmAdj) != 0) result = "emcAdmAdj value mismatch";
		if (this.admFeeCap != null && in.admFeeCap != null) if (this.admFeeCap.compareTo(in.admFeeCap) != 0) result = "admFeeCap value mismatch";
		if (this.admFeeAdj != null && in.admFeeAdj != null) if (this.admFeeAdj.compareTo(in.admFeeAdj) != 0) result = "admFeeAdj value mismatch";
		if (this.totalFssc != null && in.totalFssc != null) if (this.totalFssc.compareTo(in.totalFssc) != 0) result = "totalFssc value mismatch";
		if (this.fssc != null && in.fssc != null) if (this.fssc.compareTo(in.fssc) != 0) result = "fssc value mismatch";
		if (this.opGstBesc != null && in.opGstBesc != null) if (this.opGstBesc.compareTo(in.opGstBesc) != 0) result = "opGstBesc value mismatch";
		if (this.opGstFcc != null && in.opGstFcc != null) if (this.opGstFcc.compareTo(in.opGstFcc) != 0) result = "opGstFcc value mismatch";
		if (this.opGstFsc != null && in.opGstFsc != null) if (this.opGstFsc.compareTo(in.opGstFsc) != 0) result = "opGstFsc value mismatch";
		//if (this.opGstGmee != null && in.opGstGmee != null) if (this.opGstGmee.compareTo(in.opGstGmee) != 0) result = "opGstGmee value mismatch";
		//if (this.opGstGmef != null && in.opGstGmef != null) if (this.opGstGmef.compareTo(in.opGstGmef) != 0) result = "opGstGmef value mismatch";
		if (this.opGstHeuc != null && in.opGstHeuc != null) if (this.opGstHeuc.compareTo(in.opGstHeuc) != 0) result = "opGstHeuc value mismatch";
		if (this.opGstLcsc != null && in.opGstLcsc != null) if (this.opGstLcsc.compareTo(in.opGstLcsc) != 0) result = "opGstLcsc value mismatch";
		//if (this.opGstLmee != null && in.opGstLmee != null) if (this.opGstLmee.compareTo(in.opGstLmee) != 0) result = "opGstLmee value mismatch";
		//if (this.opGstLmef != null && in.opGstLmef != null) if (this.opGstLmef.compareTo(in.opGstLmef) != 0) result = "opGstLmef value mismatch";
		if (this.opGstMeuc != null && in.opGstMeuc != null) if (this.opGstMeuc.compareTo(in.opGstMeuc) != 0) result = "opGstMeuc value mismatch";
		if (this.opGstNeaa != null && in.opGstNeaa != null) if (this.opGstNeaa.compareTo(in.opGstNeaa) != 0) result = "opGstNeaa value mismatch";
		if (this.opGstNead != null && in.opGstNead != null) if (this.opGstNead.compareTo(in.opGstNead) != 0) result = "opGstNead value mismatch";
		if (this.opGstNegc != null && in.opGstNegc != null) if (this.opGstNegc.compareTo(in.opGstNegc) != 0) result = "opGstNegc value mismatch";
		if (this.opGstNelc != null && in.opGstNelc != null) if (this.opGstNelc.compareTo(in.opGstNelc) != 0) result = "opGstNelc value mismatch";
		//if (this.opGstNmea != null && in.opGstNmea != null) if (this.opGstNmea.compareTo(in.opGstNmea) != 0) result = "opGstNmea value mismatch";
		if (this.opGstNtsc != null && in.opGstNtsc != null) if (this.opGstNtsc.compareTo(in.opGstNtsc) != 0) result = "opGstNtsc value mismatch";
		if (this.opGstRcc != null && in.opGstRcc != null) if (this.opGstRcc.compareTo(in.opGstRcc) != 0) result = "opGstRcc value mismatch";
		if (this.opGstRsc != null && in.opGstRsc != null) if (this.opGstRsc.compareTo(in.opGstRsc) != 0) result = "opGstRsc value mismatch";
		//if (this.opGstTotalGmef != null && in.opGstTotalGmef != null) if (this.opGstTotalGmef.compareTo(in.opGstTotalGmef) != 0) result = "opGstTotalGmef value mismatch";
		if (this.opGstVcsc != null && in.opGstVcsc != null) if (this.opGstVcsc.compareTo(in.opGstVcsc) != 0) result = "opGstVcsc value mismatch";
		if (this.heuc != null && in.heuc != null) if (this.heuc.compareTo(in.heuc) != 0) result = "heuc value mismatch";
		if (this.lcsc != null && in.lcsc != null) if (this.lcsc.compareTo(in.lcsc) != 0) result = "lcsc value mismatch";
		if (this.meuc != null && in.meuc != null) if (this.meuc.compareTo(in.meuc) != 0) result = "meuc value mismatch";
		if (this.totalHeuc != null && in.totalHeuc != null) if (this.totalHeuc.compareTo(in.totalHeuc) != 0) result = "totalHeuc value mismatch";
		if (this.totalLcsc != null && in.totalLcsc != null) if (this.totalLcsc.compareTo(in.totalLcsc) != 0) result = "totalLcsc value mismatch";
		//if (this.totalLmee != null && in.totalLmee != null) if (this.totalLmee.compareTo(in.totalLmee) != 0) result = "totalLmee value mismatch";
		//if (this.totalLmef != null && in.totalLmef != null) if (this.totalLmef.compareTo(in.totalLmef) != 0) result = "totalLmef value mismatch";
		if (this.totalMeuc != null && in.totalMeuc != null) if (this.totalMeuc.compareTo(in.totalMeuc) != 0) result = "totalMeuc value mismatch";
		//if (this.totalNmea != null && in.totalNmea != null) if (this.totalNmea.compareTo(in.totalNmea) != 0) result = "totalNmea value mismatch";
		if (this.ipGstBesc != null && in.ipGstBesc != null) if (this.ipGstBesc.compareTo(in.ipGstBesc) != 0) result = "ipGstBesc value mismatch";
		if (this.ipGstEmcAdm != null && in.ipGstEmcAdm != null) if (this.ipGstEmcAdm.compareTo(in.ipGstEmcAdm) != 0) result = "ipGstEmcAdm value mismatch";
		if (this.ipGstFcc != null && in.ipGstFcc != null) if (this.ipGstFcc.compareTo(in.ipGstFcc) != 0) result = "ipGstFcc value mismatch";
		if (this.ipGstFsd != null && in.ipGstFsd != null) if (this.ipGstFsd.compareTo(in.ipGstFsd) != 0) result = "ipGstFsd value mismatch";
		//if (this.ipGstGmee != null && in.ipGstGmee != null) if (this.ipGstGmee.compareTo(in.ipGstGmee) != 0) result = "ipGstGmee value mismatch";
		//if (this.ipGstGmef != null && in.ipGstGmef != null) if (this.ipGstGmef.compareTo(in.ipGstGmef) != 0) result = "ipGstGmef value mismatch";
		if (this.ipGstHeuc != null && in.ipGstHeuc != null) if (this.ipGstHeuc.compareTo(in.ipGstHeuc) != 0) result = "ipGstHeuc value mismatch";
		if (this.ipGstLcsc != null && in.ipGstLcsc != null) if (this.ipGstLcsc.compareTo(in.ipGstLcsc) != 0) result = "ipGstLcsc value mismatch";
		//if (this.ipGstLmee != null && in.ipGstLmee != null) if (this.ipGstLmee.compareTo(in.ipGstLmee) != 0) result = "ipGstLmee value mismatch";
		//if (this.ipGstLmef != null && in.ipGstLmef != null) if (this.ipGstLmef.compareTo(in.ipGstLmef) != 0) result = "ipGstLmef value mismatch";
		if (this.ipGstMeuc != null && in.ipGstMeuc != null) if (this.ipGstMeuc.compareTo(in.ipGstMeuc) != 0) result = "ipGstMeuc value mismatch";
		if (this.ipGstNeaa != null && in.ipGstNeaa != null) if (this.ipGstNeaa.compareTo(in.ipGstNeaa) != 0) result = "ipGstNeaa value mismatch";
		if (this.ipGstNead != null && in.ipGstNead != null) if (this.ipGstNead.compareTo(in.ipGstNead) != 0) result = "ipGstNead value mismatch";
		if (this.ipGstNegc != null && in.ipGstNegc != null) if (this.ipGstNegc.compareTo(in.ipGstNegc) != 0) result = "ipGstNegc value mismatch";
		if (this.ipGstNelc != null && in.ipGstNelc != null) if (this.ipGstNelc.compareTo(in.ipGstNelc) != 0) result = "ipGstNelc value mismatch";
		//if (this.ipGstNmea != null && in.ipGstNmea != null) if (this.ipGstNmea.compareTo(in.ipGstNmea) != 0) result = "ipGstNmea value mismatch";
		if (this.ipGstNtsc != null && in.ipGstNtsc != null) if (this.ipGstNtsc.compareTo(in.ipGstNtsc) != 0) result = "ipGstNtsc value mismatch";
		if (this.ipGstPsoAdm != null && in.ipGstPsoAdm != null) if (this.ipGstPsoAdm.compareTo(in.ipGstPsoAdm) != 0) result = "ipGstPsoAdm value mismatch";
		if (this.ipGstRcc != null && in.ipGstRcc != null) if (this.ipGstRcc.compareTo(in.ipGstRcc) != 0) result = "ipGstRcc value mismatch";
		if (this.ipGstRsd != null && in.ipGstRsd != null) if (this.ipGstRsd.compareTo(in.ipGstRsd) != 0) result = "ipGstRsd value mismatch";
		if (this.ipGstVcsc != null && in.ipGstVcsc != null) if (this.ipGstVcsc.compareTo(in.ipGstVcsc) != 0) result = "ipGstVcsc value mismatch";

		return result;
	}

	public String toInputString() {
		
		String result = (this.msslAccount == true? "1": "0") + "," +
				(this.netSett == true? "1": "0") + "," +
				(this.priceNeutralization == true? "1": "0") + "," +
				(this.emcAccount == true? "1": "0") + "," +
				(this.taxable == true? "1": "0") + "," +
				(this.intertie == true? "1": "0") + "," +
				(this.psoAccount == true? "1": "0") + "," +
				(this.egaRetailer == true? "1": "0") + "," +
				(this.underRetailer == true? "1": "0") + "," +
				(this.admFee != null? this.admFee.toString(): "") + "," +
				(this.besc != null? this.besc.toString(): "") + "," +
				(this.emcAdm != null? this.emcAdm.toString(): "") + "," +
				(this.eua != null? this.eua.toString(): "") + "," +
				(this.fcc != null? this.fcc.toString(): "") + "," +
				(this.fsc != null? this.fsc.toString(): "") + "," +
				(this.fsd != null? this.fsd.toString(): "") + "," +
				(this.gesc != null? this.gesc.toString(): "") + "," +
				(this.gmee != null? this.gmee.toString(): "") + "," +
				(this.heusa != null? this.heusa.toString(): "") + "," +
				(this.incGmee != null? this.incGmee.toString(): "") + "," +
				(this.incGmef != null? this.incGmef.toString(): "") + "," +
				(this.incLmee != null? this.incLmee.toString(): "") + "," +
				(this.incLmef != null? this.incLmef.toString(): "") + "," +
				(this.incNmea != null? this.incNmea.toString(): "") + "," +
				(this.ipGstAdmFee != null? this.ipGstAdmFee.toString(): "") + "," +
				(this.ipGstFsc != null? this.ipGstFsc.toString(): "") + "," +
				(this.ipGstGesc != null? this.ipGstGesc.toString(): "") + "," +
				(this.ipGstIncGmee != null? this.ipGstIncGmee.toString(): "") + "," +
				(this.ipGstIncNmea != null? this.ipGstIncNmea.toString(): "") + "," +
				(this.ipGstLesd != null? this.ipGstLesd.toString(): "") + "," +
				(this.ipGstNasc != null? this.ipGstNasc.toString(): "") + "," +
				(this.ipGstNesc != null? this.ipGstNesc.toString(): "") + "," +
				(this.ipGstNfsc != null? this.ipGstNfsc.toString(): "") + "," +
				(this.ipGstNpsc != null? this.ipGstNpsc.toString(): "") + "," +
				(this.ipGstNrsc != null? this.ipGstNrsc.toString(): "") + "," +
				(this.ipGstRsc != null? this.ipGstRsc.toString(): "") + "," +
				(this.ipGstTotal != null? this.ipGstTotal.toString(): "") + "," +
				(this.lesd != null? this.lesd.toString(): "") + "," +
				(this.meusa != null? this.meusa.toString(): "") + "," +
				this.accountId + "," +
				(this.nasc != null? this.nasc.toString(): "") + "," +
				(this.neaa != null? this.neaa.toString(): "") + "," +
				(this.nead != null? this.nead.toString(): "") + "," +
				(this.negc != null? this.negc.toString(): "") + "," +
				(this.nelc != null? this.nelc.toString(): "") + "," +
				(this.nesc != null? this.nesc.toString(): "") + "," +
				(this.netAmount != null? this.netAmount.toString(): "") + "," +
				(this.nfsc != null? this.nfsc.toString(): "") + "," +
				(this.npsc != null? this.npsc.toString(): "") + "," +
				(this.nrsc != null? this.nrsc.toString(): "") + "," +
				(this.ntsc != null? this.ntsc.toString(): "") + "," +
				(this.opGstEmcAdm != null? this.opGstEmcAdm.toString(): "") + "," +
				(this.opGstFsd != null? this.opGstFsd.toString(): "") + "," +
				(this.opGstGesc != null? this.opGstGesc.toString(): "") + "," +
				(this.opGstHeusa != null? this.opGstHeusa.toString(): "") + "," +
				(this.opGstIncGmef != null? this.opGstIncGmef.toString(): "") + "," +
				(this.opGstIncLmee != null? this.opGstIncLmee.toString(): "") + "," +
				(this.opGstIncLmef != null? this.opGstIncLmef.toString(): "") + "," +
				(this.opGstIncNmea != null? this.opGstIncNmea.toString(): "") + "," +
				(this.opGstLesd != null? this.opGstLesd.toString(): "") + "," +
				(this.opGstMeusa != null? this.opGstMeusa.toString(): "") + "," +
				(this.opGstNasc != null? this.opGstNasc.toString(): "") + "," +
				(this.opGstNesc != null? this.opGstNesc.toString(): "") + "," +
				(this.opGstNfsc != null? this.opGstNfsc.toString(): "") + "," +
				(this.opGstNpsc != null? this.opGstNpsc.toString(): "") + "," +
				(this.opGstNrsc != null? this.opGstNrsc.toString(): "") + "," +
				(this.opGstPsoAdm != null? this.opGstPsoAdm.toString(): "") + "," +
				(this.opGstRsd != null? this.opGstRsd.toString(): "") + "," +
				(this.opGstTotal != null? this.opGstTotal.toString(): "") + "," +
				(this.psoAdm != null? this.psoAdm.toString(): "") + "," +
				(this.rcc != null? this.rcc.toString(): "") + "," +
				(this.rsc != null? this.rsc.toString(): "") + "," +
				(this.rsd != null? this.rsd.toString(): "") + "," +
				this.runId + "," +
				(this.totalAdmFee != null? this.totalAdmFee.toString(): "") + "," +
				(this.totalAmount != null? this.totalAmount.toString(): "") + "," +
				(this.totalBesc != null? this.totalBesc.toString(): "") + "," +
				(this.totalEmcAdm != null? this.totalEmcAdm.toString(): "") + "," +
				(this.totalFcc != null? this.totalFcc.toString(): "") + "," +
				(this.totalFsc != null? this.totalFsc.toString(): "") + "," +
				(this.totalFsd != null? this.totalFsd.toString(): "") + "," +
				(this.totalGesc != null? this.totalGesc.toString(): "") + "," +
				(this.totalGmee != null? this.totalGmee.toString(): "") + "," +
				(this.totalHeusa != null? this.totalHeusa.toString(): "") + "," +
				(this.totalLesd != null? this.totalLesd.toString(): "") + "," +
				(this.totalMeusa != null? this.totalMeusa.toString(): "") + "," +
				(this.totalNasc != null? this.totalNasc.toString(): "") + "," +
				(this.totalNeaa != null? this.totalNeaa.toString(): "") + "," +
				(this.totalNead != null? this.totalNead.toString(): "") + "," +
				(this.totalNegc != null? this.totalNegc.toString(): "") + "," +
				(this.totalNelc != null? this.totalNelc.toString(): "") + "," +
				(this.totalNesc != null? this.totalNesc.toString(): "") + "," +
				(this.totalNfsc != null? this.totalNfsc.toString(): "") + "," +
				(this.totalNpsc != null? this.totalNpsc.toString(): "") + "," +
				(this.totalNrsc != null? this.totalNrsc.toString(): "") + "," +
				(this.totalNtsc != null? this.totalNtsc.toString(): "") + "," +
				(this.totalIncGmee != null? this.totalIncGmee.toString(): "") + "," +
				(this.totalIncGmef != null? this.totalIncGmef.toString(): "") + "," +
				(this.totalIncLmee != null? this.totalIncLmee.toString(): "") + "," +
				(this.totalIncLmef != null? this.totalIncLmef.toString(): "") + "," +
				(this.totalIncNmea != null? this.totalIncNmea.toString(): "") + "," +
				(this.totalPsoAdm != null? this.totalPsoAdm.toString(): "") + "," +
				(this.totalRcc != null? this.totalRcc.toString(): "") + "," +
				(this.totalRsc != null? this.totalRsc.toString(): "") + "," +
				(this.totalRsd != null? this.totalRsd.toString(): "") + "," +
				(this.totalVcsc != null? this.totalVcsc.toString(): "") + "," +
				(this.vcsc != null? this.vcsc.toString(): "") + "," +
				(this.otherTotal != null? this.otherTotal.toString(): "") + "," +
				(this.facilityRsc != null? this.facilityRsc.toString(): "") + "," +
				this.displayTitle + "," +
				(this.breached == true? "1": "0") + "," +
				this.participantId + "," +
				(this.residentialAccount == true? "1": "0");
		
		return result;
	}
	
	public String toOutputString() {
		
		String result = (this.msslAccount == true? "1": "0") + "," +
				(this.netSett == true? "1": "0") + "," +
				(this.priceNeutralization == true? "1": "0") + "," +
				(this.emcAccount == true? "1": "0") + "," +
				(this.taxable == true? "1": "0") + "," +
				(this.intertie == true? "1": "0") + "," +
				(this.psoAccount == true? "1": "0") + "," +
				(this.egaRetailer == true? "1": "0") + "," +
				(this.underRetailer == true? "1": "0") + "," +
				(this.admFee != null? this.admFee.toString(): "") + "," +
				(this.besc != null? this.besc.toString(): "") + "," +
				(this.accountingEmcAdm != null? this.accountingEmcAdm.toString(): "") + "," +
				(this.eua != null? this.eua.toString(): "") + "," +
				(this.fcc != null? this.fcc.toString(): "") + "," +
				(this.fsc != null? this.fsc.toString(): "") + "," +
				(this.fsd != null? this.fsd.toString(): "") + "," +
				(this.gesc != null? this.gesc.toString(): "") + "," +
				(this.gmee != null? this.gmee.toString(): "") + "," +
				(this.heusa != null? this.heusa.toString(): "") + "," +
				(this.incGmee != null? this.incGmee.toString(): "") + "," +
				(this.incGmef != null? this.incGmef.toString(): "") + "," +
				(this.incLmee != null? this.incLmee.toString(): "") + "," +
				(this.incLmef != null? this.incLmef.toString(): "") + "," +
				(this.incNmea != null? this.incNmea.toString(): "") + "," +
				(this.ipGstAdmFee != null? this.ipGstAdmFee.toString(): "") + "," +
				(this.ipGstFsc != null? this.ipGstFsc.toString(): "") + "," +
				(this.ipGstGesc != null? this.ipGstGesc.toString(): "") + "," +
				(this.ipGstIncGmee != null? this.ipGstIncGmee.toString(): "") + "," +
				(this.ipGstIncNmea != null? this.ipGstIncNmea.toString(): "") + "," +
				(this.ipGstLesd != null? this.ipGstLesd.toString(): "") + "," +
				(this.ipGstNasc != null? this.ipGstNasc.toString(): "") + "," +
				(this.ipGstNesc != null? this.ipGstNesc.toString(): "") + "," +
				(this.ipGstNfsc != null? this.ipGstNfsc.toString(): "") + "," +
				(this.ipGstNpsc != null? this.ipGstNpsc.toString(): "") + "," +
				(this.ipGstNrsc != null? this.ipGstNrsc.toString(): "") + "," +
				(this.ipGstRsc != null? this.ipGstRsc.toString(): "") + "," +
				(this.ipGstTotal != null? this.ipGstTotal.toString(): "") + "," +
				(this.lesd != null? this.lesd.toString(): "") + "," +
				(this.meusa != null? this.meusa.toString(): "") + "," +
				this.accountId + "," +
				(this.nasc != null? this.nasc.toString(): "") + "," +
				(this.neaa != null? this.neaa.toString(): "") + "," +
				(this.nead != null? this.nead.toString(): "") + "," +
				(this.negc != null? this.negc.toString(): "") + "," +
				(this.nelc != null? this.nelc.toString(): "") + "," +
				(this.nesc != null? this.nesc.toString(): "") + "," +
				(this.netAmount != null? this.netAmount.toString(): "") + "," +
				(this.nfsc != null? this.nfsc.toString(): "") + "," +
				(this.npsc != null? this.npsc.toString(): "") + "," +
				(this.nrsc != null? this.nrsc.toString(): "") + "," +
				(this.ntsc != null? this.ntsc.toString(): "") + "," +
				(this.accountingOpGstEmcAdm != null? this.accountingOpGstEmcAdm.toString(): "") + "," +
				(this.opGstFsd != null? this.opGstFsd.toString(): "") + "," +
				(this.opGstGesc != null? this.opGstGesc.toString(): "") + "," +
				(this.opGstHeusa != null? this.opGstHeusa.toString(): "") + "," +
				(this.opGstIncGmef != null? this.opGstIncGmef.toString(): "") + "," +
				(this.opGstIncLmee != null? this.opGstIncLmee.toString(): "") + "," +
				(this.opGstIncLmef != null? this.opGstIncLmef.toString(): "") + "," +
				(this.opGstIncNmea != null? this.opGstIncNmea.toString(): "") + "," +
				(this.opGstLesd != null? this.opGstLesd.toString(): "") + "," +
				(this.opGstMeusa != null? this.opGstMeusa.toString(): "") + "," +
				(this.opGstNasc != null? this.opGstNasc.toString(): "") + "," +
				(this.opGstNesc != null? this.opGstNesc.toString(): "") + "," +
				(this.opGstNfsc != null? this.opGstNfsc.toString(): "") + "," +
				(this.opGstNpsc != null? this.opGstNpsc.toString(): "") + "," +
				(this.opGstNrsc != null? this.opGstNrsc.toString(): "") + "," +
				(this.accountingOpGstPsoAdm != null? this.accountingOpGstPsoAdm.toString(): "") + "," +
				(this.opGstRsd != null? this.opGstRsd.toString(): "") + "," +
				(this.opGstTotal != null? this.opGstTotal.toString(): "") + "," +
				(this.accountingPsoAdm != null? this.accountingPsoAdm.toString(): "") + "," +
				(this.rcc != null? this.rcc.toString(): "") + "," +
				(this.rsc != null? this.rsc.toString(): "") + "," +
				(this.rsd != null? this.rsd.toString(): "") + "," +
				this.runId + "," +
				(this.totalAdmFee != null? this.totalAdmFee.toString(): "") + "," +
				(this.totalAmount != null? this.totalAmount.toString(): "") + "," +
				(this.totalBesc != null? this.totalBesc.toString(): "") + "," +
				(this.accountingTotalEmcAdm != null? this.accountingTotalEmcAdm.toString(): "") + "," +
				(this.totalFcc != null? this.totalFcc.toString(): "") + "," +
				(this.totalFsc != null? this.totalFsc.toString(): "") + "," +
				(this.totalFsd != null? this.totalFsd.toString(): "") + "," +
				(this.totalGesc != null? this.totalGesc.toString(): "") + "," +
				(this.totalGmee != null? this.totalGmee.toString(): "") + "," +
				(this.totalHeusa != null? this.totalHeusa.toString(): "") + "," +
				(this.totalLesd != null? this.totalLesd.toString(): "") + "," +
				(this.totalMeusa != null? this.totalMeusa.toString(): "") + "," +
				(this.totalNasc != null? this.totalNasc.toString(): "") + "," +
				(this.totalNeaa != null? this.totalNeaa.toString(): "") + "," +
				(this.totalNead != null? this.totalNead.toString(): "") + "," +
				(this.totalNegc != null? this.totalNegc.toString(): "") + "," +
				(this.totalNelc != null? this.totalNelc.toString(): "") + "," +
				(this.totalNesc != null? this.totalNesc.toString(): "") + "," +
				(this.totalNfsc != null? this.totalNfsc.toString(): "") + "," +
				(this.totalNpsc != null? this.totalNpsc.toString(): "") + "," +
				(this.totalNrsc != null? this.totalNrsc.toString(): "") + "," +
				(this.totalNtsc != null? this.totalNtsc.toString(): "") + "," +
				(this.totalIncGmee != null? this.totalIncGmee.toString(): "") + "," +
				(this.totalIncGmef != null? this.totalIncGmef.toString(): "") + "," +
				(this.totalIncLmee != null? this.totalIncLmee.toString(): "") + "," +
				(this.totalIncLmef != null? this.totalIncLmef.toString(): "") + "," +
				(this.totalIncNmea != null? this.totalIncNmea.toString(): "") + "," +
				(this.accountingTotalPsoAdm != null? this.accountingTotalPsoAdm.toString(): "") + "," +
				(this.totalRcc != null? this.totalRcc.toString(): "") + "," +
				(this.totalRsc != null? this.totalRsc.toString(): "") + "," +
				(this.totalRsd != null? this.totalRsd.toString(): "") + "," +
				(this.totalVcsc != null? this.totalVcsc.toString(): "") + "," +
				(this.vcsc != null? this.vcsc.toString(): "") + "," +
				(this.otherTotal != null? this.otherTotal.toString(): "") + "," +
				(this.facilityRsc != null? this.facilityRsc.toString(): "") + "," +
				this.displayTitle + "," +
				(this.gmef != null? this.gmef.toString(): "") + "," +
				(this.lmee != null? this.lmee.toString(): "") + "," +
				(this.lmef != null? this.lmef.toString(): "") + "," +
				(this.gmea != null? this.gmea.toString(): "") + "," +
				(this.lmea != null? this.lmea.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				(this.emcAdmCap != null? this.emcAdmCap.toString(): "") + "," +
				(this.emcAdmAdj != null? this.emcAdmAdj.toString(): "") + "," +
				(this.admFeeCap != null? this.admFeeCap.toString(): "") + "," +
				(this.admFeeAdj != null? this.admFeeAdj.toString(): "") + "," +
				(this.breached == true? "1": "0") + "," +
				(this.totalFssc != null? this.totalFssc.toString(): "") + "," +
				(this.fssc != null? this.fssc.toString(): "") + "," +
				this.participantId + "," +
				(this.opGstBesc != null? this.opGstBesc.toString(): "") + "," +
				(this.opGstFcc != null? this.opGstFcc.toString(): "") + "," +
				(this.opGstFsc != null? this.opGstFsc.toString(): "") + "," +
				(this.opGstGmee != null? this.opGstGmee.toString(): "") + "," +
				(this.opGstGmef != null? this.opGstGmef.toString(): "") + "," +
				(this.opGstHeuc != null? this.opGstHeuc.toString(): "") + "," +
				(this.opGstLcsc != null? this.opGstLcsc.toString(): "") + "," +
				(this.opGstLmee != null? this.opGstLmee.toString(): "") + "," +
				(this.opGstLmef != null? this.opGstLmef.toString(): "") + "," +
				(this.opGstMeuc != null? this.opGstMeuc.toString(): "") + "," +
				(this.opGstNeaa != null? this.opGstNeaa.toString(): "") + "," +
				(this.opGstNead != null? this.opGstNead.toString(): "") + "," +
				(this.opGstNegc != null? this.opGstNegc.toString(): "") + "," +
				(this.opGstNelc != null? this.opGstNelc.toString(): "") + "," +
				(this.opGstNmea != null? this.opGstNmea.toString(): "") + "," +
				(this.opGstNtsc != null? this.opGstNtsc.toString(): "") + "," +
				(this.opGstRcc != null? this.opGstRcc.toString(): "") + "," +
				(this.opGstRsc != null? this.opGstRsc.toString(): "") + "," +
				(this.opGstTotalGmef != null? this.opGstTotalGmef.toString(): "") + "," +
				(this.opGstVcsc != null? this.opGstVcsc.toString(): "") + "," +
				(this.heuc != null? this.heuc.toString(): "") + "," +
				(this.lcsc != null? this.lcsc.toString(): "") + "," +
				(this.meuc != null? this.meuc.toString(): "") + "," +
				(this.totalHeuc != null? this.totalHeuc.toString(): "") + "," +
				(this.totalLcsc != null? this.totalLcsc.toString(): "") + "," +
				(this.totalLmee != null? this.totalLmee.toString(): "") + "," +
				(this.totalLmef != null? this.totalLmef.toString(): "") + "," +
				(this.totalMeuc != null? this.totalMeuc.toString(): "") + "," +
				(this.totalNmea != null? this.totalNmea.toString(): "") + "," +
				(this.ipGstBesc != null? this.ipGstBesc.toString(): "") + "," +
				(this.ipGstEmcAdm != null? this.ipGstEmcAdm.toString(): "") + "," +
				(this.ipGstFcc != null? this.ipGstFcc.toString(): "") + "," +
				(this.ipGstFsd != null? this.ipGstFsd.toString(): "") + "," +
				(this.ipGstGmee != null? this.ipGstGmee.toString(): "") + "," +
				(this.ipGstGmef != null? this.ipGstGmef.toString(): "") + "," +
				(this.ipGstHeuc != null? this.ipGstHeuc.toString(): "") + "," +
				(this.ipGstLcsc != null? this.ipGstLcsc.toString(): "") + "," +
				(this.ipGstLmee != null? this.ipGstLmee.toString(): "") + "," +
				(this.ipGstLmef != null? this.ipGstLmef.toString(): "") + "," +
				(this.ipGstMeuc != null? this.ipGstMeuc.toString(): "") + "," +
				(this.ipGstNeaa != null? this.ipGstNeaa.toString(): "") + "," +
				(this.ipGstNead != null? this.ipGstNead.toString(): "") + "," +
				(this.ipGstNegc != null? this.ipGstNegc.toString(): "") + "," +
				(this.ipGstNelc != null? this.ipGstNelc.toString(): "") + "," +
				(this.ipGstNmea != null? this.ipGstNmea.toString(): "") + "," +
				(this.ipGstNtsc != null? this.ipGstNtsc.toString(): "") + "," +
				(this.ipGstPsoAdm != null? this.ipGstPsoAdm.toString(): "") + "," +
				(this.ipGstRcc != null? this.ipGstRcc.toString(): "") + "," +
				(this.ipGstRsd != null? this.ipGstRsd.toString(): "") + "," +				
				(this.ipGstVcsc != null? this.ipGstVcsc.toString(): "") + "," +
				(this.residentialAccount == true? "1": "0");
		
		return result;
	}
	
	public static String getInputHeader() {
		String header = 
			"msslAccount," +
			"netSett," +
			"priceNeutralization," +
			"emcAccount," +
			"taxable," +
			"intertie," +
			"psoAccount," +
			"egaRetailer," +
			"underRetailer," +
			"admFee," +
			"besc," +
			"emcAdm," +
			"eua," +
			"fcc," +
			"fsc," +
			"fsd," +
			"gesc," +
			"gmee," +
			"heusa," +
			"incGmee," +
			"incGmef," +
			"incLmee," +
			"incLmef," +
			"incNmea," +
			"ipGstAdmFee," +
			"ipGstFsc," +
			"ipGstGesc," +
			"ipGstIncGmee," +
			"ipGstIncNmea," +
			"ipGstLesd," +
			"ipGstNasc," +
			"ipGstNesc," +
			"ipGstNfsc," +
			"ipGstNpsc," +
			"ipGstNrsc," +
			"ipGstRsc," +
			"ipGstTotal," +
			"lesd," +
			"meusa," +
			"accountId," +
			"nasc," +
			"neaa," +
			"nead," +
			"negc," +
			"nelc," +
			"nesc," +
			"netAmount," +
			"nfsc," +
			"npsc," +
			"nrsc," +
			"ntsc," +
			"opGstEmcAdm," +
			"opGstFsd," +
			"opGstGesc," +
			"opGstHeusa," +
			"opGstIncGmef," +
			"opGstIncLmee," +
			"opGstIncLmef," +
			"opGstIncNmea," +
			"opGstLesd," +
			"opGstMeusa," +
			"opGstNasc," +
			"opGstNesc," +
			"opGstNfsc," +
			"opGstNpsc," +
			"opGstNrsc," +
			"opGstPsoAdm," +
			"opGstRsd," +
			"opGstTotal," +
			"psoAdm," +
			"rcc," +
			"rsc," +
			"rsd," +
			"runId," +
			"totalAdmFee," +
			"totalAmount," +
			"totalBesc," +
			"totalEmcAdm," +
			"totalFcc," +
			"totalFsc," +
			"totalFsd," +
			"totalGesc," +
			"totalGmee," +
			"totalHeusa," +
			"totalLesd," +
			"totalMeusa," +
			"totalNasc," +
			"totalNeaa," +
			"totalNead," +
			"totalNegc," +
			"totalNelc," +
			"totalNesc," +
			"totalNfsc," +
			"totalNpsc," +
			"totalNrsc," +
			"totalNtsc," +
			"totalIncGmee," +
			"totalIncGmef," +
			"totalIncLmee," +
			"totalIncLmef," +
			"totalIncNmea," +
			"totalPsoAdm," +
			"totalRcc," +
			"totalRsc," +
			"totalRsd," +
			"totalVcsc," +
			"vcsc," +
			"otherTotal," +
			"facilityRsc," +
			"displayTitle," +
			"breached," +
			"participantId," +
			"residentialAccount";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"msslAccount," +
			"netSett," +
			"priceNeutralization," +
			"emcAccount," +
			"taxable," +
			"intertie," +
			"psoAccount," +
			"egaRetailer," +
			"underRetailer," +
			"admFee," +
			"besc," +
			"accountingEmcAdm," +
			"eua," +
			"fcc," +
			"fsc," +
			"fsd," +
			"gesc," +
			"gmee," +
			"heusa," +
			"incGmee," +
			"incGmef," +
			"incLmee," +
			"incLmef," +
			"incNmea," +
			"ipGstAdmFee," +
			"ipGstFsc," +
			"ipGstGesc," +
			"ipGstIncGmee," +
			"ipGstIncNmea," +
			"ipGstLesd," +
			"ipGstNasc," +
			"ipGstNesc," +
			"ipGstNfsc," +
			"ipGstNpsc," +
			"ipGstNrsc," +
			"ipGstRsc," +
			"ipGstTotal," +
			"lesd," +
			"meusa," +
			"accountId," +
			"nasc," +
			"neaa," +
			"nead," +
			"negc," +
			"nelc," +
			"nesc," +
			"netAmount," +
			"nfsc," +
			"npsc," +
			"nrsc," +
			"ntsc," +
			"accountingOpGstEmcAdm," +
			"opGstFsd," +
			"opGstGesc," +
			"opGstHeusa," +
			"opGstIncGmef," +
			"opGstIncLmee," +
			"opGstIncLmef," +
			"opGstIncNmea," +
			"opGstLesd," +
			"opGstMeusa," +
			"opGstNasc," +
			"opGstNesc," +
			"opGstNfsc," +
			"opGstNpsc," +
			"opGstNrsc," +
			"accountingOpGstPsoAdm," +
			"opGstRsd," +
			"opGstTotal," +
			"accountingPsoAdm," +
			"rcc," +
			"rsc," +
			"rsd," +
			"runId," +
			"totalAdmFee," +
			"totalAmount," +
			"totalBesc," +
			"accountingTotalEmcAdm," +
			"totalFcc," +
			"totalFsc," +
			"totalFsd," +
			"totalGesc," +
			"totalGmee," +
			"totalHeusa," +
			"totalLesd," +
			"totalMeusa," +
			"totalNasc," +
			"totalNeaa," +
			"totalNead," +
			"totalNegc," +
			"totalNelc," +
			"totalNesc," +
			"totalNfsc," +
			"totalNpsc," +
			"totalNrsc," +
			"totalNtsc," +
			"totalIncGmee," +
			"totalIncGmef," +
			"totalIncLmee," +
			"totalIncLmef," +
			"totalIncNmea," +
			"accountingTotalPsoAdm," +
			"totalRcc," +
			"totalRsc," +
			"totalRsd," +
			"totalVcsc," +
			"vcsc," +
			"otherTotal," +
			"facilityRsc," +
			"displayTitle," +
			"gmef," +
			"lmee," +
			"lmef," +
			"gmea," +
			"lmea," +
			"nmea," +
			"emcAdmCap," +
			"emcAdmAdj," +
			"admFeeCap," +
			"admFeeAdj," +
			"breached," +
			"totalFssc," +
			"fssc," +
			"participantId," +
			"opGstBesc," +
			"opGstFcc," +
			"opGstFsc," +
			"opGstGmee," +
			"opGstGmef," +
			"opGstHeuc," +
			"opGstLcsc," +
			"opGstLmee," +
			"opGstLmef," +
			"opGstMeuc," +
			"opGstNeaa," +
			"opGstNead," +
			"opGstNegc," +
			"opGstNelc," +
			"opGstNmea," +
			"opGstNtsc," +
			"opGstRcc," +
			"opGstRsc," +
			"opGstTotalGmef," +
			"opGstVcsc," +
			"heuc," +
			"lcsc," +
			"meuc," +
			"totalHeuc," +
			"totalLcsc," +
			"totalLmee," +
			"totalLmef," +
			"totalMeuc," +
			"totalNmea," +
			"ipGstBesc," +
			"ipGstEmcAdm," +
			"ipGstFcc," +
			"ipGstFsd," +
			"ipGstGmee," +
			"ipGstGmef," +
			"ipGstHeuc," +
			"ipGstLcsc," +
			"ipGstLmee," +
			"ipGstLmef," +
			"ipGstMeuc," +
			"ipGstNeaa," +
			"ipGstNead," +
			"ipGstNegc," +
			"ipGstNelc," +
			"ipGstNmea," +
			"ipGstNtsc," +
			"ipGstPsoAdm," +
			"ipGstRcc," +
			"ipGstRsd," +
			"ipGstVcsc," +
			"residentialAccount";

		return header;
	}
}
