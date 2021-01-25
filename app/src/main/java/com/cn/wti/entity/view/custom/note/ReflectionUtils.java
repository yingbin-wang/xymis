package com.cn.wti.entity.view.custom.note;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by wyb on 2017/4/21.
 */

public class ReflectionUtils {

    private static final String TAG = "ReflectionUtils";

    public static boolean hasMethod(String className, String method, Class[] params) {
        try {
            Class<?> targetClass = Class.forName(className);
            if (params != null) {
                targetClass.getMethod(method, params);
                return true;
            }
            targetClass.getMethod(method, new Class[0]);
            return true;
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return false;
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
            return false;
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
            return false;
        }
    }

    public static Method getMethod(String className, String method, Class[] params) {
        try {
            return Class.forName(className).getDeclaredMethod(method, params);
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
            return null;
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
            return null;
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
            return null;
        }
    }

    public static Field getField(String className, String name) {
        try {
            return Class.forName(className).getDeclaredField(name);
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
            return null;
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
            return null;
        } catch (ClassNotFoundException e4) {
            e4.printStackTrace();
            return null;
        }
    }
}
