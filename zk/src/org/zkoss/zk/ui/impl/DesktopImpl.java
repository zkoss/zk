/* DesktopImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jun 22 09:50:57     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.zkoss.lang.D;
import org.zkoss.lang.Strings;
import org.zkoss.util.logging.Log;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.util.Configuration;
import org.zkoss.zk.ui.util.Monitor;
import org.zkoss.zk.ui.ext.render.DynamicMedia;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.RequestQueue;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.au.AuBookmark;

/**
 * The implementation of {@link Desktop}.
 *
 * <p>Note: though {@link DesktopImpl} is serializable, it is designed
 * to work with Web container to enable the serialization of sessions.
 * It is not suggested to serialize and desrialize it directly since
 * many fields might be lost.
 *
 * <p>On the other hand, it is OK to serialize and deserialize
 * {@link Component}.
 *
 * @author tomyeh
 */
public class DesktopImpl implements Desktop, DesktopCtrl, java.io.Serializable {
	private static final Log log = Log.lookup(DesktopImpl.class);
    private static final long serialVersionUID = 20060622L;

	private transient WebApp _wapp;
	private transient Session _sess;
	private final String _id;
	/** The current directory of this desktop. */
	private final String _dir;
	/** The URI to access the update engine. */
	private final String _updateURI;
	/** Map(String id, Page page). */
	private final Map _pages = new LinkedHashMap(3);
	/** Map (String uuid, Component comp). */
	private transient Map _comps;
	/** A map of attributes. */
	private transient final Map _attrs = new HashMap();
		//don't create it dynamically because PageImp._ip bind it at constructor
	private transient Execution _exec;
	/** Next available ID. */
	private int _nextId = 0;
	/** The request queue. */
	private transient RequestQueue _rque;
	private String _bookmark = "";

	/**
	 * @param updateURI the URI to access the update engine (no expression allowed).
	 * Note: it is NOT encoded yet.
	 * @param dir the current directory.
	 * It is used if a relative URI is specified.
	 * If null or empty is specified, it means no current directory.
	 */
	public DesktopImpl(WebApp wapp, String updateURI, String dir) {
		if (updateURI == null || wapp == null)
			throw new IllegalArgumentException("null");

		_wapp = wapp;
		_updateURI = updateURI;

		if (dir == null) {
			dir = "";
		} else {
			final int len = dir.length() - 1;
			if (len >= 0 && dir.charAt(len) != '/')
				dir += '/';
		}
		_dir = dir;

		init();

		_sess = Sessions.getCurrent(); //must be the current session
		final DesktopCache dc = ((WebAppCtrl)wapp).getDesktopCache(_sess);
		_id = Strings.encode(
			new StringBuffer(12).append("g"), dc.getNextId()).toString();

		final Configuration config = wapp.getConfiguration();
		config.invokeDesktopInits(this); //it might throw exception

		dc.addDesktop(this); //add to cache after invokeDesktopInits

		final Monitor monitor = config.getMonitor();
		if (monitor != null) {
			try {
				monitor.desktopCreated(this);
			} catch (Throwable ex) {
				log.error(ex);
			}
		}
	}
	/** Initialization for contructor and de-serialized. */
	private void init() {
		_rque = new RequestQueueImpl();
		_comps = new HashMap(41);
	}
	public String getId() {
		return _id;
	}

	//-- Desktop --//
	public Execution getExecution() {
		return _exec;
	}
	public final Session getSession() {
		return _sess;
	}
	public String getUpdateURI(String pathInfo) {
		final String uri;
		if (pathInfo == null || pathInfo.length() == 0) {
			uri = _updateURI;
		} else {
			if (pathInfo.charAt(0) != '/')
				pathInfo = '/' + pathInfo;
			uri = _updateURI + pathInfo;
		}
		return _exec.encodeURL(uri);
	}
	public String getDynamicMediaURI(Component comp, String pathInfo) {
		if (!(((ComponentCtrl)comp).getExtraCtrl() instanceof DynamicMedia))
			throw new UiException(DynamicMedia.class+" not implemented by getExtraCtrl() of "+comp);

		final StringBuffer sb = new StringBuffer(32)
			.append("/view/").append(getId())
			.append('/').append(comp.getUuid());

		if (pathInfo != null && pathInfo.length() > 0) {
			if (!pathInfo.startsWith("/")) sb.append('/');
			sb.append(pathInfo);
		}
		return getUpdateURI(sb.toString());
	}

