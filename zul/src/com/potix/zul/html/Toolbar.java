/* Toolbar.java

{{IS_NOTE
	$Id: Toolbar.java,v 1.3 2006/04/17 06:39:57 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Jun 23 11:33:31     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.lang.Objects;

import com.potix.zk.ui.WrongValueException;
import com.potix.zul.html.impl.XulElement;

/**
 * A toolbar.
 *
 * <p>Default {@link #getSclass}: toolbox.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/04/17 06:39:57 $
 */
public class Toolbar extends XulElement {
	private String _orient = "horizontal";

	public Toolbar() {
		setSclass("toolbar");
	}

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	public String getOrient() {
		return _orient;
	}
	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	public void setOrient(String orient) throws WrongValueException {
		if (!"horizontal".equals(orient) && !"vertical".equals(orient))
			throw new WrongValueException("orient cannot be "+orient);

		if (!Objects.equals(_orient, orient)) {
			_orient = orient;
			invalidate(INNER);
		}
	}
}
