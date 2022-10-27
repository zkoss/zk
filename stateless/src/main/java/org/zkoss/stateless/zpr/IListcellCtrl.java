/* IListcellCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 17:19:25 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Listcell;

/**
 * An addition interface to {@link IListcell}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IListcellCtrl {
	static IListcell from(Listcell instance) {
		IListcell.Builder builder = new IListcell.Builder().from(
				(IListcell) instance);

		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty()) {
			builder.setChildren(children);
		}
		return builder.build();
	}
}