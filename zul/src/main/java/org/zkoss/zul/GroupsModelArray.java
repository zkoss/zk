/*GroupsModelArray.java

	Purpose:
		
	Description:
		
	History:
		Sep 3, 2008 9:50:12 AM     2008, Created by Dennis.Chen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.util.ArraysX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentCloneListener;
import org.zkoss.zul.event.GroupsDataEvent;
import org.zkoss.zul.ext.GroupsSortableModel;

/**
 * An array implementation of {@link GroupsModel}.
 * This implementation takes a list of elements that are not grouped yet,
 * and a comparator that will be used to group them.
 * The c supports regroup array to groups depends on {@link Comparator} and {@link GroupComparator}.
 * For immutable content (no re-grouping allowed), please use {@link SimpleGroupsModel} instead.
 *
 * <p>Generics:
 * <dl>
 * <dt>D</dt><dd>The class of each data</dd>
 * <dt>H</dt><dd>The class of each group header</dd>
 * <dt>F</dt><dd>The class of each group footer</dd>
 * <dt>E</dt><dd>The class of each selection. It is the common base class
 * of D, H, F. In other words, D, H and F must extend from E.</dd>
 * </dl>
 * <p>For more information, please refer to
 * <a href="http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/MVC/Model/Groups_Model">ZK Developer's Reference: Groups Model</a>
 * <p>By default, the model support cloneable when the component is cloned. (since 6.0.0)
 * @author Dennis.Chen
 * @since 5.0.5
 * @see GroupsModel
 * @see SimpleGroupsModel
 * @see GroupComparator
 * @see ComponentCloneListener
 */
