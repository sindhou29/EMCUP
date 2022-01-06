package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.ExecuteRun;
import com.emc.settlement.model.backend.service.task.runrelated.ExecuteRunTask;

@Component
public class ExecuteRunInvoker implements BaseInvoker {

	@Autowired
	private ExecuteRun executeRun;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(ExecuteRunTask.MANUALSETTLEMENTDATE.getValue())) {
			variableMap = executeRun.manualSettlementDate(variableMap);
		} 
		return variableMap;
	}
}
