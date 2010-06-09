/* AbstractItem.java


	Purpose: 
	Description: 
	History:
	2001/10/21 16:26:46, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom.impl;

import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.Serializable;
import java.util.regex.Pattern;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.UserDataHandler;

import org.zkoss.xml.Locator;
import org.zkoss.xml.Nodes;
import org.zkoss.idom.*;

/**
 * A semi-implemented item for leaf vertices.
 * A leaf item is a item without any children.
 * For cores having child cores, use Group.
 *
 * <p>Important methods that the deriving might want to override.
 *
 * <dl>
 *  <dt>Item.getName</dt>
 *  <dd>It must be overrided to provide a local name.</dd>
 *  <dt>Item.setName</dt>
 *  <dd>Overrid it if it allows to change the name.
 *  Default: throws an exception.</dd>
 *  <dt>Item.getText</dt>
 *  <dd>Override it if it has a text representation.
 *  Default: returns null.</dd>
 *  <dt>Item.setText</dt>
 *  <dd>Overrid it if it allows to change the text.
 *  Default: throws an exception.</dd>
 *  <dt>Node.getNodeType</dt>
 *  <dd>It must be overrided to provide the type.</dd>
 *  <dt>Item.clone</dt>
 *  <dd>Override it if any other members to handle specially.
 *  Note: by definition, we do deep clone only.</dd>
 *  <dt>Object.toString</dt>
 *  <dd>Override if you want a representation other than
 *  XMLOutputter does.</dd>
 * </dl>
 *
 * @author tomyeh
 * @see org.zkoss.idom.Item
 */
