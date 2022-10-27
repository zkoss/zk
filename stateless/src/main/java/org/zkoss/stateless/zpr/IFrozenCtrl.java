/* IFrozenCtrl.java

	Purpose:
		
	Description:
		
	History:
		5:50 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Frozen;

/**
 * An addition interface to {@link IFrozen}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface IFrozenCtrl {
	static IFrozen from(Frozen instance) {
		IFrozen.Builder builder = new IFrozen.Builder().from((IFrozen) instance);
		List<? extends IComponent> children = Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}
