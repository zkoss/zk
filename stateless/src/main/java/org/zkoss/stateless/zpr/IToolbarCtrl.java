/* IToolbarCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 22 11:12:52 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Toolbar;
import org.zkoss.stateless.ui.util.Immutables;

import java.util.List;

/**
 * An addition interface to {@link IToolbar}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IToolbarCtrl {
	static IToolbar from(Toolbar instance) {
		ImmutableIToolbar.Builder builder = new IToolbar.Builder().from((IToolbar) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}