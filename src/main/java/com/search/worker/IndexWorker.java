package com.search.worker;

import java.io.File;
import java.util.HashMap;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.search.comm.ResultModel;
import com.search.db.model.vo.JobVO;
import com.search.form.model.SearchForm;
import com.search.lucence.IndexSearch;
import com.search.lucence.JobIndexFile;


public class IndexWorker {

	/***
	 * 建立索引
	 * @param args
	 */
	public static void main(String[] args) {
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"classpath:/spring/applicationContext*.xml");

		//IndexSearch  search=applicationContext.getBean("indexSearch", IndexSearch.class);
		
		JobIndexFile index = applicationContext.getBean("jobIndexFile", JobIndexFile.class);

		try {
			
			String curDate="";
			if(args.length==1){
				
				curDate=args[0];
			}
			
			index.doWork(curDate);
			
			/*** demo 2
			SearchForm form=new SearchForm();
			form.setKeyword("java");
			form.setAddress("上海");
			
			ResultModel<JobVO> result=search.doSearch(form, 1, 10);
			
			System.out.println(result.getMessage());
			 ****/
			
			/****	 demo3
		   	HashMap<String, Integer>  map=search.group("title", form.getKeyword());
		   	System.out.println(map.toString());
			 ***/
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
