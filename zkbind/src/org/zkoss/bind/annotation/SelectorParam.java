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

import org.zkoss.bind.Binder;
import org.zkoss.zk.ui.select.Selectors;

/**
 * Marker annotation to identify the component of a parameter of a method &lt;br/&gt;
 * The {@linkplain #value()} is the selector to find components. It is {@link Selectors#find(org.zkoss.zk.ui.Component, String)} to select the components. &lt;br/&gt;
 * If {@linkplain #local()} is false, it uses the root component of the binder ({@link Binder#getView()}) as the base component. &lt;br/&gt;
 * If {@linkplain #local()} is true, it uses the binded component (a binded component, in {@linkplain Init}, it is root component of the binder too. 
 * In {@linkplain Command}, it is the component who triggers the command) as the base component. &lt;br/&gt;
 * &lt;br/&gt;
 * 
 * {@linkplain #index()} is the index of the selected components, if {@linkplain #index()} small than 0  you have to use List&lt;Component&gt; as the parameter type.
 * 
 * 
 * @see Init
 * @see Command
 * @see Selectors#find(org.zkoss.zk.ui.Component, String)
 * @see Selectors#find(org.zkoss.zk.ui.Component, String, int)
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
	
	/**
	 * To define to select component from local binded component or root of the binder.
	 * @return true if select from root of the binder, default false
	 */
	boolean local() default false;
	
	/**
	 * The index of selected component. If the index is -1 you have to use List&lt;Component&gt; as the parameter type 
	 * @return the index of selected component, default 0.
	 */
	int index() default 0;
}
