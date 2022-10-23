/* IAreaCtrl.java

	Purpose:

	Description:

	History:
		Thu Oct 07 14:29:20 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Area;

/**
 * An addition interface to {@link IArea}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IAreaCtrl {
	static IArea from(Area instance) {
		return new IArea.Builder()
			.from((IArea) instance)
			.build();
	}
}