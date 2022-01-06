package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.CMFEmailNotification;
import com.emc.settlement.config.invoker.BaseInvoker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CMFEmailNotificationInvoker implements BaseInvoker {

	@Autowired
	private CMFEmailNotification cmfEmailNotification;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		cmfEmailNotification.checkEmailStatus(variableMap);
		return variableMap;
	}
}
