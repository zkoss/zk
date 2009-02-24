/* Parser.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 14:26:18     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Collection;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.io.File;
import java.io.Reader;
import java.net.URL;

import org.zkoss.lang.D;
import org.zkoss.lang.Library;
import org.zkoss.lang.Classes;
import org.zkoss.lang.PotentialDeadLockException;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.logging.Log;
import org.zkoss.util.resource.Locator;
import org.zkoss.idom.Namespace;
import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.Text;
import org.zkoss.idom.CData;
import org.zkoss.idom.Item;
import org.zkoss.idom.Attribute;
import org.zkoss.idom.ProcessingInstruction;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.xel.util.Evaluators;
import org.zkoss.xel.util.MethodFunction;
import org.zkoss.web.servlet.Servlets;
	
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.ui.sys.WebAppCtrl;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.impl.RequestInfoImpl;
import org.zkoss.zk.ui.impl.ZScriptInitiator;
import org.zkoss.zk.ui.metainfo.impl.*;

/**
 * Used to prase the ZUL file
 * @author tomyeh
 */
public class Parser {
	private static final Log log = Log.lookup(Parser.class);

	private final WebApp _wapp;
	private final Locator _locator;

	/** Constructor.
	 *
	 * @param locator the locator used to locate taglib and other resources.
	 * If null, wapp is assumed ({@link WebApp} is also assumed).
	 */	
	public Parser(WebApp wapp, Locator locator) {
		if (wapp == null)
			throw new IllegalArgumentException("null");
		_wapp = wapp;
		_locator = locator != null ? locator: (Locator)wapp;
	}

