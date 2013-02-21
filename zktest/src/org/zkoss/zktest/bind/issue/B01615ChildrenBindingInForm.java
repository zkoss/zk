package org.zkoss.zktest.bind.issue;

import java.util.Arrays;
import java.util.List;

public class B01615ChildrenBindingInForm {
	private MyData myData1;
	private MyData myData2;
	private MyData myData3;
	
	public MyData getMyData1() {
		if (myData1 == null)
			myData1 = new MyData(Arrays.asList(new String[]{"A","B","C"}));
		return myData1;
	}
	
	public MyData getMyData2() {
		if (myData2 == null)
			myData2 = new MyData(Arrays.asList(new String[]{"D","E","F"}));
		return myData2;
	}
	
	public MyData getMyData3() {
		if (myData3 == null)
			myData3 = new MyData(Arrays.asList(new String[]{"X","Y","Z"}));
		return myData3;
	}
	
	static public class MyData {
		private List list;
		
		public MyData(List list) {
			super();
			this.list = list;
		}

		public List getList() {
			return list;
		}

		public void setList(List list) {
			this.list = list;
		}
		
	}
}
