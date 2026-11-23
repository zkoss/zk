/* ViewModelAnnotationResolver.java

	Purpose:

	Description:

	History:
		Mon Mar 15 12:50:22 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * The annotation resolver to handle annotations in view model.
 *
 * @author jameschu
 * @since 9.6.0
 */
public interface ViewModelAnnotationResolver {
	/**
	 * Get the specific annotation from the method
	 *
	 * @param method
	 * @param annotationClass
	 * @return T annotation
	 * @since 9.6.0
	 */
	public <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass);

	/**
	 * Get the specific annotation from the class
	 *
	 * @param clazz
	 * @param annotationClass
	 * @return T annotation
	 * @since 9.6.0
	 */
	public <T extends Annotation> T getAnnotation(Class clazz, Class<T> annotationClass);

	/**
	 * Get original method (if proxied)
	 *
	 * @param base
	 * @param method
	 * @return Method
	 * @since 9.6.0
	 */
	public Method getOriginalMethod(Object base, Method method);
}