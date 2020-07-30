package com.tselree.ftpcollector.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.tselree.ftpcollector.DAO.OmniformDAO;
import com.tselree.ftpcollector.DAO.OmniformDAOimpl;

@Configuration
@ComponentScan(basePackages="com.tselree.ftpcollector")
public class DBConfig {
	@Bean()
    public DataSource getDataSource2() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/extractor?serverTimezone=UTC&useLegacyDatetimeCode=false");
        dataSource.setUsername("pmauser");
        dataSource.setPassword("alvin147");
         
        return dataSource;
    }
	@Bean()
    public OmniformDAO getTimeRecordDAO() {
    	return new OmniformDAOimpl(getDataSource2());
    }
}
