/* Taglibs.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 10 16:42:37     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel.taglib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.net.URL;
import java.io.Serializable;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.ResourceCache;
import org.zkoss.util.resource.AbstractLoader;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.util.logging.Log;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.util.IDOMs;
import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.XelException;
import org.zkoss.xel.util.MethodFunction;

/**
 * Utilities to handle taglib.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class Taglibs {
	private static final Log log = Log.lookup(Taglibs.class);

	//Loading of TLD files//
	private static final ResourceCache _reces;

	/** Retursn the function mapper representing a list of {@link Taglib},
	 * or null if taglibs is null or empty.
	 *
	 * <p>The returned mapper is serializable.
	 *
	 * @param loc the locator used to load taglib
	 */
	public static final
	FunctionMapper getFunctionMapper(List taglibs, Locator loc) {
		if (taglibs == null || taglibs.isEmpty())
			return null;

		final Map mappers = new HashMap();
		for (Iterator it = taglibs.iterator(); it.hasNext();) {
			final Taglib taglib = (Taglib)it.next();
			final String uri = taglib.getURI();
			URL url = uri.indexOf("://") > 0 ? null: loc.getResource(uri);
			if (url == null) {
				url = Taglibs.getDefaultURL(uri);
				if (url == null)
					throw new MissingResourceException(
						"Taglib not found: "+uri, loc.getClass().getName(), uri);
			}

			final Map mtds = (Map)_reces.get(url);
//			if (D.ON && log.finerable()) log.finer("Methods for "+taglib.getPrefix()+": "+mtds);
			if (!mtds.isEmpty())
				mappers.put(taglib.getPrefix(), mtds);
		}
		return new Mapper(mappers);
	}

	/** Loads functions defined in the specified URL.
	 * @return a map of function: (String name, Function mtd).
	 */
	public static final Map loadFunctions(URL xmlUrl) throws Exception {
//		if (log.debugable()) log.debug(MCommon.FILE_OPENING, xmlUrl);
		final Element root =
			new SAXBuilder(true, false, true).build(xmlUrl).getRootElement();
			//We have to turn on namespace because xml schema might be used
		return loadFunctions(root);
	}
	/** Loads functions defined in the specified DOM.
	 * @return a map of function: (String name, Function mtd).
	 */
	public static final Map loadFunctions(Element root) throws Exception {
		final Map mtds = new HashMap();
		Exception excp = null;
		for (Iterator it = root.getElements("function").iterator();
		it.hasNext();) {
			final Element e = (Element)it.next();

			final String name = IDOMs.getRequiredElementValue(e, "name");
			final String clsName = IDOMs.getRequiredElementValue(e, "function-class");
			final String sig = IDOMs.getRequiredElementValue(e, "function-signature");
			final Class cls;
			try {
				cls = Classes.forNameByThread(clsName);
			} catch (ClassNotFoundException ex) {
				log.error("Class not found: "+clsName+", "+e.getLocator(), ex);
				excp = ex;
				continue; //to report as many errors as possible
			}

			try {
				final Method mtd = Classes.getMethodBySignature(cls, sig, null);
				if ((mtd.getModifiers() & Modifier.STATIC) != 0)
					mtds.put(name, new MethodFunction(mtd));
				else
					log.error("Not a static method: "+mtd);
			} catch (ClassNotFoundException ex) {
				log.error("Relavant class not found when loading "+clsName+", "+e.getLocator(), ex);
				excp = ex;
				continue;
			} catch (NoSuchMethodException ex) {
				log.error("Method not found in "+clsName+": "+sig+" "+e.getLocator(), ex);
				excp = ex;
				continue;
			} catch (IllegalSyntaxException ex) {
				log.error("Illegal Signature: "+sig+" "+e.getLocator(), ex);
				excp = ex;
				continue;
			}
		}
		if (excp != null)
			throw excp;
		return mtds;
	}

	static {
		try {
 			_reces = new ResourceCache(new TaglibLoader());
 			_reces.setCheckPeriod(30*60*1000);
 		} catch (Exception ex) {
			throw XelException.Aide.wrap(ex);
		}
	}

	private static class Mapper
	implements FunctionMapper, Serializable, Cloneable {
	    private static final long serialVersionUID = 20060622L;

		/** Map(String prefix, Map(name, MethodFunction)). */
		private Map _mappers;

		/** @param mappers Map(String prefix, Map(String name, Function method))
		 */
		private Mapper(Map mappers) {
			_mappers = mappers;
			for (Iterator it = mappers.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final Map mtds = new HashMap((Map)me.getValue());
					//Note: we have to make a copy since loadFunctions shares
					//the same cache
				me.setValue(mtds);
			}
		}

		//-- FunctionMapper --//
		public Function resolveFunction(String prefix, String name) {
			final Map mtds = (Map)_mappers.get(prefix);
			return mtds != null ? (MethodFunction)mtds.get(name): null;
		}

		//-- Cloneable --//
		public Object clone() {
			final Mapper clone;
			try {
				clone = (Mapper)super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError();
			}

			clone._mappers = new HashMap(clone._mappers);
			for (Iterator it = clone._mappers.entrySet().iterator();
			it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				me.setValue(new HashMap((Map)me.getValue()));
			}
			return clone;
		}

		//Object//
		public int hashCode() {
			return _mappers.hashCode();
		}
		public boolean equals(Object o) {
			return o instanceof Mapper && _mappers.equals(((Mapper)o)._mappers);
		}
		public String toString() {
			return '[' + getClass().getName() + ": " + _mappers.keySet() +']';
		}
	}

	private static class TaglibLoader extends AbstractLoader {
		//-- Loader --//
		public Object load(Object src) throws Exception {
			return loadFunctions((URL)src);
		}
	}

