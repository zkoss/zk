/* IDOMs.java

{{IS_NOTE

Purpose:
Description:
History:
C91/01/08 10:37:16, reate, Tom M. Yeh
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom.util;

import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
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
import org.zkoss.lang.Objects;
import org.zkoss.lang.SystemException;
import org.zkoss.util.Locales;
import org.zkoss.util.IllegalSyntaxException;
import org.zkoss.util.resource.Locator;

import org.zkoss.idom.*;
import org.zkoss.idom.input.SAXBuilder;
import org.zkoss.idom.transform.Transformer;

/**
 * The iDOM relevant utilities.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @see org.zkoss.idom.Item
 * @see org.zkoss.idom.Group
 */
public class IDOMs {
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

	/** Imports other XML files by processing &lt;import&gt;.
	 * Unlike {@link #imports(Element, File)}, this method
	 * assumes all imported files are loaded thru the locator's getResource().
	 *
	 * <p>Note: only the 'direct' children of the specified root is processed.
	 *
	 * <p>The format:<br>
	 * <pre><code>&lt;root&gt;
	 * ...
	 *  &lt;import url="xyz.xml"&gt;
	 * ...</code>
	 *
	 * <p>Then, this method will replace the import tag with the content
	 * of xyz.xml.
	 *
	 * <p>The imported file (say xyz.xml in the above example) could also
	 * have the import tag to import other files.
	 *
	 * @param locator the resource locator to load the imported file
	 * @param refUrl the reference url; null to denote not available.
	 * It is used to retrieve the absolute path, if the import tag specifies
	 * a relative path, i.e., without xxx:// nor /.
	 * If root is loaded from an URL, specified the URL as refUrl.<br>
	 * How it works: refUrl.substring(0, refUrl.lastIndex('/')+1) + imported-url.
	 */
	public static final void
	imports(Locator locator, Element root, String refUrl) {
		String prefix = null;
		for (final ListIterator it = root.getChildren().listIterator();
		it.hasNext();) {
			final Object o = it.next();
			if (o instanceof Element) {
				final Element e = (Element)o;
				if (e.getName().equals("import")) {
					String url = e.getAttributeValue("url");
					if (url == null || url.length() == 0)
						throw new IllegalSyntaxException(
							MCommon.XML_ATTRIBUTE_REQUIRED, new Object[] {"url", e.getLocator()});

					if (refUrl != null
					&& url.charAt(0) != '/' && url.indexOf("://") < 0) {
						if (prefix == null)
							prefix =
								refUrl.substring(0, refUrl.lastIndexOf('/') + 1);
						url = prefix + url;
					}

					imports0(it, locator, url);
				}
			}
		}
	}
	private static final void
	imports0(ListIterator it, Locator locator, String flnm) {
		final URL url = locator.getResource(flnm);
		if (url == null)
			throw new SystemException(MCommon.FILE_NOT_FOUND, flnm);

		final Element imported;
		try {
			final SAXBuilder builder = new SAXBuilder(false, false, true);
			imported = builder.build(url).getRootElement();
		} catch(Exception ex) {
			throw SystemException.Aide.wrap(ex);
		}

		imports(locator, imported, flnm); //recursive

		it.remove();
		for (Iterator e = imported.detachChildren().iterator(); e.hasNext();)
			it.add(e.next());
	}

	/** Imports other XML files by processing &lt;import&gt;.
	 * Unlike {@link #imports(Locator, Element, String)}, this method
	 * assumes all imported files are from the file system.
	 */
	public static final void imports(Element root, File refFile) {
		final File parent = refFile != null ? refFile.getParentFile(): null;
		for (final ListIterator it = root.getChildren().listIterator();
		it.hasNext();) {
			final Object o = it.next();
			if (o instanceof Element) {
				final Element e = (Element)o;
				if (e.getName().equals("import")) {
					String url = e.getAttributeValue("url");
					if (url == null || url.length() == 0)
						throw new IllegalSyntaxException(
							MCommon.XML_ATTRIBUTE_REQUIRED, new Object[] {"url", e.getLocator()});

					imports0(it,
						url.charAt(0) != '/' && url.indexOf("://") < 0 ?
							new File(parent, url): new File(url));
				}
			}
		}
	}
	private static final void imports0(ListIterator it, File file) {
		final Element imported;
		try {
			final SAXBuilder builder = new SAXBuilder(false, false, true);
			imported = builder.build(file).getRootElement();
		} catch(Exception ex) {
			throw SystemException.Aide.wrap(ex);
		}

		imports(imported, file); //recursive

		it.remove();
		for (Iterator e = imported.detachChildren().iterator(); e.hasNext();)
			it.add(e.next());
	}

