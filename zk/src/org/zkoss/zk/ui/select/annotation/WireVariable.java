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
 * Annotation for specifying variables to wire 
 * {@link SelectorComposer}.
 * @since 6.0.0
 * @author simonpai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface WireVariable {
	
	/**
	 * The selector string that specifies the Components to wire. If empty, 
	 * {@link SelectorComposer} will attempt to wire from implicit objects,
	 * XEL variables.
	 */
	String value() default "";
	
	/**
	 * If set to true, no Exception is throw when component/object is not found
	 * for wiring. By default, when {@link SelectorComposer} fails to wire an 
	 * object to a field, an UiException will be thrown. 
	 */
	boolean optional() default false;
	
	/**
	 * If true, the variable will be rewired when the composer is deserialized 
	 * in cluster environment. Session and Webapp variables are always rewired.
	 */
	boolean rewireOnActivate() default false;
	
}
