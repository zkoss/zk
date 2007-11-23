/* LanguageDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 18:01:38     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;

import org.zkoss.xel.FunctionMapper;

import org.zkoss.util.resource.Locator;
import org.zkoss.util.logging.Log;
import org.zkoss.el.Taglib;
import org.zkoss.el.FunctionMappers;
import org.zkoss.el.EvaluatorImpl;
import org.zkoss.el.ObjectResolver;
import org.zkoss.web.servlet.JavaScript;
import org.zkoss.web.servlet.StyleSheet;
import org.zkoss.web.el.ELContexts;
import org.zkoss.web.el.ELContext;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.el.Evaluator;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.device.DeviceNotFoundException;

/**
 * A definition of a language, such as xul.
 *
 * @author tomyeh
 */
public class LanguageDefinition {
	private static final Log log = Log.lookup(LanguageDefinition.class);
	//static//
	/** A map of (String name or namespace, LanguageDefinition). */
	private static final Map _ldefByName = new HashMap();
	/** A map of (String ext, LanguageDefinition). */
	private static final Map _ldefsByExt = new HashMap();
	/** A map of (String deviceType, List(LanguageDefinition). */
	private static final Map _ldefsByClient = new HashMap();

	/** The namespace for ZK. It is mainly used to resolve special components
	 * and attributes, such as zscript and use.
	 */
	public static final String ZK_NAMESPACE = "http://www.zkoss.org/2005/zk";
	/** The namespace for ZK annotations.
	 */
	public static final String ANNO_NAMESPACE = "http://www.zkoss.org/2005/zk/annotation";

	/** the device type that this definition belongs to. */
	private final String _deviceType;
	/** name */
	private final String _name;
	/** The name space. */
	private final String _ns;
	/** The extensions supported by this language definition. */
	private List _exts;
	/** The component map. */
	private final ComponentDefinitionMap _compdefs;
	/** The component name for dynamic tags. */
	private String _dyntagnm;
	/** The component definition for dynamic tags. */
	private ComponentDefinition _dyntagDefn;
	/** The set of reserved attributes used by _dyntagDefn. */
	private Set _dyntagRvAttrs;
	/** Map(String lang, String script). */
	private final Map _initscripts = new HashMap();
	/** Map(String lang, String script). */
	private final Map _eachscripts = new HashMap();
	/** The URI to render a page. */
	private final String _desktopURI, _pageURI;
	/** A list of Taglib. */
	private final List _taglibs = new LinkedList();
	/** A list of JavaScript. */
	private final List _js = new LinkedList(),
		_rojs = Collections.unmodifiableList(_js);
	private final Map _jsmods = new HashMap(5),
		_rojsmods = Collections.unmodifiableMap(_jsmods);
	/** A list of StyleSheet. */
	private final List _ss = new LinkedList(),
		_ross = Collections.unmodifiableList(_ss);
	private FunctionMapper _mapper;
	private final Locator _locator;
	/** The label template. */
	private LabelTemplate _labeltmpl;
	/** The macro template. */
	private MacroTemplate _macrotmpl;
	private final Evaluator _evalor;

	/** Returns whether the specified language exists.
	 */
	public static boolean exists(String name) {
		init();

		synchronized (_ldefByName) {
			return _ldefByName.containsKey(name);
		}
	}

