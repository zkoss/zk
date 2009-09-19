/* Element.java


	Purpose:
	Description:
	History:
	2001/10/22 17:10:29, Create, Tom M. Yeh

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.TypeInfo;

import org.zkoss.mesg.Messages;
import org.zkoss.mesg.MCommon;
import org.zkoss.util.CollectionsX;
import org.zkoss.util.CheckableTreeArray;
import org.zkoss.xml.FacadeNodeList;
import org.zkoss.idom.impl.*;

/**
 * The iDOM element.
 *
 * @author tomyeh
 * @see Attribute
 */
public class Element extends AbstractGroup
implements Attributable, Namespaceable, org.w3c.dom.Element {
	/** The namespace. */
	protected Namespace _ns;
	/** The local name. */
	protected String _lname;
	/** The attributes. May be null. */
	protected List _attrs = null;
	/** Additional namespaces. May be null*/
	protected Map _addNamespaces = null;
	/** Whether it is aware of the attribute modification. */
	boolean _attrModAware = false;

	/**
	 * Constructor.
	 *
	 * @param nsURI the namespace URI
	 * @param tname the tag name
	 */
	public Element(String nsURI, String tname) {
		int kp = tname.indexOf(':');
		String prefix = kp >= 0 ? tname.substring(0, kp): "";
		String lname  = kp >= 0 ? tname.substring(kp + 1): tname;
		setNamespace(prefix, nsURI);
		setLocalName(lname);
	}
	/**
	 * Constructor.
	 *
	 * @param ns the namespace; if null, the default namespace is assumed
	 * (not necessary {@link Namespace#NO_NAMESPACE}).
	 * @param lname the local name
	 */
	public Element(Namespace ns, String lname) {
		setNamespace(ns);
		setLocalName(lname);
	}
	/**
	 * Constructor without a namespace (i.e., {@link Namespace#NO_NAMESPACE}).
	 *
	 * @param lname the local name
	 */
	public Element(String lname) {
		this(Namespace.NO_NAMESPACE, lname);
	}
	/**
	 * Constructor.
	 * Unlike other constructors, it doesn't set the modification flag.
	 */
	protected Element() {
		_ns = Namespace.NO_NAMESPACE;
	}

	//-- Element extras --//
	/**
	 * Tests whether this element is the root element of
	 * the owning document.
	 *
	 * <p>Note: false is returned if it doesn't has any parent.
	 */
	public final boolean isRootElement() {
		return getParent() instanceof Document;
	}

	/**
	 * Returns the Namespace in scope on this element for the given
	 * prefix (this involves searching up the tree, so the results depend
	 * on the current location of the element), or null if not found.
	 *
	 * <p>If prefix is empty, it searches for the "default" namespace
	 * in scope. Thus, to search for attribute's namespace, caller
	 * have to skip this one and use NO_NAMESPACE.
	 * (due XML, an attribute without prefix is NO_NAMESPACE)
	 *
	 * @param prefix namespace prefix to look up; null for empty
	 */
	public final Namespace getNamespace(String prefix) {
		if (prefix == null)
			prefix = "";

		Namespace ns = Namespace.getSpecial(prefix);
		if (ns != null)
			return ns;

		ns = getNamespace(); //might be null if in constructor
		if (ns != null && prefix.equals(ns.getPrefix()))
			return ns;

		if (_addNamespaces != null) {
			ns = (Namespace)_addNamespaces.get(prefix);
			if (ns != null)
				return ns;
		}

		if (getParent() instanceof Element) //only Element implements it; not Namespaceable
			return ((Element)getParent()).getNamespace(prefix);

		return prefix.length() > 0 ? null: Namespace.NO_NAMESPACE;
	}
	/**
	 * Returns namespace declared on this element.
	 *
	 * <p>It is <i>not</i> a "live" representation. Also, it is read-only.
	 *
	 * <p>Note: Namespace.equals compares namespace's URI. However,
	 * the distinction here is the prefix, because it is mainly for
	 * getNamespace(prefix).
	 *
	 * @return the namespace declarations.
	 */
	public final Collection getDeclaredNamespaces() {
		return _addNamespaces == null ?
			Collections.EMPTY_LIST: _addNamespaces.values();
	}
	/**
	 * Adds a namespace to the namespace declaration.
	 *
	 * @return true if the namespace is added
	 * @exception DOMException if the name space with the same prefix
	 * already exists but with different URI
	 */
	public final boolean addDeclaredNamespace(Namespace ns) {
		if (_addNamespaces == null) {
			_addNamespaces = new LinkedHashMap(5);
		} else {
			final Namespace old = (Namespace)_addNamespaces.get(ns.getPrefix());
			if (old != null) {
				if (!old.equals(ns))
					throw new DOMException(DOMException.NAMESPACE_ERR, "Add a conflict namespace: "+ns+", while "+old+" already exists");
				return false;
			}
		}

		_addNamespaces.put(ns.getPrefix(), ns);
		return true;
	}

	/**
	 * Gets the content of this element.
	 *
	 * <p>The content of an element is the first Binary or Text child of
	 * the element. Each element can has zero or one content.
	 *
	 * <p>Note: {@link #getText} returns the catenation of all Text
	 * children, not just the first one.
	 *
	 * @return the content of this element; null if no such child
	 * @see #getContent(String)
	 */
	public final Object getContent() {
		for (final  Iterator it = _children.iterator(); it.hasNext();) {
			Object o = it.next();
			if (o instanceof Text) {
				return ((Text)o).getText();
			} else if (o instanceof Binary) {
				return ((Binary)o).getValue();
			} else if (o instanceof CData) {
				return ((CData)o).getText();
			}
		}
		return null;
	}
	/**
	 * Sets the content of this element.
	 *
	 * <p>All existent Binary or Text children of this element are removed
	 * first. If the object is a String, a Text item is created to hold
	 * it. Otherwise, a Binary item is created to hold it.
	 *
	 * <p>Non-Binary/Text children are preserved.
	 *
	 * <p>If obj is a {@link Item} or an array/collection of {@link Item},
	 * this method will add them as child vertices rather than
	 * being the content.
	 * Moreever, if the first item of the array/collection is {@link Item},
	 * it is assumed to be all valid component to being has valid vertices.
	 * If not, an exception is thrown.
	 *
	 * <p>Thus, getContent might not return the object being set by setContent.
	 *
	 * @param obj the object to set; null is OK
	 * @return the previous content
	 * @see #getContent()
	 */
	public final Object setContent(Object obj) {
		if (obj instanceof Item) {
			getChildren().add(obj);
			return null; //done
		}
		if (obj instanceof Collection) {
			final Collection c = (Collection)obj;
			final Iterator it = c.iterator();
			if (it.hasNext() && (it.next() instanceof Item)) {
				getChildren().addAll(c);
				return null; //done
			}
		} else if (obj instanceof Object[]) {
			Object[] ary = (Object[])obj;
			if (ary.length > 0 && (ary[0] instanceof Item)) {
				for (int j = 0; j < ary.length; ++j)
					getChildren().add(ary[j]);
				return null; //done
			}
		}

		Object ret = null;
		boolean retFound = false;
		boolean bStr = obj instanceof String;
		for (final Iterator it = _children.iterator(); it.hasNext();) {
			Object o = it.next();
			if (o instanceof Text) {
				if (!retFound) {
					retFound = true;
					ret = ((Text)o).getText();
				}
				if (!bStr || obj == null) {
					it.remove();
				} else {
					((Text)o).setText((String)obj);
					obj = null; //then, the following will be removed
				}
			} else if (o instanceof Binary) {
				if (!retFound) {
					retFound = true;
					ret = ((Binary)o).getValue();
				}
				if (bStr || obj == null) {
					it.remove();
				} else {
					((Binary)o).setValue(obj);
					obj = null; //then, the following will be removed
				}
			} else if (o instanceof CData) {
				if (!retFound) {
					retFound = true;
					ret = ((CData)o).getText();
				}
				it.remove(); //always remove and add
			}
		}
		if (obj != null)
			_children.add(0,
				bStr ? (Object)new Text((String)obj): (Object)new Binary(obj));
		return ret;
	}

	/**
	 * Returns the content of the child element with the giving path, or
	 * null if the content is null or the child element doesn't exist.
	 *
	 * <p>Note that there might be more than one child with the same path
	 * in an idom tree; this method simply picks the first one that matchs and
	 * returns its content. To access certain one, you might use [n] to
	 * [@attr = value] specify which one to access.
	 *
	 *
	 * <p>To know whether the child element exists or conent is null,
	 * use {@link #hasContent}.
	 *
	 * <p>The content of an element is a special feature of iDOM.
	 * Like a Map, it is designed to let developers use names (in a path-like
	 * format) to access objects. See {@link #setContent(String, Object)}.
	 *
	 * <p>Like Unix path, the giving name could use '/' to catenate a series
	 * of child elements.
	 *
	 * <p>An empty path denotes this element itself. Leading, ending
	 * and consecutive '/' will be ignored.
	 *
	 * <p>Example:<br>
	 * <code>Object o = element.getContent("abc/def");<br>
	 * String s = Objects.toString(element.getContent("ab/cd"));<br>
	 * element.setContent("t:ab/cd/f:ef", new Integer(10));</code>
	 *
	 * <p>TODO: support [n] and [@attr = value]
	 *
	 * @param path a path; e.g., "b", "a/b", "t:a/t:b"
	 * @see #getContent()
	 */
	public final Object getContent(String path) {
		Element e = this;
		int j = 0;
		while (true) {
			int k = path.indexOf('/', j);
			String tname = k >= 0 ? path.substring(j, k): path.substring(j);
			if (tname.length() > 0) {
				e = e.getElement(tname);
				if (e == null)
					return null;
			}

			if (k < 0)
				return e.getContent();
			j = k + 1;
		}
	}
	/**
	 * Tests whether the child element with the giving path exists. Note that
	 * there might be more than one child with the same path in an idom tree;
	 * this method simply tell you that "yes", at least on such path exist.
	 *
	 * To get the content, use {@link #getContent(String)}.
	 */
	public final boolean hasContent(String path) {
		Element e = this;
		int j = 0;
		while (true) {
			int k = path.indexOf('/', j);
			String tname = k >= 0 ? path.substring(j, k): path.substring(j);
			if (tname.length() > 0) {
				e = e.getElement(tname);
				if (e == null)
					return false;
			}

			if (k < 0)
				return true;
			j = k + 1;
		}
	}
	/**
	 * Sets the content of the child element with the giving path.
	 *
	 * <p>Note that there might be more than one child with the same path
	 * in an idom tree; this method simply pick one that matchs and set
	 * its content (see {@link #setContent(Object)}).
	 *
	 * <p>The content of an element is a special feature of iDOM.
	 * Like a Map, it is designed to let developers use names (in a path-like
	 * format) to access objects. See {@link #getContent(String)}.
	 *
	 * <p>Like Unix path, the giving name could use '/' to catenate a series
	 * of child elements.
	 *
	 * <p>An empty path denotes this element itself. Leading, ending
	 * and consecutive '/' will be ignored.
	 *
	 * <p>If any element in the path is not found, it will be created
	 * automatically.
	 *
	 * @param path a path; e.g., "b", "a/b", "t:a/t:b"
	 * @param obj the object to set; null is acceptable
	 * @return the previous content
	 *
	 * @see #setContent(Object)
	 * @see #removeContent
	 * @see #hasContent
	 */
	public final Object setContent(String path, Object obj) {
		Element e = this;
		int j = 0;
		while (true) {
			int k = path.indexOf('/', j);
			String tname = k >= 0 ? path.substring(j, k): path.substring(j);
			if (tname.length() > 0) {
				Element e2 = e.getElement(tname);
				if (e2 == null) {
					e2 = new Element(e.getNamespace().getURI(), tname);
					e.getChildren().add(e2);
				}
				e = e2;
			}

			if (k < 0)
				return e.setContent(obj);
			j = k + 1;
		}
	}
	/**
	 * Removes the content of the child element with the giving path,
	 * and the child element itself if no other child.
	 *
	 * <p>Unlike {@link #setContent(String, Object)} with null,
	 * the child element identified by path will be detached if it has no
	 * other child (but the content). So does its parent
	 * <i>excluding</i> this element. Thus, removeContent(path)
	 * could undo setContent(path, v).
	 *
	 * @return the previous content
	 * @see #setContent(String, Object)
	 */
	public final Object removeContent(String path) {
		Element e = this;
		int j = 0;
		while (true) {
			int k = path.indexOf('/', j);
			String tname = k >= 0 ? path.substring(j, k): path.substring(j);
			if (tname.length() > 0) {
				e = e.getElement(tname);
				if (e == null)
					return null;
			}

			if (k < 0) {
				Object ret = e.setContent(null);

				//try to remove e; not including this
				for (Group group = e;
				group != this && group.getChildren().size() == 0;) {
					Group parent = group.getParent();
					group.detach();
					group = parent;
				}
				return ret;
			}
			j = k + 1;
		}
	}

	//-- utilities --//
	/** Returns the text of a child; never null. */
	private static final String getTextOfChild(Object o) {
		if (!(o instanceof AbstractTextual))
			return "";
		final AbstractTextual t = (AbstractTextual)o;
		return t.isPartOfParentText() ? t.getText(): "";
	}

	//-- Namespaceable --//
	/**
	 * Sets the namespace.
	 * If ns is null, the default namespace is assumed (not necessary
	 * {@link Namespace#NO_NAMESPACE}.
	 * <p>According W3C/DOM, unlike element, an attribute doesn't allow
	 * a namespace that has an URI but without a prefix.
	 */
	public final void setNamespace(Namespace ns) {
		checkWritable();
		if (ns == null) {
			if (_ns != null && _ns.getPrefix().length() == 0)
				return; //nothing to do
			ns = getNamespace("");
			if (ns == null)
				ns = Namespace.NO_NAMESPACE;
		}
		final Namespace old = getNamespace(ns.getPrefix());
		if (old != null && old.equals(ns))
			ns = old; //re-use if already defined
		_ns = ns;
	}
	/** Sets the namespace.
	 */
	public final void setNamespace(String prefix, String nsURI) {
		if (nsURI == null) nsURI = "";
		final Namespace ns = getNamespace(prefix);
		if (ns != null) {
			if (ns.getURI().equals(nsURI)) {
				setNamespace(ns);
				return;
			}
		}
		setNamespace(new Namespace(prefix, nsURI));
	}
	public final Namespace getNamespace() {
		return _ns;
	}

	public final String getTagName() {
		return _ns.tagNameOf(_lname);
	}
	public final void setTagName(String tname) {
		checkWritable();
		int kp = tname.indexOf(':');
		String prefix = kp >= 0 ? tname.substring(0, kp): "";
		String lname  = kp >= 0 ? tname.substring(kp + 1): tname;
		setPrefix(prefix);
		setLocalName(lname);
	}

	public final String getLocalName() {
		return _lname;
	}
	public final void setLocalName(String lname) {
		checkWritable();
		Verifier.checkElementName(lname, getLocator());
		_lname = lname;
	}

	//-- Item --//
	/** Clear the modification flag.
	 * <p>Note: if both includingDescendant and
	 * {@link #isAttributeModificationAware} are true, attributes'
	 * modified flags are cleaned.
	 */
	public void clearModified(boolean includingDescendant) {
		if (includingDescendant && _attrModAware) {
			for (final Iterator it = _attrs.iterator(); it.hasNext();)
				((Item)it.next()).clearModified(true);
		}
		super.clearModified(includingDescendant);
	}
	public Item clone(boolean preserveModified) {
		Element elem = (Element)super.clone(preserveModified);

		if (_addNamespaces != null) {
			elem._addNamespaces = new LinkedHashMap();
			elem._addNamespaces.putAll(_addNamespaces);
		}
		if (_attrs != null) {
			elem._attrs = elem.newAttrArray();
				//NOTE: AttrArray is an inner class, so we must use the right
				//object to create the array. Here is 'elem'.

			for (final Iterator it = _attrs.iterator(); it.hasNext();) {
				Item v = ((Item)it.next()).clone(preserveModified);
				boolean bClearModified = !preserveModified || !v.isModified();

				elem._attrs.add(v); //v becomes modified (v.setParent is called)

				if (bClearModified)
					v.clearModified(false);
			}
		}
		elem._modified = preserveModified && _modified;
		return elem;
	}

	/**
	 * Gets the tag name of the element -- the name with prefix.
	 * To get the local name, use getLocalName.
	 */
	public final String getName() {
		return getTagName();
	}
	/**
	 * Sets the tag name of the element.
	 * It will affect the local name and the namespace's prefix.
	 */
	public final void setName(String tname) {
		setTagName(tname);
	}

	/** Returns the catenation of {@link Textual} children; never null.
	 * Note: both &lt;tag/&gt; and &lt;tag&gt;&lt;/tag&gt; returns an
	 * empty string. To tell the difference, check the number of children.
	 * @see #getText(boolean)
	 */
	public final String getText() {
		if (_children.size() == 1) //optimize this case
			return getTextOfChild(_children.get(0));

		final StringBuffer sb = new StringBuffer(256);
		for (final Iterator it = _children.iterator(); it.hasNext();)
			sb.append(getTextOfChild(it.next()));
		return sb.toString();
	}
	/** Returns the catenation of {@link Textual} children; never null.
	 *
	 * @param trim whether to trim before returning
	 * @see #getText()
	 */
	public final String getText(boolean trim) {
		String t = getText();
		return trim && t != null ? t.trim(): t;
	}

	//-- Attributable --//
	public final boolean isAttributeModificationAware() {
		return _attrModAware;
	}
	public final void setAttributeModificationAware(boolean aware) {
		_attrModAware = aware;
	}
	public final List getAttributeItems() {
		if (_attrs == null)
			_attrs = newAttrArray();
		return _attrs;
	}
	/** Creates an empty list of attributes.
	 */
	protected List newAttrArray() {
		return new AttrArray();
	}

	public final int getAttributeIndex
	(int indexFrom, String namespace, String name, int mode) {
		if (_attrs == null || indexFrom < 0 || indexFrom >= _attrs.size())
			return -1;

		final Pattern ptn =
			(mode & FIND_BY_REGEX) != 0 ? Pattern.compile(name): null;

		final Iterator it = _attrs.listIterator(indexFrom);
		for (int j = indexFrom; it.hasNext(); ++j)
			if (match((Attribute)it.next(), namespace, name, ptn, mode))
				return j;

		return -1;
	}
	public final int getAttributeIndex(int indexFrom, String tname) {
		return getAttributeIndex(indexFrom, null, tname, FIND_BY_TAGNAME);
	}

	public final Attribute getAttributeItem(String namespace, String name, int mode) {
		int j= getAttributeIndex(0, namespace, name, mode);
		return j >= 0 ? (Attribute)_attrs.get(j): null;
	}
	public final Attribute getAttributeItem(String tname) {
		int j= getAttributeIndex(0, tname);
		return j >= 0 ? (Attribute)_attrs.get(j): null;
	}
	public final List getAttributes(String namespace, String name, int mode) {
		if (_attrs == null)
			return Collections.EMPTY_LIST;

		Pattern ptn =
			(mode & FIND_BY_REGEX) != 0 ? Pattern.compile(name): null;

		final List list = new LinkedList();
		for (final Iterator it = _attrs.iterator(); it.hasNext();) {
			Attribute attr = (Attribute)it.next();
			if (match(attr, namespace, name, ptn, mode))
				list.add(attr);
		}
		return list;
	}

	public final Attribute setAttribute(Attribute attr) {
		checkWritable();
		int j = getAttributeIndex(0, attr.getTagName());
		if (j >= 0) {
			return (Attribute)getAttributeItems().set(j, attr);
		} else {
			getAttributeItems().add(attr);
			return null;
		}
	}

	public final String getAttributeValue
	(String namespace, String name, int mode) {
		final Attribute attr = getAttributeItem(namespace, name, mode);
		return attr != null ? attr.getValue(): null;
	}
	public final String getAttributeValue(String tname) {
		Attribute attr = getAttributeItem(tname);
		return attr != null ? attr.getValue(): null;
	}
	public final Attribute setAttributeValue(String tname, String value) {
		checkWritable();
		Attribute attr = getAttributeItem(tname);
		if (attr != null)
			attr.setValue(value);
		else
			getAttributeItems().add(new Attribute(tname, value));
		return attr;
	}

	//-- Node --//
	public final short getNodeType() {
		return ELEMENT_NODE;
	}
	/**
	 * Always null. Unlike other nodes, it is not the same as getText.
	 */
	public final String getNodeValue() {
		return null;
	}
	public final NamedNodeMap getAttributes() {
		return new AttrMap();
	}
	public final boolean hasAttributes() {
		return _attrs != null && !_attrs.isEmpty();
	}
	public final String getNamespaceURI() {
		return _ns.getURI();
	}
	public final String getPrefix() {
		return _ns.getPrefix();
	}
	public final void setPrefix(String prefix) {
		setNamespace(prefix, _ns.getURI());
	}

	//-- Element --//
	public final NodeList getElementsByTagName(String tname) {
		return new FacadeNodeList(
			getElements(null, tname, FIND_BY_TAGNAME|FIND_RECURSIVE));
	}
	public final NodeList
	getElementsByTagNameNS(String nsURI, String lname) {
		return new FacadeNodeList(getElements(nsURI, lname, FIND_RECURSIVE));
	}

	public final Attr getAttributeNode(String tname) {
		return getAttributeItem(tname);
	}
	public final Attr getAttributeNodeNS(String nsURI, String lname) {
		return getAttributeItem(nsURI, lname, 0);
	}
	public final String getAttribute(String tname) {
		String val = getAttributeValue(tname);
		return val != null ? val: ""; //w3c spec
	}
	public final String getAttributeNS(String nsURI, String lname) {
		Attribute attr = getAttributeItem(nsURI, lname, 0);
		return attr != null ? attr.getValue(): "";
	}
	public final void setAttribute(String tname, String value) {
		setAttributeValue(tname, value);
	}
	public final void setAttributeNS
	(String nsURI, String tname, String value) {
		checkWritable();

		int kp = tname.indexOf(':');
		String prefix = kp >= 0 ? tname.substring(0, kp): "";
		String lname  = kp >= 0 ? tname.substring(kp + 1): tname;
		Attribute attr = getAttributeItem(nsURI, lname, 0);
		if (attr != null) {
			attr.setPrefix(prefix); //also change prefix
			attr.setValue(value);
		} else {
			getAttributeItems().add(new Attribute(nsURI, tname, value));
		}
	}

	public final Attr setAttributeNode(Attr newAttr) {
		return setAttribute((Attribute)newAttr);
	}
	public final Attr setAttributeNodeNS(Attr newAttr) {
		Attribute attr = (Attribute)newAttr;
		int j = getAttributeIndex(
			0, attr.getNamespace().getURI(), attr.getLocalName(), 0);
		if (j >= 0) {
			return (Attr)getAttributeItems().set(j, newAttr);
		} else {
			getAttributeItems().add(newAttr);
			return null;
		}
	}

	public final void removeAttribute(String tname) {
		int j = getAttributeIndex(0, tname);
		if (j >= 0)
			_attrs.remove(j);
	}
	public final void removeAttributeNS(String nsURI, String lname) {
		int j = getAttributeIndex(0, nsURI, lname, 0);
		if (j >= 0)
			_attrs.remove(j);
	}
	public final Attr removeAttributeNode(Attr oldAttr) {
		Attribute attr = (Attribute)oldAttr;
		int j = getAttributeIndex(0, attr.getTagName());
		if (j >= 0) {
			return (Attr)_attrs.remove(j);
		} else {
			throw new DOMException(DOMException.NOT_FOUND_ERR, getLocator());
		}
	}
	public final boolean hasAttribute(String tname) {
		return getAttributeIndex(0, tname) >= 0;
	}
	public final boolean hasAttributeNS(String nsURI, String lname) {
		return getAttributeIndex(0, nsURI, lname, 0) >= 0;
	}

	public final String toString() {
		StringBuffer sb = new StringBuffer(64)
			.append("[Element: <").append(getTagName());

		String uri = getNamespace().getURI();
		if (uri.length() != 0)
			sb.append(" [").append(uri).append(']');

		if (_attrs != null) {
			for (final Iterator it = _attrs.iterator(); it.hasNext();) {
				Attribute attr = (Attribute)it.next();
				sb.append(' ').append(attr.getTagName())
				.append("=\"").append(attr.getValue()).append('"');
			}
		}

		return sb.append("/>]").toString();
	}

	public TypeInfo getSchemaTypeInfo() {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public void setIdAttribute(String name, boolean isId) throws DOMException {
		//Level 3 not yet
	}
	public void setIdAttributeNS(String namespaceURI, String localName,
	boolean isId) throws DOMException {
		//Level 3 not yet
	}
	public void setIdAttributeNode(Attr idAttr, boolean isId)
	throws DOMException {
		//Level 3 not yet
	}

	//-- AttrArray --//
	protected class AttrArray extends CheckableTreeArray {
		protected AttrArray() {
		}

		//-- utilities --//
		private Attribute checkAdd(Object newItem) {
			checkWritable();

			if (!(newItem instanceof Attribute))
				throw new DOMException(
					DOMException.HIERARCHY_REQUEST_ERR, "Invalid type", getLocator());

			Attribute attr = (Attribute)newItem;
			if (attr.getOwner() != null)
				throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
					"Attribute, "+attr.toString()+", owned by other; detach or clone it", getLocator());

			return attr;
		}

		//-- CheckableTreeArray --//
		protected void onAdd(Object newElement, Object followingElement) {
			checkAdd(newElement, followingElement, false);
		}
		protected void onSet(Object newElement, Object replaced) {
			assert(replaced != null);
			checkAdd(newElement, replaced, true);
		}
		private void checkAdd(Object newItem, Object other, boolean replace) {
			//first, remove any existent with the same uri and name
			Object attrRemoved = null;
			Attribute attr = checkAdd(newItem);

			int j = getAttributeIndex(0, attr.getTagName());
			if (j >= 0 && (!replace || get(j) != other))
				throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
					"Attribute name, " + attr.getTagName() +", is conflicts with existent one (" + j + ')', getLocator());

			try {
				if (replace)
					onRemove(other);
				attr.setOwner(Element.this);
			}catch(RuntimeException ex) {
				if (replace) {
					Attribute attrRep = (Attribute)other;
					if (attrRep.getOwner() == null)
						attrRep.setOwner(Element.this); //restore it
				}
				if (attrRemoved != null)
					super.add(j, attrRemoved); //restore it
				throw ex;
			}
		}
		protected void onRemove(Object item) {
			checkWritable();
			((Attribute)item).setOwner(null);
		}

	}
	protected class AttrMap implements NamedNodeMap {
		protected AttrMap() {
		}

		//-- NamedNodeMap --//
		public final int getLength() {
			return _attrs != null ? _attrs.size(): 0;
		}
		public final Node item(int index) {
			return index < 0 || index >= getLength() ?
				null: (Node)_attrs.get(index);
		}
		public final Node getNamedItem(String tname) {
			return getAttributeItem(tname);
		}
		public final Node getNamedItemNS(String nsURI, String lname) {
			return getAttributeItem(nsURI, lname, 0);
		}
		public final Node removeNamedItem(String tname) {
			int j = getAttributeIndex(0, tname);
			return j >= 0 ? (Node)_attrs.remove(j): null;
		}
		public final Node removeNamedItemNS(String nsURI, String lname) {
			int j = getAttributeIndex(0, nsURI, lname, 0);
			return j >= 0 ? (Node)_attrs.remove(j): null;
		}
		public final Node setNamedItem(Node node) {
			return setAttributeNode((Attr)node);
		}
		public final Node setNamedItemNS(Node node) {
			return setAttributeNodeNS((Attr)node);
		}
	}
}
