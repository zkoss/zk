/* Parser.java

	Purpose:
		
	Description:
		
	History:
		Tue May 31 14:26:18     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.io.File;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import org.zkoss.idom.Attribute;
import org.zkoss.idom.CData;
import org.zkoss.idom.Comment;
import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.Item;
import org.zkoss.idom.Namespace;
import org.zkoss.idom.ProcessingInstruction;
import org.zkoss.idom.Text;
import org.zkoss.lang.ClassResolver;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Library;
import org.zkoss.lang.PotentialDeadLockException;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.resource.Locator;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.xel.util.Evaluators;
import org.zkoss.xel.util.MethodFunction;
import org.zkoss.xml.Locators;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.impl.ZScriptInitiator;
import org.zkoss.zk.ui.metainfo.impl.AnnotationHelper;
import org.zkoss.zk.ui.metainfo.impl.ComponentDefinitionImpl;
import org.zkoss.zk.ui.metainfo.impl.ShadowDefinitionImpl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.ui.util.Configuration;

/**
 * Used to parse the ZUL file
 * @author tomyeh
 */
public class Parser {
	private static final Logger log = LoggerFactory.getLogger(Parser.class);

	private final WebApp _wapp;
	private final Locator _locator;
	private final List<NamespaceParser> _nsParsers;

	/** Constructor.
	 *
	 * @param locator the locator used to locate taglib and other resources.
	 * If null, wapp is assumed ({@link WebApp} is also assumed).
	 */
	public Parser(WebApp wapp, Locator locator) {
		if (wapp == null)
			throw new IllegalArgumentException("null");
		_wapp = wapp;
		_nsParsers = wapp.getConfiguration().getNamespaceParsers();

		// Higher is the first
		Collections.sort(_nsParsers, new Comparator<NamespaceParser>() {
			public int compare(NamespaceParser o1, NamespaceParser o2) {
				int op1 = o1.getPriority();
				int op2 = o2.getPriority();
				return (op1 < op2) ? 1 : (op1 > op2) ? -1 : 0;
			}
		});
		_locator = locator != null ? locator : wapp;
	}

	/** Parses the specified file.
	 *
	 * @param path the request path.
	 * It is used as {@link org.zkoss.zk.ui.Page#getRequestPath}, or null
	 * if not available.
	 */
	public PageDefinition parse(File file, String path) throws Exception {
		//if (log.isDebugEnabled()) log.debug("Parsing "+file);
		String extension = Servlets.getExtension(file.getName());
		final PageDefinition pgdef = parse(TreeBuilderFactory.makeBuilder(extension).parse(file), extension);
		pgdef.setRequestPath(path);
		return pgdef;
	}

	/** Parses the specified URL.
	 *
	 * @param path the request path.
	 * It is used as {@link org.zkoss.zk.ui.Page#getRequestPath}, or null
	 * if not available.
	 */
	public PageDefinition parse(URL url, String path) throws Exception {
		//if (log.isDebugEnabled()) log.debug("Parsing "+url);
		String extension = Servlets.getExtension(url.toExternalForm());
		final PageDefinition pgdef = parse(TreeBuilderFactory.makeBuilder(extension).parse(url), extension);
		pgdef.setRequestPath(path);
		return pgdef;
	}

	/** Parses from the specified reader.
	 *
	 * @param extension the default extension if doc (of reader) doesn't specify
	 * an language. Ignored if null.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 */
	public PageDefinition parse(Reader reader, String extension) throws Exception {
		//if (log.isDebugEnabled()) log.debug("Parsing "+reader);
		return parse(TreeBuilderFactory.makeBuilder(extension).parse(reader), extension);
	}

	/** Parse the raw content directly from a DOM tree.
	 *
	 * @param extension the default extension if doc doesn't specify
	 * an language. Ignored if null.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 */
	public PageDefinition parse(Document doc, String extension) throws Exception {
		//1. parse the page and import directive if any
		final List<ProcessingInstruction> pis = new LinkedList<ProcessingInstruction>();
		final List<String[]> imports = new LinkedList<String[]>();
		final List<String> impclses = new LinkedList<String>();
		String lang = null;
		for (Object o : doc.getChildren()) {
			if (!(o instanceof ProcessingInstruction))
				continue;

			final ProcessingInstruction pi = (ProcessingInstruction) o;
			final String target = pi.getTarget();
			if ("page".equals(target)) {
				//we handle only the language attribute here
				final Map<String, String> params = pi.parseData();
				final String l = params.remove("language");
				if (l != null) {
					noEL("language", l, pi);
					lang = l;
				}

				if (!params.isEmpty())
					pis.add(pi); //process it later
			} else if ("import".equals(target)) { //import
				final Map<String, String> params = pi.parseData();
				final String src = params.remove("src");
				final String dirs = params.remove("directives");
				final String cls = params.remove("class");
				if (src != null) {
					noELnorEmpty("src", src, pi);
					noEL("directives", dirs, pi);
					imports.add(new String[] { src, dirs });
				}
				if (cls != null) {
					noELnorEmpty("class", cls, pi);
					impclses.add(cls);
				}
				for (Map.Entry<String, String> me : params.entrySet()) {
					final String nm = me.getKey();
					final String val = me.getValue();
					if (val == null) {
						noELnorEmpty(nm, nm, pi);
						impclses.add(nm);
					} else {
						log.warn(message("Ignored unknown attribute for import: " + nm, pi));
					}
				}
			} else {
				pis.add(pi);
			}
		}

		//2. Create PageDefinition
		final LanguageDefinition langdef = extension != null && lang == null
				? LanguageDefinition.getByExtension(extension) : LanguageDefinition.lookup(lang);
		final PageDefinition pgdef = new PageDefinition(langdef, getLocator());

		//3a. resolve imports
		if (!imports.isEmpty()) {
			final RequestInfo ri = new RequestInfoImpl(_wapp, null, null, null, getLocator());
			final UiFactory uf = ((WebAppCtrl) _wapp).getUiFactory();
			for (final String[] imprt : imports) {
				final String path = imprt[0], dirs = imprt[1];
				try {
					final PageDefinition pd = uf.getPageDefinition(ri, path);
					if (pd == null)
						throw new UiException("The imported page not found: " + path);
					pgdef.imports(pd, parseToArray(dirs));
				} catch (PotentialDeadLockException ex) {
					throw new UiException("Recursive import not allowed: " + path, ex);
				}
			}
		}
		//3b. resolve imported classes
		for (String impcls : impclses)
			pgdef.addImportedClass(impcls);

		//4. Processing the rest of processing instructions at the top level
		for (ProcessingInstruction pi : pis)
			parse(pgdef, pi);

		//5. Processing from the root element
		final Element root = doc.getRootElement();
		if (root != null)
			parseItem(pgdef, pgdef, root, new AnnotationHelper(), false, ParsingState.ROOT); //ZK-2632: Parser support disorder template tag

		return pgdef;
	}

	/** Parse processing instruction.
	 */
	private void parse(PageDefinition pgdef, ProcessingInstruction pi) throws Exception {
		final String target = pi.getTarget();
		final Map<String, String> params = pi.parseData();
		if ("page".equals(target)) {
			parsePageDirective(pgdef, pi, params);
		} else if ("init".equals(target)) {
			parseInitDirective(pgdef, pi, params);
		} else if ("variable-resolver".equals(target) || "function-mapper".equals(target)) {
			final String clsnm = params.remove("class");
			if (isEmpty(clsnm))
				throw new UiException(message("The class attribute is required", pi));

			final Map<String, String> args = new LinkedHashMap<String, String>(params);
			if ("variable-resolver".equals(target))
				pgdef.addVariableResolverInfo(new VariableResolverInfo(clsnm, args));
			else
				pgdef.addFunctionMapperInfo(new FunctionMapperInfo(clsnm, args));
		} else if ("component".equals(target)) { //declare a component
			parseComponentDirective(pgdef, pi, params);
		} else if ("taglib".equals(target)) {
			final String uri = params.remove("uri");
			final String prefix = params.remove("prefix");
			if (!params.isEmpty())
				log.warn(message("Ignored unknown attributes: " + params.keySet(), pi));
			if (uri == null || prefix == null)
				throw new UiException(message("Both uri and prefix attribute are required", pi));
			//if (log.isDebugEnabled()) log.debug("taglib: prefix="+prefix+" uri="+uri);
			noEL("prefix", prefix, pi);
			noEL("uri", uri, pi); //not support EL (kind of chicken-egg issue)
			pgdef.addTaglib(new Taglib(prefix, toAbsoluteURI(uri)));
		} else if ("evaluator".equals(target)) {
			parseEvaluatorDirective(pgdef, pi, params);
		} else if ("xel-method".equals(target)) {
			parseXelMethod(pgdef, pi, params);
		} else if ("link".equals(target) || "meta".equals(target) || "script".equals(target)
				|| "style".equals(target)) { //declare a header element
			pgdef.addHeaderInfo(new HeaderInfo(target, params,
					ConditionImpl.getInstance(params.remove("if"), params.remove("unless"))));
		} else if ("header".equals(target)) { //declare a response header
			pgdef.addResponseHeaderInfo(new ResponseHeaderInfo(params.remove("name"), params.remove("value"),
					params.remove("append"), ConditionImpl.getInstance(params.remove("if"), params.remove("unless"))));
		} else if ("root-attributes".equals(target)) {
			for (Map.Entry<String, String> stringStringEntry : pi.parseData().entrySet()) {
				final Map.Entry me = (Map.Entry) stringStringEntry;
				pgdef.setRootAttribute((String) me.getKey(), (String) me.getValue());
			}
		} else if ("forward".equals(target)) { //forward
			final String uri = params.remove("uri");
			final String ifc = params.remove("if");
			final String unless = params.remove("unless");
			if (!params.isEmpty())
				log.warn(message("Ignored unknown attributes: " + params.keySet(), pi));
			noEmpty("uri", uri, pi);
			pgdef.addForwardInfo(new ForwardInfo(uri, ConditionImpl.getInstance(ifc, unless)));
		} else if ("import".equals(target)) { //import
			throw new UiException(message("The import directive can be used only at the top level", pi));
		} else if ("template".equals(target)) { //ZK-2623: page scope template
			final String name = params.remove("name");
			final String src = params.remove("src");
			if (name != null && name.length() != 0 && src != null && src.length() != 0)
				pgdef.addTemplateInfo(new TemplateInfo(null, name, src, params, null));
		} else {
			log.warn(message("Unknown processing instruction: " + target, pi));
		}
	}

