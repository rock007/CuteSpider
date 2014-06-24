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

    //属性
  	HashMap<String,String> propsMap=new HashMap<String,String>();
  		
	public Spider51jobProcessor(){
    	
    	site.setDomain("51job.com");
    	site.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");
    	
    	this.site.setCharset("gb2312");
    }
	
	@Override
	public Site getSite() {
		return site;
	}
	
	@Override
	public void process(Page page) {

		String title="",companyName="",salary="";
		String jobDesc,companyDesc;
		
		
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
			
			Selectable jobHtml=pageHtml.xpath("//div[@class='s_txt_jobs']");
			
			List<String> job_1_List=jobHtml.xpath("//table[@class='jobs_1']").all();
			
			Selectable tableSel,trSel,tdSel;
			String propsHtml;
			
			for(int i=1;i<job_1_List.size()+1;i++){
				
				tableSel=jobHtml.xpath("//table[@class='jobs_1']["+i+"]");
				
				System.out.println(tableSel.toString());
				
				if(i==1){
					
					title=tableSel.xpath("//tr[1]/td/text()").toString();
					
					companyName=tableSel.xpath("//tr[2]/td/").xpath("//a/text()").get();
					
					String content= tableSel.xpath("//tr[3]/td/html()").get();
					
					String str[]= content.split("<br /><br />");
					
					for(String m:str){
						
						logger.debug(m);
						propsHtml=StringUtil.html2text(m);
						int plusIndex=propsHtml.indexOf("：");
						
						if(plusIndex>0){
							
							propsMap.put(propsHtml.substring(0,plusIndex).trim(), propsHtml.substring(plusIndex+1).trim());
						}
						
					}
					
				}
				
			}
			
			tableSel=jobHtml.xpath("//table[@width='98%']");
			if(tableSel!=null){
				
				List<String> trList=tableSel.xpath("//tr").all();
				
				for(int ii=1;ii<trList.size()+1;ii++){
				
					trSel=tableSel.xpath("//tr["+ii+"]");

					tdSel=tableSel.xpath("//tr["+ii+"]/td");
					
					List<String> tdList=tableSel.xpath("//tr["+ii+"]/td").all();
					
					if(tdList.size()==2){
						
						addProps(tdList.get(0),tdList.get(1));
						
					}else if(tdList.size()==4){
						
						addProps(tdList.get(0),tdList.get(1));
						addProps(tdList.get(2),tdList.get(3));
						
					}else if(tdList.size()==6){
						
						addProps(tdList.get(0),tdList.get(1));
						addProps(tdList.get(2),tdList.get(3));
						addProps(tdList.get(4),tdList.get(5));
						
					}else{
						
						String key=tdSel.xpath("//strong/text()").get();
						String value=tableSel.xpath("//tr["+ii+"]/td/text()").get();
						addProps(key,value);
						
					}
					
				}
				
			}
			
			salary=propsMap.get("薪水范围");
			if(salary==null)salary="";
			
			companyDesc=jobHtml.xpath("//div[@class='jobs_txt']/html()").get();
			
			jobDesc=jobHtml.xpath("//div[@style='padding-bottom:30px;']/html()").get();
			
			//save 
			page.putField("jobTitle", title.trim());
			page.putField("company", companyName.trim());
			page.putField("salary",salary);
			
			page.putField("keyword", "");
			page.putField("descr", jobDesc.trim());
			page.putField("companyDesc", companyDesc);
			
			page.putField("props", propsMap);
			
			page.putField("url", pageUrl);
			page.putField("source", "51job");
			
		}else{
			page.setSkip(true);
		}        
        //分页、列表
         page.addTargetRequests(pageRefLinks.regex("http://[\\w,\\/-]+.51job.com/[\\w,\\/.-]+").all());
        
		synchronized (doneLinks) {   
        	doneLinks.put(pageUrl, doneNum++);
        	SpiderRecord.addKeyNum("51job_all", doneNum);
        }
		
	}
	
	private void addProps(String m1,String m2){
		
		if(m1==null||m2==null) return;
		
		m1=StringUtil.html2text(m1);
		m2=StringUtil.html2text(m2);
		m1=m1.trim();
		m2=m2.trim();
		
		if(m1.length()>1&&m2.length()>1){
			
			propsMap.put(m1.replaceAll(":", "").replaceAll("：", ""), m2.trim());	
		}
		
	}

}
