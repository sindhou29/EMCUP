package com.emc.settlement.backend.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.emc.settlement.common.UtilityFunctions;
import com.emc.settlement.model.backend.pojo.DateRange;
import com.emc.settlement.model.backend.pojo.SettlementRunParams;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class UtilityService {

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	private TransactionTemplate transactionTemplate;

	private static final Logger logger = Logger.getLogger(UtilityService.class);

	@Transactional
	public Map<String, Object> getTradingDatesRange(Map<String, Object> variableMap) {

		String logPrefix = (String) variableMap.get("logPrefix");
		SettlementRunParams settlementParam = (SettlementRunParams) variableMap.get("settlementParam");
		String msg="Checking TradingDates in NEMS Controller. ";
		String msgStep="UtilityService.getTradingDatesRange";

		logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

		logger.info("controllerParam_runDate " + settlementParam.getRunDate() + ",logPrefix " + logPrefix + ", controllerParam_RunType "+settlementParam.getRunType());
		settlementParam = singleTradingDatesRange(settlementParam.getRunDate(), settlementParam.getRunType(), logPrefix, settlementParam);

		List<Date> settDates =utilityFunctions.getDaysBetweenDates(settlementParam.getFromSettlementDate(),settlementParam.getToSettlementDate());

		logger.info("msgStep " + msgStep);
		logger.info(logPrefix + "End Activity: " + msgStep + " ...");

		variableMap.put("settlementParam", settlementParam);
		variableMap.put("settDates", settDates);
		logger.info("Returning from service "+msgStep+" - (settlementParam :" + settlementParam
				+ " settDates :" + settDates.toString()
				+ " settlementParam.getSettlementDate() :" + settlementParam.getSettlementDate()
				+ " settlementParam.getFromSettlementDate() :" + settlementParam.getFromSettlementDate()
				+ " settlementParam.getToSettlementDate() :" + settlementParam.getToSettlementDate() + ")");

		return variableMap;
	}

	public void logJAMMessage(Map<String, Object> variableMap) {
		String eventId = (String) variableMap.get("eventId");
		String severity = (String) variableMap.get("severity");
		String execStep = (String) variableMap.get("execStep");
		String text = (String) variableMap.get("text");
		utilityFunctions.logJAMMessage(eventId, severity, execStep, text, "");
	}

	private SettlementRunParams singleTradingDatesRange(
			Date runDate,
			String runType,
			String logPrefix,
			SettlementRunParams settlementParam) {

		String msgStep="UtilityService.singleTradingDatesRange-"+runType;
		try {

			boolean checkLastRun=false;
			Date tradingStartDate=null;
			Date tradingEndDate=null;

			logger.info(logPrefix + "Starting Activity: " + msgStep + " ...");

			logger.info("singleTradingDatesRange: runDate "+runDate);

			DateRange dateRange =
					utilityFunctions.getSettlementDateRange(runDate, runType, checkLastRun);

			logger.info("tradingStartDate" + dateRange.startDate);

			tradingStartDate =
					utilityFunctions.convertUDateToSDate(dateRange.startDate);
			tradingEndDate =
					utilityFunctions.convertUDateToSDate(dateRange.endDate);

			logger.info("tradingStartDate" + tradingStartDate);
			logger.info("tradingEndDate" + tradingEndDate);

			logger.info(logPrefix + "NEMS Controller Run - Type: " + runType +
					", from date:" + tradingStartDate + ", to date:" +
					tradingEndDate);

			settlementParam.setSettlementDate(tradingStartDate);
			settlementParam.setFromSettlementDate(tradingStartDate);
			settlementParam.setToSettlementDate(tradingEndDate);

			logger.info("ControllerParam_FromTradingDate " + tradingStartDate + ", ControllerParam_ToTradingDate " +
					tradingEndDate+ ", ControllerParam_TradingdDate " +
					tradingStartDate/*", TreadingDays " +
					dateRangeList*/
			);

			logger.info(logPrefix + "End Activity: " + msgStep + " ...");

		}
		catch (Exception e) {
			logger.error(logPrefix + "<" + msgStep + "> Exception: " + e.getMessage());
		}

		return settlementParam;
	}

	public void logJAMMessage(String eventId, String severity, String execStep, String text, String errorCode) {

		/*transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			public void doInTransactionWithoutResult(TransactionStatus status) {*/
				utilityFunctions.logJAMMessage(eventId, severity, execStep, text, errorCode);
			/*}
		});*/
		logger.info("[JAM] logJAMMessage completed");
	}


}
