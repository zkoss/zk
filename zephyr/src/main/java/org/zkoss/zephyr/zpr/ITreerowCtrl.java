/* ITreerowCtrl.java

	Purpose:
		
	Description:
		
	History:
		5:18 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.List;

import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zul.Treerow;

/**
 * An addition interface to {@link ITreerow}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface ITreerowCtrl {
	static ITreerow from(Treerow instance) {
		ITreerow.Builder builder = new ITreerow.Builder().from((ITreerow) instance);
		List<ITreecell<IAnyGroup>> children = (List<ITreecell<IAnyGroup>>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}
