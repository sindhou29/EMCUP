package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.rmi.CORBA.Util;

import com.emc.settlement.backend.common.UtilityService;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.scheduled.ScheduledMEUCDataVerification;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledMEUCDataVerificationTask;

public class ScheduledMEUCDataVerificationWIH implements WorkItemHandler {

	private static final Logger logger = Logger.getLogger(ScheduledMEUCDataVerificationWIH.class);
	
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

		variableMap.put("cutoffTime",cutoffTime);
		variableMap.put("eveId",eveId);
		variableMap.put("pollInterval",pollInterval);
		variableMap.put("settlementDate",settlementDate);
		variableMap.put("valid",valid);
		variableMap.put("soapServiceUrl",soapServiceUrl);
		variableMap.put("settlementParam",settlementParam);
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);
		variableMap.put("businessDay", businessDay);

		try {
			ScheduledMEUCDataVerification sMeucDataVerification = ContextInitializer.getInstance().getContext().getBean(ScheduledMEUCDataVerification.class);
			UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);
			UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);
			
			if(operationPath.equals(ScheduledMEUCDataVerificationTask.CHECKBUSINESSDAY.getValue())) {
	            variableMap = sMeucDataVerification.checkBusinessDay(variableMap);
	        } else if(operationPath.equals(ScheduledMEUCDataVerificationTask.INITIALIZEVARIABLES.getValue())) {
	            variableMap = sMeucDataVerification.initializeVariables(variableMap);
	        } else if(operationPath.equals(ScheduledMEUCDataVerificationTask.GETTRADINGDATESRANGE.getValue())) {
				variableMap = utilityService.getTradingDatesRange(variableMap);
			} else if(operationPath.equals(ScheduledMEUCDataVerificationTask.CREATEEVENT.getValue())) {
	            variableMap = sMeucDataVerification.createEvent(variableMap);
	        } else if(operationPath.equals(ScheduledMEUCDataVerificationTask.CHECKMENU.getValue())) {
	        	variableMap = sMeucDataVerification.checkMEUC(variableMap);
	        } else if(operationPath.equals(ScheduledMEUCDataVerificationTask.ALERTNOTIFICATION.getValue())) {
	        	sMeucDataVerification.alertNotification(variableMap);
	        } else if(operationPath.equals(ScheduledMEUCDataVerificationTask.UPDATEEVENT.getValue())) {
	        	sMeucDataVerification.updateEvent(variableMap);
	        } else if (operationPath.equals(ScheduledMEUCDataVerificationTask.CHECKSCHEDULEANDSHAREPLEX.getValue())) {
				variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
			}
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					"Exception occured on ScheduledMEUCDataVerificationWIH.executeWorkItem(): "
							+ e.getMessage());
		}

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
