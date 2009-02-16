/* Document.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	2001/10/21 16:36:39, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import java.util.List;
import java.util.Iterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMConfiguration;

import org.zkoss.util.CheckableTreeArray;
import org.zkoss.xml.FacadeNodeList;
import org.zkoss.idom.impl.*;

/**
 * Represents Document which is also W3C/DOM's document,
 * ie, org.w3c.dom.Document.
 *
 * @author tomyeh
 * @see Element
 */
public class Document extends AbstractGroup implements org.w3c.dom.Document {
	/** The document type. */
	private DocType _docType;
	/** The root element. */
	private Element _root;
	private String _docURI, _ver = "1.0";
	private boolean _stdalone, _stricterrck = true;

	/** Constructor.
	 */
	public Document(Element root, DocType dt) {
		setRootElement(root);
		setDocType(dt);
	}
	/** Constructor.
	 */
	public Document(Element root) {
		setRootElement(root);
	}
	/** Constructor.
	 */
	public Document() {
	}	

	//-- Document extras --//
	/**
	 * Gets the root element.
	 */
	public final Element getRootElement() {
		return _root;
	}
	/**
	 * Sets the root element.
	 */
	public final void setRootElement(Element root) {
		checkWritable();
		if (root == null) {
			if (_root != null)
				_children.remove(_root); //then calls this.setModified
		} else {
			if (_root != null)
				_children.set(_children.indexOf(_root), root);
			else
				_children.add(root);
		}
	}
	/**
	 * Gets the document type.
	 */
	public final DocType getDocType() {
		return _docType;
	}
	/**
	 * Sets the document type.
	 */
	public final void setDocType(DocType docType) {
		checkWritable();
		if (docType == null) {
			if (_docType != null)
				_children.remove(_docType); //then calls this.setModified
		} else {
			if (_docType != null)
				_children.set(_children.indexOf(_docType), docType);
			else
				_children.add(docType);
		}
	}

	//-- AbstractGroup --//
	protected final List newChildren() {
		return new ChildArray(); //note: it doesn't use that of AbstractGroup
	}

	//-- Item --//
	public final String getName() {
		return "#document";
	}

	//-- Node --//
	public final short getNodeType() {
		return DOCUMENT_NODE;
	}

	//-- org.w3c.dom.Document --//
	public final DocumentType getDoctype() {
		return getDocType();
	}
	public final org.w3c.dom.Element getDocumentElement() {
		return getRootElement();
	}
	public final org.w3c.dom.DOMImplementation getImplementation() {
		return DOMImplementation.THE;
	}

	// Unlike Crimson's createXxx, we don't set document here,
	// because, in iDOM, document is decided automatically.

