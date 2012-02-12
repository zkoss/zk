/* SimpleGroupsModel.java

	Purpose:
		
	Description:
		
	History:
		Sep 2, 2008 9:50:12 AM     2008, Created by Dennis.Chen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Arrays;
import java.util.Comparator;

import org.zkoss.util.ArraysX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ComponentCloneListener;

import org.zkoss.zul.event.GroupsDataEvent;
import org.zkoss.zul.ext.GroupsSortableModel;

/**
 * A simple implementation of {@link GroupsModel}.
 * This implementation assumes the data is grouped, and the grouping structure
 * is immutable.
 * If you allow the user to re-group the content, use {@link GroupsModelArray}
 * instead.
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
 * @since 3.5.0
 * @see GroupsModel
 * @see GroupsModelArray
 * @see ComponentCloneListener
 */
public class SimpleGroupsModel<D, H, F, E> extends AbstractGroupsModel<D, Object, F, E>
implements GroupsSortableModel<D>, ComponentCloneListener, Cloneable {
	
	/**
	 * member field to store group data
	 */
	protected D[][] _data;
	
	/**
	 * member field to store group head data
	 */
	protected H[] _heads;
	
	/**
	 * member field to store group foot data
	 */
	protected F[] _foots;
	
	/**
	 * memeber field to store group close status
	 */
	protected boolean[] _opens;
	
	/**
	 * Constructs a groups data model with a two-dimensional array of data.
	 * For example, if you have three groups and each of them have 5 elements,
	 * then the data argument must be a 3 x 5 array.
	 * Furthermore, data[0] is the array of elements of the first group,
	 * data[1] is elements of the second group, and so on.
	 * Of course, each group might have different number of elements.
	 *
	 * <p>This constructor assumes there is no group foot at all.
	 *
	 * @param data a two-dimensional array to represent groups data,
	 * where data[0] is the array of element of the first group,
	 * data[1] is of the second group and so on.
	 */
	public SimpleGroupsModel(D[][] data){
		this(data, null, (F[])null);
	}
	
	/**
	 * Constructor
	 * When using this constructor , 
	 * {@link #getGroup(int)} will return the corresponding Object depends on heads. 
	 * {@link #hasGroupfoot(int)} will always return false
	 * @param data a 2 dimension array to represent groups data
	 * @param heads an array to represent head data of group
	 */
	public SimpleGroupsModel(D[][] data, H[] heads){
		this(data, heads, (F[])null);
	}
	
	/**
	 * Constructor
	 * When using this constructor , 
	 * {@link #getGroup(int)} will return the corresponding Object depends on heads.  
	 * The return value of {@link #hasGroupfoot(int)} and {@link #getGroupfoot(int)} 
	 * are depends on foots. 
	 *   
	 * @param data a 2 dimension array to represent groups data
	 * @param heads an array to represent head data of group
	 * @param foots an array to represent foot data of group, if an element in this array is null, then 
	 * {@link #hasGroupfoot(int)} will return false in corresponding index.
	 */
	public SimpleGroupsModel(D[][] data, H[] heads, F[] foots){
		if (data == null)
			throw new NullPointerException();
		_data = data;
		_heads = heads;
		_foots = foots;
	}

	/**
	 * Constructor
	 * When using this constructor , 
	 * {@link #getGroup(int)} will return the corresponding Object depends on heads.  
	 * The return value of {@link #hasGroupfoot(int)} and {@link #getGroupfoot(int)} 
	 * are depends on foots. 
	 *
	 * <p>Notice that, for backward compatiblity, the last argument is <code>closes</code>.
	 * 
	 * @param data a 2 dimension array to represent groups data
	 * @param heads an array to represent head data of group
	 * @param foots an array to represent foot data of group, if an element in this array is null, then 
	 * {@link #hasGroupfoot(int)} will return false in corresponding index.
	 * @param closes an array of boolean to represent close status of group. If not specified, then
	 * {@link #isClose(int)} will return false in corresponding index(i.e. group is default to open)  
	 */
	public SimpleGroupsModel(D[][] data, H[] heads, F[] foots, boolean[] closes){
		if (data == null)
			throw new NullPointerException();
		_data = data;
		_heads = heads;
		_foots = foots;
		_opens = closes;
		if (_opens != null) {
			for (int i = 0; i < _opens.length; i++)
				_opens[i] = !_opens[i];
		}
	}
	
	public D getChild(int groupIndex, int index) {
		return _data[groupIndex][index];
	}


	public int getChildCount(int groupIndex) {
		return _data[groupIndex].length;
	}

	/** Returns the data representing the group.
	 * It is H, if heads is specified in the constructor, or D[] if
	 * not specified.
	 */
	public Object getGroup(int groupIndex) {
		return  _heads==null?_data[groupIndex]:_heads[groupIndex];
	}


	public int getGroupCount() {
		return _data.length;
	}

	public F getGroupfoot(int groupIndex) {
		return _foots == null ? null:_foots[groupIndex];
	}

	public boolean hasGroupfoot(int groupIndex) {
		return _foots == null ? false:_foots[groupIndex]!=null;
	}

	/**
	 * @deprecated As of release 6.0.0, replace with {@link #isGroupOpened(int)}
	 */
	public boolean isClose(int groupIndex) {
		return !isGroupOpened(groupIndex);
	}

	/**
	 * @deprecated As of release 6.0.0, replace with {@link #setOpenGroup(int, boolean)}
	 */
	public void setClose(int groupIndex, boolean close) {
		setOpenGroup(groupIndex, !close);
	}

	@Override
	public boolean isGroupOpened(int groupIndex) {
		return _opens == null ? true : _opens[groupIndex];
	}

	@Override
	public void setOpenGroup(int groupIndex, boolean open) {
		if (_opens == null) {
			_opens = new boolean[getGroupCount()];
		}
		if (_opens[groupIndex] != open) {
			_opens[groupIndex] = open;
			fireEvent(GroupsDataEvent.GROUPS_CHANGED,groupIndex, -1, -1);
		}
	}
	
	/**
	 * Do nothing in default implementation, however developer can override it to 
	 * re-group by manipulating {@link #_data},{@link #_heads},{@link #_foots}
	 */
	public void group(Comparator<D> cmpr, boolean ascending, int colIndex) {
	}

	/**
	 * Sort each data in each group by Comparator, developer could override
	 * {@link #sortGroupData(Object, Object[], Comparator, boolean, int)}
	 * to customize.
	 */
	public void sort(Comparator<D> cmpr, boolean ascending, int colIndex) {
		for(int i=0;i<_data.length;i++){
			sortGroupData(_heads==null?_data[i]:_heads[i],_data[i],cmpr,ascending,colIndex);
		}
		fireEvent(GroupsDataEvent.STRUCTURE_CHANGED,-1,-1,-1);
	}

	/** Sorts a group of data.
	 * <p>Default: <code>Arrays.sort(groupdata, cmpr)</code>
	 * @param group the group (the same as {@link #getGroup})
	 * @param groupdata the group of data to sort
	 */
	protected void sortGroupData(Object group, D[] groupdata,
	Comparator<D> cmpr, boolean ascending, int colIndex){
		Arrays.sort(groupdata,cmpr);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object clone() {
		SimpleGroupsModel clone = (SimpleGroupsModel)super.clone();
		if (_data != null)
			clone._data = ArraysX.duplicate(_data);
		if (_heads != null)
			clone._heads = ArraysX.duplicate(_heads);
		if (_foots != null)
			clone._foots = ArraysX.duplicate(_foots);
		if (_opens != null)
			clone._opens = (boolean[])ArraysX.duplicate(_opens);
		return clone;
	}
	/**
	 * Allows the model to clone
	 * @since 6.0.0
	 */
	@Override
	public Object willClone(Component comp) {
		return clone();
	}
}
