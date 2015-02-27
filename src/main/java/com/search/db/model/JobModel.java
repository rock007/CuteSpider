package com.search.db.model;

import java.util.Date;

public class JobModel {

	private int JobId;

	private int CompanyId; // 显示关联公司信息

	private String Title;

	private String Brief;

	private int ProvinceId;

	private int CityId;
	
	private int RegionId;

	private String Desc;

	private String DescTxt;

	private String Keywords;

	private String Salary;

	private String Edu;

	private int JobType;

	private Date CreateDate;

	private int UserId; // 所属用户，发布者，不一定有（抓取过来）

	private String SaveToMail;

	private int Status;

	private int OrderNo;

	private String Form;

	private int ApplyNum;

	private int ViewNum;

	private String Url;

	public int getJobId() {
		return JobId;
	}

	public void setJobId(int jobId) {
		JobId = jobId;
	}

	public int getCompanyId() {
		return CompanyId;
	}

	public void setCompanyId(int companyId) {
		CompanyId = companyId;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getBrief() {
		return Brief;
	}

	public void setBrief(String brief) {
		Brief = brief;
	}

	public int getProvinceId() {
		return ProvinceId;
	}

	public void setProvinceId(int provinceId) {
		ProvinceId = provinceId;
	}

	public int getRegionId() {
		return RegionId;
	}

	public void setRegionId(int regionId) {
		RegionId = regionId;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public String getDescTxt() {
		return DescTxt;
	}

	public void setDescTxt(String descTxt) {
		DescTxt = descTxt;
	}

	public String getKeywords() {
		return Keywords;
	}

	public void setKeywords(String keywords) {
		Keywords = keywords;
	}

	public String getSalary() {
		return Salary;
	}

	public void setSalary(String salary) {
		Salary = salary;
	}

	public String getEdu() {
		return Edu;
	}

	public void setEdu(String edu) {
		Edu = edu;
	}

	public int getJobType() {
		return JobType;
	}

	public void setJobType(int jobType) {
		JobType = jobType;
	}

	public Date getCreateDate() {
		return CreateDate;
	}

	public void setCreateDate(Date createDate) {
		CreateDate = createDate;
	}

	public int getUserId() {
		return UserId;
	}

	public void setUserId(int userId) {
		UserId = userId;
	}

	public String getSaveToMail() {
		return SaveToMail;
	}

	public void setSaveToMail(String saveToMail) {
		SaveToMail = saveToMail;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public int getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(int orderNo) {
		OrderNo = orderNo;
	}

	public String getForm() {
		return Form;
	}

	public void setForm(String form) {
		Form = form;
	}

	public int getApplyNum() {
		return ApplyNum;
	}

	public void setApplyNum(int applyNum) {
		ApplyNum = applyNum;
	}

	public int getViewNum() {
		return ViewNum;
	}

	public void setViewNum(int viewNum) {
		ViewNum = viewNum;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public int getCityId() {
		return CityId;
	}

	public void setCityId(int cityId) {
		CityId = cityId;
	}

	
	
}
