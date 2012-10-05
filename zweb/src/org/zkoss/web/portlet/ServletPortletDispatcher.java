/* ServletPortletDispatcher.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 17 00:44:46     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import java.lang.reflect.Method;

import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

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
			// B60-ZK-1337: issues with RenderHttpServletRequest/RenderHttpServletResponse Wrapper
			//   will cause ServletException: Original SevletRequest or wrapped original ServletRequest
			//       not passed to RequestDispatcher in violation of SRV.8.2 and SRV.14.2.5.1
			if (_rd instanceof PortletRequestDispatcher) {
				((PortletRequestDispatcher)_rd).include(request, response);
			} else {
				if (request instanceof HttpServletRequest &&
					response instanceof HttpServletResponse) {
					_rd.include((HttpServletRequest)request, (HttpServletResponse)response);
				} else {
					Method m = null;

					HttpServletRequest hreq = null;
					try {
						try {
							m = request.getClass().getMethod("getHttpServletRequest");
						} catch (NoSuchMethodException ex) {
							m = request.getClass().getMethod("getRequest");
						}
						hreq = (HttpServletRequest)m.invoke(request);
					} catch (Throwable ex) {
					}
					
					HttpServletResponse hres = null;
					try {
						try {
							m = response.getClass().getMethod("getHttpServletResponse");
						} catch (NoSuchMethodException ex) {
							m = response.getClass().getMethod("getResponse");
						}
						hres = (HttpServletResponse)m.invoke(response);
					} catch (Throwable ex) {
					}
					
					// To avoid casting exceptions when strict Servlet 2.5 compliance
					// is turned off on JBoss/Tomcat servers
					_rd.include(new HttpServletRequestWrapper(hreq), 
							    new HttpServletResponseWrapper(hres));
				}
			}
		} catch (ServletException ex) {
			if (ex.getRootCause() != null) {
			    throw new PortletException(ex.getRootCause());
			} else {
			    throw new PortletException(ex);
			}
		}
	}
}
