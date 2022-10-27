/* IMenuitemCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 15 19:19:52 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Menuitem;

/**
 * An addition interface to {@link IMenuitem}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IMenuitemCtrl {
	static IMenuitem from(Menuitem instance) {
		IMenuitem.Builder builder = new IMenuitem.Builder().from((IMenuitem) instance);
		return builder.build();
	}
}