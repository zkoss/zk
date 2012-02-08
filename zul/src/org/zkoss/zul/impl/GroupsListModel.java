/* GroupsListModelImpl.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 29 23:22:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.event.GroupsDataEvent;
import org.zkoss.zul.event.GroupsDataListener;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.GroupingInfo;
import org.zkoss.zul.ext.GroupsSortableModel;
import org.zkoss.zul.ext.Selectable;

/**
 * Encapulates {@link org.zkoss.zul.GroupsModel} as an instance of {@link org.zkoss.zul.ListModel}
 * such that it is easier to handle by {@link org.zkoss.zul.Listbox} and 
 * {@link org.zkoss.zul.Group}.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public class GroupsListModel<D, G, F> extends AbstractListModel<Object> {
	protected GroupsModel<D, G, F> _model;
	private transient int _size;
	/** An array of the group offset.
	 * The group offset is the offset from 0 that a group shall appear
	 * at the client.
	 * <p>For example, _gpofs[2] is the offset of group 2 (the third group)
	 * at the client.
	 */
	private transient int[] _gpofs;
	/** Whether a group has a foot. */
	private transient boolean[] _gpfts;
	/** Whether a group is closed. */
	private transient boolean[] _gpopens;
	private transient GroupsDataListener _listener;
	/** groupInfo list used in {@link Rows} */
	private transient List<int[]> _gpinfo;

	/** Returns the list model ({@link org.zkoss.zul.ListModel}) representing the given
	 * groups model.
	 * @since 6.0.0
	 */
	public static <D, G, F> GroupsListModel<D, G, F>
	toListModel(GroupsModel<D, G, F> model) {
		if (model instanceof GroupsSortableModel)
			return new GroupsListModelExt<D, G, F>(model);
		return new GroupsListModel<D, G, F>(model);
	}
	@SuppressWarnings("unchecked")
	protected GroupsListModel(GroupsModel<D, G, F> model) {
		_model = model;
		init();

		if (_model instanceof Selectable) {
			Selectable s = (Selectable) model;
			setMultiple(s.isMultiple());
			setSelection(s.getSelection());
		}
	}
	/*package*/ void init() {
		final int groupCount = _model.getGroupCount();
		_gpofs = new int[groupCount];
		_gpfts = new boolean[groupCount];
		_gpopens = new boolean[groupCount];
		_size = 0;
		for (int j = 0; j < groupCount; ++j) {
			_gpofs[j] = _size;
			_gpopens[j] = _model.isOpen(j);
			_size += 1 + (_gpopens[j] ? _model.getChildCount(j) : 0); //closed group deemed as zero child in ListModel
			_gpfts[j] = _model.hasGroupfoot(j);
			if (_gpfts[j]) ++_size;
		}
		if(_listener==null){
			_listener = new DataListener();
			_model.addGroupsDataListener(_listener);
		}
		
	}
	
	public List<int[]> getGroupsInfos() {
		_gpinfo = new LinkedList<int[]>();
		for(int j=0; j < _gpofs.length; ++j) {
			final int offset1 = _gpofs[j];
			final int offset2 = getNextOffset(j);
			_gpinfo.add(new int[] {offset1, offset2 - offset1, hasGroupfoot(j) ? offset2 - 1 : -1});
		}
		return _gpinfo;
	}
	
	/**
	 * Returns the offset from 0 that a group in this ListModel.
	 * <p>For example, _gpofs[2] is the offset of group 2 (the third group)
	 * in this ListModel.
	 * @param groupIndex the group index
	 * @return the offset from 0 that a group in this ListModel.
	 */
	public int getGroupOffset(int groupIndex) {
		return _gpofs[groupIndex];
	}
	
	public GroupsModel<D, G, F> getGroupsModel() {
		return _model;
	}
	/** Returns the number of groups in the data model.
	 */
	/*package*/ int getGroupCount() {
		return _gpofs.length;
	}
	/** Returns the number of items belong the specified group.
	 */
	/*package*/ int getChildCount(int groupIndex) {
		int v = getNextOffset(groupIndex) - _gpofs[groupIndex] - 1;
		return _gpfts[groupIndex] ? v - 1: v;
	}
	/**
	 * Returns whether the Group has a Groupfoot or not.
	 */
	/*package*/ boolean hasGroupfoot(int groupIndex) {
		return _gpfts[groupIndex];
	}

	/** Returns the offset of the next group.
	 */
	private int getNextOffset(int groupIndex) {
		return groupIndex >= (_gpofs.length - 1) ? _size: _gpofs[groupIndex + 1];
	}

	/**
	 * Returns the group info of given index
	 */
	public GroupingInfo getDataInfo(int index) {
		if (index < 0 || index >= _size)
			throw new IndexOutOfBoundsException("Not in 0.."+_size+": "+index);

		int gi = Arrays.binarySearch(_gpofs, index);
		if (gi >= 0)
			return new GroupDataInfo(GroupDataInfo.GROUP, gi, 0, _gpopens[gi]);

		gi = - gi - 2; //0 ~ _gpofs.length - 2
		int ofs = index - _gpofs[gi] - 1;
		if (_gpfts[gi]
		&& ofs >=  getNextOffset(gi) - _gpofs[gi] -2) //child count
			return new GroupDataInfo(GroupDataInfo.GROUPFOOT, gi, 0, _gpopens[gi]);

		return new GroupDataInfo(GroupDataInfo.ELEMENT, gi, ofs, _gpopens[gi]);
	}
	//Object//
	public boolean equals(Object o) {
		if (this == o)
			return true;
		return _model.equals(o instanceof GroupsListModel ? ((GroupsListModel)o)._model: o);
	}
	public int hashCode() {
		return _model.hashCode();
	}
	public String toString() {
		return Objects.toString(_model);
	}
	
	//For Backward Compatibility//
	/** @deprecated As of release 6.0.0, replaced with {@link #addToSelection}.
	 */
	public void addSelection(Object obj) {
		addToSelection(obj);
	}
	/** @deprecated As of release 6.0.0, replaced with {@link #removeFromSelection}.
	 */
	public void removeSelection(Object obj) {
		removeFromSelection(obj);
	}
	
	//ListModel
	//ListModel assume each item in the ListModel is visible; thus items inside closed
	//Group is deemed not in the ListModel
	public Object getElementAt(int index) {
		final GroupingInfo info = getDataInfo(index);
		if (info.getType() == GroupDataInfo.GROUP)
			return _model.getGroup(info.getGroupIndex());
		if (info.getType() == GroupDataInfo.GROUPFOOT)
			return _model.getGroupfoot(info.getGroupIndex());
		return _model.getChild(info.getGroupIndex(), info.getOffset());
	}
	//ListModel assume each item in the ListModel is visible; thus items inside closed
	//Group is not count into size
	public int getSize() {
		return _size;
	}

	//Serializable//
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		_model.removeGroupsDataListener(_listener); //avoid being serialized

		try {
			s.defaultWriteObject();
		} finally {
			_model.addGroupsDataListener(_listener);
		}
	}
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		GroupsListModel clone = (GroupsListModel) super.clone();
		clone._listener = null;
		return clone;
	}
	
	private class DataListener implements GroupsDataListener {
		public void onChange(GroupsDataEvent event) {
			int type = event.getType(),
				j0 = event.getIndex0(),
				j1 = event.getIndex1();

			switch (type) {
			case GroupsDataEvent.CONTENTS_CHANGED:
			case GroupsDataEvent.INTERVAL_ADDED:
			case GroupsDataEvent.INTERVAL_REMOVED:
				final int gi = event.getGroupIndex();
				if (gi < 0 || gi >= _gpofs.length)
					throw new IndexOutOfBoundsException("Group index not in 0.."+getGroupCount()+", "+gi);

				int ofs = _gpofs[gi] + 1;
				j0 = j0 >= 0 ? j0 + ofs: ofs;

				if (j1 >= 0) {
					j1 = j1 + ofs;
				} else {
					j1 = getNextOffset(gi) - 1;
					if (_gpfts[gi]) --j1; //exclude groupfoot
				}
				break;

			case GroupsDataEvent.GROUPS_CHANGED:
			case GroupsDataEvent.GROUPS_ADDED:
			case GroupsDataEvent.GROUPS_REMOVED:
				type -= GroupsDataEvent.GROUPS_CHANGED;
				if (j0 >= 0) {
					if (j0 >= _gpofs.length)
						throw new IndexOutOfBoundsException("Group index not in 0.."+getGroupCount()+", "+j0);
					j0 = _gpofs[j0];
				}
				if (j1 >= 0) {
					if (j1 >= _gpofs.length)
						throw new IndexOutOfBoundsException("Group index not in 0.."+getGroupCount()+", "+j1);
					j1 = getNextOffset(j1) - 1; //include groupfoot
				}
				break;
			case GroupsDataEvent.SELECTION_CHANGED:
				type = ListDataEvent.SELECTION_CHANGED;
				break;
			case GroupsDataEvent.MULTIPLE_CHANGED:
				type = ListDataEvent.MULTIPLE_CHANGED;
				break;
			}
			init();//re-initialize the model information
			fireEvent(type, j0, j1);
		}
	}
	/** The group infomation returned by {@link GroupsListModel#getDataInfo}.
	 */
	public static class GroupDataInfo implements GroupingInfo {
		/** Indicates the data is a group (aka., the head of the group). */
		public static final byte GROUP = 0;
		/** Indicates the data is a group foot. */
		public static final byte GROUPFOOT = 1;
		/** Indicates the data is an element of a group. */
		public static final byte ELEMENT = 2;

		/** The index of the group. */
		private int _groupIndex;
		/** The offset of an element in a group.
		 * It is meaningful only if {@link #type} is {@link #ELEMENT}.
		 */
		private int _offset;
		/** The type of the data.
		 * It is one of {@link #GROUP}, {@link #GROUPFOOT} and {@link #ELEMENT}.
		 */
		private byte _type;
		/** Whether the group is closed.
		 * It is meaningful only if {@link #type} is {@link #GROUP}.
		 */
		private boolean _open;

		private GroupDataInfo(byte type, int groupIndex, int offset, boolean open) {
			_type = type;
			_groupIndex = groupIndex;
			_offset = offset;
			_open = open;
		}

		@Override
		public int getType() {
			return _type;
		}

		@Override
		public int getGroupIndex() {
			return _groupIndex;
		}

		@Override
		public int getOffset() {
			return _offset;
		}

		@Override
		public boolean isOpen() {
			return _open;
		}
	}
}
/*package*/ class GroupsListModelExt<D, G, F> extends GroupsListModel<D, G, F>
implements GroupsSortableModel<D>, ComponentCloneListener, Cloneable {
	/*package*/ GroupsListModelExt(GroupsModel<D, G, F> model) {
		super(model);
	}
	/**
	 * Groups and sorts the data by the specified column and comparator.
	 * It only called when {@link org.zkoss.zul.Listbox} or {@link org.zkoss.zul.Grid} has the sort function.
	 * @param colIndex the index of the column
	 */
	@SuppressWarnings("unchecked")
	public void group(Comparator<D> cmpr, boolean ascending, int colIndex) {
		if (!(_model instanceof GroupsSortableModel))
			throw new UiException(GroupsSortableModel.class + " must be implemented in "+_model.getClass());
		((GroupsSortableModel)_model).group(cmpr, ascending, colIndex);
	}
	/** Sorts the data by the specified column and comparator.
	 */
	@SuppressWarnings("unchecked")
	public void sort(Comparator<D> cmpr, boolean ascending, int colIndex) {
		if (!(_model instanceof GroupsSortableModel))
			throw new UiException(GroupsSortableModel.class + " must be implemented in "+_model.getClass());
		((GroupsSortableModel)_model).sort(cmpr, ascending, colIndex);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object willClone(Component comp) {
		if (_model instanceof ComponentCloneListener) {
			GroupsListModelExt clone = (GroupsListModelExt) clone();
			GroupsModel m = (GroupsModel)((ComponentCloneListener) _model).willClone(comp);
			if (m != null)
				clone._model = m;
			clone.init(); // reset grouping info
			return clone;
		}
		return null; // no need to clone
	}
}
