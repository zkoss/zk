/* Test1GroupsModelArray.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sep 3, 2008 12:10:32 PM     2008, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2.group;

import java.util.Comparator;

import org.zkoss.zul.GroupsModelArray;

/**
 * @author Dennis.Chen
 *
 */
public class Test1GroupsModelArray extends GroupsModelArray{
	public Test1GroupsModelArray(Object[] data,Comparator cmpr){
		super(data,cmpr,0);
	}
	protected Object createGroupHead(Object[] groupdata,int index,int col) {
		String s1 = ((String)groupdata[0]);
		return "Group "+s1.charAt(col==0?0:s1.length()-1)+" : "+groupdata.length+" Items";
	}
	
	protected Object createGroupFoot(Object[] groupdata,int index, int col) {
		int total = 0;
		for(int i=0;i<groupdata.length;i++){
			String s1 = ((String)groupdata[i]);
			total += Integer.parseInt(""+s1.charAt(s1.length()-1));
		}
		return "Total:"+total;
	}
}
