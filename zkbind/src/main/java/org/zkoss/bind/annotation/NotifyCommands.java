/** NotifyCommands.java.

	Purpose:
		
	Description:
		
	History:
		5:54:38 PM Mar 17, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Notify a set of commands to either server or client.
 * @author jumperchen
 * @since 8.0.0
 * @see NotifyCommand
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotifyCommands {
	NotifyCommand[] value() default {};
}
