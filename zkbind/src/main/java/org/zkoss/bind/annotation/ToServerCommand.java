/** ToServerCommand.java.

	Purpose:
		
	Description:
		
	History:
		2:04:36 PM May 20, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Notify a command to server.
 * <p>
 * The value of the annotation can accept <tt>"*"</tt> to allow all commands to
 * server.
 * 
 * @author jumperchen
 * @since 8.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToServerCommand {
	String[] value() default {};
}
