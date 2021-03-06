package com.search.spider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.search.comm.StringUtil;
import com.search.db.dao.FetchLogDao;
import com.search.db.model.FetchLog;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

public class SpiderManager {

	private static final Logger logger = LoggerFactory.getLogger(SpiderManager.class);
	
	private HashMap<String,String> spiderSet;
	
	private int perSpiderNum;//进程个数
	
	private HashMap<String,Spider> spiderMap=new HashMap<String,Spider>();
	
    @Qualifier("savePipeline")
    @Autowired
    private SavePipeline savePipeline;
    
    @Qualifier("savePipeline4wb")
    @Autowired
    private SavePipeline4wb savePipeline4wb;
    
    @Resource
    private FetchLogDao fetchLogDao;
    
    String today="";
    
	public void lesgo(){
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext*.xml");
		today=StringUtil.Date2String();
		
		for (Iterator<String> it = spiderSet.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			
			logger.debug(key + "=" + spiderSet.get(key));

			String spiderName=key;
			String spierUrlStr=spiderSet.get(key);
			
			//eg:Lagou
			PageProcessor oneProcessor=applicationContext.getBean("Spider"+spiderName+"Processor",PageProcessor.class);
			
			Spider oneSpider=Spider.create(oneProcessor);
			
			String[] spiderUrls= spierUrlStr.split("\n");
			
			for(String u:spiderUrls){
				
				if(u.trim().length()>5)
					oneSpider.addUrl(u.trim());	
			}
			
			//oneSpider.addPipeline(new ConsolePipeline());
			oneSpider.addPipeline(savePipeline4wb);
			oneSpider.thread(perSpiderNum);
	        //启动爬虫
			oneSpider.start();
			spiderMap.put(spiderName, oneSpider);
		}
		
	}

	public void stop() {

		for (Iterator<String> it = spiderMap.keySet().iterator(); it.hasNext();) {
			String key = it.next();

			try {
				spiderMap.get(key).stop();
			} catch (Exception ex) {
				logger.error("关闭spider:" + key + "，失败！", ex);
			}
		}
	}
	
	public synchronized void keepLog(){
		
		logger.warn("keepLog begin");
		for (Iterator<String> it = spiderMap.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			key=key.toLowerCase();
			
			if(SpiderRecord.getNum(key)==0) continue;
			
			FetchLog log=new FetchLog();
			
			List<FetchLog> list= fetchLogDao.getBy(today, key);
			if(list.size()==0){
				
				log.setDay(today);
				log.setSite(key);
				log.setFetchNum(SpiderRecord.getNum(key+"_all"));
				log.setValidNum(SpiderRecord.getNum(key));
				
				fetchLogDao.add(log);
			}else{
				log=list.get(0);
				log.setFetchNum(log.getFetchNum()+SpiderRecord.getNum(key+"_all"));
				log.setValidNum(log.getValidNum()+SpiderRecord.getNum(key));
				
				fetchLogDao.update(log);
			}
		}		
		logger.warn("keepLog end");
	}
	
	public void print(){
		String str="";
		
		for (Iterator<String> it = spiderMap.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			Spider oneSpider=spiderMap.get(key);

			str="spider:"+key+" "+oneSpider.getUUID()+"\n";
			str+="\rstartTime:"+oneSpider.getStartTime()+"status:"+oneSpider.getStatus();
			
			System.out.println(str);
		}
	}
	
	public HashMap<String, String> getSpiderSet() {
		return spiderSet;
	}

	public void setSpiderSet(HashMap<String, String> spiderSet) {
		this.spiderSet = spiderSet;
	}

	public int getPerSpiderNum() {
		return perSpiderNum;
	}

	public void setPerSpiderNum(int perSpiderNum) {
		this.perSpiderNum = perSpiderNum;
	}
}
