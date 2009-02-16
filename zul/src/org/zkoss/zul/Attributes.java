/* Attributes.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jan 22 12:51:42     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

/**
 * Common attributes used for implementation.
 *
 * @author tomyeh
 * @since 3.0.2
 */
public class Attributes {
	/** Indicates the component being render shall generate z.skipsib.
	 */
	public static final String SKIP_SIBLING = "org.zkoss.zul.SkipSibling";
	/** It is used to check the striping state of Gird or Listbox.
	 * @since 3.0.3
	 */
	public static final String STRIPE_STATE = "org.zkoss.zul.StripeState";
	/**
	 * It is used to count the count of the rendered Treeitem.
	 * @since 3.0.7
	 */
	public static final String RENDERED_ITEM_COUNT = "org.zkoss.zul.RenderedItemCount";
	/**
	 * It is used to count the count of the visible Treeitem without its Treechildren.
	 * @since 3.0.7
	 */
	public static final String VISITED_ITEM_COUNT = "org.zkoss.zul.VisitedItemCount";
	/**
	 * It is used to count the total of the visible Treeitem, including its Treechildren.
	 * @since 3.0.7
	 */
	public static final String VISITED_ITEM_TOTAL = "org.zkoss.zul.VisitedItemTotal";
}
