/* ClientInfoEvent.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jul 25 16:34:05     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.util.TimeZone;

import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONException;
import org.zkoss.util.TimeZones;

import org.zkoss.zk.mesg.MZk;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.au.AuRequests;

/**
 * The onClientInfo event is used to notify the client's information, such
 * as time zone and screen resolutions.
 *
 * <p>This event is sent if and only if it is registered to a root component.
 *
 * <p>Note: the information returned by this event is not stored in the server.
 * Thus, you might want to store in the session's attribute
 * ({@link org.zkoss.zk.ui.Session#setAttribute}).
 *
 * @author tomyeh
 * @see org.zkoss.zk.ui.util.Clients
 */
public class ClientInfoEvent extends Event {
	private final TimeZone _timeZone;
	private final int _scrnwd, _scrnhgh, _colorDepth;
	private final int _dtwd, _dthgh, _dtx, _dty;

	/** Converts an AU request to a client-info event.
	 * @since 5.0.0
	 */
	public static final ClientInfoEvent getClientInfoEvent(AuRequest request) {
		final JSONObject data = request.getData();
		if (data == null)
			throw new UiException(MZk.ILLEGAL_REQUEST_WRONG_DATA,
				new Object[] {data, request});
		//Note: ClientInfoEvent is a broadcast event
		try {
			final JSONArray inf = data.getJSONArray("");
			return new ClientInfoEvent(request.getCommand(),
				inf.getInt(0), inf.getInt(1), inf.getInt(2), inf.getInt(3),
				inf.getInt(4), inf.getInt(5), inf.getInt(6), inf.getInt(7));
		} catch (JSONException ex) {
			throw new UiException(ex);
		}
	}

	/** Constructs an event to hold the client-info.
	 *
	 * <p>Note: {@link #getTarget} will return null. It means it is a broadcast
	 * event.
	 *
	 * @param scrnwd the screen's width
	 * @param scrnhgh the screen's height
	 * @param dtwd the desktop's width
	 * @param dthgh the desktop's height
	 * @param dtx the desktop's the left offset
	 * @param dty the desktop's the top offset
	 */
	public ClientInfoEvent(String name, int timeZoneOfs,
	int scrnwd, int scrnhgh, int colorDepth,
	int dtwd, int dthgh, int dtx, int dty) {
		super(name, null);

		final StringBuffer sb = new StringBuffer(8).append("GMT");
		//Note: we have to revert the sign
		//see http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Date:getTimezoneOffset
		_timeZone = TimeZones.getTimeZone(-timeZoneOfs);

		_scrnwd = scrnwd;
		_scrnhgh = scrnhgh;
		_colorDepth = colorDepth;

		_dtwd = dtwd;
		_dthgh = dthgh;
		_dtx = dtx;
		_dty = dty;
	}
	/** Returns the time zone of the client.
	 */
	public TimeZone getTimeZone() {
		return _timeZone;
	}
	/** Returns the pixel width of the client's screen.
	 */
	public int getScreenWidth() {
		return _scrnwd;
	}
	/** Returns the pixel height of the client's screen.
	 */
	public int getScreenHeight() {
		return _scrnhgh;
	}
	/** Returns the maximum number of colors the client's screen supports.
	 */
	public int getColorDepth() {
		return _colorDepth;
	}
	/** Returns the pixel width of the client's desktop.
	 */
	public int getDesktopWidth() {
		return _dtwd;
	}
	/** Returns the pixel height of the client's desktop.
	 */
	public int getDesktopHeight() {
		return _dthgh;
	}
	/** The the current horizontal pixel location of the top-left corner of
	 * the document in the window.
	 * It is changed by user when he scrolls the browser.
	 * <p>To change it programmingly, use {@link org.zkoss.zk.ui.util.Clients#scrollTo}.
	 */
	public int getDesktopXOffset() {
		return _dtx;
	}
	/** The the current vertical pixel location of the top-left corner of
	 * the document in the window.
	 * It is changed by user when he scrolls the browser.
	 * <p>To change it programmingly, use {@link org.zkoss.zk.ui.util.Clients#scrollTo}.
	 */
	public int getDesktopYOffset() {
		return _dty;
	}
}
