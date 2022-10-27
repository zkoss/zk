/* IFooterCtrl.java

	Purpose:

	Description:

	History:
		Tue Dec 28 15:51:30 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Footer;

/**
 * An addition interface to {@link IFooter}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IFooterCtrl {
	static IFooter from(Footer instance) {
		return new IFooter.Builder()
				.from((IFooter) instance)
				.build();
	}
}