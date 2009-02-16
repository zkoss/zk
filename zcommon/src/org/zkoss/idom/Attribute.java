/* Attribute.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	2001/10/22 17:11:10, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.w3c.dom.Attr;
import org.w3c.dom.TypeInfo;

import org.zkoss.lang.Objects;
import org.zkoss.idom.impl.*;

/**
 * The iDOM attribute.
 *
 * <p>Design decision: Attribute is also a item. The reason is
 * it simplifies the use of xpath. A xpath migt return either
 * elements or attributes, so...
 *
 * @author tomyeh
 * @see Element
 */
public class Attribute extends AbstractItem implements Namespaceable, Attr {
	/** The namespace. */
	protected Namespace _ns;
	/** The owner item. */
	protected Item _owner;
	/** The local name. */
	protected String _lname;
	/** The value. */
	protected String _value;

	/**
	 * Constructor.
	 *
	 * <p>Note: According to W3/DOM, the namespace of attributes must
	 * have a prefix if the uri is not empty.
	 *
	 * @param nsURI the namespace URI
	 * @param tname the tag name
	 */
	public Attribute(String nsURI, String tname, String value) {
		int kp = tname.indexOf(':');
		String prefix = kp >= 0 ? tname.substring(0, kp): "";
		String lname  = kp >= 0 ? tname.substring(kp + 1): tname;
		setNamespace(prefix, nsURI);
		setLocalName(lname);
		setValue(value);
	}
	/**
	 * Constructor.
	 *
	 * @param ns the namespace
	 * @param lname the local name
	 */
	public Attribute(Namespace ns, String lname, String value) {
		setNamespace(ns);
		setLocalName(lname);
		setValue(value);
	}
	/**
	 * Constructor.
	 */
	public Attribute(String lname, String value) {
		this(Namespace.NO_NAMESPACE, lname, value);
	}
	/**
	 * Constructor.
	 */
	protected Attribute() {
		_ns = Namespace.NO_NAMESPACE;
	}

	//-- Attribute extra --//
	/**
	 * Gets the value of this attribute.
	 */
	public final String getValue() {
		return _value;
	}
	/**
	 * Sets the value of this attribute.
	 * According to Section 3.3.3 of XML 1.0 spec, the value is always
	 * normalized. Whether to trim depends on whether an attribute is CDATA
	 * (default).
	 * In this version, we don't normalize or trim
	 * (i.e., consider it as CDATA).
	 *
	 * @param value the new value; null is considered as empty
	 */
	public final void setValue(String value) {
		checkWritable();

		if (value == null)
			value = "";
//		else
//			value = value.trim();
//TODO: check whether the attribute has been declared to CDATA or not

		if (!Objects.equals(_value, value)) {
			Verifier.checkCharacterData(value, getLocator());
			_value = value;
			setModified();
		}
	}

	/**
	 * Gets the item that owns this attribute.
	 */
	public final Item getOwner() {
		return _owner;
	}
	/**
	 * Sets the item that owns this attribute.
	 *
	 * <p><b><i>DO NOT</i></b> call this method. It is used internally.
	 * For user's point of view, the owner item is maintained
	 * automatically, so user never needs to update it.
	 */
	public final void setOwner(Item owner) {
		checkWritable();
		if (_owner != owner) {
			_ns = reuseNamespace(owner, _ns); //check and reuse

			//Note: new owner and old owner's setModified must be called
			if (_owner != null
			&& ((Attributable)_owner).isAttributeModificationAware())
				_owner.setModified();

			_owner = owner;
			setModified(); //then, owner.setModified is called
		}
	}
	/** Search for the namespace to see any possible to reuse.
	 */
	private static Namespace reuseNamespace(Item owner, Namespace ns) {
		if (ns.getPrefix().length() > 0 && (owner instanceof Element)) {
			final Namespace found =
				((Element)owner).getNamespace(ns.getPrefix());
			if (found == null)
				throw new DOMException(DOMException.NAMESPACE_ERR,
					"Attribute's namespace, "+ns+", not found in element");
			if (!ns.equals(found))
				throw new DOMException(DOMException.NAMESPACE_ERR,
					"Attribute's namespace, "+ns+", conflicts with element's, "+found);
			return found;
		}
		return ns;
	}

