package com.search.lucence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.grouping.AbstractAllGroupHeadsCollector;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.search.grouping.term.TermAllGroupHeadsCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class GroupSupport {
	
	public void group(IndexSearcher indexSearcher,String groupField,String content) throws IOException, ParseException {
	
	        GroupingSearch groupingSearch = new GroupingSearch(groupField);
	
	        groupingSearch.setFillSortFields(true);
	
	        groupingSearch.setCachingInMB(4.0, true);
	
	        groupingSearch.setAllGroups(true);
	
	        Analyzer analyzer=new IKAnalyzer(true);
	        String[] fields=new String[]{"title","companyName","desc"};
	        MultiFieldQueryParser  multiQParser=new MultiFieldQueryParser(Version.LUCENE_48, fields, analyzer);
	        
	        Query query = multiQParser.parse(content);
	
	        TopGroups<BytesRef> result = groupingSearch.search(indexSearcher, query, 0, 10);
	
	        System.out.println("搜索命中数：" + result.totalHitCount);
	
	        System.out.println("搜索结果分组数：" + result.groups.length);

	        HashMap<String,Integer> topMap=new HashMap<String,Integer>(); 
	        
	        for (GroupDocs<BytesRef> groupDocs : result.groups) {
	
	            System.out.println("分组：" + groupDocs.groupValue.utf8ToString());
	
	            System.out.println("组内记录：" + groupDocs.totalHits);
	
	            topMap.put(groupDocs.groupValue.utf8ToString(), groupDocs.totalHits);
	        }	        
	    }
	
	public static void main(String[] args) throws IOException, ParseException {
		
        Directory dir=FSDirectory.open(new File("/Users/fuhe-apple-02/temp/index"));
        IndexReader reader=DirectoryReader.open(dir);
        IndexSearcher searcher=new IndexSearcher(reader);
		
        GroupSupport groupTest = new GroupSupport();
		
		groupTest.group(searcher,"title", "java");
		
   }
		
			
	
}
