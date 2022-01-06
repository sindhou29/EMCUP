package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.common.UtilityService;
import com.emc.settlement.backend.scheduled.ScheduledVestingContractDataVerification;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledEMCPSOBudgetDataVerificationTask;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledVestingContractDataVerificationTask;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ScheduledVestingContractDataVerificationWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		Date cutoffTime = (Date) workItem.getParameter("cutoffTime");
		String eveId = (String) workItem.getParameter("eveId");
		Boolean exceedCutoffTime = (Boolean) workItem.getParameter("exceedCutoffTime");
		PeriodNumber pd = (PeriodNumber) workItem.getParameter("pd");
		Integer pollInterval = (Integer) workItem.getParameter("pollInterval");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		Boolean valid = (Boolean) workItem.getParameter("valid");
		String soapServiceUrl = (String) workItem.getParameter("soapServiceUrl");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		String splexMode = (String) workItem.getParameter("splexMode");
		String scheduleFlag = (String) workItem.getParameter("scheduleFlag");
		Boolean businessDay = (Boolean) workItem.getParameter("businessDay");

		variableMap.put("cutoffTime",cutoffTime);
		variableMap.put("eveId",eveId);
		variableMap.put("exceedCutoffTime",exceedCutoffTime);
		variableMap.put("pd",pd);
		variableMap.put("pollInterval",pollInterval);
		variableMap.put("settlementDate",settlementDate);
		variableMap.put("valid",valid);
		variableMap.put("soapServiceUrl",soapServiceUrl);
		variableMap.put("settlementParam",settlementParam);
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);
		variableMap.put("businessDay", businessDay);

		ScheduledVestingContractDataVerification scheduledVestingContractDataVerification  = ContextInitializer.getInstance().getContext().getBean(ScheduledVestingContractDataVerification.class);
		UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);
		UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);
		
		if(operationPath.equals(ScheduledVestingContractDataVerificationTask.CHECKBUSINESSDAY.getValue())) {
			variableMap = scheduledVestingContractDataVerification.checkBusinessDay(variableMap);
		} else if(operationPath.equals(ScheduledVestingContractDataVerificationTask.INITIALIZEVARIABLES.getValue())) {
			variableMap = scheduledVestingContractDataVerification.initializeVariables(variableMap);
		} else if((ScheduledVestingContractDataVerificationTask.GETTRADINGDATESRANGE.getValue().equals(operationPath))) {
			variableMap = utilityService.getTradingDatesRange(variableMap);
		} else if(operationPath.equals(ScheduledVestingContractDataVerificationTask.INITIALIZESETTLEMENTDATE.getValue())) {
			variableMap = scheduledVestingContractDataVerification.initializeSettlementDate(variableMap);
		} else if(operationPath.equals(ScheduledVestingContractDataVerificationTask.CHECKCUTOFFTIME.getValue())) {
			variableMap = scheduledVestingContractDataVerification.checkCutoffTime(variableMap);
		} else if(operationPath.equals(ScheduledVestingContractDataVerificationTask.SENDNOTIFICATION.getValue())) {
			scheduledVestingContractDataVerification.alertNotification(variableMap);
		} else if(operationPath.equals(ScheduledVestingContractDataVerificationTask.UPDATEEVENT.getValue())) {
			scheduledVestingContractDataVerification.updateEvent(variableMap);
		} else if (operationPath.equals(ScheduledVestingContractDataVerificationTask.CHECKSCHEDULEANDSHAREPLEX.getValue())) {
			variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
		}

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
