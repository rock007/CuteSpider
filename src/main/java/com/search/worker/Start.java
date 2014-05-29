package com.search.worker;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import com.search.spider.SavePipeline;
import com.search.spider.SpiderLiepinProcessor;

public class Start {

	public static void main(String[] args) {
		
		 ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext*.xml");
		 
		 final SpiderLiepinProcessor liepinProcessor = applicationContext.getBean(SpiderLiepinProcessor.class);
		
		 liepinProcessor.doWork();
	}
}
