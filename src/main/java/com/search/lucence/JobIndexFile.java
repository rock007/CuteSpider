package com.search.lucence;

import java.io.File;
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

	public void doWork() throws Exception {

		log.warn("index rebuild begin !");
		
		int start=0,limit=100;
		String pathFile = indexDir;

		long  begin=System.currentTimeMillis();
		
		dir = FSDirectory.open(new File(pathFile));
		IndexWriter writer = getWriter();
		
		//remove 
		//!!!writer.deleteAll();
		
		String today=StringUtil.Date2String();
		
		while(true){
			List<Job> jobs = jobDao.getByStatus(0,today,start,limit);	
			
			if(jobs.size()==0)break;
			
			for (int i = 0; i < jobs.size(); i++) {

				Document doc = new Document();

				doc.add(new LongField("jid", jobs.get(i).getJid(),Store.YES));
				doc.add(new StringField("title", jobs.get(i).getTitle(), Store.YES));
				doc.add(new TextField("companyDesc", jobs.get(i).getCompanyDesc(),
						Store.YES));
				doc.add(new TextField("companyDescHtml", jobs.get(i).getCompanyDescHtml(),
						Store.YES));
				
				if(jobs.get(i).getCompanyName()!=null){
					doc.add(new StringField("companyName",
						jobs.get(i).getCompanyName(), Store.YES));
				}
				
				doc.add(new TextField("desc", jobs.get(i).getDesc(), Store.YES));
				doc.add(new TextField("descHtml", jobs.get(i).getDescHtml(), Store.YES));
				
				
				if(jobs.get(i).getSalary()!=null){
					doc.add(new StringField("salary", jobs.get(i).getSalary(),
							Store.YES));
				}
				
				doc.add(new StringField("source", jobs.get(i).getSource(),
						Store.YES));
				doc.add(new StringField("url", jobs.get(i).getUrl(), Store.YES));

				List<Prop> propList=propDao.getPropListBySourceId(jobs.get(i).getJid());
				for(Prop p:propList){
					
					doc.add(new StringField(p.getKey(), p.getValue(), Store.YES));	
				}
				
				writer.addDocument(doc);
				
				log.info(" start:"+start+" jobId:"+jobs.get(i).getJid()+"	"+ jobs.get(i).getTitle());
			}
			
			start=start+limit;
		}
		writer.commit();
		log.warn("index rebuild over !");
		writer.close();
		
		//fix problem
		jobDao.sp_pre_fix();
		
        long  end=System.currentTimeMillis();
        log.error(today+"添加新索引共耗时"+(end-begin)/1000*60+"分钟，共计"+start+"个新职位");
	}

	public IndexWriter getWriter() throws Exception {

		Analyzer analyzer = new IKAnalyzer(); // 二元ik分词

		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_48,
				analyzer);
		return new IndexWriter(dir, iwc);
	}

	public String getIndexDir() {
		return indexDir;
	}

	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}
}
