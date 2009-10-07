/* PageImpl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 18:17:32     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.AbstractMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.Strings;
import org.zkoss.lang.Exceptions;
import org.zkoss.lang.Expectable;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.io.Serializables;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.util.DualFunctionMapper;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Richlet;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.AbstractPage;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinitionMap;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.PageSerializationListener;
import org.zkoss.zk.ui.util.PageActivationListener;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.PageConfig;
import org.zkoss.zk.ui.sys.ComponentCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.Names;
import org.zkoss.zk.ui.sys.UiEngine;
import org.zkoss.zk.ui.sys.IdGenerator;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.au.out.AuSetTitle;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.Interpreters;
import org.zkoss.zk.scripting.HierachicalAware;
import org.zkoss.zk.scripting.SerializableAware;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.NamespaceActivationListener;
import org.zkoss.zk.scripting.InterpreterNotFoundException;
import org.zkoss.zk.scripting.util.AbstractNamespace;

/**
 * An implmentation of {@link Page} and {@link PageCtrl}.
 * Refer to them for more details.
 *
 * <p>Note: though {@link PageImpl} is serializable, it is designed
 * to work with Web container to enable the serialization of sessions.
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
 * @author tomyeh
 */
public class PageImpl extends AbstractPage implements java.io.Serializable {
	private static final Log log = Log.lookup(PageImpl.class);
	private static final Log _zklog = Log.lookup("org.zkoss.zk.log");
    private static final long serialVersionUID = 20081026L;

	/** URI for redrawing as a desktop or part of another desktop. */
	private final ExValue _cplURI, _dkURI, _pgURI;
	/** The component that includes this page, or null if not included. */
	private transient Component _owner;
	/** Used to retore _owner. */
	private transient String _ownerUuid;
	private transient Desktop _desktop;
	private String _id, _uuid;
	private String _title = "", _style = "";
	private final String _path;
	private String _zslang;
	/** A list of deferred zscript [Component parent, {@link ZScript}]. */
	private List _zsDeferred;
	/** A map of attributes. */
	private transient Map _attrs;
	/** A map of event listener: Map(evtnm, List(EventListener)). */
	private transient Map _listeners;
	/** The default parent. */
	private transient Component _defparent;
	/** The reason to store it is PageDefinition is not serializable. */
	private FunctionMapper _mapper;
	/** The reason to store it is PageDefinition is not serializable. */
	private ComponentDefinitionMap _compdefs;
	/** The reason to store it is PageDefinition is not serializable. */
	private transient LanguageDefinition _langdef;
	/** The header tags. */
	private String _hdbfr = "", _hdaft = "";
	/** The root attributes. */
	private String _rootAttrs = "";
	private String _contentType, _docType, _firstLine;
	private Boolean _cacheable;
	/** The expression factory (ExpressionFactory).*/
	private Class _expfcls;
	/** A map of interpreters Map(String zslang, Interpreter ip). */
	private transient Map _ips;
	private transient NS _ns;
	/** A list of {@link VariableResolver}. */
	private transient List _resolvers;
	private boolean _complete;

	/** Constructs a page by giving the page definition.
	 *
	 * <p>Note: when a page is constructed, it doesn't belong to a desktop
	 * yet. Caller has to invoke {@link #init} to complete
	 * the creation of a page.
	 * Why two phase? Contructor could be called before execution
	 * is activated, but {@link #init} must be called in an execution.
	 *
	 * <p>Also note that {@link #getId} and {@link #getTitle}
	 * are not ready until {@link #init} is called.
	 *
	 * @param pgdef the page definition (never null).
	 */
	public PageImpl(PageDefinition pgdef) {
		this(pgdef.getLanguageDefinition(), pgdef.getComponentDefinitionMap(),
			pgdef.getRequestPath(), pgdef.getZScriptLanguage());
	}
	/** Constructs a page without page definition and richlet.
	 *
	 * @param langdef the language definition (never null)
	 * @param compdefs the component definition map.
	 * If null, an empty map is assumed.
	 * @param path the request path. If null, empty is assumed.
	 * @param zslang the zscript language. If null, "Java" is assumed.
	 */
	public PageImpl(LanguageDefinition langdef,
	ComponentDefinitionMap compdefs, String path, String zslang) {
		init();

		_langdef = langdef;
		_cplURI = new ExValue(_langdef.getCompleteURI(), String.class);
		_dkURI = new ExValue(_langdef.getDesktopURI(), String.class);
		_pgURI = new ExValue(_langdef.getPageURI(), String.class);
		_compdefs = compdefs != null ? compdefs:
			new ComponentDefinitionMap(
				_langdef.getComponentDefinitionMap().isCaseInsensitive());
		_path = path != null ? path: "";
		_zslang = zslang != null ? zslang: "Java";
	}

