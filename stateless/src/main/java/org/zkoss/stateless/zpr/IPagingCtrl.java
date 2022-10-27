/* IPagingCtrl.java

	Purpose:
		
	Description:
		
	History:
		5:50 PM 2021/11/1, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Paging;

/**
 * An addition interface to {@link IPaging}
 * that is used for implementation or tools.
 * @author jumperchen
 */
public interface IPagingCtrl {
	static IPaging from(Paging instance) {
		IPaging.Builder builder = new IPaging.Builder().from((IPaging) instance);
		return builder.build();
	}
	static final int PAGE_SIZE = 20;
}
