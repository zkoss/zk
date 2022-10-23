/*
	F96_ZK_1209ViewModelAnnotationHandler.java
	Purpose:

	Description:

	History:
		Tue Mar 16 15:47:25 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.zkoss.bind.ViewModelAnnotationResolver;
import org.zkoss.zk.ui.util.Clients;

/**
 * @author jameschu
 */
public class F96_ZK_1209ViewModelAnnotationHandler implements ViewModelAnnotationResolver {
	public <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
		Clients.log("getAnnotation: method->" + method.getName() + ", annotation->" + annotationClass.getName());
		return method.getAnnotation(annotationClass);
	}

	public <T extends Annotation> T getAnnotation(Class clazz, Class<T> annotationClass) {
		Clients.log("getAnnotation: class->" + clazz.getName() + ", annotation->" + annotationClass.getName());
		return (T) clazz.getAnnotation(annotationClass);
	}

	public Method getOriginalMethod(Object base, Method method) {
		Clients.log("getOriginalMethod: method->" + method.getName());
		return method;
	}
}
