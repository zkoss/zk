/* IBandboxCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 08 14:25:03 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zul.Bandbox;

/**
 * An addition interface to {@link IBandbox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IBandboxCtrl {
	static IBandbox from(Bandbox instance) {
		ImmutableIBandbox.Builder builder = new IBandbox.Builder().from((IBandbox) instance);
		IBandpopup child = (IBandpopup) Immutables.proxyIChild(instance);
		if (child != null)
			builder.setChild(child);
		return builder.build();
	}
}