	/** Parses a list of string separated by comma, into a String array.
	 */
	private static String[] parseToArray(String s) {
		if (s == null)
			return null;
		Collection<String> ims = CollectionsX.parse(null, s, ',', false); //NO EL
		return ims.toArray(new String[ims.size()]);
	}

	//-- derive to override --//
	/** returns locator for locating resources. */
	public Locator getLocator() {
		return _locator;
	}

	/** Process the init directive. */
	private void parseInitDirective(PageDefinition pgdef, ProcessingInstruction pi, Map<String, String> params)
			throws Exception {
		final String clsnm = params.remove("class");
		final String zsrc = params.remove("zscript");

		final Map<String, String> args = new LinkedHashMap<String, String>(params);
		if (clsnm == null) {
			if (zsrc == null)
				throw new UiException(message("Either the class or zscript attribute must be specified", pi));

			checkZScriptEnabled(pi.getLocator());
			ZScript zs = null;
			final String zslang = pgdef.getZScriptLanguage();
			if (!zsrc.contains("${")) {
				final URL url = getLocator().getResource(zsrc);
				if (url != null)
					zs = new ZScript(zslang, url);
				//Bug 2929887: defer the error message since it might not be required
			}
			if (zs == null)
				zs = new ZScript(pgdef.getEvaluatorRef(), zslang, zsrc, getLocator());

			pgdef.addInitiatorInfo(new InitiatorInfo(new ZScriptInitiator(zs), args));
		} else {
			if (zsrc != null)
				throw new UiException(message("You cannot specify both class and zscript", pi));

			pgdef.addInitiatorInfo(new InitiatorInfo(clsnm, args));
		}
	}

	private static String message(String message, Item el) {
		return org.zkoss.xml.Locators.format(message, el != null ? el.getLocator() : null);
	}

	private static String message(String message, org.zkoss.xml.Locator loc) {
		return org.zkoss.xml.Locators.format(message, loc);
	}

	private static org.zkoss.util.resource.Location location(Item el) {
		return org.zkoss.xml.Locators.toLocation(el != null ? el.getLocator() : null);
	}

	private void checkZScriptEnabled(Element el) {
		checkZScriptEnabled(el.getLocator());
	}

	private void checkZScriptEnabled(org.zkoss.xml.Locator loc) {
		if (!_wapp.getConfiguration().isZScriptEnabled())
			throw new UiException(message("zscript is not allowed since <disable-zscript> is configured", loc));
	}

	/** Process the page directive. */
	private static void parsePageDirective(PageDefinition pgdef, ProcessingInstruction pi, Map<String, String> params)
			throws Exception {
		for (Map.Entry<String, String> me : pi.parseData().entrySet()) {
			final String nm = me.getKey();
			final String val = me.getValue();
			if ("language".equals(nm)) {
				if (!(pi.getParent() instanceof Document))
					log.warn(
							message("Ignored language attribute since the page directive is not at the top level", pi));
			} else if ("title".equals(nm)) {
				pgdef.setTitle(val);
			} else if ("style".equals(nm)) {
				pgdef.setStyle(val);
			} else if ("viewport".equals(nm)) {
				pgdef.setViewport(val);
			} else if ("id".equals(nm)) {
				pgdef.setId(val);
			} else if ("widgetClass".equals(nm)) {
				pgdef.setWidgetClass(val);
			} else if ("zscriptLanguage".equals(nm) || "zscript-language".equals(nm)) { //backward compatible with 2.4.x
				noELnorEmpty("zscriptLanguage", val, pi);
				pgdef.setZScriptLanguage(val);
			} else if ("cacheable".equals(nm)) {
				noELnorEmpty("cacheable", val, pi);
				pgdef.setCacheable(Boolean.valueOf("true".equals(val)));
			} else if ("automaticTimeout".equals(nm)) {
				noELnorEmpty("automaticTimeout", val, pi);
				pgdef.setAutomaticTimeout(Boolean.valueOf("true".equals(val)));
			} else if ("contentType".equals(nm)) {
				noEmpty("contentType", val, pi);
				pgdef.setContentType(val);
			} else if ("docType".equals(nm)) {
				pgdef.setDocType(isEmpty(val) ? "" : "<!DOCTYPE " + val + '>');
			} else if ("xml".equals(nm)) {
				noEmpty("xml", val, pi);
				pgdef.setFirstLine("<?xml " + val + "?>");
			} else if ("complete".equals(nm)) {
				pgdef.setComplete("true".equals(val));
			} else {
				log.warn(message("Ignored unknown attribute: " + nm, pi));
			}
		}
	}

	/** Process the component directive. */
	private void parseComponentDirective(PageDefinition pgdef, ProcessingInstruction pi, Map<String, String> params)
			throws Exception {
		final String name = params.remove("name");
		noELnorEmpty("name", name, pi);

		String macroURI = params.remove("macroURI");
		if (macroURI == null)
			macroURI = params.remove("macro-uri"); //backward compatible (2.4.x)
		final String extds = params.remove("extends");
		final String clsnm = params.remove("class");
		final String lang = params.remove("language");
		final LanguageDefinition langdef = lang != null ? LanguageDefinition.lookup(lang)
				: pgdef.getLanguageDefinition();
		ComponentDefinition compdef;
		String templateURI = params.remove("templateURI");
		if (templateURI != null) { // assume it is shadow like (<apply>)
			noEL("templateURI", templateURI, pi);
			//no EL because pagedef must be loaded to resolve
			//the implementing class before creating an instance of shadow

			compdef = langdef.getShadowDefinition(name, pgdef, toAbsoluteURI(templateURI));
			if (!isEmpty(clsnm)) {
				noEL("class", clsnm, pi);
				compdef.setImplementationClass(clsnm);
				//Resolve later since might defined in zscript
			}
		} else if (macroURI != null) {
			//if (log.finerable()) log.finer("macro component definition: "+name);

			final String inline = params.remove("inline");
			noEL("inline", inline, pi);
			noEL("macroURI", macroURI, pi);
			//no EL because pagedef must be loaded to resolve
			//the implementing class before creating an instance of macro

			final boolean bInline = "true".equals(inline);
			compdef = langdef.getMacroDefinition(name, toAbsoluteURI(macroURI), bInline, pgdef);
			if (!isEmpty(clsnm)) {
				if (bInline)
					throw new UiException(message("class not allowed with inline macros", pi));
				noEL("class", clsnm, pi);
				compdef.setImplementationClass(clsnm);
				//Resolve later since might defined in zscript
			}
		} else {
			ComponentDefinition ref = null;
			if (extds != null) { //extends
				//if (log.finerable()) log.finer("Override component definition: "+name);

				noEL("extends", extds, pi);
				ref = langdef.getComponentDefinition(extds);
			} else {
				final Class cls = pgdef.getImportedClassResolver().resolveClass(clsnm);
				if (lang != null) {
					ref = langdef.getComponentDefinitionIfAny(cls);
				} else {
					ref = pgdef.getComponentDefinition(cls, true);
					if (ref == null) //return null if not found
						ref = Components.getDefinitionByDeviceType(langdef.getDeviceType(), cls);
				}
			}

			if (ref != null) {
				if (ref.isMacro())
					throw new UiException(message("Unable to extend from a macro component", pi));

				compdef = ref.clone(null, name);
				if (!isEmpty(clsnm)) {
					noEL("class", clsnm, pi);
					compdef.setImplementationClass(clsnm);
					//Resolve later since might defined in zscript
				}
			} else {
				//if (log.finerable()) log.finer("Add component definition: name="+name);

				noELnorEmpty("class", clsnm, pi);

				final ComponentDefinitionImpl cdi = new ComponentDefinitionImpl(null, pgdef, name,
						(Class<? extends Component>) null);
				cdi.setCurrentDirectory(getLocator().getDirectory());
				//mold URI requires it
				compdef = cdi;
				compdef.setImplementationClass(clsnm);
				//Resolve later since might be defined in zscript
			}
		}

		String wgtnm = params.remove("widgetClass");
		if (wgtnm == null)
			wgtnm = params.remove("widget-class");
		if (wgtnm != null)
			compdef.setDefaultWidgetClass(wgtnm);

		pgdef.addComponentDefinition(compdef);

		Object o = params.remove("moldURI");
		if (o == null)
			o = params.remove("mold-uri");
		if (o != null)
			throw new UnsupportedOperationException(
					message("moldURI not supported in 5.0. Use <?script?> or lang-addon.xml instead", pi));

		o = params.remove("cssURI");
		if (o != null)
			throw new UnsupportedOperationException(
					message("cssURI not supported in 5.0. Use <?link?> or lang-addon.xml instead", pi));

		compdef.setApply(params.remove("apply"));

		for (Map.Entry<String, String> me : params.entrySet()) {
			compdef.addProperty(me.getKey(), me.getValue());
		}
	}

