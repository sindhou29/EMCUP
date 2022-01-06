package com.emc.settlement.config.invoker;

import java.util.Map;

public interface BaseInvoker {

	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath);
}
