package org.zkoss.zktest.test2;

import java.util.HashMap;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;


public class B70_ZK_2460_DemoComposer extends SelectorComposer<Component> {
	private static final long serialVersionUID = 3814570327995355261L;
	
	@Wire
	private Window demoWindow;
	@Wire
	private Tree tree;

	public Treeitem tt;
	
	private B70_ZK_2460_AdvancedTreeModel B70_ZK_2460_ContactTreeModel;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);		
		B70_ZK_2460_ContactTreeModel = new B70_ZK_2460_AdvancedTreeModel(new B70_ZK_2460_ContactList().getRoot());
		tree.setItemRenderer(new B70_ZK_2460_ContactTreeRenderer());
		tree.setModel(B70_ZK_2460_ContactTreeModel);
	}

	/**
	 * The structure of tree
	 * 
	 * <pre>
	 * &lt;treeitem>
	 *   &lt;treerow>
	 *     &lt;treecell>...&lt;/treecell>
	 *   &lt;/treerow>
	 *   &lt;treechildren>
	 *     &lt;treeitem>...&lt;/treeitem>
	 *   &lt;/treechildren>
	 * &lt;/treeitem>
	 * </pre>
	 */
	private final class B70_ZK_2460_ContactTreeRenderer implements TreeitemRenderer<B70_ZK_2460_ContactTreeNode> {
		
		public void render(final Treeitem treeItem, B70_ZK_2460_ContactTreeNode treeNode, int index) throws Exception {
			B70_ZK_2460_ContactTreeNode ctn = treeNode;
			B70_ZK_2460_Contact B70_ZK_2460_Contact = (B70_ZK_2460_Contact) ctn.getData();
			final Treerow dataRow = new Treerow();
			dataRow.setParent(treeItem);
			treeItem.setValue(ctn);
			treeItem.setOpen(ctn.isOpen());
			if (!isCategory(B70_ZK_2460_Contact)) { // B70_ZK_2460_Contact Row
				Hlayout hl = new Hlayout();
				hl.appendChild(new Image("/test2/img/" + B70_ZK_2460_Contact.getProfilepic()));
				hl.appendChild(new Label(B70_ZK_2460_Contact.getName()));
				hl.setSclass("h-inline-block");
				Treecell treeCell = new Treecell();
				treeCell.appendChild(hl);
				dataRow.setDraggable("true");
				dataRow.appendChild(treeCell);
			} else { // Category Row
				dataRow.appendChild(new Treecell(B70_ZK_2460_Contact.getCategory()));
			}
			// Both category row and B70_ZK_2460_Contact row can be item dropped
			dataRow.setDroppable("true");
			dataRow.addEventListener(Events.ON_DROP, new EventListener<Event>() {
				@SuppressWarnings("unchecked")
				public void onEvent(Event event) throws Exception {
					// The dragged target is a TreeRow belongs to an Treechildren of TreeItem.
					Treeitem draggedItem = (Treeitem) ((DropEvent) event).getDragged().getParent();
					B70_ZK_2460_ContactTreeNode draggedValue = (B70_ZK_2460_ContactTreeNode) draggedItem.getValue();
					Treeitem parentItem = treeItem.getParentItem();
					B70_ZK_2460_ContactTreeModel.remove(draggedValue);
					if (isCategory((B70_ZK_2460_Contact) ((B70_ZK_2460_ContactTreeNode) treeItem.getValue()).getData())) {
						B70_ZK_2460_ContactTreeModel.add((B70_ZK_2460_ContactTreeNode) treeItem.getValue(),
								draggedValue);
					} else {
						int index = parentItem.getTreechildren().getChildren().indexOf(treeItem);
						if(draggedItem instanceof Treeitem) {
							B70_ZK_2460_ContactTreeModel.insert((B70_ZK_2460_ContactTreeNode)parentItem.getValue(), index, index,
									draggedValue);
						}
					}
				}
			});

		}

		private boolean isCategory(B70_ZK_2460_Contact B70_ZK_2460_Contact) {
			return B70_ZK_2460_Contact.getName() == null;
		}
	}
}