	/** Parses the specified file.
	 *
	 * @param path the request path.
	 * It is used as {@link org.zkoss.zk.ui.Page#getRequestPath}, or null
	 * if not available.
	 */
	public PageDefinition parse(File file, String path) throws Exception {
		//if (log.debugable()) log.debug("Parsing "+file);
		final PageDefinition pgdef =
			parse(new SAXBuilder(true, false, true).build(file),
				Servlets.getExtension(file.getName()));
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
		//if (log.debugable()) log.debug("Parsing "+url);
		final PageDefinition pgdef =
			parse(new SAXBuilder(true, false, true).build(url),
				Servlets.getExtension(url.toExternalForm()));
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
	public PageDefinition parse(Reader reader, String extension)
	throws Exception {
		//if (log.debugable()) log.debug("Parsing "+reader);
		return parse(new SAXBuilder(true, false, true).build(reader), extension);
	}
	/** Parss the raw content directly from a DOM tree.
	 *
	 * @param extension the default extension if doc doesn't specify
	 * an language. Ignored if null.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 */
	public PageDefinition parse(Document doc, String extension)
	throws Exception {
		//1. parse the page and import directive if any
		final List pis = new LinkedList(), imports = new LinkedList();
		String lang = null;
		for (Iterator it = doc.getChildren().iterator(); it.hasNext();) {
			final Object o = it.next();
			if (!(o instanceof ProcessingInstruction)) continue;

			final ProcessingInstruction pi = (ProcessingInstruction)o;
			final String target = pi.getTarget();
			if ("page".equals(target)) {
				//we handle only the language attribute here
				final Map params = pi.parseData();
				final String l = (String)params.remove("language");
				if (l != null) {
					noEL("language", l, pi);
					lang = l;
				}

				if (!params.isEmpty())
					pis.add(pi); //process it later
			} else if ("import".equals(target)) { //import
				final Map params = pi.parseData();
				final String src = (String)params.remove("src");
				final String dirs = (String)params.remove("directives");
				if (!params.isEmpty())
					log.warning("Ignored unknown attributes: "+params.keySet()+", "+pi.getLocator());
				noELnorEmpty("src", src, pi);
				noEL("directives", dirs, pi);
				imports.add(new String[] {src, dirs});
			} else {
				pis.add(pi);
			}
		}

		//2. Create PageDefinition
		final LanguageDefinition langdef =
			extension != null && lang == null?
				LanguageDefinition.getByExtension(extension):
				LanguageDefinition.lookup(lang);
		final PageDefinition pgdef = new PageDefinition(langdef, getLocator());

		//3. resolve imports
		if (!imports.isEmpty()) {
			final RequestInfo ri =
				new RequestInfoImpl(_wapp, null, null, null, _locator);
			final UiFactory uf = ((WebAppCtrl)_wapp).getUiFactory();
			for (Iterator it = imports.iterator(); it.hasNext();) {
				final String[] imprt = (String[])it.next();
				final String path = imprt[0], dirs = imprt[1];
				try {
					final PageDefinition pd = uf.getPageDefinition(ri, path);
					if (pd == null)
						throw new UiException("Import page not found: "+path);
					pgdef.imports(pd, parseToArray(dirs));
				} catch (PotentialDeadLockException ex) {
					throw new UiException("Recursive import: "+path, ex);
				}
			}
		}

		//4. Processing the rest of processing instructions at the top level
		for (Iterator it = pis.iterator(); it.hasNext();)
			parse(pgdef, (ProcessingInstruction)it.next());

		//5. Processing from the root element
		final Element root = doc.getRootElement();
		if (root != null)
			parse(pgdef, pgdef, root, new AnnotationHelper(), false);
		return pgdef;
	}
	private static Class locateClass(String clsnm) throws Exception {
		try {
			return Classes.forNameByThread(clsnm);
		} catch (ClassNotFoundException ex) {
			throw new ClassNotFoundException("Class not found: "+clsnm, ex);
		}
	}
	/** Parses a list of string separated by comma, into a String array.
	 */
	private static String[] parseToArray(String s) {
		if (s == null)
			return null;
		Collection ims = CollectionsX.parse(null, s, ',', false); //NO EL
		return (String[])ims.toArray(new String[ims.size()]);
	}

	//-- derive to override --//
	/** returns locator for locating resources. */
	public Locator getLocator() {
		return _locator;
	}

	/** Parse processing instruction.
	 */
	private void parse(PageDefinition pgdef, ProcessingInstruction pi)
	throws Exception {
		final String target = pi.getTarget();
		final Map params = pi.parseData();
		if ("page".equals(target)) {
			parsePageDirective(pgdef, pi, params);
		} else if ("init".equals(target)) {
			parseInitDirective(pgdef, pi, params);
		} else if ("variable-resolver".equals(target)
		|| "function-mapper".equals(target)) {
			final String clsnm = (String)params.remove("class");
			if (isEmpty(clsnm))
				throw new UiException("The class attribute is required, "+pi.getLocator());

			final List args = new LinkedList();
			for (int j = 0;; ++j) {
				final String arg = (String)params.remove("arg" + j);
				if (arg == null) break;
				args.add(arg);
			}

			if (!params.isEmpty())
				log.warning("Ignored unknown attributes: "+params.keySet()+", "+pi.getLocator());

			if ("variable-resolver".equals(target))
				pgdef.addVariableResolverInfo(
					clsnm.indexOf("${") >= 0 ? //class supports EL
						new VariableResolverInfo(clsnm, args):
						new VariableResolverInfo(locateClass(clsnm), args));
			else
				pgdef.addFunctionMapperInfo(
					clsnm.indexOf("${") >= 0 ? //class supports EL
						new FunctionMapperInfo(clsnm, args):
						new FunctionMapperInfo(locateClass(clsnm), args));
		} else if ("component".equals(target)) { //declare a component
			parseComponentDirective(pgdef, pi, params);
		} else if ("taglib".equals(target)) {
			final String uri = (String)params.remove("uri");
			final String prefix = (String)params.remove("prefix");
			if (!params.isEmpty())
				log.warning("Ignored unknown attributes: "+params.keySet()+", "+pi.getLocator());
			if (uri == null || prefix == null)
				throw new UiException("Both uri and prefix attribute are required, "+pi.getLocator());
			//if (D.ON && log.debugable()) log.debug("taglib: prefix="+prefix+" uri="+uri);
			noEL("prefix", prefix, pi);
			noEL("uri", uri, pi); //not support EL (kind of chicken-egg issue)
			pgdef.addTaglib(new Taglib(prefix, toAbsoluteURI(uri, false)));
		} else if ("evaluator".equals(target)) {
			parseEvaluatorDirective(pgdef, pi, params);
		} else if ("xel-method".equals(target)) {
			parseXelMethod(pgdef, pi, params);
		} else if ("link".equals(target) || "meta".equals(target)) { //declare a header element
			pgdef.addHeaderInfo(new HeaderInfo(target, params));
		} else if ("root-attributes".equals(target)) {
			for (Iterator it = pi.parseData().entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				pgdef.setRootAttribute((String)me.getKey(), (String)me.getValue());
			}
		} else if ("forward".equals(target)) { //forward
			final String uri = (String)params.remove("uri");
			final String ifc = (String)params.remove("if");
			final String unless = (String)params.remove("unless");
			if (!params.isEmpty())
				log.warning("Ignored unknown attributes: "+params.keySet()+", "+pi.getLocator());
			noEmpty("uri", uri, pi);
			pgdef.addForwardInfo(
				new ForwardInfo(uri, ConditionImpl.getInstance(ifc, unless)));
		} else if ("import".equals(target)) { //import
			throw new UiException("The import directive can be used only at the top level, "+pi.getLocator());
		} else {
			log.warning("Unknown processing instruction: "+target+", "+pi.getLocator());
		}
	}
	/** Process the init directive. */
	private void parseInitDirective(PageDefinition pgdef,
	ProcessingInstruction pi, Map params) throws Exception {
		final List args = new LinkedList();
		for (int j = 0;; ++j) {
			final String arg = (String)params.remove("arg" + j);
			if (arg == null) break;
			args.add(arg);
		}

		final String clsnm = (String)params.remove("class");
		final String zsrc = (String)params.remove("zscript");

		if (!params.isEmpty())
			log.warning("Ignored unknown attributes: "+params.keySet()+", "+pi.getLocator());

		if (isEmpty(clsnm)) {
			if (isEmpty(zsrc))
				throw new UiException("Either the class or zscript attribute must be specified, "+pi.getLocator());

			final ZScript zs;
			final String zslang = pgdef.getZScriptLanguage();
			if (zsrc.indexOf("${") >= 0) {
				zs = new ZScript(pgdef.getEvaluatorRef(),
					zslang, zsrc, null, getLocator()); //URL in EL
			} else {
				final URL url = getLocator().getResource(zsrc);
				if (url == null) throw new UiException("File not found: "+zsrc+", at "+pi.getLocator());
					//don't throw FileNotFoundException since Tomcat 'eats' it
				zs = new ZScript(pgdef.getEvaluatorRef(), zslang, url, null);
			}

			pgdef.addInitiatorInfo(
				new InitiatorInfo(new ZScriptInitiator(zs), args));
		} else {
			if (!isEmpty(zsrc))
				throw new UiException("You cannot specify both class and zscript, "+pi.getLocator());

			pgdef.addInitiatorInfo(
				clsnm.indexOf("${") >= 0 ? //class supports EL
					new InitiatorInfo(clsnm, args):
					new InitiatorInfo(locateClass(clsnm), args));
				//Note: we don't resolve the class name later because
				//no zscript run before init (and better performance)
		}
	}
	/** Process the page directive. */
	private static void parsePageDirective(PageDefinition pgdef,
	ProcessingInstruction pi, Map params) throws Exception {
		for (Iterator it = pi.parseData().entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			final String nm = (String)me.getKey();
			final String val = (String)me.getValue();
			if ("language".equals(nm)) {
				if (!(pi.getParent() instanceof Document))
					log.warning("Ignored language attribute since the page directive is not at the top level, "+pi.getLocator());
			} else if ("title".equals(nm)) {
				pgdef.setTitle(val);
			} else if ("style".equals(nm)) {
				pgdef.setStyle(val);
			} else if ("id".equals(nm)) {
				pgdef.setId(val);
			} else if ("zscriptLanguage".equals(nm)
			|| "zscript-language".equals(nm)) { //backward compatible with 2.4.x
				noELnorEmpty("zscriptLanguage", val, pi);
				pgdef.setZScriptLanguage(val);
			} else if ("cacheable".equals(nm)) {
				noELnorEmpty("cacheable", val, pi);
				pgdef.setCacheable(Boolean.valueOf("true".equals(val)));
			} else if ("contentType".equals(nm)) {
				noEmpty("contentType", val, pi);
				pgdef.setContentType(val);
			} else if ("docType".equals(nm)) {
				noEmpty("docType", val, pi);
				pgdef.setDocType("<!DOCTYPE " + val + '>');
			} else if ("xml".equals(nm)) {
				noEmpty("xml", val, pi);
				pgdef.setFirstLine("<?xml " + val + "?>");
			} else if ("complete".equals(nm)) {
				pgdef.setComplete("true".equals(val));
			} else {
				log.warning("Ignored unknown attribute: "+nm+", "+pi.getLocator());
			}
		}
	}
	/** Process the component directive. */
	private void parseComponentDirective(PageDefinition pgdef,
	ProcessingInstruction pi, Map params) throws Exception {
		final String name = (String)params.remove("name");
		noELnorEmpty("name", name, pi);

		String macroURI = (String)params.remove("macroURI");
		if (macroURI == null) macroURI = (String)params.remove("macro-uri"); //backward compatible (2.4.x)
		final String extds = (String)params.remove("extends");
		final String clsnm = (String)params.remove("class");
		ComponentDefinition compdef;
		if (macroURI != null) {
			//if (D.ON && log.finerable()) log.finer("macro component definition: "+name);

			final String inline = (String)params.remove("inline");
			noEL("inline", inline, pi);
			noEL("macroURI", macroURI, pi);
				//no EL because pagedef must be loaded to resolve
				//the impl class before creating an instance of macro

			final boolean bInline = "true".equals(inline);
			compdef = pgdef.getLanguageDefinition().getMacroDefinition(
				name, toAbsoluteURI(macroURI, false), bInline, pgdef);
			if (!isEmpty(clsnm)) {
				if (bInline)
					throw new UiException("class not allowed with inline macros, "+pi.getLocator());
				noEL("class", clsnm, pi);
				compdef.setImplementationClass(clsnm);
					//Resolve later since might defined in zscript
			}
		} else if (extds != null) { //extends
			//if (D.ON && log.finerable()) log.finer("Override component definition: "+name);

			noEL("extends", extds, pi);
			final ComponentDefinition ref = pgdef.getLanguageDefinition()
				.getComponentDefinition(extds);
			if (ref.isMacro())
				throw new UiException("Unable to extend from a macro component, "+pi.getLocator());

			compdef = ref.clone(null, name);
			if (!isEmpty(clsnm)) {
				noEL("class", clsnm, pi);
				compdef.setImplementationClass(clsnm);
					//Resolve later since might defined in zscript
			}
		} else {
			//if (D.ON && log.finerable()) log.finer("Add component definition: name="+name);

			noELnorEmpty("class", clsnm, pi);

			final ComponentDefinitionImpl cdi =
				new ComponentDefinitionImpl(null, pgdef, name, (Class)null);
			cdi.setCurrentDirectory(getLocator().getDirectory());
				//mold URI requires it
			compdef = cdi;
			compdef.setImplementationClass(clsnm);
				//Resolve later since might be defined in zscript
		}

		pgdef.addComponentDefinition(compdef);

		String moldURI = (String)params.remove("moldURI");
		if (moldURI == null) moldURI = (String)params.remove("mold-uri"); //backward comaptible (2.4.x)
		if (!isEmpty(moldURI)) {
			throw new UnsupportedOperationException("moldURI not supported in 5.0. Use lang-addon.xml instead");
		}

		compdef.setApply((String)params.remove("apply"));

		for (Iterator e = params.entrySet().iterator(); e.hasNext();) {
			final Map.Entry me = (Map.Entry)e.next();
			compdef.addProperty((String)me.getKey(), (String)me.getValue());
		}
	}
	/** Parse the evaluator directive. */
	private static void parseEvaluatorDirective(PageDefinition pgdef,
	ProcessingInstruction pi, Map params) throws Exception {
		final String clsnm = (String)params.remove("class");
		if (clsnm != null && clsnm.length() > 0) {
			noELnorEmpty("class", clsnm, pi);
			pgdef.setExpressionFactoryClass(locateClass(clsnm));
		} else { //name has the lower priorty
			final String nm = (String)params.remove("name");
			if (nm != null)
				pgdef.setExpressionFactoryClass(Evaluators.getEvaluatorClass(nm));
		}

		final String imports = (String)params.remove("import");
		if (imports != null && imports.length() > 0) {
			Collection ims = CollectionsX.parse(null, imports, ',', false); //No EL
			for (Iterator it = ims.iterator(); it.hasNext();) {
				final String im = (String)it.next();

				final int k = im.indexOf('=');
				String nm = k > 0 ? im.substring(0, k).trim(): null;
				String cn = (k >= 0 ? im.substring(k + 1): im).trim();

				if (cn.length() != 0) {
					final Class cs = locateClass(cn);
					if (nm == null || nm.length() == 0) {
						final int j = cn.lastIndexOf('.');
						nm = j >= 0 ? cn.substring(j + 1): cn;
					}
					pgdef.addExpressionImport(nm, cs);
				}
			}
		}
	}
	/** Parse the XEL method. */
	private static void parseXelMethod(PageDefinition pgdef,
	ProcessingInstruction pi, Map params) throws Exception {
		final String prefix = (String)params.remove("prefix");
		noELnorEmpty("prefix", prefix, pi);
		final String nm = (String)params.remove("name");
		noELnorEmpty("name", nm, pi);
		final String clsnm = (String)params.remove("class");
		noELnorEmpty("class", clsnm, pi);
		final String sig = (String)params.remove("signature");
		noELnorEmpty("signature", sig, pi);

		final Method mtd;
		try {
			final Class cls = Classes.forNameByThread(clsnm);
			mtd = Classes.getMethodBySignature(cls, sig, null);
		} catch (ClassNotFoundException ex) {
			throw new UiException("Class not found: "+clsnm+", "+pi.getLocator());
		} catch (Exception ex) {
			throw new UiException("Method not found: "+sig+" in "+clsnm+", "+pi.getLocator());
		}
		if ((mtd.getModifiers() & Modifier.STATIC) == 0)
			throw new UiException("Not a static method: "+mtd);

		pgdef.addXelMethod(prefix, nm, new MethodFunction(mtd));
	}
	private static void noELnorEmpty(String nm, String val, Item item)
	throws UiException {
		if (isEmpty(val))
			throw new UiException(nm + " cannot be empty, "+item.getLocator());
		noEL(nm, val, item);
	}
	private static void noEL(String nm, String val, Item item)
	throws UiException {
		if (val != null && val.indexOf("${") >= 0)
			throw new UiException(nm+" does not support EL expressions, "+item.getLocator());
	}
	/** Checks whether the value is an empty string.
	 * Note: Like {@link #noEL}, it is OK to be null!!
	 */
	private static void noEmpty(String nm, String val, Item item)
	throws UiException {
		if (val != null && val.length() == 0)
			throw new UiException(nm+" cannot be empty, "+item.getLocator());
	}
	private String toAbsoluteURI(String uri, boolean allowEL) {
		if (uri != null && uri.length() > 0) {
			final char cc = uri.charAt(0);
			if (cc != '/' && cc != '~' && (!allowEL || uri.indexOf("${") < 0)
			&& !Servlets.isUniversalURL(uri)) {
				final String dir = getLocator().getDirectory();
				if (dir != null && dir.length() > 0)
					return dir.charAt(dir.length() - 1) == '/' ?
							dir + uri: dir + '/' + uri;
			}
		}
		return uri;
	}

	/** Parses the specified elements.
	 * @param bNativeContent whether to consider the child element all native
	 * It is true if a component definition with text-as is found
	 */
	private void parse(PageDefinition pgdef, NodeInfo parent,
	Collection items, AnnotationHelper annHelper, boolean bNativeContent)
	throws Exception {
		final ComponentInfo parentInfo =
			parent instanceof ComponentInfo ? (ComponentInfo)parent: null;
		for (Iterator it = items.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof Element) {
				parse(pgdef, bNativeContent ? parentInfo: parent,
					(Element)o, annHelper, bNativeContent);
			} else if (o instanceof ProcessingInstruction) {
				parse(pgdef, (ProcessingInstruction)o);
			} else if ((o instanceof Text) || (o instanceof CData)) {
				String label = ((Item)o).getText(),
					trimLabel = label.trim();;
				if (label.length() == 0)
					continue;

				LanguageDefinition parentlang = getLanguageDefinition(parent);
				if (parentlang == null)
					parentlang = pgdef.getLanguageDefinition();

				ComponentInfo pi = parentInfo;
				while (pi instanceof ZkInfo) {
					NodeInfo n = pi.getParent();
					if (n instanceof ComponentInfo) {
						pi = (ComponentInfo)n;
					} else {
						pi = null;
						break;
					}
				}

				if (isZkSwitch(parent)) {
					if (trimLabel.length() == 0)
						continue;
					throw new UiException("Only <zk> can be used in <zk switch>, "+((Item)o).getLocator());
				}

				//Ingore blank text if no need to preserved
				if (trimLabel.length() == 0
				&& pi != null && !pi.isBlankPreserved()
				&& !(pi instanceof NativeInfo))
					continue;

				//consider as a label
				if (pi instanceof NativeInfo) {
					parentInfo.appendChild(
						new TextInfo(pgdef.getEvaluatorRef(), label));
						//Don't trim if native (3.5.0)
				} else {
					final String textAs =
						parentInfo != null ? parentInfo.getTextAs(): null;
					if (textAs != null) {
						parentInfo.addProperty(textAs, trimLabel, null);
					} else {
						if (isTrimLabel() && !parentlang.isRawLabel()) {
							if (trimLabel.length() == 0)
								continue; //ignore
							label = trimLabel;
						}
						final ComponentInfo labelInfo =
							parentlang.newLabelInfo(parentInfo, label);
						if (trimLabel.length() == 0)
							labelInfo.setReplaceableText(label); //yes, it can be replaced by a text
					}
				}
			}
		}
	}
	/** Returns whether to trim the leading and trailing whitespaces 
	 * of labels.
	 * <p>Default: false since 3.0.4.
	 *
	 * <p>If you want to trim like 3.0.4 and ealier did, you can specify
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

	private static final
	LanguageDefinition getLanguageDefinition(NodeInfo node) {
		for (; node != null; node = node.getParent()) {
			if (node instanceof ComponentInfo) {
				LanguageDefinition langdef =
					((ComponentInfo)node).getLanguageDefinition();
				if (langdef != null)
					return langdef;
			} else if (node instanceof PageDefinition) {
				return ((PageDefinition)node).getLanguageDefinition();
			}
		}
		return null;
	}

	/** Parse an component definition specified in the given element.
	 * @param bNativeContent whether to consider the child element all native
	 * It is true if a component definition with text-as is found
	 */
	private void parse(PageDefinition pgdef, NodeInfo parent,
	Element el, AnnotationHelper annHelper, boolean bNativeContent)
	throws Exception {
		final String nm = el.getLocalName();
		final Namespace ns = el.getNamespace();
		final String pref = ns != null ? ns.getPrefix(): "";
		final String uri = ns != null ? ns.getURI(): "";
		LanguageDefinition langdef = pgdef.getLanguageDefinition();
		if ("zscript".equals(nm) && isZkElement(langdef, nm, pref, uri)) {
			parseZScript(parent, el, annHelper);
		} else if ("attribute".equals(nm) && isZkElement(langdef, nm, pref, uri)) {
			if (!(parent instanceof ComponentInfo))
				throw new UiException("<attribute> cannot be the root element, "+el.getLocator());

			parseAttribute(pgdef, (ComponentInfo)parent, el, annHelper);
		} else if ("custom-attributes".equals(nm) && isZkElement(langdef, nm, pref, uri)) {
			parseCustomAttributes(langdef, parent, el, annHelper);
		} else if ("variables".equals(nm) && isZkElement(langdef, nm, pref, uri)) {
			parseVariables(langdef, parent, el, annHelper);
		} else if (LanguageDefinition.ANNO_NAMESPACE.equals(uri)) {
			parseAnnotation(el, annHelper);
		} else {
			//if (D.ON && log.debugable()) log.debug("component: "+nm+", ns:"+ns);
			final ComponentInfo compInfo;
			final boolean bzk =
				"zk".equals(nm) && isZkElement(langdef, nm, pref, uri);
			if (bzk) {
				if (annHelper.clear())
					log.warning("Annotations are ignored since <zk> doesn't support them, "+el.getLocator());
				compInfo = new ZkInfo(parent); 
			} else {
				if (isZkSwitch(parent))
					throw new UiException("Only <zk> can be used in <zk switch>, "+el.getLocator());

				boolean prefRequired =
					uri.startsWith(LanguageDefinition.NATIVE_NAMESPACE_PREFIX);
				boolean bNative = bNativeContent || prefRequired ||
					LanguageDefinition.NATIVE_NAMESPACE.equals(uri);

				if (!bNative && langdef.isNative()
				&& !langdef.getNamespace().equals(uri))
					bNative = prefRequired =
						("".equals(pref) && "".equals(uri))
						|| !LanguageDefinition.exists(uri);
					//Spec: if pref/URI not specified => native
					//		if uri unknown => native

				if (bNative) {
					if (annHelper.clear())
						log.warning("Annotations are ignored since native doesn't support them, "+el.getLocator());

					final NativeInfo ni;
					compInfo = ni = new NativeInfo(
						parent, langdef.getNativeDefinition(),
						prefRequired && pref.length() > 0 ? pref + ":" + nm: nm);

					//add declared namespace if starting with native:
					final Collection dns = el.getDeclaredNamespaces();
					if (!dns.isEmpty())
						addDeclaredNamespace(ni, dns, langdef);
				} else {
					final boolean defaultNS = isDefaultNS(langdef, pref, uri);
					final LanguageDefinition complangdef =
						defaultNS ? langdef: LanguageDefinition.lookup(uri);
					ComponentDefinition compdef =
						defaultNS ? pgdef.getComponentDefinitionMap().get(nm): null;
					if (compdef != null) {
						compInfo = new ComponentInfo(parent, compdef);
					} else if (complangdef.hasComponentDefinition(nm)) {
						compdef = complangdef.getComponentDefinition(nm);
						compInfo = new ComponentInfo(parent, compdef);
						langdef = complangdef;
					} else {
						compdef = complangdef.getDynamicTagDefinition();
						if (compdef == null)
							throw new DefinitionNotFoundException("Component definition not found: "+nm+" in "+complangdef+", "+el.getLocator());
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
			}

			String ifc = null, unless = null,
				forEach = null, forEachBegin = null, forEachEnd = null;
			AnnotationHelper attrAnnHelper = null;
			for (Iterator it = el.getAttributeItems().iterator();
			it.hasNext();) {
				final Attribute attr = (Attribute)it.next();
				final Namespace attrns = attr.getNamespace();
				final String attURI = attrns != null ? attrns.getURI(): "";
				final String attnm = attr.getLocalName();
				final String attval = attr.getValue();
				if (LanguageDefinition.ANNO_NAMESPACE.equals(attURI)) {
					if (bzk) warnWrongZkAttr(attr);
					else {
						if (attrAnnHelper == null)
							attrAnnHelper = new AnnotationHelper();
						attrAnnHelper.addByRawValue(attnm, attval);
					}
				} else if ("apply".equals(attnm) && isZkAttr(langdef, attrns)) {
					if (bzk) warnWrongZkAttr(attr);
					else compInfo.setApply(attval);
				} else if ("forward".equals(attnm) && isZkAttr(langdef, attrns)) {
					if (bzk) warnWrongZkAttr(attr);
					else compInfo.setForward(attval);
				} else if ("if".equals(attnm) && isZkAttr(langdef, attrns)) {
					ifc = attval;
				} else if ("unless".equals(attnm) && isZkAttr(langdef, attrns)) {
					unless = attval;
				} else if ("forEach".equals(attnm) && isZkAttr(langdef, attrns)) {
					forEach = attval;
				} else if ("forEachBegin".equals(attnm) && isZkAttr(langdef, attrns)) {
					forEachBegin = attval;
				} else if ("forEachEnd".equals(attnm) && isZkAttr(langdef, attrns)) {
					forEachEnd = attval;
				} else if ("fulfill".equals(attnm) && isZkAttr(langdef, attrns)) {
					if (bzk) warnWrongZkAttr(attr);
					else compInfo.setFulfill(attval);
				} else if (bzk) {
					if ("switch".equals(attnm) || "choose".equals(attnm)) {
						if (isZkSwitch(parent))
							throw new UiException("<zk "+attnm+"> cannot be used in <zk switch/choose>, "+el.getLocator());
						((ZkInfo)compInfo).setSwitch(attval);
					} else if ("case".equals(attnm)) {
						if (!isZkSwitch(parent))
							throw new UiException("<zk case> can be used only in <zk switch>, "+attr.getLocator());
						((ZkInfo)compInfo).setCase(attval);
					} else if ("when".equals(attnm)) {
						ifc = attval;
					} else {
						final String attPref = attrns != null ? attrns.getPrefix(): null;
						if (!"xmlns".equals(attnm) && !"xml".equals(attnm)
						&& attURI.indexOf("w3.org") < 0
						&& (attPref == null
						 || (!"xmlns".equals(attPref) && !"xml".equals(attPref))))
							warnWrongZkAttr(attr);
					}
				} else if (!("use".equals(attnm) && isZkAttr(langdef, attrns))) {
					final String attPref = attrns != null ? attrns.getPrefix(): "";
					if (!"xmlns".equals(attPref)
					&& !("xmlns".equals(attnm) && "".equals(attPref))
					&& !"http://www.w3.org/2001/XMLSchema-instance".equals(attURI)) {
						final int len = attval.length();
						if (len >= 3 && attval.charAt(0) == '@'
						&& attval.charAt(1) == '{' && attval.charAt(len-1) == '}') { //annotation
							if (attrAnnHelper == null)
								attrAnnHelper = new AnnotationHelper();
							attrAnnHelper.addByCompoundValue(
								attval.substring(2, len -1));
							attrAnnHelper.applyAnnotations(compInfo,
								"self".equals(attnm) ? null: attnm, true);
						} else {
							addAttribute(compInfo, attrns, attnm, attval, null);
							if (attrAnnHelper != null)
								attrAnnHelper.applyAnnotations(compInfo, attnm, true);
						}
					}
				}
			}

			compInfo.setCondition(ConditionImpl.getInstance(ifc, unless));
			compInfo.setForEach(forEach, forEachBegin, forEachEnd);
			annHelper.applyAnnotations(compInfo, null, true);

			parse(pgdef, compInfo, el.getChildren(), annHelper, bNativeContent); //recursive

			//optimize native components
			if (compInfo instanceof NativeInfo
			&& !compInfo.getChildren().isEmpty())
				optimizeNativeInfos((NativeInfo)compInfo);
		}
	}
	private void warnWrongZkAttr(Attribute attr) {
		log.warning("Attribute "+attr.getName()+" ignored in <zk>, "+attr.getLocator());
	}
	private boolean isZkSwitch(NodeInfo nodeInfo) {
		return nodeInfo instanceof ZkInfo && ((ZkInfo)nodeInfo).withSwitch();
	}
	private void parseZScript(NodeInfo parent, Element el,
	AnnotationHelper annHelper) {
		if (el.getAttributeItem("forEach") != null)
			throw new UiException("forEach not applicable to <zscript>, "+el.getLocator());
		if (annHelper.clear())
			log.warning("Annotations are ignored since <zscript> doesn't support them, "+el.getLocator());

		final String
			ifc = el.getAttributeValue("if"),
			unless = el.getAttributeValue("unless"),
			zsrc = el.getAttributeValue("src");
		final boolean
			deferred = "true".equals(el.getAttributeValue("deferred"));

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
			final ZScript zs;
			if (zsrc.indexOf("${") >= 0) {
				zs = new ZScript(parent.getEvaluatorRef(),
					zslang, zsrc, cond, getLocator());
			} else {
				final URL url = getLocator().getResource(zsrc);
				if (url == null) throw new UiException("File not found: "+zsrc+", at "+el.getLocator());
					//don't throw FileNotFoundException since Tomcat 'eats' it
				zs = new ZScript(parent.getEvaluatorRef(), zslang, url, cond);
			}

			if (deferred) zs.setDeferred(true);
			parent.appendChild(zs);
		}

		final String script = el.getText(true);
		if (!isEmpty(script)) {
			final ZScript zs =
				new ZScript(parent.getEvaluatorRef(), zslang, script, cond);
			if (deferred) zs.setDeferred(true);
			parent.appendChild(zs);
		}
	}
	private void parseAttribute(PageDefinition pgdef,
	ComponentInfo parent, Element el, AnnotationHelper annHelper)
	throws Exception {
		if (el.getAttributeItem("forEach") != null)
			throw new UiException("forEach not applicable to attribute, "+el.getLocator());

		//Test if native content is used
		boolean bNativeContent = false;
		for (Iterator it = el.getChildren().iterator(); it.hasNext();)
			if (it.next() instanceof Element) {
				bNativeContent = true;
				break;
			}

		final Attribute attr = el.getAttributeItem(null, "name", 0); //by local name
		if (attr == null)
			throw new UiException("The name attribute required, "+el.getLocator());

		final String attnm = attr.getValue();
		noEmpty("name", attnm, el);
		final ConditionImpl cond = ConditionImpl.getInstance(
				el.getAttributeValue("if"), el.getAttributeValue("unless"));
		if (bNativeContent) {
			if (Events.isValid(attnm))
				throw new UiException("Event listeners not support native content");

			final NativeInfo nativeInfo = new NativeInfo(
				parent.getEvaluatorRef(),
				pgdef.getLanguageDefinition().getNativeDefinition(), "");
			parse(pgdef, nativeInfo, el.getChildren(), annHelper, true);
			parent.addProperty(attnm, nativeInfo, cond);
		} else {
			final String trim = el.getAttributeValue("trim");
			noEL("trim", trim, el);
			final String attval = el.getText(trim != null && "true".equals(trim));
			addAttribute(parent, attr.getNamespace(), attnm, attval, cond);
		}

		annHelper.applyAnnotations(parent, attnm, true);
	}
	private static void parseCustomAttributes(LanguageDefinition langdef,
	NodeInfo parent, Element el, AnnotationHelper annHelper) throws Exception {
		//if (!el.getElements().isEmpty())
		//	throw new UiException("Child elements are not allowed for <custom-attributes>, "+el.getLocator());

		if (parent instanceof PageDefinition)
			throw new UiException("custom-attributes must be used under a component, "+el.getLocator());
		if (annHelper.clear())
			log.warning("Annotations are ignored since <custom-attribute> doesn't support them, "+el.getLocator());

		String ifc = null, unless = null, scope = null, composite = null;
		final Map attrs = new HashMap();
		for (Iterator it = el.getAttributeItems().iterator();
		it.hasNext();) {
			final Attribute attr = (Attribute)it.next();
			final Namespace attrns = attr.getNamespace();
			final String attnm = attr.getLocalName();
			final String attval = attr.getValue();
			if ("if".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				ifc = attval;
			} else if ("unless".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				unless = attval;
			} else if ("scope".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				scope = attval;
			} else if ("composite".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				composite = attval;
			} else if ("forEach".equals(attnm) && isZkElementAttr(langdef, attrns)) {
				throw new UiException("forEach not applicable to <custom-attributes>, "+el.getLocator());
			} else {
				attrs.put(attnm, attval);
			}
		}

		if (!attrs.isEmpty())
			parent.appendChild(new AttributesInfo(
				parent.getEvaluatorRef(),
				attrs, scope, composite, ConditionImpl.getInstance(ifc, unless)));
	}
	private static void parseVariables(LanguageDefinition langdef,
	NodeInfo parent, Element el, AnnotationHelper annHelper) throws Exception {
		//if (!el.getElements().isEmpty())
		//	throw new UiException("Child elements are not allowed for <variables> element, "+el.getLocator());

		if (annHelper.clear())
			log.warning("Annotations are ignored since <variables> doesn't support them, "+el.getLocator());

		String ifc = null, unless = null, composite = null;
		boolean local = false;
		final Map vars = new HashMap();
		for (Iterator it = el.getAttributeItems().iterator();
		it.hasNext();) {
			final Attribute attr = (Attribute)it.next();
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
				throw new UiException("forEach not applicable to <variables>, "+el.getLocator());
			} else {
				vars.put(attnm, attval);
			}
		}
		if (!vars.isEmpty())
			parent.appendChild(new VariablesInfo(
				parent.getEvaluatorRef(),
				vars, local, composite, ConditionImpl.getInstance(ifc, unless)));
	}
	private static void parseAnnotation(Element el, AnnotationHelper annHelper)
	throws Exception {
		if (!el.getElements().isEmpty())
			throw new UiException("Child elements are not allowed for the annotations, "+el.getLocator());

		final Map attrs = new HashMap();
		for (Iterator it = el.getAttributeItems().iterator();
		it.hasNext();) {
			final Attribute attr = (Attribute)it.next();
			attrs.put(attr.getLocalName(), attr.getValue());
		}
		annHelper.add(el.getLocalName(), attrs);
	}

