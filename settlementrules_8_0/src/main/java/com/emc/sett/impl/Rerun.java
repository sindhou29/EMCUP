package com.emc.sett.impl;

import java.math.BigDecimal;
import java.text.ParseException;

import javax.xml.datatype.DatatypeConfigurationException;

import com.emc.sett.model.AdjustmentT;
import com.emc.sett.model.RerunT;

public class Rerun extends RerunT {

	@SuppressWarnings("unused")
	public void initInput(String [] line) {
		
		String rerun_gst_name = line[0];
		String rerun_gst_rate = line[1];
		String rerun_gst_a_inc_gmef = line[2];
		String rerun_gst_a_inc_lmee = line[3];
		String rerun_gst_a_inc_lmef = line[4];
		String rerun_gst_a_inc_nmea = line[5];
		String rerun_gst_v_inc_gmee = line[6];
		String rerun_gst_v_inc_nmea = line[7];
		String rerun_gst_inc_gmee = line[8];
		String rerun_gst_inc_gmef = line[9];
		String rerun_gst_inc_lmee = line[10];
		String rerun_gst_inc_lmef = line[11];
		String rerun_gst_inc_nmea = line[12];
		String rerun_gst_total_inc_gmee = line[13];
		String rerun_gst_total_inc_gmef = line[14];
		String rerun_gst_total_inc_lmee = line[15];
		String rerun_gst_total_inc_lmef = line[16];
		String rerun_gst_total_inc_nmea = line[17];
		String rerun_gst_taxable = line[18];
		
		this.name = rerun_gst_name;
		this.gstRate = rerun_gst_rate.length() > 0? new BigDecimal(rerun_gst_rate): null;
		this.taxable = rerun_gst_taxable.equals("1");
	}
	
	public String getKey() {
		return name + gstRate.toString() + (taxable == true? "1": "0");
	}
    
