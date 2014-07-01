package com.search.lucence;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class SuggestSupport {
	
	private  String INDEX_FILE = "/Users/fuhe-apple-02/temp/index";
	private  String INDEX_FILE_SPELL = "/Users/fuhe-apple-02/temp/spell";

	private  String INDEX_FIELD = "title";//,salary,companyName
	
	public SuggestSupport(){
		
	}
	
	public SuggestSupport(String indexDir,String indexSpellDir,String indexField) throws Exception{
		
		INDEX_FILE=indexDir;
		INDEX_FILE_SPELL=indexSpellDir;
		INDEX_FIELD=indexField;
	}
	
	 public  String[] doWork(String key) {

	    	String words[]={};
	        try {
	           
	        	if(key==null||"".equals(key)){
	        		
	        		return words;
	        	}
	        	
	        	 //指定域所用的分析器
	            PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new IKAnalyzer(
	                    true));

	            // 索引读取的配置 read index conf
	            IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_48, wrapper);
	            conf.setOpenMode(OpenMode.CREATE_OR_APPEND);

	            //读取字典 read dictionary
	            Directory directory = FSDirectory.open(new File(INDEX_FILE));
	            RAMDirectory ramDir = new RAMDirectory(directory, IOContext.READ);
	            DirectoryReader indexReader = DirectoryReader.open(ramDir);

	            LuceneDictionary  dic = new LuceneDictionary(indexReader, INDEX_FIELD);
	        	
	            SpellChecker sc = new SpellChecker(FSDirectory.open(new File(INDEX_FILE_SPELL)));
	            sc.setAccuracy(0.5f);
	            sc.indexDictionary(dic, conf, true);
	            
	            words = sc.suggestSimilar(key, 10);
	            sc.close();
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return words;
	    }
	 
		public static void main(String[] args) {
			
			SuggestSupport mm=new SuggestSupport();
			
			mm.doWork("北京");
		}
}
