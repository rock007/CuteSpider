package com.search.db.model.vo;

public class CityVO {

	private int privinceId;
	
	private int cityId;
	
	private int districtId;
	
	private String cityName;

	public int getPrivinceId() {
		return privinceId;
	}

	public void setPrivinceId(int privinceId) {
		this.privinceId = privinceId;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getDistrictId() {
		return districtId;
	}

	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	
}
