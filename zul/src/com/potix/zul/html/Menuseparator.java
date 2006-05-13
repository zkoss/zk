/* Menuseparator.java

{{IS_NOTE
	$Id: Menuseparator.java,v 1.2 2006/02/27 03:55:14 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Sep 22 10:59:00     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import com.potix.zul.html.impl.XulElement;

/**
 * Used to create a separator between menu items.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:55:14 $
 */
public class Menuseparator extends XulElement {
	//-- Component --//
	/** Not childable. */
	public boolean isChildable() {
		return false;
	}
}
