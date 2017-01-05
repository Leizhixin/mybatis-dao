package com.johncena.mybatisdao.dao.sqlProvider;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.johncena.mybatisdao.enums.SqlOperationType;
import com.johncena.mybatisdao.util.Order;
import com.johncena.mybatisdao.util.SqlAnnotationUtil;
import com.johncena.mybatisdao.util.ValidationUtil;

public final class SelectSqlProvider extends BaseProvider implements SqlProvider{
	
	public String getSql(Map<String,Object> map){
		StringBuilder sql = new StringBuilder();
		Map<String,Object> argMap = (Map<String, Object>) map.get("argMap");
		Class<?> entityClz = (Class<?>) map.remove("entityClz");
		
		sql.append(getSelect(entityClz));
		Map<String,Object> conditionMap = getCondition(argMap, entityClz);
		sql.append(conditionMap.get("sql"));
		if (map.containsKey("orderList")) {
			List<Order> orderList = (List<Order>) map.get("orderList");
			sql.append(getOrder(orderList, entityClz));
		}
		map.clear();
		map.putAll((Map<String,Object>) conditionMap.get("args"));
		logger.info("generator sql = "+sql.toString()+",argMap = "+map);
		return sql.toString();
	}
	
	private String getSelect(Class<?> clz){
		StringBuilder select = new StringBuilder("select ");
		Field[] fs = SqlAnnotationUtil.getAnnotatedFields(clz, SqlOperationType.Select);
		select.append(getColumns(fs));
		select.append(" from ").append(SqlAnnotationUtil.getTableName(clz));
		return select.toString();
	}
	
	
	
	
	private String getOrder(List<Order> orderList,Class<?> entityClz){
		if(orderList==null){
			return "";
		}
		return SqlAnnotationUtil.compileOrderList(entityClz, orderList);
	}
	
	
}
