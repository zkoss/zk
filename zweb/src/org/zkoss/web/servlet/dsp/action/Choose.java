/* Choose.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 15:33:24     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.action;

import java.io.StringWriter;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.web.mesg.MWeb;
import org.zkoss.web.servlet.dsp.DspException;

/**
 * Provides the context for mutually exclusive conditional execution.
 *
 * @author tomyeh
 */
public class Choose extends AbstractAction {
	private boolean _matched, _trim;

	/** Returns whether any child {@link When} is evaluated to true. */
	/*package*/ boolean isMatched() {
		return _matched;
	}
	/** Sets whether any child {@link When} is evaluated to true. */
	/*package*/ void setMatched(boolean matched) {
		_matched = matched;
	}

	/** Returns whether to trim the result. */
	public boolean isTrim() {
		return _trim;
	}
	/** Sets whether to trim the result.
	 * <p>Default: true.
	 */
	public void setTrim(boolean trim) {
		_trim = trim;
	}

	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws DspException, IOException {
		if (nested && isEffective()) {
			final StringWriter out = getFragmentOut(ac, _trim);
			renderFragment(ac, out, _trim);
			if (out != null)
				ac.getOut().write(out.toString());
		}
	}

	//-- Object --//
	public String toString() {
		return "choose";
	}
}
