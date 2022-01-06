package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.common.UtilityService;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.backend.scheduled.ScheduledBilateralDataVerification;
import com.emc.settlement.common.UtilityFunctions;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledBilateralDataVerificationTask;

public class ScheduledBilateralDataVerificationWIH implements WorkItemHandler {

	private static final Logger logger = Logger.getLogger(ScheduledBilateralDataVerificationWIH.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		String operationPath = (String) workItem.getParameter("Operation");
		Date cutoffTime = (Date) workItem.getParameter("cutoffTime");
		Integer pollInterval = (Integer) workItem.getParameter("pollInterval");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		String eveId = (String) workItem.getParameter("eveId");
		PeriodNumber pd = (PeriodNumber) workItem.getParameter("pd");
		String soapServiceUrl = (String) workItem.getParameter("soapServiceUrl");
		Boolean valid = (Boolean) workItem.getParameter("valid");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		String splexMode = (String) workItem.getParameter("splexMode");
		String scheduleFlag = (String) workItem.getParameter("scheduleFlag");
		Boolean businessDay = (Boolean) workItem.getParameter("businessDay");

		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("cutoffTime", cutoffTime);
		variableMap.put("pollInterval", pollInterval);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("pd", pd);
		variableMap.put("eveId", eveId);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("valid", valid);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);
		variableMap.put("businessDay", businessDay);
		
		try {
			ScheduledBilateralDataVerification scheduledBilateralDataVerification = ContextInitializer.getInstance()
					.getContext().getBean(ScheduledBilateralDataVerification.class);
			UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);
			UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);
			
			if ((ScheduledBilateralDataVerificationTask.CHECKBUSINESSDAY.getValue()).equals(operationPath)) {
				variableMap = scheduledBilateralDataVerification.checkBusinessDay(variableMap);
			} else if ((ScheduledBilateralDataVerificationTask.INITIALIZEVARIABLES.getValue()).equals(operationPath)) {
				variableMap = scheduledBilateralDataVerification.initializeVariables(variableMap);
			} else if(ScheduledBilateralDataVerificationTask.GETTRADINGDATESRANGE.getValue().equals(operationPath)) {
				variableMap = utilityService.getTradingDatesRange(variableMap);
			} else if ((ScheduledBilateralDataVerificationTask.CHECK.getValue()).equals(operationPath)) {
				scheduledBilateralDataVerification.check();
			} else if ((ScheduledBilateralDataVerificationTask.CREATEEVENT.getValue()).equals(operationPath)) {
				variableMap = scheduledBilateralDataVerification.createEvent(variableMap);
			} else if ((ScheduledBilateralDataVerificationTask.UPDATEEVENT.getValue()).equals(operationPath)) {
				scheduledBilateralDataVerification.updateEvent(variableMap);
			} else if ((ScheduledBilateralDataVerificationTask.SENDALERTEMAIL.getValue()).equals(operationPath)) {
				scheduledBilateralDataVerification.sendAlertEmail(variableMap);
			} else if ((ScheduledBilateralDataVerificationTask.CHECKSCHEDULEANDSHAREPLEX.getValue()).equals(operationPath)) {
				variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
			}
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					"Exception occured on ScheduledBilateralDataVerificationWIH.executeWorkItem(): "
							+ e.getMessage());
		}

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
