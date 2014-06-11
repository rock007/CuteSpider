package com.search.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.search.db.model.FetchLog;

public interface FetchLogDao {

	   @Insert("INSERT INTO `r_fetch_log` "+
	    		" (`id`,`day`,`site`,`fetchNum`,`validNum`,`remarks`) "+
	    		" VALUES "+
	    		" ( "+
	    			" #{id},#{day},#{site},#{fetchNum},#{validNum},#{remarks}"+
	    		" )")
	    @Options(useGeneratedKeys = true, keyProperty = "id")
	    public int add(FetchLog m);
	 
	   
	    @Select(" select `id`,`day`,`site`,`fetchNum`,`validNum`,`remarks` from  `r_fetch_log` where `day`=#{day} and `site`=#{site}")
	    public List<FetchLog> getBy(@Param("day")String day,@Param("site") String site);

	    @Update(" update `r_fetch_log` set `fetchNum`=#{fetchNum},`validNum`=#{validNum}   where `day`=#{day} and `site`=#{site} ")
	    public int update(FetchLog m);
}
