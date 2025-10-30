/* B96_ZK_4945VM.java

		Purpose:
		
		Description:
		
		History:
				Fri Jul 09 10:49:56 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Init;

public class B96_ZK_4945VM {
	
	private Pojo pojo;
	
	@Init
	public void init() {
		pojo = new Pojo();
		pojo.setType("A");
		pojo.setName("Peter");
	}
	
	public Pojo getPojo() {
		return pojo;
	}
	
	public static class Pojo {
		private String name;
		private String type;
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
	}
}
