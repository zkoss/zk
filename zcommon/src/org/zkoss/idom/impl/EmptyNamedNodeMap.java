/* EmptyNamedNodeMap.java

{{IS_NOTE

	Purpose: 
	Description: 
	History:
	2001/09/28 11:39:53, Create, Tom M. Yeh.
}}IS_NOTE

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.idom.impl;

import org.w3c.dom.*;

import org.zkoss.idom.*;
import org.zkoss.idom.DOMException;

/**
 * An empty NamedNodeMap.
 *
 * @author tomyeh
 */
public class EmptyNamedNodeMap implements NamedNodeMap {
	/** The ONLY instance of EmptyNamedNodeMap.
	 */
	public static final NamedNodeMap THE = new EmptyNamedNodeMap();

	protected EmptyNamedNodeMap() {
	}
	
	public int getLength() {
		return 0;
	}
	public Node getNamedItem(String name) {
		return null;
	}
	public Node getNamedItemNS(String namespaceURI, String localName) {
		return null;
	}
	public Node item(int index) {
		return null;
	}
	public Node removeNamedItem(String name) {
		throw new DOMException(DOMException.NOT_FOUND_ERR);
	}
	public Node removeNamedItemNS(String namespaceURI, String localName) {
		throw new DOMException(DOMException.NOT_FOUND_ERR);
	}
	public Node setNamedItem(Node arg) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR);
	}
	public Node setNamedItemNS(Node arg) {
		throw new DOMException(DOMException.INVALID_ACCESS_ERR);
	}
}
