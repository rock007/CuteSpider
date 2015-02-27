package com.search.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.mapping.StatementType;

import com.search.db.model.Prop;

public interface PropDao {

    @Insert(" INSERT INTO b_job_prop(sourceId,[key],[value]) "+
    		" VALUES( "+
    			" #{sourceId},#{key},#{value} "+
    		" ) ")
    //@Options(useGeneratedKeys = true, keyProperty = "pid" )
    @SelectKey(before=false,keyProperty="pid",resultType=Integer.class,statementType=StatementType.STATEMENT,statement="select @@IDENTITY as id")
    public int add(Prop m);
    
    @Select(" select * from  b_job_prop where sourceId=#{sourceId}")
    public List<Prop> getPropListBySourceId(int sourceId);
}
