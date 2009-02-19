/* Include.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep 20 10:09:14     2005, Created by tomyeh
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
 * Includes another URL.
 *
 * @author tomyeh
 */
public class Include extends AbstractAction {
	private String _page;

	/** Returns the page (URI). */
	public String getPage() {
		return _page;
	}
	/** Sets the page (URI). */
	public void setPage(String page) {
		_page = page;
	}

	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws DspException, IOException {
		if (!isEffective())
			return;
		if (nested)
			throw new DspException(MWeb.DSP_NESTED_ACTION_NOT_ALLOWED,
				new Object[] {this, new Integer(ac.getLineNumber())});
		if (_page == null)
			throw new DspException(MWeb.DSP_ATTRIBUTE_REQUIRED,
				new Object[] {this, "page", new Integer(ac.getLineNumber())});
		ac.include(_page, null);
	}

	//-- Object --//
	public String toString() {
		return "include";
	}
}
