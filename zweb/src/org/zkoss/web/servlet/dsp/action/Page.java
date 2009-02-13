/* Page.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 09:35:48     2005, Created by tomyeh
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
 * The page action used to set the page info, such as the content type.
 *
 * @author tomyeh
 */
public class Page extends AbstractAction {
	private String _ctype, _octype;

	/** Returns the content type. */
	public String getContentType() {
		return _ctype;
	}
	/** Sets the content type. */
	public void setContentType(String ctype) {
		_ctype = ctype;
	}
	/** Sets the optional content type.
	 * It is the content type generated automatically.
	 * We will ignore it if the page is included.
	 * @since 3.0.6
	 */
	public void setOptionalContentType(String ctype) {
		_octype = ctype;
	}

	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws DspException, IOException {
		if (!isEffective())
			return;
		if (nested)
			throw new DspException(MWeb.DSP_NESTED_ACTION_NOT_ALLOWED,
				new Object[] {this, new Integer(ac.getLineNumber())});
		if (_ctype != null)
			ac.setContentType(_ctype);
		else if (!ac.isIncluded() && _octype != null)
			ac.setContentType(_octype);
	}

	//-- Object --//
	public String toString() {
		return "page";
	}
}
