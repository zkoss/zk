/* IACtrl.java

	Purpose:

	Description:

	History:
		Wed Oct 06 15:48:43 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.A;

import java.util.List;

/**
 * An addition interface to {@link IA}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IACtrl {
	static IA from(A instance) {
		ImmutableIA.Builder builder = new IA.Builder().from((IA) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}