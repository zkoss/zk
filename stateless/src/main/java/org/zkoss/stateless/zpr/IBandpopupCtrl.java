/* IBandpopupCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 08 15:22:05 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Bandpopup;

import java.util.List;

/**
 * An addition interface to {@link IBandpopup}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IBandpopupCtrl {
	static IBandpopup from(Bandpopup instance) {
		ImmutableIBandpopup.Builder builder = new IBandpopup.Builder().from((IBandpopup) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}