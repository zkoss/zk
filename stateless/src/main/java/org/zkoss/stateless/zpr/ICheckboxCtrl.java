/* ICheckboxCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 08 16:21:31 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Checkbox;

/**
 * An addition interface to {@link ICheckbox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ICheckboxCtrl {
	static ICheckbox from(Checkbox instance) {
		return new ICheckbox.Builder()
				.from((ICheckbox) instance)
				.build();
	}
}