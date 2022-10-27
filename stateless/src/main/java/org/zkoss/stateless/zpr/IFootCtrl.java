/* IFootCtrl.java

	Purpose:

	Description:

	History:
		Tue Dec 28 15:52:56 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Foot;

/**
 * An addition interface to {@link IFoot}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IFootCtrl {
	static IFoot from(Foot instance) {
		ImmutableIFoot.Builder builder = new IFoot.Builder().from((IFoot) instance);
		List<IFooter<IAnyGroup>> children = (List<IFooter<IAnyGroup>>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}