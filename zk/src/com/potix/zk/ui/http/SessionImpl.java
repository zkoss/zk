/* SessionImpl.java

{{IS_NOTE
	$Id: SessionImpl.java,v 1.15 2006/05/03 03:25:07 tomyeh Exp $
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

import javax.servlet.http.HttpSession;

import com.potix.util.logging.Log;
import com.potix.el.impl.AttributesMap;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.util.Monitor;
import com.potix.zk.ui.util.Configuration;
import com.potix.zk.ui.impl.AbstractSession;

/** An implementation of {@link com.potix.zk.ui.Session}.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.15 $ $Date: 2006/05/03 03:25:07 $
 */
public class SessionImpl extends AbstractSession {
	private static final Log log = Log.lookup(SessionImpl.class);

	private final HttpSession _hsess;
	private final Map _attrs;
	private final String _clientAddr, _clientName;

	public SessionImpl(HttpSession hsess, WebApp webapp,
	String clientAddr, String clientName) {
		super(webapp);

		_hsess = hsess;
		_clientAddr = clientAddr;
		_clientName = clientName;
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
	public void onDestroyed() {
		super.onDestroyed();

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
}
