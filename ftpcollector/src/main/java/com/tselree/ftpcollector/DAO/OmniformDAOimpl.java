package com.tselree.ftpcollector.DAO;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class OmniformDAOimpl implements OmniformDAO{
	private JdbcTemplate jdbcTemplate;
	public OmniformDAOimpl(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	@Override
	public void addOmniform(String payload) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO omniform(id,payload,stage) VALUES('00001234','"+payload+"','col')";
		jdbcTemplate.update(sql);
	}
	@Override
	public void updOmniform(String id, String payload) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String checkOmniform(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
