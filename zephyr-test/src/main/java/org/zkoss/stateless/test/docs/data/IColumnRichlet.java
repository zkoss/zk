/* IColumnRichlet.java

	Purpose:

	Description:

	History:
		Fri Apr 01 15:37:00 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.data;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.IColumn;
import org.zkoss.stateless.sul.IColumns;
import org.zkoss.stateless.sul.IGrid;
import org.zkoss.statelessex.state.IGridController;
import org.zkoss.zul.RowComparator;
import org.zkoss.zul.SimpleListModel;

/**
 * A set of example for {@link IColumn} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Grid/Column">IColumn</a>,
 * if any.
 *
 * @author katherine
 * @see IColumn
 */
@RichletMapping("/data/iColumn")
public class IColumnRichlet implements StatelessRichlet {
	@RichletMapping("/sort")
	public IGrid sort() {
		IColumns cols = IColumns.of(IColumn.of("column").withSortDirection(IColumn.SortDirection.DESCENDING)
				.withSortAscending(new RowComparator(1, true, true))
				.withSortDescending(new RowComparator(1, false, true)));
		IGrid grid = IGrid.ofColumns(cols).withAutosort(IGrid.Autosort.ENABLE).withId("grid");
		return IGridController.of(grid, initGridModel()).build();
	}

	private SimpleListModel initGridModel() {
		String[] data = new String[3];
		data[0] = "2";
		data[1] = "1";
		data[2] = "3";
		return new SimpleListModel(data);
	}
}