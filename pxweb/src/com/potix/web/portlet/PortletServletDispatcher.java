/* PortletServletDispatcher.java

{{IS_NOTE
	$Id: PortletServletDispatcher.java,v 1.2 2006/02/27 03:54:27 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Jan 17 00:44:46     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.portlet;

import java.io.IOException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

/**
 * A facade of a RequestDispatch for implementing PortletRequestDispatcher.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:54:27 $
 */
public class PortletServletDispatcher implements PortletRequestDispatcher {
	private final RequestDispatcher _rd;

	/** Returns a facade instance for the specified dispatcher. */
	public static final
	PortletRequestDispatcher getInstance(RequestDispatcher rd) {
		if (rd instanceof PortletRequestDispatcher)
			return (PortletRequestDispatcher)rd;
		return new PortletServletDispatcher(rd);
	}
	private PortletServletDispatcher(RequestDispatcher rd) {
		if (rd == null)
			throw new IllegalArgumentException("null");
		_rd = rd;
	}

	public void include(RenderRequest request, RenderResponse response)
	throws PortletException, IOException {
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
