package com.search.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.search.db.model.Prop;

public interface PropDao {

    @Insert(" INSERT INTO `b_job_prop`(`pid`,`sourceId`,`key`,`value`) "+
    		" VALUES( "+
    			" #{pid},#{sourceId},#{key},#{value} "+
    		" ) ")
    @Options(useGeneratedKeys = true, keyProperty = "pid")
    public int add(Prop m);
    
    @Select(" select * from  `b_job_prop` where sourceId=#{sourceId}")
    public List<Prop> getPropListBySourceId(int sourceId);
}
