/* PackageDataUnit.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Aug 15, 2011 6:39:26 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zktest.test2.tree;

/**
 * 
 * @author simonpai
 */
public class PackageDataUnit {
	
	private final String path;
	private final String description;
	private final String size;
	
	public PackageDataUnit(String path, String description) {
		this.path = path;
		this.description = description;
		this.size = null;
	}
	
	public PackageDataUnit(String path, String description, String size) {
		this.path = path;
		this.description = description;
		this.size = size;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getSize() {
		return size;
	}
}
