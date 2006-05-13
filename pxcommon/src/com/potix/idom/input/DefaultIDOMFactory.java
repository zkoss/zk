/* DefaultIDOMFactory.java

{{IS_NOTE

	$Header: //time/potix/rd/cvs/m3/pxcommon/src/com/potix/idom/input/DefaultIDOMFactory.java,v 1.6 2006/02/27 03:41:56 tomyeh Exp $
	Purpose: 
	Description: 
	History:
	2001/10/25 12:00:50, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.idom.input;

import com.potix.idom.*;

/**
 * The default iDOM factory.
 *
 * @author <a href="mailto:tomyeh@potix.com">Tom M. Yeh</a>
 * @version $Revision: 1.6 $ $Date: 2006/02/27 03:41:56 $
 */
public class DefaultIDOMFactory implements IDOMFactory {
	/** Constructor.
	 */
	public DefaultIDOMFactory() {
	}

	//-- IDOMFactory --//
	public Attribute newAttribute(String lname, String value) {
		return new Attribute(lname, value);
	}
	public Attribute newAttribute(Namespace ns, String lname, String value) {
		return new Attribute(ns, lname, value);
	}
	public CData newCData(String text) {
		return new CData(text);
	}
	public Comment newComment(String text) {
		return new Comment(text);
	}
	public DocType
	newDocType(String elementName, String publicId, String systemId) {
		return new DocType(elementName, publicId, systemId);
	}
	public Document newDocument(Element rootElement, DocType docType) {
		return new Document(rootElement, docType);
	}
	public Element newElement(Namespace ns, String lname) {
		return new Element(ns, lname);
	}
	public Element newElement(String lname) {
		return new Element(lname);
	}
	public ProcessingInstruction newProcessingInstruction(String target, String data) {
		return new ProcessingInstruction(target, data);
	}
	public EntityReference newEntityRef(String name) {
		return new EntityReference(name);
	}
	public Text newText(String text) {
		return new Text(text);
	}
}
