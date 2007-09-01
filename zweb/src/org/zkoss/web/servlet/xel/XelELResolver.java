/* XelELResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 14:15:18     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import org.zkoss.xel.VariableResolver;

/**
 * An EL variable resolver that is based on XEL variable resolver.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class XelELResolver implements javax.servlet.jsp.el.VariableResolver {
	private final VariableResolver _resolver;

	/**
	 * @param resolver the XEL variable resolver. Ignore if null.
	 */
	public XelELResolver(VariableResolver resolver) {
		_resolver = resolver;
	}

	//VariableResolver//
	public Object resolveVariable(String name) {
		return _resolver != null ? _resolver.resolveVariable(name): null;
	}
}
