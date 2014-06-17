package com.search.spider;

import java.util.HashMap;

public  class SpiderRecord {

	//记录处理数
	public static  HashMap<String,Integer> recordMap=new HashMap<String,Integer>();
	
	public static synchronized void addKeyNum(String key, Integer num) {
		
		recordMap.put(key.toLowerCase(), num);
	}

	public static synchronized void resetKeyNum() {

		recordMap.clear();
	}
	
	public static synchronized int getNum(String key){
		
		Integer fetchNum=SpiderRecord.recordMap.get(key.toLowerCase());
		
		if(fetchNum==null)fetchNum=0;
	
		return fetchNum;
	}
	
}
