/* ITabboxCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 22 12:35:57 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Tabbox;

/**
 * An addition interface to {@link ITabbox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ITabboxCtrl {
	static ITabbox from(Tabbox instance) {
		ImmutableITabbox.Builder builder = new ITabbox.Builder().from((ITabbox) instance);
		return builder.build();
	}
}