	/** Whether a string is null or empty. */
	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	/** Whether the name space belongs to the default language. */
	private static final
	boolean isDefaultNS(LanguageDefinition langdef, String pref, String uri) {
		return (!langdef.isNative() && "".equals(pref) && "".equals(uri))
			|| langdef.getNamespace().equals(uri);
	}
	/** Returns whether it is a ZK element.
	 * @param pref namespace's prefix
	 * @param uri namespace's URI
	 */
	private static final boolean
	isZkElement(LanguageDefinition langdef, String nm, String pref, String uri) {
		if (isDefaultNS(langdef, pref, uri))
			return !langdef.hasComponentDefinition(nm);
		return LanguageDefinition.ZK_NAMESPACE.equals(uri);
	}
	/** Returns whether it is a ZK attribute (in a non-ZK element).
	 */
	private static final boolean
	isZkAttr(LanguageDefinition langdef, Namespace attrns) {
		//if native we will make sure URI is ZK or lang's namespace
		if (langdef.isNative()
		&& attrns != null && "".equals(attrns.getPrefix()))
			return false; //if navtive, "" means not ZK

		return isZkElementAttr(langdef, attrns);
	}
	/** Similar to {@link #isZkAttr}, except it doesn't care isNative
	 * (i.e., we consider it as a ZK attribute unless a namespace
	 * other than ZK is specified.
	 */
	private static final
	boolean isZkElementAttr(LanguageDefinition langdef, Namespace attrns) {
		if (attrns == null  //not possible; just in case
		|| "".equals(attrns.getPrefix())) //w3c attr: uri is "" if prefix ""
			return true;

		final String uri = attrns.getURI();
		return  LanguageDefinition.ZK_NAMESPACE.equals(uri)
			|| langdef.getNamespace().equals(uri);
	}

