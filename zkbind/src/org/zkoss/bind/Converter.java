/* Converter.java

	Purpose:
		
	Description:
		
	History:
		Jun 22, 2011 9:55:14 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind;

import org.zkoss.zk.ui.Component;

/**
 * Generic binding conversion interface.
 * <b>U</b> is the object type for the Component, <b>B</b> is the object type for the Bean
 * and <b>C</b> is the component type of the converter. 
 * @author henrichen
 * @author dennis
 * @since 6.0.0
 */
public interface Converter<U,B,C extends Component> {
	
	/**
	 * Indicates the returned value should be ignored to load to a component or save to a bean, 
	 * It is useful for writing a converter to manipulate component directly when loading or saving
	 * @since 6.0.1
	 */
	public Object IGNORED_VALUE = new Object();
	
	/**
	 * Coerces a value to another value to load to a component
	 * @param val the bean value
	 * @param component the component to be loaded the value
	 * @param ctx the bind context
	 * @return the value to load to a component
	 */
	public U coerceToUi(B val, C component, BindContext ctx);
	
	/**
	 * Coerces a value to bean value to save to a bean
	 * @param val the value
	 * @param component the component provides the value
	 * @param ctx the bind context
	 * @return the value to save to a bean
	 */
	public B coerceToBean(U val, C component, BindContext ctx);
}
