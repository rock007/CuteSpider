package com.search.form.model;

public class SearchForm {

	private String keyword;
	
	private String address;
	
	private String dateLoop;//时间区间
	
	private String industry;
	
	private String salary;
	
	private String company;
	
	private String source;

	public String getSalary() {
		return salary;
	}

	public void setSalary(String salary) {
		this.salary = salary;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDateLoop() {
		return dateLoop;
	}

	public void setDateLoop(String dateLoop) {
		this.dateLoop = dateLoop;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}
	
	
}
