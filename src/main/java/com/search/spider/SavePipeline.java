package com.search.spider;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.search.comm.StringUtil;
import com.search.db.dao.JobDao;
import com.search.db.dao.PropDao;
import com.search.db.model.Job;
import com.search.db.model.Prop;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Component("savePipeline")
public class SavePipeline implements  Pipeline{

	private static final Logger logger = LoggerFactory.getLogger(SavePipeline.class);
	
    @Resource
    private JobDao jobDao;
    
    @Resource
    private PropDao propDao;
    
	@Override
	public void process(ResultItems resultItems, Task task) {
		
		Job m=new Job();
		//判断是否已经存在
		String url= (String)resultItems.get("url");
		
		if(url==null||"".equals(url)) {
			
			return;
		}
		List<Job>  existJobs=jobDao.getByUrl(url);
		if(existJobs.size()>0){
			
			logger.debug(url+":已经存在，不处理，跳过,count:"+existJobs.size());
			return;
		}
		
		String source=(String)resultItems.get("source");
		
		
		if(source.equals("lagou")){
		
				Integer lagouNum=SpiderRecord.recordMap.get("Lagou");
			
				if(lagouNum==null)lagouNum=0;
			
				lagouNum+=1;
				SpiderRecord.addKeyNum("Lagou", lagouNum);
				
		}else if(source.equals("liepin")){
			
				Integer liepinNum=SpiderRecord.recordMap.get("Liepin");
			
				if(liepinNum==null)liepinNum=0;
			
				liepinNum+=1;
				SpiderRecord.addKeyNum("Liepin", liepinNum);
		}
		
		
		String companyDescHtml=(String)resultItems.get("companyDesc");
		String jobDescHtml=(String)resultItems.get("descr");
		
		m.setTitle((String)resultItems.get("jobTitle"));
		m.setCompanyDesc(StringUtil.html2text(companyDescHtml));
		m.setCompanyDescHtml(companyDescHtml);
		
		m.setCompanyName((String)resultItems.get("company"));
		m.setCreateDate(StringUtil.Date2String());
		
		m.setDesc(StringUtil.html2text(jobDescHtml));
		m.setDescHtml(jobDescHtml);
		
		m.setSalary((String)resultItems.get("salary"));
		//m.setSource("liepin");
		m.setSource((String)resultItems.get("source"));
		m.setUpdateDate(StringUtil.Date2String());
		m.setUrl((String)resultItems.get("url"));
		
		int jid= jobDao.add(m);
		
		HashMap<String,String> propsMap =resultItems.get("props");
		
		if(jid>0&&propsMap.size()>0){
			//add prop
			 Iterator<Entry<String, String>> it = propsMap.entrySet().iterator();
			 
			 while (it.hasNext()) {
			        Map.Entry<String,String> pairs = (Map.Entry<String,String>)it.next();
			        
			        System.out.println(pairs.getKey() + " = " + pairs.getValue());
			
			        Prop p=new Prop();
			        p.setSourceId(m.getJid());
			        p.setKey(pairs.getKey());
			        p.setValue(pairs.getValue());
					
					propDao.add(p);
			  }
		}
		
	}	
}
