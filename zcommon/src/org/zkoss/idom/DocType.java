/* DocType.java


	Purpose:
	Description:
	History:
	2001/10/22 16:12:44, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DocumentType;

import org.zkoss.lang.Objects;
import org.zkoss.idom.impl.*;

/**
 * The iDOM DocType.
 *
 * @author tomyeh
 * @see Document
 */
public class DocType extends AbstractItem implements DocumentType {
	/** The element being constrained */
	protected String _name;
	/** The public ID of the DOCTYPE */
	protected String _pubId;
	/** The system ID of the DOCTYPE */
	protected String _sysId;
	/** The internal subset of the DOCTYPE */
	protected String _intSubset;

	/** Constructor.
	 *
	 * @param publicId the public Id; null or empty if not availabl
	 * @param systemId the system Id; null or empty if not availabl
	 */
	public DocType(String elementName, String publicId, String systemId) {
		setName(elementName);
		setPublicId(publicId);
		setSystemId(systemId);
	}
	/** Constructor.
	 */
	public DocType(String elementName, String systemId) {
		setName(elementName);
		_pubId = "";
		setSystemId(systemId);
	}
	/** Constructor.
	 */
	public DocType(String elementName) {
		this();
		setName(elementName);
		_pubId = _sysId = "";
	}
	/** Constructor.
	 */
	protected DocType() {
		_pubId = _sysId = "";
	}

	//-- DocType extrs --//
	/**
	 * Gets the public ID of an externally referenced DTD, or an empty
	 * String if none is referenced.
	 *
	 * @return the public ID of referenced DTD; never null
	 */
	public final String getPublicId() {
		return _pubId;
	}
	/**
	 * Sets the public ID of an externally referenced DTD, or an empty
	 * String if none is referenced.
	 *
	 * @param publicId the public Id; null or empty if not availabl
	 */
	public final void setPublicId(String publicId) {
		if (publicId == null)
			publicId = "";

		_pubId = publicId;
	}
	/**
	 * Gets the system ID of an externally referenced DTD, or an empty
	 * String if none is referenced.
	 *
	 * @return the system ID of referenced DTD; never null
	 */
	public final String getSystemId() {
		return _sysId;
	}
	/**
	 * Sets the system ID of an externally referenced DTD, or an empty
	 * String if none is referenced.
	 *
	 * @param systemId the system Id; null or empty if not availabl
	 */
	public final void setSystemId(String systemId) {
		if (systemId == null)
			systemId = "";

		_sysId = systemId;
	}

	/**
	 * Gets the data for the internal subset.
	 */
	public final String getInternalSubset() {
		return _intSubset;
	}
	/**
	 * Sets the data for the internal subset.
	 */
	public final void setInternalSubset(String newData) {
		_intSubset = newData;
	}

	//-- Item --//
	/**
	 * Gets the element name being constrained. Never null.
	 */
	public final String getName() {
		return _name;
	}
	public final void setName(String elementName) {
		if (elementName == null)
			elementName = "";

		//TY: we don't verify it here because it might contain colon
		//Verifier.checkElementName(elementName, getLocator());
		_name = elementName;
	}

	//-- Node --//
	public final short getNodeType() {
		return DOCUMENT_TYPE_NODE;
	}

	//-- DocumentType --//
	public final NamedNodeMap getEntities() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, getLocator()); //NOT YET
	}
	public final NamedNodeMap getNotations() {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, getLocator()); //NOT YET
	}

	//-- Object --//
	public String toString() {
		StringBuffer sb = new StringBuffer(64).append("[DocType: ").append(_name);
		if (_pubId.length() > 0)
			sb.append(" PUBLIC ").append(_pubId);
		if (_sysId.length() > 0)
			sb.append(" SYSTEM ").append(_sysId);
		return sb.append(']').toString();
	}
}
