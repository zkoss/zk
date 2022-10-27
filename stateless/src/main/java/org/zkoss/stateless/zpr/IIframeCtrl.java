/* IIframeCtrl.java

	Purpose:

	Description:

	History:
		Wed Nov 03 15:02:00 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Iframe;

/**
 * An addition interface to {@link IIframe}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IIframeCtrl {
	static IIframe from(Iframe instance) {
		return new IIframe.Builder()
				.from((IIframe) instance)
				.build();
	}
}