/* PortletServletDispatcher.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul  7 12:35:32     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import javax.portlet.PortletRequestDispatcher;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * A facade of a PortletRequestDispatch for implementing RequestDispatcher.
 * 
 * @author tomyeh
 */
public class PortletServletDispatcher implements RequestDispatcher {
	private final PortletRequestDispatcher _rd;

	/** Returns a facade instance for the specified dispatcher. */
	public static final
	RequestDispatcher getInstance(PortletRequestDispatcher rd) {
		if (rd instanceof RequestDispatcher)
			return (RequestDispatcher)rd;
		return new PortletServletDispatcher(rd);
	}
	private PortletServletDispatcher(PortletRequestDispatcher rd) {
		if (rd == null)
			throw new IllegalArgumentException("null");
		_rd = rd;
	}

	//RequestDispatcher//
	public void forward(ServletRequest request, ServletResponse response)
	throws ServletException, java.io.IOException {
		throw new UnsupportedOperationException();
	}
	public void include(ServletRequest request, ServletResponse response)
	throws ServletException, java.io.IOException {
		throw new UnsupportedOperationException(); //TODO
	}
}
