/* IListfootCtrl.java

	Purpose:

	Description:

	History:
		Fri Jan 07 18:32:45 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Listfoot;

/**
 * An addition interface to {@link IListfoot}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IListfootCtrl {
	static IListfoot from(Listfoot instance) {
		ImmutableIListfoot.Builder builder = new IListfoot.Builder().from((IListfoot) instance);
		List<IListfooter> children = (List<IListfooter>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}