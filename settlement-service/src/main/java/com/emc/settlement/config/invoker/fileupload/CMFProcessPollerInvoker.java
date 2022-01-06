package com.emc.settlement.config.invoker.fileupload;

import java.util.Map;

import com.emc.settlement.backend.fileupload.CMFProcessPoller;
import com.emc.settlement.config.invoker.BaseInvoker;
import com.emc.settlement.model.backend.service.task.fileupload.CMFProcessPollerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CMFProcessPollerInvoker implements BaseInvoker {

	@Autowired
	private CMFProcessPoller cmfProcessPoller;

	@Override
	public Map<String, Object> invoke(Map<String, Object> variableMap, String operationPath) {
		if(operationPath.equals(CMFProcessPollerTask.CHECKUNRESPONSEPROCESSES.getValue())) {
			variableMap = cmfProcessPoller.checkUnresponseProcesses(variableMap);
		} else if(operationPath.equals(CMFProcessPollerTask.CHECKUNPROCESSEDCMF.getValue())) {
			variableMap = cmfProcessPoller.checkUnProcessedCMF(variableMap);
		} else if(operationPath.equals(CMFProcessPollerTask.GETSHAREPLEXMODE.getValue())) {
			variableMap = cmfProcessPoller.getShareplexMode(variableMap);
		}
		return variableMap;
	}
}
