/**
 *
 */
package com.emc.settlement.common;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emc.settlement.model.backend.pojo.fileupload.VestingContract;


/**
 * @author DWTN1561
 *
 */
@Component
public class VestingContractImpl {


	protected static final Logger logger = Logger.getLogger(VestingContractImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

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

	public boolean hasExistingPeriodsForSacSd(VestingContract item,boolean validBool) throws SQLException
	{
		boolean isValid = false;
		boolean isMatch = false;
		String settlement_DATE_FORMAT="DD-MON-YYYY";
		try{

			// retrieve ID from NEM_SETTLEMENT_RUNS table
			String sqlSettRunCommand = "SELECT COUNT(*) COUNT FROM NEM_VESTING_CONTRACTS " +
					"WHERE SETTLEMENT_DATE = TO_DATE('" + item.settlementDate +
					"','" + settlement_DATE_FORMAT + "') " +
					" AND SAC_SOLD_ID = '" + item.settlementAccount + "' ";
			String id = null;
			int vcTotalPeriodsForSacSd = 0;

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlSettRunCommand, new Object[] {});
			for (Map row : list) {
				vcTotalPeriodsForSacSd = (Integer)row.get("COUNT");
			}


			isValid = vcTotalPeriodsForSacSd > 0;
			isMatch = isValid == validBool;

			if (! isMatch) {
				logger.log(Priority.INFO,"[EMC] VestingContract.hasExistingPeriodsForSacSd() -- vcTotalPeriodsForSacSd=" +
						vcTotalPeriodsForSacSd + ", result=" + isMatch + ", throws Exception");

				// throw Java.Exception("VestingContract record error:" + line)
			}

		}catch(Exception e)
		{
			logger.error("Exception "+e.getMessage());
		}
		return isMatch;
	}

	public void initialize(VestingContract item)
	{
		// reset all attributes
		item.isFirstVcForSd = false;
		item.checkPriceInDatabase = false;
		item.contractType = null;
		item.ebt_event_id = null;
		item.eveId = null;
		item.validation_type = 0;
		item.vcExistingContractPrice = null;
		item.vcExistingSacForSd = null;
		item.vcTotalPeriodsForSacSd = 0;
		this.initializeDbItem(item);

	}

	public void initializeDbItem(VestingContract item)
	{
		// reset the below attributes to prepare to read in a new record
		item.externalId = null;
		item.name = null;
		item.period = 0;
		item.price = null;
		item.quantity = null;
		item.settlementAccount = null;
		item.settlementDate = null;
		item.sacPurchaseId = null;
		item.sacSoldId = null;

	}

	public void initializeForNewSd(VestingContract item)
	{
		// reset the below attributes when settlement date changes
		item.isFirstVcForSd = false;
		item.checkPriceInDatabase = false;
		item.contractType = null;
		item.vcExistingContractPrice = new ArrayList<Double>();
		item.vcExistingSacForSd = new ArrayList<String>();
		item.vcTotalPeriodsForSacSd = 0;
		this.initializeDbItem(item);
	}

