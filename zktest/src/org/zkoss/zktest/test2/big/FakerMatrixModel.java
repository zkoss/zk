/* FakerMatrixModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Feb 23, 2012 2:29:27 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.test2.big;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.zkmax.zul.MatrixModel;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Sortable;

/**
 * A faker MatrixModel is used to handle a unlimited big table data. 
 * @author jumperchen
 */
public class FakerMatrixModel<Head extends List, Row extends List, Cell, Header> extends
		AbstractListModel<Row> implements MatrixModel<Row, Head, Cell, Header>, Sortable {
	
	// a rendering function
	private interface Fun<T> {
		public T apply(int index);
	}
	
	// A faker of key list implementation that contains a key to speed up the performance.
	// Because Java Collection framework didn't handle it well for huge data, it will
	// go through whole the list entries to receive the value for those methods,
	// hashCode(), equals(), and toString()
	private class FakerKeyList<T> extends AbstractList<T> {
		final int _size;
		Map<String, T> _updateCache = new HashMap<String,T> ();
		final Fun<?> _fn;
		final String _key;

		public FakerKeyList(int size, int key, Fun<?> fn) {
			_size = size;
			_key = key + "_" + size;
			_fn = fn;
		}

		@Override
		public int size() {
			return _size;
		}

		@Override
		public boolean isEmpty() {
			return _size == 0;
		}

		@Override
		public T get(int index) {
			// if changed, returns the changed value
			Object val = _updateCache.get(String.valueOf(index));
			if (val != null)
				return (T) val;
			return (T) _fn.apply(index);
		}

		@Override
		public T set(int index, T element) {
			_updateCache.put(String.valueOf(index), element);
			return element;
		}

		@Override
		public int hashCode() {
			return _key.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (obj instanceof FakerKeyList) {
				return _key.equals(((FakerKeyList)(obj))._key);
			}
			return false;
		}
		
		@Override
		public String toString() {
			return _key;
		}
	}

	private int _colSize;

	private int _rowSize;

	private Map<String, List<String>> _rowCache;

	private List<List<String>> _headerData;

	private Comparator<Cell> _sorting;

	private boolean _sortDir = true;

	@SuppressWarnings("unchecked")
	@Override
	public void sort(Comparator cmpr, boolean ascending) {
		_sorting = cmpr;
		_sortDir = ascending;
		fireEvent(ListDataEvent.STRUCTURE_CHANGED, -1, -1);
	}

	@Override
	public String getSortDirection(Comparator cmpr) {
		if (Objects.equals(_sorting, cmpr))
			return _sortDir ? "ascending" : "descending";
		return "natural";
	}

	public FakerMatrixModel(int colSize, int rowSize) {
		_colSize = colSize;
		_rowSize = rowSize;
		_rowCache = new HashMap<String, List<String>>();
		_headerData = new ArrayList<List<String>>();
		_headerData.add(new FakerKeyList<String>(colSize, 0, new Fun() {
			@Override
			public Object apply(int index) {
				return "Header x = " + index;
			}
		}));
	}


	public FakerMatrixModel(int colSize, int rowSize, int headSize) {
		_colSize = colSize;
		_rowSize = rowSize;
		_rowCache = new HashMap<String, List<String>>();
		_headerData = new ArrayList<List<String>>();
		for (int i = 0; i < headSize; i++) {
			final int rowIndex = i;
			_headerData.add(new FakerKeyList<String>(colSize, 0, new Fun() {
				@Override
				public Object apply(int index) {
					return "Header x = " + index + ", y = " + rowIndex;
				}
			}));
		}
	}
	public void update(Integer[] axis, String value) {
		List<String> list = _rowCache.get(String.valueOf(axis[1]));
		list.set(axis[0], value);
		this.fireEvent(ListDataEvent.CONTENTS_CHANGED, axis[0], axis[1]);
	}

	public void setSize(int colSize, int rowSize) {
		_colSize = colSize;
		_rowSize = rowSize;
		this.fireEvent(ListDataEvent.STRUCTURE_CHANGED, -1, -1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Row getElementAt(int index) {
		final int rowIndex = _sortDir ? index : getSize() - index - 1; // handle the sorting
		final String key = String.valueOf(rowIndex);
		List<String> value = _rowCache.get(key);
		if (value == null) {
			value = new FakerKeyList<String>(_colSize, rowIndex, new Fun() {
				@Override
				public Object apply(int index) {
					return "y = " + rowIndex;
				}});
			_rowCache.put(key, value);
		}
		return (Row) value;
	}

	@Override
	public int getSize() {
		return _rowSize;
	}

	@Override
	public int getColumnSize() {
		return _colSize;
	}

	@Override
	public int getHeadSize() {
		return _headerData.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Head getHeadAt(int rowIndex) {
		return (Head) _headerData.get(rowIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Cell getCellAt(Row rowData, int columnIndex) {
		return (Cell) rowData.get(columnIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Header getHeaderAt(Head headData, int columnIndex) {
		return (Header) headData.get(columnIndex);
	}

}
