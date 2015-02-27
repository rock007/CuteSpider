package com.search.db.model;

public class JobPropModel {

	private int Id;
	
	private int JobId;
	
	private String Key;
	
	private String Value;

	public String getKey() {
		return Key;
	}

	public void setKey(String key) {
		Key = key;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public int getJobId() {
		return JobId;
	}

	public void setJobId(int jobId) {
		JobId = jobId;
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}
	
	
}
