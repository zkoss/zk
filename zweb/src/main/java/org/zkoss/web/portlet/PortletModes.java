/* PortletModes.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 11 17:41:30     2005, Created by tomyeh

Copyright (C) 2004 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletMode;

/**
 * Utilities to handles {@link PortletMode}.
 *
 * @author tomyeh
 */
public class PortletModes {
	private PortletModes() {
		//no instance
	}
	
	/** Returns the portlet mode of the specified name.
	 */
	public static final PortletMode toPortletMode(String modeName) {
		PortletMode mode = _modes.get(modeName);
		if (mode == null) {
			synchronized (_modes) {
				mode = _modes.computeIfAbsent(modeName, k -> new PortletMode(modeName));
			}
		}
		return mode;
	}

	private static final Map<String, PortletMode> _modes = new HashMap<String, PortletMode>(4);

	static {
		_modes.put("EDIT", PortletMode.EDIT);
		_modes.put("HELP", PortletMode.HELP);
		_modes.put("VIEW", PortletMode.VIEW);
	}
}
