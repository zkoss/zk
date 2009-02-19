/* CategoryModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 14 10:25:14     2006, Created by henrichen
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
 * A catetory chart data model.
 *
 * @author henrichen
 * @see Chart
 * @see SimpleCategoryModel
 */
public interface CategoryModel extends ChartModel {
	/**
	 * Get a series of the specified index;
	 */
	public Comparable getSeries(int index);
	
	/**
	 * Get a category of the specified index;
	 */
	public Comparable getCategory(int index);

	/**
	 * Get all series as a collection.
	 */
	public Collection getSeries();

	/**
	 * Get categories of a specified series as a collection.
	 */
	public Collection getCategories();

	/**
	 * Get (series, category) pairs of this chart data model. The returned 
	 * value is a collection of List where list.get(0) is the 
	 * series, list.get(1) is the category, in the order the {@link #setValue} is 
	 * called.
	 */
	public Collection getKeys();
	
	/**
	 * Get value of the specified series and category.
	 * @param series the series
	 * @param category the category.
	 */
	public Number getValue(Comparable series, Comparable category);

	/**
	 * add or update the value of a specified series and category.
	 * @param series the series
	 * @param category the category.
	 * @param value the value
	 */
	public void setValue(Comparable series, Comparable category, Number value);

	/**
	 * remove the value of the specified series and category.
	 * @param series the series
	 * @param category the category.
	 */	
	public void removeValue(Comparable series, Comparable category);
	
	/**
	 * clear the model.
	 */
	public void clear();
}