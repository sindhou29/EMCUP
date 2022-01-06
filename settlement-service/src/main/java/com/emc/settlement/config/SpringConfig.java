package com.emc.settlement.config;


import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@ComponentScan(basePackages = "com.emc.settlement")
//@Import({DbConfigTest.class, DbConfigJndi.class})
@EnableTransactionManagement
public class SpringConfig {


    @Bean
    public DataSource dataSource() throws NamingException {
        InitialContext context = new InitialContext();
        DataSource dataSource = (DataSource) context.lookup("java:jboss/datasources/NemsDS");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        return jdbcTemplate;
    }


    @Bean
    public DataSourceTransactionManager transactionManager() throws NamingException{
        DataSourceTransactionManager transactionManager
                = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate() throws NamingException {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager());
        return transactionTemplate;
    }
}


