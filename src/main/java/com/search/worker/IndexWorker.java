package com.search.worker;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
			index.doWork();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
