/* IIntboxCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 26 15:18:11 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Intbox;

/**
 * An addition interface to {@link IIntbox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IIntboxCtrl {
	static IIntbox from(Intbox instance) {
		return new IIntbox.Builder()
		.from((IIntbox) instance)
		.build();
	}
}