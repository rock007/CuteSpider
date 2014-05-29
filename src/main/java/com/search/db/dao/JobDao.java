package com.search.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.search.db.model.Job;
import com.search.db.model.Prop;

public interface JobDao {

	
    @Insert("INSERT INTO `b_job` "+
    		" (`jid`,`title`,`salary`,`companyName`,`companyDesc`,`companyDescHtml`,`desc`,`descHtml`,`url`,`source`,`createDate`,`updateDate`) "+
    		" VALUES "+
    		" ( "+
    			" #{jid},#{title},#{salary},#{companyName},#{companyDesc},#{companyDescHtml},#{desc},#{descHtml},#{url},#{source},#{createDate},#{updateDate} "+
    		" )")
    @Options(useGeneratedKeys = true, keyProperty = "jid")
    public int add(Job m);
 
    @Select("SELECT `jid`,`title`,`salary`,`companyName`,`companyDesc`,`companyDescHtml`,`desc`,`descHtml`,`url`,`source`,`createDate`,`updateDate` "+
    		" FROM `b_job` where `url`=#{url}") 
    public List<Job> getByUrl(String url);
    
    
    @Select(" select * from  `b_job` where status=#{status}")
    public List<Job> getByStatus(int status);
    
}
