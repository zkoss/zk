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
 * <b>U</b> is the value type of the Component attribute, <b>B</b> is the property type of the Bean
 * and <b>C</b> is the component type of the converter. 
 * <p/> 
 * 
 * @author henrichen
 * @author dennis
 * @since 6.0.0
 */
public interface Converter<U,B,C extends Component> {
	
	/**
	 * Indicates the returned value should be ignored to load to a component or save to a bean, 
	 * It is useful for writing a converter to manipulate component directly when loading or saving
	 * Note : <br/>
	 * If you want to return {@link #IGNORED_VALUE} in the converter method, then you need to set <b>U</b> or <b>B</b> to Object.
	 * @since 6.0.1
	 */
	public Object IGNORED_VALUE = new Object();
	
	/**
	 * Coerces a value to another value to load to a component
	 * @param beanProp the bean value
	 * @param component the component to be loaded the value
	 * @param ctx the bind context
	 * @return the value to load to a component
	 */
	public U coerceToUi(B beanProp, C component, BindContext ctx);
	
	/**
	 * Coerces a value to bean value to save to a bean
	 * @param compAttr the value of component attribute.
	 * @param component the component provides the value
	 * @param ctx the bind context
	 * @return the value to save to a bean
	 */
	public B coerceToBean(U compAttr, C component, BindContext ctx);
}
