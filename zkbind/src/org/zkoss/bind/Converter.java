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
 * @author henrichen
 * @since 6.0.0
 */
public interface Converter {
	public Object coerceToUi(Object val, Component component, BindContext ctx);
	public Object coerceToBean(Object val, Component component, BindContext ctx);
}
