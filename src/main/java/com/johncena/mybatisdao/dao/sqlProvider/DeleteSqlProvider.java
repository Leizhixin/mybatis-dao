package com.johncena.mybatisdao.dao.sqlProvider;

import java.util.Map;

import com.johncena.mybatisdao.util.SqlAnnotationUtil;

public class DeleteSqlProvider extends BaseProvider implements SqlProvider {

	@Override
	public String getSql(Map<String, Object> map) {
		StringBuilder sql = new StringBuilder();
		Class<?> entityClz = (Class<?>) map.get("entityClz");
		Map<String,Object> argMap = (Map<String, Object>) map.get("argMap");
		Map<String,Object> conditionMap = getCondition(argMap, entityClz);
		sql.append("delete from ")
			.append(SqlAnnotationUtil.getTableName(entityClz))
			.append(conditionMap.get("sql"));
		map.clear();
		map.putAll((Map<String,Object>) conditionMap.get("args"));
		logger.info(sql.toString());
		return sql.toString();
	}

}
