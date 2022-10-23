/* ISpanCtrl.java

	Purpose:

	Description:

	History:
		4:49 PM 2022/2/24, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.List;

import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zul.Span;

/**
 * An addition interface to {@link ISpan}
 * that is used for implementation or tools.
 *
 * @author jumperchen
 */
public interface ISpanCtrl {
	static ISpan from(Span instance) {
		ISpan.Builder builder = new ISpan.Builder().from((ISpan) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}