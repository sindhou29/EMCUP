package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.SettlementMainProcess;
import com.emc.settlement.backend.scheduled.ScheduledRiskExposureVerification;
import com.emc.settlement.model.backend.service.task.runrelated.FinalAuthoriseSettlementRunTask;
import com.emc.settlement.model.backend.service.task.runrelated.SettlementMainProcessTask;

@Component
public class SettlementMainProcessInvoker implements BaseInvoker {

	@Autowired
	private SettlementMainProcess settlementMainProcess;

	@Autowired
	private ScheduledRiskExposureVerification scheduledRiskExposureVerification;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(SettlementMainProcessTask.INITIALIZEVARIABLES.getValue())) {
			variableMap = settlementMainProcess.initializeVariables(variableMap);
		} else if (operationPath.equalsIgnoreCase(SettlementMainProcessTask.CREATESCHEDULEEVENT.getValue())) {
			variableMap = settlementMainProcess.createScheduleEvent(variableMap);
		}else if (operationPath.equalsIgnoreCase(SettlementMainProcessTask.CREATERISKREPORT.getValue())) {
			scheduledRiskExposureVerification.createRiskReport(variableMap);
		}else if (operationPath.equalsIgnoreCase(SettlementMainProcessTask.STARTDAILYPRUDENTIALPROCESS.getValue())) {
			settlementMainProcess.startDailyPrudentialProcess(variableMap);
		}else if (operationPath.equalsIgnoreCase(SettlementMainProcessTask.UPDATEEVENTSTATUS.getValue())) {
			settlementMainProcess.updateEventStatus(variableMap);
		}else if (operationPath.equalsIgnoreCase(SettlementMainProcessTask.EXCEPTIONHANDLER.getValue())) {
			settlementMainProcess.exceptionHandler(variableMap);
		}else if (operationPath.equalsIgnoreCase(SettlementMainProcessTask.GENERATEADFFILE.getValue())) {
			settlementMainProcess.generateADFFile(variableMap);
		}
		return variableMap;
	}
}
