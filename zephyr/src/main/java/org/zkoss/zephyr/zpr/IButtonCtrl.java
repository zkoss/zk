/* IButtonCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 05 16:41:29 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Button;

/**
 * An addition interface to {@link IButton}
 * that is used for implementation or tools.
 * @author katherine
 */
public interface IButtonCtrl {
	static IButton from(Button instance) {
		return new IButton.Builder()
			.from((IButton) instance)
			.build();
	}
	static final String HORIZONTAL = "horizontal";
	static final String NORMAL = "normal";
	static final String BUTTON = "button";
}
