/* DirectoryTreeNode.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Aug 15, 2011 6:38:17 PM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
 */
package org.zkoss.zktest.test2.tree;

import org.zkoss.zul.DefaultTreeNode;

/**
 * 
 * @author simonpai
 */
public class DirectoryTreeNode extends DefaultTreeNode {
	
	// Node Control the default open
	private boolean open = false;
	
	public DirectoryTreeNode(Object data, DefaultTreeNode[] children,
			boolean open) {
		super(data, children);
		this.setOpen(open);
	}
	
	public DirectoryTreeNode(Object data, DefaultTreeNode[] children) {
		super(data, children);
	}
	
	public DirectoryTreeNode(Object data) {
		super(data);
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
}
