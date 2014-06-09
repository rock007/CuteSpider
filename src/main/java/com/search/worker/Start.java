package com.search.worker;

import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.search.spider.SpiderLagouProcessor;
import com.search.spider.SpiderLiepinProcessor;
import com.search.spider.SpiderManager;

public class Start {

	public static void main(String[] args) {
		
		 ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext*.xml");
		 
		 //final SpiderLiepinProcessor liepinProcessor = applicationContext.getBean(SpiderLiepinProcessor.class);
		
		 //SpiderLagouProcessor lagouProcessor=applicationContext.getBean(SpiderLagouProcessor.class);
		 
		 SpiderManager spiderManager=applicationContext.getBean("spiderManager",SpiderManager.class);
		 
		 try{
			 
			 Date beginDate=new Date();
			 Date curDate=new Date();
			 
			 //liepinProcessor.doWork();
			 //lagouProcessor.doWork();
			 
			 spiderManager.lesgo();
			 
			 int runTime=spiderManager.getHours();
			 
			 long hour=0;
			 try
			 { 
				 do {
					 
					 curDate=new Date();

				     long diff = curDate.getTime() - beginDate.getTime();

				      hour = diff / (1000 * 60 * 60 );
				      
				      Thread.sleep(30000);
					 
				 }while(hour>runTime);

			 }
			 catch (Exception e)
			 {
				 System.out.print(e.toString());
			 }
			 
			 System.out.print("work finish!");
			 
		 }catch(Exception ex){
			 
			 ex.printStackTrace();
		 }
	}
}
