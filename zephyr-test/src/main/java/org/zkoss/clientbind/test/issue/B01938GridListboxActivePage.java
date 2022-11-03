package org.zkoss.clientbind.test.issue;

import java.util.ArrayList;
import java.util.List;

public class B01938GridListboxActivePage {

	private int pageIndex = 0;
	private int pageSize = 3;
	
	List<String> data;
	
	public B01938GridListboxActivePage(){
		data = new ArrayList<String>();
		
		for(int i=0;i<500;i++){
			data.add("Item-"+i);
		}
		
	}
	
	public List<String> getData(){
		return data;
	}
	
	
	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}