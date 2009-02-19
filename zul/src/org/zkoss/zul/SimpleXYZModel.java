/* SimpleXYZModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sun Feb 10 13:52:25     2008, Created by henrichen
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
	//-- XYModel --//
	/** Not supported since we need not only x, y, but also z information.
	 */
	public void addValue(Comparable series, Number x, Number y) {
		throw new UnsupportedOperationException("Use addValue(series, x, y, z) instead!");
	}

	//-- XYZModel --//
	public Number getZ(Comparable series, int index) {
		final List xyzTuples = (List) _seriesMap.get(series);
		
		if (xyzTuples != null) {
			return ((XYZTuple)xyzTuples.get(index)).getZ();
		}
		return null;
	}

	public void addValue(Comparable series, Number x, Number y, Number z) {
		List xyzTuples = (List) _seriesMap.get(series);
		if (xyzTuples == null) {
			xyzTuples = new ArrayList(13);
			_seriesMap.put(series, xyzTuples);
			_seriesList.add(series);
		}
		xyzTuples.add(new XYZTuple(x, y, z));
		fireEvent(ChartDataEvent.CHANGED, series, null);
	}

	//-- internal class --//
	private static class XYZTuple extends XYPair {
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

