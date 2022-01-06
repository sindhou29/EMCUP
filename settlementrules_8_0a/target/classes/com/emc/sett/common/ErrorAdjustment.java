package com.emc.sett.common;

import java.math.BigDecimal;
import java.sql.Date;

public class ErrorAdjustment {

	public BigDecimal gmee = BigDecimal.ZERO;
	public BigDecimal gmef = BigDecimal.ZERO;
	public BigDecimal lmee = BigDecimal.ZERO;
	public BigDecimal lmef = BigDecimal.ZERO;
	public BigDecimal nmea = BigDecimal.ZERO;
	public BigDecimal gstRate = null;
	public BigDecimal aGmef = BigDecimal.ZERO;
	public BigDecimal aLmee = BigDecimal.ZERO;
	public BigDecimal aLmef = BigDecimal.ZERO;
	public BigDecimal aNmea = BigDecimal.ZERO;
	public BigDecimal vGmee = BigDecimal.ZERO;
	public BigDecimal vNmea = BigDecimal.ZERO;
	
	public Date settDate = null;
	public String runType = null;
	public String taxable = null;
}