	public final org.w3c.dom.Element createElement(String lname) {
		return new Element(lname);
	}
	public final org.w3c.dom.Element
	createElementNS(String nsURI, String tname) {
		return new Element(nsURI, tname);
	}
	public final Attr createAttribute(String lname) {
		return new Attribute(lname, null);
	}
	public final Attr createAttributeNS(String nsURI, String tname) {
		return new Attribute(nsURI, tname, null);
	}
	public final org.w3c.dom.DocumentFragment createDocumentFragment() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, getLocator()); //NOT YET
	}
	public final org.w3c.dom.Text createTextNode(String data) {
		return new Text(data);
	}
	public final org.w3c.dom.Comment createComment(String data) {
		return new Comment(data);
	}
	public final CDATASection createCDATASection(String data) {
		return new CData(data);
	}
	public final org.w3c.dom.ProcessingInstruction
	createProcessingInstruction(String target, String data) {
		return new ProcessingInstruction(target, data);
	}
	public final org.w3c.dom.EntityReference
	createEntityReference(String name) {
		return new EntityReference(name);
	}

	/** Gets elements that matches the tag name.
	 *
	 * <p>Unlike other implementations (Xerces or Crimson), the returned list
	 * is a snapshot of the current tree -- not a "live" representation.
	 */
	public final NodeList getElementsByTagName(String tname) {
		return new FacadeNodeList(getElements(tname));
	}
	/** Gets elements that matches the tag name and namespace.
	 *
	 * <p>Unlike other implementations (Xerces or Crimson), the returned list
	 * is a snapshot of the current tree -- not a "live" representation.
	 */
	public final NodeList getElementsByTagNameNS
	(String nsURI, String lname) {
		return new FacadeNodeList(getElements(nsURI, lname, 0));
	}
	public final org.w3c.dom.Element getElementById(String elementId) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, getLocator()); //NOT YET
	}

	public final Node importNode(Node importedNode, boolean deep) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, getLocator()); //NOT YET
	}

	public String getInputEncoding() {
		return null; //Level 3 not yet
	}
	public String getXmlEncoding() {
		return null; //Level 3 not yet
	}
	public boolean getXmlStandalone() {
		return _stdalone; //Level 3 not yet
	}
	public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
		_stdalone = xmlStandalone; //Level 3 not yet
	}
	public String getXmlVersion() {
		return _ver;
	}
	public void setXmlVersion(String xmlVersion) throws DOMException {
		_ver = xmlVersion; //Level 3 not yet
	}
	public boolean getStrictErrorChecking() {
		return _stricterrck; //Level 3 not yet
	}
	public void setStrictErrorChecking(boolean strictErrorChecking) {
		_stricterrck = strictErrorChecking; //Level 3 not yet
	}
	public String getDocumentURI() {
		return _docURI; //Level 3 not yet

	}
	public void setDocumentURI(String documentURI) {
		_docURI = documentURI; //Level 3 not yet
	}
	public Node adoptNode(Node source) throws DOMException {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public DOMConfiguration getDomConfig() {
		throw new UnsupportedOperationException("DOM Level 3");
	}
	public void normalizeDocument() {
		//Level 3 not yet
	}
	public Node renameNode(Node n, String namespaceURI, String qualifiedName)
	throws DOMException {
		throw new UnsupportedOperationException("DOM Level 3");
	}

	//-- Object --//
	public final String toString() {
		StringBuffer sb = new StringBuffer(128).append("[Document: ");
		if (_docType != null)
			sb.append(_docType.toString());
		if (_root != null)
			sb.append(_root.toString());
		return sb.append(']').toString();
	}

	//-- ChildArray --//
	protected class ChildArray extends CheckableTreeArray {
		protected ChildArray() {
		}
		protected void onAdd(Object newElement, Object followingElement) {
			checkAdd(newElement, followingElement, false);
		}
		protected void onSet(Object newElement, Object replaced) {
			assert(replaced != null);
			checkAdd(newElement, replaced, true);
		}
		private void checkAdd(Object newVal, Object other, boolean replace) {
			checkWritable();

			//allowed type?
			if (!(newVal instanceof Element) && !(newVal instanceof DocType)
			&& !(newVal instanceof Comment)
			&& !(newVal instanceof ProcessingInstruction)
			&& !(newVal instanceof EntityReference))
				throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
					"Invalid type: "+(newVal!=null ? newVal.getClass(): null)+" "+newVal, getLocator());

			//to be safe, no auto-detach
			Item newItem = (Item)newVal;
			if (newItem.getParent() != null)
				throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
					"Item, "+newItem.toString()+", owned by other, "+newItem.getParent(), getLocator());

			//reject add unless no one is set before
			if (Document.this._root != null && (newItem instanceof Element) 
			&& (!replace || !(other instanceof Element)))
				throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
					"Only one root element is allowed, when adding "+newItem, getLocator());
			if (Document.this._docType != null && (newItem instanceof DocType)
			&& (!replace || !(other instanceof DocType)))
				throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,
					"Only one document type is allowed, when adding "+newItem, getLocator());

			if (replace)
				onRemove(other);
			newItem.setParent(Document.this); //then calls this.setModified

			if (newItem instanceof Element)
				Document.this._root = (Element)newItem;
			else if (newItem instanceof DocType)
				Document.this._docType = (DocType)newItem;
		}
		protected void onRemove(Object item) {
			checkWritable();

			((Item)item).setParent(null); //then calls this.setModified

			if (item instanceof Element)
				Document.this._root = null;
			else if (item instanceof DocType)
				Document.this._docType = null;
		}
	}
}
