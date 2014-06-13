package com.search.spider;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.search.comm.StringUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

@Component("Spider51jobProcessor")
public class Spider51jobProcessor implements PageProcessor{

private static final Logger logger = LoggerFactory.getLogger(Spider51jobProcessor.class);
	
    public Site site = Site.me().setRetryTimes(3).setSleepTime(3000);
    
    private String pageUrl;
    private static HashMap<String,Integer> doneLinks=new HashMap<String,Integer>();
    private static Integer doneNum=0;

	public Spider51jobProcessor(){
    	
    	site.setDomain("51job.com");
    	site.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");
    	
    }
	
	@Override
	public Site getSite() {
		return site;
	}
	
	@Override
	public void process(Page page) {
		
		String title;
		String keyStr,valueStr;
		
		pageUrl=page.getRequest().getUrl();
		
		Html pageHtml=page.getHtml();
		Selectable pageRefLinks= page.getHtml().links();
		
		//1.页面是否已经存在过
		synchronized (doneLinks) {  
			if(doneLinks.containsKey(pageUrl)){
			
				page.setSkip(true);
				return;
			}
		}
		
		String regEx="http://search.51job.com/job/\\d+,c.html";  
		Pattern p=Pattern.compile(regEx);
		
		if(p.matcher(pageUrl).find()){
			
			//可以处理
			System.out.println("找到一个:"+pageUrl);
			
			List<String> job_1_List=pageHtml.xpath("//table[@class='jobs_1']").all();
			
			for(int i=0;i<job_1_List.size();i++){
				
				Selectable sel=pageHtml.xpath("//table[@class='jobs_1']/[1]");
				
				
			}
			
			
			
		}else{
			page.setSkip(true);
		}        
        //分页、列表
         page.addTargetRequests(pageRefLinks.regex("http://[\\w,\\/-]+.51job.com/[\\w,\\/.-]+").all());
        
		synchronized (doneLinks) {   
        	doneLinks.put(pageUrl, doneNum++);
        	SpiderRecord.addKeyNum("Lagou_all", doneNum);
        }
		
	}

}
