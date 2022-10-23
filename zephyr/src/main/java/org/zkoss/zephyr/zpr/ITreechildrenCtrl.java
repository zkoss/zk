/* ITreechildrenCtrl.java

	Purpose:
		
	Description:
		
	History:
		5:12 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.List;

import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zul.Treechildren;

/**
 * An addition interface to {@link ITreechildren}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface ITreechildrenCtrl {
	static ITreechildren from(Treechildren instance) {
		ITreechildren.Builder builder = new ITreechildren.Builder().from((ITreechildren) instance);
		List<ITreeitem> children = (List<ITreeitem>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}