	/** Parse an attribute and adds it to the definition.
	 */
	private static void addAttribute(ComponentInfo compInfo, Namespace attrns,
	String name, String value, ConditionImpl cond) throws Exception {
		if (Events.isValid(name)) {
			boolean bZkAttr = attrns == null;
			if (!bZkAttr) {
				final String uri = attrns.getURI();
				if (LanguageDefinition.CLIENT_NAMESPACE.equals(uri)) {
					compInfo.addWidgetListener(name, value, cond);
					return;
				}

				final String pref = attrns.getPrefix();
				LanguageDefinition langdef = compInfo.getLanguageDefinition();
				if (langdef == null)
					bZkAttr = true;
				else if (isDefaultNS(langdef, pref, uri))
					bZkAttr = !langdef.isDynamicReservedAttributes("[event]");
				else
					bZkAttr = LanguageDefinition.ZK_NAMESPACE.equals(uri);
			}
			if (bZkAttr) {
				final ZScript zscript = ZScript.parseContent(value);
				if (zscript.getLanguage() == null)
					zscript.setLanguage(
						compInfo.getPageDefinition().getZScriptLanguage());
						//resolve it here instead of runtime since createComponents
				compInfo.addEventHandler(name, zscript, cond);
				return; //done
			}
		} else {
			final String uri = attrns.getURI();
			if (LanguageDefinition.CLIENT_NAMESPACE.equals(uri)) {
				if (name.length() == 0 || name.charAt(0) != '$')
					throw new UiException("Unknown client attribute: "+name);
					//currently, support only method override
				compInfo.addWidgetMethod(name.substring(1), value, cond);
				return;
			}
		}
		compInfo.addProperty(name, value, cond);
	}

