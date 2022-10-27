/* ActionTarget.java

	Purpose:
		
	Description:
		
	History:
		11:36 AM 2022/1/5, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.action;

import org.zkoss.stateless.annotation.ActionVariable;

/**
 * Represents an action target which is used in {@link ActionVariable}
 * @author jumperchen
 */
public interface ActionTarget {
	/**
	 * The action target itself.
	 */
	String SELF = ".";

	/**
	 * The action target next sibling.
	 */
	String NEXT_SIBLING = SELF + "nextSibling";

	/**
	 * The action target previous sibling.
	 */
	String PREVIOUS_SIBLING = SELF + "previousSibling";

	/**
	 * The action target parent.
	 */
	String PARENT = SELF + "parent";

	/**
	 * The action target first child.
	 */
	String FIRST_CHILD = SELF + "firstChild";

	/**
	 * The action target last child.
	 */
	String LAST_CHILD = SELF + "lastChild";
}
