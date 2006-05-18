/* Otherwise.java

{{IS_NOTE
	$Id: Otherwise.java,v 1.5 2006/02/27 03:54:30 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Sep  6 15:33:38     2005, Created by tomyeh@potix.com
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
 * Represents the last alternative within a {@link Choose} action.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.5 $ $Date: 2006/02/27 03:54:30 $
 */
public class Otherwise extends AbstractAction {
	//-- Action --//
	public void render(ActionContext ac, boolean nested)
	throws javax.servlet.ServletException, IOException {
		if (!nested && !isEffective())
			return;

		final Action parent = ac.getParent();
		if (!(parent instanceof Choose))
			throw new ServletException("The parent of otherwise must be choose");
		final Choose choose = (Choose)parent;
		if (!choose.isMatched())
			ac.renderFragment(null);
	}

	//-- Object --//
	public String toString() {
		return "otherwise";
	}
}