	/** Returns the language definition of the specified name or namespace.
	 *
	 * <p>Note: the name and namespace of any language cannot be the same.
	 *
	 * @param name the name or the namespace; If null or empty, "xul/html" is assumed.
	 * @exception DefinitionNotFoundException is thrown if the definition
	 * is not found
	 */
	public static final LanguageDefinition lookup(String name) {
		init();

		if (name == null || name.length() == 0)
			name = "xul/html";
		final LanguageDefinition langdef;
		synchronized (_ldefByName) {
			langdef = (LanguageDefinition)_ldefByName.get(name);
		}
		if (langdef == null)
			if (ZK_NAMESPACE.equals(name))
				throw new DefinitionNotFoundException(ZK_NAMESPACE+" is reserved. Use it only with reserved elements and attributes, such as zscript and attribute");
			else
				throw new DefinitionNotFoundException("Language not found: "+name);
		return langdef;
	}
	/** Returns the language definition by specifying an extension.
	 * @exception DefinitionNotFoundException is thrown if the definition
	 * is not found
	 */
	public static final LanguageDefinition getByExtension(String ext) {
		init();

		if (ext == null || ext.length() == 0)
			ext = "zul";

		final LanguageDefinition langdef;
		synchronized (_ldefsByExt) {
			langdef = (LanguageDefinition)_ldefsByExt.get(ext);
		}
		if (langdef == null)
			throw new DefinitionNotFoundException("Language not found for extension "+ext);
		return langdef;
	}
	/** Returns a readonly list of language definitions belong to
	 * the specified device type.
	 *
	 * <p>A device type identifies the type of a client. For example, "ajax"
	 * represents all Web browsers with Ajax support,
	 * while "mil" represents clients that supports
	 * <i>Mobile User interface markup Language</i> (on Limited Connected Device,
	 * such as mobile phones).
	 *
	 * @param deviceType the device type, e.g., "ajax".
	 * @see #getDeviceType
	 * @see #getAll
	 */
	public static final List getByDeviceType(String deviceType) {
		init();

		final List ldefs;
		synchronized (_ldefsByClient) {
			ldefs = (List)_ldefsByClient.get(deviceType);
		}
		return ldefs != null ? ldefs: Collections.EMPTY_LIST;

	}
	/** Returns a readonly list of all language definitions
	 * regardless of the device type.
	 *
	 * @see #getByDeviceType
	 * @since 2.4.1
	 */
	public static final List getAll() {
		init();

		final List list = new LinkedList();
		synchronized (_ldefsByClient) {
			for (Iterator it = _ldefsByClient.values().iterator();
			it.hasNext();) {
				list.addAll((List)it.next());
			}
		}
		return list;
	}
	/** Returns a readonly collection of all device types.
	 * @see #getByDeviceType
	 */
	public static final Collection getDeviceTypes() {
		init();

		return _ldefsByClient.keySet();
	}
	private static final void init() {
		DefinitionLoaders.load();
	}

	/** Constructs a language defintion.
	 *			
	 * <p>Note: the name and namespace of any language cannot be the same.
	 * In other words, each language has two names, name and namespace.
	 * You can find the language back by either of them via
	 * {@link #lookup}.
	 *
	 * @param deviceType the device type; never null or empty
	 * @param desktopURI the URI used to render a desktop; never null.
	 * @param pageURI the URI used to render a page; never null.
	 * @param ignoreCase whether the component name is case-insensitive 
	 */
	public LanguageDefinition(String deviceType, String name, String namespace,
	List extensions, String desktopURI, String pageURI, boolean ignoreCase,
	Locator locator) {
		if (deviceType == null || deviceType.length() == 0)
			throw new UiException("deviceType cannot be empty");
		if (!Devices.exists(deviceType))
			throw new DeviceNotFoundException(deviceType, MZk.NOT_FOUND, deviceType);
		if (name == null || name.length() == 0)
			throw new UiException("name cannot be empty");
		if (namespace == null || namespace.length() == 0)
			throw new UiException("namespace cannot be empty");
		if (ZK_NAMESPACE.equals(namespace))
			throw new UiException(ZK_NAMESPACE+" is reserved.");
		if (desktopURI == null || desktopURI.length() == 0)
			throw new UiException("The URI for desktop cannot be empty");
		if (pageURI == null || pageURI.length() == 0)
			throw new UiException("The URI for page cannot be empty");
		if (locator == null)
			throw new UiException("locator cannot be null");

		_deviceType = deviceType;
		_name = name;
		_ns = namespace;
		_desktopURI = desktopURI;
		_pageURI = pageURI;
		_locator = locator;
		_compdefs = new ComponentDefinitionMap(ignoreCase);

		_evalor = new Evaluator() {
			public Object evaluate(Component comp, String expr, Class expectedType) {
				return evaluate0(comp, expr, expectedType);
			}
			public Object evaluate(Page page, String expr, Class expectedType) {
				return evaluate0(page, expr, expectedType);
			}
			private Object evaluate0(Object obj, String expr, Class expectedType) {
				if (expr == null || expr.length() == 0 || expr.indexOf("${") < 0)
					return expr;

				final ELContext jc = ELContexts.getCurrent();
				return new EvaluatorImpl().evaluate(
					expr, expectedType,
					new ObjectResolver(
						jc != null ? jc.getVariableResolver(): null, obj),
					getFunctionMapper());
			}
		};

		boolean replWarned = false;
		synchronized (_ldefByName) {
			if (!_ldefByName.containsKey(name)
			&& _ldefByName.containsKey(namespace))
				throw new UiException("Different language, "+name+", with the same namespace, "+namespace);

			_ldefByName.put(namespace, this);
			final Object old = _ldefByName.put(name, this);
			if (old != null) {
				final List ldefs = (List)_ldefsByClient.get(deviceType);
				if (ldefs != null) ldefs.remove(old);

				replWarned = true;
				log.warning("Replicated language: "+name+", overriden by "+this);
			//it is possible if zcommon.jar is placed in both
			//WEB-INF/lib and shared/lib, i.e., appear twice in the class path
			//We overwrite because shared/lib apears first due to
			//getResources is used (parent first)
			}
		}
		if (extensions != null) {
			synchronized (_ldefsByExt) {
				for (Iterator it = extensions.iterator(); it.hasNext();) {
					final String ext = (String)it.next();
					final Object old = _ldefsByExt.put(ext, this);
					if (!replWarned && old != null)
						log.warning("Extension "+ext+", overriden by "+this);
				}
			}
			_exts = Collections.unmodifiableList(extensions);
		} else {
			_exts = Collections.EMPTY_LIST;
		}

		synchronized (_ldefsByClient) {
			List ldefs = (List)_ldefsByClient.get(deviceType);
			if (ldefs == null)
				_ldefsByClient.put(deviceType, ldefs = new LinkedList());
			ldefs.add(this);
		}
	}
	/** Returns the device type that this definition belongs to.
	 *
	 * <p>A device type identifies the type of a client. For example, "ajax"
	 * represents all HTML compatible clients (aka., browsers),
	 * while "mil" represents clients that supports
	 * <i>Mobile Interactive markup Language</i> (on Limited Connected Device,
	 * such as mobile phones).
	 */
	public String getDeviceType() {
		return _deviceType;
	}
	 
