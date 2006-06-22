/* SessionImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 17:05:30     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.http;


import java.util.Map;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

import com.potix.util.logging.Log;
import com.potix.el.impl.AttributesMap;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.sys.SessionCtrl;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.util.Monitor;
import com.potix.zk.ui.util.Configuration;

/** An implementation of {@link com.potix.zk.ui.Session}.
 * 
 * <p>Note:<br/>
 * Though this class is serializable, it is meaningless to serialize
 * it directly. Reason: it doesn't serialize any session attributes.
 * Rather, it is serialized when Web server serialized HttpSession.
 * Also, notice that {@link SessionImpl} is stored as an attribute
 * HttpSession.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class SessionImpl implements Session, SessionCtrl,
HttpSessionActivationListener/*not ready yet, java.io.Serializable*/ {
	private static final Log log = Log.lookup(SessionImpl.class);
    private static final long serialVersionUID = 20060622L;

	transient private WebApp _wapp;
	transient private HttpSession _hsess;
		//Not to serialize since it is recalled by sessionDidActivate
	transient private Map _attrs;
		//No need to serialize attributes since it is done by Session
		//Side effect: it is meaning to serialize a session manually
		//Rather, caller has to serialize HttpSession
	private String _clientAddr, _clientName;
	private boolean _invalid;

	public SessionImpl(HttpSession hsess, WebApp wapp,
	String clientAddr, String clientName) {
		if (wapp == null || hsess == null)
			throw new IllegalArgumentException();

		_wapp = wapp;
		_hsess = hsess;
		_clientAddr = clientAddr;
		_clientName = clientName;
		init();

		final Configuration config = getWebApp().getConfiguration();
		config.invokeSessionInits(this); //it might throw exception

		final Monitor monitor = config.getMonitor();
		if (monitor != null) {
			try {
				monitor.sessionCreated(this);
			} catch (Throwable ex) {
				log.error(ex);
			}
		}
	}
	private void init() {
		_attrs = new AttributesMap() {
			protected Enumeration getKeys() {
				return _hsess.getAttributeNames();
			}
			protected Object getValue(String key) {
				return _hsess.getAttribute(key);
			}
			protected void setValue(String key, Object val) {
				_hsess.setAttribute(key, val);
			}
			protected void removeValue(String key) {
				_hsess.removeAttribute(key);
			}
		};
	}

	public Object getAttribute(String name) {
		return _hsess.getAttribute(name);
	}
	public void setAttribute(String name, Object value) {
		_hsess.setAttribute(name, value);
	}
	public void removeAttribute(String name) {
		_hsess.removeAttribute(name);
	}
	public Map getAttributes() {
		return _attrs;
	}

	public String getClientAddr() {
		return _clientAddr;
	}
	public String getClientName() {
		return _clientName;
	}

	public void invalidateNow() {
		_hsess.invalidate();
	}
	public void setMaxInactiveInterval(int interval) {
		_hsess.setMaxInactiveInterval(interval);
	}
	public Object getNativeSession() {
		return _hsess;
	}

	public final WebApp getWebApp() {
		return _wapp;
	}

	public final void invalidate() {
		_invalid = true;
	}
	public final boolean isInvalidated() {
		return _invalid;
	}

	public void onDestroyed() {
		final Configuration config = getWebApp().getConfiguration();
		config.invokeSessionCleanups(this);

		final Monitor monitor = config.getMonitor();
		if (monitor != null) {
			try {
				monitor.sessionDestroyed(this);
			} catch (Throwable ex) {
				log.error(ex);
			}
		}
	}

	/** Notification that the session is about to be passivated
	 * (aka., serialized).
	 */
	protected void sessionWillPassivate() {
	}
	/** Notification that the session has just been activated
	 * (aka., deserialized).
	 */
	protected void sessionDidActivate(WebApp wapp) {
	}

	//-- HttpSessionActivationListener --//
	public void sessionWillPassivate(HttpSessionEvent se) {
		((WebAppCtrl)_wapp).sessionWillPassivate(this);
	}
	public void sessionDidActivate(HttpSessionEvent se) {
		_hsess = se.getSession();
		final ServletContext ctx = _hsess.getServletContext();
		final WebManager webman = WebManager.getWebManager(ctx);
		if (webman == null)
			throw new UiException("Unable to activate "+_hsess+" for "+ctx);
		_wapp = webman.getWebApp();
		((WebAppCtrl)_wapp).sessionDidActivate(this);
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();
	}
}
