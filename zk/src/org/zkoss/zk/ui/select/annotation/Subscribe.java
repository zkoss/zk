/* Subscribe.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Mar 29, 2012 4:22:08 PM , Created by simonpai
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

import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;

/**
 * Annotation for subscribing an EventQueue with the annotated method.
 * {@link SelectorComposer}.
 * @since 6.0.1
 * @author simonpai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
	
	/**
	 * Name of the EventQueue.
	 */
	String value();
	
	/**
	 * Scope of the EventQueue. Available values are {@link EventQueues#DESKTOP}, 
	 * {@link EventQueues#GROUP}, {@link EventQueues#SESSION}, and 
	 * {@link EventQueues#APPLICATION}.
	 * Note: {@link #GROUP} requires ZK EE.
	 */
	String scope() default EventQueues.DESKTOP;
	
}
