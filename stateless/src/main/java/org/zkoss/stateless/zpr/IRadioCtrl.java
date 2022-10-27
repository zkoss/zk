/* IRadioCtrl.java

	Purpose:

	Description:

	History:
		Fri Dec 10 17:45:25 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Radio;

/**
 * An addition interface to {@link IRadio}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IRadioCtrl {
	static IRadio from(Radio instance) {
		return new IRadio.Builder()
				.from((IRadio) instance)
				.build();
	}
}