	/** Parse the evaluator directive. */
	private static void parseEvaluatorDirective(PageDefinition pgdef, ProcessingInstruction pi,
			Map<String, String> params) throws Exception {
		final String clsnm = params.remove("class");
		if (clsnm != null && clsnm.length() > 0) {
			noELnorEmpty("class", clsnm, pi);
			pgdef.setExpressionFactoryClass(pgdef.getImportedClassResolver().resolveClass(clsnm));
		} else { //name has the lower priority
			final String nm = params.remove("name");
			if (nm != null)
				pgdef.setExpressionFactoryClass(Evaluators.getEvaluatorClass(nm));
		}

		final String imports = params.remove("import");
		if (imports != null && imports.length() > 0) {
			Collection<String> ims = CollectionsX.parse(null, imports, ',', false); //No EL
			for (String im : ims) {
				final int k = im.indexOf('=');
				String nm = k > 0 ? im.substring(0, k).trim() : null;
				String cn = (k >= 0 ? im.substring(k + 1) : im).trim();

				if (cn.length() != 0) {
					if (nm == null || nm.length() == 0) {
						final int j = cn.lastIndexOf('.');
						nm = j >= 0 ? cn.substring(j + 1) : cn;
					}
					pgdef.addExpressionImport(nm, Classes.forNameByThread(cn));
					//evaluator's import does not support <?import class?>,
					//since it looks strange.
					//FUTURE: it is better to deprecate this attribute, and
					//have FunctionMapperExt to depend on <?import class?>
					//(however, it is worth since only MVEL/OGNL uses it)
				}
			}
		}
	}

	/** Parse the XEL method. */
	private static void parseXelMethod(PageDefinition pgdef, ProcessingInstruction pi, Map<String, String> params)
			throws Exception {
		final String prefix = params.remove("prefix");
		noELnorEmpty("prefix", prefix, pi);
		final String nm = params.remove("name");
		noELnorEmpty("name", nm, pi);
		final String clsnm = params.remove("class");
		noELnorEmpty("class", clsnm, pi);
		final String sig = params.remove("signature");
		noELnorEmpty("signature", sig, pi);

		final Method mtd;
		try {
			final ClassResolver clsresolver = pgdef.getImportedClassResolver();
			final Class cls = clsresolver.resolveClass(clsnm);
			mtd = Classes.getMethodBySignature(cls, sig, null, clsresolver);
		} catch (ClassNotFoundException ex) {
			throw new UiException(message("Class not found: " + ex.getMessage(), pi));
		} catch (Exception ex) {
			throw new UiException(message("Method not found: " + sig + " in " + clsnm, pi));
		}
		if ((mtd.getModifiers() & Modifier.STATIC) == 0)
			throw new UiException(message("Not a static method: " + mtd, pi));

		pgdef.addXelMethod(prefix, nm, new MethodFunction(mtd));
	}

	private static void noELnorEmpty(String nm, String val, Item item) throws UiException {
		if (isEmpty(val))
			throw new UiException(message(nm + " cannot be empty", item));
		noEL(nm, val, item);
	}

	private static void noEL(String nm, String val, Item item) throws UiException {
		if (val != null && val.contains("${"))
			throw new UiException(message(nm + " does not support EL expressions", item));
	}

	/** Checks whether the value is an empty string.
	 * Note: Like {@link #noEL}, it is OK to be null!!
	 */
	private static void noEmpty(String nm, String val, Item item) throws UiException {
		if (val != null && val.length() == 0)
			throw new UiException(message(nm + " cannot be empty", item));
	}

	private String toAbsoluteURI(String uri) {
		if (uri != null && uri.length() > 0) {
			final char cc = uri.charAt(0);
			if (cc != '/' && cc != '~' && !Servlets.isUniversalURL(uri)) {
				final String dir = getLocator().getDirectory();
				if (dir != null && dir.length() > 0)
					return dir.charAt(dir.length() - 1) == '/' ? dir + uri : dir + '/' + uri;
			}
		}
		return uri;
	}

	/** Parses the specified elements.
	 * @param bNativeContent whether to consider the child element all native
	 * It is true if a component definition with text-as is found
	 */
	private void parseItems(final PageDefinition pgdef, final NodeInfo parent, Collection items,
			AnnotationHelper annHelper, boolean bNativeContent) throws Exception {
		LanguageDefinition parentlang = getLanguageDefinition(parent);
		if (parentlang == null)
			parentlang = pgdef.getLanguageDefinition();
		final boolean bZkSwitch = isZkSwitch(parent);

		ComponentInfo pi = null;
		String textAs = null;
		StringBuilder textAsBuffer = null;
		for (NodeInfo p = parent; p != null; p = p.getParent())
			if (p instanceof ComponentInfo) {
				pi = (ComponentInfo) p;
				textAs = pi.getTextAs();
				if (textAs != null && pi == parent) //only direct child
					textAsBuffer = new StringBuilder();
				break; //found
			}
		final boolean isXHTML = "xhtml".equals(parentlang.getName());
		final boolean isAllBlankPreserved = !"false".equals(Library.getProperty("preserve-all-blank"));
		boolean breakLine = false;
		NativeInfo preNativeInfo = null; //check if brother is native

		//ZK-2632: Parser support disorder template tag
		for (final Object o : items) {
			if (o instanceof Element)
				parseItem(pgdef, parent, (Element) o, annHelper, bNativeContent, ParsingState.FIRST);
		}

		for (final Object o : items) {
			if (o instanceof Element) {
				breakLine = false;
				preNativeInfo = (NativeInfo) parseItem(pgdef, parent, (Element) o, annHelper, bNativeContent,
						ParsingState.SECOND);
			} else if (o instanceof ProcessingInstruction) {
				breakLine = false;
				parse(pgdef, (ProcessingInstruction) o);
			} else if (o instanceof Comment) {
				breakLine = false;
				// keep the comment, like <!--[if lte IE 9]>
				if (parentlang.isNative() || isXHTML) {
					String label = "<!--" + ((Item) o).getText() + "-->";
					final ComponentInfo labelInfo = parentlang.newLabelInfo(parent, label);
					labelInfo.addProperty("encode", "false", null);
				}
			} else if ((o instanceof Text) || (o instanceof CData)) {
				String label = ((Item) o).getText(), trimLabel = !isXHTML ? label.trim() : label; // do not trim with xhtml
				if (breakLine && (o instanceof Text) && isEmpty(label.trim())) {
					// we need to merge the breakLine into the previous one to save memory
					List<NodeInfo> children = parent.getChildren();
					final String labelAttr = parentlang.getLabelAttribute();
					for (Property prop : ((ComponentInfo) children.get(children.size() - 1)).getProperties()) {
						if (prop.getName().equals(labelAttr)) {
							prop.setRawValue(prop.getRawValue() + trimLabel);
						}
					}
					continue;
				}
				if (isEmpty(label))
					continue;

				if (bZkSwitch) {
					if (isEmpty(trimLabel))
						continue;
					throw new UiException(message("Only <zk> can be used in <zk switch>", (Item) o));
				}

				//ZK-2760: if parent is xhtml and no other sibling and length of label is 0, don't create TextInfo to it
				if (isXHTML && items.size() <= 1 && isEmpty(trimLabel.trim())) {
					continue; //don't create Text
				} else if (isEmpty(trimLabel) && (pi != null && !pi.isBlankPreserved() && !isNativeText(pi)))
					continue; //Ignore blank text if no need to preserved
				else if (isEmpty(label.trim()) && !isAllBlankPreserved)
					continue;

				if (!isXHTML && (o instanceof Text) && isEmpty(label.trim()))
					breakLine = true;

				//consider as a label
				if (isNativeText(pi)) {

					// trim the cdata when in the XHTML parser which will retain the syntax
					String newLabel = label.trim();
					if (newLabel.startsWith("<![CDATA[") && newLabel.endsWith("]]>")) {
						label = newLabel.substring(9, newLabel.length() - 3);
					}
					// Bug ZK-2845: ShadowInfo does not allow TextInfo as child
					if (!(parent instanceof ShadowInfo)) {
						// It's possible to replace multiple whitespace characters with single space
						new TextInfo(parent, isEmpty(trimLabel) ? " " : label);
					}
					//Don't trim if native (3.5.0)
				} else {
					if (textAs != null) { //implies pi != null (parent is ComponentInfo)
						if (!isEmpty(trimLabel))
							if (textAsBuffer != null) //implies pi == parent
								textAsBuffer.append(label); //concatenate all together
							else if (!(parent instanceof TemplateInfo))
								throw new UnsupportedOperationException(
										message("Not allowed in text-as", ((Item) o).getParent()));
					} else { // shadow element shouldn't support empty LabelInfo
						if (parent instanceof ShadowInfo) {
							if (isEmpty(trimLabel))
								continue; //ignore
						}
						if (isTrimLabel() && !parentlang.isRawLabel()) {
							if (isEmpty(trimLabel))
								continue; //ignore
							label = trimLabel;
						}
						if (isXHTML && preNativeInfo != null && isEmpty(label.trim())) { //Merge break line between two NativeInfo when using zhtml
							preNativeInfo.addEpilogChild(new TextInfo(null, label));
						} else {
							final ComponentInfo labelInfo = parentlang.newLabelInfo(parent, label);
							if (isEmpty(trimLabel))
								labelInfo.setReplaceableText(" "); //yes, it can be replaced by a text, and it's possible to replace multiple whitespace characters with single space
						}
					}
				}
			} else {
				breakLine = false;
			}
		}

		if (textAsBuffer != null) { //parent might be TempalteInfo
			String trimLabel = textAsBuffer.toString();

			// Bug ZK-1911
			if (pi == null || !pi.isBlankPreserved())
				trimLabel = trimLabel.trim();

			if (!isEmpty(trimLabel))
				pi.addProperty(textAs, trimLabel, null);
		}
	}

	/*package*/ static boolean isNativeText(ComponentInfo pi) { //also called by ComponentInfo
		if (pi instanceof NativeInfo)
			return true;

		if (pi != null) {
			try {
				final Class cls = pi.resolveImplementationClass(null, null);
				return cls != null && Native.class.isAssignableFrom(cls);
			} catch (Throwable ex) { //ignore
			}
		}
		return false;
	}

