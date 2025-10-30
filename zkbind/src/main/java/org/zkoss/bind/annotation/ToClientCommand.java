/** ToClientCommand.java.

	Purpose:
		
	Description:
		
	History:
		3:54:38 PM Mar 17, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Notify a command to client.
 * <p>
 * The value of the annotation can accept <tt>"*"</tt> to allow all commands to
 * client.
 * 
 * @author jumperchen
 * @since 8.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ToClientCommand {
	String[] value() default {};
}
