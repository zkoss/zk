/* IStyleCtrl.java

	Purpose:

	Description:

	History:
		Wed Nov 03 15:56:44 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Style;

/**
 * An addition interface to {@link IStyle}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IStyleCtrl {
	static IStyle from(Style instance) {
		return new IStyle.Builder()
				.from((IStyle) instance)
				.build();
	}
}