/* SelectorParam.java

	Purpose:
		
	Description:
		
	History:
		2011/12/16 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import org.zkoss.zk.ui.select.Selectors;

/**
 * Marker annotation to identify the component of a parameter of a method &lt;br/&gt;
 * The {@linkplain #value()} is the selector to find components. It uses {@link Selectors#find(org.zkoss.zk.ui.Component)} to select the components. &lt;br/&gt;
 * The base component of the selector is the view component of the binder.
 * &lt;br/&gt;
 * If the parameter type is a {@link Collection}, it passes the result directly. Otherwise it passes the first result or null if no result.
 * 
 * @see Init
 * @see Command
 * @see Selectors#find(org.zkoss.zk.ui.Component)
 * @author dennis
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectorParam {
	/**
	 * component selector of the parameter
	 * @return component selector of the parameter
	 */
	String value();
}
