/* BranchTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 20 17:09:09     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.jsp.impl;

import java.io.IOException;

import javax.servlet.jsp.JspException;

/**
 * The skeletal class used to implement the JSP tag for ZK components
 * that might have child tags (and/or components).
 *
 * <p>Remember to declare the following in the TLD file:
 * &lt;body-content&gt;scriptless&lt;/body-content&gt;
 *
 * @author tomyeh
 */
abstract public class BranchTag extends LeafTag {
	private final Children _children = new Children();

	/** Adds a child tag.
	 */
	/*package*/ void addChildTag(LeafTag child) {
		_children.addChildTag(child);
	}


	/** To process this page tag.
	 * The deriving class rarely need to override this method.
	 */
	public void doTag() throws JspException, IOException {
		if (!isEffective())
			return; //nothing to do

		initComponent();
	}
}
