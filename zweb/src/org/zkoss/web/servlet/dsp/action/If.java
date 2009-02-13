/* If.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 15:33:06     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.action;

import java.io.IOException;
import java.io.StringWriter;

import org.zkoss.web.mesg.MWeb;
import org.zkoss.web.servlet.dsp.DspException;

/**
 * Tests whether an condition is true and render the child only
 * if the condition is true.
 *
 * @author tomyeh
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
	throws DspException, IOException {
		if (nested && _cond && isEffective()) {
			final StringWriter out = getFragmentOut(ac, _trim);
			renderFragment(ac, out, _trim);
			if (out != null)
				ac.getOut().write(out.toString());
		}
	}

	//-- Object --//
	public String toString() {
		return "if";
	}
}
