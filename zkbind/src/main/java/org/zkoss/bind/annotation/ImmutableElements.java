/** ImmutableElements.java.

	Purpose:
		
	Description:
		
	History:
		9:45:00 AM Apr 30, 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zkoss.bind.proxy.FormProxyObject;

/**
 * Marker annotation to indicate that the elements of the collection for
 * {@link FormProxyObject} are immutable class.
 * 
 * @author jumperchen
 * @since 8.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ImmutableElements {

}
