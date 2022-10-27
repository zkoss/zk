/* IPanelCtrl.java

	Purpose:

	Description:

	History:
		Thu Oct 21 14:48:01 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Panel;

/**
 * An addition interface to {@link IPanel}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IPanelCtrl {
	static IPanel from(Panel instance) {
		ImmutableIPanel.Builder builder = new IPanel.Builder().from((IPanel) instance);
		return builder.build();
	}
}