package org.zkoss.zkdemo.test2.grid;

import java.util.Comparator;

import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModelExt;
import org.zkoss.zul.event.ListDataEvent;

public class FakeListModel extends AbstractListModel implements ListModelExt {
	
	private int _size = 10000;
	
	public void sort(Comparator arg0, boolean arg1) {
		System.out.println("==================SORT DATA================");
		fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
	}

	public Object getElementAt(int arg0) {
		String value = "Element At :"+arg0;
		System.out.println(value);
		return value;
	}

	public int getSize() {
		return _size;
	}
	
	public void setSize(int size){
		_size = size;
	}

}
