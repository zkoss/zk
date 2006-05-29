/* Head.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 10:49:25     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zhtml;

import com.potix.zhtml.impl.AbstractTag;

/**
 * The HEAD tag.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Head extends AbstractTag {
	public Head() {
		super("head");
	}

	//-- super --//
	/** Don't generate the id attribute.
	 */
	protected boolean shallHideId() {
		return true;
	}
}
