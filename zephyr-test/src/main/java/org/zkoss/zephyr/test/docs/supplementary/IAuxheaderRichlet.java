/* IAuxheaderRichlet.java

	Purpose:

	Description:

	History:
		4:54 PM 2022/2/14, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.supplementary;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IAuxhead;
import org.zkoss.zephyr.zpr.IAuxheader;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IColumn;
import org.zkoss.zephyr.zpr.IColumns;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IGrid;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.IRow;
import org.zkoss.zephyr.zpr.IRows;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of examples for {@link org.zkoss.zephyr.zpr.IAuxheader} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Supplementary/Auxheader">IAuxheader</a>,
 * if any.
 * @author jumperchen
 * @see org.zkoss.zephyr.zpr.IAuxheader
 */
@RichletMapping("/supplementary/iauxheader")
public class IAuxheaderRichlet implements StatelessRichlet {
	@RichletMapping("/example")
	public IComponent example() {
		return IGrid.DEFAULT.withAuxhead(
				IAuxhead.of(
						IAuxheader.of("H1'21").withColspan(6),
						IAuxheader.of("H2'21").withColspan(6)),
				IAuxhead.of(
						IAuxheader.of("Q1'21").withColspan(3),
						IAuxheader.of("Q2'21").withColspan(3),
						IAuxheader.of("Q3'21").withColspan(3),
						IAuxheader.of("Q4'21").withColspan(3))
		).withColumns(IColumns.of(
				IColumn.of("Jan"),
				IColumn.of("Feb"),
				IColumn.of("Mar"),
				IColumn.of("Apr"),
				IColumn.of("May"),
				IColumn.of("Jun"),
				IColumn.of("Jul"),
				IColumn.of("Aug"),
				IColumn.of("Sep"),
				IColumn.of("Oct"),
				IColumn.of("Nov"),
				IColumn.of("Dec")
			)
		).withRows(IRows.of(IRow.of(
						ILabel.of("1,000"),
						ILabel.of("1,100"),
						ILabel.of("1,200"),
						ILabel.of("1,300"),
						ILabel.of("1,400"),
						ILabel.of("1,500"),
						ILabel.of("1,600"),
						ILabel.of("1,700"),
						ILabel.of("1,800"),
						ILabel.of("1,900"),
						ILabel.of("2,000"),
						ILabel.of("2,100")
						)
				)
		);
	}

	@RichletMapping("/limitationOfRowspan")
	public IComponent limitationOfRowspan() {
		return IGrid.DEFAULT.withWidth("200px").withAuxhead(
				IAuxhead.of(
						IAuxheader.of("A").withRowspan(2),
						IAuxheader.of("BC").withColspan(2),
						IAuxheader.of("D").withRowspan(2)
				)
		).withColumns(
				// this is wrong since the number of column components is smaller
				IColumns.of(
						IColumn.of("B"),
						IColumn.of("C")
				)
		).withRows(IRows.of(IRow.of(
				Stream.of("E", "F", "G", "H")
					.map(str -> ILabel.of(str)).collect(Collectors.toList())
		)));
	}

	@RichletMapping("/workaroundForLimitationOfRowspan")
	public IComponent workaroundForLimitationOfRowspan() {
		return IGrid.DEFAULT.withWidth("200px").withAuxhead(
				IAuxhead.of(
						IAuxheader.of("A").withRowspan(2),
						IAuxheader.of("BC").withColspan(2),
						IAuxheader.of("D").withRowspan(2)
				),
				IAuxhead.of(
						IAuxheader.of("B"),
						IAuxheader.of("C")
				)
		).withColumns(IColumns.of(
				// workaround for limitation of rowspan
				IntStream.range(0, 4).mapToObj(i -> IColumn.DEFAULT)
						.collect(Collectors.toList())
				)
		).withRows(IRows.of(IRow.of(
				Stream.of("E", "F", "G", "H")
						.map(str -> ILabel.of(str)).collect(Collectors.toList())
		)));
	}

	@RichletMapping("/image")
	public List<IComponent> image() {
		return Arrays.asList(
				IButton.of("change image").withAction(this::changeImage),
				IGrid.DEFAULT.withAuxhead(IAuxhead.of(IAuxheader.of("A", "/zephyr/ZK-Logo.gif").withId("auxheader")))
						.withColumns(IColumns.of(IColumn.DEFAULT))
						.withRows(IRows.of(IRow.DEFAULT))
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeImage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("auxheader"),
				new IAuxheader.Updater().image("/zephyr-test/zephyr/ZK-Logo-old.gif"));
	}

	@RichletMapping("/colspan")
	public List<IComponent> colspan() {
		return Arrays.asList(
				IButton.of("change colspan").withAction(this::changeColspan),
				IGrid.DEFAULT.withAuxhead(
						IAuxhead.of(
								IAuxheader.of("A").withColspan(2).withId("auxheader1"),
								IAuxheader.of("B").withColspan(2).withId("auxheader2")),
						IAuxhead.of(
								IAuxheader.of("A-1"),
								IAuxheader.of("A-2"),
								IAuxheader.of("B-1"),
								IAuxheader.of("B-2"))
				).withColumns(IColumns.of(
						IntStream.range(0, 4).mapToObj(i -> IColumn.DEFAULT)
								.collect(Collectors.toList()))
				).withRows(IRows.of(IRow.of(
								ILabel.of("C"),
								ILabel.of("D"),
								ILabel.of("E"),
								ILabel.of("F"))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeColspan() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("auxheader1"), new IAuxheader.Updater().colspan(1))
				.smartUpdate(Locator.ofId("auxheader2"), new IAuxheader.Updater().colspan(3));
	}

	@RichletMapping("/rowspan")
	public List<IComponent> rowspan() {
		return Arrays.asList(
				IButton.of("change rowspan").withAction(this::changeRowspan),
				IGrid.DEFAULT.withAuxhead(
						IAuxhead.of(
								IAuxheader.of("A").withRowspan(2).withId("auxheader"),
								IAuxheader.of("B")),
						IAuxhead.of(
								IAuxheader.of("B-1"))
				).withColumns(IColumns.of(
						IntStream.range(0, 2).mapToObj(i -> IColumn.DEFAULT).collect(Collectors.toList()))
				).withRows(IRows.of(IRow.of(
						ILabel.of("C"),
						ILabel.of("D")))
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeRowspan() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("auxheader"), new IAuxheader.Updater().rowspan(1));
	}
}
