/* ISouthCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 16:32:48 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Caption;
import org.zkoss.zul.South;

/**
 * An addition interface to {@link ISouth}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ISouthCtrl {
	static ISouth from(South instance) {
		ImmutableISouth.Builder builder = new ISouth.Builder().from((ISouth) instance);
		IComponent child = Immutables.proxyIChild(instance, Caption.class);
		if (child != null)
			builder.setChild((IAnyGroup) child);
		return builder.build();
	}
}