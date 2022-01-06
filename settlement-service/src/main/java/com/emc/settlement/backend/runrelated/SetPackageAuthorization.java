/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author DWTN1561
 *
 */
@Service
public class SetPackageAuthorization implements Serializable{

	/**
	 * 
	 */
	public SetPackageAuthorization()  {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(RerunInclusions.class);

	@Autowired
	private UtilityFunctions utilityFunctions;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	String logPrefix = "[EMC]";
	String msgStep = "";

	@Transactional
	public void settPkgAuthorization(Map<String, Object> variableMap) 
	{

		String username = (String)variableMap.get("username"); 
		String packageId = (String)variableMap.get("packageId"); 
		String runStatus = (String)variableMap.get("runStatus");
		
		msgStep = "SetPackageAuthorization.settPkgAuthorization()";
		try{

			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");
			logger.info("Params - username : " + username + " packageId : "+packageId+" runStatus : "+runStatus);

			int rowcnt = 0;
			String pavAuthId = utilityFunctions.getEveId();
			String userId;
			String sqlCommand;
			String sqlCommand1;
			userId = utilityFunctions.getUserId(username);
			sqlCommand = "SELECT pkt.NAME FROM NEM.PAV_PACKAGE_TYPES pkt, NEM.PAV_PACKAGES pck "
					+ "WHERE pck.ID = ? AND pck.pkt_id = pkt.ID";

			//Object[] params = new Object[1];
			//params[0] =  packageId;
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sqlCommand, packageId);
			for (Map row : list) {
				String packageName = (String)row.get("NAME");

				if (packageName.equalsIgnoreCase("SETTLEMENT_RUN")) {
					if (runStatus.equalsIgnoreCase("W")) {
						// Waiting
						sqlCommand1 = "INSERT INTO NEM.NEM_PACKAGE_AUTHORISATIONS (ID, AUTHORISATION_STATUS, AUTHORISATION_DATE, PKG_ID, USR_ID) "
								+ " values ('" + pavAuthId + "', 'WAITING', SYSDATE, '" + packageId + "', '" + userId
								+ "')";
					} else if (runStatus.equalsIgnoreCase("E")) {
						// Error
						sqlCommand1 = "INSERT INTO NEM.NEM_PACKAGE_AUTHORISATIONS (ID, AUTHORISATION_STATUS, AUTHORISATION_DATE, PKG_ID, USR_ID) "
								+ " values ('" + pavAuthId + "', 'NOT AUTHORISED', SYSDATE, '" + packageId + "', '"
								+ userId + "')";
					} else {
						logger.info(logPrefix + "Run Status: '" + runStatus + "' not regonized.");

						throw new Exception("[EMC] Run Status: '" + runStatus + "' not regonized.");
					}

					jdbcTemplate.update(sqlCommand1, new Object[] {});
					logger.info(logPrefix + "Set Package Authorisation Done. ");
				}
			}
		} catch (Exception e) {
			logger.info(logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());

			throw new SettlementRunException(e.getMessage(), msgStep);
		} 
	}
	
}
