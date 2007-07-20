/* RootTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 20 19:07:04     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.jsp.impl;

/**
 * A skeletal class to implement the root ZK tag.
 * Currently, only the page tag ({@link org.zkoss.zul.jsp.PageTag})
 * extends from this class.
 *
 * @author tomyeh
 */
abstract public class RootTag extends AbstractTag {
	private final Children _children = new Children();

	/** Adds a child tag.
	 */
	/*package*/ void addChildTag(LeafTag child) {
		_children.addChildTag(child);
	}
}
