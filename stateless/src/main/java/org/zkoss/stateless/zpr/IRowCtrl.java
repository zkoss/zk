/* IRowCtrl.java

	Purpose:

	Description:

	History:
		Mon Dec 27 16:20:21 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Row;
import org.zkoss.stateless.ui.util.Immutables;

import java.util.List;

/**
 * An addition interface to {@link IRow}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IRowCtrl {
	static IRow from(Row instance) {
		ImmutableIRow.Builder builder = new IRow.Builder().from((IRow) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}