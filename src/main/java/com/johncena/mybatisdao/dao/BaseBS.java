package com.johncena.mybatisdao.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.johncena.mybatisdao.util.Order;
import com.johncena.mybatisdao.util.SqlAnnotationUtil;

public abstract class BaseBS<T> {

	protected abstract BaseMapper<T> getMapper();
	
	public List<T> selectList(Map<String,Object> map,Class<T> entityClazz,Order...orders){
		List<Map<String,Object>> result;
		if(orders!=null&&orders.length>0){
			result = getMapper().selectListOrder(map, entityClazz, Arrays.asList(orders));
		}else{			
			result = getMapper().selectList(map, entityClazz);
		}
		return SqlAnnotationUtil.transToObjectList(result, entityClazz);
	}
	
	public T selectOneCondition(Map<String,Object> map,Class<T> entityClazz){
		List<T> list = selectList(map, entityClazz);
		return list.isEmpty()?null:list.get(0);
	}
	
	public T selectOne(Serializable id,Class<T> entityClazz){
		Map<String,Object> argMap = new HashMap<String,Object>();
		String idFieldName = SqlAnnotationUtil.getPrimaryKeyName(entityClazz);
		argMap.put(idFieldName, id);
		return selectOneCondition(argMap, entityClazz);
	}
	
	private boolean isIdEmpty(T t){
		Field pk = SqlAnnotationUtil.getPkField(t.getClass());
		if(pk!=null){
			try {
				return pk.get(t)==null;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void batchInsert(List<T> list,Class<T> clz){
		if (!list.isEmpty()) {
			this.getMapper().insert(list, clz);
		}
	}
	
	public void save(T t){
		if(isIdEmpty(t)){
			insert(t);
		}else{
			update(t);
		}
	}
	
	public void update(T t){
		this.getMapper().update(t);
	}
	
	public void insert(T t){
		List<T> list = new ArrayList<T>();
		list.add(t);
		batchInsert(list,(Class<T>) t.getClass());
	}
	
	public void deleteById(Serializable id,Class<T> entityClazz){
		Map<String,Object> argMap = new HashMap<String,Object>();
		String idFieldName = SqlAnnotationUtil.getPrimaryKeyName(entityClazz);
		argMap.put(idFieldName, id);
		this.deleteByCondition(argMap, entityClazz);
	}
	
	public void deleteByCondition(Map<String,Object> argMap,Class<T> entityClazz){
		this.getMapper().delEntity(argMap, entityClazz);
	}
	
}
