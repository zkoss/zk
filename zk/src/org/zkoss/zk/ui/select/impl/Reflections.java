/**
 * 
 */
package org.zkoss.zk.ui.select.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A collection of reflection utilities.
 * @since 6.0.0
 * @author simonpai
 */
public class Reflections {
	
	// field scanner //
	/**
	 * Traverse through all fields with a certain annotation in a class and 
	 * it super classes. Subclass first. If a field is re-declared in subclass, 
	 * it will be skipped at the superclass.
	 */
	public static <A extends Annotation> void forFields(Class<?> clazz, 
			Class<A> annotationClass, FieldRunner<A> runner){
		final Set<String> handled = new HashSet<String>();
		for(Class<?> c = clazz; c != null; c = c.getSuperclass()) {
			for(Field f : c.getDeclaredFields()){
				A anno = f.getAnnotation(annotationClass);
				if(anno == null) continue;
				// skip if handled in subclass
				if(handled.contains(f.getName())) continue;
				f.setAccessible(true);
				runner.onField(c, f, anno);
				handled.add(f.getName());
			}
		}
	}
	
	// method scanner //
	/**
	 * Traverse through all methods with a certain annotation in a class, 
	 * including ones inherited from its super class.
	 */
	public static <A extends Annotation> void forMethods(Class<?> clazz, 
			Class<A> annotationClass, MethodRunner<A> runner){
		for(Method m : clazz.getMethods()){
			A anno = m.getAnnotation(annotationClass);
			if(anno == null) continue;
			m.setAccessible(true);
			runner.onMethod(clazz, m, anno);
		}
	}
	
	
	
	// field information //
	/**
	 * Return true if field is of Collection type, and an object of type clazz
	 * can be added into the Collection.
	 */
	public static boolean isAppendableToCollection(Type type, Object object){
		if(!Collection.class.isAssignableFrom(getClass(type)))
				return false;
		
		if(type instanceof ParameterizedType)
			return getClass(((ParameterizedType) type)
					.getActualTypeArguments()[0]).isAssignableFrom(
							object.getClass());
		
		return type instanceof Class<?>;
	}
	
	
	
	// method information //
	/**
	 * Return true if the given arguments can be passed as the arguments of 
	 * the method.
	 */
	public static boolean isPassableToMethod(Method method, Object ... arguments){
		Type[] types = method.getGenericParameterTypes();
		if(types.length != arguments.length) return false;
		for(int i = 0; i < types.length; i++)
			if(!getClass(types[i]).isAssignableFrom(arguments[i].getClass()))
				return false;
		return true;
	}
	
	
	
	// field operation //
	/**
	 * Set a value to a field of the object.
	 */
	public static void setFieldValue(Object bean, Field field, Object value){
		try {
			field.set(bean, value);
			
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(
					"IllegalStateException @ setFieldValue");
			
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(
					"IllegalAccessException @ setFieldValue");
		}
	}
	
	/**
	 * Add the item to a collection field of an object.
	 */
	@SuppressWarnings("unchecked")
	public static void addToCollectionField(Object owner, Field field, 
			Object item){
		if(!isAppendableToCollection(field.getGenericType(), item))
			throw new IllegalArgumentException(
					item + " is not appendable to " + field);
		try {
			field.setAccessible(true);
			Collection c = (Collection) field.get(owner);
			if(c == null) throw new IllegalArgumentException(
					field + " has not been initiated.");
			c.add(item);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Invoke a method reflexively.
	 */
	public static void invokeMethod(Method method, Object bean, 
			Object ... arguments){
		if(!isPassableToMethod(method, arguments))
			throw new IllegalArgumentException(
					"Arguments does not match method signature: " + method);
		method.setAccessible(true);
		try {
			method.invoke(bean, arguments);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	// interface //
	public interface FieldRunner<A extends Annotation> {
		public void onField(Class<?> clazz, Field field, A annotation);
	}
	
	public interface MethodRunner<A extends Annotation> {
		public void onMethod(Class<?> clazz, Method method, A annotation);
	}
	
	/**
	 * Return the Class representing the given Type.
	 */
	public static Class<?> getClass(Type type) {
		if (type instanceof Class<?>) return (Class<?>) type;
		
		else if (type instanceof ParameterizedType)
			return getClass(((ParameterizedType) type).getRawType());
		
		else if (type instanceof GenericArrayType) {
			Class<?> c = 
				getClass(((GenericArrayType) type).getGenericComponentType());
			if (c != null) return Array.newInstance(c, 0).getClass();
		}
		return null;
	}
	
}
