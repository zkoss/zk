/* ITabsCtrl.java

	Purpose:

	Description:

	History:
		Thu Oct 28 14:52:45 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Tabs;
import org.zkoss.stateless.ui.util.Immutables;

import java.util.List;

/**
 * An addition interface to {@link ITabs}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ITabsCtrl {
	static ITabs from(Tabs instance) {
		ImmutableITabs.Builder builder = new ITabs.Builder().from((ITabs) instance);
		List<ITab> children = (List<ITab>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}