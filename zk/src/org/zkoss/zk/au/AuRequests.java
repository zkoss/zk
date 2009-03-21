/* AuRequests.java

	Purpose:
		
	Description:
		
	History:
		Sat Nov 29 21:34:32     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.au;

import java.util.Set;
import java.util.LinkedHashSet;

import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONException;

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
	public static Set convertToItems(Desktop desktop, JSONArray uuids) {
		final Set items = new LinkedHashSet();
		if (uuids != null) {
			try {
				for (int len = uuids.length(), j = 0; j < len; ++j) {
					final String uuid = uuids.getString(j);
					items.add(desktop.getComponentByUuid(uuid.trim()));
				}
			} catch (JSONException ex) {
				throw new UiException(ex);
			}
		}
		return items;
	}

	/** Parses the key flags of a mouse event.
	 * @return a combination of {@link MouseEvent#ALT_KEY},
	 * {@link MouseEvent#SHIFT_KEY} and {@link MouseEvent#CTRL_KEY},
	 */
	public static int parseKeys(JSONObject flags) {
		int keys = 0;
		if (flags != null) {
			if (flags.optBoolean("altKey")) keys |= MouseEvent.ALT_KEY;
			if (flags.optBoolean("ctrlKey")) keys |= MouseEvent.CTRL_KEY;
			if (flags.optBoolean("shiftKey")) keys |= MouseEvent.SHIFT_KEY;
		}
		return keys;
	}

	/** Returns the inner width of an AU request representing the update
	 * of inner width.
	 */
	public static String getInnerWidth(AuRequest request)
	throws UiException {
		final JSONObject data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});
		return data.optString("");
	}

	/** Returns the result of an AU request representing the update result.
	 */
	public static Object getUpdateResult(AuRequest request)
	throws UiException {
		final JSONObject data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});

		try {
			final String key = data.getString("");
			final Object result = request.getDesktop().removeAttribute(key);
			if (result == null)
				throw new UiException("Content not found: "+key);
			return result;
		} catch (JSONException ex) {
			throw new UiException(ex);
		}
	}
}
