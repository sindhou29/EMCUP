package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.common.UtilityService;
import com.emc.settlement.backend.scheduled.ScheduledClawbackFileReceivingVerification;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledClawbackFileReceivingVerificationTask;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledMSSLFileReceivingVerificationTask;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ScheduledClawbackFileReceivingVerificationWIH implements WorkItemHandler {
	
	private static final Logger logger = Logger.getLogger(ScheduledClawbackFileReceivingVerificationWIH.class);
	
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		String eveId = (String) workItem.getParameter("eveId");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		Boolean valid = (Boolean) workItem.getParameter("valid");
		String splexMode = (String) workItem.getParameter("splexMode");
		String scheduleFlag = (String) workItem.getParameter("scheduleFlag");
		Boolean businessDay = (Boolean) workItem.getParameter("businessDay");

		variableMap.put("eveId", eveId);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("valid", valid);
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);
		variableMap.put("businessDay", businessDay);

		ScheduledClawbackFileReceivingVerification scheduledClawbackFileReceivingVerification = ContextInitializer
				.getInstance().getContext().getBean(ScheduledClawbackFileReceivingVerification.class);

		UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);
		UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);

		try {
			if ((ScheduledClawbackFileReceivingVerificationTask.CHECKBUSINESSDAY.getValue()).equals(operationPath)) {
				variableMap = scheduledClawbackFileReceivingVerification.checkBusinessDay(variableMap);
			} else if ((ScheduledClawbackFileReceivingVerificationTask.INITIALIZEVARIABLES.getValue()).equals(operationPath)) {
				variableMap = scheduledClawbackFileReceivingVerification.initializeVariables(variableMap);
			} else if ((ScheduledClawbackFileReceivingVerificationTask.GETTRADINGDATESRANGE.getValue()
					.equals(operationPath))) {
				variableMap = utilityService.getTradingDatesRange(variableMap);
			} else if ((ScheduledClawbackFileReceivingVerificationTask.CREATEEVENT.getValue()).equals(operationPath)) {
				variableMap = scheduledClawbackFileReceivingVerification.createEvent(variableMap);
			} else if ((ScheduledClawbackFileReceivingVerificationTask.CHECKCLAWBACKFILE.getValue())
					.equals(operationPath)) {
				variableMap = scheduledClawbackFileReceivingVerification.checkClawbackFile(variableMap);
			} else if ((ScheduledClawbackFileReceivingVerificationTask.UPDATEEVENT.getValue()).equals(operationPath)) {
				scheduledClawbackFileReceivingVerification.updateEvent(variableMap);
			} else if ((ScheduledClawbackFileReceivingVerificationTask.ALERTNOTIFICATION.getValue())
					.equals(operationPath)) {
				scheduledClawbackFileReceivingVerification.alertNotification(variableMap);
			} else if ((ScheduledClawbackFileReceivingVerificationTask.CHECKSCHEDULEANDSHAREPLEX.getValue()).equals(operationPath)) {
				variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
			}
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					"Exception occured on ScheduledClawbackFileReceivingVerificationWIH.executeWorkItem(): "
							+ e.getMessage());
		}
		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
