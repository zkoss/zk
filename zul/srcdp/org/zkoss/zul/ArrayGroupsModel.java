/*ArrayGroupsModel.java

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
 * @deprecated As of release 5.0.5, replaced with {@link GroupsModelArray},
 * which is the same but make the naming more consistent with others.
 * 
 * @author Dennis.Chen
 * @since 3.5.0
 * @see GroupsModel
 * @see SimpleGroupsModel
 */
public class ArrayGroupsModel extends GroupsModelArray {
	/**
	 * Constructor
	 * @param data an array data to be grouping.
	 * @param cmpr a comparator implementation help group the data. you could implements {@link GroupComparator} to do more grouping control.<br/>
	 * At 1st phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to sort the data.<br/>
	 * At 2nd phase, it calls {@link Comparator#compare(Object, Object)} or {@link GroupComparator#compareGroup(Object, Object)} to decide which data belong to which group. 
	 * In this phase it also invoke {@link #createGroupHead(Object[], int, int)} and {@link #createGroupFoot(Object[], int, int)} to create head of foot Object of each group.<br/>
	 * At 3rd phase, it calls {@link Comparator#compare(Object, Object)} to sort data in each group.<br/>
	 */
	public ArrayGroupsModel(Object[] data,Comparator cmpr){
		super(data, cmpr);
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
	public ArrayGroupsModel(Object[] data,Comparator cmpr, int col){
		super(data, cmpr, col);
	}
}
