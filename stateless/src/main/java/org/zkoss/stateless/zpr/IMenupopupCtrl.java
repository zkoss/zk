/* IMenupopupCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 15 18:51:31 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Menupopup;

import java.util.List;

/**
 * An addition interface to {@link IMenupopup}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IMenupopupCtrl {
	static IMenupopup from(Menupopup instance) {
		ImmutableIMenupopup.Builder builder = new IMenupopup.Builder().from((IMenupopup) instance);
		List<IChildrenOfMenupopup> children = (List<IChildrenOfMenupopup>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}