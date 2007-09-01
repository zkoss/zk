/* ELXelResolver.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 30 14:43:50     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.xel;

import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelException;

/**
 * A XEL variable resolver that is based on an EL variable resolver.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class ELXelResolver implements VariableResolver {
	private final javax.servlet.jsp.el.VariableResolver _resolver;

	/** Constructor
	 * @param resolver the XEL variable resolver. Ignore if null.
	 */
	public ELXelResolver(javax.servlet.jsp.el.VariableResolver resolver) {
		_resolver = resolver;
	}

	//VariableResolver//
	public Object resolveVariable(String name) {
		try {
			return _resolver != null ? _resolver.resolveVariable(name): null;
		} catch (javax.servlet.jsp.el.ELException ex) {
			throw new XelException(ex);
		}
	}
}
