/* IGridRichlet.java

	Purpose:

	Description:

	History:
		2:01 PM 2022/3/11, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.data;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IAuxhead;
import org.zkoss.stateless.sul.IAuxheader;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IColumn;
import org.zkoss.stateless.sul.IColumns;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IFoot;
import org.zkoss.stateless.sul.IFooter;
import org.zkoss.stateless.sul.IFrozen;
import org.zkoss.stateless.sul.IGrid;
import org.zkoss.stateless.sul.IHlayout;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.IRow;
import org.zkoss.stateless.sul.IRows;
import org.zkoss.stateless.sul.ITextbox;
import org.zkoss.statelessex.state.IGridController;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.RowComparator;
import org.zkoss.zul.SimpleListModel;

/**
 * A set of example for {@link IGrid} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Grid">IGrid</a>,
 * if any.
 *
 * @author jumperchen
 * @see IGrid
 */
@RichletMapping("/data/igrid")
public class IGridRichlet implements StatelessRichlet {
	@RichletMapping("/mold/default")
	public IGrid defaultMold() {
		return IGrid.ofColumns(IColumn.of("Type").withHflex("min"), IColumn.of("Content"))
				.withRows(IRows.of(
						IRow.of(ILabel.of("File:"), ITextbox.DEFAULT),
						IRow.of(ILabel.of("Type:"), IHlayout.of(ITextbox.DEFAULT, IButton.of("Browser..."))),
						IRow.of(ILabel.of("Description:"), ITextbox.ofMultiline(true).withRows(3))
				)).withWidth("480px");
	}
	@RichletMapping("/mold/paging")
	public IComponent pagingMold() {
		IGrid iGrid = defaultMold().withMold("paging");
		return iGrid.withPagingChild(iGrid.getPagingChild().withPageSize(2));
	}

	@RichletMapping("/autosort")
	public IGrid autosort() {
		IColumns cols = IColumns.of(IColumn.of("column").withSortDirection(IColumn.SortDirection.ASCENDING)
				.withSortAscending(new RowComparator(1, true, true)));
		IGrid grid = IGrid.ofColumns(cols).withAutosort(IGrid.Autosort.ENABLE).withHeight("500px").withId("grid");
		return IGridController.of(grid, initGridModel()).build();
	}

	@RichletMapping("/children")
	public IGrid children() {
		return IGrid.ofColumns(IColumn.of("Type").withHflex("min"), IColumn.of("Content"), IColumn.of("Content"), IColumn.of("Content"))
				.withRows(IRows.of(
						IRow.of(ILabel.of("File:"), ITextbox.DEFAULT, ITextbox.DEFAULT, ITextbox.DEFAULT)
				))
				.withAuxhead(IAuxhead.of(IAuxheader.of("h1").withColspan(2), IAuxheader.of("h2").withColspan(2)))
				.withFoot(IFoot.of(IFooter.of("f1"), IFooter.of("f2")))
				.withFrozen(IFrozen.ofColumns(2));
	}

	@RichletMapping("/emptyMessage")
	public List<IComponent> emptyMessage() {
		return Arrays.asList(
				IGrid.DEFAULT.withEmptyMessage("empty").withId("grid"),
				IButton.of("change emptyMessage").withAction(this::changeEmptyMessage)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeEmptyMessage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("grid"), new IGrid.Updater().emptyMessage("empty message"));
	}

	@RichletMapping("/innerWidth")
	public List<IComponent> innerWidth() {
		return Arrays.asList(
				IGrid.ofColumns(IColumn.of("Type").withHflex("min"), IColumn.of("Content"))
						.withRows(IRows.of(
								IRow.of(ILabel.of("File:"), ITextbox.DEFAULT)
						))
						.withInnerWidth("400px").withWidth("500px").withId("grid"),
				IButton.of("change innerWidth").withAction(this::changeInnerWidth)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeInnerWidth() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("grid"), new IGrid.Updater().innerWidth("450px"));
	}

	@RichletMapping("/oddRowSclass")
	public List<IComponent> oddRowSclass() {
		return Arrays.asList(
				IGrid.ofColumns(IColumn.of("Type").withHflex("min"), IColumn.of("Content"))
						.withRows(IRows.of(
								IRow.of(ILabel.of("File:"), ITextbox.DEFAULT),
								IRow.of(ILabel.of("File:"), ITextbox.DEFAULT)
						))
						.withOddRowSclass("odd-row").withId("grid"),
				IButton.of("change oddRowSclass").withAction(this::changeOddRowSclass)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOddRowSclass() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("grid"), new IGrid.Updater().oddRowSclass("odd"));
	}

	@RichletMapping("/visibleRows")
	public List<IComponent> visibleRows() {
		return Arrays.asList(
				IGrid.ofColumns(IColumn.of("Type").withHflex("min"), IColumn.of("Content"))
						.withRows(IRows.of(
								IRow.of(ILabel.of("Row1"), ITextbox.DEFAULT),
								IRow.of(ILabel.of("Row2"), ITextbox.DEFAULT)
						))
						.withVisibleRows(1).withId("grid"),
				IButton.of("change visibleRows").withAction(this::changeVisibleRows)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeVisibleRows() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("grid"), new IGrid.Updater().visibleRows(2));
	}

	private SimpleListModel initGridModel() {
		String[] data = new String[3];
		data[0] = "2";
		data[1] = "1";
		data[2] = "3";
		return new SimpleListModel(data);
	}
}
