package com.johncena.mybatisdao.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReflectUtil {

	private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);
	
	public static <T> Map<String,Object> obj2map(T t){
		Map<String,Object> result = new HashMap<String,Object>();
		Class<T> clz = (Class<T>) t.getClass();
		try {
			obj2map(result, t, clz);
		} catch (IllegalArgumentException e) {
			logger.info("reflect error", e);
		} catch (IllegalAccessException e) {
			logger.info("reflect error", e);
		}
		return result;
	}
	
	private static <T> void obj2map(Map<String,Object> result,T t,Class clz) throws IllegalArgumentException, IllegalAccessException{
		if(clz!=Object.class){
			Field[] fs = clz.getDeclaredFields();
			for(Field f:fs){
				f.setAccessible(true);
				result.put(f.getName(), f.get(t));
			}
			obj2map(result, t, clz.getSuperclass());
		}
	}
	
	public static<T> T newInstance(Class<T> tclz){
		T t=null;
		try {
			t =tclz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public static <A extends Annotation> A getAnnotation(Class<?> clz,Class<A> annClz){
		A a = clz.getAnnotation(annClz);
		if(a==null&&clz.getSuperclass()!=Object.class)
			return getAnnotation(clz.getSuperclass(), annClz);
		return a;
	}
	
	public static Field getField(Class<?> clz,String fieldName){
		Field f=null;
		try {
			f = clz.getDeclaredField(fieldName);
			f.setAccessible(true);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			if(clz!=Object.class)
				f = getField(clz.getSuperclass(), fieldName);
			if(f==null)
				throw new NullPointerException("变量'"+fieldName+"' 不存在于\""+clz.getName()+"\"中");
		}
		return f;
	}
	/**
	 * 获取所有变量
	 * @param clz
	 * @return
	 */
	public static Field[] getFields(Class<?> clz,Class<? extends Annotation>... aclz){
		List<Field> resList = new ArrayList<Field>();
		getFields(resList, clz,aclz);
		return resList.toArray(new Field[]{});
	}
	
	private static  void getFields(List<Field> resList,Class<?> clz,Class<? extends Annotation>[] aclzs){
		if(signedByAnno(clz, aclzs)){
			Field[] fs = clz.getDeclaredFields();
			resList.addAll(Arrays.asList(fs));
			getFields(resList,clz.getSuperclass(),aclzs);
		}
	}
	
	private static boolean signedByAnno(Class<?> clz,Class<? extends Annotation>[] aclzs){
		if(clz==Object.class)
			return false;
		if(aclzs==null||aclzs.length==0)
			return true;
		for(Class<? extends Annotation> aclz:aclzs){
			Annotation a = clz.getAnnotation(aclz);
			if(a!=null)
				return true;
		}
		return false;
	}
	
	public static <T> Object getFieldValue(Class<T> tclz,T t,String fieldName){
		Field f = getField(tclz, fieldName);		
		try {
			return f.get(t);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> void setFieldValue(T t, String fieldName,
			Object value) {
		Field f = getField(t.getClass(), fieldName);
		setFieldValue(f, t, value);
	}
	
	public static <T> void setFieldValue(Field f,T t,Object value){
		try {
			f.setAccessible(true);
			f.set(t, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static <T> Method[] getMethods(Class<?> clz,Class<? extends Annotation>... aclzs){
		List<Method> resList = new ArrayList<Method>();
		getMethods(resList, clz, aclzs);
		return resList.toArray(new Method[]{});
	}
	
	private static void getMethods(List<Method> resList,Class<?> clz,Class<? extends Annotation>[] aclzs){
		if(signedByAnno(clz, aclzs)){
			Method[] fs = clz.getDeclaredMethods();
			resList.addAll(Arrays.asList(fs));
			getMethods(resList,clz.getSuperclass(),aclzs);
		}
	}
	
	public static Method getMethod(Class<?> clz,String name,Class<?>...parameterTypes){
		try {
			Method method = clz.getDeclaredMethod(name, parameterTypes);
			return method;
		} catch (NoSuchMethodException e) {
			if(clz!=Object.class)
				return getMethod(clz.getSuperclass(), name, parameterTypes);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> Object invokeMethod(T t,String methodName,Object...params){
		Class<?>[] parameterTypes = new Class<?>[params.length];
		for(int i=0;i<params.length;i++){
			parameterTypes[i] = params[i].getClass();
		}
		Method method = getMethod(t.getClass(), methodName, parameterTypes);
		return invokeMethod(t, method, params);
	}
	
	public static <T> Object invokeMethod(T t,Method method,Object...params){
		try {
			return method.invoke(t, params);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 深拷贝
	 * @param src
	 * @return
	 */
	public static <T> T deepClone(T src){
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(byteOut);
			out.writeObject(src);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
			return (T) in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return src;
	}
}
