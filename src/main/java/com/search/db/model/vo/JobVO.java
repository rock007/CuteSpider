package com.search.db.model.vo;

import java.util.HashMap;
import com.search.db.model.Job;

public class JobVO extends Job{
	
	private HashMap<String,String> props=new HashMap<String,String>();

	public HashMap<String,String> getProps() {
		return props;
	}

	public void setProps(HashMap<String,String> props) {
		this.props = props;
	}
	
	

}
