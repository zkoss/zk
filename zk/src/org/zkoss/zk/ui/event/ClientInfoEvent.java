/* ClientInfoEvent.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 25 16:34:05     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.event;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.zkoss.util.TimeZones;
import org.zkoss.zk.au.AuRequest;

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
	private final double _dpr;
	private final String _orient, _media;
	private final boolean _mediaMatched;
	private final ZoneId _zoneId;

	/** Converts an AU request to a client-info event.
	 * @since 5.0.0
	 */
	public static final ClientInfoEvent getClientInfoEvent(AuRequest request) {
		final Map<String, Object> data = request.getData();
		//Note: ClientInfoEvent is a broadcast event
		List inf = (List) data.get("");
		return new ClientInfoEvent(request.getCommand(), getInt(inf, 0), getInt(inf, 1), getInt(inf, 2), getInt(inf, 3),
				getInt(inf, 4), getInt(inf, 5), getInt(inf, 6), getInt(inf, 7), getDouble(inf, 8), getString(inf, 9),
				getString(inf, 10), getBoolean(inf, 11), getString(inf, 12));
	}

	private static final int getInt(List inf, int j) {
		return j < inf.size() ? (Integer) inf.get(j) : 0;
	}

	private static final double getDouble(List inf, int j) {
		return j < inf.size() ? Double.parseDouble((String) inf.get(j)) : 0;
	}

	private static final String getString(List inf, int j) {
		return j < inf.size() ? String.valueOf(inf.get(j)) : "";
	}

	private static final boolean getBoolean(List inf, int j) {
		return j < inf.size() && (Boolean) inf.get(j);
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
	 * @param dpr the device's devicePixelRatio
	 * @param orient the device's orientation
	 * @param zoneId the client's zone id
	 */
	public ClientInfoEvent(String name, int timeZoneOfs, int scrnwd, int scrnhgh, int colorDepth, int dtwd, int dthgh,
			int dtx, int dty, double dpr, String orient, String zoneId, boolean mediaMatched, String media) {
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

		//devicePixelRatio and orientation on tablet device
		_dpr = dpr;
		_orient = orient;
		_zoneId = ZoneId.of(zoneId);
		//ZK-3133 for matchMedia
		_mediaMatched = mediaMatched;
		_media = media;
	}

	/** Returns the time zone of the client.
	 * The result is a GMT based time zone without any geographical region info.
	 * @see #getZoneId()
	 * @deprecated getZoneId() is preferred since 9.0.0.
	 * getTimeZone returns a TimeZone object, which is no longer the preferred option to identify time zones.
	 * Instead, use #getZoneId(), which returns a ZoneId object, which better supports the current Instant and a LocalDateTime java APIs
	 */
	@Deprecated
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
	 * <p>To change it programmatically, use {@link org.zkoss.zk.ui.util.Clients#scrollTo}.
	 */
	public int getDesktopXOffset() {
		return _dtx;
	}

	/** The the current vertical pixel location of the top-left corner of
	 * the document in the window.
	 * It is changed by user when he scrolls the browser.
	 * <p>To change it programmatically, use {@link org.zkoss.zk.ui.util.Clients#scrollTo}.
	 */
	public int getDesktopYOffset() {
		return _dty;
	}

	/**
	 * Return the current device pixel ratio on tablet/mobile device,
	 * otherwise return 1.0 instead.
	 * @since 6.5.0
	 */
	public double getDevicePixelRatio() {
		return _dpr;
	}

	/**
	 * Return the current orientation. The orientation is portrait when the
	 * media feature height is greater than or equal to media feature width,
	 * otherwise is landscape.
	 * @since 6.5.0
	 */
	public String getOrientation() {
		return _orient;
	}

	/**
	 * Utility to check if the current orientation is portrait on tablet/mobile device.
	 * @since 6.5.0
	 */
	public boolean isPortrait() {
		return "portrait".equals(_orient);
	}

	/**
	 * Utility to check if the current orientation is portrait on tablet/mobile device.
	 * @see #isPortrait()
	 * @since 6.5.0
	 */
	public boolean isVertical() {
		return isPortrait();
	}

	/**
	 * Utility to check if the current orientation is landscape on tablet/mobile device.
	 * @since 6.5.0
	 */
	public boolean isLandscape() {
		return "landscape".equals(_orient);
	}

	/**
	 * Utility to check if the current orientation is landscape on tablet/mobile device.
	 * @see #isLandscape()
	 * @since 6.5.0
	 */
	public boolean isHorizontal() {
		return isLandscape();
	}

	/**
	 * Returns the serialized media query list which is the value of <a href="https://www.zkoss.org/javadoc/latest/zk/org/zkoss/bind/annotation/MatchMedia.html">MatchMedia</a> annotation.
	 * @since 8.0.2
	 */
	public String getMedia() {
		return _media;
	}

	/**
	 * Returns true if the serialized media query list is matched
	 * @since 8.0.2
	 */
	public boolean isMediaMatched() {
		return _mediaMatched;
	}

	/**
	 * Returns the time-zone ID of the client.
	 * Compared with {@link #getTimeZone()}, the geographical region will be
	 * used as a result.
	 * @since 9.0.0
	 */
	public ZoneId getZoneId() {
		return _zoneId;
	}
}
