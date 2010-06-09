/* FacadeList.java


	Purpose: 
	Description: 
	History:
	2001/10/22 18:10:32, Create, Tom M. Yeh.

Copyright (C) 2001 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xml;

import java.util.AbstractList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The java List which is a facade of a NodeList.
 *
 * @author tomyeh
 * @see FacadeNodeList
 */
public class FacadeList extends AbstractList {
	/** The node list to facade. */
	protected NodeList _nlist;

	/** Constructor.
	 */
	public FacadeList(NodeList nlist) {
		_nlist = nlist;
	}

	//-- List --//
	public Object get(int index) {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException("index: " + index);
		return _nlist.item(index);
	}
	public int size() {
		return _nlist.getLength();
	}
}
