/* ITimeboxCtrl.java

	Purpose:

	Description:

	History:
		Wed Nov 10 17:26:35 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Timebox;

/**
 * An addition interface to {@link ITimebox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ITimeboxCtrl {
	static ITimebox from(Timebox instance) {
		return new ITimebox.Builder()
			.from((ITimebox) instance)
			.build();
	}
}