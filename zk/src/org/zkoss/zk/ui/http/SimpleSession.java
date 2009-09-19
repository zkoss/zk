/* SimpleSession.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 17:05:30     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.Serializable;
import java.io.Externalizable;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.portlet.PortletSession;

import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.xel.AttributesMap;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.sys.SessionsCtrl;
import org.zkoss.zk.ui.sys.SessionCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.util.Monitor;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.SessionSerializationListener;
import org.zkoss.zk.ui.util.SessionActivationListener;
import org.zkoss.zk.ui.impl.Attributes;
import org.zkoss.zk.ui.impl.ScopeListeners;

/** A non-serializable implementation of {@link org.zkoss.zk.ui.Session}.
 * 
 * <p>Note:<br/>
 * Though this class is serializable, it is meaningless to serialize
 * it directly. Reason: it doesn't serialize any session attributes.
 * Rather, it is serialized when Web server serialized HttpSession.
 * Also, notice that {@link SimpleSession} is stored as an attribute
 * HttpSession.
 *
 * @author tomyeh
 */
public class SimpleSession implements Session, SessionCtrl {
	private static final Log log = Log.lookup(SimpleSession.class);

	/** The attribute used to hold a set of serializable attributes that are written
	 * thru {@link #setAttribute}.
	 *
	 * <p>Note: once a HttpSession is serialized, all serializable attributes
	 * are serialized. However, if ZK's session is not serializable, it causes
	 * inconsistency. For example, if an application has stored a serializable
	 * attribute, say myAttr, in a session. Then, it will be surprised to see
	 * myAttr is available while the session is actually brand-new
	 *
	 * <p>It is used if java.io.Serializable is NOT implemented.
	 */
	private static final String ATTR_PRIVATE = "javax.zkoss.ui.session.private";

	/** The Web application that this session belongs to. */
	private WebApp _wapp;
	/** The HTTP or Portlet session that this session is associated with. */
	private Object _navsess;
	/** The device type. */
	private String _devType = "ajax";
	/** The attributes belonging to this session.
	 * Note: it is the same map of HttpSession's attributes.
	 * Note: No need to serialize attributes since it is done by Web server.
	 */
	private Map _attrs;
	private String _remoteAddr, _remoteHost, _serverName, _localAddr, _localName;
	private DesktopCache _cache;
	/** Next available component uuid. */
	private int _nextUuid;
	/** When the last client request is recieved.
	 */
	private long _tmLastReq = System.currentTimeMillis();
	private final ScopeListeners _scopeListeners = new ScopeListeners(this);
	private boolean _invalid;
	/** Indicates if {@link #invalidateNow} was called. */
	private boolean _invalidated;

