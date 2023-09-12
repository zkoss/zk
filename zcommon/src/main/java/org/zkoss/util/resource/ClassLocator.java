/* ClassLocator.java

	Purpose:
		
	Description:
		
	History:
		Tue Aug 30 09:56:06     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.idom.Document;
import org.zkoss.idom.Element;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.util.IDOMs;
import org.zkoss.lang.SystemException;
import org.zkoss.util.CollectionsX;

/**
 * The locator searches the current thread's context class loader,
 * and then this class's class loader.
 *
 * <p>It is important to use this locator if you want to load something
 * in other jar files.
 *
 * <p>Since this locator is used frequently, {@link Locators#getDefault}
 * is provided to return an instance of this class,
 *
 * @author tomyeh
 */
public class ClassLocator implements XMLResourcesLocator {
	private static final Logger log = LoggerFactory.getLogger(ClassLocator.class);
	private static final String[] ZK_MODULES = new String[]{"zkbind", "zkplus", "zk", "zul"};

	public ClassLocator() {
	}

	//XMLResourcesLocator//
	public Enumeration<URL> getResources(String name) throws IOException {
		name = resolveName(name);

		// no need to use Classes.getContextClassLoader() here because of the loading order issue
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl != null) {
			final Enumeration<URL> en = cl.getResources(name);
			if (en.hasMoreElements()) return en;
		}
		cl = ClassLocator.class.getClassLoader();
		if (cl != null) {
			final Enumeration<URL> en = cl.getResources(name);
			if (en.hasMoreElements()) return en;
		}
		return ClassLoader.getSystemResources(name);
	}
	public List<Resource> getDependentXMLResources(String name, String elName,
	String elDepends) throws IOException {
		final Map<String, XMLResource> rcmap = new LinkedHashMap<String, XMLResource>();
		for (Enumeration<URL> en = getResources(name); en.hasMoreElements();) {
			final URL url = en.nextElement();
			final XMLResource xr = new XMLResource(url, elName, elDepends);
			final XMLResource old = rcmap.put(xr.name, xr);
			if (old != null)
				log.warn("Replicate resource: "+xr.name
					+"\nOverwrite "+old.url+"\nwith "+xr.url);
			//it is possible if zcommon.jar is placed in both
			//WEB-INF/lib and shared/lib, i.e., appear twice in the class path
			//We overwrite because the order is the parent class loader first
			//so WEB-INF/lib is placed after
		}
//		if (rcmap.isEmpty() && log.isDebugEnabled()) log.debug("No resource is found for "+name);

		final List<Resource> rcs = new LinkedList<Resource>(); //a list of Document
		final Set<String> resolving = new LinkedHashSet<String>();
			//a set of names used to prevent dead-loop
		while (!rcmap.isEmpty()) {
			final Iterator<XMLResource> it = rcmap.values().iterator();
			final XMLResource xr = it.next();
			it.remove();
			resolveDependency(xr, rcs, rcmap, resolving, elName);
			assert resolving.isEmpty();
		}
		return rcs;
	}
	private static void resolveDependency(XMLResource xr,
	List<Resource> rcs, Map<String, XMLResource> rcmap, Set<String> resolving, String elName) {
		if (!resolving.add(xr.name))
			throw new IllegalStateException("Recusrive reference among "+resolving);
		
		checkCompDenpendency(xr, rcmap, elName);

		for (String nm: xr.depends) {
			final XMLResource dep = rcmap.remove(nm);
			if (dep != null) //not resolved yet
				resolveDependency(dep, rcs, rcmap, resolving, elName); //recursively
		}

		rcs.add(new Resource(xr.url, xr.document));
		resolving.remove(xr.name);

		if (log.isDebugEnabled()) log.debug("Adding resolved resource: "+xr.name);
	}

	private static void checkCompDenpendency(XMLResource xr, Map<String, XMLResource> rcmap, String elName) {
		if (xr.depends.size() > 0) {
			return;
		}

		for (String zkModule : ZK_MODULES) {
			if (zkModule.equals(xr.name)) //if it is not client component
				return;
		}
		Element xrRoot = xr.document.getRootElement();
		Element strictElement = xrRoot.getElement("strict");
		boolean strict = strictElement == null ? false : Boolean.parseBoolean(strictElement.getText(true));
		List<Element> elementList = xrRoot.getElements("component");
		Map<String, Boolean> visited = new HashMap<>(elementList.size());
		for (Element el : elementList) {

			// ignore same lang.xml extending component, such as fileupload to extend button
			visited.put(el.getElement("component-name").getText(), Boolean.TRUE);
			Element anExtends = el.getElement("extends");
			if (anExtends == null || visited.containsKey(anExtends.getText()))
				continue;

			List<Element> ambigComps = findAmbiguousComps(rcmap, el);
			if (!ambigComps.isEmpty()) {
				StringBuilder addonNameBuilder = new StringBuilder();
				for (Element ambigComp : ambigComps) {
					Element ambigRoot = ambigComp.getDocument().getRootElement();
					addonNameBuilder.append("'").append(ambigRoot.getElement(elName).getFirstChild().getNodeValue()).append("', ");
				}
				String message = "In {}, you are extending component {} which is defined in {}please define <depends> element";
				String[] messageArgs = new String[]{xr.url.toString(), el.getElementValue("extends", true), addonNameBuilder.toString()};
				warningMessage(message, messageArgs, strict);
			}
		}
	}

	private static List<Element> findAmbiguousComps(Map<String, XMLResource> rcmap, Element el) {
		List<Element> ambigComps = new ArrayList<Element>();
		for (Map.Entry<String, XMLResource> entry: rcmap.entrySet()) { // find Ambiguous Components
			Element root = entry.getValue().document.getRootElement();
			if (root == null)
				continue;
			for (Element comp : root.getElements("component")) {
				String extendedElName = el.getElementValue("extends", true);
				String compName = comp.getElementValue("component-name", true);
				if (compName != null && compName.equals(extendedElName)) {
					ambigComps.add(comp);
					break;
				}
			}
		}
		return ambigComps;
	}

	private static void warningMessage(String message, String[] args, boolean strict) {
		if (strict) {
			String f = message.replace("{}", "%s");
			String result = String.format(f, (Object []) args);
			throw new SystemException(result);
		} else {
			log.warn(message, (Object[]) args);
		}
	}
	/** Info used with getDependentXMLResource. */
	private static class XMLResource {
		private final String name;
		private final URL url;
		private final Document document;
		private final List<String> depends;

		private XMLResource(URL url, String elName, String elDepends)
		throws IOException{
			if (log.isDebugEnabled()) log.debug("Loading "+url);
			try {
				this.document = new SAXBuilder(false, false, true).build(url);
			} catch (Exception ex) {
				if (ex instanceof IOException) throw (IOException)ex;
				if (ex instanceof RuntimeException) throw (RuntimeException)ex;
				final IOException ioex = new IOException("Unable to load "+url);
				ioex.initCause(ex);
				throw ioex;
			}

			this.url = url;
			final Element root = this.document.getRootElement();
			this.name = IDOMs.getRequiredElementValue(root, elName);
			final String deps = root.getElementValue(elDepends, true);
			if (deps == null || deps.length() == 0) {
				this.depends = Collections.emptyList();
			} else {
				this.depends = new LinkedList<String>();
				CollectionsX.parse(this.depends, deps, ',');
				if (log.isTraceEnabled()) log.trace(this.name+" depends on "+this.depends);
			}
		}
		public String toString() {
			return "["+name+": "+url+" depends on "+depends+']';
		}
	};

	//-- Locator --//
	/** Always returns null.
	 */
	public String getDirectory() {
		return null;
	}
	public URL getResource(String name) {
		// no need to use Classes.getContextClassLoader() here because of the loading order issue
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL url = cl != null ? cl.getResource(resolveName(name)): null;
		if (url != null) {
			return url;
		} else {
			url = ClassLocator.class.getResource(name);
		}
		// fix if name is not starts with "/", then The getClassLoader() can find the current one.
		return url != null ? url : ClassLocator.class.getClassLoader().getResource(name);
	}
	public InputStream getResourceAsStream(String name) {
		// no need to use Classes.getContextClassLoader() here because of the loading order issue
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		final InputStream is =
			cl != null ? cl.getResourceAsStream(resolveName(name)): null;
		return is != null ? is: ClassLocator.class.getResourceAsStream(name);
	}
	private static String resolveName(String name) {
		return name != null && name.startsWith("/") ?
			name.substring(1): name;
	}

	//-- Object --//
	public int hashCode() {
		return 1123;
	}
	public boolean equals(Object o) {
		if (this == o) return true;
		return o instanceof ClassLocator;
	}
}
