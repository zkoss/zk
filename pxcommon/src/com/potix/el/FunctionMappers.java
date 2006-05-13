/* FunctionMappers.java

{{IS_NOTE
	$Id: FunctionMappers.java,v 1.15 2006/02/27 03:41:52 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Jul  5 17:34:27     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.el;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.net.URL;

import javax.servlet.jsp.el.FunctionMapper;

import com.potix.lang.D;
import com.potix.lang.Classes;
import com.potix.mesg.MCommon;
import com.potix.lang.SystemException;
import com.potix.util.IllegalSyntaxException;
import com.potix.util.resource.Locator;
import com.potix.util.resource.ResourceCache;
import com.potix.util.resource.AbstractLoader;
import com.potix.util.resource.ClassLocator;
import com.potix.util.logging.Log;
import com.potix.idom.input.SAXBuilder;
import com.potix.idom.Document;
import com.potix.idom.Element;
import com.potix.idom.util.IDOMs;

/**
 * Utilities for handling FunctionMapper.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.15 $ $Date: 2006/02/27 03:41:52 $
 */
public class FunctionMappers {
	private static final Log log = Log.lookup(FunctionMappers.class);

	private FunctionMappers() {}

	private static final ResourceCache _reces;
	private static final FunctionMapper _default;

	/** Returns the default function mapper provided by i3-el.xml.
	 *
	 * <p>Note: the prefix is assumed to be empty.
	 *
	 * <p>The default function mapper is used only in QL.
	 */
	public static final FunctionMapper getDefault() {
		return _default;
	}

	/** Retursn the function mapper representing a list of {@link Taglib},
	 * or null if taglibs is null or empty.
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
			if (D.ON && log.finerable()) log.finer("Methods for "+taglib.getPrefix()+": "+mtds);
			if (!mtds.isEmpty())
				mappers.put(taglib.getPrefix(), mtds);
		}
		return new FunctionMapper() {
			public Method resolveFunction(String prefix, String name) {
				final Map mtds = (Map)mappers.get(prefix);
				return mtds != null ? (Method)mtds.get(name): null;
			}
		};
	}

	/** Loads functions defined in the specified URL.
	 * @return a map of function: (String name, Method mtd).
	 */
	public static final Map loadMethods(URL xmlUrl) throws Exception {
		if (log.debugable()) log.debug(MCommon.FILE_OPENING, xmlUrl);
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
				log.error("Class not found: "+clsName+", "+e.getLocator());
				excp = ex;
				continue; //to report as many errors as possible
			}
			try {
				final Method mtd = Classes.getMethodBySignature(cls, sig, null);
				mtds.put(name, mtd);
			} catch (ClassNotFoundException ex) {
				log.error("Class not found: "+clsName+", "+e.getLocator());
				excp = ex;
				continue;
			} catch (NoSuchMethodException ex) {
				log.error("Method not found in "+clsName+": "+sig+" "+e.getLocator());
				excp = ex;
				continue;
			} catch (IllegalSyntaxException ex) {
				log.error("Illegal Signature: "+sig+" "+e.getLocator());
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
			final Init init = new Init();
			init.loadFromAllModules();
			if (init.methods.isEmpty()) {
				_default = null;
			} else {
				final Map mtds = init.methods;
				_default = new FunctionMapper() {
					public Method resolveFunction(String prefix, String name) {
						return prefix == null || prefix.length() == 0 ?
							(Method)mtds.get(name): null;
					}
				};
			}

 			_reces = new ResourceCache(new TaglibLoader());
 			_reces.setCheckPeriod(30*60*1000);
 		} catch (Exception ex) {
			throw SystemException.Aide.wrap(ex);
		}
	}

	private static class TaglibLoader extends AbstractLoader {
		//-- Loader --//
		public Object load(Object src) throws Exception {
			return loadMethods((URL)src);
		}
	}
	/** Used to initilizes the builtin objects and methods. */
	private static class Init {
		final Map methods = new HashMap();

		/** Initializes the builtin objects and methods from
		 * /metainfo/module/i3-el.xml of all modules.
		 */
		private final void loadFromAllModules() throws Exception {
			final ClassLocator locator = new ClassLocator();
			for (Iterator it = locator.getDependentXmlResources(
			"metainfo/i3-el.xml", "module-name", "depends").iterator();
			it.hasNext();) {
				final Document doc = (Document)it.next();
				methods.putAll(loadMethods(doc.getRootElement()));
				
			}
		}
	} //Init
}
