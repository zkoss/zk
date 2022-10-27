/* IAbsolutelayoutCtrl.java

	Purpose:

	Description:

	History:
		Tue Jan 18 11:04:29 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Absolutelayout;

/**
 * An addition interface to {@link IAbsolutelayout}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IAbsolutelayoutCtrl {
	static IAbsolutelayout from(Absolutelayout instance) {
		ImmutableIAbsolutelayout.Builder builder = new IAbsolutelayout.Builder().from((IAbsolutelayout) instance);
		List<IAbsolutechildren<IAnyGroup>> children = (List<IAbsolutechildren<IAnyGroup>>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}