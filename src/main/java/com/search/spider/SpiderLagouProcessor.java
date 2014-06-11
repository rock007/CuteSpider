package com.search.spider;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import com.search.comm.StringUtil;

@Component("SpiderLagouProcessor")
public class SpiderLagouProcessor implements PageProcessor{

	private static final Logger logger = LoggerFactory.getLogger(SpiderLagouProcessor.class);
	
    public Site site = Site.me().setRetryTimes(3).setSleepTime(3000);
    
    private String pageUrl;
    private static HashMap<String,Integer> doneLinks=new HashMap<String,Integer>();
    private static Integer doneNum=0;
    
    public SpiderLagouProcessor(){
    	
    	site.setDomain("lagou.com");
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
		
		//左边职位介绍
		Selectable mainSel=pageHtml.css(".content_l > dl.job_detail ");
		
		//公司介绍
		Selectable job_company_Sel=pageHtml.css(".content_r > dl.job_company ");
					
		title=mainSel.xpath("//dl[@class='job_detail']/dt//h1/text()").toString();
		
		if(mainSel!=null&&title!=null){
			
			page.putField("jobTitle", title.trim());
			
			//属性
			HashMap<String,String> propsMap=new HashMap<String,String>();
			
			//职位要求
			String titleProp="";
			List<String> job_require_list= mainSel.xpath("//dd[@class='job_request']/span").all();
			for(Integer i=1;i<=job_require_list.size();i++){
				
				keyStr=mainSel.xpath("//dd[@class='job_request']/span["+i+"]/text()").toString().trim();
				
				if(i==1){
					titleProp="年薪";
					
				}else if(i==2){
					titleProp="工作地点";
				}else if(i==3){
					titleProp="工作年限";
				}else if(i==4){
					titleProp="学历要求";
				}else if(i==5){
					titleProp="职位类别";
				}else{
					titleProp=i.toString();
				}
				
				if(keyStr.length()>0){
					propsMap.put(titleProp, keyStr.trim());	
				}
			}
			
			String keywords= mainSel.xpath("//dd[@class='job_request']/text()").toString();
			
			keywords=keywords.replaceAll("职位诱惑 :", "");
			page.putField("keyword", keywords.trim());
			
			String jobDate= mainSel.xpath("//dd[@class='job_request']/div/text()").regex("\\d+").toString();
			if(jobDate!=null&&!"".equals(jobDate)){
				propsMap.put("发布日期", StringUtil.Date2String(StringUtil.getBeforeDate(new Date(), Integer.parseInt(jobDate))));
			}
			String company= mainSel.xpath("//dt/h1/div/text()").toString();
			company=company.replaceAll("招聘", "");
			
			page.putField("company", company.trim());
			//职位年薪
			page.putField("salary",propsMap.get("年薪"));
			
			
			//职位描述
			String descr=mainSel.xpath("//dd[@class='job_bt']/html()").toString();
			descr=descr.replaceAll("<h3 class=\"description\">职位描述</h3>", "");
			
			page.putField("descr", descr.trim());
			
			//公司简介
			List<String> keyList= job_company_Sel.xpath("//ul/li").all();
			
			for(int ii=1;ii<= keyList.size();ii++){
				
				keyStr=job_company_Sel.xpath("//ul/li["+ii+"]/span/text()").toString();
				valueStr= job_company_Sel.xpath("//ul/li["+ii+"]/text()").toString();
				
				logger.debug(keyStr+":"+valueStr);
				if(!(valueStr==null||keyStr==null)){
					
					if(keyStr.trim().length()>0&&valueStr.trim().length()>0)
						propsMap.put(keyStr.trim(), valueStr.trim());	
				}
			}
			page.putField("props", propsMap);
			
			page.putField("companyDesc", "");
			
			
			page.putField("url", pageUrl);
			page.putField("source", "lagou");
		}else{
			
			page.setSkip(true);
		}
		
        //4.处理链接
        page.addTargetRequests(pageRefLinks.regex("http://www.lagou.com/jobs/\\d+.html").all());
        
        //分页、列表
         page.addTargetRequests(pageRefLinks.regex("(http://www.lagou.com/jobs/list_([\\w?=&%]+))").all());
        
		synchronized (doneLinks) {   
        	doneLinks.put(pageUrl, doneNum++);
        	SpiderRecord.addKeyNum("Lagou_all", doneNum);
        }
	}

}