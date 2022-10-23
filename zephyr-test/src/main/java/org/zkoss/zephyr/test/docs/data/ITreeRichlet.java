/* ITreeRichlet.java

	Purpose:

	Description:

	History:
		4:29 PM 2022/2/8, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.data;

import static org.zkoss.zephyr.action.ActionTarget.SELF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.ActionVariable;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.Self;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.util.ActionHandler;
import org.zkoss.zephyr.zpr.IAnyGroup;
import org.zkoss.zephyr.zpr.IAuxhead;
import org.zkoss.zephyr.zpr.IAuxheader;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.IFrozen;
import org.zkoss.zephyr.zpr.ITree;
import org.zkoss.zephyr.zpr.ITreecell;
import org.zkoss.zephyr.zpr.ITreechildren;
import org.zkoss.zephyr.zpr.ITreecol;
import org.zkoss.zephyr.zpr.ITreecols;
import org.zkoss.zephyr.zpr.ITreefoot;
import org.zkoss.zephyr.zpr.ITreefooter;
import org.zkoss.zephyr.zpr.ITreeitem;
import org.zkoss.zephyr.zpr.IWindow;
import org.zkoss.zephyrex.state.ITreeController;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.ITree} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree">ITree</a>,
 * if any.
 * @author jumperchen
 * @see org.zkoss.zephyr.zpr.ITree
 */
@RichletMapping("/data/itree")
public class ITreeRichlet implements StatelessRichlet {

	@RichletMapping("")
	public IComponent index() {
		return twoColumnTree();
	}

	@RichletMapping("/defaultSelection")
	public IComponent defaultSelection() {
		return ITree.of(
				ITreeitem.of("David"),
				ITreeitem.of("Thomas"),
				ITreeitem.of("Steven").withSelected(true)
		).withWidth("300px");
	}

	@RichletMapping("/mold/default")
	public IComponent twoColumnTree() {
		return IWindow.ofTitle("tree demo").withBorder("normal").withWidth("500px")
			.withChildren(
			ITree.ofId("tree").withRows(5).withTreecols(
				ITreecols.of(
						ITreecol.of("Name"),
						ITreecol.of("Description")
				)).withTreechildren(ITreechildren.of(
					ITreeitem.ofTreecells(
						ITreecell.of("Item 1"),
						ITreecell.of("Item 1 description")
					),
					ITreeitem.ofTreecells(
						ITreecell.of("Item 2"),
						ITreecell.of("Item 2 description"))
							.withTreechildren(
							ITreechildren.of(
								ITreeitem.of("Item 2.1"),
								ITreeitem.ofTreecells(
									ITreecell.of("Item 2.2"),
									ITreecell.of("Item 2.2 is something who cares")),
						ITreeitem.of("Item 3")))))
					.withTreefoot(
						ITreefoot.of(
							ITreefooter.of("Count"),
							ITreefooter.of("Summary")
						)
					)
				);
	}

	@RichletMapping("/mold/paging")
	public IComponent paging() {
		final IWindow<ITree> window = (IWindow<ITree>) twoColumnTree();
		return window.withChildren(window.getChildren().get(0).withMold("paging"));
	}

	@RichletMapping("/mold/paging/test")
	public IComponent pagingTest() {
		final IWindow<ITree> window = (IWindow<ITree>) twoColumnTree();
		return window.withChildren(window.getChildren().get(0).withMold("paging")
				.withPagingChild(ITree.PAGING.getPagingChild().withPageSize(3)));
	}

