package com.emc.settlement.backend.runrelated;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.emc.settlement.common.PavPackageImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.AccountInterface;

@Component
public class AccountInterfaceImpl {

	
	
	protected static final Logger logger = Logger.getLogger(AccountInterfaceImpl.class);
	
    @Autowired
	private JdbcTemplate jdbcTemplate;
    @Autowired
    PavPackageImpl pavPackageImpl;
    @Autowired
    UtilityFunctions utilityFunctions;
    
	public Date calculateCrDbDueDate(String strId, String sacId, String sacVersion) throws Exception
	{
		String sqlCommand = "SELECT settlement_date FROM nem_settlement_runs WHERE id = '" + strId + "'";
		String settDate = null;

		Date nextBusinessDay = null;
		try {
			settDate = jdbcTemplate.queryForObject(sqlCommand, new Object[] {}, String.class);

			if (settDate == null) {
				throw new Exception("Calculate Due Date error : Settlement Run ID " + strId + " does not exist.");
			}

			// -- determine whether the owed amount is a credit or debit
			// -- this is used to determine what the payment date should be
			sqlCommand = "SELECT DECODE(SIGN(sum(calculation_total + "
					+ "DECODE(SIGN(calculation_total), -1, -gst_amount, gst_amount))), "
					+ " -1, 'DEBIT', 'CREDIT') debit_or_credit" + " FROM nem_accounting_interfaces WHERE str_id = '"
					+ strId + "'" + " AND sac_id = '" + sacId + "' AND sac_version = '" + sacVersion + "'";
			String debitOrCredit = null;

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list) {
				debitOrCredit = (String) row.get("debit_or_credit");
				break;
			}

			sqlCommand = "SELECT number_value FROM aps_system_parameters WHERE name = '" + debitOrCredit
					+ "' || '_DUE_DATE_DAYS' ";
			int daysDue = 0;

			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list1) {
				if ((String) row.get("number_value") == null) {
					daysDue = 0;
				} else {
					// String(row[1]) returns a numeric string e.g. "20.000000"
					daysDue = Integer.parseInt((String) row.get("number_value"));
				}
				break;
			}

			if (daysDue <= 0) {
				throw new Exception("Calculate Due Date error: Debit/Credit days is not define for name "
						+ debitOrCredit + "_DUE_DATE_DAYS");
			}

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
			Date setDate = null;
			setDate = formatter.parse(settDate);

			LocalDateTime localDateTime = setDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			localDateTime = localDateTime.plusDays(daysDue);
			Date dueDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

			sqlCommand = "SELECT settlement_date FROM nem_settlement_calendar WHERE settlement_date >= TO_DATE('"
					+ formatter.format(dueDate) + "', 'DD-MON-YY') AND day_type = 'B'" + " ORDER BY settlement_date ";

