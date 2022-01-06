package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.common.UtilityService;
import com.emc.settlement.backend.scheduled.ScheduledPendingForAuthorizationEMCPSOFeesDataExistence;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledEMCPSOBudgetDataVerificationTask;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceTask;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceWIH implements WorkItemHandler {
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
		
		variableMap.put("eveId",eveId);
		variableMap.put("settlementDate",settlementDate);
		variableMap.put("valid",valid);
		variableMap.put("soapServiceUrl",soapServiceUrl);
		variableMap.put("settlementParam",settlementParam);
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);
		variableMap.put("businessDay", businessDay);
		
		ScheduledPendingForAuthorizationEMCPSOFeesDataExistence scheduledMSSLFileReceivingVerification = ContextInitializer.getInstance().getContext().getBean(ScheduledPendingForAuthorizationEMCPSOFeesDataExistence.class);
		UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);
		UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);
		
		if((ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceTask.CHECKBUSINESSDAY.getValue()).equals(operationPath)) {
			variableMap = scheduledMSSLFileReceivingVerification.checkBusinessDay(variableMap);
		} else if((ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceTask.INITIALIZEVARIABLES.getValue()).equals(operationPath)) {
			try {
				variableMap = scheduledMSSLFileReceivingVerification.initializeVariables(variableMap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if((ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceTask.GETTRADINGDATESRANGE.getValue().equals(operationPath))) {
			variableMap = utilityService.getTradingDatesRange(variableMap);
		} else if(operationPath.equals(ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceTask.CREATEEVENT.getValue())){
			variableMap = scheduledMSSLFileReceivingVerification.createEvent(variableMap);
		}else if(operationPath.equals(ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceTask.CHECKAUTHFEESDATA.getValue())) {
			variableMap = scheduledMSSLFileReceivingVerification.checkAuthFeesData(variableMap);
		}else if(operationPath.equals(ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceTask.SENDALERTEMAIL.getValue())) {
			scheduledMSSLFileReceivingVerification.sendAlertEmail(variableMap);
		}else if(operationPath.equals(ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceTask.UPDATEEVENT.getValue())) {
			scheduledMSSLFileReceivingVerification.updateEvent(variableMap);
		} else if (operationPath.equals(ScheduledPendingForAuthorizationEMCPSOFeesDataExistenceTask.CHECKSCHEDULEANDSHAREPLEX.getValue())) {
			variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
		}
				
		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
