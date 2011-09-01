/* XYModel.java

	Purpose:
		
	Description:
		
	History:
		Sun Feb 10 11:52:11     2008, Created by henrichen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Collection;

/**
 * A XYZ chart data model.
 *
 * @author henrichen
 * @see Chart
 * @see SimpleXYModel
 * @since 3.5.0
 */	
public interface XYZModel extends XYModel {

	/**
	 * Get Z value of a specified series and data index.
	 * @param series the series.
	 * @param index the data index.
	 */
	public Number getZ(Comparable series, int index);

	/**
	 * Append an (x,y,z) into a series.
	 * @param series the series.
	 * @param x the x value.
	 * @param y the y value.
	 * @param z the z value.
	 */	
	public void addValue(Comparable series, Number x, Number y, Number z);

	/**
	 * Add an (x,y,z) into a series at the specified index.
	 * @param series the series.
	 * @param x the x value.
	 * @param y the y value.
	 * @param z the z value.
	 * @param index the data index.
	 * @since 5.0.0
	 */	
	public void addValue(Comparable series, Number x, Number y, Number z, int index);

	/**
	 * Replace an (x,y,z) into a series at the specified index.
	 * @param series the series.
	 * @param x the x value.
	 * @param y the y value.
	 * @param z the z value.
	 * @param index the data index.
	 * @since 5.0.0
	 */	
	public void setValue(Comparable series, Number x, Number y, Number z, int index);
}	
