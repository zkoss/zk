/* Taglibs.java

	Purpose:

	Description:

	History:
		Fri Aug 10 16:42:37     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.taglib;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;
import org.zkoss.lang.Classes;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.util.resource.AbstractLoader;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.util.resource.XMLResourcesLocator;
import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.XelException;
import org.zkoss.xel.util.MethodFunction;
import org.zkoss.xel.util.TaglibMapper;

/**
 * Utilities to handle taglib.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class Taglibs {
	private static final Logger log = LoggerFactory.getLogger(Taglibs.class);

	//Loading of TLD files//
	private static final ResourceCache<URL, TaglibDefinition> _reces;

	/** Returns the function mapper representing a list of {@link Taglib}
	 * and imports, or null if nothing is loaded.
	 *
	 * <p>The returned mapper is serializable.
	 *
	 * @param taglibs a list of {@link Taglib}.
	 * @param imports a map of imported classes, Map&lt;String nm, Class cls&gt;
	 * Note: imports has the higher priority than import classes defined
	 * in taglibs.
	 * @param loc the locator used to load taglib
	 * @since 3.0.0
	 */
	public static final
	FunctionMapper getFunctionMapper(List<Taglib> taglibs, Map<String, Class<?>> imports, Locator loc) {
		return getFunctionMapper((Collection<Taglib>)taglibs, imports, null, loc);
	}
	/** Returns the function mapper representing a collection of {@link Taglib}
	 * and imports, or null if nothing is loaded.
	 *
	 * <p>The returned mapper is serializable.
	 *
	 * @param taglibs a collection of {@link Taglib}.
	 * @param imports a map of imported classes, Map&lt;String nm, Class cls&gt;
	 * Note: imports has the higher priority than import classes defined
	 * in taglibs.
	 * @param loc the locator used to load taglib
	 * @since 5.0.0
	 */
	public static final
	FunctionMapper getFunctionMapper(Collection<Taglib> taglibs, Map<String, Class<?>> imports, Locator loc) {
		return getFunctionMapper(taglibs, imports, null, loc);
	}
	/** Returns the function mapper representing a list of {@link Taglib},
	 * imports and functions, or null if nothing is loaded.
	 *
	 * <p>The returned mapper is serializable.
	 *
	 * @param taglibs a list of {@link Taglib}.
	 * @param imports a map of imported classes, Map&lt;String nm, Class cls&gt;.
	 * Ignored if null.
	 * Note: imports has the higher priority than import classes defined
	 * in taglibs.
	 * @param funcs a list of three-element arrays,
	 * Map&lt;String prefix, String name, Function func]&gt;.
	 * Ignored if null.
	 * @param loc the locator used to load taglib
	 * @since 3.0.0
	 */
	public static final FunctionMapper getFunctionMapper(List<Taglib> taglibs,
	Map<String, Class<?>> imports, List<FunctionDefinition> funcs, Locator loc) {
		return getFunctionMapper((Collection<Taglib>)taglibs, imports, funcs, loc);
	}
	/** Returns the function mapper representing a collection of {@link Taglib},
	 * imports and functions, or null if nothing is loaded.
	 *
	 * <p>The returned mapper is serializable.
	 *
	 * @param taglibs a collection of {@link Taglib}.
	 * @param imports a map of imported classes, Map&lt;String nm, Class cls&gt;.
	 * Ignored if null.
	 * Note: imports has the higher priority than import classes defined
	 * in taglibs.
	 * @param funcs a collection of three-element arrays,
	 * Map&lt;String prefix, String name, Function func]&gt;.
	 * Ignored if null.
	 * @param loc the locator used to load taglib
	 * @since 5.0.0
	 */
	public static final FunctionMapper getFunctionMapper(Collection<Taglib> taglibs,
	Map<String, Class<?>> imports, Collection<FunctionDefinition> funcs, Locator loc) {
		TaglibMapper mapper = null;
		if (taglibs != null && !taglibs.isEmpty()) {
			mapper = new TaglibMapper();
			for (Taglib taglib: taglibs)
				mapper.load(taglib, loc);
		}

		if (imports != null && !imports.isEmpty()) {
			if (mapper == null)
				mapper = new TaglibMapper();

			for (Iterator it = imports.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				mapper.addClass((String)me.getKey(), (Class)me.getValue());
			}
		}

		if (funcs != null && !funcs.isEmpty()) {
			if (mapper == null)
				mapper = new TaglibMapper();

			for (FunctionDefinition fi: funcs)
				mapper.addFunction(fi.prefix, fi.name, fi.function);
		}
		return mapper;
	}
	/** Returns the function mapper representing a list of {@link Taglib},
	 * or null if nothing is loaded.
	 *
	 * <p>The returned mapper is serializable.
	 *
	 * @param taglibs a list of {@link Taglib}.
	 * @param loc the locator used to load taglib
	 */
	public static final
	FunctionMapper getFunctionMapper(List<Taglib> taglibs, Locator loc) {
		return getFunctionMapper((Collection<Taglib>)taglibs, null, loc);
	}
	/** Returns the function mapper representing a collection of {@link Taglib},
	 * or null if nothing is loaded.
	 *
	 * <p>The returned mapper is serializable.
	 *
	 * @param taglibs a collection of {@link Taglib}.
	 * @param loc the locator used to load taglib
	 * @since 5.0.0
	 */
	public static final
	FunctionMapper getFunctionMapper(Collection<Taglib> taglibs, Locator loc) {
		return getFunctionMapper(taglibs, null, loc);
	}

	/** Loads functions defined in the specified URL.
	 *
	 * <p>Note: this method will cache the result, so next invocation
	 * with the same xmlURL will read directly from the cache.
	 *
	 * @return a map of functions: (String name, Function mtd).
	 */
	public static final Map<String, Function> loadFunctions(URL xmlURL) throws Exception {
		return load(xmlURL).functions;
	}
	/** Loads functions defined in the specified DOM.
	 *
	 * <p>Unlike {@link #loadFunctions(URL)}, this method
	 * doesn't use cache.
	 *
	 * @return a map of function: (String name, Function mtd).
	 */
	public static final Map<String, Function> loadFunctions(Element root) throws Exception {
		return load(root).functions;
	}
	/** Loads functions and imports defined in the specified URL.
	 *
	 * <p>Note: this method will cache the result, so next invocation
	 * with the same xmlURL will read directly from the cache.
	 *
	 * @return the content of the taglib.
	 * @since 6.0.0
	 */
	public static final TaglibDefinition load(URL xmlURL) throws Exception {
		return _reces.get(xmlURL);
	}
	/** Loads functions and imports defined in the specified DOM.
	 *
	 * <p>Unlike {@link #loadFunctions(URL)}, this method
	 * doesn't use cache.
	 *
	 * @return a two-element array
	 * [Map&lt;String nm, Function mtd&gt;, Map&lt;String nm, Class cls&gt;].
	 * The first element is the map of the functions.
	 * The second element is the map of classes to import.
	 * @since 3.0.0
	 */
	public static final TaglibDefinition load(Element root) throws Exception {
		final TaglibDefinition tagdef = new TaglibDefinition();
		Exception excp = null;
		for (Element e: root.getElements("function")) {
			final String name = IDOMs.getRequiredElementValue(e, "name");
			final String clsnm = IDOMs.getRequiredElementValue(e, "function-class");
			final String sig = IDOMs.getRequiredElementValue(e, "function-signature");
			final Class cls;
			try {
				cls = Classes.forNameByThread(clsnm);
			} catch (ClassNotFoundException ex) {
				log.error("Class not found: "+clsnm+", "+e.getLocator(), ex);
				excp = ex;
				continue; //to report as many errors as possible
			}

			try {
				final Method mtd = Classes.getMethodBySignature(cls, sig, null);
				if ((mtd.getModifiers() & Modifier.STATIC) != 0)
					tagdef.functions.put(name, new MethodFunction(mtd));
				else
					log.error("Not a static method: {}", mtd);
			} catch (ClassNotFoundException ex) {
				log.error("Relavant class not found when loading "+clsnm+", "+e.getLocator(), ex);
				excp = ex;
			} catch (NoSuchMethodException ex) {
				log.error("Method not found in "+clsnm+": "+sig+" "+e.getLocator(), ex);
				excp = ex;
			} catch (IllegalSyntaxException ex) {
				log.error("Illegal Signature: "+sig+" "+e.getLocator(), ex);
				excp = ex;
			}
		}

		for (Element e: root.getElements("import")) {
			final String name = IDOMs.getRequiredElementValue(e, "import-name");
			final String clsnm = IDOMs.getRequiredElementValue(e, "import-class");
			try {
				tagdef.classes.put(name, Classes.forNameByThread(clsnm));
			} catch (ClassNotFoundException ex) {
				log.error("Class not found: "+clsnm+", "+e.getLocator(), ex);
				excp = ex;
			}
		}

		if (excp != null)
			throw excp;
		return tagdef;
	}

	static {
		try {
 			_reces = new ResourceCache<>(new TaglibLoader());
 			_reces.setCheckPeriod(30*60*1000);
 		} catch (Exception ex) {
			throw XelException.Aide.wrap(ex);
		}
	}

	private static class TaglibLoader extends AbstractLoader<URL, TaglibDefinition> {
		//-- Loader --//
		public TaglibDefinition load(URL src) throws Exception {
			final Element root =
				new SAXBuilder(true, false, true).build(src).getRootElement();
			return Taglibs.load(root);
		}
	}

