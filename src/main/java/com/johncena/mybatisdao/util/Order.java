package com.johncena.mybatisdao.util;

public class Order {

	public final String orderFieldName;
	public final boolean isasc;
	
	public Order(String orderFieldName,boolean isasc) {
		this.orderFieldName = orderFieldName;
		this.isasc = isasc;
	}
	
	public Order(String orderFieldName) {
		this(orderFieldName,true);
	}
}
