/* IListfooterCtrl.java

	Purpose:

	Description:

	History:
		Fri Jan 07 18:39:51 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Listfooter;

/**
 * An addition interface to {@link IListfooter}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IListfooterCtrl {
	static IListfooter from(Listfooter instance) {
		ImmutableIListfooter.Builder builder = new IListfooter.Builder().from((IListfooter) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}