	/** Constructs a page by specifying a richlet.
	 *
	 * <p>Note: when a page is constructed, it doesn't belong to a desktop
	 * yet. Caller has to invoke {@link #init} to complete
	 * the creation of a page.
	 *
	 * <p>Also note that {@link #getId} and {@link #getTitle}
	 * are not ready until {@link #init} is called.
	 *
	 * @param richlet the richlet to serve this page.
	 * @param path the request path, or null if not available
	 */
	public PageImpl(Richlet richlet, String path) {
		init();

		_langdef = richlet.getLanguageDefinition();
		_cplURI = new ExValue(_langdef.getCompleteURI(), String.class);
		_dkURI = new ExValue(_langdef.getDesktopURI(), String.class);
		_pgURI = new ExValue(_langdef.getPageURI(), String.class);
		_compdefs = new ComponentDefinitionMap(
			_langdef.getComponentDefinitionMap().isCaseInsensitive());
		_path = path != null ? path: "";
		_zslang = "Java";
	}
	/** Initialized the page when contructed or deserialized.
	 */
	protected void init() {
		_ips = new LinkedHashMap(4);
		_ns = new NS();
		_attrs = new HashMap();
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
		return _mapper;
	}
	public void addFunctionMapper(FunctionMapper mapper) {
		_mapper = DualFunctionMapper.combine(mapper, _mapper);
	}

