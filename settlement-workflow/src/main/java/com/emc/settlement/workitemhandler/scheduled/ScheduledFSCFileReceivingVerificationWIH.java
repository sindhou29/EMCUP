package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.emc.settlement.backend.scheduled.ScheduledFSCFileReceivingVerification;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledFSCFileReceivingVerificationTask;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ScheduledFSCFileReceivingVerificationWIH implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		Map<String, Object> variableMap = new HashMap<String, Object>();
		String operationPath = (String) workItem.getParameter("Operation");
		String eveId = (String) workItem.getParameter("eveId");
		Date settlementDate = (Date) workItem.getParameter("settlementDate");
		Boolean valid = (Boolean) workItem.getParameter("valid");

		variableMap.put("eveId", eveId);
		variableMap.put("settlementDate", settlementDate);
		variableMap.put("valid", valid);

		ScheduledFSCFileReceivingVerification scheduledFSCFileReceivingVerification = ContextInitializer.getInstance()
				.getContext().getBean(ScheduledFSCFileReceivingVerification.class);

		if ((ScheduledFSCFileReceivingVerificationTask.CREATEEVENT.getValue()).equals(operationPath)) {
			variableMap = scheduledFSCFileReceivingVerification.createEvent(variableMap);
		} else if ((ScheduledFSCFileReceivingVerificationTask.CHECKEBTEVENT.getValue()).equals(operationPath)) {
			variableMap = scheduledFSCFileReceivingVerification.checkEBTEvent(variableMap);
		} else if ((ScheduledFSCFileReceivingVerificationTask.UPDATEEVENT.getValue()).equals(operationPath)) {
			scheduledFSCFileReceivingVerification.updateEvent(variableMap);
		} else if ((ScheduledFSCFileReceivingVerificationTask.ALERTNOTIFICAION.getValue()).equals(operationPath)) {
			scheduledFSCFileReceivingVerification.alertNotification(variableMap);
		}

		manager.completeWorkItem(workItem.getId(), variableMap);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
