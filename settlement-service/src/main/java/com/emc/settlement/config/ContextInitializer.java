package com.emc.settlement.config;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.emc.settlement.config.invoker.AccountingInterfaceInvoker;
import com.emc.settlement.config.invoker.ApproveSettlementRunInvoker;
import com.emc.settlement.config.invoker.CaptureFSCPenaltyQuantityInvoker;
import com.emc.settlement.config.invoker.CashFlowReconciliationInvoker;
import com.emc.settlement.config.invoker.ExecuteRunInvoker;
import com.emc.settlement.config.invoker.FinalAuthoriseSettlementRunInvoker;
import com.emc.settlement.config.invoker.ForceDenySettlementRunInvoker;
import com.emc.settlement.config.invoker.LoadPendingCMFFilesInvoker;
import com.emc.settlement.config.invoker.RerunInclusionsInvoker;
import com.emc.settlement.config.invoker.RunValidationsInvoker;
import com.emc.settlement.config.invoker.fileupload.SaveMSSLMeteringFileInvoker;
import com.emc.settlement.config.invoker.SetPackageAuthorizationInvoker;
import com.emc.settlement.config.invoker.SettlementMainProcessInvoker;
import com.emc.settlement.config.invoker.SettlementRunProcessInvoker;
import com.emc.settlement.config.invoker.fileupload.VerifyAndLoadClawbackQuantitiesDataInvoker;
import com.emc.settlement.config.invoker.fileupload.VerifyAndLoadMSSLMeteringDataInvoker;

import com.emc.settlement.config.invoker.fileupload.BilateralContractUploadMainInvoker;
import com.emc.settlement.config.invoker.fileupload.BilateralContractValidateAndUploadDataInvoker;
import com.emc.settlement.config.invoker.fileupload.CMFEmailNotificationInvoker;
import com.emc.settlement.config.invoker.fileupload.CMFProcessPollerInvoker;
import com.emc.settlement.config.invoker.fileupload.EMCPSOBudgetUploadInvoker;
import com.emc.settlement.config.invoker.fileupload.ForwardSalesContractUploadMainInvoker;
import com.emc.settlement.config.invoker.fileupload.JMSListenerMainInvoker;
import com.emc.settlement.config.invoker.fileupload.MMVolumeFileUploadInvoker;
import com.emc.settlement.config.invoker.fileupload.SaveClawbackQuantitiesFileInvoker;
import com.emc.settlement.config.invoker.fileupload.VestingContractUploadMainInvoker;
import com.emc.settlement.config.invoker.runrelated.IPDataVerificationsValidationsInvoker;
import com.emc.settlement.config.invoker.runrelated.PerformTestRunInvoker;
import lombok.Getter;


public class ContextInitializer {

	private static ContextInitializer contextInitializer;

	@Getter
	private ApplicationContext context;

	private ContextInitializer(){
		context = new AnnotationConfigApplicationContext(SpringConfig.class);
//		((ConfigurableEnvironment)context.getEnvironment()).setActiveProfiles("dev");
	}

	public static synchronized ContextInitializer getInstance() {
		if(contextInitializer==null) {
			contextInitializer = new ContextInitializer();
		}
		return contextInitializer;
	}