	public boolean isSameVcPriceForSdPeriod(VestingContract item , String eveId) throws SQLException
	{
		boolean isValid = false;
		String settlement_DATE_FORMAT = "DD-MON-YYYY";
		try {
			// initialise the contract price array for specified settlement date with prices
			// from database
			// if not initialised yet
			int i = 0;

			if (item.vcExistingContractPrice.size() <= 0 && !item.checkPriceInDatabase) {
				// retrieve ID from NEM_SETTLEMENT_RUNS table which will be used to fetch prices
				// retrieve list of existing external_ids used to prevent duplicate entry for
				// same (Sett-Account + Sett-Date)
				String sqlSettRunCommand = "SELECT Id, Sac_sold_id FROM nem_vesting_contracts "
						+ "WHERE settlement_date = TO_DATE('" + item.settlementDate + "','" + settlement_DATE_FORMAT
						+ "') AND eve_id = '" + item.eveId + "'";
				List<String> vcIds = new ArrayList<String>();
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlSettRunCommand, new Object[] {});
				for (Map row : list) {
					vcIds.add((String)row.get("Id"));
					item.vcExistingSacForSd.add((String)row.get("Sac_sold_id"));
				}

				// change flag to indicate we have gone into database to look for existing
				// vesting contract prices
				item.checkPriceInDatabase = true;

				if (vcIds != null && vcIds.size() > 0) {
					item.isFirstVcForSd = false;

					// indicate database have existing records for specified settlement date
					// build WHERE condition to retrieve rows for all vcIds
					String vcIdCondition = "";

					if (vcIds.size() > 0) {
						vcIdCondition = " WHERE ";

						for (int j = 0; j <= vcIds.size(); j++) {
							vcIdCondition = vcIdCondition + "vc_id = '" + vcIds.get(j) + "' OR";
						}

						// strip the last ' OR' from the where condition
						vcIdCondition = vcIdCondition.substring(0, vcIdCondition.length() - 3);
					}

					// retrieve vc param prices to initialize price array
					sqlSettRunCommand = "SELECT settlement_period, price FROM nem_vesting_contract_params "
							+ vcIdCondition + " ORDER BY settlement_period";
					i = 0;

					List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlSettRunCommand, new Object[] {});
					for (Map row : list1) {
						item.vcExistingContractPrice.add((Integer)row.get("settlement_period") - 1, (Double)row.get("price"));
						i = i + 1;
					}

					if (i == 0) {
						// IMPORTANT NOTE --- Could be a DATABASE INTEGRITY ISSUE here ?!
						// we have records in vesting_contracts table but no corresponding records in
						// vesting_contract_params
						logger.log(Priority.INFO, "[EMC] VestingContract.isSameVcPriceForSdPeriod() -- "
								+ "vesting_contracts exist with zero associated vesting_contracts_param records");
					}
				} else {
					// no existing records in database, initialise array with item price to allow
					// chacking for subsequent records
					item.isFirstVcForSd = true;
				}

				// if vc_id.length > 0
			}

			// length(vcExistingContractPrice) <= 0
			// validation rule: contract price must be same across same settlement date and
			// period
			// always true if this is the very first contract input into system
			isValid = item.isFirstVcForSd || (item.price == item.vcExistingContractPrice.get(item.period - 1));

			if (!isValid) {
				item.message = "period " + item.period + ", price1: " + item.price + ", price2: "
						+ item.vcExistingContractPrice.get(item.period - 1);
			}

		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}

		return isValid;
	}

	public int isValidNumOfPeriods(VestingContract item, List<Integer> periods, int maxPeriod)
	{
		// validation #18 - each Settlement account (Sac) has only @param maxPeriod
		// unique periods for
		// each settlement date (Sd)
		// @param periods - array containing the periods
		// @param maxPeriod - valid number of periods (48 currently)
		// @return Boolean
		// logic: sort csvPeriod array and check that sequence is in asc order from 0 -
		// @param maxPeriod
		// array value (period) does not match array index implies duplicate/missing
		// period
		boolean isValid = true;
		int isValidNo = 0;

		// [ITSM-12670]
		if (periods.size() != maxPeriod) {
			logger.log(Priority.INFO, "[EMC] VestingContract.isValidNumOfPeriods() -- fail validation #18,"
					+ " periods.length=" + periods.size());

			item.message = " has " + periods.size() + " periods not equal to " + maxPeriod;

			// isValid = false
			isValidNo = 1;
		} else {
			Collections.sort(periods);

			int j = 0;

			while (j < periods.size()) {
				if (periods.get(j) == (j + 1)) {
					j = j + 1;
				} else if (periods.get(j) < (j + 1)) {
					// isValid = false
					item.message = " has duplicate period " + periods.get(j) + ".";

					logger.log(Priority.INFO,
							"[EMC] VestingContract.isValidNumOfPeriods() -- fail validation # 18: periods[j]="
									+ periods.get(j) + ", j+1=" + (j + 1));

					isValidNo = periods.get(j);

					break;
				} else if (periods.get(j) > (j + 1)) {
					// isValid = false
					isValidNo = 1;
					item.message = " has missing period " + (j + 1) + ".";

					logger.log(Priority.INFO,
							"[EMC] VestingContract.isValidNumOfPeriods() -- fail validation # 18: periods[j]="
									+ periods.get(j) + ", j+1=" + (j + 1));

					break;
				}
			}
		}

		// return isValid
		return isValidNo;

	}


}