	/** Returns name of this language.
	 * Each language definition has a unique name and namespace.
	 */
	public String getName() {
		return _name;
	}
	/** Returns the name space.
	 * Each language definition has a unique name and namespace.
	 */
	public String getNamespace() {
		return _ns;
	}
	/** Returns the readonly list of extensions that this language definition
	 * is associated with (never null).
	 * @since 2.4.1
	 */
	public List getExtensions() {
		return _exts;
	}
	/** Returns the map of components defined in this language (never null).
	 */
	public ComponentDefinitionMap getComponentDefinitionMap() {
		return _compdefs;
	}
	/** Returns {@link ComponentDefinition} of the specified name.
	 *
	 * <p>Note: anonymous component definition won't be returned by
	 * this method.
	 *
	 * @param name the name of the component definition.
	 * @exception DefinitionNotFoundException is thrown if the definition
	 * is not found
	 */
	public ComponentDefinition getComponentDefinition(String name) {
		final ComponentDefinition compdef =
			(ComponentDefinition)_compdefs.get(name);
		if (compdef == null)
			throw new DefinitionNotFoundException("Component definition not found: "+name);
		return compdef;
	}
	/** Returns {@link ComponentDefinition} of the specified class.
	 *
	 * <p>Note: anonymous component definition won't be returned by
	 * this method.
	 *
	 * @param klass the class that implements the component.
	 * @exception DefinitionNotFoundException is thrown if the definition
	 * is not found
	 */
	public ComponentDefinition getComponentDefinition(Class klass) {
		final ComponentDefinition compdef =
			(ComponentDefinition)_compdefs.get(klass);
		if (compdef == null)
			throw new DefinitionNotFoundException("Component definition not found: "+klass);
		return compdef;
	}
	/** Returns whether the specified component is defined.
	 */
	public boolean hasComponentDefinition(String name) {
		return _compdefs.contains(name);
	}
	/** Adds a component definition.
	 */
	public void addComponentDefinition(ComponentDefinition compdef) {
		_compdefs.add(compdef);
	}

	/** Adds the script that shall execute when a page's interpreter
	 * is initialized. In other words, they are evaluated only once for each
	 * page.
	 *
	 * <p>Note: it doesn't test the existence of the specified language,
	 * such that you can add the scripting language later.
	 *
	 * @param zslang the scripting language, say, Java.
	 */
	public void addInitScript(String zslang, String script) {
		if (zslang == null || zslang.length() == 0)
			throw new IllegalArgumentException("null or empty language");
		if (script != null && script.length() > 0) {
			zslang = zslang.toLowerCase();
			synchronized (_initscripts) {
				final String s = (String)_initscripts.get(zslang);
				_initscripts.put(zslang, s != null ? s + '\n' + script: script);
			}
		}
	}
	/** Returns the intial scripts of
	 * the specified language, or null if no script.
	 */
	public String getInitScript(String zslang) {
		zslang = zslang.toLowerCase();
		synchronized (_initscripts) {
			return (String)_initscripts.get(zslang);
		}
	}

