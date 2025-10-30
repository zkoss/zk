/* Default.java

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
 * Marker annotation to identify default value of a parameter of a method. 
 * 
 * @see Init
 * @see Command
 * @author dennis
 * @since 6.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {
	/**
	 * Default value of the parameter. 
	 * It is string when you assign it in annotation, and will coerce to the corresponding type of the parameter if parameter value is not null.
	 * @return default value of the parameter
	 */
	String value();
}
