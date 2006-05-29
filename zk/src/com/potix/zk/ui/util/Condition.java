/* Condition.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  2 22:07:52     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.util;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.metainfo.PageDefinition;

/**
 * A condition to test with. It could be used in any situation, but
 * we mainly use to denote wither an element in ZUL file is effective.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface Condition {
	/** Used to evaluate whether it is effective.
	 *
	 * @param comp used as the self variable and to retrieve the page definition.
	 * Ignored if null.
	 */
	public boolean isEffective(Component comp);
	/** Used to evaluate whether it is effective.
	 *
	 * @param page used as the self variable and to retrieve the page definition
	 * if pagedef is not defined. Ignored if null.
	 * @param pagedef the page definition used to retrieve the function mapper.
	 * If null and page is not null, page's definition is used.
	 * If both null, the current page's definition is used.
	 */
	public boolean isEffective(PageDefinition pagedef, Page page);
}
