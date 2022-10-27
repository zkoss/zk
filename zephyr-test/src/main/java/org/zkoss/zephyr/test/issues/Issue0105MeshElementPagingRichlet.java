/* Issue0105MeshElementPagingRichlet.java

	Purpose:

	Description:

	History:
		5:28 PM 2022/3/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.issues;

import org.zkoss.stateless.action.data.InputData;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IColumn;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IGrid;
import org.zkoss.stateless.zpr.IHlayout;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.stateless.zpr.IListbox;
import org.zkoss.stateless.zpr.IListcell;
import org.zkoss.stateless.zpr.IListfoot;
import org.zkoss.stateless.zpr.IListfooter;
import org.zkoss.stateless.zpr.IListheader;
import org.zkoss.stateless.zpr.IListitem;
import org.zkoss.stateless.zpr.IRow;
import org.zkoss.stateless.zpr.IRows;
import org.zkoss.stateless.zpr.ITextbox;
import org.zkoss.stateless.zpr.ITree;
import org.zkoss.stateless.zpr.ITreecell;
import org.zkoss.stateless.zpr.ITreechildren;
import org.zkoss.stateless.zpr.ITreecol;
import org.zkoss.stateless.zpr.ITreecols;
import org.zkoss.stateless.zpr.ITreefoot;
import org.zkoss.stateless.zpr.ITreefooter;
import org.zkoss.stateless.zpr.ITreeitem;
import org.zkoss.stateless.zpr.IVlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * @author jumperchen
 */
@RichletMapping("/issue0105")
public class Issue0105MeshElementPagingRichlet implements StatelessRichlet {
	@RichletMapping("/grid")
	public IComponent pagingGrid() {
		IGrid iGrid = IGrid.ofColumns(IColumn.of("Type").withHflex("min"), IColumn.of("Content"))
				.withRows(IRows.of(
						IRow.of(ILabel.of("File:"), ITextbox.DEFAULT.withAction(this::testTyping)),
						IRow.of(ILabel.of("Type:"), IHlayout.of(ITextbox.DEFAULT.withAction(this::testTyping), IButton.of("Browser..."))),
						IRow.of(ILabel.of("Description:"), ITextbox.ofMultiline(true).withRows(3).withAction(this::testTyping))
				)).withWidth("480px").withMold("paging");
		return IVlayout.of(
				ILabel.ofId("msg")
		, iGrid.withPagingChild(iGrid.getPagingChild().withPageSize(2)));
	}

	@Action(type = Events.ON_CHANGE)
	public void testTyping(InputData data) {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("msg"), new ILabel.Updater().value(data.getValue()));
	}

	@RichletMapping("/listbox")
	public IComponent pagingListbox() {
		ITextbox iTextbox = ITextbox.DEFAULT.withAction(this::testTyping);
		IListbox iListbox = IListbox.ofListheaders(IListheader.of("Name"), IListheader.of("Gender"))
				.withChildren(
						IListitem.of(IListcell.of("Mary"), IListcell.of(iTextbox)),
						IListitem.of(IListcell.of("John"), IListcell.of(iTextbox)),
						IListitem.of(IListcell.of("Jane"),IListcell.of(iTextbox)),
						IListitem.of(IListcell.of("Henry"),IListcell.of(iTextbox))
				).withListfoot(IListfoot.of(
								IListfooter.of("This is footer1"),
								IListfooter.of("This is footer2")
						)
				).withWidth("450px").withMold("paging");

		return IVlayout.of(
				ILabel.ofId("msg"),
				iListbox.withPagingChild(iListbox.getPagingChild().withPageSize(2))
		);
	}

	@RichletMapping("/tree")
	public IComponent pagingTree() {
		ITextbox iTextbox = ITextbox.DEFAULT.withAction(this::testTyping);
		ITree iTree = ITree.ofId("tree").withRows(5).withTreecols(
						ITreecols.of(ITreecol.of("Name"), ITreecol.of("Description")))
				.withTreechildren(ITreechildren.of(
						ITreeitem.ofTreecells(ITreecell.of("Item 1"),
								ITreecell.of(iTextbox)),
						ITreeitem.ofTreecells(ITreecell.of("Item 2"),
										ITreecell.of(iTextbox))
								.withTreechildren(ITreechildren.of(
										ITreeitem.of("Item 2.1"),
										ITreeitem.ofTreecells(
												ITreecell.of("Item 2.2"),
												ITreecell.of(iTextbox)),
										ITreeitem.ofTreecells(ITreecell.of("Item 3"), ITreecell.of(iTextbox)))))).withTreefoot(
						ITreefoot.of(ITreefooter.of("Count"),
								ITreefooter.of("Summary"))).withMold("paging");
		return IVlayout.of(
				ILabel.ofId("msg"),
				iTree.withPagingChild(iTree.getPagingChild().withPageSize(2))
		);
	}

}
