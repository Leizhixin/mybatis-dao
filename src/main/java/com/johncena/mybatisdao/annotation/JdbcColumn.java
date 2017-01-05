package com.johncena.mybatisdao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface JdbcColumn {

	public String columnName() default "";
	
	public String defaultValue() default "";
	
	public boolean insertIgnore() default false;
	
	public boolean isPrimaryKey() default false;
	
	public boolean updateIgnore() default false;
}
