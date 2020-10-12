package com.tselree.ftpcollector.DAO;

public interface OmniformDAO {
	public void addOmniform(String payload);
	public void updOmniform(String id, String payload);
	public String checkOmniform(String id);
	public Integer checkExist(String payload);
}
