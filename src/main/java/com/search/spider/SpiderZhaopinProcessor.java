package com.search.spider;

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

public Site site = Site.me().setRetryTimes(3).setSleepTime(3000);
    
	private static final Logger logger = LoggerFactory.getLogger(SpiderZhaopinProcessor.class);

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
		
		String keyStr,valueStr;
		
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
			
			String pageTitle[]=pageHtml.getDocument().title().split("-");
			
			title=pageTitle[0].replaceAll("招聘", "");
			companyName=pageTitle[1];
			
			List<String> keyList= pageHtml.xpath("//ul[@class='terminal-ul']/li").all();
			
			String line="",lines[];
			for(int ii=0;ii< keyList.size();ii++){
				
				if(keyList.get(ii)==null) {
					logger.warn("why keylist is null!");
					continue;
				}
				
				line=StringUtil.html2text(keyList.get(ii));
				lines=line.split("：");
				
				if(lines.length!=2) continue;
				
				keyStr=lines[0];
				valueStr= lines[1];
				
				logger.debug(keyStr+":"+valueStr);
				if(!(valueStr==null||keyStr==null)){
					
					if(keyStr.trim().length()>0&&valueStr.trim().length()>0)
						propsMap.put(keyStr, valueStr);	
				}
			}
			
			salary=propsMap.get("职位月薪");
			if(salary==null)salary="";
			
			List<String> textList= pageHtml.xpath("//div[@class='tab-cont-box']/div[@class='tab-inner-cont']").all();
			
			String jobDesc=textList.get(0);
			String companyDesc=textList.get(1);
			
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
		//page.addTargetRequests(pageRefLinks.regex(regEx).all());
		//page.addTargetRequests(pageRefLinks.regex("http://sou.zhaopin.com/[\\w,\\/.-?&_]+").all());
		
		
		synchronized (doneLinks) {   
        	doneLinks.put(pageUrl, doneNum++);
        	SpiderRecord.addKeyNum("Zhaopin_all", doneNum);
        }
		
	}

	private String clearHtml(String tag){
		
		return StringUtil.html2text(tag).replaceAll("：", "").trim();
	}
	
}
