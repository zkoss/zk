/* ITabpanelsCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 28 15:01:43 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Tabpanels;
import org.zkoss.zephyr.ui.util.Immutables;

import java.util.List;

/**
 * An addition interface to {@link ITabpanels}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ITabpanelsCtrl {
	static ITabpanels from(Tabpanels instance) {
		ImmutableITabpanels.Builder builder = new ITabpanels.Builder().from((ITabpanels) instance);
		List<ITabpanel> children = (List<ITabpanel>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}