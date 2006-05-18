/* DOMImplementation.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/zk1/pxcommon/src/com/potix/idom/impl/DOMImplementation.java,v 1.6 2006/05/11 07:16:25 tomyeh Exp $
	Purpose: 
	Description: 
	History:
	2001/09/28 20:21:46, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.idom.impl;

import com.potix.idom.*;

/**
 * The iDOM's implementation of DOMImplementation.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.6 $ $Date: 2006/05/11 07:16:25 $
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
