/* ITreeitemCtrl.java

	Purpose:
		
	Description:
		
	History:
		5:14 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.util.List;

import org.zkoss.zephyr.ui.util.Immutables;
import org.zkoss.zul.Treeitem;

/**
 * An addition interface to {@link ITreeitem}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface ITreeitemCtrl {
	static ITreeitem from(Treeitem instance) {
		ITreeitem.Builder builder = new ITreeitem.Builder().from((ITreeitem) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty()) {
			switch (children.size()) {
			case 1:
				IComponent firstChild = children.get(0);
				if (firstChild instanceof ITreerow) {
					builder.setTreerow((ITreerow) firstChild);
				} else {
					builder.setTreechildren((ITreechildren) firstChild);
				}
				break;
			case 2:
				IComponent first = children.get(0);
				IComponent second = children.get(1);
				if (first instanceof ITreerow) {
					builder.setTreerow((ITreerow) first);
					builder.setTreechildren((ITreechildren) second);
				} else {
					builder.setTreerow((ITreerow) second);
					builder.setTreechildren((ITreechildren) first);
				}
				break;
			}
		}
		return builder.build();
	}
}
