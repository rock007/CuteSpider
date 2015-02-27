package com.search.comm;

import java.util.HashMap;
import java.util.Map;

 public  class DictManager {

	private  static DictManager instance;
	 
	static {
		try {
			instance = new DictManager();
		} catch (Exception e) {

		}
	}
	private static Map<String, String> salaryDic,EduDic,CompanySizDic;
	
	private DictManager(){
		
		 salaryDic=new HashMap<String,String>();
		 //51job
		 salaryDic.put("面议", "Salary_0");
		 salaryDic.put("1500以下", "Salary_5k");
		 salaryDic.put("1500-1999", "Salary_5k");
		 salaryDic.put("2000-2999", "Salary_5k");
		 salaryDic.put("3000-4499", "Salary_5k");
		 salaryDic.put("4500-5999", "Salary_5k_8k");
		 salaryDic.put("6000-7999", "Salary_5k_8k");
		 salaryDic.put("8000-9999", "Salary_8k_12k");
		 salaryDic.put("10000-14999", "Salary_8k_12k");
		 salaryDic.put("15000-19999", "Salary_12k");
		 salaryDic.put("20000-29999", "Salary_12k");
		 salaryDic.put("30000-49999", "Salary_12k");
		 salaryDic.put("50000及以上", "Salary_12k");
		 //智联
		 
		 
		 CompanySizDic=new HashMap<String,String>();
		 CompanySizDic.put("少于50人", "ERv1_11_50");
		 CompanySizDic.put("50-150人", "ERv1_51_200");
		 CompanySizDic.put("150-500人", "ERv1_201_500");
		 CompanySizDic.put("500-1000人", "ERv1_501_1000");
		 CompanySizDic.put("1000-5000人", "ERv1_1001_5000");
		 CompanySizDic.put("5000-10000人", "ERv1_5001_10000");
		 CompanySizDic.put("10000人以上", "ERv1_10000_PLUS");
		 //智联招聘
		 CompanySizDic.put("20人以下", "ERv1_11_50");
		 CompanySizDic.put("20-99人", "ERv1_51_200");
		 CompanySizDic.put("100-499人", "ERv1_201_500");
		 CompanySizDic.put("500-999人", "ERv1_501_1000");
		 CompanySizDic.put("1000-9999人", "ERv1_1001_5000");
		 //CompanySizDic.put("10000人以上", "ERv1_10000_PLUS");
		 
		 EduDic=new HashMap<String,String>();
		 EduDic.put("初中", "Edu_1");
		 EduDic.put("高中", "Edu_1");
		 EduDic.put("中技", "Edu_1");
		 EduDic.put("中专", "Edu_1");
		 EduDic.put("大专", "Edu_2");
		 EduDic.put("本科", "Edu_3");
		 EduDic.put("硕士", "Edu_4");
		 EduDic.put("博士", "Edu_4");
		 EduDic.put("其他", "Edu_0");
		 
	}
	
	/**
	 * @param args
	 */
	public  static String Salary(String k) {

		String m= salaryDic.get(k);
		
		if(m==null||m.equals("")) m="Salary_0";
		
		return m;
	}

	public static String CompanySize(String k) {
		
		String m= CompanySizDic.get(k);
		
		if(m==null||m.equals("")) m="ERv1_2_10";
		
		return m;
	}
	
	public static String Edu(String k) {
		String m= EduDic.get(k);
		
		if(m==null||m.equals("")) m="Edu_0";
		
		return m;
	}
}
