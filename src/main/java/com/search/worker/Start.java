package com.search.worker;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.search.spider.SpiderLagouProcessor;
import com.search.spider.SpiderLiepinProcessor;

public class Start {

	public static void main(String[] args) {
		
		 ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext*.xml");
		 
		 final SpiderLiepinProcessor liepinProcessor = applicationContext.getBean(SpiderLiepinProcessor.class);
		
		 SpiderLagouProcessor lagouProcessor=applicationContext.getBean(SpiderLagouProcessor.class);
		 
		 try{
			 //liepinProcessor.doWork();
			 
			 lagouProcessor.doWork();
			 
			 System.out.print("work finish!");
			 
		 }catch(Exception ex){
			 
			 ex.printStackTrace();
		 }
	}
}
