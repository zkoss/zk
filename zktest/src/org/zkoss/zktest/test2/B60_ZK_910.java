package org.zkoss.zktest.test2;

import java.util.Comparator;
import java.util.List;

import org.zkoss.zul.GroupsModelArray;

public class B60_ZK_910<D, H, F, E> extends GroupsModelArray<D, H, F, E> {
	
	public B60_ZK_910(List<D> data, Comparator<D> cmpr) {
		super(data, cmpr, 0);
	}
	
	protected H createGroupHead(D[] groupdata, int index, int col) {
		
		return (H)new Object[] { groupdata[0], new Integer(col) };
	}
}