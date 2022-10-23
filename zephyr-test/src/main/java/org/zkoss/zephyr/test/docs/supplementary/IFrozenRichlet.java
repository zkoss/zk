/* IFrozenRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 19 12:08:38 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.supplementary;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IColumn;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IFrozen;
import org.zkoss.zephyr.zpr.IGrid;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.IRow;
import org.zkoss.zephyr.zpr.IRows;
import org.zkoss.zephyr.zpr.ITextbox;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IFrozen} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Frozen">IFrozen</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IFrozen
 */
@RichletMapping("/supplementary/iFrozen")
public class IFrozenRichlet implements StatelessRichlet {
	@RichletMapping("/columns")
	public List<IComponent> columns() {
		return Arrays.asList(
				IGrid.ofColumns(IColumn.of("Type"), IColumn.of("Content"), IColumn.of("Content"))
						.withRows(IRows.of(
								IRow.of(ILabel.of("File:"), ITextbox.DEFAULT, ITextbox.DEFAULT)
						))
						.withFrozen(IFrozen.ofId("frozen").withColumns(2)).withWidth("500px"),
				IButton.of("change columns").withAction(this::changeColumns)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeColumns() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("frozen"), new IFrozen.Updater().columns(1));
	}

	@RichletMapping("/rightColumns")
	public List<IComponent> rightColumns() {
		return Arrays.asList(
				IGrid.ofColumns(IColumn.of("Type"), IColumn.of("Content"), IColumn.of("Content"))
						.withRows(IRows.of(
								IRow.of(ILabel.of("File:"), ITextbox.DEFAULT, ITextbox.DEFAULT)
						))
						.withFrozen(IFrozen.ofId("frozen").withRightColumns(2)).withWidth("500px"),
				IButton.of("change rightColumns").withAction(this::changeRightColumns)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeRightColumns() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("frozen"), new IFrozen.Updater().rightColumns(1));
	}

	@RichletMapping("/smooth")
	public List<IComponent> smooth() {
		return Arrays.asList(
				IGrid.ofColumns(IColumn.of("Type"), IColumn.of("Content"), IColumn.of("Content"))
						.withRows(IRows.of(
								IRow.of(ILabel.of("File:"), ITextbox.DEFAULT, ITextbox.DEFAULT)
						))
						.withFrozen(IFrozen.ofId("frozen").withSmooth(false).withColumns(1)),
				IButton.of("change smooth").withAction(this::changeSmooth)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSmooth() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("frozen"), new IFrozen.Updater().smooth(true));
	}

	@RichletMapping("/start")
	public List<IComponent> start() {
		return Arrays.asList(
				IGrid.ofColumns(IColumn.of("Type"), IColumn.of("Content"), IColumn.of("Content"))
						.withRows(IRows.of(
								IRow.of(ILabel.of("File:"), ITextbox.DEFAULT, ITextbox.DEFAULT)
						))
						.withFrozen(IFrozen.ofId("frozen").withStart(2).withColumns(2)).withWidth("500px"),
				IButton.of("change start").withAction(this::changeStart)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeStart() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("frozen"), new IFrozen.Updater().start(1));
	}
}