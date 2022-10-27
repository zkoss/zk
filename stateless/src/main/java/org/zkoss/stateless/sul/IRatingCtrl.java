/* IRatingCtrl.java

	Purpose:

	Description:

	History:
		Thu Nov 04 09:54:48 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Rating;

/**
 * An addition interface to {@link IRating}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IRatingCtrl {
	static IRating from(Rating instance) {
		return new IRating.Builder()
				.from((IRating) instance)
				.build();
	}
}