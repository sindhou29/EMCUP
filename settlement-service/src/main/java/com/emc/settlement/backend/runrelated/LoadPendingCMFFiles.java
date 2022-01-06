/**
 * 
 */
package com.emc.settlement.backend.runrelated;

import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emc.settlement.common.PavPackageImpl;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.constants.BusinessParameters;
import com.emc.settlement.model.backend.exceptions.SettlementRunException;
import com.emc.settlement.model.backend.pojo.SettlementRunInfo;
import com.emc.settlement.model.backend.pojo.fileupload.MSSLCorrected;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author DWTN1561
 *
 */
@Service
public class LoadPendingCMFFiles implements Serializable{

	/**
	 * 
	 */
	public LoadPendingCMFFiles() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws JsonProcessingException
	 */
	private static final Logger logger = Logger.getLogger(LoadPendingCMFFiles.class);

	@Autowired
	private UtilityFunctions utilityFunctions;
    @Autowired
	private JdbcTemplate jdbcTemplate;
    @Autowired
    PavPackageImpl pavPackageImpl;

    String logPrefix ="[EMC]";
    String msgStep = "";
    String process_name = "LoadPendingCMFFiles";
    
    @Transactional
    public void loadPendingCMFFiles(Map<String, Object> variableMap)
	{

		SettlementRunInfo runInfo = (SettlementRunInfo)variableMap.get("runInfo");
		
		msgStep =  process_name+".loadPendingCMFFiles()";
		logger.log(Priority.INFO,logPrefix + " Starting Activity: " + msgStep + " ...");
		try {

			String splex_mode = utilityFunctions.getShareplexMode();

			if (!splex_mode.equalsIgnoreCase("Y")) {

				// Test
				// settlementDate = '2010-02-22'
				// runType = "P"
				// Test
				{
					Date now = new Date();
					String dontDoAnything; // BypassDRCAPPh2forSP Added
					boolean DREffDate = false; // ByPassforSPServices Added
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");

					String settDateStr = utilityFunctions.getddMMyyyy(runInfo.settlementDate);
					File pendingDir = new File(UtilityFunctions.getProperty("SHARED_DRIVE") + File.separator +"CMF" +File.separator  + "Pending"+ File.separator );
					String destPath;
					dateFormat.setLenient(false);

					String fileToBeLoad = "Pending-" + runInfo.runType + "-" + settDateStr + ".csv";

					logger.log(Priority.INFO, "[EMC] Pending MSSL File to be upload: " + fileToBeLoad);

					fileToBeLoad = fileToBeLoad.toUpperCase();

					if (pendingDir.isDirectory() == true) {
						File[] cmfFiles = pendingDir.listFiles();

						logger.info("cmfFiles : " + cmfFiles.length);
						
						for (int n = 0; n <= cmfFiles.length - 1; n++) {
							logger.info("cmfFile name : " + cmfFiles[n].getName());
							if (cmfFiles[n].getName().toUpperCase().contains(fileToBeLoad)) {
								int count = 0;
								
								logger.log(Priority.INFO, "File Name : " + cmfFiles[n].getName());
								
								utilityFunctions.logJAMMessage(runInfo.runEveId, "I", msgStep,
										"Loading pending CMF file: " + cmfFiles[n].getName(), "");

								FileReader fileReader = new FileReader(cmfFiles[n]);
								com.opencsv.CSVReader csvReader = new com.opencsv.CSVReader(fileReader);
								String[] line = csvReader.readNext();
								String version;
								version = pavPackageImpl.getStandingVersion(runInfo.settlementDate);

								logger.log(Priority.INFO, "[EMC] Standing Version: " + version);

								String pktId = null;
								String currVersion = null;
								Map<String, String> pkgMap = new HashMap<String, String>();

								pkgMap = pavPackageImpl.createNextPkgVersion("SETTLEMENT_MSSL_QUANTITIES",
										runInfo.settlementDate);
								pktId = pkgMap.get("pkgId");
								currVersion = pkgMap.get("nextVersion");

								logger.log(Priority.INFO,
										"[EMC] Created new MSSL package version: " + currVersion
												+ " for Settlement Date: "
												+ utilityFunctions.getddMMMyyyy(runInfo.settlementDate));

								utilityFunctions.logJAMMessage(runInfo.runEveId, "I", msgStep,
										"Created new MSSL package version: " + currVersion + " for Settlement Date: "
												+ utilityFunctions.getddMMMyyyy(runInfo.settlementDate),
										"");

								String sqlCommand = "INSERT INTO NEM.NEM_SETTLEMENT_QUANTITIES (ID, VERSION, SETTLEMENT_DATE, PERIOD, "
										+ "QUANTITY_TYPE, QUANTITY, SAC_ID, SAC_VERSION, NDE_ID, NDE_VERSION) VALUES (?,?,?,?,?,?,?,?,?,?)";

								while (line != null && line.length != 0) {
									MSSLCorrected cmfMssl = new MSSLCorrected();
									cmfMssl.quantityType = String.valueOf(line[0]);
									cmfMssl.settlementDate = dateFormat.parse(String.valueOf(line[1]));
									cmfMssl.period = Integer.parseInt(String.valueOf(line[2]));
									cmfMssl.quantity = Double.parseDouble(String.valueOf(line[3]));
									cmfMssl.nodeId = String.valueOf(line[4]);
									cmfMssl.sacId = String.valueOf(line[5]);

									if (cmfMssl.quantityType.equalsIgnoreCase("WLQ")
											|| cmfMssl.quantityType.equalsIgnoreCase("WDQ")) {
										DREffDate = utilityFunctions.isAfterDRCAPEffectiveDate(cmfMssl.settlementDate); // ByPassforSPServices
																														// Added
									}

									if (cmfMssl.quantityType.equalsIgnoreCase("WLQ") && !(DREffDate)) {
										dontDoAnything = "dontDoAnything";
										line = csvReader.readNext();
									} else if (cmfMssl.quantityType.equalsIgnoreCase("WDQ") && !(DREffDate)) {
										dontDoAnything = "dontDoAnything";
										line = csvReader.readNext();
									}

									else // BypassDRCAPPh2forSP Added
									{

										int updCount;

										Object[] params = new Object[10];
										params[0] = utilityFunctions.getEveId();
										params[1] = currVersion;
										params[2] = utilityFunctions.convertUDateToSDate(runInfo.settlementDate);
										params[3] = cmfMssl.period;
										params[4] = cmfMssl.quantityType;
										params[5] = cmfMssl.quantity;
										params[6] = cmfMssl.sacId;
										if (cmfMssl.sacId == null || cmfMssl.sacId.equalsIgnoreCase("")) {
											// logMessage "mssl.sacId is null" using severity = WARNING
											params[7] = null;
										} else {
											// logMessage "mssl.sacId is not null" + cmfMssl.sacId + "," + version
											params[7] = version;
										}

										params[8] = cmfMssl.nodeId;

										if (cmfMssl.nodeId == null || cmfMssl.nodeId.equalsIgnoreCase("")) {
											// logMessage "mssl.nodeId is null" using severity = WARNING
											params[9] = null;
										} else {
											// logMessage "mssl.nodeId is not null" + cmfMssl.nodeId + "," + version
											params[9] = version;
										}
										jdbcTemplate.update(sqlCommand, params);
										line = csvReader.readNext();
										count = count + 1;

									} // BypassDRCAPPh2forSP Added end of loop for bypass


								}
								
								fileReader.close();

								utilityFunctions.logJAMMessage(runInfo.runEveId, "I", msgStep,
										"Inserted " + count + " records to NEM_SETTLEMENT_QUANTITIES", "");

								destPath = cmfFiles[n].getAbsolutePath().replace("Pending", "Processed");
								destPath = destPath.replace("Processed-", "Pending-");

								logger.log(Priority.INFO, logPrefix + " Move file from: "
										+ cmfFiles[n].getAbsolutePath() + " to: " + destPath);

								File destDir = new File(UtilityFunctions.getProperty("SHARED_DRIVE") + File.separator +"CMF" +File.separator + "Processed" +File.separator);

								if (destDir.exists() == false) {
									destDir.mkdirs();
								}

								cmfFiles[n].renameTo(new File(destPath));

								utilityFunctions.logJAMMessage(runInfo.runEveId, "I", msgStep,
										"Pending CMF file moved to: " + destPath, "");
							}
						}
					}
				}
				logger.log(Priority.INFO, "[EMC] END of Process for Pending MSSL File to be upload");
			}
		} catch (Exception e) {
			logger.error("Exception "+e.getMessage());
			throw new SettlementRunException(e.getMessage(), msgStep);
		} 		
	}

}
