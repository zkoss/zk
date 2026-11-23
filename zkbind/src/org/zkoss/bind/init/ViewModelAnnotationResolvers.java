/* ViewModelAnnotationResolvers.java

	Purpose:

	Description:

	History:
		Tue Mar 22 17:00:46 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.init;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.bind.ViewModelAnnotationResolver;
import org.zkoss.zk.ui.util.AggregationListener;

/**
 * An aggregating view model annotation resolvers.
 *
 * @author jameschu
 * @see ViewModelAnnotationResolver
 * @since 9.6.0
 */
public class ViewModelAnnotationResolvers implements AggregationListener {
	private static final Logger log = LoggerFactory.getLogger(ViewModelAnnotationResolvers.class);
	private static Map<String, ViewModelAnnotationResolver> _resolvers = new LinkedHashMap<>();

	public boolean isHandled(Class<?> klass) {
		if (ViewModelAnnotationResolver.class.isAssignableFrom(klass)) {
			try {
				synchronized (_resolvers) {
					if (!_resolvers.containsKey(klass.getName()))
						_resolvers.put(klass.getName(), (ViewModelAnnotationResolver) klass.newInstance());
				}
			} catch (Exception e) {
				log.error("Error when initial view model annotation resolver:" + klass, e);
			}
			return true;
		}
		return false;
	}

	private static List<ViewModelAnnotationResolver> getResolvers() {
		Collection<ViewModelAnnotationResolver> values;
		synchronized (_resolvers) {
			values = _resolvers.values();
		}
		return new LinkedList<ViewModelAnnotationResolver>(values);
	}

	/**
	 * Get the specific annotation from the method
	 *
	 * @param method
	 * @param annotationClass
	 * @return T annotation
	 * @since 9.6.0
	 */
	public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
		T anno = null;
		List<ViewModelAnnotationResolver> resolvers = getResolvers();
		for (ViewModelAnnotationResolver resolver : resolvers) {
			anno = resolver.getAnnotation(method, annotationClass);
			if (anno != null)
				break;
		}
		return anno;
	}

	/**
	 * Get the specific annotation from the class
	 *
	 * @param clazz
	 * @param annotationClass
	 * @return T annotation
	 * @since 9.6.0
	 */
	public static <T extends Annotation> T getAnnotation(Class clazz, Class<T> annotationClass) {
		T anno = null;
		List<ViewModelAnnotationResolver> resolvers = getResolvers();
		for (ViewModelAnnotationResolver resolver : resolvers) {
			anno = resolver.getAnnotation(clazz, annotationClass);
			if (anno != null)
				break;
		}
		return anno;
	}

	/**
	 * Get original method (if proxied)
	 *
	 * @param base
	 * @param method
	 * @return Method
	 * @since 9.6.0
	 */
	public static Method getOriginalMethod(Object base, Method method) {
		Method m;
		List<ViewModelAnnotationResolver> resolvers = getResolvers();
		for (ViewModelAnnotationResolver resolver : resolvers) {
			m = resolver.getOriginalMethod(base, method);
			if (m != null)
				break;
		}
		return method;
	}
}