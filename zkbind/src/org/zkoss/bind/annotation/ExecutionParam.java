/* QueryParam.java

	Purpose:
		
	Description:
		
	History:
		2011/12/16 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to identify the name of a parameter of a method.
 * The value of this parameter is getting from current execution's attribute.
 * 
 * @see Init
 * @see Command
 * @author dennis
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecutionParam {
	/**
	 * name of the parameter
	 * @return name of the parameter
	 */
	String value();
}
