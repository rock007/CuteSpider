package com.search.db.dao;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.StatementType;

import com.search.db.model.FetchLog;

public interface FetchLogDao {

	   @Insert("INSERT INTO FetchLogs "+
	    		" (day,site,fetchNum,validNum,[remarks]) "+
	    		" VALUES "+
	    		" ( "+
	    			" #{day},#{site},#{fetchNum},#{validNum},#{remarks}"+
	    		" )")

	    //@Options(useGeneratedKeys = true, keyProperty = "id")
	    @SelectKey(before=false,keyProperty="id",resultType=Integer.class,statementType=StatementType.STATEMENT,statement="select @@IDENTITY as id")
	    public int add(FetchLog m);
	 
	   
	    @Select(" select id,day,site,fetchNum,validNum,remarks from  FetchLogs  where day=#{day} and site=#{site}")
	    public List<FetchLog> getBy(@Param("day")String day,@Param("site") String site);

	    @Update(" update FetchLogs set fetchNum=#{fetchNum},validNum=#{validNum}   where day=#{day} and site=#{site} ")
	    public int update(FetchLog m);
}
