/* SimpleGroupModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 5, 2008 9:32:41 AM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.util.ArraysX;
import org.zkoss.zul.event.ListDataEvent;

/**
 * A simple implementation of {@link GroupModel}.
 * Note: It assumes the content is immutable.
 * @author jumperchen
 * @since 3.5.0
 * @see GroupModel
 */
public class SimpleGroupModel extends AbstractListModel 
implements ListModelExt, GroupModel, java.io.Serializable {
    private static final long serialVersionUID = 20080505L;

	private Object[] _data;
	private int[] _groups;
	private boolean[] _gpfoots;
	private GroupModel _model;

	/**
	 * Constructor. 
	 * @param data the list to represent
	 * @param groups the int array of group that indicates the number of items 
	 * belong to the specified group.
	 * @see #SimpleGroupModel(List, int[], boolean[])
	 */
	public SimpleGroupModel(List data, int[] groups) {
		this(data, groups, null);
	}
	/**
	 * Constructor. 
	 * @param data the list to represent
	 * @param groups the int array of group that indicates the number of items 
	 * belong to the specified group.
	 * @param gpfoots the boolean array of groupfoot
	 */
	public SimpleGroupModel(List data, int[] groups, boolean[] gpfoots) {
		_data = data.toArray(new Object[data.size()]);
		_groups = groups;
		_gpfoots = gpfoots;
	}
	/**
	 * Constructor.
	 *
	 * @param data the array to represent
	 * @param model an implementation of GroupModel
	 * @see #SimpleGroupModel(Object[], GroupModel, boolean)
	 */
	public SimpleGroupModel(Object[] data, GroupModel model) {
		this(data, model, false);
	}
	/**
	 * Constructor.
	 * @param data the array to represent
	 * @param model an implementation of GroupModel
	 * @param live whether to have a 'live' {@link ListModel} on top of
	 * the specified list.
	 */
	public SimpleGroupModel(Object[] data, GroupModel model, boolean live) {
		if (data == null || model == null)
			throw new NullPointerException();
		_data = live ? data: (Object[])ArraysX.clone(data);
		_model = model;
	}
	/**
	 * Constructor.
	 *
	 * @param data the array to represent
	 * @param groups the int array of group that indicates the number of items 
	 * belong to the specified group.
	 * @see #SimpleGroupModel(Object[], int[], boolean[], boolean)
	 */
	public SimpleGroupModel(Object[] data, int[] groups) {
		this(data, groups, null,  false);
	}
	/**
	 * Constructor.
	 *
	 * @param data the array to represent
	 * @param groups the int array of group that indicates the number of items 
	 * belong to the specified group.
	 * @param live whether to have a 'live' {@link ListModel} on top of
	 * the specified list.
	 * @see #SimpleGroupModel(Object[], int[], boolean[], boolean)
	 */
	public SimpleGroupModel(Object[] data, int[] groups, boolean live) {
		this(data, groups, null, live);		
	}
	/**
	 * Constructor.
	 *
	 * @param data the array to represent
	 * @param groups the int array of group that indicates the number of items 
	 * belong to the specified group.
	 * @param gpfoots the boolean array of groupfoot
	 * @see #SimpleGroupModel(Object[], int[], boolean[], boolean)
	 */
	public SimpleGroupModel(Object[] data, int[] groups, boolean[] gpfoots) {
		this(data, groups, gpfoots,  false);
	}	
	/** Constructor.
	 *
	 * @param data the array to represent
	 * @param groups the int array of group that indicates the number of items 
	 * belong to the specified group.
	 * @param gpfoots the boolean array of groupfoot
	 * @param live whether to have a 'live' {@link ListModel} on top of
	 * the specified list.
	 * If false, the content of the specified list is copied.
	 * If true, this object is a 'facade' of the specified list,
	 * i.e., when you add or remove items from this ListModelList,
	 * the inner "live" list would be changed accordingly.
	 *
	 * However, it is not a good idea to modify <code>data</code>
	 * once it is passed to this method with live is true,
	 * since {@link Listbox} is not smart enough to hanle it.
	 */
	public SimpleGroupModel(Object[] data, int[] groups, boolean[] gpfoots, boolean live) {
		if (data == null || groups == null)
			throw new NullPointerException();
		_data = live ? data: (Object[])ArraysX.clone(data);
		_groups = groups;
		_gpfoots = gpfoots;
	}
	public int getSize() {
		return _data.length;
	}
	public Object getElementAt(int j) {
		return _data[j];
	}
	public void sort(Comparator cmpr, boolean ascending) {
		for (int i = 0, from = 1; i <  getGroupCount() && from < _data.length; i++, from++) {
			int to = from + getChildCount(i);
			if (to > _data.length) to = _data.length;
			if (from < to) Arrays.sort(_data, from, to, cmpr);
			from += getChildCount(i);
		}
		fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
	}
	public int getChildCount(int index) {
		return _model != null ? _model.getChildCount(index) :  _groups[index];
	}
	public int getGroupCount() {
		return  _model != null ? _model.getGroupCount() : _groups.length;
	}	
	public boolean hasGroupfoot(int index) {
		return  _model != null ? _model.hasGroupfoot(index) : _gpfoots != null ? _gpfoots[index]: false;
	}
	public void groupByField(Comparator cmpr, boolean ascending, int index) {
		if (_model != null) {
			_model.groupByField(cmpr, ascending, index);
			return;
		}
		if (cmpr == null) return;
		LinkedList list = new LinkedList(Arrays.asList(_data));
		for (int i = 0, z = 0; i < getGroupCount(); i++) {
			list.remove(z);
			z += getChildCount(i);
			if (hasGroupfoot(i))
				list.remove(z);
		}
		Object[] data = list.toArray();
		Arrays.sort(data, cmpr);
		list.clear();
		Object group = null;
		LinkedList groups = new LinkedList();
		boolean existed = true;		
		for (int i = 0, cnt = 0; i < data.length; i++, cnt++) {
			if (!Objects.equals(group, data[i])) {
				if (group != null) groups.add(new Integer(cnt));
				group = data[i];
				existed = false;
			}
			if (!existed) {
				list.add(group);
				existed = true;
				cnt = 0;
			}
			list.add(data[i]);
			if (i == data.length - 1)
				groups.add(new Integer(cnt));
		}
		_data = list.toArray();
		_groups = new int[groups.size()];
		int i = 0;
		for (Iterator it = groups.iterator(); it.hasNext(); i++)
			_groups[i] = ((Integer)it.next()).intValue();
		
		fireEvent(ListDataEvent.GROUP_REORDERED, -1, -1);
	}

}
