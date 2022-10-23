/* ScrollData.java

	Purpose:

	Description:

	History:
		10:56 AM 2022/2/24, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.action.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import org.zkoss.zk.au.AuRequests;

/**
 * Represents an action caused by that user is scrolling or
 * has scrolled at the client.
 * @author jumperchen
 */
public class ScrollData implements ActionData {
	private final int _pos;
	private final double _dPos;
	private boolean _outBound;

	@JsonCreator
	protected ScrollData(Map data) {
		Object decimal = data.get("decimal");
		int pos;
		double dPos = 0;

		if (decimal != null) {
			dPos = AuRequests.getDouble(data, "decimal", 0);
			pos = (int) dPos;
		} else {
			pos = AuRequests.getInt(data, "", 0);
			dPos = pos;
		}
		_dPos = dPos;
		_pos = pos;
		Object outBound = data.get("outBound");
		if (outBound != null) {
			_outBound = (Boolean) outBound;
		}
	}

	/** Returns the position.
	 */
	public final int getPos() {
		return _pos;
	}

	/** Returns the position.
	 */
	public final double getPosInDouble() {
		return _dPos;
	}

	/** Return true if the scroll position is outside of boundary on tablet/mobile device.
	 */
	public boolean isOutOfBound() {
		return _outBound;
	}
}
