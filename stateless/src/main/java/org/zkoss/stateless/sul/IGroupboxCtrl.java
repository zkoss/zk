/* IGroupboxCtrl.java

	Purpose:

	Description:

	History:
		Wed Oct 20 18:00:50 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Groupbox;
import org.zkoss.stateless.ui.util.Immutables;

import java.util.List;

/**
 * An addition interface to {@link IGroupbox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IGroupboxCtrl {
	static IGroupbox from(Groupbox instance) {
		ImmutableIGroupbox.Builder builder = new IGroupbox.Builder().from((IGroupbox) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}