//----------------------------------//
	//Mapping of URI to TLD files//
	/** The default TLD files: Map(String uri, URL location). */
	private static Map _defUrls;

	/** Returns the URL associated with the specified taglib URI,
	 * or null if not found.
	 *
	 * @param uri the URI of taglib that are defined as
	 * the taglib-uri element in the /metainfo/tld/config.xml file.
	 * Both config.xml and TLD files must be locatable by the class
	 * loader (i.e., must be part of class path).
	 */
	public static final URL getDefaultURL(String uri) {
		return (URL)getDefaultTLDs().get(uri);
	}
	/** Loads the default TLD files defined in /metainfo/tld/config.xml
	 */
	private static final Map getDefaultTLDs() {
		if (_defUrls != null)
			return _defUrls;

		synchronized (Taglibs.class) {
			if (_defUrls != null)
				return _defUrls;

			final Map urls = new HashMap();
			try {
				final ClassLocator loc = new ClassLocator();
				for (Enumeration en = loc.getResources("metainfo/tld/config.xml");
				en.hasMoreElements();) {
					final URL url = (URL)en.nextElement();
					if (log.debugable()) log.debug("Loading "+url);
					try {
						final Document doc = new SAXBuilder(false, false, true).build(url);
						if (checkVersion(url, doc))
							parseConfig(urls, doc.getRootElement(), loc);
					} catch (Exception ex) {
						log.error("Failed to parse "+url, ex); //keep running
					}
				}
			} catch (Exception ex) {
				log.error(ex); //keep running
			}
			return _defUrls = urls.isEmpty() ? Collections.EMPTY_MAP: urls;
		}
	}
	/** Parse config.xml. */
	private static void parseConfig(Map urls, Element root, Locator loc) {
		for (Iterator it = root.getElements("taglib").iterator();
		it.hasNext();) {
			final Element el = (Element)it.next();
			final String s = IDOMs.getRequiredElementValue(el, "taglib-location");
			final URL url = loc.getResource(s.startsWith("/") ? s.substring(1): s);
			if (url != null) {
				urls.put(
					IDOMs.getRequiredElementValue(el, "taglib-uri"), url);
			} else {
				log.error("taglib-location not found, "+el.getLocator());
			}
		}
	}
	/** Checks and returns whether the loaded document's version is correct.
	 */
	private static boolean checkVersion(URL url, Document doc) throws Exception {
		final Element el = doc.getRootElement().getElement("version");
		if (el != null) {
			final String clsnm = IDOMs.getRequiredElementValue(el, "version-class");
			final String uid = IDOMs.getRequiredElementValue(el, "version-uid");
			final Class cls = Classes.forNameByThread(clsnm);
			final Field fld = cls.getField("UID");
			final String uidInClass = (String)fld.get(null);
			if (uid.equals(uidInClass)) {
				return true;
			} else {
				log.info("Ignore "+url+"\nCause: version not matched; expected="+uidInClass+", xml="+uid);
				return false;
			}
		} else {
			log.info("Ignore "+url+"\nCause: version not specified");
			return false; //backward compatible
		}
	}
}
