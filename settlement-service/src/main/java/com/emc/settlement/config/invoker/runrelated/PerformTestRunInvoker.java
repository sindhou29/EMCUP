package com.emc.settlement.config.invoker.runrelated;

import java.util.Map;

import com.emc.settlement.backend.runrelated.SettlementMainProcess;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.runrelated.PerformTestRunTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PerformTestRunInvoker implements BaseInvoker {

	@Autowired
	private SettlementMainProcess settlementMainProcess;


	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(PerformTestRunTask.INITIALIZEVARIABLES.getValue())) {
			variableMap = settlementMainProcess.initializeVariables(variableMap);
		}
		return variableMap;
	}
}
