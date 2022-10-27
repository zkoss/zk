/* IComboboxCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 15 13:01:44 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Combobox;

/**
 * An addition interface to {@link ICombobox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IComboboxCtrl {
	static ICombobox from(Combobox instance) {
		ImmutableICombobox.Builder builder = new ICombobox.Builder().from((ICombobox) instance);
		builder.setChildren((Iterable<? extends IComboitem>) Immutables.proxyIChildren(instance));
		return builder.build();
	}
}