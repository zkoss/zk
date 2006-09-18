/* When.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 15:33:29     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp.action;

import java.io.IOException;

import com.potix.web.mesg.MWeb;
import com.potix.web.servlet.ServletException;

/**
 * Represents an alternative within a {@link Choose} action.
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
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
	throws javax.servlet.ServletException, IOException {
		if (!isEffective())
			return;

		final Action parent = ac.getParent();
		if (!(parent instanceof Choose))
			throw new ServletException("The parent of when must be choose");

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
