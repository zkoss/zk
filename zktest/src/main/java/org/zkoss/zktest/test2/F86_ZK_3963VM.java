/* TreeVM.java

        Purpose:
                
        Description:
                
        History:
                Mon Jul 30 09:59:34 CST 2018, Created by charlesqiu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Orgitem;
import org.zkoss.zkmax.zul.OrgitemRenderer;
import org.zkoss.zkmax.zul.Orgnode;
import org.zkoss.zul.Button;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;

@SuppressWarnings("unchecked")
public class F86_ZK_3963VM {

	private DefaultTreeModel<DefaultTreeNode<String>> model;
	private DefaultTreeNode item3;
	private DefaultTreeNode root;

	@Init
	private void initModel() {
		item3 = new DefaultTreeNode("item3", new DefaultTreeNode[]{
				new DefaultTreeNode("item5"),
				new DefaultTreeNode("item6"),
				new DefaultTreeNode("item7", new DefaultTreeNode[]{
						new DefaultTreeNode("item10"),
						new DefaultTreeNode("item11")
				})
		});
		DefaultTreeNode item1 = new DefaultTreeNode("item1", new DefaultTreeNode[]{
				new DefaultTreeNode("item2"),
				item3,
				new DefaultTreeNode("item4", new DefaultTreeNode[]{
						new DefaultTreeNode("item8"),
						new DefaultTreeNode("item9", new DefaultTreeNode[]{
								new DefaultTreeNode("item12")
						}),
				})
		});
		root = new DefaultTreeNode(null,
				new DefaultTreeNode[]{
						item1
				});
		model = new DefaultTreeModel(root);
		model.addOpenObject(item1);
		model.addOpenPaths(new int[][]{{0, 1}, {0, 1, 2}});
	}

	public DefaultTreeModel getModel() {
		return model;
	}

	public DefaultTreeModel getNewModel() {
		return new DefaultTreeModel((DefaultTreeNode) root.clone());
	}

	@Command("add3")
	public void add3() {
		model.getChild(new int[]{0}).insert(item3, 1);
	}

	@Command("remove3")
	public void remove3() {
		model.getChild(new int[]{0}).remove(1);
	}

	@Command("select3")
	public void select3() {
		model.addSelectionPath(new int[]{0, 1});
	}

	@Command("clearSelection")
	public void clearSelection() {
		model.clearSelection();
	}

	@Command("open3")
	public void open3() {
		int[] item3 = {0, 1};
		boolean open = model.isPathOpened(item3);
		if (open)
			model.removeOpenPath(item3);
		else
			model.addOpenPath(item3);
	}

	public OrgitemRenderer getItemRenderer() {
		return new OrgitemRenderer() {
			@Override
			public void render(Orgitem item, Object data, int index) throws Exception {
				Orgnode orgnode = new Orgnode();
				final Button button = new Button(Objects.toString(data));
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
					@Override
					public void onEvent(Event event) throws Exception {
						Clients.showNotification(button.getLabel());
					}
				});
				button.setParent(orgnode);
				item.setValue(data);
				orgnode.setParent(item);
			}
		};
	}
}
