/* PageDefinition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 11:27:07     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.zkoss.util.resource.Locator;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.Function;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.taglib.Taglibs;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.PageConfig;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.impl.SimpleEvaluator;
import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * A page definition.
 * It represents a ZUL page.
 *
 * <p>Note: it is not thread-safe.
 *
 * <p>Note: it is not serializable.
 *
 * @author tomyeh
 * @see ComponentDefinition
 */
public class PageDefinition extends NodeInfo {
	private final LanguageDefinition _langdef;
	private final Locator _locator;
	private String _id, _title, _style;
	/** The request path. */
	private String _path = "";
	/** The zscript language. */
	private String _zslang = "Java";
	private List _taglibs;
	/** A map of imported classes for expression, Map<String nm, Class cls>. */
	private Map _expimps;
	/** A map of XEL methods, List<[String prefix, String name, Function func]>. */
	private List _xelmtds;
	/** The evaluator. */
	private Evaluator _eval;
	/** The evaluator reference. */
	private EvaluatorRef _evalr;
	/** The function mapper. */
	private FunctionMapper _mapper;
	/* List(InitiatorInfo). */
	private List _initdefs;
	/** List(VariableResolverInfo). */
	private List _resolvdefs;
	/** List(FunctionMapper mapper). */
	private List _mapperdefs;
	/** List(HeaderInfo). They are generated before ZK default headers, such as meta. */
	private List _hdBfrDefs;
	/** List(HeaderInfo). They are generated after ZK default headers. */
	private List _hdAftDefs;
	/** List(ForwardInfo). */
	private List _forwdefs;
	/** Map(String name, ExValue value). */
	private Map _rootAttrs;
	private ExValue _contentType, _docType, _firstLine;
	/** The expression factory (ExpressionFactory).*/
	private Class _expfcls;
	private final ComponentDefinitionMap _compdefs;
	private Boolean _cacheable;
	private Boolean _autoTimeout;
	private boolean _complete;

	/** Constructor.
	 * @param langdef the default language which is used if no namespace
	 * is specified. Note: a page might have components from different
	 * languages.
	 */
	public PageDefinition(LanguageDefinition langdef, Locator locator) {
		if (langdef == null)
			throw new IllegalArgumentException("null langdef");
		if (locator == null)
			throw new IllegalArgumentException("null locator");

		_langdef = langdef;
		_locator = locator;
		_compdefs = new ComponentDefinitionMap(
			_langdef.getComponentDefinitionMap().isCaseInsensitive());
	}
	/** Constructor.
	 * @param langdef the default language which is used if no namespace
	 * is specified. Note: a page might have components from different
	 * languages.
	 * @param id the identifier. See {@link #setId}.
	 * @param title the title. See {@link #setTitle}.
	 * @param style the CSS style. See {@link #setStyle}.
	 */
	public PageDefinition(LanguageDefinition langdef, String id, String title,
	String style, Locator locator) {
		this(langdef, locator);
		setId(id);
		setTitle(title);
		setStyle(style);
	}

	/** Returns the language definition that this page is default to be.
	 */
	public LanguageDefinition getLanguageDefinition() {
		return _langdef;
	}
	/** Returns the parent (always null).
	 */
	public NodeInfo getParent() {
		return null;
	}

	/** Returns the locator associated with this page definition.
	 */
	public Locator getLocator() {
		return _locator;
	}

	/** Returns the default scripting language which is assumed when
	 * a zscript element doesn't specify any language.
	 *
	 * <p>Default: Java.
	 */
	public String getZScriptLanguage() {
		return _zslang;
	}
	/** Sets the default scripting language which is assumed when
	 * a zscript element doesn't specify any language.
	 *
	 * @param zslang the default scripting language.
	 */
	public void setZScriptLanguage(String zslang) {
		if (zslang == null || zslang.length() == 0)
			throw new IllegalArgumentException("null or empty");
		_zslang = zslang;
	}

