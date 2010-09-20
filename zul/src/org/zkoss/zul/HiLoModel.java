/* HiLoModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 14 21:10:14     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Date;
import java.util.Collection;

/**
 * A HiLo chart data model (date, open, close, high, low, volumn) usually used in
 * stock market.
 *
 * @author henrichen
 * @see Chart
 * @see SimpleHiLoModel
 */
public interface HiLoModel extends ChartModel {
	/**
	 * Get the only series.
	 */
	public Comparable<?> getSeries();

	/**
	 * Set the only series.
	 * @param series the only series in legend.
	 */	
	public void setSeries(Comparable<?> series);

	/**
	 * Get data count.
	 */
	public int getDataCount();

	/**
	 * Get date of a specified data index.
	 */
	public Date getDate(int index);
	
	/**
	 * Get Open value of a specified data index.
	 * @param index the data index.
	 */
	public Number getOpen(int index);

	/**
	 * Get High value of a specified data index.
	 * @param index the data index.
	 */
	public Number getHigh(int index);

	/**
	 * Get Low value of a specified data index.
	 * @param index the data index.
	 */
	public Number getLow(int index);

	/**
	 * Get close value of a specified data index.
	 * @param index the data index.
	 */
	public Number getClose(int index);

	/**
	 * Get volume value of a specified data index.
	 * @param index the data index.
	 */
	public Number getVolume(int index);

	/**
	 * Append an (date, open, high, low, close, volumn) tuple.
	 * @param date the date
	 * @param open the open value in the date.
	 * @param high the high value in the date.
	 * @param low the low value in the date.
	 * @param close the close value in the date.
	 * @param volumn the trading volumn in the date.
	 * 
	 */	
	public void addValue(Date date, Number open, Number high, Number low, Number close, Number volumn);

	/**
	 * Add an (date, open, high, low, close, volumn) tuple at specified index.
	 * @param date the date
	 * @param open the open value in the date.
	 * @param high the high value in the date.
	 * @param low the low value in the date.
	 * @param close the close value in the date.
	 * @param volumn the trading volumn in the date.
	 * @param index the data index
	 * @since 5.0.0
	 */	
	public void addValue(Date date, Number open, Number high, Number low, Number close, Number volumn, int index);
	
	/**
	 * Replace an (date, open, high, low, close, volumn) tuple at specified index.
	 * @param date the date
	 * @param open the open value in the date.
	 * @param high the high value in the date.
	 * @param low the low value in the date.
	 * @param close the close value in the date.
	 * @param volumn the trading volumn in the date.
	 * @param index the data index
	 * @since 5.0.0 
	 */	
	public void setValue(Date date, Number open, Number high, Number low, Number close, Number volumn, int index);
	
	/**
	 * Remove (date, open, high, low, close, volumn) tuple of a data index.
	 * @param index the data index.
	 */	
	public void removeValue(int index);

	/**
	 * clear this model.
	 */	
	public void clear();
}
