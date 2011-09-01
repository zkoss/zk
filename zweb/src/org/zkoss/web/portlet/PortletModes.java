/* PortletModes.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 11 17:41:30     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import java.util.Map;
import java.util.HashMap;

import javax.portlet.PortletMode;

/**
 * Utilities to handles {@link PortletMode}.
 *
 * @author tomyeh
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
