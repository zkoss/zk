/* IRowsCtrl.java

	Purpose:

	Description:

	History:
		Mon Dec 27 16:20:32 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.List;

import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zul.Rows;

/**
 * An addition interface to {@link IRows}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IRowsCtrl {
	static IRows from(Rows instance) {
		ImmutableIRows.Builder builder = new IRows.Builder().from((IRows) instance);
		List<IRowBase> children = (List<IRowBase>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}