/* PageImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 18:17:32     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.impl;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.io.Writer;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.servlet.jsp.el.FunctionMapper;

import com.potix.lang.D;
import com.potix.lang.Objects;
import com.potix.lang.Strings;
import com.potix.lang.Exceptions;
import com.potix.lang.Expectable;
import com.potix.util.CollectionsX;
import com.potix.util.logging.Log;
import com.potix.web.servlet.StyleSheet;

import com.potix.zk.mesg.MZk;
import com.potix.zk.ui.Desktop;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Session;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.Execution;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.ComponentNotFoundException;
import com.potix.zk.ui.event.EventListener;
import com.potix.zk.ui.event.Events;
import com.potix.zk.ui.metainfo.PageDefinition;
import com.potix.zk.ui.metainfo.LanguageDefinition;
import com.potix.zk.ui.util.Interpreter;
import com.potix.zk.ui.util.Namespace;
import com.potix.zk.ui.util.VariableResolver;
import com.potix.zk.ui.sys.ExecutionCtrl;
import com.potix.zk.ui.sys.WebAppCtrl;
import com.potix.zk.ui.sys.DesktopCtrl;
import com.potix.zk.ui.sys.PageCtrl;
import com.potix.zk.ui.sys.ComponentCtrl;
import com.potix.zk.ui.sys.ComponentsCtrl;
import com.potix.zk.ui.sys.Variables;
import com.potix.zk.ui.sys.UiEngine;
import com.potix.zk.ui.impl.bsh.BshInterpreter;
import com.potix.zk.au.AuSetTitle;

/**
 * An implmentation of {@link Page} and {@link PageCtrl}.
 * Refer to them for more details.
 *
 * <p>Note: though {@link DesktopImpl} is serializable but it is designed
 * to work with Web container's session serialization.
 * It is not suggested to serialize and desrialize it directly since
 * many fields might be lost.
 *
 * <p>On the other hand, it is OK to serialize and deserialize
 * {@link Component}.
 *
 * <p>Implementation Notes:<br>
 * It is not thread-safe because it is protected by the spec:
 * at most one thread can access a page and all its components at the same time.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class PageImpl implements Page, PageCtrl, Serializable {
	private static final Log log = Log.lookup(PageImpl.class);
	private static final Log _zklog = Log.lookup("com.potix.zk.log");

	/** URI for redrawing as a desktop or part of another desktop. */
	private final String _dkUri, _pgUri;
	/** The component that includes this page, or null if not included. */
	private transient Component _owner;
	private transient Desktop _desktop;
	private String _id;
	private final Interpreter _ip;
	private String _title = "", _style = "";
	/** A list of root components. */
	private final List _roots = new LinkedList();
	private transient List _roRoots;
	/** A map of fellows. */
	private transient Map _fellows = new HashMap();
	/** A map of attributes. */
	private transient Map _attrs = new HashMap();
		//don't create it dynamically because _ip bind it at constructor
	/** A map of event listener: Map(evtnm, List(EventListener)). */
	private transient Map _listeners;
	/** The default parent. */
	private transient Component _defparent;
	private FunctionMapper _funmap;
	private List _stylesheets;

	/** Constructs a page.
	 *
	 * <p>Note: when a page is constructed, it doesn't belong to a desktop
	 * yet. Caller has to invoke {@link #init} to complete
	 * the creation of a page.
	 * Why two phase? Contructor could be called before execution
	 * is activated, but {@link #init} must be called in an execution.
	 *
	 * <p>Also note that {@link #getId} and {@link #getTitle}
	 * are not ready until {@link #init} is called.
	 */
	public PageImpl(PageDefinition pgdef) {
		_ip = new BshInterpreter();
		_funmap = pgdef.getFunctionMapper();

		final List ss = pgdef.getStyleSheets();
		_stylesheets = ss != null ? ss: Collections.EMPTY_LIST;

		final LanguageDefinition langdef = pgdef.getLanguageDefinition();
		_dkUri = langdef.getDesktopURI();
		_pgUri = langdef.getPageURI();

		init();
	}
	/** Initialized the page when contructed or deserialized.
	 */
	protected void init() {
		_roRoots = Collections.unmodifiableList(_roots);
	}

	/** Returns the UI engine.
	 */
	private final UiEngine getUiEngine() {
		return ((WebAppCtrl)_desktop.getWebApp()).getUiEngine();
	}

	//-- Page --//
	private final Execution getExecution() {
		return _desktop != null ? _desktop.getExecution():
			Executions.getCurrent();
	}
	public final FunctionMapper getFunctionMapper() {
		return _funmap;
	}
	public final List getStyleSheets() {
		return _stylesheets;
	}
	public void addFunctionMapper(FunctionMapper funmap) {
		if (funmap != null)
			if (_funmap != null)
				_funmap = new DualFuncMapper(funmap, _funmap);
			else
				_funmap = funmap;
	}
	public void addStyleSheet(StyleSheet ss) {
		if (ss != null) {
			if (!(_stylesheets instanceof LinkedList))
				_stylesheets = new LinkedList(_stylesheets);
			_stylesheets.add(ss);
			//TODO: update the client dynamically (thru Visualizer)
		}
	}

	public final String getId() {
		return _id;
	}
	public String getTitle() {
		return _title;
	}
	public void setTitle(String title) {
		if (title == null) title = "";
		if (!Objects.equals(_title, title)) {
			_title = title;
			if (_desktop != null) {
				evalTitle();
				getUiEngine().addResponse("setTitle", new AuSetTitle(_title));
			}
		}
	}
	private void evalTitle() {
		if (_title.length() > 0) {
			_title = (String)
				getExecution().evaluate(this, _title, String.class);
			if (_title == null) _title = "";
		}
	}
	private void evalStyle() {
		if (_style.length() > 0) {
			_style = (String)
				getExecution().evaluate(this, _style, String.class);
			if (_style == null) _style = "";
		}
	}

	public Collection getRoots() {
		return _roRoots;
	}

	public Map getAttributes(int scope) {
		switch (scope) {
		case DESKTOP_SCOPE:
			return _desktop != null ?
				_desktop.getAttributes(): Collections.EMPTY_MAP;
		case SESSION_SCOPE:
			return _desktop != null ?
				_desktop.getSession().getAttributes(): Collections.EMPTY_MAP;
		case APPLICATION_SCOPE:
			return _desktop != null ?
				_desktop.getWebApp().getAttributes(): Collections.EMPTY_MAP;
		case PAGE_SCOPE:
			return _attrs;
		default:
			return Collections.EMPTY_MAP;
		}
	}
	public Object getAttribute(String name, int scope) {
		return getAttributes(scope).get(name);
	}
	public Object setAttribute(String name, Object value, int scope) {
		if (value != null) {
			final Map attrs = getAttributes(scope);
			if (attrs == Collections.EMPTY_MAP)
				throw new IllegalStateException("This component doesn't belong to any ID space: "+this);
			return attrs.put(name, value);
		} else {
			return removeAttribute(name, scope);
		}
	}
	public Object removeAttribute(String name, int scope) {
			final Map attrs = getAttributes(scope);
			if (attrs == Collections.EMPTY_MAP)
				throw new IllegalStateException("This component doesn't belong to any ID space: "+this);
		return attrs.remove(name);
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

	public void invalidate() {
		getUiEngine().addInvalidate(this);
	}
	public void removeComponents() {
		for (Iterator it = new ArrayList(getRoots()).iterator();
		it.hasNext();)
			((Component)it.next()).detach();
	}

	public void setVariable(String name, Object val) {
		_ip.setVariable(name, val);
	}
	public Object getVariable(String name) {
		return _ip.getVariable(name);
	}
	public void unsetVariable(String name) {
		_ip.unsetVariable(name);
	}

	public boolean addVariableResolver(VariableResolver resolver) {
		return _ip.addVariableResolver(resolver);
	}
	public boolean removeVariableResolver(VariableResolver resolver) {
		return _ip.removeVariableResolver(resolver);
	}

	public Object resolveElVariable(String name) {
		try {
			final javax.servlet.jsp.el.VariableResolver resolv =
				getExecution().getVariableResolver();
			return resolv != null ? resolv.resolveVariable(name): null;
		} catch (javax.servlet.jsp.el.ELException ex) {
			throw UiException.Aide.wrap(ex);
		}
	}

	public boolean addEventListener(String evtnm, EventListener listener) {
		if (evtnm == null || listener == null)
			throw new IllegalArgumentException("null");
		if (!Events.isValid(evtnm))
			throw new IllegalArgumentException("Invalid event name: "+evtnm);
		if (listener.isAsap())
			log.warning("Ignored: ASAP is meaningless if an event listener added to a page: "+listener);

		if (_listeners == null)
			_listeners = new HashMap(3);

		List l = (List)_listeners.get(evtnm);
		if (l != null) {
			for (Iterator it = l.iterator(); it.hasNext();) {
				final EventListener li = (EventListener)it.next();
				if (listener.equals(li))
					return false;
			}
		} else {
			_listeners.put(evtnm, l = new LinkedList());
		}
		l.add(listener);
		return true;
	}
	public boolean removeEventListener(String evtnm, EventListener listener) {
		if (evtnm == null || listener == null)
			throw new NullPointerException();

		if (_listeners != null) {
			final List l = (List)_listeners.get(evtnm);
			if (l != null) {
				for (Iterator it = l.iterator(); it.hasNext();) {
					final EventListener li = (EventListener)it.next();
					if (listener.equals(li)) {
						if (l.size() == 1)
							_listeners.remove(evtnm);
						else
							it.remove();
						return true;
					}
				}
			}
		}
		return false;
	}

	public final Component getFellow(String compId) {
		final Component comp = (Component)_fellows.get(compId);
		if (comp == null)
			if (ComponentsCtrl.isAutoId(compId))
				throw new ComponentNotFoundException(MZk.AUTO_ID_NOT_LOCATABLE, compId);
			else
				throw new ComponentNotFoundException("Fellow component not found: "+compId);
		return comp;
	}

	//-- PageCtrl --//
	public void init(String id, String title, String style) {
		if (_desktop != null)
			throw new IllegalStateException("Don't init twice");

		if (id != null && id.length() > 0) _id = id;
		if (title != null && title.length() > 0) _title = title;
		if (style != null && style.length() > 0) _style = style;

		_desktop = Executions.getCurrent().getDesktop();
		if (_desktop == null)
			throw new IllegalArgumentException("null desktop");

		final DesktopCtrl dtctrl = (DesktopCtrl)_desktop;
		if (_id != null)
			_id = (String)getExecution().evaluate(this, _id, String.class);
		if (_id == null || _id.length() == 0)
			_id = Strings.encode(new StringBuffer(12).append("_pp"),
				dtctrl.getNextId()).toString();
		dtctrl.addPage(this);	

		evalStyle();
		evalTitle();

		_ip.setVariable("log", _zklog);
		_ip.setVariable("page", this);
		_ip.setVariable("desktop", _desktop);
		_ip.setVariable("pageScope", getAttributes());
		_ip.setVariable("desktopScope", _desktop.getAttributes());
		final Session sess = _desktop.getSession();
		_ip.setVariable("session", sess);
		_ip.setVariable("sessionScope", sess.getAttributes());
		_ip.setVariable("applicationScope", _desktop.getWebApp().getAttributes());
		_ip.setVariable("spaceOwner", this);

		final String INVALID = ".&\\%";
		if (Strings.anyOf(_id, INVALID, 0) < _id.length())
			throw new IllegalArgumentException("Invalid page ID: "+_id+"\nCharacters not allowed: "+INVALID);
	}
	public String getStyle() {
		return _style;
	}

	public final Desktop getDesktop() {
		return _desktop;
	}
	public void addRoot(Component comp) {
		assert D.OFF || comp.getParent() == null;
		for (Iterator it = _roots.iterator(); it.hasNext();)
			if (comp == it.next())
				return;
		_roots.add(comp);
	}
	public void removeRoot(Component comp) {
		for (Iterator it = _roots.iterator(); it.hasNext();)
			if (comp == it.next()) {
				it.remove();
				return;
			}
	}
	public void addFellow(Component comp) {
		final String compId = comp.getId();
		assert D.OFF || !ComponentsCtrl.isAutoId(compId);

		final Object old = _fellows.put(compId, comp);
		if (old != comp) { //possible due to recursive call
			if (old != null) {
				_fellows.put(((Component)old).getId(), old); //recover
				throw new InternalError("Called shall prevent replicated ID for roots");
			}

			if (Variables.isValid(compId)) {
				try {
					setVariable(compId, comp);
				} catch (Throwable ex) {
					if (D.ON) log.warningBriefly("Unable to setVariable: "+compId, ex);
				}
			}
		}
	}
	public void removeFellow(Component comp) {
		final String compId = comp.getId();
		if (_fellows.remove(compId) != null && Variables.isValid(compId)) {
			try {
				unsetVariable(compId);
			} catch (Throwable ex) {
				if (D.ON) log.warningBriefly("Unable to unsetVariable: "+compId, ex);
			}
		}
	}
	public boolean hasFellow(String compId) {
		return _fellows.containsKey(compId);
	}

	public void redraw(Collection responses, Writer out) throws IOException {
		if (log.debugable()) log.debug("Redrawing page: "+this+", roots="+_roots);

		final Execution exec = getExecution();
		final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
		final boolean asyncUpdate = execCtrl.getVisualizer().isEverAsyncUpdate();
		final boolean bIncluded = asyncUpdate || exec.isIncluded();
		String uri = bIncluded ? _pgUri: _dkUri;
		uri = (String)exec.evaluate(this, uri, String.class);

		final Map attrs = new HashMap(6);
		attrs.put("page", this);
		attrs.put("asyncUpdate", Boolean.valueOf(asyncUpdate));
		attrs.put("action", _desktop.getUpdateURI(null));
		attrs.put("responses",
			responses != null ? responses: Collections.EMPTY_LIST);
		if (bIncluded) {
			exec.include(out, uri, attrs, Execution.PASS_THRU_ATTR);
		} else {
			execCtrl.setHeader("Cache-Control", "no-cache");
			execCtrl.setHeader("Pragma", "no-cache");
			exec.forward(out, uri, attrs, Execution.PASS_THRU_ATTR);
			//Don't use include. Otherwise, headers (set by JSP) will be eaten.
		}
	}
	public Class getClass(String clsnm) throws ClassNotFoundException {
		return _ip.getNamespace().getClass(clsnm);
	}
	public final Namespace getNamespace() {
		return _ip.getNamespace();
	}
	public void interpret(String script, Namespace ns) {
		_ip.interpret(script, ns);
	}

	public boolean isListenerAvailable(String evtnm) {
		if (_listeners != null) {
			final List l = (List)_listeners.get(evtnm);
			return l != null && !l.isEmpty();
		}
		return false;
	}
	public Iterator getListenerIterator(String evtnm) {
		if (_listeners != null) {
			final List l = (List)_listeners.get(evtnm);
			if (l != null)
				return l.iterator();
		}
		return CollectionsX.EMPTY_ITERATOR;
	}

	public final Component getOwner() {
		return _owner;
	}
	public final void setOwner(Component comp) {
		if (_owner != null)
			throw new IllegalStateException("owner can be set only once");
		_owner = comp;
	}

 	public Component getDefaultParent() {
 		return _defparent;
 	}
 	public void setDefaultParent(Component comp) {
 		_defparent = comp;
 	}

	public void sessionWillPassivate(Desktop desktop) {
		for (Iterator it = _roots.iterator(); it.hasNext();)
			((ComponentCtrl)it.next()).sessionWillPassivate(this);
	}
	public void sessionDidActivate(Desktop desktop) {
		_desktop = desktop;

		for (Iterator it = _roots.iterator(); it.hasNext();)
			((ComponentCtrl)it.next()).sessionDidActivate(this);
	}

	//-- Serializable --//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		//TODO		
	}

	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		//TODO

		init();
	}

	//-- Object --//
	public String toString() {
		return "[Page "+_id+']';
	}

	private static class DualFuncMapper implements FunctionMapper {
		private FunctionMapper newm, oldm;
		private DualFuncMapper(FunctionMapper newm, FunctionMapper oldm) {
			this.newm = newm;
			this.oldm = oldm;
		}

		//-- FunctionMapper --//
		public Method resolveFunction(String prefix, String name) {
			Method m = this.newm.resolveFunction(prefix, name);
			if (m == null)
				m = this.oldm.resolveFunction(prefix, name);
			return m;
		}
	}
}
