/* RichletMapping.java

	Purpose:
		
	Description:
		
	History:
		4:08 PM 2021/12/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.zkoss.stateless.ui.StatelessRichlet;

/**
 * Represents a mapping of Http Request path with GET method to a
 * {@link StatelessRichlet}'s method handler or its implementation class.
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
