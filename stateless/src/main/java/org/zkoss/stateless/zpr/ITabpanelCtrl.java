/* ITabpanelCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 28 15:16:42 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Tabpanel;
import org.zkoss.stateless.ui.util.Immutables;

import java.util.List;

/**
 * An addition interface to {@link ITabpanel}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ITabpanelCtrl {
	static ITabpanel from(Tabpanel instance) {
		ImmutableITabpanel.Builder builder = new ITabpanel.Builder().from((ITabpanel) instance);
		List<? extends IComponent> children = Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren((List<IAnyGroup>) children);
		return builder.build();
	}
}