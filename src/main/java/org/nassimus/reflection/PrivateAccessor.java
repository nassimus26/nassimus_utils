package org.nassimus.reflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
/**
 * Created by VF5416 on 29/08/2016.
 */

/**
 * Provides access to private members in classes.
 * @version 0.1
 * @author Moualek Nassim
 */
public class PrivateAccessor {
    static final private Logger log = LoggerFactory.getLogger(PrivateAccessor.class);
    public static Object getField (Object o, String fieldName) {
        // Go and find the private field...
        return _getField(o, o.getClass(), fieldName);
    }
    private static Object _getField (Object o, Class clz, String fieldName) {
        final Field fields[] = clz.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if (fieldName.equals(fields[i].getName())) {
                try {
                    fields[i].setAccessible(true);
                    return fields[i].get(o);
                }
                catch (IllegalAccessException e) {
                    log.error(e.getMessage() , e);
                }
            }
        }
        if (!clz.getSuperclass().equals(Object.class))
            return _getField(o, clz.getSuperclass(), fieldName);
        return null;
    }
    public static void setField (Object o, String fieldName, Object value) {
        // Go and find the private field...
        _setField(o, o.getClass(), fieldName, value);
    }
    private static void _setField (Object o, Class clz, String fieldName, Object value) {
        final Field fields[] = clz.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if (fieldName.equals(fields[i].getName())) {
                try {
                    fields[i].setAccessible(true);
                    fields[i].set(o,value);
                    break;
                }
                catch (IllegalAccessException e) {
                    log.error(e.getMessage() , e);
                }
            }
        }
        if (!clz.getSuperclass().equals(Object.class))
            _setField(o, clz.getSuperclass(), fieldName, value);
    }
    public static Object invoke(Object o, String methodName) {
        return invoke(o, methodName, new Class[0], new Object[0]);
    }
    public static Object invoke(Object o, String methodName, Object[] params) {
        return invoke(o, methodName, null, new Object[0]);
    }
    public static Object invoke(Object o, String methodName,  Class<?>[] paramsTypes, Object[] params) {
        Method method = null;
        try {
            if (paramsTypes!=null)
                method = o.getClass().getDeclaredMethod(methodName);
            else
                method = o.getClass().getDeclaredMethod(methodName, paramsTypes);
            method.setAccessible(true);
            return method.invoke(o, params);
        } catch (Exception e) {
            log.error(e.getMessage() , e);
        }
/*
        final Method methods[] = o.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            if (methodName.equals(methods[i].getName())) {
                try {
                    methods[i].setAccessible(true);
                    return methods[i].invoke(o, params);
                }
                catch (Exception e) {
                    log.error(e.getMessage() , e);
                }
            }
        }
*/
        return null;
    }
}
