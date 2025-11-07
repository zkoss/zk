/* B103_ZK_5254Composer.java

        Purpose:

        Description:

        History:
                Thu Nov 06 22:33:02 CST 2025, Created by peggypeng

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

public class B103_ZK_5254Composer extends GenericForwardComposer{
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        List<DefaultTreeNode> childnodes = new ArrayList<DefaultTreeNode>();
        for( int i=0; i<500; ++i) {
            childnodes.add(new DefaultTreeNode("" + i));
        }

        DefaultTreeNode root = new DefaultTreeNode("",childnodes);

        ((Tree) comp.getFellow("theTree")).setModel(new DefaultTreeModel(root));
        ((Listbox) comp.getFellow("lb")).setModel(new ListModelList(Locale.getAvailableLocales()));
    }

    public void onClick$btn(Event event) throws InterruptedException{
        Window window = (Window)event.getTarget();
        Tree tree = (Tree)window.getFellow("theTree");
        tree.clearSelection();
    }
    public void onClick$btn2(Event event) throws InterruptedException{
        Window window = (Window)event.getTarget();
        Listbox lb = (Listbox)window.getFellow("lb");
        lb.clearSelection();
    }
}
