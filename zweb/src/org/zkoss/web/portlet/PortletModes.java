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
	
	public static final PortletMode toPortletMode(String modeName) {
		PortletMode mode = _modes.get(modeName);
		if (mode == null) {
			synchronized (_modes) {
				mode = _modes.get(modeName);
				if (mode == null) {
					Map<String, PortletMode> modes = new HashMap<String, PortletMode>(_modes);
					modes.put(modeName, mode = new PortletMode(modeName));
					_modes = modes;
				}
			}
		}
		return mode;
	}
	private static Map<String, PortletMode> _modes = new HashMap<String, PortletMode>(4);
	static {
		_modes.put("EDIT", PortletMode.EDIT);
		_modes.put("HELP", PortletMode.HELP);
		_modes.put("VIEW", PortletMode.VIEW);
	}
}