	/** Returns whether to trim the leading and trailing whitespaces
	 * of labels.
	 * <p>Default: false since 3.0.4.
	 *
	 * <p>If you want to trim like 3.0.4 and earlier did, you can specify
	 * the system property called "org.zkoss.zk.ui.parser.trimLabel"
	 * with a non-empty value.
	 */
	private static boolean isTrimLabel() {
		if (_trimLabel == null) {
			final String s = Library.getProperty("org.zkoss.zk.ui.parser.trimLabel");
			_trimLabel = Boolean.valueOf(s != null && s.length() > 0);
		}
		return _trimLabel.booleanValue();
	}

	private static Boolean _trimLabel;

	private static final LanguageDefinition getLanguageDefinition(NodeInfo node) {
		for (; node != null; node = node.getParent()) {
			if (node instanceof ComponentInfo) {
				LanguageDefinition langdef = ((ComponentInfo) node).getLanguageDefinition();
				if (langdef != null)
					return langdef;
			} else if (node instanceof PageDefinition) {
				return ((PageDefinition) node).getLanguageDefinition();
			} else if (node instanceof ShadowInfo) {
				return ((ShadowInfo) node).getLanguageDefinition();
			}
		}
		return null;
	}


	/** Parse an component definition specified in the given element.
	 * @param bNativeContent whether to consider the child elements all native
	 * It is true if a component definition with text-as is found
	 * @return native item for optimization
	 */
	private Object parseItem(PageDefinition pgdef, NodeInfo parent, Element el, AnnotationHelper annHelper,
			boolean bNativeContent, ParsingState parsingState) throws Exception {
		final String nm = el.getLocalName();
		final Namespace ns = el.getNamespace();
		final String pref = ns != null ? ns.getPrefix() : "";
		final String uri = ns != null ? ns.getURI() : "";
		LanguageDefinition langdef = pgdef.getLanguageDefinition();
		final String langName = langdef.getName();

		if (LanguageDefinition.ANNOTATION_NAMESPACE.equals(uri) || "annotation".equals(uri))
			throw new UiException(message("Namespace, " + uri + ", no longer supported element's annotation", el));

		//ZK-2632: Parser support disorder template tag
		if (parsingState != ParsingState.SECOND) {
			if ("attribute".equals(nm) && isZkElement(langdef, nm, pref, uri, bNativeContent)) {
				if (!(parent instanceof ComponentInfo))
					throw new UiException(message("<attribute> cannot be the root element", el));
				parseAttribute(pgdef, (ComponentInfo) parent, el, annHelper);
			} else if ("template".equals(nm) && isZkElement(langdef, nm, pref, uri, bNativeContent)) {
				parseItems(pgdef, parseTemplate(parent, el, annHelper), el.getChildren(), annHelper, bNativeContent);
			}
		}

		if (parsingState != ParsingState.FIRST) {
			if (("attribute".equals(nm) || "template".equals(nm)) && isZkElement(langdef, nm, pref, uri))
				return null;
			if ("zscript".equals(nm) && isZkElement(langdef, nm, pref, uri)) {
				checkZScriptEnabled(el);
				parseZScript(parent, el, annHelper);
			} else if ("custom-attributes".equals(nm) && isZkElement(langdef, nm, pref, uri, bNativeContent)) {
				parseCustomAttributes(langdef, parent, el, annHelper);
			} else if ("variables".equals(nm) && isZkElement(langdef, nm, pref, uri, bNativeContent)) {
				parseVariables(langdef, parent, el, annHelper);
			} else if ("zk".equals(nm) && isZkElement(langdef, nm, pref, uri)) {
				parseItems(pgdef, parseZk(parent, el, annHelper), el.getChildren(), annHelper, bNativeContent);
			} else if (isShadowElement(langdef, pgdef, nm, pref, uri, bNativeContent)) {
				NodeInfo nodeInfo = parseShadowElement(pgdef, parent, el, annHelper);
				parseItems(pgdef, nodeInfo, el.getChildren(), annHelper, bNativeContent);
				if (nodeInfo instanceof ShadowInfo && ((ShadowInfo) nodeInfo).isAnnotationNamespacedRoot()) {
					annHelper.setIgnoreAnnotNamespace(false);
				} else if (nodeInfo instanceof TemplateInfo && ((TemplateInfo) nodeInfo).isAnnotationNamespacedRoot()) {
					annHelper.setIgnoreAnnotNamespace(false);
				}
			} else {
				//if (log.isDebugEnabled()) log.debug("component: "+nm+", ns:"+ns);
				if (isZkSwitch(parent))
					throw new UiException(message("Only <zk> can be used in <zk switch>", el));

				boolean prefRequired = uri.startsWith(LanguageDefinition.NATIVE_NAMESPACE_PREFIX);
				boolean bNative = bNativeContent || prefRequired || LanguageDefinition.NATIVE_NAMESPACE.equals(uri)
						|| "native".equals(uri);

				if (!bNative && langdef.isNative() && !langdef.getNamespace().equals(uri))
					bNative = prefRequired = ("".equals(pref) && "".equals(uri)) || !LanguageDefinition.exists(uri);
				//Spec: if pref/URI not specified => native
				//		if uri unknown => native

				final ComponentInfo compInfo;
				if (bNative) {
					if (annHelper.clear())
						log.warn(message("Annotations are ignored since native doesn't support them", el));

					final NativeInfo ni;
					compInfo = ni = new NativeInfo(parent, langdef.getNativeDefinition(),
							prefRequired && pref.length() > 0 ? pref + ":" + nm : nm);

					//add declared namespace if starting with native:
					final Collection<Namespace> dns = el.getDeclaredNamespaces();
					if (!dns.isEmpty())
						addDeclaredNamespace(ni, dns, langdef);
				} else {
					final boolean defaultNS = isDefaultNS(langdef, pref, uri);
					final LanguageDefinition complangdef = defaultNS ? langdef : LanguageDefinition.lookup(uri);
					ComponentDefinition compdef = defaultNS ? pgdef.getComponentDefinitionMap().get(nm) : null;
					if (compdef != null) {
						compInfo = new ComponentInfo(parent, compdef, nm);
					} else if (complangdef.hasComponentDefinition(nm)) {
						compdef = complangdef.getComponentDefinition(nm);
						compInfo = new ComponentInfo(parent, compdef, nm);
						langdef = complangdef;
					} else {
						compdef = complangdef.getDynamicTagDefinition();
						if (compdef == null)
							throw new DefinitionNotFoundException(
									message("Component definition not found: " + nm + " in " + complangdef, el));
						compInfo = new ComponentInfo(parent, compdef, nm);
						langdef = complangdef;
					}

					//process use first because addProperty needs it
					String use = el.getAttributeValue("use");
					if (use != null) {
						use = use.trim();
						if (use.length() != 0)
							compInfo.setImplementation(use);
						//Resolve later since might defined in zscript
					}
				}

				String ifc = null, unless = null, forEach = null, forEachBegin = null, forEachEnd = null,
						forEachStep = null;
				AnnotationHelper attrAnnHelper = null;
				//ZK 8: If the attribute of viewModel being used, auto apply "BindComposer"
				boolean isMVVM = false;
				boolean shouldIgnoreAnnotNamespace = annHelper.shouldIgnoreAnnotNamespace();
				for (final Attribute attr : el.getAttributeItems()) {
					final Namespace attrns = attr.getNamespace();
					final String attURI = attrns != null ? attrns.getURI() : "";
					final String attnm = attr.getLocalName();
					final String attval = attr.getValue();
					final String attPref = attrns != null ? attrns.getPrefix() : "";

					// ZK-2494: use getName instead of getLocalName when namespace is native or xml and attribute uri 
					// is not one of known namespaces in zk. Exclude xmlns to avoid redefine.
					if (isNativeNamespace(uri) || isXmlNamespace(uri) || "native".equals(langName)
							|| "xml".equals(langName)) {
						if (!isZkAttr(langdef, attrns) && !isZKNamespace(attURI) && !"xmlns".equals(attPref)
								&& !("xmlns".equals(attnm) && "".equals(attPref))
								&& !"http://www.w3.org/2001/XMLSchema-instance".equals(attURI)) {

							// Bug ZK-2995
							boolean handled = false;
							for (NamespaceParser nsParser : _nsParsers) {
								if (nsParser.isMatched(attURI)) {
									if (nsParser.parse(attr, compInfo, pgdef)) {
										handled = true;
										break;
									}
								}
							}
							if (!handled) {
								compInfo.addProperty(attr.getName(), attval, null);
							}
							continue;
						} else if (isClientNamespace(attURI) || isClientAttrNamespace(attURI)) {
							compInfo.addProperty(attnm, attval, null);
							continue;
						} else if (isClientAttrPrefixNamespace(attURI)) { // ZK-4474
							compInfo.addProperty(attPref + ":" + attnm, attval, null);
							continue;
						}
					}

					if ("apply".equals(attnm) && isZkAttr(langdef, attrns)) {
						compInfo.setApply(attval);
					} else if ("forward".equals(attnm) && isZkAttr(langdef, attrns)) {
						compInfo.setForward(attval);
					} else if ("if".equals(attnm) && isZkAttr(langdef, attrns)) {
						ifc = attval;
					} else if ("unless".equals(attnm) && isZkAttr(langdef, attrns)) {
						unless = attval;
					} else if ("forEach".equals(attnm) && isZkAttr(langdef, attrns)) {
						forEach = attval;
					} else if ("forEachStep".equals(attnm) && isZkAttr(langdef, attrns)) {
						forEachStep = attval;
					} else if ("forEachBegin".equals(attnm) && isZkAttr(langdef, attrns)) {
						forEachBegin = attval;
					} else if ("forEachEnd".equals(attnm) && isZkAttr(langdef, attrns)) {
						forEachEnd = attval;
					} else if ("fulfill".equals(attnm) && isZkAttr(langdef, attrns, bNativeContent)) {
						compInfo.setFulfill(attval);
					} else if (!shouldIgnoreAnnotNamespace && (LanguageDefinition.ANNOTATION_NAMESPACE.equals(attURI) || "annotation".equals(attURI))) {
						//ZK 6: annotation namespace mandates annotation
						if (attrAnnHelper == null)
							attrAnnHelper = new AnnotationHelper();
						applyAttrAnnot(attrAnnHelper, compInfo, attnm, attval.trim(), true, location(attr));
					} else if (!"use".equals(attnm) || !isZkAttr(langdef, attrns, bNativeContent)) {
						if (!"xmlns".equals(attPref) && !("xmlns".equals(attnm) && "".equals(attPref))
								&& !"http://www.w3.org/2001/XMLSchema-instance".equals(attURI)) {
							String attvaltrim = attval.trim();
							if ("viewModel".equals(attnm)) {
								if (attval.indexOf("@id") == -1) {
									attvaltrim = "@id('vm') " + attvaltrim;
									attvaltrim = attvaltrim.replace("@(", "@init(");
								}
							} else {
								attvaltrim = modifyAttrValueIfSimplified(attnm, attval.trim());
							}
							if (!bNativeContent && !bNative && (shouldIgnoreAnnotNamespace
									|| (attURI.length() == 0 || LanguageDefinition.ZK_NAMESPACE.endsWith(attURI)))
									&& AnnotationHelper.isAnnotation(attvaltrim)) {
								if (attrAnnHelper == null)
									attrAnnHelper = new AnnotationHelper();
								applyAttrAnnot(attrAnnHelper, compInfo, attnm, attvaltrim, true, location(attr));
								//ZK 8: If the attribute of viewModel being used, auto apply "BindComposer"
								Configuration config = WebApps.getCurrent().getConfiguration();
								if (config.getBinderInitAttribute().equals(attnm))
									isMVVM = true;
								//F80 - store subtree's binder annotation count
								Set<String> binderAnnotations = config.getBinderAnnotations();
								for (String annot : binderAnnotations) {
									if (attvaltrim.contains(annot)) {
										compInfo.enableBindingAnnotation();
										break;
									}
								}
							} else {
								boolean handled = false;
								for (NamespaceParser nsParser : _nsParsers) {
									if (nsParser.isMatched(attURI)) {
										if (nsParser.parse(attr, compInfo, pgdef)) {
											handled = true;
											break;
										}
									}
								}
								if (!handled) {
									addAttribute(compInfo, attrns, attnm, attval, null, attr.getLocator());
									if (attrAnnHelper != null)
										attrAnnHelper.applyAnnotations(compInfo, attnm, true);
								}
							}
						}
					}
				}

				//ZK 8: If the attribute of viewModel being used, auto apply "BindComposer"
				if (isMVVM) {
					String apply = compInfo.getApply();
					String bindComposerStr = Library.getProperty("org.zkoss.bind.defaultComposer.class", "org.zkoss.bind.BindComposer");
					if (apply != null && apply.contains(bindComposerStr) && !apply.contains(",")) {
						log.warn(message("If the attribute of viewModel is being used, then \"" + bindComposerStr
								+ "\" will be applied automatically. "
								+ "Notice that if you want to use another Composer, you will need to apply the \""
								+ bindComposerStr + "\" yourself.", el));
					} else if (apply == null || apply.length() == 0) { //ZK-2763: only auto apply "BindComposer" when the attribute "apply" isn't used.
						compInfo.setApply(bindComposerStr);
					}
				}
				compInfo.setCondition(ConditionImpl.getInstance(ifc, unless));
				compInfo.setForEach(forEach, forEachBegin, forEachEnd, forEachStep);
				annHelper.applyAnnotations(compInfo, null, true);

				//only provide if there already has other annotation
				if (compInfo.getAnnotationMap() != null && el.getLocator() != null) {
					//provide component location info as a annotation with it's location.
					compInfo.addAnnotation(null, "ZKLOC", null, Locators.toLocation(el.getLocator()));
				}

				final Collection<Item> items = el.getChildren();
				String textAs = null;
				if (!bNativeContent && !items.isEmpty() //if empty, no need to check
						&& (textAs = compInfo.getTextAs()) != null) {
					//if textAs is specified, we detect if any child element
					//if so, we parse them as property
					//if not, we handle it normally and text, if any, will be
					//trimmed and assigned as a property (in parseItems)
					if (compInfo.isChildAllowedInTextAs() //don't consider it as text if childable and element found
							|| !textAsAllowed(langdef, items, bNativeContent))
						textAs = null;
				}

				if (textAs != null)
					parseAsProperty(pgdef, compInfo, textAs, items, annHelper, null);
				else
					parseItems(pgdef, compInfo, items, annHelper, bNativeContent);

				//optimize native components
				if (compInfo instanceof NativeInfo && !compInfo.getChildren().isEmpty()) {
					optimizeNativeInfos((NativeInfo) compInfo);
					return compInfo;
				}
			} //end-of-else//
		}
		return null;
	}

