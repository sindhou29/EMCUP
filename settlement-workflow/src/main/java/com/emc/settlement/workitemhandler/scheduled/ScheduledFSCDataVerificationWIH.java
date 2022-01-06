/**
 * 
 */
package com.emc.settlement.workitemhandler.scheduled;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.emc.settlement.backend.scheduled.ScheduledFSCDataVerification;
import com.emc.settlement.config.ContextInitializer;
import com.emc.settlement.model.backend.pojo.PeriodNumber;
import com.emc.settlement.model.backend.service.task.scheduled.ScheduledFSCDataVerificationTask;
/**
 * @author THC
 *
 */
public class ScheduledFSCDataVerificationWIH implements WorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

	    String operationPath = (String) workItem.getParameter("Operation");
	    Date cutoffTime = (Date)workItem.getParameter("cutoffTime");
    	Integer pollInterval = (Integer)workItem.getParameter("pollInterval");
    	Date settlementDate = (Date)workItem.getParameter("settlementDate");
    	String eveId = (String)workItem.getParameter("eveId");
    	PeriodNumber pd = (PeriodNumber)workItem.getParameter("pd");
    	String soapServiceUrl = (String)workItem.getParameter("soapServiceUrl");
    	String breachAccMsg = (String)workItem.getParameter("breachAccMsg");
    	Boolean valid = (Boolean)workItem.getParameter("valid");
    	Boolean exceedCutoffTime = (Boolean)workItem.getParameter("exceedCutoffTime");

        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("cutoffTime", cutoffTime);
        variableMap.put("pollInterval", pollInterval);
        variableMap.put("settlementDate", settlementDate);
        variableMap.put("eveId", eveId);
        variableMap.put("pd", pd);
        variableMap.put("soapServiceUrl", soapServiceUrl);
        variableMap.put("breachAccMsg", breachAccMsg);
        variableMap.put("valid", valid);
        variableMap.put("exceedCutoffTime", exceedCutoffTime);

        ScheduledFSCDataVerification sFsdDataVerification = ContextInitializer.getInstance().getContext().getBean(ScheduledFSCDataVerification.class);
        if(operationPath.equals(ScheduledFSCDataVerificationTask.INITIALIZESETTLEMENTDATE.getValue())) {
           variableMap = sFsdDataVerification.initializeSettlementDate(variableMap);
        } else if(operationPath.equals(ScheduledFSCDataVerificationTask.SENDNOTIFICATION.getValue())) {
            sFsdDataVerification.sendNotification(variableMap);
        } else if(operationPath.equals(ScheduledFSCDataVerificationTask.UPDATEEVENT.getValue())) {
            sFsdDataVerification.updateEvent(variableMap);
        } else if(operationPath.equals(ScheduledFSCDataVerificationTask.CHECKCUTOFFTIME.getValue())) {
            variableMap = sFsdDataVerification.checkCutoffTime(variableMap);
        }

        manager.completeWorkItem(workItem.getId(), variableMap);
    }

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		manager.abortWorkItem(workItem.getId());
	}
}
