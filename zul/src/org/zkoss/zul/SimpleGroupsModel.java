/* SimpleGroupsModel.java

	Purpose:
		
	Description:
		
	History:
		Sep 2, 2008 9:50:12 AM     2008, Created by Dennis.Chen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Arrays;
import java.util.Comparator;

import org.zkoss.zul.event.GroupsDataEvent;

/**
 * A simple implementation of {@link GroupsModel}.
 * Note: It assumes the content is immutable.
 * @author Dennis.Chen
 * @since 3.5.0
 * @see GroupsModel
 */
public class SimpleGroupsModel extends AbstractGroupsModel implements GroupsModelExt{
	
	/**
	 * member field to store group data
	 */
	protected Object[][] _data;
	
	/**
	 * member field to store group head data
	 */
	protected Object[] _heads;
	
	/**
	 * member field to store group foot data
	 */
	protected Object[] _foots;
	
	/**
	 * memeber field to store group close status
	 */
	protected boolean[] _closes;
	
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
	public SimpleGroupsModel(Object[][] data){
		this(data,null,(Object[])null);
	}
	
	/**
	 * Constructor
	 * When using this constructor , 
	 * {@link #getGroup(int)} will return the corresponding Object depends on heads. 
	 * {@link #hasGroupfoot(int)} will always return false
	 * @param data a 2 dimension array to represent groups data
	 * @param heads an array to represent head data of group
	 */
	public SimpleGroupsModel(Object[][] data,Object[] heads){
		this(data,heads,(Object[])null);
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
	public SimpleGroupsModel(Object[][] data,Object[] heads,Object[] foots){
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
	 * @param data a 2 dimension array to represent groups data
	 * @param heads an array to represent head data of group
	 * @param foots an array to represent foot data of group, if an element in this array is null, then 
	 * {@link #hasGroupfoot(int)} will return false in corresponding index.
	 * @param closes an array of boolean to represent close status of group. If not specified, then
	 * {@link #isClose(int)} will return false in corresponding index(i.e. group is default to open)  
	 */
	public SimpleGroupsModel(Object[][] data,Object[] heads,Object[] foots, boolean[] closes){
		if (data == null)
			throw new NullPointerException();
		_data = data;
		_heads = heads;
		_foots = foots;
		_closes = closes;
	}
	
	public Object getChild(int groupIndex, int index) {
		return _data[groupIndex][index];
	}


	public int getChildCount(int groupIndex) {
		return _data[groupIndex].length;
	}


	public Object getGroup(int groupIndex) {
		return  _heads==null?_data[groupIndex]:_heads[groupIndex];
	}


	public int getGroupCount() {
		return _data.length;
	}

	public Object getGroupfoot(int groupIndex) {
		return _foots == null ? null:_foots[groupIndex];
	}

	public boolean hasGroupfoot(int groupIndex) {
		return _foots == null ? false:_foots[groupIndex]!=null;
	}

	public boolean isClose(int groupIndex) {
		return _closes == null ? false : _closes[groupIndex];
	}

	public void setClose(int groupIndex, boolean close) {
		if (_closes == null) {
			_closes = new boolean[getGroupCount()];
		}
		if (_closes[groupIndex] != close) {
			_closes[groupIndex] = close;
			fireEvent(GroupsDataEvent.GROUPS_CHANGED,groupIndex,-1,-1);
		}
	}
	
	/**
	 * Do nothing in default implementation, however developer can override it to 
	 * re-group by manipulating {@link #_data},{@link #_heads},{@link #_foots}
	 */
	public void group(Comparator cmpr, boolean ascending, int colIndex) {
	}

	/**
	 * Sort each data in each group by Comparator, developer could override
	 * {@link #sortGroupData(Object, Object[], Comparator, boolean, int)}
	 * to customize.
	 */
	public void sort(Comparator cmpr, boolean ascending, int colIndex) {
		for(int i=0;i<_data.length;i++){
			sortGroupData(_heads==null?_data[i]:_heads[i],_data[i],cmpr,ascending,colIndex);
		}
		fireEvent(GroupsDataEvent.GROUPS_CHANGED,-1,-1,-1);
	}

	protected void sortGroupData(Object group,Object[] groupdata,Comparator cmpr,boolean ascending, int colIndex){
		Arrays.sort(groupdata,cmpr);
	}

}
