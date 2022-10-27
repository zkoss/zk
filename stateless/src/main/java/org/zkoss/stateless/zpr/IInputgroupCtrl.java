/* IInputgroupCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 29 19:19:20 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Inputgroup;

/**
 * An addition interface to {@link IInputgroup}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IInputgroupCtrl {
	static IInputgroup from(Inputgroup instance) {
		IInputgroup.Builder builder = new IInputgroup.Builder().from((IInputgroup) instance);
		List<IChildrenOfInputgroup> children = (List<IChildrenOfInputgroup>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}
}