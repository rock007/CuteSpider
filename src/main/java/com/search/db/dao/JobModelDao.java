package com.search.db.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.StatementType;
import com.search.db.model.JobModel;
import com.search.db.model.vo.CityVO;

public interface JobModelDao {
	
    @Insert(" INSERT INTO Jobs "+
    		" (  [CompanyId], [Title], [Brief], [ProvinceId], [CityId], [RegionId], [Desc], [Keywords], [Salary], [Edu], [JobType], [CreateDate], [UserId], [SaveToMail], [Status], [OrderNo], [DescTxt], [Form], [Url], [ApplyNum], [ViewNum] ) "+
    		" VALUES "+
    		" ( "+
    			"  #{CompanyId}, #{Title}, #{Brief}, #{ProvinceId}, #{CityId}, #{RegionId}, #{Desc}, #{Keywords}, #{Salary}, #{Edu}, #{JobType}, #{CreateDate}, #{UserId}, #{SaveToMail}, #{Status}, #{OrderNo}, #{DescTxt}, #{Form}, #{Url}, #{ApplyNum}, #{ViewNum} "+
    		" )  ")
    @SelectKey(before=false,keyProperty="JobId",resultType=Integer.class,statementType=StatementType.STATEMENT,statement="select @@IDENTITY as id")
    public int add(JobModel m);
 
    @Select("select [JobId], [CompanyId], [Title], [Brief], [ProvinceId], [CityId], [RegionId], [Desc], [Keywords], [Salary], [Edu], [JobType], [CreateDate], [UserId], [SaveToMail], [Status], [OrderNo], [DescTxt], [Form], [Url], [ApplyNum], [ViewNum]"+
    		" from Jobs where [Url]=#{url}") 
    public List<JobModel> getByUrl(String url);

    @Select(value= "{ CALL  [sp_FindCity]( #{cityName, mode=IN, jdbcType=VARCHAR},#{privinceId, mode=OUT, jdbcType=INTEGER},#{cityId, mode=OUT, jdbcType=INTEGER},#{districtId, mode=OUT, jdbcType=INTEGER})}")
    @Options(statementType = StatementType.CALLABLE)
    public int sp_FindCity(CityVO city);
}
