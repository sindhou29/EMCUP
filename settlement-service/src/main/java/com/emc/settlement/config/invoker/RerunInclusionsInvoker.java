package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.RerunInclusions;
import com.emc.settlement.backend.runrelated.RunValidations;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.service.task.runrelated.RerunInclusionsTask;
import com.emc.settlement.model.backend.service.task.runrelated.RunValidationsTask;

@Component
public class RerunInclusionsInvoker implements BaseInvoker {

	@Autowired
	private RerunInclusions rerunInclusions;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(RerunInclusionsTask.INCLUDERERUNSPRELIM.getValue())) {
			rerunInclusions.includeRerunsPrelim(variableMap);
		} else if (operationPath.equalsIgnoreCase(RerunInclusionsTask.INCLUDERERUNSFINAL.getValue())) {
			rerunInclusions.includeRerunsFinal(variableMap);
		}else if (operationPath.equalsIgnoreCase(RerunInclusionsTask.GETLASTINCLUDEDRERUNS.getValue())) {
			rerunInclusions.getLastIncludedReruns(variableMap);
		}
		return variableMap;
	}
}
