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

import javax.servlet.jsp.el.FunctionMapper;

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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.el.Evaluator;

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
	/** A map of (String clientType, List(LanguageDefinition). */
	private static final Map _ldefsByClient = new HashMap();

	/** The namespace for ZK. It is mainly used to resolve special components
	 * and attributes, such as zscript and use.
	 */
	public static final String ZK_NAMESPACE = "http://www.zkoss.org/2005/zk";
	/** The namespace for ZK annotations.
	 */
	public static final String ANNO_NAMESPACE = "http://www.zkoss.org/2005/zk/annotation";

	/** the client type that this definition belongs to. */
	private final String _clientType;
	/** name */
	private final String _name;
	/** The name space. */
	private final String _ns;
	/** A map of (String name, ComponentDefinition). */
	private final Map _compdefs = new HashMap();
	/** A map of (String clsnm, ComponentDefinition). */
	private final Map _compdefsByClass = new HashMap();
	/** The component name for dynamic tags. */
	private String _dyntagnm;
	/** The component definition for dynamic tags. */
	private ComponentDefinition _dyntagDefn;
	/** The set of reserved attributes used by _dyntagDefn. */
	private Set _dyntagRvAttrs;
	/** A list of ZK scripts (String). */
	private final List _scripts = new LinkedList();
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
	/** Whether the element name is case-insensitive. */
	private boolean _ignoreCase;

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
	 * the specified client type.
	 *
	 * <p>A client type identifies the type of a client. For example, "html"
	 * represents all HTML compatible clients (aka., browsers),
	 * while "mul" represents clients that supports
	 * <i>Mobile User interface markup Language</i> (on Limited Connected Device,
	 * such as mobile phones).
	 *
	 * @param clientType the client type, e.g., "html".
	 * @see #getClientType
	 */
	public static final List getByClientType(String clientType) {
		init();

		final List ldefs;
		synchronized (_ldefsByClient) {
			ldefs = (List)_ldefsByClient.get(clientType);
		}
		return ldefs != null ? ldefs: Collections.EMPTY_LIST;

	}
	/** Returns a readonly collection of all client types.
	 * @see #getByClientType
	 */
	public static final Collection getClientTypes() {
		init();

		return _ldefsByClient.keySet();
	}
	private static void init() {
		if (_ldefsByClient.isEmpty()) {//OK not to syn because LinkedList
			synchronized (_ldefsByClient) {
				if (_ldefsByClient.isEmpty())
					DefinitionLoaders.load();
			}
		}
	}

	/** Constructs a language defintion.
	 *			
	 * <p>Note: the name and namespace of any language cannot be the same.
	 * In other words, each language has two names, name and namespace.
	 * You can find the language back by either of them via
	 * {@link #lookup}.
	 *
	 * @param desktopURI the URI used to render a desktop; never null.
	 * @param pageURI the URI used to render a page; never null.
	 */
	public LanguageDefinition(String clientType, String name, String namespace,
	List extensions, String desktopURI, String pageURI, Locator locator) {
		if (clientType == null || clientType.length() == 0)
			throw new UiException("clientType cannot be empty");
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

		_clientType = clientType;
		_name = name;
		_ns = namespace;
		_desktopURI = desktopURI;
		_pageURI = pageURI;
		_locator = locator;

		_evalor = new Evaluator() {
			public Object evaluate(Component comp, String expr, Class expectedType) {
				if (expr == null || expr.length() == 0 || expr.indexOf("${") < 0)
					return expr;

				try {
					final ELContext jc = ELContexts.getCurrent();
					return new EvaluatorImpl().evaluate(
						expr, expectedType,
						new ObjectResolver(
							jc != null ? jc.getVariableResolver(): null, comp),
						getFunctionMapper());
				} catch (javax.servlet.jsp.el.ELException ex) {
					throw UiException.Aide.wrap(ex);
				}
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
				final List ldefs = (List)_ldefsByClient.get(clientType);
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
		}
		synchronized (_ldefsByClient) {
			List ldefs = (List)_ldefsByClient.get(clientType);
			if (ldefs == null)
				_ldefsByClient.put(clientType, ldefs = new LinkedList());
			ldefs.add(this);
		}
	}
	/** Returns the client type that this definition belongs to.
	 *
	 * <p>A client type identifies the type of a client. For example, "html"
	 * represents all HTML compatible clients (aka., browsers),
	 * while "mul" represents clients that supports
	 * <i>Mobile User interface markup Language</i> (on Limited Connected Device,
	 * such as mobile phones).
	 */
	public String getClientType() {
		return _clientType;
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
		if (isCaseInsensitive())
			name = name.toLowerCase();
		synchronized (_compdefs) {
			final ComponentDefinition compdef =
				(ComponentDefinition)_compdefs.get(name);
			if (compdef == null)
				throw new DefinitionNotFoundException("Component definition not found: "+name);
			return compdef;
		}
	}
	/** Returns whether the component of the specified name exists.
	 */
	public boolean hasComponentDefinition(String name) {
		if (isCaseInsensitive())
			name = name.toLowerCase();
		synchronized (_compdefs) {
			return _compdefs.containsKey(name);
		}
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
		synchronized (_compdefsByClass) {
			for (Class cls = klass; cls != null; cls = cls.getSuperclass()) {
				final ComponentDefinition compdef =
					(ComponentDefinition)_compdefsByClass.get(cls.getName());
				if (compdef != null) return compdef;
			}
		}
		throw new DefinitionNotFoundException("Component definition not found: "+klass);
	}
	/** Adds a {@link ComponentDefinition}.
	 * @return the previous component definition, or null if it isn't defined yet.
	 */
	/*package*/
	ComponentDefinition addComponentDefinition(ComponentDefinition compdef) {
		final Object implcls = compdef.getImplementationClass();
		ComponentDefinition old;
		synchronized (_compdefsByClass) {
			old = (ComponentDefinition)_compdefsByClass.put(
				implcls instanceof Class ?
					((Class)implcls).getName(): implcls, compdef);
		}
		if (old != null)
			log.info(old+" is overwriten by "+compdef+" because they use the same class: "+implcls);

		final String nm = compdef.getName();
		synchronized (_compdefs) {
			old = (ComponentDefinition)_compdefs.put(
				isCaseInsensitive() ? nm.toLowerCase(): nm, compdef);
		}
		if (old != null)
			log.info(old+" is overwriten by "+compdef+" because they use the same name: "+compdef.getName());
		return old;
	}

	/** Adds a script for BeanShell.
	 */
	public void addScript(String script) {
		if (script != null && script.length() > 0) {
			synchronized (_scripts) {
				_scripts.add(script);
			}
		}
	}
	/** Returns all scripts. */
	/*package*/ List getScripts() {
		return _scripts;
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

	/** Returns whether the element names are case-insensitive.
	 */
	public boolean isCaseInsensitive() {
		return _ignoreCase;
	}
	/** Sets whether the element names are case-insensitive.
	 */
	/*package*/ void setCaseInsensitive(boolean caseInsensitive) {
		_ignoreCase = caseInsensitive;
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
	 * an {@link InstanceDefinition} by calling {@link #newLabelDefinition}.
	 *
	 * <p>To be able to call {@link #newLabelDefinition}, this method must
	 * be called to define the component and attribute names used to create
	 * an {@link InstanceDefinition} for a label.
	 */
	public void setLabelTemplate(String compName, String propName, boolean raw) {
		_labeltmpl = compName != null ?
			new LabelTemplate(compName, propName, raw): null;
	}
	/** Constructs and returns an {@link InstanceDefinition} for
	 * the specified parent and text,
	 */
	public InstanceDefinition
	newLabelDefinition(InstanceDefinition parent, String text) {
		if (_labeltmpl == null)
			throw new UiException("No default label component is supported by "+this);
		return _labeltmpl.newDefinition(parent, text);
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
		private InstanceDefinition newDefinition(
		InstanceDefinition parent, String text) {
			if (_compdef == null) //no sync since racing is OK
				_compdef = getComponentDefinition(_name);

			final InstanceDefinition instdef =
				new InstanceDefinition(parent, _compdef);
			instdef.addProperty(_prop, text, null);
			return instdef;
		}
	}
}
