package com.emc.sett.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import com.emc.sett.model.GlobalT;

public class Global extends GlobalT {

	@SuppressWarnings("unused")
	public void initInput(String [] line) throws ParseException, DatatypeConfigurationException {
		
		String cmwh_emcadm = line[0];
		String cmwh_psoadm = line[1];
		String csz = line[2];
		String dim = line[3];
		String emc_bdgt = line[4];
		String meuc = line[5];
		String pso_bdgt = line[6];
		String input_gst = line[7];
		String non_input_gst = line[8];
		String non_output_gst = line[9];
		String output_gst = line[10];
		String run_type = line[11];
		String sett_run_id = line[12];
		String settlement_date = line[13];
		String total_tte = line[14];
		String zero_input_gst = line[15];
		String zero_output_gst = line[16];
		String standing_version = line[17];
		String total_emcadm = line[18];
		String total_psoadm = line[19];
		String input_gst_code = line[20];
		String non_input_gst_code = line[21];
		String non_output_gst_code = line[22];
		String output_gst_code = line[23];
		String zero_input_gst_code = line[24];
		String zero_output_gst_code = line[25];
		String EMCADM_price_cap = line[26];
		String price_cap_effective_flag = line[27];
		String fsc_scheme_startdate = line[28];
		String fsc_scheme_enddate = line[29];
		String igs_effective_flag = line[30];
		
		this.csz = csz.length() > 0? new BigDecimal(csz): null;
		this.dim = dim.length() > 0? new BigDecimal(dim): null;
		this.emcBudget = emc_bdgt.length() > 0? new BigDecimal(emc_bdgt): null;
		this.meuc = meuc.length() > 0? new BigDecimal(meuc): null;
		this.psoBudget = pso_bdgt.length() > 0? new BigDecimal(pso_bdgt): null;
		this.inputGstRate = input_gst.length() > 0? new BigDecimal(input_gst): null;
		this.nonInputGstRate = non_input_gst.length() > 0? new BigDecimal(non_input_gst): null;
		this.nonOutputGstRate = non_output_gst.length() > 0? new BigDecimal(non_output_gst): null;
		this.outputGstRate = output_gst.length() > 0? new BigDecimal(output_gst): null;
		this.runType = run_type;
		this.runId = sett_run_id;
	    GregorianCalendar calender = new GregorianCalendar();
	    calender.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(settlement_date));
	    this.tradingDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
		this.totalTte = total_tte.length() > 0? new BigDecimal(total_tte): null;
		this.zeroInputGstRate = zero_input_gst.length() > 0? new BigDecimal(zero_input_gst): null;
		this.zeroOutputGstRate = zero_output_gst.length() > 0? new BigDecimal(zero_output_gst): null;
		this.standingVersion = standing_version;
		this.totalEmcAdm = total_emcadm.length() > 0? new BigDecimal(total_emcadm): null;
		this.totalPsoAdm = total_psoadm.length() > 0? new BigDecimal(total_psoadm): null;
		this.inputGstCode = input_gst_code;
		this.nonInputGstCode = non_input_gst_code;
		this.nonOutputGstCode = non_output_gst_code;
		this.outputGstCode = output_gst_code;
		this.zeroInputGstCode = zero_input_gst_code;
		this.zeroOutputGstCode = zero_output_gst_code;
		this.emcAdmPriceCap = EMCADM_price_cap.length() > 0? new BigDecimal(EMCADM_price_cap): null;
		this.priceCapEffective = price_cap_effective_flag.equals("1");
	    calender = new GregorianCalendar();
	    calender.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(fsc_scheme_startdate));
	    this.fscStartDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
	    calender = new GregorianCalendar();
	    calender.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(fsc_scheme_enddate));
	    this.fscEndDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
		this.igsEffective = igs_effective_flag.equals("1");
		this.totalEmcAdmSet = false;
		this.totalPsoAdmSet = false;
		this.totalTteSet = false;
	}

	//
	// for unit or regression testing
	//
	public void initOutput(String [] line) throws ParseException, DatatypeConfigurationException {
		
		String round_cmwh_emcadm = line[0];
		String round_cmwh_psoadm = line[1];
		String csz = line[2];
		String dim = line[3];
		String emc_bdgt = line[4];
		String meuc = line[5];
		String pso_bdgt = line[6];
		String input_gst = line[7];
		String non_input_gst = line[8];
		String non_output_gst = line[9];
		String output_gst = line[10];
		String run_type = line[11];
		String sett_run_id = line[12];
		String settlement_date = line[13];
		String total_tte = line[14];
		String zero_input_gst = line[15];
		String zero_output_gst = line[16];
		String standing_version = line[17];
		String total_emcadm = line[18];
		String total_psoadm = line[19];
		String total_gmee = line[20];
		String total_gmef = line[21];
		String total_lmee = line[22];
		String total_lmef = line[23];
		String total_nmea = line[24];
		String cmwh_emcadm = line[25];
		String cmwh_psoadm = line[26];
		String EMCADM_price_cap = line[27];
		String EMCADM_price_adj_rate = line[28];
		String EMCADM_price_adj_rate_round = line[29];
		String fsc_scheme_startdate = line[30];
		String fsc_scheme_enddate = line[31];
		
		this.cmwhEmcAdmRounded = round_cmwh_emcadm.length() > 0? new BigDecimal(round_cmwh_emcadm): null;
		this.cmwhPsoAdmRounded = round_cmwh_psoadm.length() > 0? new BigDecimal(round_cmwh_psoadm): null;
		this.csz = csz.length() > 0? new BigDecimal(csz): null;
		this.dim = dim.length() > 0? new BigDecimal(dim): null;
		this.emcBudget = emc_bdgt.length() > 0? new BigDecimal(emc_bdgt): null;
		this.meuc = meuc.length() > 0? new BigDecimal(meuc): null;
		this.psoBudget = pso_bdgt.length() > 0? new BigDecimal(pso_bdgt): null;
		this.inputGstRate = input_gst.length() > 0? new BigDecimal(input_gst): null;
		this.nonInputGstRate = non_input_gst.length() > 0? new BigDecimal(non_input_gst): null;
		this.nonOutputGstRate = non_output_gst.length() > 0? new BigDecimal(non_output_gst): null;
		this.outputGstRate = output_gst.length() > 0? new BigDecimal(output_gst): null;
		this.runType = run_type;
		this.runId = sett_run_id;
	    GregorianCalendar calender = new GregorianCalendar();
	    calender.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(settlement_date));
	    this.tradingDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
		this.totalTte = total_tte.length() > 0? new BigDecimal(total_tte): null;
		this.zeroInputGstRate = zero_input_gst.length() > 0? new BigDecimal(zero_input_gst): null;
		this.zeroOutputGstRate = zero_output_gst.length() > 0? new BigDecimal(zero_output_gst): null;
		this.standingVersion = standing_version;
		this.totalEmcAdm = total_emcadm.length() > 0? new BigDecimal(total_emcadm): null;
		this.totalPsoAdm = total_psoadm.length() > 0? new BigDecimal(total_psoadm): null;
		this.totalGmee = total_gmee.length() > 0? new BigDecimal(total_gmee): null;
		this.totalGmef = total_gmef.length() > 0? new BigDecimal(total_gmef): null;
		this.totalLmee = total_lmee.length() > 0? new BigDecimal(total_lmee): null;
		this.totalLmef = total_lmef.length() > 0? new BigDecimal(total_lmef): null;
		this.totalNmea = total_nmea.length() > 0? new BigDecimal(total_nmea): null;
		this.cmwhEmcAdm = cmwh_emcadm.length() > 0? new BigDecimal(cmwh_emcadm): null;
		this.cmwhPsoAdm = cmwh_psoadm.length() > 0? new BigDecimal(cmwh_psoadm): null;
		this.emcAdmPriceCap = EMCADM_price_cap.length() > 0? new BigDecimal(EMCADM_price_cap): null;
		this.emcAdmPriceAdjRate = EMCADM_price_adj_rate.length() > 0? new BigDecimal(EMCADM_price_adj_rate): null;
		this.emcAdmPriceAdjRateRounded = EMCADM_price_adj_rate_round.length() > 0? new BigDecimal(EMCADM_price_adj_rate_round): null;
	    calender = new GregorianCalendar();
	    calender.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(fsc_scheme_startdate));
	    this.fscStartDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
	    calender = new GregorianCalendar();
	    calender.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(fsc_scheme_enddate));
	    this.fscEndDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(calender);
	}
	
	public String PFCheck(Global in) {

		String result = null;

		if ((this.cmwhEmcAdmRounded == null && in.cmwhEmcAdmRounded != null) || (this.cmwhEmcAdmRounded != null && in.cmwhEmcAdmRounded == null)) result = "cmwhEmcAdmRounded missing value";
		if ((this.cmwhPsoAdmRounded == null && in.cmwhPsoAdmRounded != null) || (this.cmwhPsoAdmRounded != null && in.cmwhPsoAdmRounded == null)) result = "cmwhPsoAdmRounded missing value";
		if ((this.totalTte == null && in.totalTte != null) || (this.totalTte != null && in.totalTte == null)) result = "totalTte missing value";
		if ((this.totalEmcAdm == null && in.totalEmcAdm != null) || (this.totalEmcAdm != null && in.totalEmcAdm == null)) result = "totalEmcAdm missing value";
		if ((this.totalPsoAdm == null && in.totalPsoAdm != null) || (this.totalPsoAdm != null && in.totalPsoAdm == null)) result = "totalPsoAdm missing value";
		if ((this.totalGmee == null && in.totalGmee != null) || (this.totalGmee != null && in.totalGmee == null)) result = "totalGmee missing value";
		if ((this.totalGmef == null && in.totalGmef != null) || (this.totalGmef != null && in.totalGmef == null)) result = "totalGmef missing value";
		if ((this.totalLmee == null && in.totalLmee != null) || (this.totalLmee != null && in.totalLmee == null)) result = "totalLmee missing value";
		if ((this.totalLmef == null && in.totalLmef != null) || (this.totalLmef != null && in.totalLmef == null)) result = "totalLmef missing value";
		if ((this.totalNmea == null && in.totalNmea != null) || (this.totalNmea != null && in.totalNmea == null)) result = "totalNmea missing value";
		if ((this.cmwhEmcAdm == null && in.cmwhEmcAdm != null) || (this.cmwhEmcAdm != null && in.cmwhEmcAdm == null)) result = "cmwhEmcAdm missing value";
		if ((this.cmwhPsoAdm == null && in.cmwhPsoAdm != null) || (this.cmwhPsoAdm != null && in.cmwhPsoAdm == null)) result = "cmwhPsoAdm missing value";
		if ((this.emcAdmPriceAdjRate == null && in.emcAdmPriceAdjRate != null) || (this.emcAdmPriceAdjRate != null && in.emcAdmPriceAdjRate == null)) result = "emcAdmPriceAdjRate missing value";
		if ((this.emcAdmPriceAdjRateRounded == null && in.emcAdmPriceAdjRateRounded != null) || (this.emcAdmPriceAdjRateRounded != null && in.emcAdmPriceAdjRateRounded == null)) result = "emcAdmPriceAdjRateRounded missing value";

		return result;
	}
	
	public String RSCheck(Global in) {

		String result = null;

		if ((this.totalGmee == null && in.totalGmee != null) || (this.totalGmee != null && in.totalGmee == null)) result = "totalGmee missing value";
		if ((this.totalGmef == null && in.totalGmef != null) || (this.totalGmef != null && in.totalGmef == null)) result = "totalGmef missing value";
		if ((this.totalLmee == null && in.totalLmee != null) || (this.totalLmee != null && in.totalLmee == null)) result = "totalLmee missing value";
		if ((this.totalLmef == null && in.totalLmef != null) || (this.totalLmef != null && in.totalLmef == null)) result = "totalLmef missing value";
		if ((this.totalNmea == null && in.totalNmea != null) || (this.totalNmea != null && in.totalNmea == null)) result = "totalNmea missing value";

		return result;
	}
	
	public String equal(Global in) {

		String result = null;

		if (this.cmwhEmcAdmRounded != null && in.cmwhEmcAdmRounded != null) if (this.cmwhEmcAdmRounded.compareTo(in.cmwhEmcAdmRounded) != 0) result = "cmwhEmcAdmRounded value mismatch";
		if (this.cmwhPsoAdmRounded != null && in.cmwhPsoAdmRounded != null) if (this.cmwhPsoAdmRounded.compareTo(in.cmwhPsoAdmRounded) != 0) result = "cmwhPsoAdmRounded value mismatch";
		if (this.totalTte != null && in.totalTte != null) if (this.totalTte.compareTo(in.totalTte) != 0) result = "totalTte value mismatch";
		if (this.totalEmcAdm != null && in.totalEmcAdm != null) if (this.totalEmcAdm.compareTo(in.totalEmcAdm) != 0) result = "totalEmcAdm value mismatch";
		if (this.totalPsoAdm != null && in.totalPsoAdm != null) if (this.totalPsoAdm.compareTo(in.totalPsoAdm) != 0) result = "totalPsoAdm value mismatch";
		if (this.totalGmee != null && in.totalGmee != null) if (this.totalGmee.compareTo(in.totalGmee) != 0) result = "totalGmee value mismatch";
		if (this.totalGmef != null && in.totalGmef != null) if (this.totalGmef.compareTo(in.totalGmef) != 0) result = "totalGmef value mismatch";
		if (this.totalLmee != null && in.totalLmee != null) if (this.totalLmee.compareTo(in.totalLmee) != 0) result = "totalLmee value mismatch";
		if (this.totalLmef != null && in.totalLmef != null) if (this.totalLmef.compareTo(in.totalLmef) != 0) result = "totalLmef value mismatch";
		if (this.totalNmea != null && in.totalNmea != null) if (this.totalNmea.compareTo(in.totalNmea) != 0) result = "totalNmea value mismatch";
		if (this.cmwhEmcAdm != null && in.cmwhEmcAdm != null) if (this.cmwhEmcAdm.setScale(8, BigDecimal.ROUND_HALF_UP).compareTo(in.cmwhEmcAdm.setScale(8, BigDecimal.ROUND_HALF_UP)) != 0) result = "cmwhEmcAdm value mismatch";
		if (this.cmwhPsoAdm != null && in.cmwhPsoAdm != null) if (this.cmwhPsoAdm.setScale(8, BigDecimal.ROUND_HALF_UP).compareTo(in.cmwhPsoAdm.setScale(8, BigDecimal.ROUND_HALF_UP)) != 0) result = "cmwhPsoAdm value mismatch";
		if (this.emcAdmPriceAdjRate != null && in.emcAdmPriceAdjRate != null) if (this.emcAdmPriceAdjRate.setScale(8, BigDecimal.ROUND_HALF_UP).compareTo(in.emcAdmPriceAdjRate.setScale(8, BigDecimal.ROUND_HALF_UP)) != 0) result = "emcAdmPriceAdjRate value mismatch";
		if (this.emcAdmPriceAdjRateRounded != null && in.emcAdmPriceAdjRateRounded != null) if (this.emcAdmPriceAdjRateRounded.compareTo(in.emcAdmPriceAdjRateRounded) != 0) result = "emcAdmPriceAdjRateRounded value mismatch";

		return result;
	}

	public String toInputString() {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String tdStr = "";
		String fscsStr = "";
		String fsceStr = "";
		
		if (this.tradingDate != null) {
			Calendar d = this.tradingDate.toGregorianCalendar();
			formatter.setTimeZone(d.getTimeZone());
			tdStr = formatter.format(d.getTime());
		}
		
		if (this.fscStartDate != null) {
			Calendar d = this.fscStartDate.toGregorianCalendar();
			formatter.setTimeZone(d.getTimeZone());
			fscsStr = formatter.format(d.getTime());
		}
		
		if (this.fscEndDate != null) {
			Calendar d = this.fscEndDate.toGregorianCalendar();
			formatter.setTimeZone(d.getTimeZone());
			fsceStr = formatter.format(d.getTime());
		}
		
		String result = (this.cmwhEmcAdm != null? this.cmwhEmcAdm.toString(): "") + "," +
				(this.cmwhPsoAdm != null? this.cmwhPsoAdm.toString(): "") + "," +
				(this.csz != null? this.csz.toString(): "") + "," +
				(this.dim != null? this.dim.toString(): "") + "," +
				(this.emcBudget != null? this.emcBudget.toString(): "") + "," +
				(this.meuc != null? this.meuc.toString(): "") + "," +
				(this.psoBudget != null? this.psoBudget.toString(): "") + "," +
				(this.inputGstRate != null? this.inputGstRate.toString(): "") + "," +
				(this.nonInputGstRate != null? this.nonInputGstRate.toString(): "") + "," +
				(this.nonOutputGstRate != null? this.nonOutputGstRate.toString(): "") + "," +
				(this.outputGstRate != null? this.outputGstRate.toString(): "") + "," +
				this.runType + "," +
				this.runId + "," +
				(this.tradingDate != null? tdStr: "") + "," +
				(this.totalTte != null? this.totalTte.toString(): "") + "," +
				(this.zeroInputGstRate != null? this.zeroInputGstRate.toString(): "") + "," +
				(this.zeroOutputGstRate != null? this.zeroOutputGstRate.toString(): "") + "," +
				this.standingVersion + "," +
				(this.totalEmcAdm != null? this.totalEmcAdm.toString(): "") + "," +
				(this.totalPsoAdm != null? this.totalPsoAdm.toString(): "") + "," +
				(this.inputGstCode != null? this.inputGstCode.toString(): "") + "," +
				(this.nonInputGstCode != null? this.nonInputGstCode.toString(): "") + "," +
				(this.nonOutputGstCode != null? this.nonOutputGstCode.toString(): "") + "," +
				(this.outputGstCode != null? this.outputGstCode.toString(): "") + "," +
				(this.zeroInputGstCode != null? this.zeroInputGstCode.toString(): "") + "," +
				(this.zeroOutputGstCode != null? this.zeroOutputGstCode.toString(): "") + "," +
				(this.emcAdmPriceCap != null? this.emcAdmPriceCap.toString(): "") + "," +
				(this.priceCapEffective == true? "1": "0") + "," +
				(this.fscStartDate != null? fscsStr: "") + "," +
				(this.fscEndDate != null? fsceStr: "") + "," +
				(this.igsEffective == true? "1": "0");
		
		return result;
	}

	public String toOutputString() {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String tdStr = "";
		String fscsStr = "";
		String fsceStr = "";
		
		if (this.tradingDate != null) {
			Calendar d = this.tradingDate.toGregorianCalendar();
			formatter.setTimeZone(d.getTimeZone());
			tdStr = formatter.format(d.getTime());
		}
		
		if (this.fscStartDate != null) {
			Calendar d = this.fscStartDate.toGregorianCalendar();
			formatter.setTimeZone(d.getTimeZone());
			fscsStr = formatter.format(d.getTime());
		}
		
		if (this.fscEndDate != null) {
			Calendar d = this.fscEndDate.toGregorianCalendar();
			formatter.setTimeZone(d.getTimeZone());
			fsceStr = formatter.format(d.getTime());
		}
		
		String result = (this.cmwhEmcAdmRounded != null? this.cmwhEmcAdmRounded.toString(): "") + "," +
				(this.cmwhPsoAdmRounded != null? this.cmwhPsoAdmRounded.toString(): "") + "," +
				(this.csz != null? this.csz.toString(): "") + "," +
				(this.dim != null? this.dim.toString(): "") + "," +
				(this.emcBudget != null? this.emcBudget.toString(): "") + "," +
				(this.meuc != null? this.meuc.toString(): "") + "," +
				(this.psoBudget != null? this.psoBudget.toString(): "") + "," +
				(this.inputGstRate != null? this.inputGstRate.toString(): "") + "," +
				(this.nonInputGstRate != null? this.nonInputGstRate.toString(): "") + "," +
				(this.nonOutputGstRate != null? this.nonOutputGstRate.toString(): "") + "," +
				(this.outputGstRate != null? this.outputGstRate.toString(): "") + "," +
				this.runType + "," +
				this.runId + "," +
				(this.tradingDate != null? tdStr: "") + "," +
				(this.totalTte != null? this.totalTte.toString(): "") + "," +
				(this.zeroInputGstRate != null? this.zeroInputGstRate.toString(): "") + "," +
				(this.zeroOutputGstRate != null? this.zeroOutputGstRate.toString(): "") + "," +
				this.standingVersion + "," +
				(this.totalEmcAdm != null? this.totalEmcAdm.toString(): "") + "," +
				(this.totalPsoAdm != null? this.totalPsoAdm.toString(): "") + "," +
				(this.totalGmee != null? this.totalGmee.toString(): "") + "," +
				(this.totalGmef != null? this.totalGmef.toString(): "") + "," +
				(this.totalLmee != null? this.totalLmee.toString(): "") + "," +
				(this.totalLmef != null? this.totalLmef.toString(): "") + "," +
				(this.totalNmea != null? this.totalNmea.toString(): "") + "," +
				(this.cmwhEmcAdm != null? this.cmwhEmcAdm.toString(): "") + "," +
				(this.cmwhPsoAdm != null? this.cmwhPsoAdm.toString(): "") + "," +
				(this.emcAdmPriceCap != null? this.emcAdmPriceCap.toString(): "") + "," +
				(this.emcAdmPriceAdjRate != null? this.emcAdmPriceAdjRate.toString(): "") + "," +
				(this.emcAdmPriceAdjRateRounded != null? this.emcAdmPriceAdjRateRounded.toString(): "") + "," +
				(this.fscStartDate != null? fscsStr: "") + "," +
				(this.fscEndDate != null? fsceStr: "");
		
		return result;
	}
	
	public static String getInputHeader() {
		String header = 
			"cmwhEmcAdm," +
			"cmwhPsoAdm," +
			"csz," +
			"dim," +
			"emcBudget," +
			"meuc," +
			"psoBudget," +
			"inputGstRate," +
			"nonInputGstRate," +
			"nonOutputGstRate," +
			"outputGstRate," +
			"runType," +
			"runId," +
			"tradingDate," +
			"totalTte," +
			"zeroInputGstRate," +
			"zeroOutputGstRate," +
			"standingVersion," +
			"totalEmcAdm," +
			"totalPsoAdm," +
			"inputGstCode," +
			"nonInputGstCode," +
			"nonOutputGstCode," +
			"outputGstCode," +
			"zeroInputGstCode," +
			"zeroOutputGstCode," +
			"emcAdmPriceCap," +
			"priceCapEffective," +
			"fscStartDate," +
			"fscEndDate," +
			"igsEffective";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"cmwhEmcAdmRounded," +
			"cmwhPsoAdmRounded," +
			"csz," +
			"dim," +
			"emcBudget," +
			"meuc," +
			"psoBudget," +
			"inputGstRate," +
			"nonInputGstRate," +
			"nonOutputGstRate," +
			"outputGstRate," +
			"runType," +
			"runId," +
			"tradingDate," +
			"totalTte," +
			"zeroInputGstRate," +
			"zeroOutputGstRate," +
			"standingVersion," +
			"totalEmcAdm," +
			"totalPsoAdm," +
			"totalGmee," +
			"totalGmef," +
			"totalLmee," +
			"totalLmef," +
			"totalNmea," +
			"cmwhEmcAdm," +
			"cmwhPsoAdm," +
			"emcAdmPriceCap," +
			"emcAdmPriceAdjRate," +
			"emcAdmPriceAdjRateRounded," +
			"fscStartDate," +
			"fscEndDate";

		return header;
	}
}
