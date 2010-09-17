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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.zkoss.util.ArraysX;
import org.zkoss.zul.event.GroupsDataEvent;

/**
 * An array implementation of {@link GroupsModel}.
 * This implementation supports regroup array to groups depends on {@link Comparator} and {@link GroupComparator}.
 * For immutable content (no re-grouping allowed), please use {@link #SimpleGroupsModel} instead.
 * 
 * @author Dennis.Chen
 * @since 5.0.5
 * @see GroupsModel
 * @see SimpleGroupsModel
 */
public class GroupsModelArray<D, H, F> extends AbstractGroupsModel<D, H, F>
implements GroupsModelExt<D> {
	
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
	public GroupsModelArray(D[] data, Comparator<D> cmpr){
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
	public GroupsModelArray(D[] data, Comparator<D> cmpr, int col){
		if (data == null || cmpr == null)
			throw new IllegalArgumentException("null parameter");
		_nativedata = ArraysX.duplicate(data);
		_comparator = cmpr;
		group(_comparator,true,col);
	}

	public D getChild(int groupIndex, int index) {
		return _data[groupIndex][index];
	}

	public int getChildCount(int groupIndex) {
		return _data[groupIndex].length;
	}

	@SuppressWarnings("unchecked")
	public H getGroup(int groupIndex) {
		return (H)_heads[groupIndex];
	}

	public int getGroupCount() {
		return _data.length;
	}

	@SuppressWarnings("unchecked")
	public F getGroupfoot(int groupIndex) {
		return (F)_foots[groupIndex];
	}

	public boolean hasGroupfoot(int groupIndex) {
		return _foots == null ? false:_foots[groupIndex]!=null;
	}

	public void sort(Comparator<D> cmpr, boolean ascending, int col) {
		sortAllGroupData(cmpr,ascending,col);

		fireEvent(GroupsDataEvent.GROUPS_CHANGED,-1,-1,-1);
	}

	public void group(final Comparator<D> cmpr, boolean ascending, int col) {
		Comparator<D> cmprx;
		if(cmpr instanceof GroupComparator){
			cmprx = new Comparator<D>(){
				public int compare(D o1, D o2) {
					return ((GroupComparator<D>)cmpr).compareGroup(o1, o2);
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
	private void sortAllGroupData(Comparator<D> cmpr,boolean ascending, int col) {
		for(int i=0;i<_data.length;i++){
			sortGroupData(getGroup(i),_data[i],cmpr,ascending,col);
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
	protected void sortGroupData(H group,D[] groupdata,Comparator<D> cmpr,boolean ascending, int col){
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
		List<List<D>> group = new ArrayList<List<D>>();
		List<D> gdata = null;
		D last = null;
		D curr = null;
		
		//regroup native
		for(int i=0;i<_nativedata.length;i++){
			curr = _nativedata[i];
			boolean hitn = false;
			boolean hita = false;
			if(last==null || cmpr.compare(last,curr)!=0){
				hitn = true;
				gdata = new ArrayList<D>();
				group.add(gdata);
			}
			gdata.add(curr);
			last = _nativedata[i];
		}
		
		//prepare data,head & foot
		List<D>[] gd = new List[group.size()];
		group.toArray(gd);
		
		_data = (D[][])Array.newInstance(_nativedata.getClass().getComponentType(), gd.length); //new D[gd.length][];
		_foots = new Object[gd.length];
		_heads = new Object[gd.length];
		_closes = new boolean[_data.length];
		
		for(int i=0;i<gd.length;i++){
			gdata = gd[i];
			_data[i] = (D[])Array.newInstance(
				_nativedata.getClass().getComponentType().getComponentType(), gdata.size());
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
	@SuppressWarnings("unchecked")
	protected H createGroupHead(D[] groupdata,int index,int col) {
		return (H)groupdata[0];
	}

	/**
	 * create group foot Object, default implementation return null, which means no foot .
	 * you can override this method to return your Object.
	 * @param groupdata data the already in a group.
	 * @param index group index
	 * @param col column to group
	 */
	protected F createGroupFoot(D[] groupdata,int index,int col) {
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
	protected boolean createGroupClose(D[] groupdata,int index,int col) {
		return false;
	}
}
