package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.common.UtilityService;
import com.emc.settlement.backend.scheduled.ScheduledRerunAccountsVerification;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledEMCPSOBudgetDataVerificationTask;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledRerunAccountsVerificationTask;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ScheduledRerunAccountsVerificationWIH implements WorkItemHandler {

	private static final Logger logger = Logger.getLogger(ScheduledRerunAccountsVerificationWIH.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		Date cutoffTime = (Date) workItem.getParameter("cutoffTime");
		String errAlert = (String) workItem.getParameter("errAlert");
		String eveId = (String) workItem.getParameter("eveId");
		Integer pollInterval = (Integer) workItem.getParameter("pollInterval");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		String standingVersion = (String) workItem.getParameter("standingVersion");
		Boolean valid = (Boolean) workItem.getParameter("valid");
		String soapServiceUrl = (String) workItem.getParameter("soapServiceUrl");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		String splexMode = (String) workItem.getParameter("splexMode");
		String scheduleFlag = (String) workItem.getParameter("scheduleFlag");
		Boolean businessDay = (Boolean) workItem.getParameter("businessDay");
		
		variableMap.put("cutoffTime", cutoffTime);
		variableMap.put("errAlert", errAlert);
		variableMap.put("eveId", eveId);
		variableMap.put("pollInterval", pollInterval);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("standingVersion", standingVersion);
		variableMap.put("valid", valid);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);
		variableMap.put("businessDay", businessDay);

		ScheduledRerunAccountsVerification scheduledRerunAccountsVerification = ContextInitializer.getInstance()
				.getContext().getBean(ScheduledRerunAccountsVerification.class);
		UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);
		UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);
		
		try {
			if (operationPath.equals(ScheduledRerunAccountsVerificationTask.CHECKBUSINESSDAY.getValue())) {
				variableMap = scheduledRerunAccountsVerification.checkBusinessDay(variableMap);
			} else if (operationPath.equals(ScheduledRerunAccountsVerificationTask.INITIALIZEVARIABLES.getValue())) {
				variableMap = scheduledRerunAccountsVerification.initializeVariables(variableMap);
			} else if((ScheduledRerunAccountsVerificationTask.GETTRADINGDATESRANGE.getValue().equals(operationPath))) {
				variableMap = utilityService.getTradingDatesRange(variableMap);
			} else if (operationPath.equals(ScheduledRerunAccountsVerificationTask.PREPAREDATA.getValue())) {
				variableMap = scheduledRerunAccountsVerification.prepareData(variableMap);
			} else if (operationPath
					.equals(ScheduledRerunAccountsVerificationTask.VERIFYACCOUNTSEXISTENCE.getValue())) {
				variableMap = scheduledRerunAccountsVerification.verifySettAccountsInRerun(variableMap);
			} else if (operationPath.equals(ScheduledRerunAccountsVerificationTask.PREPAREALERTINFO.getValue())) {
				scheduledRerunAccountsVerification.prepareAlertInfo(variableMap);
			} else if (operationPath.equals(ScheduledRerunAccountsVerificationTask.UPDATEEVENT.getValue())) {
				scheduledRerunAccountsVerification.updateEvent(variableMap);
			} else if (operationPath.equals(ScheduledRerunAccountsVerificationTask.CHECKSCHEDULEANDSHAREPLEX.getValue())) {
				variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
			}
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					"Exception occured on ScheduledRerunAccountsVerificationWIH.executeWorkItem(): " + e.getMessage());
		}

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
