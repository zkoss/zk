/* IAbsolutechildrenCtrl.java

	Purpose:

	Description:

	History:
		Wed Oct 06 15:29:24 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Absolutechildren;

/**
 * An addition interface to {@link IAbsolutechildren}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IAbsolutechildrenCtrl {
	static IAbsolutechildren from(Absolutechildren instance) {
		return new IAbsolutechildren.Builder()
			.from((IAbsolutechildren) instance)
			.build();
	}
}