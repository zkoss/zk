/* B96_ZK_4804Composer.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 16 12:45:36 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;

/**
 * @author rudyhuang
 */
public class B96_ZK_4804Composer extends GenericForwardComposer<Component> {
	private Tree theTree;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		theTree = (Tree) comp.getFellow("theTree");

		List<DefaultTreeNode<String>> childnodes = new ArrayList<>();
		for (int i = 0; i < 15000; ++i) {
			childnodes.add(new DefaultTreeNode<>("" + i));
		}

		DefaultTreeNode<String> root = new DefaultTreeNode<>("", childnodes);

		theTree.setModel(new DefaultTreeModel<>(root));

		theTree.addEventListener("onNewModel", event -> {
			Thread.sleep(2000);
			Clients.clearBusy();

			theTree.setModel((DefaultTreeModel<?>) event.getData());
			theTree.invalidate();
		});
	}

	public void onClick$btn(Event e) {
		List<DefaultTreeNode<String>> childnodes = new ArrayList<>();
		for (int i = 15000; i > 0; --i) {
			childnodes.add(new DefaultTreeNode<>("" + i));
		}

		DefaultTreeNode<String> root = new DefaultTreeNode<>("", childnodes);

		// Current scroll position in tree does not seem to be reset when clearing model.
		// The same issue occurs if replacing the echoEvent with just setting the new model after first setting null.
		//
		// Comment out this line and all is well
		theTree.setModel(null);

		Clients.showBusy("Loading new model...");
		Events.echoEvent("onNewModel", theTree, new DefaultTreeModel<>(root));
	}
}
