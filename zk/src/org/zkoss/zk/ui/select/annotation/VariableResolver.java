/* VariableResolver.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Dec 13, 2011 6:34:56 PM , Created by simonpai
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

/**
 * Annotation for adding variable resolvers to a SelectorComposer.
 * @author simonpai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VariableResolver {
	
	/**
	 * Variable resolver class names. It is assumed each class specified here 
	 * has a default constructor.
	 */
	Class<? extends org.zkoss.xel.VariableResolver>[] value();
	
}
