/** ClientCommand.java.

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
 * <p> By default, the {link #notifyClient()} will be false.
 * If the server side VM object is not associated with a command method,
 * it won't throw an exception.  
 * @author jumperchen
 * @since 8.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientCommand {
	String[] value() default {};
}
