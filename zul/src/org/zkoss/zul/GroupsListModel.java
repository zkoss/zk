/* GroupsListModel.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Aug 29 23:22:57     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Comparator;
import java.util.Arrays;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zul.event.GroupsDataEvent;
import org.zkoss.zul.event.GroupsDataListener;

/**
 * Encapulates {@link GroupsModel} as an instance of {@link ListModel}
 * such that it is easier to handle by {@link Listbox} and {@link Group}.
 *
 * @author tomyeh
 * @since 3.5.0
 */
/*package*/ class GroupsListModel extends AbstractListModel {
	private final GroupsModel _model;
	private transient int _size, _groupCount;
	/** An array of the group offset.
	 * The group offset is the offset from 0 that a group shall appear
	 * at the client.
	 * <p>For example, _gpofs[2] is the offset of group 2 (the third group)
	 * at the client.
	 */
	private transient int[] _gpofs;
	/** Whether a group has a foot. */
	private transient boolean[] _gpfts;
	private transient GroupsDataListener _listener;

	/*package*/ GroupsListModel(GroupsModel model) {
		_model = model;
		init();
	}
	private void init() {
		_groupCount = _model.getGroupCount();
		_gpofs = new int[_groupCount];
		_gpfts = new boolean[_groupCount];
		_size = 0;
		for (int j = 0; j < _groupCount; ++j) {
			_gpofs[j] = _size;
			_size += 1 + _model.getChildCount(j);
			_gpfts[j] = _model.hasGroupfoot(j);
			if (_gpfts[j]) ++_size;
		}
		//_size += _groupCount;, already add 1 in above loop for group head

		_model.addGroupsDataListener(_listener = new DataListener());
	}

	/*package*/ GroupsModel getGroupsModel() {
		return _model;
	}
	/** Returns the number of groups in the data model.
	 */
	/*package*/ int getGroupCount() {
		return _model.getGroupCount();
	}
	/** Returns the number of items belong the specified group.
	 */
	/*package*/ int getChildCount(int groupIndex) {
		return _model.getChildCount(groupIndex);
	}
	/**
	 * Returns whether the Group has a Groupfoot or not.
	 */
	/*package*/ boolean hasGroupfoot(int groupIndex) {
		return _model.hasGroupfoot(groupIndex);
	}
	/**
	 * Groups and sorts the data by the specified column and comparator.
	 * It only called when {@link Listbox} or {@link Grid} has the sort function.
	 * @param colIndex the index of the column
	 */
	/*package*/ void group(Comparator cmpr, boolean ascending, int colIndex) {
		if (!(_model instanceof GroupsModelExt))
			throw new UiException("GroupsModelExt must be implemented in "+_model.getClass());
		((GroupsModelExt)_model).group(cmpr, ascending, colIndex);
	}
	/** Sorts the data by the specified column and comparator.
	 */
	/*package*/ void sort(Comparator cmpr, boolean ascending, int colIndex) {
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
	 * return the group info of given index
	 * [0] group index 
	 * [1] type of index: 0-grouphead, 1-row, 2- groupfoot
	 * [2] offset : the offset of index in group, only available when type is row 
	 * [3] hasfoot : 1 if this group has foot.
	 */
	/*package*/ int[] getGroupInfo(int index) {
		if (index < 0 || index >= _size)
			throw new IndexOutOfBoundsException("Not in 0.."+_size+": "+index);

		int gi = Arrays.binarySearch(_gpofs, index);
		if (gi >= 0) {
			return new int[]{gi, 0 , -1, (_gpfts[gi])?1:0};
		} else {
			gi = - gi - 2; //0 ~ _gpofs.length - 2
			int ofs = index - _gpofs[gi] - 1;
			if (_gpfts[gi]) {
				if (ofs >=  getNextOffset(gi) - _gpofs[gi] -2) //child count
					return new int[]{gi, 2, -1,(_gpfts[gi])?1:0};
			}
			return new int[]{gi, 1, ofs,(_gpfts[gi])?1:0};
		}
	}
	//ListModel//
	public Object getElementAt(int index) {
		if (index < 0 || index >= _size)
			throw new IndexOutOfBoundsException("Not in 0.."+_size+": "+index);

		int gi = Arrays.binarySearch(_gpofs, index);
		if (gi >= 0) {
			return _model.getGroup(gi);
		} else {
			gi = - gi - 2; //0 ~ _gpofs.length - 2
			int ofs = index - _gpofs[gi] - 1;
			if (_gpfts[gi]) {
				if (ofs >=  getNextOffset(gi) - _gpofs[gi] -2) //child count
					return _model.getGroupfoot(gi);
			}
			return _model.getChild(gi, ofs);
		}
	}
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
					throw new IndexOutOfBoundsException("Group index not in 0.."+_groupCount+", "+gi);

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
						throw new IndexOutOfBoundsException("Group index not in 0.."+_groupCount+", "+j0);
					j0 = _gpofs[j0];
				}
				if (j1 >= 0) {
					if (j1 >= _gpofs.length)
						throw new IndexOutOfBoundsException("Group index not in 0.."+_groupCount+", "+j1);
					j1 = getNextOffset(j1) - 1; //include groupfoot
				}
			}
			fireEvent(type, j0, j1);
		}
	}
}
