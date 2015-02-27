package com.search.test;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.search.spider.SavePipeline;
import com.search.spider.Spider51jobProcessor;
import com.search.spider.SpiderZhaopinProcessor;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

public class SpiderDemo {
    /**
     * 智联  http://sou.zhaopin.com/Jobs/searchresult.ashx?jl=%E9%BB%84%E7%9F%B3&sm=0&p=1&sf=0
     * 无忧    http://search.51job.com/jobsearch/search_result.php?fromJs=1&jobarea=180400&funtype=0000&industrytype=00&issuedate=9&providesalary=99&keywordtype=2&lang=c&stype=2&workyear=99&cotype=99&degreefrom=99&jobterm=01&companysize=99&fromType=1
     * */
    public static void main(String[] args) {
    	
    	//System.setProperty("javax.net.ssl.trustStore", "/Users/fuhe-apple-02/Documents/workspace-sts-3.4.0.RELEASE/CuteSpider/jssecacerts");
    	
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext*.xml");
		
		try{
			
			Spider51jobProcessor spider=applicationContext.getBean("Spider51jobProcessor",Spider51jobProcessor.class);
				
			Spider oneSpider=Spider.create(spider);
			
			//oneSpider.addUrl("http://51job.com/");
			oneSpider.addUrl("http://search.51job.com/job/62481016,c.html");
			oneSpider.addPipeline(new ConsolePipeline());
			oneSpider.thread(1);
	        //启动爬虫
			oneSpider.run();
			
			/****  
			SpiderZhaopinProcessor spider=applicationContext.getBean("SpiderZhaopinProcessor",SpiderZhaopinProcessor.class);
			
			Spider oneSpider=Spider.create(spider);
			
			//oneSpider.addUrl("http://zhaopin.com/");
			oneSpider.addUrl("http://jobs.zhaopin.com/121088806250047.htm?ssidkey=y&ss=201&ff=03");
			oneSpider.addPipeline(new ConsolePipeline());
			oneSpider.thread(1);
	        //启动爬虫
			oneSpider.run();
			****/
		}catch(Exception ex){
			ex.printStackTrace();
		}
    }
    
}