	//-- Merge --//
	/** Merges two iDOM tree.
	 * The child elements of the src tree is merged into the dst trees,
	 * such that child elements with the same name are put together.
	 */
	public static void merge(Element dst, Element src) {
		for (final Iterator it = src.getElements().iterator(); it.hasNext();) {
			final Element e = (Element)it.next();
			e.detach();

			final String name = e.getName();
			if (dst.getElement(name) == null) { //not found
				dst.getChildren().add(e);
				continue;
			}

			final ListIterator i2 = dst.getChildren().listIterator();
			for (boolean found = false; i2.hasNext();) {
				final Object o = i2.next();
				if (o instanceof Element) {
					final boolean match = ((Element)o).getName().equals(name);
					if (found != match) {
						if (found) { //&& !match
							i2.previous();
							break;
						} else { //!found && match
							found = true;
						}
					}
				}
			}
			i2.add(e);
		}
	}
	//-- Main --//
	/** Handles XML files.
	 */
	public static void main(String[] args) throws Exception {
		final List srcs = new LinkedList();
		boolean merge = false, force = false, forceNewer = false, noDocType = false;
		for (int j = 0; j < args.length; ++j) {
			if (args[j].length() == 0)
				continue;
			if (args[j].charAt(0) == '-') {
				if (args[j].length() == 1) {
					error("Wrong argument, "+args[j]);
					return;
				}
				switch(args[j].charAt(1)) {
				case 'h':
					help();
					return;
				case 'm':
					merge = true;
					break;
				case 'f':
					force = true;
					break;
				case 'n':
					forceNewer = true;
					break;
				default:
					error("Wrong argument, "+args[j]);
					return;
				}
			} else {
				srcs.add(args[j]);
			}
		}

		if (srcs.size() < 2) {
			error("At least one source file and one destination file must be specified");
			return;
		}

		if (!merge) merge = true; //Currently we support only merge

		final File dst = new File((String)srcs.remove(srcs.size() - 1));
		if (!force && !forceNewer && dst.exists()) {
			error(dst+" exists. Specify -f or -n if you want to overwrite it");
			return;
		}

		boolean doit = !forceNewer;
		for (final ListIterator it = srcs.listIterator(); it.hasNext();) {
			final File src = new File((String)it.next());
			if (!src.exists()) {
				error(Messages.get(MCommon.FILE_NOT_FOUND, src));
				return;
			}
			if (!doit && dst.lastModified() < src.lastModified()) {
				doit = true;
			}
			it.set(src);
		}

		if (!doit)
			return;
		if (merge)
			mergeFile(dst, srcs);
	}
	private static void mergeFile(File dst, List srcs)
	throws Exception {
		System.out.println("Merge "+srcs+" to "+dst);
		Document doc = null;
		for (final Iterator it = srcs.iterator(); it.hasNext();) {
			final File src = (File)it.next();
			final Document d = new SAXBuilder(false, false).build(src);
			if (doc == null) {
				doc = d;
			} else {
				merge(doc.getRootElement(), d.getRootElement());
			}
		}
		new Transformer().transform(doc, new StreamResult(dst));
	}
	private static void error(String msg) {
		System.err.println("idoms: "+msg+"\nidoms: Try 'idoms -h' for more information");
	}
	private static void help() {
		System.out.println(
		"Handles XML files.\n\n"
		+"Usage:\n"
		+"\tidoms [-h][-m] src-file1 [src-file2]... dst-file\n\n"
		+"-f\tForce to overwrite if the destination exists. Optional.\n"
		+"-n\tForce to overwrite if the destination is older. Optional.\n"
		+"-h\tShow this message.\n"
		+"-m\tMerges files. Optional.\n"
		);
	}
}
