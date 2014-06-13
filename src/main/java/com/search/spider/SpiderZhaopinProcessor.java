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

@Component("SpiderZhaopinProcessor")
public class SpiderZhaopinProcessor implements PageProcessor{

private static final Logger logger = LoggerFactory.getLogger(SpiderZhaopinProcessor.class);
	
    public Site site = Site.me().setRetryTimes(3).setSleepTime(3000);
    
    private String pageUrl;
    private static HashMap<String,Integer> doneLinks=new HashMap<String,Integer>();
    private static Integer doneNum=0;

	public SpiderZhaopinProcessor(){
    	
    	site.setDomain("zhaopin.com");
    	site.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");
    	
    }
	
	@Override
	public Site getSite() {
		return site;
	}
	
	@Override
	public void process(Page page) {
		
		String title,companyName,salary;
		
		//属性
		HashMap<String,String> propsMap=new HashMap<String,String>();
		
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
		
		String regEx="http://jobs.zhaopin.com/(\\d)+.htm\\?([\\w=&]+)";  
		Pattern p=Pattern.compile(regEx);
		
		if(p.matcher(pageUrl).find()){
			
			//可以处理
			System.out.println("找到一个:"+pageUrl);
			
			Selectable jobSel=pageHtml.xpath("//div[@class='top-left']");
			
			Selectable table1=jobSel.xpath("//table[1]");
			
			List<String> table1TRList=table1.xpath("tr").all();
	
			title=table1.xpath("//tr[1]/td/h1/text()").toString();
			companyName=table1.xpath("//tr[2]/td/h2/a/text()").toString();
			
			Selectable tr,td;
			for(int i=2;i<table1TRList.size();i++){
				
				//tr=table1.xpath("//tr["+(i+1)+"]");
				
				List<String> tdStrs=table1.xpath("//tr["+(i+1)+"]/td").all();
				
				if(tdStrs.size()==2){
					
					propsMap.put(clearHtml(tdStrs.get(0)), clearHtml(tdStrs.get(1)));
					
				}else if(tdStrs.size()==4){
					propsMap.put(clearHtml(tdStrs.get(0)), clearHtml(tdStrs.get(1)));
					propsMap.put(clearHtml(tdStrs.get(2)), clearHtml(tdStrs.get(3)));
				}
			}
			
			Selectable table2=jobSel.xpath("//table[2]");
			List<String> table2TRList=table2.xpath("tr").all();
			
			for(int i=0;i<table2TRList.size();i++){
				
				List<String> tdStrs=table2.xpath("//tr["+(i+1)+"]/td").all();
				
				if(tdStrs.size()==2){
					
					propsMap.put(clearHtml(tdStrs.get(0)), clearHtml(tdStrs.get(1)));
					
				}else if(tdStrs.size()==4){
					propsMap.put(clearHtml(tdStrs.get(0)), clearHtml(tdStrs.get(1)));
					propsMap.put(clearHtml(tdStrs.get(2)), clearHtml(tdStrs.get(3)));
				}
			}
			
			salary=propsMap.get("职位月薪");
			
			String jobDesc=pageHtml.$("div.terminalpage-content >ol").toString();
			
			String companyDesc=pageHtml.xpath("//div[@class='terminalpage-content clearfix']").toString();
			
			//save 
			page.putField("jobTitle", title.trim());
			page.putField("company", companyName.trim());
			page.putField("salary",salary);
			
			page.putField("keyword", "");
			page.putField("descr", jobDesc.trim());
			page.putField("companyDesc", companyDesc);
			
			page.putField("props", propsMap);
			
			page.putField("url", pageUrl);
			page.putField("source", "zhaopin");
			
		}else{
			page.setSkip(true);
		}        
        //分页、列表
         page.addTargetRequests(pageRefLinks.regex("http://[\\w,\\/-_]+.zhaopin.com/[\\w,\\/.-?&_]+").all());
        
		synchronized (doneLinks) {   
        	doneLinks.put(pageUrl, doneNum++);
        	SpiderRecord.addKeyNum("Lagou_all", doneNum);
        }
		
	}

	private String clearHtml(String tag){
		
		return StringUtil.html2text(tag).trim();
	}
	
}
