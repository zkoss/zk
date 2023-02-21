/* Issue0078ListboxPagingRichlet.java

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
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IListbox;
import org.zkoss.stateless.sul.IListhead;
import org.zkoss.stateless.sul.IListheader;
import org.zkoss.stateless.sul.IListitem;

/**
 * @author katherine
 */
@RichletMapping("/stateless/issue0078/listbox")
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