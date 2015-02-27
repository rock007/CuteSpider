package com.search.worker;

/***
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.search.spider.SpiderManager;

public class StartSpider {

	private static final Logger log = Logger.getLogger(Start.class);  
	 
	static SpiderManager spiderManager;
	

	public static void main(String[] args) {
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext*.xml");
		
		try {
			 spiderManager=applicationContext.getBean("spiderManager",SpiderManager.class);
			 spiderManager.lesgo();
			 
		}catch (Exception ex) {
			
			log.error("start exception",ex );
		}
		 log.warn("work finish!");
	}

}

***/


import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import com.search.spider.SpiderManager;
import com.search.spider.SpiderRecord;

public  class StartSpider{

	private static final Logger log = Logger.getLogger(StartSpider.class);  
	 
	static SpiderManager spiderManager;
	
	static boolean isFucked=false;
	
	public static void main(String[] args) {
		
		 ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext*.xml");
		 
		 Integer beginHour,spendHour;
		 boolean isSpiderStarted=false;
		 long keepTime=0;
		 
		 try{
			 log.debug("let's happy!");
			 
			 if(args.length!=2){
				 
				 log.warn("参数不正确，12点开始 3个小时，eg: xx 12 3 ");
				 return;
			 }
			 
			 try{
				 beginHour=Integer.parseInt(args[0]);
				 spendHour=Integer.parseInt(args[1]);
			 }catch (Exception ex){
				 log.warn("参数只能是数字，12点开始 3个小时，eg: xx 12 3 ");
				 return;				 
			 }
			 
		     addSignalHandler("INT");/* 注册KILL信号 */
		     addSignalHandler("TERM");/* 注册CTRL+C信号 */
		      
			 spiderManager=applicationContext.getBean("spiderManager",SpiderManager.class);
			 
			 while(true){
				 
				 if(isFucked) break;
				 
				 Date curDate=new Date();
				 Calendar calendar = Calendar.getInstance();
			     calendar.setTime(curDate);
			     
			     if(isSpiderStarted){
			    	 //启动中
			    	 if(calendar.get(Calendar.HOUR_OF_DAY)==(beginHour+spendHour)){
						 log.warn("spider timeout:"+(beginHour+spendHour));
						 //时间到了！
						 spiderManager.stop();
						 isSpiderStarted=false;
						 
						 //保存抓取短讯
						 spiderManager.keepLog();
						 SpiderRecord.resetKeyNum();
						 //发送邮件
						 
					 }
			    	 keepTime++;
				     if(keepTime>=10){
				    	  
				    	  log.warn("spider keepLog:"+SpiderRecord.recordMap.toString());
				    	  keepTime=0;
				    	  spiderManager.keepLog();
				    	  SpiderRecord.resetKeyNum();
				      }
			     }else{
			    	 //等待中
					 if(calendar.get(Calendar.HOUR_OF_DAY)==beginHour){
						 log.warn("启动spider："+beginHour);
						 //启动
						 spiderManager.lesgo();
						 isSpiderStarted=true;
					 }			    	 
			     }
			     
				 Thread.sleep(60000);
			 }
			
		 }catch(Exception ex){
			 
			 log.error("start exception",ex );
		 }
		 log.warn("work finish!");
	}
	 
	@SuppressWarnings("restriction")
	private static  void addSignalHandler(String signalName) {
	     Signal.handle(new Signal(signalName), new SignalHandler() {
	         public void handle(Signal sig) {
	        	 log.warn("I am fucking killed!");
	           
	             isFucked=true;
	             spiderManager.stop();
				 //保存抓取短讯
				 spiderManager.keepLog();
	         }
	     });
	     log.debug("Added signal handler:"+signalName);
	}
}
