package com.johncena.mybatisdao.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.johncena.mybatisdao.annotation.EntityParent;
import com.johncena.mybatisdao.annotation.JdbcColumn;
import com.johncena.mybatisdao.annotation.MybatisEntity;
import com.johncena.mybatisdao.enums.SqlOperationType;
/**
 * 主要用于框架内部代码解析自定义的注解
 * @author johncena
 *
 */
public class SqlAnnotationUtil {
	/**
	 * 根据class获取表名 class必须被@MybatisEntity注解
	 * @param tclz
	 * @return
	 */
	public static <T> String getTableName(Class<T> tclz){
		if(!tclz.isAnnotationPresent(MybatisEntity.class)){
			throw new IllegalStateException("entity must be annotationed by Annotation MybatisEntity ");
		}
		MybatisEntity entityAnn = ReflectUtil.getAnnotation(tclz, MybatisEntity.class);
		String tableName = entityAnn.tableName();
		if(StringUtils.isEmpty(tableName)){
			tableName = getDefaultDbName(tclz.getSimpleName());
		}
		return tableName;
	}
	/**
	 * 默认获取表名和表的列名的规则
	 * @param srcName
	 * @return
	 */
	private static String getDefaultDbName(String srcName){
		Pattern pat = Pattern.compile("[A-Z]");
		Matcher mat = pat.matcher(srcName);
		String tableName = srcName.substring(1);
		while(mat.find()){
			String repStr = mat.group();
			tableName = tableName.replace(repStr, "_"+repStr.toLowerCase());
		}
		tableName = srcName.substring(0, 1)+tableName;
		return tableName;
	}
	
	public static Field[] getAnnotatedFields(Class<?> clz, SqlOperationType sqlOpration){
		List<Field> fl = new ArrayList<Field>();
		createAnnotatedFieldsList(fl, clz,sqlOpration);
		return fl.toArray(new Field[fl.size()]);
	}

	private static void createAnnotatedFieldsList(List<Field> fl,Class<?> clz,SqlOperationType sqlOpration){
		Field[] fs = clz.getDeclaredFields();
		for(Field f:fs){
			if(f.isAnnotationPresent(JdbcColumn.class)){
				boolean insertIgnore = f.getAnnotation(JdbcColumn.class).insertIgnore();
				boolean updateIgnore = f.getAnnotation(JdbcColumn.class).updateIgnore();
				if(sqlOpration==SqlOperationType.Insert&&insertIgnore)
					continue;
				if(sqlOpration==SqlOperationType.Update&&updateIgnore)
					continue;
				fl.add(f);
			}
		}
		if(clz.getSuperclass()!=null&&clz.getSuperclass().isAnnotationPresent(EntityParent.class)){
			createAnnotatedFieldsList(fl, clz.getSuperclass(),sqlOpration);
		}
	}
	/**
	 * 根据实体字段获取对应的数据库列
	 * @param f
	 * @return
	 */
	public static String getColumnName(Field f){
		JdbcColumn column = f.getAnnotation(JdbcColumn.class);
		return StringUtils.isEmpty(column.columnName())?
				getDefaultDbName(f.getName()):column.columnName();
	}
	/**
	 * 根据map对象和class对象拼接条件sql语句
	 * @param map
	 * @param clz
	 * @return
	 */
	public static <T> String getCondition(Map<String,String> map,Class<T> clz){
		Map<String,String> jdbcMap = getFieldJdbcMap(clz);
		StringBuilder condition = new StringBuilder(" where 1=1 ");
		for(Map.Entry<String, String> e:map.entrySet()){
			condition.append("and ").append(jdbcMap.get(e.getKey()))
				.append(" ").append(e.getValue())
				.append(" ").append("#{")
				.append(e.getKey()).append("}");
		}
		return condition.toString();
	} 
	/**
	 * 获取属性名与数据库表字段对应的HashMap集合 key:为实体属性 value:数据库字段
	 * @param clz
	 * @return
	 */
	private static <T> Map<String,String> getFieldJdbcMap(Class<T> clz){
		Map<String,String> jdbcMap = new HashMap<String, String>();
		Field[] fields = clz.getDeclaredFields();
		for(Field f:fields){
			if(f.isAnnotationPresent(JdbcColumn.class)){
				jdbcMap.put(f.getName(), getColumnName(f));
			}
		}
		return jdbcMap;
	}
	/**
	 * 获取属性名与数据库表字段对应的HashMap集合 key:数据库字段 value:为实体属性
	 * @param clz
	 * @return
	 */
	private static <T> Map<String,String> getJdbcFieldMap(Class<T> clz){
		Map<String,String> jdbcMap = new HashMap<String, String>();
		Field[] fields = clz.getDeclaredFields();
		for(Field f:fields){
			if(f.isAnnotationPresent(JdbcColumn.class)){
				jdbcMap.put(getColumnName(f),f.getName());
			}
		}
		return jdbcMap;
	}
	/**
	 * 将数据库查询的结果转换为实体
	 * @param dbresult
	 * @param clz
	 * @return
	 */
	public static <T> T transToObject(Map<String,Object> dbresult,Class<T> clz){
		return transToObject(dbresult, getJdbcFieldMap(clz), clz);
	}
	
