/* ITreecellCtrl.java

	Purpose:
		
	Description:
		
	History:
		5:10 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Treecell;

/**
 * An addition interface to {@link ITreecell}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface ITreecellCtrl {
	static ITreecell from(Treecell instance) {
		ITreecell.Builder builder = new ITreecell.Builder().from((ITreecell) instance);
		List children = Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}
