package org.zkoss.zktest.bind.basic;

import java.util.ArrayList;
import java.util.List;

public class PerformanceVMInner1 {

	int count;
	static int switchCount = 0;
	
	List<Bean> list1 = new ArrayList<Bean>();
	List<Bean> list2 = new ArrayList<Bean>();
	
	public PerformanceVMInner1(){
		for(int i=0;i<10;i++){
			list1.add(new Bean("bean "+i));
			list2.add(new Bean("bean "+i));
		}
	}
	
	
	public List<Bean> getList1(){
		return list1;
	}
	public List<Bean> getList2(){
		return list2;
	}
	
	
	public String getFoo(){
		return "Foo:"+(++count);
	}
	
	public int getCount(){
		return count;
	}
	
	public int getSwitchCount(){
		return ++switchCount;
	}
	
	public class Bean {
		String name;
		int count = 0;
		
		public Bean(String name){
			this.name = name;
		}
		
		public String getName(){
			return name+":"+(++count);
		}
	}
	
}
