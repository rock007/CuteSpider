package com.search.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.search.comm.StringUtil;
import com.search.spider.SavePipeline;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class SpiderLiepinProcessor implements PageProcessor{

	private static final Logger logger = LoggerFactory.getLogger(SpiderLiepinProcessor.class);
	
    private Site site = Site.me().setRetryTimes(3).setSleepTime(3000);
    
    private String pageUrl;
    private HashMap<String,Integer> doneLinks=new HashMap<String,Integer>();
    private Integer doneNum=0;
    
    public SpiderLiepinProcessor(){
    	
    	site.setDomain("liepin.com");
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
		/***
		for(String m: pageRefLinks.all()){
			
			if(m.indexOf("www.liepin.com/zhaopin")>8){
				
				m=m.replaceAll("http://www.liepin.com/zhaopin/", "");
			}
			
		}
		***/
		
		//1.页面是否已经存在过
		if(doneLinks.containsKey(pageUrl)){
			
			page.setSkip(true);
			return;
		}
		
		Selectable mainSel=pageHtml.css(".main > .main-view ");
		String title=mainSel.xpath("//h1/text()").toString();
		
		if(mainSel!=null&&title!=null){
			
			page.putField("jobTitle", title);
			
			List<String> keywords= mainSel.xpath("//div[@class='tag-list clearfix']/span").all();
			String keywordStr="";
			for(int i=0;i<keywords.size();i++){
				
				keywordStr+=mainSel.xpath("//div[@class='tag-list clearfix']/span["+(i+1)+"]/text()").toString()+",";
			}
			page.putField("keyword", keywordStr);
			
			Selectable companySel= mainSel.xpath("//div[@class='job-title']");
			
			page.putField("company", companySel.xpath("//p[1]/a/text()"));
			//职位年薪
			page.putField("salary",companySel.xpath("//p[2]/strong[2]/text()"));
			
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
			page.putField("props", propsMap);
			//职位描述
			String descr=mainSel.xpath("//div[@class='content content-word']/html()").toString();
			page.putField("descr", descr);
			
			//公司简介
			String companyDesc=mainSel.xpath("//div[@class='content content-word']/html()").toString();
			page.putField("companyDesc", companyDesc);
		}
		
        //4.处理链接
        page.addTargetRequests(pageRefLinks.regex("http://job.liepin.com/\\d+_\\d+").all());
        
        //分页
        page.addTargetRequests(pageRefLinks.regex("(http://www.liepin.com/zhaopin/\\?pubTime=\\d+&dqs=\\d+&curPage=\\d+)").all());
        
        //xx热门职位
        page.addTargetRequests(pageRefLinks.regex("(www.liepin.com/zhaopin/\\S+_\\S+)").all());
        
        //其他城市招聘
        //page.addTargetRequests(pageRefLinks.regex("www\\.liepin\\.com/zhaopin/[a-z]+/").all());
		
		doneLinks.put(pageUrl, doneNum++);
	}

    public static void main(String[] args) {

    	SpiderLiepinProcessor processor=new SpiderLiepinProcessor();
    	processor.site.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");
    	
        Spider.create(processor)
                .addUrl("http://www.liepin.com/zhaopin/")
                .addPipeline(new ConsolePipeline())
                .addPipeline(new SavePipeline())
                //开启5个线程抓取
                .thread(1)
                //启动爬虫
                .run();
        
        try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }
}
