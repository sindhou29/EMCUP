package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.common.UtilityService;
import com.emc.settlement.backend.scheduled.ScheduledMSSLDataVerification;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledEMCPSOBudgetDataVerificationTask;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledMSSLDataVerificationTask;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ScheduledMSSLDataVerificationWIH implements WorkItemHandler {

	private static final Logger logger = Logger.getLogger(ScheduledMSSLDataVerificationWIH.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		Date cutoffTime = (Date) workItem.getParameter("cutoffTime");
		String eveId = (String) workItem.getParameter("eveId");
		String msslQuantityVersion = (String) workItem.getParameter("msslQuantityVersion");
		PeriodNumber pd = (PeriodNumber) workItem.getParameter("pd");
		Integer pollInterval = (Integer) workItem.getParameter("pollInterval");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		String standingVersion = (String) workItem.getParameter("standingVersion");
		Boolean valid = (Boolean) workItem.getParameter("valid");
		String soapServiceUrl = (String) workItem.getParameter("soapServiceUrl");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		SettRunPkg settRunPackage = (SettRunPkg) workItem.getParameter("settRunPackage");
		String result = (String) workItem.getParameter("result");
		Boolean exceedCutoffTime = (Boolean) workItem.getParameter("exceedCutoffTime");
		String splexMode = (String) workItem.getParameter("splexMode");
		String scheduleFlag = (String) workItem.getParameter("scheduleFlag");
		Boolean businessDay = (Boolean) workItem.getParameter("businessDay");

		variableMap.put("cutoffTime", cutoffTime);
		variableMap.put("eveId", eveId);
		variableMap.put("msslQuantityVersion", msslQuantityVersion);
		variableMap.put("pd", pd);
		variableMap.put("pollInterval", pollInterval);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("standingVersion", standingVersion);
		variableMap.put("valid", valid);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("settRunPackage", settRunPackage);
		variableMap.put("result", result);
		variableMap.put("exceedCutoffTime", exceedCutoffTime);
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);
		variableMap.put("businessDay", businessDay);

		ScheduledMSSLDataVerification scheduledMSSLDataVerification = ContextInitializer.getInstance().getContext()
				.getBean(ScheduledMSSLDataVerification.class);
		UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);
		UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);
		
		try {
			if(operationPath.equals(ScheduledMSSLDataVerificationTask.CHECKBUSINESSDAY.getValue())) {
				variableMap = scheduledMSSLDataVerification.checkBusinessDay(variableMap);
			} else if ((ScheduledMSSLDataVerificationTask.INITIALIZEVARIABLES.getValue()).equals(operationPath)) {
				variableMap = scheduledMSSLDataVerification.initializeVariables(variableMap);
			} else if((ScheduledMSSLDataVerificationTask.GETTRADINGDATESRANGE.getValue().equals(operationPath))) {
				variableMap = utilityService.getTradingDatesRange(variableMap);
			} else if ((ScheduledMSSLDataVerificationTask.PREPAREDATA.getValue()).equals(operationPath)) {
				variableMap = scheduledMSSLDataVerification.prepareData(variableMap);
			} else if ((ScheduledMSSLDataVerificationTask.UPDATEEVENT.getValue()).equals(operationPath)) {
				scheduledMSSLDataVerification.updateEvent(variableMap);
			} else if ((ScheduledMSSLDataVerificationTask.CHECKMSSLVERSION.getValue()).equals(operationPath)) {
				variableMap = scheduledMSSLDataVerification.checkMSSLVersion(variableMap);
			} else if ((ScheduledMSSLDataVerificationTask.SENDALERTEMAIL.getValue()).equals(operationPath)) {
				scheduledMSSLDataVerification.sendAlertEmail(variableMap);
			} else if ((ScheduledMSSLDataVerificationTask.CHECKSCHEDULEANDSHAREPLEX.getValue()).equals(operationPath)) {
				variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
			}
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					"Exception occured on ScheduledMSSLDataVerificationWIH.executeWorkItem(): " + e.getMessage());
		}

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
