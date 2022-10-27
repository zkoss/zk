/* StatelessOnly.java

	Purpose:
		
	Description:
		
	History:
		12:19 PM 2021/10/26, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.immutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation to indicate a method is declared in Stateless only not to use proxy
 * mechanism. (Internal use only)
 *
 * @author jumperchen
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface StatelessOnly {
}
