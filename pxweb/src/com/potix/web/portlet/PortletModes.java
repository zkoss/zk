/* PortletModes.java

{{IS_NOTE
	$Id: PortletModes.java,v 1.3 2006/02/27 03:54:27 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Jan 11 17:41:30     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.portlet;

import java.util.Map;
import java.util.HashMap;

import javax.portlet.PortletMode;

/**
 * Utilities to handles {@link PortletMode}.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/02/27 03:54:27 $
 */
public class PortletModes {
	/** Returns the portlet mode of the specified name.
	 */
	synchronized
	public static final PortletMode toPortletMode(String modeName) {
		PortletMode mode = (PortletMode)_modes.get(modeName);
		if (mode == null)
			_modes.put(modeName, mode = new PortletMode(modeName));
		return mode;
	}
	private static final Map _modes = new HashMap(5);
	static {
		_modes.put("EDIT", PortletMode.EDIT);
		_modes.put("HELP", PortletMode.HELP);
		_modes.put("VIEW", PortletMode.VIEW);
	}
}
