/* MyObject.java

		Purpose:
		
		Description:
		
		History:
				Mon May 10 16:49:54 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.advance;

public class MyObject {
	private String name;
	private String description;

	public MyObject(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
