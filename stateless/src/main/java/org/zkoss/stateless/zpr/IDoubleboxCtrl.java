/* IDoubleboxCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 26 15:43:14 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Doublebox;

/**
 * An addition interface to {@link IDoublebox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IDoubleboxCtrl {
	static IDoublebox from(Doublebox instance) {
		return new IDoublebox.Builder()
				.from((IDoublebox) instance)
				.build();
	}
}