public abstract class AbstractItem
implements Item, Node, Serializable, Cloneable {
    private static final long serialVersionUID = 20060622L;

	/** The parent. */
	private Group _parent;
	/** The locator. */
	private Locator _loc;
	/** The read-only flag. */
	private boolean _readonly;
	/** The map of user data. */
	private Map _usrdta;
	/** The modification flag. */
	protected transient boolean _modified;

	/** Constructor.
	 */
	protected AbstractItem() {
	}

	//-- utilities --//
	/** Checks whether this item is writable (ie, isReady).
	 * @exception DOMException with NO_MODIFICATION_ALLOWED_ERR if not writable
	 */
	protected final void checkWritable() {
		if (isReadonly())
			throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, _loc);
	}

	//-- utilities --//
	/** Tests whether a namespaceable item matches the criteria.
	 * If mode don't contain FIND_BY_REGEX, ptn is ignored.
	 */
	protected static boolean match
	(Namespaceable vtx, String namespace, String name, Pattern ptn, int mode) {
		if (name != null) {
			String val = (mode & FIND_BY_TAGNAME) != 0 ?
				vtx.getTagName(): vtx.getLocalName();
			if ((mode & FIND_BY_REGEX) != 0) {
				if (!ptn.matcher(val).matches())
					return false;
			} else if ((mode & FIND_IGNORE_CASE) != 0) {
				if (!name.equalsIgnoreCase(val))
					return false;
			} else {
				if (!name.equals(val))
					return false;
			}
		}

		if (namespace != null) {
			String val = (mode & FIND_BY_PREFIX) != 0 ?
				vtx.getNamespace().getPrefix(): vtx.getNamespace().getURI();
			if (!namespace.equals(val))
				return false;
		}
		return true;
	}

	//-- Item --//
	public boolean isReadonly() {
		return _readonly || (_parent!=null && _parent.isReadonly());
	}
	/**
	 * Sets the read-only flag of this item. It causes this item
	 * and all its descendants read-only, see {@link #isReadonly}.
	 *
	 * <p>Deriving class might restrict more conditions here.
	 */
	public void setReadonly(boolean readonly) {
		_readonly = readonly;
	}

	public boolean isModified() {
		return _modified;
	}
	public void clearModified(boolean includingDescendant) {
		_modified = false;
	}
	public void setModified() {
		_modified = true;
		if (_parent != null)
			_parent.setModified();
	}

	public void setName(String name) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, _loc);
	}
	public String getText() {
		return null;
	}
	public void setText(String text) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, _loc);
	}

	public Document getDocument() {
		for (Item v = _parent; v != null; v = v.getParent())
			if (v instanceof Document)
				return (Document)v;
		return null;
	}

	public Item detach() {
		checkWritable();
		if (_parent != null) {
			_parent.getChildren().remove(this);
			assert(_parent == null);
			assert(isModified());
		}
		return this;
	}
	public Item clone(boolean preserveModified) {
		try {
			AbstractItem v = (AbstractItem)super.clone();
			v._readonly = false;
			v._parent = null;
			if (!preserveModified)
				v._modified = false;
			return v;
		}catch(CloneNotSupportedException ex) {
			throw new InternalError();
		}
	}
	public final Group getParent() {
		return _parent;
	}
	public void setParent(Group parent) {
		checkWritable();
		if (_parent != parent) {
			//Note: new parent and old parent's setModified must be called
			if (_parent != null)
				_parent.setModified();
			_parent = parent;
			setModified(); //then, parent.setModified is called
		}
	}

	public final Locator getLocator() {
		return _loc;
	}
	public final void setLocator(Locator loc) {
		checkWritable();
		_loc = loc;
	}

	//-- Node --//
	public String getNodeName() {
		return getName();
	}
	public String getNodeValue() {
		return getText();
	}
	public void setNodeValue(String nodeValue) {
		setText(nodeValue);
	}

	public org.w3c.dom.Document getOwnerDocument() {
		return getDocument();
	}
	public final Node cloneNode(boolean deep) {
		if (!deep)
			throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "always deep", _loc); //NOT YET
		return (Node)clone(false);
	}

	public final Node getParentNode() {
		return (Node)getParent();
	}
	public final Node getPreviousSibling() {
		if (_parent == null)
			return null;

		List list = _parent.getChildren();
		int j = list.indexOf(this);
		if (j < 0)
			throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "internal error", _loc);
		return j == 0 ? null: (Node)list.get(j - 1);
	}
	public final Node getNextSibling() {
		if (_parent == null)
			return null;

		List list = _parent.getChildren();
		int j = list.indexOf(this);
		if (j < 0)
			throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "internal error", _loc);
		return j == list.size()-1 ? null: (Node)list.get(j + 1);
	}
	public NodeList getChildNodes() {
		return Nodes.EMPTY_NODELIST;
	}
	public Node getFirstChild() {
		return null;
	}
	public Node getLastChild() {
		return null;
	}
	public boolean hasChildNodes() {
		return false;
	}
	public Node insertBefore(Node newChild, Node refChild) {
		throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, _loc);
	}
	public Node replaceChild(Node newChild, Node oldChild) {
		throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, _loc);
	}
	public Node removeChild(Node oldChild) {
		throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, _loc);
	}
	public Node appendChild(Node newChild) {
		throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, _loc);
	}

	public final void normalize() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, _loc); //NOT YET
	}
	public final boolean isSupported(String feature, String version) {
		return DOMImplementation.THE.hasFeature(feature, version);
	}

	public String getNamespaceURI() {
		return null;
	}
	public String getPrefix() {
		return null;
	}
	public String getLocalName() {
		return getName();
	}
	public void setPrefix(String prefix) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR, _loc);
	}

	public NamedNodeMap getAttributes() {
		return EmptyNamedNodeMap.THE;
	}
	public boolean hasAttributes() {
		return false;
	}

	public String getBaseURI() {
		return null; //Level 3 not yet
	}
	public short compareDocumentPosition(Node other) throws DOMException {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public String getTextContent() throws DOMException {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public void setTextContent(String textContent) throws DOMException {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public boolean isSameNode(Node other) {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public String lookupPrefix(String namespaceURI) {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public boolean isDefaultNamespace(String namespaceURI) {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public String lookupNamespaceURI(String prefix) {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public boolean isEqualNode(Node arg) {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public Object getFeature(String feature, String version) {
		return null;
	}
	public Object setUserData(String key, Object data, UserDataHandler handler) {
		if (handler != null)
			throw new UnsupportedOperationException("DOM Level 3");
		if (_usrdta == null) _usrdta = new LinkedHashMap();
		return _usrdta.put(key, data);
	}
	public Object getUserData(String key) {
		return _usrdta != null ? _usrdta.get(key): null;
	}

	//-- Object --//
	/** Overriding this method is prohibited.
	 */
	public final boolean equals(Object o) {
		return this == o;
	}
	/** Overriding this method is prohibited.
	 */
	public final int hashCode() {
		return super.hashCode();
	}

	//-- Cloneable --//
	/**
	 * Clones this object (a deep cloning not including contents contained
	 * in Textual nodes).
	 * Note: after cloning, the read-only flag always becomes false,
	 * and the parent becomes null (i.e., detached).
	 */
	public Object clone() {
		return clone(false);
	}
}
