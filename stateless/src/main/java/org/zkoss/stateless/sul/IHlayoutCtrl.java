/* IHlayoutCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 12:07:01 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Hlayout;
import org.zkoss.stateless.ui.util.Immutables;

import java.util.List;

/**
 * An addition interface to {@link IHlayout}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IHlayoutCtrl {
	static IHlayout from(Hlayout instance) {
		ImmutableIHlayout.Builder builder = new IHlayout.Builder().from((IHlayout) instance);
		List<? extends IComponent> children = Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}