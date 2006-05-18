/* DesktopImpl.java

{{IS_NOTE
	$Id: DesktopImpl.java,v 1.2 2006/05/03 03:25:07 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Jun 22 09:50:57     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.potix.lang.D;
import com.potix.lang.Strings;
import com.potix.util.logging.Log;

import com.potix.zk.ui.WebApp;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.Sessions;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.ComponentNotFoundException;
import com.potix.zk.ui.util.Configuration;
import com.potix.zk.ui.util.Monitor;
import com.potix.zk.ui.sys.RequestQueue;
import com.potix.zk.ui.sys.DesktopCache;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.sys.DesktopCtrl;
import com.potix.zk.au.AuPrint;

/**
 * The implementation of {@link Desktop}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/05/03 03:25:07 $
 */
public class DesktopImpl implements Desktop, DesktopCtrl {
	private static final Log log = Log.lookup(DesktopImpl.class);

	private final WebApp _wapp;
	private final Session _sess;
	private final String _id;
	/** The current directory of this desktop. */
	private final String _dir;
	/** The URI to access the update engine. */
	private final String _updateURI;
	/** Map(String id, Page page). */
	private final Map _pages = new LinkedHashMap(3);
	/** Map (String uuid, Component comp). */
	private final Map _comps = new HashMap(41);
	/** A map of attributes. */
	private final Map _attrs = new HashMap();
		//don't create it dynamically because PageImp._ip bind it at constructor
	private Execution _exec;
	/** Next available ID. */
	private int _nextId = 0;
	/** The request queue. */
	private final RequestQueue _rque = new RequestQueueImpl();

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

	public void print() {
		if (_exec == null)
			throw new IllegalStateException("Not the current desktop: "+this);
		((WebAppCtrl)_wapp).getUiEngine().addResponse("print", new AuPrint());
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

	//-- Object --//
	public String toString() {
		return "[Desktop "+_id+']';
	}
}
