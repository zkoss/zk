/* MainWindow.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct ,31st    2007, Created by Jeff Liu
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2.grid;

import java.util.Comparator;

import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModelExt;
import org.zkoss.zul.event.ListDataEvent;
/**
 * 
 * @author Jeff
 *
 */
public class FakeListModel extends AbstractListModel implements ListModelExt {
	
	private int _size;
	private boolean _asc = true;

	public FakeListModel() {
		this(10000);
	}
	public FakeListModel(int size) {
		_size = size;
	}

	// ListModelExt
	public void sort(Comparator cmpr, boolean asc) {
//		System.out.println("==================SORT DATA================");
		_asc = asc;
		invalidate();
	}
	public void invalidate() {
		fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
	}

	//AbstractListModel
	public Object getElementAt(int v) {
		String value = "V"+(_asc ? v: _size - v);
//		System.out.println(value);
		return value;
	}
	
	//AbstractListModel
	public int getSize() {
		return _size;
	}
	
	//AbstractListModel
	public void setSize(int size){
		_size = size;
	}

}
