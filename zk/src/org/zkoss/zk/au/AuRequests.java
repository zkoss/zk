/* AuRequests.java

	Purpose:
		
	Description:
		
	History:
		Sat Nov 29 21:34:32     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.au;

import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.List;
import java.util.Iterator;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.MouseEvent;

/**
 * Utilities to convert {@link AuRequest} to {@link org.zkoss.zk.ui.event.Event}
 * or its deriving classes.
 * 
 * @author tomyeh
 * @since 5.0.0
 */
public class AuRequests {
	/** Converts the data of the specified request to a set of Component.
	 * The data is assumed to contain a list of item ID in the
	 * comman-separated format
	 *
	 * @return a set of components.
	 */
	public static Set convertToItems(Desktop desktop, List uuids) {
		final Set items = new LinkedHashSet();
		if (uuids != null)
			for (Iterator it = uuids.iterator(); it.hasNext();) {
				final String uuid = (String)it.next();
				items.add(desktop.getComponentByUuid(uuid.trim()));
			}
		return items;
	}

	/** Parses the key flags of a mouse event.
	 * @return a combination of {@link MouseEvent#ALT_KEY},
	 * {@link MouseEvent#SHIFT_KEY} and {@link MouseEvent#CTRL_KEY},
	 */
	public static int parseKeys(Map data) {
		int keys = 0;
		if (data != null) {
			if (getBoolean(data, "altKey")) keys |= MouseEvent.ALT_KEY;
			if (getBoolean(data, "ctrlKey")) keys |= MouseEvent.CTRL_KEY;
			if (getBoolean(data, "shiftKey")) keys |= MouseEvent.SHIFT_KEY;
			switch (getInt(data, "which", -1)) {
			case 1: keys |= MouseEvent.LEFT_CLICK; break;
			case 2: keys |= MouseEvent.MIDDLE_CLICK; break;
			case 3: keys |= MouseEvent.RIGHT_CLICK; break;
			}
		}
		return keys;
	}

	/** Returns the inner width of an AU request representing the update
	 * of inner width.
	 */
	public static String getInnerWidth(AuRequest request)
	throws UiException {
		final Map data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});
		return (String)data.get("");
	}

	/** Returns the result of an AU request representing the update result.
	 */
	public static Object getUpdateResult(AuRequest request)
	throws UiException {
		final Map data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});

		final String key = (String)data.get("contentId");
		final Object result = request.getDesktop().removeAttribute(key);
		if (result == null)
			throw new UiException("Content not found: "+key);
		return result;
	}

	/** Returns the integer value of the specified key in the data.
	 * @param defVal the default value; used if not found.
	 */
	public static int getInt(Map data, String key, int defVal) {
		final Object o = data.get(key);
		return o != null ? ((Number)o).intValue(): defVal;
	}
	/** Returns the integer value of the specified key in the data.
	 * @param defVal the default value; used if not found.
	 */
	public static long getLong(Map data, String key, long defVal) {
		final Object o = data.get(key);
		return o != null ? ((Number)o).longValue(): defVal;
	}
	/** Returns whether the specified key is defined.
	 */
	public static boolean getBoolean(Map data, String key) {
		final Object o = data.get(key);
		return o != null
			&& (!(o instanceof Boolean) || ((Boolean)o).booleanValue());
	}
}
