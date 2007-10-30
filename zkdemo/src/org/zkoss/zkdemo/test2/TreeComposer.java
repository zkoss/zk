package org.zkoss.zkdemo.test2;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

public class TreeComposer implements Composer {

	public void doAfterCompose(Component comp) throws Exception {
		Window win = ((Window)comp);
		Treeitem treeitem = (Treeitem)win.getFellow("ti");
		Treeitem newitem = new Treeitem("Created by Composer");
		newitem.setParent(treeitem.getTreechildren());
	}

}

