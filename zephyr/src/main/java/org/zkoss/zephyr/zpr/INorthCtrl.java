/* INorthCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:32:05 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zul.Caption;
import org.zkoss.zul.North;

/**
 * An addition interface to {@link INorth}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface INorthCtrl {
	static INorth from(North instance) {
		ImmutableINorth.Builder builder = new INorth.Builder().from((INorth) instance);
		IComponent child = Immutables.proxyIChild(instance, Caption.class);
		if (child != null)
			builder.setChild((IAnyGroup) child);
		return builder.build();
	}
}