	private static String modifyAttrValueIfSimplified(String attrName, String attrValue) {
		boolean isCommand = attrValue.matches("@(global-)?command\\(.*\\)");
		if (attrValue.matches("@\\(.*\\)") || isCommand) {
			if (Events.isValid(attrName) || isCommand) {
				int attrValueIndex = attrValue.indexOf("(") + 1;
				String commandProperty = attrValue.substring(attrValueIndex, attrValue.length() - 1);
				boolean isNamedParam = false;
				int len = commandProperty.length();
				final StringBuilder modifiedCommandPropertySb = new StringBuilder(len);
				final StringBuilder sb = new StringBuilder(len);
				String nm = null;
				char quot = (char) 0;
				int paramIndex = -1;
				int inRoundBrackets = 0;
				for (int j = 0;; ++j) {
					if (j >= len) {
						modifiedCommandPropertySb.append(modifyAttrValueIfSimplified0(nm, sb.toString().trim(), paramIndex, isNamedParam));
						break; //done
					}
					char cc = commandProperty.charAt(j);
					if (quot == (char) 0) {
						if (cc == ',') {
							sb.append(cc);
							modifiedCommandPropertySb.append(modifyAttrValueIfSimplified0(nm, sb.toString().trim(), paramIndex, isNamedParam));
							if (paramIndex != 0 && !isNamedParam && nm != null) { // named param and un-named parameters together
								throwCommandSimplifiedErrorUsage();
							}
							isNamedParam = nm != null;
							nm = null; //cleanup
							sb.setLength(0); //cleanup
							paramIndex++;
							continue; //next name=value
						} else if (cc == '=' && inRoundBrackets == 0) {
							nm = sb.toString().trim(); //name found
							sb.setLength(0); //cleanup
							continue; //parse value
						} else if (cc == '\'' || cc == '"') {
							quot = cc;
						} else if (cc == '(') {
							inRoundBrackets++;
						} else if (cc == ')') {
							inRoundBrackets--;
						}
					} else if (cc == quot) {
						quot = (char) 0;
					}
					sb.append(cc);
					if (cc == '\\' && j < len - 1)
						sb.append(commandProperty.charAt(++j));
					//Note: we don't decode \x. Rather, we preserve it such
					//that the data binder can use them
				}
				if (!isCommand) {
					attrValue = "@command(" + modifiedCommandPropertySb + ")";
				} else {
					attrValue = attrValue.substring(0, attrValueIndex) + modifiedCommandPropertySb + ")";
				}
			} else
				attrValue = "@bind" + attrValue.substring(1);
		}
		return attrValue;
	}

	public static final String SIMPLIFIED_COMMAND_PARAM_PREFIX = "zk_Param_";

	private static String modifyAttrValueIfSimplified0(String nm, String val, int paramIndex, boolean isNamedParam) {
		if (nm == null) {
			if (isNamedParam)
				throwCommandSimplifiedErrorUsage();
			if (paramIndex != -1) //skip command method name
				val = SIMPLIFIED_COMMAND_PARAM_PREFIX + paramIndex + "=" + val;
		} else {
			val = nm + "=" + val;
		}
		return val;
	}

	private static void throwCommandSimplifiedErrorUsage() {
		throw new UiException("Not allowed to use named parameters and un-named parameters together.");
	}

	private boolean textAsAllowed(LanguageDefinition langdef, Collection<Item> items, boolean bNativeContent) {
		boolean textAsAllowed = true;
		String xmlFound = null; //whether a XML fragment
		String zkElem = null; //a ZK element
		boolean empty = true; //whether there is anything other than whitespace
		for (Iterator<Item> it = items.iterator();;) {
			if (zkElem != null && xmlFound != null)
				throw new UnsupportedOperationException(message(
						"Unable to handle XML fragment, <" + xmlFound + ">, with <" + zkElem
								+ ">. Please use CDATA instead",
						(it.hasNext() ? it.next() : items.iterator().next().getParent())));
			//might be possible to handle but not worth
			if (!it.hasNext())
				break;

			final Item o = it.next();
			if (empty)
				empty = (o instanceof Text || o instanceof CData) && o.getText().trim().length() == 0;

			if (o instanceof Element) {
				final Element e = (Element) o;
				final String n = e.getLocalName();
				if (isZkElement(langdef, e, bNativeContent) && ("attribute".equals(n) || "custom-attributes".equals(n)
						|| "variables".equals(n) || "template".equals(n) || "zscript".equals(n))) { //we have to skip zscript because of B50-3259479
					zkElem = n;
					textAsAllowed = false;
					//unable to handle them because EL/zscript might affect
					//the result
				} else {
					xmlFound = n;
					//including "zk" (risk if allowed to mix zk with attribute...)
				}
			}
		}
		return textAsAllowed && !empty;
		//if empty (i.e., only whitespace), so don't handle textAs (i.e., ignore it)
	}

