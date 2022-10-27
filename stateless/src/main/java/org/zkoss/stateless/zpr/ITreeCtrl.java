/* ITreeCtrl.java

	Purpose:
		
	Description:
		
	History:
		4:32 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Tree;

/**
 * An addition interface to {@link ITree}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface ITreeCtrl {
	static ITree from(Tree instance) {
		ITree.Builder builder = new ITree.Builder().from((ITree) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty()) {
			for (IComponent iComponent: children) {
				if (iComponent instanceof ITreecols) {
					builder.setTreecols((ITreecols) iComponent);
				} else if (iComponent instanceof ITreechildren) {
					builder.setTreechildren((ITreechildren) iComponent);
				} else if (iComponent instanceof ITreefoot) {
					builder.setTreefoot((ITreefoot) iComponent);
				} else if (iComponent instanceof IAuxhead) {
					builder.addAuxhead((IAuxhead) iComponent);
				} else if (iComponent instanceof IPaging) {
					builder.setPagingChild((IPaging) iComponent);
				} else if (iComponent instanceof IFrozen) {
					builder.setFrozen((IFrozen) iComponent);
				}
			}
		}
		return builder.build();
	}
}
