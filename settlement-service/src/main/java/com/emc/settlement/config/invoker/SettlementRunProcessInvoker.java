package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.SettlementRunProcess;
import com.emc.settlement.model.backend.service.task.runrelated.SettlementRunProcessTask;

@Component
public class SettlementRunProcessInvoker implements BaseInvoker {

	@Autowired
	private SettlementRunProcess settlementRunProcess;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(SettlementRunProcessTask.STARTLOGGINGMESSAGES.getValue())) {
			variableMap = settlementRunProcess.startLoggingMessages(variableMap);
		} else if (operationPath.equalsIgnoreCase(SettlementRunProcessTask.REGISTEREVENTFORRUN.getValue())) {
			variableMap = settlementRunProcess.registerEventForRun(variableMap);
		}else if (operationPath.equalsIgnoreCase(SettlementRunProcessTask.PREREQUISITECOMPLETE.getValue())) {
			variableMap = settlementRunProcess.preRequisiteComplete(variableMap);
		}else if (operationPath.equalsIgnoreCase(SettlementRunProcessTask.PREPARERUNPACKAGES.getValue())) {
			variableMap = settlementRunProcess.prepareRunPackages(variableMap);
		}else if (operationPath.equalsIgnoreCase(SettlementRunProcessTask.CREATERUNENTRY.getValue())) {
			variableMap = settlementRunProcess.createRunEntry(variableMap);
		}else if (operationPath.equalsIgnoreCase(SettlementRunProcessTask.INVOKERULES.getValue())) {
			variableMap = settlementRunProcess.invokeRules(variableMap);
		}else if (operationPath.equalsIgnoreCase(SettlementRunProcessTask.EXCEPTIONNOTIFICATION.getValue())) {
			settlementRunProcess.exceptionNotification(variableMap);
		}else if (operationPath.equalsIgnoreCase(SettlementRunProcessTask.UPDATETESTRUNSTATUS.getValue())) {
			settlementRunProcess.updateTestRunStatus(variableMap);
		}
		return variableMap;
	}
}
