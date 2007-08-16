/* Parser.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 14:26:18     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.io.File;
import java.io.Reader;
import java.net.URL;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.lang.PotentialDeadLockException;
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
import org.zkoss.idom.util.IDOMs;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.el.Taglib;
import org.zkoss.web.servlet.Servlets;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Condition;
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
		final List pis = new LinkedList();
		final List imports = new LinkedList();
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
				if (isEmpty(src))
					throw new UiException("The src attribute is required, "+pi.getLocator());
				if (!params.isEmpty())
					log.warning("Ignored unknown attributes: "+params.keySet()+", "+pi.getLocator());
				noEL("src", src, pi);
				imports.add(src);
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
				final String path = (String)it.next();
				try {
					final PageDefinition pd = uf.getPageDefinition(ri, path);
					if (pd == null)
						throw new UiException("Import page not found: "+path);
					pgdef.imports(pd);
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
		if (root != null) parse(pgdef, pgdef, root, new AnnotationHelper());
		return pgdef;
	}
	private static Class locateClass(String clsnm) throws Exception {
		try {
			return Classes.forNameByThread(clsnm);
		} catch (ClassNotFoundException ex) {
			throw new ClassNotFoundException("Class not found: "+clsnm, ex);
		}
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
		if ("taglib".equals(target)) {
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
		} else if ("init".equals(target)) {
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
					zs = new ZScript(zslang, zsrc, null, getLocator()); //URL in EL
				} else {
					final URL url = getLocator().getResource(zsrc);
					if (url == null) throw new UiException("File not found: "+zsrc+", at "+pi.getLocator());
						//don't throw FileNotFoundException since Tomcat 'eats' it
					zs = new ZScript(zslang, url, null);
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
		} else if ("variable-resolver".equals(target)) {
			final String clsnm = (String)params.remove("class");
			if (isEmpty(clsnm))
				throw new UiException("The class attribute is required, "+pi.getLocator());
			if (!params.isEmpty())
				log.warning("Ignored unknown attributes: "+params.keySet()+", "+pi.getLocator());

			pgdef.addVariableResolverInfo(
				clsnm.indexOf("${") >= 0 ? //class supports EL
					new VariableResolverInfo(clsnm):
					new VariableResolverInfo(locateClass(clsnm)));
		} else if ("component".equals(target)) { //declare a component
			final String name = (String)params.remove("name");
			if (isEmpty(name)) throw new UiException("name is required, "+pi.getLocator());
			noEL("name", name, pi); //note: macro-uri supports EL

			final String macroURI = (String)params.remove("macro-uri");
			final String extds = (String)params.remove("extends");
			final String clsnm = (String)params.remove("class");
			ComponentDefinition compdef;
			if (macroURI != null) {
				//if (D.ON && log.finerable()) log.finer("macro component definition: "+name);

				final String inline = (String)params.remove("inline");
				noEL("inline", inline, pi);
				noEL("macro-uri", macroURI, pi);
					//no EL because pagedef must be loaded to resolve
					//the impl class before creating an instance of macro

				final boolean bInline = "true".equals(inline);
				compdef = pgdef.getLanguageDefinition().getMacroDefinition(
					name, toAbsoluteURI(macroURI, false), bInline, true);
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

				if (isEmpty(clsnm)) throw new UiException("class is required, "+pi.getLocator());
				noEL("class", clsnm, pi);

				final ComponentDefinitionImpl cdi =
					new ComponentDefinitionImpl(null, name, (Class)null);
				cdi.setCurrentDirectory(getLocator().getDirectory());
					//mold URI requires it
				compdef = cdi;
				compdef.setImplementationClass(clsnm);
					//Resolve later since might be defined in zscript
			}

			pgdef.addComponentDefinition(compdef);

			final String moldnm = (String)params.remove("mold-name");
			noEL("mold-name", moldnm, pi);
			final String moldURI = (String)params.remove("mold-uri");
			if (!isEmpty(moldURI))
				compdef.addMold(isEmpty(moldnm) ? "default": moldnm,
					toAbsoluteURI(moldURI, true));
			for (Iterator e = params.entrySet().iterator(); e.hasNext();) {
				final Map.Entry me = (Map.Entry)e.next();
				compdef.addProperty((String)me.getKey(), (String)me.getValue());
			}
		} else if ("link".equals(target) || "meta".equals(target)) { //declare a header element
			pgdef.addHeaderInfo(new HeaderInfo(target, params));
		} else if ("page".equals(target)) {
			parsePageDirective(pgdef, pi, params);
		} else if ("import".equals(target)) { //import
			throw new UiException("The import directive can be used only at the top level, "+pi.getLocator());
		} else {
			log.warning("Unknown processing instruction: "+target+", "+pi.getLocator());
		}
	}
	/** Process the page directive. */
	private static void parsePageDirective(PageDefinition pgdef,
	ProcessingInstruction pi, Map params) {
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
			} else if ("zscript-language".equals(nm)) {
				if (isEmpty(val))
					throw new UiException("zscript-language cannot be empty, "+pi.getLocator());
				noEL("zscript-language", val, pi);
				pgdef.setZScriptLanguage(val);
			} else if (nm.startsWith("xmlns:") || "xmlns".equals(nm)) {
				pgdef.setRootAttribute(nm, val);
			} else {
				log.warning("Ignored unknown attribute: "+nm+", "+pi.getLocator());
			}
		}
	}
	private static void noEL(String nm, String val, Item item)
	throws UiException {
		if (val != null && val.indexOf("${") >= 0)
			throw new UiException(nm+" does not support EL expressions, "+item.getLocator());
	}
	/** Checks whether the value is an empty string.
	 * Note: Like {@link #noEL}, it is OK to be null!!
	 * To check neither null nor empty, use IDOMs.getRequiredAttribute.
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
	 */
	private void parse(PageDefinition pgdef, NodeInfo parent,
	Collection items, AnnotationHelper annHelper)
	throws Exception {
		for (Iterator it = items.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof Element) {
				parse(pgdef, parent, (Element)o, annHelper);
			} else if (o instanceof ProcessingInstruction) {
				parse(pgdef, (ProcessingInstruction)o);
			} else if ((o instanceof Text) || (o instanceof CData)) {
				String label = ((Item)o).getText(),
					trimLabel = label.trim();;

				LanguageDefinition parentlang = getLanguageDefinition(parent);
				if (parentlang == null)
					parentlang = pgdef.getLanguageDefinition();

				if (trimLabel.length() > 0) { //consider as a label
					final ComponentInfo parentInfo = (ComponentInfo)parent;
					if (parentInfo.getComponentDefinition().isNative()) {
						//TODO: merge to prolog if parentInfo has no child
						new ComponentInfo(parentInfo,
							parentlang.getNativeDefinition(), null)
							.addProperty("prolog", trimLabel, null);
					} else {
						final String textAs = parentInfo.getTextAs();
						if (textAs != null) {
							parentInfo.addProperty(textAs, trimLabel, null);
						} else {
							if (!parentlang.isRawLabel())
								label = trimLabel;
							parentlang.newLabelInfo(parentInfo, label);
						}
					}
				}
			}
		}
	}

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
	 */
	private void parse(PageDefinition pgdef, NodeInfo parent,
	Element el, AnnotationHelper annHelper)
	throws Exception {
		final String nm = el.getLocalName();
		final Namespace ns = el.getNamespace();
		final String pref = ns != null ? ns.getPrefix(): "";
		final String uri = ns != null ? ns.getURI(): "";
		final LanguageDefinition langdef = pgdef.getLanguageDefinition();
		if ("zscript".equals(nm) && isZkElement(langdef, nm, pref, uri)) {
			parseZScript(parent, el, annHelper);
		} else if ("attribute".equals(nm) && isZkElement(langdef, nm, pref, uri)) {
			if (!(parent instanceof ComponentInfo))
				throw new UiException("<attribute> cannot be the root element, "+el.getLocator());

			parseAttribute((ComponentInfo)parent, el, annHelper);
		} else if ("custom-attributes".equals(nm) && isZkElement(langdef, nm, pref, uri)) {
			parseCustomAttributes(parent, el, annHelper);
		} else if ("variables".equals(nm) && isZkElement(langdef, nm, pref, uri)) {
			parseVariables(parent, el, annHelper);
		} else if (LanguageDefinition.ANNO_NAMESPACE.equals(uri)) {
			parseAnnotation(el, annHelper);
		} else {
			//if (D.ON && log.debugable()) log.debug("component: "+nm+", ns:"+ns);

			final ComponentInfo compInfo;
			if ("zk".equals(nm) && isZkElement(langdef, nm, pref, uri)) {
				if (annHelper.clear())
					log.warning("Annotations are ignored since <zk> doesn't support them, "+el.getLocator());
				compInfo = new ComponentInfo(parent, ComponentDefinition.ZK); 
			} else if (LanguageDefinition.NATIVE_NAMESPACE.equals(uri)) {
				if (annHelper.clear())
					log.warning("Annotations are ignored since inline doesn't support them, "+el.getLocator());
				compInfo = new ComponentInfo(
					parent, langdef.getNativeDefinition(), nm);
			} else {
				if (LanguageDefinition.ZK_NAMESPACE.equals(uri))
					throw new UiException("Unknown ZK component: "+el+", "+el.getLocator());

				final LanguageDefinition complangdef;
				if (isDefault(langdef, pref, uri)) {
					complangdef = langdef;
				} else {
					complangdef = LanguageDefinition.lookup(uri);
				}

				ComponentDefinition compdef =
					pgdef.getComponentDefinitionMap().get(nm);
				if (compdef != null) {
					compInfo = new ComponentInfo(parent, compdef);
				} else if (complangdef.hasComponentDefinition(nm)) {
					compdef = complangdef.getComponentDefinition(nm);
					compInfo = new ComponentInfo(parent, compdef);
				} else {
					compdef = complangdef.getDynamicTagDefinition();
					if (compdef == null)
						throw new DefinitionNotFoundException("Component definition not found: "+nm+" in "+complangdef+", "+el.getLocator());
					compInfo = new ComponentInfo(parent, compdef, nm);
				}

				//process use first because addProperty needs it
				final String use = el.getAttributeValue("use");
				if (use != null) {
					noEmpty("use", use, el);
					noEL("use", use, el);
					compInfo.setImplementationClass(use);
						//Resolve later since might defined in zscript
				}
			}

			String ifc = null, unless = null,
				forEach = null, forEachBegin = null, forEachEnd = null;
			AnnotationHelper attrAnnHelper = null;
			for (Iterator it = el.getAttributeItems().iterator();
			it.hasNext();) {
				final Attribute attr = (Attribute)it.next();
				final Namespace attrns = attr.getNamespace();
				final String attnm = attr.getLocalName();
				final String attval = attr.getValue();
				if (attrns != null
				&& LanguageDefinition.ANNO_NAMESPACE.equals(attrns.getURI())) {
					if (attrAnnHelper == null)
						attrAnnHelper = new AnnotationHelper();
					attrAnnHelper.addByRawValue(attnm, attval);
				} else if ("if".equals(attnm)) {
					ifc = attval;
				} else if ("unless".equals(attnm)) {
					unless = attval;
				} else if ("forEach".equals(attnm)) {
					forEach = attval;
				} else if ("forEachBegin".equals(attnm)) {
					forEachBegin = attval;
				} else if ("forEachEnd".equals(attnm)) {
					forEachEnd = attval;
				} else if ("fulfill".equals(attnm)) {
					compInfo.setFulfill(attval);
				} else if (!"use".equals(attnm)) {
					final Namespace attns = attr.getNamespace();
					final String attpref = attns != null ? attns.getPrefix(): "";
					final String attruri = attns != null ? attns.getURI(): "";
					if (!"xmlns".equals(attpref)
					&& !("xmlns".equals(attnm) && "".equals(attpref))
					&& !"http://www.w3.org/2001/XMLSchema-instance".equals(attruri)) {
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
							addAttribute(compInfo, attns, attnm, attval, null);
							if (attrAnnHelper != null)
								attrAnnHelper.applyAnnotations(compInfo, attnm, true);
						}
					}
				}
			}

			compInfo.setCondition(ConditionImpl.getInstance(ifc, unless));
			compInfo.setForEach(forEach, forEachBegin, forEachEnd);
			annHelper.applyAnnotations(compInfo, null, true);

			parse(pgdef, compInfo, el.getChildren(), annHelper); //recursive
		}
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

		final Condition cond = ConditionImpl.getInstance(ifc, unless);
		if (!isEmpty(zsrc)) { //ignore empty (not error)
			final ZScript zs;
			if (zsrc.indexOf("${") >= 0) {
				zs = new ZScript(zslang, zsrc, cond, getLocator());
			} else {
				final URL url = getLocator().getResource(zsrc);
				if (url == null) throw new UiException("File not found: "+zsrc+", at "+el.getLocator());
					//don't throw FileNotFoundException since Tomcat 'eats' it
				zs = new ZScript(zslang, url, cond);
			}

			if (deferred) zs.setDeferred(true);
			parent.appendChild(zs);
		}

		final String script = el.getText(true);
		if (!isEmpty(script)) {
			final ZScript zs = new ZScript(zslang, script, cond);
			if (deferred) zs.setDeferred(true);
			parent.appendChild(zs);
		}
	}
	private void parseAttribute(ComponentInfo parent, Element el,
	AnnotationHelper annHelper) throws Exception {
		//if (!el.getElements().isEmpty())
		//	throw new UiException("Child elements are not allowed for the attribute element, "+el.getLocator());

		if (el.getAttributeItem("forEach") != null)
			throw new UiException("forEach not applicable to attribute, "+el.getLocator());

		//FUTURE: handling the namespace of if and unless
		final String attnm = IDOMs.getRequiredAttributeValue(el, "name");
		final String trim = el.getAttributeValue("trim");
		noEL("trim", trim, el);
		final String attval = el.getText(trim != null && "true".equals(trim));
		addAttribute(parent, null, attnm, attval,
			ConditionImpl.getInstance(
				el.getAttributeValue("if"), el.getAttributeValue("unless")));

		annHelper.applyAnnotations(parent, attnm, true);
	}
	private void parseCustomAttributes(NodeInfo parent, Element el,
	AnnotationHelper annHelper) throws Exception {
		//if (!el.getElements().isEmpty())
		//	throw new UiException("Child elements are not allowed for <custom-attributes>, "+el.getLocator());

		if (parent instanceof PageDefinition)
			throw new UiException("custom-attributes must be used under a component, "+el.getLocator());
		if (annHelper.clear())
			log.warning("Annotations are ignored since <custom-attribute> doesn't support them, "+el.getLocator());

		String ifc = null, unless = null, scope = null;
		final Map attrs = new HashMap();
		for (Iterator it = el.getAttributeItems().iterator();
		it.hasNext();) {
			final Attribute attr = (Attribute)it.next();
			final String attnm = attr.getLocalName();
			final String attval = attr.getValue();
			if ("if".equals(attnm)) {
				ifc = attval;
			} else if ("unless".equals(attnm)) {
				unless = attval;
			} else if ("scope".equals(attnm)) {
				scope = attval;
			} else if ("forEach".equals(attnm)) {
				throw new UiException("forEach not applicable to <custom-attributes>, "+el.getLocator());
			} else {
				attrs.put(attnm, attval);
			}
		}

		if (!attrs.isEmpty())
			parent.appendChild(new AttributesInfo(
				attrs, scope, ConditionImpl.getInstance(ifc, unless)));
	}
	private void parseVariables(NodeInfo parent, Element el,
	AnnotationHelper annHelper) throws Exception {
		//if (!el.getElements().isEmpty())
		//	throw new UiException("Child elements are not allowed for <variables> element, "+el.getLocator());

		if (el.getAttributeItem("forEach") != null)
			throw new UiException("forEach not applicable to <variables>, "+el.getLocator());
		if (annHelper.clear())
			log.warning("Annotations are ignored since <variables> doesn't support them, "+el.getLocator());

		String ifc = null, unless = null;
		boolean local = false;
		final Map vars = new HashMap();
		for (Iterator it = el.getAttributeItems().iterator();
		it.hasNext();) {
			final Attribute attr = (Attribute)it.next();
			final String attnm = attr.getLocalName();
			final String attval = attr.getValue();
			if ("if".equals(attnm)) {
				ifc = attval;
			} else if ("unless".equals(attnm)) {
				unless = attval;
			} else if ("local".equals(attnm)) {
				local = "true".equals(attval);
			} else if ("forEach".equals(attnm)) {
				throw new UiException("forEach not applicable to <variables>, "+el.getLocator());
			} else {
				vars.put(attnm, attval);
			}
		}
		if (!vars.isEmpty())
			parent.appendChild(new VariablesInfo(
				vars, local, ConditionImpl.getInstance(ifc, unless)));
	}
	private void parseAnnotation(Element el, AnnotationHelper annHelper)
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

	/** Whether the name space belongs to the default language. */
	private static final
	boolean isDefault(LanguageDefinition langdef, String pref, String uri) {
		return ("".equals(pref) && "".equals(uri))
			|| langdef.getNamespace().equals(uri);
	}
	/** Whether a string is null or empty. */
	private static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}
	/** Returns whether the element is conflict with the language definition.
	 * @param pref namespace's prefix
	 * @param uri namespace's URI
	 */
	private static final boolean
	isZkElement(LanguageDefinition langdef, String nm, String pref, String uri) {
		if (isDefault(langdef, pref, uri))
			return !langdef.hasComponentDefinition(nm);
		return LanguageDefinition.ZK_NAMESPACE.equals(uri);
	}

	/** Parse an attribute and adds it to the definition.
	 */
	private void addAttribute(ComponentInfo compInfo, Namespace attrns,
	String name, String value, Condition cond) throws Exception {
		if (Events.isValid(name)) {
			boolean bZkAttr = attrns == null;
			if (!bZkAttr) {
				String pref = attrns.getPrefix(), uri = attrns.getURI();
				LanguageDefinition langdef = compInfo.getLanguageDefinition();
				if (langdef == null)
					bZkAttr = true;
				else if (isDefault(langdef, pref, uri))
					bZkAttr = !langdef.isDynamicReservedAttributes("[event]");
				else
					bZkAttr = LanguageDefinition.ZK_NAMESPACE.equals(uri);
			}
			if (bZkAttr) {
				final ZScript zscript = ZScript.parseContent(value, null);
				if (zscript.getLanguage() == null)
					zscript.setLanguage(
						compInfo.getPageDefinition().getZScriptLanguage());
						//resolve it here instead of runtime since createComponents
				compInfo.addEventHandler(name, zscript, cond);
				return; //done
			}
		}
		compInfo.addProperty(name, value, cond);
	}
}
