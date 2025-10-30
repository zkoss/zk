/* Command.java

	Purpose:
		
	Description:
		
	History:
		2:12 PM 7/15/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to identify a Command method for {@link org.zkoss.zk.ui.util.Composer}
 * to use.
 * @author jumperchen
 * @since 8.0.0
 * @see org.zkoss.zk.ui.util.ConventionWires#wireServiceCommand(org.zkoss.zk.ui.Component, Object) 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String[] value() default {};
}