	//-- Namespaceable --//
	/** Sets the namespace.
	 */
	public final void setNamespace(String prefix, String nsURI) {
		setNamespace(
			(prefix == null || prefix.length() == 0)
			&& (nsURI == null || nsURI.length() ==0 ) ?
				null: new Namespace(prefix, nsURI));
	}
	/**
	 * Sets the namespace.
	 * <p>According W3C/DOM, unlike element, an attribute doesn't allow
	 * a namespace that has an URI but without a prefix.
	 */
	public final void setNamespace(Namespace ns) {
		checkWritable();

		if (ns != null && ns.getPrefix().length() == 0
		&& ns.getURI().length() != 0)
			throw new DOMException(DOMException.NAMESPACE_ERR,
				"Attribute's namespace without a prefix cannot have URI", getLocator());

		if (ns == null || ns.getPrefix().length() == 0)
			ns = Namespace.NO_NAMESPACE;
		else
			ns = reuseNamespace(_owner, ns); //check and reuse

		if (!Objects.equals(_ns, ns)) {
			_ns = ns;
			setModified();
		}
	}
	public final Namespace getNamespace() {
		return _ns;
	}

	public final String getTagName() {
		return _ns.tagNameOf(_lname);
	}
	/**
	 * Sets the tag name.
	 *
	 * <p>Changing a name improperly might cause replicated attribute
	 * names which won't be detected by this method.
	 */
	public final void setTagName(String tname) {
		checkWritable();

		if (!Objects.equals(tname, getName())) {
			int kp = tname.indexOf(':');
			String prefix = kp >= 0 ? tname.substring(0, kp): "";
			String lname  = kp >= 0 ? tname.substring(kp + 1): tname;
			setPrefix(prefix);
			setLocalName(lname);
			setModified();
		}
	}

	public final String getLocalName() {
		return _lname;
	}
	/**
	 * Sets the local name of this attribute.
	 *
	 * <p>Changing a name improperly might cause replicated attribute
	 * names which won't be detected by this method.
	 */
	public final void setLocalName(String lname) {
		checkWritable();
		if (!Objects.equals(lname, getLocalName())) {
			Verifier.checkAttributeName(lname, getLocator());
			_lname = lname;
			setModified();
		}
	}		

	//-- Item --//
	/**
	 * Tests whether this attribute is read-only.
	 * Note: An attribute is read-only if the read-only flag is set (setReadonly)
	 * or any of its owner item is read-only (getOwner().isReadonly()).
	 */
	public final boolean isReadonly() {
		return super.isReadonly() || (_owner != null && _owner.isReadonly());
	}

	public void setModified() {
		assert(getParent() == null);
		_modified = true;
		if (_owner != null
		&& ((Attributable)_owner).isAttributeModificationAware())
			_owner.setModified();
	}

	public final String getName() {
		return getTagName();
	}
	public final void setName(String tname) {
		setTagName(tname);
	}
	public final String getText() {
		return getValue();
	}
	public final void setText(String text) {
		setValue(text); //and checkWritable and setModified
	}

	/**
	 * Gets the document that owns this attribute.
	 */
	public final Document getDocument() {
		return _owner != null ? _owner.getDocument(): null;
	}

	/**
	 * Detach the attribute from its owner, if any.
	 * Only attributes that belongs to no item or the same item
	 * are allowed to be added to a item. So, detach is useful
	 * to move an attribute out from a item (and then you might
	 * add it to another item).
	 */
	public Item detach() {
		checkWritable();
		if (_owner != null) {
			((Attributable)_owner).getAttributeItems().remove(this);
			assert(_owner == null);
			setModified();
		}
		return this;
	}
	public void setParent(Item parent) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR,
			"Attributes do not have parent", getLocator());
	}

	//-- Node --//
	public final short getNodeType() {
		return ATTRIBUTE_NODE;
	}

	public final org.w3c.dom.Document getOwnerDocument() {
		return getDocument();
	}
	public final String getNamespaceURI() {
		return _ns.getURI();
	}
	public final String getPrefix() {
		return _ns.getPrefix();
	}
	/**
	 * Sets the namespace prefix of this attribute.
	 *
	 * <p>Changing a prefix improperly might cause replicated attribute
	 * names which won't be detected by this method.
	 */
	public final void setPrefix(String prefix) {
		setNamespace(prefix, _ns.getURI());
	}

	public TypeInfo getSchemaTypeInfo() {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public boolean isId() {
		throw new UnsupportedOperationException("DOM Level 3");
	}

	//-- Attr --//
	public final boolean getSpecified() {
		return false; //TODO
	}
	public final org.w3c.dom.Element getOwnerElement() {
		return (org.w3c.dom.Element)getOwner();
	}

	//-- Object --//
	public final String toString() {
		return "[Attribute: " + getTagName() + "=\"" + _value + "\"]";
	}

	public Item clone(boolean preserveModified) {
		Attribute v = (Attribute)super.clone(preserveModified);
		v._owner = null;
		return v;
	}
}
