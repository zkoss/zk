/* PieModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 14 10:20:14     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Collection;

/**
 * A Pie chart data model.
 *
 * @author henrichen
 * @see Chart
 * @see SimplePieModel
 */
public interface PieModel extends ChartModel {
	/**
	 * Get category of the specified index (0 based).
	 * @param index the index of the category.
	 */
	public Comparable getCategory(int index);
		
	/**
	 * Get categories as a collection.
	 */
	public Collection getCategories();
	
	/**
	 * Get value of the specified category.
	 * @param category the pie category.
	 */
	public Number getValue(Comparable category);

	/**
	 * add or update the value of a specified category.
	 * @param category the pie category.
	 * @param value the pie value.
	 */
	public void setValue(Comparable category, Number value);

	/**
	 * remove the value of the specified category.
	 * @param category the pie category.
	 */	
	public void removeValue(Comparable category);
	
	/**
	 * clear the model.
	 */
	public void clear();
}