/* IDateboxCtrl.java

	Purpose:

	Description:

	History:
		Tue Nov 09 15:34:06 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Datebox;

/**
 * An addition interface to {@link IDatebox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IDateboxCtrl {
	static IDatebox from(Datebox instance) {
		return new IDatebox.Builder()
				.from((IDatebox) instance)
				.build();
	}
}