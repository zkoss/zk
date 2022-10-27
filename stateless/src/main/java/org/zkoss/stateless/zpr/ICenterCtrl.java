/* ICenterCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:33:20 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;

/**
 * An addition interface to {@link ICenter}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ICenterCtrl {
	static ICenter from(Center instance) {
		ImmutableICenter.Builder builder = new ICenter.Builder().from((ICenter) instance);
		IComponent child = Immutables.proxyIChild(instance, Caption.class);
		if (child != null)
			builder.setChild((IAnyGroup) child);
		return builder.build();
	}
}