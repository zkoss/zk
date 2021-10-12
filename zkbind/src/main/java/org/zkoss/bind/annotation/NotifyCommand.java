/** NotifyCommand.java.

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
 * Notify a command to either server or client.
 * If the server side VM object is not associated with a given command method,
 * it won't throw an exception.  
 * @author jumperchen
 * @since 8.0.0
 * @see NotifyCommands
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotifyCommand {
	String[] value() default {};

	String onChange() default "null"; // cannot put empty string here.
}