public class GroupsModelArray<D, H, F, E> extends AbstractGroupsModel<D, H, F, E>
		implements GroupsSortableModel<D>, ComponentCloneListener, Cloneable {

	/**
	 * member field to store native (original) array data
	 */
	protected D[] _nativedata;

	/**
	 * member field to store Comparator for initial grouping.
	 */
	protected Comparator<D> _comparator;

	/**
	 * member field to store group data
	 */
	protected D[][] _data;

	/**
	 * member field to store group head data (generated in {@link #organizeGroup})
	 */
	protected Object[] _heads;

	/**
	 * member field to store group foot data (generated in {@link #organizeGroup})
	 */
	protected Object[] _foots;

	/**
	 * member field to store group close status
	 */
	protected boolean[] _opens;

	/**
	 * the sorting comparator
	 */
	protected Comparator<D> _sorting;

	/**
	 * is the sort ascending?
	 */
	protected boolean _sortDir;

	/**
	 * Constructor with an array of data.
	 * @param data an array data to be grouping.
	 * @param cmpr a comparator implementation help group the data. you could implements {@link GroupComparator} to do more grouping control.<br/>
	 * At 1st phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to sort the data.<br/>
	 * At 2nd phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to decide which data belong to which group. 
	 * In this phase it also invoke {@link #createGroupHead(Object[], int, int)} and {@link #createGroupFoot(Object[], int, int)} to create head of foot Object of each group.<br/>
	 * At 3rd phase, it calls {@link Comparator#compare(Object, Object)} to sort data in each group.<br/>
	 */
	public GroupsModelArray(D[] data, Comparator<D> cmpr) {
		this(data, cmpr, 0);
	}

	/**
	 * Constructor with an array of data.
	 * It is the same as GroupsModelArray(data, cmpr, col, true), i.e.,
	 * <code>data</code> will be cloned first, so <code>data</code>'s content
	 * won't be changed.
	 * @param data an array data to be grouping.
	 * @param cmpr a comparator implementation help group the data. you could implements {@link GroupComparator} to do more grouping control.<br/>
	 * At 1st phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to sort the data.<br/>
	 * At 2nd phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to decide which data belong to which group. 
	 * In this phase it also invoke {@link #createGroupHead(Object[], int, int)} and {@link #createGroupFoot(Object[], int, int)} to create head of foot Object of each group.<br/>
	 * At 3rd phase, it calls {@link Comparator#compare(Object, Object)} to sort data in each group.<br/>
	 * @param col column index associate with cmpr.
	 */
	public GroupsModelArray(D[] data, Comparator<D> cmpr, int col) {
		this(data, cmpr, col, true); //clone
	}

	/**
	 * Constructor with an array of data.
	 * @param data an array data to be grouping.
	 * @param cmpr a comparator implementation help group the data. you could implements {@link GroupComparator} to do more grouping control.<br/>
	 * At 1st phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to sort the data.<br/>
	 * At 2nd phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to decide which data belong to which group. 
	 * In this phase it also invoke {@link #createGroupHead(Object[], int, int)} and {@link #createGroupFoot(Object[], int, int)} to create head of foot Object of each group.<br/>
	 * At 3rd phase, it calls {@link Comparator#compare(Object, Object)} to sort data in each group.<br/>
	 * @param col column index associate with cmpr.
	 * @param clone whether to clone <code>data</code>. If not cloning,
	 * data's content will be changed.
	 * @since 5.0.6
	 */
	public GroupsModelArray(D[] data, Comparator<D> cmpr, int col, boolean clone) {
		if (data == null || cmpr == null)
			throw new IllegalArgumentException("null parameter");
		_nativedata = clone ? (D[]) ArraysX.duplicate(data) : data;
		_comparator = cmpr;
		group(_comparator, true, col);
	}

	public D getChild(int groupIndex, int index) {
		return _data[groupIndex][index];
	}

	public int getChildCount(int groupIndex) {
		return _data[groupIndex].length;
	}

	@SuppressWarnings("unchecked")
	public H getGroup(int groupIndex) {
		return (H) _heads[groupIndex];
	}

	public int getGroupCount() {
		return _data.length;
	}

	@SuppressWarnings("unchecked")
	public F getGroupfoot(int groupIndex) {
		return (F) _foots[groupIndex];
	}

	public boolean hasGroupfoot(int groupIndex) {
		return _foots == null ? false : _foots[groupIndex] != null;
	}

	public void sort(Comparator<D> cmpr, boolean ascending, int col) {
		_sorting = cmpr;
		_sortDir = ascending;
		sortAllGroupData(cmpr, ascending, col);

		fireEvent(GroupsDataEvent.STRUCTURE_CHANGED, -1, -1, -1);
	}

	public void group(final Comparator<D> cmpr, boolean ascending, int col) {
		Comparator<D> cmprx;
		if (cmpr instanceof GroupComparator) {
			cmprx = new Comparator<D>() {
				public int compare(D o1, D o2) {
					return ((GroupComparator<D>) cmpr).compareGroup(o1, o2);
				}
			};
		} else {
			cmprx = cmpr;
		}

		sortDataInGroupOrder(cmprx, ascending, col); //use comparator from constructor to sort native data
		organizeGroup(cmprx, col);
		if (cmprx != cmpr)
			sortAllGroupData(cmpr, ascending, col); //sort by original comparator

		fireEvent(GroupsDataEvent.GROUPS_RESET, -1, -1, -1);
	}

	public boolean addOpenGroup(int groupIndex) {
		return setOpenGroup0(groupIndex, true);
	}

	public boolean removeOpenGroup(int groupIndex) {
		return setOpenGroup0(groupIndex, false);
	}

	private boolean setOpenGroup0(int groupIndex, boolean open) {
		if (_opens == null) {
			if (open)
				return true; // _opens == null means all open
			int length = getGroupCount();
			_opens = new boolean[length];
			for (int i = 0; i < length; i++)
				_opens[i] = true;
		}
		if (_opens[groupIndex] != open) {
			_opens[groupIndex] = open;
			fireEvent(GroupsDataEvent.GROUPS_OPENED, groupIndex, groupIndex, groupIndex);
			return true;
		}
		return false;
	}

	public boolean isGroupOpened(int groupIndex) {
		return _opens == null || _opens[groupIndex];
	}

	/**
	 * Sorts data in each group, the group order will not change. invoke this method doesn't fire event.
	 */
	private void sortAllGroupData(Comparator<D> cmpr, boolean ascending, int col) {
		for (int i = 0; i < _data.length; i++) {
			sortGroupData(getGroup(i), _data[i], cmpr, ascending, col);
		}
	}

	/**
	 * Sorts data within a group. Notice that this method doesn't fire event.
	 * <p>There are three steps to re-group data:
	 * {@link #sortDataInGroupOrder}, {@link #organizeGroup} and then
	 * {@link #sortGroupData}.
	 *
	 * <p>It is the last step of grouping. It sorts data in the specified
	 * group.
	 */
	protected void sortGroupData(H group, D[] groupdata, Comparator<D> cmpr, boolean ascending, int col) {
		Arrays.sort(groupdata, cmpr);
	}

	/**
	 * Organizes groups based sorted data.
	 *
	 * <p>There are three steps to re-group data:
	 * {@link #sortDataInGroupOrder}, {@link #organizeGroup} and then
	 * {@link #sortGroupData}.
	 *
	 * <p>It is the second step of grouping. It creates group data
	 * based on the data sorted in the group order by
	 * {@link #sortDataInGroupOrder}.
	 *
	 * @param cmpr the comparator used to compare data in the group order.
	 * Notice that the comparator is never an instance of {@link GroupComparator}.
	 * The implementation just uses {@link Comparator#compare} to sort
	 * the data.
	 * @param col column index
	 */
	@SuppressWarnings("unchecked")
	protected void organizeGroup(Comparator<D> cmpr, int col) {
		List<List<D>> group = new LinkedList<List<D>>();
		List<D> gdata = null;
		D last = null;
		D curr = null;

		//regroup native
		for (int i = 0; i < _nativedata.length; i++) {
			curr = _nativedata[i];
			if (last == null || cmpr.compare(last, curr) != 0) {
				gdata = new LinkedList<D>();
				group.add(gdata);
			}
			gdata.add(curr);
			last = _nativedata[i];
		}

		//prepare data,head & foot
		List<D>[] gd = new List[group.size()];
		group.toArray(gd);

		Class<?> classD = _nativedata.getClass().getComponentType();
		_data = (D[][]) Array.newInstance(classD, gd.length, 0); //new D[gd.length][];
		_foots = new Object[gd.length];
		_heads = new Object[gd.length];
		_opens = new boolean[_data.length];

		for (int i = 0; i < gd.length; i++) {
			gdata = gd[i];
			_data[i] = (D[]) Array.newInstance(classD, gdata.size());
			gdata.toArray(_data[i]);
			_heads[i] = createGroupHead(_data[i], i, col);
			_foots[i] = createGroupFoot(_data[i], i, col);
			_opens[i] = createGroupOpen(_data[i], i, col);
		}
	}

	/**
	 * create group head Object, default implementation return first element of groupdata.
	 * you can override this method to return your Object.
	 * @param groupdata data the already in a group.
	 * @param index group index
	 * @param col column to group
	 */
	@SuppressWarnings("unchecked")
	protected H createGroupHead(D[] groupdata, int index, int col) {
		final D o = groupdata[0];
		return o != null && o.getClass().isArray() && col < Array.getLength(o) ? (H) Array.get(o, col) : (H) o;
	}

	/**
	 * create group foot Object, default implementation return null, which means no foot .
	 * you can override this method to return your Object.
	 * @param groupdata data the already in a group.
	 * @param index group index
	 * @param col column to group
	 */
	protected F createGroupFoot(D[] groupdata, int index, int col) {
		return null;
	}

	/**
	 * Sorts the native data in the group order.
	 * After sorted, all data in the first group shall be placed in front
	 * of the second group, and so on.
	 *
	 * <p>There are three steps to re-group data:
	 * {@link #sortDataInGroupOrder}, {@link #organizeGroup} and then
	 * {@link #sortGroupData}.
	 *
	 * @param cmpr the comparator used to compare data in the group order.
	 * Notice that the comparator is never an instance of {@link GroupComparator}.
	 * The implementation just uses {@link Comparator#compare} to sort
	 * the data.
	 */
	protected void sortDataInGroupOrder(Comparator<D> cmpr, boolean ascending, int colIndex) {
		Arrays.sort(_nativedata, cmpr);
	}

	/**
	 * create group open status, default implementation return true, which means open the group.
	 * you can override this method to return your group open status.
	 * @param groupdata data the already in a group.
	 * @param index group index
	 * @param col column to group
	 * @since 6.0.0
	 */
	protected boolean createGroupOpen(D[] groupdata, int index, int col) {
		return true;
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		GroupsModelArray clone = (GroupsModelArray) super.clone();
		if (_nativedata != null)
			clone._nativedata = ArraysX.duplicate(_nativedata);
		if (_data != null)
			clone._data = ArraysX.duplicate(_data);
		if (_heads != null)
			clone._heads = ArraysX.duplicate(_heads);
		if (_foots != null)
			clone._foots = ArraysX.duplicate(_foots);
		if (_opens != null)
			clone._opens = (boolean[]) ArraysX.duplicate(_opens);
		if (_sorting != null)
			clone._sorting = _sorting;
		if (_sortDir)
			clone._sortDir = true;
		return clone;
	}

	/**
	 * Allows the model to clone
	 * @since 6.0.0
	 */
	public Object willClone(Component comp) {
		return clone();
	}

	//Object//
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof GroupsModelArray) {
			return Arrays.equals(_nativedata, ((GroupsModelArray) o)._nativedata);
		}
		return false;
	}

	public int hashCode() {
		return Arrays.hashCode(_nativedata);
	}

	public String toString() {
		return Objects.toString(_nativedata);
	}

	@Override
	public String getSortDirection(Comparator<D> cmpr) {
		if (Objects.equals(_sorting, cmpr))
			return _sortDir ? "ascending" : "descending";
		return "natural";
	}
}
