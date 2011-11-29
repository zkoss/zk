/* IgnoreZScript.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Nov 9, 2011 10:48:57 AM , Created by simonpai
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

import org.zkoss.zk.ui.select.GenericAnnotatedComposer;

/**
 * Annotation for specifying to wire zscript when auto-wiring in 
 * {@link GenericAnnotatedComposer}.
 * @since 6.0.0
 * @author simonpai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WireZScript {
	
	/**
	 * If true, the composer will wire variables declared in zscript. If this
	 * annotation is absent (in all of composers' ancestors), or present with 
	 * value "false", zscript variable is NOT wired.
	 */
	boolean value() default true;
	
}
