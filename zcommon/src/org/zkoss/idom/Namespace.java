/* Namespace.java

{{IS_NOTE

	Purpose: Namespace
	Description:
	History:
		2001/10/21 16:06:46, Create, Tom M. Yeh
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Represents the namespace.
 * A namespace is immutable, so you have to get a new one
 *
 * @author tomyeh
 * @see Item
 */
public final class Namespace implements Serializable, Cloneable {
    private static final long serialVersionUID = 20060622L;

	/** The <code>Namespace</code> for when <i>not</i> in a namespace
	 */
	public static final Namespace NO_NAMESPACE = newSpecialNamespace("", "");
	/** The xml namespace.
	 */
	public static final Namespace XML_NAMESPACE = newSpecialNamespace(
			"xml", "http://www.w3.org/XML/1998/namespace");
	/** The xmlns namespace.
	 */
	public static final Namespace XMLNS_NAMESPACE = newSpecialNamespace(
			"xmlns", "http://www.w3.org/XML/1998/namespace");

	/** The prefix mapped to this namespace */
	private String _prefix;
	/** The URI for this namespace */
	private String _uri;

	/** Returns the special namespace if prefix is special, or null if not.
	 */
	public static Namespace getSpecial(String prefix) {
		if (prefix.equals("xml"))
			return Namespace.XML_NAMESPACE;
		if (prefix.equals("xmlns"))
			return Namespace.XMLNS_NAMESPACE;
		return null;
	}
		
	/** Assigns a spacial namespace whose name starts with xml.
	 * We need it because the verifier will reject it.
	 */
	private static final Namespace
	newSpecialNamespace(String prefix, String uri) {
		Namespace ns = new Namespace("", uri);
		ns._prefix = prefix; //assign directly to avoid checkNam...
		return ns;
	}

	/**
	 * Contructor.
	 *
	 * @param prefix String prefix to map to this namespace.
	 * @param uri String URI for namespace.
	 * @exception DOMException with NAMESPACE_ERR if the given prefix and uri
	 * is invalid
	 */
	public Namespace(String prefix, String uri) {
		Verifier.checkNamespacePrefix(prefix, null);
		Verifier.checkNamespaceURI(uri, null);

		if (prefix.length() != 0 && uri.length() == 0)
			throw new DOMException(DOMException.NAMESPACE_ERR,
				"Non-empty prefix, "+prefix+", requires a URI");
		_prefix = prefix;
		_uri = uri;
	}

	/**
	 * Gets the tag name of the giving local name.
	 */
	public final String tagNameOf(String name) {
		assert(name.indexOf(':') < 0);

		int len = _prefix.length();
		return len == 0 ? name: _prefix + ':' + name;
	}

	/**
	 * Gets the prefix mapped to this Namespace.
	 */
	public final String getPrefix() {
		return _prefix;
	}
	/**
	 * Gets the namespace URI for this Namespace.
	 */
	public final String getURI() {
		return _uri;
	}

	/** Tests whether two namespace are the same in both prefix
	 * and namespace URI.
	 * On the other hand, equals check only the namespace URI.
	 *
	 * <p>Note: unlike equals, it throws DOMException if prefix
	 * is the same but URI is different.
	 *
	 * @exception DOMException if they have the same prefix
	 * but with different namespace URI
	 */
	public final boolean equalsAll(Namespace ns) {
		if (_prefix.equals(ns._prefix))
			if (_uri.equals(ns._uri))
				return true;
			else
				throw new DOMException(DOMException.NAMESPACE_ERR,
					"The same prefix, " + _prefix + ", cannot have different URI: "+_uri+" vs "+ns._uri);
		return false;
	}

	//-- cloneable --//
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	//-- Object --//
	/** Note: equals() is based on URI only. */
	public boolean equals(Object o) {
		return this == o
			|| ((o instanceof Namespace) && _uri.equals(((Namespace)o)._uri));
	}
	/** Note: hashCode() is based on URI only. */
	public int hashCode() {
		return _uri.hashCode();
	}
	public String toString() {
		return "[Namespace: \"" + _prefix + "\", \"" + _uri + "\"]";
	}
}
