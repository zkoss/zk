/* SimpleHiLoModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 14 21:19:30     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.zul.event.ChartDataEvent;

/**
 * A HiLo data model implementation of {@link HiLoModel}.
 * A HiLo model is an model generally used in stock market to hold 
 * (date, open, high, low, close, volume) tuple data objects .
 *
 * @author henrichen
 * @see HiLoModel
 * @see Chart
 */
public class SimpleHiLoModel extends AbstractChartModel implements HiLoModel {
	private static final long serialVersionUID = 20091008182747L;
	private Comparable<?> _series; //the only series
	private List<HiLoTuple> _hlTuples = new ArrayList<HiLoTuple>(31);
	
	//-- HiLoModel --//
	public Comparable<?> getSeries() {
		return _series;
	}
	
	public void setSeries(Comparable<?> series) {
		_series = series;
	}
	
	public int getDataCount() {
		return _hlTuples.size();
	}

	public Date getDate(int index) {
		return _hlTuples.get(index).getDate();
	}
	
	public Number getOpen(int index) {
		return _hlTuples.get(index).getOpen();
	}

	public Number getHigh(int index) {
		return _hlTuples.get(index).getHigh();
	}

	public Number getLow(int index) {
		return _hlTuples.get(index).getLow();
	}

	public Number getClose(int index) {
		return _hlTuples.get(index).getClose();
	}

	public Number getVolume(int index) {
		return _hlTuples.get(index).getVolume();
	}
	
	public void addValue(Date date, Number open, Number high, Number low, Number close, Number volume) {
		addValue(date, open, high, low, close, volume, -1);
	}
	
	public void addValue(Date date, Number open, Number high, Number low, Number close, Number volume, int index) {
		int i = addValue0(date, open, high, low, close, volume, index);
		fireEvent(ChartDataEvent.ADDED, null, date, 0, i, _hlTuples.get(i).toNumbers());
	}

	public void setValue(Date date, Number open, Number high, Number low, Number close, Number volume, int index) {
		removeValue0(index);
		addValue0(date, open, high, low, close, volume, index);
		fireEvent(ChartDataEvent.CHANGED, null, date, 0, index, _hlTuples.get(index).toNumbers());
	}
	
	private int addValue0(Date date, Number open, Number high, Number low, Number close, Number volume, int index) {
		int i = index;
		if (index >= 0)
			_hlTuples.add(index, new HiLoTuple(date, open, high, low, close, volume));
		else {
			i = _hlTuples.size();
			_hlTuples.add(new HiLoTuple(date, open, high, low, close, volume));
		}
		return i;
	}
	
	public void removeValue(int index) {
		HiLoTuple value = removeValue0(index);
		fireEvent(ChartDataEvent.REMOVED, null, null, 0, index, value.toNumbers());
	}
	
	private HiLoTuple removeValue0(int index) {
		return _hlTuples.remove(index);
	}
	
	public void clear() {
		_hlTuples.clear();
		fireEvent(ChartDataEvent.REMOVED, null, null, -1, -1, null);
	}
	
	//-- internal class --//
	private static class HiLoTuple implements java.io.Serializable {
		private static final long serialVersionUID = 20091008182814L;
		private Date _date;
		private Number _open;
		private Number _high;
		private Number _low;
		private Number _close;
		private Number _volume;
		
		private HiLoTuple(Date date, Number open, Number high, Number low, Number close, Number volume) {
			_date = date;
			_open = open;
			_high = high;
			_low = low;
			_close = close;
			_volume = volume;
		}
		
		private Number[] toNumbers() {
			return new Number[] {_open, _high, _low, _close, _volume}; 
		}
		private Date getDate() {
			return _date;
		}
		
		private Number getOpen() {
			return _open;
		}
		
		private Number getHigh() {
			return _high;
		}

		private Number getLow() {
			return _low;
		}

		private Number getClose() {
			return _close;
		}

		private Number getVolume() {
			return _volume;
		}
	}
	
	public Object clone() {
		SimpleHiLoModel clone = (SimpleHiLoModel) super.clone();
		if (_hlTuples != null)
			clone._hlTuples = new ArrayList<HiLoTuple>(_hlTuples);
		return clone;
	}
}

