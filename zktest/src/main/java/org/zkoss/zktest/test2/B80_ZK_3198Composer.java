/* B80_ZK_3198Composer.java

	Purpose:

	Description:

	History:
		Fri, June 3, 2016 02:27:14 PM, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;

/**
 *
 * @author jameschu
 */
public class B80_ZK_3198Composer extends SelectorComposer {

	@Wire
	Tree selectionTree;
	
	private DefaultTreeNode<String> myRoot = new DefaultTreeNode<String>("root",new ArrayList<DefaultTreeNode<String>>());
	private DefaultTreeModel<String> myTreeModel = new DefaultTreeModel<String>(myRoot);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		for (int i = 0; i < 100; i++) {
			myRoot.add(new DefaultTreeNode<String>("test"+i));
		}
		selectionTree.setModel(myTreeModel);
	}
	
}
