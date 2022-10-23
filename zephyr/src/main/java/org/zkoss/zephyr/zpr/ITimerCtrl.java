/* ITimerCtrl.java

	Purpose:

	Description:

	History:
		Thu Nov 04 18:06:13 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Timer;

/**
 * An addition interface to {@link ITimer}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ITimerCtrl {
	static ITimer from(Timer instance) {
		return new ITimer.Builder()
		.from((ITimer) instance)
		.build();
	}
}