/* IMenubarCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 11:24:42 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Menubar;

import java.util.List;

/**
 * An addition interface to {@link IMenubar}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IMenubarCtrl {
	static IMenubar from(Menubar instance) {
		ImmutableIMenubar.Builder builder = new IMenubar.Builder().from((IMenubar) instance);
		List<IChildrenOfMenupopup> children = (List<IChildrenOfMenupopup>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}