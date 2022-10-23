/* Issue0078ListboxPagingRichlet.java

	Purpose:

	Description:

	History:
		Thu Jan 20 16:39:15 CST 2021, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.issues;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IListbox;
import org.zkoss.zephyr.zpr.IListhead;
import org.zkoss.zephyr.zpr.IListheader;
import org.zkoss.zephyr.zpr.IListitem;

/**
 * @author katherine
 */
@RichletMapping("/issue0078/listbox")
public class Issue0078ListboxPagingRichlet implements StatelessRichlet {
	@RichletMapping("")
	public IComponent pagingMold() {
		return IListbox.PAGING.withPagingChild(
						IListbox.PAGING.getPagingChild().withPageSize(5)).withChildren(
						IntStream.range(1, 53)
								.mapToObj(i -> IListitem.of(String.valueOf(i)))
								.collect(Collectors.toList()))
				.withListhead(IListhead.of(IListheader.of("header")));
	}
}