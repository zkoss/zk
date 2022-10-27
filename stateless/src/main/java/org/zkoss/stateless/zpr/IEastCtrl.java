/* IEastCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:33:10 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Caption;
import org.zkoss.zul.East;

/**
 * An addition interface to {@link IEast}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IEastCtrl {
	static IEast from(East instance) {
		ImmutableIEast.Builder builder = new IEast.Builder().from((IEast) instance);
		IComponent child = Immutables.proxyIChild(instance, Caption.class);
		if (child != null)
			builder.setChild((IAnyGroup) child);
		return builder.build();
	}
}