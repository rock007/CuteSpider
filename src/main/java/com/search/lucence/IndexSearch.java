package com.search.lucence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReader.ReaderClosedListener;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.search.comm.ResultModel;
import com.search.comm.StringUtil;
import com.search.db.model.Job;

public class IndexSearch {
	
	private static final Logger logger = LoggerFactory.getLogger(IndexSearch.class);
	
	//private String indexDir;
	
	private static Directory directory;
	
	private static SearcherManager mgr;
	private  SearcherFactory searcherFactory;
	
    //private  IndexReader reader;  
    
    public IndexSearch(String indexDir){
    	
    	try {  

			if (mgr == null) {
				directory = FSDirectory.open(new File(indexDir));

				searcherFactory = new SearcherFactory();

				mgr = new SearcherManager(directory, null);
			}
        } catch (Exception e) {  
            e.printStackTrace();  
            logger.error("初始化SearchManager", e);
        }
    }

	public ResultModel<Job>  doSearch(HashMap<String,String> map,int page,int pageMaxCount) throws Exception {
	    
		 	long  begin=System.currentTimeMillis();
		 	String debugStr="";
			ResultModel<Job> retsult=new ResultModel<Job>();
		
	        IndexSearcher searcher=mgr.acquire();
	        
	        try {
	        	String  keyword=map.get("keyword");
	        	String address=map.get("address");
	        	String salary=map.get("salary");
	        	String source =map.get("source");
	        	String company=map.get("comapny");
	        	
	        	BooleanQuery query = new BooleanQuery();
	        	
	        	if(keyword==null||"".equals(keyword)){
				
	        		retsult.setMessage("keyword is empty!");
			        retsult.setCode(1);
			        retsult.setPageIndex(page);
			        retsult.setPageCount(0);
			        retsult.setList(new ArrayList<Job>());
			        
	        		return retsult;
				}

		        //Analyzer analyzer=new SmartChineseAnalyzer(Version.LUCENE_CURRENT);
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
	        		
	        		query.add(salaryQuery, BooleanClause.Occur.SHOULD);
	        	}
	        	
	        	if(source!=null&&!"".equals(source)){
	        		
	        		query.add(new TermQuery(new Term("source", source)), BooleanClause.Occur.SHOULD);
	        	}
	        	if(company!=null&&!"".equals(company)){
	        		
	        		query.add(new TermQuery(new Term("company", company)), BooleanClause.Occur.SHOULD);
	        	}
		        
		        //排序
		        //Sort sort = new Sort(new SortField("发布日期", SortField.DOC, false));
		        
		        //page
		        TopScoreDocCollector topCollector=TopScoreDocCollector.create(searcher.getIndexReader().maxDoc(), false);
		        searcher.search(query, topCollector);
		        System.out.println("一共有多少条记录命中:"+topCollector.getTotalHits());
		        
		        int start = (page - 1) * pageMaxCount;//start:开始条数   pageMaxCount：显示多少条
		        ScoreDoc[] scoreDocs= topCollector.topDocs(start, pageMaxCount).scoreDocs;

		        long  end=System.currentTimeMillis();
		        debugStr+="搜索共耗时:"+(end-begin)+" 毫秒";
		        
		        logger.debug(debugStr);
		       
		        retsult.setMessage(debugStr);
		        retsult.setCode(1);
		        retsult.setPageIndex(page);
		        retsult.setPageCount(topCollector.getTotalHits());
		        
		        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
		        Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
		        
		        List<Job> jobs=new ArrayList<Job>();
		        for (int i = 0; i < scoreDocs.length; i++) {
		        	   Document doc = searcher.doc(scoreDocs[i].doc);
		        	   
			            logger.debug("title===="+doc.get("title"));
			            logger.debug("id--" + scoreDocs[i].doc + "---scors--" + scoreDocs[i].score+"---index--"+scoreDocs[i].shardIndex);
			            
			            Job oneJob=new Job();
			            
			            //oneJob.setJid(Integer.parseInt(doc.get("jid")));
			            oneJob.setCompanyDesc(doc.get("companyDesc"));
			            oneJob.setCompanyName(doc.get("companyName"));
			            oneJob.setCreateDate(doc.get("title"));
			            
			            String text = doc.get("desc");  
			            //标红
			            TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(),scoreDocs[i].doc , "desc", analyzer);
			            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 300);
			            