	/** Parses the items as if they are native and they will become a property
	 * rather than child components.
	 */
	private void parseAsProperty(PageDefinition pgdef, ComponentInfo compInfo, String name, Collection items,
			AnnotationHelper annHelper, ConditionImpl cond) throws Exception {
		final NativeInfo nativeInfo = new NativeInfo(compInfo.getEvaluatorRef(),
				pgdef.getLanguageDefinition().getNativeDefinition(), "");
		//Note: nativeInfo can not be a child. Rather, it will be a property
		parseItems(pgdef, nativeInfo, items, annHelper, true);
		compInfo.addProperty(name, nativeInfo, cond);
	}

	/** @param val the value (it was trimmed before called). */
	private static void applyAttrAnnot(AnnotationHelper attrAnnHelper, ComponentInfo compInfo, String nm, String val,
			boolean selfAllowed, org.zkoss.util.resource.Location loc) {
		attrAnnHelper.addByCompoundValue(val.trim(), loc);
		attrAnnHelper.applyAnnotations(compInfo, selfAllowed && "self".equals(nm) ? null : nm, true);
	}

	/** @param val the value (it was trimmed before called). */
	private static void applyAttrAnnot(AnnotationHelper attrAnnHelper, ShadowInfo compInfo, String nm, String val,
			boolean selfAllowed, org.zkoss.util.resource.Location loc) {
		attrAnnHelper.addByCompoundValue(val.trim(), loc);
		attrAnnHelper.applyAnnotations(compInfo, selfAllowed && "self".equals(nm) ? null : nm, true);
	}

	private static void warnWrongZkAttr(Attribute attr) {
		log.warn(message("Attribute " + attr.getName() + " ignored in <zk>", attr));
	}

	private static boolean isZkSwitch(NodeInfo nodeInfo) {
		return nodeInfo instanceof ZkInfo && ((ZkInfo) nodeInfo).withSwitch();
	}

	private void parseZScript(NodeInfo parent, Element el, AnnotationHelper annHelper) {
		if (el.getAttributeItem("forEach") != null)
			throw new UiException(message("forEach not applicable to <zscript>", el));
		if (annHelper.clear())
			log.warn(message("Annotations are ignored since <zscript> doesn't support them", el));

		final String ifc = el.getAttributeValue("if"), unless = el.getAttributeValue("unless"),
				zsrc = el.getAttributeValue("src");
		final boolean deferred = "true".equals(el.getAttributeValue("deferred"));

		String zslang = el.getAttributeValue("language");
		if (zslang == null) {
			zslang = parent.getPageDefinition().getZScriptLanguage();
			//we have to resolve it in parser since a page might be
			//created by use of createComponents
		} else {
			noEmpty("language", zslang, el);
			noEL("language", zslang, el);
		}

		final ConditionImpl cond = ConditionImpl.getInstance(ifc, unless);
		if (!isEmpty(zsrc)) { //ignore empty (not error)
			ZScriptInfo zs = null;
			if (!zsrc.contains("${")) {
				final URL url = getLocator().getResource(zsrc);
				if (url != null)
					zs = new ZScriptInfo(parent, zslang, url, cond);
				//Bug 2929887: defer the error message since it might not be required
			}
			if (zs == null)
				zs = new ZScriptInfo(parent, zslang, zsrc, getLocator(), cond);

			if (deferred)
				zs.setDeferred(true);
		}

		String script = el.getText(false);
		if (!isEmpty(script.trim())) {
			final org.zkoss.xml.Locator l = el.getLocator();
			int lno = l != null ? l.getLineNumber() : 0;
			if (lno > 1) {
				final StringBuilder sb = new StringBuilder(lno);
				while (--lno > 0)
					sb.append('\n');
				script = sb.append(script).toString();
			}
			final ZScriptInfo zs = new ZScriptInfo(parent, zslang, script, cond);
			if (deferred)
				zs.setDeferred(true);
		}
	}

	private void parseAttribute(PageDefinition pgdef, ComponentInfo parent, Element el, AnnotationHelper annHelper)
			throws Exception {
		if (el.getAttributeItem("forEach") != null)
			throw new UiException(message("forEach not applicable to attribute", el));

		//Test if any element is used
		String elFound = null;
		for (final Item o : el.getChildren()) {
			if (o instanceof Element) {
				elFound = ((Element) o).getName();
				break;
			}
		}

		final Attribute attr = el.getAttributeItem(null, "name", 0); //by local name
		if (attr == null)
			throw new UiException(message("The name attribute required", el));

		final String attnm = attr.getValue();
		noEmpty("name", attnm, el);
		final ConditionImpl cond = ConditionImpl.getInstance(el.getAttributeValue("if"),
				el.getAttributeValue("unless"));
		if (elFound != null) {
			if (Events.isValid(attnm))
				throw new UiException(message("<" + elFound + "> not allowed in an event listener", el));

			parseAsProperty(pgdef, parent, attnm, el.getChildren(), annHelper, cond);
		} else {
			final String trim = el.getAttributeValue("trim");
			noEL("trim", trim, el);
			final String attval = el.getText(trim != null && "true".equals(trim));
			addAttribute(parent, attr.getNamespace(), attnm, attval, cond, el.getLocator());
		}

		annHelper.applyAnnotations(parent, attnm, true);
	}

	private static void parseCustomAttributes(LanguageDefinition langdef, NodeInfo parent, Element el,
			AnnotationHelper annHelper) throws Exception {
		//if (!el.getElements().isEmpty())
		//	throw new UiException(message("Child elements are not allowed for <custom-attributes>", el));

		if (parent instanceof PageDefinition)
			throw new UiException(message("<custom-attributes> must be used under a component", el));
		if (annHelper.clear())
			log.warn(message("Annotations are ignored since <custom-attributes> doesn't support them", el)); //old style annotation not supported

		String ifc = null, unless = null, scope = null, composite = null;
		final Map<String, String> attrs = new LinkedHashMap<String, String>();
		AnnotationHelper attrAnnHelper = null;
		for (final Attribute attr : el.getAttributeItems()) {
			final Namespace attrns = attr.getNamespace();
			final String attnm = attr.getLocalName();
			final String attval = attr.getValue();
			final String attvaltrim;
			if ("if".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				ifc = attval;
			} else if ("unless".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				unless = attval;
			} else if ("scope".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				scope = attval;
			} else if ("composite".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				composite = attval;
			} else if ("forEach".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				throw new UiException(message("forEach not applicable to <custom-attributes>", el));
			} else if (parent instanceof ComponentInfo && AnnotationHelper.isAnnotation(attvaltrim = attval.trim())) {
				if (attrAnnHelper == null)
					attrAnnHelper = new AnnotationHelper();
				applyAttrAnnot(attrAnnHelper, (ComponentInfo) parent, attnm, attvaltrim, false, location(attr));
			} else {
				attrs.put(attnm, attval);
			}
		}

		if (!attrs.isEmpty())
			new AttributesInfo(parent, attrs, scope, composite, ConditionImpl.getInstance(ifc, unless));
	}

	private static void parseVariables(LanguageDefinition langdef, NodeInfo parent, Element el,
			AnnotationHelper annHelper) throws Exception {
		//if (!el.getElements().isEmpty())
		//	throw new UiException(message("Child elements are not allowed for <variables> element", el));

		if (annHelper.clear())
			log.warn(message("Annotations are ignored since <variables> doesn't support them", el)); //old style annotation not supported here

		String ifc = null, unless = null, composite = null;
		boolean local = false;
		final Map<String, String> vars = new LinkedHashMap<String, String>();
		for (final Attribute attr : el.getAttributeItems()) {
			final Namespace attrns = attr.getNamespace();
			final String attnm = attr.getLocalName();
			final String attval = attr.getValue();
			if ("if".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				ifc = attval;
			} else if ("unless".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				unless = attval;
			} else if ("local".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				local = "true".equals(attval);
			} else if ("composite".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				composite = attval;
			} else if ("forEach".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				throw new UiException(message("forEach not applicable to <variables>", el));
			} else {
				vars.put(attnm, attval);
			}
		}
		if (!vars.isEmpty())
			new VariablesInfo(parent, vars, local, composite, ConditionImpl.getInstance(ifc, unless));
	}