	/** Construts a ZK session with a HTTP session.
	 *
	 * @param hsess the original HTTP session.
	 * @param request the original request causing this session to be created.
	 * If HTTP and servlet, it is javax.servlet.http.HttpServletRequest.
	 * If portlet, it is javax.portlet.RenderRequest.
	 * @since 3.0.1
	 */
	public SimpleSession(WebApp wapp, HttpSession hsess, Object request) {
		this(wapp, (Object)hsess, request);
	}
	/** Construts a ZK session with either a HTTP session or a Portlet session.
	 *
	 * <p>Note: it assumes the scope of attributes is
	 * PortletSession.APPLICATION_SCOPE.
	 *
	 * @param navsess the original session, either an instance of
	 * HttpSession or PortletSession.
	 * Notice: we don't declare PortletSession in API
	 * to avoid this class failed to be loaded in some system (without
	 * portlet-api.jar)
	 * @param request the original request causing this session to be created.
	 * If portlet, it is javax.portlet.RenderRequest.
	 * @since 3.6.2
	 */
	public SimpleSession(WebApp wapp, Object navsess, Object request) {
		if (wapp == null || navsess == null)
			throw new IllegalArgumentException();

		SessionsCtrl.updateCount(true);
		_wapp = wapp;
		_navsess = navsess;

		cleanSessAttrs(); //after _navsess is initialized

		if (request instanceof ServletRequest) {
			final ServletRequest req = (ServletRequest)request;
			_remoteAddr = req.getRemoteAddr();
			_remoteHost = req.getRemoteHost();
			_serverName = req.getServerName();
			if (Servlets.isServlet24()) {
				_localAddr = req.getLocalAddr();
				_localName = req.getLocalName();
			} else {
				_localAddr = _localName = "";
			}
		}

		init();

		final Configuration config = getWebApp().getConfiguration();
		config.invokeSessionInits(this, request); //it might throw exception

		final Monitor monitor = config.getMonitor();
		if (monitor != null) {
			try {
				monitor.sessionCreated(this);
			} catch (Throwable ex) {
				log.error(ex);
			}
		}
	}
	/** Called to initialize some members after this object is deserialized.
	 * <p>In other words, it is called by the deriving class if it implements
	 * java.io.Serializable.
	 */
	private final void init() {
		_attrs = new AttributesMap() {
			protected Enumeration getKeys() {
				return getAttrNames();
			}
			protected Object getValue(String key) {
				return getAttribute(key);
			}
			protected void setValue(String key, Object val) {
				setAttribute(key, val);
			}
			protected void removeValue(String key) {
				removeAttribute(key);
			}
		};
	}
	/** Cleans up the attribute being set.
	 */
	private final void cleanSessAttrs() {
		final Object names = getAttribute(ATTR_PRIVATE);
		if (names instanceof Set) { //Bug 1954655: bakward-compatible
			for (Iterator it = ((Set)names).iterator(); it.hasNext();)
				rmAttr((String)it.next());
		}
		rmAttr(ATTR_PRIVATE);
	}
	private final Enumeration getAttrNames() {
		return _navsess instanceof HttpSession ?
			((HttpSession)_navsess).getAttributeNames():
			_navsess != null ?
				((PortletSession)_navsess)
					.getAttributeNames(PortletSession.APPLICATION_SCOPE):
				CollectionsX.EMPTY_ENUMERATION;
	}
	public String getDeviceType() {
		return _devType;
	}
	public void setDeviceType(String deviceType) {
		if (deviceType == null || deviceType.length() == 0)
			throw new IllegalArgumentException();

		//Note: we don't check whether any conflict (e.g., two desktops
		//have diff device types).
		//The existence and others are checked by DesktopImpl
		//and this method is called when Desktop.setDeviceType is called
		_devType = deviceType;
	}

	public Object getAttribute(String name) {
		return _navsess instanceof HttpSession ?
				((HttpSession)_navsess).getAttribute(name):
			_navsess != null ?
				((PortletSession)_navsess)
					.getAttribute(name, PortletSession.APPLICATION_SCOPE):
				null;
	}
	public boolean hasAttribute(String name) {
		return getAttribute(name) != null; //Servlet limitation
	}
	public Object setAttribute(String name, Object value) {
		final Object old = getAttribute(name);
		if (!(this instanceof Serializable || this instanceof Externalizable)) {
			final boolean bStore = value instanceof Serializable || value instanceof Externalizable;
			synchronized (this) {
				setAttr(name, value);

				Object prv = getAttribute(ATTR_PRIVATE);
				if (bStore) {
					if (!(prv instanceof Set))
						setAttr(ATTR_PRIVATE, prv = new HashSet());
					((Set)prv).add(name);
				} else {
					if (prv instanceof Set)
						((Set)prv).remove(name);
				}
			}
		} else {
			setAttr(name, value);
		}
		return old;
	}
	public Object getAttribute(String name, boolean recurse) {
		Object val = getAttribute(name);
		return val != null || !recurse || _wapp == null ?
			val: _wapp.getAttribute(name, true);
	}
	public boolean hasAttribute(String name, boolean recurse) {
		return hasAttribute(name)
		|| (recurse && _wapp != null && _wapp.hasAttribute(name, true));
	}
	public Object setAttribute(String name, Object value, boolean recurse) {
		if (recurse && !hasAttribute(name)) {
			if (_wapp != null) {
				if (_wapp.hasAttribute(name, true))
					return _wapp.setAttribute(name, value, true);
			}
		}
		return setAttribute(name, value);
	}
	public Object removeAttribute(String name, boolean recurse) {
		if (recurse && !hasAttribute(name)) {
			if (_wapp != null) {
				if (_wapp.hasAttribute(name, true))
					return _wapp.removeAttribute(name, true);
			}
			return null;
		}
		return removeAttribute(name);
	}

