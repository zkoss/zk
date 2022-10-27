/* ITreecolsCtrl.java

	Purpose:
		
	Description:
		
	History:
		5:09 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Treecols;

/**
 * An addition interface to {@link ITreecols}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface ITreecolsCtrl {
	static ITreecols from(Treecols instance) {
		ITreecols.Builder builder = new ITreecols.Builder().from((ITreecols) instance);
		List<ITreecol> children = (List<ITreecol>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}
