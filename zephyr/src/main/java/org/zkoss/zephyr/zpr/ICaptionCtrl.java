/* ICaptionCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 15:56:19 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Caption;

/**
 * An addition interface to {@link ICaption}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ICaptionCtrl {
	static ICaption from(Caption instance) {
		ImmutableICaption.Builder builder = new ICaption.Builder().from((ICaption) instance);
		return new ICaption.Builder()
			.from((ICaption) instance)
			.build();
	}
}