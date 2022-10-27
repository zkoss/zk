/* ICellCtrl.java

	Purpose:

	Description:

	History:
		Mon Dec 27 16:21:08 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Cell;

/**
 * An addition interface to {@link ICell}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ICellCtrl {
	static ICell from(Cell instance) {
		ImmutableICell.Builder builder = new ICell.Builder().from((ICell) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}