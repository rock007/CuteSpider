package com.search.spider;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.search.comm.DictManager;
import com.search.comm.StringUtil;
import com.search.db.dao.CompanyModelDao;
import com.search.db.dao.JobModelDao;
import com.search.db.dao.JobPropModelDao;
import com.search.db.dao.PropDao;
import com.search.db.model.CompanyModel;
import com.search.db.model.JobModel;
import com.search.db.model.JobPropModel;
import com.search.db.model.Prop;
import com.search.db.model.vo.CityVO;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 保存至56我帮.找工作
 * @author 文华
 *
 */
@Component("savePipeline4wb")
public class SavePipeline4wb implements  Pipeline{

	private static final Logger logger = LoggerFactory.getLogger(SavePipeline4wb.class);
	
    @Resource
    private JobModelDao jobModelDao;
    
    @Resource
    private CompanyModelDao companyDao;
    
    @Resource
    private JobPropModelDao jobPropModelDao;
    
    HashMap<String,String> propsMap;
    
	@Override
	public void process(ResultItems resultItems, Task task) {
		
		CompanyModel com=new CompanyModel();
		JobModel job=new JobModel();
		
		//判断是否已经存在
		String url= (String)resultItems.get("url");
		
		if(url==null||"".equals(url)) {
			
			return;
		}
		List<JobModel>  existJobs=jobModelDao.getByUrl(url);
		if(existJobs.size()>0){
			
			logger.debug(url+":已经存在，不处理，跳过,count:"+existJobs.size());
			return;
		}
		//!!!
		propsMap =resultItems.get("props");
		
		String source=(String)resultItems.get("source");
		
		if(source!=null&&!"".equals(source)){
			
			Integer indexNum=SpiderRecord.getNum(source);
			indexNum+=1;
			
			SpiderRecord.addKeyNum(source, indexNum);
		}else{
			
			logger.debug(url+":可能不是职位页面，source为空！跳过");
			return;
		}
		
		//
		String companyDescHtml=(String)resultItems.get("companyDesc");
		String jobDescHtml=(String)resultItems.get("descr");
		
		com.setRemarksTxt(StringUtil.html2text(companyDescHtml));
		com.setRemarks(companyDescHtml);
		com.setName((String)resultItems.get("company"));
		
		String com_industry;
		String address="";
		String brief="";
		
		String linkMan=getPropsValue("",true,"保密");
		String phone=getPropsValue("",true,"123456789");
		String size="ERv1_2_10";
		String webSite="";
		
		String edu= "";
		String saveToMail=getPropsValue("",true,"scarecrow_Online@163.com");
		String keywords=(String)resultItems.get("keyword");
		String jobbrief=keywords; 
		
		String industry=getPropsValue("公司行业");
		
		if(source.equals("51job")) {
			
			size=getPropsValue("公司规模");
			edu= getPropsValue("学    历");
			
		}else if(source.equals("zhaopin")) {
			size=getPropsValue("公司规模");
			webSite=getPropsValue("公司主页");
			edu= getPropsValue("最低学历");
			address=getPropsValue("公司地址");
			
			//job_type=getPropsValue("工作性质");
			
		}else if(source.equals("liepin")) {
			address=getPropsValue("地址");
			size=getPropsValue("规模");
			industry=getPropsValue("行业");
		}
		//城市
		String city4job=getPropsValue("工作地点");
				
		if(!city4job.endsWith("市")) {
			city4job=city4job+"市";
		}
				
		CityVO city=new CityVO();
		city.setCityName(city4job);
				
		jobModelDao.sp_FindCity(city);
				
		com.setAddress(address);
		com.setBrief(brief);
		
		//公司行业
		com_industry="Iv1_OTHER";//!!!
		com.setIndustry(com_industry);
		com.setLinkMan(linkMan);
		com.setLogo("");
		com.setOrderNo(0);
		com.setPhone(phone);
		com.setPostalCode("");
		
		//公司城市（no work）
		com.setProvinceId(city.getPrivinceId());
		com.setCityId(city.getCityId());
		com.setRegionId(city.getDistrictId());
		
		//公司规模
		com.setSize(DictManager.CompanySize(size));
		com.setWebSite(webSite);
		
		job.setTitle((String)resultItems.get("jobTitle"));
		job.setDescTxt(StringUtil.html2text(jobDescHtml));
		job.setDesc(jobDescHtml);
		
		//工资
		//job.setSalary("Salary_0");
		String salaryTemp=(String)resultItems.get("salary");
		job.setSalary(DictManager.Salary(salaryTemp));//!!!
		
		job.setForm(source);
		job.setUrl((String)resultItems.get("url"));
		
		//先公司
		int companyid=0;
		List<CompanyModel> companies= companyDao.getByName(com.getName());
		
		if(companies.size()>0) {
			
			companyid=companies.get(0).getCompanyId();
			
		}else {
			
			if(companyDao.add(com)!=1) {
				
				logger.debug(url+":公司插入数据库失败！跳过");
				return;
			}
			companyid= com.getCompanyId();	
		}
		
		//学历		
		edu=DictManager.Edu(edu);
		job.setCompanyId(companyid);
		job.setApplyNum(0);
		job.setBrief(jobbrief);
		job.setCreateDate(new Date());
		job.setEdu(edu);
		job.setJobType(1);
		
		job.setKeywords(keywords);
		job.setOrderNo(0);
		
		job.setProvinceId(city.getPrivinceId());
		job.setCityId(city.getCityId());
		job.setRegionId(city.getDistrictId());
		
		job.setSaveToMail(saveToMail);
		job.setStatus(0);
		job.setViewNum(0);
		
		if(jobModelDao.add(job)!=1) {
			logger.debug(url+":职位插入数据库失败！");
			return ;
		}
		
		int jid= job.getJobId();
		
		if(jid>0&&propsMap.size()>0){
			//add prop
			 Iterator<Entry<String, String>> it = propsMap.entrySet().iterator();
			 
			 while (it.hasNext()) {
			        Map.Entry<String,String> pairs = (Map.Entry<String,String>)it.next();
			        
			        logger.debug(pairs.getKey() + " = " + pairs.getValue());
			
			        JobPropModel p=new JobPropModel();
			        p.setJobId(jid);
			        p.setKey(pairs.getKey().trim());
			        p.setValue(pairs.getValue().trim());
					
			        jobPropModelDao.add(p);
			  }
		}
		
	}
	
	private String getPropsValue(String key,boolean isRequired,String empty) {

		String msg="";
		String txt = propsMap.get(key);

		if (txt != null)
			msg= txt.trim();
		
		if(msg.equals("")&&isRequired) return empty;
		
		return msg;
	}
	
	private String getPropsValue(String key,boolean isRequired) {
		
		return getPropsValue(key,isRequired,"***");
	}
	
private String getPropsValue(String key) {
		
		return getPropsValue(key,false);
	}

}
