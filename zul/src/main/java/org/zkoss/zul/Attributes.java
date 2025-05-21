/* Attributes.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 22 12:51:42     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
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
	/**
	 * It is used to check whether Treechildren should render its children or not
	 * @since 5.0.0
	 */
	public static final String SHALL_RENDER_ITEM = "org.zkoss.zul.ShallRenderItem";

	/**
	 * It is used to store the items which are rendered from model 
	 */
	public static final String MODEL_RENDERAS = "org.zkoss.zul.model.renderAs";

	/**
	 * This is used to revert the fix of ZK-5468 starting from ZK 10.0.0, which is caused
	 * all Components with Model to be re-rendered when the model is changed, such as
	 * in Listbox and Grid without ROD, as well as in Tabbox, Searchbox, Stepbar,
	 * Organigram, and Chosenbox.
	 * <p>Note: This attribute is intended solely for backward compatibility and
	 * is not meant to be used by developers for other purposes.
	 *
	 * @since 10.2.0
	 */
	public static final String SELECTIVE_COMPONENT_UPDATE = "org.zkoss.zul.model.selectiveComponentUpdate.enable";

	//Internal use
	static final String BEFORE_MODEL_ITEMS_RENDERED = "org.zkoss.zul.BeforeModelItemsRendered";
}
