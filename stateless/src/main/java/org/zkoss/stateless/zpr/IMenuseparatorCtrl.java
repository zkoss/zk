/* IMenuseparatorCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 10:36:49 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Menuseparator;

/**
 * An addition interface to {@link IMenuseparator}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IMenuseparatorCtrl {
	static IMenuseparator from(Menuseparator instance) {
		return new IMenuseparator.Builder()
			.from((IMenuseparator) instance)
			.build();
	}
}