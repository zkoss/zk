/* IWestCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:33:00 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Caption;
import org.zkoss.zul.West;

/**
 * An addition interface to {@link IWest}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IWestCtrl {
	static IWest from(West instance) {
		ImmutableIWest.Builder builder = new IWest.Builder().from((IWest) instance);
		IComponent child = Immutables.proxyIChild(instance, Caption.class);
		if (child != null)
			builder.setChild((IAnyGroup) child);
		return builder.build();
	}
}