	@RichletMapping("/checkmark")
	public List<IComponent> checkmark() {
		final IWindow<ITree> window = (IWindow<ITree>) twoColumnTree();
		return Arrays.asList(
				IButton.of("change checkmark").withAction(this::changeCheckmark),
				window.withChildren(window.getChildren().get(0).withCheckmark(true).withId("tree"))
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCheckmark() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tree"), new ITree.Updater().checkmark(false));
	}

	@RichletMapping("/checkmarkDeselectOther")
	public List<IComponent> checkmarkDeselectOther() {
		final IWindow<ITree> window = (IWindow<ITree>) twoColumnTree();
		return Arrays.asList(
				IButton.of("change checkmarkDeselectOther").withAction(this::changeCheckmarkDeselectOther),
				window.withChildren(window.getChildren().get(0).withCheckmark(true)
						.withMultiple(true).withCheckmarkDeselectOther(true).withId("tree"))
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCheckmarkDeselectOther() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tree"), new ITree.Updater().checkmarkDeselectOther(false));
	}

	@RichletMapping("/frozen")
	public IComponent frozen() {
		return ITree.ofId("tree").withRows(5).withWidth("600px").withFrozen(
				IFrozen.ofColumns(2).withStart(1)
		).withTreecols(
				ITreecols.of(
						ITreecol.of("ID").withHflex("min"),
						ITreecol.of("Priority").withHflex("min"),
						ITreecol.of("Status").withHflex("min"),
						ITreecol.of("Summary").withHflex("min"),
						ITreecol.of("Detail").withHflex("min")
				).withSizable(true)
		).withTreechildren(
			ITreechildren.of(
				ITreeitem.ofTreecells(
					ITreecell.of("0001"),
					ITreecell.of("1"),
					ITreecell.of("closed"),
					ITreecell.of("Fix login issue"),
					ITreecell.of("Login does not work at all")
				),
				ITreeitem.ofTreecells(
					ITreecell.of("0002"),
					ITreecell.of("3"),
					ITreecell.of("open"),
					ITreecell.of("Button style broken"),
					ITreecell.of("Check main.css")
				).withTreechildren(
					ITreechildren.of(
						ITreeitem.ofTreecells(
							ITreecell.of("00021"),
							ITreecell.of("1"),
							ITreecell.of("closed"),
							ITreecell.of("Fix logout issue"),
							ITreecell.of("Logout does not work at all")
						)
					)
				),
				ITreeitem.ofTreecells(
					ITreecell.of("0003"),
					ITreecell.of("2"),
					ITreecell.of("open"),
					ITreecell.of("Client search result"),
					ITreecell.of("Search service returns incomplete result")
				)
			)
		);
	}

	@RichletMapping("/multiple")
	public List<IComponent> multiple() {
		final IWindow<ITree> window = (IWindow<ITree>) twoColumnTree();
		return Arrays.asList(
				IButton.of("change multiple").withAction(this::doMultiple),
				window.withChildren(window.getChildren().get(0).withCheckmark(true).withMultiple(true))
		);
	}

	@Action(type = Events.ON_CLICK)
	public void doMultiple() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tree"), new ITree.Updater().multiple(false));
	}

	@RichletMapping("/nonselectableTags")
	public IComponent nonselectableTags() {
		final IWindow<IAnyGroup> window = (IWindow<IAnyGroup>) twoColumnTree();
		return window.withChildren(((ITree) window.getChildren().get(0)).withId("tree"),
				IButton.of("nonselectable tags").withAction(ActionHandler.of(this::doNonselectableTags)));
	}

	@Action(type = Events.ON_CLICK)
	public void doNonselectableTags() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tree"), new ITree.Updater().nonselectableTags("*"));
	}

	@RichletMapping("/rightSelect")
	public IComponent rightSelect() {
		final IWindow<IAnyGroup> window = (IWindow<IAnyGroup>) twoColumnTree();
		return window.withChildren(((ITree) window.getChildren().get(0)).withCheckmark(true).withId("tree"),
				IButton.of("rightSelect false").withAction(ActionHandler.of(this::doRightSelect)));
	}

