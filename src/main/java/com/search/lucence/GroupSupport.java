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
	
	        //groupingSearch.setGroupSort(new Sort(SortField.FIELD_SCORE));
	        //groupingSearch.setGroupSort(Sort.INDEXORDER);
	        
	        groupingSearch.setFillSortFields(true);
	
	        groupingSearch.setCachingInMB(4.0, true);
	
	        groupingSearch.setAllGroups(true);
	
	        //!!!groupingSearch.setAllGroupHeads(true);
	
	        //groupingSearch.setGroupDocsLimit(10); 
	
	        //QueryParser parser = new QueryParser(Version.LUCENE_48, groupField, new IKAnalyzer(true));
	        Analyzer analyzer=new IKAnalyzer(true);
	        String[] fields=new String[]{"title","companyName","desc"};
	        MultiFieldQueryParser  multiQParser=new MultiFieldQueryParser(Version.LUCENE_48, fields, analyzer);
	        
	        Query query = multiQParser.parse(content);
	
	        TopGroups<BytesRef> result = groupingSearch.search(indexSearcher, query, 0, indexSearcher.getIndexReader().maxDoc());
	
	        System.out.println("搜索命中数：" + result.totalHitCount);
	
	        System.out.println("搜索结果分组数：" + result.groups.length);
	 
	        Document document;
	        HashMap<Integer,String> topMap=new HashMap<Integer,String>(); 
	        
	        for (GroupDocs<BytesRef> groupDocs : result.groups) {
	
	            System.out.println("分组：" + groupDocs.groupValue.utf8ToString());
	
	            System.out.println("组内记录：" + groupDocs.totalHits);
	
	            topMap.put(groupDocs.totalHits, groupDocs.groupValue.utf8ToString());
	            //System.out.println("groupDocs.scoreDocs.length:" + groupDocs.scoreDocs.length);
	            
	            for (ScoreDoc scoreDoc : groupDocs.scoreDocs) {
	
	                System.out.println(indexSearcher.doc(scoreDoc.doc).get(groupField));
	                break;
	            }
	        }
	        
	        
	        //排序
	        /****
	        Arrays.sort(result.groups, new Comparator<GroupDocs<BytesRef>>() {   
	            public int compare(Map.Entry<Integer,String> o1, Map.Entry<Integer,String> o2) {      
	                //return (o2.getValue() - o1.getValue()); 
	                return o1.getKey()-o2.getKey();
	            }
	        });
	        ***/
	        
	        List<Map.Entry<Integer, String>> infoIds =
	        	    new ArrayList<Map.Entry<Integer,String>>(topMap.entrySet());
	        
	        Collections.sort(infoIds, new Comparator<Map.Entry<Integer,String>>() {   
	            public int compare(Map.Entry<Integer,String> o1, Map.Entry<Integer,String> o2) {      
	                //return (o2.getValue() - o1.getValue()); 
	                return o2.getKey()-o1.getKey();
	            }
	        }); 

	      //排序后
	        for (int i = 0; i < infoIds.size(); i++) {
	            String id = infoIds.get(i).toString();
	            
	            System.out.println(id+"  >"+infoIds.get(i).getKey()+" "+infoIds.get(i).getValue());
	        }
	        
	        
	    }
	
	public static void main(String[] args) throws IOException, ParseException {
		
        Directory dir=FSDirectory.open(new File("/Users/fuhe-apple-02/temp/index"));
        IndexReader reader=DirectoryReader.open(dir);
        IndexSearcher searcher=new IndexSearcher(reader);
		
        GroupSupport groupTest = new GroupSupport();
		
		groupTest.group(searcher,"tags", "java");
		
   }
		
			
	
}
