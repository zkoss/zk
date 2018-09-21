/* B80_ZK_3853Composer.java

        Purpose:
                
        Description:
                
        History:
                Thu Aug 30 17:08:24 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.TriStateTreeModel;
import org.zkoss.zkmax.zul.TriStateTreeNode;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;

public class F86_ZK_3853Composer extends SelectorComposer<Component> {

	@Wire
	Tree treem;
	DefaultTreeModel<String> model ;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		initModel();
		treem.setModel(model);
	}

	private void initModel() {
		DefaultTreeNode root = new TriStateTreeNode(null,
				new TriStateTreeNode[] {
				new TriStateTreeNode("A", new TriStateTreeNode[] {
						new TriStateTreeNode("B", new TriStateTreeNode[] {
								new TriStateTreeNode("C1"),
								new TriStateTreeNode("C2"),
								new TriStateTreeNode("C3"),
								new TriStateTreeNode("C4"),
								new TriStateTreeNode("C5"),
								new TriStateTreeNode("C6")
						}),
						new TriStateTreeNode("B3", new TriStateTreeNode[] {
								new TriStateTreeNode("C7"),
								new TriStateTreeNode("C8"),
								new TriStateTreeNode("C9")
						}),
						new TriStateTreeNode("B4"),
				}),
		});
		model = new TriStateTreeModel<>(root, true);
		model.setMultiple(true);
	}

	@Listen ("onClick = #print")
	public void print() {
		Iterator iterator = model.getSelection().iterator();
		while (iterator.hasNext()) {
			Clients.log(iterator.next().toString());
		}
	}

}
