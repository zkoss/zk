/* ServletPortletDispatcher.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 17 00:44:46     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

/**
 * A facade of a RequestDispatch for implementing PortletRequestDispatcher.
 *
 * @author tomyeh
 */
public class ServletPortletDispatcher implements PortletRequestDispatcher {
	private final RequestDispatcher _rd;

	/** Returns a facade instance for the specified dispatcher. */
	public static final
	PortletRequestDispatcher getInstance(RequestDispatcher rd) {
		if (rd instanceof PortletRequestDispatcher)
			return (PortletRequestDispatcher)rd;
		return new ServletPortletDispatcher(rd);
	}
	private ServletPortletDispatcher(RequestDispatcher rd) {
		if (rd == null)
			throw new IllegalArgumentException("null");
		_rd = rd;
	}

	public void include(RenderRequest request, RenderResponse response)
	throws PortletException, java.io.IOException {
		try {
			_rd.include(RenderHttpServletRequest.getInstance(request), 
				RenderHttpServletResponse.getInstance(response));
		} catch (ServletException ex) {
			if (ex.getRootCause() != null) {
			    throw new PortletException(ex.getRootCause());
			} else {
			    throw new PortletException(ex);
			}
		}
	}
}
