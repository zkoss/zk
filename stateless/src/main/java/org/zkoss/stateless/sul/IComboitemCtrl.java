/* IComboitemCtrl.java

	Purpose:

	Description:

	History:
		Fri Oct 15 12:38:31 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Comboitem;

/**
 * An addition interface to {@link IComboitem}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IComboitemCtrl {
	static IComboitem from(Comboitem instance) {
		return new IComboitem.Builder()
				.from((IComboitem) instance)
				.build();
	}
}