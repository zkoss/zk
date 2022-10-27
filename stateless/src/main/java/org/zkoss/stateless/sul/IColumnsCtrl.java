/* IColumnsCtrl.java

	Purpose:

	Description:

	History:
		Tue Dec 28 14:45:40 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Columns;

/**
 * An addition interface to {@link IColumns}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IColumnsCtrl {
	static IColumns from(Columns instance) {
		ImmutableIColumns.Builder builder = new IColumns.Builder().from((IColumns) instance);
		List<IColumn<IAnyGroup>> children = (List<IColumn<IAnyGroup>>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}