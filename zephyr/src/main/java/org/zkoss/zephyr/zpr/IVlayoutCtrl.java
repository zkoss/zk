/* IVlayoutCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 12:07:33 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Vlayout;
import org.zkoss.zephyr.ui.util.Immutables;

import java.util.List;

/**
 * An addition interface to {@link IVlayout}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IVlayoutCtrl {
	static IVlayout from(Vlayout instance) {
		ImmutableIVlayout.Builder builder = new IVlayout.Builder().from((IVlayout) instance);
		List<? extends IComponent> children = Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}