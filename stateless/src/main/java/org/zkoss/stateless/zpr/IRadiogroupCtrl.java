/* IRadiogroupCtrl.java

	Purpose:

	Description:

	History:
		Fri Dec 10 17:18:59 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import java.util.List;

import org.zkoss.lang.Strings;
import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Radiogroup;

/**
 * An addition interface to {@link IRadiogroup}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IRadiogroupCtrl {
	static IRadiogroup from(Radiogroup instance) {
		ImmutableIRadiogroup.Builder builder = new IRadiogroup.Builder().from((IRadiogroup) instance);
		List<IRadio> children = (List<IRadio>) Immutables.proxyIChildren(instance);
		if (!children.isEmpty())
			builder.setChildren(children);
		return builder.build();
	}

	static String genGroupName(IRadiogroup radiogroup) {
		return Strings.encode(new StringBuffer(16).append("_pg"), System.identityHashCode(radiogroup)).toString();
	}
}