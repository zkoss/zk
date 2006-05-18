/* ForEachStatus.java

{{IS_NOTE
	$Id: ForEachStatus.java,v 1.2 2006/03/20 14:51:12 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed Mar  8 12:57:14     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.util;

/**
 * Represents the runtime information of each iteration caused by
 * {@link ForEach}.
 *
 * <p>The main use is to get the object in the outer iteration:
 * <code>forEachStatus.previous.each</code>
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/03/20 14:51:12 $
 */
public interface ForEachStatus {
	/** Returns the status of the enclosing forEach statement.
	 */
	public ForEachStatus getPrevious();
	/** Returns the object of this iteration.
	 */
	public Object getEach();
	/** Returns the index of the current iterator, starting from 0.
	 */
	public int getIndex();
}
