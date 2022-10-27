/* IFileuploadCtrl.java

	Purpose:

	Description:

	History:
		Wed Oct 27 17:35:30 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import org.zkoss.zul.Fileupload;

/**
 * An addition interface to {@link IFileupload}
 * that is used for implementation or tools.
 *
 * @author katherine
 */
public interface IFileuploadCtrl {
	static IFileupload from(Fileupload instance) {
		return new IFileupload.Builder()
				.from((IFileupload) instance)
				.build();
	}
}