	private static <T> T transToObject(Map<String,Object> dbresult,Map<String,String> jdbcFieldMap,Class<T> clz){
		T t = ReflectUtil.newInstance(clz);
		for(Map.Entry<String, Object> entry:dbresult.entrySet()){
			if(jdbcFieldMap.containsKey(entry.getKey())){
				String fieldName = jdbcFieldMap.get(entry.getKey());
				Object fieldValue = entry.getValue();
				ReflectUtil.setFieldValue(t, fieldName, fieldValue);
			}
		}
		return t;
	}
	/**
	 * 将数据库查询的结果转换为实体集合
	 * @param dbresult
	 * @param clz
	 * @return
	 */
	public static <T> List<T> transToObjectList(List<Map<String,Object>> dbresList,Class<T> clz){
		Map<String,String> jdbcFieldMap = getJdbcFieldMap(clz);
		List<T> resList = new ArrayList<T>();
		for(Map<String,Object> dbres:dbresList){
			resList.add(transToObject(dbres, jdbcFieldMap, clz));
		}
		return resList;
	}
	/**
	 * 通过class获取主键名
	 * @param clz
	 * @return
	 */
	public static <T> String getPrimaryKeyName(Class<T> clz){
		Field f = getPkField(clz);
		if(f!=null)
			return f.getName();
		throw new UnsupportedOperationException(clz.getName()+" has no primary key ");
	}
	/**
	 * 获取主键属性
	 * @param tclz
	 * @return
	 */
	public static Field getPkField(Class<?> tclz){
		if(!tclz.isAnnotationPresent(MybatisEntity.class)&&!tclz.isAnnotationPresent(EntityParent.class)){
			throw new IllegalStateException("entity must be annotationed by Annotation MybatisEntity ");
		}
		Field[] fs = getAnnotatedFields(tclz, SqlOperationType.Select);
		for(Field f:fs){
			if(f.isAnnotationPresent(JdbcColumn.class)&&
					f.getAnnotation(JdbcColumn.class).isPrimaryKey()){
				f.setAccessible(true);
				return f;
			}
		}
		if(tclz.getSuperclass().isAnnotationPresent(EntityParent.class))
			return getPkField(tclz.getSuperclass());
		return null;
	}
	/**
	 * 根据class,和排序对象集合获取排序的sql
	 * @param clz
	 * @param orderList
	 * @return
	 */
	public static String compileOrderList(Class<?> clz,List<Order> orderList){
		StringBuilder sql = new StringBuilder();
		if(!orderList.isEmpty()){
			sql.append(" order by ");
			for(Order order:orderList){
				Field f = ReflectUtil.getField(clz, order.orderFieldName);
				if(f.isAnnotationPresent(JdbcColumn.class)){
					JdbcColumn colAnn = f.getAnnotation(JdbcColumn.class);
					sql.append(colAnn.columnName());
					if(!order.isasc){
						sql.append(" desc");
					}
					sql.append(",");
				}
			}
			sql.deleteCharAt(sql.lastIndexOf(","));
		}
		return sql.toString();
	}
	
	public static String getValueSql(Field f,Object entity,Map<String,String> valueMap,int index){
		StringBuilder sql = new StringBuilder();
		String fieldName = f.getName();
		String valueStr;
		if(valueMap.containsKey(fieldName)){
			valueStr = valueMap.get(fieldName);
			valueStr = valueStr.replaceAll("[\\d+]", ""+index);
			return valueStr;
		}
		if (index!=-1) {
			valueStr = "#{list[" + index + "]." + fieldName + "}";
		}else{
			valueStr = "#{entity."+fieldName+"}";
		}
		if(f.isAnnotationPresent(JdbcColumn.class)){
			JdbcColumn colAnn = f.getAnnotation(JdbcColumn.class);
			if(StringUtils.isNotEmpty(colAnn.defaultValue())){
				try {
					f.setAccessible(true);
					Object value = f.get(entity);
					if(value!=null){
						sql.append(valueStr);
					}else{
						valueStr = compileValue(colAnn.defaultValue(),entity,valueMap,index);
						sql.append(valueStr);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new IllegalStateException("设置值错误");
				}
			}else{
				sql.append(valueStr);
			}
		}
		valueMap.put(fieldName, valueStr);
		return sql.toString();
	}
	
	private static String compileValue(String valueStr,Object entity,Map<String,String> valueMap,int index){
		String retVal = valueStr;
		Pattern pat = Pattern.compile("#\\{[a-zA-Z]+\\}");
		Matcher mat = pat.matcher(valueStr);
		while(mat.find()){
			String groupStr = mat.group();
			String fieldName = groupStr.replace("#{", "").replace("}", "");
			Field f = ReflectUtil.getField(entity.getClass(), fieldName);
			String valueSql = getValueSql(f, entity, valueMap, index);
			retVal = retVal.replace("#{", "val_").replace("val_"+fieldName, valueSql).replace("}", "");
		}
		return retVal;
	}
	
}
