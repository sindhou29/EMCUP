package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.common.UtilityService;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.scheduled.ScheduledRiskExposureVerification;
import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledRiskExposureVerificationTask;

public class ScheduledRiskExposureVerificationWIH implements WorkItemHandler {

	private static final Logger logger = Logger.getLogger(ScheduledRiskExposureVerificationWIH.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		String eveId = (String) workItem.getParameter("eveId");
		Date runDate = (Date) workItem.getParameter("runDate");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		String settlementType = (String) workItem.getParameter("settlementType");
		String soapServiceUrl = (String) workItem.getParameter("soapServiceUrl");
		Boolean isSchedule = (Boolean) workItem.getParameter("isSchedule");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");
		String splexMode = (String) workItem.getParameter("splexMode");
		String scheduleFlag = (String) workItem.getParameter("scheduleFlag");

		variableMap.put("eveId", eveId);
		variableMap.put("runDate", runDate);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("settlementType", settlementType);
		variableMap.put("isSchedule", isSchedule);
		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("logPrefix", "[SH-SREV] ");
		variableMap.put("splexMode", splexMode);
		variableMap.put("scheduleFlag", scheduleFlag);
		
		ScheduledRiskExposureVerification scheduledRiskExposureVerification = ContextInitializer.getInstance()
				.getContext().getBean(ScheduledRiskExposureVerification.class);
		UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);
		UtilityFunctions utilityFunctions = ContextInitializer.getInstance().getContext().getBean(UtilityFunctions.class);

		try {
			if (operationPath.equals(ScheduledRiskExposureVerificationTask.INITIALIZEVARIABLES.getValue())) {
				variableMap = scheduledRiskExposureVerification.initializeVariables(variableMap);
			} else if (operationPath.equals(ScheduledRiskExposureVerificationTask.GETTRADINGDATESRANGE.getValue())) {
				variableMap = utilityService.getTradingDatesRange(variableMap);
			} else if (operationPath.equals(ScheduledRiskExposureVerificationTask.CREATEEVENT.getValue())) {
				variableMap = scheduledRiskExposureVerification.createEvent(variableMap);
			} else if (operationPath.equals(ScheduledRiskExposureVerificationTask.CREATERISKREPORT.getValue())) {
				scheduledRiskExposureVerification.createRiskReport(variableMap);
			} else if (operationPath.equals(ScheduledRiskExposureVerificationTask.UPDATEEVENT.getValue())) {
				scheduledRiskExposureVerification.updateEvent(variableMap);
			} else if (operationPath.equals(ScheduledRiskExposureVerificationTask.CHECKSCHEDULEANDSHAREPLEX.getValue())) {
				variableMap = utilityFunctions.checkScheduleAndShareplex(variableMap);
			}
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					"Exception occured on ScheduledRiskExposureVerificationWIH.executeWorkItem(): " + e.getMessage());
		}

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
