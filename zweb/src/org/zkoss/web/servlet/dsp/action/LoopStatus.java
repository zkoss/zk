/* LoopStatus.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 17:14:40     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.action;

/**
 * Exposes the current status of an iteration.
 * Used with {@link ForEach} if {@link ForEach#setVarStatus} is called.
 *
 * @author tomyeh
 */
public interface LoopStatus {
	/** Retrieves the index of the current round of the iteration (0-based).
	 */
	public int getIndex();
	/** Retrieves the current item in the interation.
	 */
	public Object getCurrent();
}
