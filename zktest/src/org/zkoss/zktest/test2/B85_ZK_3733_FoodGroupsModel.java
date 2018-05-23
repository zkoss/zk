package org.zkoss.zktest.test2;

import org.zkoss.zul.GroupsModelArray;

import java.io.Serializable;
import java.util.Comparator;

public class B85_ZK_3733_FoodGroupsModel extends GroupsModelArray<B85_ZK_3733_Food, B85_ZK_3733_FoodGroupsModel.FoodGroupInfo, Object, Object> {
	private static final long serialVersionUID = 1L;
 
	public B85_ZK_3733_FoodGroupsModel(B85_ZK_3733_Food[] data, Comparator<B85_ZK_3733_Food> cmpr) {
		super(data, cmpr);
	}
	
	@Override
	protected FoodGroupInfo createGroupHead(B85_ZK_3733_Food[] groupdata, int index, int col) {
		return new FoodGroupInfo(groupdata[0], index, col);
	}
	
	@Override
	protected Object createGroupFoot(B85_ZK_3733_Food[] groupdata, int index, int col) {
		return groupdata.length;
	}
	
	public int getSize() {
		return B85_ZK_3733_FoodData.size();
	}
	
	public static class FoodGroupInfo implements Serializable {
	 
		private static final long serialVersionUID = 1L;
		private B85_ZK_3733_Food firstChild;
		private int groupIndex;
		private int colIndex;
		
		public FoodGroupInfo(B85_ZK_3733_Food firstChild, int groupIndex, int colIndex) {
			super();
			this.firstChild = firstChild;
			this.groupIndex = groupIndex;
			this.colIndex = colIndex;
		}
		
		public B85_ZK_3733_Food getFirstChild() {
			return firstChild;
		}
		public int getGroupIndex() {
			return groupIndex;
		}
		public int getColIndex() {
			return colIndex;
		}
	}
}