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
 * Common MeshElement for {@link org.zkoss.zul.Grid}, {@link org.zkoss.zul.Listbox}, {@link org.zkoss.zul.Tree}.
 * @author henrichen
 * @since 5.0.6
 */
public interface MeshElement extends XulElement {
	/**
	 * Sets column span hint of this component. 
	 * <p>String number span indicates how this component distributes remaining empty space to the 
	 * specified column(0-based). "0" means distribute remaining empty space to the 1st column; "1" means 
	 * distribute remaining empty space to the 2nd column, etc.. The spanning column will grow to 
	 * fit the extra remaining space.</p>
	 * <p>Special span hint with "true" means span ALL columns proportionally per their 
	 * original widths while null or "false" means NOT spanning any column.</p>
	 * <p>Default: null. That is, NOT span any column.</p>
	 * <p>Note span is meaningful only if there is remaining empty space for columns.</p>
	 * 
	 * @param span the column span hint.
	 * @since 5.0.6
	 * @see #getSpan 
	 * @see #setSpan(boolean)
	 */
	public void setSpan(String span);
	
	/**
	 * Return column span hint of this component.
	 * <p>Default: null
	 * @return column span hint of this component.
	 * @since 5.0.6
	 * @see #setSpan 
	 */
	public String getSpan();
	/**
	 * Sets whether distributes remaining empty space of this component to ALL columns proportionally. 
	 * <p>Default: false. That is, NOT span any column.</p>
	 * <p>Note span is meaningful only if there is remaining empty space for columns.</p>
	 * @param span whether to span the width of ALL columns to occupy the whole mesh element(grid/listbox/tree).
	 * @since 5.0.5
	 */
	public void setSpan(boolean span);
	
	/**
	 * Returns whether distributes remaining empty space of this component to ANY column. 
	 * <p>Default: false.</p>
	 * @return whether distributes remaining empty space of this component to ANY column.
	 * @since 5.0.5
	 * @see #getSpan
	 * @see #setSpan(boolean)
	 * @see #setSpan(String)
	 */
	public boolean isSpan();
	
	/**
	 * Sets whether sizing grid/listbox/tree column width by its content; it equals set hflex="min" on each column.
	 * <p>Default: false. 
	 * @param byContent 
	 * @since 5.0.0
	 */
	public void setSizedByContent(boolean byContent);
	
	/**
	 * Returns whether sizing grid/listbox/tree column width by its content. Default is false.
	 * @since 5.0.0
	 * @see #setSizedByContent
	 */
	public boolean isSizedByContent();

	/**
	 * Sets whether the auto-paging facility is turned on when mold is
	 * "paging". If it is set to true, the {@link #setPageSize} is ignored; 
	 * rather, the page size is automatically determined by the height of the 
	 * Listbox dynamically. 
	 * @param autopaging true to turn on the auto-paging facility.
	 * @since 5.0.2
	 */
	public void setAutopaging(boolean autopaging);
	
	/**
	 * Returns whether the auto-paging facility is turned on when mold is
	 * "paging". If it is set to true, the {@link #setPageSize} is ignored; 
	 * rather, the page size is automatically determined by the height of the 
	 * Listbox dynamically. 
	 * @return whether the "autopaging" facility is turned on.
	 * @since 5.0.2
	 */
	public boolean isAutopaging();
}
