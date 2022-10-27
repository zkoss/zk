/* ICombobuttonCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 08 16:58:08 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Combobutton;

/**
 * An addition interface to {@link ICombobutton}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ICombobuttonCtrl {
	static ICombobutton from(Combobutton instance) {
		ImmutableICombobutton.Builder builder = new ICombobutton.Builder().from((ICombobutton) instance);
		IComponent child = Immutables.proxyIChild(instance);
		if (child != null)
			builder.setChild((IPopup) child);
		return builder.build();
	}
}