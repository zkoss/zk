/* MeshElement.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Feb 11, 2011 5:35:25 PM, Created by henrichen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/


package org.zkoss.zul.impl.api;

/**
 * Common MeshElement for {@link Grid}, {@link Listbox}, {@Tree}.
 * @author henrichen
 * @since 5.0.6
 */
public interface MeshElement extends XulElement {
	/**
	 * Sets whether to span the width of the columns evenly to occupy the whole grid. It 
	 * is meaningful only if there are extra space available for columns.
	 * <p>Default: false. It means the width of a column takes only specified
	 * space based on its setting even there are extra space.
	 * @param span whether to span the width of the columns to occupy the whole grid.
	 */
	public void setSpan(boolean span);
	
	/**
	 * Returns whether span column width when there are extra space.
	 * <p>Default: false.
	 * @return whether span column width when there are extra space.
	 */
	public boolean isSpan();
	
	/**
	 * Sets whether sizing grid/listbox/tree column width by its content; it equals set hflex="min" on each column.
	 * <p>Default: false. 
	 * <p> You can also specify the "sized-by-content" attribute of component in 
	 * lang-addon.xml directly, it will then take higher priority.
	 * @param byContent 
	 * @since 5.0.0
	 */
	public void setSizedByContent(boolean byContent);
	
	/**
	 * Returns whether sizing grid/listbox/tree column width by its content. Default is false.
	 * <p>Note: if the "sized-by-content" attribute of component is specified, 
	 * it's prior to the original value.
	 * @since 5.0.0
	 * @see #setSizedByContent
	 */
	public boolean isSizedByContent();
}
