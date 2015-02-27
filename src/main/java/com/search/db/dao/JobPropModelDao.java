package com.search.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.mapping.StatementType;

import com.search.db.model.JobPropModel;
import com.search.db.model.Prop;

public interface JobPropModelDao {

    @Insert(" INSERT INTO JobProps(JobId,[Key],[Value]) "+
    		" VALUES( "+
    			" #{JobId},#{Key},#{Value} "+
    		" ) ")
    @SelectKey(before=false,keyProperty="Id",resultType=Integer.class,statementType=StatementType.STATEMENT,statement="select @@IDENTITY as id")
    public int add(JobPropModel m);
    
    @Select(" select * from  JobProps where JobId=#{JobId}")
    public List<JobPropModel> getPropListBySourceId(int sourceId);
}
