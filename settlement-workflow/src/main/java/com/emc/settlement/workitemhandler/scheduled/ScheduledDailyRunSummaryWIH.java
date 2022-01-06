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

import com.emc.settlement.backend.scheduled.ScheduledDailyRunSummary;
import com.emc.settlement.backend.scheduled.ScheduledRiskExposureVerification;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledDailyRunSummaryTask;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledRiskExposureVerificationTask;

public class ScheduledDailyRunSummaryWIH implements WorkItemHandler {

	private static final Logger logger = Logger.getLogger(ScheduledDailyRunSummaryWIH.class);

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		String soapServiceUrl = (String) workItem.getParameter("soapServiceUrl");
		SettlementRunParams settlementParam = (SettlementRunParams) workItem.getParameter("settlementParam");

		variableMap.put("soapServiceUrl", soapServiceUrl);
		variableMap.put("settlementParam", settlementParam);
		variableMap.put("logPrefix", "[SH-DRS] ");
		ScheduledDailyRunSummary scheduledDailyRunSummary = ContextInitializer.getInstance()
				.getContext().getBean(ScheduledDailyRunSummary.class);
		UtilityService utilityService = ContextInitializer.getInstance().getContext().getBean(UtilityService.class);

		try {
			if (operationPath.equals(ScheduledDailyRunSummaryTask.INITIALIZEVARIABLES.getValue())) {
				variableMap = scheduledDailyRunSummary.initializeVariables(variableMap);
			} else if (operationPath.equals(ScheduledDailyRunSummaryTask.GETTRADINGDATESRANGE.getValue())) {
				variableMap = utilityService.getTradingDatesRange(variableMap);
			} else if (operationPath.equals(ScheduledDailyRunSummaryTask.CHECKTODAYRUN.getValue())) {
				scheduledDailyRunSummary.checkTodayRun(variableMap);
			}
		} catch (Exception e) {
			logger.log(Priority.ERROR,
					"Exception occured on ScheduledDailyRunSummaryWIH.executeWorkItem(): " + e.getMessage());
		}

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
