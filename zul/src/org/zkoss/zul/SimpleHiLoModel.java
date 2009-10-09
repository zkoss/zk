/* SimpleHiLoModel.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 14 21:19:30     2006, Created by henrichen

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
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
	private Comparable _series; //the only series
	private List _hlTuples = new ArrayList(31);
	
	//-- HiLoModel --//
	public Comparable getSeries() {
		return _series;
	}
	
	public void setSeries(Comparable series) {
		_series = series;
	}
	
	public int getDataCount() {
		return _hlTuples.size();
	}

	public Date getDate(int index) {
		return ((HiLoTuple)_hlTuples.get(index)).getDate();
	}
	
	public Number getOpen(int index) {
		return ((HiLoTuple)_hlTuples.get(index)).getOpen();
	}

	public Number getHigh(int index) {
		return ((HiLoTuple)_hlTuples.get(index)).getHigh();
	}

	public Number getLow(int index) {
		return ((HiLoTuple)_hlTuples.get(index)).getLow();
	}

	public Number getClose(int index) {
		return ((HiLoTuple)_hlTuples.get(index)).getClose();
	}

	public Number getVolume(int index) {
		return ((HiLoTuple)_hlTuples.get(index)).getVolume();
	}
	
	public void addValue(Date date, Number open, Number high, Number low, Number close, Number volume) {
		_hlTuples.add(new HiLoTuple(date, open, high, low, close, volume));
		fireEvent(ChartDataEvent.CHANGED, null, null);
	}
	
	public void removeValue(int index) {
		_hlTuples.remove(index);
		fireEvent(ChartDataEvent.REMOVED, null, null);
	}
	
	public void clear() {
		_hlTuples.clear();
		fireEvent(ChartDataEvent.REMOVED, null, null);
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
}

