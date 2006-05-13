/* If.java

{{IS_NOTE
	$Id: If.java,v 1.5 2006/02/27 03:54:30 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 15:33:06     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp.action;

import java.io.IOException;
import java.io.StringWriter;

import com.potix.web.mesg.MWeb;
import com.potix.web.servlet.ServletException;

/**
 * Tests whether an condition is true and render the child only
 * if the condition is true.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/02/27 03:54:30 $
 */
public class If extends AbstractAction {
	private boolean _cond, _trim = true;

	/** Returns the test result. */
	public boolean getTest() {
		return _cond;
	}
	/** Sets the test result. */
	public void setTest(boolean cond) {
		_cond = cond;
	}
	/** Returns whether to trim the result.
	 * <p>Default: true.
	 */
	public boolean isTrim() {
		return _trim;
	}
	/** Sets whether to trim the result. */
	public void setTrim(boolean trim) {
		_trim = trim;
	}

	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws javax.servlet.ServletException, IOException {
		if (nested && _cond && isEffective()) {
			if (_trim) {
				final StringWriter sw = new StringWriter();
				ac.renderFragment(sw);
				ac.getOut().write(sw.toString().trim());
			} else {
				ac.renderFragment(null);
			}
		}
	}

	//-- Object --//
	public String toString() {
		return "if";
	}
}