	public String getRequestPath() {
		return _path;
	}
	public final String getId() {
		return _id;
	}
	public final String getUuid() {
		return _uuid;
	}
	public void setId(String id) {
		if (_desktop != null && _desktop.getPages().contains(this))
			throw new UiException("Unable to change ID after initialized");
		if (id != null && id.length() > 0) _id = id;
		//No need to update client since it is allowed only before init(...)
	}
	public String getTitle() {
		return _title;
	}
	public void setTitle(String title) {
		if (title == null) title = "";
		if (!_title.equals(title)) {
			_title = title;
			if (_desktop != null) {
				final Execution exec = getExecution();
				if (_title.length() > 0) {
					_title = (String)exec.evaluate(this, _title, String.class);
					if (_title == null) _title = "";
				}

				if (exec.isAsyncUpdate(this))
					getUiEngine().addResponse("setTitle", new AuSetTitle(_title));
			}
		}
	}
	public String getStyle() {
		return _style;
	}
	public void setStyle(String style) {
		if (style == null) style = "";
		if (!_style.equals(style)) {
			_style = style;
			if (_desktop != null) {
				final Execution exec = getExecution();
				if (_style.length() > 0) {
					_style = (String)exec.evaluate(this, _style, String.class);
					if (_style == null) _style = "";
				}
				//FUTURE: might support the change of style dynamically
			}
		}
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
		case REQUEST_SCOPE:
			final Execution exec = getExecution();
			if (exec != null) return exec.getAttributes();
			//fall thru
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

	public Class resolveClass(String clsnm) throws ClassNotFoundException {
		try {
			return Classes.forNameByThread(clsnm);
		} catch (ClassNotFoundException ex) {
			for (Iterator it = getLoadedInterpreters().iterator();
			it.hasNext();) {
				final Class c = ((Interpreter)it.next()).getClass(clsnm);
				if (c != null)
					return c;
			}
			throw ex;
		}
	}
	public void setVariable(String name, Object val) {
		_ns.setVariable(name, val, true);
	}
	public boolean containsVariable(String name) {
		return _ns.containsVariable(name, true);
	}
	public Object getVariable(String name) {
		return _ns.getVariable(name, true);
	}
	public void unsetVariable(String name) {
		_ns.unsetVariable(name, true);
	}
	public Class getZScriptClass(String clsnm) {
		for (Iterator it = getLoadedInterpreters().iterator();
		it.hasNext();) {
			Class cls = ((Interpreter)it.next()).getClass(clsnm);
			if (cls != null)
				return cls;
		}

		try {
			return Classes.forNameByThread(clsnm);
		} catch (ClassNotFoundException ex) {
			return null;
		}
	}
	public Function getZScriptFunction(String name, Class[] argTypes) {
		for (Iterator it = getLoadedInterpreters().iterator();
		it.hasNext();) {
			Function mtd =
				((Interpreter)it.next()).getFunction(name, argTypes);
			if (mtd != null)
				return mtd;
		}
		return null;
	}
	public Function getZScriptFunction(Namespace ns, String name, Class[] argTypes) {
		for (Iterator it = getLoadedInterpreters().iterator();
		it.hasNext();) {
			final Object ip = it.next();
			Function mtd =
				ip instanceof HierachicalAware ?
				((HierachicalAware)ip).getFunction(ns, name, argTypes):
				((Interpreter)ip).getFunction(name, argTypes);
			if (mtd != null)
				return mtd;
		}
		return null;
	}
	public Function getZScriptFunction(
	Component comp, String name, Class[] argTypes) {
		return getZScriptFunction(comp != null ? comp.getNamespace(): null,
			name, argTypes);
	}
	public Object getZScriptVariable(String name) {
		for (Iterator it = getLoadedInterpreters().iterator();
		it.hasNext();) {
			final Object val = ((Interpreter)it.next()).getVariable(name);
			if (val != null)
				return val;
		}
		return null;
	}
	public Object getZScriptVariable(Namespace ns, String name) {
		for (Iterator it = getLoadedInterpreters().iterator();
		it.hasNext();) {
			final Object ip = it.next();
			final Object val = ip instanceof HierachicalAware ?
				((HierachicalAware)ip).getVariable(ns, name):
				((Interpreter)ip).getVariable(name);
			if (val != null)
				return val;
		}
		return null;
	}
	public Object getZScriptVariable(Component comp, String name) {
		return getZScriptVariable(comp != null ? comp.getNamespace(): null,
			name);
	}

	public Object getXelVariable(String name) {
		final VariableResolver resolv =
			getExecution().getVariableResolver();
		return resolv != null ? resolv.resolveVariable(name): null;
	}

	/** Resolves the variable defined in variable resolvers.
	 */
	private Object resolveVariable(String name) {
		if (_resolvers != null) {
			for (Iterator it = _resolvers.iterator(); it.hasNext();) {
				Object o = ((VariableResolver)it.next()).resolveVariable(name);
				if (o != null)
					return o;
			}
		}
		return null;
	}
	public boolean addVariableResolver(VariableResolver resolver) {
		if (resolver == null)
			throw new IllegalArgumentException("null");

		if (_resolvers == null)
			_resolvers = new LinkedList();
		else if (_resolvers.contains(resolver))
			return false;

		_resolvers.add(0, resolver); //FILO order
		return true;
	}
	public boolean removeVariableResolver(VariableResolver resolver) {
		return _resolvers != null && _resolvers.remove(resolver);
	}

	public boolean addEventListener(String evtnm, EventListener listener) {
		if (evtnm == null || listener == null)
			throw new IllegalArgumentException("null");
		if (!Events.isValid(evtnm))
			throw new IllegalArgumentException("Invalid event name: "+evtnm);

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

	public boolean isComplete() {
		return _complete;
	}
	public void setComplete(boolean complete) {
		_complete = complete;
	}

	//-- PageCtrl --//
	public void preInit() {
		if (_desktop != null)
			throw new IllegalStateException("init twice");

		final Execution exec = Executions.getCurrent();
		_desktop = exec.getDesktop();
		if (_desktop == null)
			throw new IllegalArgumentException("null desktop");
	}
	public void init(PageConfig config) {
		final Execution exec = Executions.getCurrent();

		initVariables();

		if (((ExecutionCtrl)exec).isRecovering()) {
			final String uuid = config.getUuid(), id = config.getId();
			if (uuid == null || id == null)
				throw new IllegalArgumentException("both id and uuid are required in recovering");
			_uuid = uuid;
			_id = id;
		} else {
			final IdGenerator idgen =
				((WebAppCtrl)_desktop.getWebApp()).getIdGenerator();
			if (idgen != null)
				_uuid = idgen.nextPageUuid(this);
			if (_uuid == null)
				_uuid = ((DesktopCtrl)_desktop).getNextUuid();

			if (_id == null) {
				final String id = config.getId();
				if (id != null && id.length() != 0) _id = id;
			}
			if (_id != null)
				_id = (String)exec.evaluate(this, _id, String.class);
			if (_id != null && _id.length() != 0) {
				final String INVALID = ".&\\%";
				if (Strings.anyOf(_id, INVALID, 0) < _id.length())
					throw new IllegalArgumentException("Invalid page ID: "+_id+". Invalid characters: "+INVALID);
			} else {
				_id = _uuid;
			}
		}

		String s = config.getHeaders(true);
		if (s != null) _hdbfr = s;
		s = config.getHeaders(false);
		if (s != null) _hdaft = s;

		if (_title.length() == 0) {
			s = config.getTitle();
			if (s != null) setTitle(s);
		}
		if (_style.length() == 0) {
			s = config.getStyle();
			if (s != null) setStyle(s);
		}

		((DesktopCtrl)_desktop).addPage(this);	
	}
	private void initVariables() {
		setVariable("log", _zklog);
		setVariable("page", this);
		setVariable("pageScope", getAttributes());
		setVariable("requestScope", REQUEST_ATTRS);
		setVariable("spaceOwner", this);

		if (_desktop != null) {
			setVariable("desktop", _desktop);
			setVariable("desktopScope", _desktop.getAttributes());
			final WebApp wapp = _desktop.getWebApp();
			setVariable("application", wapp);
			setVariable("applicationScope", wapp.getAttributes());
			final Session sess = _desktop.getSession();
			setVariable("session", sess);
			setVariable("sessionScope",
				sess != null ? sess.getAttributes(): Collections.EMPTY_MAP);
		}
	}
	public void destroy() {
		super.destroy();

		for (Iterator it = getLoadedInterpreters().iterator(); it.hasNext();) {
			final Interpreter ip = (Interpreter)it.next();
			try {
				ip.destroy();
			} catch (Throwable ex) {
				log.error("Failed to destroy "+ip, ex);
			}
		}
		_ips.clear();

		//theorectically, the following is not necessary, but, to be safe...
		_attrs.clear();
		_ips = null; //not clear since it is better to NPE than memory leak
		_desktop = null;
		_owner = _defparent = null;
		_listeners = null;
		_ns = null;
		_resolvers = null;
	}

	private static final Map REQUEST_ATTRS = new AbstractMap() {
		public Set entrySet() {
			final Execution exec = Executions.getCurrent();
			if (exec == null) return Collections.EMPTY_SET;
			return exec.getAttributes().entrySet();
		}
		public Object put(Object name, Object value) {
			final Execution exec = Executions.getCurrent();
			if (exec == null) throw new IllegalStateException("No execution at all");
			return exec.getAttributes().put(name, value);
		}
		public boolean containsKey(Object name) {
			final Execution exec = Executions.getCurrent();
			return exec != null && exec.getAttributes().containsKey(name);
		}
		public Object get(Object name) {
			final Execution exec = Executions.getCurrent();
			if (exec == null) return null;
			return exec.getAttributes().get(name);
		}
		public Object remove(Object name) {
			final Execution exec = Executions.getCurrent();
			if (exec == null) return null;
			return exec.getAttributes().remove(name);
		}
	};

	public String getHeaders(boolean before) {
		return before ? _hdbfr: _hdaft;
	}
	public String getHeaders() {
		return _hdbfr + _hdaft;
	}
	public String getRootAttributes() {
		return _rootAttrs;
	}
	public void setRootAttributes(String rootAttrs) {
		_rootAttrs = rootAttrs != null ? rootAttrs: "";
	}
	public String getContentType() {
		return _contentType;
	}
	public void setContentType(String contentType) {
		_contentType = contentType;
	}
	public String getDocType() {
		return _docType;
	}
	public void setDocType(String docType) {
		_docType = docType;
	}
	public String getFirstLine() {
		return _firstLine;
	}
	public void setFirstLine(String firstLine) {
		_firstLine = firstLine;
	}
	public Boolean getCacheable() {
		return _cacheable;
	}
	public void setCacheable(Boolean cacheable) {
		_cacheable = cacheable;
	}

	public final Desktop getDesktop() {
		return _desktop;
	}

	public void redraw(Collection responses, Writer out) throws IOException {
		final Execution exec = getExecution();
		final ExecutionCtrl execCtrl = (ExecutionCtrl)exec;
		final boolean asyncUpdate = execCtrl.getVisualizer().isEverAsyncUpdate();
		final boolean proxy = isProxy(exec);
		boolean bIncluded = exec.isIncluded()
			|| exec.getAttribute(ATTR_REDRAW_BY_INCLUDE) != null
			|| proxy;
		final String uri = (String)
			(_complete ? _cplURI: bIncluded ? _pgURI: _dkURI)
				.getValue(_langdef.getEvaluator(), this);
				//desktop and page URI is defined in language

		if (!bIncluded && asyncUpdate) //Bug 2522437
			bIncluded = getOwner() != null
				|| _desktop.getPages().iterator().next() != this;
		if (bIncluded)
			exec.setAttribute("org.zkoss.zk.ui.page.included", Boolean.TRUE);
			//maintain original state since desktop.dsp will include page.dsp
		else
			bIncluded |= asyncUpdate;

		//a temporary solution before IE8 really mature
		if (!asyncUpdate)
			try {
				if (exec.isBrowser("ie8")
				&& !exec.containsResponseHeader("X-UA-Compatible"))
					exec.setResponseHeader("X-UA-Compatible", "IE=EmulateIE7");
			} catch (Throwable ex) { //ignore (it might not be allowed)
			}

		final Map attrs = new HashMap(8);
		attrs.put("page", this);
		attrs.put("asyncUpdate", Boolean.valueOf(asyncUpdate));
			//whether it is caused by AU request
		attrs.put("proxy", Boolean.valueOf(proxy));
			//whether it is caused by ZK Proxy
		attrs.put("embed", Boolean.valueOf(asyncUpdate || proxy));
			//if embed, not to generate JavaScript and CSS
		attrs.put("responses",
			responses != null ? responses: Collections.EMPTY_LIST);
		if (bIncluded) {
			exec.include(out, uri, attrs, Execution.PASS_THRU_ATTR);
		} else {
//FUTURE: Consider if config.isKeepDesktopAcrossVisits() implies cacheable
//Why yes: the client doesn't need to ask the server for updated content
//Why no: browsers seems fail to handle DHTML correctly (when BACK to
//a DHTML page), so it is better to let the server handle cache, if any
			final boolean cacheable =
				_cacheable != null ?  _cacheable.booleanValue():
					_desktop.getDevice().isCacheable();
			if (!cacheable) {
				//Bug 1520444
				exec.setResponseHeader("Pragma", "no-cache");
				exec.addResponseHeader("Cache-Control", "no-cache");
				exec.addResponseHeader("Cache-Control", "no-store");
				//exec.addResponseHeader("Cache-Control", "private");
				//exec.addResponseHeader("Cache-Control", "max-age=0");
				//exec.addResponseHeader("Cache-Control", "s-maxage=0");
				//exec.addResponseHeader("Cache-Control", "must-revalidate");
				//exec.addResponseHeader("Cache-Control", "proxy-revalidate");
				//exec.addResponseHeader("Cache-Control", "post-check=0");
				//exec.addResponseHeader("Cache-Control", "pre-check=0");
				exec.setResponseHeader("Expires", "-1");

				exec.setAttribute(Attributes.NO_CACHE, Boolean.TRUE);
				//so ZkFns.outLangJavaScripts generates zk.keepDesktop correctly
			}

			exec.forward(out, uri, attrs, Execution.PASS_THRU_ATTR);
				//Don't use include. Otherwise, headers will be gone.
		}
	}
	/** Tests if the request is sent by ZK Proxy. */
	private static boolean isProxy(Execution exec) {
		final String ua = exec.getHeader("User-Agent");
		return ua != null && ua.indexOf("ZK Proxy") >= 0;
	}

	public final Namespace getNamespace() {
		return _ns;
	}
	public void interpret(String zslang, String script, Namespace ns) {
		getInterpreter(zslang).interpret(script, ns);
	}
	public Interpreter getInterpreter(String zslang) {
		zslang = (zslang != null ? zslang: _zslang).toLowerCase();
		Interpreter ip = (Interpreter)_ips.get(zslang);
		if (ip == null) {
			ip = Interpreters.newInterpreter(zslang, this);
			_ips.put(zslang, ip);
				//set first to avoid dead loop if script calls interpret again

			final String script = _langdef.getInitScript(zslang);
			if (script != null)
				ip.interpret(script, _ns);

			//evaluate deferred zscripts, if any
			try {
				evalDeferredZScripts(ip, zslang);
			} catch (IOException ex) {
				throw new UiException(ex);
			}
		}
		return ip;
	}

	public Collection getLoadedInterpreters() {
		return _ips != null ? _ips.values(): Collections.EMPTY_LIST; //just in case
	}
	public String getZScriptLanguage() {
		return _zslang;
	}
	public void setZScriptLanguage(String zslang)
	throws InterpreterNotFoundException {
		if (!Objects.equals(zslang, _zslang)) {
			if (!Interpreters.exists(zslang))
				throw new InterpreterNotFoundException(zslang, MZk.NOT_FOUND, zslang);
			_zslang = zslang;
		}
	}
	public void addDeferredZScript(Component parent, ZScript zscript) {
		if (zscript != null) {
			if (_zsDeferred == null)
				_zsDeferred = new LinkedList();
			_zsDeferred.add(new Object[] {parent, zscript});
		}
	}
	/** Evaluates the deferred zscript.
	 * It is called when the interpreter is loaded
	 */
	private void evalDeferredZScripts(Interpreter ip, String zslang)
	throws IOException {
		if (_zsDeferred != null) {
			for (Iterator it = _zsDeferred.iterator(); it.hasNext();) {
				final Object[] zsInfo = (Object[])it.next();
				final ZScript zscript = (ZScript)zsInfo[1];
				String targetlang = zscript.getLanguage();
				if (targetlang == null)
					targetlang = _zslang; //use default

				if (targetlang.equalsIgnoreCase(zslang)) { //case insensitive
					it.remove(); //done

					final Component parent = (Component)zsInfo[0];
					if ((parent == null || parent.getPage() == this)
					&& isEffective(zscript, parent)) {
						ip.interpret(zscript.getContent(this, parent),
							parent != null ? parent.getNamespace(): _ns);
					}
				}
			}

			if (_zsDeferred.isEmpty())
				_zsDeferred = null;
		}
	}
	private boolean isEffective(Condition cond, Component comp) {
		return comp != null ? cond.isEffective(comp): cond.isEffective(this);
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
				return new ListenerIterator(l);
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
		for (Iterator it = getRoots().iterator(); it.hasNext();)
			((ComponentCtrl)it.next()).sessionWillPassivate(this);

		willPassivate(_attrs.values());

		if (_listeners != null)
			for (Iterator it = _listeners.values().iterator(); it.hasNext();)
				willPassivate((Collection)it.next());

		willPassivate(_resolvers);

		for (Iterator it = _ns.getVariableNames().iterator();
		it.hasNext();) {
			final Object val = _ns.getVariable((String)it.next(), true);
			willPassivate(val);
			if (val instanceof NamespaceActivationListener)
				((NamespaceActivationListener)val).willPassivate(_ns);
		}
	}
	public void sessionDidActivate(Desktop desktop) {
		_desktop = desktop;

		if (_ownerUuid != null) {
			_owner = _desktop.getComponentByUuid(_ownerUuid);
			_ownerUuid = null;
		}

		for (Iterator it = getRoots().iterator(); it.hasNext();)
			((ComponentCtrl)it.next()).sessionDidActivate(this);

		initVariables(); //since some variables depend on desktop

		didActivate(_attrs.values());

		if (_listeners != null)
			for (Iterator it = _listeners.values().iterator(); it.hasNext();)
				didActivate((Collection)it.next());

		didActivate(_resolvers);

		for (Iterator it = _ns.getVariableNames().iterator();
		it.hasNext();) {
			final Object val = _ns.getVariable((String)it.next(), true);
			didActivate(val);
			if (val instanceof NamespaceActivationListener)
				((NamespaceActivationListener)val).didActivate(_ns);
		}
	}
	private void willPassivate(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				willPassivate(it.next());
	}
	private void willPassivate(Object o) {
		if (o instanceof PageSerializationListener)
			((PageActivationListener)o).willPassivate(this);
	}
	private void didActivate(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				didActivate(it.next());
	}
	private void didActivate(Object o) {
		if (o instanceof PageSerializationListener)
			((PageActivationListener)o).didActivate(this);
	}

	public LanguageDefinition getLanguageDefinition() {
		return _langdef;
	}
	public ComponentDefinitionMap getComponentDefinitionMap() {
		return _compdefs;
	}
	public ComponentDefinition getComponentDefinition(String name, boolean recur) {
		final ComponentDefinition compdef = _compdefs.get(name);
		if (!recur || compdef != null)
			return compdef;

		try {
			return _langdef.getComponentDefinition(name);
		} catch (DefinitionNotFoundException ex) {
		}
		return null;
	}
	public ComponentDefinition getComponentDefinition(Class cls, boolean recur) {
		final ComponentDefinition compdef = _compdefs.get(cls);
		if (!recur || compdef != null)
			return compdef;

		try {
			return _langdef.getComponentDefinition(cls);
		} catch (DefinitionNotFoundException ex) {
		}
		return null;
	}
	public Class getExpressionFactoryClass() {
		return _expfcls;
	}
	public void setExpressionFactoryClass(Class expfcls) {
		if (expfcls != null && !ExpressionFactory.class.isAssignableFrom(expfcls))
			throw new IllegalArgumentException(expfcls+" must implement "+ExpressionFactory.class);
		_expfcls = expfcls;
	}

	//-- Serializable --//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		s.writeObject(_langdef != null ? _langdef.getName(): null);
		s.writeObject(_owner != null ? _owner.getUuid(): null);
		s.writeObject(_defparent != null ? _defparent.getUuid(): null);

		willSerialize(_attrs.values());
		Serializables.smartWrite(s, _attrs);

		if (_listeners != null)
			for (Iterator it = _listeners.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				s.writeObject(me.getKey());

				final Collection ls = (Collection)me.getValue();
				willSerialize(ls);
				Serializables.smartWrite(s, ls);
			}
		s.writeObject(null);

		willSerialize(_resolvers);
		Serializables.smartWrite(s, _resolvers);

		//handle namespace
		for (Iterator it = _ns._vars.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String nm = (String)me.getKey();
			final Object val = me.getValue();
			willSerialize(val); //always called even not serializable

			if (isVariableSerializable(nm, val)
			&& (val == null || val instanceof java.io.Serializable || val instanceof java.io.Externalizable)) {
				s.writeObject(nm);
				s.writeObject(val);
			}
		}
		s.writeObject(null); //denote end-of-namespace

		//Handles interpreters
		for (Iterator it = _ips.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final Object ip = me.getValue();
			if (ip instanceof SerializableAware) {
				s.writeObject((String)me.getKey()); //zslang

				((SerializableAware)ip).write(s,
					new SerializableAware.Filter() {
						public boolean accept(String name, Object value) {
							return isVariableSerializable(name, value)
							&& (value == null || value instanceof java.io.Serializable || value instanceof java.io.Externalizable);
						}
					});
			}
		}
		s.writeObject(null); //denote end-of-interpreters
	}
	private void willSerialize(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				willSerialize(it.next());
	}
	private void willSerialize(Object o) {
		if (o instanceof PageSerializationListener)
			((PageSerializationListener)o).willSerialize(this);
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();

		final String langnm = (String)s.readObject();
		if (langnm != null)
			_langdef = LanguageDefinition.lookup(langnm);

		_ownerUuid = (String)s.readObject();
			//_owner is restored later when sessionDidActivate is called

		final String pid = (String)s.readObject();
		if (pid != null)
			_defparent = fixDefaultParent(getRoots(), pid);

		Serializables.smartRead(s, _attrs);
		didDeserialize(_attrs.values());

		for (;;) {
			final String evtnm = (String)s.readObject();
			if (evtnm == null) break; //no more

			if (_listeners == null) _listeners = new HashMap();
			final Collection ls = Serializables.smartRead(s, (Collection)null);
			_listeners.put(evtnm, ls);
			didDeserialize(ls);
		}

		_resolvers = (List)Serializables.smartRead(s, _resolvers); //might be null
		didDeserialize(_resolvers);

		//handle namespace
		initVariables();
		for (;;) {
			final String nm = (String)s.readObject();
			if (nm == null) break; //no more

			Object val = s.readObject();
			_ns.setVariable(nm, val, true);
			didDeserialize(val);
		}

		//Handles interpreters
		for (;;) {
			final String zslang = (String)s.readObject();
			if (zslang == null) break; //no more

			((SerializableAware)getInterpreter(zslang)).read(s);
		}
	}
	private void didDeserialize(Collection c) {
		if (c != null)
			for (Iterator it = c.iterator(); it.hasNext();)
				didDeserialize(it.next());
	}
	private void didDeserialize(Object o) {
		if (o instanceof PageSerializationListener)
			((PageSerializationListener)o).didDeserialize(this);
	}
	private static boolean isVariableSerializable(String name, Object value) {
		return !_nonSerNames.contains(name) && !(value instanceof Component);
	}
	private final static Set _nonSerNames = new HashSet();
	static {
		final String[] nms = {
			"alert", "log", "page", "desktop", "pageScope", "desktopScope",
			"applicationScope", "requestScope", "spaceOwner",
			"session", "sessionScope", "execution"};
		for (int j = 0; j < nms.length; ++j)
			_nonSerNames.add(nms[j]);
	}
	private static final
	Component fixDefaultParent(Collection c, String uuid) {
		for (Iterator it = c.iterator(); it.hasNext();) {
			Component comp = (Component)it.next();
			if (uuid.equals(comp.getUuid()))
				return comp; //found

			comp = fixDefaultParent(comp.getChildren(), uuid);
			if (comp != null) return comp;
		}
		return null;
	}

	//-- Object --//
	public String toString() {
		return "[Page "+_id+']';
	}

	private class NS extends AbstractNamespace {
		private final Map _vars = new HashMap();

		//Namespace//
		public Component getOwner() {
			return null;
		}
		public Page getOwnerPage() {
			return PageImpl.this;
		}
		public Set getVariableNames() {
			return _vars.keySet();
		}
		public boolean containsVariable(String name, boolean local) {
			return _vars.containsKey(name) || hasFellow(name)
				|| resolveVariable(name) != null;
		}
		public Object getVariable(String name, boolean local) {
			Object val = _vars.get(name);
			if (val != null || _vars.containsKey(name))
				return val;

			val = getFellowIfAny(name);
			return val != null ? val: resolveVariable(name);
		}
		public void setVariable(String name, Object value, boolean local) {
			_vars.put(name, value);
			notifyAdd(name, value);
		}
		public void unsetVariable(String name, boolean local) {
			_vars.remove(name);
			notifyRemove(name);
		}

		public Namespace getParent() {
			return null;
		}
		public void setParent(Namespace parent) {
			throw new UnsupportedOperationException();
		}
	}
}
