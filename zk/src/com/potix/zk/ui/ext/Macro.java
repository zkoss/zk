/* Macro.java

{{IS_NOTE
	$Id: Macro.java,v 1.1 2006/04/17 07:10:23 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Apr 17 12:51:06     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.ext;

/**
 * Implemented by a macro component such that caller could ask it to
 * create child components after setting the intialial properties.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.1 $ $Date: 2006/04/17 07:10:23 $
 * @see com.potix.zk.ui.Component#applyProperties
 */
public interface Macro {
	/** Called to generate child components based on the macro URI.
	 */
	public void createChildren();
}
