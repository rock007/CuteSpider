package com.search.worker;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.search.lucence.JobIndexFile;
import com.search.spider.SpiderLagouProcessor;
import com.search.spider.SpiderLiepinProcessor;
import com.search.spider.SpiderManager;
import com.search.spider.SpiderRecord;

public class Start {

	 private static final Logger log = Logger.getLogger(Start.class);  
	 
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
			 
			 SpiderManager spiderManager=applicationContext.getBean("spiderManager",SpiderManager.class);
			 JobIndexFile index = applicationContext.getBean("jobIndexFile", JobIndexFile.class);
			 
			 while(true){
				 
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
						 
						 //更新索引
						 index.doWork();
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
			 
			 ex.printStackTrace();
			 log.error("start exception",ex );
		 }
		 log.warn("work finish!");
	}
	
	public void doFetchURL(){
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext*.xml");
		 SpiderManager spiderManager=applicationContext.getBean("spiderManager",SpiderManager.class);
		 
		 try{
			 
			 Date beginDate=new Date();
			 Date curDate=new Date();
			 
			 //启动
			 spiderManager.lesgo();
			 
			 int runTime=2;//spiderManager.getHours();
			 
			 long hour=0,keepTime=0;
			 try
			 { 
				 do {
					 
					 curDate=new Date();

				     long diff = curDate.getTime() - beginDate.getTime();

				      hour = diff / (1000 * 60 * 60 );
				      
				      //暂停一分钟
				      Thread.sleep(60000);
				      keepTime++;
				      if(keepTime>=10){
				    	  
				    	  keepTime=0;
				    	  spiderManager.keepLog();
				      }
				 }while(hour < runTime);
				 
				 //时间到了！
				 spiderManager.stop();
				 
				 //保存抓取短讯
				 spiderManager.keepLog();
				 //发送邮件
			 }
			 catch (Exception e)
			 {
				 System.out.print(e.toString());
			 }
		 }catch(Exception ex){
			 
			 ex.printStackTrace();
		 }
	}
}
