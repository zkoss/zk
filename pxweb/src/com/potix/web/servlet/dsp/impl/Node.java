/* Node.java

{{IS_NOTE
	$Id: Node.java,v 1.5 2006/02/27 03:54:32 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Sat Sep 17 13:53:14     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp.impl;

import java.util.List;
import java.util.LinkedList;
import java.io.Writer;
import java.io.IOException;

/**
 * Represents a node in an {@link com.potix.web.servlet.dsp.Interpretation}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/02/27 03:54:32 $
 */
abstract class Node {
	protected List _children;

	/** Interprets the node to generate the result to the output
	 * specified in the interpret context.
	 */
	abstract void interpret(InterpretContext ic)
	throws javax.servlet.ServletException, IOException;

	/** Adds a child. */
	void addChild(Node node) {
		if (node == null)
			throw new IllegalArgumentException("null");
		if (_children == null)
			_children = new LinkedList();
		_children.add(node);
	}
	/** Adds a child to the specified position. */
	void addChild(int pos, Node node) {
		if (node == null)
			throw new IllegalArgumentException("null");
		if (_children == null)
			_children = new LinkedList();
		_children.add(pos, node);
	}
}
