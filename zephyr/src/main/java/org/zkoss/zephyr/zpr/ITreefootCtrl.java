/* ITreefootCtrl.java

	Purpose:

	Description:

	History:
		5:27 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.List;

import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zul.Treefoot;

/**
 * An addition interface to {@link ITreefoot}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface ITreefootCtrl {
	static ITreefoot from(Treefoot instance) {
		ITreefoot.Builder builder = new ITreefoot.Builder().from((ITreefoot) instance);
		List<ITreefooter<IAnyGroup>> children = (List<ITreefooter<IAnyGroup>>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}
