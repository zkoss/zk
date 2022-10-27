/* IDoublespinnerCtrl.java

	Purpose:

	Description:

	History:
		Wed Oct 27 16:01:58 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Doublespinner;

/**
 * An addition interface to {@link IDoublespinner}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IDoublespinnerCtrl {
	static IDoublespinner from(Doublespinner instance) {
				return new IDoublespinner.Builder()
				.from((IDoublespinner) instance)
				.build();
	}
}