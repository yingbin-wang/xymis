package com.hyphenate.easeui.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Administrator
 *	反射工具
 */
@SuppressWarnings({"unused","rawtypes","unchecked","resource"})
public class ReflectHelper {

	/**
	 * 通过类名获取 类对象
	 * @param className
	 * @return
     */
	public static Class getCalss(String className){

		Class cObj = null;
		try {
			cObj = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return  cObj;
	}

	/**
	 * 获取obj对象fieldName的Field
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Field getFieldByFieldName(Object obj, String fieldName) {
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
			}
		}
		return null;
	}

	/**
	 * 获取obj对象fieldName的属性值
	 * @param obj
	 * @param fieldName
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Object getValueByFieldName(Object obj, String fieldName)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field field = getFieldByFieldName(obj, fieldName);
		Object value = null;
		if(field!=null){
			if (field.isAccessible()) {
				value = field.get(obj);
			} else {
				field.setAccessible(true);
				value = field.get(obj);
				field.setAccessible(false);
			}
		}
		return value;
	}

	/**
	 * 设置obj对象fieldName的属性值
	 * @param obj
	 * @param fieldName
	 * @param value
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void setValueByFieldName(Object obj, String fieldName,
			Object value) throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field field = obj.getClass().getDeclaredField(fieldName);
		if (field.isAccessible()) {
			field.set(obj, value);
		} else {
			field.setAccessible(true);
			field.set(obj, value);
			field.setAccessible(false);
		}
	}
	
	
	/**
	 * @param className 类名
	 * @param methodName 方法名
	 * @param param     方法参数
	 * @param parameterType 方法参数类型
	 * @throws Exception
	 */
	
	private static void callMethod(String className,String methodName,Object[] param,Class...parameterType) throws Exception {
		Class cObj = Class.forName(className);
		Method m = cObj.getDeclaredMethod(methodName,parameterType);
		Object o = m.invoke(cObj.newInstance(),param);
	}
	
	/**
	 * 根据实例对象 反射执行指定方法名
	 * @param o 实例对象
	 * @param methodName 方法名
	 * @param param 参数数组
	 * @param parameterType 参数类型
	 * @return
	 * @throws Exception
	 */
	public static Object callMethod2(Object o,String methodName,Object[] param,Class...parameterType) throws Exception {
		Class cObj = o.getClass();
		Method m = null;
		if (param==null) {
			try {
				m = cObj.getDeclaredMethod(methodName);
			} catch (Exception e) {
				m = cObj.getMethod(methodName);
			}
			
			return m.invoke(o);
		}else {
			try {
				m = cObj.getDeclaredMethod(methodName,parameterType);
			} catch (Exception e) {
				m = cObj.getMethod(methodName, parameterType);
			}
			
			return m.invoke(o,param);
		}
		
	}
}
