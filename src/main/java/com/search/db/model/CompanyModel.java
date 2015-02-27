package com.search.db.model;

public class CompanyModel {

	private int CompanyId;

	private String Name;

	private String LinkMan;

	private String Phone;

	private int ProvinceId;

	private int CityId;

	private int RegionId;

	private String PostalCode;

	private String Address;

	private String Industry;

	private String Size;// 公司规模

	private String WebSite;

	private String Logo;

	private String Remarks;

	private String RemarksTxt;

	private String Brief;

	// 关联用户(可能无)
	private int UserId;

	private int OrderNo;

	public int getCompanyId() {
		return CompanyId;
	}

	public void setCompanyId(int companyId) {
		CompanyId = companyId;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getLinkMan() {
		return LinkMan;
	}

	public void setLinkMan(String linkMan) {
		LinkMan = linkMan;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public int getProvinceId() {
		return ProvinceId;
	}

	public void setProvinceId(int provinceId) {
		ProvinceId = provinceId;
	}

	public int getCityId() {
		return CityId;
	}

	public void setCityId(int cityId) {
		CityId = cityId;
	}

	public int getRegionId() {
		return RegionId;
	}

	public void setRegionId(int regionId) {
		RegionId = regionId;
	}

	public String getPostalCode() {
		return PostalCode;
	}

	public void setPostalCode(String postalCode) {
		PostalCode = postalCode;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getIndustry() {
		return Industry;
	}

	public void setIndustry(String industry) {
		Industry = industry;
	}

	public String getSize() {
		return Size;
	}

	public void setSize(String size) {
		Size = size;
	}

	public String getWebSite() {
		return WebSite;
	}

	public void setWebSite(String webSite) {
		WebSite = webSite;
	}

	public String getLogo() {
		return Logo;
	}

	public void setLogo(String logo) {
		Logo = logo;
	}

	public String getRemarks() {
		return Remarks;
	}

	public void setRemarks(String remarks) {
		Remarks = remarks;
	}

	public String getRemarksTxt() {
		return RemarksTxt;
	}

	public void setRemarksTxt(String remarksTxt) {
		RemarksTxt = remarksTxt;
	}

	public String getBrief() {
		return Brief;
	}

	public void setBrief(String brief) {
		Brief = brief;
	}

	public int getUserId() {
		return UserId;
	}

	public void setUserId(int userId) {
		UserId = userId;
	}

	public int getOrderNo() {
		return OrderNo;
	}

	public void setOrderNo(int orderNo) {
		OrderNo = orderNo;
	}
	
	
}