	private static NodeInfo parseShadowElement(PageDefinition pgdef, NodeInfo parent, Element el,
			AnnotationHelper annHelper) throws Exception {
		String ifc = null, unless = null, name = el.getLocalName();
		AnnotationHelper attrAnnHelper = null;
		final LanguageDefinition lookup = LanguageDefinition.lookup("xul/html");
		ComponentDefinition shadowDefinition = lookup.hasShadowDefinition(name) ? lookup.getShadowDefinition(name)
				: pgdef.getComponentDefinitionMap().get(name);
		final ShadowInfo compInfo = new ShadowInfo(parent, shadowDefinition, name,
				ConditionImpl.getInstance(ifc, unless));
		boolean annotated = false;
		boolean shouldIgnoreAnnotNamespace = annHelper.shouldIgnoreAnnotNamespace();
		for (final Attribute attr: el.getAttributeItems()) {
			final Namespace attrns = attr.getNamespace();
			final String attURI = attrns != null ? attrns.getURI() : "";
			final String attnm = attr.getLocalName();
			final String attval = attr.getValue();
			if ("if".equals(attnm)) {
				ifc = attval;
			} else if ("unless".equals(attnm)) {
				unless = attval;
			} else if (!shouldIgnoreAnnotNamespace && (LanguageDefinition.ANNOTATION_NAMESPACE.equals(attURI) || "annotation".equals(attURI))) {
				annotated = true;
				//ZK 6: annotation namespace mandates annotation
				if (attrAnnHelper == null)
					attrAnnHelper = new AnnotationHelper();
				applyAttrAnnot(attrAnnHelper, compInfo, attnm, attval.trim(), false, location(attr));
			} else {
				final String attPref = attrns != null ? attrns.getPrefix() : null;
				String attvaltrim;
				if (!"xmlns".equals(attPref) && !("xmlns".equals(attnm) && "".equals(attPref))
						&& !"http://www.w3.org/2001/XMLSchema-instance".equals(attURI)) {
					attvaltrim = modifyAttrValueIfSimplified(attnm, attval.trim());
					if ((attURI.length() == 0 || LanguageDefinition.ZK_NAMESPACE.endsWith(attURI))
							&& AnnotationHelper.isAnnotation(attvaltrim)) { // annotation
						if (attrAnnHelper == null)
							attrAnnHelper = new AnnotationHelper();
						applyAttrAnnot(attrAnnHelper, compInfo, attnm, attvaltrim, true, location(attr));
						//F80 - store subtree's binder annotation count
						Set<String> binderAnnotations = WebApps.getCurrent().getConfiguration().getBinderAnnotations();
						for (String annot : binderAnnotations) {
							if (attvaltrim.contains(annot)) {
								compInfo.enableBindingAnnotation();
								annotated = true;
								break;
							}
						}
					} else {
						compInfo.addProperty(attnm, attval, null);
						if (attrAnnHelper != null)
							attrAnnHelper.applyAnnotations(compInfo, attnm, true);
					}
				}
			}
		}

		compInfo.setCondition(ConditionImpl.getInstance(ifc, unless));
		annHelper.applyAnnotations(compInfo, null, true);

		Node root = el.getFirstChild();
		while (root != null && !(root instanceof Element)) {
			if (root instanceof Text) {
				if (!isEmpty(((Text) root).getText().trim()))
					break;
			}
			root = root.getNextSibling();
		}
		if (root != null) {
			String rn = root.getLocalName();
			if (!"template".equals(rn)) { // automatically create a template tag for it.
				Node item = root;
				while (item != null) {
					if ("template".equals(item.getLocalName()))
						throw new UiException(message(rn + " does not support between templates", (Item) root));
					item = item.getNextSibling();
				}
				TemplateInfo templateInfo = new TemplateInfo(compInfo, "", null, null, null);
				if (!shouldIgnoreAnnotNamespace && annotated) {
					annHelper.setIgnoreAnnotNamespace(true);
					templateInfo.setAnnotationNamespacedRoot(true);
				}
				return templateInfo;
			}
		}
		if (!shouldIgnoreAnnotNamespace && annotated) {
			annHelper.setIgnoreAnnotNamespace(true);
			compInfo.setAnnotationNamespacedRoot(true);
		}
		return compInfo;
	}

	private static TemplateInfo parseTemplate(NodeInfo parent, Element el, AnnotationHelper annHelper)
			throws Exception {
		if (annHelper.clear())
			log.warn(message("Annotations are ignored since <template> doesn't support them", el));
		if (el.getAttributeItem("forEach") != null)
			log.warn(message("forEach is ignored since <template> doesn't support it", el));

		String ifc = null, unless = null, name = null, src = null;
		final Map<String, String> params = new LinkedHashMap<String, String>(); //reserve the order
		for (final Attribute attr : el.getAttributeItems()) {
			final Namespace attrns = attr.getNamespace();
			final String attURI = attrns != null ? attrns.getURI() : "";
			final String attnm = attr.getLocalName();
			final String attval = attr.getValue();
			if ("name".equals(attnm)) {
				name = attval;
			} else if ("src".equals(attnm)) {
				src = attval;
			} else if ("if".equals(attnm)) {
				ifc = attval;
			} else if ("unless".equals(attnm)) {
				unless = attval;
			} else {
				final String attPref = attrns != null ? attrns.getPrefix() : null;
				if (!"xmlns".equals(attnm) && !"xml".equals(attnm) && !attURI.contains("w3.org")
						&& (attPref == null || (!"xmlns".equals(attPref) && !"xml".equals(attPref))))
					params.put(attnm, attval);
			}
		}
		if (name == null) {
			//throw new UiException(message("The name attribute required", el));
			name = "";
			// support "" an empty string for ZK 8.0.0 shadow element

		}
		return new TemplateInfo(parent, name, src, params, ConditionImpl.getInstance(ifc, unless));
	}

	private static ZkInfo parseZk(NodeInfo parent, Element el, AnnotationHelper annHelper) throws Exception {
		if (annHelper.clear())
			log.warn(message("Annotations are ignored since <zk> doesn't support them", el));

		final ZkInfo zi = new ZkInfo(parent, null);
		String ifc = null, unless = null, forEach = null, forEachBegin = null, forEachEnd = null, forEachStep = null;
		for (final Attribute attr : el.getAttributeItems()) {
			final Namespace attrns = attr.getNamespace();
			final String attURI = attrns != null ? attrns.getURI() : "";
			final String attnm = attr.getLocalName();
			final String attval = attr.getValue();
			if ("if".equals(attnm) || "when".equals(attnm)) {
				ifc = attval;
			} else if ("unless".equals(attnm)) {
				unless = attval;
			} else if ("forEach".equals(attnm)) {
				forEach = attval;
			} else if ("forEachStep".equals(attnm)) {
				forEachStep = attval;
			} else if ("forEachBegin".equals(attnm)) {
				forEachBegin = attval;
			} else if ("forEachEnd".equals(attnm)) {
				forEachEnd = attval;
			} else if ("switch".equals(attnm) || "choose".equals(attnm)) {
				if (isZkSwitch(parent))
					throw new UiException(message("<zk " + attnm + "> cannot be used in <zk switch/choose>", el));
				zi.setSwitch(attval);
			} else if ("case".equals(attnm)) {
				if (!isZkSwitch(parent))
					throw new UiException(message("<zk case> can be used only in <zk switch>", attr));
				zi.setCase(attval);
			} else {
				final String attPref = attrns != null ? attrns.getPrefix() : null;
				if (!"xmlns".equals(attnm) && !"xml".equals(attnm) && !attURI.contains("w3.org")
						&& (attPref == null || (!"xmlns".equals(attPref) && !"xml".equals(attPref))))
					warnWrongZkAttr(attr);
			}
		}
		zi.setCondition(ConditionImpl.getInstance(ifc, unless));
		zi.setForEach(forEach, forEachBegin, forEachEnd, forEachStep);
		return zi;
	}

	/** Whether a string is null or empty. */
	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	/** Whether the name space belongs to the default language. */
	private static final boolean isDefaultNS(LanguageDefinition langdef, String pref, String uri) {
		return (!langdef.isNative() && "".equals(pref) && "".equals(uri)) || langdef.getNamespace().equals(uri);
	}

	/** Returns whether it is a Shadow element.
	 * @param pref namespace's prefix
	 * @param uri namespace's URI
	 * @param bNativeContent whether to ignore if URI not specified explicitly
	 */
	private static final boolean isShadowElement(LanguageDefinition langdef, PageDefinition pgdef, String nm,
			String pref, String uri, boolean bNativeContent) {
		// feature in 8.0.0, no need to check namespace, if any.
		if ("true".equalsIgnoreCase(Library.getProperty("org.zkoss.zk.namespace.tolerant", "false")))
			return langdef.hasShadowDefinition(nm) || (!"xul/html".equals(langdef.getName())
					&& LanguageDefinition.lookup("xul/html").hasShadowDefinition(nm));
		ComponentDefinition componentDefinition = pgdef.getComponentDefinitionMap().get(nm);
		// F80 - support another namespace
		if (componentDefinition instanceof ShadowDefinitionImpl || LanguageDefinition.SHADOW_NAMESPACE.equals(uri)
				|| LanguageDefinition.SHADOW_NAME.equals(uri))
			return true;
		if (isDefaultNS(langdef, pref, uri))
			return !bNativeContent && langdef.hasShadowDefinition(nm);
		return false;
	}

	/** Returns whether it is a ZK element.
	 * @param pref namespace's prefix
	 * @param uri namespace's URI
	 * @param bNativeContent whether to ignore if URI not specified explicitly
	 */
	private static final boolean isZkElement(LanguageDefinition langdef, String nm, String pref, String uri,
			boolean bNativeContent) {
		// feature in 8.0.0, no need to check namespace, if any.
		if ("true".equalsIgnoreCase(Library.getProperty("org.zkoss.zk.namespace.tolerant", "false")))
			return true;
		if (isDefaultNS(langdef, pref, uri))
			return !bNativeContent && !langdef.hasComponentDefinition(nm);
		return LanguageDefinition.ZK_NAMESPACE.equals(uri) || "zk".equals(uri);
	}

	private static final boolean isZkElement(LanguageDefinition langdef, Element el, boolean bNativeContent) {
		final Namespace ns = el.getNamespace();
		return isZkElement(langdef, el.getLocalName(), ns != null ? ns.getPrefix() : "", ns != null ? ns.getURI() : "",
				bNativeContent);
	}

	private static final boolean isZkElement(LanguageDefinition langdef, String nm, String pref, String uri) {
		return isZkElement(langdef, nm, pref, uri, false);
	}

	/** Returns whether it is a ZK attribute (in a non-ZK element).
	 * @param bNativeContent whether to ignore if URI not specified explicitly
	 */
	private static final boolean isZkAttr(LanguageDefinition langdef, Namespace attrns, boolean bNativeContent) {
		//if native we will make sure URI is ZK or lang's namespace
		if ((bNativeContent || langdef.isNative()) && attrns != null && "".equals(attrns.getPrefix()))
			return false; //if navtive, "" means not ZK

		return isZkElementAttr(langdef, attrns);
	}

	private static final boolean isZkAttr(LanguageDefinition langdef, Namespace attrns) {
		return isZkAttr(langdef, attrns, false);
	}