	public Map<String, Object> invokeBilateralContractValidateAndUploadData(Map<String, Object> variableMap, String operationPath) {
		BilateralContractValidateAndUploadDataInvoker bilateralContractValidateAndUploadDataInvoker = context.getBean(BilateralContractValidateAndUploadDataInvoker.class);
		return bilateralContractValidateAndUploadDataInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeSaveMSSL(Map<String, Object> variableMap, String operationPath) {

		SaveMSSLMeteringFileInvoker saveMSSLMeteringFileInvoker  = context.getBean(SaveMSSLMeteringFileInvoker.class);
		return saveMSSLMeteringFileInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeVerifyMSSL(Map<String, Object> variableMap, String operationPath) {

		VerifyAndLoadMSSLMeteringDataInvoker verifyAndLoadMSSLMeteringDataInvoker = context.getBean(VerifyAndLoadMSSLMeteringDataInvoker.class);
		return verifyAndLoadMSSLMeteringDataInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeAccountingInterface(Map<String, Object> variableMap, String operationPath) {
		AccountingInterfaceInvoker accountingInterfaceInvoker = context.getBean(AccountingInterfaceInvoker.class);
		return accountingInterfaceInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeApproveSettlementRun(Map<String, Object> variableMap, String operationPath) {
		ApproveSettlementRunInvoker approveSettlementRunInvoker = context.getBean(ApproveSettlementRunInvoker.class);
		return approveSettlementRunInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeCaptureFSCPenaltyQuantity(Map<String, Object> variableMap, String operationPath) {
		CaptureFSCPenaltyQuantityInvoker captureFSCPenaltyQuantityInvoker = context.getBean(CaptureFSCPenaltyQuantityInvoker.class);
		return captureFSCPenaltyQuantityInvoker.invoke(variableMap, operationPath);
	}
	
	public Map<String, Object> invokeCashFlowReconciliation(Map<String, Object> variableMap, String operationPath) {
		CashFlowReconciliationInvoker cashFlowReconciliationInvoker = context.getBean(CashFlowReconciliationInvoker.class);
		return cashFlowReconciliationInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeExecuteRun(Map<String, Object> variableMap, String operationPath) {
		ExecuteRunInvoker executeRunInvoker = context.getBean(ExecuteRunInvoker.class);
		return executeRunInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeFinalAuthoriseSettlementRun(Map<String, Object> variableMap, String operationPath) {
		FinalAuthoriseSettlementRunInvoker finalAuthoriseSettlementRunInvoker = context.getBean(FinalAuthoriseSettlementRunInvoker.class);
		return finalAuthoriseSettlementRunInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeForceDenySettlementRun(Map<String, Object> variableMap, String operationPath) {
		ForceDenySettlementRunInvoker forceDenySettlementRunInvoker = context.getBean(ForceDenySettlementRunInvoker.class);
		return forceDenySettlementRunInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeLoadPendingCMFFiles(Map<String, Object> variableMap, String operationPath) {
		LoadPendingCMFFilesInvoker loadPendingCMFFilesInvoker = context.getBean(LoadPendingCMFFilesInvoker.class);
		return loadPendingCMFFilesInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeRerunInclusions(Map<String, Object> variableMap, String operationPath) {
		RerunInclusionsInvoker rerunInclusionsInvoker = context.getBean(RerunInclusionsInvoker.class);
		return rerunInclusionsInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeRunValidations(Map<String, Object> variableMap, String operationPath) {
		RunValidationsInvoker runValidationsInvoker = context.getBean(RunValidationsInvoker.class);
		return runValidationsInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeSetPackageAuthorization(Map<String, Object> variableMap, String operationPath) {
		SetPackageAuthorizationInvoker setPackageAuthorizationInvoker = context.getBean(SetPackageAuthorizationInvoker.class);
		return setPackageAuthorizationInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeSettlementMainProcess(Map<String, Object> variableMap, String operationPath) {
		SettlementMainProcessInvoker settlementMainProcessInvoker = context.getBean(SettlementMainProcessInvoker.class);
		return settlementMainProcessInvoker.invoke(variableMap, operationPath);
	}

	public Map<String, Object> invokeSettlementRunProcess(Map<String, Object> variableMap, String operationPath) {
		SettlementRunProcessInvoker settlementRunProcessInvoker = context.getBean(SettlementRunProcessInvoker.class);
		return settlementRunProcessInvoker.invoke(variableMap, operationPath);
	}


	public Map<String,Object> invokeBilateralContractUploadMain(Map<String, Object> variableMap, String operationPath) {
		BilateralContractUploadMainInvoker bilateralContractUploadMainInvoker = context.getBean(BilateralContractUploadMainInvoker.class);
		return bilateralContractUploadMainInvoker.invoke(variableMap,operationPath);
	}

	public Map<String,Object> invokeCMFEmailNotification(Map<String, Object> variableMap, String operationPath) {
		CMFEmailNotificationInvoker cmfEmailNotificationInvoker = context.getBean(CMFEmailNotificationInvoker.class);
		return cmfEmailNotificationInvoker.invoke(variableMap, operationPath);
	}

	public Map<String,Object> invokeCMFProcessPoller(Map<String, Object> variableMap, String operationPath) {
		CMFProcessPollerInvoker cMFProcessPollerInvoker = context.getBean(CMFProcessPollerInvoker.class);
		return cMFProcessPollerInvoker.invoke(variableMap, operationPath);
	}

	public Map<String,Object> invokeEMCPSOBudgetUpload(Map<String, Object> variableMap, String operationPath) {
		EMCPSOBudgetUploadInvoker eMCPSOBudgetUploadInvoker = context.getBean(EMCPSOBudgetUploadInvoker.class);
		return eMCPSOBudgetUploadInvoker.invoke(variableMap, operationPath);

	}

	public Map<String,Object> invokeForwardSalesContractUploadMain(Map<String, Object> variableMap, String operationPath) {
		ForwardSalesContractUploadMainInvoker forwardSalesContractUploadMainInvoker = context.getBean(ForwardSalesContractUploadMainInvoker.class);
		return forwardSalesContractUploadMainInvoker.invoke(variableMap, operationPath);

	}

	public Map<String,Object> invokeJMSListenerMain(Map<String, Object> variableMap, String operationPath) {
		JMSListenerMainInvoker jMSListenerMainInvoker = context.getBean(JMSListenerMainInvoker.class);
		return jMSListenerMainInvoker.invoke(variableMap, operationPath);

	}

	public Map<String,Object> invokeMMVolumeFileUpload(Map<String, Object> variableMap, String operationPath) {
		MMVolumeFileUploadInvoker mMVolumeFileUploadInvoker = context.getBean(MMVolumeFileUploadInvoker.class);
		return mMVolumeFileUploadInvoker.invoke(variableMap, operationPath);

	}

	public Map<String,Object> invokeSaveClawbackQuantitiesFile(Map<String, Object> variableMap, String operationPath) {
		SaveClawbackQuantitiesFileInvoker saveClawbackQuantitiesFileInvoker = context.getBean(SaveClawbackQuantitiesFileInvoker.class);
		return saveClawbackQuantitiesFileInvoker.invoke(variableMap, operationPath);

	}

	public Map<String,Object> invokeVerifyAndLoadClawbackQuantitiesData(Map<String, Object> variableMap, String operationPath) {
		VerifyAndLoadClawbackQuantitiesDataInvoker verifyAndLoadClawbackQuantitiesDataInvoker = context.getBean(VerifyAndLoadClawbackQuantitiesDataInvoker.class);
		return verifyAndLoadClawbackQuantitiesDataInvoker.invoke(variableMap, operationPath);

	}

	public Map<String,Object> invokeVestingContractUploadMain(Map<String, Object> variableMap, String operationPath) {
		VestingContractUploadMainInvoker vestingContractUploadMainInvoker = context.getBean(VestingContractUploadMainInvoker.class);
		return vestingContractUploadMainInvoker.invoke(variableMap, operationPath);

	}

	public Map<String, Object> invokeIPDataVerificationsValidations(Map<String, Object> variableMap, String operationPath) {
		IPDataVerificationsValidationsInvoker ipDataVerificationsValidationsInvoker = context.getBean(IPDataVerificationsValidationsInvoker.class);
		return ipDataVerificationsValidationsInvoker.invoke(variableMap, operationPath);
	}

	public Map<String,Object> invokePerformTestRun(Map<String, Object> variableMap, String operationPath) {
		PerformTestRunInvoker performTestRunInvoker = context.getBean(PerformTestRunInvoker.class);
		return performTestRunInvoker.invoke(variableMap, operationPath);

	}
}
