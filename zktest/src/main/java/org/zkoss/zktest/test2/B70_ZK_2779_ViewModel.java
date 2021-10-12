package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Treeitem;

/**
 * @author jumperchen
 *
 */
public class B70_ZK_2779_ViewModel {
	DefaultTreeModel<TreeObject> treeModel;

	@Init
	public void init() {
		create();
	}

	@Command
	public void treeSelect(
			@ContextParam(ContextType.TRIGGER_EVENT) SelectEvent event) {
//		Clients.log("selection count before select "
//				+ treeModel.getSelectionCount());
		Treeitem item = (Treeitem) event.getReference();
		DefaultTreeNode<TreeObject> treenode = item.getValue();
		TreeObject node = treenode.getData();

		if (node.getType() == 1) { // folder
			if (item.isLoaded()) {
				for (Treeitem child : item.getTreechildren().getItems()) {
					child.setSelected(item.isSelected());
					if (item.isSelected()) {
						treeModel
								.addToSelection((DefaultTreeNode<TreeObject>) child
										.getValue());
					} else {
						treeModel
								.removeFromSelection((DefaultTreeNode<TreeObject>) child
										.getValue());
					}
					TreeNode<TreeObject> childNode = child.getValue();
					childNode.getData().setSelected(item.isSelected());
				}
			} else {
				for (TreeNode<TreeObject> child : treenode.getChildren()) {
					if (item.isSelected()) {
						treeModel.addToSelection(child);
					} else {
						treeModel.removeFromSelection(child);

					}
					child.getData().setSelected(item.isSelected());
				}
			}
			node.setSelected(node.isSelected());
		}
		Clients.log("selection count after unchecked is "
				+ treeModel.getSelectionCount());
	}

	public DefaultTreeModel<TreeObject> getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(DefaultTreeModel<TreeObject> treeModel) {
		this.treeModel = treeModel;
	}

	public static class TreeObject {
		int type;
		int id;
		String name;
		boolean selected;
		int status;

		public TreeObject(int type, int id, String name) {
			this.type = type;
			this.id = id;
			this.name = name;
		}

		public TreeObject(int type, int id, String name, boolean selected) {
			this.type = type;
			this.id = id;
			this.name = name;
			this.selected = selected;
		}

		public TreeObject(int type, int id, String name, boolean selected,
				int status) {
			this.type = type;
			this.id = id;
			this.name = name;
			this.selected = selected;
			this.status = status;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			TreeObject that = (TreeObject) o;

			if (type != that.type)
				return false;
			return id == that.id;

		}

		@Override
		public int hashCode() {
			int result = type;
			result = 31 * result + id;
			return result;
		}

		@Override
		public String toString() {
			return "TreeObject{" + "type=" + type + ", id=" + id + ", name='"
					+ name + '\'' + ", selected=" + selected + ", status="
					+ status + '}';
		}
	}

	public void create() {
		List<DefaultTreeNode<TreeObject>> folderNodes = new ArrayList<DefaultTreeNode<TreeObject>>();
		List<DefaultTreeNode<TreeObject>> selection = new ArrayList<DefaultTreeNode<TreeObject>>();
		List<DefaultTreeNode<TreeObject>> objectNodes = new ArrayList<DefaultTreeNode<TreeObject>>();
		DefaultTreeNode<TreeObject> object = null;
		DefaultTreeNode<TreeObject> folderNode = null;

		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2205,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2206,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2197,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2201,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2202,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2200,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2203,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2198,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2210,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2204,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		folderNode = new DefaultTreeNode<TreeObject>(new TreeObject(1, 226,
				"folder (objects: 10, selected: 10)", true, 0), objectNodes);
		selection.add(folderNode);
		folderNodes.add(folderNode);
		objectNodes.clear();

		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2437,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2429,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2434,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2440,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2430,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2438,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2435,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2433,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2442,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2432,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		folderNode = new DefaultTreeNode<TreeObject>(new TreeObject(1, 227,
				"folder (objects: 10, selected: 10)", true, 0), objectNodes);
		selection.add(folderNode);
		folderNodes.add(folderNode);
		objectNodes.clear();
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2216,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2228,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2219,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2222,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2215,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2212,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2225,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2485,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2231,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		object = new DefaultTreeNode<TreeObject>(new TreeObject(2, 2483,
				"object", true));
		objectNodes.add(object);
		selection.add(object);
		folderNode = new DefaultTreeNode<TreeObject>(new TreeObject(1, 218,
				"folder (objects: 10, selected: 10)", true, 0), objectNodes);
		selection.add(folderNode);
		folderNodes.add(folderNode);
		objectNodes.clear();
		treeModel = new DefaultTreeModel<TreeObject>(
				new DefaultTreeNode<TreeObject>(null, folderNodes), true);
		treeModel.setMultiple(true);
		treeModel.setSelection(selection);
	}
}
