/*
	B101_ZK_5764ViewModelAnnotationHandler.java
	Purpose:

	Description:

	History:
		Fri Aug 02 12:50:22 CST 2024, Created by jameschu

Copyright (C) 2024 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.zkoss.bind.ViewModelAnnotationResolver;

/**
 * @author jameschu
 */
public class B101_ZK_5764ViewModelAnnotationHandler implements ViewModelAnnotationResolver {
	public <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
		return method.getAnnotation(annotationClass);
	}

	public <T extends Annotation> T getAnnotation(Class clazz, Class<T> annotationClass) {
		return (T) clazz.getAnnotation(annotationClass);
	}

	public Method getOriginalMethod(Object base, Method method) {
		try {
			String targetMethodName = "update";
			if (targetMethodName.equals(method.getName())) {
				return this.getClass().getMethod(targetMethodName);
			}
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		return method;
	}

	public void update() {
	}
}
