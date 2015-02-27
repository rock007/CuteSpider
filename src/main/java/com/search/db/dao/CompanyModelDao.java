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

import com.search.db.model.CompanyModel;
import com.search.db.model.Job;
import com.search.db.model.Prop;

public interface CompanyModelDao {
	
    @Insert(" INSERT INTO Companies "+
    		" (  [Name], [LinkMan], [Phone], [ProvinceId], [CityId], [RegionId], [PostalCode], [Address], [Industry], [Size], [WebSite], [Logo], [Remarks], [Brief], [UserId], [OrderNo], [RemarksTxt] ) "+
    		" VALUES "+
    		" ( "+
    			"  #{Name}, #{LinkMan}, #{Phone}, #{ProvinceId}, #{CityId}, #{RegionId}, #{PostalCode}, #{Address}, #{Industry}, #{Size}, #{WebSite}, #{Logo}, #{Remarks}, #{Brief}, #{UserId}, #{OrderNo}, #{RemarksTxt} "+
    		" )  ")
    @SelectKey(before=false,keyProperty="CompanyId",resultType=Integer.class,statementType=StatementType.STATEMENT,statement="select @@IDENTITY as id")
    public int add(CompanyModel m);
 
    @Select("SELECT [CompanyId], [Name], [LinkMan], [Phone], [ProvinceId], [CityId], [RegionId], [PostalCode], [Address], [Industry], [Size], [WebSite], [Logo], [Remarks], [Brief], [UserId], [OrderNo], [RemarksTxt] "+
    		" FROM Companies where [Name]=#{name}")
    public List<CompanyModel> getByName(String name);
    
}
