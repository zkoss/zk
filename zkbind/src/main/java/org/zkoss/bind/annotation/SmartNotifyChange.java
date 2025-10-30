/** SmartNotifyChange.java.

	Purpose:
		
	Description:
		
	History:
		5:05:31 PM Jan 7, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Notify value change once it changed, unlike {@link NotifyChange}.
 * <p>
 * Usually the annotation is used with {@link Command}.
 * @author jumperchen
 * @since 8.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SmartNotifyChange {
	String[] value() default {};
}
