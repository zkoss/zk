/* IgnoreXel.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Nov 9, 2011 10:57:12 AM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.select;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying to wire XEL context when auto-wiring in 
 * @{GenericAnnotatedComposer}.
 * @since 6.0.0
 * @author simonpai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WireXel {
	
	/**
	 * If true, the composer will wire variables from XEL context. If this
	 * annotation is absent (in all of composers' ancestors), or present with 
	 * value "false", XEL variable is NOT wired.
	 */
	boolean value() default true;
	
}
