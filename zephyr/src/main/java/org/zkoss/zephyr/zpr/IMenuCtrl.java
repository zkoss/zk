/* IMenuCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 15 18:59:59 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zul.Menu;

/**
 * An addition interface to {@link IMenu}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IMenuCtrl {
	static IMenu from(Menu instance) {
		ImmutableIMenu.Builder builder = new IMenu.Builder().from((IMenu) instance);
		IMenupopup child = (IMenupopup) Immutables.proxyIChild(instance);
		if (child != null)
			builder.setChild(child);
		return builder.build();
	}
}