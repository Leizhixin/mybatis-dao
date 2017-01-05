package com.johncena.mybatisdao.dao.sqlProvider;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.johncena.mybatisdao.annotation.JdbcColumn;
import com.johncena.mybatisdao.enums.SqlOperationType;
import com.johncena.mybatisdao.util.SqlAnnotationUtil;

public class UpdateSqlProvider extends BaseProvider implements SqlProvider {

	@Override
	public String getSql(Map<String, Object> map) {
		Object entity = map.get("entity");
		Field[] fs = SqlAnnotationUtil.getAnnotatedFields(entity.getClass(), SqlOperationType.Update);
		StringBuilder sql = new StringBuilder();
		sql.append("update ").append(SqlAnnotationUtil.getTableName(entity.getClass()))
			.append(" set ");
		Map<String,String> valueMap = new HashMap<String,String>();
		for(Field f:fs){
			String columnName = f.getAnnotation(JdbcColumn.class).columnName();
			sql.append(columnName).append(" = ").append(SqlAnnotationUtil.getValueSql(f, entity, valueMap, -1)).append(",");
		}
		sql.deleteCharAt(sql.lastIndexOf(","));
		Field pk = SqlAnnotationUtil.getPkField(entity.getClass());
		if(pk!=null){
			sql.append(" where ")
				.append(pk.getAnnotation(JdbcColumn.class).columnName())
				.append("=").append(SqlAnnotationUtil.getValueSql(pk, entity, valueMap, -1));
		}
//		sql = new StringBuilder(sql.toString().replaceAll("list\\[\\d\\]", "entity"));
		logger.info("generate sql = "+sql.toString()+", args = "+entity);
		return sql.toString();
	}
	
}
