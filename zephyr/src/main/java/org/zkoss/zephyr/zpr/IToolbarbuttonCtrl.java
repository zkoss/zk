/* IToolbarbuttonCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 12:28:57 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Toolbarbutton;

/**
 * An addition interface to {@link IToolbarbutton}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IToolbarbuttonCtrl {
	static IToolbarbutton from(Toolbarbutton instance) {
		return new IToolbarbutton.Builder()
		.from((IToolbarbutton) instance)
		.build();
	}
}