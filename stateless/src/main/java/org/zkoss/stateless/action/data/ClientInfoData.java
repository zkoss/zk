/* ClientInfoData.java

	Purpose:
		
	Description:
		
	History:
		4:04 PM 2022/8/12, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action.data;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The onClientInfo action is used to notify the client's information, such
 * as time zone and screen resolutions.
 *
 * <p>This action is sent if and only if it is registered to a root component.
 * @author jumperchen
 */
public class ClientInfoData implements ActionData {
//	private final TimeZone _timeZone;
	private final int _scrnwd, _scrnhgh, _colorDepth;
	private final int _dtwd, _dthgh, _dtx, _dty;
	private final double _dpr;
	private final String _orient, _media;
	private final boolean _mediaMatched;
	private final ZoneId _zoneId;

	@JsonCreator
	protected ClientInfoData(Map data) {
		List inf = (List) data.get("");

		//Note: we have to revert the sign
		//see http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Objects:Date:getTimezoneOffset
//	Deprecated	_timeZone = TimeZones.getTimeZone(-getInt(inf, 0));
		_scrnwd = getInt(inf, 1);
		_scrnhgh = getInt(inf, 2);
		_colorDepth = getInt(inf, 3);
		_dtwd = getInt(inf, 4);
		_dthgh = getInt(inf, 5);
		_dtx = getInt(inf, 6);
		_dty = getInt(inf, 7);

		//devicePixelRatio and orientation on tablet device
		_dpr = getDouble(inf, 8);
		_orient = getString(inf, 9);
		_zoneId = ZoneId.of(getString(inf, 10));

		_mediaMatched = getBoolean(inf, 11);
		_media = getString(inf, 12);
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
	 */
	public double getDevicePixelRatio() {
		return _dpr;
	}

	/**
	 * Return the current orientation. The orientation is portrait when the
	 * media feature height is greater than or equal to media feature width,
	 * otherwise is landscape.
	 */
	public String getOrientation() {
		return _orient;
	}

	/**
	 * Utility to check if the current orientation is portrait on tablet/mobile device.
	 */
	public boolean isPortrait() {
		return "portrait".equals(_orient);
	}

	/**
	 * Utility to check if the current orientation is portrait on tablet/mobile device.
	 * @see #isPortrait()
	 */
	public boolean isVertical() {
		return isPortrait();
	}

	/**
	 * Utility to check if the current orientation is landscape on tablet/mobile device.
	 */
	public boolean isLandscape() {
		return "landscape".equals(_orient);
	}

	/**
	 * Utility to check if the current orientation is landscape on tablet/mobile device.
	 * @see #isLandscape()
	 */
	public boolean isHorizontal() {
		return isLandscape();
	}

	/**
	 * Returns the serialized media query list which is the value of <a href="https://www.zkoss.org/javadoc/latest/zk/org/zkoss/bind/annotation/MatchMedia.html">MatchMedia</a> annotation.
	 */
	public String getMedia() {
		return _media;
	}

	/**
	 * Returns true if the serialized media query list is matched
	 */
	public boolean isMediaMatched() {
		return _mediaMatched;
	}

	/**
	 * Returns the time-zone ID of the client.
	 */
	public ZoneId getZoneId() {
		return _zoneId;
	}
}