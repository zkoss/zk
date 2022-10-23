/* ITextboxCtrl.java

	Purpose:

	Description:

	History:
		Wed Oct 13 09:18:20 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Textbox;

/**
 * An addition interface to {@link ITextbox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ITextboxCtrl {
	static ITextbox from(Textbox instance) {
		return new ITextbox.Builder()
				.from((ITextbox) instance)
				.build();
	}
	static final String TEXT = "text";
}