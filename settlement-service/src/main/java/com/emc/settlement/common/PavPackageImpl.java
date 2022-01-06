package com.emc.settlement.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.emc.settlement.model.backend.pojo.CommonValue;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PavPackageImpl {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private UtilityFunctions utilityFunctions;

	protected static final Logger logger = Logger.getLogger(PavPackageImpl.class);

	public String createClawbackPackage(Date settlementDate) {
		String pkgId = "";
		String nextVersion = "";
		try{
			String packageTypeName = "SETTLEMENT_CLAWBACK_QUANTITIES";

			// pkgId as String
			String sqlCommand = "SELECT id FROM NEM.PAV_PACKAGE_TYPES WHERE name = ?";

			boolean isEmptyResult = true;

			String strSacId = null;

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {packageTypeName});
			for (Map row : list) {
				isEmptyResult = false;
			}

			if (!isEmptyResult) {
				nextVersion = this.getNextVersion(packageTypeName, settlementDate);
			}
			
			logger.log(Priority.INFO,
					"[EMC] Created Package for SETTLEMENT_CLAWBACK_QUANTITIES, Version: " + nextVersion);
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return nextVersion;

	}

	public String getNextVersion(String packageTypeName, Date settlementDate) {
		Map<String, String> map = this.createNextPkgVersion(packageTypeName, settlementDate);
		return map.get("nextVersion");
	}

	public void createMCPackages(SettRunPkg settRunPackage)
	{
		logger.log(Priority.INFO,"[EMC] Starting Method: EMC.PavPackage.createMCPackages() ...");
		    
		    Map<String, String> map = new HashMap<String, String>();
		    
		    map = this.createNextPkgVersion( "SETTLEMENT_MC_QUANTITIES",  settRunPackage.settlementDate);

		    
		    settRunPackage.mcQtyPkgId = map.get("pkgId");
		    settRunPackage.mcQtyPkgVer = map.get("nextVersion");

		    logger.log(Priority.INFO,"[EMC] Created Package for SETTLEMENT_MC_QUANTITIES, Version: " + settRunPackage.mcQtyPkgVer);

		    map = null;
		    map  = this.createNextPkgVersion( "SETTLEMENT_MC_PRICES", settRunPackage.settlementDate);

		    settRunPackage.mcPricePkgId = map.get("pkgId");
		    settRunPackage.mcPricePkgVer = map.get("nextVersion");

		    logger.log(Priority.INFO,"[EMC] Created Package for SETTLEMENT_MC_PRICES, Version: " + settRunPackage.mcQtyPkgVer);
		
		
	}


	public String createMMVolumePackage(Date settlementDate)
	{
		// Achilles - create PavPackage version for MM Volume type
		// SETTLEMENT_FSC_MM_VOLUMES
		String nextVersion = null;
		try{
			String packageTypeName = "SETTLEMENT_FSC_MM_VOLUMES";

			// pkgId as String
			String sqlCommand = "SELECT id FROM NEM.PAV_PACKAGE_TYPES WHERE name = ?";

			boolean isEmptyResult = true;

			/*pstmt = conn.prepareStatement(sqlCommand);
			pstmt.setString(1, packageTypeName);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				isEmptyResult = false;
				break;
			}*/
			try {
				//Object[] params = new Object[1];
				//params[0] =  packageTypeName;
				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, packageTypeName);
				for (Map row : list) {
					isEmptyResult = false;
					break;
				}
				
			}
			catch (DataAccessException e) {
				logger.log(Priority.INFO, "[EMC] createMMVolumePackage, Empty result for Query: "+ sqlCommand );
			}

			if (!isEmptyResult) {
				nextVersion = getNextVersion(packageTypeName, settlementDate);

			}

			logger.log(Priority.INFO, "[EMC] Created Package for SETTLEMENT_FSC_MM_VOLUMES, Version: " + nextVersion);
		}
		catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return nextVersion;

	}

	public String createMSSLPackage(Date settlementDate) {
		// logMessage "[EMC] Starting Method: UtilityFunctions.createMSSLPackage() ..."
		String pkgId = "";
		String nextVersion = "";
		nextVersion = this.getNextVersion("SETTLEMENT_MSSL_QUANTITIES", settlementDate);

		logger.log(Priority.INFO, "[EMC] Created Package for SETTLEMENT_MSSL_QUANTITIES, Version: " + nextVersion);

		return nextVersion;
	}


	public Map<String, String> createNextPkgVersion(String pkgType, Date settDate)
	{
		Map<String, String> retMap = new HashMap<String, String>();
		try{

			// logMessage "[EMC] Starging method PavPackage.createNextPkgVersion() ..."


			String sqlCommand;

			// nextVersion as Int
			String pkgTypeId = "";
			String nextVersion = "";
			String pkgId = "";


			// pkgId as String
			sqlCommand = "SELECT id FROM NEM.PAV_PACKAGE_TYPES WHERE name = ?";
			String strSacId = null;

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {pkgType});
			for (Map row : list) {
				pkgTypeId = (String)row.get("ID");
				break;
			}

			CallableStatement cstmt = null;
			try(Connection conn = jdbcTemplate.getDataSource().getConnection()) {
				cstmt = conn.prepareCall("{? = call PAV$PACKAGING.GET_PKT_VERSION(?,?)}");
				cstmt.setString(2, pkgType);
				cstmt.setString(3, "NEXT");
				cstmt.registerOutParameter(1, Types.VARCHAR);
				cstmt.executeUpdate();
				nextVersion = cstmt.getString(1);
			}
			finally {
				if(cstmt != null)UtilityFunctions.close(cstmt);
			}

			/*SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withSchemaName("NEM")
					.withCatalogName("PAV$PACKAGING")
					.withFunctionName("GET_PKT_VERSION")
					.withReturnValue()
					.useInParameterNames("package_type","curr_next")
					.declareParameters(new SqlInOutParameter("package_type", OracleTypes.VARCHAR),
							new SqlInOutParameter("curr_next", OracleTypes.VARCHAR),
							new SqlInOutParameter("OUT_1", OracleTypes.NUMBER));
			SqlParameterSource in = new MapSqlParameterSource()
					.addValue("package_type", pkgType)
					.addValue("curr_next", "NEXT");
			nextVersion = jdbcCall.executeFunction(String.class, in);*/
			
			// logMessage "[EMC] Next Version for [" + pkgType + "] is: " + nextVersion
			String comment = pkgType + " for " + utilityFunctions.getddMMMyyyy(settDate);
			pkgId = utilityFunctions.getEveId();
			sqlCommand = "INSERT INTO NEM.pav_packages(id, version, ready, comments, effective_date, end_date, pkt_id) "
					+ "VALUES (?,?,'Y',?,?,?,?)";

			logger.log(Priority.INFO, "[SQL] " + sqlCommand);

			Object[] params = new Object[6];
			params[0] =  pkgId;
			params[1] =  nextVersion;
			params[2] =  comment;
			params[3] =  utilityFunctions.convertUDateToSDate(utilityFunctions.truncateTime(settDate));
			params[4] =  utilityFunctions.convertUDateToSDate(utilityFunctions.truncateTime(settDate));
			params[5] =  pkgTypeId;
			logger.info("PavPackages: pkgId: "+pkgId+" Next Version: "+nextVersion);
			jdbcTemplate.update(sqlCommand, params);
			
			retMap.put("pkgId", pkgId);
			retMap.put("nextVersion", nextVersion);

		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return retMap;
	}

	public void createPkgAggregations(SettRunPkg pkg) throws Exception {
		logger.log(Priority.INFO, "[EMC] Starting method PavPackage.createPkgAggregations() ...");
		try{

			String pkgAggrType = "";
			String pkgTypeId = "";
			String sqlCommand;

			// Writing to pav_package_aggregations
			logger.log(Priority.INFO, "[EMC] Aggregating SETTLEMENT_RUN and SETTLEMENT_INPUTS ... ");

			int rowcnt = 0;
			sqlCommand = "SELECT id FROM NEM.pav_pkg_aggregation_types WHERE UPPER(name) = UPPER(?)";

			//Object[] params = new Object[1];
			//params[0] =  "SETTLEMENT_RUN_INPUTS";
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, "SETTLEMENT_RUN_INPUTS");
			for (Map row : list) {
				rowcnt = rowcnt + 1;
				pkgAggrType = (String)row.get("ID");

			}


			if (rowcnt == 0) {
				throw new Exception("No data found for Package Name: 'SETTLEMENT_RUN_INPUTS'");

			}

			String comment = "Aggregation of SETTLEMENT_RUN and SETTLEMENT_INPUTS for " + utilityFunctions.getddMMMyyyy(pkg.settlementDate);
			String sqlPkgAggr = "INSERT INTO NEM.pav_package_aggregations " +
					"(pkg_id_parent, pkg_id_child, pat_id, comments) VALUES (?,?,?,?)";

		    Object[] params1 = new Object[4];
			params1[0] =  pkg.settRunPkgId;
			params1[1] =  pkg.settInputPkgId;
			params1[2] =  pkgAggrType;
			params1[3] =  comment;
			int upd_cnt = jdbcTemplate.update(sqlPkgAggr, params1);
			
			
		    logger.log(Priority.INFO,"[EMC] Aggregating SETTLEMENT_INPUTS and SETTLEMENT input packages ... ");

		    rowcnt = 0;
		    sqlCommand = "SELECT id FROM NEM.pav_package_types WHERE UPPER(name) = UPPER(?)";

			//Object[] params3 = new Object[1];
			//params3[0] = "SETTLEMENT_INPUTS";
			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, "SETTLEMENT_INPUTS");
			for (Map row : list1) {
				rowcnt = rowcnt + 1;
				pkgTypeId = (String) row.get("ID");
			}
	    
		    
		    if (rowcnt == 0) {
					throw new Exception( "No data found for Package Name: 'SETTLEMENT_INPUTS'");

		    }

		    String aggrName;
		    String aggrId;
		    String parentTypeId;
		    String parentTypeName;
		    String childTypeId;
		    String childTypeName;
		    sqlCommand = "SELECT pat.name aggrName, pat.id aggrId, pt1.id parentTypeId, " + 
		                 "pt1.name parentTypeName, pt2.id childTypeId, pt2.name childTypeName " + 
		                 "FROM NEM.pav_pkg_aggregation_types pat, NEM.pav_package_types pt1, NEM.pav_package_types pt2 " + 
		                 "WHERE pat.pkt_id_parent = ? AND pt1.id = pat.pkt_id_parent AND pt2.id = pat.pkt_id_child";
		    String sqlStanding = "SELECT id FROM NEM.pav_packages p0 WHERE p0.pkt_id = ? " + 
		    "AND p0.effective_date <= ? AND p0.end_date >= ? AND p0.ready = 'Y' AND p0.released_date " + 
		    "= (select max(released_date) from pav_packages p1 where p1.pkt_id = p0.pkt_id and " + 
		    "p1.effective_date <= ? and p1.end_date >= ? and ready = 'Y')";
		    String sqlOthers = "SELECT id FROM NEM.pav_packages p0 WHERE p0.pkt_id = ? " + 
		    "AND trunc(p0.effective_date) = trunc(?) AND ready = 'Y' AND p0.version " + 
		    "= (select to_char(max(to_number(version))) from NEM.pav_packages p1 " + 
		    "where p1.pkt_id = p0.pkt_id and p1.effective_date = p0.effective_date and ready = 'Y')";

			List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sqlCommand, new Object[] {pkgTypeId});
			for (Map row : list2) {
			        aggrName = (String)row.get("aggrName");
			        aggrId = (String)row.get("aggrId");
			        parentTypeId = (String)row.get("parentTypeId");
			        parentTypeName = (String)row.get("parentTypeName");
			        childTypeId = (String)row.get("childTypeId");
			        childTypeName = (String)row.get("childTypeName");

				logger.log(Priority.INFO, "[EMC] Getting the package details for aggregation " + parentTypeName + " - " + childTypeName + " ... ");

				String childPkgId = null;

			        if (childTypeName.equalsIgnoreCase("STANDING")) {
						Object[] params2 = new Object[5];
						params2[0] =  childTypeId;
						params2[1] =  utilityFunctions.convertUDateToSDate(pkg.settlementDate);
						params2[2] =  utilityFunctions.convertUDateToSDate(pkg.settlementDate);
						params2[3] =  utilityFunctions.convertUDateToSDate(pkg.settlementDate);
						params2[4] =  utilityFunctions.convertUDateToSDate(pkg.settlementDate);
						List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sqlStanding, params2);
						for (Map row1 : list3) {
							childPkgId  = (String)row1.get("ID");
						}

			        }
			        else {
						Object[] params2 = new Object[2];
						params2[0] =  childTypeId;
						params2[1] =  utilityFunctions.convertUDateToSDate(pkg.settlementDate);
						List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sqlOthers, params2);
						for (Map row1 : list3) {
							childPkgId  = (String)row1.get("ID");
						}			            
			        }

			        if (childPkgId == null) {
			            logger.log(Priority.INFO,"[EMC] Package " + childTypeName + " not found. Can not proceed.");

			            throw new Exception( "Package " + childTypeName + " not found. Can not proceed.");
			        }

			        logger.log(Priority.INFO,"[EMC] Inserting new Aggregation " + parentTypeName + " - " + pkg.settInputPkgId + " - " + childTypeName + " " + childPkgId + " ... ");

			        comment = "Aggregation " + aggrName + " for " + parentTypeName + " - " + childTypeName + 
			                  " ... " + utilityFunctions.getddMMMyyyyhhmmss(pkg.settlementDate) ;

			        Object[] params2 = new Object[4];
					params2[0] =  pkg.settInputPkgId;
					params2[1] =  childPkgId;
					params2[2] =  aggrId;
					params2[3] =  comment;
					jdbcTemplate.update(sqlPkgAggr, params2);
			    }
			} catch ( Exception e) {
				logger.error("Exception "+e.getMessage());
				throw new Exception( "Package Aggregation " + e.getMessage());
			}
	}
	
	public String getCurrVersionPkt(String pktType, String versionDate, String eventId)
	{
		// Test
		// pktType = "SETTLEMENT_MSSL_QUANTITIES"
		// versionDate = "01-Feb-2010"
		// eventId = ""
		// Test
		String ORACLE_PCK_DATE_FORMAT = "DD-MON-RRRR";
		String pVersion = null;
		String sqlCommand;
		String oraToDateFunctionStr = "TO_DATE('" + versionDate + "', '" + ORACLE_PCK_DATE_FORMAT + "') ";
		try{
			// INTO pVersion
			if (pktType.equalsIgnoreCase("STANDING")) {
				sqlCommand = "SELECT TO_CHAR(MAX(version)) version FROM NEM.nem_standing_versions_mv " + " WHERE TRUNC("
						+ oraToDateFunctionStr + ") BETWEEN effective_date AND end_date";

				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
				for (Map row : list) {
					pVersion = (String)row.get("version");
				}
			} else {
				String cvPktId = getPktTypeIdPkt(pktType, eventId);
				sqlCommand = "SELECT pck.version FROM NEM.PAV_PACKAGES pck, NEM.PAV_PACKAGE_TYPES pkt"
						+ " WHERE pck.pkt_id = '" + cvPktId + "' AND pck.pkt_id = pkt.id "
						+ " AND pck.version <> 'C' AND pck.ready = 'Y' " + " AND ( ( " + oraToDateFunctionStr
						+ " BETWEEN NVL(pck.effective_date, " + oraToDateFunctionStr + ") " + " AND NVL(pck.end_date, "
						+ oraToDateFunctionStr + ") " + " AND pkt.package_group <> 'TXL' ) "
						+ " OR ( pck.effective_date = " + oraToDateFunctionStr + " ) "
						+ " AND pkt.package_group =  'TXL' ) " + " ORDER BY TO_NUMBER(pck.version) DESC ";

				List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
				for (Map row : list) {
					pVersion = (String)row.get("version");
					break;
				}

				if (pVersion == null) {
					if (eventId != null) {
						// NEED TO IMPLEMENT: capture eventId into jam_messages
						// JAM$MESSAGING.log_message(event_id,'E','Package type '||pkg_type||' does not
						// exist.','PAV$PACKAGING.GET_CURR_VERSION_PKT');
						logger.log(Priority.INFO, "[EMC] PavPackage.getCurrVersionPkt() -- pVersion is null");
					}
					throw new Exception("No Version found for Package type: " + pktType);
				}
			}
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}

		return pVersion;

	}

	public void getCurrentMCPackages(SettRunPkg settRunPackage)
	{
		logger.log(Priority.INFO, "[EMC] Starting Method: EMC.PavPackage.getCurrentMCPackages() ...");
		try {

			String dbpath = CommonValue.nemDB;
			String sqlCommand;

			// settRunPackage as SettRunPkg
			sqlCommand = "select pkg.id, pkg.version from NEM.pav_packages pkg, NEM.pav_package_types pkt "
					+ "where pkt.id = pkg.pkt_id and pkt.name = ? and ready = 'Y' "
					+ "and trunc(?) between trunc(effective_date) and trunc(end_date) "
					+ "and version = ( select max(to_number(version)) from NEM.pav_packages pkg, NEM.pav_package_types pkt "
					+ "where pkt.id = pkg.pkt_id and pkt.name = ? "
					+ "and ready = 'Y' and trunc(?) between trunc(effective_date) and trunc(end_date) )";

			Object[] params = new Object[4];
			params[0] =  "SETTLEMENT_MC_QUANTITIES";
			params[1] =  utilityFunctions.convertUDateToSDate(settRunPackage.settlementDate);
			params[2] =  "SETTLEMENT_MC_QUANTITIES";
			params[3] =  utilityFunctions.convertUDateToSDate(settRunPackage.settlementDate);
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, params);
			for (Map row : list) {
				settRunPackage.mcQtyPkgId = (String)row.get("id");
				settRunPackage.mcQtyPkgVer = (String)row.get("version");
			}

			logger.log(Priority.INFO,
					"[EMC] Current version for 'SETTLEMENT_MC_QUANTITIES' : " + settRunPackage.mcQtyPkgVer);

			Object[] params1 = new Object[4];
			params1[0] =  "SETTLEMENT_MC_PRICES";
			params1[1] =  utilityFunctions.convertUDateToSDate(settRunPackage.settlementDate);
			params1[2] =  "SETTLEMENT_MC_PRICES";
			params1[3] =  utilityFunctions.convertUDateToSDate(settRunPackage.settlementDate);
			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, params1);
			for (Map row : list1) {
				settRunPackage.mcPricePkgId = (String)row.get("id");
				settRunPackage.mcPricePkgVer = (String)row.get("version");
			}
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}

		logger.log(Priority.INFO, "[EMC] Current version for 'SETTLEMENT_MC_PRICES' : " + settRunPackage.mcPricePkgVer);

	}

	public String getCurrentMSSLPackage(Date settDate) throws Exception
	{
		String msslQtyVer = null;

		try{
			logger.log(Priority.INFO, "[EMC] Starting method PavPackage.getCurrentMSSLPackage() ...");
			String pkgTypeId = null;
			String sqlCommand;

			sqlCommand = "SELECT id FROM NEM.PAV_PACKAGE_TYPES " + "WHERE NAME = 'SETTLEMENT_MSSL_QUANTITIES'";

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list) {
				pkgTypeId = (String)row.get("id");
				break;
			}

			if (pkgTypeId == null) {
				throw new Exception("No data found for package name: 'SETTLEMENT_MSSL_QUANTITIES'");
			}

			sqlCommand = "SELECT pck.version FROM NEM.pav_packages pck, NEM.pav_package_types pkt "
					+ "WHERE pck.pkt_id = ? AND pck.pkt_id = pkt.id AND pck.version <> 'C' AND pck.ready = 'Y' "
					+ "AND (( ? between NVL(pck.effective_date, ?) AND NVL(pck.end_date, ?) "
					+ "AND pkt.package_group <> 'TXL' ) OR ( trunc(pck.effective_date) = trunc(?)) "
					+ "AND pkt.package_group = 'TXL') ORDER BY TO_NUMBER(pck.version) DESC";

			Object[] params1 = new Object[5];
			params1[0] =  pkgTypeId;
			params1[1] =  utilityFunctions.convertUDateToSDate(settDate);
			params1[2] =  utilityFunctions.convertUDateToSDate(settDate);
			params1[3] =  utilityFunctions.convertUDateToSDate(settDate);
			params1[4] =  utilityFunctions.convertUDateToSDate(settDate);
			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, params1);
			for (Map row : list1) {
				msslQtyVer = (String)row.get("version");
				logger.log(Priority.INFO, "[EMC] Current version for 'SETTLEMENT_MSSL_QUANTITIES' : " + msslQtyVer);
				break;
				// Only get the latest version
			}
		} catch (SQLException e) {
			logger.error("Exception "+e.getMessage());
		}
		return msslQtyVer;
	}

	public String getPktTypeIdPkt(String pktType, String eventId)
	{
		String id = null;
		try{
			/*
			 * NEED TO CONFIRM: exception handling routine and error message if package ID
			 * cannot be found.
			 */
			String sqlCommand = "SELECT id  FROM PAV_PACKAGE_TYPES WHERE UPPER(name) = UPPER('" + pktType + "') ";

			// resultset should have only just 1 row or zero rows

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
			for (Map row : list) {
				id = (String)row.get("id");
			}

			if (id == null) {
				if (eventId != null) {
					// NEED TO IMPLEMENT: capture eventId into jam_messages
					// JAM$MESSAGING.log_message(event_id,'E',substr(SQLERRM,1,4000),'PAV$PACKAGING.GET_PKG_TYPE_ID_PKT')
					logger.log(Priority.INFO, "[EMC] PacPackage.getPktTypeIdPkt() -- package id is null");
				}
				throw new Exception("Package id is not defined for '" + pktType + "'");

			}

		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return id;

	}

	public String getStandingVersion(Date settlementDate) throws Exception
	{
		String standingVer = null;
		try{
			String sqlCommand = "select to_char(max(version))  version " + "from NEM.NEM_STANDING_VERSIONS_MV "
					+ "where trunc(?) between effective_date and end_date";

			//Object[] params = new Object[1];
			//params[0] =  utilityFunctions.convertUDateToSDate(settlementDate);
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, utilityFunctions.convertUDateToSDate(settlementDate));
			for (Map row : list) {
				standingVer = (String)row.get("version");
			}

			if (standingVer == null) {

				throw new Exception("Error getting standing current version !!!");

			}
		} catch (SQLException e) {
			logger.error("Exception "+e.getMessage());
		}
		return standingVer;
	}

}
