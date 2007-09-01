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
package org.zkoss.xel;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.net.URL;

import org.zkoss.lang.D;
import org.zkoss.lang.Classes;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.util.logging.Log;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.util.IDOMs;

/**
 * Utilities to handle taglib.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class Taglibs {
	private static final Log log = Log.lookup(Taglibs.class);

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

		synchronized (FunctionMappers.class) {
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
