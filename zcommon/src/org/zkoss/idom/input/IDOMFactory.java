/* IDOMFactory.java

{{IS_NOTE

	Purpose:
	Description:
	History:
	2001/10/25 11:32:45, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom.input;

import org.zkoss.idom.*;

/**
 * A dom factory. It is the interface used by SaxBuild to create
 * corresponding vertices. By implementing this interface or
 * overriding DefaultDomFactory, caller could create a set of
 * vertices other than the default ones.
 *
 * @author tomyeh
 * @see SAXBuilder
 */
public interface IDOMFactory {
	/**
	 * Creates an Attribute without namespace.
	 */
	public Attribute newAttribute(String lname, String value);
	/**
	 * Creates an Attribute with namespace.
	 */
	public Attribute newAttribute(Namespace ns, String lname, String value);
	/**
	 * Creates a CData.
	 */
	public CData newCData(String text);
	/**
	 * Creates a Comment.
	 */
	public Comment newComment(String text);
	/**
	 * Creates a DocType.
	 *
	 * @param elementName the root element's name
	 * @param publicId the public Id; null for empty
	 * @param systemId the system Id; null for empty
	 */
	public DocType newDocType(String elementName, String publicId, String systemId);
	/**
	 * Creates a Document.
	 *
	 * @param docType the document type; null for not available
	 */
	public Document newDocument(Element rootElement, DocType docType);
	/**
	 * Creates an Element with a namespace.
	 */
	public Element newElement(Namespace ns, String lname);
	/**
	 * Creates an Element without namespace.
	 */
	public Element newElement(String lname);
	/**
	 * Creates a processing instruction.
	 *
	 * @param data the raw data; null for empty
	 */
	public ProcessingInstruction
	newProcessingInstruction(String target, String data);
	/**
	 * Creates a Entityref.
	 *
	 * @param name the entity reference's name
	 */
	public EntityReference newEntityRef(String name);
	/**
	 * Creates a Text.
	 */
	public Text newText(String text);
}