	public boolean addScopeListener(ScopeListener listener) {
		return _scopeListeners.addScopeListener(listener);
	}
	public boolean removeScopeListener(ScopeListener listener) {
		return _scopeListeners.removeScopeListener(listener);
	}
	/** Returns all scope listeners.
	 */
	/*package*/ ScopeListeners getScopeListeners() {
		return _scopeListeners;
	}

	private void setAttr(String name, Object value) {
		if (_navsess instanceof HttpSession)
			((HttpSession)_navsess).setAttribute(name, value);
		else if (_navsess != null)
			((PortletSession)_navsess).setAttribute(name, value, PortletSession.APPLICATION_SCOPE);
	}
	public Object removeAttribute(String name) {
		Object old = getAttribute(name);
		if (!(this instanceof Serializable || this instanceof Externalizable)) {
			synchronized (this) {
				rmAttr(name);

				Object prv = getAttribute(ATTR_PRIVATE);
				if (prv instanceof Set)
					((Set)prv).remove(name);
			}
		} else {
			rmAttr(name);
		}
		return old;
	}
	private void rmAttr(String name) {
		if (_navsess instanceof HttpSession)
			((HttpSession)_navsess).removeAttribute(name);
		else if (_navsess != null)
			((PortletSession)_navsess).removeAttribute(name, PortletSession.APPLICATION_SCOPE);
	}
	public Map getAttributes() {
		return _attrs;
	}

	public String getRemoteAddr() {
		return _remoteAddr;
	}
	public String getRemoteHost() {
		return _remoteHost;
	}
	public String getServerName() {
		return _serverName;
	}
	public String getLocalName() {
		return _localName;
	}
	public String getLocalAddr() {
		return _localAddr;
	}

	public void invalidateNow() {
		_invalid = true;

		if (!_invalidated) {
			_invalidated = true; //to avoid called twice

			rmAttr(Attributes.RENEW_NATIVE_SESSION); //See HttpSessionListener
			if (_navsess instanceof HttpSession)
				((HttpSession)_navsess).invalidate();
			else
				((PortletSession)_navsess).invalidate();
		}
	}
	public void setMaxInactiveInterval(int interval) {
		if (_navsess instanceof HttpSession)
			((HttpSession)_navsess).setMaxInactiveInterval(interval);
		else if (_navsess != null)
			((PortletSession)_navsess).setMaxInactiveInterval(interval);
	}
	public int getMaxInactiveInterval() {
		return _navsess instanceof HttpSession ?
				((HttpSession)_navsess).getMaxInactiveInterval():
			_navsess != null ? 
				((PortletSession)_navsess).getMaxInactiveInterval():
				-1;
	}
	public Object getNativeSession() {
		return _navsess;
	}

