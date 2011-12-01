/* Command.java

	Purpose:
		
	Description:
		
	History:
		Jul 5, 2011 2:56:03 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to identify a Command method. 
 * A Command method could also use {@link Param} to assign a binding argument as its parameter, 
 * and {@link DefaultValue} to assign a default value if the binding argument is null.
 * 
 * @see {@link Param}
 * @see {@link DefaultValue}
 * @author henrichen
 * @author dennischen
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String[] value() default {};
}