			List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list2) {
				nextBusinessDay = (Date)row.get("settlement_date");
				break;
			}
		} catch (SQLException e) {
			logger.error("Exception "+e.getMessage());
		}		

		return nextBusinessDay;
		
	}
	
	public String calculateDueDate(String strId, String sacId, String sacVersion) throws Exception
	{
		String sqlCommand = "SELECT settlement_date FROM nem_settlement_runs WHERE id = '" + strId + "'";
		String settDate = null;
		String nextBusinessDay = null;	
		
		try{
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list) {
				settDate = (String)row.get("settlement_date");
				break;
			}
		

		if (settDate == null) {
				throw new Exception( "Calculate Due Date error : Settlement Run ID " + strId + " does not exist.");

		}

		//  -- determine whether the owed amount is a credit or debit
		//  -- this is used to determine what the payment date should be
		sqlCommand = "SELECT DECODE(SIGN(sum(calculation_total + " + 
		             "DECODE(SIGN(calculation_total), -1, -gst_amount, gst_amount))), " + 
		             " -1, 'DEBIT', 'CREDIT') debit_or_credit" + 
		             " FROM nem_accounting_interfaces WHERE str_id = '" + strId + "'" + 
		             " AND sac_id = '" + sacId + "' AND sac_version = '" + sacVersion + "'";
		String debitOrCredit = null;

			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list1) {
				debitOrCredit = (String)row.get("debit_or_credit");
				break;
			}


		sqlCommand = "SELECT number_value FROM aps_system_parameters WHERE name = '" + debitOrCredit + 
		             "' || '_DUE_DATE_DAYS' ";
		int daysDue = 0;
		
		List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
		for (Map row : list2) {
			    if (((String)row.get("number_value")) == null) {
			        daysDue = 0;
			    }
			    else {
			        // String(row[1]) returns a numeric string e.g. "20.000000"
			        daysDue = Integer.parseInt((String)row.get("number_value"));
			    }				
			    break;
			}


		if (daysDue <= 0) {
				throw new Exception( "Calculate Due Date error: Debit/Credit days is not define for name " + 
				                debitOrCredit + "_DUE_DATE_DAYS");

		}
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
        Date setDate=null;
			setDate = formatter.parse(settDate);

        LocalDateTime localDateTime = setDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        localDateTime = localDateTime.plusDays(daysDue);
		Date dueDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

		sqlCommand = "SELECT TO_CHAR(settlement_date, 'DDMONYYYY') settlement_date FROM nem_settlement_calendar WHERE settlement_date >= TO_DATE('" + 
				formatter.format(dueDate) + "', 'DD-MON-YY') AND day_type = 'B'" + 
		             " ORDER BY settlement_date ";
		List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
		for (Map row : list3) {
				nextBusinessDay = (String)row.get("settlement_date");
				break;
			}
		} catch (SQLException e) {
			logger.error("Exception "+e.getMessage());
		}
		return nextBusinessDay;
		
	}
	
	public void doSettlementFinalRun(String sqlCommand, Object[] inParam, AccountInterface accountInterface) throws Exception
	{
		/*
		  Fetch the settlement run result to populate accounting interface table

		  @param runID  (when fetching from nem_settlement_results)
		  OR
		  @param srtVersion, runId (when fetching from nem_non_period_charges table)

		  Called from process/activity DoAccountingInterface*/
//		String[] params;
//		Any[] params1;
		int rows = 0;
		try{
			if (sqlCommand.length() <= 0) {
				throw new Exception("SQL Query is not defined.");
			}

			// stdVersion as String = getCurrVersionPkt(PavPackage, pktType : "STANDING",
			// versionDate : settDate, eventId : null)
			String standingVer = pavPackageImpl.getStandingVersion(accountInterface.settlementDate); // TODO MURALI
			String sqlDebCre = "SELECT DECODE(debit_credit,'D', -1, 1) result_sign "
					+ " FROM NEM_SETTLEMENT_RESULT_TYPES st " + " WHERE NAME = ? AND VERSION = ? ";
			String sqlCalCode = "SELECT name FROM nem_settlement_result_types WHERE id = ? AND version = ? "
					+ " UNION ALL "
					+ " SELECT solomon_code FROM nem_non_period_charges npc, nem_non_period_charge_codes ncc "
					+ " WHERE npc.ID = ? AND npc.ncc_id = ncc.ID ";
			String sqlGstCode = "SELECT gst.NAME " + "FROM nem_non_period_charges npc, nem_gst_codes gst "
					+ " WHERE npc.ID = ? AND gst.ID = npc.gst_id AND gst.VERSION = ? " + " UNION ALL "
					+ "SELECT gst.NAME FROM nem_settlement_result_types srt, nem_gst_codes gst "
					+ " WHERE srt.ID = ? AND srt.version = ? "
					+ " AND gst.ID = srt.gst_id AND gst.VERSION = srt.gst_version ";
			String sqlNpcDesc = "SELECT npc.NAME FROM nem_non_period_charges npc " + "WHERE npc.ID = ? ";
			String sqlAcctName = "SELECT External_Id FROM nem_settlement_accounts " + "WHERE ID = ? AND VERSION = ? ";
			String sqlAcctIntf = "INSERT INTO nem_accounting_interfaces ( "
					+ " str_id, settlement_date, settlement_run_date, run_type, account_external_id, "
					+ " calculation_code, calculation_total, gst_code, gst_amount, srt_id, npc_id, "
					+ " sac_id, sac_version, description, srt_version ) "
					+ " VALUES ( ?,?,SYSDATE,?,?,DECODE(?,'NBSC','BESC',?),?,?,?,?,?,?,?,?,?) ";


			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand,inParam);
			for (Map row : list) {

				// query returns the fields:
				// 1. calculation_result,
				// 2. gst_amount,
				// 3. str_id,
				// 4. srt_or_npc_id,
				// 5. gst.name,
				// 6. sac_id,
				// 7. sac_version,
				// 8. 'SRT' srt_npc_type
				// 9. subq.srt_version srt_version
				// 10. rtyp.name
				String srtNpcType = null;

				accountInterface.calculationTotal = (BigDecimal)row.get("calculation_result");

				if (!accountInterface.calculationTotal.equals(new BigDecimal(0))) {
					accountInterface.gstAmount = (BigDecimal)row.get("gst_amount");
					accountInterface.sacId = (String)row.get("sac_id");
					accountInterface.sacVersion = (String)row.get("sac_version");
					accountInterface.srtVersion = (String)row.get("srt_version");
					srtNpcType = (String)row.get("srt_npc_type");
					String rtypName = (String)row.get("rtyp_name");

					// apply the debit/credit sign to the result
					int dcSign = 1;

					Object[] params1 = new Object[2];
					params1[0] =  rtypName;
					params1[1] =  accountInterface.sacVersion;
					List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlDebCre, params1);
					for (Map row1 : list1) {
						dcSign = ((BigDecimal)row1.get("result_sign")).intValue();
					}

					// dcSign as Int = debitCreditSign(this, name : rtypName, version : sacVersion)
					accountInterface.calculationTotal = accountInterface.calculationTotal.multiply(new BigDecimal(dcSign));

					// gstAmount = gstAmount * dcSign
					accountInterface.gstAmount = accountInterface.gstAmount.abs();

					// gstAmount -> positive
					if (srtNpcType.equalsIgnoreCase("SRT")) {
						accountInterface.srtId = (String)row.get("srt_or_npc_id");

						Object[] params2 = new Object[3];
						params2[0] =  accountInterface.srtId;
						params2[1] =  accountInterface.sacVersion;
						params2[2] =  accountInterface.srtId;
						List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlCalCode, params2);
						for (Map row2 : list2) {
							accountInterface.calcCode = (String)row2.get("name");
						}

						// calcCode = getCalculationCode(this, typeId : srtId, version : sacVersion)
						accountInterface.gstCode = (String)row.get("name");
						accountInterface.npcId = null;
						accountInterface.description = null;
					} else {
						accountInterface.npcId = (String)row.get("srt_or_npc_id");

						accountInterface.calcCode = null;

						Object[] params2 = new Object[3];
						params2[0] =  accountInterface.npcId;
						params2[1] =  accountInterface.sacVersion;
						params2[2] =  accountInterface.npcId;
						List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlCalCode, params2);
						for (Map row2 : list2) {
							accountInterface.calcCode = (String)row2.get("name");
						}

						if (accountInterface.calcCode == null) {
							try {
								throw new Exception("Get Calculation Code error: Account code is not defined for ID "
										+ accountInterface.npcId + ".");
							} catch (Exception e) {
								logger.error("Exception "+e.getMessage());
							}
						}

						// calcCode = getCalculationCode(this, typeId : npcId, version : sacVersion)
						accountInterface.gstCode = null;

						Object[] params3 = new Object[4];
						params3[0] =  accountInterface.npcId;
						params3[1] =  standingVer;
						params3[2] =  accountInterface.npcId;
						params3[3] =  standingVer;
						List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sqlGstCode, params3);
						for (Map row3 : list3) {
							accountInterface.gstCode = (String)row3.get("NAME");
						}

						if (accountInterface.gstCode == null) {
							logger.log(Priority.INFO,
									"[EMC] AccountingInterface.getGstCode() -- Gst Code not defined for NPC Id '"
											+ accountInterface.npcId + "'. ");

							throw new Exception(
									"Get GST Code error: Gst Code not defined for NPC Id '" + accountInterface.npcId + "'. ");

						}

						// gstCode = getGstCode(this, typeId : npcId, settDate :
						// settlementDate.format("dd-MMM-yyyy"))
						accountInterface.srtId = null;
						accountInterface.description = null;

						//Object[] params4 = new Object[1];
						//params4[0] =  accountInterface.npcId;
						List<Map<String, Object>> list4 = jdbcTemplate.queryForList(sqlNpcDesc, accountInterface.npcId);
						for (Map row2 : list4) {
							accountInterface.description = (String)row2.get("NAME");
						}

						if (accountInterface.description == null) {
							throw new Exception("Get Short Description error: NPC Code " + accountInterface.npcId + " is not defined.");

						}

						// description = getShortDescription(this, npcId : npcId)
					}

					accountInterface.clientId = null; 

					Object[] params2 = new Object[2];
					params2[0] =  accountInterface.sacId;
					params2[1] =  accountInterface.sacVersion;
					List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlAcctName, params2);
					for (Map row1 : list2) {
						accountInterface.clientId = (String)row1.get("External_Id");
					}

					if (accountInterface.clientId == null) {
						throw new Exception("Get Client ID error: Client ID is not defined for Settlement Account ID "
								+ accountInterface.sacId + ".");

					}

					// clientId = getClientId(this, sacId : sacId, version : sacVersion)

					Object[] params3 = new Object[15];
					params3[0] =  accountInterface.runId;
					params3[1] =  utilityFunctions.convertUDateToSDate(accountInterface.settlementDate);
					params3[2] =  accountInterface.runType;
					params3[3] =  accountInterface.clientId;
					params3[4] =  accountInterface.calcCode;
					params3[5] =  accountInterface.calcCode;
					params3[6] =  accountInterface.calculationTotal;
					params3[7] =  accountInterface.gstCode;
					params3[8] =  accountInterface.gstAmount;
					params3[9] =  accountInterface.srtId;
					params3[10] =  accountInterface.npcId;
					params3[11] =  accountInterface.sacId;
					params3[12] =  accountInterface.sacVersion;
					params3[13] =  accountInterface.description;
					params3[14] =  accountInterface.srtVersion == null ? "" : accountInterface.srtVersion.trim();
					jdbcTemplate.update(sqlAcctIntf, params3);
					// populateInterfaceTable this
					rows = rows + 1;
				}

				// this.calculationTotal > 0
			}


			if (rows <= 0) {
			    logger.log(Priority.INFO,"[EMC] AccountInterface.doSettlementFinalRun() -- zero rows fetch");
			}
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
			throw new SettlementRunException("Exception : "+e.getMessage(), "doSettlementFinalRun");
		}		
	}
	
	public String getFilename()
	{
		String sqlCommand = "SELECT character_value FROM aps_system_parameters WHERE NAME = 'ACCOUNTFILE_DIR'";

		String acifDir = "";
		String dirSeparator = "/";
		String filename=null;
		try {
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list) {
				acifDir = (String) row.get("character_value");
			}

			if (acifDir.indexOf("\\") >= 0) {
				dirSeparator = "\\\\";
			}

			acifDir = StringEscapeUtils.escapeJava(acifDir);

			logger.log(Priority.INFO, "AccountingInterface.getFilename() -- acifDir=" + acifDir);

			sqlCommand = "SELECT usap_seq.NEXTVAL FROM DUAL";

			logger.log(Priority.INFO, "AccountingInterface.getFilename() -- sqlCommand=" + sqlCommand);

			sqlCommand = "SELECT 'solomon' || TO_CHAR (SYSDATE, 'YYYYMMDDHH24MISS') || '.dat' filename FROM DUAL";

			logger.log(Priority.INFO, "AccountingInterface.getFilename() -- sqlCommand=" + sqlCommand);

			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list1) {
				filename = (String) row.get("filename");
			}
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}

		String acifDirFilename = acifDir + dirSeparator + filename;

		logger.log(Priority.INFO, "AccountingInterface.getFilename() -- returns acifDirFilename=" + acifDirFilename);

		return acifDirFilename;
		
	}
	
}
