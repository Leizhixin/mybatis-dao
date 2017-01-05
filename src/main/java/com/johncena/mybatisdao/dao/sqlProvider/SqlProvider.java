package com.johncena.mybatisdao.dao.sqlProvider;

import java.util.Map;

public interface SqlProvider {

	public String getSql(Map<String,Object> map);
	
}