	@Action(type = Events.ON_CLICK)
	public void doRightSelect() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tree"), new ITree.Updater().rightSelect(false));
	}

	@RichletMapping("/selectOnHighlightDisabled")
	public IComponent selectOnHighlightDisabled() {
		final IWindow<IAnyGroup> window = (IWindow<IAnyGroup>) twoColumnTree();
		return window.withChildren(((ITree) window.getChildren().get(0)).withSelectOnHighlightDisabled(true).withId("tree"),
				IButton.of("selectOnHighlightDisabled false").withAction(ActionHandler.of(this::doSelectOnHighlight)));
	}

	@Action(type = Events.ON_CLICK)
	public void doSelectOnHighlight() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tree"), new ITree.Updater().selectOnHighlightDisabled(false));
	}

	@RichletMapping("/scrollable")
	public IComponent scrollable() {
		return ITree.DEFAULT.withRows(4)
		.withTreecols(
			ITreecols.of(
				ITreecol.of("Name"),
				ITreecol.of("Description")
			)
		).withTreechildren(
			ITreechildren.of(
					ITreeitem.ofTreecells(
						ITreecell.of("Item 1"),
						ITreecell.of("Item 1 description")
					),
					ITreeitem.ofTreecells(
						ITreecell.of("Item 2"),
						ITreecell.of("Item 2 description")
					).withTreechildren(
						ITreechildren.of(
							ITreeitem.of("Item 2.1",
								ITreeitem.of("Item 2.1.1"),
								ITreeitem.of("Item 2.1.2")
							),
							ITreeitem.ofTreecells(
								ITreecell.of("Item 2.2"),
								ITreecell.of("Item 2.2 is something who cares")
							)
						)
					),
					ITreeitem.of("Item 3")
			)
		);
	}
	@RichletMapping("/createOnOpen")
	public IComponent createOnOpen() {
		return ITree.DEFAULT.withWidth("400px")
				.withTreecols(
						ITreecols.of(
								ITreecol.of("Subject"),
								ITreecol.of("From")
						)
				).withTreechildren(
						ITreechildren.of(
								ITreeitem.ofTreecells(
										ITreecell.of("Intel Snares XML"),
										ITreecell.of("David Needle")
								).withTreechildren(
										ITreechildren.DEFAULT
								).withOpen(false).withAction(this::doCreateOnOpen)
						)
				);
	}

	@Action(type = Events.ON_OPEN)
	public void doCreateOnOpen(Self self, @ActionVariable(targetId = SELF, field = "empty") boolean isEmpty) {
		if (isEmpty) {
			UiAgent.getCurrent()
					.replaceChildren(self.findChild(ITreechildren.class),
							ITreeitem.of("New added"));
		}
	}

	@RichletMapping("/sort")
	public IComponent sort() {
		ITreecols cols = ITreecols.of(ITreecol.of("column").withSortAscending(genAscComparator())
				.withSortDescending(genDscComparator()));
		ITree tree = ITree.ofTreecols(cols).withHeight("500px");
		return ITreeController.of(tree, initTreeModel()).build();
	}

	@RichletMapping("/autoSort")
	public IComponent autoSort() {
		ITreecols cols = ITreecols.of(ITreecol.of("column")
				.withSortDescending(genDscComparator()).withSortDirection(ITreecol.SortDirection.DESCENDING));
		ITree tree = ITree.ofTreecols(cols).withAutosort(ITree.Autosort.ENABLE).withHeight("500px");
		return ITreeController.of(tree, initTreeModel()).build();
	}

	private DefaultTreeModel initTreeModel() {
		DefaultTreeNode root = new DefaultTreeNode("ROOT", new ArrayList());
		DefaultTreeModel stm = new DefaultTreeModel(root);
		DefaultTreeNode visibleRoot = new DefaultTreeNode("Visible ROOT", new ArrayList());
		root.add(visibleRoot);

		// add a tree node which has 2000 children
		DefaultTreeNode node2 = new DefaultTreeNode("0", Collections.emptyList());
		for (int i = 0; i < 10; i++) {
			node2.add(new DefaultTreeNode(i, Collections.emptyList()));
		}
		visibleRoot.add(node2);

		// add other nodes
		for (int i = 1; i < 10; i++) {
			visibleRoot.add(new DefaultTreeNode(i, Collections.emptyList()));
		}
		for (int i = 0; i < 10; i++) {
			root.add(new DefaultTreeNode(Integer.toHexString(i), Collections.emptyList()));
		}

		int childSize = root.getChildCount();
		DefaultTreeNode lastNode = (DefaultTreeNode) root.getChildAt(childSize - 1);
		for (int i = 0; i < 10; i++) {
			lastNode.add(new DefaultTreeNode(i, Collections.emptyList()));
		}
		return stm;
	}

	private Comparator genAscComparator() {
		return new Comparator() {
			public int compare(Object t1, Object t2) {
				return ((TreeNode) t1).getData().toString().compareTo(((TreeNode) t2).getData().toString());
			}
		};
	}

	private Comparator genDscComparator() {
		return new Comparator() {
			public int compare(Object t1, Object t2) {
				return ((TreeNode) t2).getData().toString().compareTo(((TreeNode) t1).getData().toString());
			}
		};
	}

	@RichletMapping("/children")
	public IComponent children() {
		return ITree.DEFAULT.withAuxhead(IAuxhead.of(IAuxheader.of("auxheader")))
				.withRows(4).withTreecols(
						ITreecols.of(
								ITreecol.of("Name"),
								ITreecol.of("Description")
						)
				).withTreechildren(
						ITreechildren.of(
								ITreeitem.ofTreecells(
										ITreecell.of("Item 1"),
										ITreecell.of("Item 1 description")
								),
								ITreeitem.ofTreecells(
										ITreecell.of("Item 2"),
										ITreecell.of("Item 2 description")
								).withTreechildren(
										ITreechildren.of(
												ITreeitem.of("Item 2.1",
														ITreeitem.of("Item 2.1.1"),
														ITreeitem.of("Item 2.1.2")
												),
												ITreeitem.ofTreecells(
														ITreecell.of("Item 2.2"),
														ITreecell.of("Item 2.2 is something who cares")
												)
										)
								),
								ITreeitem.of("Item 3")
						)
				).withTreefoot(
						ITreefoot.of(
								ITreefooter.of("Count"),
								ITreefooter.of("Summary")
						)
				);
	}

	@RichletMapping("/heightAndRows")
	public IComponent heightAndRows() {
		return IDiv.of(IButton.of("check not allow to set width").withAction(this::doHeightAndRows));
	}

	@Action(type = Events.ON_CLICK)
	public void doHeightAndRows() {
		ITree.DEFAULT.withHeight("100px").withRows(1);
	}
}
