/* ContextParam.java

	Purpose:
		
	Description:
		
	History:
		2011/12/15 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to identify the name of a parameter of a method. <br/>
 * The value of this parameter is getting from the special {@link ContextType}
 * 
 * @see Command
 * @see Default
 * @author dennis
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ContextParam {
	/**
	 * the {@link ContextType}
	 * @return type of the context
	 */
	ContextType value();
}
