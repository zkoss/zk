/* XelELResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 31 16:33:31     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.xel.el;

import org.zkoss.xel.VariableResolver;

/**
 * An EL variable resolver that is based on a XEL variable resolver.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class XelELResolver implements javax.servlet.jsp.el.VariableResolver {
	private final VariableResolver _resolver;

	public XelELResolver(VariableResolver resolver) {
		_resolver = resolver;
	}
	public Object resolveVariable(String name) {
		return _resolver != null ? _resolver.resolveVariable(name): null;
	}
}
