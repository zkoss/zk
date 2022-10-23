/* ISpaceCtrl.java

	Purpose:

	Description:

	History:
		Wed Nov 03 16:39:18 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Space;

/**
 * An addition interface to {@link ISpace}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ISpaceCtrl {
	static ISpace from(Space instance) {
		return new ISpace.Builder()
				.from((ISpace) instance)
				.build();
	}
}