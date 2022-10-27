/* IHtmlCtrl.java

	Purpose:

	Description:

	History:
		Wed Nov 03 14:55:46 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.zkoss.zul.Html;

/**
 * An addition interface to {@link IHtml}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IHtmlCtrl {
	static IHtml from(Html instance) {
		return new IHtml.Builder()
				.from((IHtml) instance)
				.build();
	}
}