	/** Adds the script that shall execute each time before evaluating
	 * zscript.
	 *
	 * <p>Note: it doesn't test the existence of the specified language,
	 * such that you can add the scripting language later.
	 *
	 * @param zslang the scripting language, say, Java.
	 */
	public void addEachTimeScript(String zslang, String script) {
		if (zslang == null || zslang.length() == 0)
			throw new IllegalArgumentException("null or empty language");
		if (script != null && script.length() > 0) {
			zslang = zslang.toLowerCase();
			synchronized (_eachscripts) {
				final String s = (String)_eachscripts.get(zslang);
				_eachscripts.put(zslang, s != null ? s + '\n' + script: script);
			}
		}
	}
	/** Returns the each-time scripts of 
	 * the specified language, or null if no scripts.
	 *
	 * <p>The each-time script is evaluated each time before evaluating
	 * zscript.
	 */
	public String getEachTimeScript(String zslang) {
		zslang = zslang.toLowerCase();
		synchronized (_eachscripts) {
			return (String)_eachscripts.get(zslang);
		}
	}

	/** Adds a {@link JavaScript} required by this language.
	 */
	public void addJavaScript(JavaScript js) {
		if (js == null)
			throw new IllegalArgumentException("null js");
		synchronized (_js) {
			_js.add(js);
		}
	}
	/** Returns a readonly list of all {@link JavaScript} required
	 * by this language.
	 */
	public List getJavaScripts() {
		return _rojs;
	}

	/** Adds the definition of a JavaScript module to this language.
	 * <p>A JavaScript module represents a JavaScript file. This definition
	 * is mainly used to define its version, such that ZK could encode
	 * its URL such that browsers know when to reload it.
	 */
	public void addJavaScriptModule(String name, String version) {
		if (name == null || name.length() == 0
		|| version == null || version.length() == 0)
			throw new IllegalArgumentException();
		_jsmods.put(name, version);
	}
	/** Returns a map of definitions of JavaScript modules,
	 * (String name, String version).
	 */
	public Map getJavaScriptModules() {
		return _rojsmods;
	}

	/** Adds a {@link StyleSheet} required by this language.
	 */
	public void addStyleSheet(StyleSheet ss) {
		if (ss == null)
			throw new IllegalArgumentException("null ss");
		synchronized (_ss) {
			_ss.add(ss);
		}
	}
	/** Returns a readonly list of all {@link StyleSheet} required
	 * by this language.
	 */
	public List getStyleSheets() {
		return _ross;
	}

	/** Returns whether the component names are case-insensitive.
	 */
	public boolean isCaseInsensitive() {
		return _compdefs.isCaseInsensitive();
	}

	/** Return the URI to render a full page (which might be an expression).
	 */
	public String getDesktopURI() {
		return _desktopURI;
	}
	/** Return the URI to render a included page (which might be an expression).
	 */
	public String getPageURI() {
		return _pageURI;
	}

	/** Initializes the specified definition of a macro component
	 * based on {@link #setMacroTemplate}.
	 */
	public void initMacroDefinition(ComponentDefinition compdef) {
		if (_macrotmpl == null)
			throw new UiException("No macro component is supported by "+this);
		if (!compdef.isMacro())
			throw new UiException("Not macro component: "+compdef);

		compdef.addMold("default", _macrotmpl.moldURI);
		compdef.setImplementationClass(_macrotmpl.klass);
	}
	/** Sets the macro template.
	 */
	public void setMacroTemplate(Class klass, String moldURI) {
		_macrotmpl = klass != null ? new MacroTemplate(klass, moldURI): null;
	}

