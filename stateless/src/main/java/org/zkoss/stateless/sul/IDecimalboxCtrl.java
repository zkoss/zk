/* IDecimalboxCtrl.java

	Purpose:

	Description:

	History:
		Wed Oct 27 15:51:25 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Decimalbox;

/**
 * An addition interface to {@link IDecimalbox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IDecimalboxCtrl {
	static IDecimalbox from(Decimalbox instance) {
		return new IDecimalbox.Builder()
				.from((IDecimalbox) instance)
				.build();
	}
}