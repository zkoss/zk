package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

public class B60_ZK_1007_TreeComposer  extends GenericForwardComposer<Div>  {

	private static final long serialVersionUID = -2653808762374580742L;
	private Label lbl;
	private Tree treeGrid;

	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		Events.echoEvent(new Event("onResult",treeGrid));
		
	}
	
	public void onResult$treeGrid(Event event){
		lbl.setValue(lbl.getValue()+": Render items : " + treeGrid.getAttribute("test"));
	}

	public void onInitModel$treeGrid() {
		lbl.setValue(lbl.getValue() + "onInitModel/");
	}

	public void onInitRender$treeGrid() {
		lbl.setValue(lbl.getValue() + "onInitRender/");
	}

	public DefaultTreeModel<FileInfo> getTreeModel() {
		return new DefaultTreeModel<FileInfo>(getFileInfoTreeData());
	}

	public FileInfoRenderer getTreeRenderer() {
		return new FileInfoRenderer();
	}

	private DefaultTreeNode<FileInfo> getFileInfoTreeData() {
		List<DefaultTreeNode<FileInfo>> inner3 = new ArrayList<DefaultTreeNode<FileInfo>>();
		inner3.add(new DefaultTreeNode<FileInfo>(new FileInfo("zcommon.jar",
				"ZK Common Library")));
		inner3.add(new DefaultTreeNode<FileInfo>(new FileInfo("zk.jar",
				"ZK Core Library")));

		List<DefaultTreeNode<FileInfo>> inner2 = new ArrayList<DefaultTreeNode<FileInfo>>();
		inner2.add(new DefaultTreeNode<FileInfo>(new FileInfo("/lib",
				"ZK Libraries"), inner3));
		inner2.add(new DefaultTreeNode<FileInfo>(new FileInfo("/src",
				"Source Code")));
		inner2.add(new DefaultTreeNode<FileInfo>(new FileInfo("/xsd",
				"XSD Files")));

		List<DefaultTreeNode<FileInfo>> inner1 = new ArrayList<DefaultTreeNode<FileInfo>>();
		inner1.add(new DefaultTreeNode<FileInfo>(new FileInfo("/doc",
				"Release and License Notes")));
		inner1.add(new DefaultTreeNode<FileInfo>(new FileInfo("/dist",
				"Distribution"), inner2));

		return new DefaultTreeNode<FileInfo>(null, inner1);
	}

	public class FileInfoRenderer implements
			TreeitemRenderer<DefaultTreeNode<FileInfo>> {

		public void render(final Treeitem item, DefaultTreeNode<FileInfo> data,
				int index) throws Exception {
			if(item.getTree().getAttribute("test") != null){
				item.getTree().setAttribute("test", (Integer) item.getTree().getAttribute("test") + 1);
			}else{
				item.getTree().setAttribute("test", 1);
			}
			
			item.setValue(data);
			final FileInfo fi = data.getData();
			item.setOpen(false);
			// for update treeNode data
			Treerow tr = item.getTreerow();
			if (tr == null) {
				tr = new Treerow();
			} else {
				tr.getChildren().clear();
			}
			item.appendChild(tr);
			// render file path cell
			Treecell pathCell = new Treecell();
			pathCell.setLabel(fi.getPath());
			pathCell.setParent(tr);
			// render file description cell
			Treecell descriptionCell = new Treecell();
			descriptionCell.setLabel(fi.getDescription());
			descriptionCell.setParent(tr);
		}
	}

	public class FileInfo {
		private String path;
		private String description;

		public FileInfo() {
		}

		public FileInfo(String path, String description) {
			this.path = path;
			this.description = description;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}
}