	/** Adds the declared namespaces to the native info, if necessary.
	 */
	private static void addDeclaredNamespace(
	NativeInfo nativeInfo, Collection namespaces, LanguageDefinition langdef) {
		for (Iterator it = namespaces.iterator(); it.hasNext();) {
			final Namespace ns = (Namespace)it.next();
			final String uri = ns.getURI();
			boolean bNatPrefix =
				uri.startsWith(LanguageDefinition.NATIVE_NAMESPACE_PREFIX);
			if (bNatPrefix
			|| (langdef.isNative()
				&& !LanguageDefinition.ZK_NAMESPACE.equals(uri)
				&& !LanguageDefinition.ANNO_NAMESPACE.equals(uri)
				&& !LanguageDefinition.NATIVE_NAMESPACE.equals(uri)
				&& !langdef.getNamespace().equals(uri)))
				nativeInfo.addDeclaredNamespace(
					new Namespace(ns.getPrefix(),
						bNatPrefix ? uri.substring(LanguageDefinition.NATIVE_NAMESPACE_PREFIX.length()):
							uri));
		}
	}
	/** Minimizes the native infos such that UiEngine creates
	 * the minimal number of components.
	 */
	private static void optimizeNativeInfos(NativeInfo compInfo) {
		//Optimize 1: merge to prolog, if the first children are
		//native and have no child
		for (Iterator it = compInfo.getChildren().iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof NativeInfo) {
				final NativeInfo childInfo = (NativeInfo)o;
				if (!childInfo.getChildren().isEmpty())
					break;
				childInfo.setParentDirectly(null);
			} else if (o instanceof ComponentInfo) {
				break;
			}

			compInfo.addPrologChildDirectly(o);
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
					final NativeInfo childInfo = (NativeInfo)o;
					if (!childInfo.getChildren().isEmpty()) {
						it.next();
						break;
					}
					childInfo.setParentDirectly(null);
				} else if (o instanceof ComponentInfo) {
					it.next();
					break;
				}
			}
			while (it.hasNext()) {
				final Object o = it.next();
				compInfo.addEpilogChildDirectly(o);
				it.remove();
			}
		}

		//Optimize 3: merge to split child
		//If there is only one native child, we make it a split child and
		//make all its children (grand-chidren) up one level
		if (compInfo.getChildren().size() == 1
		&& compInfo.getSplitChild() == null /*just in case*/) {
			Iterator it = compInfo.getChildren().iterator();
			final Object o = it.next();
			if (o instanceof NativeInfo) {
				final NativeInfo childInfo = (NativeInfo)o;
				//FUTURE: enhance UiEngineImpl to handle split's forEach
				if (!childInfo.withForEach()) {
					childInfo.setParentDirectly(null);
					compInfo.setSplitChild(childInfo);
					it.remove();

					for (it = childInfo.getChildren().iterator(); it.hasNext();) {
						final Object gc = it.next();
						it.remove();
						compInfo.appendChildDirectly(gc);
						if (gc instanceof ComponentInfo)
							((ComponentInfo)gc).setParentDirectly(compInfo);
					}
				}
			}
		}
	}
}