			            String brief = "";
			            for (int j = 0; j < frag.length; j++) {
			              if ((frag[j] != null) && (frag[j].getScore() > 0)) {
			                brief += frag[j].toString();
			              }
			            }
			            
			            oneJob.setDescHtml(doc.get("descHtml"));
			            oneJob.setDesc(brief);
			           
			            //oneJob.setJid(doc.get("title"));
			            oneJob.setSalary(doc.get("salary"));
			            oneJob.setSource(doc.get("source"));
			            oneJob.setTitle(doc.get("title"));
			            oneJob.setUrl(doc.get("url"));
			            
			            jobs.add(oneJob);
		         }
		        retsult.setList(jobs);
	        	
	        }
	        finally {
	        	mgr.release(searcher);
	        }
	        searcher = null;
	        
	        return retsult;
	}

	
	
	public ResultModel<Job>  doSearchV2(HashMap<String,String> map) throws Exception {
	    
	 	long  start=System.currentTimeMillis();
	 	String debugStr="";
	 
		ResultModel<Job> retsult=new ResultModel<Job>();
	
        IndexReader reader=DirectoryReader.open(directory);
        IndexSearcher searcher=new IndexSearcher(reader);
        
        /*** demo1
        Term term=new Term("title", "成本经理");
        
        TermQuery query=new TermQuery(term);
        ***/
        
        /**** demo2
        //----------组合查询
        BooleanQuery query=new BooleanQuery();
        //-----------地名带有浙字
        Term term1=new Term("ADDRESS", "浙");
        TermQuery tq1=new TermQuery(term1);
        
        BooleanClause clause=new BooleanClause(tq1, BooleanClause.Occur.SHOULD);   
        query.add(clause);
        
        //--------权重最高的
        Term term2=new Term("weight", "1");
        TermQuery tq2=new TermQuery(term2);
        BooleanClause clause2=new BooleanClause(tq2, BooleanClause.Occur.MUST);
        query.add(clause2);
        ***/
        
        //Analyzer analyzer=new StandardAnalyzer(Version.LUCENE_CURRENT);
        Analyzer analyzer=new IKAnalyzer(true);
        String[] fields=new String[]{"title","companyName","weight","desc","source"};
        MultiFieldQueryParser  multiQParser=new MultiFieldQueryParser(Version.LUCENE_48, fields, analyzer);
         
        String  keyword=map.get("keyword");
        Query query=multiQParser.parse(keyword);
        
        TopDocs topdocs=searcher.search(query, 5);
        
        ScoreDoc[] scoreDocs=topdocs.scoreDocs;
        debugStr="查询结果总数---" + topdocs.totalHits+"最大的评分--"+topdocs.getMaxScore()+"\n";
        long  end=System.currentTimeMillis();
        debugStr+="搜索共耗时:"+(end-start)+" 毫秒";
        
        logger.debug(debugStr);
       
        retsult.setMessage(debugStr);
        retsult.setCode(1);
        retsult.setPageIndex(1);
        retsult.setPageCount(topdocs.totalHits);
        
        List<Job> jobs=new ArrayList<Job>();
        for(int i=0; i < scoreDocs.length; i++) {
            int doc = scoreDocs[i].doc;
            Document document = searcher.doc(doc);
            
            logger.debug("title===="+document.get("title"));
            logger.debug("id--" + scoreDocs[i].doc + "---scors--" + scoreDocs[i].score+"---index--"+scoreDocs[i].shardIndex);
            
            Job oneJob=new Job();
            oneJob.setCompanyDesc(document.get("companyDesc"));
            oneJob.setCompanyName(document.get("companyName"));
            oneJob.setCreateDate(document.get("title"));
            oneJob.setDesc(document.get("desc"));
            //oneJob.setJid(document.get("title"));
            oneJob.setSalary(document.get("salary"));
            oneJob.setSource(document.get("source"));
            oneJob.setTitle(document.get("title"));
            oneJob.setUrl(document.get("url"));
            
            jobs.add(oneJob);
        }
        reader.close();
        retsult.setList(jobs);
        
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
    
   
    
    
}
