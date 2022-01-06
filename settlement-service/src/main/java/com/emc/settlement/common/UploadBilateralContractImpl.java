package com.emc.settlement.common;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UploadBilateralContractImpl {

	
	public UploadBilateralContractImpl() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	PavPackageImpl pavPackageImpl;

	protected static final Logger logger = Logger.getLogger(UploadBilateralContractImpl.class);
	
	public int blcHasValidAncillaryGroup()
	{
		try {
			int validationNum = 0;

			// 0 = success
			boolean isMatch = false;

			String sellerSacId = null;
			String sacVersion = null;
			String buyerSacId = null;
			String message = null;

			// retrieve ID using latest version number
			// NOTE ASSUMPTION : SQL below assumes that nem_ancillary_providers version
			// number is in sync with
			// nem_settlement_account version number !!
			String sqlCommand = "SELECT F.sac_id, F.acg_id, F.acg_version FROM nem_facilities F "
					+ " WHERE (F.sac_id = '" + sellerSacId + "' OR F.Sac_id = '" + buyerSacId + "') "
					+ " AND F.Sac_version = '" + sacVersion + "' " + " AND F.id IN ( "
					+ " SELECT fct_id FROM nem_ancillary_providers P, nem_ancillary_groups G "
					+ " WHERE P.acg_id = G.id AND P.acg_version = G.version AND G.ancillary_type = 'RSV' AND "
					+ " P.version = '" + sacVersion + "' )";

			// resultset should have only just 1 row or zero rows
			String[] sacId = new String[1];
			String[] acgId = new String[1];
			String[] acgVersion = new String[1];

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
		    for (Map row : list) {
				sacId[0] = (String)row.get("sac_id");
				acgId[0] = (String)row.get("acg_id");
				acgVersion[0] = (String)row.get("acg_version");
			}

			if (sacId == null || acgVersion == null) {
				// there is no ancillary group assocaited with 'Reserve' bilateral contract
				// buyer and seller
				message = "No ancillary group is associated with bilateral contract of type RESERVE";

				return 15;
			}

			// there is no ancillary group assocaited with 'Reserve' bilateral contract
			// buyer and seller
			if (sacId.toString().indexOf(sellerSacId) == -1) {
				message = "Seller Settlement Account " + sellerSacId + " does not own any facility that"
						+ " is an ancillary service provider for the ancillary reserve group.";

				return 23;
			} else if (sacId.toString().indexOf(buyerSacId) == -1) {
				message = "No ancillary group is associated with bilateral contract of type RESERVE for account "
						+ buyerSacId;

				return 15;
			}

			// retrieve ID using latest version number
			sqlCommand = "SELECT MAX(TO_NUMBER(version)) count FROM nem_ancillary_groups ";
			String version = null;


			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
		    for (Map row : list1) {
				version = (String)row.get("count");
			}

			for (String s : acgVersion) {
				if (!s.equals(version)) {
					// ancillary group not valid because version is not the latest
					message = "Ancillary Reserve Group associated with the bilateral contract"
							+ " of type RESERVE is not a valid ancillary group.";
					return 16;
				}
			}

		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return 0;

	}
	
	public boolean contractNameExist(String item)
	{
		boolean isValid = false;

		try {
			String version = null;

			// retrieve ID using latest version number
			String sqlCommand = "SELECT count(*) count FROM nem_bilateral_contracts ";
			sqlCommand = sqlCommand + "WHERE Name = '" + item + "' ";

			// resultset should have only just 1 row or zero rows
			String externalId = null;
			BigDecimal count = new BigDecimal(0);

			
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
		    for (Map row : list) {
				count = (BigDecimal)row.get("count");
			}

			isValid = count.intValue() > 0;

		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}
		return isValid;		
	}
	
	public boolean facilitiesExist(String acgId, String sacId, String version, String eveId )
	{
		BigDecimal count = new BigDecimal(0);
		try {
			/* 
			Checks the existence of facilities within a specific ancillary group for a specific
			   settlement account and version. If facilities exist then return TRUE else return FALSE.*/
			String sqlCommand = "SELECT COUNT(*) number_of_facilities FROM NEM_FACILITIES fac," + 
			" NEM_ANCILLARY_PROVIDERS acp, NEM_ANCILLARY_GROUPS acg, NEM_NODES nod" + 
			" WHERE UPPER(acg.id) = UPPER('" + acgId + "')" + 
			" AND acg.id = acp.acg_id AND acg.version = acp.acg_version AND acp.fct_id = fac.id" + 
			" AND acp.fct_version = fac.version AND fac.nde_id = nod.id AND fac.nde_version = nod.version" + 
			" AND nod.sac_id = '" + sacId + "' AND nod.sac_version = '" + version + "'";

			// resultset should have only just 1 row or zero rows

			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
		    for (Map row : list) {
				count = (BigDecimal)row.get("number_of_facilities");
			}

			
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}		
		return (count.intValue() > 0);
	}
	
	public String getAncillaryGroupId(String serviceName, String startDate)
	{
		// find ancillary group Id using service name with current version number
		String acgId = null;
		try {
			String version = null;
			version = pavPackageImpl.getCurrVersionPkt("STANDING", startDate, null);

			// retrieve ID using latest version number
			// NOTE ASSUMPTION : SQL below assumes that nem_ancillary_providers version
			// number is in sync with
			// nem_settlement_account version number !!
			String sqlCommand = "SELECT id FROM NEM_ANCILLARY_GROUPS acg " + " WHERE UPPER(acg.name) = UPPER('"
					+ serviceName + "')" + " AND acg.version = '" + version + "'";
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, new Object[] {});
		    for (Map row : list) {
				acgId = (String)row.get("id");
			}
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
		}

		return acgId;

	}
}
