/* IListboxCtrl.java

	Purpose:

	Description:

	History:
		Tue Oct 19 17:17:56 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.stateless.ui.util.Immutables;
import org.zkoss.zul.Listbox;

/**
 * An addition interface to {@link IListbox}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IListboxCtrl {
	static IListbox from(Listbox instance) {
		ImmutableIListbox.Builder builder = new IListbox.Builder().from((IListbox) instance);
		List<IComponent> children = (List<IComponent>) Immutables.proxyIChildren(instance);
		List<IListitemBase> childrenOfListboxe = new ArrayList<>();
		if (!children.isEmpty()) {
			for (IComponent iComponent: children) {
				if (iComponent instanceof IListhead) {
					builder.setListhead((IListhead) iComponent);
				} else if (iComponent instanceof IListitemBase) {
					childrenOfListboxe.add((IListitemBase) iComponent);
				} else if (iComponent instanceof IListfoot) {
					builder.setListfoot((IListfoot) iComponent);
				} else if (iComponent instanceof IAuxhead) {
					builder.addAuxhead((IAuxhead) iComponent);
				} else if (iComponent instanceof IPaging) {
					builder.setPagingChild((IPaging) iComponent);
				} else if (iComponent instanceof IFrozen) {
					builder.setFrozen((IFrozen) iComponent);
				}
			}
			if (!childrenOfListboxe.isEmpty()) builder.setChildren(childrenOfListboxe);
		}
		return builder.build();
	}
}