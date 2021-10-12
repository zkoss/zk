/* B01165VM1.java

	Purpose:
		
	Description:
		
	History:
		Jun 1, 2012, Created by Ian Tsai(Zanyking)

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under ZOL in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Ian Y.T Tsai(zanyking)
 *
 */
public class B01165VM1 {

	public static class Bean{
		private String id;
		private String desc;
		public Bean() {}
		public Bean(String id, String desc) {
			super();
			this.id = id;
			this.desc = desc;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		
	}
	private static final List<Bean> BEANS; 
	static {
		ArrayList<Bean> bs = new ArrayList<Bean>();
		bs.add(new Bean("b1","this is b1"));
		bs.add(new Bean("b2","this is b2"));
		bs.add(new Bean("b3","this is b3"));
		bs.add(new Bean("b4","this is b4"));
		BEANS = Collections.unmodifiableList(bs);
	}
	
	private Bean selected;
	private List<Bean> list;
	
	
	
	public B01165VM1() {
		list = BEANS;
		selected = list.get(2);
	}
	
	
	
	public Bean getSelected() {
		return selected;
	}
	public void setSelected(Bean selected) {
		this.selected = selected;
	}
	public List<Bean> getList() {
		return list;
	}
	public void setList(List<Bean> list) {
		this.list = list;
	}
	 
	
	
	
}
