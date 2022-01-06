package com.emc.settlement.backend.fileupload;

import static com.emc.settlement.model.backend.constants.BusinessParameters.PROCESS_NAME_JMS_LISTENER_MAIN;
import static java.net.InetAddress.getLocalHost;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;

import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.pojo.Message;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j
public class JMSListenerMain {

	@Autowired
	private UtilityFunctions utilityFunctions;

	private static final Logger logger = Logger.getLogger(JMSListenerMain.class);


	@Transactional(readOnly = true)
	public Map<String, Object> jmsReceiver(Map<String, Object> variableMap) {

		String msgStep = PROCESS_NAME_JMS_LISTENER_MAIN + "." + "jmsReceiver()";
		Message message = (Message) variableMap.get("message");

		logger.log(Priority.INFO,"[EMC] Starting Activity: JMSReceiver ... ");
		String activityName = "jmsReceiver()";
		try {
			String msgType = message.jmsType;

			logger.log(Priority.INFO, "[EMC] METERINGQ - Received JMS Message: " + msgType);

			logger.log(Priority.INFO,"[EMC] METERINGQ - Received JMS correlationId: " + message.correlationId);

			logger.log(Priority.INFO,"[EMC] METERINGQ - Received JMS destination: " + message.destination);

			logger.log(Priority.INFO,"[EMC] METERINGQ - Received JMS MessageId: " + message.messageId);

			logger.log(Priority.INFO,"[EMC] METERINGQ - Received JMS ReplyTo: " + message.replyTo);

			logger.log(Priority.INFO,"[EMC] METERINGQ - Received JMS TextValue: " + message.textValue.substring(100));

			String processName = null;
			String fileType = null;
			variableMap.put("comments", msgType + " via EBT");
			variableMap.put("fileContentName", "");
			variableMap.put("filename", "");
			variableMap.put("uploadUser", "SYSTEM");
			variableMap.put("uploadMethod", "EBT");

			if (msgType.equals(UtilityFunctions.getProperty("JMS_TYPE_VESTING"))) {
				processName = "VestingContractUploadMain";
				fileType = "VEST";
				variableMap.put("fileType", fileType);
			}
			else if (msgType.equals(UtilityFunctions.getProperty("JMS_TYPE_DMF"))) {
				processName = "SaveMSSLMeteringFile";
				fileType = "MTR";
				variableMap.put("fileType", fileType);
			}
			else if (msgType.equals(UtilityFunctions.getProperty("JMS_TYPE_CMF"))) {
				processName = "SaveMSSLMeteringFile";
				fileType = "CTR";
				variableMap.put("fileType", fileType);
			}
			else if (msgType.equals(UtilityFunctions.getProperty("JMS_TYPE_FSC"))) {
				processName = "ForwardSalesContractUploadMain";
				fileType = "FSR";
				variableMap.put("fileType", fileType);
			}
			else {
				logger.log(Priority.WARN, "[EMC] METERINGQ - Unknown JMS Message Type: " + message.jmsType);

				// Save unknown messages to file system
				String dir = UtilityFunctions.getProperty("SHARED_DRIVE") + File.separator + UtilityFunctions.getProperty("INCOMING_JMS_DIR") +
						File.separator + utilityFunctions.getddMMyyyy(new Date());
				File d = new File(dir);

				if (d.exists() == false) {
					utilityFunctions.makeDirs(dir);
				}

				StringBuilder textFile = new StringBuilder();
				String filename = dir + File.separator + "Msg-" + utilityFunctions.getyyyyMMddHHmmss(new Date()) + ".dat";

				textFile.append("Message Type: " + message.jmsType+ "\n");

				textFile.append("correlationId: " + message.correlationId+ "\n");

				textFile.append("destination: " + message.destination+ "\n");

				textFile.append("messageId: " + message.messageId+ "\n");

				textFile.append("replyTo: " + message.replyTo+ "\n");

				textFile.append("textValue: " + message.textValue);


				try (FileWriter fw = new FileWriter(filename); BufferedWriter writer = new BufferedWriter(fw)) {

					writer.write(textFile.toString());
				}
				catch (IOException e) {
					logger.error("Exception "+e.getMessage());
				}

				logger.log(Priority.INFO, "[EMC] Unknown incoming JMS message written to: " + filename);

				variableMap.put("fileType", "OTHER");
				return variableMap;
			}

			String fn = UtilityFunctions.getProperty("SHARED_DRIVE") + UtilityFunctions.getProperty("UPLOADED_FILE_DIR") + 
					"MSSL_" + fileType + "_" + utilityFunctions.getyyyyMMddHHmmssSSS(new Date()) + ".dat";
			utilityFunctions.writeToFile(message.textValue, fn);
			variableMap.put("fileContentName", fn);
			variableMap.put("filename", fn);

			// Create Process Instance
			logger.log(Priority.INFO, "[EMC] Starting Receiving Process ...");

		}
		catch (Exception e) {
			String fn = UtilityFunctions.getProperty("SHARED_DRIVE") + "\\FailedMSSLFile" + File.separator +
					"MSSL_" + utilityFunctions.getyyyyMMddHHmmssSSS(new Date()) + ".dat";

			logger.log(Priority.ERROR, "[EMC] <" + activityName + "> Exception: " + e.getMessage());

			logger.log(Priority.ERROR, "[EMC] Stack Trace: " + e.getStackTrace().toString());

			String eveId = null;
			eveId = utilityFunctions.createJAMEvent("EXE", "Receive MSSL JMS");
			try {
				logger.log(Priority.ERROR, "[EMC] Saved MSSL File to: " + fn + " on Server: "
						+ getLocalHost().getHostName());

				// Create JAM_EVENTS
				utilityFunctions.logJAMMessage(eveId,  "E", "JMSListener",
						"Saved MSSL File to: " + fn + " on Server: "
								+ getLocalHost().getHostName()
						,"");
			}
			catch (UnknownHostException e1) {
				utilityFunctions.logJAMMessage(eveId,  "E", "JMSListener",
						"Saved MSSL File to: " + fn + " on Server: "
						,"");
			}

			utilityFunctions.updateJAMEvent(true, eveId);

		}
		logger.log(Priority.INFO, "Returning from service "+msgStep);
		return variableMap;
	}
}
