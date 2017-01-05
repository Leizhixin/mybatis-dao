package com.johncena.mybatisdao.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import com.johncena.mybatisdao.dao.sqlProvider.DeleteSqlProvider;
import com.johncena.mybatisdao.dao.sqlProvider.InsertSqlProvider;
import com.johncena.mybatisdao.dao.sqlProvider.SelectSqlProvider;
import com.johncena.mybatisdao.dao.sqlProvider.UpdateSqlProvider;
import com.johncena.mybatisdao.util.Order;

public interface BaseMapper<T> {

	@SelectProvider(type=SelectSqlProvider.class,method="getSql")
	List<Map<String,Object>> selectList(@Param(value = "argMap") Map<String,Object> argMap,@Param(value="entityClz") Class<T> entityClazz);
	
	@SelectProvider(type=SelectSqlProvider.class,method="getSql")
	List<Map<String,Object>> selectListOrder(@Param(value = "argMap") Map<String,Object> argMap,@Param(value="entityClz") Class<T> entityClazz,
			@Param("orderList") List<Order> orderList);
	
	@InsertProvider(type=InsertSqlProvider.class,method="getSql")
	void insert(@Param("list") List<T> list,@Param("clz") Class<T> clz);
	
	@UpdateProvider(type=UpdateSqlProvider.class,method="getSql")
	void update(@Param("entity") T t);
	
	@DeleteProvider(type=DeleteSqlProvider.class,method="getSql")
	void delEntity(@Param(value = "argMap") Map<String,Object> argMap,
			@Param(value="entityClz") Class<T> entityClazz);
}