	/** Sets the component and attribute names used to represent a label.
	 * Since label is used a lot in a page, there is a simple way to create
	 * an {@link ComponentInfo} by calling {@link #newLabelInfo}.
	 *
	 * <p>To be able to call {@link #newLabelInfo}, this method must
	 * be called to define the component and attribute names used to create
	 * an {@link ComponentInfo} for a label.
	 */
	public void setLabelTemplate(String compName, String propName, boolean raw) {
		_labeltmpl = compName != null ?
			new LabelTemplate(compName, propName, raw): null;
	}
	/** Constructs and returns an {@link ComponentInfo} for
	 * the specified parent and text,
	 */
	public ComponentInfo newLabelInfo(ComponentInfo parent, String text) {
		if (_labeltmpl == null)
			throw new UiException("No default label component is supported by "+this);
		return _labeltmpl.newComponentInfo(parent, text);
	}
	/** Returns whether the raw label is required.
	 * If true, the parser won't trim the text, and the text is generated
	 * directly to the output without any wrapping.
	 */
	public boolean isRawLabel() {
		return _labeltmpl != null && _labeltmpl.raw;
	}

	/** Adds the definition for the dynamic tag.
	 *
	 * @param compnm the component name used to represent any of dynamic
	 * tags for this language. If null, it means this language definition
	 * doesn't support the dynamic tag.
	 * @param reservedAttrs a set of reserved attributes that
	 * the dynamic tag support. The reserved attributes are the if, unless
	 * and use attributes.
	 */
	public void setDynamicTagInfo(String compnm, Set reservedAttrs) {
		if (compnm != null && _dyntagnm != null)
			log.warning("Overwriting the definition of dynamic tag. Previous="+_dyntagnm+" New="+compnm+" for "+this);
		_dyntagnm = compnm;
		_dyntagDefn = null;
		_dyntagRvAttrs = compnm == null || reservedAttrs.isEmpty() ? null:
			Collections.unmodifiableSet(new HashSet(reservedAttrs));
	}
	/** Returns the component defintion of the dynamic tag, or null if
	 * this language doesn't support the dynamic tag.
	 * @exception DefinitionNotFoundException is thrown if the definition
	 * is not found
	 */
	public ComponentDefinition getDynamicTagDefinition() {
		if (_dyntagDefn == null) { //no sync since racing is OK
			if (_dyntagnm == null)
				return null;
			_dyntagDefn = getComponentDefinition(_dyntagnm);
		}
		return _dyntagDefn;
	}
	/** Returns whether a reserved attribute is used by the dynamic tag
	 * ({@link #getDynamicTagDefinition}).
	 */
	public boolean isDynamicReservedAttributes(String attr) {
		return _dyntagRvAttrs != null && _dyntagRvAttrs.contains(attr);
	}

	/** Adds a tag lib. */
	public void addTaglib(Taglib taglib) {
		synchronized (_taglibs) {
			_taglibs.add(taglib);
			_mapper = null; //ask for re-parse
		}
	}
	/** Returns the function mapper. */
	private FunctionMapper getFunctionMapper() {
		if (_mapper == null) {
			synchronized (this) {
				_mapper =
					FunctionMappers.getFunctionMapper(_taglibs, _locator);
			}
		}
		return _mapper;
	}

	/** Returns the evaluator associated with this definition.
	 */
	public Evaluator getEvaluator() {
		return _evalor;
	}

	//Object//
	public String toString() {
		return "[LanguageDefinition: "+_name+']';
	}

	private static class MacroTemplate {
		private final Class klass;
		private final String moldURI;
		private MacroTemplate(Class klass, String moldURI) {
			if (klass == null || !Component.class.isAssignableFrom(klass)
			|| moldURI == null || moldURI.length() == 0)
				throw new IllegalArgumentException("class="+klass+", mold="+moldURI);
			this.klass = klass;
			this.moldURI = moldURI;
		}
	}
	private class LabelTemplate {
		/** The component definition. */
		private ComponentDefinition _compdef;
		/** The component name used for contructing a label. */
		private final String _name;
		/** The component property used for contructing a label. */
		private final String _prop;
		/** Whether the raw label is required. */
		private final boolean raw;

		private LabelTemplate(String name, String prop, boolean raw) {
			if (name == null || name.length() == 0
			|| prop == null || prop.length() == 0)
				throw new IllegalArgumentException();
			_name = name;
			_prop = prop;
			this.raw = raw;
		}
		private ComponentInfo newComponentInfo(ComponentInfo parent, String text) {
			if (_compdef == null) //no sync since racing is OK
				_compdef = getComponentDefinition(_name);

			final ComponentInfo info = new ComponentInfo(parent, _compdef);
			info.addProperty(_prop, text, null);
			return info;
		}
	}
}
