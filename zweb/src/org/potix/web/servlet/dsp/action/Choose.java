/* Choose.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 15:33:24     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp.action;

import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;

import com.potix.web.mesg.MWeb;
import com.potix.web.servlet.ServletException;

/**
 * Provides the context for mutually exclusive conditional execution.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Choose extends AbstractAction {
	private boolean _matched, _trim = true;

	/** Returns whether any child {@link When} is evaluated to true. */
	/*package*/ boolean isMatched() {
		return _matched;
	}
	/** Sets whether any child {@link When} is evaluated to true. */
	/*package*/ void setMatched(boolean matched) {
		_matched = matched;
	}

	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws javax.servlet.ServletException, IOException {
		if (nested && isEffective()) {
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
		return "choose";
	}
}
