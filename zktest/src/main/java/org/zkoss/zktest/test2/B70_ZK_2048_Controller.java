package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zktest.util.ModelProvider;
import org.zkoss.zul.Button;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.TreeDataEvent;
import org.zkoss.zul.event.TreeDataListener;

public final class B70_ZK_2048_Controller extends
		GenericForwardComposer<Window> {
	Vlayout pnUpdate;
	Tree treeEgipto;
	Button btOk;
	Label result;
	private DefaultTreeModel treeModel = null;
	private transient TreeDataListener treeDataListener = null;

	private TreeDataListener getTreedataListener() {
		if (treeDataListener == null) {
			treeDataListener = new TreeDataListener() {
				public void onChange(TreeDataEvent event) {
					System.out.println(event.getType() + " == "
							+ TreeDataEvent.SELECTION_CHANGED);
				}
			};
		}
		return treeDataListener;
	}

	private String initTree() {
		String msg = null;
		getTreeModel().addTreeDataListener(getTreedataListener());
		treeEgipto.setModel(getTreeModel());
		treeEgipto.setItemRenderer(ModelProvider.TreeRendererFactory
				.getBeanTreeitemRenderer());
		treeEgipto.setMultiple(true);
		treeEgipto.setCheckmark(true);

		return msg;
	}

	public void onClick$btOk(Event event) {
		initTree();
		pnUpdate.setVisible(true);
	}

	private DefaultTreeModel getTreeModel() {
		if (treeModel == null) {
			treeModel = ModelProvider.TreeModelFactory.getBeanDefaultTreeModel(
					1, 1, 1);
		}
		return treeModel;
	}
}
