/* IListitemCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 17:18:54 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Listitem;

/**
 * An addition interface to {@link IListitem}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IListitemCtrl {
	static IListitem from(Listitem instance) {
		IListitem.Builder builder = new IListitem.Builder().from((IListitem) instance);
		List<IListcell<IAnyGroup>> children = (List<IListcell<IAnyGroup>>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}