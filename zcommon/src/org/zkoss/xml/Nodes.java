/* Nodes.java


	Purpose: 
	Description: 
	History:
	2001/09/27 17:15:27, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xml;

import java.util.Collections;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.zkoss.idom.Item;

/**
 * Node related utilities.
 * It supports iDOM.
 *
 * @author tomyeh
 */
public class Nodes {
	/** The empty node list. */
	public static final NodeList EMPTY_NODELIST
		= new FacadeNodeList(Collections.EMPTY_LIST);

	/**
	 * Get the text value of a node.
	 *
	 * <p>If the node is <i>not</i> an element, Node.getNodeValue is called.
	 * If the node is an element, the returned string is a catenation of
	 * all values of TEXT_NODE and CDATA_SECTION_NODE.
	 *
	 * <p>Textual nodes include Text, CDATA and Binary (iDOM's extension).
	 */
	public static final String valueOf(Node node) {
		if (node instanceof Item) {
			String v = ((Item)node).getText();
			return v != null ? v: "";
		}

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			StringBuffer sb = new StringBuffer();
			NodeList nl = node.getChildNodes();
			Node child;
			for (int j=0; (child=nl.item(j)) != null; ++j) {
				int type = child.getNodeType();
				if (type==Node.TEXT_NODE || type==Node.CDATA_SECTION_NODE)
					sb.append(valueOf(child));
			}
			return sb.toString();
		}
		String v = node.getNodeValue();
		return v != null ? v: "";
	}
}
