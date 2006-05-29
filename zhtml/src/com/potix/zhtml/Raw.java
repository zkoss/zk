/* Raw.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct  4 09:15:59     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zhtml;

import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.ext.DynamicTag;

import com.potix.zhtml.impl.AbstractTag;

/**
 * The raw component used to generate raw HTML elements.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Raw extends AbstractTag implements DynamicTag {
	public Raw(String tagname) {
		super(tagname);
	}
	public Raw() {
	}

	//-- DynamicTag --//
	public boolean hasTag(String tagname) {
		return tagname != null && !"zscript".equals(tagname)
			&& !"attribute".equals(tagname);
	}
	public void setTag(String tagname) throws WrongValueException {
		if (tagname == null || tagname.length() == 0)
			throw new WrongValueException("A tag name is required");
		_tagnm = tagname;
	}
}
