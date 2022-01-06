package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.common.UtilityService;
import com.emc.settlement.backend.scheduled.ScheduledMSSLFileReceivingVerification;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledEMCPSOBudgetDataVerificationTask;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledMSSLFileReceivingVerificationTask;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ScheduledMSSLFileReceivingVerificationWIH implements WorkItemHandler {

	private static final Logger logger = Logger.getLogger(ScheduledMSSLFileReceivingVerificationWIH.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		String eveId = (String) workItem.getParameter("eveId");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		Boolean valid = (Boolean) workItem.getParameter("valid");
		String soapServiceUrl = (String) workItem.getParameter("soapServiceUrl");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		String splexMode = (String) workItem.getParameter("splexMode");
		String scheduleFlag = (String) workItem.getParameter("scheduleFlag");
		Boolean businessDay = (Boolean) workItem.getParameter("businessDay");

		variableMap.put("eveId", eveId);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("valid", valid);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);
		variableMap.put("businessDay", businessDay);
		
		ScheduledMSSLFileReceivingVerification scheduledMSSLFileReceivingVerification = ContextInitializer.getInstance()
				.getContext().getBean(ScheduledMSSLFileReceivingVerification.class);
		UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);
		UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);
		
		try {
			if ((ScheduledMSSLFileReceivingVerificationTask.CHECKBUSINESSDAY.getValue()).equals(operationPath)) {
				variableMap = scheduledMSSLFileReceivingVerification.checkBusinessDay(variableMap);
			} else if ((ScheduledMSSLFileReceivingVerificationTask.INITIALIZEVARIABLES.getValue()).equals(operationPath)) {
				variableMap = scheduledMSSLFileReceivingVerification.initializeVariables(variableMap);
			} else if((ScheduledMSSLFileReceivingVerificationTask.GETTRADINGDATESRANGE.getValue().equals(operationPath))) {
				variableMap = utilityService.getTradingDatesRange(variableMap);
			} else if ((ScheduledMSSLFileReceivingVerificationTask.CREATEEVENT.getValue()).equals(operationPath)) {
				variableMap = scheduledMSSLFileReceivingVerification.createEvent(variableMap);
			} else if ((ScheduledMSSLFileReceivingVerificationTask.CHECKMSSL.getValue()).equals(operationPath)) {
				variableMap = scheduledMSSLFileReceivingVerification.checkMSSL(variableMap);
			} else if ((ScheduledMSSLFileReceivingVerificationTask.UPDATEEVENT.getValue()).equals(operationPath)) {
				scheduledMSSLFileReceivingVerification.updateEvent(variableMap);
			} else if ((ScheduledMSSLFileReceivingVerificationTask.ALERTNOTIFICATION.getValue())
					.equals(operationPath)) {
				scheduledMSSLFileReceivingVerification.alertNotification(variableMap);
			} else if ((ScheduledMSSLFileReceivingVerificationTask.CHECKSCHEDULEANDSHAREPLEX.getValue()).equals(operationPath)) {
				variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
			}
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					"Exception occured on ScheduledMSSLFileReceivingVerificationWIH.executeWorkItem(): "
							+ e.getMessage());
		}

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
