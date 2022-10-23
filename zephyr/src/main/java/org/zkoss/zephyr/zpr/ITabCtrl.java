/* ITabCtrl.java

	Purpose:

	Description:

	History:
		Thu Oct 28 09:46:03 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Tab;

/**
 * An addition interface to {@link ITab}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ITabCtrl {
	static ITab from(Tab instance) {
		return new ITab.Builder()
				.from((ITab) instance)
				.build();
	}
}