/* GroupsListModelImpl.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 29 23:22:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import java.util.Comparator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.GroupsModelExt;
import org.zkoss.zul.event.GroupsDataEvent;
import org.zkoss.zul.event.GroupsDataListener;

/**
 * Encapulates {@link GroupsModel} as an instance of {@link ListModel}
 * such that it is easier to handle by {@link org.zkoss.zul.Listbox} and 
 * {@link org.zkoss.zul.Group}.
 *
 * @author tomyeh
 * @since 3.5.0
 */
public class GroupsListModel extends AbstractListModel {
	private final GroupsModel _model;
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
	private transient boolean[] _gpcloses;
	private transient GroupsDataListener _listener;
	/** groupInfo list used in {@link Rows} */
	private transient List _gpinfo;

	public GroupsListModel(GroupsModel model) {
		_model = model;
		init();
	}
	private void init() {
		final int groupCount = _model.getGroupCount();
		_gpofs = new int[groupCount];
		_gpfts = new boolean[groupCount];
		_gpcloses = new boolean[groupCount];
		_size = 0;
		for (int j = 0; j < groupCount; ++j) {
			_gpofs[j] = _size;
			_gpcloses[j] = _model.isClose(j);
			_size += 1 + (_gpcloses[j] ? 0 : _model.getChildCount(j)); //closed group deemed as zero child in ListModel
			_gpfts[j] = _model.hasGroupfoot(j);
			if (_gpfts[j]) ++_size;
		}
		if(_listener==null){
			_listener = new DataListener();
			_model.addGroupsDataListener(_listener);
		}
		
	}
	
	public List getGroupsInfo() {
		_gpinfo = new LinkedList();
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
	
	public GroupsModel getGroupsModel() {
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
	/**
	 * Groups and sorts the data by the specified column and comparator.
	 * It only called when {@link org.zkoss.zul.Listbox} or {@link org.zkoss.zul.Grid} has the sort function.
	 * @param colIndex the index of the column
	 */
	public void group(Comparator cmpr, boolean ascending, int colIndex) {
		if (!(_model instanceof GroupsModelExt))
			throw new UiException("GroupsModelExt must be implemented in "+_model.getClass());
		((GroupsModelExt)_model).group(cmpr, ascending, colIndex);
	}
	/** Sorts the data by the specified column and comparator.
	 */
	public void sort(Comparator cmpr, boolean ascending, int colIndex) {
		if (!(_model instanceof GroupsModelExt))
			throw new UiException("GroupsModelExt must be implemented in "+_model.getClass());
		((GroupsModelExt)_model).sort(cmpr, ascending, colIndex);
	}

	/** Returns the offset of the next group.
	 */
	private int getNextOffset(int groupIndex) {
		return groupIndex >= (_gpofs.length - 1) ? _size: _gpofs[groupIndex + 1];
	}

	/**
	 * Returns the group info of given index
	 */
	public GroupDataInfo getDataInfo(int index) {
		if (index < 0 || index >= _size)
			throw new IndexOutOfBoundsException("Not in 0.."+_size+": "+index);

		int gi = Arrays.binarySearch(_gpofs, index);
		if (gi >= 0)
			return new GroupDataInfo(GroupDataInfo.GROUP, gi, 0, _gpcloses[gi]);

		gi = - gi - 2; //0 ~ _gpofs.length - 2
		int ofs = index - _gpofs[gi] - 1;
		if (_gpfts[gi]
		&& ofs >=  getNextOffset(gi) - _gpofs[gi] -2) //child count
			return new GroupDataInfo(GroupDataInfo.GROUPFOOT, gi, 0, _gpcloses[gi]);

		return new GroupDataInfo(GroupDataInfo.ELEMENT, gi, ofs, _gpcloses[gi]);
	}
	//ListModel
	//ListModel assume each item in the ListModel is visible; thus items inside closed
	//Group is deemed not in the ListModel
	public Object getElementAt(int index) {
		final GroupDataInfo info = getDataInfo(index);
		if (info.type == GroupDataInfo.GROUP)
			return _model.getGroup(info.groupIndex);
		if (info.type == GroupDataInfo.GROUPFOOT)
			return _model.getGroupfoot(info.groupIndex);
		return _model.getChild(info.groupIndex, info.offset);
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
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();
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
			}
			init();//re-initialize the model information
			fireEvent(type, j0, j1);
		}
	}
	/** The group infomation returned by {@link GroupsListModel#getDataInfo}.
	 */
	public static class GroupDataInfo {
		/** Indicates the data is a group (aka., the head of the group). */
		public static final byte GROUP = 0;
		/** Indicates the data is a group foot. */
		public static final byte GROUPFOOT = 1;
		/** Indicates the data is an element of a group. */
		public static final byte ELEMENT = 2;

		/** The index of the group. */
		private int groupIndex;
		/** The offset of an element in a group.
		 * It is meaningful only if {@link #type} is {@link #ELEMENT}.
		 */
		private int offset;
		/** The type of the data.
		 * It is one of {@link #GROUP}, {@link #GROUPFOOT} and {@link #ELEMENT}.
		 */
		public byte type;
		/** Whether the group is closed.
		 * It is meaningful only if {@link #type} is {@link #GROUP}.
		 */
		public boolean close;

		private GroupDataInfo(byte type, int groupIndex, int offset, boolean close) {
			this.type = type;
			this.groupIndex = groupIndex;
			this.offset = offset;
			this.close = close;
		}
	}
}
