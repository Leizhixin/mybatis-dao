package com.johncena.mybatisdao.dao.sqlProvider;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.johncena.mybatisdao.util.SqlAnnotationUtil;
import com.johncena.mybatisdao.util.ValidationUtil;

public abstract class BaseProvider implements SqlProvider {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected String getColumns(Field[] fs){
		StringBuilder columns = new StringBuilder();
		for(Field f:fs){
			columns.append(SqlAnnotationUtil.getColumnName(f))
				.append(",");
		}
		columns.deleteCharAt(columns.lastIndexOf(","));
		return columns.toString();
	}

	protected Map<String,Object> getCondition(Map<String,Object> map,Class<?> entityClz){
		Map<String,String> sqlMap = new LinkedHashMap<String,String>();
		Map<String,Object> argsMap = new HashMap<String,Object>();
		Map<String,Object> conditionMap = new HashMap<String,Object>();
		for(String key:map.keySet()){
			String[] sqlArr = key.split(" ");
			if (ValidationUtil.isValidePropName(sqlArr[0])) {
				if (sqlArr.length == 1) {
					sqlMap.put(sqlArr[0], "=");
					argsMap.put(sqlArr[0], map.get(key));
				} else if (sqlArr.length == 2&&ValidationUtil.isValidSqlOprator(sqlArr[1])) {
					sqlMap.put(sqlArr[0], sqlArr[1]);
					argsMap.put(sqlArr[0], map.get(key));
				} 
			}
		}
		conditionMap.put("sql", SqlAnnotationUtil.getCondition(sqlMap, entityClz));
		conditionMap.put("args", argsMap);
		return conditionMap;
	}
}
