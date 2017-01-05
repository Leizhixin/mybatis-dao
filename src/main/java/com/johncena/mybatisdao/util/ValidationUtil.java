package com.johncena.mybatisdao.util;

import org.apache.commons.lang.StringUtils;

public class ValidationUtil {

	private static final String validSqlOperator = "=|<|>|>=|<=|<>|like";
	
	public static boolean isValidePropName(String str){
		return StringUtils.isNotEmpty(str)&&str.matches("^[a-zA-Z]+$");
	}
	
	public static boolean isValidSqlOprator(String str){
		return StringUtils.isNotEmpty(str)&&validSqlOperator.indexOf(str)!=-1;
	}
	
	public static void main(String[] args) {
		System.out.println(isValidePropName("abc"));
	}
}
