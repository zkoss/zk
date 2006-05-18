/* ForEach.java

{{IS_NOTE
	$Id: ForEach.java,v 1.2 2006/03/20 14:51:11 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Mar  8 11:12:53     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.util;

/**
 * Used to denote a collection of elements.
 * Currently, only {@link com.potix.zk.ui.metainfo.InstanceDefinition}
 * uses it to represent the forEach attribute.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/03/20 14:51:11 $
 */
public interface ForEach {
	/** Advanced to the next element.
	 *
	 * @return false if there is no more element.
	 */
	public boolean next();
}
