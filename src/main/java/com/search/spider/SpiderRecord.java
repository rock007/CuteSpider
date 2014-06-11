package com.search.spider;

import java.util.HashMap;

public  class SpiderRecord {

	//记录处理数
	public static  HashMap<String,Integer> recordMap=new HashMap<String,Integer>();
	
	public static void addKeyNum(String key,Integer num){
		
		synchronized (recordMap) {  
			
			recordMap.put(key, num);
			
		}
	}
	
	public static void resetKeyNum(){
		
		synchronized (recordMap) {  
			
			recordMap.clear();;
			
		}
	}
	
}
