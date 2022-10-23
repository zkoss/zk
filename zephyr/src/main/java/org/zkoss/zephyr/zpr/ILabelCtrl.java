/* ILabelCtrl.java

	Purpose:
		
	Description:
		
	History:
		3:09 PM 2021/9/29, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Label;

/**
 * An addition interface to {@link ILabel}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface ILabelCtrl {

	static ILabel from(Label instance) {
		return new ILabel.Builder()
				.from((ILabel) instance)
				.build();
	}
}
