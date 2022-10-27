/* IListheadCtrl.java

	Purpose:

	Description:

	History:
		Fri Jan 07 18:37:33 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Listhead;

/**
 * An addition interface to {@link IListhead}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IListheadCtrl {
	static IListhead from(Listhead instance) {
		ImmutableIListhead.Builder builder = new IListhead.Builder().from((IListhead) instance);
		List<IListheader> children = (List<IListheader>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}