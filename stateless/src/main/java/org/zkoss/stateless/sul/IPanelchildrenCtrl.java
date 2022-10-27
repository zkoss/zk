/* IPanelchildrenCtrl.java

	Purpose:

	Description:

	History:
		Thu Oct 21 14:50:35 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Panelchildren;

import java.util.List;

/**
 * An addition interface to {@link IPanelchildren}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IPanelchildrenCtrl {
	static IPanelchildren from(Panelchildren instance) {
		ImmutableIPanelchildren.Builder builder = new IPanelchildren.Builder().from((IPanelchildren) instance);
		List<? extends IComponent> children = Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}