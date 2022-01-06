package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.scheduled.ScheduledVestingContractFileReceivingVerification;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledVestingContractFileReceivingVerificationTask;

public class ScheduledVestingContractFileReceivingVerificationWIH implements WorkItemHandler {
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		String soapServiceUrl = (String) workItem.getParameter("soapServiceUrl");
		String splex_mode = (String) workItem.getParameter("splex_mode");
		String eveId = (String) workItem.getParameter("eveId");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		Boolean valid = (Boolean) workItem.getParameter("valid");
		String splexMode = (String) workItem.getParameter("splexMode");
		String scheduleFlag = (String) workItem.getParameter("scheduleFlag");

		variableMap.put("soapServiceUrl",soapServiceUrl);
		variableMap.put("splex_mode",splex_mode);
		variableMap.put("eveId",eveId);
		variableMap.put("settlementDate",settlementDate);
		variableMap.put("valid",valid);
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);

		ScheduledVestingContractFileReceivingVerification scheduledVestingContractFileReceivingVerification  = ContextInitializer.getInstance().getContext().getBean(ScheduledVestingContractFileReceivingVerification.class);
		UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);
		
		if(operationPath.equals(ScheduledVestingContractFileReceivingVerificationTask.INITIALIZEVARIABLES.getValue())){
			variableMap=scheduledVestingContractFileReceivingVerification.initializeVariables(variableMap);
		}else if(operationPath.equals(ScheduledVestingContractFileReceivingVerificationTask.CHECKCOUNT.getValue())){
			variableMap=scheduledVestingContractFileReceivingVerification.checkCount(variableMap);
		}else if(operationPath.equals(ScheduledVestingContractFileReceivingVerificationTask.CREATEEVENT.getValue())){
			variableMap=scheduledVestingContractFileReceivingVerification.createEvent(variableMap);
		}else if(operationPath.equals(ScheduledVestingContractFileReceivingVerificationTask.CHECKEBTEVENT.getValue())) {
			variableMap=scheduledVestingContractFileReceivingVerification.checkEBTEvent(variableMap);
		}else if(operationPath.equals(ScheduledVestingContractFileReceivingVerificationTask.ALERTNOTIFICATION.getValue())) {
			scheduledVestingContractFileReceivingVerification.alertNotification(variableMap);
		}else if(operationPath.equals(ScheduledVestingContractFileReceivingVerificationTask.UPDATEEVENT.getValue())) {
			scheduledVestingContractFileReceivingVerification.updateEvent(variableMap);
		}else if (operationPath.equals(ScheduledVestingContractFileReceivingVerificationTask.CHECKSCHEDULEANDSHAREPLEX.getValue())) {
			variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
		}	

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
