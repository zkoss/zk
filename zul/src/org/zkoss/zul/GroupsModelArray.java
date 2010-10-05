/*GroupsModelArray.java

	Purpose:
		
	Description:
		
	History:
		Sep 3, 2008 9:50:12 AM     2008, Created by Dennis.Chen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.zkoss.util.ArraysX;
import org.zkoss.zul.event.GroupsDataEvent;

/**
 * An array implementation of {@link GroupsModel}.
 * This implementation supports regroup array to groups depends on {@link Comparator} and {@link GroupComparator}.
 * For immutable content (no re-grouping allowed), please use {@link SimpleGroupsModel} instead.
 * 
 * @author Dennis.Chen
 * @since 5.0.5
 * @see GroupsModel
 * @see SimpleGroupsModel
 */
public class GroupsModelArray extends AbstractGroupsModel implements GroupsModelExt{
	
	/**
	 * member field to store native array data
	 */
	protected Object[] _nativedata;
	
	/**
	 * member field to store Comparator for initial grouping.
	 */
	protected Comparator _comparator;
	
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
	 * member field to store group close status
	 */
	protected boolean[] _closes;
	
	/**
	 * Constructor
	 * @param data an array data to be grouping.
	 * @param cmpr a comparator implementation help group the data. you could implements {@link GroupComparator} to do more grouping control.<br/>
	 * At 1st phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to sort the data.<br/>
	 * At 2nd phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to decide which data belong to which group. 
	 * In this phase it also invoke {@link #createGroupHead(Object[], int, int)} and {@link #createGroupFoot(Object[], int, int)} to create head of foot Object of each group.<br/>
	 * At 3rd phase, it calls {@link Comparator#compare(Object, Object)} to sort data in each group.<br/>
	 */
	public GroupsModelArray(Object[] data,Comparator cmpr){
		this(data,cmpr,0);
	}
	/**
	 * 
	 * @param data an array data to be grouping.
	 * @param cmpr a comparator implementation help group the data. you could implements {@link GroupComparator} to do more grouping control.<br/>
	 * At 1st phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to sort the data.<br/>
	 * At 2nd phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to decide which data belong to which group. 
	 * In this phase it also invoke {@link #createGroupHead(Object[], int, int)} and {@link #createGroupFoot(Object[], int, int)} to create head of foot Object of each group.<br/>
	 * At 3rd phase, it calls {@link Comparator#compare(Object, Object)} to sort data in each group.<br/>
	 * @param col column index associate with cmpr.
	 */
	public GroupsModelArray(Object[] data,Comparator cmpr, int col){
		if (data == null || cmpr == null)
			throw new IllegalArgumentException("null parameter");
		_nativedata = (Object[])ArraysX.duplicate(data);
		_comparator = cmpr;
		group(_comparator,true,col);
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

	public void sort(Comparator cmpr, boolean ascending, int col) {
		sortAllGroupData(cmpr,ascending,col);

		fireEvent(GroupsDataEvent.GROUPS_CHANGED,-1,-1,-1);
	}

	public void group(final Comparator cmpr, boolean ascending, int col) {
		Comparator cmprx;
		if(cmpr instanceof GroupComparator){
			cmprx = new Comparator(){
				public int compare(Object o1, Object o2) {
					return ((GroupComparator)cmpr).compareGroup(o1, o2);
				}
			};
		}else{
			cmprx = cmpr;
		}
		
		sortDataInGroupOrder(cmprx, ascending,col);//use comparator from constructor to sort native data
		organizeGroup(cmprx, col);
		if (cmprx != cmpr)
			sortAllGroupData(cmpr, ascending,col);//sort by original comparator

		fireEvent(GroupsDataEvent.GROUPS_RESET,-1,-1,-1);
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
	 * Sorts data in each group, the group order will not change. invoke this method doesn't fire event.
	 */
	private void sortAllGroupData(Comparator cmpr,boolean ascending, int col) {
		for(int i=0;i<_data.length;i++){
			sortGroupData(_heads[i],_data[i],cmpr,ascending,col);
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
	protected void sortGroupData(Object group,Object[] groupdata,Comparator cmpr,boolean ascending, int col){
		Arrays.sort(groupdata,cmpr);
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
	protected void organizeGroup(Comparator cmpr, int col) {
		List group = new ArrayList();
		List gdata = null;
		Object last = null;
		Object curr = null;
		
		//regroup native
		for(int i=0;i<_nativedata.length;i++){
			curr = _nativedata[i];
			boolean hitn = false;
			boolean hita = false;
			if(last==null || cmpr.compare(last,curr)!=0){
				hitn = true;
				gdata = new ArrayList();
				group.add(gdata);
			}
			gdata.add(curr);
			last = _nativedata[i];
		}
		
		//prepare data,head & foot
		List[] gd = new List[group.size()];
		group.toArray(gd);
		
		_data = new Object[gd.length][];
		_foots = new Object[_data.length];
		_heads = new Object[_data.length];
		_closes = new boolean[_data.length];
		
		for(int i=0;i<gd.length;i++){
			gdata = (List)gd[i];
			_data[i] = new Object[gdata.size()];
			gdata.toArray(_data[i]);
			_heads[i] = createGroupHead(_data[i],i,col);
			_foots[i] = createGroupFoot(_data[i],i,col);
			_closes[i] = createGroupClose(_data[i],i,col);
		}
	}


	/**
	 * create group head Object, default implementation return first element of groupdata.
	 * you can override this method to return your Object.
	 * @param groupdata data the already in a group.
	 * @param index group index
	 * @param col column to group
	 */
	protected Object createGroupHead(Object[] groupdata,int index,int col) {
		return groupdata[0];
	}

	/**
	 * create group foot Object, default implementation return null, which means no foot .
	 * you can override this method to return your Object.
	 * @param groupdata data the already in a group.
	 * @param index group index
	 * @param col column to group
	 */
	protected Object createGroupFoot(Object[] groupdata,int index,int col) {
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
	protected void sortDataInGroupOrder(Comparator cmpr, boolean ascending, int colIndex) {
		Arrays.sort(_nativedata,cmpr);
	}

	/**
	 * create group close status, default implementation return false, which means open the group.
	 * you can override this method to return your group close status.
	 * @param groupdata data the already in a group.
	 * @param index group index
	 * @param col column to group
	 * @since 5.0.0
	 */
	protected boolean createGroupClose(Object[] groupdata,int index,int col) {
		return false;
	}
}
