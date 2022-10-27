/* IWindowCtrl.java

	Purpose:

	Description:

	History:
		Thu Oct 21 17:04:52 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Window;

import java.util.List;

/**
 * An addition interface to {@link IWindow}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IWindowCtrl {
	static IWindow from(Window instance) {
		ImmutableIWindow.Builder builder = new IWindow.Builder().from((IWindow) instance);
		List<? extends IComponent> children = Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}