    public BigDecimal sumIncludedGmee() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getGmee() != null) {
		    		result = result.add(adj.getGmee());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedIpGstGmee() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getIpGstGmee() != null) {
		    		result = result.add(adj.getIpGstGmee());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedTotalGmee() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getTotalGmee() != null) {
		    		result = result.add(adj.getTotalGmee());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedGmef() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getGmef() != null) {
		    		result = result.add(adj.getGmef());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedOpGstGmef() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getOpGstGmef() != null) {
		    		result = result.add(adj.getOpGstGmef());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedTotalGmef() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getTotalGmef() != null) {
		    		result = result.add(adj.getTotalGmef());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedLmee() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getLmee() != null) {
		    		result = result.add(adj.getLmee());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedOpGstLmee() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getOpGstLmee() != null) {
		    		result = result.add(adj.getOpGstLmee());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedTotalLmee() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getTotalLmee() != null) {
		    		result = result.add(adj.getTotalLmee());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedLmef() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getLmef() != null) {
		    		result = result.add(adj.getLmef());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedOpGstLmef() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getOpGstLmef() != null) {
		    		result = result.add(adj.getOpGstLmef());
	    		}
	    	}
		}
    	
        return result;
    }
    
    public BigDecimal sumIncludedTotalLmef() {
    	
		BigDecimal result = BigDecimal.ZERO;
		
		if (this.adjustments != null) {
	    	for (AdjustmentT adj : this.adjustments) {
	    		if (adj.getTotalLmef() != null) {
		    		result = result.add(adj.getTotalLmef());
	    		}
	    	}
		}
    	
        return result;
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
	public void initOutput(String [] line) throws ParseException, DatatypeConfigurationException {
		
		String rerun_gst_name = line[0];
		String rerun_gst_rate = line[1];
		String rerun_gst_a_inc_gmef = line[2];
		String rerun_gst_a_inc_lmee = line[3];
		String rerun_gst_a_inc_lmef = line[4];
		String rerun_gst_a_inc_nmea = line[5];
		String rerun_gst_v_inc_gmee = line[6];
		String rerun_gst_v_inc_nmea = line[7];
		String rerun_gst_inc_gmee = line[8];
		String rerun_gst_inc_gmef = line[9];
		String rerun_gst_inc_lmee = line[10];
		String rerun_gst_inc_lmef = line[11];
		String rerun_gst_inc_nmea = line[12];
		String rerun_gst_total_inc_gmee = line[13];
		String rerun_gst_total_inc_gmef = line[14];
		String rerun_gst_total_inc_lmee = line[15];
		String rerun_gst_total_inc_lmef = line[16];
		String rerun_gst_total_inc_nmea = line[17];
		String rerun_gst_taxable = line[18];
		
		this.name = rerun_gst_name;
		this.gstRate = rerun_gst_rate.length() > 0? new BigDecimal(rerun_gst_rate): null;
		this.opGstIncGmef = rerun_gst_a_inc_gmef.length() > 0? new BigDecimal(rerun_gst_a_inc_gmef): null;
		this.opGstIncLmee = rerun_gst_a_inc_lmee.length() > 0? new BigDecimal(rerun_gst_a_inc_lmee): null;
		this.opGstIncLmef = rerun_gst_a_inc_lmef.length() > 0? new BigDecimal(rerun_gst_a_inc_lmef): null;
		this.opGstIncNmea = rerun_gst_a_inc_nmea.length() > 0? new BigDecimal(rerun_gst_a_inc_nmea): null;
		this.ipGstIncGmee = rerun_gst_v_inc_gmee.length() > 0? new BigDecimal(rerun_gst_v_inc_gmee): null;
		this.ipGstIncNmea = rerun_gst_v_inc_nmea.length() > 0? new BigDecimal(rerun_gst_v_inc_nmea): null;
		this.incGmee = rerun_gst_inc_gmee.length() > 0? new BigDecimal(rerun_gst_inc_gmee): null;
		this.incGmef = rerun_gst_inc_gmef.length() > 0? new BigDecimal(rerun_gst_inc_gmef): null;
		this.incLmee = rerun_gst_inc_lmee.length() > 0? new BigDecimal(rerun_gst_inc_lmee): null;
		this.incLmef = rerun_gst_inc_lmef.length() > 0? new BigDecimal(rerun_gst_inc_lmef): null;
		this.incNmea = rerun_gst_inc_nmea.length() > 0? new BigDecimal(rerun_gst_inc_nmea): null;
		this.totalIncGmee = rerun_gst_total_inc_gmee.length() > 0? new BigDecimal(rerun_gst_total_inc_gmee): null;
		this.totalIncGmef = rerun_gst_total_inc_gmef.length() > 0? new BigDecimal(rerun_gst_total_inc_gmef): null;
		this.totalIncLmee = rerun_gst_total_inc_lmee.length() > 0? new BigDecimal(rerun_gst_total_inc_lmee): null;
		this.totalIncLmef = rerun_gst_total_inc_lmef.length() > 0? new BigDecimal(rerun_gst_total_inc_lmef): null;
		this.totalIncNmea = rerun_gst_total_inc_nmea.length() > 0? new BigDecimal(rerun_gst_total_inc_nmea): null;
		this.taxable = rerun_gst_taxable.equals("1");
	}
	
	public String PFCheck(Rerun in) {

		String result = null;

		if ((this.opGstIncGmef == null && in.opGstIncGmef != null) || (this.opGstIncGmef != null && in.opGstIncGmef == null)) result = "opGstIncGmef missing value";
		if ((this.opGstIncLmee == null && in.opGstIncLmee != null) || (this.opGstIncLmee != null && in.opGstIncLmee == null)) result = "opGstIncLmee missing value";
		if ((this.opGstIncLmef == null && in.opGstIncLmef != null) || (this.opGstIncLmef != null && in.opGstIncLmef == null)) result = "opGstIncLmef missing value";
		if ((this.opGstIncNmea == null && in.opGstIncNmea != null) || (this.opGstIncNmea != null && in.opGstIncNmea == null)) result = "opGstIncNmea missing value";
		if ((this.ipGstIncGmee == null && in.ipGstIncGmee != null) || (this.ipGstIncGmee != null && in.ipGstIncGmee == null)) result = "ipGstIncGmee missing value";
		if ((this.ipGstIncNmea == null && in.ipGstIncNmea != null) || (this.ipGstIncNmea != null && in.ipGstIncNmea == null)) result = "ipGstIncNmea missing value";
		if ((this.incGmee == null && in.incGmee != null) || (this.incGmee != null && in.incGmee == null)) result = "incGmee missing value";
		if ((this.incGmef == null && in.incGmef != null) || (this.incGmef != null && in.incGmef == null)) result = "incGmef missing value";
		if ((this.incLmee == null && in.incLmee != null) || (this.incLmee != null && in.incLmee == null)) result = "incLmee missing value";
		if ((this.incLmef == null && in.incLmef != null) || (this.incLmef != null && in.incLmef == null)) result = "incLmef missing value";
		if ((this.incNmea == null && in.incNmea != null) || (this.incNmea != null && in.incNmea == null)) result = "incNmea missing value";
		if ((this.totalIncGmee == null && in.totalIncGmee != null) || (this.totalIncGmee != null && in.totalIncGmee == null)) result = "totalIncGmee missing value";
		if ((this.totalIncGmef == null && in.totalIncGmef != null) || (this.totalIncGmef != null && in.totalIncGmef == null)) result = "totalIncGmef missing value";
		if ((this.totalIncLmee == null && in.totalIncLmee != null) || (this.totalIncLmee != null && in.totalIncLmee == null)) result = "totalIncLmee missing value";
		if ((this.totalIncLmef == null && in.totalIncLmef != null) || (this.totalIncLmef != null && in.totalIncLmef == null)) result = "totalIncLmef missing value";
		if ((this.totalIncNmea == null && in.totalIncNmea != null) || (this.totalIncNmea != null && in.totalIncNmea == null)) result = "totalIncNmea missing value";

		return result;
	}
	
	public String RSCheck(Rerun in) {

		String result = null;

/*		if ((this.opGstIncGmef == null && in.opGstIncGmef != null) || (this.opGstIncGmef != null && in.opGstIncGmef == null)) result = "opGstIncGmef missing value";
		if ((this.opGstIncLmee == null && in.opGstIncLmee != null) || (this.opGstIncLmee != null && in.opGstIncLmee == null)) result = "opGstIncLmee missing value";
		if ((this.opGstIncLmef == null && in.opGstIncLmef != null) || (this.opGstIncLmef != null && in.opGstIncLmef == null)) result = "opGstIncLmef missing value";
		if ((this.opGstIncNmea == null && in.opGstIncNmea != null) || (this.opGstIncNmea != null && in.opGstIncNmea == null)) result = "opGstIncNmea missing value";
		if ((this.ipGstIncGmee == null && in.ipGstIncGmee != null) || (this.ipGstIncGmee != null && in.ipGstIncGmee == null)) result = "ipGstIncGmee missing value";
		if ((this.ipGstIncNmea == null && in.ipGstIncNmea != null) || (this.ipGstIncNmea != null && in.ipGstIncNmea == null)) result = "ipGstIncNmea missing value";
		if ((this.incGmee == null && in.incGmee != null) || (this.incGmee != null && in.incGmee == null)) result = "incGmee missing value";
		if ((this.incGmef == null && in.incGmef != null) || (this.incGmef != null && in.incGmef == null)) result = "incGmef missing value";
		if ((this.incLmee == null && in.incLmee != null) || (this.incLmee != null && in.incLmee == null)) result = "incLmee missing value";
		if ((this.incLmef == null && in.incLmef != null) || (this.incLmef != null && in.incLmef == null)) result = "incLmef missing value";
		if ((this.incNmea == null && in.incNmea != null) || (this.incNmea != null && in.incNmea == null)) result = "incNmea missing value";
		if ((this.totalIncGmee == null && in.totalIncGmee != null) || (this.totalIncGmee != null && in.totalIncGmee == null)) result = "totalIncGmee missing value";
		if ((this.totalIncGmef == null && in.totalIncGmef != null) || (this.totalIncGmef != null && in.totalIncGmef == null)) result = "totalIncGmef missing value";
		if ((this.totalIncLmee == null && in.totalIncLmee != null) || (this.totalIncLmee != null && in.totalIncLmee == null)) result = "totalIncLmee missing value";
		if ((this.totalIncLmef == null && in.totalIncLmef != null) || (this.totalIncLmef != null && in.totalIncLmef == null)) result = "totalIncLmef missing value";
		if ((this.totalIncNmea == null && in.totalIncNmea != null) || (this.totalIncNmea != null && in.totalIncNmea == null)) result = "totalIncNmea missing value";
*/
		return result;
	}
	
	public String equal(Rerun in) {

		String result = null;

		if (this.opGstIncGmef != null && in.opGstIncGmef != null) if (this.opGstIncGmef.compareTo(in.opGstIncGmef) != 0) result = "opGstIncGmef value mismatch";
		if (this.opGstIncLmee != null && in.opGstIncLmee != null) if (this.opGstIncLmee.compareTo(in.opGstIncLmee) != 0) result = "opGstIncLmee value mismatch";
		if (this.opGstIncLmef != null && in.opGstIncLmef != null) if (this.opGstIncLmef.compareTo(in.opGstIncLmef) != 0) result = "opGstIncLmef value mismatch";
		if (this.opGstIncNmea != null && in.opGstIncNmea != null) if (this.opGstIncNmea.compareTo(in.opGstIncNmea) != 0) result = "opGstIncNmea value mismatch";
		if (this.ipGstIncGmee != null && in.ipGstIncGmee != null) if (this.ipGstIncGmee.compareTo(in.ipGstIncGmee) != 0) result = "ipGstIncGmee value mismatch";
		if (this.ipGstIncNmea != null && in.ipGstIncNmea != null) if (this.ipGstIncNmea.compareTo(in.ipGstIncNmea) != 0) result = "ipGstIncNmea value mismatch";
		if (this.incGmee != null && in.incGmee != null) if (this.incGmee.compareTo(in.incGmee) != 0) result = "incGmee value mismatch";
		if (this.incGmef != null && in.incGmef != null) if (this.incGmef.compareTo(in.incGmef) != 0) result = "incGmef value mismatch";
		if (this.incLmee != null && in.incLmee != null) if (this.incLmee.compareTo(in.incLmee) != 0) result = "incLmee value mismatch";
		if (this.incLmef != null && in.incLmef != null) if (this.incLmef.compareTo(in.incLmef) != 0) result = "incLmef value mismatch";
		if (this.incNmea != null && in.incNmea != null) if (this.incNmea.compareTo(in.incNmea) != 0) result = "incNmea value mismatch";
		if (this.totalIncGmee != null && in.totalIncGmee != null) if (this.totalIncGmee.compareTo(in.totalIncGmee) != 0) result = "totalIncGmee value mismatch";
		if (this.totalIncGmef != null && in.totalIncGmef != null) if (this.totalIncGmef.compareTo(in.totalIncGmef) != 0) result = "totalIncGmef value mismatch";
		if (this.totalIncLmee != null && in.totalIncLmee != null) if (this.totalIncLmee.compareTo(in.totalIncLmee) != 0) result = "totalIncLmee value mismatch";
		if (this.totalIncLmef != null && in.totalIncLmef != null) if (this.totalIncLmef.compareTo(in.totalIncLmef) != 0) result = "totalIncLmef value mismatch";
		if (this.totalIncNmea != null && in.totalIncNmea != null) if (this.totalIncNmea.compareTo(in.totalIncNmea) != 0) result = "totalIncNmea value mismatch";

		return result;
	}
	
	public String toInputString() {
		
		String result = this.name + "," +
				(this.gstRate != null? this.gstRate.toString(): "") + "," +
				(this.opGstIncGmef != null? this.opGstIncGmef.toString(): "") + "," +
				(this.opGstIncLmee != null? this.opGstIncLmee.toString(): "") + "," +
				(this.opGstIncLmef != null? this.opGstIncLmef.toString(): "") + "," +
				(this.opGstIncNmea != null? this.opGstIncNmea.toString(): "") + "," +
				(this.ipGstIncGmee != null? this.ipGstIncGmee.toString(): "") + "," +
				(this.ipGstIncNmea != null? this.ipGstIncNmea.toString(): "") + "," +
				(this.incGmee != null? this.incGmee.toString(): "") + "," +
				(this.incGmef != null? this.incGmef.toString(): "") + "," +
				(this.incLmee != null? this.incLmee.toString(): "") + "," +
				(this.incLmef != null? this.incLmef.toString(): "") + "," +
				(this.incNmea != null? this.incNmea.toString(): "") + "," +
				(this.totalIncGmee != null? this.totalIncGmee.toString(): "") + "," +
				(this.totalIncGmef != null? this.totalIncGmef.toString(): "") + "," +
				(this.totalIncLmee != null? this.totalIncLmee.toString(): "") + "," +
				(this.totalIncLmef != null? this.totalIncLmef.toString(): "") + "," +
				(this.totalIncNmea != null? this.totalIncNmea.toString(): "") + "," +
				(this.taxable == true? "1": "0");
		
		return result;
	}

	public String toOutputString() {
		
		String result = this.name + "," +
				(this.gstRate != null? this.gstRate.toString(): "") + "," +
				(this.opGstIncGmef != null? this.opGstIncGmef.toString(): "") + "," +
				(this.opGstIncLmee != null? this.opGstIncLmee.toString(): "") + "," +
				(this.opGstIncLmef != null? this.opGstIncLmef.toString(): "") + "," +
				(this.opGstIncNmea != null? this.opGstIncNmea.toString(): "") + "," +
				(this.ipGstIncGmee != null? this.ipGstIncGmee.toString(): "") + "," +
				(this.ipGstIncNmea != null? this.ipGstIncNmea.toString(): "") + "," +
				(this.incGmee != null? this.incGmee.toString(): "") + "," +
				(this.incGmef != null? this.incGmef.toString(): "") + "," +
				(this.incLmee != null? this.incLmee.toString(): "") + "," +
				(this.incLmef != null? this.incLmef.toString(): "") + "," +
				(this.incNmea != null? this.incNmea.toString(): "") + "," +
				(this.totalIncGmee != null? this.totalIncGmee.toString(): "") + "," +
				(this.totalIncGmef != null? this.totalIncGmef.toString(): "") + "," +
				(this.totalIncLmee != null? this.totalIncLmee.toString(): "") + "," +
				(this.totalIncLmef != null? this.totalIncLmef.toString(): "") + "," +
				(this.totalIncNmea != null? this.totalIncNmea.toString(): "") + "," +
				(this.taxable == true? "1": "0");
		
		return result;
	}

	public static String getInputHeader() {
		String header = 
			"name," +
			"gstRate," +
			"opGstIncGmef," +
			"opGstIncLmee," +
			"opGstIncLmef," +
			"opGstIncNmea," +
			"ipGstIncGmee," +
			"ipGstIncNmea," +
			"incGmee," +
			"incGmef," +
			"incLmee," +
			"incLmef," +
			"incNmea," +
			"totalIncGmee," +
			"totalIncGmef," +
			"totalIncLmee," +
			"totalIncLmef," +
			"totalIncNmea," +
			"taxable";

		return header;
	}
	
	public static String getOutputHeader() {
		String header = 
			"name," +
			"gstRate," +
			"opGstIncGmef," +
			"opGstIncLmee," +
			"opGstIncLmef," +
			"opGstIncNmea," +
			"ipGstIncGmee," +
			"ipGstIncNmea," +
			"incGmee," +
			"incGmef," +
			"incLmee," +
			"incLmef," +
			"incNmea," +
			"totalIncGmee," +
			"totalIncGmef," +
			"totalIncLmee," +
			"totalIncLmef," +
			"totalIncNmea," +
			"taxable";

		return header;
	}
}
