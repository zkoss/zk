/* AbstractTag.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jul 20 11:45:45     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsp.zul.impl;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.el.ELException;

/**
 * The skeletal class for implementing the generic JSP tags.
 * 
 * @author tomyeh
 */
public class AbstractTag extends SimpleTagSupport {
	private boolean _if = true;
	private boolean _unless = false;

	/** Returns whether this tag is effecive. If false, this tag does nothing
	 * (as if not specified at all).
	 */
	public boolean isEffective() {
		return _if && !_unless;
	}

	/** Returns the if condition.
	 * If the if condition is false, this tag is ignored.
	 * If the unless condition ({@link #getUnless}) is true, this tag is
	 * ignored, too.
	 * Tags deriving from this class shall invoke {@link #isEffective} to
	 * know the result.
	 *
	 * <p>Default: true.
	 */
	public boolean getIf() {
		return _if;
	}
	/** Sets the if condition.
	 */
	public void setIf(boolean ifcond) {
		_if = ifcond;
	}
	/** Returns the unless condition.
	 * If the unless condition is true, this tag is ignored.
	 * If the if condition ({@link #getIf}) is true, this tag is ignored, too.
	 * Tags deriving from this class shall invoke {@link #isEffective} to
	 * know the result.
	 *
	 * <p>Default: false.
	 */
	public boolean getUnless() {
		return _unless;
	}
	/** Sets the unless condition.
	 */
	public void setUnless(boolean unless) {
		_unless = unless;
	}

}
