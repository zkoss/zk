/* FunctionMappers.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul  5 17:34:27     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.el;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.util.MissingResourceException;
import java.net.URL;
import java.io.Serializable;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.mesg.MCommon;
import org.zkoss.lang.SystemException;
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
import org.zkoss.el.impl.MethodFunction;

/**
 * Utilities for handling FunctionMapper.
 *
 * @author tomyeh
 */
public class FunctionMappers {
	private static final Log log = Log.lookup(FunctionMappers.class);

	private FunctionMappers() {}

	private static final ResourceCache _reces;

	/** An empty function mapper, i.e., it has no function defined at all.
	 */
	public static final FunctionMapper EMPTY_MAPPER = new EmptyMapper();

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
			final URL url = loc.getResource(taglib.getURI());
			if (url == null)
				throw new MissingResourceException(
					"Taglib not found: "+taglib.getURI(), loc.getClass().getName(), taglib.getURI());
			final Map mtds = (Map)_reces.get(url);
//			if (D.ON && log.finerable()) log.finer("Methods for "+taglib.getPrefix()+": "+mtds);
			if (!mtds.isEmpty())
				mappers.put(taglib.getPrefix(), mtds);
		}
		return new MyMapper(mappers);
	}

	/** Loads functions defined in the specified URL.
	 * @return a map of function: (String name, Method mtd).
	 */
	public static final Map loadMethods(URL xmlUrl) throws Exception {
//		if (log.debugable()) log.debug(MCommon.FILE_OPENING, xmlUrl);
		final Element root =
			new SAXBuilder(true, false, true).build(xmlUrl).getRootElement();
			//We have to turn on namespace because xml schema might be used
		return loadMethods(root);
	}
	/** Loads functions defined in the specified DOM.
	 * @return a map of function: (String name, Method mtd).
	 */
	public static final Map loadMethods(Element root) throws Exception {
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
					mtds.put(name, mtd);
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
			throw SystemException.Aide.wrap(ex);
		}
	}

	private static class MyMapper
	implements FunctionMapper, Serializable, Cloneable {
	    private static final long serialVersionUID = 20060622L;

		/** Map(String prefix, Map(name, MethodFunction)). */
		private Map _mappers;

		/** @param mappers Map(String prefix, Map(String name, Method method))
		 */
		private MyMapper(Map mappers) {
			_mappers = mappers;
			for (Iterator it = mappers.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final Map mtds = new HashMap((Map)me.getValue());
					//Note: we have to make a copy since loadMethods shares
					//the same cache
				toMethodFunction(mtds);
				me.setValue(mtds);
			}
		}
		/** Converts a map of (any, Method) to (any, {@link MethodFunction}).
		 */
		private static void toMethodFunction(Map mtds) {
			for (Iterator it = mtds.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final Method mtd = (Method)me.getValue();
				if (mtd != null) me.setValue(new MethodFunction(mtd));
			}
		}

		//-- FunctionMapper --//
		public Function resolveFunction(String prefix, String name) {
			final Map mtds = (Map)_mappers.get(prefix);
			return mtds != null ? (MethodFunction)mtds.get(name): null;
		}
		public Collection getClassNames() {
			return Collections.EMPTY_LIST;
		}
		public Class resolveClass(String name) {
			return null;
		}

		//-- Cloneable --//
		public Object clone() {
			final MyMapper clone;
			try {
				clone = (MyMapper)super.clone();
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
			return o instanceof MyMapper && _mappers.equals(((MyMapper)o)._mappers);
		}
	}
	private static class EmptyMapper
	implements FunctionMapper, Serializable {
	    private static final long serialVersionUID = 20060622L;
		//-- FunctionMapper --//
		public Function resolveFunction(String prefix, String name) {
			return null;
		}
		public Collection getClassNames() {
			return Collections.EMPTY_LIST;
		}
		public Class resolveClass(String name) {
			return null;
		}
	}

	private static class TaglibLoader extends AbstractLoader {
		//-- Loader --//
		public Object load(Object src) throws Exception {
			return loadMethods((URL)src);
		}
	}
}
