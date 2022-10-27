/* IMeshElementRichlet.java

	Purpose:

	Description:

	History:
		Tue Feb 22 16:42:32 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.base_components;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.zpr.IMeshElement;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.zpr.IColumn;
import org.zkoss.stateless.zpr.IColumns;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IDiv;
import org.zkoss.stateless.zpr.IGrid;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.stateless.zpr.IListbox;
import org.zkoss.stateless.zpr.IListhead;
import org.zkoss.stateless.zpr.IListheader;
import org.zkoss.stateless.zpr.IListitem;
import org.zkoss.stateless.zpr.IRow;
import org.zkoss.stateless.zpr.ITree;
import org.zkoss.stateless.zpr.ITreecol;
import org.zkoss.stateless.zpr.ITreecols;
import org.zkoss.stateless.zpr.ITreeitem;
import org.zkoss.statelessex.state.IGridController;
import org.zkoss.statelessex.state.IListboxController;
import org.zkoss.statelessex.state.ITreeController;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.SimpleListModel;

/**
 * A set of example for {@link IMeshElement} Java Docs.
 *
 * @author katherine
 * @see IMeshElement
 */
@RichletMapping("/base_components/IMeshElement")
public class IMeshElementRichlet implements StatelessRichlet {

	@RichletMapping("/paging")
	public IComponent paging() {
		ITree tree = ITree.DEFAULT.withMold("paging").withAutopaging(true)
				.withHeight("200px").withPagingPosition("top").withPagingChild(ITree.PAGING.getPagingChild().withPageSize(3));
		IGrid grid = IGrid.DEFAULT.withMold("paging").withAutopaging(true)
				.withHeight("200px").withPagingPosition("top").withPagingChild(ITree.PAGING.getPagingChild().withPageSize(3));
		IListbox listbox = IListbox.DEFAULT.withMold("paging").withAutopaging(true)
				.withHeight("200px").withPagingPosition("top").withPagingChild(ITree.PAGING.getPagingChild().withPageSize(3));
		return IDiv.of(
				ITreeController.of(tree, genTreeModel()).build(),
				IGridController.of(grid, genListModel()).build(),
				IListboxController.of(listbox, genListModel()).build()
		);
	}

	@RichletMapping("/nativeScrollbar")
	public IComponent nativeScrollbar() {
		return IDiv.of(
				genTree().withHeight("100px").withNativeScrollbar(false),
				genGrid().withHeight("100px").withNativeScrollbar(false),
				genListbox().withHeight("100px").withNativeScrollbar(false)
		);
	}

	@RichletMapping("/sizedByContent")
	public IComponent sizedByContent() {
		return IDiv.of(
				ITree.of(ITreeitem.of("123456789")).withSizedByContent(true),
				ITree.of(ITreeitem.of("123456789")).withSizedByContent(false),
				IGrid.of(IRow.of(ILabel.of("123456789"))).withSizedByContent(true),
				IGrid.of(IRow.of(ILabel.of("123456789"))).withSizedByContent(false),
				IListbox.of(IListitem.of("123456789")).withSizedByContent(true),
				IListbox.of(IListitem.of("123456789")).withSizedByContent(false)
		);
	}

	@RichletMapping("/span")
	public IComponent span() {
		return IDiv.of(
				ITree.DEFAULT.withTreecols(ITreecols.of(ITreecol.of("12345").withHflex("min"),
						ITreecol.of("12345").withHflex("min"))).withSpan(true).withWidth("400px"),
				IGrid.DEFAULT.withColumns(IColumns.of(IColumn.of("12345").withHflex("min"),
						IColumn.of("12345").withHflex("min"))).withSpan(true).withWidth("400px"),
				IListbox.DEFAULT.withListhead(IListhead.of((IListheader) IListheader.of("12345").withHflex("min"),
						(IListheader) IListheader.of("12345").withHflex("min"))).withSpan(true).withWidth("400px")
		);
	}

	public ITree genTree() {
		return ITree.of(IntStream.range(1, 11).mapToObj(i ->
						ITreeitem.of(String.valueOf(i))).collect(Collectors.toList()))
				.withTreecols(ITreecols.of(ITreecol.of("treecol")));
	}

	public IGrid genGrid() {
		return IGrid.of(IntStream.range(1, 11).mapToObj(i ->
						IRow.of(ILabel.of(String.valueOf(i)))).collect(Collectors.toList()))
				.withColumns(IColumns.of(IColumn.of("column")));
	}

	public IListbox genListbox() {
		return IListbox.of(IntStream.range(1, 11).mapToObj(i ->
						IListitem.of(String.valueOf(i))).collect(Collectors.toList()))
				.withListhead(IListhead.of(IListheader.of("header")));
	}

	public DefaultTreeModel genTreeModel() {
		ArrayList children = new ArrayList();
		children.add(new DefaultTreeNode("item 1"));
		children.add(new DefaultTreeNode("item 2"));
		children.add(new DefaultTreeNode("item 3"));
		children.add(new DefaultTreeNode("item 4"));
		children.add(new DefaultTreeNode("item 5"));
		return new DefaultTreeModel(new DefaultTreeNode("Root", children));
	}

	public ListModel genListModel() {
		String[] data = new String[5];
		for(int j=0; j < data.length; ++j) {
			data[j] = "option " + j;
		}
		return new SimpleListModel(data);
	}
}