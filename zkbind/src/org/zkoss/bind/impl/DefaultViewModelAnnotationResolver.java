/* DefaultViewModelAnnotationResolver.java
	Purpose:

	Description:

	History:
		Tue Mar 23 17:39:46 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.zkoss.bind.ViewModelAnnotationResolver;

/**
 * A default view model annotation resolver
 * @author jameschu
 */
public class DefaultViewModelAnnotationResolver implements ViewModelAnnotationResolver {
	public <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
		return method.getAnnotation(annotationClass);
	}

	public <T extends Annotation> T getAnnotation(Class clazz, Class<T> annotationClass) {
		return (T) clazz.getAnnotation(annotationClass);
	}

	public Method getOriginalMethod(Object base, Method method) {
		return method;
	}
}
