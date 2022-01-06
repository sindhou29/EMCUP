package com.emc.settlement.config.invoker;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.settlement.backend.runrelated.SetPackageAuthorization;
import com.emc.settlement.model.backend.service.task.runrelated.SetPackageAuthorizationTask;

@Component
public class SetPackageAuthorizationInvoker implements BaseInvoker {

	@Autowired
	private SetPackageAuthorization setPackageAuthorization;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if (operationPath.equalsIgnoreCase(SetPackageAuthorizationTask.SETTPKGAUTHORIZATION.getValue())) {
			setPackageAuthorization.settPkgAuthorization(variableMap);
		}
		return variableMap;
	}
}
