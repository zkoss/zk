/* ITrackCtrl.java

	Purpose:

	Description:

	History:
		4:04 PM 2022/3/21, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Track;

/**
 /**
 * An addition interface to {@link ITrack}
 * that is used for implementation or tools.
 *
 * @author jumperchen
 */
public interface ITrackCtrl {
	static ITrack from(Track instance) {
		ITrack.Builder builder = new ITrack.Builder()
				.from((ITrack) instance);
		return builder.build();
	}
}
