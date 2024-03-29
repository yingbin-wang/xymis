package com.cn.wti.util.page;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PageDataSingleton extends HashMap implements Map{
	
	private static final long serialVersionUID = 1L;
	
	Map map = null;
	
	private static PageDataSingleton instance;  
	
	public static synchronized PageDataSingleton getInstance() {  
		if (instance == null) {  
			instance = new PageDataSingleton();  
		}  
		return instance;   
	}
	
	public PageDataSingleton(Map<String, Object> map){
		
		this.map = map;
	}
	
	public PageDataSingleton() {
		map = new HashMap();
	}
	
	@Override
	public Object get(Object key) {
		Object obj = null;
		obj = map.get(key);
		return obj;
	}
	
	public String getString(Object key) {
		return (String)get(key);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object put(Object key, Object value) {
		return map.put(key, value);
	}
	
	@Override
	public Object remove(Object key) {
		return map.remove(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return map.containsValue(value);
	}

	public Set entrySet() {
		// TODO Auto-generated method stub
		return map.entrySet();
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return map.isEmpty();
	}

	public Set keySet() {
		// TODO Auto-generated method stub
		return map.keySet();
	}

	@SuppressWarnings("unchecked")
	public void putAll(Map t) {
		// TODO Auto-generated method stub
		map.putAll(t);
	}

	public int size() {
		// TODO Auto-generated method stub
		return map.size();
	}

	public Collection values() {
		// TODO Auto-generated method stub
		return map.values();
	}
	
}
