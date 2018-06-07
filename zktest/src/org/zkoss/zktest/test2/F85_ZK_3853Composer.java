/* F85_ZK_3853Composer.java

        Purpose:
                
        Description:
                
        History:
                Thu Jun 07 16:11:07 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.*;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

public class F85_ZK_3853Composer extends SelectorComposer<Component> {
	@Wire
	Tree tree1;
	DefaultTreeModel<String> model ;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		initModel();
		tree1.setModel(model);
	}

	private void initModel() {
		DefaultTreeNode root = new DefaultTreeNode(null,
				new DefaultTreeNode[] {
				new DefaultTreeNode("A", new DefaultTreeNode[] {
						new DefaultTreeNode("B", new DefaultTreeNode[] {
								new DefaultTreeNode("C1"),
								new DefaultTreeNode("C2")
						}),
						new DefaultTreeNode("B3", new DefaultTreeNode[] {
								new DefaultTreeNode("C3"),
								new DefaultTreeNode("C4"),
								new DefaultTreeNode("C5")
						}),
						new DefaultTreeNode("B3"),
				}),
		});
		model = new DefaultTreeModel<>(root, true);
		model.setMultiple(true);
	}

	@Listen("onClick = #print")
	public void print() {
		Iterator iterator = model.getSelection().iterator();
		while (iterator.hasNext()) {
			Clients.log(iterator.next().toString());
		}
	}
}
