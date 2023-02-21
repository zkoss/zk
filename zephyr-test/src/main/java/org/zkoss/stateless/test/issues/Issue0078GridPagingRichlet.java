/* Issue0078GridPagingRichlet.java

	Purpose:

	Description:

	History:
		Thu Jan 20 16:39:15 CST 2021, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.issues;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.IColumn;
import org.zkoss.stateless.sul.IColumns;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IGrid;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.IRow;

/**
 * @author katherine
 */
@RichletMapping("/stateless/issue0078/grid")
public class Issue0078GridPagingRichlet implements StatelessRichlet {
	@RichletMapping("")
	public IComponent pagingMold() {
		return IGrid.of(IntStream.range(1, 53).mapToObj(i ->
						IRow.of(ILabel.of(String.valueOf(i)))).collect(Collectors.toList()))
				.withPagingChild(IGrid.PAGING.getPagingChild().withPageSize(5))
				.withColumns(IColumns.of(IColumn.of("column")));
	}
}