	/** Returns the identitifer that will be assigned to pages created from
	 * this definition, or null if the identifier shall be generated automatically.
	 * <p>Note: the returned value might contain EL expressions.
	 */
	public String getId() {
		return _id;
	}
	/** Sets the identifier that will be assigned to pages created from this
	 * definition.
	 * @param id the identifier. It might contain EL expressions.
	 * If null or empty (null is assumed), page's ID is generated automatically.
	 * If not empty, ID (after evaluated) must be unquie in the same request.
	 */
	public void setId(String id) {
		_id = id != null && id.length() > 0 ? id: null;
	}
		
	/** Returns the title that will be assigned to pages created from
	 * this definition, or null if no title is assigned at the beginning.
	 * <p>Note: the returned value might contain EL expressions.
	 */
	public String getTitle() {
		return _title;
	}
	/** Sets the title that will be assigned to pages created from
	 * this definition, or null if no title is assigned at the beginning.
	 * @param title the title. If empty, null is assumed.
	 */
	public void setTitle(String title) {
		_title = title != null && title.length() > 0 ? title: null;
	}

	/** Returns the CSS style that will be assigned to pages created from
	 * this definition, or null if no style is assigned at the beginning.
	 * <p>Note: the returned value might contain EL expressions.
	 */
	public String getStyle() {
		return _style;
	}
	/** Sets the CSS style that will be assigned to pages created from
	 * this definition, or null if no style is assigned at the beginning.
	 * @param style the CSS style. If empty, null is assumed.
	 */
	public void setStyle(String style) {
		_style = style != null && style.length() > 0 ? style: null;
	}

	/** Returns the request path of this page definition, or ""
	 * if not available.
	 * <p>It is the same as the servlet path
	 * (javax.servlet.http.HttpServletRequest's getServletPath), if ZK is running
	 * at a servlet container.
	 */
	public String getRequestPath() {
		return _path;
	}
	/** Sets the request path of this page definition.
	 */
	public void setRequestPath(String path) {
		_path = path != null ? path: "";
	}

	/** Imports the specified directives from the specified page definition.
	 *
	 * @param pgdef the page definition to import from.
	 * @param directives an array of the directive names to import.
	 * If null, {"init", "component"} is assumed, i.e., only the init
	 * directives and component definitions are imported.<br/>
	 * Importable directives include "component", "init", "meta",
	 * "taglib", "variable-resolver", and "xel-method".
	 * If "*", all of them are imported.
	 * Note: "meta" implies "link".
	 * @since 3.0.2
	 */
	public void imports(PageDefinition pgdef, String[] directives) {
		if (pgdef._initdefs != null
		&& (directives == null || contains(directives, "init")))
			for (Iterator it = pgdef._initdefs.iterator(); it.hasNext();)
				addInitiatorInfo((InitiatorInfo)it.next());

		if (directives == null || contains(directives, "component")) {
			for (Iterator it = pgdef._compdefs.getNames().iterator();
			it.hasNext();)
				addComponentDefinition(pgdef._compdefs.get((String)it.next()));
		}

		if (pgdef._taglibs != null
		&& directives != null && contains(directives, "taglib")) {
			for (Iterator it = pgdef._taglibs.iterator(); it.hasNext();)
				addTaglib((Taglib)it.next());
		}

		if (pgdef._resolvdefs != null
		&& directives != null && contains(directives, "variable-resolver")) {
			for (Iterator it = pgdef._resolvdefs.iterator(); it.hasNext();)
				addVariableResolverInfo((VariableResolverInfo)it.next());
		}

		if (pgdef._mapperdefs != null
		&& directives != null && contains(directives, "function-mapper")) {
			for (Iterator it = pgdef._mapperdefs.iterator(); it.hasNext();)
				addFunctionMapperInfo((FunctionMapperInfo)it.next());
		}

		if (pgdef._xelmtds != null
		&& directives != null && contains(directives, "xel-method")) {
			for (Iterator it = pgdef._xelmtds.iterator(); it.hasNext();) {
				final Object[] inf = (Object[])it.next();
				addXelMethod((String)inf[0], (String)inf[1], (Function)inf[2]);
			}
		}

		if (pgdef._hdBfrDefs != null
		&& directives != null && contains(directives, "meta")) {
			for (Iterator it = pgdef._hdBfrDefs.iterator(); it.hasNext();)
				addHeaderInfo((HeaderInfo)it.next(), true);
		}
		if (pgdef._hdAftDefs != null
		&& directives != null && contains(directives, "meta")) {
			for (Iterator it = pgdef._hdAftDefs.iterator(); it.hasNext();)
				addHeaderInfo((HeaderInfo)it.next(), false);
		}
	}
	private static boolean contains(String[] dirs, String dir) {
		for (int j = dirs.length; --j >= 0;)
			if ("*".equals(dirs[j]) || dir.equalsIgnoreCase(dirs[j]))
				return true;
		return false;
	}
	/** Imports the init directives and component definitions from
	 * the specified page definition.
	 *
	 * <p>It is the same as imports(pgdef, null).
	 *
	 * @since #imports(PageDefinition, String[])
	 */
	public void imports(PageDefinition pgdef) {
		imports(pgdef, null);
	}

