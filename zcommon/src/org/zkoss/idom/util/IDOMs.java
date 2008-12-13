/* IDOMs.java

{{IS_NOTE

Purpose:
Description:
History:
	2002/01/08 10:37:16, Create, Tom M. Yeh
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom.util;

import java.lang.reflect.Field;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.net.URL;
import java.io.File;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.zkoss.mesg.MCommon;
import org.zkoss.mesg.Messages;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.lang.SystemException;
import org.zkoss.util.Locales;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.util.resource.Locator;
import org.zkoss.util.logging.Log;

import org.zkoss.idom.*;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.transform.Transformer;

/**
 * The iDOM relevant utilities.
 *
 * @author tomyeh
 * @see org.zkoss.idom.Item
 * @see org.zkoss.idom.Group
 */
public class IDOMs {
	private static final Log log = Log.lookup(IDOMs.class);

	/** Returns the required element.
	 * @param elemnm the element name
	 */
	public static final Element getRequiredElement(Element e, String elemnm)
	throws IllegalSyntaxException {
		final Element sub = e.getElement(elemnm);
		if (sub == null)
			throw new IllegalSyntaxException(
				MCommon.XML_ELEMENT_REQUIRED, new Object[] {elemnm, e.getLocator()});
		return sub;
	}
	/** Returns the required element value.
	 * <p>Note: the returned value may be an empty string (if the element
	 * contains no text at all).
	 * @exception IllegalSyntaxException if the element is not found
	 */
	public static final String getRequiredElementValue(Element e, String elemnm)
	throws IllegalSyntaxException {
		final Element sub = e.getElement(elemnm);
		if (sub == null)
			throw new IllegalSyntaxException(
				MCommon.XML_ELEMENT_REQUIRED, new Object[] {elemnm, e.getLocator()});
		return sub.getText(true);
	}
	/** Returns the required attribute value.
	 * @exception IllegalSyntaxException if the element is not found
	 */
	public static final String getRequiredAttributeValue(Element e, String attrnm)
	throws IllegalSyntaxException {
		final Attribute attr = e.getAttributeItem(attrnm);
		if (attr == null)
			throw new IllegalSyntaxException(
				MCommon.XML_ATTRIBUTE_REQUIRED, new Object[] {attrnm, e.getLocator()});
		return attr.getValue();
	}
	/** Returns the first child element, or null if no child element at all.
	 */
	public static final Element getFirstElement(Group group) {
		final Iterator it = group.getElements().iterator();
		return it.hasNext() ? (Element)it.next(): null;
	}

	/** Returns the first element whose sub-element called "name" has the
	 * same content as the name argument, or null if not found.
	 *
	 * @param elems a list of elements to look for the specified name
	 */
	public static final Element findElement(List elems, String name) {
		for (final Iterator it = elems.iterator(); it.hasNext();) {
			final Element e = (Element)it.next();
			if (Objects.equals(name, e.getElementValue("name", true)))
				return e;
		}
		return null;
	}

	/** Parses a tree of parameter elements into a map.
	 *
	 * <p>The tree of parameter elements is as follows.
	 * <pre><code>
	 * &lt;type&gt;
	 *   &lt;name&gt;any&lt;/name&gt;
	 *   &lt;vaue&gt;any&lt;/vaue&gt;
	 * &lt;/type&gt;
	 *
	 * @return the map after parsed (never null). An empty map is returned
	 * if no parameter is defined. The map is in the same order of the element tree.
	 */
	public static final
	Map parseParams(Element elm, String type, String name, String value) {
		final Map map = new LinkedHashMap();
		for (Iterator it = elm.getElements(type).iterator(); it.hasNext();) {
			final Element el = (Element)it.next();
			final String nm = getRequiredElementValue(el, name);
			final String val = getRequiredElementValue(el, value);
			map.put(nm, val);
		}
		return map;
	}
	
