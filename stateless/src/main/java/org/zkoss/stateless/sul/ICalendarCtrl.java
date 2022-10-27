/* ICalendarCtrl.java

	Purpose:

	Description:

	History:
		Thu Nov 18 13:21:19 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Calendar;

/**
 * An addition interface to {@link ICalendar}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ICalendarCtrl {
	static ICalendar from(Calendar instance) {
		return new ICalendar.Builder()
			.from((ICalendar) instance)
			.build();
	}
}