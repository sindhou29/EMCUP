/**
 *
 */
package com.emc.settlement.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emc.settlement.model.backend.pojo.fileupload.ForwardSalesContract;


/**
 * @author DWTN1561
 *
 */

@Component
public class ForwardSalesContractImpl {

	/**
	 *
	 */
	public ForwardSalesContractImpl() {
		// TODO Auto-generated constructor stub
	}

	protected static final Logger logger = Logger.getLogger(ForwardSalesContractImpl.class);

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	String message = "";

	public int convertMonth(String item)
	{
		int result = 0;

		if (item.toLowerCase().equals("jan")) {
			result = 1;
		}
		else if (item.toLowerCase().equals("feb")) {
			result = 2;
		}
		else if (item.toLowerCase().equals("mar")) {
			result = 3;
		}
		else if (item.toLowerCase().equals("apr")) {
			result = 4;
		}
		else if (item.toLowerCase().equals("may")) {
			result = 5;
		}
		else if (item.toLowerCase().equals("jun")) {
			result = 6;
		}
		else if (item.toLowerCase().equals("jul")) {
			result = 7;
		}
		else if (item.toLowerCase().equals("aug")) {
			result = 8;
		}
		else if (item.toLowerCase().equals("sep")) {
			result = 9;
		}
		else if (item.toLowerCase().equals("oct")) {
			result = 10;
		}
		else if (item.toLowerCase().equals("nov")) {
			result = 11;
		}
		else if (item.toLowerCase().equals("dec")) {
			result = 12;
		}

		return result;

	}

	public int hasCoveredAllDatesOfQuarter(List tradingDates, String strFirstSettDate)
	{
		//Validation #24 Each FSC contract reference must cover all trading dates and periods of the applicable quarter in the file.

		//get all settlement dates from the current quarter
		int noOfMissingTradingDates=0;
		try{

			String sqlQuarterDates = "select settlement_date From NEM_SETTLEMENT_CALENDAR " +
					" where settlement_date >= TO_DATE(?, 'dd MON yyyy hh24:mi:ss') and settlement_date <= TO_DATE(?, 'dd MON yyyy hh24:mi:ss') ";

			List quarterDatesSet = new ArrayList();
			Date settDate;

			//params[0] = UtilityFunctions.getSysParamTime(paramName : "FSC_EFF_START_DATE").format(mask : "dd-MMM-yyyy");
			//params[1] = UtilityFunctions.getSysParamTime(paramName : "FSC_EFF_END_DATE").format(mask : "dd-MMM-yyyy");
			//logger.log(Priority.INFO,""+params[0]);
			//logger.log(Priority.INFO,""+params[1]);

			//get quarter start date and end date
			Date firstSettlementDate = utilityFunctions.stringToDate( strFirstSettDate,  "dd-MMM-yyyy");
			int quarter = ((firstSettlementDate.getMonth() - 1 ) / 3) + 1;

			String qtrStartDate = null;
			String qtrEndDate = null;

			if(quarter == 1){
				qtrStartDate = "01-Jan-"+firstSettlementDate.getYear();
				qtrEndDate = "31-Mar-"+firstSettlementDate.getYear();
			}else if(quarter == 2){
				qtrStartDate = "01-Apr-"+firstSettlementDate.getYear();
				qtrEndDate = "30-Jun-"+firstSettlementDate.getYear();
			}else if(quarter == 3){
				qtrStartDate = "01-Jul-"+firstSettlementDate.getYear();
				qtrEndDate = "30-Sep-"+firstSettlementDate.getYear();
			}else if(quarter == 4){
				qtrStartDate = "01-Nov-"+firstSettlementDate.getYear();
				qtrEndDate = "31-Dec-"+firstSettlementDate.getYear();
			}


			logger.log(Priority.INFO,""+qtrStartDate);
			logger.log(Priority.INFO,""+qtrEndDate);

			Object[] params = new Object[2];
			params[0] =  qtrStartDate;
			params[1] =  qtrEndDate;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlQuarterDates, params);
			for (Map row : list) {
				settDate = (Date) row.get("settlement_date");
				quarterDatesSet.add(settDate);
			}

			logger.log(Priority.INFO,"**Total Number of Days of current Quarter --> "+quarterDatesSet.size());

			noOfMissingTradingDates = quarterDatesSet.size() - tradingDates.size();
			if(noOfMissingTradingDates != 0){

				message = "Each FSC contract reference must cover all trading dates and periods of the applicable quarter in the file. "+
						" FSC contract reference has "+noOfMissingTradingDates+" missing trading dates from the applicable quarter. ";

				logger.log(Priority.INFO,message);
			}
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}

		return noOfMissingTradingDates;
	}

	public int isValidNumOfPeriods(List<Integer> periods, int maxPeriod)
	{
		// validation #19 - each Settlement account (Sac) has only @param maxPeriod unique periods for 
		//			each settlement date (Sd)
		//@param periods - array containing the periods
		//@param maxPeriod - valid number of periods (48 currently)
		//@return Boolean
		//logic: sort csvPeriod array and check that sequence is in asc order from 0 - @param maxPeriod
		//			array value (period) does not match array index implies duplicate/missing period
		boolean isValid = true;
		int isValidNo = 0;

		//[ITSM-12670]
		if (periods.size() != maxPeriod) {
			logger.log(Priority.INFO,"[EMC] ForwardSalesContract.isValidNumOfPeriods() -- fail validation #19," +
					" periods.length=" + periods.size());

			message = " has " + periods.size() + " periods not equal to " + maxPeriod;

			// isValid = false
			isValidNo = 1;
		}
		else {
			Collections.sort(periods);

			int j = 0;

			while (j < periods.size()) {
				if (periods.get(j) == (j + 1)) {
					j = j + 1;
				}
				else if (periods.get(j) < (j + 1)) {
					// isValid = false
					message = " has duplicate period " + periods.get(j) + ".";

					logger.log(Priority.INFO,"[EMC] ForwardSalesContract.isValidNumOfPeriods() -- fail validation # 19: periods[j]=" +
							periods.get(j) + ", j+1=" + (j + 1));

					isValidNo = periods.get(j);

					break;
				}
				else if (periods.get(j) > (j + 1)) {
					// isValid = false
					isValidNo = 1;
					message = " has missing period " + (j + 1) + ".";

					logger.log(Priority.INFO,"[EMC] ForwardSalesContract.isValidNumOfPeriods() -- fail validation # 19: periods[j]=" +
							periods.get(j) + ", j+1=" + (j + 1));

					break;
				}
			}
		}

		//return isValid
		return isValidNo;

	}

	public void initializeDbItem(ForwardSalesContract fsc)
	{
		// reset the below attributes to prepare to read in a new record
		fsc.externalId = null;
		fsc.name = null;
		fsc.period = 0;
		fsc.price = null;
		fsc.quantity = null;
		fsc.settlementAccount = null;
		fsc.settlementDate = null;
		fsc.sacPurchaseId = null;
		fsc.sacSoldId = null;
	}

}
