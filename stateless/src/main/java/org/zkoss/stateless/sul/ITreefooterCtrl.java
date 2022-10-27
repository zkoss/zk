/* ITreefooterCtrl.java

	Purpose:
		
	Description:
		
	History:
		5:27 PM 2021/10/25, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Treefooter;

/**
 * An addition interface to {@link ITreefooter}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface ITreefooterCtrl {
	static ITreefooter from(Treefooter instance) {
		ITreefooter.Builder builder = new ITreefooter.Builder().from((ITreefooter) instance);
		List<? extends IComponent> children = Immutables.proxyIChildren(
				instance);
		if (!children.isEmpty())
			builder.setChildren((List<IAnyGroup>) children);
		return builder.build();
	}
}
