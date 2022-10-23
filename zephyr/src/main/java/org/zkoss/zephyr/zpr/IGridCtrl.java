/* IGridCtrl.java

	Purpose:

	Description:

	History:
		Tue Dec 28 18:14:30 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.List;

import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zul.Grid;

/**
 * An addition interface to {@link IGrid}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IGridCtrl {
	static IGrid from(Grid instance) {
		ImmutableIGrid.Builder builder = new IGrid.Builder().from((IGrid) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty()) {
			for (IComponent iComponent: children) {
				if (iComponent instanceof IColumns) {
					builder.setColumns((IColumns) iComponent);
				} else if (iComponent instanceof IRows) {
					builder.setRows((IRows) iComponent);
				} else if (iComponent instanceof IFoot) {
					builder.setFoot((IFoot) iComponent);
				} else if (iComponent instanceof IAuxhead) {
					builder.addAuxhead((IAuxhead) iComponent);
				} else if (iComponent instanceof IPaging) {
					builder.setPagingChild((IPaging) iComponent);
				} else if (iComponent instanceof IFrozen) {
					builder.setFrozen((IFrozen) iComponent);
				}
			}
		}
		return builder.build();
	}
}