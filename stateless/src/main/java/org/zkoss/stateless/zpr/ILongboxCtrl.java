/* ILongboxCtrl.java

	Purpose:

	Description:

	History:
		Wed Oct 27 15:59:50 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Longbox;

/**
 * An addition interface to {@link ILongbox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ILongboxCtrl {
	static ILongbox from(Longbox instance) {
		return new ILongbox.Builder()
		.from((ILongbox) instance)
		.build();
	}
}