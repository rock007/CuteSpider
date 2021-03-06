package com.search.db.dao;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.StatementType;
import org.springframework.stereotype.Component;

import com.search.db.model.Job;
import com.search.db.model.Prop;

public interface JobDao {
	
    @Insert(" INSERT INTO b_job "+
    		" (title,salary,companyName,companyDesc,companyDescHtml,[desc],descHtml,url,[source],createDate,updateDate) "+
    		" VALUES "+
    		" ( "+
    			" #{title},#{salary},#{companyName},#{companyDesc},#{companyDescHtml},#{desc},#{descHtml},#{url},#{source},#{createDate},#{updateDate} "+
    		" )  ")
    //@Options(useGeneratedKeys = true, keyProperty = "jid")
    @SelectKey(before=false,keyProperty="jid",resultType=Integer.class,statementType=StatementType.STATEMENT,statement="select @@IDENTITY as id")
    public int add(Job m);
 
    @Select("SELECT jid,title,salary,companyName,companyDesc,companyDescHtml,[desc],descHtml,url,[source],createDate,updateDate "+
    		" FROM b_job where url=#{url}") 
    public List<Job> getByUrl(String url);
    
    
    @Select(" select * from  `b_job` where status=#{status} and createDate=#{createDate} limit #{start} , #{limit}")
    public List<Job> getByStatus(@Param("status") int status,@Param("createDate") String createDate,@Param("start")int start,@Param("limit")int limit);
    
    @Select(" select * from  b_job where status=#{status}  limit #{start} , #{limit}")
    public List<Job> getAllByStatus(@Param("status") int status,@Param("start")int start,@Param("limit")int limit);
    
    
    @Update("call sp_pre_index() ")
    public int sp_pre_fix();
}
