package com.emc.settlement_rules_process;

import javax.sql.DataSource;

import com.emc.sett.common.AbstractSettlementData;
import com.emc.sett.common.IRuleflowInterface;
import com.emc.sett.utils.DBUtility;
import com.emc.settlement.model.backend.pojo.AlertNotification;
import com.emc.settlement.model.backend.pojo.SettRunPkg;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;

public class WriteDataTaskHandler {

	public void executeWorkItem(org.kie.api.runtime.process.ProcessContext ctx) throws Exception {

        IRuleflowInterface irule = (IRuleflowInterface)ctx.getVariable("irule");
        AlertNotification alert = (AlertNotification)ctx.getVariable("alert");
        SettRunPkg runPackage = (SettRunPkg)ctx.getVariable("runPackage");
        SettlementRunParams runParams = (SettlementRunParams)ctx.getVariable("runParams");
        DataSource nems = (DataSource)ctx.getVariable("nems");
        AbstractSettlementData settData = (AbstractSettlementData)ctx.getVariable("settData");

        AlertNotification sender = irule.storeData( "", runPackage, runParams, alert, settData, nems );
        
        if (sender != null && sender.getNotificationReady() != null && sender.getNotificationReady() == true) {
        	DBUtility.sendEmail(nems, sender);
        }
    }
}
