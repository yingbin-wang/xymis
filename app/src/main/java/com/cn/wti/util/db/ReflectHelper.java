package com.cn.wti.util.db;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import dalvik.system.DexFile;

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

	/**
	 获取当前路径
	 */
	public static String getSrcPath() throws IOException{
		File file=new File("");
		String path="src"+File.separator+"mian"+File.separator+"java";
		return path;
	}

	/**
	 * 从包package中获取所有的Class
	 * @param packageName
	 * @return
	 */
	public static List<Class<?>> getClasses(String packageName){

		//第一个class类的集合
		List<Class<?>> classes = new ArrayList<Class<?>>();
		//是否循环迭代
		boolean recursive = true;
		//获取包的名字 并进行替换
		String packageDirName = packageName.replace('.', '/');
		//定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(getSrcPath()+File.separator+packageDirName);
			//循环迭代下去
			while (dirs.hasMoreElements()){
				//获取下一个元素
				URL url = dirs.nextElement();
				//得到协议的名称
				String protocol = url.getProtocol();
				//如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					//获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					//以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				} else if ("jar".equals(protocol)){
					//如果是jar包文件
					//定义一个JarFile
					JarFile jar;
					try {
						//获取jar
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						//从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						//同样的进行循环迭代
						while (entries.hasMoreElements()) {
							//获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							//如果是以/开头的
							if (name.charAt(0) == '/') {
								//获取后面的字符串
								name = name.substring(1);
							}
							//如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								//如果以"/"结尾 是一个包
								if (idx != -1) {
									//获取包名 把"/"替换成"."
									packageName = name.substring(0, idx).replace('/', '.');
								}
								//如果可以迭代下去 并且是一个包
								if ((idx != -1) || recursive){
									//如果是一个.class文件 而且不是目录
									if (name.endsWith(".class") && !entry.isDirectory()) {
										//去掉后面的".class" 获取真正的类名
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										try {
											//添加到classes
											classes.add(Class.forName(packageName + '.' + className));
										} catch (ClassNotFoundException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes){
		//获取此包的目录 建立一个File
		File dir = new File(packagePath);
		//如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		//如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			//自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		//循环所有文件
		for (File file : dirfiles) {
			//如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
						file.getAbsolutePath(),
						recursive,
						classes);
			}
			else {
				//如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					//添加到集合中去
					classes.add(Class.forName(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static List<String > getClassName(String packageCodePath, String packageName){
		List<String >classNameList=new ArrayList<String >();
		try {

			DexFile df = new DexFile(packageCodePath);//通过DexFile查找当前的APK中可执行文件
			Enumeration<String> enumeration = df.entries();//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
			while (enumeration.hasMoreElements()) {//遍历
				String className = (String) enumeration.nextElement();

				if (className.contains(packageName)) {//在当前所有可执行的类里面查找包含有该包名的所有类
					classNameList.add(className);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return  classNameList;
	}

	public static List getClassesName1(String packageCodePath, String packageName) {
		ArrayList classes = new ArrayList<>();
		try {
			DexFile df = new DexFile(packageCodePath);
			String regExp = "^" + packageName + ".\\w+$";
			for (Enumeration iter = df.entries(); iter.hasMoreElements(); ) {
				String className = (String) iter.nextElement();
				if (className.matches(regExp)) {
					classes.add(className);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classes;
	}
}
