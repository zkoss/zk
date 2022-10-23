/* ISliderCtrl.java

	Purpose:

	Description:

	History:
		Wed Dec 01 17:23:20 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Slider;

/**
 * An addition interface to {@link ISlider}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface ISliderCtrl {
	static ISlider from(Slider instance) {
			return new ISlider.Builder()
				.from((ISlider) instance)
				.build();
	}
}