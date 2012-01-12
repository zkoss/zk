/* Reference.java

	Purpose:
		
	Description:
		
	History:
		Jan 12, 2012 9:03:41 AM, Created by henrichen

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.sys;

import org.zkoss.bind.xel.zel.BindELContext;

/**
 * A reference to an expression.
 * @author henrichen
 * @since 6.0.0
 * @see BindELContext#getVariableMapper
 */
public interface Reference {
	public Object getValue(BindELContext ctx); //@see BindELResolver
}
