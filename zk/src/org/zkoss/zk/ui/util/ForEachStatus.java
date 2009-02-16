/* ForEachStatus.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Mar  8 12:57:14     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

/**
 * Represents the runtime information of each iteration caused by
 * {@link ForEach}.
 *
 * <p>The main use is to get the object in the outer iteration:
 * <code>forEachStatus.previous.each</code>
 *
 * @author tomyeh
 */
public interface ForEachStatus {
	/** Returns the status of the enclosing forEach statement.
	 */
	public ForEachStatus getPrevious();
	/** Returns the object of this iteration.
	 */
	public Object getEach();
	/** Returns the index of the current round of the iteration.
	 * If iteration is being performed over a subset of an underlying array,
	 * collection, or other type, the index returned is absolute with respect
	 * to the underlying collection. Indices are 0-based.
	 *
	 * <p>For example, if the iteration starts at the fifth element
	 * (forEachBegin is 4), then the first value returned by this method will be 4.
	 */
	public int getIndex();
	/** Returns the index (starting from 0) that the iteration begins at,
	 * or null if not specified (and 0 is assumed).
	 */
	public Integer getBegin();
	/** Returns the index (starting from 0) that the iteration ends at,
	 * or null if not specified (and the last element is assumed).
	 */
	public Integer getEnd();
}
