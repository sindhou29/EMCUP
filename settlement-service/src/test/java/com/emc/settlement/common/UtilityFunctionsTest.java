package com.emc.settlement.common;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import com.emc.invoice.services.bc.am.common.InvInternalServices;
import com.emc.invoice.services.bc.am.common.InvInternalServices_Service;
import com.emc.settlement.model.backend.exceptions.SettRunInfoException;
import com.emc.settlement.config.TestConfig;
import com.oracle.xmlns.adf.svc.errors.ServiceException;
import oracle.jdbc.internal.OracleTypes;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class UtilityFunctionsTest {

	@Autowired
	private UtilityFunctions utilityFunctions;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void testgetSysParamTime() {
		assertNotNull(utilityFunctions.getSysParamTime("NT_EFFECTIVE_DATE"));
	}

	@Test
	public void testGetShareplexMode() {
		assertNotNull(utilityFunctions.getShareplexMode());
	}

	@Test
	public void testGetHostNEMSDBDetails() {
		assertNotNull(utilityFunctions.getHostNEMSDBDetails());
	}

	@Test
	public void testGetFileUploadAllowedBeforeCP66() {
		try {
			assertNotNull(utilityFunctions.getFileUploadAllowedBeforeCP66(new Date(), 0, "M", 20));
		}
		catch (Exception e) {
			printFailure();
		}
	}

	@Test
	public void testIsBusinessDay() {
		try {
			assertNotNull(utilityFunctions.isBusinessDay(new Date()));
		}
		catch (Exception e) {
			printFailure();
		}
	}

	@Test
	public void testGetSACByNodeName() {
		try {
			assertNotNull(utilityFunctions.getSACByNodeName("POWSERY : GT : SER GT1", "40"));
		}
		catch (Exception e) {
			printFailure();
		}
	}

	@Test
	public void testGetSettRunInfo() {
		try {
			assertNotNull(utilityFunctions.getSettRunInfo("BFCAC9D90F16ACC9E030330A010A63F5"));
		}
		catch (SettRunInfoException e) {
			printFailure();
		}
	}

	@Test
	public void testGetSettRunStatus() {
		Calendar instance = Calendar.getInstance();
		instance.set(2018, 0, 31);
		assertNotNull(utilityFunctions.getSettRunStatus(instance.getTime(), "P"));
	}

	@Test
	public void testGetProcessUrl() {
		Calendar instance = Calendar.getInstance();
		instance.set(2018, 7, 31);
		assertNotNull(utilityFunctions.getProcessUrl("MarketRulesProcess", instance.getTime()));
	}

	public void printFailure() {
		fail("Exception thrown");
	}

	@Test
	public void getLastBusinessDayOfMonth() {
		Calendar instance = Calendar.getInstance();
		instance.set(2006, 6, 21);
		assertNotNull(utilityFunctions.getLastBusinessDayOfMonth(instance.getTime()));
	}

	@Test
	public void getLatestRunId() {
		Calendar instance = Calendar.getInstance();
		instance.set(2015, 8, 6);
		assertNotNull(utilityFunctions.getLatestRunId(instance.getTime(), "P"));
	}

	@Test
	public void getMCRIds() {
		Calendar instance = Calendar.getInstance();
		instance.set(2015, 8, 6);
		assertNotNull(utilityFunctions.getMCRIds(instance.getTime()));
	}

	@Test
	public void getSettlementDate() {
		assertNotNull(utilityFunctions.getSettlementDate(new Date(), 1));
	}

	@Test
	public void testGetPktVersion() {
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withSchemaName("NEM")
				.withCatalogName("PAV$PACKAGING")
				.withFunctionName("GET_PKT_VERSION")
//				.withReturnValue()
//				.useInParameterNames("package_type","curr_next")
				.declareParameters(new SqlParameter("package_type", OracleTypes.VARCHAR),
						new SqlParameter("curr_next", OracleTypes.VARCHAR)/*,
						new SqlOutParameter("OUT_1", OracleTypes.NUMBER)*/);
		SqlParameterSource in = new MapSqlParameterSource()
				.addValue("package_type", "SETTLEMENT_MC_QUANTITIES")
				.addValue("curr_next", "NEXT");
		String nextVersion = jdbcCall.executeFunction(String.class, in);
		assertNotNull(nextVersion);
	}

	@Test
	public void testGetSettlementDateRange() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -2);
		utilityFunctions.getSettlementDateRange(utilityFunctions.addDays(cal.getTime(), 1), "P", false);
	}

	@Test
	public void testBase64Decode() throws IOException {
		String str = "";
		String st = utilityFunctions.base64Decode(str);
		assertNotNull(st);
	}

	@Test
	public void testInvInternalServices() {
		try {
			Map<String, String> propertiesMap = UtilityFunctions.getProperties();
//		String invrptservicesURL = propertiesMap.get("invrptservicesURL");
			String invrptservicesURL = "http://10.1.152.71:9541";
			URL url = new URL(invrptservicesURL + "/invrptservices/InvInternalServices?WSDL");
//		logger.log(Priority.INFO, logPrefix + "<" + msgStep + "> URL: " + url);
			Calendar cal = Calendar.getInstance();
			cal.set(2018, 8, 28,0,0,0);
			Date settlementDate = cal.getTime();

			InvInternalServices_Service service = new InvInternalServices_Service();
			InvInternalServices invInternalServicesSoapHttpPort = service.getInvInternalServicesSoapHttpPort();
			invInternalServicesSoapHttpPort.generateADFFile(utilityFunctions.toXMLGregorianCalendar(settlementDate),
					"786492ABFBA301D6E0530A01970B9DF5", "STL", false);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (ServiceException e) {
			e.printStackTrace();
		}
		catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNotification() {
		/*SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
				.withSchemaName("NEM")
				.withCatalogName("NEM$AQ_SERVICES")
				.withProcedureName("AQ_ENQUEUE_MESSAGE");
		*//*Map<String, Object> inMap = new HashMap<String, Object>();
		inMap.put("p_payload_char", "");
		inMap.put("p_queue_name", "omsfo.aq_notification_queue");*//*
		SqlParameterSource in = new MapSqlParameterSource().addValue("p_payload_char", "").addValue("p_queue_name", "omsfo.aq_notification_queue");
		jdbcCall.execute(in);*/
		CallableStatement cstmt = null;
		try(Connection conn = jdbcTemplate.getDataSource().getConnection()) {
//			cstmt = conn.prepareCall("NEM.NEM$AQ_SERVICES.AQ_ENQUEUE_MESSAGE");
			cstmt = conn.prepareCall("{call NEM.NEM$AQ_SERVICES.AQ_ENQUEUE_MESSAGE(?,?)}");
			cstmt.setString(1, "");
			cstmt.setString(2, "omsfo.aq_notification_queue");
			cstmt.executeUpdate();

		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Complete");
	}
 }
