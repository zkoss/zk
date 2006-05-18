/* Include.java

{{IS_NOTE
	$Id: Include.java,v 1.2 2006/02/27 03:54:30 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Sep 20 10:09:14     2005, Created by tomyeh@potix.com
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
 * Includes another URL.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:54:30 $
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
	throws javax.servlet.ServletException, IOException {
		if (!isEffective())
			return;
		if (nested)
			throw new ServletException(MWeb.DSP_NESTED_ACTION_NOT_ALLOWED,
				new Object[] {this, new Integer(ac.getLineNumber())});
		if (_page == null)
			throw new ServletException(MWeb.DSP_ATTRIBUTE_REQUIRED,
				new Object[] {this, "page", new Integer(ac.getLineNumber())});
		ac.include(_page, null);
	}

	//-- Object --//
	public String toString() {
		return "include";
	}
}
