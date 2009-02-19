/* WaferMapModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Feb 10 18:52:12     2008, Created by henrichen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;
import org.zkoss.zul.event.ChartDataEvent;
import org.zkoss.zul.event.ChartDataListener;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A semiconductor wafer map data model to be used with wafermap chart.
 * A value of a spcified (x,y) on a wafter matrix.
 *
 * @author henrichen
 * @see Chart
 * @since 3.5.0
 */
public class WaferMapModel extends AbstractChartModel {
	private int _xsize;
	private int _ysize;
	private double _space;
	private Map _values; //(IntPair, Number)
	
	/**
	 * data model to be used with wafermap chart.
	 * @param xsize the x size of the wafer matrix.
	 * @param ysize the y size of the wafer matrix.
	 */
	public WaferMapModel(int xsize, int ysize) {
		this(xsize, ysize, 1d);
	}

	/**
	 * data model to be used with wafermap chart.
	 * @param xsize the x size of the wafer matrix.
	 * @param ysize the y size of the wafer matrix.
	 * @param space the space between chips.
	 */
	public WaferMapModel(int xsize, int ysize, double space) {
		_xsize = xsize;
		_ysize = ysize;
		_space = space;
	}
	
	/**
	 * Returns the x size of the wafer matrix.
	 */
	public int getXsize() {
		return _xsize;
	}
	
	/**
	 * Returns the y size of the wafer matrix.
	 */
	public int getYsize() {
		return _ysize;
	}
	
	/**
	 * Returns the space between chips, default to 1.
	 */
	public double getSpace() {
		return _space;
	}
	
	/** Returns the value of the given chip at (x,y) of the wafer matrix.
	 * @param x the x index of the wafer matrix.
	 * @param y the y index of the wafer matrix.
	 * @return the value of the given chip at (x,y) of the wafer matrix.
	 */
	public Number getValue(int x, int y) {
		if (_values != null) {
			return (Number) _values.get(new IntPair(x, y));
		}
		return null;
	}

	/** Add a value to the given chip at (x,y) (0-based) of the wafer chip matrix. If 
	 * the given x or y is bigger than the specified matrix size, the
	 * IndexOutOfBoundsException will be thrown.
	 * @param value the chip value on the wafer matrix.
	 * @param x the x index to specify the chip on the wafer.
	 * @param y the y index to specify the chip on the wafer.
	 */
	public void addValue(int value, int x, int y) {
		if (x >= _xsize) 
			throw new IndexOutOfBoundsException("x size: "+_xsize+", x: "+x);
		if (y >= _ysize) 
			throw new IndexOutOfBoundsException("y size: "+_ysize+", y: "+y);
		if (_values == null) {
			_values = new HashMap(256);
		}
		_values.put(new IntPair(x, y), new Integer(value));
		fireEvent(ChartDataEvent.CHANGED, null, null);
	}
	
	/** Internal Use Only. The entrySet of the added values.
	 */
	public Collection getEntrySet() {
		return _values == null ? new HashSet(0) : _values.entrySet();
	}
	
	/**
	 * remove the value of the specified x and y.
	 * @param x the x index of the wafer matrix.
	 * @param y the y index of the wafer matrix.
	 */	
	public void removeValue(int x, int y) {
		if (_values != null) {
			final Object old = _values.remove(new IntPair(x, y));
			if (old != null) {
				fireEvent(ChartDataEvent.REMOVED, null, null);
			}
		}
	}
	
	/**
	 * clear the model.
	 */
	public void clear() {
		if (_values != null && !_values.isEmpty()) {
			_values.clear();
			fireEvent(ChartDataEvent.REMOVED, null, null);
		}
	}

	//-- internal class --//
	public static class IntPair {
		private int _x;
		private int _y;
		
		private IntPair(int x, int y) {
			_x = x;
			_y = y;
		}
		
		public int getX() {
			return _x;
		}
		
		public int getY() {
			return _y;
		}
		
		public int hashCode() {
			return _x ^ _y;
		}
		
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof IntPair)) {
				return false;
			}
			final IntPair o = (IntPair) other;
			return o._x == _x && o._y == _y;
		}
	}
}

