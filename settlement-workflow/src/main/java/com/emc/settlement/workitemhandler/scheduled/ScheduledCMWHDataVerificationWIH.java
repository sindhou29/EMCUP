package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.common.UtilityService;
import com.emc.settlement.backend.scheduled.ScheduledCMWHDataVerification;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledBilateralDataVerificationTask;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledCMWHDataVerificationTask;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ScheduledCMWHDataVerificationWIH implements WorkItemHandler {

	private static final Logger logger = Logger.getLogger(ScheduledCMWHDataVerificationWIH.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		Date cutoffTime = (Date) workItem.getParameter("cutoffTime");
		String eveId = (String) workItem.getParameter("eveId");
		Integer pollInterval = (Integer) workItem.getParameter("pollInterval");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		Boolean valid = (Boolean) workItem.getParameter("valid");
		String soapServiceUrl = (String) workItem.getParameter("soapServiceUrl");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		String splexMode = (String) workItem.getParameter("splexMode");
		String scheduleFlag = (String) workItem.getParameter("scheduleFlag");
		Boolean businessDay = (Boolean) workItem.getParameter("businessDay");

		variableMap.put("cutoffTime", cutoffTime);
		variableMap.put("eveId", eveId);
		variableMap.put("pollInterval", pollInterval);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("valid", valid);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);
		variableMap.put("businessDay", businessDay);

		ScheduledCMWHDataVerification scheduledCMWHDataVerificationMain = ContextInitializer.getInstance().getContext()
				.getBean(ScheduledCMWHDataVerification.class);
		UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);
		UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);
		try {
			if ((ScheduledCMWHDataVerificationTask.CHECKBUSINESSDAY.getValue()).equals(operationPath)) {
				variableMap = scheduledCMWHDataVerificationMain.checkBusinessDay(variableMap);
			} else if ((ScheduledCMWHDataVerificationTask.INITIALIZEVARIABLES.getValue()).equals(operationPath)) {
				variableMap = scheduledCMWHDataVerificationMain.initializeVariables(variableMap);
			} else if((ScheduledCMWHDataVerificationTask.GETTRADINGDATESRANGE.getValue().equals(operationPath))) {
				variableMap = utilityService.getTradingDatesRange(variableMap);
			} else if ((ScheduledCMWHDataVerificationTask.CREATEEVENT.getValue()).equals(operationPath)) {
				variableMap = scheduledCMWHDataVerificationMain.createEvent(variableMap);
			} else if ((ScheduledCMWHDataVerificationTask.UPDATEEVENT.getValue()).equals(operationPath)) {
				scheduledCMWHDataVerificationMain.updateEvent(variableMap);
			} else if ((ScheduledCMWHDataVerificationTask.CHECKCMWH.getValue()).equals(operationPath)) {
				variableMap = scheduledCMWHDataVerificationMain.checkCMWH(variableMap);
			} else if ((ScheduledCMWHDataVerificationTask.ALERTNOTIFICATION.getValue()).equals(operationPath)) {
				scheduledCMWHDataVerificationMain.alertNotification(variableMap);
			} else if ((ScheduledCMWHDataVerificationTask.CHECKSCHEDULEANDSHAREPLEX.getValue()).equals(operationPath)) {
				variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
			} 
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					"Exception occured on ScheduledCMWHDataVerificationWIH.executeWorkItem(): " + e.getMessage());
		}

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
