/* ISelectboxCtrl.java

	Purpose:

	Description:

	History:
		Thu Jan 13 12:26:00 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Selectbox;

/**
 * An addition interface to {@link ISelectbox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ISelectboxCtrl {
	static ISelectbox from(Selectbox instance) {
		return new ISelectbox.Builder()
				.from((ISelectbox) instance)
				.build();
	}
}