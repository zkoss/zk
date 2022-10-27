/* IProgressmeterCtrl.java

	Purpose:

	Description:

	History:
		Thu Dec 16 11:51:22 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Progressmeter;

/**
 * An addition interface to {@link IProgressmeter}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IProgressmeterCtrl {
	static IProgressmeter from(Progressmeter instance) {
		return new IProgressmeter.Builder()
				.from((IProgressmeter) instance)
				.build();
	}
}