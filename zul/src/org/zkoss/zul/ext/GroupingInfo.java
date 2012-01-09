/* GroupingInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 9, 2012 4:40:00 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zul.ext;

import org.zkoss.zul.GroupsModel;

/**
 * Represents the runtime information of each iteration caused by {@link GroupsModel}
 * @author jumperchen
 * @since 6.0.0
 */
public interface GroupingInfo {
	/** Indicates the data is a group (aka., the head of the group). */
	public static final byte GROUP = 0;
	/** Indicates the data is a group foot. */
	public static final byte GROUPFOOT = 1;
	/** Indicates the data is an element of a group. */
	public static final byte ELEMENT = 2;
	
	/** The type of the data.
	 * It is one of {@link #GROUP}, {@link #GROUPFOOT} and {@link #ELEMENT}.
	 */
	public int getType();

	/**
	 * Returns the index of this group
	 */
	public int getGroupIndex();
	
	/** The offset of an element in a group.
	 * It is meaningful only if {@link #type} is {@link #ELEMENT}.
	 */
	public int getOffset();
	
	/** Whether the group is opened.
	 * It is meaningful only if {@link #type} is {@link #GROUP}.
	 */
	public boolean isOpen();

}
