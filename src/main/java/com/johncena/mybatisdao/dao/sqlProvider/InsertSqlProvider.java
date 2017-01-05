package com.johncena.mybatisdao.dao.sqlProvider;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.johncena.mybatisdao.enums.SqlOperationType;
import com.johncena.mybatisdao.util.SqlAnnotationUtil;

public class InsertSqlProvider extends BaseProvider implements SqlProvider{

	@Override
	public String getSql(Map<String, Object> map) {
		Class<?> clz = (Class<?>) map.get("clz");
		List<?> entityList = (List<?>) map.get("list");
		Field[] fs = SqlAnnotationUtil.getAnnotatedFields(clz, SqlOperationType.Insert);
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append(SqlAnnotationUtil.getTableName(clz)).append("(").append(getColumns(fs)).append(")").append(" values");
		int index = 0;
		Map<String,String> valueMap = new HashMap<String,String>();
		for(Object entity:entityList){
			sql.append(createValues(fs, entity,valueMap,index++)).append(",");
		}
		sql.deleteCharAt(sql.lastIndexOf(","));
		logger.info("generator sql = "+sql.toString()+",argMap = "+map);
		return sql.toString();
	}

	private  String createValues(Field[] fs,Object entity,Map<String,String> valueMap,int index){
		StringBuilder sql = new StringBuilder();
		sql.append("(");
		for(Field f:fs){
			sql.append(SqlAnnotationUtil.getValueSql(f,entity,valueMap,index)).append(",");
		}
		sql.deleteCharAt(sql.lastIndexOf(","));
		sql.append(")");
		return sql.toString();
	}
	
}
