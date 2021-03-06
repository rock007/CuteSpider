package com.search.spider;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.search.comm.StringUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;


@Component("SpiderLiepinProcessor")
public class SpiderLiepinProcessor implements PageProcessor{

	private static final Logger logger = LoggerFactory.getLogger(SpiderLiepinProcessor.class);
	
    public Site site = Site.me().setRetryTimes(3).setSleepTime(3000);
    
    private String pageUrl;
    private static HashMap<String,Integer> doneLinks=new HashMap<String,Integer>();
    private static Integer doneNum=0;
    
    public SpiderLiepinProcessor(){
    	
    	site.setDomain("liepin.com");
    	site.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");
    }
    
	@Override
	public Site getSite() {

		return site;
	}
	@Override
	public void process(Page page) {
		
		pageUrl=page.getRequest().getUrl();
		
		Html pageHtml=page.getHtml();
		Selectable pageRefLinks= page.getHtml().links();
		//需要检查链接是否正确 eg:http://www.liepin.com/zhaopin/www.liepin.com/zhaopin/yuleyundong_shanghai/
		
		//1.页面是否已经存在过
		synchronized (doneLinks) {  
			if(doneLinks.containsKey(pageUrl)){
			
				page.setSkip(true);
				return;
			}
		}
		
		Selectable mainSel=pageHtml.css(".main > .title ");
		String title=mainSel.xpath("//h1/text()").toString();
		String companyName=mainSel.xpath("//h3/text()").toString();
		
		if(mainSel!=null&&title!=null){
			
			page.putField("jobTitle", title);
			
			List<String> keywords= mainSel.xpath("//div[@class='tag-list clearfix']/span").all();
			String keywordStr="";
			for(int i=0;i<keywords.size();i++){
				
				keywordStr+=mainSel.xpath("//div[@class='tag-list clearfix']/span["+(i+1)+"]/text()").toString()+",";
			}
			page.putField("keyword", keywordStr);
			
			//Selectable companySel= mainSel.xpath("//div[@class='job-title']");
			
			page.putField("company", companyName);
			//职位年薪
			page.putField("salary",mainSel.xpath("//p[@class='job-main-title']/text()").toString());
			
			HashMap<String,String> propsMap=new HashMap<String,String>();
			List<String> keyList= mainSel.$(" ul >li").all();
			for(String keyStr:keyList){
				
				keyStr=StringUtil.htmlRemoveTag(keyStr);
				
				logger.debug(keyStr);
				String [] poros=keyStr.split("：");
				if(poros.length==2){
					propsMap.put(poros[0].trim(), poros[1].trim());	
				}
			}
			
			//公司
			Selectable sideSel=pageHtml.css(".side");
			String companyProps=sideSel.xpath("//div[@class='content content-word']").toString();		
			String line=StringUtil.html2text(companyProps);
			
			String lines[]=line.split(" ");
			
			propsMap.put(lines[0].trim(), lines[1].trim());
			for(int i=2;i<lines.length;i++) {
				
				String temp[]=lines[i].split("：");
				if(temp.length==2) {
					propsMap.put(temp[0].trim(), temp[1].trim());
				}
			}
			
			page.putField("props", propsMap);
			//职位描述
			String descr=mainSel.xpath("//div[@class='content content-word']").all().get(0).toString();
			page.putField("descr", descr);
			
			//公司简介
			String companyDesc=mainSel.xpath("//div[@class='content content-word']").all().get(1).toString();
			page.putField("companyDesc", companyDesc);
			
			page.putField("url", pageUrl);
			page.putField("source", "liepin");
		}else{
			
			page.setSkip(true);
		}
		
        //4.处理链接
        page.addTargetRequests(pageRefLinks.regex("http://job.liepin.com/\\d+_\\d+").all());
        
        //分页
        page.addTargetRequests(pageRefLinks.regex("(http://www.liepin.com/zhaopin/\\?pubTime=\\d+&dqs=\\d+&curPage=\\d+)").all());
        
        //xx热门职位
        //page.addTargetRequests(pageRefLinks.regex("(www.liepin.com/zhaopin/\\S+_\\S+)").all());
        
        //其他城市招聘
        //page.addTargetRequests(pageRefLinks.regex("www\\.liepin\\.com/zhaopin/[a-z]+/").all());
		
        synchronized (doneLinks) {   
        	doneLinks.put(pageUrl, doneNum++);
        	SpiderRecord.addKeyNum("Liepin_all", doneNum);
        }
	}

}
