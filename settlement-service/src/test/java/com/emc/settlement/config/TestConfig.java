package com.emc.settlement.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.emc.settlement.common.DBUtility;
import oracle.jdbc.pool.OracleDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@ComponentScan(basePackages = "com.emc.settlement")
@Configuration
public class TestConfig {

	@Bean
	public DataSource dataSource() throws NamingException, SQLException {

		OracleDataSource ds = new OracleDataSource();
		ds.setURL("jdbc:oracle:thin:@10.1.151.13:9983:emcdev");	// tmp
		ds.setUser("sebu");
		ds.setPassword("sharp_2018");
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		builder.bind("java:jboss/datasources/NemsDS", ds);
		builder.activate();

		Properties props = System.getProperties();
		props.setProperty(DBUtility.EMC_DATASOURCE_JNDI, "java:jboss/datasources/NemsDS");

		InitialContext context = new InitialContext();
		DataSource dataSource = (DataSource) context.lookup("java:jboss/datasources/NemsDS");
		return dataSource;
	}

	/*@Bean
	@Qualifier("anonymousDataSource")
	public DataSource anonymousDataSource() throws NamingException {
		InitialContext context = new InitialContext();
		DataSource dataSource = (DataSource) context.lookup("java:jboss/datasources/NemsDS");
		return dataSource;
	}*/

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.setResultsMapCaseInsensitive(true);
		return jdbcTemplate;
	}

	@Bean
	public DataSourceTransactionManager transactionManager() throws NamingException, SQLException {
		DataSourceTransactionManager transactionManager
				= new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource());
		return transactionManager;
	}

	/*@Bean
	@Qualifier("anonymousTransactionManager")
	public DataSourceTransactionManager anonymousTransactionManager() throws NamingException{
		DataSourceTransactionManager transactionManager
				= new DataSourceTransactionManager();
		transactionManager.setDataSource(anonymousDataSource());
		return transactionManager;
	}*/

	/*@Bean
	@Qualifier("anonymousJdbcTemplate")
	public JdbcTemplate anonymousJdbcTemplate(DataSource dataSource) throws NamingException, SQLException {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(anonymousDataSource());
		jdbcTemplate.setResultsMapCaseInsensitive(true);
		jdbcTemplate.getDataSource().getConnection().setAutoCommit(true);
		return jdbcTemplate;
	}*/

	@Bean
	public TransactionTemplate transactionTemplate() throws NamingException, SQLException {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager());
		return transactionTemplate;
	}
}
