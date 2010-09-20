/* SimpleXYZModel.java

	Purpose:
		
	Description:
		
	History:
		Sun Feb 10 13:52:25     2008, Created by henrichen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A XYZ data model implementation of {@link XYZModel}.
 * A XYZ model is an N series of (X, Y, Z) data objects .
 *
 * @author henrichen
 * @see XYModel
 * @see Chart
 * @since 3.5.0
 */
public class SimpleXYZModel extends SimpleXYModel implements XYZModel {
	private static final long serialVersionUID = 20091008183739L;

	//-- XYModel --//
	/** Not supported since we need not only x, y, but also z information.
	 */
	public void addValue(Comparable<?> series, Number x, Number y) {
		throw new UnsupportedOperationException("Use addValue(series, x, y, z) instead!");
	}
	/** Not supported since we need not only x, y, but also z information.
	 */
	public void addValue(Comparable<?> series, Number x, Number y, int index) {
		throw new UnsupportedOperationException("Use addValue(series, x, y, z, index) instead!");
	}
	/** Not supported since we need not only x, y, but also z information.
	 */
	public void setValue(Comparable<?> series, Number x, Number y, int index) {
		throw new UnsupportedOperationException("Use setValue(series, x, y, z, index) instead!");
	}
	
	//-- XYZModel --//
	public Number getZ(Comparable<?> series, int index) {
		final List<XYPair> xyzTuples = _seriesMap.get(series);
		
		if (xyzTuples != null) {
			return ((XYZTuple)xyzTuples.get(index)).getZ();
		}
		return null;
	}

	public void addValue(Comparable<?> series, Number x, Number y, Number z) {
		addValue(series, x, y, z, -1);
	}
	
	public void setValue(Comparable<?> series, Number x, Number y, Number z, int index) {
		removeValue0(series, index);
		addValue0(series, x, y, z, index);
		fireEvent(ChartDataEvent.CHANGED, series, null);
	}
	
	public void addValue(Comparable<?> series, Number x, Number y, Number z, int index) {
		addValue0(series, x, y, z, index);
		fireEvent(ChartDataEvent.CHANGED, series, null);
	}
	
	private void addValue0(Comparable<?> series, Number x, Number y, Number z, int index) {
		List<XYPair> xyzTuples = _seriesMap.get(series);
		if (xyzTuples == null) {
			xyzTuples = new ArrayList<XYPair>();
			_seriesMap.put(series, xyzTuples);
			_seriesList.add(series);
		}
		if (index >= 0)
			xyzTuples.add(index, new XYZTuple(x, y, z));
		else
			xyzTuples.add(new XYZTuple(x, y, z));
	}

	public void removeValue(Comparable<?> series, int index) {
		removeValue0(series, index);
		fireEvent(ChartDataEvent.REMOVED, series, null);
	}
	
	private void removeValue0(Comparable<?> series, int index) {
		List<XYPair> xyzTuples = _seriesMap.get(series);
		if (xyzTuples == null) {
			return;
		}
		xyzTuples.remove(index);
	}
	
	//-- internal class --//
	private static class XYZTuple extends XYPair {
		private static final long serialVersionUID = 20091008183759L;
		private Number _z;
		
		private XYZTuple(Number x, Number y, Number z) {
			super(x, y);
			_z = z;
		}
		
		public Number getZ() {
			return _z;
		}
	}
}
