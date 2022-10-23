/* IDivCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 08 15:52:25 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zul.Div;

import java.util.List;

/**
 * An addition interface to {@link IDiv}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IDivCtrl {

	static IDiv from(Div instance) {
		ImmutableIDiv.Builder builder = new IDiv.Builder().from((IDiv) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}