	/** Formats the specified element for better readability by
	 * adding white spaces.
	 */
	public static void format(Element e) {
		//add proper spacing between consecutive elements
		boolean elemFound = true;
		for (final ListIterator it = e.getChildren().listIterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof Element) {
				if (elemFound) { //insert space
					it.previous();
					it.add(new Text("\n\t"));
					it.next();
				} else {
					elemFound = true;
				}

				format((Element)o); //recursive
			} else {
				elemFound = false;
			}
		}
	}

	/** Converts elements to their contents if the giving object is
	 * an element or an array or a collection of elements.
	 * One item of an collection might be another collection or array.
	 */
	public static final Object toContents(Object obj) {
		if (obj instanceof Collection) {
			Collection c = (Collection)obj;
			boolean cvted = false;
			Collection rets = new LinkedList();
			for (Iterator it = c.iterator(); it.hasNext();) {
				Object o = it.next();
				Object o2 = toContents(o); //recursive
				if (o != o2)
					cvted = true;
				rets.add(o2);
			}

			if (cvted)
				return rets;
		} else if (obj instanceof Object[]) {
			Object[] ary = (Object[])obj;
			boolean cvted = false;
			Object[] rets = new Object[ary.length];
			for (int j = 0; j < ary.length; ++j) {
				Object o2 = toContents(ary[j]); //recursive
				if (ary[j] != o2)
					cvted = true;
				rets[j] = o2;
			}
			if (cvted)
				return rets;
		} else if (obj instanceof Element) {
			return ((Element)obj).getContent();
		}
		return obj;
	}
	/** Set the contents of elements.
	 * The val argument could be an array and a collection, and each
	 * item will be assigned to each of the list one-by-one.
	 *
	 * <p>Unlike {@link #toContents}, it handles only a collection of elements
	 * -- all items must be elements.
	 *
	 * @param elems the collection of elements
	 * @param val the value which could be an object, an array or a collection
	 */
	public static final void setContents(Collection elems, Object val) {
		Object[] ary = null;
		if (val instanceof Object[]) {
			ary = (Object[])val;
		} else if (val instanceof Collection) {
			ary = ((Collection)val).toArray();
		} else {
			ary = new Object[] {val};
		}

		Iterator it = elems.iterator();
		for (int j = 0; it.hasNext(); ++j) {
			((Element)it.next()).setContent(j < ary.length ? ary[j]: null);
		}
	}

	/** Transforms a document to a string.
	 * The string is XML correct.
	 */
	public final static String toString(Document doc)
	throws TransformerConfigurationException, TransformerException {
		final StringWriter writer = new StringWriter();
		new Transformer().transform(doc, new StreamResult(writer));
		return writer.toString();
	}
	/**
	 * Print a readable tree of the specified group to System.out.
	 * It is for debug purpose and the generated format is <i>not</i> XML.
	 * To generate XML, uses {@link Transformer} or {@link #toString}.
	 * @see #toString
	 */
	public static final void dumpTree(Group group) {
		dumpTree(System.out, group);
	}
	/**
	 * Print a readable tree of the specified group to the specified stream.
	 * It is for debug purpose and the generated format is <i>not</i> XML.
	 * To generate XML, uses {@link Transformer} or {@link #toString}.
	 * @see #toString
	 */
	public static final void dumpTree(PrintStream s, Group group) {
		dumpTree(new PrintWriter(s, true), group);
	}
	/**
	 * Print a readable tree of the specified group to the specified writer.
	 * It is for debug purpose and the generated format is <i>not</i> XML.
	 * To generate XML, uses {@link Transformer} or {@link #toString}.
	 * @see #toString
	 */
	public static final void dumpTree(PrintWriter s, Group group) {
		dumpTree(s, group, "");
	}
	private static final void
	dumpTree(PrintWriter s, Item vtx, String prefix) {
		s.print(prefix);
		s.print(vtx);
		s.print(vtx.isReadonly() ? 'R' : ' ');
		s.println(vtx.isModified() ? 'M' : ' ');
		if (vtx instanceof Group) {
			prefix = prefix + "  ";
			for (Iterator it = ((Group)vtx).getChildren().iterator();
			it.hasNext();)
				dumpTree(s, (Item)it.next(), prefix);
		}
	}

	/** Returnss whether the loaded document's version is correct.
	 *
	 * <p>It assumes the version info is specified in the document in
	 * the following format:
	 *
	 * <pre></code>
&lt;version>
	&lt;version-class>org.zkoss.zul.Version&lt;/version-class>
	&lt;version-uid>3.0.0&lt;/version-uid>
&lt;/version>
</code></pre>
	 *
	 * <p>Note: it returns true if the version info is not found.
	 *
	 * @param doc the document to check
	 * @param url the URL used to show the readable message if the
	 * version doesn't match
	 * @since 3.0.0
	 */
	public static boolean checkVersion(Document doc, URL url)
	throws Exception {
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
			return true;
		}
	}
}