	public void notifyClientRequest(boolean keepAlive) {
		final long now = System.currentTimeMillis();
		if (keepAlive) {
			_tmLastReq = now;
		} else {
			final int tmout = getMaxInactiveInterval();
			if (tmout >= 0 && (now - _tmLastReq) / 1000 > tmout)
				invalidate();
		}
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

	public DesktopCache getDesktopCache() {
		return _cache;
	}
	public void setDesktopCache(DesktopCache cache) {
		_cache = cache;
	}
	public void recover(Object nativeSession) {
		if (_invalidated)
			log.info("Recover an invalidated session, "+this);

		_invalidated = _invalid = false;
		if (_navsess == null)
			sessionDidActivate((HttpSession)nativeSession);
		else
			_navsess = nativeSession;
			//Session Fixation Protection might change native session
			//Not sure it is clever to call sessionDidActivate
			//To be safe, we recover _navsess only
	}

	public void onDestroyed() {
		SessionsCtrl.updateCount(false);

		_invalidated = _invalid = true;

		final Configuration config = getWebApp().getConfiguration();
		config.invokeSessionCleanups(this);

		cleanSessAttrs();

		final Monitor monitor = config.getMonitor();
		if (monitor != null) {
			try {
				monitor.sessionDestroyed(this);
			} catch (Throwable ex) {
				log.error(ex);
			}
		}

		_navsess = null; //clean up
	}

	//--Serializable for deriving classes--//
	/** Used by the deriving class,
	 * only if the deriving class implements java.io.Serializable.
	 */
	protected SimpleSession() {}
	/** Used by the deriving class to write this object,
	 * only if the deriving class implements java.io.Serializable.
	 */
	protected void writeThis(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.writeObject(_remoteAddr);
		s.writeObject(_remoteHost);
		s.writeObject(_serverName);
		s.writeObject(_localAddr);
		s.writeObject(_localName);

		s.writeObject(_cache);
		s.writeInt(_nextUuid);

		//Since HttpSession will serialize attributes by the container
		//we ony invoke the notification
		for (Enumeration en = getAttrNames(); en.hasMoreElements();) {
			final String nm = (String)en.nextElement();
			willSerialize(getAttribute(nm));
		}
	}
	private void willSerialize(Object o) {
		if (o instanceof SessionSerializationListener)
			((SessionSerializationListener)o).willSerialize(this);
	}
	/** Used by the deriving class to read back this object,
	 * only if the deriving class implements java.io.Serializable.
	 */
	protected void readThis(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		init();

		_remoteAddr = (String)s.readObject();
		_remoteHost = (String)s.readObject();
		_serverName = (String)s.readObject();
		_localAddr = (String)s.readObject();
		_localName = (String)s.readObject();

		_cache = (DesktopCache)s.readObject();
		_nextUuid = s.readInt();
	}

	/** Used by the deriving class to pre-process a session before writing
	 * the session
	 */
	protected void sessionWillPassivate() {
		((WebAppCtrl)_wapp).sessionWillPassivate(this);

		for (Enumeration en = getAttrNames(); en.hasMoreElements();) {
			final String nm = (String)en.nextElement();
			willPassivate(getAttribute(nm));
		}
	}
	/** Used by the deriving class to post-process a session after
	 * it is read back.
	 *
	 * <p>Application shall not call this method directly.
	 */
	protected void sessionDidActivate(HttpSession hsess) {
		//Note: in Tomcat, servlet is activated later, so we have to
		//add listener to WebManager instead of process now

		_navsess = hsess;
		WebManager.addActivationListener(
			hsess.getServletContext(),
				//FUTURE: getServletContext only in Servlet 2.3 or later
			new WebManagerActivationListener() {
				public void didActivate(WebManager webman) {
					_wapp = webman.getWebApp();
					((WebAppCtrl)_wapp)
						.sessionDidActivate(SimpleSession.this);
				}
			});

		for (Enumeration en = getAttrNames(); en.hasMoreElements();) {
			final String nm = (String)en.nextElement();
			didActivate(getAttribute(nm));
		}
	}
	private void willPassivate(Object o) {
		if (o instanceof SessionActivationListener) {
			try {
				((SessionActivationListener)o).willPassivate(this);
			} catch (AbstractMethodError ex) { //backward compatible
			}
		}
	}
	private void didActivate(Object o) {
		if (o instanceof SessionActivationListener) {
			try {
				((SessionActivationListener)o).didActivate(this);
			} catch (AbstractMethodError ex) { //backward compatible
			}
		}
	}
}
