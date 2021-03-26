/* PageDefinition.java

	Purpose:
		
	Description:
		
	History:
		Tue May 31 11:27:07     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.html.HTMLs;
import org.zkoss.lang.ClassResolver;
import org.zkoss.lang.ImportedClassResolver;
import org.zkoss.util.resource.Locator;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.Expressions;
import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.taglib.FunctionDefinition;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.xel.taglib.Taglibs;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.sys.PageConfig;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.EvaluatorRef;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.impl.SimpleEvaluator;

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
public class PageDefinition implements NodeInfo {
	/** A list of a children ({@link NodeInfo}). */
	private final List<NodeInfo> _children = new LinkedList<NodeInfo>();
	private final LanguageDefinition _langdef;
	private final Locator _locator;
	private String _id, _title, _style, _viewport;
	/** The request path. */
	private String _path = "";
	/** The zscript language. */
	private String _zslang = "Java";
	private List<Taglib> _taglibs;
	/** A map of imported classes for expression, Map<String nm, Class cls>. */
	private Map<String, Class<?>> _expimps;
	/** A list of XEL methods, List<FunctionDefinition>. */
	private List<FunctionDefinition> _xelfuncs;
	/** The evaluator. */
	private Evaluator _eval;
	/** The evaluator reference. */
	private EvaluatorRef _evalr;
	/** The function mapper. */
	private FunctionMapper _mapper;
	/* List(InitiatorInfo). */
	private List<InitiatorInfo> _initdefs;
	/** List(VariableResolverInfo). */
	private List<VariableResolverInfo> _resolvdefs;
	/** List(FunctionMapperInfo mapper). */
	private List<FunctionMapperInfo> _mapperdefs;
	/** List(HeaderInfo). They are generated before ZK default headers, such as meta. */
	private List<HeaderInfo> _hdBfrDefs;
	/** List(HeaderInfo). They are generated after ZK default headers. */
	private List<HeaderInfo> _hdAftDefs;
	/** List(ResponseHeaderInfo). */
	private List<ResponseHeaderInfo> _hdResDefs;
	/** List(ForwardInfo). */
	private List<ForwardInfo> _forwdefs;
	/** Map(String name, ExValue value). */
	private Map<String, ExValue> _rootAttrs;
	private ExValue _contentType, _docType, _firstLine, _wgtcls;
	/** The class of the expression factory (ExpressionFactory).*/
	private Class<? extends ExpressionFactory> _expfcls;
	/** The class resolver. */
	private final ImportedClassResolver _clsresolver = new ImportedClassResolver();
	private final ComponentDefinitionMap _compdefs;
	private Boolean _cacheable;
	private Boolean _autoTimeout;
	private boolean _complete;

	//ZK-2623: page scope template
	private Map<String, TemplateInfo> _templatesInfo;

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
		_compdefs = new ComponentDefinitionMap(_langdef.getComponentDefinitionMap().isCaseInsensitive());
	}

	/** Constructor.
	 * @param langdef the default language which is used if no namespace
	 * is specified. Note: a page might have components from different
	 * languages.
	 * @param id the identifier. See {@link #setId}.
	 * @param title the title. See {@link #setTitle}.
	 * @param style the CSS style. See {@link #setStyle}.
	 */
	public PageDefinition(LanguageDefinition langdef, String id, String title, String style, Locator locator) {
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

	/** Returns the identifier that will be assigned to pages created from
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
	 * If not empty, ID (after evaluated) must be unique in the same request.
	 */
	public void setId(String id) {
		_id = id != null && id.length() > 0 ? id : null;
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
		_title = title != null && title.length() > 0 ? title : null;
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
		_style = style != null && style.length() > 0 ? style : null;
	}

	/** Returns the viewport that will be assigned to pages created from
	 * this definition, or "auto" if no viewport is assigned at the beginning.
	 * <p>Note: the returned value might contain EL expressions.
	 * @since 6.5.0
	 */
	public String getViewport() {
		return _viewport;
	}

	/** Sets the viewport that will be assigned to pages created from
	 * this definition, or "auto" if no viewport is assigned at the beginning.
	 * @param viewport the viewport setting. If empty, "auto" is assumed.
	 * @since 6.5.0
	 */
	public void setViewport(String viewport) {
		_viewport = viewport != null && viewport.length() > 0 ? viewport : "auto";
	}

	/** Returns the widget class of the given page, or null if the default is used.
	 * @since 5.0.5
	 */
	public String getWidgetClass(Page page) {
		return _wgtcls != null ? (String) _wgtcls.getValue(_evalr, page) : null;
	}

	/** Sets the widget class of the page.
	 * @param wgtcls the widget class. It may contain EL expressions.
	 * If null or empty, the default widget class is assumed.
	 * @since 5.0.5
	 */
	public void setWidgetClass(String wgtcls) {
		_wgtcls = wgtcls != null && wgtcls.length() > 0 ? new ExValue(wgtcls, String.class) : null;
	}

	/** Returns the request path of this page definition, or ""
	 * if not available.
	 * <p>It is the same as the servlet path
	 * (jakarta.servlet.http.HttpServletRequest's getServletPath), if ZK is running
	 * at a servlet container.
	 */
	public String getRequestPath() {
		return _path;
	}

	/** Sets the request path of this page definition.
	 */
	public void setRequestPath(String path) {
		_path = path != null ? path : "";
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
		if (directives == null || contains(directives, "import"))
			_clsresolver.addAll(pgdef._clsresolver);

		if (pgdef._initdefs != null && (directives == null || contains(directives, "init")))
			for (InitiatorInfo ii : pgdef._initdefs)
				addInitiatorInfo(ii);

		if (directives == null || contains(directives, "component")) {
			for (Iterator it = pgdef._compdefs.getNames().iterator(); it.hasNext();)
				addComponentDefinition(pgdef._compdefs.get((String) it.next()));
		}

		if (pgdef._taglibs != null && directives != null && contains(directives, "taglib"))
			for (Taglib tl : pgdef._taglibs)
				addTaglib(tl);

		if (pgdef._resolvdefs != null && directives != null && contains(directives, "variable-resolver")) {
			for (VariableResolverInfo vri : pgdef._resolvdefs)
				addVariableResolverInfo(vri);
		}

		if (pgdef._mapperdefs != null && directives != null && contains(directives, "function-mapper")) {
			for (FunctionMapperInfo fmi : pgdef._mapperdefs)
				addFunctionMapperInfo(fmi);
		}

		if (pgdef._xelfuncs != null && directives != null && contains(directives, "xel-method"))
			for (FunctionDefinition xfi : pgdef._xelfuncs)
				addXelMethod(xfi.prefix, xfi.name, xfi.function);

		if (pgdef._hdBfrDefs != null && directives != null && contains(directives, "meta")) {
			for (HeaderInfo hi : pgdef._hdBfrDefs)
				addHeaderInfo(hi, true);
		}
		if (pgdef._hdAftDefs != null && directives != null && contains(directives, "meta")) {
			for (HeaderInfo hi : pgdef._hdAftDefs)
				addHeaderInfo(hi, false);
		}

		if (pgdef._hdResDefs != null && directives != null && contains(directives, "header")) {
			for (ResponseHeaderInfo rhi : pgdef._hdResDefs)
				addResponseHeaderInfo(rhi);
		}
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

	private static boolean contains(String[] dirs, String dir) {
		for (int j = dirs.length; --j >= 0;)
			if ("*".equals(dirs[j]) || dir.equalsIgnoreCase(dirs[j]))
				return true;
		return false;
	}

	/** Adds an imported class
	 * Like Java, it is used to import a class or a package of classes, so
	 * that it simplifies the use of the apply attribute, the init directive
	 * and others.
	 * 
	 * @param clsptn the class's fully-qualified name, e.g., <code>com.foo.FooComposer</code>,
	 * a wildcard representing all classes of the give package, e.g., <code>com.foo.*</code>.
	 * @since 6.0.0
	 */
	public void addImportedClass(String clsptn) throws ClassNotFoundException {
		_clsresolver.addImportedClass(clsptn);
	}

	/** Returns a readonly list of the imported class names.
	 * @since 6.0.0
	 */
	public List<String> getImportedClasses() {
		return _clsresolver.getImportedClasses();
	}

	/** Returns the class resolver represented by {@link #getImportedClasses}.
	 * @since 6.0.0
	 */
	public ClassResolver getImportedClassResolver() {
		return _clsresolver;
	}

	/** Adds a definition of {@link Initiator}. */
	public void addInitiatorInfo(InitiatorInfo init) {
		if (init == null)
			throw new IllegalArgumentException("null");

		if (_initdefs == null)
			_initdefs = new LinkedList<InitiatorInfo>();
		_initdefs.add(init);
	}

	/** Returns a list of all {@link Initiator} and invokes
	 * its {@link Initiator#doInit} before returning.
	 * It never returns null.
	 */
	public List<Initiator> doInit(Page page) {
		if (_initdefs == null)
			return Collections.emptyList();

		final List<Initiator> inits = new LinkedList<Initiator>();
		try {
			for (InitiatorInfo ii : _initdefs) {
				final Initiator init = ii.newInitiator(getEvaluator(), page);
				if (init != null)
					inits.add(init);
			}
		} catch (Throwable ex) {
			throw UiException.Aide.wrap(ex);
		}
		return inits;
	}

	/** Adds a definition of {@link VariableResolver}.
	 */
	public void addVariableResolverInfo(VariableResolverInfo resolver) {
		if (resolver == null)
			throw new IllegalArgumentException("null");

		if (_resolvdefs == null)
			_resolvdefs = new LinkedList<VariableResolverInfo>();
		_resolvdefs.add(resolver);
	}

	/** Adds a definition of {@link FunctionMapper}.
	 * @since 3.5.0
	 */
	public void addFunctionMapperInfo(FunctionMapperInfo mapper) {
		if (mapper == null)
			throw new IllegalArgumentException("null");

		if (_mapperdefs == null)
			_mapperdefs = new LinkedList<FunctionMapperInfo>();
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
		checkXelModifiable();
		if (name == null || prefix == null || func == null)
			throw new IllegalArgumentException();
		if (_xelfuncs == null)
			_xelfuncs = new LinkedList<FunctionDefinition>();
		_xelfuncs.add(new FunctionDefinition(prefix, name, func));
	}

	/** Adds a response header.
	 * @since 5.0.2
	 */
	public void addResponseHeaderInfo(ResponseHeaderInfo header) {
		if (header == null)
			throw new IllegalArgumentException();
		if (_hdResDefs == null)
			_hdResDefs = new LinkedList<ResponseHeaderInfo>();
		_hdResDefs.add(header);
	}

	/** Returns a map of response headers (never null).
	 * The value of each entry is a two-element object array. The
	 * first element of the array is the value which is an instance of {@link java.util.Date}
	 * or {@link String} (and never null).
	 * The second element indicates whether to add (rather than set)
	 * header. It is an instance of Boolean (and never null).
	 */
	public Collection<Object[]> getResponseHeaders(Page page) {
		final List<Object[]> headers = new LinkedList<Object[]>();
		if (_hdResDefs != null)
			for (ResponseHeaderInfo rhi : _hdResDefs) {
				headers.add(new Object[] { rhi.getName(), rhi.getValue(this, page),
						Boolean.valueOf(rhi.shallAppend(this, page)) });
			}
		return headers;
	}

	/** Adds a header definition ({@link HeaderInfo}).
	 * @param before whether to place the header <b>before</b> ZK's
	 * CSS/JS headers. If false, it is placed after ZK's CSS/JS headers.
	 * @since 3.6.1
	 */
	public void addHeaderInfo(HeaderInfo header, boolean before) {
		if (header == null)
			throw new IllegalArgumentException();

		if (before) {
			if (_hdBfrDefs == null)
				_hdBfrDefs = new LinkedList<HeaderInfo>();
			_hdBfrDefs.add(header);
		} else {
			if (_hdAftDefs == null)
				_hdAftDefs = new LinkedList<HeaderInfo>();
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

	/** Returns the content that shall be generated inside the head element
	 * and before ZK's default tags (never null).
	 * For example, it might consist of &lt;meta&gt; and &lt;link&gt;.
	 *
	 * <p>Since it is generated before ZK's default tags (such as CSS and JS),
	 * it cannot override ZK's default behaviors.
	 *
	 * @see #getAfterHeadTags
	 * @since 5.0.5
	 */
	public String getBeforeHeadTags(Page page) {
		return getHeadTags(page, _hdBfrDefs);
	}

	/** Returns the content that shall be generated inside the head element
	 * and after ZK's default tags (never null).
	 * For example, it might consist of &lt;meta&gt; and &lt;link&gt;.
	 *
	 * <p>Since it is generated after ZK's default tags (such as CSS and JS),
	 * it could override ZK's default behaviors.
	 *
	 * @see #getBeforeHeadTags
	 * @since 5.0.5
	 */
	public String getAfterHeadTags(Page page) {
		return getHeadTags(page, _hdAftDefs);
	}

	private String getHeadTags(Page page, List<HeaderInfo> defs) {
		if (defs == null)
			return "";

		final StringBuffer sb = new StringBuffer(256);
		for (HeaderInfo hi : defs) {
			final String s = hi.toHTML(this, page);
			if (s != null && s.length() > 0)
				sb.append(s).append('\n');
		}
		return sb.toString();
	}

	/** Adds a forward definition ({@link ForwardInfo}).
	 */
	public void addForwardInfo(ForwardInfo forward) {
		if (forward == null)
			throw new IllegalArgumentException("null");

		if (_forwdefs == null)
			_forwdefs = new LinkedList<ForwardInfo>();
		_forwdefs.add(forward);
	}

	/** Returns the URI to forward to, or null if not to forward.
	 * It evaluates the forward definition (added by {@link #addForwardInfo})
	 * one-by-one, if any, to see whether to forward.
	 * Returns null if no forward definition, or no forward definition's
	 * condition is satisfied.
	 */
	public String getForwardURI(Page page) {
		if (_forwdefs == null)
			return null;

		for (ForwardInfo fi : _forwdefs) {
			final String uri = fi.resolveURI(this, page);
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
		return _contentType != null ? (String) _contentType.getValue(_evalr, page) : null;
	}

	/** Sets the content type.
	 *
	 * <p>Default: null (use the device default).
	 *
	 * @param contentType the content type. It may contain EL expressions.
	 * @since 3.0.0
	 */
	public void setContentType(String contentType) {
		_contentType = contentType != null && contentType.length() > 0 ? new ExValue(contentType, String.class) : null;
	}

	/** Returns the doc type (&lt;!DOCTYPE&gt;) (after evaluation),
	 * or null to use the device default.
	 *
	 * @param page the page used to evaluate EL expressions, if any
	 * @since 3.0.0
	 */
	public String getDocType(Page page) {
		return _docType != null ? (String) _docType.getValue(_evalr, page) : null;
	}

	/** Sets the doc type (&lt;!DOCTYPE&gt;).
	 *
	 * <p>Default: null (use the device default).
	 *
	 * @param docType the doc type. It may contain EL expressions.
	 * If null, it means device's default will be used.
	 * If empty, it means no doc type will be generated.
	 * @since 3.0.0
	 */
	public void setDocType(String docType) {
		_docType = docType != null ? new ExValue(docType, String.class) : null;
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
		return _firstLine != null ? (String) _firstLine.getValue(_evalr, page) : null;
	}

	/** Sets the first line to be generated to the output.
	 *
	 * <p>Default: null (i.e., nothing generated)
	 * @since 3.0.0
	 */
	public void setFirstLine(String firstLine) {
		_firstLine = firstLine != null && firstLine.length() > 0 ? new ExValue(firstLine, String.class) : null;
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

	/** Returns whether to automatically redirect to the timeout URI.
	 *
	 * @see #setAutomaticTimeout
	 * @since 3.6.3
	 */
	public Boolean getAutomaticTimeout() {
		return _autoTimeout;
	}

	/** Sets whether to automatically redirect to the timeout URI.
	 *
	 * <p>Default: null (use the device default).
	 * Refer to {@link org.zkoss.zk.ui.util.Configuration#setAutomaticTimeout}
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
	 * The previous attribute of the same name will be replaced.
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
			_rootAttrs = new LinkedHashMap<String, ExValue>();
		}

		if (value == null)
			_rootAttrs.remove(name);
		else
			_rootAttrs.put(name, new ExValue(value, String.class));
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
		for (Map.Entry<String, ExValue> me : _rootAttrs.entrySet()) {
			final String val = (String) me.getValue().getValue(eval, page);
			if (val != null)
				HTMLs.appendAttribute(sb, me.getKey(), val);
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
			if (langdef != ld2 && !langdef.getDeviceType().equals(ld2.getDeviceType()))
				throw new UiException("Component definition, " + compdef
						+ ", does not belong to the same device type of the page definition, " + ld2.getDeviceType());
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
	 * @param recurse whether to look up the component from {@link #getLanguageDefinition}
	 */
	public ComponentDefinition getComponentDefinition(String name, boolean recurse) {
		ComponentDefinition compdef = _compdefs.get(name);
		if (!recurse || compdef != null)
			return compdef;

		compdef = _langdef.getComponentDefinitionIfAny(name);
		if (compdef == null)
			compdef = _langdef.getShadowDefinitionIfAny(name);
		return compdef;
	}

	/** Returns the component definition of the specified class, or null
	 * if not found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 *
	 * @param recurse whether to look up the component from {@link #getLanguageDefinition}
	 */
	public ComponentDefinition getComponentDefinition(Class cls, boolean recurse) {
		ComponentDefinition compdef = _compdefs.get(cls);
		if (!recurse || compdef != null)
			return compdef;

		compdef = _langdef.getComponentDefinitionIfAny(cls);
		if (compdef == null)
			compdef = _langdef.getShadowDefinitionIfAny(cls);
		return compdef;
	}

	/** Adds a tag lib. */
	public void addTaglib(Taglib taglib) {
		checkXelModifiable();
		if (taglib == null)
			throw new IllegalArgumentException("null");

		if (_taglibs == null)
			_taglibs = new LinkedList<Taglib>();
		_taglibs.add(taglib);
	}

	/** Adds an imported class to the expression factory.
	 * @since 3.0.0
	 */
	public void addExpressionImport(String nm, Class<?> cls) {
		checkXelModifiable();
		if (nm == null || cls == null)
			throw new IllegalArgumentException();

		if (_expimps == null)
			_expimps = new HashMap<String, Class<?>>(4);
		_expimps.put(nm, cls);
	}

	private void checkXelModifiable() {
		if (_eval != null || _mapper != null)
			throw new IllegalStateException("getEvaluator() has been called, and no further change is allowed");
	}

	/** Sets the implementation of the expression factory that shall
	 * be used by this page.
	 *
	 * <p>Default: null (use the default).
	 *
	 * @param expfcls the implementation class, or null to use the default.
	 * Note: expfcls must implement {@link ExpressionFactory}.
	 * If null is specified, the class defined in
	 * {@link org.zkoss.zk.ui.util.Configuration#getExpressionFactoryClass}
	 * @exception IllegalArgumentException if expfcls does not implement ExpressionFactory
	 * @since 3.0.0
	 */
	@SuppressWarnings("unchecked")
	public void setExpressionFactoryClass(Class<?> expfcls) {
		checkXelModifiable();
		if (expfcls != null && !ExpressionFactory.class.isAssignableFrom(expfcls))
			throw new IllegalArgumentException(ExpressionFactory.class + " must be implemented: " + expfcls);
		_expfcls = (Class) expfcls;
	}

	/** Returns the implementation of the expression factory that
	 * is used by this page, or null if
	 * {@link org.zkoss.zk.ui.util.Configuration#getExpressionFactoryClass}
	 * is used.
	 *
	 * @see #setExpressionFactoryClass
	 * @since 3.0.0
	 */
	public Class<? extends ExpressionFactory> getExpressionFactoryClass() {
		return _expfcls;
	}

	// (NodeInfo)
	public Evaluator getEvaluator() {
		if (_eval == null)
			_eval = newEvaluator();
		return _eval;
	}

	private Evaluator newEvaluator() {
		return new SimpleEvaluator(getTaglibMapper(), _expfcls);
	}

	// (NodeInfo)
	public EvaluatorRef getEvaluatorRef() {
		if (_evalr == null)
			_evalr = newEvaluatorRef();
		return _evalr;
	}

	private EvaluatorRef newEvaluatorRef() {
		return new PageEvalRef(this);
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
			_mapper = Taglibs.getFunctionMapper(_taglibs, _expimps, _xelfuncs, _locator);
			if (_mapper == null)
				_mapper = Expressions.EMPTY_MAPPER;
		}
		return _mapper != Expressions.EMPTY_MAPPER ? _mapper : null;
	}

	/** Adds a template info
	 * The previous src of the same name will be replaced.
	 *
	 * @param info the src. If null, the attribute is removed.
	 * @since 8.0.0
	 */
	public void addTemplateInfo(TemplateInfo info) {
		if (info == null)
			throw new IllegalArgumentException();

		if (_templatesInfo == null)
			_templatesInfo = new LinkedHashMap<String, TemplateInfo>();

		_templatesInfo.put(info.getName(), info);
	}

	/** Gets templates info map
	 * @since 8.0.0
	 */
	public Map<String, TemplateInfo> getTemplatesInfo() {
		return _templatesInfo;
	}

	/** Initializes the context for the given page before rendering
	 * this page definition.
	 * <p>It is called before {@link Initiator#doInit} and {@link #init}.
	 *
	 * @param page the page to initialize the context. It cannot be null.
	 */
	public void preInit(Page page) {
		page.addClassResolver(_clsresolver);

		page.addFunctionMapper(getTaglibMapper());

		if (_mapperdefs != null)
			for (Iterator it = _mapperdefs.iterator(); it.hasNext();) {
				final FunctionMapperInfo def = (FunctionMapperInfo) it.next();
				try {
					FunctionMapper mapper = def.newFunctionMapper(this, page);
					if (mapper != null)
						page.addFunctionMapper(mapper);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
				}
			}

		if (_resolvdefs != null)
			for (Iterator it = _resolvdefs.iterator(); it.hasNext();) {
				final VariableResolverInfo def = (VariableResolverInfo) it.next();
				try {
					VariableResolver resolver = def.newVariableResolver(this, page);
					if (resolver != null)
						page.addVariableResolver(resolver);
				} catch (Throwable ex) {
					throw UiException.Aide.wrap(ex);
				}
			}
	}

	/** Initializes a page after execution is activated.
	 * It setup the identifier and title, and adds it to desktop.
	 * <p>It is called after {@link #preInit} and {@link Initiator#doInit}.
	 */
	public void init(final Page page, final boolean evalHeaders) {
		final PageCtrl pageCtrl = (PageCtrl) page;
		pageCtrl.init(new PageConfig() {
			public String getId() {
				return _id;
			}

			public String getUuid() {
				return null;
			}

			public String getTitle() {
				return _title;
			}

			public String getStyle() {
				return _style;
			}

			public String getViewport() {
				return _viewport;
			}

			public String getBeforeHeadTags() {
				return evalHeaders ? PageDefinition.this.getBeforeHeadTags(page) : "";
			}

			public String getAfterHeadTags() {
				return evalHeaders ? PageDefinition.this.getAfterHeadTags(page) : "";
			}

			public Collection<Object[]> getResponseHeaders() {
				if (evalHeaders)
					return PageDefinition.this.getResponseHeaders(page);
				return Collections.emptyList();
			}
		});

		String s = getRootAttributes(page);
		if (s != null)
			pageCtrl.setRootAttributes(s);

		s = getContentType(page);
		if (s != null)
			pageCtrl.setContentType(s);

		s = getDocType(page);
		if (s != null)
			pageCtrl.setDocType(s);

		s = getFirstLine(page);
		if (s != null)
			pageCtrl.setFirstLine(s);

		s = getWidgetClass(page);
		if (s != null)
			pageCtrl.setWidgetClass(s);

		if (_cacheable != null)
			pageCtrl.setCacheable(_cacheable);
		if (_autoTimeout != null)
			pageCtrl.setAutomaticTimeout(_autoTimeout);
		if (_expfcls != null)
			page.setExpressionFactoryClass(_expfcls);
		page.setComplete(_complete);
	}

	//NodeInfo//
	/** Returns the parent (always null).
	 */
	public NodeInfo getParent() {
		return null;
	}

	/** Returns the page definition (always this).
	 */
	public PageDefinition getPageDefinition() {
		return this;
	}

	public void appendChild(NodeInfo child) {
		NodeInfo oldp = child.getParent();
		if (oldp != null)
			oldp.removeChild(child);

		_children.add(child);
		((LeafInfo) child).setParentDirectly(this); //except root, all are LeafInfo
		BranchInfo.fixEvaluatorRefDown(child, getEvaluatorRef());
		//Use getEvaluatorRef() to force _evalr being assigned
	}

	public boolean removeChild(NodeInfo child) {
		if (child != null && _children.remove(child)) {
			((LeafInfo) child).setParentDirectly(null); //except root, all are LeafInfo
			BranchInfo.fixEvaluatorRefDown(child, null);
			return true;
		}
		return false;
	}

	public List<NodeInfo> getChildren() {
		return _children;
	}

	//Object//
	public String toString() {
		return "[PageDefinition: " + (_id != null ? _id : _title != null ? _title : "" + System.identityHashCode(this))
				+ ", path=" + _path + ']';
	}
}
