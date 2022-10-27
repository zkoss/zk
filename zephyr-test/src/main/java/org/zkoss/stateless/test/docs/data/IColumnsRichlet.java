/* IColumnsRichlet.java

	Purpose:

	Description:

	History:
		Fri Apr 01 09:48:18 CST 2022, Created by katherine

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
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IColumn;
import org.zkoss.stateless.sul.IColumns;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IGrid;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.IRow;
import org.zkoss.stateless.sul.IRows;
import org.zkoss.stateless.sul.ITextbox;
import org.zkoss.statelessex.sul.IGroup;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IColumns} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Grid/Columns">IColumns</a>,
 * if any.
 *
 * @author katherine
 * @see IColumns
 */
@RichletMapping("/data/iColumns")
public class IColumnsRichlet implements StatelessRichlet {
	@RichletMapping("/columnsgroup")
	public List<IComponent> columnsgroup() {
		return Arrays.asList(
				IGrid.ofColumns(
						IColumns.ofId("cloumns")
								.withChildren(IColumn.of("c1"), IColumn.of("c2")).withColumnsgroup(false)
								.withMenupopup("auto")
						)
						.withRows(IRows.of(
								IGroup.of("group1"),
								IRow.of(ILabel.of("row 1"), ITextbox.DEFAULT),
								IRow.of(ILabel.of("row 2"), ITextbox.DEFAULT))
						),
				IButton.of("change columnsgroup").withAction(this::changeColumnsgroup)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeColumnsgroup() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cloumns"), new IColumns.Updater().columnsgroup(true));
	}

	@RichletMapping("/columnshide")
	public List<IComponent> columnshide() {
		return Arrays.asList(
				IGrid.ofColumns(
						IColumns.ofId("cloumns")
								.withChildren(IColumn.of("c1"), IColumn.of("c2"))
								.withColumnshide(false).withMenupopup("auto")
						)
						.withRows(IRows.of(
								IRow.of(ILabel.of("row 1"), ITextbox.DEFAULT),
								IRow.of(ILabel.of("row 2"), ITextbox.DEFAULT))
						),
				IButton.of("change columnsgroup").withAction(this::changeColumnshide)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeColumnshide() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cloumns"), new IColumns.Updater().columnshide(true));
	}

	@RichletMapping("/menupopup")
	public List<IComponent> menupopup() {
		return Arrays.asList(
				IGrid.ofColumns(
						IColumns.ofId("cloumns").withMenupopup("auto")
								.withChildren(IColumn.of("c1"), IColumn.of("c2"))
				),
				IButton.of("change columnsgroup").withAction(this::changeMenupopup)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMenupopup() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cloumns"), new IColumns.Updater().menupopup("none"));
	}
}