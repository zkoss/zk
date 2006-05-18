/* LoopStatus.java

{{IS_NOTE
	$Id: LoopStatus.java,v 1.2 2006/02/27 03:54:30 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 17:14:40     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.servlet.dsp.action;

/**
 * Exposes the current status of an iteration.
 * Used with {@link ForEach} if {@link ForEach#setVarStatus} is called.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:54:30 $
 */
public interface LoopStatus {
	/** Retrieves the index of the current round of the iteration (0-based).
	 */
	public int getIndex();
	/** Retrieves the current item in the interation.
	 */
	public Object getCurrent();
}
