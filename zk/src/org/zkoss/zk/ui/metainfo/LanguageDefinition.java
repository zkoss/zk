/* LanguageDefinition.java

	Purpose:
		
	Description:
		
	History:
		Tue May 31 18:01:38     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;

import org.zkoss.util.FastReadArray;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.logging.Log;
import org.zkoss.xel.taglib.Taglibs;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.web.servlet.JavaScript;
import org.zkoss.web.servlet.StyleSheet;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ext.Macro;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.sys.PageRenderer;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.impl.SimpleEvaluator;
import org.zkoss.zk.xel.impl.EvaluatorRef;
import org.zkoss.zk.device.Devices;
import org.zkoss.zk.device.DeviceNotFoundException;
import org.zkoss.zk.ui.metainfo.impl.ComponentDefinitionImpl;

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
	/** A map of (String widgetClass, WidgetDefinition). */
	private static final Map _wgtdefs = new HashMap();

	/** The namespace for ZK. It is mainly used to resolve special components
	 * and attributes, such as zscript and use.
	 */
	public static final String ZK_NAMESPACE = "http://www.zkoss.org/2005/zk";
	/** The namespace for ZK annotations.
	 */
	public static final String ANNO_NAMESPACE = "http://www.zkoss.org/2005/zk/annotation";
	/** The namespace for ZK native namespace.
	 * @since 3.0.0
	 */
	public static final String NATIVE_NAMESPACE = "http://www.zkoss.org/2005/zk/native";
	/*** The namespace for ZK client namespace. It is used to specify
	 * the client attributes, such as the event listener.
	 * @since 5.0.0
	 */
	public static final String CLIENT_NAMESPACE = "http://www.zkoss.org/2005/zk/client";

	/** The namespace for ZK native namespace prefix.
	 * If a namespace starts with {@link #NATIVE_NAMESPACE_PREFIX} ("native:"),
	 * it means it is also a native space ({@link #NATIVE_NAMESPACE}
	 * but the namespace prefix and uri will be generated.
	 *
	 * <p>For example,
	 * <pre><code>&lt;s:svg xmlns:s="native:http://www.w3.org/2000/svg"/&gt;</code></pre>
	 *
	 * <p>generates the following output:
	 *
	 * <pre><code>&lt;s:svg xmlns:s="http://www.w3.org/2000/svg"/&gt;</code></pre>
	 *
	 * where the prefix <code>s</code> and URI <code>http://www.w3.org/2000/svg</code>
	 * are both generated.
	 *
	 * @since 3.0.0
	 */
	public static final String NATIVE_NAMESPACE_PREFIX = "native:";

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
	/** A list of Taglib. */
	private final FastReadArray _taglibs = new FastReadArray(Taglib.class);
	/** A list of JavaScript. */
	private final FastReadArray _js = new FastReadArray(JavaScript.class);
	/** A list of deferrable JavaScript package. */
	private final FastReadArray _pkgs = new FastReadArray(String.class);
	private final Map _jsmods = new HashMap(5),
		_rojsmods = Collections.unmodifiableMap(_jsmods);
	/** A list of StyleSheet. */
	private final FastReadArray _ss = new FastReadArray(StyleSheet.class);
	private final Locator _locator;
	/** The label template. */
	private LabelTemplate _labeltmpl;
	/** The macro template. */
	private Class _macrocls;
	/** The native component definition. */
	private ComponentDefinition _nativedef;
	/** The evaluator. */
	private Evaluator _eval;
	/** The evaluator reference. */
	private EvaluatorRef _evalr;
	/** The page renderer. */
	private PageRenderer _pgrend;
	/** A set of CSS URI. */
	private final Set _cssURIs = new LinkedHashSet();
	/** Whether it is a native language. */
	private final boolean _native;

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
	 *
	 * @param ext the extension, e.g., "zul".
	 * If null, "zul" is assumed.
	 * @exception DefinitionNotFoundException is thrown if the definition
	 * is not found
	 */
	public static final LanguageDefinition getByExtension(String ext) {
		init();

		if (ext == null)
			ext = "zul";

		final LanguageDefinition langdef;
		synchronized (_ldefsByExt) {
			langdef = (LanguageDefinition)_ldefsByExt.get(ext);
		}
		if (langdef == null)
			throw new DefinitionNotFoundException("Language not found for extension "+ext);
		return langdef;
	}
	/** Associates an extension to a language.
	 *
	 * @param lang the language name. It cannot be null.
	 * @param ext the extension, e.g., "svg". It cannot be null.
	 * @since 3.0.0
	 */
	public static final void addExtension(String ext, String lang) {
		if (lang == null || ext == null)
			throw new IllegalArgumentException();

		init();

		final LanguageDefinition langdef = lookup(lang); //ensure it exists
		synchronized (_ldefsByExt) {
			_ldefsByExt.put(ext, langdef);
		}
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
		try {
			DefinitionLoaders.load();
		} catch (java.io.IOException ex) {
			throw new UiException(ex);
		}
	}

	/** Constructs a language defintion.
	 *			
	 * <p>Note: the name and namespace of any language cannot be the same.
	 * In other words, each language has two names, name and namespace.
	 * You can find the language back by either of them via
	 * {@link #lookup}.
	 *
	 * @param deviceType the device type; never null or empty
	 * @param pageRenderer the page renderer used to render a page; never null.
	 * @param ignoreCase whether the component name is case-insensitive
	 * @param bNative whether it is native (i.e., all tags are
	 * {@link org.zkoss.zk.ui.ext.Native}).
	 * If native, the namespaces found in a ZUML page is no longer
	 * used to specified a language. Rather, it is output to the client
	 * directly.
	 * @since 5.0.0
	 */
	public LanguageDefinition(String deviceType, String name, String namespace,
	List extensions, PageRenderer pageRenderer,
	boolean ignoreCase, boolean bNative, Locator locator) {
		if (deviceType == null || deviceType.length() == 0)
			throw new UiException("deviceType cannot be empty");
		if (!Devices.exists(deviceType))
			throw new DeviceNotFoundException(deviceType, MZk.NOT_FOUND, deviceType);
		if (ZK_NAMESPACE.equals(namespace))
			throw new UiException(ZK_NAMESPACE+" is reserved.");
		if (name == null || name.length() == 0
		|| namespace == null || namespace.length() == 0
		|| pageRenderer == null || locator == null)
			throw new IllegalArgumentException();

		_deviceType = deviceType;
		_name = name;
		_ns = namespace;
		_locator = locator;
		_native = bNative;
		_pgrend = pageRenderer;
		_compdefs = new ComponentDefinitionMap(ignoreCase);

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
	/** Returns whether this is a native language.
	 * If true, it means all tags in a ZUML page is considered as
	 * native and all namespaces (except ZK namespace) are output
	 * the client directly.
	 *
	 * @since 3.0.0
	 */
	public boolean isNative() {
		return _native;
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
	/** Returns a readonly collection of all component definitions in this language.
	 * @since 3.6.3
	 */
	public Collection getComponentDefinitions() {
		return _compdefs.getDefinitions();
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
	/** Returns {@link ComponentDefinition} of the specified name, or null
	 * if not found.
	 * It is the same as {@link #getComponentDefinition}, except this method
	 * won't throw any exception.
	 *
	 * @param name the name of the component definition.
	 * @since 3.0.2
	 */
	public ComponentDefinition getComponentDefinitionIfAny(String name) {
		return (ComponentDefinition)_compdefs.get(name);
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
		if (compdef == null)
			throw new IllegalArgumentException();
		_compdefs.add(compdef);
	}

	/** Returns whether the specified widget is defined.
	 * @param widgetClass the name of the widget class (JavaScript class),
	 * including the package name.
	 * @since 5.0.0
	 */
	public boolean hasWidgetDefinition(String widgetClass) {
		return _wgtdefs.containsKey(widgetClass);
	}
	/** Returns the widget of the specified class name.
	 *
	 * @param widgetClass the name of the widget class (JavaScript class),
	 * including the package name.
	 * @exception DefinitionNotFoundException is thrown if the definition
	 * is not found
	 * @since 5.0.0
	 */
	public WidgetDefinition getWidgetDefinition(String widgetClass) {
		final WidgetDefinition wgtdef = getWidgetDefinitionIfAny(widgetClass);
		if (wgtdef == null)
			throw new DefinitionNotFoundException("Widget definition not found: "+widgetClass);
		return wgtdef;
	}
	/** Returns the widget of the specified class name, or null if not found.
	 * It is the same as {@link #getWidgetDefinition}, except this method
	 * won't throw any exception.
	 *
	 * @param widgetClass the name of the widget class (JavaScript class),
	 * including the package name.
	 * @since 5.0.0
	 */
	public WidgetDefinition getWidgetDefinitionIfAny(String widgetClass) {
		return (WidgetDefinition)_wgtdefs.get(widgetClass);
	}
	/** Adds a widget definition.
	 * @since 5.0.0
	 */
	public void addWidgetDefinition(WidgetDefinition wgtdef) {
		_wgtdefs.put(wgtdef.getWidgetClass(), wgtdef);
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
			throw new IllegalArgumentException();
		_js.add(js);
	}
	/** Returns a readonly list of all {@link JavaScript} required
	 * by this language.
	 */
	public Collection getJavaScripts() {
		return new CollectionsX.ArrayCollection(_js.toArray());
	}

	/** Adds a deferrable JavaScript package required by this langauge.
	 * @param pkg the package name, such as "foo.fly"
	 * @since 5.0.0
	 */
	public void addDeferJavaScriptPackage(String pkg) {
		if (pkg == null || pkg.length() == 0)
			throw new IllegalArgumentException();
		_pkgs.add(pkg);
	}
	/** Returns a list of deferrable JavaScript package (String)
	 * required by this language.
	 * @since 5.0.0
	 */
	public Collection getDeferJavaScriptPackages() {
		return new CollectionsX.ArrayCollection(_pkgs.toArray());
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
			throw new IllegalArgumentException();
		_ss.add(ss);
	}
	/** Returns a readonly list of all {@link StyleSheet} required
	 * by this language.
	 */
	public Collection getStyleSheets() {
		return new CollectionsX.ArrayCollection(_ss.toArray());
	}

	/** Returns whether the component names are case-insensitive.
	 */
	public boolean isCaseInsensitive() {
		return _compdefs.isCaseInsensitive();
	}

	/** Returns the page render for this language.
	 * @since 5.0.0
	 */
	public PageRenderer getPageRenderer() {
		return _pgrend;
	}

	/** Sets the macro template.
	 *
	 * @since 5.0.0
	 */
	public void setMacroTemplate(Class klass) {
		if (klass == null || !Component.class.isAssignableFrom(klass)
		|| !Macro.class.isAssignableFrom(klass))
			throw new IllegalArgumentException("Illegal macro class: "+klass);
		_macrocls = klass;
	}
	/** Instantiates and returns the component definition for the specified condition.
	 *
	 * @param pgdef the page definition the macro definitioin belongs to.
	 * If null, it belongs to this language definition.
	 * @param macroURI the ZUML page's URI that is used to render
	 * instances of this macro definition.
	 * @exception UnsupportedOperationException if this language doesn't
	 * support the macros
	 * @since 3.0.0
	 */
	public ComponentDefinition getMacroDefinition(
	String name, String macroURI, boolean inline, PageDefinition pgdef) {
		if (_macrocls == null)
			throw new UiException("Macro not supported by "+this);

		final ComponentDefinition compdef =
			ComponentDefinitionImpl.newMacroDefinition(
				pgdef != null ? null: this, pgdef,
				name, _macrocls, macroURI, inline);
		return compdef;
	}

	/** Sets the native template.
	 * @since 3.0.0
	 */
	public void setNativeTemplate(Class klass) {
		if (klass == null || !Component.class.isAssignableFrom(klass)
		|| !Native.class.isAssignableFrom(klass))
			throw new IllegalArgumentException("Illegal native class: "+klass);
		_nativedef =
			ComponentDefinitionImpl.newNativeDefinition(this, "native", klass);;
	}
	/** Returns the component definition for the native components.
	 *
	 * @exception UnsupportedOperationException if this language doesn't
	 * support the native namespace
	 * @since 3.0.0
	 */
	public ComponentDefinition getNativeDefinition() {
		if (_nativedef == null)
			throw new UnsupportedOperationException("Native not supported by "+this);
		return _nativedef;
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
	/** Returns whether this language prefers the raw label.
	 * By raw labels we mean the text shall not be trimmed and
	 * shall be generated directly to the output (rather than wrapping
	 * with, say, SPAN).
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
		_taglibs.add(taglib);
		_eval = null; //ask for re-gen
	}

	/** Returns the evaluator based on this language definition (never null).
	 * @since 3.0.0
	 */
	public Evaluator getEvaluator() {
		if (_eval == null)
			_eval = newEvaluator();
		return _eval;
	}
	private Evaluator newEvaluator() {
		return new SimpleEvaluator(
			Taglibs.getFunctionMapper(
				new CollectionsX.ArrayCollection(_taglibs.toArray()), _locator), null);
	}
	/** Returns the evaluator reference (never null).
	 * <p>This method is used only for implementation only.
	 * @since 3.0.0
	 */
	public EvaluatorRef getEvaluatorRef() {
		if (_evalr == null)
			_evalr = newEvaluatorRef();
		return _evalr;
	}
	private EvaluatorRef newEvaluatorRef() {
		return new LangEvalRef(this);
	}

	/** Adds the URI of a CSS file that is part of this language.
	 * @param cssURI the URI of a CSS file
	 * @since 5.0.0
	 */
	public void addCSSURI(String cssURI) {
		if (cssURI == null || cssURI.length() == 0)
			throw new IllegalArgumentException();
		_cssURIs.add(cssURI);
	}
	/** Returns a readonly collection of the URIs of CSS files of this language.
	 * @since 5.0.0
	 */
	public Collection getCSSURIs() {
		return _cssURIs;
	}

	//Object//
	public String toString() {
		return "[LanguageDefinition: "+_name+']';
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
