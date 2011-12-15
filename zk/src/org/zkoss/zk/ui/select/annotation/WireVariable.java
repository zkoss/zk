/* WireVariable.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Dec 13, 2011 4:42:38 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.select.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zkoss.zk.ui.select.SelectorComposer;

/**
 * Annotation for specifying variables to wire, from XEL, implicit objects, or
 * custom variable resolvers.
 * {@link SelectorComposer}.
 * @since 6.0.0
 * @author simonpai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface WireVariable {
	
	/**
	 * The name of variable to wire. If empty, it will use field name or method
	 * name (without "set" prefix).
	 */
	String value() default "";
	
	/**
	 * If set to true, no Exception is throw when the variable is not found
	 * for wiring. By default, when {@link SelectorComposer} fails to wire an 
	 * object to a field/method, an UiException will be thrown. 
	 */
	boolean optional() default false;
	
	/**
	 * If true, the variable will be rewired when the composer is deserialized 
	 * in cluster environment. Session and Webapp variables are always rewired.
	 */
	boolean rewireOnActivate() default false;
	
}
