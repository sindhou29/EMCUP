package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.common.UtilityService;
import com.emc.settlement.backend.scheduled.ScheduledRegenerationOfMCRData;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledEMCPSOBudgetDataVerificationTask;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledRegenerationOfMCRDataTask;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ScheduledRegenerationOfMCRDataWIH implements WorkItemHandler {

	private static final Logger logger = Logger.getLogger(ScheduledRegenerationOfMCRDataWIH.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		Boolean businessDay = (Boolean) workItem.getParameter("businessDay");
		String eveId = (String) workItem.getParameter("eveId");
		Integer finish = (Integer) workItem.getParameter("finish");
		PeriodNumber pd = (PeriodNumber) workItem.getParameter("pd");
		SettRunPkg settRunPackage = (SettRunPkg) workItem.getParameter("settRunPackage");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		String soapServiceUrl = (String) workItem.getParameter("soapServiceUrl");
		String splexMode = (String) workItem.getParameter("splexMode");
		String scheduleFlag = (String) workItem.getParameter("scheduleFlag");

		variableMap.put("businessDay", businessDay);
		variableMap.put("eveId", eveId);
		variableMap.put("finish", finish);
		variableMap.put("pd", pd);
		variableMap.put("settRunPackage", settRunPackage);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);

		ScheduledRegenerationOfMCRData scheduledRegenerationOfMCRData = ContextInitializer.getInstance().getContext()
				.getBean(ScheduledRegenerationOfMCRData.class);
		UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);
		UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);
		
		try {
			if(operationPath.equals(ScheduledRegenerationOfMCRDataTask.CHECKBUSINESSDAY.getValue())) {
				variableMap = scheduledRegenerationOfMCRData.checkBusinessDay(variableMap);
			} else if (operationPath.equals(ScheduledRegenerationOfMCRDataTask.INITIALIZEVARIABLES.getValue())) {
				variableMap = scheduledRegenerationOfMCRData.initializeVariables(variableMap);
			} else if((ScheduledRegenerationOfMCRDataTask.GETTRADINGDATESRANGE.getValue().equals(operationPath))) {
				variableMap = utilityService.getTradingDatesRange(variableMap);
			} else if (operationPath.equals(ScheduledRegenerationOfMCRDataTask.INITIALIZE.getValue())) {
				variableMap = scheduledRegenerationOfMCRData.initialize(variableMap);
			} else if ((operationPath.equals(ScheduledRegenerationOfMCRDataTask.CREATEMCEDATAPACKAGES.getValue()))) {
				variableMap = scheduledRegenerationOfMCRData.createMCEDataPackages(variableMap);
			} else if (operationPath.equals(ScheduledRegenerationOfMCRDataTask.NEXTDAY.getValue())) {
				variableMap = scheduledRegenerationOfMCRData.nextDay(variableMap);
			} else if (operationPath.equals(ScheduledRegenerationOfMCRDataTask.CHECKSCHEDULEANDSHAREPLEX.getValue())) {
				variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
			}
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					"Exception occured on ScheduledRegenerationOfMCRDataWIH.executeWorkItem(): " + e.getMessage());
		}
		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
