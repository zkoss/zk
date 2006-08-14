/* CategoryModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Aug 14 10:25:14     2006, Created by henrichen@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html;

import java.util.Collection;

/**
 * A catetory chart data model.
 *
 * @author <a href="mailto:henrichen@potix.com">henrichen@potix.com</a>
 * @see Chart
 * @see SimpleChartEngine
 */
public interface CategoryModel extends ChartModel {
	/**
	 * Get all series as a collection.
	 */
	public Collection getSeries();

	/**
	 * Get categories of a specified series as a collection.
	 */
	public Collection getCategories(Comparable series);
	
	/**
	 * Get value of the specified series and category.
	 * @param series the series
	 * @param category the category.
	 */
	public Number getValue(Comparable series, Comparable category);

	/**
	 * remove the whole specified series.
	 * @param series the series
	 */	
	public void removeSeries(Comparable series);

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