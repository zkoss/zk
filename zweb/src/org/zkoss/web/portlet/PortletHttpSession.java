/* PortletHttpSession.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jan 17 10:46:06     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import javax.servlet.http.HttpSession;
import javax.portlet.PortletSession;

/**
 * A facade of PortletSession for implementing HttpSession.
 *
 * <p>Note: all attributes set and get thru this class are in
 * the applicatiop scope (PortletSession.APPLICATION_SCOPE).
 *
 * @author tomyeh
 */
public class PortletHttpSession implements HttpSession {
	private final PortletSession _sess;
	public static HttpSession getInstance(PortletSession sess) {
		if (sess instanceof HttpSession)
			return (HttpSession)sess;
		return new PortletHttpSession(sess);
	}
	private PortletHttpSession(PortletSession sess) {
		if (sess == null)
			throw new IllegalArgumentException("null");
		_sess = sess;
	}

	/** Returns the portlet session being wrapped by this object.
	 * @since 3.0.5
	 */
	public PortletSession getPortletSess() {
		return _sess;
	}

	//-- HttpSession --//
	public Object getAttribute(String name) {
		return _sess.getAttribute(name, PortletSession.APPLICATION_SCOPE);
	}
	public java.util.Enumeration getAttributeNames() {
		return _sess.getAttributeNames(PortletSession.APPLICATION_SCOPE);
	}
	public long getCreationTime() {
		return _sess.getCreationTime();
	}
	public String getId() {
		return _sess.getId();
	}
	public long getLastAccessedTime() {
		return _sess.getLastAccessedTime();
	}
	public int getMaxInactiveInterval() {
		return _sess.getMaxInactiveInterval();
	}
	public javax.servlet.ServletContext getServletContext() {
		return PortletServletContext.getInstance(_sess.getPortletContext());
	}
	/**
	 * @deprecated
	 */
	public javax.servlet.http.HttpSessionContext getSessionContext() {
		return null;
	}
	/**
	 * @deprecated
	 */
	public Object getValue(String name) {
		return null;
	}
	/**
	 * @deprecated
	 */
	public String[] getValueNames() {
		return null;
	}
	public void invalidate() {
		_sess.invalidate();
	}
	public boolean isNew() {
		return _sess.isNew();
	}
	/**
	 * @deprecated
	 */
	public void putValue(String name, Object value) {
	}
	public void removeAttribute(String name) {
		_sess.removeAttribute(name, PortletSession.APPLICATION_SCOPE);
	}
	/**
	 * @deprecated
	 */
	public void removeValue(String name) {
	}
	public void setAttribute(String name, Object value) {
		_sess.setAttribute(name, value, PortletSession.APPLICATION_SCOPE);
	}
	public void setMaxInactiveInterval(int interval) {
		_sess.setMaxInactiveInterval(interval);
	}

	//Object//
	public int hashCode() {
		return _sess.hashCode();
	}
	public boolean equals(Object o) {
		PortletSession val =
			o instanceof PortletSession ? (PortletSession)o:
			o instanceof PortletHttpSession ? ((PortletHttpSession)o)._sess: null;
		return val != null && val.equals(_sess);
	}
}
