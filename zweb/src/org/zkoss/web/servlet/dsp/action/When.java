/* When.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 15:33:29     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.action;

import java.io.IOException;

import org.zkoss.web.mesg.MWeb;
import org.zkoss.web.servlet.dsp.DspException;

/**
 * Represents an alternative within a {@link Choose} action.
 * 
 * @author tomyeh
 */
public class When extends AbstractAction {
	private boolean _cond;

	/** Returns the test result. */
	public boolean getTest() {
		return _cond;
	}
	/** Sets the test result. */
	public void setTest(boolean cond) {
		_cond = cond;
	}

	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws DspException, IOException {
		if (!isEffective())
			return;

		final Action parent = ac.getParent();
		if (!(parent instanceof Choose))
			throw new DspException("The parent of when must be choose");

		final Choose choose = (Choose)parent;
		if (_cond && !choose.isMatched()) {
			choose.setMatched(true);
			if (nested)
				ac.renderFragment(null);
		}
	}

	//-- Object --//
	public String toString() {
		return "otherwise";
	}
}