	public Page getPage(String pageId) {
		//We allow user to access this method concurrently, so synchronized
		//is required
		final Page page;
		synchronized (_pages) {
			page = (Page)_pages.get(pageId);
		}
		if (page == null)
			throw new ComponentNotFoundException("Page not found: "+pageId);
		return page;
	}
	public boolean hasPage(String pageId) {
		return _pages.containsKey(pageId);
	}
	public Collection getPages() {
		//No synchronized is required because it cannot be access concurrently
		return _pages.values();
	}

	public String getBookmark() {
		return _bookmark;
	}
	public void setBookmark(String name) {
		if (_exec == null)
			throw new IllegalStateException("Not the current desktop: "+this);
		if (name.indexOf('#') >= 0 || name.indexOf('?') >= 0)
			throw new IllegalArgumentException("Illegal character: # ?");
		_bookmark = name;
		((WebAppCtrl)_wapp).getUiEngine()
			.addResponse("bookmark", new AuBookmark(name));
	}

	public Collection getComponents() {
		return _comps.values();
	}
	public Component getComponentByUuid(String uuid) {
		final Component comp = (Component)_comps.get(uuid);
		if (comp == null)
			throw new ComponentNotFoundException("Component not found: "+uuid);
		return comp;
	}
	public Component getComponentByUuidIfAny(String uuid) {
		return (Component)_comps.get(uuid);
	}
	public void addComponent(Component comp) {
		final Object old = _comps.put(comp.getUuid(), comp);
		if (old != comp && old != null) {
			_comps.put(((Component)old).getUuid(), old); //recover
			throw new InternalError("Caller shall prevent it: Register a component twice: "+comp);
		}
	}
	public void removeComponent(Component comp) {
		_comps.remove(comp.getUuid());
	}

	public Map getAttributes() {
		return _attrs;
	}
	public Object getAttribute(String name) {
		return _attrs.get(name);
	}
	public Object setAttribute(String name, Object value) {
		return value != null ? _attrs.put(name, value): removeAttribute(name);
	}
	public Object removeAttribute(String name) {
		return _attrs.remove(name);
	}

	public WebApp getWebApp() {
		return _wapp;
	}

	public String getCurrentDirectory() {
		return _dir;
	}

	//-- DesktopCtrl --//
	public RequestQueue getRequestQueue() {
		return _rque;
	}
	public void setExecution(Execution exec) {
		_exec = exec;
	}

	public int getNextId() {
		return _nextId++;
	}
	public void addPage(Page page) {
		//We have to synchronize it due to getPage allows concurrent access
		synchronized (_pages) {
			final Object old = _pages.put(page.getId(), page);
			if (old != null) {
				_pages.put(((Page)old).getId(), old); //recover
				log.warning(
					page == old ? "Register a page twice: "+page:
						"Replicated ID: "+page+"; already used by "+old);
			}
			if (D.ON && log.debugable()) log.debug("After added, pages: "+_pages);
		}
	}
	public void removePage(Page page) {
		synchronized (_pages) {
			if (_pages.remove(page.getId()) == null) {
				log.warning("Removing non-exist page: "+page+"\nCurrent pages: "+_pages.values());
				return;
			}
			if (D.ON && log.debugable()) log.debug("After removed, pages: "+_pages.values());
		}
		removeComponents(page.getRoots());
	}
	private void removeComponents(Collection comps) {
		for (Iterator it = comps.iterator(); it.hasNext();) {
			final Component comp = (Component)it.next();
			removeComponents(comp.getChildren()); //recursive
			removeComponent(comp);
		}
	}

	public void setBookmarkByClient(String name) {
		_bookmark = name != null ? name: "";
	}

	//-- Object --//
	public String toString() {
		return "[Desktop "+_id+']';
	}

	public void sessionWillPassivate(Session sess) {
		for (Iterator it = _pages.values().iterator(); it.hasNext();)
			((PageCtrl)it.next()).sessionWillPassivate(this);
	}
	public void sessionDidActivate(Session sess) {
		_sess = sess;
		_wapp = sess.getWebApp();

		for (Iterator it = _pages.values().iterator(); it.hasNext();)
			((PageCtrl)it.next()).sessionDidActivate(this);
	}

	//-- Serializable --//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		Serializables.smartWrite(s, _attrs);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();

		//get back _comps from _pages
		for (Iterator it = _pages.values().iterator(); it.hasNext();)
			for (Iterator e = ((Page)it.next()).getRoots().iterator();
			e.hasNext();)
				addAllComponents((Component)e.next());

		Serializables.smartRead(s, _attrs);
	}
	private void addAllComponents(Component comp) {
		addComponent(comp);
		for (Iterator it = comp.getChildren().iterator(); it.hasNext();)
			addAllComponents((Component)it.next());
	}
}
