/* DOMImplementation.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	2001/09/28 20:21:46, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom.impl;

import org.zkoss.idom.*;

/**
 * The iDOM's implementation of DOMImplementation.
 *
 * @author tomyeh
 */
public class DOMImplementation implements org.w3c.dom.DOMImplementation {
	/** DOM implementation singleton.
	 */
	public static final DOMImplementation THE = new DOMImplementation();

	protected DOMImplementation() {
	}

	//-- DOMImplementation --//
	public boolean hasFeature(String feature, String version) {
		return "XML".equalsIgnoreCase(feature) &&
			(version==null || "2.0".equals(version) || "1.0".equals(version));
	}
	public org.w3c.dom.DocumentType createDocumentType
	(String tname, String publicId, String systemId) {
		// Note that DOM2 specifies that ownerDocument = null
		return new DocType(tname, publicId, systemId);
	}
	public org.w3c.dom.Document createDocument
	(String nsURI, String tname, org.w3c.dom.DocumentType docType) {
		return new Document(new Element(nsURI, tname), (DocType)docType);
	}

	public Object getFeature(String feature, String version) {
		return null; //Level 3 not yet
	}
}
