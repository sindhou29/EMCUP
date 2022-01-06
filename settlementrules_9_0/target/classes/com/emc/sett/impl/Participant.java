package com.emc.sett.impl;

import java.math.BigDecimal;

import com.emc.sett.model.ParticipantT;

public class Participant extends ParticipantT {
	
	public void initInput(String [] line) {
		
		String mp_name = line[0];
		String mp_emc = line[1];
		String mp_pso = line[2];
		String mp_mssl = line[3];
		String mp_taxable = line[4];
		String mp_rerun_gst_rate = line[5];
		
		this.participantId = mp_name;
		this.emcAccount = mp_emc.equals("1");
		this.psoAccount = mp_pso.equals("1");
		this.msslAccount = mp_mssl.equals("1");
		this.taxable = mp_taxable.equals("1");
		this.rerunGstRate = mp_rerun_gst_rate.length() > 0? new BigDecimal(mp_rerun_gst_rate): null;
	}
	
	public String getKey() {
		return participantId;
	}

	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) {
		
		String mp_a_besc = line[0];
		String mp_a_emcadm = line[1];
		String mp_a_fcc = line[2];
		String mp_a_fsc = line[3];
		String mp_a_fsd = line[4];
		String mp_a_gesc = line[5];
		String mp_a_gmee = line[6];
		String mp_a_gmef = line[7];
		String mp_a_heuc = line[8];
		String mp_a_lcsc = line[9];
		String mp_a_lesd = line[10];
		String mp_a_lmee = line[11];
		String mp_a_lmef = line[12];
		String mp_a_meuc = line[13];
		String mp_a_nasc = line[14];
		String mp_a_neaa = line[15];
		String mp_a_nead = line[16];
		String mp_a_negc = line[17];
		String mp_a_nelc = line[18];
		String mp_a_nesc = line[19];
		String mp_a_nfsc = line[20];
		String mp_a_nmea = line[21];
		String mp_a_npsc = line[22];
		String mp_a_nrsc = line[23];
		String mp_a_ntsc = line[24];
		String mp_a_psoadm = line[25];
		String mp_a_rcc = line[26];
		String mp_a_rsc = line[27];
		String mp_a_rsd = line[28];
		String mp_a_total = line[29];
		String mp_a_vcsc = line[30];
		String mp_besc = line[31];
		String mp_emcadm = line[32];
		String mp_fcc = line[33];
		String mp_fsc = line[34];
		String mp_fsd = line[35];
		String mp_gesc = line[36];
		String mp_gmee = line[37];
		String mp_gmef = line[38];
		String mp_heuc = line[39];
		String mp_lcsc = line[40];
		String mp_lesd = line[41];
		String mp_lmee = line[42];
		String mp_lmef = line[43];
		String mp_meuc = line[44];
		String mp_name = line[45];
		String mp_nasc = line[46];
		String mp_neaa = line[47];
		String mp_nead = line[48];
		String mp_negc = line[49];
		String mp_nelc = line[50];
		String mp_nesc = line[51];
		String mp_net_total = line[52];
		String mp_nfsc = line[53];
		String mp_nmea = line[54];
		String mp_npsc = line[55];
		String mp_nrsc = line[56];
		String mp_ntsc = line[57];
		String mp_psoadm = line[58];
		String mp_rcc = line[59];
		String mp_rsc = line[60];
		String mp_rsd = line[61];
		String mp_total_amount = line[62];
		String mp_total_besc = line[63];
		String mp_total_emcadm = line[64];
		String mp_total_fcc = line[65];
		String mp_total_fsc = line[66];
		String mp_total_fsd = line[67];
		String mp_total_gesc = line[68];
		String mp_total_gmee = line[69];
		String mp_total_gmef = line[70];
		String mp_total_heuc = line[71];
		String mp_total_lcsc = line[72];
		String mp_total_lesd = line[73];
		String mp_total_lmee = line[74];
		String mp_total_lmef = line[75];
		String mp_total_meuc = line[76];
		String mp_total_nasc = line[77];
		String mp_total_neaa = line[78];
		String mp_total_nead = line[79];
		String mp_total_negc = line[80];
		String mp_total_nelc = line[81];
		String mp_total_nesc = line[82];
		String mp_total_nfsc = line[83];
		String mp_total_nmea = line[84];
		String mp_total_npsc = line[85];
		String mp_total_nrsc = line[86];
		String mp_total_ntsc = line[87];
		String mp_total_psoadm = line[88];
		String mp_total_rcc = line[89];
		String mp_total_rsc = line[90];
		String mp_total_rsd = line[91];
		String mp_total_vcsc = line[92];
		String mp_v_besc = line[93];
		String mp_v_emcadm = line[94];
		String mp_v_fcc = line[95];
		String mp_v_fsc = line[96];
		String mp_v_fsd = line[97];
		String mp_v_gesc = line[98];
		String mp_v_gmee = line[99];
		String mp_v_gmef = line[100];
		String mp_v_heuc = line[101];
		String mp_v_lcsc = line[102];
		String mp_v_lesd = line[103];
		String mp_v_lmee = line[104];
		String mp_v_lmef = line[105];
		String mp_v_meuc = line[106];
		String mp_v_nasc = line[107];
		String mp_v_neaa = line[108];
		String mp_v_nead = line[109];
		String mp_v_negc = line[110];
		String mp_v_nelc = line[111];
		String mp_v_nesc = line[112];
		String mp_v_nfsc = line[113];
		String mp_v_nmea = line[114];
		String mp_v_npsc = line[115];
		String mp_v_nrsc = line[116];
		String mp_v_ntsc = line[117];
		String mp_v_psoadm = line[118];
		String mp_v_rcc = line[119];
		String mp_v_rsc = line[120];
		String mp_v_rsd = line[121];
		String mp_v_total = line[122];
		String mp_v_vcsc = line[123];
		String mp_vcsc = line[124];
		String mp_emc = line[125];
		String mp_pso = line[126];
		String mp_mssl = line[127];
		String mp_taxable = line[128];
		String mp_rerun_gst_rate = line[129];
		String mp_fssc = line[130];
		String mp_v_fssc = line[131];
		String mp_a_fssc = line[132];
		String mp_total_fssc = line[133];
		
		this.opGstBesc = mp_a_besc.length() > 0? new BigDecimal(mp_a_besc): null;
		this.opGstEmcAdm = mp_a_emcadm.length() > 0? new BigDecimal(mp_a_emcadm): null;
		this.opGstFcc = mp_a_fcc.length() > 0? new BigDecimal(mp_a_fcc): null;
		this.opGstFsc = mp_a_fsc.length() > 0? new BigDecimal(mp_a_fsc): null;
		this.opGstFsd = mp_a_fsd.length() > 0? new BigDecimal(mp_a_fsd): null;
		this.opGstGesc = mp_a_gesc.length() > 0? new BigDecimal(mp_a_gesc): null;
		this.opGstGmee = mp_a_gmee.length() > 0? new BigDecimal(mp_a_gmee): null;
		this.opGstGmef = mp_a_gmef.length() > 0? new BigDecimal(mp_a_gmef): null;
		this.opGstHeuc = mp_a_heuc.length() > 0? new BigDecimal(mp_a_heuc): null;
		this.opGstLcsc = mp_a_lcsc.length() > 0? new BigDecimal(mp_a_lcsc): null;
		this.opGstLesd = mp_a_lesd.length() > 0? new BigDecimal(mp_a_lesd): null;
		this.opGstLmee = mp_a_lmee.length() > 0? new BigDecimal(mp_a_lmee): null;
		this.opGstLmef = mp_a_lmef.length() > 0? new BigDecimal(mp_a_lmef): null;
		this.opGstMeuc = mp_a_meuc.length() > 0? new BigDecimal(mp_a_meuc): null;
		this.opGstNasc = mp_a_nasc.length() > 0? new BigDecimal(mp_a_nasc): null;
		this.opGstNeaa = mp_a_neaa.length() > 0? new BigDecimal(mp_a_neaa): null;
		this.opGstNead = mp_a_nead.length() > 0? new BigDecimal(mp_a_nead): null;
		this.opGstNegc = mp_a_negc.length() > 0? new BigDecimal(mp_a_negc): null;
		this.opGstNelc = mp_a_nelc.length() > 0? new BigDecimal(mp_a_nelc): null;
		this.opGstNesc = mp_a_nesc.length() > 0? new BigDecimal(mp_a_nesc): null;
		this.opGstNfsc = mp_a_nfsc.length() > 0? new BigDecimal(mp_a_nfsc): null;
		this.opGstNmea = mp_a_nmea.length() > 0? new BigDecimal(mp_a_nmea): null;
		this.opGstNpsc = mp_a_npsc.length() > 0? new BigDecimal(mp_a_npsc): null;
		this.opGstNrsc = mp_a_nrsc.length() > 0? new BigDecimal(mp_a_nrsc): null;
		this.opGstNtsc = mp_a_ntsc.length() > 0? new BigDecimal(mp_a_ntsc): null;
		this.opGstPsoAdm = mp_a_psoadm.length() > 0? new BigDecimal(mp_a_psoadm): null;
		this.opGstRcc = mp_a_rcc.length() > 0? new BigDecimal(mp_a_rcc): null;
		this.opGstRsc = mp_a_rsc.length() > 0? new BigDecimal(mp_a_rsc): null;
		this.opGstRsd = mp_a_rsd.length() > 0? new BigDecimal(mp_a_rsd): null;
		this.opGstTotal = mp_a_total.length() > 0? new BigDecimal(mp_a_total): null;
		this.opGstVcsc = mp_a_vcsc.length() > 0? new BigDecimal(mp_a_vcsc): null;
		this.besc = mp_besc.length() > 0? new BigDecimal(mp_besc): null;
		this.emcAdm = mp_emcadm.length() > 0? new BigDecimal(mp_emcadm): null;
		this.fcc = mp_fcc.length() > 0? new BigDecimal(mp_fcc): null;
		this.fsc = mp_fsc.length() > 0? new BigDecimal(mp_fsc): null;
		this.fsd = mp_fsd.length() > 0? new BigDecimal(mp_fsd): null;
		this.gesc = mp_gesc.length() > 0? new BigDecimal(mp_gesc): null;
		this.gmee = mp_gmee.length() > 0? new BigDecimal(mp_gmee): null;
		this.gmef = mp_gmef.length() > 0? new BigDecimal(mp_gmef): null;
		this.heuc = mp_heuc.length() > 0? new BigDecimal(mp_heuc): null;
		this.lcsc = mp_lcsc.length() > 0? new BigDecimal(mp_lcsc): null;
		this.lesd = mp_lesd.length() > 0? new BigDecimal(mp_lesd): null;
		this.lmee = mp_lmee.length() > 0? new BigDecimal(mp_lmee): null;
		this.lmef = mp_lmef.length() > 0? new BigDecimal(mp_lmef): null;
		this.meuc = mp_meuc.length() > 0? new BigDecimal(mp_meuc): null;
		this.participantId = mp_name;
		this.nasc = mp_nasc.length() > 0? new BigDecimal(mp_nasc): null;
		this.neaa = mp_neaa.length() > 0? new BigDecimal(mp_neaa): null;
		this.nead = mp_nead.length() > 0? new BigDecimal(mp_nead): null;
		this.negc = mp_negc.length() > 0? new BigDecimal(mp_negc): null;
		this.nelc = mp_nelc.length() > 0? new BigDecimal(mp_nelc): null;
		this.nesc = mp_nesc.length() > 0? new BigDecimal(mp_nesc): null;
		this.netTotal = mp_net_total.length() > 0? new BigDecimal(mp_net_total): null;
		this.nfsc = mp_nfsc.length() > 0? new BigDecimal(mp_nfsc): null;
		this.nmea = mp_nmea.length() > 0? new BigDecimal(mp_nmea): null;
		this.npsc = mp_npsc.length() > 0? new BigDecimal(mp_npsc): null;
		this.nrsc = mp_nrsc.length() > 0? new BigDecimal(mp_nrsc): null;
		this.ntsc = mp_ntsc.length() > 0? new BigDecimal(mp_ntsc): null;
		this.psoAdm = mp_psoadm.length() > 0? new BigDecimal(mp_psoadm): null;
		this.rcc = mp_rcc.length() > 0? new BigDecimal(mp_rcc): null;
		this.rsc = mp_rsc.length() > 0? new BigDecimal(mp_rsc): null;
		this.rsd = mp_rsd.length() > 0? new BigDecimal(mp_rsd): null;
		this.totalAmount = mp_total_amount.length() > 0? new BigDecimal(mp_total_amount): null;
		this.totalBesc = mp_total_besc.length() > 0? new BigDecimal(mp_total_besc): null;
		this.totalEmcAdm = mp_total_emcadm.length() > 0? new BigDecimal(mp_total_emcadm): null;
		this.totalFcc = mp_total_fcc.length() > 0? new BigDecimal(mp_total_fcc): null;
		this.totalFsc = mp_total_fsc.length() > 0? new BigDecimal(mp_total_fsc): null;
		this.totalFsd = mp_total_fsd.length() > 0? new BigDecimal(mp_total_fsd): null;
		this.totalGesc = mp_total_gesc.length() > 0? new BigDecimal(mp_total_gesc): null;
		this.totalGmee = mp_total_gmee.length() > 0? new BigDecimal(mp_total_gmee): null;
		this.totalGmef = mp_total_gmef.length() > 0? new BigDecimal(mp_total_gmef): null;
		this.totalHeuc = mp_total_heuc.length() > 0? new BigDecimal(mp_total_heuc): null;
		this.totalLcsc = mp_total_lcsc.length() > 0? new BigDecimal(mp_total_lcsc): null;
		this.totalLesd = mp_total_lesd.length() > 0? new BigDecimal(mp_total_lesd): null;
		this.totalLmee = mp_total_lmee.length() > 0? new BigDecimal(mp_total_lmee): null;
		this.totalLmef = mp_total_lmef.length() > 0? new BigDecimal(mp_total_lmef): null;
		this.totalMeuc = mp_total_meuc.length() > 0? new BigDecimal(mp_total_meuc): null;
		this.totalNasc = mp_total_nasc.length() > 0? new BigDecimal(mp_total_nasc): null;
		this.totalNeaa = mp_total_neaa.length() > 0? new BigDecimal(mp_total_neaa): null;
		this.totalNead = mp_total_nead.length() > 0? new BigDecimal(mp_total_nead): null;
		this.totalNegc = mp_total_negc.length() > 0? new BigDecimal(mp_total_negc): null;
		this.totalNelc = mp_total_nelc.length() > 0? new BigDecimal(mp_total_nelc): null;
		this.totalNesc = mp_total_nesc.length() > 0? new BigDecimal(mp_total_nesc): null;
		this.totalNfsc = mp_total_nfsc.length() > 0? new BigDecimal(mp_total_nfsc): null;
		this.totalNmea = mp_total_nmea.length() > 0? new BigDecimal(mp_total_nmea): null;
		this.totalNpsc = mp_total_npsc.length() > 0? new BigDecimal(mp_total_npsc): null;
		this.totalNrsc = mp_total_nrsc.length() > 0? new BigDecimal(mp_total_nrsc): null;
		this.totalNtsc = mp_total_ntsc.length() > 0? new BigDecimal(mp_total_ntsc): null;
		this.totalPsoAdm = mp_total_psoadm.length() > 0? new BigDecimal(mp_total_psoadm): null;
		this.totalRcc = mp_total_rcc.length() > 0? new BigDecimal(mp_total_rcc): null;
		this.totalRsc = mp_total_rsc.length() > 0? new BigDecimal(mp_total_rsc): null;
		this.totalRsd = mp_total_rsd.length() > 0? new BigDecimal(mp_total_rsd): null;
		this.totalVcsc = mp_total_vcsc.length() > 0? new BigDecimal(mp_total_vcsc): null;
		this.ipGstBesc = mp_v_besc.length() > 0? new BigDecimal(mp_v_besc): null;
		this.ipGstEmcAdm = mp_v_emcadm.length() > 0? new BigDecimal(mp_v_emcadm): null;
		this.ipGstFcc = mp_v_fcc.length() > 0? new BigDecimal(mp_v_fcc): null;
		this.ipGstFsc = mp_v_fsc.length() > 0? new BigDecimal(mp_v_fsc): null;
		this.ipGstFsd = mp_v_fsd.length() > 0? new BigDecimal(mp_v_fsd): null;
		this.ipGstGesc = mp_v_gesc.length() > 0? new BigDecimal(mp_v_gesc): null;
		this.ipGstGmee = mp_v_gmee.length() > 0? new BigDecimal(mp_v_gmee): null;
		this.ipGstGmef = mp_v_gmef.length() > 0? new BigDecimal(mp_v_gmef): null;
		this.ipGstHeuc = mp_v_heuc.length() > 0? new BigDecimal(mp_v_heuc): null;
		this.ipGstLcsc = mp_v_lcsc.length() > 0? new BigDecimal(mp_v_lcsc): null;
		this.ipGstLesd = mp_v_lesd.length() > 0? new BigDecimal(mp_v_lesd): null;
		this.ipGstLmee = mp_v_lmee.length() > 0? new BigDecimal(mp_v_lmee): null;
		this.ipGstLmef = mp_v_lmef.length() > 0? new BigDecimal(mp_v_lmef): null;
		this.ipGstMeuc = mp_v_meuc.length() > 0? new BigDecimal(mp_v_meuc): null;
		this.ipGstNasc = mp_v_nasc.length() > 0? new BigDecimal(mp_v_nasc): null;
		this.ipGstNeaa = mp_v_neaa.length() > 0? new BigDecimal(mp_v_neaa): null;
		this.ipGstNead = mp_v_nead.length() > 0? new BigDecimal(mp_v_nead): null;
		this.ipGstNegc = mp_v_negc.length() > 0? new BigDecimal(mp_v_negc): null;
		this.ipGstNelc = mp_v_nelc.length() > 0? new BigDecimal(mp_v_nelc): null;
		this.ipGstNesc = mp_v_nesc.length() > 0? new BigDecimal(mp_v_nesc): null;
		this.ipGstNfsc = mp_v_nfsc.length() > 0? new BigDecimal(mp_v_nfsc): null;
		this.ipGstNmea = mp_v_nmea.length() > 0? new BigDecimal(mp_v_nmea): null;
		this.ipGstNpsc = mp_v_npsc.length() > 0? new BigDecimal(mp_v_npsc): null;
		this.ipGstNrsc = mp_v_nrsc.length() > 0? new BigDecimal(mp_v_nrsc): null;
		this.ipGstNtsc = mp_v_ntsc.length() > 0? new BigDecimal(mp_v_ntsc): null;
		this.ipGstPsoAdm = mp_v_psoadm.length() > 0? new BigDecimal(mp_v_psoadm): null;
		this.ipGstRcc = mp_v_rcc.length() > 0? new BigDecimal(mp_v_rcc): null;
		this.ipGstRsc = mp_v_rsc.length() > 0? new BigDecimal(mp_v_rsc): null;
		this.ipGstRsd = mp_v_rsd.length() > 0? new BigDecimal(mp_v_rsd): null;
		this.ipGstTotal = mp_v_total.length() > 0? new BigDecimal(mp_v_total): null;
		this.ipGstVcsc = mp_v_vcsc.length() > 0? new BigDecimal(mp_v_vcsc): null;
		this.vcsc = mp_vcsc.length() > 0? new BigDecimal(mp_vcsc): null;
		this.emcAccount = mp_emc.equals("1");
		this.psoAccount = mp_pso.equals("1");
		this.msslAccount = mp_mssl.equals("1");
		this.taxable = mp_taxable.equals("1");
		this.rerunGstRate = mp_rerun_gst_rate.length() > 0? new BigDecimal(mp_rerun_gst_rate): null;
		this.fssc = mp_fssc.length() > 0? new BigDecimal(mp_fssc): null;
		this.ipGstFssc = mp_v_fssc.length() > 0? new BigDecimal(mp_v_fssc): null;
		this.opGstFssc = mp_a_fssc.length() > 0? new BigDecimal(mp_a_fssc): null;
		this.totalFssc = mp_total_fssc.length() > 0? new BigDecimal(mp_total_fssc): null;
	}
	
	public String PFCheck(Participant in) {

		String result = null;

		if ((this.opGstBesc == null && in.opGstBesc != null) || (this.opGstBesc != null && in.opGstBesc == null)) result = "opGstBesc missing value";
		if ((this.opGstEmcAdm == null && in.opGstEmcAdm != null) || (this.opGstEmcAdm != null && in.opGstEmcAdm == null)) result = "opGstEmcAdm missing value";
		if ((this.opGstFcc == null && in.opGstFcc != null) || (this.opGstFcc != null && in.opGstFcc == null)) result = "opGstFcc missing value";
		if ((this.opGstFsc == null && in.opGstFsc != null) || (this.opGstFsc != null && in.opGstFsc == null)) result = "opGstFsc missing value";
		if ((this.opGstFsd == null && in.opGstFsd != null) || (this.opGstFsd != null && in.opGstFsd == null)) result = "opGstFsd missing value";
		if ((this.opGstGesc == null && in.opGstGesc != null) || (this.opGstGesc != null && in.opGstGesc == null)) result = "opGstGesc missing value";
		if ((this.opGstGmee == null && in.opGstGmee != null) || (this.opGstGmee != null && in.opGstGmee == null)) result = "opGstGmee missing value";
		if ((this.opGstGmef == null && in.opGstGmef != null) || (this.opGstGmef != null && in.opGstGmef == null)) result = "opGstGmef missing value";
		if ((this.opGstHeuc == null && in.opGstHeuc != null) || (this.opGstHeuc != null && in.opGstHeuc == null)) result = "opGstHeuc missing value";
		if ((this.opGstLcsc == null && in.opGstLcsc != null) || (this.opGstLcsc != null && in.opGstLcsc == null)) result = "opGstLcsc missing value";
		if ((this.opGstLesd == null && in.opGstLesd != null) || (this.opGstLesd != null && in.opGstLesd == null)) result = "opGstLesd missing value";
		if ((this.opGstLmee == null && in.opGstLmee != null) || (this.opGstLmee != null && in.opGstLmee == null)) result = "opGstLmee missing value";
		if ((this.opGstLmef == null && in.opGstLmef != null) || (this.opGstLmef != null && in.opGstLmef == null)) result = "opGstLmef missing value";
		if ((this.opGstMeuc == null && in.opGstMeuc != null) || (this.opGstMeuc != null && in.opGstMeuc == null)) result = "opGstMeuc missing value";
		if ((this.opGstNasc == null && in.opGstNasc != null) || (this.opGstNasc != null && in.opGstNasc == null)) result = "opGstNasc missing value";
		if ((this.opGstNeaa == null && in.opGstNeaa != null) || (this.opGstNeaa != null && in.opGstNeaa == null)) result = "opGstNeaa missing value";
		if ((this.opGstNead == null && in.opGstNead != null) || (this.opGstNead != null && in.opGstNead == null)) result = "opGstNead missing value";
		if ((this.opGstNegc == null && in.opGstNegc != null) || (this.opGstNegc != null && in.opGstNegc == null)) result = "opGstNegc missing value";
		if ((this.opGstNelc == null && in.opGstNelc != null) || (this.opGstNelc != null && in.opGstNelc == null)) result = "opGstNelc missing value";
		if ((this.opGstNesc == null && in.opGstNesc != null) || (this.opGstNesc != null && in.opGstNesc == null)) result = "opGstNesc missing value";
		if ((this.opGstNfsc == null && in.opGstNfsc != null) || (this.opGstNfsc != null && in.opGstNfsc == null)) result = "opGstNfsc missing value";
		if ((this.opGstNmea == null && in.opGstNmea != null) || (this.opGstNmea != null && in.opGstNmea == null)) result = "opGstNmea missing value";
		if ((this.opGstNpsc == null && in.opGstNpsc != null) || (this.opGstNpsc != null && in.opGstNpsc == null)) result = "opGstNpsc missing value";
		if ((this.opGstNrsc == null && in.opGstNrsc != null) || (this.opGstNrsc != null && in.opGstNrsc == null)) result = "opGstNrsc missing value";
		if ((this.opGstNtsc == null && in.opGstNtsc != null) || (this.opGstNtsc != null && in.opGstNtsc == null)) result = "opGstNtsc missing value";
		if ((this.opGstPsoAdm == null && in.opGstPsoAdm != null) || (this.opGstPsoAdm != null && in.opGstPsoAdm == null)) result = "opGstPsoAdm missing value";
		if ((this.opGstRcc == null && in.opGstRcc != null) || (this.opGstRcc != null && in.opGstRcc == null)) result = "opGstRcc missing value";
		if ((this.opGstRsc == null && in.opGstRsc != null) || (this.opGstRsc != null && in.opGstRsc == null)) result = "opGstRsc missing value";
		if ((this.opGstRsd == null && in.opGstRsd != null) || (this.opGstRsd != null && in.opGstRsd == null)) result = "opGstRsd missing value";
		if ((this.opGstTotal == null && in.opGstTotal != null) || (this.opGstTotal != null && in.opGstTotal == null)) result = "opGstTotal missing value";
		if ((this.opGstVcsc == null && in.opGstVcsc != null) || (this.opGstVcsc != null && in.opGstVcsc == null)) result = "opGstVcsc missing value";
		if ((this.besc == null && in.besc != null) || (this.besc != null && in.besc == null)) result = "besc missing value";
		if ((this.emcAdm == null && in.emcAdm != null) || (this.emcAdm != null && in.emcAdm == null)) result = "emcAdm missing value";
		if ((this.fcc == null && in.fcc != null) || (this.fcc != null && in.fcc == null)) result = "fcc missing value";
		if ((this.fsc == null && in.fsc != null) || (this.fsc != null && in.fsc == null)) result = "fsc missing value";
		if ((this.fsd == null && in.fsd != null) || (this.fsd != null && in.fsd == null)) result = "fsd missing value";
		if ((this.gesc == null && in.gesc != null) || (this.gesc != null && in.gesc == null)) result = "gesc missing value";
		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.gmef == null && in.gmef != null) || (this.gmef != null && in.gmef == null)) result = "gmef missing value";
		if ((this.heuc == null && in.heuc != null) || (this.heuc != null && in.heuc == null)) result = "heuc missing value";
		if ((this.lcsc == null && in.lcsc != null) || (this.lcsc != null && in.lcsc == null)) result = "lcsc missing value";
		if ((this.lesd == null && in.lesd != null) || (this.lesd != null && in.lesd == null)) result = "lesd missing value";
		if ((this.lmee == null && in.lmee != null) || (this.lmee != null && in.lmee == null)) result = "lmee missing value";
		if ((this.lmef == null && in.lmef != null) || (this.lmef != null && in.lmef == null)) result = "lmef missing value";
		if ((this.meuc == null && in.meuc != null) || (this.meuc != null && in.meuc == null)) result = "meuc missing value";

		if ((this.nasc == null && in.nasc != null) || (this.nasc != null && in.nasc == null)) result = "nasc missing value";
		if ((this.neaa == null && in.neaa != null) || (this.neaa != null && in.neaa == null)) result = "neaa missing value";
		if ((this.nead == null && in.nead != null) || (this.nead != null && in.nead == null)) result = "nead missing value";
		if ((this.negc == null && in.negc != null) || (this.negc != null && in.negc == null)) result = "negc missing value";
		if ((this.nelc == null && in.nelc != null) || (this.nelc != null && in.nelc == null)) result = "nelc missing value";
		if ((this.nesc == null && in.nesc != null) || (this.nesc != null && in.nesc == null)) result = "nesc missing value";
		if ((this.netTotal == null && in.netTotal != null) || (this.netTotal != null && in.netTotal == null)) result = "netTotal missing value";
		if ((this.nfsc == null && in.nfsc != null) || (this.nfsc != null && in.nfsc == null)) result = "nfsc missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";
		if ((this.npsc == null && in.npsc != null) || (this.npsc != null && in.npsc == null)) result = "npsc missing value";
		if ((this.nrsc == null && in.nrsc != null) || (this.nrsc != null && in.nrsc == null)) result = "nrsc missing value";
		if ((this.ntsc == null && in.ntsc != null) || (this.ntsc != null && in.ntsc == null)) result = "ntsc missing value";
		if ((this.psoAdm == null && in.psoAdm != null) || (this.psoAdm != null && in.psoAdm == null)) result = "psoAdm missing value";
		if ((this.rcc == null && in.rcc != null) || (this.rcc != null && in.rcc == null)) result = "rcc missing value";
		if ((this.rsc == null && in.rsc != null) || (this.rsc != null && in.rsc == null)) result = "rsc missing value";
		if ((this.rsd == null && in.rsd != null) || (this.rsd != null && in.rsd == null)) result = "rsd missing value";
		if ((this.totalAmount == null && in.totalAmount != null) || (this.totalAmount != null && in.totalAmount == null)) result = "totalAmount missing value";
		if ((this.totalBesc == null && in.totalBesc != null) || (this.totalBesc != null && in.totalBesc == null)) result = "totalBesc missing value";
		if ((this.totalEmcAdm == null && in.totalEmcAdm != null) || (this.totalEmcAdm != null && in.totalEmcAdm == null)) result = "totalEmcAdm missing value";
		if ((this.totalFcc == null && in.totalFcc != null) || (this.totalFcc != null && in.totalFcc == null)) result = "totalFcc missing value";
		if ((this.totalFsc == null && in.totalFsc != null) || (this.totalFsc != null && in.totalFsc == null)) result = "totalFsc missing value";
		if ((this.totalFsd == null && in.totalFsd != null) || (this.totalFsd != null && in.totalFsd == null)) result = "totalFsd missing value";
		if ((this.totalGesc == null && in.totalGesc != null) || (this.totalGesc != null && in.totalGesc == null)) result = "totalGesc missing value";
		if ((this.totalGmee == null && in.totalGmee != null) || (this.totalGmee != null && in.totalGmee == null)) result = "totalGmee missing value";
		if ((this.totalGmef == null && in.totalGmef != null) || (this.totalGmef != null && in.totalGmef == null)) result = "totalGmef missing value";
		if ((this.totalHeuc == null && in.totalHeuc != null) || (this.totalHeuc != null && in.totalHeuc == null)) result = "totalHeuc missing value";
		if ((this.totalLcsc == null && in.totalLcsc != null) || (this.totalLcsc != null && in.totalLcsc == null)) result = "totalLcsc missing value";
		if ((this.totalLesd == null && in.totalLesd != null) || (this.totalLesd != null && in.totalLesd == null)) result = "totalLesd missing value";
		if ((this.totalLmee == null && in.totalLmee != null) || (this.totalLmee != null && in.totalLmee == null)) result = "totalLmee missing value";
		if ((this.totalLmef == null && in.totalLmef != null) || (this.totalLmef != null && in.totalLmef == null)) result = "totalLmef missing value";
		if ((this.totalMeuc == null && in.totalMeuc != null) || (this.totalMeuc != null && in.totalMeuc == null)) result = "totalMeuc missing value";
		if ((this.totalNasc == null && in.totalNasc != null) || (this.totalNasc != null && in.totalNasc == null)) result = "totalNasc missing value";
		if ((this.totalNeaa == null && in.totalNeaa != null) || (this.totalNeaa != null && in.totalNeaa == null)) result = "totalNeaa missing value";
		if ((this.totalNead == null && in.totalNead != null) || (this.totalNead != null && in.totalNead == null)) result = "totalNead missing value";
		if ((this.totalNegc == null && in.totalNegc != null) || (this.totalNegc != null && in.totalNegc == null)) result = "totalNegc missing value";
		if ((this.totalNelc == null && in.totalNelc != null) || (this.totalNelc != null && in.totalNelc == null)) result = "totalNelc missing value";
		if ((this.totalNesc == null && in.totalNesc != null) || (this.totalNesc != null && in.totalNesc == null)) result = "totalNesc missing value";
		if ((this.totalNfsc == null && in.totalNfsc != null) || (this.totalNfsc != null && in.totalNfsc == null)) result = "totalNfsc missing value";
		if ((this.totalNmea == null && in.totalNmea != null) || (this.totalNmea != null && in.totalNmea == null)) result = "totalNmea missing value";
		if ((this.totalNpsc == null && in.totalNpsc != null) || (this.totalNpsc != null && in.totalNpsc == null)) result = "totalNpsc missing value";
		if ((this.totalNrsc == null && in.totalNrsc != null) || (this.totalNrsc != null && in.totalNrsc == null)) result = "totalNrsc missing value";
		if ((this.totalNtsc == null && in.totalNtsc != null) || (this.totalNtsc != null && in.totalNtsc == null)) result = "totalNtsc missing value";
		if ((this.totalPsoAdm == null && in.totalPsoAdm != null) || (this.totalPsoAdm != null && in.totalPsoAdm == null)) result = "totalPsoAdm missing value";
		if ((this.totalRcc == null && in.totalRcc != null) || (this.totalRcc != null && in.totalRcc == null)) result = "totalRcc missing value";
		if ((this.totalRsc == null && in.totalRsc != null) || (this.totalRsc != null && in.totalRsc == null)) result = "totalRsc missing value";
		if ((this.totalRsd == null && in.totalRsd != null) || (this.totalRsd != null && in.totalRsd == null)) result = "totalRsd missing value";
		if ((this.totalVcsc == null && in.totalVcsc != null) || (this.totalVcsc != null && in.totalVcsc == null)) result = "totalVcsc missing value";
		if ((this.ipGstBesc == null && in.ipGstBesc != null) || (this.ipGstBesc != null && in.ipGstBesc == null)) result = "ipGstBesc missing value";
		if ((this.ipGstEmcAdm == null && in.ipGstEmcAdm != null) || (this.ipGstEmcAdm != null && in.ipGstEmcAdm == null)) result = "ipGstEmcAdm missing value";
		if ((this.ipGstFcc == null && in.ipGstFcc != null) || (this.ipGstFcc != null && in.ipGstFcc == null)) result = "ipGstFcc missing value";
		if ((this.ipGstFsc == null && in.ipGstFsc != null) || (this.ipGstFsc != null && in.ipGstFsc == null)) result = "ipGstFsc missing value";
		if ((this.ipGstFsd == null && in.ipGstFsd != null) || (this.ipGstFsd != null && in.ipGstFsd == null)) result = "ipGstFsd missing value";
		if ((this.ipGstGesc == null && in.ipGstGesc != null) || (this.ipGstGesc != null && in.ipGstGesc == null)) result = "ipGstGesc missing value";
		if ((this.ipGstGmee == null && in.ipGstGmee != null) || (this.ipGstGmee != null && in.ipGstGmee == null)) result = "ipGstGmee missing value";
		if ((this.ipGstGmef == null && in.ipGstGmef != null) || (this.ipGstGmef != null && in.ipGstGmef == null)) result = "ipGstGmef missing value";
		if ((this.ipGstHeuc == null && in.ipGstHeuc != null) || (this.ipGstHeuc != null && in.ipGstHeuc == null)) result = "ipGstHeuc missing value";
		if ((this.ipGstLcsc == null && in.ipGstLcsc != null) || (this.ipGstLcsc != null && in.ipGstLcsc == null)) result = "ipGstLcsc missing value";
		if ((this.ipGstLesd == null && in.ipGstLesd != null) || (this.ipGstLesd != null && in.ipGstLesd == null)) result = "ipGstLesd missing value";
		if ((this.ipGstLmee == null && in.ipGstLmee != null) || (this.ipGstLmee != null && in.ipGstLmee == null)) result = "ipGstLmee missing value";
		if ((this.ipGstLmef == null && in.ipGstLmef != null) || (this.ipGstLmef != null && in.ipGstLmef == null)) result = "ipGstLmef missing value";
		if ((this.ipGstMeuc == null && in.ipGstMeuc != null) || (this.ipGstMeuc != null && in.ipGstMeuc == null)) result = "ipGstMeuc missing value";
		if ((this.ipGstNasc == null && in.ipGstNasc != null) || (this.ipGstNasc != null && in.ipGstNasc == null)) result = "ipGstNasc missing value";
		if ((this.ipGstNeaa == null && in.ipGstNeaa != null) || (this.ipGstNeaa != null && in.ipGstNeaa == null)) result = "ipGstNeaa missing value";
		if ((this.ipGstNead == null && in.ipGstNead != null) || (this.ipGstNead != null && in.ipGstNead == null)) result = "ipGstNead missing value";
		if ((this.ipGstNegc == null && in.ipGstNegc != null) || (this.ipGstNegc != null && in.ipGstNegc == null)) result = "ipGstNegc missing value";
		if ((this.ipGstNelc == null && in.ipGstNelc != null) || (this.ipGstNelc != null && in.ipGstNelc == null)) result = "ipGstNelc missing value";
		if ((this.ipGstNesc == null && in.ipGstNesc != null) || (this.ipGstNesc != null && in.ipGstNesc == null)) result = "ipGstNesc missing value";
		if ((this.ipGstNfsc == null && in.ipGstNfsc != null) || (this.ipGstNfsc != null && in.ipGstNfsc == null)) result = "ipGstNfsc missing value";
		if ((this.ipGstNmea == null && in.ipGstNmea != null) || (this.ipGstNmea != null && in.ipGstNmea == null)) result = "ipGstNmea missing value";
		if ((this.ipGstNpsc == null && in.ipGstNpsc != null) || (this.ipGstNpsc != null && in.ipGstNpsc == null)) result = "ipGstNpsc missing value";
		if ((this.ipGstNrsc == null && in.ipGstNrsc != null) || (this.ipGstNrsc != null && in.ipGstNrsc == null)) result = "ipGstNrsc missing value";
		if ((this.ipGstNtsc == null && in.ipGstNtsc != null) || (this.ipGstNtsc != null && in.ipGstNtsc == null)) result = "ipGstNtsc missing value";
		if ((this.ipGstPsoAdm == null && in.ipGstPsoAdm != null) || (this.ipGstPsoAdm != null && in.ipGstPsoAdm == null)) result = "ipGstPsoAdm missing value";
		if ((this.ipGstRcc == null && in.ipGstRcc != null) || (this.ipGstRcc != null && in.ipGstRcc == null)) result = "ipGstRcc missing value";
		if ((this.ipGstRsc == null && in.ipGstRsc != null) || (this.ipGstRsc != null && in.ipGstRsc == null)) result = "ipGstRsc missing value";
		if ((this.ipGstRsd == null && in.ipGstRsd != null) || (this.ipGstRsd != null && in.ipGstRsd == null)) result = "ipGstRsd missing value";
		if ((this.ipGstTotal == null && in.ipGstTotal != null) || (this.ipGstTotal != null && in.ipGstTotal == null)) result = "ipGstTotal missing value";
		if ((this.ipGstVcsc == null && in.ipGstVcsc != null) || (this.ipGstVcsc != null && in.ipGstVcsc == null)) result = "ipGstVcsc missing value";
		if ((this.vcsc == null && in.vcsc != null) || (this.vcsc != null && in.vcsc == null)) result = "vcsc missing value";

		if ((this.fssc == null && in.fssc != null) || (this.fssc != null && in.fssc == null)) result = "fssc missing value";
		if ((this.ipGstFssc == null && in.ipGstFssc != null) || (this.ipGstFssc != null && in.ipGstFssc == null)) result = "ipGstFssc missing value";
		if ((this.opGstFssc == null && in.opGstFssc != null) || (this.opGstFssc != null && in.opGstFssc == null)) result = "opGstFssc missing value";
		if ((this.totalFssc == null && in.totalFssc != null) || (this.totalFssc != null && in.totalFssc == null)) result = "totalFssc missing value";

		return result;
	}
	
	public String RSCheck(Participant in) {

		String result = null;

		if ((this.opGstBesc == null && in.opGstBesc != null) || (this.opGstBesc != null && in.opGstBesc == null)) result = "opGstBesc missing value";
		//if ((this.opGstEmcAdm == null && in.opGstEmcAdm != null) || (this.opGstEmcAdm != null && in.opGstEmcAdm == null)) result = "opGstEmcAdm missing value";
		if ((this.opGstFcc == null && in.opGstFcc != null) || (this.opGstFcc != null && in.opGstFcc == null)) result = "opGstFcc missing value";
		if ((this.opGstFsc == null && in.opGstFsc != null) || (this.opGstFsc != null && in.opGstFsc == null)) result = "opGstFsc missing value";
		//if ((this.opGstFsd == null && in.opGstFsd != null) || (this.opGstFsd != null && in.opGstFsd == null)) result = "opGstFsd missing value";
		//if ((this.opGstGesc == null && in.opGstGesc != null) || (this.opGstGesc != null && in.opGstGesc == null)) result = "opGstGesc missing value";
		if ((this.opGstGmee == null && in.opGstGmee != null) || (this.opGstGmee != null && in.opGstGmee == null)) result = "opGstGmee missing value";
		if ((this.opGstGmef == null && in.opGstGmef != null) || (this.opGstGmef != null && in.opGstGmef == null)) result = "opGstGmef missing value";
		//if ((this.opGstHeuc == null && in.opGstHeuc != null) || (this.opGstHeuc != null && in.opGstHeuc == null)) result = "opGstHeuc missing value";
		if ((this.opGstLcsc == null && in.opGstLcsc != null) || (this.opGstLcsc != null && in.opGstLcsc == null)) result = "opGstLcsc missing value";
		//if ((this.opGstLesd == null && in.opGstLesd != null) || (this.opGstLesd != null && in.opGstLesd == null)) result = "opGstLesd missing value";
		if ((this.opGstLmee == null && in.opGstLmee != null) || (this.opGstLmee != null && in.opGstLmee == null)) result = "opGstLmee missing value";
		if ((this.opGstLmef == null && in.opGstLmef != null) || (this.opGstLmef != null && in.opGstLmef == null)) result = "opGstLmef missing value";
		//if ((this.opGstMeuc == null && in.opGstMeuc != null) || (this.opGstMeuc != null && in.opGstMeuc == null)) result = "opGstMeuc missing value";
		//if ((this.opGstNasc == null && in.opGstNasc != null) || (this.opGstNasc != null && in.opGstNasc == null)) result = "opGstNasc missing value";
		if ((this.opGstNeaa == null && in.opGstNeaa != null) || (this.opGstNeaa != null && in.opGstNeaa == null)) result = "opGstNeaa missing value";
		if ((this.opGstNead == null && in.opGstNead != null) || (this.opGstNead != null && in.opGstNead == null)) result = "opGstNead missing value";
		//if ((this.opGstNegc == null && in.opGstNegc != null) || (this.opGstNegc != null && in.opGstNegc == null)) result = "opGstNegc missing value";
		if ((this.opGstNelc == null && in.opGstNelc != null) || (this.opGstNelc != null && in.opGstNelc == null)) result = "opGstNelc missing value";
		//if ((this.opGstNesc == null && in.opGstNesc != null) || (this.opGstNesc != null && in.opGstNesc == null)) result = "opGstNesc missing value";
		//if ((this.opGstNfsc == null && in.opGstNfsc != null) || (this.opGstNfsc != null && in.opGstNfsc == null)) result = "opGstNfsc missing value";
		if ((this.opGstNmea == null && in.opGstNmea != null) || (this.opGstNmea != null && in.opGstNmea == null)) result = "opGstNmea missing value";
		//if ((this.opGstNpsc == null && in.opGstNpsc != null) || (this.opGstNpsc != null && in.opGstNpsc == null)) result = "opGstNpsc missing value";
		if ((this.opGstNrsc == null && in.opGstNrsc != null) || (this.opGstNrsc != null && in.opGstNrsc == null)) result = "opGstNrsc missing value";
		if ((this.opGstNtsc == null && in.opGstNtsc != null) || (this.opGstNtsc != null && in.opGstNtsc == null)) result = "opGstNtsc missing value";
		//if ((this.opGstPsoAdm == null && in.opGstPsoAdm != null) || (this.opGstPsoAdm != null && in.opGstPsoAdm == null)) result = "opGstPsoAdm missing value";
		if ((this.opGstRcc == null && in.opGstRcc != null) || (this.opGstRcc != null && in.opGstRcc == null)) result = "opGstRcc missing value";
		if ((this.opGstRsc == null && in.opGstRsc != null) || (this.opGstRsc != null && in.opGstRsc == null)) result = "opGstRsc missing value";
		if ((this.opGstRsd == null && in.opGstRsd != null) || (this.opGstRsd != null && in.opGstRsd == null)) result = "opGstRsd missing value";
		//if ((this.opGstTotal == null && in.opGstTotal != null) || (this.opGstTotal != null && in.opGstTotal == null)) result = "opGstTotal missing value";
		if ((this.opGstVcsc == null && in.opGstVcsc != null) || (this.opGstVcsc != null && in.opGstVcsc == null)) result = "opGstVcsc missing value";
		if ((this.besc == null && in.besc != null) || (this.besc != null && in.besc == null)) result = "besc missing value";
		//if ((this.emcAdm == null && in.emcAdm != null) || (this.emcAdm != null && in.emcAdm == null)) result = "emcAdm missing value";
		if ((this.fcc == null && in.fcc != null) || (this.fcc != null && in.fcc == null)) result = "fcc missing value";
		if ((this.fsc == null && in.fsc != null) || (this.fsc != null && in.fsc == null)) result = "fsc missing value";
		//if ((this.fsd == null && in.fsd != null) || (this.fsd != null && in.fsd == null)) result = "fsd missing value";
		//if ((this.gesc == null && in.gesc != null) || (this.gesc != null && in.gesc == null)) result = "gesc missing value";
		if ((this.gmee == null && in.gmee != null) || (this.gmee != null && in.gmee == null)) result = "gmee missing value";
		if ((this.gmef == null && in.gmef != null) || (this.gmef != null && in.gmef == null)) result = "gmef missing value";
		//if ((this.heuc == null && in.heuc != null) || (this.heuc != null && in.heuc == null)) result = "heuc missing value";
		//if ((this.lcsc == null && in.lcsc != null) || (this.lcsc != null && in.lcsc == null)) result = "lcsc missing value";
		//if ((this.lesd == null && in.lesd != null) || (this.lesd != null && in.lesd == null)) result = "lesd missing value";
		if ((this.lmee == null && in.lmee != null) || (this.lmee != null && in.lmee == null)) result = "lmee missing value";
		if ((this.lmef == null && in.lmef != null) || (this.lmef != null && in.lmef == null)) result = "lmef missing value";
		//if ((this.meuc == null && in.meuc != null) || (this.meuc != null && in.meuc == null)) result = "meuc missing value";

		//if ((this.nasc == null && in.nasc != null) || (this.nasc != null && in.nasc == null)) result = "nasc missing value";
		//if ((this.neaa == null && in.neaa != null) || (this.neaa != null && in.neaa == null)) result = "neaa missing value";
		//if ((this.nead == null && in.nead != null) || (this.nead != null && in.nead == null)) result = "nead missing value";
		//if ((this.negc == null && in.negc != null) || (this.negc != null && in.negc == null)) result = "negc missing value";
		//if ((this.nelc == null && in.nelc != null) || (this.nelc != null && in.nelc == null)) result = "nelc missing value";
		//if ((this.nesc == null && in.nesc != null) || (this.nesc != null && in.nesc == null)) result = "nesc missing value";
		//if ((this.netTotal == null && in.netTotal != null) || (this.netTotal != null && in.netTotal == null)) result = "netTotal missing value";
		//if ((this.nfsc == null && in.nfsc != null) || (this.nfsc != null && in.nfsc == null)) result = "nfsc missing value";
		if ((this.nmea == null && in.nmea != null) || (this.nmea != null && in.nmea == null)) result = "nmea missing value";
		//if ((this.npsc == null && in.npsc != null) || (this.npsc != null && in.npsc == null)) result = "npsc missing value";
		if ((this.nrsc == null && in.nrsc != null) || (this.nrsc != null && in.nrsc == null)) result = "nrsc missing value";
		if ((this.ntsc == null && in.ntsc != null) || (this.ntsc != null && in.ntsc == null)) result = "ntsc missing value";
		//if ((this.psoAdm == null && in.psoAdm != null) || (this.psoAdm != null && in.psoAdm == null)) result = "psoAdm missing value";
		if ((this.rcc == null && in.rcc != null) || (this.rcc != null && in.rcc == null)) result = "rcc missing value";
		if ((this.rsc == null && in.rsc != null) || (this.rsc != null && in.rsc == null)) result = "rsc missing value";
		if ((this.rsd == null && in.rsd != null) || (this.rsd != null && in.rsd == null)) result = "rsd missing value";
		//if ((this.totalAmount == null && in.totalAmount != null) || (this.totalAmount != null && in.totalAmount == null)) result = "totalAmount missing value";
		if ((this.totalBesc == null && in.totalBesc != null) || (this.totalBesc != null && in.totalBesc == null)) result = "totalBesc missing value";
		//if ((this.totalEmcAdm == null && in.totalEmcAdm != null) || (this.totalEmcAdm != null && in.totalEmcAdm == null)) result = "totalEmcAdm missing value";
		if ((this.totalFcc == null && in.totalFcc != null) || (this.totalFcc != null && in.totalFcc == null)) result = "totalFcc missing value";
		if ((this.totalFsc == null && in.totalFsc != null) || (this.totalFsc != null && in.totalFsc == null)) result = "totalFsc missing value";
		//if ((this.totalFsd == null && in.totalFsd != null) || (this.totalFsd != null && in.totalFsd == null)) result = "totalFsd missing value";
		//if ((this.totalGesc == null && in.totalGesc != null) || (this.totalGesc != null && in.totalGesc == null)) result = "totalGesc missing value";
		if ((this.totalGmee == null && in.totalGmee != null) || (this.totalGmee != null && in.totalGmee == null)) result = "totalGmee missing value";
		if ((this.totalGmef == null && in.totalGmef != null) || (this.totalGmef != null && in.totalGmef == null)) result = "totalGmef missing value";
		//if ((this.totalHeuc == null && in.totalHeuc != null) || (this.totalHeuc != null && in.totalHeuc == null)) result = "totalHeuc missing value";
		//if ((this.totalLcsc == null && in.totalLcsc != null) || (this.totalLcsc != null && in.totalLcsc == null)) result = "totalLcsc missing value";
		//if ((this.totalLesd == null && in.totalLesd != null) || (this.totalLesd != null && in.totalLesd == null)) result = "totalLesd missing value";
		if ((this.totalLmee == null && in.totalLmee != null) || (this.totalLmee != null && in.totalLmee == null)) result = "totalLmee missing value";
		if ((this.totalLmef == null && in.totalLmef != null) || (this.totalLmef != null && in.totalLmef == null)) result = "totalLmef missing value";
		//if ((this.totalMeuc == null && in.totalMeuc != null) || (this.totalMeuc != null && in.totalMeuc == null)) result = "totalMeuc missing value";
		//if ((this.totalNasc == null && in.totalNasc != null) || (this.totalNasc != null && in.totalNasc == null)) result = "totalNasc missing value";
		//if ((this.totalNeaa == null && in.totalNeaa != null) || (this.totalNeaa != null && in.totalNeaa == null)) result = "totalNeaa missing value";
		//if ((this.totalNead == null && in.totalNead != null) || (this.totalNead != null && in.totalNead == null)) result = "totalNead missing value";
		//if ((this.totalNegc == null && in.totalNegc != null) || (this.totalNegc != null && in.totalNegc == null)) result = "totalNegc missing value";
		//if ((this.totalNelc == null && in.totalNelc != null) || (this.totalNelc != null && in.totalNelc == null)) result = "totalNelc missing value";
		//if ((this.totalNesc == null && in.totalNesc != null) || (this.totalNesc != null && in.totalNesc == null)) result = "totalNesc missing value";
		//if ((this.totalNfsc == null && in.totalNfsc != null) || (this.totalNfsc != null && in.totalNfsc == null)) result = "totalNfsc missing value";
		if ((this.totalNmea == null && in.totalNmea != null) || (this.totalNmea != null && in.totalNmea == null)) result = "totalNmea missing value";
		//if ((this.totalNpsc == null && in.totalNpsc != null) || (this.totalNpsc != null && in.totalNpsc == null)) result = "totalNpsc missing value";
		if ((this.totalNrsc == null && in.totalNrsc != null) || (this.totalNrsc != null && in.totalNrsc == null)) result = "totalNrsc missing value";
		if ((this.totalNtsc == null && in.totalNtsc != null) || (this.totalNtsc != null && in.totalNtsc == null)) result = "totalNtsc missing value";
		//if ((this.totalPsoAdm == null && in.totalPsoAdm != null) || (this.totalPsoAdm != null && in.totalPsoAdm == null)) result = "totalPsoAdm missing value";
		if ((this.totalRcc == null && in.totalRcc != null) || (this.totalRcc != null && in.totalRcc == null)) result = "totalRcc missing value";
		if ((this.totalRsc == null && in.totalRsc != null) || (this.totalRsc != null && in.totalRsc == null)) result = "totalRsc missing value";
		if ((this.totalRsd == null && in.totalRsd != null) || (this.totalRsd != null && in.totalRsd == null)) result = "totalRsd missing value";
		//if ((this.totalVcsc == null && in.totalVcsc != null) || (this.totalVcsc != null && in.totalVcsc == null)) result = "totalVcsc missing value";
		if ((this.ipGstBesc == null && in.ipGstBesc != null) || (this.ipGstBesc != null && in.ipGstBesc == null)) result = "ipGstBesc missing value";
		if ((this.ipGstEmcAdm == null && in.ipGstEmcAdm != null) || (this.ipGstEmcAdm != null && in.ipGstEmcAdm == null)) result = "ipGstEmcAdm missing value";
		if ((this.ipGstFcc == null && in.ipGstFcc != null) || (this.ipGstFcc != null && in.ipGstFcc == null)) result = "ipGstFcc missing value";
		if ((this.ipGstFsc == null && in.ipGstFsc != null) || (this.ipGstFsc != null && in.ipGstFsc == null)) result = "ipGstFsc missing value";
		if ((this.ipGstFsd == null && in.ipGstFsd != null) || (this.ipGstFsd != null && in.ipGstFsd == null)) result = "ipGstFsd missing value";
		//if ((this.ipGstGesc == null && in.ipGstGesc != null) || (this.ipGstGesc != null && in.ipGstGesc == null)) result = "ipGstGesc missing value";
		if ((this.ipGstGmee == null && in.ipGstGmee != null) || (this.ipGstGmee != null && in.ipGstGmee == null)) result = "ipGstGmee missing value";
		if ((this.ipGstGmef == null && in.ipGstGmef != null) || (this.ipGstGmef != null && in.ipGstGmef == null)) result = "ipGstGmef missing value";
		if ((this.ipGstHeuc == null && in.ipGstHeuc != null) || (this.ipGstHeuc != null && in.ipGstHeuc == null)) result = "ipGstHeuc missing value";
		//if ((this.ipGstLcsc == null && in.ipGstLcsc != null) || (this.ipGstLcsc != null && in.ipGstLcsc == null)) result = "ipGstLcsc missing value";
		//if ((this.ipGstLesd == null && in.ipGstLesd != null) || (this.ipGstLesd != null && in.ipGstLesd == null)) result = "ipGstLesd missing value";
		if ((this.ipGstLmee == null && in.ipGstLmee != null) || (this.ipGstLmee != null && in.ipGstLmee == null)) result = "ipGstLmee missing value";
		if ((this.ipGstLmef == null && in.ipGstLmef != null) || (this.ipGstLmef != null && in.ipGstLmef == null)) result = "ipGstLmef missing value";
		if ((this.ipGstMeuc == null && in.ipGstMeuc != null) || (this.ipGstMeuc != null && in.ipGstMeuc == null)) result = "ipGstMeuc missing value";
		//if ((this.ipGstNasc == null && in.ipGstNasc != null) || (this.ipGstNasc != null && in.ipGstNasc == null)) result = "ipGstNasc missing value";
		if ((this.ipGstNeaa == null && in.ipGstNeaa != null) || (this.ipGstNeaa != null && in.ipGstNeaa == null)) result = "ipGstNeaa missing value";
		if ((this.ipGstNead == null && in.ipGstNead != null) || (this.ipGstNead != null && in.ipGstNead == null)) result = "ipGstNead missing value";
		//if ((this.ipGstNegc == null && in.ipGstNegc != null) || (this.ipGstNegc != null && in.ipGstNegc == null)) result = "ipGstNegc missing value";
		if ((this.ipGstNelc == null && in.ipGstNelc != null) || (this.ipGstNelc != null && in.ipGstNelc == null)) result = "ipGstNelc missing value";
		//if ((this.ipGstNesc == null && in.ipGstNesc != null) || (this.ipGstNesc != null && in.ipGstNesc == null)) result = "ipGstNesc missing value";
		if ((this.ipGstNfsc == null && in.ipGstNfsc != null) || (this.ipGstNfsc != null && in.ipGstNfsc == null)) result = "ipGstNfsc missing value";
		if ((this.ipGstNmea == null && in.ipGstNmea != null) || (this.ipGstNmea != null && in.ipGstNmea == null)) result = "ipGstNmea missing value";
		//if ((this.ipGstNpsc == null && in.ipGstNpsc != null) || (this.ipGstNpsc != null && in.ipGstNpsc == null)) result = "ipGstNpsc missing value";
		if ((this.ipGstNrsc == null && in.ipGstNrsc != null) || (this.ipGstNrsc != null && in.ipGstNrsc == null)) result = "ipGstNrsc missing value";
		if ((this.ipGstNtsc == null && in.ipGstNtsc != null) || (this.ipGstNtsc != null && in.ipGstNtsc == null)) result = "ipGstNtsc missing value";
		if ((this.ipGstPsoAdm == null && in.ipGstPsoAdm != null) || (this.ipGstPsoAdm != null && in.ipGstPsoAdm == null)) result = "ipGstPsoAdm missing value";
		if ((this.ipGstRcc == null && in.ipGstRcc != null) || (this.ipGstRcc != null && in.ipGstRcc == null)) result = "ipGstRcc missing value";
		if ((this.ipGstRsc == null && in.ipGstRsc != null) || (this.ipGstRsc != null && in.ipGstRsc == null)) result = "ipGstRsc missing value";
		if ((this.ipGstRsd == null && in.ipGstRsd != null) || (this.ipGstRsd != null && in.ipGstRsd == null)) result = "ipGstRsd missing value";
		//if ((this.ipGstTotal == null && in.ipGstTotal != null) || (this.ipGstTotal != null && in.ipGstTotal == null)) result = "ipGstTotal missing value";
		if ((this.ipGstVcsc == null && in.ipGstVcsc != null) || (this.ipGstVcsc != null && in.ipGstVcsc == null)) result = "ipGstVcsc missing value";
		//if ((this.vcsc == null && in.vcsc != null) || (this.vcsc != null && in.vcsc == null)) result = "vcsc missing value";

		if ((this.fssc == null && in.fssc != null) || (this.fssc != null && in.fssc == null)) result = "fssc missing value";
		if ((this.ipGstFssc == null && in.ipGstFssc != null) || (this.ipGstFssc != null && in.ipGstFssc == null)) result = "ipGstFssc missing value";
		if ((this.opGstFssc == null && in.opGstFssc != null) || (this.opGstFssc != null && in.opGstFssc == null)) result = "opGstFssc missing value";
		if ((this.totalFssc == null && in.totalFssc != null) || (this.totalFssc != null && in.totalFssc == null)) result = "totalFssc missing value";

		return result;
	}
	
	public String equal(Participant in) {

		String result = null;

		if (this.opGstBesc != null && in.opGstBesc != null) if (this.opGstBesc.compareTo(in.opGstBesc) != 0) result = "opGstBesc value mismatch";
		if (this.opGstEmcAdm != null && in.opGstEmcAdm != null) if (this.opGstEmcAdm.compareTo(in.opGstEmcAdm) != 0) result = "opGstEmcAdm value mismatch";
		if (this.opGstFcc != null && in.opGstFcc != null) if (this.opGstFcc.compareTo(in.opGstFcc) != 0) result = "opGstFcc value mismatch";
		if (this.opGstFsc != null && in.opGstFsc != null) if (this.opGstFsc.compareTo(in.opGstFsc) != 0) result = "opGstFsc value mismatch";
		if (this.opGstFsd != null && in.opGstFsd != null) if (this.opGstFsd.compareTo(in.opGstFsd) != 0) result = "opGstFsd value mismatch";
		if (this.opGstGesc != null && in.opGstGesc != null) if (this.opGstGesc.compareTo(in.opGstGesc) != 0) result = "opGstGesc value mismatch";
		if (this.opGstGmee != null && in.opGstGmee != null) if (this.opGstGmee.compareTo(in.opGstGmee) != 0) result = "opGstGmee value mismatch";
		if (this.opGstGmef != null && in.opGstGmef != null) if (this.opGstGmef.compareTo(in.opGstGmef) != 0) result = "opGstGmef value mismatch";
		if (this.opGstHeuc != null && in.opGstHeuc != null) if (this.opGstHeuc.compareTo(in.opGstHeuc) != 0) result = "opGstHeuc value mismatch";
		if (this.opGstLcsc != null && in.opGstLcsc != null) if (this.opGstLcsc.compareTo(in.opGstLcsc) != 0) result = "opGstLcsc value mismatch";
		if (this.opGstLesd != null && in.opGstLesd != null) if (this.opGstLesd.compareTo(in.opGstLesd) != 0) result = "opGstLesd value mismatch";
		if (this.opGstLmee != null && in.opGstLmee != null) if (this.opGstLmee.compareTo(in.opGstLmee) != 0) result = "opGstLmee value mismatch";
		if (this.opGstLmef != null && in.opGstLmef != null) if (this.opGstLmef.compareTo(in.opGstLmef) != 0) result = "opGstLmef value mismatch";
		if (this.opGstMeuc != null && in.opGstMeuc != null) if (this.opGstMeuc.compareTo(in.opGstMeuc) != 0) result = "opGstMeuc value mismatch";
		if (this.opGstNasc != null && in.opGstNasc != null) if (this.opGstNasc.compareTo(in.opGstNasc) != 0) result = "opGstNasc value mismatch";
		if (this.opGstNeaa != null && in.opGstNeaa != null) if (this.opGstNeaa.compareTo(in.opGstNeaa) != 0) result = "opGstNeaa value mismatch";
		if (this.opGstNead != null && in.opGstNead != null) if (this.opGstNead.compareTo(in.opGstNead) != 0) result = "opGstNead value mismatch";
		if (this.opGstNegc != null && in.opGstNegc != null) if (this.opGstNegc.compareTo(in.opGstNegc) != 0) result = "opGstNegc value mismatch";
		if (this.opGstNelc != null && in.opGstNelc != null) if (this.opGstNelc.compareTo(in.opGstNelc) != 0) result = "opGstNelc value mismatch";
		if (this.opGstNesc != null && in.opGstNesc != null) if (this.opGstNesc.compareTo(in.opGstNesc) != 0) result = "opGstNesc value mismatch";
		if (this.opGstNfsc != null && in.opGstNfsc != null) if (this.opGstNfsc.compareTo(in.opGstNfsc) != 0) result = "opGstNfsc value mismatch";
		if (this.opGstNmea != null && in.opGstNmea != null) if (this.opGstNmea.compareTo(in.opGstNmea) != 0) result = "opGstNmea value mismatch";
		//if (this.opGstNpsc != null && in.opGstNpsc != null) if (this.opGstNpsc.compareTo(in.opGstNpsc) != 0) result = "opGstNpsc value mismatch";
		if (this.opGstNrsc != null && in.opGstNrsc != null) if (this.opGstNrsc.compareTo(in.opGstNrsc) != 0) result = "opGstNrsc value mismatch";
		if (this.opGstNtsc != null && in.opGstNtsc != null) if (this.opGstNtsc.compareTo(in.opGstNtsc) != 0) result = "opGstNtsc value mismatch";
		if (this.opGstPsoAdm != null && in.opGstPsoAdm != null) if (this.opGstPsoAdm.compareTo(in.opGstPsoAdm) != 0) result = "opGstPsoAdm value mismatch";
		if (this.opGstRcc != null && in.opGstRcc != null) if (this.opGstRcc.compareTo(in.opGstRcc) != 0) result = "opGstRcc value mismatch";
		if (this.opGstRsc != null && in.opGstRsc != null) if (this.opGstRsc.compareTo(in.opGstRsc) != 0) result = "opGstRsc value mismatch";
		if (this.opGstRsd != null && in.opGstRsd != null) if (this.opGstRsd.compareTo(in.opGstRsd) != 0) result = "opGstRsd value mismatch";
		if (this.opGstTotal != null && in.opGstTotal != null) if (this.opGstTotal.compareTo(in.opGstTotal) != 0) result = "opGstTotal value mismatch";
		if (this.opGstVcsc != null && in.opGstVcsc != null) if (this.opGstVcsc.compareTo(in.opGstVcsc) != 0) result = "opGstVcsc value mismatch";
		if (this.besc != null && in.besc != null) if (this.besc.compareTo(in.besc) != 0) result = "besc value mismatch";
		if (this.emcAdm != null && in.emcAdm != null) if (this.emcAdm.compareTo(in.emcAdm) != 0) result = "emcAdm value mismatch";
		if (this.fcc != null && in.fcc != null) if (this.fcc.compareTo(in.fcc) != 0) result = "fcc value mismatch";
		if (this.fsc != null && in.fsc != null) if (this.fsc.compareTo(in.fsc) != 0) result = "fsc value mismatch";
		if (this.fsd != null && in.fsd != null) if (this.fsd.compareTo(in.fsd) != 0) result = "fsd value mismatch";
		if (this.gesc != null && in.gesc != null) if (this.gesc.compareTo(in.gesc) != 0) result = "gesc value mismatch";
		if (this.gmee != null && in.gmee != null) if (this.gmee.compareTo(in.gmee) != 0) result = "gmee value mismatch";
		if (this.gmef != null && in.gmef != null) if (this.gmef.compareTo(in.gmef) != 0) result = "gmef value mismatch";
		if (this.heuc != null && in.heuc != null) if (this.heuc.compareTo(in.heuc) != 0) result = "heuc value mismatch";
		if (this.lcsc != null && in.lcsc != null) if (this.lcsc.compareTo(in.lcsc) != 0) result = "lcsc value mismatch";
		if (this.lesd != null && in.lesd != null) if (this.lesd.compareTo(in.lesd) != 0) result = "lesd value mismatch";
		if (this.lmee != null && in.lmee != null) if (this.lmee.compareTo(in.lmee) != 0) result = "lmee value mismatch";
		if (this.lmef != null && in.lmef != null) if (this.lmef.compareTo(in.lmef) != 0) result = "lmef value mismatch";
		if (this.meuc != null && in.meuc != null) if (this.meuc.compareTo(in.meuc) != 0) result = "meuc value mismatch";

		if (this.nasc != null && in.nasc != null) if (this.nasc.compareTo(in.nasc) != 0) result = "nasc value mismatch";
		if (this.neaa != null && in.neaa != null) if (this.neaa.compareTo(in.neaa) != 0) result = "neaa value mismatch";
		if (this.nead != null && in.nead != null) if (this.nead.compareTo(in.nead) != 0) result = "nead value mismatch";
		if (this.negc != null && in.negc != null) if (this.negc.compareTo(in.negc) != 0) result = "negc value mismatch";
		if (this.nelc != null && in.nelc != null) if (this.nelc.compareTo(in.nelc) != 0) result = "nelc value mismatch";
		if (this.nesc != null && in.nesc != null) if (this.nesc.compareTo(in.nesc) != 0) result = "nesc value mismatch";
		if (this.netTotal != null && in.netTotal != null) if (this.netTotal.compareTo(in.netTotal) != 0) result = "netTotal value mismatch";
		if (this.nfsc != null && in.nfsc != null) if (this.nfsc.compareTo(in.nfsc) != 0) result = "nfsc value mismatch";
		if (this.nmea != null && in.nmea != null) if (this.nmea.compareTo(in.nmea) != 0) result = "nmea value mismatch";
		if (this.npsc != null && in.npsc != null) if (this.npsc.compareTo(in.npsc) != 0) result = "npsc value mismatch";
		if (this.nrsc != null && in.nrsc != null) if (this.nrsc.compareTo(in.nrsc) != 0) result = "nrsc value mismatch";
		if (this.ntsc != null && in.ntsc != null) if (this.ntsc.compareTo(in.ntsc) != 0) result = "ntsc value mismatch";
		if (this.psoAdm != null && in.psoAdm != null) if (this.psoAdm.compareTo(in.psoAdm) != 0) result = "psoAdm value mismatch";
		if (this.rcc != null && in.rcc != null) if (this.rcc.compareTo(in.rcc) != 0) result = "rcc value mismatch";
		if (this.rsc != null && in.rsc != null) if (this.rsc.compareTo(in.rsc) != 0) result = "rsc value mismatch";
		if (this.rsd != null && in.rsd != null) if (this.rsd.compareTo(in.rsd) != 0) result = "rsd value mismatch";
		if (this.totalAmount != null && in.totalAmount != null) if (this.totalAmount.compareTo(in.totalAmount) != 0) result = "totalAmount value mismatch";
		if (this.totalBesc != null && in.totalBesc != null) if (this.totalBesc.compareTo(in.totalBesc) != 0) result = "totalBesc value mismatch";
		if (this.totalEmcAdm != null && in.totalEmcAdm != null) if (this.totalEmcAdm.compareTo(in.totalEmcAdm) != 0) result = "totalEmcAdm value mismatch";
		if (this.totalFcc != null && in.totalFcc != null) if (this.totalFcc.compareTo(in.totalFcc) != 0) result = "totalFcc value mismatch";
		if (this.totalFsc != null && in.totalFsc != null) if (this.totalFsc.compareTo(in.totalFsc) != 0) result = "totalFsc value mismatch";
		if (this.totalFsd != null && in.totalFsd != null) if (this.totalFsd.compareTo(in.totalFsd) != 0) result = "totalFsd value mismatch";
		if (this.totalGesc != null && in.totalGesc != null) if (this.totalGesc.compareTo(in.totalGesc) != 0) result = "totalGesc value mismatch";
		if (this.totalGmee != null && in.totalGmee != null) if (this.totalGmee.compareTo(in.totalGmee) != 0) result = "totalGmee value mismatch";
		if (this.totalGmef != null && in.totalGmef != null) if (this.totalGmef.compareTo(in.totalGmef) != 0) result = "totalGmef value mismatch";
		if (this.totalHeuc != null && in.totalHeuc != null) if (this.totalHeuc.compareTo(in.totalHeuc) != 0) result = "totalHeuc value mismatch";
		if (this.totalLcsc != null && in.totalLcsc != null) if (this.totalLcsc.compareTo(in.totalLcsc) != 0) result = "totalLcsc value mismatch";
		if (this.totalLesd != null && in.totalLesd != null) if (this.totalLesd.compareTo(in.totalLesd) != 0) result = "totalLesd value mismatch";
		if (this.totalLmee != null && in.totalLmee != null) if (this.totalLmee.compareTo(in.totalLmee) != 0) result = "totalLmee value mismatch";
		if (this.totalLmef != null && in.totalLmef != null) if (this.totalLmef.compareTo(in.totalLmef) != 0) result = "totalLmef value mismatch";
		if (this.totalMeuc != null && in.totalMeuc != null) if (this.totalMeuc.compareTo(in.totalMeuc) != 0) result = "totalMeuc value mismatch";
		if (this.totalNasc != null && in.totalNasc != null) if (this.totalNasc.compareTo(in.totalNasc) != 0) result = "totalNasc value mismatch";
		if (this.totalNeaa != null && in.totalNeaa != null) if (this.totalNeaa.compareTo(in.totalNeaa) != 0) result = "totalNeaa value mismatch";
		if (this.totalNead != null && in.totalNead != null) if (this.totalNead.compareTo(in.totalNead) != 0) result = "totalNead value mismatch";
		if (this.totalNegc != null && in.totalNegc != null) if (this.totalNegc.compareTo(in.totalNegc) != 0) result = "totalNegc value mismatch";
		if (this.totalNelc != null && in.totalNelc != null) if (this.totalNelc.compareTo(in.totalNelc) != 0) result = "totalNelc value mismatch";
		if (this.totalNesc != null && in.totalNesc != null) if (this.totalNesc.compareTo(in.totalNesc) != 0) result = "totalNesc value mismatch";
		if (this.totalNfsc != null && in.totalNfsc != null) if (this.totalNfsc.compareTo(in.totalNfsc) != 0) result = "totalNfsc value mismatch";
		if (this.totalNmea != null && in.totalNmea != null) if (this.totalNmea.compareTo(in.totalNmea) != 0) result = "totalNmea value mismatch";
		if (this.totalNpsc != null && in.totalNpsc != null) if (this.totalNpsc.compareTo(in.totalNpsc) != 0) result = "totalNpsc value mismatch";
		if (this.totalNrsc != null && in.totalNrsc != null) if (this.totalNrsc.compareTo(in.totalNrsc) != 0) result = "totalNrsc value mismatch";
		if (this.totalNtsc != null && in.totalNtsc != null) if (this.totalNtsc.compareTo(in.totalNtsc) != 0) result = "totalNtsc value mismatch";
		if (this.totalPsoAdm != null && in.totalPsoAdm != null) if (this.totalPsoAdm.compareTo(in.totalPsoAdm) != 0) result = "totalPsoAdm value mismatch";
		if (this.totalRcc != null && in.totalRcc != null) if (this.totalRcc.compareTo(in.totalRcc) != 0) result = "totalRcc value mismatch";
		if (this.totalRsc != null && in.totalRsc != null) if (this.totalRsc.compareTo(in.totalRsc) != 0) result = "totalRsc value mismatch";
		if (this.totalRsd != null && in.totalRsd != null) if (this.totalRsd.compareTo(in.totalRsd) != 0) result = "totalRsd value mismatch";
		if (this.totalVcsc != null && in.totalVcsc != null) if (this.totalVcsc.compareTo(in.totalVcsc) != 0) result = "totalVcsc value mismatch";
		if (this.ipGstBesc != null && in.ipGstBesc != null) if (this.ipGstBesc.compareTo(in.ipGstBesc) != 0) result = "ipGstBesc value mismatch";
		if (this.ipGstEmcAdm != null && in.ipGstEmcAdm != null) if (this.ipGstEmcAdm.compareTo(in.ipGstEmcAdm) != 0) result = "ipGstEmcAdm value mismatch";
		if (this.ipGstFcc != null && in.ipGstFcc != null) if (this.ipGstFcc.compareTo(in.ipGstFcc) != 0) result = "ipGstFcc value mismatch";
		if (this.ipGstFsc != null && in.ipGstFsc != null) if (this.ipGstFsc.compareTo(in.ipGstFsc) != 0) result = "ipGstFsc value mismatch";
		if (this.ipGstFsd != null && in.ipGstFsd != null) if (this.ipGstFsd.compareTo(in.ipGstFsd) != 0) result = "ipGstFsd value mismatch";
		if (this.ipGstGesc != null && in.ipGstGesc != null) if (this.ipGstGesc.compareTo(in.ipGstGesc) != 0) result = "ipGstGesc value mismatch";
		if (this.ipGstGmee != null && in.ipGstGmee != null) if (this.ipGstGmee.compareTo(in.ipGstGmee) != 0) result = "ipGstGmee value mismatch";
		if (this.ipGstGmef != null && in.ipGstGmef != null) if (this.ipGstGmef.compareTo(in.ipGstGmef) != 0) result = "ipGstGmef value mismatch";
		if (this.ipGstHeuc != null && in.ipGstHeuc != null) if (this.ipGstHeuc.compareTo(in.ipGstHeuc) != 0) result = "ipGstHeuc value mismatch";
		if (this.ipGstLcsc != null && in.ipGstLcsc != null) if (this.ipGstLcsc.compareTo(in.ipGstLcsc) != 0) result = "ipGstLcsc value mismatch";
		if (this.ipGstLesd != null && in.ipGstLesd != null) if (this.ipGstLesd.compareTo(in.ipGstLesd) != 0) result = "ipGstLesd value mismatch";
		if (this.ipGstLmee != null && in.ipGstLmee != null) if (this.ipGstLmee.compareTo(in.ipGstLmee) != 0) result = "ipGstLmee value mismatch";
		if (this.ipGstLmef != null && in.ipGstLmef != null) if (this.ipGstLmef.compareTo(in.ipGstLmef) != 0) result = "ipGstLmef value mismatch";
		if (this.ipGstMeuc != null && in.ipGstMeuc != null) if (this.ipGstMeuc.compareTo(in.ipGstMeuc) != 0) result = "ipGstMeuc value mismatch";
		if (this.ipGstNasc != null && in.ipGstNasc != null) if (this.ipGstNasc.compareTo(in.ipGstNasc) != 0) result = "ipGstNasc value mismatch";
		if (this.ipGstNeaa != null && in.ipGstNeaa != null) if (this.ipGstNeaa.compareTo(in.ipGstNeaa) != 0) result = "ipGstNeaa value mismatch";
		if (this.ipGstNead != null && in.ipGstNead != null) if (this.ipGstNead.compareTo(in.ipGstNead) != 0) result = "ipGstNead value mismatch";
		if (this.ipGstNegc != null && in.ipGstNegc != null) if (this.ipGstNegc.compareTo(in.ipGstNegc) != 0) result = "ipGstNegc value mismatch";
		if (this.ipGstNelc != null && in.ipGstNelc != null) if (this.ipGstNelc.compareTo(in.ipGstNelc) != 0) result = "ipGstNelc value mismatch";
		if (this.ipGstNesc != null && in.ipGstNesc != null) if (this.ipGstNesc.compareTo(in.ipGstNesc) != 0) result = "ipGstNesc value mismatch";
		if (this.ipGstNfsc != null && in.ipGstNfsc != null) if (this.ipGstNfsc.compareTo(in.ipGstNfsc) != 0) result = "ipGstNfsc value mismatch";
		if (this.ipGstNmea != null && in.ipGstNmea != null) if (this.ipGstNmea.compareTo(in.ipGstNmea) != 0) result = "ipGstNmea value mismatch";
		//if (this.ipGstNpsc != null && in.ipGstNpsc != null) if (this.ipGstNpsc.compareTo(in.ipGstNpsc) != 0) result = "ipGstNpsc value mismatch";
		if (this.ipGstNrsc != null && in.ipGstNrsc != null) if (this.ipGstNrsc.compareTo(in.ipGstNrsc) != 0) result = "ipGstNrsc value mismatch";
		if (this.ipGstNtsc != null && in.ipGstNtsc != null) if (this.ipGstNtsc.compareTo(in.ipGstNtsc) != 0) result = "ipGstNtsc value mismatch";
		if (this.ipGstPsoAdm != null && in.ipGstPsoAdm != null) if (this.ipGstPsoAdm.compareTo(in.ipGstPsoAdm) != 0) result = "ipGstPsoAdm value mismatch";
		if (this.ipGstRcc != null && in.ipGstRcc != null) if (this.ipGstRcc.compareTo(in.ipGstRcc) != 0) result = "ipGstRcc value mismatch";
		if (this.ipGstRsc != null && in.ipGstRsc != null) if (this.ipGstRsc.compareTo(in.ipGstRsc) != 0) result = "ipGstRsc value mismatch";
		if (this.ipGstRsd != null && in.ipGstRsd != null) if (this.ipGstRsd.compareTo(in.ipGstRsd) != 0) result = "ipGstRsd value mismatch";
		if (this.ipGstTotal != null && in.ipGstTotal != null) if (this.ipGstTotal.compareTo(in.ipGstTotal) != 0) result = "ipGstTotal value mismatch";
		if (this.ipGstVcsc != null && in.ipGstVcsc != null) if (this.ipGstVcsc.compareTo(in.ipGstVcsc) != 0) result = "ipGstVcsc value mismatch";
		if (this.vcsc != null && in.vcsc != null) if (this.vcsc.compareTo(in.vcsc) != 0) result = "vcsc value mismatch";

		if (this.fssc != null && in.fssc != null) if (this.fssc.compareTo(in.fssc) != 0) result = "fssc value mismatch";
		if (this.ipGstFssc != null && in.ipGstFssc != null) if (this.ipGstFssc.compareTo(in.ipGstFssc) != 0) result = "ipGstFssc value mismatch";
		if (this.opGstFssc != null && in.opGstFssc != null) if (this.opGstFssc.compareTo(in.opGstFssc) != 0) result = "opGstFssc value mismatch";
		if (this.totalFssc != null && in.totalFssc != null) if (this.totalFssc.compareTo(in.totalFssc) != 0) result = "totalFssc value mismatch";

		return result;
	}
	
	public String toInputString() {
		
		String result = (this.participantId != null? this.participantId: "") + "," +
				(this.emcAccount == true? "1": "0") + "," +
				(this.psoAccount == true? "1": "0") + "," +
				(this.msslAccount == true? "1": "0") + "," +
				(this.taxable == true? "1": "0") + "," +
				(this.rerunGstRate != null? this.rerunGstRate.toString(): "");

		return result;
	}

	public String toOutputString() {
		
		String result = (this.opGstBesc != null? this.opGstBesc.toString(): "") + "," +
				(this.opGstEmcAdm != null? this.opGstEmcAdm.toString(): "") + "," +
				(this.opGstFcc != null? this.opGstFcc.toString(): "") + "," +
				(this.opGstFsc != null? this.opGstFsc.toString(): "") + "," +
				(this.opGstFsd != null? this.opGstFsd.toString(): "") + "," +
				(this.opGstGesc != null? this.opGstGesc.toString(): "") + "," +
				(this.opGstGmee != null? this.opGstGmee.toString(): "") + "," +
				(this.opGstGmef != null? this.opGstGmef.toString(): "") + "," +
				(this.opGstHeuc != null? this.opGstHeuc.toString(): "") + "," +
				(this.opGstLcsc != null? this.opGstLcsc.toString(): "") + "," +
				(this.opGstLesd != null? this.opGstLesd.toString(): "") + "," +
				(this.opGstLmee != null? this.opGstLmee.toString(): "") + "," +
				(this.opGstLmef != null? this.opGstLmef.toString(): "") + "," +
				(this.opGstMeuc != null? this.opGstMeuc.toString(): "") + "," +
				(this.opGstNasc != null? this.opGstNasc.toString(): "") + "," +
				(this.opGstNeaa != null? this.opGstNeaa.toString(): "") + "," +
				(this.opGstNead != null? this.opGstNead.toString(): "") + "," +
				(this.opGstNegc != null? this.opGstNegc.toString(): "") + "," +
				(this.opGstNelc != null? this.opGstNelc.toString(): "") + "," +
				(this.opGstNesc != null? this.opGstNesc.toString(): "") + "," +
				(this.opGstNfsc != null? this.opGstNfsc.toString(): "") + "," +
				(this.opGstNmea != null? this.opGstNmea.toString(): "") + "," +
				(this.opGstNpsc != null? this.opGstNpsc.toString(): "") + "," +
				(this.opGstNrsc != null? this.opGstNrsc.toString(): "") + "," +
				(this.opGstNtsc != null? this.opGstNtsc.toString(): "") + "," +
				(this.opGstPsoAdm != null? this.opGstPsoAdm.toString(): "") + "," +
				(this.opGstRcc != null? this.opGstRcc.toString(): "") + "," +
				(this.opGstRsc != null? this.opGstRsc.toString(): "") + "," +
				(this.opGstRsd != null? this.opGstRsd.toString(): "") + "," +
				(this.opGstTotal != null? this.opGstTotal.toString(): "") + "," +
				(this.opGstVcsc != null? this.opGstVcsc.toString(): "") + "," +
				(this.besc != null? this.besc.toString(): "") + "," +
				(this.emcAdm != null? this.emcAdm.toString(): "") + "," +
				(this.fcc != null? this.fcc.toString(): "") + "," +
				(this.fsc != null? this.fsc.toString(): "") + "," +
				(this.fsd != null? this.fsd.toString(): "") + "," +
				(this.gesc != null? this.gesc.toString(): "") + "," +
				(this.gmee != null? this.gmee.toString(): "") + "," +
				(this.gmef != null? this.gmef.toString(): "") + "," +
				(this.heuc != null? this.heuc.toString(): "") + "," +
				(this.lcsc != null? this.lcsc.toString(): "") + "," +
				(this.lesd != null? this.lesd.toString(): "") + "," +
				(this.lmee != null? this.lmee.toString(): "") + "," +
				(this.lmef != null? this.lmef.toString(): "") + "," +
				(this.meuc != null? this.meuc.toString(): "") + "," +
				this.participantId + "," +
				(this.nasc != null? this.nasc.toString(): "") + "," +
				(this.neaa != null? this.neaa.toString(): "") + "," +
				(this.nead != null? this.nead.toString(): "") + "," +
				(this.negc != null? this.negc.toString(): "") + "," +
				(this.nelc != null? this.nelc.toString(): "") + "," +
				(this.nesc != null? this.nesc.toString(): "") + "," +
				(this.netTotal != null? this.netTotal.toString(): "") + "," +
				(this.nfsc != null? this.nfsc.toString(): "") + "," +
				(this.nmea != null? this.nmea.toString(): "") + "," +
				(this.npsc != null? this.npsc.toString(): "") + "," +
				(this.nrsc != null? this.nrsc.toString(): "") + "," +
				(this.ntsc != null? this.ntsc.toString(): "") + "," +
				(this.psoAdm != null? this.psoAdm.toString(): "") + "," +
				(this.rcc != null? this.rcc.toString(): "") + "," +
				(this.rsc != null? this.rsc.toString(): "") + "," +
				(this.rsd != null? this.rsd.toString(): "") + "," +
				(this.totalAmount != null? this.totalAmount.toString(): "") + "," +
				(this.totalBesc != null? this.totalBesc.toString(): "") + "," +
				(this.totalEmcAdm != null? this.totalEmcAdm.toString(): "") + "," +
				(this.totalFcc != null? this.totalFcc.toString(): "") + "," +
				(this.totalFsc != null? this.totalFsc.toString(): "") + "," +
				(this.totalFsd != null? this.totalFsd.toString(): "") + "," +
				(this.totalGesc != null? this.totalGesc.toString(): "") + "," +
				(this.totalGmee != null? this.totalGmee.toString(): "") + "," +
				(this.totalGmef != null? this.totalGmef.toString(): "") + "," +
				(this.totalHeuc != null? this.totalHeuc.toString(): "") + "," +
				(this.totalLcsc != null? this.totalLcsc.toString(): "") + "," +
				(this.totalLesd != null? this.totalLesd.toString(): "") + "," +
				(this.totalLmee != null? this.totalLmee.toString(): "") + "," +
				(this.totalLmef != null? this.totalLmef.toString(): "") + "," +
				(this.totalMeuc != null? this.totalMeuc.toString(): "") + "," +
				(this.totalNasc != null? this.totalNasc.toString(): "") + "," +
				(this.totalNeaa != null? this.totalNeaa.toString(): "") + "," +
				(this.totalNead != null? this.totalNead.toString(): "") + "," +
				(this.totalNegc != null? this.totalNegc.toString(): "") + "," +
				(this.totalNelc != null? this.totalNelc.toString(): "") + "," +
				(this.totalNesc != null? this.totalNesc.toString(): "") + "," +
				(this.totalNfsc != null? this.totalNfsc.toString(): "") + "," +
				(this.totalNmea != null? this.totalNmea.toString(): "") + "," +
				(this.totalNpsc != null? this.totalNpsc.toString(): "") + "," +
				(this.totalNrsc != null? this.totalNrsc.toString(): "") + "," +
				(this.totalNtsc != null? this.totalNtsc.toString(): "") + "," +
				(this.totalPsoAdm != null? this.totalPsoAdm.toString(): "") + "," +
				(this.totalRcc != null? this.totalRcc.toString(): "") + "," +
				(this.totalRsc != null? this.totalRsc.toString(): "") + "," +
				(this.totalRsd != null? this.totalRsd.toString(): "") + "," +
				(this.totalVcsc != null? this.totalVcsc.toString(): "") + "," +
				(this.ipGstBesc != null? this.ipGstBesc.toString(): "") + "," +
				(this.ipGstEmcAdm != null? this.ipGstEmcAdm.toString(): "") + "," +
				(this.ipGstFcc != null? this.ipGstFcc.toString(): "") + "," +
				(this.ipGstFsc != null? this.ipGstFsc.toString(): "") + "," +
				(this.ipGstFsd != null? this.ipGstFsd.toString(): "") + "," +
				(this.ipGstGesc != null? this.ipGstGesc.toString(): "") + "," +
				(this.ipGstGmee != null? this.ipGstGmee.toString(): "") + "," +
				(this.ipGstGmef != null? this.ipGstGmef.toString(): "") + "," +
				(this.ipGstHeuc != null? this.ipGstHeuc.toString(): "") + "," +
				(this.ipGstLcsc != null? this.ipGstLcsc.toString(): "") + "," +
				(this.ipGstLesd != null? this.ipGstLesd.toString(): "") + "," +
				(this.ipGstLmee != null? this.ipGstLmee.toString(): "") + "," +
				(this.ipGstLmef != null? this.ipGstLmef.toString(): "") + "," +
				(this.ipGstMeuc != null? this.ipGstMeuc.toString(): "") + "," +
				(this.ipGstNasc != null? this.ipGstNasc.toString(): "") + "," +
				(this.ipGstNeaa != null? this.ipGstNeaa.toString(): "") + "," +
				(this.ipGstNead != null? this.ipGstNead.toString(): "") + "," +
				(this.ipGstNegc != null? this.ipGstNegc.toString(): "") + "," +
				(this.ipGstNelc != null? this.ipGstNelc.toString(): "") + "," +
				(this.ipGstNesc != null? this.ipGstNesc.toString(): "") + "," +
				(this.ipGstNfsc != null? this.ipGstNfsc.toString(): "") + "," +
				(this.ipGstNmea != null? this.ipGstNmea.toString(): "") + "," +
				(this.ipGstNpsc != null? this.ipGstNpsc.toString(): "") + "," +
				(this.ipGstNrsc != null? this.ipGstNrsc.toString(): "") + "," +
				(this.ipGstNtsc != null? this.ipGstNtsc.toString(): "") + "," +
				(this.ipGstPsoAdm != null? this.ipGstPsoAdm.toString(): "") + "," +
				(this.ipGstRcc != null? this.ipGstRcc.toString(): "") + "," +
				(this.ipGstRsc != null? this.ipGstRsc.toString(): "") + "," +
				(this.ipGstRsd != null? this.ipGstRsd.toString(): "") + "," +
				(this.ipGstTotal != null? this.ipGstTotal.toString(): "") + "," +
				(this.ipGstVcsc != null? this.ipGstVcsc.toString(): "") + "," +
				(this.vcsc != null? this.vcsc.toString(): "") + "," +
				(this.emcAccount == true? "1": "0") + "," +
				(this.psoAccount == true? "1": "0") + "," +
				(this.msslAccount == true? "1": "0") + "," +
				(this.taxable == true? "1": "0") + "," +
				(this.rerunGstRate != null? this.rerunGstRate.toString(): "") + "," +
				(this.fssc != null? this.fssc.toString(): "") + "," +
				(this.ipGstFssc != null? this.ipGstFssc.toString(): "") + "," +
				(this.opGstFssc != null? this.opGstFssc.toString(): "") + "," +
				(this.totalFssc != null? this.totalFssc.toString(): "");
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"participantId," +
			"emcAccount," +
			"psoAccount," +
			"msslAccount," +
			"taxable," +
			"rerunGstRate";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"opGstBesc," +
			"opGstEmcAdm," +
			"opGstFcc," +
			"opGstFsc," +
			"opGstFsd," +
			"opGstGesc," +
			"opGstGmee," +
			"opGstGmef," +
			"opGstHeuc," +
			"opGstLcsc," +
			"opGstLesd," +
			"opGstLmee," +
			"opGstLmef," +
			"opGstMeuc," +
			"opGstNasc," +
			"opGstNeaa," +
			"opGstNead," +
			"opGstNegc," +
			"opGstNelc," +
			"opGstNesc," +
			"opGstNfsc," +
			"opGstNmea," +
			"opGstNpsc," +
			"opGstNrsc," +
			"opGstNtsc," +
			"opGstPsoAdm," +
			"opGstRcc," +
			"opGstRsc," +
			"opGstRsd," +
			"opGstTotal," +
			"opGstVcsc," +
			"besc," +
			"emcAdm," +
			"fcc," +
			"fsc," +
			"fsd," +
			"gesc," +
			"gmee," +
			"gmef," +
			"heuc," +
			"lcsc," +
			"lesd," +
			"lmee," +
			"lmef," +
			"meuc," +
			"participantId," +
			"nasc," +
			"neaa," +
			"nead," +
			"negc," +
			"nelc," +
			"nesc," +
			"netTotal," +
			"nfsc," +
			"nmea," +
			"npsc," +
			"nrsc," +
			"ntsc," +
			"psoAdm," +
			"rcc," +
			"rsc," +
			"rsd," +
			"totalAmount," +
			"totalBesc," +
			"totalEmcAdm," +
			"totalFcc," +
			"totalFsc," +
			"totalFsd," +
			"totalGesc," +
			"totalGmee," +
			"totalGmef," +
			"totalHeuc," +
			"totalLcsc," +
			"totalLesd," +
			"totalLmee," +
			"totalLmef," +
			"totalMeuc," +
			"totalNasc," +
			"totalNeaa," +
			"totalNead," +
			"totalNegc," +
			"totalNelc," +
			"totalNesc," +
			"totalNfsc," +
			"totalNmea," +
			"totalNpsc," +
			"totalNrsc," +
			"totalNtsc," +
			"totalPsoAdm," +
			"totalRcc," +
			"totalRsc," +
			"totalRsd," +
			"totalVcsc," +
			"ipGstBesc," +
			"ipGstEmcAdm," +
			"ipGstFcc," +
			"ipGstFsc," +
			"ipGstFsd," +
			"ipGstGesc," +
			"ipGstGmee," +
			"ipGstGmef," +
			"ipGstHeuc," +
			"ipGstLcsc," +
			"ipGstLesd," +
			"ipGstLmee," +
			"ipGstLmef," +
			"ipGstMeuc," +
			"ipGstNasc," +
			"ipGstNeaa," +
			"ipGstNead," +
			"ipGstNegc," +
			"ipGstNelc," +
			"ipGstNesc," +
			"ipGstNfsc," +
			"ipGstNmea," +
			"ipGstNpsc," +
			"ipGstNrsc," +
			"ipGstNtsc," +
			"ipGstPsoAdm," +
			"ipGstRcc," +
			"ipGstRsc," +
			"ipGstRsd," +
			"ipGstTotal," +
			"ipGstVcsc," +
			"vcsc," +
			"emcAccount," +
			"psoAccount," +
			"msslAccount," +
			"taxable," +
			"rerunGstRate," +
			"fssc," +
			"ipGstFssc," +
			"opGstFssc," +
			"totalFssc";

		return header;
	}
}
