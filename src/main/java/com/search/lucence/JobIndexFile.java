package com.search.lucence;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.search.comm.StringUtil;
import com.search.db.dao.JobDao;
import com.search.db.dao.PropDao;
import com.search.db.model.Job;
import com.search.db.model.Prop;

public class JobIndexFile {

	private static final Logger log = Logger.getLogger(JobIndexFile.class);  
	
	private String indexDir;
	
	@Resource
	private JobDao jobDao;

	@Resource
	private PropDao propDao;

	private Directory dir;

	public void doWork(String createDate)throws Exception{
		
		log.warn("index rebuild begin !");
		
		int start=0,limit=30;
		String pathFile = indexDir;
		boolean isDeleteAll=false;
		
		long  begin=System.currentTimeMillis();
		
		dir = FSDirectory.open(new File(pathFile));
		
		if(createDate.equals("")){
			isDeleteAll=true;
		}
		
		if (IndexWriter.isLocked(dir)) {
			IndexWriter.unlock(dir);
		}
		
		IndexWriter writer = getWriter(isDeleteAll);
		
		//全部重新索引
		if(isDeleteAll){
			
			writer.deleteAll();
			writer.commit();
			log.warn("index clear all !");
			
		}else{
			
			log.warn("index build  for Date :"+createDate);
			
		}
		
		//fix problem
		jobDao.sp_pre_fix();
		
		List<Job> jobs =new ArrayList<Job>();
		while(true){
			
			if(isDeleteAll){
				 jobs = jobDao.getAllByStatus(0,start,limit);	
			}else{
				 jobs = jobDao.getByStatus(0,createDate,start,limit);		
			}
			
			if(jobs.size()==0)break;
			
			for (int i = 0; i < jobs.size(); i++) {

				try{
					Document doc = new Document();

					doc.add(new LongField("jid", jobs.get(i).getJid(),Store.YES));
					doc.add(makeTextField("title", jobs.get(i).getTitle()));
					doc.add( makeTextField("companyDesc", jobs.get(i).getCompanyDesc()));
					doc.add( makeTextField("companyDescHtml", jobs.get(i).getCompanyDescHtml()));
					
					doc.add(makeStringField("companyName",
							jobs.get(i).getCompanyName()));
					
					doc.add( makeTextField("desc", jobs.get(i).getDesc()));
					doc.add(makeTextField("descHtml", jobs.get(i).getDescHtml()));
					
					doc.add(makeStringField("salary", jobs.get(i).getSalary()));
					
					
					doc.add(makeStringField("source", jobs.get(i).getSource()));
					doc.add(makeStringField("url", jobs.get(i).getUrl()));
					
					//createDate
					doc.add(makeStringField("createDate", jobs.get(i).getCreateDate()));
					
					List<Prop> propList=propDao.getPropListBySourceId(jobs.get(i).getJid());
					for(Prop p:propList){
						
						doc.add( makeStringField(p.getKey(), p.getValue()));	
					}
					
					writer.addDocument(doc);
					
					log.info(" start:"+start+" jobId:"+jobs.get(i).getJid()+"	"+ jobs.get(i).getTitle());
					
				}catch(Exception ex){
				
					log.error("index err", ex);
					
				}
			}
			
			start=start+limit;
			writer.commit();	
		}
		//writer.commit();
		log.warn("index rebuild over !");
		writer.close();
		
        long  end=System.currentTimeMillis();
        log.error(createDate+"  添加新索引共耗时"+((end-begin)/(1000*60))+"分钟，共计"+start+"个新职位");
	}
	
	public void doWork() {

		try{
			doWork(StringUtil.Date2String());
		}catch(Exception ex){
			
			log.error("index exception!!!", ex);
		}
	}

	public IndexWriter getWriter(boolean create) throws Exception {

		Analyzer analyzer = new IKAnalyzer(); // 二元ik分词

		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_48,
				analyzer);

		iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		return new IndexWriter(dir, iwc);
	}

	private StringField makeStringField(String field,String value){
		
		if(value==null||value.equals("")){
			
			return new StringField(field, "",
					Store.YES);
		}else{
			return new StringField(field, value,
					Store.YES);
		}
	}
	
	private TextField makeTextField(String field,String value){
		
		if(value==null||value.equals("")){
			
			return new TextField(field, "",
					Store.YES);
		}else{
			return new TextField(field, value,
					Store.YES);
		}
	}
	
	public String getIndexDir() {
		return indexDir;
	}

	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}
}
