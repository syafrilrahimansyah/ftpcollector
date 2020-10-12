package com.tselree.ftpcollector.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.tselree.ftpcollector.DAO.OmniformDAO;
import com.tselree.ftpcollector.DAO.OmniformDAOimpl;

@Configuration
@PropertySource({
    "file:src/main/resources/application.properties" 
})
public class DBConfig {
	@Autowired
    Environment env;
	
	@Bean()
    public DataSource getDataSource2() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("DB_DRIVER"));
        dataSource.setUrl(env.getProperty("DB_URL"));
        dataSource.setUsername(env.getProperty("DB_USERNAME"));
        dataSource.setPassword(env.getProperty("DB_PASSWORD"));
         
        return dataSource;
    }
	@Bean()
    public OmniformDAO getTimeRecordDAO() {
    	return new OmniformDAOimpl(getDataSource2());
    }
}
