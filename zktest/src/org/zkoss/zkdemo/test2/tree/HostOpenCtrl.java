/* HostOpenCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jan 24, 2008 11:11:19 AM     2008, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkdemo.test2.tree;

import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.Treeitem;


/**
 * @author Dennis.Chen
 *
 */
public class HostOpenCtrl implements EventListener{
	org.zkoss.zkdemo.test2.tree.HostTreeModel.FakeHost host;
	Treeitem treeitem;
	public HostOpenCtrl(org.zkoss.zkdemo.test2.tree.HostTreeModel.FakeHost host2,Treeitem treeitem) {
		this.host = host2;
		this.treeitem = treeitem;
	}


	public void onEvent(Event event) throws Exception {
		if(((OpenEvent)event).isOpen()){
			List hostitems = treeitem.getParent().getChildren();// find all host node.
			int size = hostitems.size();
			for(int i=0;i<size;i++){
				Treeitem item = (Treeitem)hostitems.get(i);
				if(item!=treeitem && item.isLoaded()){//if not opening node and is loaded, then close it.
					item.unload();
				}
				
			}
		}
	}

}