	/** Adds a defintion of {@link org.zkoss.zk.ui.util.Initiator}. */
	public void addInitiatorInfo(InitiatorInfo init) {
		if (init == null)
			throw new IllegalArgumentException("null");

		if (_initdefs == null)
			_initdefs = new LinkedList();
		_initdefs.add(init);
	}
	/** Returns a list of all {@link Initiator} and invokes
	 * its {@link Initiator#doInit} before returning.
	 * It never returns null.
	 */
	public List doInit(Page page) {
		if (_initdefs == null)
			return Collections.EMPTY_LIST;

		final List inits = new LinkedList();
		for (Iterator it = _initdefs.iterator(); it.hasNext();) {
			final InitiatorInfo def = (InitiatorInfo)it.next();
			try {
				final Initiator init = def.newInitiator(getEvaluator(), page);
				if (init != null) inits.add(init);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
		return inits;
	}

	/** Adds a defintion of {@link VariableResolver}.
	 */
	public void addVariableResolverInfo(VariableResolverInfo resolver) {
		if (resolver == null)
			throw new IllegalArgumentException("null");

		if (_resolvdefs == null)
			_resolvdefs = new LinkedList();
		_resolvdefs.add(resolver);
	}
	/** Adds a defintion of {@link FunctionMapper}.
	 * @since 3.5.0
	 */
	public void addFunctionMapperInfo(FunctionMapperInfo mapper) {
		if (mapper == null)
			throw new IllegalArgumentException("null");

		if (_mapperdefs == null)
			_mapperdefs = new LinkedList();
		_mapperdefs.add(mapper);
	}
	/** Adds a XEL method.
	 *
	 * @param prefix the prefix of the method name
	 * @param name the method name. The final name is "prefix:name"
	 * @param func the function.
	 * @since 3.0.0
	 */
	public void addXelMethod(String prefix, String name, Function func) {
		if (name == null || prefix == null || func == null)
			throw new IllegalArgumentException();
		if (_xelmtds == null)
			_xelmtds = new LinkedList();
		_xelmtds.add(new Object[] {prefix, name, func});
		_eval = null; //ask for re-gen
		_mapper = null; //ask for re-parse
	}
	/** Initializes XEL context for the specified page.
	 *
	 * @param page the page to initialize the context. It cannot be null.
	 */
	public void initXelContext(Page page) {
		page.addFunctionMapper(getTaglibMapper());

		if (_mapperdefs != null)
			for (Iterator it = _mapperdefs.iterator(); it.hasNext();) {
				final FunctionMapperInfo def = (FunctionMapperInfo)it.next();
				try {
					FunctionMapper mapper =
						def.newFunctionMapper(this, page);
					if (mapper != null) 
						page.addFunctionMapper(mapper);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
				}
			}

		if (_resolvdefs != null)
			for (Iterator it = _resolvdefs.iterator(); it.hasNext();) {
				final VariableResolverInfo def = (VariableResolverInfo)it.next();
				try {
					VariableResolver resolver =
						def.newVariableResolver(this, page);
					if (resolver != null) 
						page.addVariableResolver(resolver);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
	}

	/** Adds a header definition ({@link HeaderInfo}).
	 * @param before whether to place the header <b>before</b> ZK's
	 * CSS/JS headers. If false, it is placed after ZK's CSS/JS headers.
	 * @since 3.6.1
	 */
	public void addHeaderInfo(HeaderInfo header, boolean before) {
		if (header == null)
			throw new IllegalArgumentException("null");

		if (before) {
			if (_hdBfrDefs == null)
				_hdBfrDefs = new LinkedList();
			_hdBfrDefs.add(header);
		} else {
			if (_hdAftDefs == null)
				_hdAftDefs = new LinkedList();
			_hdAftDefs.add(header);
		}
	}
	/** Adds a header definition ({@link HeaderInfo}).
	 * It places the meta headers before ZK's CSS/JS headers,
	 * and others after ZK's CSS/JS headers.
	 */
	public void addHeaderInfo(HeaderInfo header) {
		addHeaderInfo(header, "meta".equals(header.getName()));
	}
	/** Converts the header definitions (added by {@link #addHeaderInfo}) to
	 * HTML tags.
	 *
	 * @param before whether to return the headers that shall be shown
	 * before ZK's CSS/JS headers.
	 * If true, only the headers that shall be shown before (such as meta)
	 * are returned.
	 * If true, only the headers that shall be shown after (such as link)
	 * are returned.
	 * @see #getHeaders(Page)
	 * @since 3.6.1
	 */
	public String getHeaders(Page page, boolean before) {
		final List defs = before ? _hdBfrDefs: _hdAftDefs;
		if (defs == null)
			return "";

		final StringBuffer sb = new StringBuffer(256);
		for (Iterator it = defs.iterator(); it.hasNext();)
			sb.append(((HeaderInfo)it.next()).toHTML(this, page)).append('\n');
		return sb.toString();
	}
	/** Converts all header definitions (added by {@link #addHeaderInfo}) to
	 * HTML tags, no matter that shall be shown before or after ZK's
	 * CSS/JS headers.
	 * To have better control, use {@link #getHeaders(Page,boolean)} instead.
	 */
	public String getHeaders(Page page) {
		return getHeaders(page, true) + getHeaders(page, false);
	}
	/** Adds a forward definition ({@link ForwardInfo}).
	 */
	public void addForwardInfo(ForwardInfo forward) {
		if (forward == null)
			throw new IllegalArgumentException("null");

		if (_forwdefs == null)
			_forwdefs = new LinkedList();
		_forwdefs.add(forward);
	}
	/** Returns the URI to forward to, or null if not to foward.
	 * It evaluates the forward definition (added by {@link #addForwardInfo})
	 * one-by-one, if any, to see whether to foward.
	 * Returns null if no forward definition, or no forward definition's
	 * condition is satisfied.
	 */
	public String getForwardURI(Page page) {
		if (_forwdefs == null)
			return null;

		for (Iterator it = _forwdefs.iterator(); it.hasNext();) {
			final String uri = ((ForwardInfo)it.next()).resolveURI(this, page);
			if (uri != null)
				return uri;
		}
		return null;
	}

	/** Returns the content type (after evaluation),
	 * or null to use the device default.
	 *
	 * @param page the page used to evaluate EL expressions, if any
	 * @since 3.0.0
	 */
	public String getContentType(Page page) {
		return _contentType != null ?
			(String)_contentType.getValue(_evalr, page): null;
	}
	/** Sets the content type.
	 *
	 * <p>Default: null (use the device default).
	 *
	 * @param contentType the content type. It may coontain EL expressions.
	 * @since 3.0.0
	 */
	public void setContentType(String contentType) {
		_contentType = contentType != null && contentType.length() > 0 ?
			new ExValue(contentType, String.class): null;
	}
	/** Returns the doc type (&lt;!DOCTYPE&gt;) (after evaluation),
	 * or null to use the device default.
	 *
	 * @param page the page used to evaluate EL expressions, if any
	 * @since 3.0.0
	 */
	public String getDocType(Page page) {
		return _docType != null ?
			(String)_docType.getValue(_evalr, page): null;
	}
	/** Sets the doc type (&lt;!DOCTYPE&gt;).
	 *
	 * <p>Default: null (use the device default).
	 *
	 * @param docType the doc type. It may coontain EL expressions.
	 * @since 3.0.0
	 */
	public void setDocType(String docType) {
		_docType = docType != null && docType.length() > 0 ?
			new ExValue(docType, String.class): null;
	}
	/** Returns the first line to be generated to the output
	 * (after evaluation), or null if nothing to generate.
	 *
	 * <p>For XML devices, it is usually the xml processing instruction:<br/>
	 * <code>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
	 *
	 * @param page the page used to evaluate EL expressions, if any
	 * @since 3.0.0
	 */
	public String getFirstLine(Page page) {
		return _firstLine != null ?
			(String)_firstLine.getValue(_evalr, page): null;
	}
	/** Sets the first line to be generated to the output.
	 *
	 * <p>Default: null (i.e., nothing generated)
	 * @since 3.0.0
	 */
	public void setFirstLine(String firstLine) {
		_firstLine = firstLine != null && firstLine.length() > 0 ?
			new ExValue(firstLine, String.class): null;
	}

	/** Returns if the client can cache the rendered result, or null
	 * to use the device default.
	 *
	 * @since 3.0.0
	 */
	public Boolean getCacheable() {
		return _cacheable;
	}
	/** Sets if the client can cache the rendered result.
	 *
	 * <p>Default: null (use the device default).
	 * @since 3.0.0
	 */
	public void setCacheable(Boolean cacheable) {
		_cacheable = cacheable;
	}

	/** Returns whether to automatical redirect to the timeout URI.
	 *
	 * @see #setAutomaticTimeout
	 * @since 3.6.3
	 */
	public Boolean getAutomaticTimeout() {
		return _autoTimeout;
	}
	/** Sets whether to automatical redirect to the timeout URI.
	 *
	 * <p>Default: null (use the device default).
	 * Refer to {@link org.zkoss.zk.device.Device#setAutomaticTimeout}
	 * for details.
	 * @since 3.6.3
	 */
	public void setAutomaticTimeout(Boolean autoTimeout) {
		_autoTimeout = autoTimeout;
	}

	/** Returns if the page is a complete page.
	 * By complete we mean the page has everything that the client expects.
	 * For example, for HTML output, the page will generate
	 * the HTML, HEAD and BODY tags.
	 *
	 * <p>By default (false), we assume a page is complete if and only if
	 * it is <em>not</em> included by other page.
	 *
	 * <p>If you have a page that has a complete HTML page and
	 * it is included by other page, you have to specify the complete flag
	 * to be true.
	 * @since 3.0.4
	 */
	public boolean isComplete() {
		return _complete;
	}
	/** Sets if the page is a complete page.
	 * <p>Default: false. It means a page is complete if and only if it
	 * is <em>not</em> included by other page.
	 *
	 * @param complete whether the page is complete.
	 * If true, this page is assumed to be complete no matter it is included
	 * or not. If false, this page is assumed to be complete if it is
	 * not included by other page.
	 * @see #isComplete
	 * @since 3.0.4
	 */
	public void setComplete(boolean complete) {
		_complete = complete;
	}
	/** Adds a root attribute.
	 * The previous attributee of the same will be replaced.
	 *
	 * @param value the value. If null, the attribute is removed.
	 * It can be an EL expression.
	 * @since 3.0.0
	 */
	public void setRootAttribute(String name, String value) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException();

		if (_rootAttrs == null) {
			if (value == null)
				return; //nothing to
			_rootAttrs = new LinkedHashMap();
		}

		if (value == null) _rootAttrs.remove(name);
		else _rootAttrs.put(name, new ExValue(value, String.class));
	}
	/** Converts the header definitions (added by {@link #setRootAttribute})
	 * to the attributes of the root element.
	 * For HTML, the root element is the HTML element.
	 * @since 3.0.0
	 */
	public String getRootAttributes(Page page) {
		if (_rootAttrs == null || _rootAttrs.isEmpty())
			return "";

		final Evaluator eval = getEvaluator();
		final StringBuffer sb = new StringBuffer(256);
		for (Iterator it = _rootAttrs.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String val = (String)
				((ExValue)me.getValue()).getValue(eval, page);
			if (val != null)
				HTMLs.appendAttribute(sb, (String)me.getKey(), val);
		}
		return sb.toString();
	}

	/** Returns the map of component definition (never null).
	 */
	public ComponentDefinitionMap getComponentDefinitionMap() {
		return _compdefs;
	}
	/** Adds a component definition belonging to this page definition only.
	 *
	 * <p>It is the same as calling {@link ComponentDefinitionMap#add} 
	 * against {@link #getComponentDefinitionMap}
	 */
	public void addComponentDefinition(ComponentDefinition compdef) {
		final LanguageDefinition langdef = compdef.getLanguageDefinition();
		if (langdef != null) {
			final LanguageDefinition ld2 = getLanguageDefinition();
			if (langdef != ld2
			&& !langdef.getDeviceType().equals(ld2.getDeviceType()))
				throw new UiException("Component definition, "+compdef
					+", does not belong to the same device type of the page definition, "
					+ld2.getDeviceType());
		}
		_compdefs.add(compdef);
	}
	/** Returns the component definition of the specified name, or null
	 * if not found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 *
	 * @param recur whether to look up the component from {@link #getLanguageDefinition}
	 */
	public ComponentDefinition getComponentDefinition(String name, boolean recur) {
		final ComponentDefinition compdef = _compdefs.get(name);
		if (!recur || compdef != null)
			return compdef;

		try {
			return getLanguageDefinition().getComponentDefinition(name);
		} catch (DefinitionNotFoundException ex) {
		}
		return null;
	}
	/** Returns the component definition of the specified class, or null
	 * if not found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 *
	 * @param recur whether to look up the component from {@link #getLanguageDefinition}
	 */
	public ComponentDefinition getComponentDefinition(Class cls, boolean recur) {
		final ComponentDefinition compdef = _compdefs.get(cls);
		if (!recur || compdef != null)
			return compdef;

		try {
			return getLanguageDefinition().getComponentDefinition(cls);
		} catch (DefinitionNotFoundException ex) {
		}
		return null;
	}

	/** Adds a tag lib. */
	public void addTaglib(Taglib taglib) {
		if (taglib == null)
			throw new IllegalArgumentException("null");

		if (_taglibs == null)
			_taglibs = new LinkedList();
		_taglibs.add(taglib);
		_eval = null; //ask for re-gen
		_mapper = null; //ask for re-parse
	}
	/** Adds an imported class to the expression factory.
	 * @since 3.0.0
	 */
	public void addExpressionImport(String nm, Class cls) {
		if (nm == null || cls == null)
			throw new IllegalArgumentException();
		if (_expimps == null)
			_expimps = new HashMap(4);
		_expimps.put(nm, cls);
		_eval = null; //ask for re-gen
		_mapper = null; //ask for re-parse
	}
	/** Sets the implementation of the expression factory that shall
	 * be used by this page.
	 *
	 * <p>Default: null (use the default).
	 *
	 * @param expfcls the implemtation class, or null to use the default.
	 * Note: expfcls must implement {@link ExpressionFactory}.
	 * If null is specified, the class defined in
	 * {@link org.zkoss.zk.ui.util.Configuration#getExpressionFactoryClass}
	 * @since 3.0.0
	 */
	public void setExpressionFactoryClass(Class expfcls) {
		if (expfcls != null && !ExpressionFactory.class.isAssignableFrom(expfcls))
			throw new IllegalArgumentException(expfcls+" must implement "+ExpressionFactory.class);
		_expfcls = expfcls;
	}
	/** Returns the implementation of the expression factory that
	 * is used by this page, or null if
	 * {@link org.zkoss.zk.ui.util.Configuration#getExpressionFactoryClass}
	 * is used.
	 *
	 * @see #setExpressionFactoryClass
	 * @since 3.0.0
	 */
	public Class getExpressionFactoryClass() {
		return _expfcls;
	}

	/** Returns the evaluator based on this page definition (never null).
	 * @since 3.0.0
	 */
	public Evaluator getEvaluator() {
		if (_eval == null)
			_eval = newEvaluator();
		return _eval;
	}
	private Evaluator newEvaluator() {
		return new SimpleEvaluator(getFunctionMapper(), _expfcls);
	}
	/** Returns the evaluator reference (never null).
	 *
	 * @since 3.0.0
	 */
	public EvaluatorRef getEvaluatorRef() {
		if (_evalr == null)
			_evalr = newEvaluatorRef();
		return _evalr;
	}
	private EvaluatorRef newEvaluatorRef() {
		return new PageEvalRef(this);
	}

	/** @deprecated As of release 3.5.0, replaced by {@link #getTaglibMapper}.
	 */
	public FunctionMapper getFunctionMapper() {
		return getTaglibMapper();
	}
	/** Returns the mapper representing the functions defined in
	 * taglib and xel-method.
	 *
	 * <p>Note: it doesn't include the function mapper defined added by
	 * {@link #addFunctionMapperInfo}.
	 * @since 3.5.0
	 */
	public FunctionMapper getTaglibMapper() {
		if (_mapper == null) {
			_mapper = Taglibs.getFunctionMapper(_taglibs, _expimps, _xelmtds, _locator);
			if (_mapper == null)
				_mapper = Expressions.EMPTY_MAPPER;
		}
		return _mapper != Expressions.EMPTY_MAPPER ? _mapper: null;
	}

	/** Initializes a page after execution is activated.
	 * It setup the identifier and title, and adds it to desktop.
	 */
	public void init(final Page page, final boolean evalHeaders) {
		final PageCtrl pageCtrl = (PageCtrl)page;
		pageCtrl.init(
			new PageConfig() {
				public String getId() {return _id;}
				public String getUuid() {return null;}
				public String getTitle() {return _title;}
				public String getStyle() {return _style;}
				public String getHeaders(boolean before) {
					return evalHeaders ?
						PageDefinition.this.getHeaders(page, before): "";
				}
				public String getHeaders() {
					return evalHeaders ?
						PageDefinition.this.getHeaders(page): "";
				}
			});

		String s = getRootAttributes(page);
		if (s != null) pageCtrl.setRootAttributes(s);

		s = getContentType(page);
		if (s != null) pageCtrl.setContentType(s);

		s = getDocType(page);
		if (s != null) pageCtrl.setDocType(s);

		s = getFirstLine(page);
		if (s != null) pageCtrl.setFirstLine(s);

		if (_cacheable != null) pageCtrl.setCacheable(_cacheable);
		if (_autoTimeout != null) pageCtrl.setAutomaticTimeout(_autoTimeout);
		if (_expfcls != null) page.setExpressionFactoryClass(_expfcls);
		page.setComplete(_complete);
	}

	//NodeInfo//
	public PageDefinition getPageDefinition() {
		return this;
	}

	//Object//
	public String toString() {
		return "[PageDefinition: "
			+(_id != null ? _id: _title != null ? _title: ""+System.identityHashCode(this))
			+", path="+_path+']';
	}
}
