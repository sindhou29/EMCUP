package com.emc.settlement.common;

import java.util.Calendar;

import com.emc.settlement.config.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class PavPackageTest {

	@Autowired
	private PavPackageImpl pavPackage;

	/*@Before
	public void setup() throws SQLException, NamingException {
		OracleDataSource ds = new OracleDataSource();
		ds.setURL("jdbc:oracle:thin:@10.1.151.13:9983:emcdev");	// tmp
		ds.setUser("sebu");
		ds.setPassword("sharp_2018");
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		builder.bind("java:jboss/datasources/NemsDS", ds);
		builder.activate();

		Properties props = System.getProperties();
		props.setProperty(DBUtility.EMC_DATASOURCE_JNDI, "java:jboss/datasources/NemsDS");
	}*/

	@Test
	public void testCreateNextPkgVersion() {
		Calendar cal = Calendar.getInstance();
		cal.set(2018,04,12,0,0,0);
		cal.set(Calendar.MILLISECOND, 0);
		pavPackage.createNextPkgVersion("SETTLEMENT_MSSL_QUANTITIES", cal.getTime());
	}
}
