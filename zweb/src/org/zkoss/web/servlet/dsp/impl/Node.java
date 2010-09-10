/* Node.java

	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 13:53:14     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.impl;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.web.servlet.dsp.DspException;

/**
 * Represents a node in an {@link org.zkoss.web.servlet.dsp.Interpretation}.
 *
 * @author tomyeh
 */
abstract class Node {
	protected List<Node> _children;

	/** Interprets the node to generate the result to the output
	 * specified in the interpret context.
	 */
	abstract void interpret(InterpretContext ic)
	throws DspException, IOException;

	/** Adds a child. */
	void addChild(Node node) {
		if (node == null)
			throw new IllegalArgumentException("null");
		if (_children == null)
			_children = new LinkedList<Node>();
		_children.add(node);
	}
	/** Adds a child to the specified position. */
	void addChild(int pos, Node node) {
		if (node == null)
			throw new IllegalArgumentException("null");
		if (_children == null)
			_children = new LinkedList<Node>();
		_children.add(pos, node);
	}

	/** Returns the list of child nodes ({@link Node}).
	 * @since 3.0.0
	 */
	@SuppressWarnings("unchecked")
	public List<Node> getChildren() {
		return _children != null ? _children: Collections.EMPTY_LIST;
	}
}
