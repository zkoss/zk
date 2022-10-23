/* IAuxheaderCtrl.java

	Purpose:

	Description:

	History:
		Thu Oct 07 17:51:04 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Auxheader;

/**
 * An addition interface to {@link IAuxheader}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IAuxheaderCtrl {
	static IAuxheader from(Auxheader instance) {
		return new IAuxheader.Builder()
				.from((IAuxheader) instance)
				.build();
	}
}