//----------------------------------//
	//Mapping of URI to TLD files//
	/** The default TLD files: Map(String uri, URL location). */
	private static final AtomicReference<Map<String, URL>> _defURLsRef = new AtomicReference<>();

	/** Returns the URL associated with the specified taglib URI,
	 * or null if not found.
	 *
	 * @param uri the URI of taglib that are defined as
	 * the taglib-uri element in the /metainfo/tld/config.xml file.
	 * Both config.xml and TLD files must be locatable by the class
	 * loader (i.e., must be part of class path).
	 */
	public static final URL getDefaultURL(String uri) {
		return getDefaultTLDs().get(uri);
	}
	/** Loads the default TLD files defined in /metainfo/tld/config.xml
	 */
	private static final Map<String, URL> getDefaultTLDs() {
		Map<String, URL> defURLs = _defURLsRef.get();
		if (defURLs != null) {
			return defURLs;
		}

		synchronized (Taglibs.class) {
			defURLs = _defURLsRef.get();
			if (defURLs != null) {
				return defURLs;
			}

			final Map<String, URL> urls = new HashMap<String, URL>();
			try {
				final ClassLocator loc = new ClassLocator();
				for (XMLResourcesLocator.Resource res :
								loc.getDependentXMLResources("metainfo/tld/config.xml", "config-name", "depends")) {
					if (log.isDebugEnabled()) log.debug("Loading {}", res.url);
					try {
						if (IDOMs.checkVersion(res.document, res.url))
							parseConfig(urls, res.document.getRootElement(), loc);
					} catch (Exception ex) {
						log.error("Failed to parse {}", res.url, ex); //keep running
					}
				}
			} catch (Exception ex) {
				log.error("", ex); //keep running
			}
			if (urls.isEmpty()) {
				defURLs = Collections.emptyMap();
			} else {
				defURLs = urls;
			}
			_defURLsRef.set(defURLs);
			return defURLs;
		}
	}

	/** Parse config.xml. */
	private static void parseConfig(Map<String, URL> urls, Element root, Locator loc) {
		for (final Element el : root.getElements("taglib")) {
			final String s = IDOMs.getRequiredElementValue(el,
					"taglib-location");
			final URL url = loc.getResource(
					s.startsWith("/") ? s.substring(1) : s);
			if (url != null) {
				urls.put(IDOMs.getRequiredElementValue(el, "taglib-uri"), url);
			} else {
				log.error("{} not found, {}", s, el.getLocator());
			}
		}
	}
}
