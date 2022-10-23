/* RichletMapping.java

	Purpose:
		
	Description:
		
	History:
		4:08 PM 2021/12/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a mapping of Http Request path with GET method to a
 * {@link org.zkoss.zephyr.ui.StatelessRichlet}'s method handler or its implementation class.
 *
 * @author jumperchen
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RichletMapping {
	/**
	 * The url value for the mapping.
	 */
	String value();
}
