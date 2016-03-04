/* ForEachStatus.java

	Purpose:
		
	Description:
		
	History:
		Wed Mar  8 12:57:14     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
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
 * <p> Since 8.0.0, we enhance this class to be consistent with JSTL's varStatus
 * properties. 
 *
 * @author tomyeh
 */
public interface ForEachStatus {
	/** Returns the status of the enclosing forEach statement.
	 */
	public ForEachStatus getPrevious();

	/** Returns the object of this iteration.
	 * @deprecated As of release 8.0.0, use {@link #getCurrent()} instead.
	 */
	public Object getEach();

	/**
	 * Returns the object of this iteration.
	 * @since 8.0.0
	 */
	public Object getCurrent();

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

	/**Returns whether the current round is the first pass through the iteration
	 * @since 8.0.0
	 */
	public boolean isFirst();

	/**Returns whether the current round is the last pass through the iteration
	 * @since 8.0.0
	 */
	public boolean isLast();

	/** Returns the value of the 'step' attribute for the associated tag,
	 * or null if no 'step' attribute was specified.
	 * @since 8.0.0
	 */
	public Integer getStep();

	/**
	 * Retrieves the "count" of the current round of the iteration. The count is
	 * a relative, 1-based sequence number identifying the current "round" of
	 * iteration (in context with all rounds the current iteration will
	 * perform). As an example, an iteration with begin = 5, end = 15, and step
	 * = 5 produces the counts 1, 2, and 3 in that order.
	 * 
	 * @since 8.0.0
	 */
	public int getCount();
}
