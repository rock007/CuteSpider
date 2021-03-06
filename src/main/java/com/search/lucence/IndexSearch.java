package com.search.lucence;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReader.ReaderClosedListener;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FieldCache.Parser;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.search.comm.ResultModel;
import com.search.comm.StringUtil;
import com.search.db.dao.PropDao;
import com.search.db.model.Job;
import com.search.db.model.Prop;
import com.search.db.model.vo.JobVO;
import com.search.form.model.SearchForm;

public class IndexSearch {
	
	private static final Logger logger = LoggerFactory.getLogger(IndexSearch.class);
	
	private  Directory directory;
	
	private  SearcherManager mgr;
	//private  SearcherFactory searcherFactory;
	
	@Resource
	private PropDao propDao;
	
    public IndexSearch(String indexDir){
    	try {  

			if (mgr == null) {
				directory = FSDirectory.open(new File(indexDir));
				mgr = new SearcherManager(directory, null);
			}
        } catch (Exception e) {  
            e.printStackTrace();  
            logger.error("初始化SearchManager", e);
        }
    }

	public ResultModel<JobVO>  doSearchV1(SearchForm form,int page,int pageMaxCount) throws Exception {
	    
		 	long  begin=System.currentTimeMillis();
		 	String debugStr="";
			ResultModel<JobVO> retsult=new ResultModel<JobVO>();
		
	        IndexSearcher searcher=mgr.acquire();
	        
	        try {
	        	String  keyword=form.getKeyword();
	        	String address=form.getAddress();
	        	String salary=form.getSalary();
	        	String source =form.getSource();
	        	String company=form.getCompany();
	        	
	        	if(keyword==null||"".equals(keyword)){
				
	        		retsult.setMessage("keyword is empty!");
			        retsult.setCode(1);
			        retsult.setPageIndex(page);
			        retsult.setPageCount(0);
			        retsult.setList(new ArrayList<JobVO>());
			        
	        		return retsult;
				}

	        	BooleanQuery query = new BooleanQuery();
	        	
		        Analyzer analyzer=new IKAnalyzer(true);
		        String[] fields=new String[]{"title","companyName","desc"};
		        MultiFieldQueryParser  multiQParser=new MultiFieldQueryParser(Version.LUCENE_48, fields, analyzer);
		        
		        Query keyQuery=multiQParser.parse(keyword);
	        	
		        query.add(keyQuery, BooleanClause.Occur.MUST);
		       
	        	if(address!=null&&!"".equals(address)){
	        		Query addressQuery=new FuzzyQuery(new Term("工作地点",address));
	        		//Query query1 = new TermQuery(new Term("工作地点", address)); // 词语搜索
	        		
	        		query.add(addressQuery, BooleanClause.Occur.MUST);
	        	}
	        	
	        	if(salary!=null&&!"".equals(salary)){
	        		
	        		Query salaryQuery = new TermQuery(new Term("salary", salary));
	        		
	        		query.add(salaryQuery, BooleanClause.Occur.MUST);
	        	}
	        	
	        	if(source!=null&&!"".equals(source)){
	        		
	        		query.add(new TermQuery(new Term("source", source)), BooleanClause.Occur.MUST);
	        	}
	        	if(company!=null&&!"".equals(company)){
	        		Query companyQuery=new FuzzyQuery(new Term("companyName",company));
	        		query.add(companyQuery, BooleanClause.Occur.MUST);
	        	}
		    	
		        //page
		        TopScoreDocCollector topCollector=TopScoreDocCollector.create(searcher.getIndexReader().maxDoc(), false);
		        searcher.search(query,topCollector);
		        debugStr+="一共有"+topCollector.getTotalHits()+"记录  ";
		        
		        int start = (page - 1) * pageMaxCount;//start:开始条数   pageMaxCount：显示多少条
		        ScoreDoc[] scoreDocs= topCollector.topDocs(start, pageMaxCount).scoreDocs;

		        long  end=System.currentTimeMillis();
		        debugStr+="搜索共耗时"+(end-begin)+"毫秒";
		        
		        logger.debug(debugStr);
		       
		        retsult.setMessage(debugStr);
		        retsult.setCode(1);
		        retsult.setPageIndex(page);
		        retsult.setPageCount(topCollector.getTotalHits());
		        
		        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
		        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
		        
		        List<JobVO> jobs=new ArrayList<JobVO>();
		        for (int i = 0; i < scoreDocs.length; i++) {
		        	   Document doc = searcher.doc(scoreDocs[i].doc);
		        	   
			            logger.debug("title===="+doc.get("title"));
			            logger.debug("id--" + scoreDocs[i].doc + "---scors--" + scoreDocs[i].score+"---index--"+scoreDocs[i].shardIndex);
			            
			            JobVO oneJob=new JobVO();
			            
			            oneJob.setJid(Integer.parseInt(doc.get("jid")));
			            oneJob.setCompanyDesc(doc.get("companyDesc"));
			            oneJob.setCompanyName(doc.get("companyName"));
			            oneJob.setCreateDate(doc.get("title"));
			            
			            String text = doc.get("desc");  
			            //标红
			            TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(),scoreDocs[i].doc , "desc", analyzer);
			            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 200);
			            
			            String brief = "";
			            for (int j = 0; j < frag.length; j++) {
			              if ((frag[j] != null) && (frag[j].getScore() > 0)) {
			                brief += frag[j].toString();
			              }
			            }
			            if(brief.length()==0){
			            	
			            	brief=text.substring(0,text.length()>200?200:text.length());
			            }
			            oneJob.setDescHtml(doc.get("descHtml"));
			            oneJob.setDesc(brief);
			           
			            oneJob.setSalary(doc.get("salary"));
			            oneJob.setSource(doc.get("source"));
			            oneJob.setTitle(doc.get("title"));
			            oneJob.setUrl(doc.get("url"));
			            
			            List<Prop> propList=propDao.getPropListBySourceId(oneJob.getJid());
						for(Prop p:propList){
							oneJob.getProps().put(p.getKey(), p.getValue());
						}
			            
			            jobs.add(oneJob);
		         }
		        retsult.setList(jobs);
	        	
	        }catch(Exception ex){
	        	logger.error("search key", ex);
	        }
	        finally {
	        	mgr.release(searcher);
	        }
	        searcher = null;
	        
	        return retsult;
	}
	
	public ResultModel<JobVO>  doSearch(SearchForm form,int page,int pageMaxCount) throws Exception {
	
		long  dateSpanBegin=System.currentTimeMillis();
	 	String debugStr="";
		ResultModel<JobVO> retsult=new ResultModel<JobVO>();
	
        IndexSearcher searcher=mgr.acquire();
        
        try {
        	String  keyword=form.getKeyword();
        	String address=form.getAddress();
        	String salary=form.getSalary();
        	String source =form.getSource();
        	String company=form.getCompany();
        	
        	BooleanQuery query = new BooleanQuery();
        	
        	if(keyword==null||"".equals(keyword)){
			
        		retsult.setMessage("keyword is empty!");
		        retsult.setCode(1);
		        retsult.setPageIndex(page);
		        retsult.setPageCount(0);
		        retsult.setList(new ArrayList<JobVO>());
		        
        		return retsult;
			}

	        Analyzer analyzer=new IKAnalyzer(true);
	        String[] fields=new String[]{"title","companyName","desc"};
	        MultiFieldQueryParser  multiQParser=new MultiFieldQueryParser(Version.LUCENE_48, fields, analyzer);
	        
	        Query keyQuery=multiQParser.parse(keyword);
        	
	        query.add(keyQuery, BooleanClause.Occur.MUST);
	       
        	if(address!=null&&!"".equals(address)){
        		
        		Query addressQuery=new FuzzyQuery(new Term("工作地点",address));
        		query.add(addressQuery, BooleanClause.Occur.MUST);
        	}
        	
        	if(salary!=null&&!"".equals(salary)){
        		
        		Query salaryQuery = new TermQuery(new Term("salary", salary));
        		
        		query.add(salaryQuery, BooleanClause.Occur.MUST);
        	}
        	
        	if(source!=null&&!"".equals(source)){
        		
        		query.add(new TermQuery(new Term("source", source)), BooleanClause.Occur.MUST);
        	}
        	if(company!=null&&!"".equals(company)){
        		Query companyQuery=new FuzzyQuery(new Term("companyName",company));
        		query.add(companyQuery, BooleanClause.Occur.MUST);
        	}
        	
        	 SortField jobDate=new SortField("createDate",SortField.Type.STRING,true);
         	 Sort sortOrderBy =  new  Sort( new  SortField[]{ jobDate});  
             
             TopDocs topdocs=searcher.search(query,pageMaxCount*page,sortOrderBy);
             
             ScoreDoc[] hits=topdocs.scoreDocs;
            
 	        retsult.setCode(1);
 	        retsult.setPageIndex(page);
 	        retsult.setPageCount(topdocs.totalHits);
 	        
 	        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
 	        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
 	         
             List<JobVO> jobs=new ArrayList<JobVO>();
             
             int start = (page - 1) * pageMaxCount;//start:开始条数   pageMaxCount：显示多少条
             int end = Math.min(hits.length, start + pageMaxCount);
             
             for(int i = start; i < end; i++) {
            	 
            	 Document doc = searcher.doc(hits[i].doc);
            	 
		            JobVO oneJob=new JobVO();
		            
		            oneJob.setJid(Integer.parseInt(doc.get("jid")));
		            oneJob.setCompanyDesc(doc.get("companyDesc"));
		            oneJob.setCompanyName(doc.get("companyName"));
		            oneJob.setCreateDate(doc.get("title"));
		            
		            String text = doc.get("desc"); 
		            
		            //标红
		            TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(),hits[i].doc , "desc", analyzer);
		            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 200);
		            
		            String brief = "";
		            for (int j = 0; j < frag.length; j++) {
		              if ((frag[j] != null) && (frag[j].getScore() > 0)) {
		                brief += frag[j].toString();
		              }
		            }
		            if(brief.length()==0){
		            	
		            	brief=text.substring(0,text.length()>200?200:text.length());
		            }
		            
		            oneJob.setDescHtml(doc.get("descHtml"));
		            oneJob.setDesc(brief);
		           
		            oneJob.setSalary(doc.get("salary"));
		            oneJob.setSource(doc.get("source"));
		            oneJob.setTitle(doc.get("title"));
		            oneJob.setUrl(doc.get("url"));
		            
		            List<Prop> propList=propDao.getPropListBySourceId(oneJob.getJid());
					for(Prop p:propList){
						oneJob.getProps().put(p.getKey(), p.getValue());
					}
		            
		            jobs.add(oneJob);
             }
             retsult.setList(jobs);
        }
        finally {
        	mgr.release(searcher);
        	
        	 long  dateSpanEnd=System.currentTimeMillis();
 	         debugStr+="搜索共耗时"+(dateSpanEnd-dateSpanBegin)+"毫秒";
 	        
 	        logger.debug(debugStr);
 	        retsult.setMessage(debugStr);
        }
        searcher = null;
        
        return retsult;	
    
	}
	
    public void searchPageByAfter(String expr, int pageIndex, int pageSize) throws Exception{  
    	
        IndexSearcher searcher = mgr.acquire();  
        
        QueryParser parser = new QueryParser(Version.LUCENE_48, "mycontent", new StandardAnalyzer(Version.LUCENE_48));  
        try {  
            Query query = parser.parse(expr);  
            TopDocs tds = searcher.search(query, (pageIndex-1)*pageSize);  
            
            //使用IndexSearcher.searchAfter()搜索,该方法第一个参数为上一页记录中的最后一条记录
            
            if(pageIndex > 1){  
                tds = searcher.searchAfter(tds.scoreDocs[(pageIndex-1)*pageSize-1], query, pageSize);  
            }else{  
                tds = searcher.searchAfter(null, query, pageSize);  
            }  
            for(ScoreDoc sd : tds.scoreDocs){  
                Document doc = searcher.doc(sd.doc);  
                System.out.println("文档编号:" + sd.doc + "-->" + doc.get("myname") + "-->" + doc.get("mycontent"));  
            }  
        } catch (Exception e) {  
            e.printStackTrace();
            
        } finally {  
              
        }  
    }
   
    public  void doPagingSearch(SearchForm form,int page,int pageMaxCount,
			 Query query, int hitsPerPage) throws IOException {

    	IndexSearcher searcher=mgr.acquire();
    	
    	try{
    		
    		TopDocs results = searcher.search(query, 5 * hitsPerPage);
    		// 查找出来的所有文档
    		ScoreDoc[] hits = results.scoreDocs;
    		// 总条数
    		int numTotalHits = results.totalHits;
    		System.out.println(numTotalHits + " total matching documents");

    		int start = 0;
    		int end = Math.min(numTotalHits, hitsPerPage);

    		while (true) {
    			if (end > hits.length) {
    				System.out
    						.println("Only results 1 - " + hits.length + " of "
    								+ numTotalHits
    								+ " total matching documents collected.");
    				System.out.println("Collect more (y/n) ?");
    				
    				hits = searcher.search(query, numTotalHits).scoreDocs;
    			}

    			end = Math.min(hits.length, start + hitsPerPage);

    			for (int i = start; i < end; i++) {
    				
    				Document doc = searcher.doc(hits[i].doc);
    				
    				//String path = doc.get("path");

    			}

    			if (numTotalHits >= end) {

    				end = Math.min(numTotalHits, start + hitsPerPage);
    			}
    		}
    		
    		
    	}catch (Exception ex){
    		
    		
    	}finally{
    		mgr.release(searcher);
    		
    	}
    	searcher=null;
    	
		
	}
    
    public HashMap<String,Integer> group(String groupField,String content) throws IOException, ParseException {
    	
    	HashMap<String,Integer> topMap=new HashMap<String,Integer>();
    	IndexSearcher  searcher= mgr.acquire();
    	
		  try{
			  
			  if(content!=null&&!"".equals(content)){
		
				   GroupingSearch groupingSearch = new GroupingSearch(groupField);
					
			        groupingSearch.setFillSortFields(true);
			        groupingSearch.setCachingInMB(4.0, true);
			        groupingSearch.setAllGroups(true);
			
			        Analyzer analyzer=new IKAnalyzer(true);
			        String[] fields=new String[]{"title","companyName","desc"};
			        MultiFieldQueryParser  multiQParser=new MultiFieldQueryParser(Version.LUCENE_48, fields, analyzer);
			        
			        Query query = multiQParser.parse(content);
			        
			        TopGroups<BytesRef> result = groupingSearch.search(searcher, query, 0, 10);
			
			        logger.debug("搜索命中数：" + result.totalHitCount+" "+"搜索结果分组数：" + result.groups.length);
			        for (GroupDocs<BytesRef> groupDocs : result.groups) {
			
			            logger.debug("分组：" + groupDocs.groupValue.utf8ToString()+" "+"组内记录：" + groupDocs.totalHits);
			            
			            topMap.put(groupDocs.groupValue.utf8ToString(), groupDocs.totalHits);
			        }
				  
			  }
		        
		  }catch(Exception ex){
			  
			  logger.error("get search group", ex);
		  }finally {
	          mgr.release(searcher);
	      }
	      searcher = null;
	        
		  return topMap;
    }
    
}