	/** Similar to {@link #isZkAttr}, except it doesn't care isNative
	 * (i.e., we consider it as a ZK attribute unless a namespace
	 * other than ZK is specified.
	 */
	private static final boolean isZkElementAttr(LanguageDefinition langdef, Namespace attrns) {
		if (attrns == null //not possible; just in case
				|| "".equals(attrns.getPrefix())) //w3c attr: uri is "" if prefix ""
			return true;

		final String uri = attrns.getURI();
		return LanguageDefinition.ZK_NAMESPACE.equals(uri) || "zk".equals(uri) || langdef.getNamespace().equals(uri);
	}

	/** Parse an attribute and adds it to the definition.
	 */
	private void addAttribute(ComponentInfo compInfo, Namespace attrns, String name, String value, ConditionImpl cond,
			org.zkoss.xml.Locator xl) throws Exception {
		if (Events.isValid(name)) {
			boolean bZkAttr = attrns == null;
			if (!bZkAttr) {
				final String uri = attrns.getURI();
				if (LanguageDefinition.CLIENT_NAMESPACE.equals(uri) || "client".equals(uri)) {
					compInfo.addWidgetListener(name, value, cond);
					return;
				}
				if (LanguageDefinition.CLIENT_ATTRIBUTE_NAMESPACE.equals(uri) || "client/attribute".equals(uri)) {
					compInfo.addWidgetAttribute(name, value, cond);
					return;
				}

				final String pref = attrns.getPrefix();
				LanguageDefinition langdef = compInfo.getLanguageDefinition();
				ComponentDefinition compdef = compInfo.getComponentDefinition();

				if (langdef == null)
					bZkAttr = true;
				// refix ZK-2495: skip native components
				else if (isDefaultNS(langdef, pref, uri) && !compdef.isNative())
					bZkAttr = !langdef.isDynamicReservedAttributes("[event]");
				else
					bZkAttr = LanguageDefinition.ZK_NAMESPACE.equals(uri) || "zk".equals(uri);
			}
			if (bZkAttr) {
				checkZScriptEnabled(xl);
				final int lno = xl != null ? xl.getLineNumber() : 0;
				final ZScript zscript = ZScript.parseContent(value, lno);
				if (zscript.getLanguage() == null)
					zscript.setLanguage(compInfo.getPageDefinition().getZScriptLanguage());
				//resolve it here instead of runtime since createComponents
				compInfo.addEventHandler(name, zscript, cond);
				return; //done
			}
		} else {
			final String uri = attrns.getURI();
			if (LanguageDefinition.CLIENT_NAMESPACE.equals(uri) || "client".equals(uri)) {
				if (name.length() == 0)
					throw new UiException(message("Client attribute name required", xl));
				if ("use".equals(name)) {
					if (cond != null)
						throw new UnsupportedOperationException(message("if and unless not allowed for w:use", xl));
					compInfo.setWidgetClass(value);
				} else {
					compInfo.addWidgetOverride(name, value, cond);
				}
				return;
			}
			if (LanguageDefinition.CLIENT_ATTRIBUTE_NAMESPACE.equals(uri) || "client/attribute".equals(uri)) {
				compInfo.addWidgetAttribute(name, value, cond);
				return;
			}
		}

		// ZK-4474
		if (attrns != null) {
			if (isClientAttrPrefixNamespace(attrns.getURI())) {
				compInfo.addWidgetAttribute(attrns.getPrefix() + ":" + name, value, cond);
				return;
			}
		}
		
		compInfo.addProperty(name, value, cond);
	}

	/** Adds the declared namespaces to the native info, if necessary.
	 */
	private static void addDeclaredNamespace(NativeInfo nativeInfo, Collection<Namespace> namespaces,
			LanguageDefinition langdef) {
		for (Namespace ns : namespaces) {
			final String uri = ns.getURI();
			boolean bNatPrefix = uri.startsWith(LanguageDefinition.NATIVE_NAMESPACE_PREFIX);
			// ZK-2494: Should also consider adding declaredNamespace inside a native component
			if (bNatPrefix || (!isZKNamespace(uri) && !langdef.getNamespace().equals(uri)))
				nativeInfo.addDeclaredNamespace(new Namespace(ns.getPrefix(),
						bNatPrefix ? uri.substring(LanguageDefinition.NATIVE_NAMESPACE_PREFIX.length()) : uri));
		}
	}

	/** Minimizes the native infos such that UiEngine creates
	 * the minimal number of components.
	 */
	private static void optimizeNativeInfos(NativeInfo compInfo) {
		//Optimize 1: merge to prolog, if the first children are
		//native and have no child
		for (Iterator it = compInfo.getChildren().iterator(); it.hasNext();) {
			final NodeInfo o = (NodeInfo) it.next();
			if (o instanceof NativeInfo) {
				final NativeInfo childInfo = (NativeInfo) o;
				if (!childInfo.getChildren().isEmpty())
					break;
				childInfo.setParentDirectly(null);

				// merge annotation if any
				AnnotationMap annotationMap = childInfo.getAnnotationMap();
				if (annotationMap != null) {
					for (String propName : annotationMap.getAnnotatedProperties()) {
						for (Annotation anno : annotationMap.getAnnotations(propName)) {
							compInfo.addAnnotation(propName, anno.getName(), anno.getAttributes(), anno.getLocation());
						}
					}
				}
			} else if (o instanceof ComponentInfo || o instanceof ZkInfo || o instanceof ShadowInfo) {
				break;
			}

			compInfo.addPrologChild(o);
			it.remove(); //detach it from the children list
		}

		//Optimize 2: merge to epilog if the last children, are
		//native and have no child
		int sz = compInfo.getChildren().size();
		if (sz >= 0) {
			final ListIterator it = compInfo.getChildren().listIterator(sz);
			while (it.hasPrevious()) {
				final Object o = it.previous();
				if (o instanceof NativeInfo) {
					final NativeInfo childInfo = (NativeInfo) o;
					if (!childInfo.getChildren().isEmpty()) {
						it.next();
						break;
					}
					childInfo.setParentDirectly(null);
				} else if (o instanceof ComponentInfo || o instanceof ZkInfo || o instanceof ShadowInfo) {
					it.next();
					break;
				}
			}
			while (it.hasNext()) {
				final NodeInfo o = (NodeInfo) it.next();
				compInfo.addEpilogChild(o);
				it.remove();
			}
		}

		//Optimize 3: merge to split child
		//If there is only one native child, we make it a split child and
		//make all its children (grand-children) up one level
		if (compInfo.getChildren().size() == 1 && compInfo.getSplitChild() == null /*just in case*/) {
			Iterator it = compInfo.getChildren().iterator();
			final Object o = it.next();
			if (o instanceof NativeInfo) {
				final NativeInfo childInfo = (NativeInfo) o;
				//FUTURE: enhance UiEngineImpl to handle split's forEach
				if (!childInfo.withForEach() && !childInfo.withCondition()) { // B65-ZK-1626
					childInfo.setParentDirectly(null);
					compInfo.setSplitChild(childInfo);
					it.remove();

					for (it = new ArrayList<NodeInfo>(childInfo.getChildren()).iterator(); it.hasNext();)
						compInfo.appendChild((NodeInfo) it.next());
				}
			}
		}
	}

	/** Returns the language definition of the specified name or namespace.
	 *  Return null if Definition not found
	 */
	private static LanguageDefinition langdefLookup(String name) {
		if (name == null || name.length() == 0) {
			return null;
		}

		try {
			return LanguageDefinition.lookup(name);
		} catch (DefinitionNotFoundException e) {
			return null;
		}
	}

	/** Returns whether the given uri is a known namespace in ZK or not
	 */
	private static boolean isZKNamespace(String uri) {
		return LanguageDefinition.ZK_NAMESPACE.equals(uri) || "zk".equals(uri)
				|| LanguageDefinition.NATIVE_NAMESPACE.equals(uri) || "native".equals(uri) || "annotation".equals(uri)
				|| LanguageDefinition.ANNOTATION_NAMESPACE.equals(uri) || "client".equals(uri)
				|| LanguageDefinition.CLIENT_NAMESPACE.equals(uri) || "client/attribute".equals(uri)
				|| LanguageDefinition.CLIENT_ATTRIBUTE_NAMESPACE.equals(uri) || "xhtml".equals(uri)
				|| "http://www.w3.org/1999/xhtml/".equals(uri) || "zul".equals(uri)
				|| "http://www.zkoss.org/2005/zul/".equals(uri) || "xml".equals(uri)
				|| "http://www.zkoss.org/2007/xml".equals(uri) || isClientAttrPrefixNamespace(uri) || langdefLookup(uri) != null;
	}

	/** Returns whether the given uri is in native namespace or not
	 */
	private static boolean isNativeNamespace(String uri) {
		return "native".equals(uri) || LanguageDefinition.NATIVE_NAMESPACE.equals(uri);
	}

	/** Returns whether the given uri is in xml namespace or not
	 */
	private static boolean isXmlNamespace(String uri) {
		return "xml".equals(uri) || "http://www.zkoss.org/2007/xml".equals(uri);
	}

	/** Returns whether the given uri is in client namespace or not
	 */
	private static boolean isClientNamespace(String uri) {
		return "client".equals(uri) || LanguageDefinition.CLIENT_NAMESPACE.equals(uri);
	}

	/** Returns whether the given uri is in client attribute namespace or not
	 */
	private static boolean isClientAttrNamespace(String uri) {
		return "client/attribute".equals(uri) || LanguageDefinition.CLIENT_ATTRIBUTE_NAMESPACE.equals(uri);
	}

	/** Returns whether the given uri is in client raw attribute namespace or not
	 */
	private static boolean isClientAttrPrefixNamespace(String uri) {
		return LanguageDefinition.CLIENT_ATTRIBUTE_PREFIX_NAME.equals(uri) || LanguageDefinition.CLIENT_ATTRIBUTE_PREFIX_NAMESPACE.equals(uri);
	}

	//ZK-2632: Parser support disorder template tag
	private enum ParsingState {
		ROOT, FIRST, SECOND
	}
}
