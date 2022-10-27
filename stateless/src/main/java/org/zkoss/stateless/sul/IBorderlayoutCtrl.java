/* IBorderlayoutCtrl.java

	Purpose:

	Description:

	History:
		Wed Oct 20 15:06:32 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Borderlayout;

/**
 * An addition interface to {@link IBorderlayout}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IBorderlayoutCtrl {
	static IBorderlayout from(Borderlayout instance) {
		ImmutableIBorderlayout.Builder builder = new IBorderlayout.Builder().from((IBorderlayout) instance);
		return builder.build();
	}
}