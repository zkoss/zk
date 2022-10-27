/* IPopupCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 08 18:50:10 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Popup;

import java.util.List;

/**
 * An addition interface to {@link IPopup}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IPopupCtrl {
	static IPopup from(Popup instance) {
		ImmutableIPopup.Builder builder = new IPopup.Builder().from((IPopup) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}