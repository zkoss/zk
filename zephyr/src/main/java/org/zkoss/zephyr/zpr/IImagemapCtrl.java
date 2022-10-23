/* IImagemapCtrl.java

	Purpose:

	Description:

	History:
		Wed Oct 27 14:38:18 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import org.zkoss.zul.Imagemap;
import org.zkoss.zephyr.ui.util.Immutables;

import java.util.List;

/**
 * An addition interface to {@link IImagemap}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IImagemapCtrl {
	static IImagemap from(Imagemap instance) {
		ImmutableIImagemap.Builder builder = new IImagemap.Builder().from((IImagemap) instance);
		List<IArea> children = (List<IArea>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}