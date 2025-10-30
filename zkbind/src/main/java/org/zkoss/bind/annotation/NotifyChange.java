/* NotifyChange.java

	Purpose:
		
	Description:
		
	History:
		2011/12/15 Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Notify value change.  <br/>
 * By default, a property set by binder will notify this property changed.  <br/>
 * You could use this annotation on the set method to change or add notification target. <br/>
 * You could also add this annotation on a command method to notify properties that will be changed after the command. <br/>
 * To avoid the default notification, use {@link NotifyChangeDisabled} on the set method independently.    <br/>
 *  
 * @author henrichen
 * @see NotifyChangeDisabled
 * @since 6.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotifyChange {
	String[] value() default {};
}
