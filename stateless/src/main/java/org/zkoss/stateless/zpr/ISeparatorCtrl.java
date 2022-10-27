/* ISeparatorCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 29 14:59:17 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Separator;

/**
 * An addition interface to {@link ISeparator}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ISeparatorCtrl {
	static ISeparator from(Separator instance) {
		return new ISeparator.Builder()
				.from((ISeparator) instance)
				.build();
	}
}