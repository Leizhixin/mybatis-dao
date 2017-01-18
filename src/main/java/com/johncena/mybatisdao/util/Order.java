package com.johncena.mybatisdao.util;
/**
 * 排序对象
 * @author johncena
 *
 */
public class Order {
	/**
	 * 需要进行排序的字段(实体变量名)
	 */
	public final String orderFieldName;
	/**
	 * 是否是升序
	 */
	public final boolean isasc;
	
	public Order(String orderFieldName,boolean isasc) {
		this.orderFieldName = orderFieldName;
		this.isasc = isasc;
	}
	
	public Order(String orderFieldName) {
		this(orderFieldName,true);
	}
}
