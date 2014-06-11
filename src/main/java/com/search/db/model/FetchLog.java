package com.search.db.model;

public class FetchLog {

	private int id;
	
	private String day;
	
	private String site;
	
	private int fetchNum;
	
	private int validNum;
	
	private String remarks;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public int getFetchNum() {
		return fetchNum;
	}

	public void setFetchNum(int fetchNum) {
		this.fetchNum = fetchNum;
	}

	public int getValidNum() {
		return validNum;
	}

	public void setValidNum(int validNum) {
		this.validNum = validNum;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
}
