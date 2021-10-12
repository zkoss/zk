/* TemplateCtrl.java

	Purpose:
		
	Description:
		
	History:
		3:08 PM 2021/9/9, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.metainfo.TemplateInfo;

/**
 * An addition interface to {@link Template}
 * that is used for implementation or tools.
 * @author jumperchen
 * @since 10.0.0
 */
public interface TemplateCtrl {
	/**
	 * Returns the template src source, if any.
	 * @return
	 */
	String getSrc();

	/**
	 * Returns a meta information about the Template element.
	 * @return
	 */
	TemplateInfo getMeta();
}