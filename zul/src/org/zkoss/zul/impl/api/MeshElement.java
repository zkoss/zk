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
	 * Sets whether to span the width of the columns to occupy the whole grid. It 
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
}
